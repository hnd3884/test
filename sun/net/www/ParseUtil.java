package sun.net.www;

import java.nio.charset.CharacterCodingException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import sun.nio.cs.ThreadLocalCoders;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.io.File;
import java.util.BitSet;

public class ParseUtil
{
    static BitSet encodedInPath;
    private static final char[] hexDigits;
    private static final long L_DIGIT;
    private static final long H_DIGIT = 0L;
    private static final long L_HEX;
    private static final long H_HEX;
    private static final long L_UPALPHA = 0L;
    private static final long H_UPALPHA;
    private static final long L_LOWALPHA = 0L;
    private static final long H_LOWALPHA;
    private static final long L_ALPHA = 0L;
    private static final long H_ALPHA;
    private static final long L_ALPHANUM;
    private static final long H_ALPHANUM;
    private static final long L_MARK;
    private static final long H_MARK;
    private static final long L_UNRESERVED;
    private static final long H_UNRESERVED;
    private static final long L_RESERVED;
    private static final long H_RESERVED;
    private static final long L_ESCAPED = 1L;
    private static final long H_ESCAPED = 0L;
    private static final long L_DASH;
    private static final long H_DASH;
    private static final long L_URIC;
    private static final long H_URIC;
    private static final long L_PCHAR;
    private static final long H_PCHAR;
    private static final long L_PATH;
    private static final long H_PATH;
    private static final long L_USERINFO;
    private static final long H_USERINFO;
    private static final long L_REG_NAME;
    private static final long H_REG_NAME;
    private static final long L_SERVER;
    private static final long H_SERVER;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public static String encodePath(final String s) {
        return encodePath(s, true);
    }
    
