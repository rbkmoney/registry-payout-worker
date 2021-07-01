package com.rbkmoney.registry.payout.worker.service;

import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.ShopAccount;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.registry.payout.worker.model.FilesOperations;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import org.apache.thrift.TException;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MockTransactions {

    @MockBean
    private InvoicingSrv.Iface invoicingClient;

    @MockBean
    private PartyManagementSrv.Iface partyManagementClient;

    public FilesOperations createTransactions() throws TException, IOException {
        MultiValueMap<String, Long> payments = new LinkedMultiValueMap<>();
        MultiValueMap<String, Long> refunds = new LinkedMultiValueMap<>();
        for (int i = 0; i < 5; i++) {
            payments.put(String.valueOf(i), Arrays.asList((long) i + 5, (long) i + 9));
            refunds.put(String.valueOf(i - 1), Arrays.asList((long) i - 2, (long) i - 3));
        }
        FilesOperations filesOperations = new FilesOperations();
        filesOperations.setPayments(payments);
        filesOperations.setRefunds(refunds);
        mockPayment(filesOperations.getPayments(), filesOperations.getRefunds());
        mockPartyManagement();
        return filesOperations;
    }

    private void mockPayment(MultiValueMap<String, Long> invoicePaym,
                             MultiValueMap<String, Long> invoiceRef) throws TException, IOException {
        mockPayment(invoicePaym);
        mockPayment(invoiceRef);
    }

    private void mockPayment(MultiValueMap<String, Long> map) throws TException, IOException {
        for (String key : map.keySet()) {
            if (Integer.parseInt(key) < 3) {
                when(invoicingClient.get(InvoicingHgClientService.USER_INFO, key,
                        InvoicingHgClientService.EVENT_RANGE))
                        .thenReturn(new Invoice().setInvoice(buildInvoice(
                                "testPartyId" + Integer.parseInt(key),
                                "testShopId" + Integer.parseInt(key),
                                key)));
            } else {
                when(invoicingClient.get(InvoicingHgClientService.USER_INFO, key,
                        InvoicingHgClientService.EVENT_RANGE))
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

    private void mockPartyManagement() throws TException {
        when(partyManagementClient.getShopAccount(any(), any(), any()))
                .thenReturn(new ShopAccount().setCurrency(new CurrencyRef("RUB")));

    }
}
