package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CRLAnnContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private CRLAnnContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static CRLAnnContent getInstance(final Object o) {
        if (o instanceof CRLAnnContent) {
            return (CRLAnnContent)o;
        }
        if (o != null) {
            return new CRLAnnContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CRLAnnContent(final CertificateList list) {
        this.content = new DERSequence(list);
    }
    
    public CertificateList[] getCertificateLists() {
        final CertificateList[] array = new CertificateList[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertificateList.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
