package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.damsel.payment_processing.Invoice;
import com.rbkmoney.damsel.payment_processing.InvoicingSrv;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import org.apache.thrift.TException;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class MockTransactions {

    @MockBean
    private InvoicingSrv.Iface invoicingClient;

    public Transactions createTransactions() throws TException, IOException {
        MultiValueMap<String, Float> payments = new LinkedMultiValueMap<>();
        MultiValueMap<String, Float> refunds = new LinkedMultiValueMap<>();
        for (int i = 0; i < 5; i++) {
            payments.put(String.valueOf(i), Arrays.asList((float) i, (float) i + 1));
            refunds.put(String.valueOf(i - 1), Arrays.asList((float) i - 1, (float) i - 2));
        }
        Transactions transactions = new Transactions();
        transactions.setInvoicePayments(payments);
        transactions.setInvoiceRefunds(refunds);
        mockPayment(transactions.getInvoicePayments(), transactions.getInvoiceRefunds());
        return transactions;
    }

    private void mockPayment(MultiValueMap<String, Float> invoicePaym,
                             MultiValueMap<String, Float> invoiceRef) throws TException, IOException {
        mockPayment(invoicePaym);
        mockPayment(invoiceRef);
    }

    private void mockPayment(MultiValueMap<String, Float> map) throws TException, IOException {
        for (String key : map.keySet()) {
            if (Integer.parseInt(key) < 3) {
                when(invoicingClient.get(HgClientService.USER_INFO, key,
                        HgClientService.EVENT_RANGE))
                        .thenReturn(new Invoice().setInvoice(buildInvoice(
                                "testPartyId" + Integer.parseInt(key),
                                "testShopId" + Integer.parseInt(key),
                                key)));
            } else {
                when(invoicingClient.get(HgClientService.USER_INFO, key,
                        HgClientService.EVENT_RANGE))
                        .thenReturn(new Invoice().setInvoice(buildInvoice(
                                "testPartyId" + (Integer.parseInt(key) - 3),
                                "testShopId" + (Integer.parseInt(key) - 2),
                                key)));
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
