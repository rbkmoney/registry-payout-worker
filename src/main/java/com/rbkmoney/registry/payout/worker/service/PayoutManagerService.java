package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.service.hg.rsb.InvoicingHgClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutManagerService {

    private final PayoutManagementSrv.Iface payoutManagerClient;
    private final PartyManagementSrv.Iface partyManagementClient;

    public void sendPayouts(Payouts payouts) {
        for (String party : payouts.getPayouts().keySet()) {
            PayoutParams payoutParams = new PayoutParams();
            Map<String, Long> shops = payouts.getPayouts().get(party);
            for (String shop : shops.keySet()) {
                if (shops.get(shop) > 0) {
                    try {
                        Cash cash = new Cash();
                        cash.setAmount(shops.get(shop));
                        CurrencyRef currencyRef = partyManagementClient
                                .getShopAccount(InvoicingHgClientService.USER_INFO, party, shop)
                                .getCurrency();
                        cash.setCurrency(currencyRef);
                        ShopParams shopParams = new ShopParams();
                        shopParams.setPartyId(party);
                        shopParams.setShopId(shop);
                        payoutParams.setCash(cash);
                        payoutParams.setShopParams(shopParams);
                        payoutManagerClient.createPayout(payoutParams);
                        log.info("Payout created with params {}", payoutParams);
                    } catch (TException e) {
                        log.error("Received error when create payout ", e);
                    }
                }
            }
        }
    }

}
