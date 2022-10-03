package org.bouncycastle.asn1;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public abstract class ASN1OctetString extends ASN1Primitive implements ASN1OctetStringParser
{
    byte[] string;
    
    public static ASN1OctetString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1OctetString) {
            return getInstance(object);
        }
        return BEROctetString.fromSequence(ASN1Sequence.getInstance(object));
    }
    
    public static ASN1OctetString getInstance(final Object o) {
        if (o == null || o instanceof ASN1OctetString) {
            return (ASN1OctetString)o;
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + ex.getMessage());
            }
        }
        if (o instanceof ASN1Encodable) {
            final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
            if (asn1Primitive instanceof ASN1OctetString) {
                return (ASN1OctetString)asn1Primitive;
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public ASN1OctetString(final byte[] string) {
        if (string == null) {
            throw new NullPointerException("string cannot be null");
        }
        this.string = string;
    }
    
    public InputStream getOctetStream() {
        return new ByteArrayInputStream(this.string);
    }
    
    public ASN1OctetStringParser parser() {
        return this;
    }
    
    public byte[] getOctets() {
        return this.string;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getOctets());
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1OctetString && Arrays.areEqual(this.string, ((ASN1OctetString)asn1Primitive).string);
    }
    
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }
    
    @Override
    ASN1Primitive toDERObject() {
        return new DEROctetString(this.string);
    }
    
    @Override
    ASN1Primitive toDLObject() {
        return new DEROctetString(this.string);
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    @Override
    public String toString() {
        return "#" + Strings.fromByteArray(Hex.encode(this.string));
    }
}
