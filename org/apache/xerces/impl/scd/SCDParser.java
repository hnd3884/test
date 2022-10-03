package org.apache.xerces.impl.scd;

import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.XML11Char;
import java.util.ArrayList;
import org.apache.xerces.xni.QName;
import java.util.List;

class SCDParser
{
    private List steps;
    private static final int CHARTYPE_AT = 1;
    private static final int CHARTYPE_TILDE = 2;
    private static final int CHARTYPE_PERIOD = 3;
    private static final int CHARTYPE_STAR = 4;
    private static final int CHARTYPE_ZERO = 5;
    private static final int CHARTYPE_1_THROUGH_9 = 6;
    private static final int CHARTYPE_NC_NAMESTART = 7;
    private static final int CHARTYPE_NC_NAME = 8;
    private static final int CHARTYPE_OPEN_BRACKET = 9;
    private static final int CHARTYPE_CLOSE_BRACKET = 10;
    private static final int CHARTYPE_OPEN_PAREN = 11;
    private static final int CHARTYPE_CLOSE_PAREN = 12;
    private static final int CHARTYPE_COLON = 13;
    private static final int CHARTYPE_SLASH = 14;
    private static final int CHARTYPE_NOMORE = 0;
    private static final short LIST_SIZE = 15;
    public static final QName WILDCARD;
    public static final QName ZERO;
    
    public SCDParser() {
        this.steps = new ArrayList(15);
    }
    
    private static int getCharType(final int n) throws SCDException {
        switch (n) {
            case 64: {
                return 1;
            }
            case 126: {
                return 2;
            }
            case 46: {
                return 3;
            }
            case 42: {
                return 4;
            }
            case 58: {
                return 13;
            }
            case 47: {
                return 14;
            }
            case 40: {
                return 11;
            }
            case 41: {
                return 12;
            }
            case 91: {
                return 9;
            }
            case 93: {
                return 10;
            }
            case 48: {
                return 5;
            }
            default: {
                if (n == 0) {
                    return 0;
                }
                if (n >= 49 && n <= 57) {
                    return 6;
                }
                if (XML11Char.isXML11NCNameStart(n)) {
                    return 7;
                }
                if (XML11Char.isXML11NCName(n)) {
                    return 8;
                }
                throw new SCDException("Error in SCP: Unsupported character " + (char)n + " (" + n + ")");
            }
        }
    }
    
    public static char charAt(final String s, final int n) {
        if (n >= s.length()) {
            return '\uffff';
        }
        return s.charAt(n);
    }
    
    private static QName readQName(final String s, final int[] array, final int n, final NamespaceContext namespaceContext) throws SCDException {
        return readNameTest(s, array, n, namespaceContext);
    }
    
    private static QName readNameTest(final String s, final int[] array, int n, final NamespaceContext namespaceContext) throws SCDException {
        final int n2 = n;
        final int n3 = n;
        String substring = null;
        if (charAt(s, n) == '*') {
            array[0] = n + 1;
            return SCDParser.WILDCARD;
        }
        if (charAt(s, n) == '0') {
            array[0] = n + 1;
            return SCDParser.ZERO;
        }
        if (!XML11Char.isXML11NCNameStart(charAt(s, n))) {
            throw new SCDException("Error in SCP: Invalid nametest starting character '" + charAt(s, n) + "'");
        }
        while (XML11Char.isXML11NCName(charAt(s, ++n))) {}
        String substring2 = s.substring(n2, n);
        if (charAt(s, n) == ':') {
            if (XML11Char.isXML11NCNameStart(charAt(s, ++n))) {
                final int n4 = n;
                while (XML11Char.isXML11NCName(charAt(s, n++))) {}
                substring = s.substring(n4, n - 1);
            }
            if (substring == null) {
                substring = substring2;
                substring2 = "";
            }
            array[0] = n - 1;
        }
        else {
            array[0] = n;
            substring = substring2;
            substring2 = "";
        }
        final String substring3 = s.substring(n3, array[0]);
        if (namespaceContext == null) {
            throw new SCDException("Error in SCP: Namespace context is null");
        }
        final String uri = namespaceContext.getURI(substring2.intern());
        if ("".equals(substring2)) {
            return new QName(substring2, substring, substring3, uri);
        }
        if (uri != null) {
            return new QName(substring2, substring, substring3, uri);
        }
        throw new SCDException("Error in SCP: The prefix \"" + substring2 + "\" is undeclared in this context");
    }
    
