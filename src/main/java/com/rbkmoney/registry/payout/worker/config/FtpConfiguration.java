package com.rbkmoney.registry.payout.worker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FtpConfiguration {
    private String host;
    private int port;
    private String username;
    private String password;
    private String parentPath;
}
