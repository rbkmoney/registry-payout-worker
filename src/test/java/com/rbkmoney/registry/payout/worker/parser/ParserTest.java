package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = RegistryPayoutWorkerApplication.class)
public class ParserTest {

    @Autowired
    RsbParser rsbParser;

    @Test
    void testRsbParser() throws FileNotFoundException {
        File file = new File("src/test/resources/test.xls");
        InputStream inputStream = new FileInputStream(file);
        Transactions transactions = rsbParser.parse(inputStream);
        assertEquals(10, transactions.getInvoicePayments().size());
        assertEquals(1, transactions.getInvoiceRefunds().size());
        assertEquals(97000, transactions.getInvoicePayments().get("1Tgz70wxfxA").get(0));
        assertEquals(24250, transactions.getInvoiceRefunds().get("1ThpZ6eiyh6").get(0), 0);
    }

}
