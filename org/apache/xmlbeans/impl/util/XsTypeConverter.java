package org.apache.xmlbeans.impl.util;

import java.net.URI;
import java.util.Date;
import java.util.Calendar;
import org.apache.xmlbeans.GDateSpecification;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.GDate;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.apache.xmlbeans.impl.common.InvalidLexicalValueException;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlError;
import java.util.Collection;
import java.math.BigDecimal;

public final class XsTypeConverter
{
    private static final String POS_INF_LEX = "INF";
    private static final String NEG_INF_LEX = "-INF";
    private static final String NAN_LEX = "NaN";
    private static final char NAMESPACE_SEP = ':';
    private static final String EMPTY_PREFIX = "";
    private static final BigDecimal DECIMAL__ZERO;
    private static final String[] URI_CHARS_TO_BE_REPLACED;
    private static final String[] URI_CHARS_REPLACED_WITH;
    private static final char[] CH_ZEROS;
    
    public static float lexFloat(final CharSequence cs) throws NumberFormatException {
        final String v = cs.toString();
        try {
            if (cs.length() > 0) {
                final char ch = cs.charAt(cs.length() - 1);
                if ((ch == 'f' || ch == 'F') && cs.charAt(cs.length() - 2) != 'N') {
                    throw new NumberFormatException("Invalid char '" + ch + "' in float.");
                }
            }
            return Float.parseFloat(v);
        }
        catch (final NumberFormatException e) {
            if (v.equals("INF")) {
                return Float.POSITIVE_INFINITY;
            }
            if (v.equals("-INF")) {
                return Float.NEGATIVE_INFINITY;
            }
            if (v.equals("NaN")) {
                return Float.NaN;
            }
            throw e;
        }
    }
    
