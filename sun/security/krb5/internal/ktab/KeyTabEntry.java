package sun.security.krb5.internal.ktab;

import java.io.UnsupportedEncodingException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.Realm;
import sun.security.krb5.PrincipalName;

public class KeyTabEntry implements KeyTabConstants
{
    PrincipalName service;
    Realm realm;
    KerberosTime timestamp;
    int keyVersion;
    int keyType;
    byte[] keyblock;
    boolean DEBUG;
    
    public KeyTabEntry(final PrincipalName service, final Realm realm, final KerberosTime timestamp, final int keyVersion, final int keyType, final byte[] array) {
        this.keyblock = null;
        this.DEBUG = Krb5.DEBUG;
        this.service = service;
        this.realm = realm;
        this.timestamp = timestamp;
        this.keyVersion = keyVersion;
        this.keyType = keyType;
        if (array != null) {
            this.keyblock = array.clone();
        }
    }
    
    public PrincipalName getService() {
        return this.service;
    }
    
    public EncryptionKey getKey() {
        return new EncryptionKey(this.keyblock, this.keyType, new Integer(this.keyVersion));
    }
    
    public String getKeyString() {
        final StringBuffer sb = new StringBuffer("0x");
        for (int i = 0; i < this.keyblock.length; ++i) {
            sb.append(String.format("%02x", this.keyblock[i] & 0xFF));
        }
        return sb.toString();
    }
    
    public int entryLength() {
        int n = 0;
        final String[] nameStrings = this.service.getNameStrings();
        for (int i = 0; i < nameStrings.length; ++i) {
            try {
                n += 2 + nameStrings[i].getBytes("8859_1").length;
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        int length = 0;
        try {
            length = this.realm.toString().getBytes("8859_1").length;
        }
        catch (final UnsupportedEncodingException ex2) {}
        final int n2 = 4 + length + n + 4 + 4 + 1 + 2 + 2 + this.keyblock.length;
        if (this.DEBUG) {
            System.out.println(">>> KeyTabEntry: key tab entry size is " + n2);
        }
        return n2;
    }
    
    public KerberosTime getTimeStamp() {
        return this.timestamp;
    }
}
