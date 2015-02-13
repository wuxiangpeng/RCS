package com.cmcc.rcs.cpm;

public interface CPMListener {
    void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent);
    void processIOException(IOExceptionEvent exceptionEvent);
    void processRequest(RequestEvent requestEvent);
    void processResponse(ResponseEvent responseEvent);
    void processTimeout(TimeoutEvent timeoutEvent);
    void processTransactionTerminated(
            TransactionTerminatedEvent transactionTerminatedEvent);
}

