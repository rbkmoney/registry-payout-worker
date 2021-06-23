package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.model.Transactions;

import java.io.InputStream;

public interface RegistryParser {

    boolean isParse(String provider);

    Transactions parse(InputStream inputStream);
}
