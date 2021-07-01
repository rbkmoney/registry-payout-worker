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
        PayoutStorage payoutStorage = hgClientService.getPayouts(createTransactions());
        assertEquals(4, payoutStorage.getPayouts().size());
        assertEquals(1, payoutStorage.getPayouts().get("testPartyId-1").size());
        assertEquals(2, payoutStorage.getPayouts().get("testPartyId0").size());
        assertEquals(2, payoutStorage.getPayouts().get("testPartyId1").size());
        assertEquals(1, payoutStorage.getPayouts().get("testPartyId2").size());
        assertEquals(-5, payoutStorage.getPayouts().get("testPartyId-1").get("testShopId-1"));
        assertEquals(11, payoutStorage.getPayouts().get("testPartyId0").get("testShopId0"));
        assertEquals(17, payoutStorage.getPayouts().get("testPartyId0").get("testShopId1"));
        assertEquals(15, payoutStorage.getPayouts().get("testPartyId1").get("testShopId1"));
        assertEquals(22, payoutStorage.getPayouts().get("testPartyId1").get("testShopId2"));
        assertEquals(17, payoutStorage.getPayouts().get("testPartyId2").get("testShopId2"));
        assertNull(payoutStorage.getPayouts().get("testPartyId0").get("testShopId2"));
    }

}
