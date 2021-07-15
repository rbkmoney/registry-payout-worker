package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.config.properties.FtpProperties;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.reader.FilePayoutStorageReader;
import com.rbkmoney.registry.payout.worker.service.payoutmngr.PayoutManagerService;
import com.rbkmoney.registry.payout.worker.sshclient.RegistrySSHClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true")
public class RegistryPayoutWorkerService {

    private final FtpProperties ftpProperties;
    private final FilePayoutStorageReader filePayoutStorageReader;
    private final PayoutManagerService payoutManagerService;
    private final RegistrySSHClient sshClient;

    @Scheduled(fixedRateString = "${scheduling.fixed.rate}")
    public void readTransactionsFromRegistries() {
        try (SFTPClient sftpClient = sshClient.initialize().newSFTPClient()) {
            List<RemoteResourceInfo> ftpDirs = sftpClient.ls(ftpProperties.getParentPath());
            for (RemoteResourceInfo ftpDir : ftpDirs) {
                if (isDirectoryToSkip(ftpDir.getName())) {
                    continue;
                }
                PayoutStorage payoutStorage = filePayoutStorageReader.readFiles(sftpClient, ftpDir);
                payoutManagerService.sendPayouts(payoutStorage);
            }
        } catch (Exception ex) {
            log.error("Received error while connect to Ftp client:", ex);
        }
    }

    private boolean isDirectoryToSkip(String dirName) {
        return dirName.startsWith(".");
    }

}
