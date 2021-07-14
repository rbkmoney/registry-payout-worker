package com.rbkmoney.registry.payout.worker.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sftp")
public class FtpProperties {
    private String host;
    private int port;
    private int connectTimeout;
    private String username;
    private String privateKeyPath;
    private String privateKeyPassphrase;
    private String parentPath;
}
