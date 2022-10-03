package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.text.MessageFormat;
import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.Document;
import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.FieldPosition;
import java.text.DecimalFormat;

public final class BasisLibrary
{
    private static final String EMPTYSTRING = "";
    private static final ThreadLocal<StringBuilder> threadLocalStringBuilder;
    private static final ThreadLocal<StringBuffer> threadLocalStringBuffer;
    private static final int DOUBLE_FRACTION_DIGITS = 340;
    private static final double lowerBounds = 0.001;
    private static final double upperBounds = 1.0E7;
    private static DecimalFormat defaultFormatter;
    private static DecimalFormat xpathFormatter;
    private static String defaultPattern;
    private static FieldPosition _fieldPosition;
    private static char[] _characterArray;
    private static final ThreadLocal<AtomicInteger> threadLocalPrefixIndex;
    public static final String RUN_TIME_INTERNAL_ERR = "RUN_TIME_INTERNAL_ERR";
    public static final String RUN_TIME_COPY_ERR = "RUN_TIME_COPY_ERR";
    public static final String DATA_CONVERSION_ERR = "DATA_CONVERSION_ERR";
    public static final String EXTERNAL_FUNC_ERR = "EXTERNAL_FUNC_ERR";
    public static final String EQUALITY_EXPR_ERR = "EQUALITY_EXPR_ERR";
    public static final String INVALID_ARGUMENT_ERR = "INVALID_ARGUMENT_ERR";
    public static final String FORMAT_NUMBER_ERR = "FORMAT_NUMBER_ERR";
    public static final String ITERATOR_CLONE_ERR = "ITERATOR_CLONE_ERR";
    public static final String AXIS_SUPPORT_ERR = "AXIS_SUPPORT_ERR";
    public static final String TYPED_AXIS_SUPPORT_ERR = "TYPED_AXIS_SUPPORT_ERR";
    public static final String STRAY_ATTRIBUTE_ERR = "STRAY_ATTRIBUTE_ERR";
    public static final String STRAY_NAMESPACE_ERR = "STRAY_NAMESPACE_ERR";
    public static final String NAMESPACE_PREFIX_ERR = "NAMESPACE_PREFIX_ERR";
    public static final String DOM_ADAPTER_INIT_ERR = "DOM_ADAPTER_INIT_ERR";
    public static final String PARSER_DTD_SUPPORT_ERR = "PARSER_DTD_SUPPORT_ERR";
    public static final String NAMESPACES_SUPPORT_ERR = "NAMESPACES_SUPPORT_ERR";
    public static final String CANT_RESOLVE_RELATIVE_URI_ERR = "CANT_RESOLVE_RELATIVE_URI_ERR";
    public static final String UNSUPPORTED_XSL_ERR = "UNSUPPORTED_XSL_ERR";
    public static final String UNSUPPORTED_EXT_ERR = "UNSUPPORTED_EXT_ERR";
    public static final String UNKNOWN_TRANSLET_VERSION_ERR = "UNKNOWN_TRANSLET_VERSION_ERR";
    public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
    public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
    public static final String UNALLOWED_EXTENSION_FUNCTION_ERR = "UNALLOWED_EXTENSION_FUNCTION_ERR";
    public static final String UNALLOWED_EXTENSION_ELEMENT_ERR = "UNALLOWED_EXTENSION_ELEMENT_ERR";
    private static ResourceBundle m_bundle;
    public static final String ERROR_MESSAGES_KEY = "error-messages";
    
    public static int countF(final DTMAxisIterator iterator) {
        return iterator.getLast();
    }
    
    @Deprecated
    public static int positionF(final DTMAxisIterator iterator) {
        return iterator.isReverse() ? (iterator.getLast() - iterator.getPosition() + 1) : iterator.getPosition();
    }
    
    public static double sumF(final DTMAxisIterator iterator, final DOM dom) {
        try {
            double result = 0.0;
            int node;
            while ((node = iterator.next()) != -1) {
                result += Double.parseDouble(dom.getStringValueX(node));
            }
            return result;
        }
        catch (final NumberFormatException e) {
            return Double.NaN;
        }
    }
    
    public static String stringF(final int node, final DOM dom) {
        return dom.getStringValueX(node);
    }
    
    public static String stringF(final Object obj, final DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        return obj.toString();
    }
    
    public static String stringF(final Object obj, final int node, final DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        if (!(obj instanceof Double)) {
            return (obj != null) ? obj.toString() : "";
        }
        final Double d = (Double)obj;
        final String result = d.toString();
        final int length = result.length();
        if (result.charAt(length - 2) == '.' && result.charAt(length - 1) == '0') {
            return result.substring(0, length - 2);
        }
        return result;
    }
    
