package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CscaMasterList extends ASN1Object
{
    private ASN1Integer version;
    private Certificate[] certList;
    
    public static CscaMasterList getInstance(final Object o) {
        if (o instanceof CscaMasterList) {
            return (CscaMasterList)o;
        }
        if (o != null) {
            return new CscaMasterList(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CscaMasterList(final ASN1Sequence asn1Sequence) {
        this.version = new ASN1Integer(0L);
        if (asn1Sequence == null || asn1Sequence.size() == 0) {
            throw new IllegalArgumentException("null or empty sequence passed.");
        }
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Incorrect sequence size: " + asn1Sequence.size());
        }
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        final ASN1Set instance = ASN1Set.getInstance(asn1Sequence.getObjectAt(1));
        this.certList = new Certificate[instance.size()];
        for (int i = 0; i < this.certList.length; ++i) {
            this.certList[i] = Certificate.getInstance(instance.getObjectAt(i));
        }
    }
    
    public CscaMasterList(final Certificate[] array) {
        this.version = new ASN1Integer(0L);
        this.certList = this.copyCertList(array);
    }
    
    public int getVersion() {
        return this.version.getValue().intValue();
    }
    
    public Certificate[] getCertStructs() {
        return this.copyCertList(this.certList);
    }
    
    private Certificate[] copyCertList(final Certificate[] array) {
        final Certificate[] array2 = new Certificate[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        for (int i = 0; i < this.certList.length; ++i) {
            asn1EncodableVector2.add(this.certList[i]);
        }
        asn1EncodableVector.add(new DERSet(asn1EncodableVector2));
        return new DERSequence(asn1EncodableVector);
    }
}
