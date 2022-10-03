package sun.security.x509;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.security.util.DerOutputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.util.Map;
import java.io.IOException;
import java.util.Collections;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import sun.security.util.ObjectIdentifier;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;

public class X500Name implements GeneralNameInterface, Principal
{
    private String dn;
    private String rfc1779Dn;
    private String rfc2253Dn;
    private String canonicalDn;
    private RDN[] names;
    private X500Principal x500Principal;
    private byte[] encoded;
    private volatile List<RDN> rdnList;
    private volatile List<AVA> allAvaList;
    private static final int[] commonName_data;
    private static final int[] SURNAME_DATA;
    private static final int[] SERIALNUMBER_DATA;
    private static final int[] countryName_data;
    private static final int[] localityName_data;
    private static final int[] stateName_data;
    private static final int[] streetAddress_data;
    private static final int[] orgName_data;
    private static final int[] orgUnitName_data;
    private static final int[] title_data;
    private static final int[] GIVENNAME_DATA;
    private static final int[] INITIALS_DATA;
    private static final int[] GENERATIONQUALIFIER_DATA;
    private static final int[] DNQUALIFIER_DATA;
    private static final int[] ipAddress_data;
    private static final int[] DOMAIN_COMPONENT_DATA;
    private static final int[] userid_data;
    public static final ObjectIdentifier commonName_oid;
    public static final ObjectIdentifier SERIALNUMBER_OID;
    public static final ObjectIdentifier countryName_oid;
    public static final ObjectIdentifier localityName_oid;
    public static final ObjectIdentifier orgName_oid;
    public static final ObjectIdentifier orgUnitName_oid;
    public static final ObjectIdentifier stateName_oid;
    public static final ObjectIdentifier streetAddress_oid;
    public static final ObjectIdentifier title_oid;
    public static final ObjectIdentifier DNQUALIFIER_OID;
    public static final ObjectIdentifier SURNAME_OID;
    public static final ObjectIdentifier GIVENNAME_OID;
    public static final ObjectIdentifier INITIALS_OID;
    public static final ObjectIdentifier GENERATIONQUALIFIER_OID;
    public static final ObjectIdentifier ipAddress_oid;
    public static final ObjectIdentifier DOMAIN_COMPONENT_OID;
    public static final ObjectIdentifier userid_oid;
    private static final Constructor<X500Principal> principalConstructor;
    private static final Field principalField;
    
    public X500Name(final String s) throws IOException {
        this(s, Collections.emptyMap());
    }
    
    public X500Name(final String s, final Map<String, String> map) throws IOException {
        this.parseDN(s, map);
    }
    
    public X500Name(final String s, final String s2) throws IOException {
        if (s == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (s2.equalsIgnoreCase("RFC2253")) {
            this.parseRFC2253DN(s);
        }
        else {
            if (!s2.equalsIgnoreCase("DEFAULT")) {
                throw new IOException("Unsupported format " + s2);
            }
            this.parseDN(s, Collections.emptyMap());
        }
    }
    
    public X500Name(final String s, final String s2, final String s3, final String s4) throws IOException {
        (this.names = new RDN[4])[3] = new RDN(1);
        this.names[3].assertion[0] = new AVA(X500Name.commonName_oid, new DerValue(s));
        this.names[2] = new RDN(1);
        this.names[2].assertion[0] = new AVA(X500Name.orgUnitName_oid, new DerValue(s2));
        this.names[1] = new RDN(1);
        this.names[1].assertion[0] = new AVA(X500Name.orgName_oid, new DerValue(s3));
        this.names[0] = new RDN(1);
        this.names[0].assertion[0] = new AVA(X500Name.countryName_oid, new DerValue(s4));
    }
    
    public X500Name(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws IOException {
        (this.names = new RDN[6])[5] = new RDN(1);
        this.names[5].assertion[0] = new AVA(X500Name.commonName_oid, new DerValue(s));
        this.names[4] = new RDN(1);
        this.names[4].assertion[0] = new AVA(X500Name.orgUnitName_oid, new DerValue(s2));
        this.names[3] = new RDN(1);
        this.names[3].assertion[0] = new AVA(X500Name.orgName_oid, new DerValue(s3));
        this.names[2] = new RDN(1);
        this.names[2].assertion[0] = new AVA(X500Name.localityName_oid, new DerValue(s4));
        this.names[1] = new RDN(1);
        this.names[1].assertion[0] = new AVA(X500Name.stateName_oid, new DerValue(s5));
        this.names[0] = new RDN(1);
        this.names[0].assertion[0] = new AVA(X500Name.countryName_oid, new DerValue(s6));
    }
    
    public X500Name(final RDN[] array) throws IOException {
        if (array == null) {
            this.names = new RDN[0];
        }
        else {
            this.names = array.clone();
            for (int i = 0; i < this.names.length; ++i) {
                if (this.names[i] == null) {
                    throw new IOException("Cannot create an X500Name");
                }
            }
        }
    }
    
    public X500Name(final DerValue derValue) throws IOException {
        this(derValue.toDerInputStream());
    }
    
    public X500Name(final DerInputStream derInputStream) throws IOException {
        this.parseDER(derInputStream);
    }
    
    public X500Name(final byte[] array) throws IOException {
        this.parseDER(new DerInputStream(array));
    }
    
    public List<RDN> rdns() {
        Object rdnList = this.rdnList;
        if (rdnList == null) {
            rdnList = Collections.unmodifiableList((List<? extends RDN>)Arrays.asList((T[])this.names));
            this.rdnList = (List<RDN>)rdnList;
        }
        return (List<RDN>)rdnList;
    }
    
    public int size() {
        return this.names.length;
    }
    
    public List<AVA> allAvas() {
        Object allAvaList = this.allAvaList;
        if (allAvaList == null) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < this.names.length; ++i) {
                list.addAll(this.names[i].avas());
            }
            allAvaList = Collections.unmodifiableList((List<?>)list);
            this.allAvaList = (List<AVA>)allAvaList;
        }
        return (List<AVA>)allAvaList;
    }
    
