package com.rbkmoney.registry.payout.worker.mapper;

import com.rbkmoney.registry.payout.worker.model.*;

import java.util.*;

public class PayoutMapper {

    public static Map<PartyShop, Payout> mapTransactionToPayout(Map<PartyShop, List<Transaction>> transactions) {
        Map<PartyShop, Payout> payouts = new HashMap<>();
        for (PartyShop partyShop : transactions.keySet()) {
            long sum = transactions.get(partyShop).stream()
                    .mapToLong(Transaction::getAmount)
                    .sum();
            Payout payout = Payout.builder()
                    .amount(sum)
                    .currency(transactions.get(partyShop).get(0).getCurrency())
                    .build();
            payouts.put(partyShop, payout);
        }
        return payouts;
    }

}
