package sun.security.krb5.internal.ktab;

import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.RealmException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import java.io.IOException;
import sun.security.krb5.internal.Krb5;
import java.io.InputStream;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class KeyTabInputStream extends KrbDataInputStream implements KeyTabConstants
{
    boolean DEBUG;
    int index;
    
    public KeyTabInputStream(final InputStream inputStream) {
        super(inputStream);
        this.DEBUG = Krb5.DEBUG;
    }
    
    int readEntryLength() throws IOException {
        return this.read(4);
    }
    
    KeyTabEntry readEntry(final int index, final int n) throws IOException, RealmException {
        this.index = index;
        if (this.index == 0) {
            return null;
        }
        if (this.index < 0) {
            this.skip(Math.abs(this.index));
            return null;
        }
        int read = this.read(2);
        this.index -= 2;
        if (n == 1281) {
            --read;
        }
        final Realm realm = new Realm(this.readName());
        final String[] array = new String[read];
        for (int i = 0; i < read; ++i) {
            array[i] = this.readName();
        }
        final int read2 = this.read(4);
        this.index -= 4;
        final PrincipalName principalName = new PrincipalName(read2, array, realm);
        final KerberosTime timeStamp = this.readTimeStamp();
        int n2 = this.read() & 0xFF;
        --this.index;
        final int read3 = this.read(2);
        this.index -= 2;
        final int read4 = this.read(2);
        this.index -= 2;
        final byte[] key = this.readKey(read4);
        this.index -= read4;
        if (this.index >= 4) {
            final int read5 = this.read(4);
            if (read5 != 0) {
                n2 = read5;
            }
            this.index -= 4;
        }
        if (this.index < 0) {
            throw new RealmException("Keytab is corrupted");
        }
        this.skip(this.index);
        return new KeyTabEntry(principalName, realm, timeStamp, n2, read3, key);
    }
    
    byte[] readKey(final int n) throws IOException {
        final byte[] array = new byte[n];
        this.read(array, 0, n);
        return array;
    }
    
    KerberosTime readTimeStamp() throws IOException {
        this.index -= 4;
        return new KerberosTime(this.read(4) * 1000L);
    }
    
    String readName() throws IOException {
        final int read = this.read(2);
        this.index -= 2;
        final byte[] array = new byte[read];
        this.read(array, 0, read);
        this.index -= read;
        final String s = new String(array);
        if (this.DEBUG) {
            System.out.println(">>> KeyTabInputStream, readName(): " + s);
        }
        return s;
    }
}
