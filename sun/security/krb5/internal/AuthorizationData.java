package sun.security.krb5.internal;

import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Asn1Exception;
import java.util.Vector;
import sun.security.util.DerValue;
import java.io.IOException;

public class AuthorizationData implements Cloneable
{
    private AuthorizationDataEntry[] entry;
    
    private AuthorizationData() {
        this.entry = null;
    }
    
    public AuthorizationData(final AuthorizationDataEntry[] array) throws IOException {
        this.entry = null;
        if (array != null) {
            this.entry = new AuthorizationDataEntry[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create an AuthorizationData");
                }
                this.entry[i] = (AuthorizationDataEntry)array[i].clone();
            }
        }
    }
    
    public AuthorizationData(final AuthorizationDataEntry authorizationDataEntry) {
        this.entry = null;
        (this.entry = new AuthorizationDataEntry[1])[0] = authorizationDataEntry;
    }
    
    public Object clone() {
        final AuthorizationData authorizationData = new AuthorizationData();
        if (this.entry != null) {
            authorizationData.entry = new AuthorizationDataEntry[this.entry.length];
            for (int i = 0; i < this.entry.length; ++i) {
                authorizationData.entry[i] = (AuthorizationDataEntry)this.entry[i].clone();
            }
        }
        return authorizationData;
    }
    
    public AuthorizationData(final DerValue derValue) throws Asn1Exception, IOException {
        this.entry = null;
        final Vector vector = new Vector();
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        while (derValue.getData().available() > 0) {
            vector.addElement(new AuthorizationDataEntry(derValue.getData().getDerValue()));
        }
        if (vector.size() > 0) {
            vector.copyInto(this.entry = new AuthorizationDataEntry[vector.size()]);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerValue[] array = new DerValue[this.entry.length];
        for (int i = 0; i < this.entry.length; ++i) {
            array[i] = new DerValue(this.entry[i].asn1Encode());
        }
        derOutputStream.putSequence(array);
        return derOutputStream.toByteArray();
    }
    
    public static AuthorizationData parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new AuthorizationData(derValue.getData().getDerValue());
    }
    
    public void writeAuth(final CCacheOutputStream cCacheOutputStream) throws IOException {
        for (int i = 0; i < this.entry.length; ++i) {
            this.entry[i].writeEntry(cCacheOutputStream);
        }
    }
    
    @Override
    public String toString() {
        String string = "AuthorizationData:\n";
        for (int i = 0; i < this.entry.length; ++i) {
            string += this.entry[i].toString();
        }
        return string;
    }
    
    public int count() {
        return this.entry.length;
    }
    
    public AuthorizationDataEntry item(final int n) {
        return (AuthorizationDataEntry)this.entry[n].clone();
    }
}