    public int avaSize() {
        return this.allAvas().size();
    }
    
    public boolean isEmpty() {
        for (int length = this.names.length, i = 0; i < length; ++i) {
            if (this.names[i].assertion.length != 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.getRFC2253CanonicalName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X500Name)) {
            return false;
        }
        final X500Name x500Name = (X500Name)o;
        if (this.canonicalDn != null && x500Name.canonicalDn != null) {
            return this.canonicalDn.equals(x500Name.canonicalDn);
        }
        final int length = this.names.length;
        if (length != x500Name.names.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (this.names[i].assertion.length != x500Name.names[i].assertion.length) {
                return false;
            }
        }
        return this.getRFC2253CanonicalName().equals(x500Name.getRFC2253CanonicalName());
    }
    
    private String getString(final DerValue derValue) throws IOException {
        if (derValue == null) {
            return null;
        }
        final String asString = derValue.getAsString();
        if (asString == null) {
            throw new IOException("not a DER string encoding, " + derValue.tag);
        }
        return asString;
    }
    
    @Override
    public int getType() {
        return 4;
    }
    
    public String getCountry() throws IOException {
        return this.getString(this.findAttribute(X500Name.countryName_oid));
    }
    
    public String getOrganization() throws IOException {
        return this.getString(this.findAttribute(X500Name.orgName_oid));
    }
    
    public String getOrganizationalUnit() throws IOException {
        return this.getString(this.findAttribute(X500Name.orgUnitName_oid));
    }
    
    public String getCommonName() throws IOException {
        return this.getString(this.findAttribute(X500Name.commonName_oid));
    }
    
    public String getLocality() throws IOException {
        return this.getString(this.findAttribute(X500Name.localityName_oid));
    }
    
    public String getState() throws IOException {
        return this.getString(this.findAttribute(X500Name.stateName_oid));
    }
    
    public String getDomain() throws IOException {
        return this.getString(this.findAttribute(X500Name.DOMAIN_COMPONENT_OID));
    }
    
    public String getDNQualifier() throws IOException {
        return this.getString(this.findAttribute(X500Name.DNQUALIFIER_OID));
    }
    
    public String getSurname() throws IOException {
        return this.getString(this.findAttribute(X500Name.SURNAME_OID));
    }
    
    public String getGivenName() throws IOException {
        return this.getString(this.findAttribute(X500Name.GIVENNAME_OID));
    }
    
    public String getInitials() throws IOException {
        return this.getString(this.findAttribute(X500Name.INITIALS_OID));
    }
    
    public String getGeneration() throws IOException {
        return this.getString(this.findAttribute(X500Name.GENERATIONQUALIFIER_OID));
    }
    
    public String getIP() throws IOException {
        return this.getString(this.findAttribute(X500Name.ipAddress_oid));
    }
    
    @Override
    public String toString() {
        if (this.dn == null) {
            this.generateDN();
        }
        return this.dn;
    }
    
    public String getRFC1779Name() {
        return this.getRFC1779Name(Collections.emptyMap());
    }
    
