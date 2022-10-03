package sun.security.krb5;

import sun.security.krb5.internal.ccache.CCacheOutputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.net.UnknownHostException;
import java.util.Locale;
import java.net.InetAddress;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.krb5.internal.util.KerberosString;
import java.util.Vector;
import sun.security.util.DerValue;
import java.util.Arrays;
import sun.misc.Unsafe;

public class PrincipalName implements Cloneable
{
    public static final int KRB_NT_UNKNOWN = 0;
    public static final int KRB_NT_PRINCIPAL = 1;
    public static final int KRB_NT_SRV_INST = 2;
    public static final int KRB_NT_SRV_HST = 3;
    public static final int KRB_NT_SRV_XHST = 4;
    public static final int KRB_NT_UID = 5;
    public static final int KRB_NT_ENTERPRISE = 10;
    public static final String TGS_DEFAULT_SRV_NAME = "krbtgt";
    public static final int TGS_DEFAULT_NT = 2;
    public static final char NAME_COMPONENT_SEPARATOR = '/';
    public static final char NAME_REALM_SEPARATOR = '@';
    public static final char REALM_COMPONENT_SEPARATOR = '.';
    public static final String NAME_COMPONENT_SEPARATOR_STR = "/";
    public static final String NAME_REALM_SEPARATOR_STR = "@";
    public static final String REALM_COMPONENT_SEPARATOR_STR = ".";
    private final int nameType;
    private final String[] nameStrings;
    private final Realm nameRealm;
    private final boolean realmDeduced;
    private transient String salt;
    private static final long NAME_STRINGS_OFFSET;
    private static final Unsafe UNSAFE;
    
    public PrincipalName(final int nameType, final String[] array, final Realm nameRealm) {
        this.salt = null;
        if (nameRealm == null) {
            throw new IllegalArgumentException("Null realm not allowed");
        }
        validateNameStrings(array);
        this.nameType = nameType;
        this.nameStrings = array.clone();
        this.nameRealm = nameRealm;
        this.realmDeduced = false;
    }
    
    public PrincipalName(final String[] array, final String s) throws RealmException {
        this(0, array, new Realm(s));
    }
    
