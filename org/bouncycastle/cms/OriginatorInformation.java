package org.bouncycastle.cms;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Set;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import java.util.ArrayList;
import org.bouncycastle.util.Store;
import org.bouncycastle.asn1.cms.OriginatorInfo;

public class OriginatorInformation
{
    private OriginatorInfo originatorInfo;
    
    OriginatorInformation(final OriginatorInfo originatorInfo) {
        this.originatorInfo = originatorInfo;
    }
    
    public Store getCertificates() {
        final ASN1Set certificates = this.originatorInfo.getCertificates();
        if (certificates != null) {
            final ArrayList list = new ArrayList(certificates.size());
            final Enumeration objects = certificates.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1Sequence) {
                    list.add((Object)new X509CertificateHolder(Certificate.getInstance((Object)asn1Primitive)));
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    public Store getCRLs() {
        final ASN1Set crLs = this.originatorInfo.getCRLs();
        if (crLs != null) {
            final ArrayList list = new ArrayList(crLs.size());
            final Enumeration objects = crLs.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1Sequence) {
                    list.add((Object)new X509CRLHolder(CertificateList.getInstance((Object)asn1Primitive)));
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    public OriginatorInfo toASN1Structure() {
        return this.originatorInfo;
    }
}
