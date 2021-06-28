package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HgClientService {

    private final InvoicingSrv.Iface invoicing;
    public static final UserInfo USER_INFO = new UserInfo(
            "registry-payout-worker",
            UserType.internal_user(new InternalUser()));
    public static final EventRange EVENT_RANGE = new EventRange().setLimit(1);

    public Payouts get(Transactions transactions) throws TException {
        Payouts payouts = new Payouts();
        Map<String, Map<String, Float>> invoicePayment =
                mapPartyShop(transactions.getInvoicePayments(), payouts);
        Map<String, Map<String, Float>> invoiceRefund =
                mapPartyShop(setNegativeNumber(transactions.getInvoiceRefunds()), payouts);
        payouts.putAll(invoicePayment, invoiceRefund);
        return payouts;
    }

    private Map<String, Map<String, Float>> mapPartyShop(MultiValueMap<String, Float> map,
                                                         Payouts payouts) throws TException {
        Map<String, Map<String, Float>> partyMap = payouts.getPayouts();
        for (String key : map.keySet()) {
            Invoice invoice = invoicing.get(USER_INFO, key, EVENT_RANGE);
            String party = invoice.getInvoice().getOwnerId();
            String shop = invoice.getInvoice().getShopId();
            System.out.println(key + ": " + map.get(key) + ": " + party + ": " + shop);
            Map<String, Float> shopMap = partyMap.get(party) != null ? partyMap.get(party) : new HashMap<>();
            float sum = (float) map.get(key).stream().mapToDouble(a -> a).sum();
            shopMap.merge(shop, sum, Float::sum);
            partyMap.put(party, shopMap);
        }
        return partyMap;
    }

    private MultiValueMap<String, Float> setNegativeNumber(MultiValueMap<String, Float> invoiceRefund) {
        for (String key : invoiceRefund.keySet()) {
            List<Float> list =
                    invoiceRefund.get(key).stream().map(v -> -v).collect(Collectors.toList());
            invoiceRefund.put(key, list);
        }
        return invoiceRefund;
    }

}
