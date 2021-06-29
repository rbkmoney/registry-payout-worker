package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Payouts {
    public Map<String, Map<String, Long>> payouts = new HashMap<>();

    public void putAll(Map<String, Map<String, Long>> invoicePayment,
                       Map<String, Map<String, Long>> invoiceRefund) {
        payouts.putAll(invoicePayment);
        payouts.putAll(invoiceRefund);
    }
}
