package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;
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
    public Payouts getPayouts(Transactions transactions) {
        Payouts payouts = new Payouts();
        Map<String, Map<String, Long>> invoicePayment =
                mapPartyShop(transactions.getInvoicePayments(), payouts);
        Map<String, Map<String, Long>> invoiceRefund =
                mapPartyShop(setNegativeNumber(transactions.getInvoiceRefunds()), payouts);
        payouts.putAll(invoicePayment, invoiceRefund);
        return payouts;
    }

    private Map<String, Map<String, Long>> mapPartyShop(MultiValueMap<String, Long> map,
                                                        Payouts payouts) {
        Map<String, Map<String, Long>> partyMap = payouts.getPayouts();
        for (String key : map.keySet()) {
            try {
                Invoice invoice = invoicing.get(USER_INFO, key, EVENT_RANGE);
                String party = invoice.getInvoice().getOwnerId();
                String shop = invoice.getInvoice().getShopId();
                Map<String, Long> shopMap = partyMap.get(party) != null ? partyMap.get(party) : new HashMap<>();
                long sum = map.get(key).stream().mapToLong(a -> a).sum();
                shopMap.merge(shop, sum, Long::sum);
                partyMap.put(party, shopMap);
            } catch (TException e) {
                log.error("Received error when get invoice ", e);
            }
        }
        return partyMap;
    }

    private MultiValueMap<String, Long> setNegativeNumber(MultiValueMap<String, Long> invoiceRefund) {
        for (String key : invoiceRefund.keySet()) {
            List<Long> list =
                    invoiceRefund.get(key).stream().map(v -> v > 0 ? -v : v).collect(Collectors.toList());
            invoiceRefund.put(key, list);
        }
        return invoiceRefund;
    }

}
