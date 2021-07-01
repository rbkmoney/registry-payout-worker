package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.FilesOperations;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.registry.payout.worker.constant.PathToReadConstant.RSB;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingHgClientService implements HgClientService {

    private final InvoicingSrv.Iface invoicing;
    public static final UserInfo USER_INFO = new UserInfo(
            "registry-payout-worker",
            UserType.internal_user(new InternalUser()));
    public static final EventRange EVENT_RANGE = new EventRange().setLimit(1);

    @Override
    public boolean isGetPayouts(String provider) {
        return RSB.equals(provider);
    }

    @Override
    public PayoutStorage getPayouts(FilesOperations filesOperations) {
        PayoutStorage payoutStorage = new PayoutStorage();
        Map<String, Map<String, Long>> payments =
                mapPartyShop(filesOperations.getPayments(), payoutStorage);
        Map<String, Map<String, Long>> refunds =
                mapPartyShop(setNegativeNumber(filesOperations.getRefunds()), payoutStorage);
        payoutStorage.putAll(payments, refunds);
        return payoutStorage;
    }

    private Map<String, Map<String, Long>> mapPartyShop(MultiValueMap<String, Long> invoices,
                                                        PayoutStorage payoutStorage) {
        Map<String, Map<String, Long>> payouts = payoutStorage.getPayouts();
        for (String invoiceId : invoices.keySet()) {
            try {
                Invoice invoice = invoicing.get(USER_INFO, invoiceId, EVENT_RANGE);
                String partyId = invoice.getInvoice().getOwnerId();
                String shopId = invoice.getInvoice().getShopId();
                Map<String, Long> shops = payouts.get(partyId) != null ? payouts.get(partyId) : new HashMap<>();
                long sum = invoices.get(invoiceId).stream().mapToLong(a -> a).sum();
                shops.merge(shopId, sum, Long::sum);
                payouts.put(partyId, shops);
            } catch (TException e) {
                log.error("Received error when get invoice ", e);
            }
        }
        return payouts;
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
