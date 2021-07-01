package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.RegistryPayoutWorkerApplication;
import com.rbkmoney.registry.payout.worker.model.FilesOperations;
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
        FilesOperations filesOperations = rsbParser.parse(inputStream);
        assertEquals(10, filesOperations.getPayments().size());
        assertEquals(1, filesOperations.getRefunds().size());
        assertEquals(97000, filesOperations.getPayments().get("1Tgz70wxfxA").get(0));
        assertEquals(24250, filesOperations.getRefunds().get("1ThpZ6eiyh6").get(0), 0);
    }

}
