package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class OcspListID extends ASN1Object
{
    private ASN1Sequence ocspResponses;
    
    public static OcspListID getInstance(final Object o) {
        if (o instanceof OcspListID) {
            return (OcspListID)o;
        }
        if (o != null) {
            return new OcspListID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OcspListID(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 1) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.ocspResponses = (ASN1Sequence)asn1Sequence.getObjectAt(0);
        final Enumeration objects = this.ocspResponses.getObjects();
        while (objects.hasMoreElements()) {
            OcspResponsesID.getInstance(objects.nextElement());
        }
    }
    
    public OcspListID(final OcspResponsesID[] array) {
        this.ocspResponses = new DERSequence(array);
    }
    
    public OcspResponsesID[] getOcspResponses() {
        final OcspResponsesID[] array = new OcspResponsesID[this.ocspResponses.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = OcspResponsesID.getInstance(this.ocspResponses.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.ocspResponses);
    }
}
