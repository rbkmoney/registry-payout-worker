package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.model.Transaction;
import com.rbkmoney.registry.payout.worker.model.TransactionsStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RsbParser {

    private static final String NUMERIC_PATTERN = "-?\\d+(,\\d+)?";

    public TransactionsStorage parse(InputStream inputStream) {
        List<Transaction> payments = new ArrayList<>();
        List<Transaction> refunds = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIter = sheet.rowIterator();
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                String merchTrxId = row.getCell(10).getStringCellValue();
                String paymentAmount = row.getCell(4).getStringCellValue();
                if (!merchTrxId.isEmpty() && isNumeric(paymentAmount)) {
                    Transaction transaction = Transaction.builder()
                            .id(merchTrxId.substring(0, merchTrxId.indexOf(".")))
                            .amount(getAmount(paymentAmount))
                            .currency(row.getCell(6).getStringCellValue())
                            .build();
                    if (transaction.getAmount() > 0) {
                        payments.add(transaction);
                    } else {
                        refunds.add(transaction);
                    }
                }
            }
        } catch (EmptyFileException | InvalidFormatException | IOException ex) {
            log.error("Failed parse registry.", ex);
        }
        return TransactionsStorage.builder()
                .payments(payments)
                .refunds(refunds)
                .build();
    }

    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile(NUMERIC_PATTERN);
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private Long getAmount(String paymentAmount) {
        return (long) Double.parseDouble(paymentAmount.replace(",", ".")) * 100;
    }

}
