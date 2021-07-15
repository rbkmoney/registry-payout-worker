package com.rbkmoney.registry.payout.worker.reader;

import com.rbkmoney.registry.payout.worker.handler.RegistryPayoutHandler;
import com.rbkmoney.registry.payout.worker.handler.SkipRegistryPayoutPayoutHandler;
import com.rbkmoney.registry.payout.worker.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.registry.payout.worker.mapper.PayoutMapper.mapTransactionToPayout;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilePayoutStorageReader {

    private final List<RegistryPayoutHandler> handlers;
    private static final String PROCESSED_PATH = "processed";

    public PayoutStorage readFiles(SFTPClient sftpClient, RemoteResourceInfo ftpDir) throws IOException {
        PayoutStorage payoutStorage = new PayoutStorage();
        List<RemoteResourceInfo> resourceInfoList = sftpClient.ls(ftpDir.getPath());
        for (RemoteResourceInfo resourceInfo : resourceInfoList) {
            if (resourceInfo.isRegularFile()) {
                Map<PartyShop, List<Transaction>> transactions = readFile(sftpClient, resourceInfo, ftpDir.getName());
                payoutStorage.getPayouts().putAll(mapTransactionToPayout(transactions));
                movingFileToProcessedPath(resourceInfoList, resourceInfo, sftpClient);
            }
        }
        return payoutStorage;
    }

    private Map<PartyShop, List<Transaction>> readFile(SFTPClient sftpClient,
                                                       RemoteResourceInfo resourceInfo,
                                                       String providerPath) throws IOException {
        try (RemoteFile remoteFile = sftpClient.open(resourceInfo.getPath());
                InputStream inputStream = remoteFile.new RemoteFileInputStream(0)) {
            log.info("File {} was received successfully", resourceInfo.getName());
            return handlers.stream()
                    .filter(handler -> handler.isHadle(providerPath))
                    .findFirst()
                    .orElse(new SkipRegistryPayoutPayoutHandler())
                    .handle(inputStream);
        }
    }

    private void movingFileToProcessedPath(List<RemoteResourceInfo> resourceInfoList,
                                           RemoteResourceInfo resourceInfo,
                                           SFTPClient sftpClient) throws IOException {
        if (isProcessedPathNotExist(resourceInfoList)) {
            sftpClient.mkdir(resourceInfo.getParent() + "/" + PROCESSED_PATH);
        }
        sftpClient.rename(resourceInfo.getPath(),
                resourceInfo.getParent() + "/" + PROCESSED_PATH + "/" + resourceInfo.getName());
    }

    private boolean isProcessedPathNotExist(final List<RemoteResourceInfo> list) {
        return list.stream().noneMatch(o -> o.getName().equals(PROCESSED_PATH));
    }

}
