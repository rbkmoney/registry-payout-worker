package com.rbkmoney.registry.payout.worker.config;

import com.rbkmoney.damsel.payment_processing.InvoicingSrv;
import com.rbkmoney.payout.manager.PayoutManagementSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ServiceConfiguration {

    @Bean
    public InvoicingSrv.Iface invoicingClient(
            @Value("${service.invoicing.url}") Resource resource,
            @Value("${service.invoicing.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(InvoicingSrv.Iface.class);
    }

    @Bean
    public PayoutManagementSrv.Iface payoutManagerClient(
            @Value("${service.payoutmgmt.url}") Resource resource,
            @Value("${service.payoutmgmt.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(PayoutManagementSrv.Iface.class);
    }
}
