package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.ftp.TestFtpClient;
import com.rbkmoney.registry.payout.worker.ftp.TestFtpServer;
import com.rbkmoney.registry.payout.worker.handler.RegistryPayoutHandler;
import com.rbkmoney.registry.payout.worker.handler.RsbRegistryPayoutPayoutHandler;
import com.rbkmoney.registry.payout.worker.model.PartyShop;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.parser.RsbParser;
import com.rbkmoney.registry.payout.worker.service.MockTransactions;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.ftpserver.FtpServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@Slf4j
@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class FileReaderTest extends MockTransactions {

    private static final String TEST_FILE_NAME = "src/test/resources/test.xls";
    private static final String TEST_FILE_NAME_DELETE = "test.xls";

    @Autowired
    private InvoicingHgClientService invoicingClient;

    @Autowired
    private RsbParser rsbParser;

    @Test
    void testFileReader() throws IOException {
        FtpServer ftpServer = null;
        FTPClient ftpClient = null;
        try {
            TestFtpServer testFtpServer = new TestFtpServer();
            ftpServer = testFtpServer.createServer();
            ftpServer.start();
            ftpClient = setFtpClient();
            storeFileOnFtp(ftpClient);
            FTPFile[] ftpDirs = ftpClient.listDirectories();
            for (FTPFile ftpDir : ftpDirs) {
                if (ftpDir.getName().equals(".") || ftpDir.getName().equals("..")) {
                    continue;
                }
                ftpClient.changeWorkingDirectory(ftpDir.getName());
                PayoutStorage payoutStorage = filereader().readFiles(ftpClient, "rsb");
                assertEquals(6, payoutStorage.getPayouts().size());
                assertEquals(-500, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId5")
                        .shopId("testShopId6")
                        .build())
                        .getAmount());
                assertEquals(1100, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId0")
                        .shopId("testShopId0")
                        .build())
                        .getAmount());
                assertEquals(1700, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId0")
                        .shopId("testShopId1")
                        .build())
                        .getAmount());
                assertEquals(1500, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId1")
                        .shopId("testShopId1")
                        .build())
                        .getAmount());
                assertEquals(2200, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId1")
                        .shopId("testShopId2")
                        .build())
                        .getAmount());
                assertEquals(1700, payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId2")
                        .shopId("testShopId2")
                        .build())
                        .getAmount());
                assertNull(payoutStorage.getPayouts().get(PartyShop.builder()
                        .partyId("testPartyId0")
                        .shopId("testShopId2")
                        .build()));
                ftpClient.changeToParentDirectory();
            }
            deleteFileFromFtp(ftpClient);
        } catch (Exception ex) {
            log.error("Received exception", ex);
        } finally {
            stopFtpClient(ftpClient);
            stopFtpServer(ftpServer);
        }
    }

    FTPClient setFtpClient() throws IOException {
        TestFtpClient testFtpClient = new TestFtpClient();
        return testFtpClient.getFtpClient(true);
    }

    void storeFileOnFtp(FTPClient ftpClient) throws IOException {
        File file = new File(TEST_FILE_NAME);
        ftpClient.changeWorkingDirectory("src/test/resources/");
        ftpClient.makeDirectory("registry");
        ftpClient.changeWorkingDirectory("registry");
        ftpClient.makeDirectory("rsb");
        ftpClient.changeWorkingDirectory("rsb");
        InputStream inputStream = new FileInputStream(file);
        ftpClient.storeFile(file.getName(), inputStream);
        ftpClient.changeToParentDirectory();
        inputStream.close();
    }

    void deleteFileFromFtp(FTPClient ftpClient) throws IOException {
        ftpClient.changeWorkingDirectory("rsb/processed");
        ftpClient.deleteFile(TEST_FILE_NAME_DELETE);
        ftpClient.changeToParentDirectory();
        ftpClient.removeDirectory("processed");
        ftpClient.changeToParentDirectory();
        ftpClient.removeDirectory("rsb");
        ftpClient.changeToParentDirectory();
        ftpClient.removeDirectory("registry");
    }

    FilePayoutStorageReader filereader() {
        RegistryPayoutHandler registryParser = new RsbRegistryPayoutPayoutHandler(invoicingClient, rsbParser);
        List<RegistryPayoutHandler> list = new ArrayList<>();
        list.add(registryParser);
        return new FilePayoutStorageReader(list);
    }


    void stopFtpClient(FTPClient ftpClient) throws IOException {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            log.error("Received error while close FTP client: ", ex);
            throw ex;
        }
    }

    void stopFtpServer(FtpServer ftpServer) {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

}
