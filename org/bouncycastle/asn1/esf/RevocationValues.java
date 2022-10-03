package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class RevocationValues extends ASN1Object
{
    private ASN1Sequence crlVals;
    private ASN1Sequence ocspVals;
    private OtherRevVals otherRevVals;
    
    public static RevocationValues getInstance(final Object o) {
        if (o instanceof RevocationValues) {
            return (RevocationValues)o;
        }
        if (o != null) {
            return new RevocationValues(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private RevocationValues(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    final ASN1Sequence crlVals = (ASN1Sequence)asn1TaggedObject.getObject();
                    final Enumeration objects2 = crlVals.getObjects();
                    while (objects2.hasMoreElements()) {
                        CertificateList.getInstance(objects2.nextElement());
                    }
                    this.crlVals = crlVals;
                    continue;
                }
                case 1: {
                    final ASN1Sequence ocspVals = (ASN1Sequence)asn1TaggedObject.getObject();
                    final Enumeration objects3 = ocspVals.getObjects();
                    while (objects3.hasMoreElements()) {
                        BasicOCSPResponse.getInstance(objects3.nextElement());
                    }
                    this.ocspVals = ocspVals;
                    continue;
                }
                case 2: {
                    this.otherRevVals = OtherRevVals.getInstance(asn1TaggedObject.getObject());
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("invalid tag: " + asn1TaggedObject.getTagNo());
                }
            }
        }
    }
    
    public RevocationValues(final CertificateList[] array, final BasicOCSPResponse[] array2, final OtherRevVals otherRevVals) {
        if (null != array) {
            this.crlVals = new DERSequence(array);
        }
        if (null != array2) {
            this.ocspVals = new DERSequence(array2);
        }
        this.otherRevVals = otherRevVals;
    }
    
    public CertificateList[] getCrlVals() {
        if (null == this.crlVals) {
            return new CertificateList[0];
        }
        final CertificateList[] array = new CertificateList[this.crlVals.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = CertificateList.getInstance(this.crlVals.getObjectAt(i));
        }
        return array;
    }
    
    public BasicOCSPResponse[] getOcspVals() {
        if (null == this.ocspVals) {
            return new BasicOCSPResponse[0];
        }
        final BasicOCSPResponse[] array = new BasicOCSPResponse[this.ocspVals.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = BasicOCSPResponse.getInstance(this.ocspVals.getObjectAt(i));
        }
        return array;
    }
    
    public OtherRevVals getOtherRevVals() {
        return this.otherRevVals;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (null != this.crlVals) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.crlVals));
        }
        if (null != this.ocspVals) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.ocspVals));
        }
        if (null != this.otherRevVals) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.otherRevVals.toASN1Primitive()));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
