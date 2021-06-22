package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;
import org.springframework.util.MultiValueMap;

@Data
public class Transactions {
    public MultiValueMap<String, Float> invoicePayments;
    public MultiValueMap<String, Float> invoiceRefunds;
}
