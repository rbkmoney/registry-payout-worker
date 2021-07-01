package com.rbkmoney.registry.payout.worker.handler;

import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.parser.RsbParser;
import com.rbkmoney.registry.payout.worker.service.hg.InvoicingHgClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
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
    public PayoutStorage handle(InputStream inputStream, PayoutStorage payoutStorage) {
        Map<String, Long> registryOperations = rsbParser.parse(inputStream);
        log.info("Read {} transactions", registryOperations.size());
        return invoicingHgClientService.getPayouts(registryOperations, payoutStorage);
    }
}
