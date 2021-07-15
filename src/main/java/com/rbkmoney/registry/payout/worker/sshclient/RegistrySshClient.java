package com.rbkmoney.registry.payout.worker.sshclient;

import com.rbkmoney.registry.payout.worker.config.properties.FtpProperties;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class RegistrySshClient {

    private final FtpProperties ftpProperties;

    public SSHClient initialize() throws IOException {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.setConnectTimeout(ftpProperties.getConnectTimeout());
        sshClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
        KeyProvider keyProvider = sshClient.loadKeys(ftpProperties.getPrivateKeyPath(),
                ftpProperties.getPrivateKeyPassphrase());
        sshClient.authPublickey(ftpProperties.getUsername(), keyProvider);
        return sshClient;
    }
}
