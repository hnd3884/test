package sun.security.krb5.internal.ccache;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.AuthorizationDataEntry;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.EncryptionKey;
import java.util.StringTokenizer;
import sun.security.krb5.RealmException;
import sun.security.krb5.Realm;
import sun.misc.IOUtils;
import java.util.ArrayList;
import sun.security.krb5.PrincipalName;
import java.io.IOException;
import java.io.InputStream;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class CCacheInputStream extends KrbDataInputStream implements FileCCacheConstants
{
    private static boolean DEBUG;
    
    public CCacheInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    public Tag readTag() throws IOException {
        final char[] array = new char[1024];
        int read = -1;
        Integer n = null;
        Integer n2 = null;
        int i = this.read(2);
        if (i < 0) {
            throw new IOException("stop.");
        }
        if (i > array.length) {
            throw new IOException("Invalid tag length.");
        }
        while (i > 0) {
            read = this.read(2);
            final int read2 = this.read(2);
            switch (read) {
                case 1: {
                    n = new Integer(this.read(4));
                    n2 = new Integer(this.read(4));
                    break;
                }
            }
            i -= 4 + read2;
        }
        return new Tag(i, read, n, n2);
    }
    
    public PrincipalName readPrincipal(final int n) throws IOException, RealmException {
        int read;
        if (n == 1281) {
            read = 0;
        }
        else {
            read = this.read(4);
        }
        int length4 = this.readLength4();
        final ArrayList list = new ArrayList();
        if (n == 1281) {
            --length4;
        }
        for (int i = 0; i <= length4; ++i) {
            list.add(new String(IOUtils.readExactlyNBytes(this, this.readLength4())));
        }
        if (list.isEmpty()) {
            throw new IOException("No realm or principal");
        }
        if (this.isRealm((String)list.get(0))) {
            final String s = (String)list.remove(0);
            if (list.isEmpty()) {
                throw new IOException("No principal name components");
            }
            return new PrincipalName(read, (String[])list.toArray(new String[list.size()]), new Realm(s));
        }
        else {
            try {
                return new PrincipalName(read, (String[])list.toArray(new String[list.size()]), Realm.getDefault());
            }
            catch (final RealmException ex) {
                return null;
            }
        }
    }
    
    boolean isRealm(final String s) {
        try {
            final Realm realm = new Realm(s);
        }
        catch (final Exception ex) {
            return false;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ".");
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            for (int i = 0; i < nextToken.length(); ++i) {
                if (nextToken.charAt(i) >= '\u008d') {
                    return false;
                }
            }
        }
        return true;
    }
    
    EncryptionKey readKey(final int n) throws IOException {
        final int read = this.read(2);
        if (n == 1283) {
            this.read(2);
        }
        return new EncryptionKey(IOUtils.readExactlyNBytes(this, this.readLength4()), read, new Integer(n));
    }
    
    long[] readTimes() throws IOException {
        return new long[] { this.read(4) * 1000L, this.read(4) * 1000L, this.read(4) * 1000L, this.read(4) * 1000L };
    }
    
    boolean readskey() throws IOException {
        return this.read() != 0;
    }
    
    HostAddress[] readAddr() throws IOException, KrbApErrException {
        final int length4 = this.readLength4();
        if (length4 > 0) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < length4; ++i) {
                final int read = this.read(2);
                final int length5 = this.readLength4();
                if (length5 != 4 && length5 != 16) {
                    if (CCacheInputStream.DEBUG) {
                        System.out.println("Incorrect address format.");
                    }
                    return null;
                }
                final byte[] array = new byte[length5];
                for (int j = 0; j < length5; ++j) {
                    array[j] = (byte)this.read(1);
                }
                list.add(new HostAddress(read, array));
            }
            return (HostAddress[])list.toArray(new HostAddress[list.size()]);
        }
        return null;
    }
    
    AuthorizationDataEntry[] readAuth() throws IOException {
        final int length4 = this.readLength4();
        if (length4 > 0) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < length4; ++i) {
                list.add(new AuthorizationDataEntry(this.read(2), IOUtils.readExactlyNBytes(this, this.readLength4())));
            }
            return (AuthorizationDataEntry[])list.toArray(new AuthorizationDataEntry[list.size()]);
        }
        return null;
    }
    
    byte[] readData() throws IOException {
        final int length4 = this.readLength4();
        if (length4 == 0) {
            return null;
        }
        return IOUtils.readExactlyNBytes(this, length4);
    }
    
    boolean[] readFlags() throws IOException {
        final boolean[] array = new boolean[32];
        final int read = this.read(4);
        if ((read & 0x40000000) == 0x40000000) {
            array[1] = true;
        }
        if ((read & 0x20000000) == 0x20000000) {
            array[2] = true;
        }
        if ((read & 0x10000000) == 0x10000000) {
            array[3] = true;
        }
        if ((read & 0x8000000) == 0x8000000) {
            array[4] = true;
        }
        if ((read & 0x4000000) == 0x4000000) {
            array[5] = true;
        }
        if ((read & 0x2000000) == 0x2000000) {
            array[6] = true;
        }
        if ((read & 0x1000000) == 0x1000000) {
            array[7] = true;
        }
        if ((read & 0x800000) == 0x800000) {
            array[8] = true;
        }
        if ((read & 0x400000) == 0x400000) {
            array[9] = true;
        }
        if ((read & 0x200000) == 0x200000) {
            array[10] = true;
        }
        if ((read & 0x100000) == 0x100000) {
            array[11] = true;
        }
        if (CCacheInputStream.DEBUG) {
            String s = ">>> CCacheInputStream: readFlags() ";
            if (array[1]) {
                s += " FORWARDABLE;";
            }
            if (array[2]) {
                s += " FORWARDED;";
            }
            if (array[3]) {
                s += " PROXIABLE;";
            }
            if (array[4]) {
                s += " PROXY;";
            }
            if (array[5]) {
                s += " MAY_POSTDATE;";
            }
            if (array[6]) {
                s += " POSTDATED;";
            }
            if (array[7]) {
                s += " INVALID;";
            }
            if (array[8]) {
                s += " RENEWABLE;";
            }
            if (array[9]) {
                s += " INITIAL;";
            }
            if (array[10]) {
                s += " PRE_AUTH;";
            }
            if (array[11]) {
                s += " HW_AUTH;";
            }
            System.out.println(s);
        }
        return array;
    }
    
    Object readCred(final int n) throws IOException, RealmException, KrbApErrException, Asn1Exception {
        PrincipalName principal = null;
        try {
            principal = this.readPrincipal(n);
        }
        catch (final Exception ex) {}
        if (CCacheInputStream.DEBUG) {
            System.out.println(">>>DEBUG <CCacheInputStream>  client principal is " + principal);
        }
        PrincipalName principal2 = null;
        try {
            principal2 = this.readPrincipal(n);
        }
        catch (final Exception ex2) {}
        if (CCacheInputStream.DEBUG) {
            System.out.println(">>>DEBUG <CCacheInputStream> server principal is " + principal2);
        }
        final EncryptionKey key = this.readKey(n);
        if (CCacheInputStream.DEBUG) {
            System.out.println(">>>DEBUG <CCacheInputStream> key type: " + key.getEType());
        }
        final long[] times = this.readTimes();
        final KerberosTime kerberosTime = new KerberosTime(times[0]);
        final KerberosTime kerberosTime2 = (times[1] == 0L) ? null : new KerberosTime(times[1]);
        final KerberosTime kerberosTime3 = new KerberosTime(times[2]);
        final KerberosTime kerberosTime4 = (times[3] == 0L) ? null : new KerberosTime(times[3]);
        if (CCacheInputStream.DEBUG) {
            System.out.println(">>>DEBUG <CCacheInputStream> auth time: " + kerberosTime.toDate().toString());
            System.out.println(">>>DEBUG <CCacheInputStream> start time: " + ((kerberosTime2 == null) ? "null" : kerberosTime2.toDate().toString()));
            System.out.println(">>>DEBUG <CCacheInputStream> end time: " + kerberosTime3.toDate().toString());
            System.out.println(">>>DEBUG <CCacheInputStream> renew_till time: " + ((kerberosTime4 == null) ? "null" : kerberosTime4.toDate().toString()));
        }
        final boolean readskey = this.readskey();
        final TicketFlags ticketFlags = new TicketFlags(this.readFlags());
        final HostAddress[] addr = this.readAddr();
        HostAddresses hostAddresses = null;
        if (addr != null) {
            hostAddresses = new HostAddresses(addr);
        }
        final AuthorizationDataEntry[] auth = this.readAuth();
        AuthorizationData authorizationData = null;
        if (auth != null) {
            authorizationData = new AuthorizationData(auth);
        }
        final byte[] data = this.readData();
        final byte[] data2 = this.readData();
        if (principal == null || principal2 == null) {
            return null;
        }
        try {
            if (principal2.getRealmString().equals("X-CACHECONF:")) {
                final String[] nameStrings = principal2.getNameStrings();
                if (nameStrings[0].equals("krb5_ccache_conf_data")) {
                    return new CredentialsCache.ConfigEntry(nameStrings[1], (nameStrings.length > 2) ? new PrincipalName(nameStrings[2]) : null, data);
                }
            }
            return new Credentials(principal, principal2, key, kerberosTime, kerberosTime2, kerberosTime3, kerberosTime4, readskey, ticketFlags, hostAddresses, authorizationData, (data != null) ? new Ticket(data) : null, (data2 != null) ? new Ticket(data2) : null);
        }
        catch (final Exception ex3) {
            if (CCacheInputStream.DEBUG) {
                ex3.printStackTrace(System.out);
            }
            return null;
        }
    }
    
    static {
        CCacheInputStream.DEBUG = Krb5.DEBUG;
    }
}
