package com.adventnet.sym.server.mdm.certificates.scep.request;

import org.jscep.transport.request.Operation;

public class MdmScepRequest
{
    private final long enrollmentRequestId;
    private final long customerId;
    private final Operation operation;
    private final byte[] pkiMessage;
    
    public MdmScepRequest(final long enrollmentRequestId, final long customerId, final Operation operation, final byte[] pkiMessage) {
        this.enrollmentRequestId = enrollmentRequestId;
        this.customerId = customerId;
        this.operation = operation;
        this.pkiMessage = pkiMessage;
    }
    
    public long getEnrollmentRequestId() {
        return this.enrollmentRequestId;
    }
    
    public long getCustomerId() {
        return this.customerId;
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    
    public byte[] getPkiMessage() {
        return this.pkiMessage;
    }
}
