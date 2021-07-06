package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.PartyShop;
import com.rbkmoney.registry.payout.worker.model.Transaction;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RegistryPayoutHandler {

    boolean isHadle(String provider);

    Map<PartyShop, List<Transaction>> handle(InputStream inputStream);
}
