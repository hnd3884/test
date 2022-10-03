package org.apache.xml.security.utils;

import java.io.StringReader;
import java.io.IOException;

public class RFC2253Parser
{
    static boolean _TOXML;
    static int counter;
    
    public static String rfc2253toXMLdsig(final String s) {
        RFC2253Parser._TOXML = true;
        return rfctoXML(normalize(s));
    }
    
    public static String xmldsigtoRFC2253(final String s) {
        RFC2253Parser._TOXML = false;
        return xmltoRFC(normalize(s));
    }
    
    public static String normalize(final String s) {
        if (s == null || s.equals("")) {
            return "";
        }
        try {
            final String semicolonToComma = semicolonToComma(s);
            final StringBuffer sb = new StringBuffer();
            int n = 0;
            int n2 = 0;
            int index;
            for (int n3 = 0; (index = semicolonToComma.indexOf(",", n3)) >= 0; n3 = index + 1) {
                n2 += countQuotes(semicolonToComma, n3, index);
                if (index > 0 && semicolonToComma.charAt(index - 1) != '\\' && n2 % 2 != 1) {
                    sb.append(parseRDN(semicolonToComma.substring(n, index).trim()) + ",");
                    n = index + 1;
                    n2 = 0;
                }
            }
            sb.append(parseRDN(trim(semicolonToComma.substring(n))));
            return sb.toString();
        }
        catch (final IOException ex) {
            return s;
        }
    }
    
