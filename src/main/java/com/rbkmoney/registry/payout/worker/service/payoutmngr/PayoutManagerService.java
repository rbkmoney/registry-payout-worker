package com.rbkmoney.registry.payout.worker.service.payoutmngr;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutManagerService {

    private final PayoutManagementSrv.Iface payoutManagerClient;
    private final PartyManagementSrv.Iface partyManagementClient;

    public void sendPayouts(Payouts payouts) throws TException {
        createPayouts(payouts).forEach((PayoutParams payoutParams) -> {
            try {
                payoutManagerClient.createPayout(payoutParams);
                log.info("Payout created with params {}", payoutParams);
            } catch (TException e) {
                log.error("Received error when create payout ", e);
            }
        });
    }

    public List<PayoutParams> createPayouts(Payouts payouts) throws TException {
        List<PayoutParams> listPayoutParams = new ArrayList<>();
        for (String party : payouts.getPayouts().keySet()) {
            Map<String, Long> shops = payouts.getPayouts().get(party);
            for (String shop : shops.keySet()) {
                if (shops.get(shop) > 0) {
                    Cash cash = new Cash();
                    cash.setAmount(shops.get(shop));
                    CurrencyRef currencyRef = partyManagementClient
                            .getShopAccount(InvoicingHgClientService.USER_INFO, party, shop)
                            .getCurrency();
                    cash.setCurrency(currencyRef);
                    ShopParams shopParams = new ShopParams();
                    shopParams.setPartyId(party);
                    shopParams.setShopId(shop);
                    PayoutParams payoutParams = new PayoutParams();
                    payoutParams.setCash(cash);
                    payoutParams.setShopParams(shopParams);
                    listPayoutParams.add(payoutParams);
                }
            }
        }
        return listPayoutParams;
    }

}
