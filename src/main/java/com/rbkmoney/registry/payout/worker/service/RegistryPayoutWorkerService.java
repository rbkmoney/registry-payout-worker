package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.config.properties.FtpProperties;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import com.rbkmoney.registry.payout.worker.reader.FtpTransactionsReader;
import com.rbkmoney.registry.payout.worker.service.hg.HgClientService;
import com.rbkmoney.registry.payout.worker.service.hg.SkipHgClientService;
import com.rbkmoney.registry.payout.worker.service.payoutmngr.PayoutManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistryPayoutWorkerService {

    private final FtpProperties ftpProperties;
    private final FtpTransactionsReader ftpTransactionsReader;
    private final List<HgClientService> hgClientServices;
    private final PayoutManagerService payoutManagerService;

    @Scheduled(fixedRateString = "${scheduling.fixed.rate}")
    public void readTransactionsFromRegistries() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = ftpClient();
            ftpClient.changeWorkingDirectory(ftpProperties.getParentPath());
            FTPFile[] ftpDirs = ftpClient.listDirectories();
            for (FTPFile ftpDir : ftpDirs) {
                if (ftpDir.getName().equals(".") || ftpDir.getName().equals("..")) {
                    continue;
                }
                ftpClient.changeWorkingDirectory(ftpDir.getName());
                Transactions transactions = ftpTransactionsReader.readFiles(ftpClient, ftpDir.getName());
                ftpClient.changeToParentDirectory();
                log.info("Read {} payments and {} refunds from {}",
                        transactions.getInvoicePayments().size(),
                        transactions.getInvoiceRefunds().size(),
                        ftpDir.getName());
                Payouts payouts = hgClientServices.stream()
                        .filter(hgClientService -> hgClientService.isGetPayouts(ftpDir.getName()))
                        .findFirst()
                        .orElse(new SkipHgClientService())
                        .getPayouts(transactions);
                payoutManagerService.sendPayouts(payouts);
            }
        } catch (Exception ex) {
            log.error("Received error while connect to Ftp client:", ex);
        } finally {
            closeFtp(ftpClient);
        }
    }

    public FTPClient ftpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpProperties.getHost());
        ftpClient.login(ftpProperties.getUsername(), ftpProperties.getPassword());
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    private void closeFtp(FTPClient ftpClient) {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            log.error("Received error while close FTP client: ", ex);
        }
    }

}
