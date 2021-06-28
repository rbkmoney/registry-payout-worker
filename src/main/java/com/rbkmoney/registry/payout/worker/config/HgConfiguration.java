package com.rbkmoney.registry.payout.worker.config;

import com.rbkmoney.damsel.payment_processing.InvoicingSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class HgConfiguration {

    @Bean
    public InvoicingSrv.Iface invoicingClient(@Value("${hg.invoicing.url}") Resource resource,
                                              @Value("${hg.invoicing.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(InvoicingSrv.Iface.class);
    }
}
