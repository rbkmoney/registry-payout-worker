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
    private static final String PROCESSED_PATH = "/processed/";

    public PayoutStorage readFiles(SFTPClient sftpClient, RemoteResourceInfo ftpDir) throws IOException {
        PayoutStorage payoutStorage = new PayoutStorage();
        List<RemoteResourceInfo> remoteResourceInfoList = sftpClient.ls(ftpDir.getPath());
        for (RemoteResourceInfo remoteResourceInfo : remoteResourceInfoList) {
            if (remoteResourceInfo.isRegularFile()) {
                RemoteFile remoteFile = sftpClient.open(remoteResourceInfo.getPath());
                InputStream inputStream = remoteFile.new RemoteFileInputStream(0);
                log.info("File {} was received successfully", remoteResourceInfo.getName());
                Map<PartyShop, List<Transaction>> transactions = handlers.stream()
                        .filter(handler -> handler.isHadle(ftpDir.getName()))
                        .findFirst()
                        .orElse(new SkipRegistryPayoutPayoutHandler())
                        .handle(inputStream);
                payoutStorage.getPayouts().putAll(mapTransactionToPayout(transactions));
                inputStream.close();
                if (processedPathNotExist(remoteResourceInfoList)) {
                    sftpClient.mkdir(remoteResourceInfo.getParent() + PROCESSED_PATH);
                }
                sftpClient.rename(remoteResourceInfo.getPath(),
                        remoteResourceInfo.getParent() + PROCESSED_PATH + remoteResourceInfo.getName());
            }
        }
        return payoutStorage;
    }

    private boolean processedPathNotExist(final List<RemoteResourceInfo> list) {
        return list.stream().noneMatch(o -> o.getName().equals(PROCESSED_PATH));
    }

}
