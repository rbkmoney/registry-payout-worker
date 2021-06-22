package com.rbkmoney.registry.payout.worker.exception;

public class RegistryPayoutWorkerException extends RuntimeException {

    public RegistryPayoutWorkerException() {
    }

    public RegistryPayoutWorkerException(String message) {
        super(message);
    }

    public RegistryPayoutWorkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryPayoutWorkerException(Throwable cause) {
        super(cause);
    }

}
