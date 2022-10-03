package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.crmf.CertId;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class RevRepContent extends ASN1Object
{
    private ASN1Sequence status;
    private ASN1Sequence revCerts;
    private ASN1Sequence crls;
    
    private RevRepContent(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.status = ASN1Sequence.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            if (instance.getTagNo() == 0) {
                this.revCerts = ASN1Sequence.getInstance(instance, true);
            }
            else {
                this.crls = ASN1Sequence.getInstance(instance, true);
            }
        }
    }
    
    public static RevRepContent getInstance(final Object o) {
        if (o instanceof RevRepContent) {
            return (RevRepContent)o;
        }
        if (o != null) {
            return new RevRepContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PKIStatusInfo[] getStatus() {
        final PKIStatusInfo[] array = new PKIStatusInfo[this.status.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = PKIStatusInfo.getInstance(this.status.getObjectAt(i));
        }
        return array;
    }
    
    public CertId[] getRevCerts() {
        if (this.revCerts == null) {
            return null;
        }
        final CertId[] array = new CertId[this.revCerts.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertId.getInstance(this.revCerts.getObjectAt(i));
        }
        return array;
    }
    
    public CertificateList[] getCrls() {
        if (this.crls == null) {
            return null;
        }
        final CertificateList[] array = new CertificateList[this.crls.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertificateList.getInstance(this.crls.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.status);
        this.addOptional(asn1EncodableVector, 0, this.revCerts);
        this.addOptional(asn1EncodableVector, 1, this.crls);
        return new DERSequence(asn1EncodableVector);
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final int n, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, n, asn1Encodable));
        }
    }
}
