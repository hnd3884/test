package jcifs.spnego.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DERObjectIdentifier extends DERObject
{
    String identifier;
    
    public static DERObjectIdentifier getInstance(final Object obj) {
        if (obj == null || obj instanceof DERObjectIdentifier) {
            return (DERObjectIdentifier)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERObjectIdentifier(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERObjectIdentifier getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    DERObjectIdentifier(final byte[] bytes) {
        final StringBuffer objId = new StringBuffer();
        int value = 0;
        boolean first = true;
        for (int i = 0; i != bytes.length; ++i) {
            final int b = bytes[i] & 0xFF;
            value = value * 128 + (b & 0x7F);
            if ((b & 0x80) == 0x0) {
                if (first) {
                    switch (value / 40) {
                        case 0: {
                            objId.append('0');
                            break;
                        }
                        case 1: {
                            objId.append('1');
                            value -= 40;
                            break;
                        }
                        default: {
                            objId.append('2');
                            value -= 80;
                            break;
                        }
                    }
                    first = false;
                }
                objId.append('.');
                objId.append(Integer.toString(value));
                value = 0;
            }
        }
        this.identifier = objId.toString();
    }
    
    public DERObjectIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public String getId() {
        return this.identifier;
    }
    
    private void writeField(final OutputStream out, final int fieldValue) throws IOException {
        if (fieldValue >= 128) {
            if (fieldValue >= 16384) {
                if (fieldValue >= 2097152) {
                    if (fieldValue >= 268435456) {
                        out.write(fieldValue >> 28 | 0x80);
                    }
                    out.write(fieldValue >> 21 | 0x80);
                }
                out.write(fieldValue >> 14 | 0x80);
            }
            out.write(fieldValue >> 7 | 0x80);
        }
        out.write(fieldValue & 0x7F);
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final OIDTokenizer tok = new OIDTokenizer(this.identifier);
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        final DEROutputStream dOut = new DEROutputStream(bOut);
        this.writeField(bOut, Integer.parseInt(tok.nextToken()) * 40 + Integer.parseInt(tok.nextToken()));
        while (tok.hasMoreTokens()) {
            this.writeField(bOut, Integer.parseInt(tok.nextToken()));
        }
        dOut.close();
        final byte[] bytes = bOut.toByteArray();
        out.writeEncoded(6, bytes);
    }
    
    public int hashCode() {
        return this.identifier.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERObjectIdentifier && this.identifier.equals(((DERObjectIdentifier)o).identifier);
    }
}
