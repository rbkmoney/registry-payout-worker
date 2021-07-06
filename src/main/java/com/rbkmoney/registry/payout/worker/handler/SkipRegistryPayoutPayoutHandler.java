package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.PartyShop;
import com.rbkmoney.registry.payout.worker.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkipRegistryPayoutPayoutHandler implements RegistryPayoutHandler {

    @Override
    public boolean isHadle(String provider) {
        return false;
    }

    @Override
    public Map<PartyShop, List<Transaction>> handle(InputStream inputStream) {
        log.error("No handlers available to get payouts.");
        return new HashMap<>();
    }
}
