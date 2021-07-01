package com.rbkmoney.registry.payout.worker.model;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class RegistryOperations {
    public MultiValueMap<String, Long> payments = new LinkedMultiValueMap<>();
    public MultiValueMap<String, Long> refunds = new LinkedMultiValueMap<>();
}
