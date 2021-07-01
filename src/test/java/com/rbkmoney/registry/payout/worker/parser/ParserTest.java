package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.RegistryOperations;
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
        RegistryOperations registryOperations = rsbParser.parse(inputStream);
        assertEquals(5, registryOperations.getPayments().size());
        assertEquals(900, registryOperations.getPayments().get("0").get(0));
        assertEquals(500, registryOperations.getPayments().get("0").get(1));
        assertEquals(1000, registryOperations.getPayments().get("1").get(0));
        assertEquals(600, registryOperations.getPayments().get("1").get(1));
        assertEquals(1100, registryOperations.getPayments().get("2").get(0));
        assertEquals(700, registryOperations.getPayments().get("2").get(1));
        assertEquals(1200, registryOperations.getPayments().get("3").get(0));
        assertEquals(800, registryOperations.getPayments().get("3").get(1));
        assertEquals(1300, registryOperations.getPayments().get("4").get(0));
        assertEquals(900, registryOperations.getPayments().get("4").get(1));
        assertEquals(5, registryOperations.getRefunds().size());
        assertEquals(200, registryOperations.getRefunds().get("-1").get(0));
        assertEquals(300, registryOperations.getRefunds().get("-1").get(1));
        assertEquals(100, registryOperations.getRefunds().get("0").get(0));
        assertEquals(200, registryOperations.getRefunds().get("0").get(1));
        assertEquals(0, registryOperations.getRefunds().get("1").get(0));
        assertEquals(100, registryOperations.getRefunds().get("1").get(1));
        assertEquals(100, registryOperations.getRefunds().get("2").get(0));
        assertEquals(200, registryOperations.getRefunds().get("3").get(0));
        assertEquals(100, registryOperations.getRefunds().get("3").get(1));
    }

}
