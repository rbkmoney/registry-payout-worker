package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class Transactions {
    public MultiValueMap<String, Long> invoicePayments = new LinkedMultiValueMap<>();
    public MultiValueMap<String, Long> invoiceRefunds = new LinkedMultiValueMap<>();

    public void addAll(Transactions transactions) {
        if (transactions.getInvoicePayments() != null) {
            invoicePayments.addAll(transactions.getInvoicePayments());
        }
        if (transactions.getInvoiceRefunds() != null) {
            invoiceRefunds.addAll(transactions.getInvoiceRefunds());
        }
    }
}
