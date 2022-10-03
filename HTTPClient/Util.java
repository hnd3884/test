package HTTPClient;

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.net.URL;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.BitSet;

public class Util
{
    private static final BitSet Separators;
    private static final BitSet TokenChar;
    private static final BitSet UnsafeChar;
    private static DateFormat http_format;
    private static DateFormat parse_1123;
    private static DateFormat parse_850;
    private static DateFormat parse_asctime;
    private static final Object http_format_lock;
    private static final Object http_parse_lock;
    static final char[] hex_map;
    
    private Util() {
    }
    
    static final Object[] resizeArray(final Object[] src, final int new_size) {
        final Class compClass = src.getClass().getComponentType();
        final Object[] tmp = (Object[])Array.newInstance(compClass, new_size);
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final NVPair[] resizeArray(final NVPair[] src, final int new_size) {
        final NVPair[] tmp = new NVPair[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final AuthorizationInfo[] resizeArray(final AuthorizationInfo[] src, final int new_size) {
        final AuthorizationInfo[] tmp = new AuthorizationInfo[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final Cookie[] resizeArray(final Cookie[] src, final int new_size) {
        final Cookie[] tmp = new Cookie[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final String[] resizeArray(final String[] src, final int new_size) {
        final String[] tmp = new String[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final boolean[] resizeArray(final boolean[] src, final int new_size) {
        final boolean[] tmp = new boolean[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final byte[] resizeArray(final byte[] src, final int new_size) {
        final byte[] tmp = new byte[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final char[] resizeArray(final char[] src, final int new_size) {
        final char[] tmp = new char[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static final int[] resizeArray(final int[] src, final int new_size) {
        final int[] tmp = new int[new_size];
        System.arraycopy(src, 0, tmp, 0, (src.length < new_size) ? src.length : new_size);
        return tmp;
    }
    
    static String[] splitProperty(final String prop) {
        if (prop == null) {
            return new String[0];
        }
        final StringTokenizer tok = new StringTokenizer(prop, "|");
        final String[] list = new String[tok.countTokens()];
        for (int idx = 0; idx < list.length; ++idx) {
            list[idx] = tok.nextToken().trim();
        }
        return list;
    }
    
    static final Hashtable getList(final Hashtable cntxt_list, final Object cntxt) {
        synchronized (cntxt_list) {
            Hashtable list = cntxt_list.get(cntxt);
            if (list == null) {
                list = new Hashtable();
                cntxt_list.put(cntxt, list);
            }
            return list;
        }
    }
    
    static final int[] compile_search(final byte[] search) {
        final int[] cmp = { 0, 1, 0, 1, 0, 1 };
        for (int idx = 0; idx < search.length; ++idx) {
            int end;
            for (end = idx + 1; end < search.length && search[idx] != search[end]; ++end) {}
            if (end < search.length) {
                if (end - idx > cmp[1]) {
                    cmp[4] = cmp[2];
                    cmp[5] = cmp[3];
                    cmp[2] = cmp[0];
                    cmp[3] = cmp[1];
                    cmp[1] = end - (cmp[0] = idx);
                }
                else if (end - idx > cmp[3]) {
                    cmp[4] = cmp[2];
                    cmp[5] = cmp[3];
                    cmp[3] = end - (cmp[2] = idx);
                }
                else if (end - idx > cmp[3]) {
                    cmp[5] = end - (cmp[4] = idx);
                }
            }
        }
        final int[] array = cmp;
        final int n = 1;
        array[n] += cmp[0];
        final int[] array2 = cmp;
        final int n2 = 3;
        array2[n2] += cmp[2];
        final int[] array3 = cmp;
        final int n3 = 5;
        array3[n3] += cmp[4];
        return cmp;
    }
    
    static final int findStr(final byte[] search, final int[] cmp, final byte[] str, int beg, final int end) {
        final int c1f = cmp[0];
        final int c1l = cmp[1];
        final int d1 = c1l - c1f;
        final int c2f = cmp[2];
        final int c2l = cmp[3];
        final int d2 = c2l - c2f;
        final int c3f = cmp[4];
        final int c3l = cmp[5];
        final int d3 = c3l - c3f;
        while (beg + search.length <= end) {
            if (search[c1l] == str[beg + c1l]) {
                if (search[c1f] == str[beg + c1f]) {
                    boolean same = true;
                    for (int idx = 0; idx < search.length; ++idx) {
                        if (search[idx] != str[beg + idx]) {
                            same = false;
                            break;
                        }
                    }
                    if (same) {
                        break;
                    }
                }
                beg += d1;
            }
            else if (search[c2l] == str[beg + c2l]) {
                beg += d2;
            }
            else if (search[c3l] == str[beg + c3l]) {
                beg += d3;
            }
            else {
                ++beg;
            }
        }
        if (beg + search.length > end) {
            return -1;
        }
        return beg;
    }
    
    public static final String dequoteString(final String str) {
        if (str.indexOf(92) == -1) {
            return str;
        }
        final char[] buf = str.toCharArray();
        int pos = 0;
        int num_deq = 0;
        while (pos < buf.length) {
            if (buf[pos] == '\\' && pos + 1 < buf.length) {
                System.arraycopy(buf, pos + 1, buf, pos, buf.length - pos - 1);
                ++num_deq;
            }
            ++pos;
        }
        return new String(buf, 0, buf.length - num_deq);
    }
    
    public static final String quoteString(final String str, final String qlist) {
        char[] list;
        int idx;
        for (list = qlist.toCharArray(), idx = 0; idx < list.length && str.indexOf(list[idx]) == -1; ++idx) {}
        if (idx == list.length) {
            return str;
        }
        int len = str.length();
        char[] buf = new char[len * 2];
        str.getChars(0, len, buf, 0);
        for (int pos = 0; pos < len; ++pos) {
            if (qlist.indexOf(buf[pos], 0) != -1) {
                if (len == buf.length) {
                    buf = resizeArray(buf, len + str.length());
                }
                System.arraycopy(buf, pos, buf, pos + 1, len - pos);
                ++len;
                buf[pos++] = '\\';
            }
        }
        return new String(buf, 0, len);
    }
    
    public static final Vector parseHeader(final String header) throws ParseException {
        return parseHeader(header, true);
    }
    
    public static final Vector parseHeader(final String header, final boolean dequote) throws ParseException {
        if (header == null) {
            return null;
        }
        final char[] buf = header.toCharArray();
        final Vector elems = new Vector();
        boolean first = true;
        int beg = -1;
        int end = 0;
        final int len = buf.length;
        final int[] abeg = { 0 };
        while (true) {
            if (!first) {
                beg = skipSpace(buf, end);
                if (beg == len) {
                    break;
                }
                if (buf[beg] != ',') {
                    throw new ParseException("Bad header format: '" + header + "'\nExpected \",\" at position " + beg);
                }
            }
            first = false;
            beg = skipSpace(buf, beg + 1);
            if (beg == len) {
                break;
            }
            if (buf[beg] == ',') {
                end = beg;
            }
            else {
                if (buf[beg] == '=' || buf[beg] == ';' || buf[beg] == '\"') {
                    throw new ParseException("Bad header format: '" + header + "'\nEmpty element name at position " + beg);
                }
                for (end = beg + 1; end < len && !Character.isWhitespace(buf[end]) && buf[end] != '=' && buf[end] != ',' && buf[end] != ';'; ++end) {}
                final String elem_name = new String(buf, beg, end - beg);
                beg = skipSpace(buf, end);
                String elem_value;
                if (beg < len && buf[beg] == '=') {
                    abeg[0] = beg + 1;
                    elem_value = parseValue(buf, abeg, header, dequote);
                    end = abeg[0];
                }
                else {
                    elem_value = null;
                    end = beg;
                }
                NVPair[] params = new NVPair[0];
                while (true) {
                    beg = skipSpace(buf, end);
                    if (beg == len) {
                        break;
                    }
                    if (buf[beg] != ';') {
                        break;
                    }
                    beg = skipSpace(buf, beg + 1);
                    if (beg == len || buf[beg] == ',') {
                        end = beg;
                        break;
                    }
                    if (buf[beg] == ';') {
                        end = beg;
                    }
                    else {
                        if (buf[beg] == '=' || buf[beg] == '\"') {
                            throw new ParseException("Bad header format: '" + header + "'\nEmpty parameter name at position " + beg);
                        }
                        for (end = beg + 1; end < len && !Character.isWhitespace(buf[end]) && buf[end] != '=' && buf[end] != ',' && buf[end] != ';'; ++end) {}
                        final String param_name = new String(buf, beg, end - beg);
                        beg = skipSpace(buf, end);
                        String param_value;
                        if (beg < len && buf[beg] == '=') {
                            abeg[0] = beg + 1;
                            param_value = parseValue(buf, abeg, header, dequote);
                            end = abeg[0];
                        }
                        else {
                            param_value = null;
                            end = beg;
                        }
                        params = resizeArray(params, params.length + 1);
                        params[params.length - 1] = new NVPair(param_name, param_value);
                    }
                }
                elems.addElement(new HttpHeaderElement(elem_name, elem_value, params));
            }
        }
        return elems;
    }
    
    private static String parseValue(final char[] buf, final int[] abeg, final String header, final boolean dequote) throws ParseException {
        int end;
        int beg = end = abeg[0];
        final int len = buf.length;
        beg = skipSpace(buf, beg);
        String value;
        if (beg < len && buf[beg] == '\"') {
            end = ++beg;
            char[] deq_buf = null;
            int deq_pos = 0;
            int lst_pos = beg;
            while (end < len && buf[end] != '\"') {
                if (buf[end] == '\\') {
                    if (dequote) {
                        if (deq_buf == null) {
                            deq_buf = new char[buf.length];
                        }
                        System.arraycopy(buf, lst_pos, deq_buf, deq_pos, end - lst_pos);
                        deq_pos += end - lst_pos;
                        lst_pos = ++end;
                    }
                    else {
                        ++end;
                    }
                }
                ++end;
            }
            if (end == len) {
                throw new ParseException("Bad header format: '" + header + "'\nClosing <\"> for quoted-string" + " starting at position " + (beg - 1) + " not found");
            }
            if (deq_buf != null) {
                System.arraycopy(buf, lst_pos, deq_buf, deq_pos, end - lst_pos);
                deq_pos += end - lst_pos;
                value = new String(deq_buf, 0, deq_pos);
            }
            else {
                value = new String(buf, beg, end - beg);
            }
            ++end;
        }
        else {
            for (end = beg; end < len && !Character.isWhitespace(buf[end]) && buf[end] != ',' && buf[end] != ';'; ++end) {}
            value = new String(buf, beg, end - beg);
        }
        abeg[0] = end;
        return value;
    }
    
    public static final boolean hasToken(final String header, final String token) throws ParseException {
        return header != null && parseHeader(header).contains(new HttpHeaderElement(token));
    }
    
    public static final HttpHeaderElement getElement(final Vector header, final String name) {
        final int idx = header.indexOf(new HttpHeaderElement(name));
        if (idx == -1) {
            return null;
        }
        return header.elementAt(idx);
    }
    
    public static final String getParameter(final String param, final String hdr) throws ParseException {
        final NVPair[] params = parseHeader(hdr).firstElement().getParams();
        for (int idx = 0; idx < params.length; ++idx) {
            if (params[idx].getName().equalsIgnoreCase(param)) {
                return params[idx].getValue();
            }
        }
        return null;
    }
    
    public static final String assembleHeader(final Vector pheader) {
        final StringBuffer hdr = new StringBuffer(200);
        for (int len = pheader.size(), idx = 0; idx < len; ++idx) {
            pheader.elementAt(idx).appendTo(hdr);
            hdr.append(", ");
        }
        hdr.setLength(hdr.length() - 2);
        return hdr.toString();
    }
    
    static final int skipSpace(final char[] str, int pos) {
        for (int len = str.length; pos < len && Character.isWhitespace(str[pos]); ++pos) {}
        return pos;
    }
    
    static final int findSpace(final char[] str, int pos) {
        for (int len = str.length; pos < len && !Character.isWhitespace(str[pos]); ++pos) {}
        return pos;
    }
    
    static final int skipToken(final char[] str, int pos) {
        for (int len = str.length; pos < len && Util.TokenChar.get(str[pos]); ++pos) {}
        return pos;
    }
    
    static final boolean needsQuoting(final String str) {
        int len;
        int pos;
        for (len = str.length(), pos = 0; pos < len && Util.TokenChar.get(str.charAt(pos)); ++pos) {}
        return pos < len;
    }
    
    public static final boolean sameHttpURL(final URL url1, final URL url2) {
        if (!url1.getProtocol().equalsIgnoreCase(url2.getProtocol())) {
            return false;
        }
        if (!url1.getHost().equalsIgnoreCase(url2.getHost())) {
            return false;
        }
        int port1 = url1.getPort();
        int port2 = url2.getPort();
        if (port1 == -1) {
            port1 = URI.defaultPort(url1.getProtocol());
        }
        if (port2 == -1) {
            port2 = URI.defaultPort(url1.getProtocol());
        }
        if (port1 != port2) {
            return false;
        }
        try {
            return URI.unescape(url1.getFile(), null).equals(URI.unescape(url2.getFile(), null));
        }
        catch (final ParseException ex) {
            return url1.getFile().equals(url2.getFile());
        }
    }
    
    public static final int defaultPort(final String protocol) {
        return URI.defaultPort(protocol);
    }
    
    static final Date parseHttpDate(final String dstr) {
        synchronized (Util.http_parse_lock) {
            if (Util.parse_1123 == null) {
                setupParsers();
            }
            monitorexit(Util.http_parse_lock);
        }
        try {
            return Util.parse_1123.parse(dstr);
        }
        catch (final java.text.ParseException ex) {
            try {
                return Util.parse_850.parse(dstr);
            }
            catch (final java.text.ParseException ex2) {
                try {
                    return Util.parse_asctime.parse(dstr);
                }
                catch (final java.text.ParseException pe) {
                    throw new IllegalArgumentException(pe.toString());
                }
            }
        }
    }
    
    private static final void setupParsers() {
        Util.parse_1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        Util.parse_850 = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss 'GMT'", Locale.US);
        Util.parse_asctime = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US);
        Util.parse_1123.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Util.parse_850.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Util.parse_asctime.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Util.parse_1123.setLenient(true);
        Util.parse_850.setLenient(true);
        Util.parse_asctime.setLenient(true);
    }
    
    public static final String httpDate(final Date date) {
        synchronized (Util.http_format_lock) {
            if (Util.http_format == null) {
                setupFormatter();
            }
            monitorexit(Util.http_format_lock);
        }
        return Util.http_format.format(date);
    }
    
    private static final void setupFormatter() {
        (Util.http_format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)).setTimeZone(new SimpleTimeZone(0, "GMT"));
    }
    
    static final String escapeUnsafeChars(final String path) {
        final int len = path.length();
        final char[] buf = new char[3 * len];
        int dst = 0;
        for (int src = 0; src < len; ++src) {
            final char ch = path.charAt(src);
            if (ch >= '\u0080' || Util.UnsafeChar.get(ch)) {
                buf[dst++] = '%';
                buf[dst++] = Util.hex_map[(ch & '\u00f0') >>> 4];
                buf[dst++] = Util.hex_map[ch & '\u000f'];
            }
            else {
                buf[dst++] = ch;
            }
        }
        if (dst > len) {
            return new String(buf, 0, dst);
        }
        return path;
    }
    
    public static final String getPath(final String resource) {
        int end = resource.length();
        int p;
        if ((p = resource.indexOf(35)) != -1) {
            end = p;
        }
        if ((p = resource.indexOf(63)) != -1 && p < end) {
            end = p;
        }
        if ((p = resource.indexOf(59)) != -1 && p < end) {
            end = p;
        }
        return resource.substring(0, end);
    }
    
    public static final String getParams(final String resource) {
        final int beg;
        if ((beg = resource.indexOf(59)) == -1) {
            return null;
        }
        final int f;
        if ((f = resource.indexOf(35)) != -1 && f < beg) {
            return null;
        }
        final int q;
        if ((q = resource.indexOf(63)) != -1 && q < beg) {
            return null;
        }
        if (q == -1 && f == -1) {
            return resource.substring(beg + 1);
        }
        if (f == -1 || (q != -1 && q < f)) {
            return resource.substring(beg + 1, q);
        }
        return resource.substring(beg + 1, f);
    }
    
    public static final String getQuery(final String resource) {
        final int beg;
        if ((beg = resource.indexOf(63)) == -1) {
            return null;
        }
        final int f;
        if ((f = resource.indexOf(35)) != -1 && f < beg) {
            return null;
        }
        if (f == -1) {
            return resource.substring(beg + 1);
        }
        return resource.substring(beg + 1, f);
    }
    
    public static final String getFragment(final String resource) {
        final int beg;
        if ((beg = resource.indexOf(35)) == -1) {
            return null;
        }
        return resource.substring(beg + 1);
    }
    
    public static final boolean wildcardMatch(final String pattern, final String name) {
        return wildcardMatch(pattern, name, 0, 0, pattern.length(), name.length());
    }
    
    private static final boolean wildcardMatch(final String pattern, final String name, final int ppos, int npos, final int plen, final int nlen) {
        final int star = pattern.indexOf(42, ppos);
        if (star < 0) {
            return plen - ppos == nlen - npos && pattern.regionMatches(ppos, name, npos, plen - ppos);
        }
        if (!pattern.regionMatches(ppos, name, npos, star - ppos)) {
            return false;
        }
        if (star == plen - 1) {
            return true;
        }
        while (!wildcardMatch(pattern, name, star + 1, npos, plen, nlen) && npos < nlen) {
            ++npos;
        }
        return npos < nlen;
    }
    
    static {
        Separators = new BitSet(128);
        TokenChar = new BitSet(128);
        UnsafeChar = new BitSet(128);
        http_format_lock = new Object();
        http_parse_lock = new Object();
        Util.Separators.set(40);
        Util.Separators.set(41);
        Util.Separators.set(60);
        Util.Separators.set(62);
        Util.Separators.set(64);
        Util.Separators.set(44);
        Util.Separators.set(59);
        Util.Separators.set(58);
        Util.Separators.set(92);
        Util.Separators.set(34);
        Util.Separators.set(47);
        Util.Separators.set(91);
        Util.Separators.set(93);
        Util.Separators.set(63);
        Util.Separators.set(61);
        Util.Separators.set(123);
        Util.Separators.set(125);
        Util.Separators.set(32);
        Util.Separators.set(9);
        for (int ch = 32; ch < 127; ++ch) {
            Util.TokenChar.set(ch);
        }
        Util.TokenChar.xor(Util.Separators);
        for (int ch2 = 0; ch2 < 32; ++ch2) {
            Util.UnsafeChar.set(ch2);
        }
        Util.UnsafeChar.set(32);
        Util.UnsafeChar.set(60);
        Util.UnsafeChar.set(62);
        Util.UnsafeChar.set(34);
        Util.UnsafeChar.set(123);
        Util.UnsafeChar.set(125);
        Util.UnsafeChar.set(124);
        Util.UnsafeChar.set(92);
        Util.UnsafeChar.set(94);
        Util.UnsafeChar.set(126);
        Util.UnsafeChar.set(91);
        Util.UnsafeChar.set(93);
        Util.UnsafeChar.set(96);
        Util.UnsafeChar.set(127);
        hex_map = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
