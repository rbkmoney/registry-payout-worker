package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.model.FilesOperations;

import java.io.InputStream;

public interface RegistryParser {

    boolean isParse(String provider);

    FilesOperations parse(InputStream inputStream);
}
