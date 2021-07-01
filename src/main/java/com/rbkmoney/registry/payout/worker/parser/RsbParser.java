package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.model.RegistryOperations;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RsbParser {

    private static final String NUMERIC_PATTERN = "-?\\d+(,\\d+)?";

    public RegistryOperations parse(InputStream inputStream) {
        RegistryOperations registryOperations = new RegistryOperations();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIter = sheet.rowIterator();
            MultiValueMap<String, Long> payments = new LinkedMultiValueMap<>();
            MultiValueMap<String, Long> refunds = new LinkedMultiValueMap<>();
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                String merchTrxId = row.getCell(10).getStringCellValue();
                String paymentAmount = row.getCell(4).getStringCellValue();
                if (!merchTrxId.isEmpty() && isNumeric(paymentAmount)) {
                    long amount = Long.parseLong(paymentAmount.replace(",", ""));
                    String invoiceId = merchTrxId.substring(0, merchTrxId.indexOf("."));
                    if (amount > 0) {
                        payments.add(invoiceId, amount);
                    } else {
                        refunds.add(invoiceId, Math.abs(amount));
                    }
                }
            }
            registryOperations.setPayments(payments);
            registryOperations.setRefunds(refunds);
        } catch (EmptyFileException | InvalidFormatException | IOException ex) {
            log.error("Failed parse registry.", ex);
        }
        return registryOperations;
    }

    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile(NUMERIC_PATTERN);
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

}
