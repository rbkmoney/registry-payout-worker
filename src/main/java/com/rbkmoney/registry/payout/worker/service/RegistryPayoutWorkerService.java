package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.config.properties.FtpProperties;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.reader.FilePayoutStorageReader;
import com.rbkmoney.registry.payout.worker.service.payoutmngr.PayoutManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true")
public class RegistryPayoutWorkerService {

    private final FtpProperties ftpProperties;
    private final FilePayoutStorageReader filePayoutStorageReader;
    private final PayoutManagerService payoutManagerService;

    @Scheduled(fixedRateString = "${scheduling.fixed.rate}")
    public void readTransactionsFromRegistries() {
        try (SSHClient sshClient = new SSHClient()) {
            initialize(sshClient);
            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
                List<RemoteResourceInfo> ftpDirs = sftpClient.ls(ftpProperties.getParentPath());
                for (RemoteResourceInfo ftpDir : ftpDirs) {
                    if (isDirectoryToSkip(ftpDir.getName())) {
                        continue;
                    }
                    PayoutStorage payoutStorage = filePayoutStorageReader.readFiles(sftpClient, ftpDir);
                    payoutManagerService.sendPayouts(payoutStorage);
                }
            }
        } catch (Exception ex) {
            log.error("Received error while connect to Ftp client:", ex);
        }
    }

    private void initialize(SSHClient sshClient) throws IOException {
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.setConnectTimeout(ftpProperties.getConnectTimeout());
        sshClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
        KeyProvider keyProvider = sshClient.loadKeys(ftpProperties.getPrivateKeyPath(),
                ftpProperties.getPrivateKeyPassphrase());
        sshClient.authPublickey(ftpProperties.getUsername(), keyProvider);
    }

    private boolean isDirectoryToSkip(String dirName) {
        return dirName.startsWith(".");
    }

}
