package org.jscep.client;

import org.jscep.transaction.TransactionId;
import java.security.cert.CertStore;
import org.jscep.transaction.FailInfo;

public final class EnrollmentResponse
{
    private final FailInfo failInfo;
    private final CertStore certStore;
    private final TransactionId transId;
    
    public EnrollmentResponse(final TransactionId transId) {
        this(transId, null, null);
    }
    
    public EnrollmentResponse(final TransactionId transId, final FailInfo failInfo) {
        this(transId, null, failInfo);
    }
    
    public EnrollmentResponse(final TransactionId transId, final CertStore certStore) {
        this(transId, certStore, null);
    }
    
    private EnrollmentResponse(final TransactionId transId, final CertStore certStore, final FailInfo failInfo) {
        this.transId = transId;
        this.certStore = certStore;
        this.failInfo = failInfo;
    }
    
    public boolean isPending() {
        return this.failInfo == null && this.certStore == null;
    }
    
    public boolean isFailure() {
        return this.failInfo != null;
    }
    
    public boolean isSuccess() {
        return this.certStore != null;
    }
    
    public TransactionId getTransactionId() {
        return this.transId;
    }
    
    public CertStore getCertStore() {
        if (this.isSuccess()) {
            return this.certStore;
        }
        throw new IllegalStateException();
    }
    
    public FailInfo getFailInfo() {
        if (this.isFailure()) {
            return this.failInfo;
        }
        throw new IllegalStateException();
    }
}
