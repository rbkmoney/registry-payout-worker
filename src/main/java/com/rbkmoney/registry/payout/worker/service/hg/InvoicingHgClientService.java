package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingHgClientService {

    private final InvoicingSrv.Iface invoicing;
    public static final UserInfo USER_INFO = new UserInfo(
            "registry-payout-worker",
            UserType.internal_user(new InternalUser()));
    public static final EventRange EVENT_RANGE = new EventRange().setLimit(1);

    public PayoutStorage getPayouts(Map<String, Long> registryOperations, PayoutStorage payoutStorage) {
        mapPartyShop(registryOperations, payoutStorage.getPayouts());
        return payoutStorage;
    }

    private void mapPartyShop(Map<String, Long> registryOperations,
                              Map<PayoutStorage.PartyShop, Long> payouts) {
        for (String invoiceId : registryOperations.keySet()) {
            try {
                Invoice invoice = invoicing.get(USER_INFO, invoiceId, EVENT_RANGE);
                PayoutStorage.PartyShop partyShop = PayoutStorage.PartyShop.builder()
                        .partyId(invoice.getInvoice().getOwnerId())
                        .shopId(invoice.getInvoice().getShopId())
                        .build();
                long amount = registryOperations.get(invoiceId);
                payouts.merge(partyShop, amount, Long::sum);
            } catch (TException e) {
                log.error("Received error when get invoice ", e);
            }
        }
    }

}
