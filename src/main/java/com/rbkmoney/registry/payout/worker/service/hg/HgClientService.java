package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.registry.payout.worker.model.PayoutStorage;
import com.rbkmoney.registry.payout.worker.model.FilesOperations;

public interface HgClientService {

    boolean isGetPayouts(String provider);

    PayoutStorage getPayouts(FilesOperations filesOperations);
}
