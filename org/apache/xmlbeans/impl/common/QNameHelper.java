package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaType;
import java.util.Collections;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import org.apache.xmlbeans.xml.stream.XMLName;
import javax.xml.namespace.QName;
import java.util.Map;

public class QNameHelper
{
    private static final Map WELL_KNOWN_PREFIXES;
    private static final char[] hexdigits;
    public static final int MAX_NAME_LENGTH = 64;
    public static final String URI_SHA1_PREFIX = "URI_SHA_1_";
    
    public static XMLName getXMLName(final QName qname) {
        if (qname == null) {
            return null;
        }
        return XMLNameHelper.forLNS(qname.getLocalPart(), qname.getNamespaceURI());
    }
    
    public static QName forLNS(final String localname, String uri) {
        if (uri == null) {
            uri = "";
        }
        return new QName(uri, localname);
    }
    
    public static QName forLN(final String localname) {
        return new QName("", localname);
    }
    
    public static QName forPretty(final String pretty, final int offset) {
        final int at = pretty.indexOf(64, offset);
        if (at < 0) {
            return new QName("", pretty.substring(offset));
        }
        return new QName(pretty.substring(at + 1), pretty.substring(offset, at));
    }
    
    public static String pretty(final QName name) {
        if (name == null) {
            return "null";
        }
        if (name.getNamespaceURI() == null || name.getNamespaceURI().length() == 0) {
            return name.getLocalPart();
        }
        return name.getLocalPart() + "@" + name.getNamespaceURI();
    }
    
