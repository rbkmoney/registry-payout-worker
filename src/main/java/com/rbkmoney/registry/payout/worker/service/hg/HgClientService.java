package com.rbkmoney.registry.payout.worker.service.hg;

import com.rbkmoney.registry.payout.worker.model.Payouts;
import com.rbkmoney.registry.payout.worker.model.Transactions;

public interface HgClientService {

    boolean isGetPayouts(String provider);

    Payouts getPayouts(Transactions transactions);
}
