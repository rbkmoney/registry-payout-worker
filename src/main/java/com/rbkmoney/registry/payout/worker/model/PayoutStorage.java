package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PayoutStorage {
    public Map<String, Map<String, Long>> payouts = new HashMap<>();

    public void putAll(Map<String, Map<String, Long>> payment,
                       Map<String, Map<String, Long>> refund) {
        if (payment != null) {
            payouts.putAll(payment);
        }
        if (refund != null) {
            payouts.putAll(refund);
        }
    }
}
