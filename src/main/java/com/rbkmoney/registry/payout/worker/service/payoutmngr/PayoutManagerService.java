package com.rbkmoney.registry.payout.worker.service.payoutmngr;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
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
    private final PartyManagementSrv.Iface partyManagementClient;

    public void sendPayouts(PayoutStorage payoutStorage) throws TException {
        createPayouts(payoutStorage).forEach((PayoutParams payoutParams) -> {
            try {
                payoutManagerClient.createPayout(payoutParams);
                log.info("Payout created with params {}", payoutParams);
            } catch (TException e) {
                log.error("Received error when create payout ", e);
            }
        });
    }

    public List<PayoutParams> createPayouts(PayoutStorage payoutStorage) throws TException {
        List<PayoutParams> listPayoutParams = new ArrayList<>();
        for (PayoutStorage.PartyShop partyShop : payoutStorage.getPayouts().keySet()) {
            Long amount = payoutStorage.getPayouts().get(partyShop);
            if (amount > 0) {
                Cash cash = new Cash();
                cash.setAmount(amount);
                CurrencyRef currencyRef = partyManagementClient
                        .getShopAccount(
                                InvoicingHgClientService.USER_INFO,
                                partyShop.getPartyId(),
                                partyShop.getShopId())
                        .getCurrency();
                cash.setCurrency(currencyRef);
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
