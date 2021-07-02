package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.handler.RegistryPayoutHandler;
import com.rbkmoney.registry.payout.worker.handler.SkipRegistryPayoutPayoutHandler;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilePayoutStorageReader {

    private final List<RegistryPayoutHandler> handlers;
    private static final String PATH_TO_PROCESSED_FILE = "processed";

    public PayoutStorage readFiles(FTPClient ftpClient, String pathDir) throws IOException {
        PayoutStorage payoutStorage = new PayoutStorage();
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                InputStream inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
                if (ftpClient.completePendingCommand()) {
                    log.info("File {} was received successfully.", ftpFile.getName());
                }
                PayoutStorage filePayoutStorage = handlers.stream()
                        .filter(handler -> handler.isHadle(pathDir))
                        .findFirst()
                        .orElse(new SkipRegistryPayoutPayoutHandler())
                        .handle(inputStream, payoutStorage);
                payoutStorage.getPayouts().putAll(filePayoutStorage.getPayouts());
                inputStream.close();
                ftpClient.makeDirectory(PATH_TO_PROCESSED_FILE);
                ftpClient.rename(ftpClient.printWorkingDirectory() + "/" + ftpFile.getName(),
                        ftpClient.printWorkingDirectory() + "/" + PATH_TO_PROCESSED_FILE + "/" + ftpFile.getName());
            }
        }
        return payoutStorage;
    }

}
