package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.model.FilesOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkipHgClientService implements HgClientService {

    @Override
    public boolean isGetPayouts(String provider) {
        return false;
    }

    @Override
    public PayoutStorage getPayouts(FilesOperations filesOperations) {
        log.error("No HgClientServices available to get payouts.");
        return new PayoutStorage();
    }

}
