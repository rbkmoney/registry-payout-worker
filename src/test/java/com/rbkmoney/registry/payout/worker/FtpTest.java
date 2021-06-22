package com.rbkmoney.registry.payout.worker;

import com.rbkmoney.registry.payout.worker.service.RegistryPayoutWorkerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class FtpTest {

    private FakeFtpServer fakeFtpServer;
    private FtpClient ftpClient;

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
    void testFtp() {
        try {
            assertEquals("/", ftpClient.printWorkingDirectory());
            ftpClient.changeWorkingDirectory("registry");
            assertEquals("/registry", ftpClient.printWorkingDirectory());
            ftpClient.changeWorkingDirectory("rsb");
            Collection<String> collection = ftpClient.listFiles("");
            assertTrue(collection.contains("test.xls"));
        } catch (Exception ex) {
            log.error("Received exception", ex);
        }
    }

}
