package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CompleteRevocationRefs extends ASN1Object
{
    private ASN1Sequence crlOcspRefs;
    
    public static CompleteRevocationRefs getInstance(final Object o) {
        if (o instanceof CompleteRevocationRefs) {
            return (CompleteRevocationRefs)o;
        }
        if (o != null) {
            return new CompleteRevocationRefs(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CompleteRevocationRefs(final ASN1Sequence crlOcspRefs) {
        final Enumeration objects = crlOcspRefs.getObjects();
        while (objects.hasMoreElements()) {
            CrlOcspRef.getInstance(objects.nextElement());
        }
        this.crlOcspRefs = crlOcspRefs;
    }
    
    public CompleteRevocationRefs(final CrlOcspRef[] array) {
        this.crlOcspRefs = new DERSequence(array);
    }
    
    public CrlOcspRef[] getCrlOcspRefs() {
        final CrlOcspRef[] array = new CrlOcspRef[this.crlOcspRefs.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = CrlOcspRef.getInstance(this.crlOcspRefs.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.crlOcspRefs;
    }
}
