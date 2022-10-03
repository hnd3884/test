package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DERSet;
import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.DERUTF8String;
import com.maverick.crypto.asn1.DERPrintableString;
import com.maverick.crypto.asn1.DERIA5String;
import java.io.IOException;
import java.io.InputStream;
import com.maverick.crypto.asn1.ASN1InputStream;
import java.io.ByteArrayInputStream;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.DERObject;
import java.util.Enumeration;
import com.maverick.crypto.asn1.DERString;
import com.maverick.crypto.asn1.ASN1Set;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.ASN1Sequence;
import java.util.Vector;
import java.util.Hashtable;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.DEREncodable;

public class X509Name implements DEREncodable
{
    public static final DERObjectIdentifier C;
    public static final DERObjectIdentifier O;
    public static final DERObjectIdentifier OU;
    public static final DERObjectIdentifier T;
    public static final DERObjectIdentifier CN;
    public static final DERObjectIdentifier SN;
    public static final DERObjectIdentifier L;
    public static final DERObjectIdentifier ST;
    public static final DERObjectIdentifier SURNAME;
    public static final DERObjectIdentifier GIVENNAME;
    public static final DERObjectIdentifier INITIALS;
    public static final DERObjectIdentifier GENERATION;
    public static final DERObjectIdentifier UNIQUE_IDENTIFIER;
    public static final DERObjectIdentifier EmailAddress;
    public static final DERObjectIdentifier E;
    public static final DERObjectIdentifier DC;
    public static final DERObjectIdentifier UID;
    public static Hashtable OIDLookUp;
    public static boolean DefaultReverse;
    public static Hashtable DefaultSymbols;
    public static Hashtable RFC2253Symbols;
    public static Hashtable SymbolLookUp;
    public static Hashtable DefaultLookUp;
    private Vector x;
    private Vector u;
    private Vector v;
    private ASN1Sequence w;
    
