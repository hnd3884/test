package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ocsp.Request;

public class Req
{
    private Request req;
    
    public Req(final Request req) {
        this.req = req;
    }
    
    public CertificateID getCertID() {
        return new CertificateID(this.req.getReqCert());
    }
    
    public Extensions getSingleRequestExtensions() {
        return this.req.getSingleRequestExtensions();
    }
}
