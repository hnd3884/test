package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.util.Store;
import java.util.ArrayList;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.List;

public class OriginatorInfoGenerator
{
    private final List origCerts;
    private final List origCRLs;
    
    public OriginatorInfoGenerator(final X509CertificateHolder x509CertificateHolder) {
        this.origCerts = new ArrayList(1);
        this.origCRLs = null;
        this.origCerts.add(x509CertificateHolder.toASN1Structure());
    }
    
    public OriginatorInfoGenerator(final Store store) throws CMSException {
        this(store, null);
    }
    
    public OriginatorInfoGenerator(final Store store, final Store store2) throws CMSException {
        this.origCerts = CMSUtils.getCertificatesFromStore(store);
        if (store2 != null) {
            this.origCRLs = CMSUtils.getCRLsFromStore(store2);
        }
        else {
            this.origCRLs = null;
        }
    }
    
    public OriginatorInformation generate() {
        if (this.origCRLs != null) {
            return new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), CMSUtils.createDerSetFromList(this.origCRLs)));
        }
        return new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), (ASN1Set)null));
    }
}
