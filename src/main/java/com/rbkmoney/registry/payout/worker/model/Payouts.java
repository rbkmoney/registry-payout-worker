package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Payouts {
    public Map<String, Map<String, Float>> payouts = new HashMap<>();

    public void putAll(Map<String, Map<String, Float>> invoicePayment,
                       Map<String, Map<String, Float>> invoiceRefund) {
        payouts.putAll(invoicePayment);
        payouts.putAll(invoiceRefund);
    }
}
