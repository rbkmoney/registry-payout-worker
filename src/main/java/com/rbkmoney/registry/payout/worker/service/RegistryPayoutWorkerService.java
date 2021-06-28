package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.config.FtpConfiguration;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import com.rbkmoney.registry.payout.worker.reader.FtpTransactionsReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistryPayoutWorkerService {

    private final FtpConfiguration ftpConfiguration;
    private final FtpTransactionsReader ftpTransactionsReader;
    private final HgClientService hgClientService;

    @Scheduled(fixedRateString = "${scheduling.fixed.rate}")
    public void readTransactionsFromRegistries() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = ftpClient();
            ftpClient.changeWorkingDirectory(ftpConfiguration.getParentPath());
            Transactions transactions = ftpTransactionsReader.readDirectories(ftpClient);
            log.info("Read {} payments and {} refunds",
                    transactions.getInvoicePayments().size(), transactions.getInvoiceRefunds().size());
            Payouts payouts = hgClientService.getPayouts(transactions);
        } catch (Exception ex) {
            log.error("Received error while connect to Ftp client:", ex);
        } finally {
            closeFtp(ftpClient);
        }
    }

    public FTPClient ftpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpConfiguration.getHost());
        ftpClient.login(ftpConfiguration.getUsername(), ftpConfiguration.getPassword());
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
