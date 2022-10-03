package org.ietf.jgss;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.io.InputStream;
import sun.security.util.ObjectIdentifier;

public class Oid
{
    private ObjectIdentifier oid;
    private byte[] derEncoding;
    
    public Oid(final String s) throws GSSException {
        try {
            this.oid = new ObjectIdentifier(s);
            this.derEncoding = null;
        }
        catch (final Exception ex) {
            throw new GSSException(11, "Improperly formatted Object Identifier String - " + s);
        }
    }
    
    public Oid(final InputStream inputStream) throws GSSException {
        try {
            final DerValue derValue = new DerValue(inputStream);
            this.derEncoding = derValue.toByteArray();
            this.oid = derValue.getOID();
        }
        catch (final IOException ex) {
            throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
        }
    }
    
    public Oid(final byte[] array) throws GSSException {
        try {
            final DerValue derValue = new DerValue(array);
            this.derEncoding = derValue.toByteArray();
            this.oid = derValue.getOID();
        }
        catch (final IOException ex) {
            throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
        }
    }
    
    static Oid getInstance(final String s) {
        Oid oid = null;
        try {
            oid = new Oid(s);
        }
        catch (final GSSException ex) {}
        return oid;
    }
    
    @Override
    public String toString() {
        return this.oid.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Oid) {
            return this.oid.equals((Object)((Oid)o).oid);
        }
        return o instanceof ObjectIdentifier && this.oid.equals(o);
    }
    
    public byte[] getDER() throws GSSException {
        if (this.derEncoding == null) {
            final DerOutputStream derOutputStream = new DerOutputStream();
            try {
                derOutputStream.putOID(this.oid);
            }
            catch (final IOException ex) {
                throw new GSSException(11, ex.getMessage());
            }
            this.derEncoding = derOutputStream.toByteArray();
        }
        return this.derEncoding.clone();
    }
    
    public boolean containedIn(final Oid[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(this)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.oid.hashCode();
    }
}
