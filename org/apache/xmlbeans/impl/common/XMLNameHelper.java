package org.apache.xmlbeans.impl.common;

import java.io.UnsupportedEncodingException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.xml.stream.XMLName;

public class XMLNameHelper
{
    private static final char[] hexdigits;
    
    public static QName getQName(final XMLName xmlName) {
        if (xmlName == null) {
            return null;
        }
        return QNameHelper.forLNS(xmlName.getLocalName(), xmlName.getNamespaceUri());
    }
    
    public static XMLName forLNS(final String localname, String uri) {
        if (uri == null) {
            uri = "";
        }
        return new XmlNameImpl(uri, localname);
    }
    
    public static XMLName forLN(final String localname) {
        return new XmlNameImpl("", localname);
    }
    
    public static XMLName forPretty(final String pretty, final int offset) {
        final int at = pretty.indexOf(64, offset);
        if (at < 0) {
            return new XmlNameImpl("", pretty.substring(offset));
        }
        return new XmlNameImpl(pretty.substring(at + 1), pretty.substring(offset, at));
    }
    
    public static String pretty(final XMLName name) {
        if (name == null) {
            return "null";
        }
        if (name.getNamespaceUri() == null || name.getNamespaceUri().length() == 0) {
            return name.getLocalName();
        }
        return name.getLocalName() + "@" + name.getNamespaceUri();
    }
    
    private static boolean isSafe(final int c) {
        return (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || (c >= 48 && c <= 57);
    }
    
    public static String hexsafe(final String s) {
        final StringBuffer result = new StringBuffer();
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
                        result.append(XMLNameHelper.hexdigits[utf8[j] >> 4 & 0xF]);
                        result.append(XMLNameHelper.hexdigits[utf8[j] & 0xF]);
                    }
                }
                catch (final UnsupportedEncodingException uee) {
                    result.append("_BAD_UTF8_CHAR");
                }
            }
        }
        return result.toString();
    }
    
    public static String hexsafedir(final XMLName name) {
        if (name.getNamespaceUri() == null || name.getNamespaceUri().length() == 0) {
            return "_nons/" + hexsafe(name.getLocalName());
        }
        return hexsafe(name.getNamespaceUri()) + "/" + hexsafe(name.getLocalName());
    }
    
    static {
        hexdigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