    private static int scanNCName(final String s, int n) {
        if (XML11Char.isXML11NCNameStart(charAt(s, n))) {
            while (XML11Char.isXML11NCName(charAt(s, ++n))) {}
        }
        return n;
    }
    
    private static int scanXmlnsSchemeData(final String s, int i) throws SCDException {
        int n = 0;
        do {
            final char char1 = charAt(s, i);
            if (char1 < '\0' || char1 > 1114111) {
                throw new SCDException("Error in SCD: the character '" + (int)char1 + "' at position " + ++i + " is invalid for xmlns scheme data");
            }
            if (char1 != '^') {
                ++i;
                if (char1 == '(') {
                    ++n;
                }
                else {
                    if (char1 != ')') {
                        continue;
                    }
                    if (--n == -1) {
                        return i - 1;
                    }
                    if (charAt(s, i - 2) == '(') {
                        throw new SCDException("Error in SCD: empty xmlns scheme data between '(' and ')'");
                    }
                    continue;
                }
            }
            else {
                if (charAt(s, i + 1) != '(' && charAt(s, i + 1) != ')' && charAt(s, i + 1) != '^') {
                    throw new SCDException("Error in SCD: '^' character is used as a non escape character at position " + ++i);
                }
                i += 2;
            }
        } while (i < s.length());
        String s2 = "";
        if (n != -1) {
            s2 = "Unbalanced parentheses exist within xmlns scheme data section";
        }
        throw new SCDException("Error in SCD: Attempt to read an invalid xmlns Scheme data. " + s2);
    }
    
    private static int skipWhiteSpaces(final String s, int n) {
        while (XML11Char.isXML11Space(charAt(s, n))) {
            ++n;
        }
        return n;
    }
    
    private static int readPredicate(final String s, final int[] array, int n) throws SCDException {
        final int index = s.indexOf(93, n);
        if (index >= 0) {
            try {
                final int int1 = Integer.parseInt(s.substring(n, index));
                if (int1 > 0) {
                    array[0] = index + 1;
                    return int1;
                }
                throw new SCDException("Error in SCP: Invalid predicate value " + int1);
            }
            catch (final NumberFormatException ex) {
                ex.printStackTrace();
                throw new SCDException("Error in SCP: A NumberFormatException occurred while reading the predicate");
            }
        }
        throw new SCDException("Error in SCP: Attempt to read an invalid predicate starting from position " + ++n);
    }
    
    public List parseSCP(String s, final NamespaceContext namespaceContext, final boolean b) throws SCDException {
        this.steps.clear();
        if (s.length() == 1 && s.charAt(0) == '/') {
            this.steps.add(new Step((short)100, null, 0));
            return this.steps;
        }
        if (b) {
            if ("./".equals(s.substring(0, 2))) {
                s = s.substring(1);
            }
            else {
                if (s.charAt(0) == '/') {
                    throw new SCDException("Error in incomplete SCP: Invalid starting character");
                }
                s = '/' + s;
            }
        }
        final int[] array = { 0 };
        while (array[0] < s.length()) {
            if (charAt(s, array[0]) != '/') {
                throw new SCDException("Error in SCP: Invalid character '" + charAt(s, array[0]) + " ' at position" + array[0]);
            }
            int n;
            if (charAt(s, array[0] + 1) == '/') {
                if (array[0] + 1 != s.length() - 1) {
                    this.steps.add(new Step((short)27, SCDParser.WILDCARD, 0));
                    n = array[0] + 2;
                }
                else {
                    n = array[0] + 1;
                }
            }
            else if (array[0] != s.length() - 1) {
                n = array[0] + 1;
            }
            else {
                n = array[0];
            }
            this.steps.add(processStep(s, array, n, namespaceContext));
        }
        return this.steps;
    }
    
