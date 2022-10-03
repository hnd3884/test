package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Object;

public class KeyUsage extends ASN1Object
{
    public static final int digitalSignature = 128;
    public static final int nonRepudiation = 64;
    public static final int keyEncipherment = 32;
    public static final int dataEncipherment = 16;
    public static final int keyAgreement = 8;
    public static final int keyCertSign = 4;
    public static final int cRLSign = 2;
    public static final int encipherOnly = 1;
    public static final int decipherOnly = 32768;
    private DERBitString bitString;
    
    public static KeyUsage getInstance(final Object o) {
        if (o instanceof KeyUsage) {
            return (KeyUsage)o;
        }
        if (o != null) {
            return new KeyUsage(DERBitString.getInstance(o));
        }
        return null;
    }
    
    public static KeyUsage fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.keyUsage));
    }
    
    public KeyUsage(final int n) {
        this.bitString = new DERBitString(n);
    }
    
    private KeyUsage(final DERBitString bitString) {
        this.bitString = bitString;
    }
    
    public boolean hasUsages(final int n) {
        return (this.bitString.intValue() & n) == n;
    }
    
    public byte[] getBytes() {
        return this.bitString.getBytes();
    }
    
    public int getPadBits() {
        return this.bitString.getPadBits();
    }
    
    @Override
    public String toString() {
        final byte[] bytes = this.bitString.getBytes();
        if (bytes.length == 1) {
            return "KeyUsage: 0x" + Integer.toHexString(bytes[0] & 0xFF);
        }
        return "KeyUsage: 0x" + Integer.toHexString((bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.bitString;
    }
}
