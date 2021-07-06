package com.rbkmoney.registry.payout.worker.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payout {
    private Long amount;
    private String currency;
}
