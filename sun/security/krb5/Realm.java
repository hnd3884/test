package sun.security.krb5;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.util.LinkedList;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerValue;

public class Realm implements Cloneable
{
    public static final boolean AUTODEDUCEREALM;
    private final String realm;
    
    public Realm(final String s) throws RealmException {
        this.realm = parseRealm(s);
    }
    
    public static Realm getDefault() throws RealmException {
        try {
            return new Realm(Config.getInstance().getDefaultRealm());
        }
        catch (final RealmException ex) {
            throw ex;
        }
        catch (final KrbException ex2) {
            throw new RealmException(ex2);
        }
    }
    
    public Object clone() {
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof Realm && this.realm.equals(((Realm)o).realm));
    }
    
    @Override
    public int hashCode() {
        return this.realm.hashCode();
    }
    
    public Realm(final DerValue derValue) throws Asn1Exception, RealmException, IOException {
        if (derValue == null) {
            throw new IllegalArgumentException("encoding can not be null");
        }
        this.realm = new KerberosString(derValue).toString();
        if (this.realm == null || this.realm.length() == 0) {
            throw new RealmException(601);
        }
        if (!isValidRealmString(this.realm)) {
            throw new RealmException(600);
        }
    }
    
    @Override
    public String toString() {
        return this.realm;
    }
    
    public static String parseRealmAtSeparator(final String s) throws RealmException {
        if (s == null) {
            throw new IllegalArgumentException("null input name is not allowed");
        }
        final String s2 = new String(s);
        String substring = null;
        int i = 0;
        while (i < s2.length()) {
            if (s2.charAt(i) == '@' && (i == 0 || s2.charAt(i - 1) != '\\')) {
                if (i + 1 < s2.length()) {
                    substring = s2.substring(i + 1, s2.length());
                    break;
                }
                throw new IllegalArgumentException("empty realm part not allowed");
            }
            else {
                ++i;
            }
        }
        if (substring != null) {
            if (substring.length() == 0) {
                throw new RealmException(601);
            }
            if (!isValidRealmString(substring)) {
                throw new RealmException(600);
            }
        }
        return substring;
    }
    
    public static String parseRealmComponent(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("null input name is not allowed");
        }
        final String s2 = new String(s);
        String substring = null;
        int i = 0;
        while (i < s2.length()) {
            if (s2.charAt(i) == '.' && (i == 0 || s2.charAt(i - 1) != '\\')) {
                if (i + 1 < s2.length()) {
                    substring = s2.substring(i + 1, s2.length());
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return substring;
    }
    
    protected static String parseRealm(final String s) throws RealmException {
        String realmAtSeparator = parseRealmAtSeparator(s);
        if (realmAtSeparator == null) {
            realmAtSeparator = s;
        }
        if (realmAtSeparator == null || realmAtSeparator.length() == 0) {
            throw new RealmException(601);
        }
        if (!isValidRealmString(realmAtSeparator)) {
            throw new RealmException(600);
        }
        return realmAtSeparator;
    }
    
    protected static boolean isValidRealmString(final String s) {
        if (s == null) {
            return false;
        }
        if (s.length() == 0) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '/' || s.charAt(i) == '\0') {
                return false;
            }
        }
        return true;
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putDerValue(new KerberosString(this.realm).toDerValue());
        return derOutputStream.toByteArray();
    }
    
    public static Realm parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException, RealmException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new Realm(derValue.getData().getDerValue());
    }
    
    public static String[] getRealmsList(final String s, final String s2) {
        try {
            return parseCapaths(s, s2);
        }
        catch (final KrbException ex) {
            return parseHierarchy(s, s2);
        }
    }
    
    private static String[] parseCapaths(final String s, final String s2) throws KrbException {
        final Config instance = Config.getInstance();
        if (!instance.exists("capaths", s, s2)) {
            throw new KrbException("No conf");
        }
        final LinkedList list = new LinkedList();
        String s3 = s2;
        while (true) {
            final String all = instance.getAll("capaths", s, s3);
            if (all == null) {
                break;
            }
            final String[] split = all.split("\\s+");
            boolean b = false;
            for (int i = split.length - 1; i >= 0; --i) {
                if (!list.contains(split[i]) && !split[i].equals(".") && !split[i].equals(s) && !split[i].equals(s2)) {
                    if (!split[i].equals(s3)) {
                        b = true;
                        list.addFirst(split[i]);
                    }
                }
            }
            if (!b) {
                break;
            }
            s3 = list.getFirst();
        }
        list.addFirst(s);
        return list.toArray(new String[list.size()]);
    }
    
    private static String[] parseHierarchy(final String s, final String s2) {
        final String[] split = s.split("\\.");
        final String[] split2 = s2.split("\\.");
        int length = split.length;
        int length2 = split2.length;
        boolean b = false;
        --length2;
        --length;
        while (length2 >= 0 && length >= 0 && split2[length2].equals(split[length])) {
            b = true;
            --length2;
            --length;
        }
        final LinkedList<String> list = new LinkedList<String>();
        for (int i = 0; i <= length; ++i) {
            list.addLast(subStringFrom(split, i));
        }
        if (b) {
            list.addLast(subStringFrom(split, length + 1));
        }
        for (int j = length2; j >= 0; --j) {
            list.addLast(subStringFrom(split2, j));
        }
        list.removeLast();
        return list.toArray(new String[list.size()]);
    }
    
    private static String subStringFrom(final String[] array, final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int i = n; i < array.length; ++i) {
            if (sb.length() != 0) {
                sb.append('.');
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    static {
        AUTODEDUCEREALM = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.krb5.autodeducerealm"));
    }
}
