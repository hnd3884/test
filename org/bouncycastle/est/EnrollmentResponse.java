package org.bouncycastle.est;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class EnrollmentResponse
{
    private final Store<X509CertificateHolder> store;
    private final long notBefore;
    private final ESTRequest requestToRetry;
    private final Source source;
    
    public EnrollmentResponse(final Store<X509CertificateHolder> store, final long notBefore, final ESTRequest requestToRetry, final Source source) {
        this.store = store;
        this.notBefore = notBefore;
        this.requestToRetry = requestToRetry;
        this.source = source;
    }
    
    public boolean canRetry() {
        return this.notBefore < System.currentTimeMillis();
    }
    
    public Store<X509CertificateHolder> getStore() {
        return this.store;
    }
    
    public long getNotBefore() {
        return this.notBefore;
    }
    
    public ESTRequest getRequestToRetry() {
        return this.requestToRetry;
    }
    
    public Object getSession() {
        return this.source.getSession();
    }
    
    public Source getSource() {
        return this.source;
    }
    
    public boolean isCompleted() {
        return this.requestToRetry == null;
    }
}
