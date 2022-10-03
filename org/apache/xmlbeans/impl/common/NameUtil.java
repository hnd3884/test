package org.apache.xmlbeans.impl.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.Set;

public class NameUtil
{
    public static final char HYPHEN = '-';
    public static final char PERIOD = '.';
    public static final char COLON = ':';
    public static final char USCORE = '_';
    public static final char DOT = '·';
    public static final char TELEIA = '\u0387';
    public static final char AYAH = '\u06dd';
    public static final char ELHIZB = '\u06de';
    private static final boolean DEBUG = false;
    private static final Set javaWords;
    private static final Set extraWords;
    private static final Set javaNames;
    private static final String JAVA_NS_PREFIX = "java:";
    private static final String LANG_PREFIX = "java.";
    private static final int START = 0;
    private static final int PUNCT = 1;
    private static final int DIGIT = 2;
    private static final int MARK = 3;
    private static final int UPPER = 4;
    private static final int LOWER = 5;
    private static final int NOCASE = 6;
    
    public static boolean isValidJavaIdentifier(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        final int len = id.length();
        if (len == 0) {
            return false;
        }
        if (NameUtil.javaWords.contains(id)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(id.charAt(0))) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            if (!Character.isJavaIdentifierPart(id.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String getClassNameFromQName(final QName qname) {
        return getClassNameFromQName(qname, false);
    }
    
    public static String getClassNameFromQName(final QName qname, final boolean useJaxRpcRules) {
        final String java_type = upperCamelCase(qname.getLocalPart(), useJaxRpcRules);
        final String uri = qname.getNamespaceURI();
        String java_pkg = null;
        java_pkg = getPackageFromNamespace(uri, useJaxRpcRules);
        if (java_pkg != null) {
            return java_pkg + "." + java_type;
        }
        return java_type;
    }
    
    public static String getNamespaceFromPackage(final Class clazz) {
        for (Class curr_clazz = clazz; curr_clazz.isArray(); curr_clazz = curr_clazz.getComponentType()) {}
        final String fullname = clazz.getName();
        final int lastdot = fullname.lastIndexOf(46);
        final String pkg_name = (lastdot < 0) ? "" : fullname.substring(0, lastdot);
        return "java:" + pkg_name;
    }
    
    private static boolean isUriSchemeChar(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '-' || ch == '.' || ch == '+';
    }
    
    private static boolean isUriAlphaChar(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }
    
    private static int findSchemeColon(final String uri) {
        final int len = uri.length();
        if (len == 0) {
            return -1;
        }
        if (!isUriAlphaChar(uri.charAt(0))) {
            return -1;
        }
        int i;
        for (i = 1; i < len && isUriSchemeChar(uri.charAt(i)); ++i) {}
        if (i == len) {
            return -1;
        }
        if (uri.charAt(i) != ':') {
            return -1;
        }
        while (i < len && uri.charAt(i) == ':') {
            ++i;
        }
        return i - 1;
    }
    
    private static String jls77String(final String name) {
        final StringBuffer buf = new StringBuffer(name);
        for (int i = 0; i < name.length(); ++i) {
            if (!Character.isJavaIdentifierPart(buf.charAt(i)) || '$' == buf.charAt(i)) {
                buf.setCharAt(i, '_');
            }
        }
        if (buf.length() == 0 || !Character.isJavaIdentifierStart(buf.charAt(0))) {
            buf.insert(0, '_');
        }
        if (isJavaReservedWord(name)) {
            buf.append('_');
        }
        return buf.toString();
    }
    
    private static List splitDNS(final String dns) {
        final List result = new ArrayList();
        int end = dns.length();
        for (int begin = dns.lastIndexOf(46); begin != -1; --begin) {
            if (dns.charAt(begin) == '.') {
                result.add(jls77String(dns.substring(begin + 1, end)));
                end = begin;
            }
        }
        result.add(jls77String(dns.substring(0, end)));
        if (result.size() >= 3 && result.get(result.size() - 1).toLowerCase().equals("www")) {
            result.remove(result.size() - 1);
        }
        return result;
    }
    
    private static String processFilename(final String filename) {
        final int i = filename.lastIndexOf(46);
        if (i > 0 && (i + 1 + 2 == filename.length() || i + 1 + 3 == filename.length() || filename.substring(i + 1).toLowerCase() == "html")) {
            return filename.substring(0, i);
        }
        return filename;
    }
    
    public static String getPackageFromNamespace(final String uri) {
        return getPackageFromNamespace(uri, false);
    }
    
    public static String getPackageFromNamespace(final String uri, final boolean useJaxRpcRules) {
        if (uri == null || uri.length() == 0) {
            return "noNamespace";
        }
        final int len = uri.length();
        int i = findSchemeColon(uri);
        List result = null;
        if (i == len - 1) {
            result = new ArrayList();
            result.add(uri.substring(0, i));
        }
        else if (i >= 0 && uri.substring(0, i).equals("java")) {
            result = Arrays.asList(uri.substring(i + 1).split("\\."));
        }
        else {
            result = new ArrayList();
            ++i;
        Label_0183:
            while (i < len) {
                while (uri.charAt(i) == '/') {
                    if (++i >= len) {
                        break Label_0183;
                    }
                }
                final int start = i;
                while (uri.charAt(i) != '/' && ++i < len) {}
                final int end = i;
                result.add(uri.substring(start, end));
            }
            if (result.size() > 1) {
                result.set(result.size() - 1, processFilename(result.get(result.size() - 1)));
            }
            if (result.size() > 0) {
                final List splitdns = splitDNS(result.get(0));
                result.remove(0);
                result.addAll(0, splitdns);
            }
        }
        final StringBuffer buf = new StringBuffer();
        final Iterator it = result.iterator();
        while (it.hasNext()) {
            final String part = nonJavaKeyword(lowerCamelCase(it.next(), useJaxRpcRules, true));
            if (part.length() > 0) {
                buf.append(part);
                buf.append('.');
            }
        }
        if (buf.length() == 0) {
            return "noNamespace";
        }
        if (useJaxRpcRules) {
            return buf.substring(0, buf.length() - 1).toLowerCase();
        }
        return buf.substring(0, buf.length() - 1);
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            System.out.println(upperCaseUnderbar(args[i]));
        }
    }
    
    public static String upperCaseUnderbar(final String xml_name) {
        final StringBuffer buf = new StringBuffer();
        final List words = splitWords(xml_name, false);
        final int sz = words.size() - 1;
        if (sz >= 0 && !Character.isJavaIdentifierStart(words.get(0).charAt(0))) {
            buf.append("X_");
        }
        for (int i = 0; i < sz; ++i) {
            buf.append(words.get(i));
            buf.append('_');
        }
        if (sz >= 0) {
            buf.append(words.get(sz));
        }
        for (int len = buf.length(), j = 0; j < len; ++j) {
            final char c = buf.charAt(j);
            buf.setCharAt(j, Character.toUpperCase(c));
        }
        return buf.toString();
    }
    
    public static String upperCamelCase(final String xml_name) {
        return upperCamelCase(xml_name, false);
    }
    
    public static String upperCamelCase(final String xml_name, final boolean useJaxRpcRules) {
        final StringBuffer buf = new StringBuffer();
        final List words = splitWords(xml_name, useJaxRpcRules);
        if (words.size() > 0) {
            if (!Character.isJavaIdentifierStart(words.get(0).charAt(0))) {
                buf.append("X");
            }
            final Iterator itr = words.iterator();
            while (itr.hasNext()) {
                buf.append(itr.next());
            }
        }
        return buf.toString();
    }
    
    public static String lowerCamelCase(final String xml_name) {
        return lowerCamelCase(xml_name, false, true);
    }
    
    public static String lowerCamelCase(final String xml_name, final boolean useJaxRpcRules, final boolean fixGeneratedName) {
        final StringBuffer buf = new StringBuffer();
        final List words = splitWords(xml_name, useJaxRpcRules);
        if (words.size() > 0) {
            final String first = words.get(0).toLowerCase();
            final char f = first.charAt(0);
            if (!Character.isJavaIdentifierStart(f) && fixGeneratedName) {
                buf.append("x");
            }
            buf.append(first);
            final Iterator itr = words.iterator();
            itr.next();
            while (itr.hasNext()) {
                buf.append(itr.next());
            }
        }
        return buf.toString();
    }
    
    public static String upperCaseFirstLetter(final String s) {
        if (s.length() == 0 || Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        final StringBuffer buf = new StringBuffer(s);
        buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
        return buf.toString();
    }
    
    private static void addCapped(final List list, final String str) {
        if (str.length() > 0) {
            list.add(upperCaseFirstLetter(str));
        }
    }
    
    public static List splitWords(final String name, final boolean useJaxRpcRules) {
        final List list = new ArrayList();
        final int len = name.length();
        int start = 0;
        int prefix = 0;
        for (int i = 0; i < len; ++i) {
            int current = getCharClass(name.charAt(i), useJaxRpcRules);
            if (prefix != 1 && current == 1) {
                addCapped(list, name.substring(start, i));
                while ((current = getCharClass(name.charAt(i), useJaxRpcRules)) == 1) {
                    if (++i >= len) {
                        return list;
                    }
                }
                start = i;
            }
            else if (prefix == 2 != (current == 2) || (prefix == 5 && current != 5) || isLetter(prefix) != isLetter(current)) {
                addCapped(list, name.substring(start, i));
                start = i;
            }
            else if (prefix == 4 && current == 5 && i > start + 1) {
                addCapped(list, name.substring(start, i - 1));
                start = i - 1;
            }
            prefix = current;
        }
        addCapped(list, name.substring(start));
        return list;
    }
    
    public static int getCharClass(final char c, final boolean useJaxRpcRules) {
        if (isPunctuation(c, useJaxRpcRules)) {
            return 1;
        }
        if (Character.isDigit(c)) {
            return 2;
        }
        if (Character.isUpperCase(c)) {
            return 4;
        }
        if (Character.isLowerCase(c)) {
            return 5;
        }
        if (Character.isLetter(c)) {
            return 6;
        }
        if (Character.isJavaIdentifierPart(c)) {
            return 3;
        }
        return 1;
    }
    
    private static boolean isLetter(final int state) {
        return state == 4 || state == 5 || state == 6;
    }
    
    public static boolean isPunctuation(final char c, final boolean useJaxRpcRules) {
        return c == '-' || c == '.' || c == ':' || c == '·' || (c == '_' && !useJaxRpcRules) || c == '\u0387' || c == '\u06dd' || c == '\u06de';
    }
    
    public static String nonJavaKeyword(final String word) {
        if (isJavaReservedWord(word)) {
            return 'x' + word;
        }
        return word;
    }
    
    public static String nonExtraKeyword(final String word) {
        if (isExtraReservedWord(word, true)) {
            return word + "Value";
        }
        return word;
    }
    
    public static String nonJavaCommonClassName(final String name) {
        if (isJavaCommonClassName(name)) {
            return "X" + name;
        }
        return name;
    }
    
    private static boolean isJavaReservedWord(final String word) {
        return isJavaReservedWord(word, true);
    }
    
    private static boolean isJavaReservedWord(String word, final boolean ignore_case) {
        if (ignore_case) {
            word = word.toLowerCase();
        }
        return NameUtil.javaWords.contains(word);
    }
    
    private static boolean isExtraReservedWord(String word, final boolean ignore_case) {
        if (ignore_case) {
            word = word.toLowerCase();
        }
        return NameUtil.extraWords.contains(word);
    }
    
    public static boolean isJavaCommonClassName(final String word) {
        return NameUtil.javaNames.contains(word);
    }
    
    static {
        javaWords = new HashSet(Arrays.asList("assert", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "threadsafe", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"));
        extraWords = new HashSet(Arrays.asList("i", "target", "org", "com"));
        javaNames = new HashSet(Arrays.asList("CharSequence", "Cloneable", "Comparable", "Runnable", "Boolean", "Byte", "Character", "Class", "ClassLoader", "Compiler", "Double", "Float", "InheritableThreadLocal", "Integer", "Long", "Math", "Number", "Object", "Package", "Process", "Runtime", "RuntimePermission", "SecurityManager", "Short", "StackTraceElement", "StrictMath", "String", "StringBuffer", "System", "Thread", "ThreadGroup", "ThreadLocal", "Throwable", "Void", "ArithmeticException", "ArrayIndexOutOfBoundsException", "ArrayStoreException", "ClassCastException", "ClassNotFoundException", "CloneNotSupportedException", "Exception", "IllegalAccessException", "IllegalArgumentException", "IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException", "IndexOutOfBoundsException", "InstantiationException", "InterruptedException", "NegativeArraySizeException", "NoSuchFieldException", "NoSuchMethodException", "NullPointerException", "NumberFormatException", "RuntimeException", "SecurityException", "StringIndexOutOfBoundsException", "UnsupportedOperationException", "AbstractMethodError", "AssertionError", "ClassCircularityError", "ClassFormatError", "Error", "ExceptionInInitializerError", "IllegalAccessError", "IncompatibleClassChangeError", "InstantiationError", "InternalError", "LinkageError", "NoClassDefFoundError", "NoSuchFieldError", "NoSuchMethodError", "OutOfMemoryError", "StackOverflowError", "ThreadDeath", "UnknownError", "UnsatisfiedLinkError", "UnsupportedClassVersionError", "VerifyError", "VirtualMachineError", "BigInteger", "BigDecimal", "Enum", "Date", "GDate", "GDuration", "QName", "List", "XmlObject", "XmlCursor", "XmlBeans", "SchemaType"));
    }
}
