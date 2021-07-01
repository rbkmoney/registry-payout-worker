package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.PayoutStorage;

import java.io.InputStream;

public interface RegistryPayoutHandler {

    boolean isHadle(String provider);

    PayoutStorage handle(InputStream inputStream, PayoutStorage payouts);
}
