package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CRLDistPoint extends ASN1Object
{
    ASN1Sequence seq;
    
    public static CRLDistPoint getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static CRLDistPoint getInstance(final Object o) {
        if (o instanceof CRLDistPoint) {
            return (CRLDistPoint)o;
        }
        if (o != null) {
            return new CRLDistPoint(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CRLDistPoint(final ASN1Sequence seq) {
        this.seq = null;
        this.seq = seq;
    }
    
    public CRLDistPoint(final DistributionPoint[] array) {
        this.seq = null;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    public DistributionPoint[] getDistributionPoints() {
        final DistributionPoint[] array = new DistributionPoint[this.seq.size()];
        for (int i = 0; i != this.seq.size(); ++i) {
            array[i] = DistributionPoint.getInstance(this.seq.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("CRLDistPoint:");
        sb.append(lineSeparator);
        final DistributionPoint[] distributionPoints = this.getDistributionPoints();
        for (int i = 0; i != distributionPoints.length; ++i) {
            sb.append("    ");
            sb.append(distributionPoints[i]);
            sb.append(lineSeparator);
        }
        return sb.toString();
    }
}
