package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.ftp.TestFtpClient;
import com.rbkmoney.registry.payout.worker.ftp.TestFtpServer;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import com.rbkmoney.registry.payout.worker.parser.RegistryParser;
import com.rbkmoney.registry.payout.worker.parser.rsb.RsbParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.ftpserver.FtpServer;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
public class FileReaderTest {

    private static final String TEST_FILE_NAME = "src/test/resources/test.xls";
    private static final String TEST_FILE_NAME_DELETE = "test.xls";

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
                Transactions transactions = filereader().readFiles(ftpClient, "rsb");
                assertEquals(10, transactions.getInvoicePayments().size());
                assertEquals(1, transactions.getInvoiceRefunds().size());
                assertEquals(97000, transactions.getInvoicePayments().get("1Tgz70wxfxA").get(0));
                assertEquals(24250, transactions.getInvoiceRefunds().get("1ThpZ6eiyh6").get(0), 0);
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

    FtpTransactionsReader filereader() {
        RegistryParser registryParser = new RsbParser();
        List<RegistryParser> list = new ArrayList<>();
        list.add(registryParser);
        return new FtpTransactionsReader(list);
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
