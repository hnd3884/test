package com.adventnet.iam.xss;

import org.owasp.esapi.codecs.MySQLCodec;
import org.json.JSONException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.adventnet.iam.security.SecurityUtil;
import org.json.JSONArray;
import java.util.Iterator;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.reference.DefaultEncoder;
import java.util.logging.Logger;

public class IAMEncoder
{
    private static final Logger LOGGER;
    private static final String DOUBLE_QUOTE = "\"";
    private static final String NULL_STRING = "null";
    static IAMEncoder encoder;
    static DefaultEncoder defaultEncoder;
    public static Codec defaultDBCodec;
    public static Codec ansi_codec;
    
    public static IAMEncoder encoder() {
        if (IAMEncoder.encoder == null) {
            IAMEncoder.encoder = new IAMEncoder();
        }
        return IAMEncoder.encoder;
    }
    
    public IAMEncoder() {
        final List<String> codecsList = new ArrayList<String>();
        codecsList.add("PercentCodec");
        IAMEncoder.defaultEncoder = new DefaultEncoder((List)codecsList);
    }
    
    public String canonicalize(final String input) {
        if (input == null) {
            return null;
        }
        try {
            return IAMEncoder.defaultEncoder.canonicalize(input);
        }
        catch (final Throwable e) {
            IAMEncoder.LOGGER.log(Level.FINE, "Canonicalize failed : {0}", e);
            return null;
        }
    }
    
    public static String encodeHTML(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForHTML(data);
    }
    
    public static String encodeHTMLAttribute(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForHTMLAttribute(data);
    }
    
    public static String encodeJavaScript(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForJavaScript(data);
    }
    
    public static String encodeJavaScript(final JSONObject json) {
        if (json == null) {
            return "null";
        }
        try {
            final Iterator<String> keys = json.keys();
            final StringBuffer sb = new StringBuffer("{");
            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                final String name = keys.next();
                if (name == null) {
                    sb.append("null");
                }
                else {
                    sb.append("\"").append(encodeJavaScript(name)).append("\"");
                }
                sb.append(':');
                sb.append(jsonValueToString(json.opt(name)));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static String encodeJavaScript(final JSONArray json) {
        if (json == null) {
            return "null";
        }
        try {
            final StringBuffer sb = new StringBuffer("[");
            for (int limit = json.length(), i = 0; i < limit; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(jsonValueToString(json.opt(i)));
            }
            sb.append(']');
            return sb.toString();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static String encodeCSS(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForCSS(data);
    }
    
    public static String encodeURL(final String data) throws Exception {
        return encodeURL(data, true);
    }
    
    public static String encodeURL(String data, final boolean isApplicationXwwwURLEncoded) throws Exception {
        if (SecurityUtil.isValid(data)) {
            String s;
            if (isApplicationXwwwURLEncoded) {
                encoder();
                s = IAMEncoder.defaultEncoder.encodeForURL(data);
            }
            else {
                encoder();
                s = IAMEncoder.defaultEncoder.encodeForURL(data).replaceAll("\\+", "%20");
            }
            data = s;
        }
        return data;
    }
    
    public static String encodeHeader(final String data) throws Exception {
        return encodeChars(data);
    }
    
    private static String encodeChars(final String input) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (c > ' ' && c < '\u007f') {
                sb.append(c);
            }
            else {
                String str;
                try {
                    str = URLEncoder.encode(String.valueOf(c), "UTF-8");
                }
                catch (final UnsupportedEncodingException e) {
                    str = " ";
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }
    
    public static String encodeVBScript(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForVBScript(data);
    }
    
    public static String encodeSQL(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForSQL(IAMEncoder.defaultDBCodec, data);
    }
    
    public static String encodeSQLForNonPatternContext(final String data) {
        encoder();
        return IAMEncoder.defaultEncoder.encodeForSQL(IAMEncoder.ansi_codec, data);
    }
    
    public static String safeEncodeHTML(final String data) throws Exception {
        if (isOutputEncodeRequired(data, "html_escape_pat")) {
            encoder();
            return IAMEncoder.defaultEncoder.encodeForHTML(data);
        }
        return data;
    }
    
    public static String safeEncodeHTMLAttribute(final String data) throws Exception {
        if (isOutputEncodeRequired(data, "html_attribute_escape_pat")) {
            encoder();
            return IAMEncoder.defaultEncoder.encodeForHTMLAttribute(data);
        }
        return data;
    }
    
    public static String safeEncodeJavaScript(final String data) throws Exception {
        if (isOutputEncodeRequired(data, "javascript_escape_pat")) {
            encoder();
            return IAMEncoder.defaultEncoder.encodeForJavaScript(data);
        }
        return data;
    }
    
    public static String safeEncodeCSS(final String data) throws Exception {
        if (isOutputEncodeRequired(data, "css_escape_pat")) {
            encoder();
            return IAMEncoder.defaultEncoder.encodeForCSS(data);
        }
        return data;
    }
    
    private static boolean isOutputEncodeRequired(final String data, final String patternName) throws Exception {
        if (!SecurityUtil.isValid(data)) {
            return false;
        }
        IAMEncoder.LOGGER.log(Level.FINE, "Output Encode");
        if (SecurityFilterProperties.getCommonRegexPattern("cleartextpattern").matcher(data).matches()) {
            IAMEncoder.LOGGER.log(Level.FINE, "XSS DETECT/FILTERING NOT NEEDED AS CLEARTEXT PARAM : {0} ", data);
            return false;
        }
        final Pattern encodeCheckPattern = SecurityFilterProperties.getCommonRegexPattern(patternName);
        if (encodeCheckPattern == null) {
            IAMEncoder.LOGGER.log(Level.SEVERE, "Pattern not defined in security-common.xml file . Pattern name : ", patternName);
            throw new IAMSecurityException("PATTERN_NOT_DEFINED");
        }
        return encodeCheckPattern.matcher(data).find();
    }
    
    private static String jsonValueToString(final Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Number) {
            return jsonNumberToString((Number)value);
        }
        if (value instanceof String) {
            return "\"" + encodeJavaScript(value.toString()) + "\"";
        }
        if (value instanceof JSONObject) {
            return encodeJavaScript((JSONObject)value);
        }
        if (value instanceof JSONArray) {
            return encodeJavaScript((JSONArray)value);
        }
        if (value instanceof Map) {
            return encodeJavaScript(new JSONObject((Map)value));
        }
        if (value instanceof Collection) {
            return encodeJavaScript(new JSONArray((Collection)value));
        }
        if (value.getClass().isArray()) {
            return encodeJavaScript(new JSONArray(value));
        }
        return "\"" + encodeJavaScript(value.toString()) + "\"";
    }
    
    private static String jsonNumberToString(final Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        if (n instanceof Double) {
            if (((Double)n).isInfinite() || ((Double)n).isNaN()) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }
        else if (n instanceof Float && (((Float)n).isInfinite() || ((Float)n).isNaN())) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
        String s = n.toString();
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    static {
        LOGGER = Logger.getLogger(IAMEncoder.class.getName());
        IAMEncoder.encoder = null;
        IAMEncoder.defaultEncoder = null;
        IAMEncoder.defaultDBCodec = (Codec)new MySQLCodec(MySQLCodec.Mode.STANDARD);
        IAMEncoder.ansi_codec = (Codec)new MySQLCodec(MySQLCodec.Mode.ANSI);
    }
}