    private static boolean isSafe(final int c) {
        return (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || (c >= 48 && c <= 57);
    }
    
    public static String hexsafe(final String s) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (isSafe(ch)) {
                result.append(ch);
            }
            else {
                byte[] utf8 = null;
                try {
                    utf8 = s.substring(i, i + 1).getBytes("UTF-8");
                    for (int j = 0; j < utf8.length; ++j) {
                        result.append('_');
                        result.append(QNameHelper.hexdigits[utf8[j] >> 4 & 0xF]);
                        result.append(QNameHelper.hexdigits[utf8[j] & 0xF]);
                    }
                }
                catch (final UnsupportedEncodingException uee) {
                    result.append("_BAD_UTF8_CHAR");
                }
            }
        }
        if (result.length() <= 64) {
            return result.toString();
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] inputBytes = null;
            try {
                inputBytes = s.getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException uee2) {
                inputBytes = new byte[0];
            }
            final byte[] digest = md.digest(inputBytes);
            assert digest.length == 20;
            result = new StringBuffer("URI_SHA_1_");
            for (int j = 0; j < digest.length; ++j) {
                result.append(QNameHelper.hexdigits[digest[j] >> 4 & 0xF]);
                result.append(QNameHelper.hexdigits[digest[j] & 0xF]);
            }
            return result.toString();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Using in a JDK without an SHA implementation");
        }
    }
    
    public static String hexsafedir(final QName name) {
        if (name.getNamespaceURI() == null || name.getNamespaceURI().length() == 0) {
            return "_nons/" + hexsafe(name.getLocalPart());
        }
        return hexsafe(name.getNamespaceURI()) + "/" + hexsafe(name.getLocalPart());
    }
    
    private static Map buildWKP() {
        final Map result = new HashMap();
        result.put("http://www.w3.org/XML/1998/namespace", "xml");
        result.put("http://www.w3.org/2001/XMLSchema", "xs");
        result.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        result.put("http://schemas.xmlsoap.org/wsdl/", "wsdl");
        result.put("http://schemas.xmlsoap.org/soap/encoding/", "soapenc");
        result.put("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        return Collections.unmodifiableMap((Map<?, ?>)result);
    }
    
    public static String readable(final SchemaType sType) {
        return readable(sType, QNameHelper.WELL_KNOWN_PREFIXES);
    }
    
    public static String readable(final SchemaType sType, final Map nsPrefix) {
        if (sType.getName() != null) {
            return readable(sType.getName(), nsPrefix);
        }
        if (sType.isAttributeType()) {
            return "attribute type " + readable(sType.getAttributeTypeAttributeName(), nsPrefix);
        }
        if (sType.isDocumentType()) {
            return "document type " + readable(sType.getDocumentElementName(), nsPrefix);
        }
        if (sType.isNoType() || sType.getOuterType() == null) {
            return "invalid type";
        }
        final SchemaType outerType = sType.getOuterType();
        final SchemaField container = sType.getContainerField();
        if (outerType.isAttributeType()) {
            return "type of attribute " + readable(container.getName(), nsPrefix);
        }
        if (outerType.isDocumentType()) {
            return "type of element " + readable(container.getName(), nsPrefix);
        }
        if (container != null) {
            if (container.isAttribute()) {
                return "type of " + container.getName().getLocalPart() + " attribute in " + readable(outerType, nsPrefix);
            }
            return "type of " + container.getName().getLocalPart() + " element in " + readable(outerType, nsPrefix);
        }
        else {
            if (outerType.getBaseType() == sType) {
                return "base type of " + readable(outerType, nsPrefix);
            }
            if (outerType.getSimpleVariety() == 3) {
                return "item type of " + readable(outerType, nsPrefix);
            }
            if (outerType.getSimpleVariety() == 2) {
                return "member type " + sType.getAnonymousUnionMemberOrdinal() + " of " + readable(outerType, nsPrefix);
            }
            return "inner type in " + readable(outerType, nsPrefix);
        }
    }
    
    public static String readable(final QName name) {
        return readable(name, QNameHelper.WELL_KNOWN_PREFIXES);
    }
    
    public static String readable(final QName name, final Map prefixes) {
        if (name.getNamespaceURI().length() == 0) {
            return name.getLocalPart();
        }
        final String prefix = prefixes.get(name.getNamespaceURI());
        if (prefix != null) {
            return prefix + ":" + name.getLocalPart();
        }
        return name.getLocalPart() + " in namespace " + name.getNamespaceURI();
    }
    
    public static String suggestPrefix(final String namespace) {
        final String result = QNameHelper.WELL_KNOWN_PREFIXES.get(namespace);
        if (result != null) {
            return result;
        }
        int len = namespace.length();
        int i = namespace.lastIndexOf(47);
        if (i > 0 && i == namespace.length() - 1) {
            len = i;
            i = namespace.lastIndexOf(47, i - 1);
        }
        ++i;
        if (namespace.startsWith("www.", i)) {
            i += 4;
        }
        while (i < len && !XMLChar.isNCNameStart(namespace.charAt(i))) {
            ++i;
        }
        for (int end = i + 1; end < len; ++end) {
            if (!XMLChar.isNCName(namespace.charAt(end)) || !Character.isLetterOrDigit(namespace.charAt(end))) {
                len = end;
                break;
            }
        }
        if (namespace.length() >= i + 3 && startsWithXml(namespace, i)) {
            if (namespace.length() >= i + 4) {
                return "x" + Character.toLowerCase(namespace.charAt(i + 3));
            }
            return "ns";
        }
        else {
            if (len - i > 4) {
                if (isVowel(namespace.charAt(i + 2)) && !isVowel(namespace.charAt(i + 3))) {
                    len = i + 4;
                }
                else {
                    len = i + 3;
                }
            }
            if (len - i == 0) {
                return "ns";
            }
            return namespace.substring(i, len).toLowerCase();
        }
    }
    
    private static boolean startsWithXml(final String s, final int i) {
        return s.length() >= i + 3 && (s.charAt(i) == 'X' || s.charAt(i) == 'x') && (s.charAt(i + 1) == 'M' || s.charAt(i + 1) == 'm') && (s.charAt(i + 2) == 'L' || s.charAt(i + 2) == 'l');
    }
    
    private static boolean isVowel(final char ch) {
        switch (ch) {
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static String namespace(SchemaType sType) {
        while (sType != null) {
            if (sType.getName() != null) {
                return sType.getName().getNamespaceURI();
            }
            if (sType.getContainerField() != null && sType.getContainerField().getName().getNamespaceURI().length() > 0) {
                return sType.getContainerField().getName().getNamespaceURI();
            }
            sType = sType.getOuterType();
        }
        return "";
    }
    
    public static String getLocalPart(final String qname) {
        final int index = qname.indexOf(58);
        return (index < 0) ? qname : qname.substring(index + 1);
    }
    
    public static String getPrefixPart(final String qname) {
        final int index = qname.indexOf(58);
        return (index >= 0) ? qname.substring(0, index) : "";
    }
    
    static {
        WELL_KNOWN_PREFIXES = buildWKP();
        hexdigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
