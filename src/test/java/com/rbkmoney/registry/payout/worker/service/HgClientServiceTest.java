package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.Payouts;
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
        Payouts payouts = hgClientService.getPayouts(createTransactions());
        assertEquals(4, payouts.getPayouts().size());
        assertEquals(1, payouts.getPayouts().get("testPartyId-1").size());
        assertEquals(2, payouts.getPayouts().get("testPartyId0").size());
        assertEquals(2, payouts.getPayouts().get("testPartyId1").size());
        assertEquals(1, payouts.getPayouts().get("testPartyId2").size());
        assertEquals(-3, payouts.getPayouts().get("testPartyId-1").get("testShopId-1"));
        assertEquals(0, payouts.getPayouts().get("testPartyId0").get("testShopId0"));
        assertEquals(2, payouts.getPayouts().get("testPartyId0").get("testShopId1"));
        assertEquals(2, payouts.getPayouts().get("testPartyId1").get("testShopId1"));
        assertEquals(9, payouts.getPayouts().get("testPartyId1").get("testShopId2"));
        assertEquals(2, payouts.getPayouts().get("testPartyId2").get("testShopId2"));
        assertNull(payouts.getPayouts().get("testPartyId0").get("testShopId2"));
    }

}
