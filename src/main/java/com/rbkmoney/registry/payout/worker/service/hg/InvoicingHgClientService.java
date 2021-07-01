package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.model.RegistryOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingHgClientService {

    private final InvoicingSrv.Iface invoicing;
    public static final UserInfo USER_INFO = new UserInfo(
            "registry-payout-worker",
            UserType.internal_user(new InternalUser()));
    public static final EventRange EVENT_RANGE = new EventRange().setLimit(1);

    public PayoutStorage getPayouts(RegistryOperations registryOperations, PayoutStorage payoutStorage) {
        mapPartyShop(registryOperations.getPayments(), payoutStorage.getPayouts());
        mapPartyShop(setNegativeNumber(registryOperations.getRefunds()), payoutStorage.getPayouts());
        return payoutStorage;
    }

    private void mapPartyShop(MultiValueMap<String, Long> invoices,
                              Map<PayoutStorage.PartyShop, Long> payouts) {
        for (String invoiceId : invoices.keySet()) {
            try {
                Invoice invoice = invoicing.get(USER_INFO, invoiceId, EVENT_RANGE);
                PayoutStorage.PartyShop partyShop = PayoutStorage.PartyShop.builder()
                        .partyId(invoice.getInvoice().getOwnerId())
                        .shopId(invoice.getInvoice().getShopId())
                        .build();
                long amount = invoices.get(invoiceId).stream().mapToLong(a -> a).sum();
                payouts.merge(partyShop, amount, Long::sum);
            } catch (TException e) {
                log.error("Received error when get invoice ", e);
            }
        }
    }

    private MultiValueMap<String, Long> setNegativeNumber(MultiValueMap<String, Long> refunds) {
        for (String refund : refunds.keySet()) {
            List<Long> list =
                    refunds.get(refund).stream().map(v -> v > 0 ? -v : v).collect(Collectors.toList());
            refunds.put(refund, list);
        }
        return refunds;
    }

}
