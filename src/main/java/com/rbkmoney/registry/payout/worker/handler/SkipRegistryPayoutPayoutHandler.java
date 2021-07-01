package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkipRegistryPayoutPayoutHandler implements RegistryPayoutHandler {

    @Override
    public boolean isHadle(String provider) {
        return false;
    }

    @Override
    public PayoutStorage handle(InputStream inputStream, PayoutStorage payoutStorage) {
        log.error("No handlers available to get payouts.");
        return new PayoutStorage();
    }
}
