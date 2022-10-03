package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class ScryptParams extends ASN1Object
{
    private final byte[] salt;
    private final BigInteger costParameter;
    private final BigInteger blockSize;
    private final BigInteger parallelizationParameter;
    private final BigInteger keyLength;
    
    public ScryptParams(final byte[] array, final int n, final int n2, final int n3) {
        this(array, BigInteger.valueOf(n), BigInteger.valueOf(n2), BigInteger.valueOf(n3), null);
    }
    
    public ScryptParams(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        this(array, BigInteger.valueOf(n), BigInteger.valueOf(n2), BigInteger.valueOf(n3), BigInteger.valueOf(n4));
    }
    
    public ScryptParams(final byte[] array, final BigInteger costParameter, final BigInteger blockSize, final BigInteger parallelizationParameter, final BigInteger keyLength) {
        this.salt = Arrays.clone(array);
        this.costParameter = costParameter;
        this.blockSize = blockSize;
        this.parallelizationParameter = parallelizationParameter;
        this.keyLength = keyLength;
    }
    
    public static ScryptParams getInstance(final Object o) {
        if (o instanceof ScryptParams) {
            return (ScryptParams)o;
        }
        if (o != null) {
            return new ScryptParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ScryptParams(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 4 && asn1Sequence.size() != 5) {
            throw new IllegalArgumentException("invalid sequence: size = " + asn1Sequence.size());
        }
        this.salt = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0)).getOctets());
        this.costParameter = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1)).getValue();
        this.blockSize = ASN1Integer.getInstance(asn1Sequence.getObjectAt(2)).getValue();
        this.parallelizationParameter = ASN1Integer.getInstance(asn1Sequence.getObjectAt(3)).getValue();
        if (asn1Sequence.size() == 5) {
            this.keyLength = ASN1Integer.getInstance(asn1Sequence.getObjectAt(4)).getValue();
        }
        else {
            this.keyLength = null;
        }
    }
    
    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
    
    public BigInteger getCostParameter() {
        return this.costParameter;
    }
    
    public BigInteger getBlockSize() {
        return this.blockSize;
    }
    
    public BigInteger getParallelizationParameter() {
        return this.parallelizationParameter;
    }
    
    public BigInteger getKeyLength() {
        return this.keyLength;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DEROctetString(this.salt));
        asn1EncodableVector.add(new ASN1Integer(this.costParameter));
        asn1EncodableVector.add(new ASN1Integer(this.blockSize));
        asn1EncodableVector.add(new ASN1Integer(this.parallelizationParameter));
        if (this.keyLength != null) {
            asn1EncodableVector.add(new ASN1Integer(this.keyLength));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
