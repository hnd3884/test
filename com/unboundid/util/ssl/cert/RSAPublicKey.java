package com.unboundid.util.ssl.cert;

import com.unboundid.asn1.ASN1BigInteger;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1BitString;
import java.math.BigInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RSAPublicKey extends DecodedPublicKey
{
    private static final long serialVersionUID = 1837190736740174338L;
    private final BigInteger modulus;
    private final BigInteger publicExponent;
    
    RSAPublicKey(final BigInteger modulus, final BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }
    
    RSAPublicKey(final ASN1BitString subjectPublicKey) throws CertException {
        try {
            final byte[] keyBytes = subjectPublicKey.getBytes();
            final ASN1Element[] keyElements = ASN1Sequence.decodeAsSequence(keyBytes).elements();
            this.modulus = keyElements[0].decodeAsBigInteger().getBigIntegerValue();
            this.publicExponent = keyElements[1].decodeAsBigInteger().getBigIntegerValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_RSA_PUBLIC_KEY_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    ASN1BitString encode() {
        final ASN1Sequence publicKeySequence = new ASN1Sequence(new ASN1Element[] { new ASN1BigInteger(this.modulus), new ASN1BigInteger(this.publicExponent) });
        final boolean[] bits = ASN1BitString.getBitsForBytes(publicKeySequence.encode());
        return new ASN1BitString(bits);
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RSAPublicKey(modulus=");
        StaticUtils.toHex(this.modulus.toByteArray(), ":", buffer);
        buffer.append(", publicExponent=");
        StaticUtils.toHex(this.publicExponent.toByteArray(), ":", buffer);
        buffer.append(')');
    }
}
