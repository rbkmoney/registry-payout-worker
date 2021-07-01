package com.rbkmoney.registry.payout.worker.parser;

import com.rbkmoney.registry.payout.worker.model.FilesOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class SkipParser implements RegistryParser {

    @Override
    public boolean isParse(String provider) {
        return false;
    }

    @Override
    public FilesOperations parse(InputStream inputStream) {
        log.error("No RegistryParsers available to parse file.");
        return new FilesOperations();
    }

}
