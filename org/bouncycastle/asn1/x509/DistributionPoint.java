package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Object;

public class DistributionPoint extends ASN1Object
{
    DistributionPointName distributionPoint;
    ReasonFlags reasons;
    GeneralNames cRLIssuer;
    
    public static DistributionPoint getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DistributionPoint getInstance(final Object o) {
        if (o == null || o instanceof DistributionPoint) {
            return (DistributionPoint)o;
        }
        if (o instanceof ASN1Sequence) {
            return new DistributionPoint((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid DistributionPoint: " + o.getClass().getName());
    }
    
    public DistributionPoint(final ASN1Sequence asn1Sequence) {
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(i));
            switch (instance.getTagNo()) {
                case 0: {
                    this.distributionPoint = DistributionPointName.getInstance(instance, true);
                    break;
                }
                case 1: {
                    this.reasons = new ReasonFlags(DERBitString.getInstance(instance, false));
                    break;
                }
                case 2: {
                    this.cRLIssuer = GeneralNames.getInstance(instance, false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + instance.getTagNo());
                }
            }
        }
    }
    
    public DistributionPoint(final DistributionPointName distributionPoint, final ReasonFlags reasons, final GeneralNames crlIssuer) {
        this.distributionPoint = distributionPoint;
        this.reasons = reasons;
        this.cRLIssuer = crlIssuer;
    }
    
    public DistributionPointName getDistributionPoint() {
        return this.distributionPoint;
    }
    
    public ReasonFlags getReasons() {
        return this.reasons;
    }
    
    public GeneralNames getCRLIssuer() {
        return this.cRLIssuer;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.distributionPoint != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.distributionPoint));
        }
        if (this.reasons != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.reasons));
        }
        if (this.cRLIssuer != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.cRLIssuer));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        final String lineSeparator = Strings.lineSeparator();
        final StringBuffer sb = new StringBuffer();
        sb.append("DistributionPoint: [");
        sb.append(lineSeparator);
        if (this.distributionPoint != null) {
            this.appendObject(sb, lineSeparator, "distributionPoint", this.distributionPoint.toString());
        }
        if (this.reasons != null) {
            this.appendObject(sb, lineSeparator, "reasons", this.reasons.toString());
        }
        if (this.cRLIssuer != null) {
            this.appendObject(sb, lineSeparator, "cRLIssuer", this.cRLIssuer.toString());
        }
        sb.append("]");
        sb.append(lineSeparator);
        return sb.toString();
    }
    
    private void appendObject(final StringBuffer sb, final String s, final String s2, final String s3) {
        final String s4 = "    ";
        sb.append(s4);
        sb.append(s2);
        sb.append(":");
        sb.append(s);
        sb.append(s4);
        sb.append(s4);
        sb.append(s3);
        sb.append(s);
    }
}
