package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1BigInteger;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import java.math.BigInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RSAPrivateKey extends DecodedPrivateKey
{
    private static final long serialVersionUID = -7101141316095373904L;
    private final BigInteger coefficient;
    private final BigInteger exponent1;
    private final BigInteger exponent2;
    private final BigInteger modulus;
    private final BigInteger prime1;
    private final BigInteger prime2;
    private final BigInteger privateExponent;
    private final BigInteger publicExponent;
    private final List<BigInteger[]> otherPrimeInfos;
    private final RSAPrivateKeyVersion version;
    
    RSAPrivateKey(final RSAPrivateKeyVersion version, final BigInteger modulus, final BigInteger publicExponent, final BigInteger privateExponent, final BigInteger prime1, final BigInteger prime2, final BigInteger exponent1, final BigInteger exponent2, final BigInteger coefficient, final List<BigInteger[]> otherPrimeInfos) {
        this.version = version;
        this.modulus = modulus;
        this.publicExponent = publicExponent;
        this.privateExponent = privateExponent;
        this.prime1 = prime1;
        this.prime2 = prime2;
        this.exponent1 = exponent1;
        this.exponent2 = exponent2;
        this.coefficient = coefficient;
        this.otherPrimeInfos = otherPrimeInfos;
    }
    
    RSAPrivateKey(final ASN1OctetString encodedPrivateKey) throws CertException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(encodedPrivateKey.getValue()).elements();
            final int versionIntValue = elements[0].decodeAsInteger().intValue();
            this.version = RSAPrivateKeyVersion.valueOf(versionIntValue);
            if (this.version == null) {
                throw new CertException(CertMessages.ERR_RSA_PRIVATE_KEY_UNSUPPORTED_VERSION.get(versionIntValue));
            }
            this.modulus = elements[1].decodeAsBigInteger().getBigIntegerValue();
            this.publicExponent = elements[2].decodeAsBigInteger().getBigIntegerValue();
            this.privateExponent = elements[3].decodeAsBigInteger().getBigIntegerValue();
            this.prime1 = elements[4].decodeAsBigInteger().getBigIntegerValue();
            this.prime2 = elements[5].decodeAsBigInteger().getBigIntegerValue();
            this.exponent1 = elements[6].decodeAsBigInteger().getBigIntegerValue();
            this.exponent2 = elements[7].decodeAsBigInteger().getBigIntegerValue();
            this.coefficient = elements[8].decodeAsBigInteger().getBigIntegerValue();
            if (elements.length == 9) {
                this.otherPrimeInfos = Collections.emptyList();
            }
            else {
                final ASN1Element[] otherPrimesElements = elements[9].decodeAsSequence().elements();
                final ArrayList<BigInteger[]> otherPrimes = new ArrayList<BigInteger[]>(otherPrimesElements.length);
                for (final ASN1Element e : otherPrimesElements) {
                    final ASN1Element[] primeElements = e.decodeAsSequence().elements();
                    otherPrimes.add(new BigInteger[] { primeElements[0].decodeAsBigInteger().getBigIntegerValue(), primeElements[1].decodeAsBigInteger().getBigIntegerValue(), primeElements[2].decodeAsBigInteger().getBigIntegerValue() });
                }
                this.otherPrimeInfos = Collections.unmodifiableList((List<? extends BigInteger[]>)otherPrimes);
            }
        }
        catch (final CertException e2) {
            Debug.debugException(e2);
            throw e2;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new CertException(CertMessages.ERR_RSA_PRIVATE_KEY_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    ASN1OctetString encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(9);
        elements.add(new ASN1Integer(this.version.getIntValue()));
        elements.add(new ASN1BigInteger(this.modulus));
        elements.add(new ASN1BigInteger(this.publicExponent));
        elements.add(new ASN1BigInteger(this.privateExponent));
        elements.add(new ASN1BigInteger(this.prime1));
        elements.add(new ASN1BigInteger(this.prime2));
        elements.add(new ASN1BigInteger(this.exponent1));
        elements.add(new ASN1BigInteger(this.exponent2));
        elements.add(new ASN1BigInteger(this.coefficient));
        if (!this.otherPrimeInfos.isEmpty()) {
            final ArrayList<ASN1Element> otherElements = new ArrayList<ASN1Element>(this.otherPrimeInfos.size());
            for (final BigInteger[] info : this.otherPrimeInfos) {
                otherElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1BigInteger(info[0]), new ASN1BigInteger(info[1]), new ASN1BigInteger(info[2]) }));
            }
            elements.add(new ASN1Sequence(otherElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public RSAPrivateKeyVersion getVersion() {
        return this.version;
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
    
    public BigInteger getPrime1() {
        return this.prime1;
    }
    
    public BigInteger getPrime2() {
        return this.prime2;
    }
    
    public BigInteger getExponent1() {
        return this.exponent1;
    }
    
    public BigInteger getExponent2() {
        return this.exponent2;
    }
    
    public BigInteger getCoefficient() {
        return this.coefficient;
    }
    
    public List<BigInteger[]> getOtherPrimeInfos() {
        return this.otherPrimeInfos;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RSAPrivateKey(version='");
        buffer.append(this.version.getName());
        buffer.append("', modulus=");
        StaticUtils.toHex(this.modulus.toByteArray(), ":", buffer);
        buffer.append(", publicExponent=");
        StaticUtils.toHex(this.publicExponent.toByteArray(), ":", buffer);
        buffer.append(", privateExponent=");
        StaticUtils.toHex(this.privateExponent.toByteArray(), ":", buffer);
        buffer.append(", prime1=");
        StaticUtils.toHex(this.prime1.toByteArray(), ":", buffer);
        buffer.append(", prime2=");
        StaticUtils.toHex(this.prime2.toByteArray(), ":", buffer);
        buffer.append(", exponent1=");
        StaticUtils.toHex(this.exponent1.toByteArray(), ":", buffer);
        buffer.append(", exponent2=");
        StaticUtils.toHex(this.exponent2.toByteArray(), ":", buffer);
        buffer.append(", coefficient=");
        StaticUtils.toHex(this.coefficient.toByteArray(), ":", buffer);
        if (!this.otherPrimeInfos.isEmpty()) {
            buffer.append(", otherPrimeInfos={");
            final Iterator<BigInteger[]> iterator = this.otherPrimeInfos.iterator();
            while (iterator.hasNext()) {
                final BigInteger[] array = iterator.next();
                buffer.append("PrimeInfo(prime=");
                StaticUtils.toHex(array[0].toByteArray(), ":", buffer);
                buffer.append(", exponent=");
                StaticUtils.toHex(array[1].toByteArray(), ":", buffer);
                buffer.append(", coefficient=");
                StaticUtils.toHex(array[2].toByteArray(), ":", buffer);
                buffer.append(')');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
