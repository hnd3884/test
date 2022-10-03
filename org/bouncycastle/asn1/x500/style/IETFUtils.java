package org.bouncycastle.asn1.x500.style;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.asn1.DERUniversalString;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Strings;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Vector;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameStyle;

public class IETFUtils
{
    private static String unescape(final String s) {
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
        char c = '\0';
        for (int i = n2; i != charArray.length; ++i) {
            final char c2 = charArray[i];
            if (c2 != ' ') {
                b2 = true;
            }
            if (c2 == '\"') {
                if (n == 0) {
                    b = !b;
                }
                else {
                    sb.append(c2);
                }
                n = 0;
            }
            else if (c2 == '\\' && n == 0 && !b) {
                n = 1;
                length = sb.length();
            }
            else if (c2 != ' ' || n != 0 || b2) {
                if (n != 0 && isHexDigit(c2)) {
                    if (c != '\0') {
                        sb.append((char)(convertHex(c) * 16 + convertHex(c2)));
                        n = 0;
                        c = '\0';
                    }
                    else {
                        c = c2;
                    }
                }
                else {
                    sb.append(c2);
                    n = 0;
                }
            }
        }
        if (sb.length() > 0) {
            while (sb.charAt(sb.length() - 1) == ' ' && length != sb.length() - 1) {
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.toString();
    }
    
    private static boolean isHexDigit(final char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }
    
    private static int convertHex(final char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        return c - 'A' + 10;
    }
    
    public static RDN[] rDNsFromString(final String s, final X500NameStyle x500NameStyle) {
        final X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(s);
        final X500NameBuilder x500NameBuilder = new X500NameBuilder(x500NameStyle);
        while (x500NameTokenizer.hasMoreTokens()) {
            final String nextToken = x500NameTokenizer.nextToken();
            if (nextToken.indexOf(43) > 0) {
                final X500NameTokenizer x500NameTokenizer2 = new X500NameTokenizer(nextToken, '+');
                final X500NameTokenizer x500NameTokenizer3 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
                final String nextToken2 = x500NameTokenizer3.nextToken();
                if (!x500NameTokenizer3.hasMoreTokens()) {
                    throw new IllegalArgumentException("badly formatted directory string");
                }
                final String nextToken3 = x500NameTokenizer3.nextToken();
                final ASN1ObjectIdentifier attrNameToOID = x500NameStyle.attrNameToOID(nextToken2.trim());
                if (x500NameTokenizer2.hasMoreTokens()) {
                    final Vector<ASN1ObjectIdentifier> vector = new Vector<ASN1ObjectIdentifier>();
                    final Vector vector2 = new Vector();
                    vector.addElement(attrNameToOID);
                    vector2.addElement(unescape(nextToken3));
                    while (x500NameTokenizer2.hasMoreTokens()) {
                        final X500NameTokenizer x500NameTokenizer4 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
                        final String nextToken4 = x500NameTokenizer4.nextToken();
                        if (!x500NameTokenizer4.hasMoreTokens()) {
                            throw new IllegalArgumentException("badly formatted directory string");
                        }
                        final String nextToken5 = x500NameTokenizer4.nextToken();
                        vector.addElement(x500NameStyle.attrNameToOID(nextToken4.trim()));
                        vector2.addElement(unescape(nextToken5));
                    }
                    x500NameBuilder.addMultiValuedRDN(toOIDArray(vector), toValueArray(vector2));
                }
                else {
                    x500NameBuilder.addRDN(attrNameToOID, unescape(nextToken3));
                }
            }
            else {
                final X500NameTokenizer x500NameTokenizer5 = new X500NameTokenizer(nextToken, '=');
                final String nextToken6 = x500NameTokenizer5.nextToken();
                if (!x500NameTokenizer5.hasMoreTokens()) {
                    throw new IllegalArgumentException("badly formatted directory string");
                }
                x500NameBuilder.addRDN(x500NameStyle.attrNameToOID(nextToken6.trim()), unescape(x500NameTokenizer5.nextToken()));
            }
        }
        return x500NameBuilder.build().getRDNs();
    }
    
    private static String[] toValueArray(final Vector vector) {
        final String[] array = new String[vector.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (String)vector.elementAt(i);
        }
        return array;
    }
    
    private static ASN1ObjectIdentifier[] toOIDArray(final Vector vector) {
        final ASN1ObjectIdentifier[] array = new ASN1ObjectIdentifier[vector.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (ASN1ObjectIdentifier)vector.elementAt(i);
        }
        return array;
    }
    
    public static String[] findAttrNamesForOID(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Hashtable hashtable) {
        int n = 0;
        final Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            if (asn1ObjectIdentifier.equals(elements.nextElement())) {
                ++n;
            }
        }
        final String[] array = new String[n];
        int n2 = 0;
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            if (asn1ObjectIdentifier.equals(hashtable.get(s))) {
                array[n2++] = s;
            }
        }
        return array;
    }
    
    public static ASN1ObjectIdentifier decodeAttrName(final String s, final Hashtable hashtable) {
        if (Strings.toUpperCase(s).startsWith("OID.")) {
            return new ASN1ObjectIdentifier(s.substring(4));
        }
        if (s.charAt(0) >= '0' && s.charAt(0) <= '9') {
            return new ASN1ObjectIdentifier(s);
        }
        final ASN1ObjectIdentifier asn1ObjectIdentifier = hashtable.get(Strings.toLowerCase(s));
        if (asn1ObjectIdentifier == null) {
            throw new IllegalArgumentException("Unknown object id - " + s + " - passed to distinguished name");
        }
        return asn1ObjectIdentifier;
    }
    
