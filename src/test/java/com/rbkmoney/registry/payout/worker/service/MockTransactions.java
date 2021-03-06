package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.damsel.payment_processing.Invoice;
import com.rbkmoney.damsel.payment_processing.InvoicingSrv;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.registry.payout.worker.model.TransactionsStorage;
import com.rbkmoney.registry.payout.worker.parser.RsbParser;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.*;

import static org.mockito.Mockito.when;

public class MockTransactions {

    @MockBean
    private InvoicingSrv.Iface invoicingClient;

    @Autowired
    private RsbParser rsbParser;

    @BeforeEach
    public void init() throws TException, IOException {
        mockOperations();
    }


    public TransactionsStorage ctreateOperations() throws IOException {
        File file = new File("src/test/resources/test.xls");
        InputStream inputStream = new FileInputStream(file);
        TransactionsStorage transactionsStorage = rsbParser.parse(inputStream);
        return transactionsStorage;
    }

    private void mockOperations() throws TException, IOException {
        for (int i = 0; i <= 8; i++) {
            if (i < 3) {
                when(invoicingClient.get(InvoicingHgClientService.USER_INFO, String.valueOf(i),
                        InvoicingHgClientService.EVENT_RANGE))
                        .thenReturn(new Invoice().setInvoice(buildInvoice(
                                "testPartyId" + i,
                                "testShopId" + i,
                                String.valueOf(i))));
            } else {
                when(invoicingClient.get(InvoicingHgClientService.USER_INFO, String.valueOf(i),
                        InvoicingHgClientService.EVENT_RANGE))
                        .thenReturn(new Invoice().setInvoice(buildInvoice(
                                "testPartyId" + (i - 3),
                                "testShopId" + (i - 2),
                                String.valueOf(i))));
            }
        }

    }

    private static com.rbkmoney.damsel.domain.Invoice buildInvoice(
            String partyId,
            String shopId,
            String invoiceId) throws IOException {
        MockTBaseProcessor thriftBaseProcessor = new MockTBaseProcessor(MockMode.RANDOM, 15, 1);
        return thriftBaseProcessor.process(
                new com.rbkmoney.damsel.domain.Invoice(),
                new TBaseHandler<>(com.rbkmoney.damsel.domain.Invoice.class))
                .setId(invoiceId)
                .setShopId(shopId)
                .setOwnerId(partyId);
    }

}
