package sun.security.krb5.internal;

import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Asn1Exception;
import java.util.Vector;
import sun.security.util.DerValue;
import java.io.IOException;

public class LastReq
{
    private LastReqEntry[] entry;
    
    public LastReq(final LastReqEntry[] array) throws IOException {
        this.entry = null;
        if (array != null) {
            this.entry = new LastReqEntry[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a LastReqEntry");
                }
                this.entry[i] = (LastReqEntry)array[i].clone();
            }
        }
    }
    
    public LastReq(final DerValue derValue) throws Asn1Exception, IOException {
        this.entry = null;
        final Vector vector = new Vector();
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        while (derValue.getData().available() > 0) {
            vector.addElement(new LastReqEntry(derValue.getData().getDerValue()));
        }
        if (vector.size() > 0) {
            vector.copyInto(this.entry = new LastReqEntry[vector.size()]);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.entry != null && this.entry.length > 0) {
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            for (int i = 0; i < this.entry.length; ++i) {
                derOutputStream2.write(this.entry[i].asn1Encode());
            }
            derOutputStream.write((byte)48, derOutputStream2);
            return derOutputStream.toByteArray();
        }
        return null;
    }
    
    public static LastReq parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new LastReq(derValue.getData().getDerValue());
    }
}
