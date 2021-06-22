package com.rbkmoney.registry.payout.worker;

import com.rbkmoney.registry.payout.worker.service.RegistryPayoutWorkerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.*;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class FtpTest {

    private FakeFtpServer fakeFtpServer;
    private FtpClient ftpClient;

    @Autowired
    RegistryPayoutWorkerService registryPayoutWorkerService;

    @BeforeEach
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/"));
        fileSystem.add(new FileEntry("/registry/rsb/test.xls"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @AfterEach
    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
        String ftpUrl = String.format(
                "ftp://user:password@localhost:%d/", fakeFtpServer.getServerControlPort());
        URLConnection urlConnection = new URL(ftpUrl).openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        Files.copy(inputStream, new File("test.xls").toPath());
        inputStream.close();
        assertTrue(new File("test.xls").exists());
        new File("test.xls").delete();
    }


    @Test
    void testFtp() {
        try {
            assertEquals("/", ftpClient.printWorkingDirectory());
            ftpClient.changeWorkingDirectory("registry");
            assertEquals("/registry", ftpClient.printWorkingDirectory());
            ftpClient.changeWorkingDirectory("rsb");
            Collection<String> collection = ftpClient.listFiles("");
            assertTrue(collection.contains("test.xls"));
            registryPayoutWorkerService.readTransactionsFromRegistries();
        } catch (Exception ex) {
            log.error("Received exception", ex);
        }
    }

}
