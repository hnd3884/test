package com.sun.org.apache.xml.internal.security.utils;

import java.io.StringReader;
import java.io.IOException;

public class RFC2253Parser
{
    public static String rfc2253toXMLdsig(final String s) {
        return rfctoXML(normalize(s, true));
    }
    
    public static String xmldsigtoRFC2253(final String s) {
        return xmltoRFC(normalize(s, false));
    }
    
    public static String normalize(final String s) {
        return normalize(s, true);
    }
    
    public static String normalize(final String s, final boolean b) {
        if (s == null || s.equals("")) {
            return "";
        }
        try {
            final String semicolonToComma = semicolonToComma(s);
            final StringBuilder sb = new StringBuilder();
            int n = 0;
            int n2 = 0;
            int index;
            for (int n3 = 0; (index = semicolonToComma.indexOf(44, n3)) >= 0; n3 = index + 1) {
                n2 += countQuotes(semicolonToComma, n3, index);
                if (index > 0 && semicolonToComma.charAt(index - 1) != '\\' && n2 % 2 == 0) {
                    sb.append(parseRDN(semicolonToComma.substring(n, index).trim(), b)).append(",");
                    n = index + 1;
                    n2 = 0;
                }
            }
            sb.append(parseRDN(trim(semicolonToComma.substring(n)), b));
            return sb.toString();
        }
        catch (final IOException ex) {
            return s;
        }
    }
    
    static String parseRDN(final String s, final boolean b) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int n = 0;
        int n2 = 0;
        int index;
        for (int n3 = 0; (index = s.indexOf(43, n3)) >= 0; n3 = index + 1) {
            n2 += countQuotes(s, n3, index);
            if (index > 0 && s.charAt(index - 1) != '\\' && n2 % 2 == 0) {
                sb.append(parseATAV(trim(s.substring(n, index)), b)).append("+");
                n = index + 1;
                n2 = 0;
            }
        }
        sb.append(parseATAV(trim(s.substring(n)), b));
        return sb.toString();
    }
    
    static String parseATAV(final String s, final boolean b) throws IOException {
        final int index = s.indexOf(61);
        if (index == -1 || (index > 0 && s.charAt(index - 1) == '\\')) {
            return s;
        }
        final String normalizeAT = normalizeAT(s.substring(0, index));
        String s2;
        if (normalizeAT.charAt(0) >= '0' && normalizeAT.charAt(0) <= '9') {
            s2 = s.substring(index + 1);
        }
        else {
            s2 = normalizeV(s.substring(index + 1), b);
        }
        return normalizeAT + "=" + s2;
    }
    
    static String normalizeAT(final String s) {
        String s2 = s.toUpperCase().trim();
        if (s2.startsWith("OID")) {
            s2 = s2.substring(3);
        }
        return s2;
    }
    
    static String normalizeV(final String s, final boolean b) throws IOException {
        String s2 = trim(s);
        if (s2.startsWith("\"")) {
            final StringBuilder sb = new StringBuilder();
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
        if (b) {
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
        final StringBuilder sb = new StringBuilder();
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
        final StringBuilder sb = new StringBuilder();
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
        final StringBuilder sb = new StringBuilder();
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
        final StringBuilder sb = new StringBuilder();
        int n = 0;
        int index;
        for (int n2 = 0; (index = s.indexOf("\\20", n2)) >= 0; n2 = index + 3) {
            sb.append(trim(s.substring(n, index))).append("\\ ");
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
        final StringBuilder sb = new StringBuilder();
        int n = 0;
        int n2 = 0;
        int index;
        for (int n3 = 0; (index = s.indexOf(s2, n3)) >= 0; n3 = index + 1) {
            n2 += countQuotes(s, n3, index);
            if (index > 0 && s.charAt(index - 1) != '\\' && n2 % 2 == 0) {
                sb.append(trim(s.substring(n, index))).append(s3);
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
}
