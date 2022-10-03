package jcifs.spnego.asn1;

import java.io.IOException;

public class DERBoolean extends DERObject
{
    byte value;
    public static final DERBoolean FALSE;
    public static final DERBoolean TRUE;
    
    public static DERBoolean getInstance(final Object obj) {
        if (obj == null || obj instanceof DERBoolean) {
            return (DERBoolean)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERBoolean(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERBoolean getInstance(final boolean value) {
        return value ? DERBoolean.TRUE : DERBoolean.FALSE;
    }
    
    public static DERBoolean getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERBoolean(final byte[] value) {
        this.value = value[0];
    }
    
    public DERBoolean(final boolean value) {
        this.value = (byte)(value ? -1 : 0);
    }
    
    public boolean isTrue() {
        return this.value != 0;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final byte[] bytes = { this.value };
        out.writeEncoded(1, bytes);
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERBoolean && this.value == ((DERBoolean)o).value;
    }
    
    static {
        FALSE = new DERBoolean(false);
        TRUE = new DERBoolean(true);
    }
}
