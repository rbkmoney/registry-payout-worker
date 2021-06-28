package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.Payouts;
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
    private HgClientService hgClientService;

    @Test
    void testHgClientService() throws TException, IOException {
        Payouts payouts = hgClientService.getPayouts(createTransactions());
        assertEquals(8, payouts.getPayouts().size());
        assertEquals(-25, payouts.getPayouts().get("testPartyId12").get("testShopId12"));
        assertEquals(2, payouts.getPayouts().get("testPartyId1").size());
        assertEquals(4, payouts.getPayouts().get("testPartyId1").get("testShopId2"));
        assertEquals(4, payouts.getPayouts().get("testPartyId1").get("testShopId2"));
        assertEquals(2, payouts.getPayouts().get("testPartyId2").size());
        assertEquals(8, payouts.getPayouts().get("testPartyId2").get("testShopId2"));
        assertEquals(1, payouts.getPayouts().get("testPartyId0").size());
        assertNull(payouts.getPayouts().get("testPartyId0").get("testShopId4"));
        assertEquals(4, payouts.getPayouts().get("testPartyId0").get("testShopId1"));
    }

}
