package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.registry.payout.worker.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingHgClientService {

    private final InvoicingSrv.Iface invoicing;
    public static final UserInfo USER_INFO = new UserInfo(
            "registry-payout-worker",
            UserType.internal_user(new InternalUser()));
    public static final EventRange EVENT_RANGE = new EventRange().setLimit(1);

    public Map<PartyShop, List<Transaction>> groupTransactionsByPartyShop(TransactionsStorage transactionsStorage) {
        Map<PartyShop, List<Transaction>> partyShopListMap = new HashMap<>();
        Map<PartyShop, List<Transaction>> payments =
                mapPartyShop(transactionsStorage.getPayments(), partyShopListMap);
        Map<PartyShop, List<Transaction>> refunds =
                mapPartyShop(transactionsStorage.getRefunds(), partyShopListMap);
        partyShopListMap.putAll(payments);
        partyShopListMap.putAll(refunds);
        return partyShopListMap;
    }

    private Map<PartyShop, List<Transaction>> mapPartyShop(List<Transaction> transactions,
                                                           Map<PartyShop, List<Transaction>> partyShopListMap) {
        for (Transaction transaction : transactions) {
            try {
                Invoice invoice = invoicing.get(USER_INFO, transaction.getId(), EVENT_RANGE);
                PartyShop partyShop = PartyShop.builder()
                        .partyId(invoice.getInvoice().getOwnerId())
                        .shopId(invoice.getInvoice().getShopId())
                        .build();
                if (partyShopListMap.containsKey(partyShop)) {
                    partyShopListMap.get(partyShop).add(transaction);
                } else {
                    partyShopListMap.put(partyShop, new ArrayList<>(Collections.singletonList(transaction)));
                }
            } catch (TException e) {
                log.error("Received error when get invoice ", e);
            }
        }
        return partyShopListMap;
    }

}