    public static double numberF(final int node, final DOM dom) {
        return stringToReal(dom.getStringValueX(node));
    }
    
    public static double numberF(final Object obj, final DOM dom) {
        if (obj instanceof Double) {
            return (double)obj;
        }
        if (obj instanceof Integer) {
            return (double)obj;
        }
        if (obj instanceof Boolean) {
            return obj ? 1.0 : 0.0;
        }
        if (obj instanceof String) {
            return stringToReal((String)obj);
        }
        if (obj instanceof DTMAxisIterator) {
            final DTMAxisIterator iter = (DTMAxisIterator)obj;
            return stringToReal(dom.getStringValueX(iter.reset().next()));
        }
        if (obj instanceof Node) {
            return stringToReal(dom.getStringValueX(((Node)obj).node));
        }
        if (obj instanceof DOM) {
            return stringToReal(((DOM)obj).getStringValue());
        }
        final String className = obj.getClass().getName();
        runTimeError("INVALID_ARGUMENT_ERR", className, "number()");
        return 0.0;
    }
    
    public static double roundF(final double d) {
        return (d < -0.5 || d > 0.0) ? Math.floor(d + 0.5) : ((d == 0.0) ? d : (Double.isNaN(d) ? Double.NaN : -0.0));
    }
    
    public static boolean booleanF(final Object obj) {
        if (obj instanceof Double) {
            final double temp = (double)obj;
            return temp != 0.0 && !Double.isNaN(temp);
        }
        if (obj instanceof Integer) {
            return (double)obj != 0.0;
        }
        if (obj instanceof Boolean) {
            return (boolean)obj;
        }
        if (obj instanceof String) {
            return !((String)obj).equals("");
        }
        if (obj instanceof DTMAxisIterator) {
            final DTMAxisIterator iter = (DTMAxisIterator)obj;
            return iter.reset().next() != -1;
        }
        if (obj instanceof Node) {
            return true;
        }
        if (obj instanceof DOM) {
            final String temp2 = ((DOM)obj).getStringValue();
            return !temp2.equals("");
        }
        final String className = obj.getClass().getName();
        runTimeError("INVALID_ARGUMENT_ERR", className, "boolean()");
        return false;
    }
    
    public static String substringF(final String value, final double start) {
        if (Double.isNaN(start)) {
            return "";
        }
        final int strlen = getStringLength(value);
        int istart = (int)Math.round(start) - 1;
        if (istart > strlen) {
            return "";
        }
        if (istart < 1) {
            istart = 0;
        }
        try {
            istart = value.offsetByCodePoints(0, istart);
            return value.substring(istart);
        }
        catch (final IndexOutOfBoundsException e) {
            runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
            return null;
        }
    }
    
    public static String substringF(final String value, final double start, final double length) {
        if (Double.isInfinite(start) || Double.isNaN(start) || Double.isNaN(length) || length < 0.0) {
            return "";
        }
        int istart = (int)Math.round(start) - 1;
        int ilength = (int)Math.round(length);
        int isum;
        if (Double.isInfinite(length)) {
            isum = Integer.MAX_VALUE;
        }
        else {
            isum = istart + ilength;
        }
        final int strlen = getStringLength(value);
        if (isum < 0 || istart > strlen) {
            return "";
        }
        if (istart < 0) {
            ilength += istart;
            istart = 0;
        }
        try {
            istart = value.offsetByCodePoints(0, istart);
            if (isum > strlen) {
                return value.substring(istart);
            }
            final int offset = value.offsetByCodePoints(istart, ilength);
            return value.substring(istart, offset);
        }
        catch (final IndexOutOfBoundsException e) {
            runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
            return null;
        }
    }
    
    public static String substring_afterF(final String value, final String substring) {
        final int index = value.indexOf(substring);
        if (index >= 0) {
            return value.substring(index + substring.length());
        }
        return "";
    }
    
    public static String substring_beforeF(final String value, final String substring) {
        final int index = value.indexOf(substring);
        if (index >= 0) {
            return value.substring(0, index);
        }
        return "";
    }
    
    public static String translateF(final String value, final String from, final String to) {
        final int tol = to.length();
        final int froml = from.length();
        final int valuel = value.length();
        final StringBuilder result = BasisLibrary.threadLocalStringBuilder.get();
        result.setLength(0);
        for (int i = 0; i < valuel; ++i) {
            final char ch = value.charAt(i);
            int j = 0;
            while (j < froml) {
                if (ch == from.charAt(j)) {
                    if (j < tol) {
                        result.append(to.charAt(j));
                        break;
                    }
                    break;
                }
                else {
                    ++j;
                }
            }
            if (j == froml) {
                result.append(ch);
            }
        }
        return result.toString();
    }
    
