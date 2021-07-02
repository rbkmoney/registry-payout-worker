package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class HgClientServiceTest extends MockTransactions {

    @Autowired
    private InvoicingHgClientService hgClientService;

    @Test
    void testHgClientService() throws TException, IOException {
        PayoutStorage payoutStorage = hgClientService.getPayouts(ctreateOperations(),
                new PayoutStorage());
        assertEquals(6, payoutStorage.getPayouts().size());
        assertEquals(-500, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId5")
                .shopId("testShopId6")
                .build()));
        assertEquals(1100, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId0")
                .build()));
        assertEquals(1700, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId1")
                .build()));
        assertEquals(1500, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId1")
                .build()));
        assertEquals(2200, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId2")
                .build()));
        assertEquals(1700, payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId2")
                .shopId("testShopId2")
                .build()));
        assertNull(payoutStorage.getPayouts().get(PayoutStorage.PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId2")
                .build()));
    }

}
