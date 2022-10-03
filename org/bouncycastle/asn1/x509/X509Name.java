package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Strings;
import java.util.Enumeration;
import java.io.IOException;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.asn1.DERUniversalString;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.Vector;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class X509Name extends ASN1Object
{
    @Deprecated
    public static final ASN1ObjectIdentifier C;
    @Deprecated
    public static final ASN1ObjectIdentifier O;
    @Deprecated
    public static final ASN1ObjectIdentifier OU;
    @Deprecated
    public static final ASN1ObjectIdentifier T;
    @Deprecated
    public static final ASN1ObjectIdentifier CN;
    public static final ASN1ObjectIdentifier SN;
    public static final ASN1ObjectIdentifier STREET;
    public static final ASN1ObjectIdentifier SERIALNUMBER;
    public static final ASN1ObjectIdentifier L;
    public static final ASN1ObjectIdentifier ST;
    public static final ASN1ObjectIdentifier SURNAME;
    public static final ASN1ObjectIdentifier GIVENNAME;
    public static final ASN1ObjectIdentifier INITIALS;
    public static final ASN1ObjectIdentifier GENERATION;
    public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER;
    public static final ASN1ObjectIdentifier BUSINESS_CATEGORY;
    public static final ASN1ObjectIdentifier POSTAL_CODE;
    public static final ASN1ObjectIdentifier DN_QUALIFIER;
    public static final ASN1ObjectIdentifier PSEUDONYM;
    public static final ASN1ObjectIdentifier DATE_OF_BIRTH;
    public static final ASN1ObjectIdentifier PLACE_OF_BIRTH;
    public static final ASN1ObjectIdentifier GENDER;
    public static final ASN1ObjectIdentifier COUNTRY_OF_CITIZENSHIP;
    public static final ASN1ObjectIdentifier COUNTRY_OF_RESIDENCE;
    public static final ASN1ObjectIdentifier NAME_AT_BIRTH;
    public static final ASN1ObjectIdentifier POSTAL_ADDRESS;
    public static final ASN1ObjectIdentifier DMD_NAME;
    public static final ASN1ObjectIdentifier TELEPHONE_NUMBER;
    public static final ASN1ObjectIdentifier NAME;
    @Deprecated
    public static final ASN1ObjectIdentifier EmailAddress;
    public static final ASN1ObjectIdentifier UnstructuredName;
    public static final ASN1ObjectIdentifier UnstructuredAddress;
    public static final ASN1ObjectIdentifier E;
    public static final ASN1ObjectIdentifier DC;
    public static final ASN1ObjectIdentifier UID;
    public static boolean DefaultReverse;
    public static final Hashtable DefaultSymbols;
    public static final Hashtable RFC2253Symbols;
    public static final Hashtable RFC1779Symbols;
    public static final Hashtable DefaultLookUp;
    @Deprecated
    public static final Hashtable OIDLookUp;
    @Deprecated
    public static final Hashtable SymbolLookUp;
    private static final Boolean TRUE;
    private static final Boolean FALSE;
    private X509NameEntryConverter converter;
    private Vector ordering;
    private Vector values;
    private Vector added;
    private ASN1Sequence seq;
    private boolean isHashCodeCalculated;
    private int hashCodeValue;
    
    public static X509Name getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static X509Name getInstance(final Object o) {
        if (o == null || o instanceof X509Name) {
            return (X509Name)o;
        }
        if (o instanceof X500Name) {
            return new X509Name(ASN1Sequence.getInstance(((X500Name)o).toASN1Primitive()));
        }
        if (o != null) {
            return new X509Name(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    protected X509Name() {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
    }
    
    @Deprecated
    public X509Name(final ASN1Sequence seq) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.seq = seq;
        final Enumeration objects = seq.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Set instance = ASN1Set.getInstance(objects.nextElement().toASN1Primitive());
            for (int i = 0; i < instance.size(); ++i) {
                final ASN1Sequence instance2 = ASN1Sequence.getInstance(instance.getObjectAt(i).toASN1Primitive());
                if (instance2.size() != 2) {
                    throw new IllegalArgumentException("badly sized pair");
                }
                this.ordering.addElement(ASN1ObjectIdentifier.getInstance(instance2.getObjectAt(0)));
                final ASN1Encodable object = instance2.getObjectAt(1);
                if (object instanceof ASN1String && !(object instanceof DERUniversalString)) {
                    final String string = ((ASN1String)object).getString();
                    if (string.length() > 0 && string.charAt(0) == '#') {
                        this.values.addElement("\\" + string);
                    }
                    else {
                        this.values.addElement(string);
                    }
                }
                else {
                    try {
                        this.values.addElement("#" + this.bytesToString(Hex.encode(object.toASN1Primitive().getEncoded("DER"))));
                    }
                    catch (final IOException ex) {
                        throw new IllegalArgumentException("cannot encode value");
                    }
                }
                this.added.addElement((i != 0) ? X509Name.TRUE : X509Name.FALSE);
            }
        }
    }
    
    @Deprecated
    public X509Name(final Hashtable hashtable) {
        this(null, hashtable);
    }
    
    public X509Name(final Vector vector, final Hashtable hashtable) {
        this(vector, hashtable, new X509DefaultEntryConverter());
    }
    
    @Deprecated
    public X509Name(final Vector vector, final Hashtable hashtable, final X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        if (vector != null) {
            for (int i = 0; i != vector.size(); ++i) {
                this.ordering.addElement(vector.elementAt(i));
                this.added.addElement(X509Name.FALSE);
            }
        }
        else {
            final Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                this.ordering.addElement(keys.nextElement());
                this.added.addElement(X509Name.FALSE);
            }
        }
        for (int j = 0; j != this.ordering.size(); ++j) {
            final ASN1ObjectIdentifier asn1ObjectIdentifier = this.ordering.elementAt(j);
            if (hashtable.get(asn1ObjectIdentifier) == null) {
                throw new IllegalArgumentException("No attribute for object id - " + asn1ObjectIdentifier.getId() + " - passed to distinguished name");
            }
            this.values.addElement(hashtable.get(asn1ObjectIdentifier));
        }
    }
    
    @Deprecated
    public X509Name(final Vector vector, final Vector vector2) {
        this(vector, vector2, new X509DefaultEntryConverter());
    }
    
    @Deprecated
    public X509Name(final Vector vector, final Vector vector2, final X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        if (vector.size() != vector2.size()) {
            throw new IllegalArgumentException("oids vector must be same length as values.");
        }
        for (int i = 0; i < vector.size(); ++i) {
            this.ordering.addElement(vector.elementAt(i));
            this.values.addElement(vector2.elementAt(i));
            this.added.addElement(X509Name.FALSE);
        }
    }
    
    @Deprecated
    public X509Name(final String s) {
        this(X509Name.DefaultReverse, X509Name.DefaultLookUp, s);
    }
    
    @Deprecated
    public X509Name(final String s, final X509NameEntryConverter x509NameEntryConverter) {
        this(X509Name.DefaultReverse, X509Name.DefaultLookUp, s, x509NameEntryConverter);
    }
    
    @Deprecated
    public X509Name(final boolean b, final String s) {
        this(b, X509Name.DefaultLookUp, s);
    }
    
    @Deprecated
    public X509Name(final boolean b, final String s, final X509NameEntryConverter x509NameEntryConverter) {
        this(b, X509Name.DefaultLookUp, s, x509NameEntryConverter);
    }
    
    @Deprecated
    public X509Name(final boolean b, final Hashtable hashtable, final String s) {
        this(b, hashtable, s, new X509DefaultEntryConverter());
    }
    
    private ASN1ObjectIdentifier decodeOID(String trim, final Hashtable hashtable) {
        trim = trim.trim();
        if (Strings.toUpperCase(trim).startsWith("OID.")) {
            return new ASN1ObjectIdentifier(trim.substring(4));
        }
        if (trim.charAt(0) >= '0' && trim.charAt(0) <= '9') {
            return new ASN1ObjectIdentifier(trim);
        }
        final ASN1ObjectIdentifier asn1ObjectIdentifier = hashtable.get(Strings.toLowerCase(trim));
        if (asn1ObjectIdentifier == null) {
            throw new IllegalArgumentException("Unknown object id - " + trim + " - passed to distinguished name");
        }
        return asn1ObjectIdentifier;
    }
    
    private String unescape(final String s) {
        if (s.length() == 0 || (s.indexOf(92) < 0 && s.indexOf(34) < 0)) {
            return s.trim();
        }
        final char[] charArray = s.toCharArray();
        int n = 0;
        boolean b = false;
        final StringBuffer sb = new StringBuffer(s.length());
        int n2 = 0;
        if (charArray[0] == '\\' && charArray[1] == '#') {
            n2 = 2;
            sb.append("\\#");
        }
        boolean b2 = false;
        int length = 0;
        for (int i = n2; i != charArray.length; ++i) {
            final char c = charArray[i];
            if (c != ' ') {
                b2 = true;
            }
            if (c == '\"') {
                if (n == 0) {
                    b = !b;
                }
                else {
                    sb.append(c);
                }
                n = 0;
            }
            else if (c == '\\' && n == 0 && !b) {
                n = 1;
                length = sb.length();
            }
            else if (c != ' ' || n != 0 || b2) {
                sb.append(c);
                n = 0;
            }
        }
        if (sb.length() > 0) {
            while (sb.charAt(sb.length() - 1) == ' ' && length != sb.length() - 1) {
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.toString();
    }
    
    public X509Name(final boolean b, final Hashtable hashtable, final String s, final X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        final X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(s);
        while (x509NameTokenizer.hasMoreTokens()) {
            final String nextToken = x509NameTokenizer.nextToken();
            if (nextToken.indexOf(43) > 0) {
                final X509NameTokenizer x509NameTokenizer2 = new X509NameTokenizer(nextToken, '+');
                this.addEntry(hashtable, x509NameTokenizer2.nextToken(), X509Name.FALSE);
                while (x509NameTokenizer2.hasMoreTokens()) {
                    this.addEntry(hashtable, x509NameTokenizer2.nextToken(), X509Name.TRUE);
                }
            }
            else {
                this.addEntry(hashtable, nextToken, X509Name.FALSE);
            }
        }
        if (b) {
            final Vector<Object> ordering = new Vector<Object>();
            final Vector<Object> values = new Vector<Object>();
            final Vector<Object> added = new Vector<Object>();
            int n = 1;
            for (int i = 0; i < this.ordering.size(); ++i) {
                if (this.added.elementAt(i)) {
                    ordering.insertElementAt(this.ordering.elementAt(i), n);
                    values.insertElementAt(this.values.elementAt(i), n);
                    added.insertElementAt(this.added.elementAt(i), n);
                    ++n;
                }
                else {
                    ordering.insertElementAt(this.ordering.elementAt(i), 0);
                    values.insertElementAt(this.values.elementAt(i), 0);
                    added.insertElementAt(this.added.elementAt(i), 0);
                    n = 1;
                }
            }
            this.ordering = ordering;
            this.values = values;
            this.added = added;
        }
    }
    
    private void addEntry(final Hashtable hashtable, final String s, final Boolean b) {
        final X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(s, '=');
        final String nextToken = x509NameTokenizer.nextToken();
        if (!x509NameTokenizer.hasMoreTokens()) {
            throw new IllegalArgumentException("badly formatted directory string");
        }
        final String nextToken2 = x509NameTokenizer.nextToken();
        this.ordering.addElement(this.decodeOID(nextToken, hashtable));
        this.values.addElement(this.unescape(nextToken2));
        this.added.addElement(b);
    }
    
    public Vector getOIDs() {
        final Vector vector = new Vector();
        for (int i = 0; i != this.ordering.size(); ++i) {
            vector.addElement(this.ordering.elementAt(i));
        }
        return vector;
    }
    
    public Vector getValues() {
        final Vector vector = new Vector();
        for (int i = 0; i != this.values.size(); ++i) {
            vector.addElement(this.values.elementAt(i));
        }
        return vector;
    }
    
    public Vector getValues(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Vector vector = new Vector();
        for (int i = 0; i != this.values.size(); ++i) {
            if (this.ordering.elementAt(i).equals(asn1ObjectIdentifier)) {
                final String s = this.values.elementAt(i);
                if (s.length() > 2 && s.charAt(0) == '\\' && s.charAt(1) == '#') {
                    vector.addElement(s.substring(1));
                }
                else {
                    vector.addElement(s);
                }
            }
        }
        return vector;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.seq == null) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            ASN1ObjectIdentifier asn1ObjectIdentifier = null;
            for (int i = 0; i != this.ordering.size(); ++i) {
                final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
                final ASN1ObjectIdentifier asn1ObjectIdentifier2 = this.ordering.elementAt(i);
                asn1EncodableVector3.add(asn1ObjectIdentifier2);
                asn1EncodableVector3.add(this.converter.getConvertedValue(asn1ObjectIdentifier2, this.values.elementAt(i)));
                if (asn1ObjectIdentifier == null || (boolean)this.added.elementAt(i)) {
                    asn1EncodableVector2.add(new DERSequence(asn1EncodableVector3));
                }
                else {
                    asn1EncodableVector.add(new DERSet(asn1EncodableVector2));
                    asn1EncodableVector2 = new ASN1EncodableVector();
                    asn1EncodableVector2.add(new DERSequence(asn1EncodableVector3));
                }
                asn1ObjectIdentifier = asn1ObjectIdentifier2;
            }
            asn1EncodableVector.add(new DERSet(asn1EncodableVector2));
            this.seq = new DERSequence(asn1EncodableVector);
        }
        return this.seq;
    }
    
    public boolean equals(final Object o, final boolean b) {
        if (!b) {
            return this.equals(o);
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509Name) && !(o instanceof ASN1Sequence)) {
            return false;
        }
        if (this.toASN1Primitive().equals(((ASN1Encodable)o).toASN1Primitive())) {
            return true;
        }
        X509Name instance;
        try {
            instance = getInstance(o);
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
        final int size = this.ordering.size();
        if (size != instance.ordering.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!((ASN1ObjectIdentifier)this.ordering.elementAt(i)).equals(instance.ordering.elementAt(i))) {
                return false;
            }
            if (!this.equivalentStrings((String)this.values.elementAt(i), (String)instance.values.elementAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.isHashCodeCalculated) {
            return this.hashCodeValue;
        }
        this.isHashCodeCalculated = true;
        for (int i = 0; i != this.ordering.size(); ++i) {
            final String stripInternalSpaces = this.stripInternalSpaces(this.canonicalize(this.values.elementAt(i)));
            this.hashCodeValue ^= this.ordering.elementAt(i).hashCode();
            this.hashCodeValue ^= stripInternalSpaces.hashCode();
        }
        return this.hashCodeValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509Name) && !(o instanceof ASN1Sequence)) {
            return false;
        }
        if (this.toASN1Primitive().equals(((ASN1Encodable)o).toASN1Primitive())) {
            return true;
        }
        X509Name instance;
        try {
            instance = getInstance(o);
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
        final int size = this.ordering.size();
        if (size != instance.ordering.size()) {
            return false;
        }
        final boolean[] array = new boolean[size];
        int n;
        int n2;
        int n3;
        if (this.ordering.elementAt(0).equals(instance.ordering.elementAt(0))) {
            n = 0;
            n2 = size;
            n3 = 1;
        }
        else {
            n = size - 1;
            n2 = -1;
            n3 = -1;
        }
        for (int i = n; i != n2; i += n3) {
            boolean b = false;
            final ASN1ObjectIdentifier asn1ObjectIdentifier = this.ordering.elementAt(i);
            final String s = this.values.elementAt(i);
            for (int j = 0; j < size; ++j) {
                if (!array[j]) {
                    if (asn1ObjectIdentifier.equals(instance.ordering.elementAt(j)) && this.equivalentStrings(s, (String)instance.values.elementAt(j))) {
                        array[j] = true;
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equivalentStrings(final String s, final String s2) {
        final String canonicalize = this.canonicalize(s);
        final String canonicalize2 = this.canonicalize(s2);
        return canonicalize.equals(canonicalize2) || this.stripInternalSpaces(canonicalize).equals(this.stripInternalSpaces(canonicalize2));
    }
    
    private String canonicalize(final String s) {
        String s2 = Strings.toLowerCase(s.trim());
        if (s2.length() > 0 && s2.charAt(0) == '#') {
            final ASN1Primitive decodeObject = this.decodeObject(s2);
            if (decodeObject instanceof ASN1String) {
                s2 = Strings.toLowerCase(((ASN1String)decodeObject).getString().trim());
            }
        }
        return s2;
    }
    
    private ASN1Primitive decodeObject(final String s) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decode(s.substring(1)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unknown encoding in name: " + ex);
        }
    }
    
    private String stripInternalSpaces(final String s) {
        final StringBuffer sb = new StringBuffer();
        if (s.length() != 0) {
            char char1 = s.charAt(0);
            sb.append(char1);
            for (int i = 1; i < s.length(); ++i) {
                final char char2 = s.charAt(i);
                if (char1 != ' ' || char2 != ' ') {
                    sb.append(char2);
                }
                char1 = char2;
            }
        }
        return sb.toString();
    }
    
    private void appendValue(final StringBuffer sb, final Hashtable hashtable, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        final String s2 = hashtable.get(asn1ObjectIdentifier);
        if (s2 != null) {
            sb.append(s2);
        }
        else {
            sb.append(asn1ObjectIdentifier.getId());
        }
        sb.append('=');
        int i = sb.length();
        sb.append(s);
        int length = sb.length();
        if (s.length() >= 2 && s.charAt(0) == '\\' && s.charAt(1) == '#') {
            i += 2;
        }
        while (i < length && sb.charAt(i) == ' ') {
            sb.insert(i, "\\");
            i += 2;
            ++length;
        }
        while (--length > i && sb.charAt(length) == ' ') {
            sb.insert(length, '\\');
        }
        while (i <= length) {
            switch (sb.charAt(i)) {
                case '\"':
                case '+':
                case ',':
                case ';':
                case '<':
                case '=':
                case '>':
                case '\\': {
                    sb.insert(i, "\\");
                    i += 2;
                    ++length;
                    continue;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
    }
    
    public String toString(final boolean b, final Hashtable hashtable) {
        final StringBuffer sb = new StringBuffer();
        final Vector vector = new Vector();
        int n = 1;
        StringBuffer sb2 = null;
        for (int i = 0; i < this.ordering.size(); ++i) {
            if (this.added.elementAt(i)) {
                sb2.append('+');
                this.appendValue(sb2, hashtable, (ASN1ObjectIdentifier)this.ordering.elementAt(i), (String)this.values.elementAt(i));
            }
            else {
                sb2 = new StringBuffer();
                this.appendValue(sb2, hashtable, (ASN1ObjectIdentifier)this.ordering.elementAt(i), (String)this.values.elementAt(i));
                vector.addElement(sb2);
            }
        }
        if (b) {
            for (int j = vector.size() - 1; j >= 0; --j) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(',');
                }
                sb.append(vector.elementAt(j).toString());
            }
        }
        else {
            for (int k = 0; k < vector.size(); ++k) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(',');
                }
                sb.append(vector.elementAt(k).toString());
            }
        }
        return sb.toString();
    }
    
    private String bytesToString(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        return new String(array2);
    }
    
    @Override
    public String toString() {
        return this.toString(X509Name.DefaultReverse, X509Name.DefaultSymbols);
    }
    
    static {
        C = new ASN1ObjectIdentifier("2.5.4.6");
        O = new ASN1ObjectIdentifier("2.5.4.10");
        OU = new ASN1ObjectIdentifier("2.5.4.11");
        T = new ASN1ObjectIdentifier("2.5.4.12");
        CN = new ASN1ObjectIdentifier("2.5.4.3");
        SN = new ASN1ObjectIdentifier("2.5.4.5");
        STREET = new ASN1ObjectIdentifier("2.5.4.9");
        SERIALNUMBER = X509Name.SN;
        L = new ASN1ObjectIdentifier("2.5.4.7");
        ST = new ASN1ObjectIdentifier("2.5.4.8");
        SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
        GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
        INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
        GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
        UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");
        BUSINESS_CATEGORY = new ASN1ObjectIdentifier("2.5.4.15");
        POSTAL_CODE = new ASN1ObjectIdentifier("2.5.4.17");
        DN_QUALIFIER = new ASN1ObjectIdentifier("2.5.4.46");
        PSEUDONYM = new ASN1ObjectIdentifier("2.5.4.65");
        DATE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.1");
        PLACE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.2");
        GENDER = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.3");
        COUNTRY_OF_CITIZENSHIP = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.4");
        COUNTRY_OF_RESIDENCE = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.5");
        NAME_AT_BIRTH = new ASN1ObjectIdentifier("1.3.36.8.3.14");
        POSTAL_ADDRESS = new ASN1ObjectIdentifier("2.5.4.16");
        DMD_NAME = new ASN1ObjectIdentifier("2.5.4.54");
        TELEPHONE_NUMBER = X509ObjectIdentifiers.id_at_telephoneNumber;
        NAME = X509ObjectIdentifiers.id_at_name;
        EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
        UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
        UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
        E = X509Name.EmailAddress;
        DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
        UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
        X509Name.DefaultReverse = false;
        DefaultSymbols = new Hashtable();
        RFC2253Symbols = new Hashtable();
        RFC1779Symbols = new Hashtable();
        DefaultLookUp = new Hashtable();
        OIDLookUp = X509Name.DefaultSymbols;
        SymbolLookUp = X509Name.DefaultLookUp;
        TRUE = new Boolean(true);
        FALSE = new Boolean(false);
        X509Name.DefaultSymbols.put(X509Name.C, "C");
        X509Name.DefaultSymbols.put(X509Name.O, "O");
        X509Name.DefaultSymbols.put(X509Name.T, "T");
        X509Name.DefaultSymbols.put(X509Name.OU, "OU");
        X509Name.DefaultSymbols.put(X509Name.CN, "CN");
        X509Name.DefaultSymbols.put(X509Name.L, "L");
        X509Name.DefaultSymbols.put(X509Name.ST, "ST");
        X509Name.DefaultSymbols.put(X509Name.SN, "SERIALNUMBER");
        X509Name.DefaultSymbols.put(X509Name.EmailAddress, "E");
        X509Name.DefaultSymbols.put(X509Name.DC, "DC");
        X509Name.DefaultSymbols.put(X509Name.UID, "UID");
        X509Name.DefaultSymbols.put(X509Name.STREET, "STREET");
        X509Name.DefaultSymbols.put(X509Name.SURNAME, "SURNAME");
        X509Name.DefaultSymbols.put(X509Name.GIVENNAME, "GIVENNAME");
        X509Name.DefaultSymbols.put(X509Name.INITIALS, "INITIALS");
        X509Name.DefaultSymbols.put(X509Name.GENERATION, "GENERATION");
        X509Name.DefaultSymbols.put(X509Name.UnstructuredAddress, "unstructuredAddress");
        X509Name.DefaultSymbols.put(X509Name.UnstructuredName, "unstructuredName");
        X509Name.DefaultSymbols.put(X509Name.UNIQUE_IDENTIFIER, "UniqueIdentifier");
        X509Name.DefaultSymbols.put(X509Name.DN_QUALIFIER, "DN");
        X509Name.DefaultSymbols.put(X509Name.PSEUDONYM, "Pseudonym");
        X509Name.DefaultSymbols.put(X509Name.POSTAL_ADDRESS, "PostalAddress");
        X509Name.DefaultSymbols.put(X509Name.NAME_AT_BIRTH, "NameAtBirth");
        X509Name.DefaultSymbols.put(X509Name.COUNTRY_OF_CITIZENSHIP, "CountryOfCitizenship");
        X509Name.DefaultSymbols.put(X509Name.COUNTRY_OF_RESIDENCE, "CountryOfResidence");
        X509Name.DefaultSymbols.put(X509Name.GENDER, "Gender");
        X509Name.DefaultSymbols.put(X509Name.PLACE_OF_BIRTH, "PlaceOfBirth");
        X509Name.DefaultSymbols.put(X509Name.DATE_OF_BIRTH, "DateOfBirth");
        X509Name.DefaultSymbols.put(X509Name.POSTAL_CODE, "PostalCode");
        X509Name.DefaultSymbols.put(X509Name.BUSINESS_CATEGORY, "BusinessCategory");
        X509Name.DefaultSymbols.put(X509Name.TELEPHONE_NUMBER, "TelephoneNumber");
        X509Name.DefaultSymbols.put(X509Name.NAME, "Name");
        X509Name.RFC2253Symbols.put(X509Name.C, "C");
        X509Name.RFC2253Symbols.put(X509Name.O, "O");
        X509Name.RFC2253Symbols.put(X509Name.OU, "OU");
        X509Name.RFC2253Symbols.put(X509Name.CN, "CN");
        X509Name.RFC2253Symbols.put(X509Name.L, "L");
        X509Name.RFC2253Symbols.put(X509Name.ST, "ST");
        X509Name.RFC2253Symbols.put(X509Name.STREET, "STREET");
        X509Name.RFC2253Symbols.put(X509Name.DC, "DC");
        X509Name.RFC2253Symbols.put(X509Name.UID, "UID");
        X509Name.RFC1779Symbols.put(X509Name.C, "C");
        X509Name.RFC1779Symbols.put(X509Name.O, "O");
        X509Name.RFC1779Symbols.put(X509Name.OU, "OU");
        X509Name.RFC1779Symbols.put(X509Name.CN, "CN");
        X509Name.RFC1779Symbols.put(X509Name.L, "L");
        X509Name.RFC1779Symbols.put(X509Name.ST, "ST");
        X509Name.RFC1779Symbols.put(X509Name.STREET, "STREET");
        X509Name.DefaultLookUp.put("c", X509Name.C);
        X509Name.DefaultLookUp.put("o", X509Name.O);
        X509Name.DefaultLookUp.put("t", X509Name.T);
        X509Name.DefaultLookUp.put("ou", X509Name.OU);
        X509Name.DefaultLookUp.put("cn", X509Name.CN);
        X509Name.DefaultLookUp.put("l", X509Name.L);
        X509Name.DefaultLookUp.put("st", X509Name.ST);
        X509Name.DefaultLookUp.put("sn", X509Name.SN);
        X509Name.DefaultLookUp.put("serialnumber", X509Name.SN);
        X509Name.DefaultLookUp.put("street", X509Name.STREET);
        X509Name.DefaultLookUp.put("emailaddress", X509Name.E);
        X509Name.DefaultLookUp.put("dc", X509Name.DC);
        X509Name.DefaultLookUp.put("e", X509Name.E);
        X509Name.DefaultLookUp.put("uid", X509Name.UID);
        X509Name.DefaultLookUp.put("surname", X509Name.SURNAME);
        X509Name.DefaultLookUp.put("givenname", X509Name.GIVENNAME);
        X509Name.DefaultLookUp.put("initials", X509Name.INITIALS);
        X509Name.DefaultLookUp.put("generation", X509Name.GENERATION);
        X509Name.DefaultLookUp.put("unstructuredaddress", X509Name.UnstructuredAddress);
        X509Name.DefaultLookUp.put("unstructuredname", X509Name.UnstructuredName);
        X509Name.DefaultLookUp.put("uniqueidentifier", X509Name.UNIQUE_IDENTIFIER);
        X509Name.DefaultLookUp.put("dn", X509Name.DN_QUALIFIER);
        X509Name.DefaultLookUp.put("pseudonym", X509Name.PSEUDONYM);
        X509Name.DefaultLookUp.put("postaladdress", X509Name.POSTAL_ADDRESS);
        X509Name.DefaultLookUp.put("nameofbirth", X509Name.NAME_AT_BIRTH);
        X509Name.DefaultLookUp.put("countryofcitizenship", X509Name.COUNTRY_OF_CITIZENSHIP);
        X509Name.DefaultLookUp.put("countryofresidence", X509Name.COUNTRY_OF_RESIDENCE);
        X509Name.DefaultLookUp.put("gender", X509Name.GENDER);
        X509Name.DefaultLookUp.put("placeofbirth", X509Name.PLACE_OF_BIRTH);
        X509Name.DefaultLookUp.put("dateofbirth", X509Name.DATE_OF_BIRTH);
        X509Name.DefaultLookUp.put("postalcode", X509Name.POSTAL_CODE);
        X509Name.DefaultLookUp.put("businesscategory", X509Name.BUSINESS_CATEGORY);
        X509Name.DefaultLookUp.put("telephonenumber", X509Name.TELEPHONE_NUMBER);
        X509Name.DefaultLookUp.put("name", X509Name.NAME);
    }
}
