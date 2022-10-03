package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.StringTokenizer;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class Util
{
    private static char filesep;
    
    public static String noExtName(final String name) {
        final int index = name.lastIndexOf(46);
        return name.substring(0, (index >= 0) ? index : name.length());
    }
    
    public static String baseName(final String name) {
        int index = name.lastIndexOf(92);
        if (index < 0) {
            index = name.lastIndexOf(47);
        }
        if (index >= 0) {
            return name.substring(index + 1);
        }
        final int lastColonIndex = name.lastIndexOf(58);
        if (lastColonIndex > 0) {
            return name.substring(lastColonIndex + 1);
        }
        return name;
    }
    
    public static String pathName(final String name) {
        int index = name.lastIndexOf(47);
        if (index < 0) {
            index = name.lastIndexOf(92);
        }
        return name.substring(0, index + 1);
    }
    
    public static String toJavaName(final String name) {
        if (name.length() > 0) {
            final StringBuffer result = new StringBuffer();
            char ch = name.charAt(0);
            result.append(Character.isJavaIdentifierStart(ch) ? ch : '_');
            for (int n = name.length(), i = 1; i < n; ++i) {
                ch = name.charAt(i);
                result.append(Character.isJavaIdentifierPart(ch) ? ch : '_');
            }
            return result.toString();
        }
        return name;
    }
    
    public static Type getJCRefType(final String signature) {
        return Type.getType(signature);
    }
    
    public static String internalName(final String cname) {
        return cname.replace('.', Util.filesep);
    }
    
    public static void println(final String s) {
        System.out.println(s);
    }
    
    public static void println(final char ch) {
        System.out.println(ch);
    }
    
    public static void TRACE1() {
        System.out.println("TRACE1");
    }
    
    public static void TRACE2() {
        System.out.println("TRACE2");
    }
    
    public static void TRACE3() {
        System.out.println("TRACE3");
    }
    
    public static String replace(final String base, final char ch, final String str) {
        return (base.indexOf(ch) < 0) ? base : replace(base, String.valueOf(ch), new String[] { str });
    }
    
    public static String replace(final String base, final String delim, final String[] str) {
        final int len = base.length();
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            final char ch = base.charAt(i);
            final int k = delim.indexOf(ch);
            if (k >= 0) {
                result.append(str[k]);
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }
    
    public static String escape(final String input) {
        return replace(input, ".-/:", new String[] { "$dot$", "$dash$", "$slash$", "$colon$" });
    }
    
    public static String getLocalName(final String qname) {
        final int index = qname.lastIndexOf(":");
        return (index > 0) ? qname.substring(index + 1) : qname;
    }
    
    public static String getPrefix(final String qname) {
        final int index = qname.lastIndexOf(":");
        return (index > 0) ? qname.substring(0, index) : "";
    }
    
    public static boolean isLiteral(final String str) {
        for (int length = str.length(), i = 0; i < length - 1; ++i) {
            if (str.charAt(i) == '{' && str.charAt(i + 1) != '{') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidQNames(final String str) {
        if (str != null && !str.equals("")) {
            final StringTokenizer tokens = new StringTokenizer(str);
            while (tokens.hasMoreTokens()) {
                if (!XML11Char.isXML11ValidQName(tokens.nextToken())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        final String temp = SecuritySupport.getSystemProperty("file.separator", "/");
        Util.filesep = temp.charAt(0);
    }
}
