package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Map;

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
        Map<String, Long> registryOperations = rsbParser.parse(inputStream);
        assertEquals(6, registryOperations.size());
        assertEquals(-500, registryOperations.get("-1"));
        assertEquals(1100, registryOperations.get("0"));
        assertEquals(1500, registryOperations.get("1"));
        assertEquals(1700, registryOperations.get("2"));
        assertEquals(1700, registryOperations.get("3"));
        assertEquals(2200, registryOperations.get("4"));
    }

}
