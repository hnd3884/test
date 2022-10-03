package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class DistributionPointName extends ASN1Object implements ASN1Choice
{
    ASN1Encodable name;
    int type;
    public static final int FULL_NAME = 0;
    public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
    
    public static DistributionPointName getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1TaggedObject.getInstance(asn1TaggedObject, true));
    }
    
    public static DistributionPointName getInstance(final Object o) {
        if (o == null || o instanceof DistributionPointName) {
            return (DistributionPointName)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new DistributionPointName((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public DistributionPointName(final int type, final ASN1Encodable name) {
        this.type = type;
        this.name = name;
    }
    
    public DistributionPointName(final GeneralNames generalNames) {
        this(0, generalNames);
    }
    
    public int getType() {
        return this.type;
    }
    
    public ASN1Encodable getName() {
        return this.name;
    }
    
    public DistributionPointName(final ASN1TaggedObject asn1TaggedObject) {
        this.type = asn1TaggedObject.getTagNo();
        if (this.type == 0) {
            this.name = GeneralNames.getInstance(asn1TaggedObject, false);
        }
        else {
            this.name = ASN1Set.getInstance(asn1TaggedObject, false);
        }
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.type, this.name);
    }
    
    @Override
    public String toString() {
        final String lineSeparator = Strings.lineSeparator();
        final StringBuffer sb = new StringBuffer();
        sb.append("DistributionPointName: [");
        sb.append(lineSeparator);
        if (this.type == 0) {
            this.appendObject(sb, lineSeparator, "fullName", this.name.toString());
        }
        else {
            this.appendObject(sb, lineSeparator, "nameRelativeToCRLIssuer", this.name.toString());
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
