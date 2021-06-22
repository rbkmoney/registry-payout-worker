package com.rbkmoney.registry.payout.worker.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

@Slf4j
public class TestFtpClient {

    private String host = "localhost";
    private int port = TestFtpServer.FTP_PORT;
    private String username = TestFtpServer.FTP_TEST_USER;
    private String password = TestFtpServer.FTP_TEST_PASSWORD;

    public FTPClient getFtpClient(boolean needLogin) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try {
            connectToFtpServer(ftpClient);
            if (needLogin) {
                loginToFtpServer(ftpClient);
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient;
        } catch (IOException ex) {
            log.error("Received error while create FTP client: ", ex);
            throw ex;
        }
    }

    private void connectToFtpServer(FTPClient ftpClient) throws IOException {
        long currentTime = System.currentTimeMillis();
        while (!ftpClient.isConnected()) {
            if (System.currentTimeMillis() - currentTime > 5000L) {
                log.error("Timeout error. Failed to connect to server!");
                throw new RuntimeException("Timeout error. Failed to connect to server!");
            }
            ftpClient.connect(host, port);
        }
    }

    private void loginToFtpServer(FTPClient ftpClient) throws IOException {
        long currentTime = System.currentTimeMillis();
        while (!ftpClient.login(username, password)) {
            if (System.currentTimeMillis() - currentTime > 5000L) {
                log.error("Timeout error. Failed to login to server!");
                throw new RuntimeException("Timeout error. Failed to login to server!");
            }
        }
    }

}
