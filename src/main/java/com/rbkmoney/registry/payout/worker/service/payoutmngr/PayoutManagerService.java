package com.rbkmoney.registry.payout.worker.service.payoutmngr;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.registry.payout.worker.model.PartyShop;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutManagerService {

    private final PayoutManagementSrv.Iface payoutManagerClient;

    public void sendPayouts(PayoutStorage payoutStorage) {
        createPayouts(payoutStorage).forEach((PayoutParams payoutParams) -> {
            try {
                payoutManagerClient.createPayout(payoutParams);
                log.info("Payout created with params {}", payoutParams);
            } catch (TException e) {
                log.error("Received error when create payout ", e);
            }
        });
    }

    public List<PayoutParams> createPayouts(PayoutStorage payoutStorage) {
        List<PayoutParams> listPayoutParams = new ArrayList<>();
        for (PartyShop partyShop : payoutStorage.getPayouts().keySet()) {
            Long amount = payoutStorage.getPayouts().get(partyShop).getAmount();
            if (amount > 0) {
                Cash cash = new Cash();
                cash.setAmount(amount);
                cash.setCurrency(new CurrencyRef(payoutStorage.getPayouts().get(partyShop).getCurrency()));
                ShopParams shopParams = new ShopParams();
                shopParams.setPartyId(partyShop.getPartyId());
                shopParams.setShopId(partyShop.getShopId());
                PayoutParams payoutParams = new PayoutParams();
                payoutParams.setCash(cash);
                payoutParams.setShopParams(shopParams);
                listPayoutParams.add(payoutParams);
            }
        }
        return listPayoutParams;
    }

}
