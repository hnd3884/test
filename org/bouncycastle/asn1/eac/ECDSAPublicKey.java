package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DEROctetString;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ECDSAPublicKey extends PublicKeyDataObject
{
    private ASN1ObjectIdentifier usage;
    private BigInteger primeModulusP;
    private BigInteger firstCoefA;
    private BigInteger secondCoefB;
    private byte[] basePointG;
    private BigInteger orderOfBasePointR;
    private byte[] publicPointY;
    private BigInteger cofactorF;
    private int options;
    private static final int P = 1;
    private static final int A = 2;
    private static final int B = 4;
    private static final int G = 8;
    private static final int R = 16;
    private static final int Y = 32;
    private static final int F = 64;
    
    ECDSAPublicKey(final ASN1Sequence asn1Sequence) throws IllegalArgumentException {
        final Enumeration objects = asn1Sequence.getObjects();
        this.usage = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        this.options = 0;
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            if (!(nextElement instanceof ASN1TaggedObject)) {
                throw new IllegalArgumentException("Unknown Object Identifier!");
            }
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)nextElement;
            switch (asn1TaggedObject.getTagNo()) {
                case 1: {
                    this.setPrimeModulusP(UnsignedInteger.getInstance(asn1TaggedObject).getValue());
                    continue;
                }
                case 2: {
                    this.setFirstCoefA(UnsignedInteger.getInstance(asn1TaggedObject).getValue());
                    continue;
                }
                case 3: {
                    this.setSecondCoefB(UnsignedInteger.getInstance(asn1TaggedObject).getValue());
                    continue;
                }
                case 4: {
                    this.setBasePointG(ASN1OctetString.getInstance(asn1TaggedObject, false));
                    continue;
                }
                case 5: {
                    this.setOrderOfBasePointR(UnsignedInteger.getInstance(asn1TaggedObject).getValue());
                    continue;
                }
                case 6: {
                    this.setPublicPointY(ASN1OctetString.getInstance(asn1TaggedObject, false));
                    continue;
                }
                case 7: {
                    this.setCofactorF(UnsignedInteger.getInstance(asn1TaggedObject).getValue());
                    continue;
                }
                default: {
                    this.options = 0;
                    throw new IllegalArgumentException("Unknown Object Identifier!");
                }
            }
        }
        if (this.options != 32 && this.options != 127) {
            throw new IllegalArgumentException("All options must be either present or absent!");
        }
    }
    
    public ECDSAPublicKey(final ASN1ObjectIdentifier usage, final byte[] array) throws IllegalArgumentException {
        this.usage = usage;
        this.setPublicPointY(new DEROctetString(array));
    }
    
    public ECDSAPublicKey(final ASN1ObjectIdentifier usage, final BigInteger primeModulusP, final BigInteger firstCoefA, final BigInteger secondCoefB, final byte[] array, final BigInteger orderOfBasePointR, final byte[] array2, final int n) {
        this.usage = usage;
        this.setPrimeModulusP(primeModulusP);
        this.setFirstCoefA(firstCoefA);
        this.setSecondCoefB(secondCoefB);
        this.setBasePointG(new DEROctetString(array));
        this.setOrderOfBasePointR(orderOfBasePointR);
        this.setPublicPointY(new DEROctetString(array2));
        this.setCofactorF(BigInteger.valueOf(n));
    }
    
    @Override
    public ASN1ObjectIdentifier getUsage() {
        return this.usage;
    }
    
    public byte[] getBasePointG() {
        if ((this.options & 0x8) != 0x0) {
            return Arrays.clone(this.basePointG);
        }
        return null;
    }
    
    private void setBasePointG(final ASN1OctetString asn1OctetString) throws IllegalArgumentException {
        if ((this.options & 0x8) == 0x0) {
            this.options |= 0x8;
            this.basePointG = asn1OctetString.getOctets();
            return;
        }
        throw new IllegalArgumentException("Base Point G already set");
    }
    
    public BigInteger getCofactorF() {
        if ((this.options & 0x40) != 0x0) {
            return this.cofactorF;
        }
        return null;
    }
    
    private void setCofactorF(final BigInteger cofactorF) throws IllegalArgumentException {
        if ((this.options & 0x40) == 0x0) {
            this.options |= 0x40;
            this.cofactorF = cofactorF;
            return;
        }
        throw new IllegalArgumentException("Cofactor F already set");
    }
    
    public BigInteger getFirstCoefA() {
        if ((this.options & 0x2) != 0x0) {
            return this.firstCoefA;
        }
        return null;
    }
    
    private void setFirstCoefA(final BigInteger firstCoefA) throws IllegalArgumentException {
        if ((this.options & 0x2) == 0x0) {
            this.options |= 0x2;
            this.firstCoefA = firstCoefA;
            return;
        }
        throw new IllegalArgumentException("First Coef A already set");
    }
    
    public BigInteger getOrderOfBasePointR() {
        if ((this.options & 0x10) != 0x0) {
            return this.orderOfBasePointR;
        }
        return null;
    }
    
    private void setOrderOfBasePointR(final BigInteger orderOfBasePointR) throws IllegalArgumentException {
        if ((this.options & 0x10) == 0x0) {
            this.options |= 0x10;
            this.orderOfBasePointR = orderOfBasePointR;
            return;
        }
        throw new IllegalArgumentException("Order of base point R already set");
    }
    
    public BigInteger getPrimeModulusP() {
        if ((this.options & 0x1) != 0x0) {
            return this.primeModulusP;
        }
        return null;
    }
    
    private void setPrimeModulusP(final BigInteger primeModulusP) {
        if ((this.options & 0x1) == 0x0) {
            this.options |= 0x1;
            this.primeModulusP = primeModulusP;
            return;
        }
        throw new IllegalArgumentException("Prime Modulus P already set");
    }
    
    public byte[] getPublicPointY() {
        if ((this.options & 0x20) != 0x0) {
            return Arrays.clone(this.publicPointY);
        }
        return null;
    }
    
    private void setPublicPointY(final ASN1OctetString asn1OctetString) throws IllegalArgumentException {
        if ((this.options & 0x20) == 0x0) {
            this.options |= 0x20;
            this.publicPointY = asn1OctetString.getOctets();
            return;
        }
        throw new IllegalArgumentException("Public Point Y already set");
    }
    
    public BigInteger getSecondCoefB() {
        if ((this.options & 0x4) != 0x0) {
            return this.secondCoefB;
        }
        return null;
    }
    
    private void setSecondCoefB(final BigInteger secondCoefB) throws IllegalArgumentException {
        if ((this.options & 0x4) == 0x0) {
            this.options |= 0x4;
            this.secondCoefB = secondCoefB;
            return;
        }
        throw new IllegalArgumentException("Second Coef B already set");
    }
    
    public boolean hasParameters() {
        return this.primeModulusP != null;
    }
    
    public ASN1EncodableVector getASN1EncodableVector(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(asn1ObjectIdentifier);
        if (!b) {
            asn1EncodableVector.add(new UnsignedInteger(1, this.getPrimeModulusP()));
            asn1EncodableVector.add(new UnsignedInteger(2, this.getFirstCoefA()));
            asn1EncodableVector.add(new UnsignedInteger(3, this.getSecondCoefB()));
            asn1EncodableVector.add(new DERTaggedObject(false, 4, new DEROctetString(this.getBasePointG())));
            asn1EncodableVector.add(new UnsignedInteger(5, this.getOrderOfBasePointR()));
        }
        asn1EncodableVector.add(new DERTaggedObject(false, 6, new DEROctetString(this.getPublicPointY())));
        if (!b) {
            asn1EncodableVector.add(new UnsignedInteger(7, this.getCofactorF()));
        }
        return asn1EncodableVector;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.getASN1EncodableVector(this.usage, !this.hasParameters()));
    }
}
