package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.model.FilesOperations;
import com.rbkmoney.registry.payout.worker.parser.RegistryParser;
import com.rbkmoney.registry.payout.worker.parser.SkipParser;
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
public class FtpTransactionsReader {

    private final List<RegistryParser> parsers;
    private static final String PATH_TO_PROCESSED_FILE = "processed";

    public FilesOperations readFiles(FTPClient ftpClient, String pathDir) throws IOException {
        FilesOperations filesOperations = new FilesOperations();
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                InputStream inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
                if (ftpClient.completePendingCommand()) {
                    log.info("File {} was received successfully.", ftpFile.getName());
                }
                FilesOperations fileOperations = parsers.stream()
                        .filter(parser -> parser.isParse(pathDir))
                        .findFirst()
                        .orElse(new SkipParser())
                        .parse(inputStream);
                filesOperations.addAll(fileOperations);
                inputStream.close();
                ftpClient.makeDirectory(PATH_TO_PROCESSED_FILE);
                ftpClient.rename(ftpClient.printWorkingDirectory() + "/" + ftpFile.getName(),
                        ftpClient.printWorkingDirectory() + "/" + PATH_TO_PROCESSED_FILE + "/" + ftpFile.getName());
            }
        }
        return filesOperations;
    }

}