    public String getRFC1779Name(final Map<String, String> map) throws IllegalArgumentException {
        if (!map.isEmpty()) {
            return this.generateRFC1779DN(map);
        }
        if (this.rfc1779Dn != null) {
            return this.rfc1779Dn;
        }
        return this.rfc1779Dn = this.generateRFC1779DN(map);
    }
    
    public String getRFC2253Name() {
        return this.getRFC2253Name(Collections.emptyMap());
    }
    
    public String getRFC2253Name(final Map<String, String> map) {
        if (!map.isEmpty()) {
            return this.generateRFC2253DN(map);
        }
        if (this.rfc2253Dn != null) {
            return this.rfc2253Dn;
        }
        return this.rfc2253Dn = this.generateRFC2253DN(map);
    }
    
    private String generateRFC2253DN(final Map<String, String> map) {
        if (this.names.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(48);
        for (int i = this.names.length - 1; i >= 0; --i) {
            if (i < this.names.length - 1) {
                sb.append(',');
            }
            sb.append(this.names[i].toRFC2253String(map));
        }
        return sb.toString();
    }
    
    public String getRFC2253CanonicalName() {
        if (this.canonicalDn != null) {
            return this.canonicalDn;
        }
        if (this.names.length == 0) {
            return this.canonicalDn = "";
        }
        final StringBuilder sb = new StringBuilder(48);
        for (int i = this.names.length - 1; i >= 0; --i) {
            if (i < this.names.length - 1) {
                sb.append(',');
            }
            sb.append(this.names[i].toRFC2253String(true));
        }
        return this.canonicalDn = sb.toString();
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
    
    private DerValue findAttribute(final ObjectIdentifier objectIdentifier) {
        if (this.names != null) {
            for (int i = 0; i < this.names.length; ++i) {
                final DerValue attribute = this.names[i].findAttribute(objectIdentifier);
                if (attribute != null) {
                    return attribute;
                }
            }
        }
        return null;
    }
    
    public DerValue findMostSpecificAttribute(final ObjectIdentifier objectIdentifier) {
        if (this.names != null) {
            for (int i = this.names.length - 1; i >= 0; --i) {
                final DerValue attribute = this.names[i].findAttribute(objectIdentifier);
                if (attribute != null) {
                    return attribute;
                }
            }
        }
        return null;
    }
    
    private void parseDER(final DerInputStream derInputStream) throws IOException {
        final byte[] byteArray = derInputStream.toByteArray();
        DerValue[] array;
        try {
            array = derInputStream.getSequence(5);
        }
        catch (final IOException ex) {
            if (byteArray == null) {
                array = null;
            }
            else {
                array = new DerInputStream(new DerValue((byte)48, byteArray).toByteArray()).getSequence(5);
            }
        }
        if (array == null) {
            this.names = new RDN[0];
        }
        else {
            this.names = new RDN[array.length];
            for (int i = 0; i < array.length; ++i) {
                this.names[i] = new RDN(array[i]);
            }
        }
    }
    
    @Deprecated
    public void emit(final DerOutputStream derOutputStream) throws IOException {
        this.encode(derOutputStream);
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (int i = 0; i < this.names.length; ++i) {
            this.names[i].encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    public byte[] getEncodedInternal() throws IOException {
        if (this.encoded == null) {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            for (int i = 0; i < this.names.length; ++i) {
                this.names[i].encode(derOutputStream2);
            }
            derOutputStream.write((byte)48, derOutputStream2);
            this.encoded = derOutputStream.toByteArray();
        }
        return this.encoded;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.getEncodedInternal().clone();
    }
    
    private void parseDN(final String s, final Map<String, String> map) throws IOException {
        if (s == null || s.length() == 0) {
            this.names = new RDN[0];
            return;
        }
        final ArrayList list = new ArrayList();
        int n = 0;
        int n2 = 0;
        for (int n3 = 0, n4 = s.indexOf(44), n5 = s.indexOf(59); n4 >= 0 || n5 >= 0; n4 = s.indexOf(44, n3), n5 = s.indexOf(59, n3)) {
            int min;
            if (n5 < 0) {
                min = n4;
            }
            else if (n4 < 0) {
                min = n5;
            }
            else {
                min = Math.min(n4, n5);
            }
            n2 += countQuotes(s, n3, min);
            if (min >= 0 && n2 != 1 && !escaped(min, n3, s)) {
                list.add(new RDN(s.substring(n, min), map));
                n = min + 1;
                n2 = 0;
            }
            n3 = min + 1;
        }
        list.add(new RDN(s.substring(n), map));
        Collections.reverse(list);
        this.names = (RDN[])list.toArray(new RDN[list.size()]);
    }
    
    private void parseRFC2253DN(final String s) throws IOException {
        if (s.length() == 0) {
            this.names = new RDN[0];
            return;
        }
        final ArrayList list = new ArrayList();
        int n = 0;
        for (int n2 = 0, i = s.indexOf(44); i >= 0; i = s.indexOf(44, n2)) {
            if (i > 0 && !escaped(i, n2, s)) {
                list.add(new RDN(s.substring(n, i), "RFC2253"));
                n = i + 1;
            }
            n2 = i + 1;
        }
        list.add(new RDN(s.substring(n), "RFC2253"));
        Collections.reverse(list);
        this.names = (RDN[])list.toArray(new RDN[list.size()]);
    }
    
    static int countQuotes(final String s, final int n, final int n2) {
        int n3 = 0;
        for (int i = n; i < n2; ++i) {
            if ((s.charAt(i) == '\"' && i == n) || (s.charAt(i) == '\"' && s.charAt(i - 1) != '\\')) {
                ++n3;
            }
        }
        return n3;
    }
    
    private static boolean escaped(int i, final int n, final String s) {
        if (i == 1 && s.charAt(i - 1) == '\\') {
            return true;
        }
        if (i > 1 && s.charAt(i - 1) == '\\' && s.charAt(i - 2) != '\\') {
            return true;
        }
        if (i > 1 && s.charAt(i - 1) == '\\' && s.charAt(i - 2) == '\\') {
            int n2 = 0;
            --i;
            while (i >= n) {
                if (s.charAt(i) == '\\') {
                    ++n2;
                }
                --i;
            }
            return n2 % 2 != 0;
        }
        return false;
    }
    
    private void generateDN() {
        if (this.names.length == 1) {
            this.dn = this.names[0].toString();
            return;
        }
        final StringBuilder sb = new StringBuilder(48);
        if (this.names != null) {
            for (int i = this.names.length - 1; i >= 0; --i) {
                if (i != this.names.length - 1) {
                    sb.append(", ");
                }
                sb.append(this.names[i].toString());
            }
        }
        this.dn = sb.toString();
    }
    
    private String generateRFC1779DN(final Map<String, String> map) {
        if (this.names.length == 1) {
            return this.names[0].toRFC1779String(map);
        }
        final StringBuilder sb = new StringBuilder(48);
        if (this.names != null) {
            for (int i = this.names.length - 1; i >= 0; --i) {
                if (i != this.names.length - 1) {
                    sb.append(", ");
                }
                sb.append(this.names[i].toRFC1779String(map));
            }
        }
        return sb.toString();
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else if (generalNameInterface.getType() != 4) {
            n = -1;
        }
        else {
            final X500Name x500Name = (X500Name)generalNameInterface;
            if (x500Name.equals(this)) {
                n = 0;
            }
            else if (x500Name.names.length == 0) {
                n = 2;
            }
            else if (this.names.length == 0) {
                n = 1;
            }
            else if (x500Name.isWithinSubtree(this)) {
                n = 1;
            }
            else if (this.isWithinSubtree(x500Name)) {
                n = 2;
            }
            else {
                n = 3;
            }
        }
        return n;
    }
    
    private boolean isWithinSubtree(final X500Name x500Name) {
        if (this == x500Name) {
            return true;
        }
        if (x500Name == null) {
            return false;
        }
        if (x500Name.names.length == 0) {
            return true;
        }
        if (this.names.length == 0) {
            return false;
        }
        if (this.names.length < x500Name.names.length) {
            return false;
        }
        for (int i = 0; i < x500Name.names.length; ++i) {
            if (!this.names[i].equals(x500Name.names[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        return this.names.length;
    }
    
    public X500Name commonAncestor(final X500Name x500Name) {
        if (x500Name == null) {
            return null;
        }
        final int length = x500Name.names.length;
        final int length2 = this.names.length;
        if (length2 == 0 || length == 0) {
            return null;
        }
        final int n = (length2 < length) ? length2 : length;
        int i = 0;
        while (i < n) {
            if (!this.names[i].equals(x500Name.names[i])) {
                if (i == 0) {
                    return null;
                }
                break;
            }
            else {
                ++i;
            }
        }
        final RDN[] array = new RDN[i];
        for (int j = 0; j < i; ++j) {
            array[j] = this.names[j];
        }
        X500Name x500Name2;
        try {
            x500Name2 = new X500Name(array);
        }
        catch (final IOException ex) {
            return null;
        }
        return x500Name2;
    }
    
    public X500Principal asX500Principal() {
        if (this.x500Principal == null) {
            try {
                this.x500Principal = X500Name.principalConstructor.newInstance(this);
            }
            catch (final Exception ex) {
                throw new RuntimeException("Unexpected exception", ex);
            }
        }
        return this.x500Principal;
    }
    
    public static X500Name asX500Name(final X500Principal x500Principal) {
        try {
            final X500Name x500Name = (X500Name)X500Name.principalField.get(x500Principal);
            x500Name.x500Principal = x500Principal;
            return x500Name;
        }
        catch (final Exception ex) {
            throw new RuntimeException("Unexpected exception", ex);
        }
    }
    
    static {
        commonName_data = new int[] { 2, 5, 4, 3 };
        SURNAME_DATA = new int[] { 2, 5, 4, 4 };
        SERIALNUMBER_DATA = new int[] { 2, 5, 4, 5 };
        countryName_data = new int[] { 2, 5, 4, 6 };
        localityName_data = new int[] { 2, 5, 4, 7 };
        stateName_data = new int[] { 2, 5, 4, 8 };
        streetAddress_data = new int[] { 2, 5, 4, 9 };
        orgName_data = new int[] { 2, 5, 4, 10 };
        orgUnitName_data = new int[] { 2, 5, 4, 11 };
        title_data = new int[] { 2, 5, 4, 12 };
        GIVENNAME_DATA = new int[] { 2, 5, 4, 42 };
        INITIALS_DATA = new int[] { 2, 5, 4, 43 };
        GENERATIONQUALIFIER_DATA = new int[] { 2, 5, 4, 44 };
        DNQUALIFIER_DATA = new int[] { 2, 5, 4, 46 };
        ipAddress_data = new int[] { 1, 3, 6, 1, 4, 1, 42, 2, 11, 2, 1 };
        DOMAIN_COMPONENT_DATA = new int[] { 0, 9, 2342, 19200300, 100, 1, 25 };
        userid_data = new int[] { 0, 9, 2342, 19200300, 100, 1, 1 };
        commonName_oid = ObjectIdentifier.newInternal(X500Name.commonName_data);
        SERIALNUMBER_OID = ObjectIdentifier.newInternal(X500Name.SERIALNUMBER_DATA);
        countryName_oid = ObjectIdentifier.newInternal(X500Name.countryName_data);
        localityName_oid = ObjectIdentifier.newInternal(X500Name.localityName_data);
        orgName_oid = ObjectIdentifier.newInternal(X500Name.orgName_data);
        orgUnitName_oid = ObjectIdentifier.newInternal(X500Name.orgUnitName_data);
        stateName_oid = ObjectIdentifier.newInternal(X500Name.stateName_data);
        streetAddress_oid = ObjectIdentifier.newInternal(X500Name.streetAddress_data);
        title_oid = ObjectIdentifier.newInternal(X500Name.title_data);
        DNQUALIFIER_OID = ObjectIdentifier.newInternal(X500Name.DNQUALIFIER_DATA);
        SURNAME_OID = ObjectIdentifier.newInternal(X500Name.SURNAME_DATA);
        GIVENNAME_OID = ObjectIdentifier.newInternal(X500Name.GIVENNAME_DATA);
        INITIALS_OID = ObjectIdentifier.newInternal(X500Name.INITIALS_DATA);
        GENERATIONQUALIFIER_OID = ObjectIdentifier.newInternal(X500Name.GENERATIONQUALIFIER_DATA);
        ipAddress_oid = ObjectIdentifier.newInternal(X500Name.ipAddress_data);
        DOMAIN_COMPONENT_OID = ObjectIdentifier.newInternal(X500Name.DOMAIN_COMPONENT_DATA);
        userid_oid = ObjectIdentifier.newInternal(X500Name.userid_data);
        final PrivilegedExceptionAction<Object[]> privilegedExceptionAction = new PrivilegedExceptionAction<Object[]>() {
            @Override
            public Object[] run() throws Exception {
                final Class<X500Principal> clazz = X500Principal.class;
                final Constructor declaredConstructor = clazz.getDeclaredConstructor(X500Name.class);
                declaredConstructor.setAccessible(true);
                final Field declaredField = clazz.getDeclaredField("thisX500Name");
                declaredField.setAccessible(true);
                return new Object[] { declaredConstructor, declaredField };
            }
        };
        try {
            final Object[] array = AccessController.doPrivileged((PrivilegedExceptionAction<Object[]>)privilegedExceptionAction);
            principalConstructor = (Constructor)array[0];
            principalField = (Field)array[1];
        }
        catch (final Exception ex) {
            throw new InternalError("Could not obtain X500Principal access", ex);
        }
    }
}
