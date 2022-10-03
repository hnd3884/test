package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.RevokedInfo;

public class RevokedStatus implements CertificateStatus
{
    RevokedInfo info;
    
    public RevokedStatus(final RevokedInfo info) {
        this.info = info;
    }
    
    public RevokedStatus(final Date date, final int n) {
        this.info = new RevokedInfo(new ASN1GeneralizedTime(date), CRLReason.lookup(n));
    }
    
    public Date getRevocationTime() {
        return OCSPUtils.extractDate(this.info.getRevocationTime());
    }
    
    public boolean hasRevocationReason() {
        return this.info.getRevocationReason() != null;
    }
    
    public int getRevocationReason() {
        if (this.info.getRevocationReason() == null) {
            throw new IllegalStateException("attempt to get a reason where none is available");
        }
        return this.info.getRevocationReason().getValue().intValue();
    }
}
