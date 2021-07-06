package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.PartyShop;
import com.rbkmoney.registry.payout.worker.model.Transaction;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class HgClientServiceTest extends MockTransactions {

    @Autowired
    private InvoicingHgClientService hgClientService;

    @Test
    void testHgClientService() throws IOException {
        Map<PartyShop, List<Transaction>> payoutStorage =
                hgClientService.groupTransactionsByPartyShop(ctreateOperations());
        assertEquals(6, payoutStorage.size());
        assertEquals(2, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId5")
                .shopId("testShopId6")
                .build()).size());
        assertEquals(4, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId0")
                .build()).size());
        assertEquals(4, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId1")
                .build()).size());
        assertEquals(4, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId1")
                .build()).size());
        assertEquals(2, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId2")
                .build()).size());
        assertEquals(3, payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId2")
                .shopId("testShopId2")
                .build()).size());
        assertNull(payoutStorage.get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId2")
                .build()));
    }

}
