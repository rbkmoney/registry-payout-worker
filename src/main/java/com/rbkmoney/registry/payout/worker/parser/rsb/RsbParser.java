package com.rbkmoney.registry.payout.worker.parser.rsb;

import com.rbkmoney.registry.payout.worker.exception.RegistryPayoutWorkerException;
import com.rbkmoney.registry.payout.worker.model.Transactions;
import com.rbkmoney.registry.payout.worker.parser.RegistryParser;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

import static com.rbkmoney.registry.payout.worker.constant.PathToReadConstant.RSB;

@Component
public class RsbParser implements RegistryParser {

    @Override
    public boolean isParse(String provider) {
        return RSB.equals(provider);
    }

    @Override
    public Transactions parse(InputStream inputStream) {
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException | InvalidFormatException ex) {
            throw new RegistryPayoutWorkerException("Failed create Workbook: ", ex);
        }
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIter = sheet.rowIterator();
        MultiValueMap<String, Float> payments = new LinkedMultiValueMap<>();
        MultiValueMap<String, Float> refunds = new LinkedMultiValueMap<>();
        while (rowIter.hasNext()) {
            HSSFRow row = (HSSFRow) rowIter.next();
            String merchTrxId = row.getCell(10).getStringCellValue();
            String paymentAmount = row.getCell(4).getStringCellValue();
            if (!merchTrxId.isEmpty() && isNumeric(paymentAmount)) {
                float amount = Float.parseFloat(paymentAmount.replace(",", "."));
                String invoice = merchTrxId.substring(0, merchTrxId.indexOf("."));
                if (amount > 0) {
                    payments.add(invoice, amount);
                } else {
                    refunds.add(invoice, Math.abs(amount));
                }
            }
        }
        Transactions transactions = new Transactions();
        transactions.setInvoicePayments(payments);
        transactions.setInvoiceRefunds(refunds);
        return transactions;
    }

    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(,\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

}
