package org.glassfish.jersey.uri;

import java.util.LinkedList;
import javax.ws.rs.core.PathSegment;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.nio.charset.Charset;

public class UriComponent
{
    private static final char[] HEX_DIGITS;
    private static final String[] SCHEME;
    private static final String[] UNRESERVED;
    private static final String[] SUB_DELIMS;
    private static final boolean[][] ENCODING_TABLES;
    private static final Charset UTF_8_CHARSET;
    private static final int[] HEX_TABLE;
    
    private UriComponent() {
    }
    
    public static void validate(final String s, final Type t) {
        validate(s, t, false);
    }
    
    public static void validate(final String s, final Type t, final boolean template) {
        final int i = _valid(s, t, template);
        if (i > -1) {
            throw new IllegalArgumentException(LocalizationMessages.URI_COMPONENT_INVALID_CHARACTER(s, t, s.charAt(i), i));
        }
    }
    
    public static boolean valid(final String s, final Type t) {
        return valid(s, t, false);
    }
    
    public static boolean valid(final String s, final Type t, final boolean template) {
        return _valid(s, t, template) == -1;
    }
    
    private static int _valid(final String s, final Type t, final boolean template) {
        final boolean[] table = UriComponent.ENCODING_TABLES[t.ordinal()];
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (((c < '\u0080' && c != '%' && !table[c]) || c >= '\u0080') && (!template || (c != '{' && c != '}'))) {
                return i;
            }
        }
        return -1;
    }
    
    public static String contextualEncode(final String s, final Type t) {
        return _encode(s, t, false, true);
    }
    
    public static String contextualEncode(final String s, final Type t, final boolean template) {
        return _encode(s, t, template, true);
    }
    
    public static String encode(final String s, final Type t) {
        return _encode(s, t, false, false);
    }
    
    public static String encode(final String s, final Type t, final boolean template) {
        return _encode(s, t, template, false);
    }
    
    public static String encodeTemplateNames(String s) {
        int i = s.indexOf(123);
        if (i != -1) {
            s = s.replace("{", "%7B");
        }
        i = s.indexOf(125);
        if (i != -1) {
            s = s.replace("}", "%7D");
        }
        return s;
    }
    
    private static String _encode(final String s, final Type t, final boolean template, final boolean contextualEncode) {
        final boolean[] table = UriComponent.ENCODING_TABLES[t.ordinal()];
        boolean insideTemplateParam = false;
        StringBuilder sb = null;
        int codePoint;
        for (int offset = 0; offset < s.length(); offset += Character.charCount(codePoint)) {
            codePoint = s.codePointAt(offset);
            if (codePoint < 128 && table[codePoint]) {
                if (sb != null) {
                    sb.append((char)codePoint);
                }
            }
            else {
                if (template) {
                    boolean leavingTemplateParam = false;
                    if (codePoint == 123) {
                        insideTemplateParam = true;
                    }
                    else if (codePoint == 125) {
                        insideTemplateParam = false;
                        leavingTemplateParam = true;
                    }
                    if (insideTemplateParam || leavingTemplateParam) {
                        if (sb != null) {
                            sb.append(Character.toChars(codePoint));
                        }
                        continue;
                    }
                }
                if (contextualEncode && codePoint == 37 && offset + 2 < s.length() && isHexCharacter(s.charAt(offset + 1)) && isHexCharacter(s.charAt(offset + 2))) {
                    if (sb != null) {
                        sb.append('%').append(s.charAt(offset + 1)).append(s.charAt(offset + 2));
                    }
                    offset += 2;
                }
                else {
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(s.substring(0, offset));
                    }
                    if (codePoint < 128) {
                        if (codePoint == 32 && t == Type.QUERY_PARAM) {
                            sb.append('+');
                        }
                        else {
                            appendPercentEncodedOctet(sb, (char)codePoint);
                        }
                    }
                    else {
                        appendUTF8EncodedCharacter(sb, codePoint);
                    }
                }
            }
        }
        return (sb == null) ? s : sb.toString();
    }
    
    private static void appendPercentEncodedOctet(final StringBuilder sb, final int b) {
        sb.append('%');
        sb.append(UriComponent.HEX_DIGITS[b >> 4]);
        sb.append(UriComponent.HEX_DIGITS[b & 0xF]);
    }
    
    private static void appendUTF8EncodedCharacter(final StringBuilder sb, final int codePoint) {
        final CharBuffer chars = CharBuffer.wrap(Character.toChars(codePoint));
        final ByteBuffer bytes = UriComponent.UTF_8_CHARSET.encode(chars);
        while (bytes.hasRemaining()) {
            appendPercentEncodedOctet(sb, bytes.get() & 0xFF);
        }
    }
    
    private static boolean[][] initEncodingTables() {
        final boolean[][] tables = new boolean[Type.values().length][];
        final List<String> l = new ArrayList<String>();
        l.addAll(Arrays.asList(UriComponent.SCHEME));
        tables[Type.SCHEME.ordinal()] = initEncodingTable(l);
        l.clear();
        l.addAll(Arrays.asList(UriComponent.UNRESERVED));
        tables[Type.UNRESERVED.ordinal()] = initEncodingTable(l);
        l.addAll(Arrays.asList(UriComponent.SUB_DELIMS));
        tables[Type.HOST.ordinal()] = initEncodingTable(l);
        tables[Type.PORT.ordinal()] = initEncodingTable(Arrays.asList("0-9"));
        l.add(":");
        tables[Type.USER_INFO.ordinal()] = initEncodingTable(l);
        l.add("@");
        tables[Type.AUTHORITY.ordinal()] = initEncodingTable(l);
        (tables[Type.PATH_SEGMENT.ordinal()] = initEncodingTable(l))[59] = false;
        (tables[Type.MATRIX_PARAM.ordinal()] = tables[Type.PATH_SEGMENT.ordinal()].clone())[61] = false;
        l.add("/");
        tables[Type.PATH.ordinal()] = initEncodingTable(l);
        (tables[Type.QUERY.ordinal()] = initEncodingTable(l))[33] = false;
        tables[Type.QUERY.ordinal()][42] = false;
        tables[Type.QUERY.ordinal()][39] = false;
        tables[Type.QUERY.ordinal()][40] = false;
        tables[Type.QUERY.ordinal()][41] = false;
        tables[Type.QUERY.ordinal()][59] = false;
        tables[Type.QUERY.ordinal()][58] = false;
        tables[Type.QUERY.ordinal()][64] = false;
        tables[Type.QUERY.ordinal()][36] = false;
        tables[Type.QUERY.ordinal()][44] = false;
        tables[Type.QUERY.ordinal()][47] = false;
        tables[Type.QUERY.ordinal()][63] = false;
        (tables[Type.QUERY_PARAM.ordinal()] = Arrays.copyOf(tables[Type.QUERY.ordinal()], tables[Type.QUERY.ordinal()].length))[61] = false;
        tables[Type.QUERY_PARAM.ordinal()][43] = false;
        tables[Type.QUERY_PARAM.ordinal()][38] = false;
        tables[Type.QUERY_PARAM_SPACE_ENCODED.ordinal()] = tables[Type.QUERY_PARAM.ordinal()];
        tables[Type.FRAGMENT.ordinal()] = tables[Type.QUERY.ordinal()];
        return tables;
    }
    
    private static boolean[] initEncodingTable(final List<String> allowed) {
        final boolean[] table = new boolean[128];
        for (final String range : allowed) {
            if (range.length() == 1) {
                table[range.charAt(0)] = true;
            }
            else {
                if (range.length() != 3 || range.charAt(1) != '-') {
                    continue;
                }
                for (int i = range.charAt(0); i <= range.charAt(2); ++i) {
                    table[i] = true;
                }
            }
        }
        return table;
    }
    
    public static String decode(final String s, final Type t) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        final int n = s.length();
        if (n == 0) {
            return s;
        }
        if (s.indexOf(37) < 0) {
            if (t != Type.QUERY_PARAM) {
                return s;
            }
            if (s.indexOf(43) < 0) {
                return s;
            }
        }
        else {
            if (n < 2) {
                throw new IllegalArgumentException(LocalizationMessages.URI_COMPONENT_ENCODED_OCTET_MALFORMED(1));
            }
            if (s.charAt(n - 2) == '%') {
                throw new IllegalArgumentException(LocalizationMessages.URI_COMPONENT_ENCODED_OCTET_MALFORMED(n - 2));
            }
        }
        if (t == null) {
            return decode(s, n);
        }
        switch (t) {
            case HOST: {
                return decodeHost(s, n);
            }
            case QUERY_PARAM: {
                return decodeQueryParam(s, n);
            }
            default: {
                return decode(s, n);
            }
        }
    }
    
    public static MultivaluedMap<String, String> decodeQuery(final URI u, final boolean decode) {
        return decodeQuery(u.getRawQuery(), decode);
    }
    
    public static MultivaluedMap<String, String> decodeQuery(final String q, final boolean decode) {
        return decodeQuery(q, true, decode);
    }
    
    public static MultivaluedMap<String, String> decodeQuery(final String q, final boolean decodeNames, final boolean decodeValues) {
        final MultivaluedMap<String, String> queryParameters = (MultivaluedMap<String, String>)new MultivaluedStringMap();
        if (q == null || q.length() == 0) {
            return queryParameters;
        }
        int s = 0;
        do {
            final int e = q.indexOf(38, s);
            if (e == -1) {
                decodeQueryParam(queryParameters, q.substring(s), decodeNames, decodeValues);
            }
            else if (e > s) {
                decodeQueryParam(queryParameters, q.substring(s, e), decodeNames, decodeValues);
            }
            s = e + 1;
        } while (s > 0 && s < q.length());
        return queryParameters;
    }
    
    private static void decodeQueryParam(final MultivaluedMap<String, String> params, final String param, final boolean decodeNames, final boolean decodeValues) {
        try {
            final int equals = param.indexOf(61);
            if (equals > 0) {
                params.add((Object)(decodeNames ? URLDecoder.decode(param.substring(0, equals), "UTF-8") : param.substring(0, equals)), (Object)(decodeValues ? URLDecoder.decode(param.substring(equals + 1), "UTF-8") : param.substring(equals + 1)));
            }
            else if (equals != 0) {
                if (param.length() > 0) {
                    params.add((Object)(decodeNames ? URLDecoder.decode(param, "UTF-8") : param), (Object)"");
                }
            }
        }
        catch (final UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static List<PathSegment> decodePath(final URI u, final boolean decode) {
        String rawPath = u.getRawPath();
        if (rawPath != null && rawPath.length() > 0 && rawPath.charAt(0) == '/') {
            rawPath = rawPath.substring(1);
        }
        return decodePath(rawPath, decode);
    }
    
    public static List<PathSegment> decodePath(final String path, final boolean decode) {
        final List<PathSegment> segments = new LinkedList<PathSegment>();
        if (path == null) {
            return segments;
        }
        int e = -1;
        int s;
        do {
            s = e + 1;
            e = path.indexOf(47, s);
            if (e > s) {
                decodePathSegment(segments, path.substring(s, e), decode);
            }
            else {
                if (e != s) {
                    continue;
                }
                segments.add(PathSegmentImpl.EMPTY_PATH_SEGMENT);
            }
        } while (e != -1);
        if (s < path.length()) {
            decodePathSegment(segments, path.substring(s), decode);
        }
        else {
            segments.add(PathSegmentImpl.EMPTY_PATH_SEGMENT);
        }
        return segments;
    }
    
    public static void decodePathSegment(final List<PathSegment> segments, final String segment, final boolean decode) {
        final int colon = segment.indexOf(59);
        if (colon != -1) {
            segments.add((PathSegment)new PathSegmentImpl((colon == 0) ? "" : segment.substring(0, colon), decode, decodeMatrix(segment, decode)));
        }
        else {
            segments.add((PathSegment)new PathSegmentImpl(segment, decode));
        }
    }
    
    public static MultivaluedMap<String, String> decodeMatrix(final String pathSegment, final boolean decode) {
        final MultivaluedMap<String, String> matrixMap = (MultivaluedMap<String, String>)new MultivaluedStringMap();
        int s = pathSegment.indexOf(59) + 1;
        if (s == 0 || s == pathSegment.length()) {
            return matrixMap;
        }
        do {
            final int e = pathSegment.indexOf(59, s);
            if (e == -1) {
                decodeMatrixParam(matrixMap, pathSegment.substring(s), decode);
            }
            else if (e > s) {
                decodeMatrixParam(matrixMap, pathSegment.substring(s, e), decode);
            }
            s = e + 1;
        } while (s > 0 && s < pathSegment.length());
        return matrixMap;
    }
    
    private static void decodeMatrixParam(final MultivaluedMap<String, String> params, final String param, final boolean decode) {
        final int equals = param.indexOf(61);
        if (equals > 0) {
            params.add((Object)decode(param.substring(0, equals), Type.MATRIX_PARAM), (Object)(decode ? decode(param.substring(equals + 1), Type.MATRIX_PARAM) : param.substring(equals + 1)));
        }
        else if (equals != 0) {
            if (param.length() > 0) {
                params.add((Object)decode(param, Type.MATRIX_PARAM), (Object)"");
            }
        }
    }
    
    private static String decode(final String s, final int n) {
        final StringBuilder sb = new StringBuilder(n);
        ByteBuffer bb = null;
        int i = 0;
        while (i < n) {
            final char c = s.charAt(i++);
            if (c != '%') {
                sb.append(c);
            }
            else {
                bb = decodePercentEncodedOctets(s, i, bb);
                i = decodeOctets(i, bb, sb);
            }
        }
        return sb.toString();
    }
    
    private static String decodeQueryParam(final String s, final int n) {
        final StringBuilder sb = new StringBuilder(n);
        ByteBuffer bb = null;
        int i = 0;
        while (i < n) {
            final char c = s.charAt(i++);
            if (c != '%') {
                if (c != '+') {
                    sb.append(c);
                }
                else {
                    sb.append(' ');
                }
            }
            else {
                bb = decodePercentEncodedOctets(s, i, bb);
                i = decodeOctets(i, bb, sb);
            }
        }
        return sb.toString();
    }
    
    private static String decodeHost(final String s, final int n) {
        final StringBuilder sb = new StringBuilder(n);
        ByteBuffer bb = null;
        boolean betweenBrackets = false;
        int i = 0;
        while (i < n) {
            final char c = s.charAt(i++);
            if (c == '[') {
                betweenBrackets = true;
            }
            else if (betweenBrackets && c == ']') {
                betweenBrackets = false;
            }
            if (c != '%' || betweenBrackets) {
                sb.append(c);
            }
            else {
                bb = decodePercentEncodedOctets(s, i, bb);
                i = decodeOctets(i, bb, sb);
            }
        }
        return sb.toString();
    }
    
    private static ByteBuffer decodePercentEncodedOctets(final String s, int i, ByteBuffer bb) {
        if (bb == null) {
            bb = ByteBuffer.allocate(1);
        }
        else {
            bb.clear();
        }
        while (true) {
            bb.put((byte)(decodeHex(s, i++) << 4 | decodeHex(s, i++)));
            if (i == s.length()) {
                break;
            }
            if (s.charAt(i++) != '%') {
                break;
            }
            if (bb.position() != bb.capacity()) {
                continue;
            }
            bb.flip();
            final ByteBuffer bb_new = ByteBuffer.allocate(s.length() / 3);
            bb_new.put(bb);
            bb = bb_new;
        }
        bb.flip();
        return bb;
    }
    
    private static int decodeOctets(final int i, final ByteBuffer bb, final StringBuilder sb) {
        if (bb.limit() == 1 && (bb.get(0) & 0xFF) < 128) {
            sb.append((char)bb.get(0));
            return i + 2;
        }
        final CharBuffer cb = UriComponent.UTF_8_CHARSET.decode(bb);
        sb.append(cb.toString());
        return i + bb.limit() * 3 - 1;
    }
    
    private static int decodeHex(final String s, final int i) {
        final int v = decodeHex(s.charAt(i));
        if (v == -1) {
            throw new IllegalArgumentException(LocalizationMessages.URI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(i, s.charAt(i)));
        }
        return v;
    }
    
    private static int[] initHexTable() {
        final int[] table = new int[128];
        Arrays.fill(table, -1);
        for (char c = '0'; c <= '9'; ++c) {
            table[c] = c - '0';
        }
        for (char c = 'A'; c <= 'F'; ++c) {
            table[c] = c - 'A' + 10;
        }
        for (char c = 'a'; c <= 'f'; ++c) {
            table[c] = c - 'a' + 10;
        }
        return table;
    }
    
    private static int decodeHex(final char c) {
        return (c < '\u0080') ? UriComponent.HEX_TABLE[c] : -1;
    }
    
    public static boolean isHexCharacter(final char c) {
        return c < '\u0080' && UriComponent.HEX_TABLE[c] != -1;
    }
    
    public static String fullRelativeUri(final URI uri) {
        if (uri == null) {
            return null;
        }
        final String query = uri.getRawQuery();
        return uri.getRawPath() + ((query != null && query.length() > 0) ? ("?" + query) : "");
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        SCHEME = new String[] { "0-9", "A-Z", "a-z", "+", "-", "." };
        UNRESERVED = new String[] { "0-9", "A-Z", "a-z", "-", ".", "_", "~" };
        SUB_DELIMS = new String[] { "!", "$", "&", "'", "(", ")", "*", "+", ",", ";", "=" };
        ENCODING_TABLES = initEncodingTables();
        UTF_8_CHARSET = Charset.forName("UTF-8");
        HEX_TABLE = initHexTable();
    }
    
    public enum Type
    {
        UNRESERVED, 
        SCHEME, 
        AUTHORITY, 
        USER_INFO, 
        HOST, 
        PORT, 
        PATH, 
        PATH_SEGMENT, 
        MATRIX_PARAM, 
        QUERY, 
        QUERY_PARAM, 
        QUERY_PARAM_SPACE_ENCODED, 
        FRAGMENT;
    }
    
    private static final class PathSegmentImpl implements PathSegment
    {
        private static final PathSegment EMPTY_PATH_SEGMENT;
        private final String path;
        private final MultivaluedMap<String, String> matrixParameters;
        
        PathSegmentImpl(final String path, final boolean decode) {
            this(path, decode, (MultivaluedMap<String, String>)new MultivaluedStringMap());
        }
        
        PathSegmentImpl(final String path, final boolean decode, final MultivaluedMap<String, String> matrixParameters) {
            this.path = (decode ? UriComponent.decode(path, Type.PATH_SEGMENT) : path);
            this.matrixParameters = matrixParameters;
        }
        
        public String getPath() {
            return this.path;
        }
        
        public MultivaluedMap<String, String> getMatrixParameters() {
            return this.matrixParameters;
        }
        
        @Override
        public String toString() {
            return this.path;
        }
        
        static {
            EMPTY_PATH_SEGMENT = (PathSegment)new PathSegmentImpl("", false);
        }
    }
}