    static String parseRDN(final String s) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        int n2 = 0;
        int index;
        for (int n3 = 0; (index = s.indexOf("+", n3)) >= 0; n3 = index + 1) {
            n2 += countQuotes(s, n3, index);
            if (index > 0 && s.charAt(index - 1) != '\\' && n2 % 2 != 1) {
                sb.append(parseATAV(trim(s.substring(n, index))) + "+");
                n = index + 1;
                n2 = 0;
            }
        }
        sb.append(parseATAV(trim(s.substring(n))));
        return sb.toString();
    }
    
    static String parseATAV(final String s) throws IOException {
        final int index = s.indexOf("=");
        if (index == -1 || (index > 0 && s.charAt(index - 1) == '\\')) {
            return s;
        }
        return normalizeAT(s.substring(0, index)) + "=" + normalizeV(s.substring(index + 1));
    }
    
    static String normalizeAT(final String s) {
        String s2 = s.toUpperCase().trim();
        if (s2.startsWith("OID")) {
            s2 = s2.substring(3);
        }
        return s2;
    }
    
    static String normalizeV(final String s) throws IOException {
        String s2 = trim(s);
        if (s2.startsWith("\"")) {
            final StringBuffer sb = new StringBuffer();
            int read;
            while ((read = new StringReader(s2.substring(1, s2.length() - 1)).read()) > -1) {
                final char c = (char)read;
                if (c == ',' || c == '=' || c == '+' || c == '<' || c == '>' || c == '#' || c == ';') {
                    sb.append('\\');
                }
                sb.append(c);
            }
            s2 = trim(sb.toString());
        }
        if (RFC2253Parser._TOXML) {
            if (s2.startsWith("#")) {
                s2 = '\\' + s2;
            }
        }
        else if (s2.startsWith("\\#")) {
            s2 = s2.substring(1);
        }
        return s2;
    }
    
    static String rfctoXML(final String s) {
        try {
            return changeWStoXML(changeLess32toXML(s));
        }
        catch (final Exception ex) {
            return s;
        }
    }
    
    static String xmltoRFC(final String s) {
        try {
            return changeWStoRFC(changeLess32toRFC(s));
        }
        catch (final Exception ex) {
            return s;
        }
    }
    
    static String changeLess32toRFC(final String s) throws IOException {
        final StringBuffer sb = new StringBuffer();
        final StringReader stringReader = new StringReader(s);
        int read;
        while ((read = stringReader.read()) > -1) {
            final char c = (char)read;
            if (c == '\\') {
                sb.append(c);
                final char c2 = (char)stringReader.read();
                final char c3 = (char)stringReader.read();
                if (((c2 >= '0' && c2 <= '9') || (c2 >= 'A' && c2 <= 'F') || (c2 >= 'a' && c2 <= 'f')) && ((c3 >= '0' && c3 <= '9') || (c3 >= 'A' && c3 <= 'F') || (c3 >= 'a' && c3 <= 'f'))) {
                    sb.append((char)Byte.parseByte("" + c2 + c3, 16));
                }
                else {
                    sb.append(c2);
                    sb.append(c3);
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    static String changeLess32toXML(final String s) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read;
        while ((read = new StringReader(s).read()) > -1) {
            if (read < 32) {
                sb.append('\\');
                sb.append(Integer.toHexString(read));
            }
            else {
                sb.append((char)read);
            }
        }
        return sb.toString();
    }
    
    static String changeWStoXML(final String s) throws IOException {
        final StringBuffer sb = new StringBuffer();
        final StringReader stringReader = new StringReader(s);
        int read;
        while ((read = stringReader.read()) > -1) {
            final char c = (char)read;
            if (c == '\\') {
                final char c2 = (char)stringReader.read();
                if (c2 == ' ') {
                    sb.append('\\');
                    sb.append("20");
                }
                else {
                    sb.append('\\');
                    sb.append(c2);
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    static String changeWStoRFC(final String s) {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        int index;
        for (int n2 = 0; (index = s.indexOf("\\20", n2)) >= 0; n2 = index + 3) {
            sb.append(trim(s.substring(n, index)) + "\\ ");
            n = index + 3;
        }
        sb.append(s.substring(n));
        return sb.toString();
    }
    
    static String semicolonToComma(final String s) {
        return removeWSandReplace(s, ";", ",");
    }
    
    static String removeWhiteSpace(final String s, final String s2) {
        return removeWSandReplace(s, s2, s2);
    }
    
    static String removeWSandReplace(final String s, final String s2, final String s3) {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        int n2 = 0;
        int index;
        for (int n3 = 0; (index = s.indexOf(s2, n3)) >= 0; n3 = index + 1) {
            n2 += countQuotes(s, n3, index);
            if (index > 0 && s.charAt(index - 1) != '\\' && n2 % 2 != 1) {
                sb.append(trim(s.substring(n, index)) + s3);
                n = index + 1;
                n2 = 0;
            }
        }
        sb.append(trim(s.substring(n)));
        return sb.toString();
    }
    
    private static int countQuotes(final String s, final int n, final int n2) {
        int n3 = 0;
        for (int i = n; i < n2; ++i) {
            if (s.charAt(i) == '\"') {
                ++n3;
            }
        }
        return n3;
    }
    
    static String trim(final String s) {
        String s2 = s.trim();
        final int n = s.indexOf(s2) + s2.length();
        if (s.length() > n && s2.endsWith("\\") && !s2.endsWith("\\\\") && s.charAt(n) == ' ') {
            s2 += " ";
        }
        return s2;
    }
    
    public static void main(final String[] array) throws Exception {
        testToXML("CN=\"Steve, Kille\",  O=Isode Limited, C=GB");
        testToXML("CN=Steve Kille    ,   O=Isode Limited,C=GB");
        testToXML("\\ OU=Sales+CN=J. Smith,O=Widget Inc.,C=US\\ \\ ");
        testToXML("CN=L. Eagle,O=Sue\\, Grabbit and Runn,C=GB");
        testToXML("CN=Before\\0DAfter,O=Test,C=GB");
        testToXML("CN=\"L. Eagle,O=Sue, = + < > # ;Grabbit and Runn\",C=GB");
        testToXML("1.3.6.1.4.1.1466.0=#04024869,O=Test,C=GB");
        final StringBuffer sb = new StringBuffer();
        sb.append('L');
        sb.append('u');
        sb.append('\uc48d');
        sb.append('i');
        sb.append('\uc487');
        testToXML("SN=" + sb.toString());
        testToRFC("CN=\"Steve, Kille\",  O=Isode Limited, C=GB");
        testToRFC("CN=Steve Kille    ,   O=Isode Limited,C=GB");
        testToRFC("\\20OU=Sales+CN=J. Smith,O=Widget Inc.,C=US\\20\\20 ");
        testToRFC("CN=L. Eagle,O=Sue\\, Grabbit and Runn,C=GB");
        testToRFC("CN=Before\\12After,O=Test,C=GB");
        testToRFC("CN=\"L. Eagle,O=Sue, = + < > # ;Grabbit and Runn\",C=GB");
        testToRFC("1.3.6.1.4.1.1466.0=\\#04024869,O=Test,C=GB");
        final StringBuffer sb2 = new StringBuffer();
        sb2.append('L');
        sb2.append('u');
        sb2.append('\uc48d');
        sb2.append('i');
        sb2.append('\uc487');
        testToRFC("SN=" + sb2.toString());
    }
    
    static void testToXML(final String s) {
        System.out.println("start " + RFC2253Parser.counter++ + ": " + s);
        System.out.println("         " + rfc2253toXMLdsig(s));
        System.out.println("");
    }
    
    static void testToRFC(final String s) {
        System.out.println("start " + RFC2253Parser.counter++ + ": " + s);
        System.out.println("         " + xmldsigtoRFC2253(s));
        System.out.println("");
    }
    
    static {
        RFC2253Parser._TOXML = true;
        RFC2253Parser.counter = 0;
    }
}
