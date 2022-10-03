package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.DigestInfo;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class MacData extends ASN1Object
{
    private static final BigInteger ONE;
    DigestInfo digInfo;
    byte[] salt;
    BigInteger iterationCount;
    
    public static MacData getInstance(final Object o) {
        if (o instanceof MacData) {
            return (MacData)o;
        }
        if (o != null) {
            return new MacData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private MacData(final ASN1Sequence asn1Sequence) {
        this.digInfo = DigestInfo.getInstance(asn1Sequence.getObjectAt(0));
        this.salt = Arrays.clone(((ASN1OctetString)asn1Sequence.getObjectAt(1)).getOctets());
        if (asn1Sequence.size() == 3) {
            this.iterationCount = ((ASN1Integer)asn1Sequence.getObjectAt(2)).getValue();
        }
        else {
            this.iterationCount = MacData.ONE;
        }
    }
    
    public MacData(final DigestInfo digInfo, final byte[] array, final int n) {
        this.digInfo = digInfo;
        this.salt = Arrays.clone(array);
        this.iterationCount = BigInteger.valueOf(n);
    }
    
    public DigestInfo getMac() {
        return this.digInfo;
    }
    
    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
    
    public BigInteger getIterationCount() {
        return this.iterationCount;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.digInfo);
        asn1EncodableVector.add(new DEROctetString(this.salt));
        if (!this.iterationCount.equals(MacData.ONE)) {
            asn1EncodableVector.add(new ASN1Integer(this.iterationCount));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