    public static X509Name getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static X509Name getInstance(final Object o) {
        if (o == null || o instanceof X509Name) {
            return (X509Name)o;
        }
        if (o instanceof ASN1Sequence) {
            return new X509Name((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public X509Name(final ASN1Sequence w) {
        this.x = new Vector();
        this.u = new Vector();
        this.v = new Vector();
        this.w = w;
        final Enumeration objects = w.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Set set = objects.nextElement();
            for (int i = 0; i < set.size(); ++i) {
                final ASN1Sequence asn1Sequence = (ASN1Sequence)set.getObjectAt(i);
                this.x.addElement(asn1Sequence.getObjectAt(0));
                this.u.addElement(((DERString)asn1Sequence.getObjectAt(1)).getString());
                this.v.addElement((i != 0) ? new Boolean(true) : new Boolean(false));
            }
        }
    }
    
    public X509Name(final Hashtable hashtable) {
        this(null, hashtable);
    }
    
    public X509Name(final Vector vector, final Hashtable hashtable) {
        this.x = new Vector();
        this.u = new Vector();
        this.v = new Vector();
        if (vector != null) {
            for (int i = 0; i != vector.size(); ++i) {
                this.x.addElement(vector.elementAt(i));
                this.v.addElement(new Boolean(false));
            }
        }
        else {
            final Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                this.x.addElement(keys.nextElement());
                this.v.addElement(new Boolean(false));
            }
        }
        for (int j = 0; j != this.x.size(); ++j) {
            final DERObjectIdentifier derObjectIdentifier = this.x.elementAt(j);
            if (hashtable.get(derObjectIdentifier) == null) {
                throw new IllegalArgumentException("No attribute for object id - " + derObjectIdentifier.getId() + " - passed to distinguished name");
            }
            this.u.addElement(hashtable.get(derObjectIdentifier));
        }
    }
    
    public X509Name(final Vector vector, final Vector vector2) {
        this.x = new Vector();
        this.u = new Vector();
        this.v = new Vector();
        if (vector.size() != vector2.size()) {
            throw new IllegalArgumentException("oids vector must be same length as values.");
        }
        for (int i = 0; i < vector.size(); ++i) {
            this.x.addElement(vector.elementAt(i));
            this.u.addElement(vector2.elementAt(i));
            this.v.addElement(new Boolean(false));
        }
    }
    
    public X509Name(final String s) {
        this(X509Name.DefaultReverse, X509Name.DefaultLookUp, s);
    }
    
    public X509Name(final boolean b, final String s) {
        this(b, X509Name.DefaultLookUp, s);
    }
    
    public X509Name(final boolean b, final Hashtable hashtable, final String s) {
        this.x = new Vector();
        this.u = new Vector();
        this.v = new Vector();
        final X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(s);
        while (x509NameTokenizer.hasMoreTokens()) {
            final String nextToken = x509NameTokenizer.nextToken();
            final int index = nextToken.indexOf(61);
            if (index == -1) {
                throw new IllegalArgumentException("badly formated directory string");
            }
            final String substring = nextToken.substring(0, index);
            final String substring2 = nextToken.substring(index + 1);
            DERObjectIdentifier derObjectIdentifier;
            if (substring.toUpperCase().startsWith("OID.")) {
                derObjectIdentifier = new DERObjectIdentifier(substring.substring(4));
            }
            else if (substring.charAt(0) >= '0' && substring.charAt(0) <= '9') {
                derObjectIdentifier = new DERObjectIdentifier(substring);
            }
            else {
                derObjectIdentifier = hashtable.get(substring.toLowerCase());
                if (derObjectIdentifier == null) {
                    throw new IllegalArgumentException("Unknown object id - " + substring + " - passed to distinguished name");
                }
            }
            this.x.addElement(derObjectIdentifier);
            this.u.addElement(substring2);
            this.v.addElement(new Boolean(false));
        }
        if (b) {
            final Vector<Object> x = new Vector<Object>();
            final Vector<Object> u = new Vector<Object>();
            for (int i = this.x.size() - 1; i >= 0; --i) {
                x.addElement(this.x.elementAt(i));
                u.addElement(this.u.elementAt(i));
                this.v.addElement(new Boolean(false));
            }
            this.x = x;
            this.u = u;
        }
    }
    
    public Vector getOIDs() {
        final Vector vector = new Vector();
        for (int i = 0; i != this.x.size(); ++i) {
            vector.addElement(this.x.elementAt(i));
        }
        return vector;
    }
    
    public Vector getValues() {
        final Vector vector = new Vector();
        for (int i = 0; i != this.u.size(); ++i) {
            vector.addElement(this.u.elementAt(i));
        }
        return vector;
    }
    
    private boolean b(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) > '\u007f') {
                return false;
            }
        }
        return true;
    }
    
    public DERObject getDERObject() {
        if (this.w == null) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            for (int i = 0; i != this.x.size(); ++i) {
                final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
                final DERObjectIdentifier derObjectIdentifier = this.x.elementAt(i);
                asn1EncodableVector2.add(derObjectIdentifier);
                final String s = this.u.elementAt(i);
                if (s.charAt(0) == '#') {
                    final String lowerCase = s.toLowerCase();
                    final byte[] array = new byte[lowerCase.length() / 2];
                    for (int j = 0; j != array.length; ++j) {
                        final char char1 = lowerCase.charAt(j * 2 + 1);
                        final char char2 = lowerCase.charAt(j * 2 + 2);
                        if (char1 < 'a') {
                            array[j] = (byte)(char1 - '0' << 4);
                        }
                        else {
                            array[j] = (byte)(char1 - 'a' + 10 << 4);
                        }
                        if (char2 < 'a') {
                            final byte[] array2 = array;
                            final int n = j;
                            array2[n] |= (byte)(char2 - '0');
                        }
                        else {
                            final byte[] array3 = array;
                            final int n2 = j;
                            array3[n2] |= (byte)(char2 - 'a' + 10);
                        }
                    }
                    final ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(array));
                    try {
                        asn1EncodableVector2.add(asn1InputStream.readObject());
                    }
                    catch (final IOException ex) {
                        throw new RuntimeException("bad object in '#' string");
                    }
                }
                else if (derObjectIdentifier.equals(X509Name.EmailAddress)) {
                    asn1EncodableVector2.add(new DERIA5String(s));
                }
                else if (this.b(s)) {
                    asn1EncodableVector2.add(new DERPrintableString(s));
                }
                else {
                    asn1EncodableVector2.add(new DERUTF8String(s));
                }
                asn1EncodableVector.add(new DERSet(new DERSequence(asn1EncodableVector2)));
            }
            this.w = new DERSequence(asn1EncodableVector);
        }
        return this.w;
    }
    
    public boolean equals(final Object o, final boolean b) {
        if (o == this) {
            return true;
        }
        if (!b) {
            return this.equals(o);
        }
        if (o == null || !(o instanceof X509Name)) {
            return false;
        }
        final X509Name x509Name = (X509Name)o;
        final int size = this.x.size();
        if (size != x509Name.x.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final String id = this.x.elementAt(i).getId();
            final String s = this.u.elementAt(i);
            final String id2 = x509Name.x.elementAt(i).getId();
            final String s2 = x509Name.u.elementAt(i);
            if (id.equals(id2)) {
                final String lowerCase = s.trim().toLowerCase();
                final String lowerCase2 = s2.trim().toLowerCase();
                if (!lowerCase.equals(lowerCase2)) {
                    final StringBuffer sb = new StringBuffer();
                    final StringBuffer sb2 = new StringBuffer();
                    if (lowerCase.length() != 0) {
                        char char1 = lowerCase.charAt(0);
                        sb.append(char1);
                        for (int j = 1; j < lowerCase.length(); ++j) {
                            final char char2 = lowerCase.charAt(j);
                            if (char1 != ' ' || char2 != ' ') {
                                sb.append(char2);
                            }
                            char1 = char2;
                        }
                    }
                    if (lowerCase2.length() != 0) {
                        char char3 = lowerCase2.charAt(0);
                        sb2.append(char3);
                        for (int k = 1; k < lowerCase2.length(); ++k) {
                            final char char4 = lowerCase2.charAt(k);
                            if (char3 != ' ' || char4 != ' ') {
                                sb2.append(char4);
                            }
                            char3 = char4;
                        }
                    }
                    if (!sb.toString().equals(sb2.toString())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof X509Name)) {
            return false;
        }
        final X509Name x509Name = (X509Name)o;
        if (this.getDERObject().equals(x509Name.getDERObject())) {
            return true;
        }
        final int size = this.x.size();
        if (size != x509Name.x.size()) {
            return false;
        }
        final boolean[] array = new boolean[size];
        for (int i = 0; i < size; ++i) {
            boolean b = false;
            final String id = this.x.elementAt(i).getId();
            String lowerCase = this.u.elementAt(i);
            for (int j = 0; j < size; ++j) {
                if (!array[j]) {
                    final String id2 = x509Name.x.elementAt(j).getId();
                    final String s = x509Name.u.elementAt(j);
                    if (id.equals(id2)) {
                        lowerCase = lowerCase.trim().toLowerCase();
                        final String lowerCase2 = s.trim().toLowerCase();
                        if (lowerCase.equals(lowerCase2)) {
                            array[j] = true;
                            b = true;
                            break;
                        }
                        final StringBuffer sb = new StringBuffer();
                        final StringBuffer sb2 = new StringBuffer();
                        if (lowerCase.length() != 0) {
                            char char1 = lowerCase.charAt(0);
                            sb.append(char1);
                            for (int k = 1; k < lowerCase.length(); ++k) {
                                final char char2 = lowerCase.charAt(k);
                                if (char1 != ' ' || char2 != ' ') {
                                    sb.append(char2);
                                }
                                char1 = char2;
                            }
                        }
                        if (lowerCase2.length() != 0) {
                            char char3 = lowerCase2.charAt(0);
                            sb2.append(char3);
                            for (int l = 1; l < lowerCase2.length(); ++l) {
                                final char char4 = lowerCase2.charAt(l);
                                if (char3 != ' ' || char4 != ' ') {
                                    sb2.append(char4);
                                }
                                char3 = char4;
                            }
                        }
                        if (sb.toString().equals(sb2.toString())) {
                            array[j] = true;
                            b = true;
                            break;
                        }
                    }
                }
            }
            if (!b) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        final Enumeration objects = ((ASN1Sequence)this.getDERObject()).getObjects();
        int n = 0;
        while (objects.hasMoreElements()) {
            n ^= objects.nextElement().hashCode();
        }
        return n;
    }
    
    private void b(final StringBuffer sb, final Hashtable hashtable, final DERObjectIdentifier derObjectIdentifier, final String s) {
        final String s2 = hashtable.get(derObjectIdentifier);
        if (s2 != null) {
            sb.append(s2);
        }
        else {
            sb.append(derObjectIdentifier.getId());
        }
        sb.append("=");
        int i = sb.length();
        sb.append(s);
        for (int length = sb.length(); i != length; ++i) {
            if (sb.charAt(i) == ',' || sb.charAt(i) == '\"' || sb.charAt(i) == '\\' || sb.charAt(i) == '+' || sb.charAt(i) == '<' || sb.charAt(i) == '>' || sb.charAt(i) == ';') {
                sb.insert(i, "\\");
                ++i;
                ++length;
            }
        }
    }
    
    public String toString(final boolean b, final Hashtable hashtable) {
        final StringBuffer sb = new StringBuffer();
        int n = 1;
        if (b) {
            for (int i = this.x.size() - 1; i >= 0; --i) {
                if (n != 0) {
                    n = 0;
                }
                else if (this.v.elementAt(i + 1)) {
                    sb.append(" + ");
                }
                else {
                    sb.append(",");
                }
                this.b(sb, hashtable, (DERObjectIdentifier)this.x.elementAt(i), (String)this.u.elementAt(i));
            }
        }
        else {
            for (int j = 0; j < this.x.size(); ++j) {
                if (n != 0) {
                    n = 0;
                }
                else if (this.v.elementAt(j)) {
                    sb.append(" + ");
                }
                else {
                    sb.append(",");
                }
                this.b(sb, hashtable, (DERObjectIdentifier)this.x.elementAt(j), (String)this.u.elementAt(j));
            }
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.toString(X509Name.DefaultReverse, X509Name.DefaultSymbols);
    }
    
    static {
        C = new DERObjectIdentifier("2.5.4.6");
        O = new DERObjectIdentifier("2.5.4.10");
        OU = new DERObjectIdentifier("2.5.4.11");
        T = new DERObjectIdentifier("2.5.4.12");
        CN = new DERObjectIdentifier("2.5.4.3");
        SN = new DERObjectIdentifier("2.5.4.5");
        L = new DERObjectIdentifier("2.5.4.7");
        ST = new DERObjectIdentifier("2.5.4.8");
        SURNAME = new DERObjectIdentifier("2.5.4.4");
        GIVENNAME = new DERObjectIdentifier("2.5.4.42");
        INITIALS = new DERObjectIdentifier("2.5.4.43");
        GENERATION = new DERObjectIdentifier("2.5.4.44");
        UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
        EmailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");
        E = X509Name.EmailAddress;
        DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
        UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
        X509Name.OIDLookUp = new Hashtable();
        X509Name.DefaultReverse = false;
        X509Name.DefaultSymbols = X509Name.OIDLookUp;
        X509Name.RFC2253Symbols = new Hashtable();
        X509Name.SymbolLookUp = new Hashtable();
        X509Name.DefaultLookUp = X509Name.SymbolLookUp;
        X509Name.DefaultSymbols.put(X509Name.C, "C");
        X509Name.DefaultSymbols.put(X509Name.O, "O");
        X509Name.DefaultSymbols.put(X509Name.T, "T");
        X509Name.DefaultSymbols.put(X509Name.OU, "OU");
        X509Name.DefaultSymbols.put(X509Name.CN, "CN");
        X509Name.DefaultSymbols.put(X509Name.L, "L");
        X509Name.DefaultSymbols.put(X509Name.ST, "ST");
        X509Name.DefaultSymbols.put(X509Name.SN, "SN");
        X509Name.DefaultSymbols.put(X509Name.EmailAddress, "E");
        X509Name.DefaultSymbols.put(X509Name.DC, "DC");
        X509Name.DefaultSymbols.put(X509Name.UID, "UID");
        X509Name.DefaultSymbols.put(X509Name.SURNAME, "SURNAME");
        X509Name.DefaultSymbols.put(X509Name.GIVENNAME, "GIVENNAME");
        X509Name.DefaultSymbols.put(X509Name.INITIALS, "INITIALS");
        X509Name.DefaultSymbols.put(X509Name.GENERATION, "GENERATION");
        X509Name.RFC2253Symbols.put(X509Name.C, "C");
        X509Name.RFC2253Symbols.put(X509Name.O, "O");
        X509Name.RFC2253Symbols.put(X509Name.T, "T");
        X509Name.RFC2253Symbols.put(X509Name.OU, "OU");
        X509Name.RFC2253Symbols.put(X509Name.CN, "CN");
        X509Name.RFC2253Symbols.put(X509Name.L, "L");
        X509Name.RFC2253Symbols.put(X509Name.ST, "ST");
        X509Name.RFC2253Symbols.put(X509Name.SN, "SN");
        X509Name.RFC2253Symbols.put(X509Name.EmailAddress, "EMAILADDRESS");
        X509Name.RFC2253Symbols.put(X509Name.DC, "DC");
        X509Name.RFC2253Symbols.put(X509Name.UID, "UID");
        X509Name.RFC2253Symbols.put(X509Name.SURNAME, "SURNAME");
        X509Name.RFC2253Symbols.put(X509Name.GIVENNAME, "GIVENNAME");
        X509Name.RFC2253Symbols.put(X509Name.INITIALS, "INITIALS");
        X509Name.RFC2253Symbols.put(X509Name.GENERATION, "GENERATION");
        X509Name.DefaultLookUp.put("c", X509Name.C);
        X509Name.DefaultLookUp.put("o", X509Name.O);
        X509Name.DefaultLookUp.put("t", X509Name.T);
        X509Name.DefaultLookUp.put("ou", X509Name.OU);
        X509Name.DefaultLookUp.put("cn", X509Name.CN);
        X509Name.DefaultLookUp.put("l", X509Name.L);
        X509Name.DefaultLookUp.put("st", X509Name.ST);
        X509Name.DefaultLookUp.put("sn", X509Name.SN);
        X509Name.DefaultLookUp.put("emailaddress", X509Name.E);
        X509Name.DefaultLookUp.put("dc", X509Name.DC);
        X509Name.DefaultLookUp.put("e", X509Name.E);
        X509Name.DefaultLookUp.put("uid", X509Name.UID);
        X509Name.DefaultLookUp.put("surname", X509Name.SURNAME);
        X509Name.DefaultLookUp.put("givenname", X509Name.GIVENNAME);
        X509Name.DefaultLookUp.put("initials", X509Name.INITIALS);
        X509Name.DefaultLookUp.put("generation", X509Name.GENERATION);
    }
}
