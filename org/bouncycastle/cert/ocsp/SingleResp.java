package org.bouncycastle.cert.ocsp;

import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ocsp.SingleResponse;

public class SingleResp
{
    private SingleResponse resp;
    private Extensions extensions;
    
    public SingleResp(final SingleResponse resp) {
        this.resp = resp;
        this.extensions = resp.getSingleExtensions();
    }
    
    public CertificateID getCertID() {
        return new CertificateID(this.resp.getCertID());
    }
    
    public CertificateStatus getCertStatus() {
        final CertStatus certStatus = this.resp.getCertStatus();
        if (certStatus.getTagNo() == 0) {
            return null;
        }
        if (certStatus.getTagNo() == 1) {
            return new RevokedStatus(RevokedInfo.getInstance((Object)certStatus.getStatus()));
        }
        return new UnknownStatus();
    }
    
    public Date getThisUpdate() {
        return OCSPUtils.extractDate(this.resp.getThisUpdate());
    }
    
    public Date getNextUpdate() {
        if (this.resp.getNextUpdate() == null) {
            return null;
        }
        return OCSPUtils.extractDate(this.resp.getNextUpdate());
    }
    
    public boolean hasExtensions() {
        return this.extensions != null;
    }
    
    public Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public List getExtensionOIDs() {
        return OCSPUtils.getExtensionOIDs(this.extensions);
    }
    
    public Set getCriticalExtensionOIDs() {
        return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
    }
}