    private static void validateNameStrings(final String[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Null nameStrings not allowed");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty nameStrings not allowed");
        }
        for (final String s : array) {
            if (s == null) {
                throw new IllegalArgumentException("Null nameString not allowed");
            }
            if (s.isEmpty()) {
                throw new IllegalArgumentException("Empty nameString not allowed");
            }
        }
    }
    
    public Object clone() {
        try {
            final PrincipalName principalName = (PrincipalName)super.clone();
            PrincipalName.UNSAFE.putObject(this, PrincipalName.NAME_STRINGS_OFFSET, this.nameStrings.clone());
            return principalName;
        }
        catch (final CloneNotSupportedException ex) {
            throw new AssertionError((Object)"Should never happen");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PrincipalName) {
            final PrincipalName principalName = (PrincipalName)o;
            return this.nameRealm.equals(principalName.nameRealm) && Arrays.equals(this.nameStrings, principalName.nameStrings);
        }
        return false;
    }
    
    public PrincipalName(final DerValue derValue, final Realm nameRealm) throws Asn1Exception, IOException {
        this.salt = null;
        if (nameRealm == null) {
            throw new IllegalArgumentException("Null realm not allowed");
        }
        this.realmDeduced = false;
        this.nameRealm = nameRealm;
        if (derValue == null) {
            throw new IllegalArgumentException("Null encoding not allowed");
        }
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.nameType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue4 = derValue3.getData().getDerValue();
        if (derValue4.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final Vector vector = new Vector();
        while (derValue4.getData().available() > 0) {
            vector.addElement(new KerberosString(derValue4.getData().getDerValue()).toString());
        }
        vector.copyInto(this.nameStrings = new String[vector.size()]);
        validateNameStrings(this.nameStrings);
    }
    
    public static PrincipalName parse(final DerInputStream derInputStream, final byte b, final boolean b2, Realm default1) throws Asn1Exception, IOException, RealmException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (default1 == null) {
            default1 = Realm.getDefault();
        }
        return new PrincipalName(derValue2, default1);
    }
    
    private static String[] parseName(final String s) {
        final Vector vector = new Vector();
        String s2 = s;
        int i = 0;
        int n = 0;
        while (i < s2.length()) {
            if (s2.charAt(i) == '/') {
                if (i > 0 && s2.charAt(i - 1) == '\\') {
                    s2 = s2.substring(0, i - 1) + s2.substring(i, s2.length());
                    continue;
                }
                if (n <= i) {
                    vector.addElement(s2.substring(n, i));
                }
                n = i + 1;
            }
            else if (s2.charAt(i) == '@') {
                if (i > 0 && s2.charAt(i - 1) == '\\') {
                    s2 = s2.substring(0, i - 1) + s2.substring(i, s2.length());
                    continue;
                }
                if (n < i) {
                    vector.addElement(s2.substring(n, i));
                }
                n = i + 1;
                break;
            }
            ++i;
        }
        if (i == s2.length()) {
            vector.addElement(s2.substring(n, i));
        }
        final String[] array = new String[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    public PrincipalName(final String s, final int n, String realmAtSeparator) throws RealmException {
        this.salt = null;
        if (s == null) {
            throw new IllegalArgumentException("Null name not allowed");
        }
        final String[] name = parseName(s);
        validateNameStrings(name);
        if (realmAtSeparator == null) {
            realmAtSeparator = Realm.parseRealmAtSeparator(s);
        }
        this.realmDeduced = (realmAtSeparator == null);
        switch (n) {
            case 3: {
                if (name.length >= 2) {
                    String substring = name[1];
                    try {
                        final String canonicalHostName = InetAddress.getByName(substring).getCanonicalHostName();
                        if (canonicalHostName.toLowerCase(Locale.ENGLISH).startsWith(substring.toLowerCase(Locale.ENGLISH) + ".")) {
                            substring = canonicalHostName;
                        }
                    }
                    catch (final UnknownHostException | SecurityException ex) {}
                    if (substring.endsWith(".")) {
                        substring = substring.substring(0, substring.length() - 1);
                    }
                    name[1] = substring.toLowerCase(Locale.ENGLISH);
                }
                this.nameStrings = name;
                this.nameType = n;
                if (realmAtSeparator != null) {
                    this.nameRealm = new Realm(realmAtSeparator);
                    break;
                }
                final String mapHostToRealm = mapHostToRealm(name[1]);
                if (mapHostToRealm != null) {
                    this.nameRealm = new Realm(mapHostToRealm);
                }
                else {
                    this.nameRealm = Realm.getDefault();
                }
                break;
            }
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
            case 10: {
                this.nameStrings = name;
                this.nameType = n;
                if (realmAtSeparator != null) {
                    this.nameRealm = new Realm(realmAtSeparator);
                    break;
                }
                this.nameRealm = Realm.getDefault();
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal name type");
            }
        }
    }
    
    public PrincipalName(final String s, final int n) throws RealmException {
        this(s, n, null);
    }
    
    public PrincipalName(final String s) throws RealmException {
        this(s, 0);
    }
    
    public PrincipalName(final String s, final String s2) throws RealmException {
        this(s, 0, s2);
    }
    
    public static PrincipalName tgsService(final String s, final String s2) throws KrbException {
        return new PrincipalName(2, new String[] { "krbtgt", s }, new Realm(s2));
    }
    
    public String getRealmAsString() {
        return this.getRealmString();
    }
    
    public String getPrincipalNameAsString() {
        final StringBuffer sb = new StringBuffer(this.nameStrings[0]);
        for (int i = 1; i < this.nameStrings.length; ++i) {
            sb.append(this.nameStrings[i]);
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    public String getName() {
        return this.toString();
    }
    
    public int getNameType() {
        return this.nameType;
    }
    
    public String[] getNameStrings() {
        return this.nameStrings.clone();
    }
    
    public byte[][] toByteArray() {
        final byte[][] array = new byte[this.nameStrings.length][];
        for (int i = 0; i < this.nameStrings.length; ++i) {
            array[i] = new byte[this.nameStrings[i].length()];
            array[i] = this.nameStrings[i].getBytes();
        }
        return array;
    }
    
    public String getRealmString() {
        return this.nameRealm.toString();
    }
    
    public Realm getRealm() {
        return this.nameRealm;
    }
    
    public String getSalt() {
        if (this.salt == null) {
            final StringBuffer sb = new StringBuffer();
            sb.append(this.nameRealm.toString());
            for (int i = 0; i < this.nameStrings.length; ++i) {
                sb.append(this.nameStrings[i]);
            }
            return sb.toString();
        }
        return this.salt;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.nameStrings.length; ++i) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(this.nameStrings[i].replace("@", "\\@"));
        }
        sb.append("@");
        sb.append(this.nameRealm.toString());
        return sb.toString();
    }
    
    public String getNameString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.nameStrings.length; ++i) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(this.nameStrings[i]);
        }
        return sb.toString();
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.nameType));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        final DerValue[] array = new DerValue[this.nameStrings.length];
        for (int i = 0; i < this.nameStrings.length; ++i) {
            array[i] = new KerberosString(this.nameStrings[i]).toDerValue();
        }
        derOutputStream3.putSequence(array);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public boolean match(final PrincipalName principalName) {
        boolean b = true;
        if (this.nameRealm != null && principalName.nameRealm != null && !this.nameRealm.toString().equalsIgnoreCase(principalName.nameRealm.toString())) {
            b = false;
        }
        if (this.nameStrings.length != principalName.nameStrings.length) {
            b = false;
        }
        else {
            for (int i = 0; i < this.nameStrings.length; ++i) {
                if (!this.nameStrings[i].equalsIgnoreCase(principalName.nameStrings[i])) {
                    b = false;
                }
            }
        }
        return b;
    }
    
    public void writePrincipal(final CCacheOutputStream cCacheOutputStream) throws IOException {
        cCacheOutputStream.write32(this.nameType);
        cCacheOutputStream.write32(this.nameStrings.length);
        final byte[] bytes = this.nameRealm.toString().getBytes();
        cCacheOutputStream.write32(bytes.length);
        cCacheOutputStream.write(bytes, 0, bytes.length);
        for (int i = 0; i < this.nameStrings.length; ++i) {
            final byte[] bytes2 = this.nameStrings[i].getBytes();
            cCacheOutputStream.write32(bytes2.length);
            cCacheOutputStream.write(bytes2, 0, bytes2.length);
        }
    }
    
    public String getInstanceComponent() {
        if (this.nameStrings != null && this.nameStrings.length >= 2) {
            return new String(this.nameStrings[1]);
        }
        return null;
    }
    
    static String mapHostToRealm(final String s) {
        String s2 = null;
        try {
            final Config instance = Config.getInstance();
            if ((s2 = instance.get("domain_realm", s)) != null) {
                return s2;
            }
            for (int i = 1; i < s.length(); ++i) {
                if (s.charAt(i) == '.' && i != s.length() - 1) {
                    s2 = instance.get("domain_realm", s.substring(i));
                    if (s2 != null) {
                        break;
                    }
                    s2 = instance.get("domain_realm", s.substring(i + 1));
                    if (s2 != null) {
                        break;
                    }
                }
            }
        }
        catch (final KrbException ex) {}
        return s2;
    }
    
    public boolean isRealmDeduced() {
        return this.realmDeduced;
    }
    
    static {
        try {
            final Unsafe unsafe = Unsafe.getUnsafe();
            NAME_STRINGS_OFFSET = unsafe.objectFieldOffset(PrincipalName.class.getDeclaredField("nameStrings"));
            UNSAFE = unsafe;
        }
        catch (final ReflectiveOperationException ex) {
            throw new Error(ex);
        }
    }
}
