package org.bouncycastle.cert;

import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;

public class X509CRLEntryHolder
{
    private TBSCertList.CRLEntry entry;
    private GeneralNames ca;
    
    X509CRLEntryHolder(final TBSCertList.CRLEntry entry, final boolean b, final GeneralNames ca) {
        this.entry = entry;
        this.ca = ca;
        if (b && entry.hasExtensions()) {
            final Extension extension = entry.getExtensions().getExtension(Extension.certificateIssuer);
            if (extension != null) {
                this.ca = GeneralNames.getInstance((Object)extension.getParsedValue());
            }
        }
    }
    
    public BigInteger getSerialNumber() {
        return this.entry.getUserCertificate().getValue();
    }
    
    public Date getRevocationDate() {
        return this.entry.getRevocationDate().getDate();
    }
    
    public boolean hasExtensions() {
        return this.entry.hasExtensions();
    }
    
    public GeneralNames getCertificateIssuer() {
        return this.ca;
    }
    
    public Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Extensions extensions = this.entry.getExtensions();
        if (extensions != null) {
            return extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public Extensions getExtensions() {
        return this.entry.getExtensions();
    }
    
    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.entry.getExtensions());
    }
    
    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.entry.getExtensions());
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.entry.getExtensions());
    }
}
