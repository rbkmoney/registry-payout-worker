package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class FilesOperations {
    public MultiValueMap<String, Long> payments = new LinkedMultiValueMap<>();
    public MultiValueMap<String, Long> refunds = new LinkedMultiValueMap<>();

    public void addAll(FilesOperations operations) {
        if (operations.getPayments() != null) {
            payments.addAll(operations.getPayments());
        }
        if (operations.getRefunds() != null) {
            refunds.addAll(operations.getRefunds());
        }
    }
}