    private static Step processStep(final String s, final int[] array, final int n, final NamespaceContext namespaceContext) throws SCDException {
        int predicate = 0;
        short qnameToAxis = 0;
        QName qName = null;
        switch (getCharType(charAt(s, n))) {
            case 1: {
                qnameToAxis = 0;
                qName = readNameTest(s, array, n + 1, namespaceContext);
                break;
            }
            case 2: {
                qnameToAxis = 2;
                qName = readNameTest(s, array, n + 1, namespaceContext);
                break;
            }
            case 3: {
                qnameToAxis = 23;
                qName = SCDParser.WILDCARD;
                array[0] = n + 1;
                break;
            }
            case 5: {
                qnameToAxis = 1;
                qName = SCDParser.ZERO;
                array[0] = n + 1;
                break;
            }
            case 4: {
                qnameToAxis = 1;
                qName = SCDParser.WILDCARD;
                array[0] = n + 1;
                break;
            }
            case 7: {
                final QName qName2 = readQName(s, array, n, namespaceContext);
                final int n2 = array[0];
                if (array[0] == s.length()) {
                    qnameToAxis = 1;
                    qName = qName2;
                    break;
                }
                if (charAt(s, n2) == ':' && charAt(s, n2 + 1) == ':') {
                    qnameToAxis = Axis.qnameToAxis(qName2.rawname);
                    if (qnameToAxis == 26) {
                        throw new SCDException("Error in SCP: Extension axis {" + qName2.rawname + "} not supported!");
                    }
                    qName = readNameTest(s, array, n2 + 2, namespaceContext);
                    break;
                }
                else {
                    if (charAt(s, n2) == '(') {
                        throw new SCDException("Error in SCP: Extension accessor not supported!");
                    }
                    if (charAt(s, n2) == '/') {
                        return new Step((short)1, qName2, predicate);
                    }
                    qnameToAxis = 1;
                    qName = qName2;
                    break;
                }
                break;
            }
            default: {
                throw new SCDException("Error in SCP: Invalid character '" + charAt(s, n) + "' at position " + n);
            }
        }
        if (array[0] < s.length()) {
            if (charAt(s, array[0]) == '[') {
                predicate = readPredicate(s, array, array[0] + 1);
            }
            else {
                if (charAt(s, array[0]) == '/') {
                    return new Step(qnameToAxis, qName, predicate);
                }
                throw new SCDException("Error in SCP: Unexpected character '" + charAt(s, array[0]) + "' at position " + array[0]);
            }
        }
        if (charAt(s, array[0]) == '/') {
            return new Step(qnameToAxis, qName, predicate);
        }
        if (array[0] < s.length()) {
            throw new SCDException("Error in SCP: Unexpected character '" + s.charAt(array[0]) + "' at the end");
        }
        return new Step(qnameToAxis, qName, predicate);
    }
    
    public List parseRelativeSCD(final String s, final boolean b) throws SCDException {
        final int[] array = { 0 };
        final NamespaceSupport namespaceSupport = new NamespaceSupport();
        while (array[0] < s.length()) {
            if ("xmlns".equals(s.substring(array[0], array[0] + 5))) {
                array[0] = readxmlns(s, namespaceSupport, array[0] + 5);
            }
            else {
                if (!"xscd".equals(s.substring(array[0], array[0] + 4))) {
                    throw new SCDException("Error in SCD: Expected 'xmlns' or 'xscd' at position " + ++array[0]);
                }
                final String substring = s.substring(array[0] + 4, s.length());
                if (charAt(substring, 0) == '(' && charAt(substring, substring.length() - 1) == ')') {
                    return this.parseSCP(substring.substring(1, substring.length() - 1), namespaceSupport, b);
                }
                throw new SCDException("Error in SCD: xscd() part is invalid at position " + ++array[0]);
            }
        }
        throw new SCDException("Error in SCD: Error at position " + ++array[0]);
    }
    
    private static int readxmlns(final String s, final NamespaceContext namespaceContext, int n) throws SCDException {
        if (charAt(s, n++) != '(') {
            throw new SCDException("Error in SCD: Invalid xmlns pointer part at position " + ++n);
        }
        final int n2 = n;
        n = scanNCName(s, n);
        if (n == n2) {
            throw new SCDException("Error in SCD: Missing namespace name at position " + ++n);
        }
        final String substring = s.substring(n2, n);
        n = skipWhiteSpaces(s, n);
        if (charAt(s, n) != '=') {
            throw new SCDException("Error in SCD: Expected a  '=' character at position " + ++n);
        }
        final int skipWhiteSpaces;
        n = (skipWhiteSpaces = skipWhiteSpaces(s, ++n));
        n = scanXmlnsSchemeData(s, n);
        if (n == skipWhiteSpaces) {
            throw new SCDException("Error in SCD: Missing namespace value at position " + ++n);
        }
        final String substring2 = s.substring(skipWhiteSpaces, n);
        if (charAt(s, n) == ')') {
            namespaceContext.declarePrefix(substring.intern(), substring2.intern());
            return ++n;
        }
        throw new SCDException("Error in SCD: Invalid xmlns pointer part at position " + ++n);
    }
    
    static {
        WILDCARD = new QName(null, "*", "*", null);
        ZERO = new QName(null, "0", "0", null);
    }
}
