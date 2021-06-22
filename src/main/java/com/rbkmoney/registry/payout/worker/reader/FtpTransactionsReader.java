package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.exception.RegistryPayoutWorkerException;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import com.rbkmoney.registry.payout.worker.parser.RegistryParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FtpTransactionsReader {

    private final List<RegistryParser> parsers;
    private static final String PATH_TO_PROCESSED_FILE = "processed";

    public Transactions readDirectories(FTPClient ftpClient) throws IOException {
        Transactions transactions = new Transactions();
        MultiValueMap<String, Float> payments = new LinkedMultiValueMap<>();
        MultiValueMap<String, Float> refunds = new LinkedMultiValueMap<>();
        FTPFile[] ftpDirs = ftpClient.listDirectories();
        for (FTPFile ftpDir : ftpDirs) {
            if (ftpDir.getName().equals(".") || ftpDir.getName().equals("..")) {
                continue;
            }
            ftpClient.changeWorkingDirectory(ftpDir.getName());
            transactions = readFiles(ftpClient, ftpDir.getName());
            putTransactionsIntoMap(transactions, payments, refunds);
            ftpClient.changeToParentDirectory();
        }
        return setTransactions(transactions, payments, refunds);
    }

    private Transactions readFiles(FTPClient ftpClient, String pathDir) throws IOException {
        Transactions transactions = new Transactions();
        MultiValueMap<String, Float> payments = new LinkedMultiValueMap<>();
        MultiValueMap<String, Float> refunds = new LinkedMultiValueMap<>();
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                InputStream inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
                if (ftpClient.completePendingCommand()) {
                    log.info("File {} was received successfully.", ftpFile.getName());
                }
                transactions = parsers.stream()
                        .filter(parser -> parser.isParse(pathDir))
                        .findFirst()
                        .orElseThrow(RegistryPayoutWorkerException::new)
                        .parse(inputStream);
                inputStream.close();
                ftpClient.makeDirectory(PATH_TO_PROCESSED_FILE);
                ftpClient.rename(ftpClient.printWorkingDirectory() + "/" + ftpFile.getName(),
                        ftpClient.printWorkingDirectory() + "/" + PATH_TO_PROCESSED_FILE + "/" + ftpFile.getName());
                putTransactionsIntoMap(transactions, payments, refunds);
            }
        }
        return setTransactions(transactions, payments, refunds);
    }

    private void putTransactionsIntoMap(Transactions transactions,
                                        MultiValueMap<String, Float> payments,
                                        MultiValueMap<String, Float> refunds) {
        if (transactions.getInvoicePayments() != null) {
            payments.addAll(transactions.getInvoicePayments());
        }
        if (transactions.getInvoiceRefunds() != null) {
            refunds.addAll(transactions.getInvoiceRefunds());
        }
    }

    private Transactions setTransactions(Transactions transactions,
                                         MultiValueMap<String, Float> payments,
                                         MultiValueMap<String, Float> refunds) {
        transactions.setInvoicePayments(payments);
        transactions.setInvoiceRefunds(refunds);
        return transactions;
    }

}