    public static ASN1Encodable valueFromHexString(final String s, final int n) throws IOException {
        final byte[] array = new byte[(s.length() - n) / 2];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (byte)(convertHex(s.charAt(i * 2 + n)) << 4 | convertHex(s.charAt(i * 2 + n + 1)));
        }
        return ASN1Primitive.fromByteArray(array);
    }
    
    public static void appendRDN(final StringBuffer sb, final RDN rdn, final Hashtable hashtable) {
        if (rdn.isMultiValued()) {
            final AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
            int n = 1;
            for (int i = 0; i != typesAndValues.length; ++i) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append('+');
                }
                appendTypeAndValue(sb, typesAndValues[i], hashtable);
            }
        }
        else if (rdn.getFirst() != null) {
            appendTypeAndValue(sb, rdn.getFirst(), hashtable);
        }
    }
    
    public static void appendTypeAndValue(final StringBuffer sb, final AttributeTypeAndValue attributeTypeAndValue, final Hashtable hashtable) {
        final String s = hashtable.get(attributeTypeAndValue.getType());
        if (s != null) {
            sb.append(s);
        }
        else {
            sb.append(attributeTypeAndValue.getType().getId());
        }
        sb.append('=');
        sb.append(valueToString(attributeTypeAndValue.getValue()));
    }
    
    public static String valueToString(final ASN1Encodable asn1Encodable) {
        final StringBuffer sb = new StringBuffer();
        if (asn1Encodable instanceof ASN1String && !(asn1Encodable instanceof DERUniversalString)) {
            final String string = ((ASN1String)asn1Encodable).getString();
            if (string.length() > 0 && string.charAt(0) == '#') {
                sb.append("\\" + string);
            }
            else {
                sb.append(string);
            }
        }
        else {
            try {
                sb.append("#" + bytesToString(Hex.encode(asn1Encodable.toASN1Primitive().getEncoded("DER"))));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("Other value has no encoded form");
            }
        }
        int length = sb.length();
        int i = 0;
        if (sb.length() >= 2 && sb.charAt(0) == '\\' && sb.charAt(1) == '#') {
            i += 2;
        }
        while (i != length) {
            if (sb.charAt(i) == ',' || sb.charAt(i) == '\"' || sb.charAt(i) == '\\' || sb.charAt(i) == '+' || sb.charAt(i) == '=' || sb.charAt(i) == '<' || sb.charAt(i) == '>' || sb.charAt(i) == ';') {
                sb.insert(i, "\\");
                ++i;
                ++length;
            }
            ++i;
        }
        int n = 0;
        if (sb.length() > 0) {
            while (sb.length() > n && sb.charAt(n) == ' ') {
                sb.insert(n, "\\");
                n += 2;
            }
        }
        for (int n2 = sb.length() - 1; n2 >= 0 && sb.charAt(n2) == ' '; --n2) {
            sb.insert(n2, '\\');
        }
        return sb.toString();
    }
    
    private static String bytesToString(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        return new String(array2);
    }
    
    public static String canonicalize(final String s) {
        String s2 = Strings.toLowerCase(s);
        if (s2.length() > 0 && s2.charAt(0) == '#') {
            final ASN1Primitive decodeObject = decodeObject(s2);
            if (decodeObject instanceof ASN1String) {
                s2 = Strings.toLowerCase(((ASN1String)decodeObject).getString());
            }
        }
        if (s2.length() > 1) {
            int n;
            for (n = 0; n + 1 < s2.length() && s2.charAt(n) == '\\' && s2.charAt(n + 1) == ' '; n += 2) {}
            int n2;
            for (n2 = s2.length() - 1; n2 - 1 > 0 && s2.charAt(n2 - 1) == '\\' && s2.charAt(n2) == ' '; n2 -= 2) {}
            if (n > 0 || n2 < s2.length() - 1) {
                s2 = s2.substring(n, n2 + 1);
            }
        }
        return stripInternalSpaces(s2);
    }
    
    private static ASN1Primitive decodeObject(final String s) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decode(s.substring(1)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unknown encoding in name: " + ex);
        }
    }
    
    public static String stripInternalSpaces(final String s) {
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
    
    public static boolean rDNAreEqual(final RDN rdn, final RDN rdn2) {
        if (!rdn.isMultiValued()) {
            return !rdn2.isMultiValued() && atvAreEqual(rdn.getFirst(), rdn2.getFirst());
        }
        if (!rdn2.isMultiValued()) {
            return false;
        }
        final AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
        final AttributeTypeAndValue[] typesAndValues2 = rdn2.getTypesAndValues();
        if (typesAndValues.length != typesAndValues2.length) {
            return false;
        }
        for (int i = 0; i != typesAndValues.length; ++i) {
            if (!atvAreEqual(typesAndValues[i], typesAndValues2[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean atvAreEqual(final AttributeTypeAndValue attributeTypeAndValue, final AttributeTypeAndValue attributeTypeAndValue2) {
        return attributeTypeAndValue == attributeTypeAndValue2 || (attributeTypeAndValue != null && attributeTypeAndValue2 != null && attributeTypeAndValue.getType().equals(attributeTypeAndValue2.getType()) && canonicalize(valueToString(attributeTypeAndValue.getValue())).equals(canonicalize(valueToString(attributeTypeAndValue2.getValue()))));
    }
}
