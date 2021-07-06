package com.rbkmoney.registry.payout.worker.model;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class PartyShop {
    private String partyId;
    private String shopId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyShop partyShop = (PartyShop) o;
        return Objects.equals(partyId, partyShop.partyId)
                && Objects.equals(shopId, partyShop.shopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, shopId);
    }
}
