package jcifs.spnego.asn1;

import java.io.IOException;

public class DERT61String extends DERObject implements DERString
{
    String string;
    
    public static DERT61String getInstance(final Object obj) {
        if (obj == null || obj instanceof DERT61String) {
            return (DERT61String)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERT61String(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERT61String getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERT61String(final byte[] string) {
        this.string = new String(string);
    }
    
    public DERT61String(final String string) {
        this.string = string;
    }
    
    public String getString() {
        return this.string;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(20, this.string.getBytes());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERT61String && this.getString().equals(((DERT61String)o).getString());
    }
}
