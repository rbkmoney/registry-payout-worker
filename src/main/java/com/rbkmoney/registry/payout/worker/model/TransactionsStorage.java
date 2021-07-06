package com.rbkmoney.registry.payout.worker.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionsStorage {
    private List<Transaction> payments;
    private List<Transaction> refunds;
}
