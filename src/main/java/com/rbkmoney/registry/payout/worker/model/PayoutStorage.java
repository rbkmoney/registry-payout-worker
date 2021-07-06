package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PayoutStorage {
    public Map<PartyShop, Payout> payouts = new HashMap<>();
}