    public static float lexFloat(final CharSequence cs, final Collection errors) {
        try {
            return lexFloat(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid float: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return Float.NaN;
        }
    }
    
    public static String printFloat(final float value) {
        if (value == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (value == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        if (Float.isNaN(value)) {
            return "NaN";
        }
        return Float.toString(value);
    }
    
    public static double lexDouble(final CharSequence cs) throws NumberFormatException {
        final String v = cs.toString();
        try {
            if (cs.length() > 0) {
                final char ch = cs.charAt(cs.length() - 1);
                if (ch == 'd' || ch == 'D') {
                    throw new NumberFormatException("Invalid char '" + ch + "' in double.");
                }
            }
            return Double.parseDouble(v);
        }
        catch (final NumberFormatException e) {
            if (v.equals("INF")) {
                return Double.POSITIVE_INFINITY;
            }
            if (v.equals("-INF")) {
                return Double.NEGATIVE_INFINITY;
            }
            if (v.equals("NaN")) {
                return Double.NaN;
            }
            throw e;
        }
    }
    
    public static double lexDouble(final CharSequence cs, final Collection errors) {
        try {
            return lexDouble(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid double: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return Double.NaN;
        }
    }
    
    public static String printDouble(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        if (Double.isNaN(value)) {
            return "NaN";
        }
        return Double.toString(value);
    }
    
    public static BigDecimal lexDecimal(final CharSequence cs) throws NumberFormatException {
        final String v = cs.toString();
        return new BigDecimal(trimTrailingZeros(v));
    }
    
    public static BigDecimal lexDecimal(final CharSequence cs, final Collection errors) {
        try {
            return lexDecimal(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid long: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return XsTypeConverter.DECIMAL__ZERO;
        }
    }
    
    public static String printDecimal(final BigDecimal value) {
        final String intStr = value.unscaledValue().toString();
        final int scale = value.scale();
        if (scale == 0 || (value.longValue() == 0L && scale < 0)) {
            return intStr;
        }
        final int begin = (value.signum() < 0) ? 1 : 0;
        int delta = scale;
        final StringBuffer result = new StringBuffer(intStr.length() + 1 + Math.abs(scale));
        if (begin == 1) {
            result.append('-');
        }
        if (scale > 0) {
            delta -= intStr.length() - begin;
            if (delta >= 0) {
                result.append("0.");
                while (delta > XsTypeConverter.CH_ZEROS.length) {
                    result.append(XsTypeConverter.CH_ZEROS);
                    delta -= XsTypeConverter.CH_ZEROS.length;
                }
                result.append(XsTypeConverter.CH_ZEROS, 0, delta);
                result.append(intStr.substring(begin));
            }
            else {
                delta = begin - delta;
                result.append(intStr.substring(begin, delta));
                result.append('.');
                result.append(intStr.substring(delta));
            }
        }
        else {
            result.append(intStr.substring(begin));
            while (delta < -XsTypeConverter.CH_ZEROS.length) {
                result.append(XsTypeConverter.CH_ZEROS);
                delta += XsTypeConverter.CH_ZEROS.length;
            }
            result.append(XsTypeConverter.CH_ZEROS, 0, -delta);
        }
        return result.toString();
    }
    
    public static BigInteger lexInteger(final CharSequence cs) throws NumberFormatException {
        if (cs.length() > 1 && cs.charAt(0) == '+' && cs.charAt(1) == '-') {
            throw new NumberFormatException("Illegal char sequence '+-'");
        }
        final String v = cs.toString();
        return new BigInteger(trimInitialPlus(v));
    }
    
    public static BigInteger lexInteger(final CharSequence cs, final Collection errors) {
        try {
            return lexInteger(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid long: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return BigInteger.ZERO;
        }
    }
    
    public static String printInteger(final BigInteger value) {
        return value.toString();
    }
    
    public static long lexLong(final CharSequence cs) throws NumberFormatException {
        final String v = cs.toString();
        return Long.parseLong(trimInitialPlus(v));
    }
    
    public static long lexLong(final CharSequence cs, final Collection errors) {
        try {
            return lexLong(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid long: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return 0L;
        }
    }
    
    public static String printLong(final long value) {
        return Long.toString(value);
    }
    
    public static short lexShort(final CharSequence cs) throws NumberFormatException {
        return parseShort(cs);
    }
    
    public static short lexShort(final CharSequence cs, final Collection errors) {
        try {
            return lexShort(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid short: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return 0;
        }
    }
    
    public static String printShort(final short value) {
        return Short.toString(value);
    }
    
    public static int lexInt(final CharSequence cs) throws NumberFormatException {
        return parseInt(cs);
    }
    
    public static int lexInt(final CharSequence cs, final Collection errors) {
        try {
            return lexInt(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid int:" + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return 0;
        }
    }
    
    public static String printInt(final int value) {
        return Integer.toString(value);
    }
    
    public static byte lexByte(final CharSequence cs) throws NumberFormatException {
        return parseByte(cs);
    }
    
    public static byte lexByte(final CharSequence cs, final Collection errors) {
        try {
            return lexByte(cs);
        }
        catch (final NumberFormatException e) {
            final String msg = "invalid byte: " + (Object)cs;
            errors.add(XmlError.forMessage(msg));
            return 0;
        }
    }
    
    public static String printByte(final byte value) {
        return Byte.toString(value);
    }
    
    public static boolean lexBoolean(final CharSequence v) {
        switch (v.length()) {
            case 1: {
                final char c = v.charAt(0);
                if ('0' == c) {
                    return false;
                }
                if ('1' == c) {
                    return true;
                }
                break;
            }
            case 4: {
                if ('t' == v.charAt(0) && 'r' == v.charAt(1) && 'u' == v.charAt(2) && 'e' == v.charAt(3)) {
                    return true;
                }
                break;
            }
            case 5: {
                if ('f' == v.charAt(0) && 'a' == v.charAt(1) && 'l' == v.charAt(2) && 's' == v.charAt(3) && 'e' == v.charAt(4)) {
                    return false;
                }
                break;
            }
        }
        final String msg = "invalid boolean: " + (Object)v;
        throw new InvalidLexicalValueException(msg);
    }
    
    public static boolean lexBoolean(final CharSequence value, final Collection errors) {
        try {
            return lexBoolean(value);
        }
        catch (final InvalidLexicalValueException e) {
            errors.add(XmlError.forMessage(e.getMessage()));
            return false;
        }
    }
    
    public static String printBoolean(final boolean value) {
        return value ? "true" : "false";
    }
    
    public static String lexString(final CharSequence cs, final Collection errors) {
        final String v = cs.toString();
        return v;
    }
    
    public static String lexString(final CharSequence lexical_value) {
        return lexical_value.toString();
    }
    
    public static String printString(final String value) {
        return value;
    }
    
    public static QName lexQName(final CharSequence charSeq, final NamespaceContext nscontext) {
        boolean hasFirstCollon = false;
        int firstcolon;
        for (firstcolon = 0; firstcolon < charSeq.length(); ++firstcolon) {
            if (charSeq.charAt(firstcolon) == ':') {
                hasFirstCollon = true;
                break;
            }
        }
        String prefix;
        String localname;
        if (hasFirstCollon) {
            prefix = charSeq.subSequence(0, firstcolon).toString();
            localname = charSeq.subSequence(firstcolon + 1, charSeq.length()).toString();
            if (firstcolon == 0) {
                throw new InvalidLexicalValueException("invalid xsd:QName '" + charSeq.toString() + "'");
            }
        }
        else {
            prefix = "";
            localname = charSeq.toString();
        }
        String uri = nscontext.getNamespaceURI(prefix);
        if (uri == null) {
            if (prefix != null && prefix.length() > 0) {
                throw new InvalidLexicalValueException("Can't resolve prefix: " + prefix);
            }
            uri = "";
        }
        return new QName(uri, localname);
    }
    
    public static QName lexQName(final String xsd_qname, final Collection errors, final NamespaceContext nscontext) {
        try {
            return lexQName(xsd_qname, nscontext);
        }
        catch (final InvalidLexicalValueException e) {
            errors.add(XmlError.forMessage(e.getMessage()));
            final int idx = xsd_qname.indexOf(58);
            return new QName(null, xsd_qname.substring(idx));
        }
    }
    
    public static String printQName(final QName qname, final NamespaceContext nsContext, final Collection errors) {
        final String uri = qname.getNamespaceURI();
        assert uri != null;
        String prefix;
        if (uri.length() > 0) {
            prefix = nsContext.getPrefix(uri);
            if (prefix == null) {
                final String msg = "NamespaceContext does not provide prefix for namespaceURI " + uri;
                errors.add(XmlError.forMessage(msg));
            }
        }
        else {
            prefix = null;
        }
        return getQNameString(uri, qname.getLocalPart(), prefix);
    }
    
    public static String getQNameString(final String uri, final String localpart, final String prefix) {
        if (prefix != null && uri != null && uri.length() > 0 && prefix.length() > 0) {
            return prefix + ':' + localpart;
        }
        return localpart;
    }
    
    public static GDate lexGDate(final CharSequence charSeq) {
        return new GDate(charSeq);
    }
    
    public static GDate lexGDate(final String xsd_gdate, final Collection errors) {
        try {
            return lexGDate(xsd_gdate);
        }
        catch (final IllegalArgumentException e) {
            errors.add(XmlError.forMessage(e.getMessage()));
            return new GDateBuilder().toGDate();
        }
    }
    
    public static String printGDate(final GDate gdate, final Collection errors) {
        return gdate.toString();
    }
    
    public static XmlCalendar lexDateTime(final CharSequence v) {
        final GDateSpecification value = getGDateValue(v, 14);
        return value.getCalendar();
    }
    
    public static String printDateTime(final Calendar c) {
        return printDateTime(c, 14);
    }
    
    public static String printTime(final Calendar c) {
        return printDateTime(c, 15);
    }
    
    public static String printDate(final Calendar c) {
        return printDateTime(c, 16);
    }
    
    public static String printDate(final Date d) {
        final GDateSpecification value = getGDateValue(d, 16);
        return value.toString();
    }
    
    public static String printDateTime(final Calendar c, final int type_code) {
        final GDateSpecification value = getGDateValue(c, type_code);
        return value.toString();
    }
    
    public static String printDateTime(final Date c) {
        final GDateSpecification value = getGDateValue(c, 14);
        return value.toString();
    }
    
    public static CharSequence printHexBinary(final byte[] val) {
        return HexBin.bytesToString(val);
    }
    
    public static byte[] lexHexBinary(final CharSequence lexical_value) {
        final byte[] buf = HexBin.decode(lexical_value.toString().getBytes());
        if (buf != null) {
            return buf;
        }
        throw new InvalidLexicalValueException("invalid hexBinary value");
    }
    
    public static CharSequence printBase64Binary(final byte[] val) {
        final byte[] bytes = Base64.encode(val);
        return new String(bytes);
    }
    
    public static byte[] lexBase64Binary(final CharSequence lexical_value) {
        final byte[] buf = Base64.decode(lexical_value.toString().getBytes());
        if (buf != null) {
            return buf;
        }
        throw new InvalidLexicalValueException("invalid base64Binary value");
    }
    
    public static GDateSpecification getGDateValue(final Date d, final int builtin_type_code) {
        final GDateBuilder gDateBuilder = new GDateBuilder(d);
        gDateBuilder.setBuiltinTypeCode(builtin_type_code);
        final GDate value = gDateBuilder.toGDate();
        return value;
    }
    
    public static GDateSpecification getGDateValue(final Calendar c, final int builtin_type_code) {
        final GDateBuilder gDateBuilder = new GDateBuilder(c);
        gDateBuilder.setBuiltinTypeCode(builtin_type_code);
        final GDate value = gDateBuilder.toGDate();
        return value;
    }
    
    public static GDateSpecification getGDateValue(final CharSequence v, final int builtin_type_code) {
        final GDateBuilder gDateBuilder = new GDateBuilder(v);
        gDateBuilder.setBuiltinTypeCode(builtin_type_code);
        final GDate value = gDateBuilder.toGDate();
        return value;
    }
    
    private static String trimInitialPlus(final String xml) {
        if (xml.length() > 0 && xml.charAt(0) == '+') {
            return xml.substring(1);
        }
        return xml;
    }
    
    private static String trimTrailingZeros(final String xsd_decimal) {
        final int last_char_idx = xsd_decimal.length() - 1;
        if (xsd_decimal.charAt(last_char_idx) == '0') {
            final int last_point = xsd_decimal.lastIndexOf(46);
            if (last_point >= 0) {
                for (int idx = last_char_idx; idx > last_point; --idx) {
                    if (xsd_decimal.charAt(idx) != '0') {
                        return xsd_decimal.substring(0, idx + 1);
                    }
                }
                return xsd_decimal.substring(0, last_point);
            }
        }
        return xsd_decimal;
    }
    
    private static int parseInt(final CharSequence cs) {
        return parseIntXsdNumber(cs, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    private static short parseShort(final CharSequence cs) {
        return (short)parseIntXsdNumber(cs, -32768, 32767);
    }
    
    private static byte parseByte(final CharSequence cs) {
        return (byte)parseIntXsdNumber(cs, -128, 127);
    }
    
    private static int parseIntXsdNumber(final CharSequence ch, final int min_value, final int max_value) {
        final int length = ch.length();
        if (length < 1) {
            throw new NumberFormatException("For input string: \"" + ch.toString() + "\"");
        }
        int sign = 1;
        int result = 0;
        int start = 0;
        char c = ch.charAt(0);
        int limit;
        int limit2;
        if (c == '-') {
            ++start;
            limit = min_value / 10;
            limit2 = -(min_value % 10);
        }
        else if (c == '+') {
            ++start;
            sign = -1;
            limit = -(max_value / 10);
            limit2 = max_value % 10;
        }
        else {
            sign = -1;
            limit = -(max_value / 10);
            limit2 = max_value % 10;
        }
        for (int i = 0; i < length - start; ++i) {
            c = ch.charAt(i + start);
            final int v = Character.digit(c, 10);
            if (v < 0) {
                throw new NumberFormatException("For input string: \"" + ch.toString() + "\"");
            }
            if (result < limit || (result == limit && v > limit2)) {
                throw new NumberFormatException("For input string: \"" + ch.toString() + "\"");
            }
            result = result * 10 - v;
        }
        return sign * result;
    }
    
    public static CharSequence printAnyURI(final CharSequence val) {
        return val;
    }
    
    public static CharSequence lexAnyURI(final CharSequence lexical_value) {
        final StringBuffer s = new StringBuffer(lexical_value.toString());
        for (int ic = 0; ic < XsTypeConverter.URI_CHARS_TO_BE_REPLACED.length; ++ic) {
            for (int i = 0; (i = s.indexOf(XsTypeConverter.URI_CHARS_TO_BE_REPLACED[ic], i)) >= 0; i += 3) {
                s.replace(i, i + 1, XsTypeConverter.URI_CHARS_REPLACED_WITH[ic]);
            }
        }
        try {
            URI.create(s.toString());
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException("invalid anyURI value: " + (Object)lexical_value, e);
        }
        return lexical_value;
    }
    
    static {
        DECIMAL__ZERO = new BigDecimal(0.0);
        URI_CHARS_TO_BE_REPLACED = new String[] { " ", "{", "}", "|", "\\", "^", "[", "]", "`" };
        URI_CHARS_REPLACED_WITH = new String[] { "%20", "%7b", "%7d", "%7c", "%5c", "%5e", "%5b", "%5d", "%60" };
        CH_ZEROS = new char[] { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };
    }
}
