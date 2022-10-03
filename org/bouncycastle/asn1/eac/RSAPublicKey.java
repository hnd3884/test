package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class RSAPublicKey extends PublicKeyDataObject
{
    private ASN1ObjectIdentifier usage;
    private BigInteger modulus;
    private BigInteger exponent;
    private int valid;
    private static int modulusValid;
    private static int exponentValid;
    
    RSAPublicKey(final ASN1Sequence asn1Sequence) {
        this.valid = 0;
        final Enumeration objects = asn1Sequence.getObjects();
        this.usage = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final UnsignedInteger instance = UnsignedInteger.getInstance(objects.nextElement());
            switch (instance.getTagNo()) {
                case 1: {
                    this.setModulus(instance);
                    continue;
                }
                case 2: {
                    this.setExponent(instance);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Unknown DERTaggedObject :" + instance.getTagNo() + "-> not an Iso7816RSAPublicKeyStructure");
                }
            }
        }
        if (this.valid != 3) {
            throw new IllegalArgumentException("missing argument -> not an Iso7816RSAPublicKeyStructure");
        }
    }
    
    public RSAPublicKey(final ASN1ObjectIdentifier usage, final BigInteger modulus, final BigInteger exponent) {
        this.valid = 0;
        this.usage = usage;
        this.modulus = modulus;
        this.exponent = exponent;
    }
    
    @Override
    public ASN1ObjectIdentifier getUsage() {
        return this.usage;
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.exponent;
    }
    
    private void setModulus(final UnsignedInteger unsignedInteger) {
        if ((this.valid & RSAPublicKey.modulusValid) == 0x0) {
            this.valid |= RSAPublicKey.modulusValid;
            this.modulus = unsignedInteger.getValue();
            return;
        }
        throw new IllegalArgumentException("Modulus already set");
    }
    
    private void setExponent(final UnsignedInteger unsignedInteger) {
        if ((this.valid & RSAPublicKey.exponentValid) == 0x0) {
            this.valid |= RSAPublicKey.exponentValid;
            this.exponent = unsignedInteger.getValue();
            return;
        }
        throw new IllegalArgumentException("Exponent already set");
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.usage);
        asn1EncodableVector.add(new UnsignedInteger(1, this.getModulus()));
        asn1EncodableVector.add(new UnsignedInteger(2, this.getPublicExponent()));
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        RSAPublicKey.modulusValid = 1;
        RSAPublicKey.exponentValid = 2;
    }
}
