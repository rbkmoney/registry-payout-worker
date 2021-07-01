package com.rbkmoney.registry.payout.worker.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PayoutStorage {
    public Map<PartyShop, Long> payouts = new HashMap<>();

    @Data
    @Builder
    public static class PartyShop {
        private String partyId;
        private String shopId;
    }
}