    public static String encodePath(final String s, final boolean b) {
        char[] array = new char[s.length() * 2 + 16];
        int n = 0;
        final char[] charArray = s.toCharArray();
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char c = charArray[i];
            if ((!b && c == '/') || (b && c == File.separatorChar)) {
                array[n++] = '/';
            }
            else if (c <= '\u007f') {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    array[n++] = c;
                }
                else if (ParseUtil.encodedInPath.get(c)) {
                    n = escape(array, c, n);
                }
                else {
                    array[n++] = c;
                }
            }
            else if (c > '\u07ff') {
                n = escape(array, (char)(0x80 | (c >> 0 & 0x3F)), escape(array, (char)(0x80 | (c >> 6 & 0x3F)), escape(array, (char)(0xE0 | (c >> 12 & 0xF)), n)));
            }
            else {
                n = escape(array, (char)(0x80 | (c >> 0 & 0x3F)), escape(array, (char)(0xC0 | (c >> 6 & 0x1F)), n));
            }
            if (n + 9 > array.length) {
                int n2 = array.length * 2 + 16;
                if (n2 < 0) {
                    n2 = Integer.MAX_VALUE;
                }
                final char[] array2 = new char[n2];
                System.arraycopy(array, 0, array2, 0, n);
                array = array2;
            }
        }
        return new String(array, 0, n);
    }
    
    private static int escape(final char[] array, final char c, int n) {
        array[n++] = '%';
        array[n++] = Character.forDigit(c >> 4 & 0xF, 16);
        array[n++] = Character.forDigit(c & '\u000f', 16);
        return n;
    }
    
    private static byte unescape(final String s, final int n) {
        return (byte)Integer.parseInt(s.substring(n + 1, n + 3), 16);
    }
    
    public static String decode(final String s) {
        final int length = s.length();
        if (length == 0 || s.indexOf(37) < 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(length);
        final ByteBuffer allocate = ByteBuffer.allocate(length);
        final CharBuffer allocate2 = CharBuffer.allocate(length);
        final CharsetDecoder onUnmappableCharacter = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        char c = s.charAt(0);
        int i = 0;
    Label_0069:
        while (i < length) {
            assert c == s.charAt(i);
            if (c == '%') {
                allocate.clear();
                while (ParseUtil.$assertionsDisabled || length - i >= 2) {
                    try {
                        allocate.put(unescape(s, i));
                    }
                    catch (final NumberFormatException ex) {
                        throw new IllegalArgumentException();
                    }
                    i += 3;
                    if (i < length) {
                        c = s.charAt(i);
                        if (c == '%') {
                            continue;
                        }
                    }
                    allocate.flip();
                    allocate2.clear();
                    onUnmappableCharacter.reset();
                    if (onUnmappableCharacter.decode(allocate, allocate2, true).isError()) {
                        throw new IllegalArgumentException("Error decoding percent encoded characters");
                    }
                    if (onUnmappableCharacter.flush(allocate2).isError()) {
                        throw new IllegalArgumentException("Error decoding percent encoded characters");
                    }
                    sb.append(allocate2.flip().toString());
                    continue Label_0069;
                }
                throw new AssertionError();
            }
            sb.append(c);
            if (++i >= length) {
                break;
            }
            c = s.charAt(i);
        }
        return sb.toString();
    }
    
    public String canonizeString(String s) {
        s.length();
        int index;
        while ((index = s.indexOf("/../")) >= 0) {
            final int lastIndex;
            if ((lastIndex = s.lastIndexOf(47, index - 1)) >= 0) {
                s = s.substring(0, lastIndex) + s.substring(index + 3);
            }
            else {
                s = s.substring(index + 3);
            }
        }
        int index2;
        while ((index2 = s.indexOf("/./")) >= 0) {
            s = s.substring(0, index2) + s.substring(index2 + 2);
        }
        while (s.endsWith("/..")) {
            final int index3 = s.indexOf("/..");
            final int lastIndex2;
            if ((lastIndex2 = s.lastIndexOf(47, index3 - 1)) >= 0) {
                s = s.substring(0, lastIndex2 + 1);
            }
            else {
                s = s.substring(0, index3);
            }
        }
        if (s.endsWith("/.")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
    public static URL fileToEncodedURL(final File file) throws MalformedURLException {
        String s = encodePath(file.getAbsolutePath());
        if (!s.startsWith("/")) {
            s = "/" + s;
        }
        if (!s.endsWith("/") && file.isDirectory()) {
            s += "/";
        }
        return new URL("file", "", s);
    }
    
    public static URI toURI(final URL url) {
        final String protocol = url.getProtocol();
        String s = url.getAuthority();
        String s2 = url.getPath();
        final String query = url.getQuery();
        final String ref = url.getRef();
        if (s2 != null && !s2.startsWith("/")) {
            s2 = "/" + s2;
        }
        if (s != null && s.endsWith(":-1")) {
            s = s.substring(0, s.length() - 3);
        }
        URI uri;
        try {
            uri = createURI(protocol, s, s2, query, ref);
        }
        catch (final URISyntaxException ex) {
            uri = null;
        }
        return uri;
    }
    
    private static URI createURI(final String s, final String s2, final String s3, final String s4, final String s5) throws URISyntaxException {
        final String string = toString(s, null, s2, null, null, -1, s3, s4, s5);
        checkPath(string, s, s3);
        return new URI(string);
    }
    
    private static String toString(final String s, final String s2, final String s3, final String s4, final String s5, final int n, final String s6, final String s7, final String s8) {
        final StringBuffer sb = new StringBuffer();
        if (s != null) {
            sb.append(s);
            sb.append(':');
        }
        appendSchemeSpecificPart(sb, s2, s3, s4, s5, n, s6, s7);
        appendFragment(sb, s8);
        return sb.toString();
    }
    
    private static void appendSchemeSpecificPart(final StringBuffer sb, final String s, final String s2, final String s3, final String s4, final int n, final String s5, final String s6) {
        if (s != null) {
            if (s.startsWith("//[")) {
                final int index = s.indexOf("]");
                if (index != -1 && s.indexOf(":") != -1) {
                    String substring;
                    String substring2;
                    if (index == s.length()) {
                        substring = s;
                        substring2 = "";
                    }
                    else {
                        substring = s.substring(0, index + 1);
                        substring2 = s.substring(index + 1);
                    }
                    sb.append(substring);
                    sb.append(quote(substring2, ParseUtil.L_URIC, ParseUtil.H_URIC));
                }
            }
            else {
                sb.append(quote(s, ParseUtil.L_URIC, ParseUtil.H_URIC));
            }
        }
        else {
            appendAuthority(sb, s2, s3, s4, n);
            if (s5 != null) {
                sb.append(quote(s5, ParseUtil.L_PATH, ParseUtil.H_PATH));
            }
            if (s6 != null) {
                sb.append('?');
                sb.append(quote(s6, ParseUtil.L_URIC, ParseUtil.H_URIC));
            }
        }
    }
    
    private static void appendAuthority(final StringBuffer sb, final String s, final String s2, final String s3, final int n) {
        if (s3 != null) {
            sb.append("//");
            if (s2 != null) {
                sb.append(quote(s2, ParseUtil.L_USERINFO, ParseUtil.H_USERINFO));
                sb.append('@');
            }
            final boolean b = s3.indexOf(58) >= 0 && !s3.startsWith("[") && !s3.endsWith("]");
            if (b) {
                sb.append('[');
            }
            sb.append(s3);
            if (b) {
                sb.append(']');
            }
            if (n != -1) {
                sb.append(':');
                sb.append(n);
            }
        }
        else if (s != null) {
            sb.append("//");
            if (s.startsWith("[")) {
                final int index = s.indexOf("]");
                if (index != -1 && s.indexOf(":") != -1) {
                    String substring;
                    String substring2;
                    if (index == s.length()) {
                        substring = s;
                        substring2 = "";
                    }
                    else {
                        substring = s.substring(0, index + 1);
                        substring2 = s.substring(index + 1);
                    }
                    sb.append(substring);
                    sb.append(quote(substring2, ParseUtil.L_REG_NAME | ParseUtil.L_SERVER, ParseUtil.H_REG_NAME | ParseUtil.H_SERVER));
                }
            }
            else {
                sb.append(quote(s, ParseUtil.L_REG_NAME | ParseUtil.L_SERVER, ParseUtil.H_REG_NAME | ParseUtil.H_SERVER));
            }
        }
    }
    
    private static void appendFragment(final StringBuffer sb, final String s) {
        if (s != null) {
            sb.append('#');
            sb.append(quote(s, ParseUtil.L_URIC, ParseUtil.H_URIC));
        }
    }
    
    private static String quote(final String s, final long n, final long n2) {
        s.length();
        StringBuffer sb = null;
        final boolean b = (n & 0x1L) != 0x0L;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 < '\u0080') {
                if (!match(char1, n, n2) && !isEscaped(s, i)) {
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append(s.substring(0, i));
                    }
                    appendEscape(sb, (byte)char1);
                }
                else if (sb != null) {
                    sb.append(char1);
                }
            }
            else if (b && (Character.isSpaceChar(char1) || Character.isISOControl(char1))) {
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append(s.substring(0, i));
                }
                appendEncoded(sb, char1);
            }
            else if (sb != null) {
                sb.append(char1);
            }
        }
        return (sb == null) ? s : sb.toString();
    }
    
    private static boolean isEscaped(final String s, final int n) {
        return s != null && s.length() > n + 2 && s.charAt(n) == '%' && match(s.charAt(n + 1), ParseUtil.L_HEX, ParseUtil.H_HEX) && match(s.charAt(n + 2), ParseUtil.L_HEX, ParseUtil.H_HEX);
    }
    
    private static void appendEncoded(final StringBuffer sb, final char c) {
        ByteBuffer encode = null;
        try {
            encode = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap("" + c));
        }
        catch (final CharacterCodingException ex) {
            assert false;
        }
        while (encode.hasRemaining()) {
            final int n = encode.get() & 0xFF;
            if (n >= 128) {
                appendEscape(sb, (byte)n);
            }
            else {
                sb.append((char)n);
            }
        }
    }
    
    private static void appendEscape(final StringBuffer sb, final byte b) {
        sb.append('%');
        sb.append(ParseUtil.hexDigits[b >> 4 & 0xF]);
        sb.append(ParseUtil.hexDigits[b >> 0 & 0xF]);
    }
    
    private static boolean match(final char c, final long n, final long n2) {
        if (c < '@') {
            return (1L << c & n) != 0x0L;
        }
        return c < '\u0080' && (1L << c - '@' & n2) != 0x0L;
    }
    
    private static void checkPath(final String s, final String s2, final String s3) throws URISyntaxException {
        if (s2 != null && s3 != null && s3.length() > 0 && s3.charAt(0) != '/') {
            throw new URISyntaxException(s, "Relative path in absolute URI");
        }
    }
    
    private static long lowMask(final char c, final char c2) {
        long n = 0L;
        final int max = Math.max(Math.min(c, 63), 0);
        for (int max2 = Math.max(Math.min(c2, 63), 0), i = max; i <= max2; ++i) {
            n |= 1L << i;
        }
        return n;
    }
    
    private static long lowMask(final String s) {
        final int length = s.length();
        long n = 0L;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 < '@') {
                n |= 1L << char1;
            }
        }
        return n;
    }
    
    private static long highMask(final char c, final char c2) {
        long n = 0L;
        final int n2 = Math.max(Math.min(c, 127), 64) - 64;
        for (int n3 = Math.max(Math.min(c2, 127), 64) - 64, i = n2; i <= n3; ++i) {
            n |= 1L << i;
        }
        return n;
    }
    
    private static long highMask(final String s) {
        final int length = s.length();
        long n = 0L;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= '@' && char1 < '\u0080') {
                n |= 1L << char1 - '@';
            }
        }
        return n;
    }
    
    static {
        (ParseUtil.encodedInPath = new BitSet(256)).set(61);
        ParseUtil.encodedInPath.set(59);
        ParseUtil.encodedInPath.set(63);
        ParseUtil.encodedInPath.set(47);
        ParseUtil.encodedInPath.set(35);
        ParseUtil.encodedInPath.set(32);
        ParseUtil.encodedInPath.set(60);
        ParseUtil.encodedInPath.set(62);
        ParseUtil.encodedInPath.set(37);
        ParseUtil.encodedInPath.set(34);
        ParseUtil.encodedInPath.set(123);
        ParseUtil.encodedInPath.set(125);
        ParseUtil.encodedInPath.set(124);
        ParseUtil.encodedInPath.set(92);
        ParseUtil.encodedInPath.set(94);
        ParseUtil.encodedInPath.set(91);
        ParseUtil.encodedInPath.set(93);
        ParseUtil.encodedInPath.set(96);
        for (int i = 0; i < 32; ++i) {
            ParseUtil.encodedInPath.set(i);
        }
        ParseUtil.encodedInPath.set(127);
        hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        L_DIGIT = lowMask('0', '9');
        L_HEX = ParseUtil.L_DIGIT;
        H_HEX = (highMask('A', 'F') | highMask('a', 'f'));
        H_UPALPHA = highMask('A', 'Z');
        H_LOWALPHA = highMask('a', 'z');
        H_ALPHA = (ParseUtil.H_LOWALPHA | ParseUtil.H_UPALPHA);
        L_ALPHANUM = (ParseUtil.L_DIGIT | 0x0L);
        H_ALPHANUM = (0x0L | ParseUtil.H_ALPHA);
        L_MARK = lowMask("-_.!~*'()");
        H_MARK = highMask("-_.!~*'()");
        L_UNRESERVED = (ParseUtil.L_ALPHANUM | ParseUtil.L_MARK);
        H_UNRESERVED = (ParseUtil.H_ALPHANUM | ParseUtil.H_MARK);
        L_RESERVED = lowMask(";/?:@&=+$,[]");
        H_RESERVED = highMask(";/?:@&=+$,[]");
        L_DASH = lowMask("-");
        H_DASH = highMask("-");
        L_URIC = (ParseUtil.L_RESERVED | ParseUtil.L_UNRESERVED | 0x1L);
        H_URIC = (ParseUtil.H_RESERVED | ParseUtil.H_UNRESERVED | 0x0L);
        L_PCHAR = (ParseUtil.L_UNRESERVED | 0x1L | lowMask(":@&=+$,"));
        H_PCHAR = (ParseUtil.H_UNRESERVED | 0x0L | highMask(":@&=+$,"));
        L_PATH = (ParseUtil.L_PCHAR | lowMask(";/"));
        H_PATH = (ParseUtil.H_PCHAR | highMask(";/"));
        L_USERINFO = (ParseUtil.L_UNRESERVED | 0x1L | lowMask(";:&=+$,"));
        H_USERINFO = (ParseUtil.H_UNRESERVED | 0x0L | highMask(";:&=+$,"));
        L_REG_NAME = (ParseUtil.L_UNRESERVED | 0x1L | lowMask("$,;:@&=+"));
        H_REG_NAME = (ParseUtil.H_UNRESERVED | 0x0L | highMask("$,;:@&=+"));
        L_SERVER = (ParseUtil.L_USERINFO | ParseUtil.L_ALPHANUM | ParseUtil.L_DASH | lowMask(".:@[]"));
        H_SERVER = (ParseUtil.H_USERINFO | ParseUtil.H_ALPHANUM | ParseUtil.H_DASH | highMask(".:@[]"));
    }
}