    public static String normalize_spaceF(final int node, final DOM dom) {
        return normalize_spaceF(dom.getStringValueX(node));
    }
    
    public static String normalize_spaceF(final String value) {
        int i = 0;
        final int n = value.length();
        final StringBuilder result = BasisLibrary.threadLocalStringBuilder.get();
        result.setLength(0);
        while (i < n && isWhiteSpace(value.charAt(i))) {
            ++i;
        }
        while (true) {
            if (i < n && !isWhiteSpace(value.charAt(i))) {
                result.append(value.charAt(i++));
            }
            else {
                if (i == n) {
                    break;
                }
                while (i < n && isWhiteSpace(value.charAt(i))) {
                    ++i;
                }
                if (i >= n) {
                    continue;
                }
                result.append(' ');
            }
        }
        return result.toString();
    }
    
    public static String generate_idF(final int node) {
        if (node > 0) {
            return "N" + node;
        }
        return "";
    }
    
    public static String getLocalName(String value) {
        int idx = value.lastIndexOf(58);
        if (idx >= 0) {
            value = value.substring(idx + 1);
        }
        idx = value.lastIndexOf(64);
        if (idx >= 0) {
            value = value.substring(idx + 1);
        }
        return value;
    }
    
    public static void unresolved_externalF(final String name) {
        runTimeError("EXTERNAL_FUNC_ERR", name);
    }
    
    public static void unallowed_extension_functionF(final String name) {
        runTimeError("UNALLOWED_EXTENSION_FUNCTION_ERR", name);
    }
    
    public static void unallowed_extension_elementF(final String name) {
        runTimeError("UNALLOWED_EXTENSION_ELEMENT_ERR", name);
    }
    
    public static void unsupported_ElementF(final String qname, final boolean isExtension) {
        if (isExtension) {
            runTimeError("UNSUPPORTED_EXT_ERR", qname);
        }
        else {
            runTimeError("UNSUPPORTED_XSL_ERR", qname);
        }
    }
    
    public static String namespace_uriF(final DTMAxisIterator iter, final DOM dom) {
        return namespace_uriF(iter.next(), dom);
    }
    
    public static String system_propertyF(final String name) {
        if (name.equals("xsl:version")) {
            return "1.0";
        }
        if (name.equals("xsl:vendor")) {
            return "Apache Software Foundation (Xalan XSLTC)";
        }
        if (name.equals("xsl:vendor-url")) {
            return "http://xml.apache.org/xalan-j";
        }
        runTimeError("INVALID_ARGUMENT_ERR", name, "system-property()");
        return "";
    }
    
    public static String namespace_uriF(final int node, final DOM dom) {
        final String value = dom.getNodeName(node);
        final int colon = value.lastIndexOf(58);
        if (colon >= 0) {
            return value.substring(0, colon);
        }
        return "";
    }
    
    public static String objectTypeF(final Object obj) {
        if (obj instanceof String) {
            return "string";
        }
        if (obj instanceof Boolean) {
            return "boolean";
        }
        if (obj instanceof Number) {
            return "number";
        }
        if (obj instanceof DOM) {
            return "RTF";
        }
        if (obj instanceof DTMAxisIterator) {
            return "node-set";
        }
        return "unknown";
    }
    
