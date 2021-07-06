package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.payout.manager.PayoutParams;
import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.*;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import com.rbkmoney.registry.payout.worker.service.payoutmngr.PayoutManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

import static com.rbkmoney.registry.payout.worker.mapper.PayoutMapper.mapTransactionToPayout;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class PayoutManagerServiceTest extends MockTransactions {

    @Autowired
    private InvoicingHgClientService hgClientService;

    @Autowired
    private PayoutManagerService payoutManagerService;

    @Test
    void testPayoutManagerClientService() throws IOException {
        Map<PartyShop, List<Transaction>> transaction =
                hgClientService.groupTransactionsByPartyShop(ctreateOperations());
        PayoutStorage payoutStorage = new PayoutStorage();
        payoutStorage.getPayouts().putAll(mapTransactionToPayout(transaction));
        List<PayoutParams> payoutParams = payoutManagerService.createPayouts(payoutStorage);
        Collections.sort(payoutParams);
        assertEquals(5, payoutParams.size());
        assertEquals(1700, payoutParams.get(4).getCash().getAmount());
        assertEquals("testShopId2", payoutParams.get(4).getShopParams().getShopId());
        assertEquals("testPartyId2", payoutParams.get(4).getShopParams().getPartyId());
        assertEquals(2200, payoutParams.get(3).getCash().getAmount());
        assertEquals("testShopId2", payoutParams.get(3).getShopParams().getShopId());
        assertEquals("testPartyId1", payoutParams.get(3).getShopParams().getPartyId());
        assertEquals(1500, payoutParams.get(2).getCash().getAmount());
        assertEquals("testShopId1", payoutParams.get(2).getShopParams().getShopId());
        assertEquals("testPartyId1", payoutParams.get(2).getShopParams().getPartyId());
        assertEquals(1700, payoutParams.get(1).getCash().getAmount());
        assertEquals("testShopId1", payoutParams.get(1).getShopParams().getShopId());
        assertEquals("testPartyId0", payoutParams.get(1).getShopParams().getPartyId());
        assertEquals(1100, payoutParams.get(0).getCash().getAmount());
        assertEquals("testShopId0", payoutParams.get(0).getShopParams().getShopId());
        assertEquals("testPartyId0", payoutParams.get(0).getShopParams().getPartyId());
    }

}
