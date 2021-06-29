package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;
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
    public Payouts getPayouts(Transactions transactions) {
        log.error("No HgClientServices available to get payouts.");
        return new Payouts();
    }

}