    public static DTMAxisIterator nodesetF(final Object obj) {
        if (obj instanceof DOM) {
            final DOM dom = (DOM)obj;
            return new SingletonIterator(dom.getDocument(), true);
        }
        if (obj instanceof DTMAxisIterator) {
            return (DTMAxisIterator)obj;
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", "node-set", className);
        return null;
    }
    
    private static boolean isWhiteSpace(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }
    
    private static boolean compareStrings(final String lstring, final String rstring, final int op, final DOM dom) {
        switch (op) {
            case 0: {
                return lstring.equals(rstring);
            }
            case 1: {
                return !lstring.equals(rstring);
            }
            case 2: {
                return numberF(lstring, dom) > numberF(rstring, dom);
            }
            case 3: {
                return numberF(lstring, dom) < numberF(rstring, dom);
            }
            case 4: {
                return numberF(lstring, dom) >= numberF(rstring, dom);
            }
            case 5: {
                return numberF(lstring, dom) <= numberF(rstring, dom);
            }
            default: {
                runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
                return false;
            }
        }
    }
    
    public static boolean compare(final DTMAxisIterator left, final DTMAxisIterator right, final int op, final DOM dom) {
        left.reset();
        int lnode;
        while ((lnode = left.next()) != -1) {
            final String lvalue = dom.getStringValueX(lnode);
            right.reset();
            int rnode;
            while ((rnode = right.next()) != -1) {
                if (lnode == rnode) {
                    if (op == 0) {
                        return true;
                    }
                    if (op == 1) {
                        continue;
                    }
                }
                if (compareStrings(lvalue, dom.getStringValueX(rnode), op, dom)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean compare(final int node, final DTMAxisIterator iterator, final int op, final DOM dom) {
        Label_0200: {
            switch (op) {
                case 0: {
                    int rnode = iterator.next();
                    if (rnode != -1) {
                        final String value = dom.getStringValueX(node);
                        while (node != rnode && !value.equals(dom.getStringValueX(rnode))) {
                            if ((rnode = iterator.next()) == -1) {
                                break Label_0200;
                            }
                        }
                        return true;
                    }
                    break;
                }
                case 1: {
                    int rnode = iterator.next();
                    if (rnode != -1) {
                        final String value = dom.getStringValueX(node);
                        while (node == rnode || value.equals(dom.getStringValueX(rnode))) {
                            if ((rnode = iterator.next()) == -1) {
                                break Label_0200;
                            }
                        }
                        return true;
                    }
                    break;
                }
                case 3: {
                    int rnode;
                    while ((rnode = iterator.next()) != -1) {
                        if (rnode > node) {
                            return true;
                        }
                    }
                    break;
                }
                case 2: {
                    int rnode;
                    while ((rnode = iterator.next()) != -1) {
                        if (rnode < node) {
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    public static boolean compare(final DTMAxisIterator left, final double rnumber, final int op, final DOM dom) {
        switch (op) {
            case 0: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) == rnumber) {
                        return true;
                    }
                }
                break;
            }
            case 1: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) != rnumber) {
                        return true;
                    }
                }
                break;
            }
            case 2: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) > rnumber) {
                        return true;
                    }
                }
                break;
            }
            case 3: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) < rnumber) {
                        return true;
                    }
                }
                break;
            }
            case 4: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) >= rnumber) {
                        return true;
                    }
                }
                break;
            }
            case 5: {
                int node;
                while ((node = left.next()) != -1) {
                    if (numberF(dom.getStringValueX(node), dom) <= rnumber) {
                        return true;
                    }
                }
                break;
            }
            default: {
                runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
                break;
            }
        }
        return false;
    }
    
    public static boolean compare(final DTMAxisIterator left, final String rstring, final int op, final DOM dom) {
        int node;
        while ((node = left.next()) != -1) {
            if (compareStrings(dom.getStringValueX(node), rstring, op, dom)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean compare(Object left, Object right, int op, final DOM dom) {
        boolean result = false;
        boolean hasSimpleArgs = hasSimpleType(left) && hasSimpleType(right);
        if (op != 0 && op != 1) {
            if (left instanceof Node || right instanceof Node) {
                if (left instanceof Boolean) {
                    right = new Boolean(booleanF(right));
                    hasSimpleArgs = true;
                }
                if (right instanceof Boolean) {
                    left = new Boolean(booleanF(left));
                    hasSimpleArgs = true;
                }
            }
            if (hasSimpleArgs) {
                switch (op) {
                    case 2: {
                        return numberF(left, dom) > numberF(right, dom);
                    }
                    case 3: {
                        return numberF(left, dom) < numberF(right, dom);
                    }
                    case 4: {
                        return numberF(left, dom) >= numberF(right, dom);
                    }
                    case 5: {
                        return numberF(left, dom) <= numberF(right, dom);
                    }
                    default: {
                        runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
                        break;
                    }
                }
            }
        }
        if (hasSimpleArgs) {
            if (left instanceof Boolean || right instanceof Boolean) {
                result = (booleanF(left) == booleanF(right));
            }
            else if (left instanceof Double || right instanceof Double || left instanceof Integer || right instanceof Integer) {
                result = (numberF(left, dom) == numberF(right, dom));
            }
            else {
                result = stringF(left, dom).equals(stringF(right, dom));
            }
            if (op == 1) {
                result = !result;
            }
        }
        else {
            if (left instanceof Node) {
                left = new SingletonIterator(((Node)left).node);
            }
            if (right instanceof Node) {
                right = new SingletonIterator(((Node)right).node);
            }
            if (hasSimpleType(left) || (left instanceof DOM && right instanceof DTMAxisIterator)) {
                final Object temp = right;
                right = left;
                left = temp;
                op = Operators.swapOp(op);
            }
            if (left instanceof DOM) {
                if (right instanceof Boolean) {
                    result = (boolean)right;
                    return result == (op == 0);
                }
                final String sleft = ((DOM)left).getStringValue();
                if (right instanceof Number) {
                    result = (((Number)right).doubleValue() == stringToReal(sleft));
                }
                else if (right instanceof String) {
                    result = sleft.equals(right);
                }
                else if (right instanceof DOM) {
                    result = sleft.equals(((DOM)right).getStringValue());
                }
                if (op == 1) {
                    result = !result;
                }
                return result;
            }
            else {
                final DTMAxisIterator iter = ((DTMAxisIterator)left).reset();
                if (right instanceof DTMAxisIterator) {
                    result = compare(iter, (DTMAxisIterator)right, op, dom);
                }
                else if (right instanceof String) {
                    result = compare(iter, (String)right, op, dom);
                }
                else if (right instanceof Number) {
                    final double temp2 = ((Number)right).doubleValue();
                    result = compare(iter, temp2, op, dom);
                }
                else if (right instanceof Boolean) {
                    final boolean temp3 = (boolean)right;
                    result = (iter.reset().next() != -1 == temp3);
                }
                else if (right instanceof DOM) {
                    result = compare(iter, ((DOM)right).getStringValue(), op, dom);
                }
                else {
                    if (right == null) {
                        return false;
                    }
                    final String className = right.getClass().getName();
                    runTimeError("INVALID_ARGUMENT_ERR", className, "compare()");
                }
            }
        }
        return result;
    }
    
    public static boolean testLanguage(String testLang, final DOM dom, final int node) {
        String nodeLang = dom.getLanguage(node);
        if (nodeLang == null) {
            return false;
        }
        nodeLang = nodeLang.toLowerCase();
        testLang = testLang.toLowerCase();
        if (testLang.length() == 2) {
            return nodeLang.startsWith(testLang);
        }
        return nodeLang.equals(testLang);
    }
    
    private static boolean hasSimpleType(final Object obj) {
        return obj instanceof Boolean || obj instanceof Double || obj instanceof Integer || obj instanceof String || obj instanceof Node || obj instanceof DOM;
    }
    
    public static double stringToReal(final String s) {
        try {
            return Double.valueOf(s);
        }
        catch (final NumberFormatException e) {
            return Double.NaN;
        }
    }
    
    public static int stringToInt(final String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException e) {
            return -1;
        }
    }
    
    public static String realToString(double d) {
        final double m = Math.abs(d);
        if (m >= 0.001 && m < 1.0E7) {
            final String result = Double.toString(d);
            final int length = result.length();
            if (result.charAt(length - 2) == '.' && result.charAt(length - 1) == '0') {
                return result.substring(0, length - 2);
            }
            return result;
        }
        else {
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                return Double.toString(d);
            }
            d += 0.0;
            final StringBuffer result2 = BasisLibrary.threadLocalStringBuffer.get();
            result2.setLength(0);
            BasisLibrary.xpathFormatter.format(d, result2, BasisLibrary._fieldPosition);
            return result2.toString();
        }
    }
    
    public static int realToInt(final double d) {
        return (int)d;
    }
    
    public static String formatNumber(final double number, final String pattern, DecimalFormat formatter) {
        if (formatter == null) {
            formatter = BasisLibrary.defaultFormatter;
        }
        try {
            final StringBuffer result = BasisLibrary.threadLocalStringBuffer.get();
            result.setLength(0);
            if (pattern != BasisLibrary.defaultPattern) {
                formatter.applyLocalizedPattern(pattern);
            }
            formatter.format(number, result, BasisLibrary._fieldPosition);
            return result.toString();
        }
        catch (final IllegalArgumentException e) {
            runTimeError("FORMAT_NUMBER_ERR", Double.toString(number), pattern);
            return "";
        }
    }
    
    public static DTMAxisIterator referenceToNodeSet(final Object obj) {
        if (obj instanceof Node) {
            return new SingletonIterator(((Node)obj).node);
        }
        if (obj instanceof DTMAxisIterator) {
            return ((DTMAxisIterator)obj).cloneIterator().reset();
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, "node-set");
        return null;
    }
    
    public static NodeList referenceToNodeList(final Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            final DTMAxisIterator iter = referenceToNodeSet(obj);
            return dom.makeNodeList(iter);
        }
        if (obj instanceof DOM) {
            dom = (DOM)obj;
            return dom.makeNodeList(0);
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, "org.w3c.dom.NodeList");
        return null;
    }
    
    public static org.w3c.dom.Node referenceToNode(final Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            final DTMAxisIterator iter = referenceToNodeSet(obj);
            return dom.makeNode(iter);
        }
        if (obj instanceof DOM) {
            dom = (DOM)obj;
            final DTMAxisIterator iter = dom.getChildren(0);
            return dom.makeNode(iter);
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, "org.w3c.dom.Node");
        return null;
    }
    
    public static long referenceToLong(final Object obj) {
        if (obj instanceof Number) {
            return ((Number)obj).longValue();
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, Long.TYPE);
        return 0L;
    }
    
    public static double referenceToDouble(final Object obj) {
        if (obj instanceof Number) {
            return ((Number)obj).doubleValue();
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, Double.TYPE);
        return 0.0;
    }
    
    public static boolean referenceToBoolean(final Object obj) {
        if (obj instanceof Boolean) {
            return (boolean)obj;
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, Boolean.TYPE);
        return false;
    }
    
    public static String referenceToString(final Object obj, final DOM dom) {
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        final String className = obj.getClass().getName();
        runTimeError("DATA_CONVERSION_ERR", className, String.class);
        return null;
    }
    
    public static DTMAxisIterator node2Iterator(final org.w3c.dom.Node node, final Translet translet, final DOM dom) {
        final org.w3c.dom.Node inNode = node;
        final NodeList nodelist = new NodeList() {
            @Override
            public int getLength() {
                return 1;
            }
            
            @Override
            public org.w3c.dom.Node item(final int index) {
                if (index == 0) {
                    return inNode;
                }
                return null;
            }
        };
        return nodeList2Iterator(nodelist, translet, dom);
    }
    
    private static DTMAxisIterator nodeList2IteratorUsingHandleFromNode(final NodeList nodeList, final Translet translet, final DOM dom) {
        final int n = nodeList.getLength();
        final int[] dtmHandles = new int[n];
        DTMManager dtmManager = null;
        if (dom instanceof MultiDOM) {
            dtmManager = ((MultiDOM)dom).getDTMManager();
        }
        for (int i = 0; i < n; ++i) {
            final org.w3c.dom.Node node = nodeList.item(i);
            int handle;
            if (dtmManager != null) {
                handle = dtmManager.getDTMHandleFromNode(node);
            }
            else {
                if (!(node instanceof DTMNodeProxy) || ((DTMNodeProxy)node).getDTM() != dom) {
                    runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
                    return null;
                }
                handle = ((DTMNodeProxy)node).getDTMNodeNumber();
            }
            dtmHandles[i] = handle;
            System.out.println("Node " + i + " has handle 0x" + Integer.toString(handle, 16));
        }
        return new ArrayNodeListIterator(dtmHandles);
    }
    
    public static DTMAxisIterator nodeList2Iterator(final NodeList nodeList, final Translet translet, final DOM dom) {
        int n = 0;
        Document doc = null;
        DTMManager dtmManager = null;
        final int[] proxyNodes = new int[nodeList.getLength()];
        if (dom instanceof MultiDOM) {
            dtmManager = ((MultiDOM)dom).getDTMManager();
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final org.w3c.dom.Node node = nodeList.item(i);
            if (node instanceof DTMNodeProxy) {
                final DTMNodeProxy proxy = (DTMNodeProxy)node;
                final DTM nodeDTM = proxy.getDTM();
                final int handle = proxy.getDTMNodeNumber();
                boolean isOurDOM = nodeDTM == dom;
                if (!isOurDOM && dtmManager != null) {
                    try {
                        isOurDOM = (nodeDTM == dtmManager.getDTM(handle));
                    }
                    catch (final ArrayIndexOutOfBoundsException ex) {}
                }
                if (isOurDOM) {
                    proxyNodes[i] = handle;
                    ++n;
                    continue;
                }
            }
            proxyNodes[i] = -1;
            final int nodeType = node.getNodeType();
            if (doc == null) {
                if (!(dom instanceof MultiDOM)) {
                    runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
                    return null;
                }
                try {
                    final AbstractTranslet at = (AbstractTranslet)translet;
                    doc = at.newDocument("", "__top__");
                }
                catch (final ParserConfigurationException e) {
                    runTimeError("RUN_TIME_INTERNAL_ERR", e.getMessage());
                    return null;
                }
            }
            switch (nodeType) {
                case 1:
                case 3:
                case 4:
                case 5:
                case 7:
                case 8: {
                    final Element mid = doc.createElementNS(null, "__dummy__");
                    mid.appendChild(doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    break;
                }
                case 2: {
                    final Element mid = doc.createElementNS(null, "__dummy__");
                    mid.setAttributeNodeNS((Attr)doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    break;
                }
                default: {
                    runTimeError("RUN_TIME_INTERNAL_ERR", "Don't know how to convert node type " + nodeType);
                    break;
                }
            }
        }
        DTMAxisIterator iter = null;
        DTMAxisIterator childIter = null;
        DTMAxisIterator attrIter = null;
        if (doc != null) {
            final MultiDOM multiDOM = (MultiDOM)dom;
            final DOM idom = (DOM)dtmManager.getDTM(new DOMSource(doc), false, null, true, false);
            final DOMAdapter domAdapter = new DOMAdapter(idom, translet.getNamesArray(), translet.getUrisArray(), translet.getTypesArray(), translet.getNamespaceArray());
            multiDOM.addDOMAdapter(domAdapter);
            final DTMAxisIterator iter2 = idom.getAxisIterator(3);
            final DTMAxisIterator iter3 = idom.getAxisIterator(3);
            iter = new AbsoluteIterator(new StepIterator(iter2, iter3));
            iter.setStartNode(0);
            childIter = idom.getAxisIterator(3);
            attrIter = idom.getAxisIterator(2);
        }
        final int[] dtmHandles = new int[n];
        n = 0;
        for (int j = 0; j < nodeList.getLength(); ++j) {
            if (proxyNodes[j] != -1) {
                dtmHandles[n++] = proxyNodes[j];
            }
            else {
                final org.w3c.dom.Node node2 = nodeList.item(j);
                DTMAxisIterator iter4 = null;
                final int nodeType2 = node2.getNodeType();
                switch (nodeType2) {
                    case 1:
                    case 3:
                    case 4:
                    case 5:
                    case 7:
                    case 8: {
                        iter4 = childIter;
                        break;
                    }
                    case 2: {
                        iter4 = attrIter;
                        break;
                    }
                    default: {
                        throw new InternalRuntimeError("Mismatched cases");
                    }
                }
                if (iter4 != null) {
                    iter4.setStartNode(iter.next());
                    dtmHandles[n] = iter4.next();
                    if (dtmHandles[n] == -1) {
                        throw new InternalRuntimeError("Expected element missing at " + j);
                    }
                    if (iter4.next() != -1) {
                        throw new InternalRuntimeError("Too many elements at " + j);
                    }
                    ++n;
                }
            }
        }
        if (n != dtmHandles.length) {
            throw new InternalRuntimeError("Nodes lost in second pass");
        }
        return new ArrayNodeListIterator(dtmHandles);
    }
    
    public static DOM referenceToResultTree(final Object obj) {
        try {
            return (DOM)obj;
        }
        catch (final IllegalArgumentException e) {
            final String className = obj.getClass().getName();
            runTimeError("DATA_CONVERSION_ERR", "reference", className);
            return null;
        }
    }
    
    public static DTMAxisIterator getSingleNode(final DTMAxisIterator iterator) {
        final int node = iterator.next();
        return new SingletonIterator(node);
    }
    
    public static void copy(final Object obj, final SerializationHandler handler, final int node, final DOM dom) {
        try {
            if (obj instanceof DTMAxisIterator) {
                final DTMAxisIterator iter = (DTMAxisIterator)obj;
                dom.copy(iter.reset(), handler);
            }
            else if (obj instanceof Node) {
                dom.copy(((Node)obj).node, handler);
            }
            else if (obj instanceof DOM) {
                final DOM newDom = (DOM)obj;
                newDom.copy(newDom.getDocument(), handler);
            }
            else {
                final String string = obj.toString();
                final int length = string.length();
                if (length > BasisLibrary._characterArray.length) {
                    BasisLibrary._characterArray = new char[length];
                }
                string.getChars(0, length, BasisLibrary._characterArray, 0);
                handler.characters(BasisLibrary._characterArray, 0, length);
            }
        }
        catch (final SAXException e) {
            runTimeError("RUN_TIME_COPY_ERR");
        }
    }
    
    public static void checkAttribQName(final String name) {
        final int firstOccur = name.indexOf(58);
        final int lastOccur = name.lastIndexOf(58);
        final String localName = name.substring(lastOccur + 1);
        if (firstOccur > 0) {
            final String newPrefix = name.substring(0, firstOccur);
            if (firstOccur != lastOccur) {
                final String oriPrefix = name.substring(firstOccur + 1, lastOccur);
                if (!XML11Char.isXML11ValidNCName(oriPrefix)) {
                    runTimeError("INVALID_QNAME_ERR", oriPrefix + ":" + localName);
                }
            }
            if (!XML11Char.isXML11ValidNCName(newPrefix)) {
                runTimeError("INVALID_QNAME_ERR", newPrefix + ":" + localName);
            }
        }
        if (!XML11Char.isXML11ValidNCName(localName) || localName.equals("xmlns")) {
            runTimeError("INVALID_QNAME_ERR", localName);
        }
    }
    
    public static void checkNCName(final String name) {
        if (!XML11Char.isXML11ValidNCName(name)) {
            runTimeError("INVALID_NCNAME_ERR", name);
        }
    }
    
    public static void checkQName(final String name) {
        if (!XML11Char.isXML11ValidQName(name)) {
            runTimeError("INVALID_QNAME_ERR", name);
        }
    }
    
    public static String startXslElement(String qname, String namespace, final SerializationHandler handler, final DOM dom, final int node) {
        try {
            final int index = qname.indexOf(58);
            if (index > 0) {
                final String prefix = qname.substring(0, index);
                Label_0082: {
                    if (namespace != null) {
                        if (namespace.length() != 0) {
                            break Label_0082;
                        }
                    }
                    try {
                        namespace = dom.lookupNamespace(node, prefix);
                    }
                    catch (final RuntimeException e) {
                        handler.flushPending();
                        final NamespaceMappings nm = handler.getNamespaceMappings();
                        namespace = nm.lookupNamespace(prefix);
                        if (namespace == null) {
                            runTimeError("NAMESPACE_PREFIX_ERR", prefix);
                        }
                    }
                }
                handler.startElement(namespace, qname.substring(index + 1), qname);
                handler.namespaceAfterStartElement(prefix, namespace);
            }
            else if (namespace != null && namespace.length() > 0) {
                final String prefix = generatePrefix();
                qname = prefix + ':' + qname;
                handler.startElement(namespace, qname, qname);
                handler.namespaceAfterStartElement(prefix, namespace);
            }
            else {
                handler.startElement(null, null, qname);
            }
        }
        catch (final SAXException e2) {
            throw new RuntimeException(e2.getMessage());
        }
        return qname;
    }
    
    public static String getPrefix(final String qname) {
        final int index = qname.indexOf(58);
        return (index > 0) ? qname.substring(0, index) : null;
    }
    
    public static String generatePrefix() {
        return "ns" + BasisLibrary.threadLocalPrefixIndex.get().getAndIncrement();
    }
    
    public static void resetPrefixIndex() {
        BasisLibrary.threadLocalPrefixIndex.get().set(0);
    }
    
    public static void runTimeError(final String code) {
        throw new RuntimeException(BasisLibrary.m_bundle.getString(code));
    }
    
    public static void runTimeError(final String code, final Object[] args) {
        final String message = MessageFormat.format(BasisLibrary.m_bundle.getString(code), args);
        throw new RuntimeException(message);
    }
    
    public static void runTimeError(final String code, final Object arg0) {
        runTimeError(code, new Object[] { arg0 });
    }
    
    public static void runTimeError(final String code, final Object arg0, final Object arg1) {
        runTimeError(code, new Object[] { arg0, arg1 });
    }
    
    public static void consoleOutput(final String msg) {
        System.out.println(msg);
    }
    
    public static String replace(final String base, final char ch, final String str) {
        return (base.indexOf(ch) < 0) ? base : replace(base, String.valueOf(ch), new String[] { str });
    }
    
    public static String replace(final String base, final String delim, final String[] str) {
        final int len = base.length();
        final StringBuilder result = BasisLibrary.threadLocalStringBuilder.get();
        result.setLength(0);
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
    
    public static String mapQNameToJavaName(final String base) {
        return replace(base, ".-:/{}?#%*", new String[] { "$dot$", "$dash$", "$colon$", "$slash$", "", "$colon$", "$ques$", "$hash$", "$per$", "$aster$" });
    }
    
    public static int getStringLength(final String str) {
        return str.codePointCount(0, str.length());
    }
    
    static {
        threadLocalStringBuilder = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder();
            }
        };
        threadLocalStringBuffer = new ThreadLocal<StringBuffer>() {
            @Override
            protected StringBuffer initialValue() {
                return new StringBuffer();
            }
        };
        BasisLibrary.defaultPattern = "";
        final NumberFormat f = NumberFormat.getInstance(Locale.getDefault());
        (BasisLibrary.defaultFormatter = (DecimalFormat)((f instanceof DecimalFormat) ? f : new DecimalFormat())).setMaximumFractionDigits(340);
        BasisLibrary.defaultFormatter.setMinimumFractionDigits(0);
        BasisLibrary.defaultFormatter.setMinimumIntegerDigits(1);
        BasisLibrary.defaultFormatter.setGroupingUsed(false);
        (BasisLibrary.xpathFormatter = new DecimalFormat("", new DecimalFormatSymbols(Locale.US))).setMaximumFractionDigits(340);
        BasisLibrary.xpathFormatter.setMinimumFractionDigits(0);
        BasisLibrary.xpathFormatter.setMinimumIntegerDigits(1);
        BasisLibrary.xpathFormatter.setGroupingUsed(false);
        BasisLibrary._fieldPosition = new FieldPosition(0);
        BasisLibrary._characterArray = new char[32];
        threadLocalPrefixIndex = new ThreadLocal<AtomicInteger>() {
            @Override
            protected AtomicInteger initialValue() {
                return new AtomicInteger();
            }
        };
        final String resource = "com.sun.org.apache.xalan.internal.xsltc.runtime.ErrorMessages";
        BasisLibrary.m_bundle = SecuritySupport.getResourceBundle(resource);
    }
}
