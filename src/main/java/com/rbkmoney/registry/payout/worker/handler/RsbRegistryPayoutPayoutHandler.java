package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.*;
import com.rbkmoney.registry.payout.worker.parser.RsbParser;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.registry.payout.worker.constant.PathToReadConstant.RSB;

@Slf4j
@Component
@RequiredArgsConstructor
public class RsbRegistryPayoutPayoutHandler implements RegistryPayoutHandler {

    private final InvoicingHgClientService invoicingHgClientService;
    private final RsbParser rsbParser;

    @Override
    public boolean isHadle(String provider) {
        return RSB.equals(provider);
    }

    @Override
    public Map<PartyShop, List<Transaction>> handle(InputStream inputStream) {
        TransactionsStorage transactionsStorage = rsbParser.parse(inputStream);
        log.info("Read {} payments and {} refunds", transactionsStorage.getPayments().size(),
                transactionsStorage.getRefunds().size());
        return invoicingHgClientService.groupTransactionsByPartyShop(transactionsStorage);
    }
}
