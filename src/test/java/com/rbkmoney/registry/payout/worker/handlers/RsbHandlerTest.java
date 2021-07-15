package com.rbkmoney.registry.payout.worker.handlers;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.handler.RsbRegistryPayoutPayoutHandler;
import com.rbkmoney.registry.payout.worker.model.*;
import com.rbkmoney.registry.payout.worker.service.MockTransactions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.registry.payout.worker.mapper.PayoutMapper.mapTransactionToPayout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class RsbHandlerTest extends MockTransactions {

    private static final String TEST_FILE_NAME = "src/test/resources/test.xls";
    @Autowired
    private RsbRegistryPayoutPayoutHandler rsbRegistryPayoutPayoutHandler;

    @Test
    void testPayoutStorageFromRegistryFile() throws IOException {
        File file = new File(TEST_FILE_NAME);
        PayoutStorage payoutStorage = new PayoutStorage();
        Map<PartyShop, List<Transaction>> transactions =
                rsbRegistryPayoutPayoutHandler.handle(new FileInputStream(file));
        payoutStorage.getPayouts().putAll(mapTransactionToPayout(transactions));
        assertEquals(6, payoutStorage.getPayouts().size());
        assertEquals(-500, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId5")
                .shopId("testShopId6")
                .build())
                .getAmount());
        assertEquals(1100, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId0")
                .build())
                .getAmount());
        assertEquals(1700, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId1")
                .build())
                .getAmount());
        assertEquals(1500, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId1")
                .build())
                .getAmount());
        assertEquals(2200, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId1")
                .shopId("testShopId2")
                .build())
                .getAmount());
        assertEquals(1700, payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId2")
                .shopId("testShopId2")
                .build())
                .getAmount());
        assertNull(payoutStorage.getPayouts().get(PartyShop.builder()
                .partyId("testPartyId0")
                .shopId("testShopId2")
                .build()));
    }

}
