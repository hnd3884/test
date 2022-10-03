package HTTPClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;
import java.util.Hashtable;

public class URI
{
    public static final boolean ENABLE_BACKWARDS_COMPATIBILITY = true;
    protected static final Hashtable defaultPorts;
    protected static final Hashtable usesGenericSyntax;
    protected static final Hashtable usesSemiGenericSyntax;
    protected static final BitSet alphanumChar;
    protected static final BitSet markChar;
    protected static final BitSet reservedChar;
    protected static final BitSet unreservedChar;
    protected static final BitSet uricChar;
    protected static final BitSet pcharChar;
    protected static final BitSet userinfoChar;
    protected static final BitSet schemeChar;
    protected static final BitSet hostChar;
    protected static final BitSet opaqueChar;
    protected static final BitSet reg_nameChar;
    public static final BitSet resvdSchemeChar;
    public static final BitSet resvdUIChar;
    public static final BitSet resvdHostChar;
    public static final BitSet resvdPathChar;
    public static final BitSet resvdQueryChar;
    public static final BitSet escpdPathChar;
    public static final BitSet escpdQueryChar;
    public static final BitSet escpdFragChar;
    protected static final int OPAQUE = 0;
    protected static final int SEMI_GENERIC = 1;
    protected static final int GENERIC = 2;
    protected int type;
    protected String scheme;
    protected String opaque;
    protected String userinfo;
    protected String host;
    protected int port;
    protected String path;
    protected String query;
    protected String fragment;
    protected URL url;
    private int hashCode;
    private static final char[] hex;
    private static final String nl;
    
    public URI(final String uri) throws ParseException {
        this((URI)null, uri);
    }
    
    public URI(final URI base, final String rel_uri) throws ParseException {
        this.port = -1;
        this.hashCode = -1;
        final char[] uri = rel_uri.toCharArray();
        int pos;
        int len;
        for (pos = 0, len = uri.length; pos < len; ++pos) {
            if (!Character.isWhitespace(uri[pos])) {
                break;
            }
        }
        while (len > 0 && Character.isWhitespace(uri[len - 1])) {
            --len;
        }
        if (pos < len - 3 && uri[pos + 3] == ':' && (uri[pos] == 'u' || uri[pos] == 'U') && (uri[pos + 1] == 'r' || uri[pos + 1] == 'R') && (uri[pos + 2] == 'i' || uri[pos + 2] == 'I' || uri[pos + 2] == 'l' || uri[pos + 2] == 'L')) {
            pos += 4;
        }
        int idx;
        for (idx = pos; idx < len && uri[idx] != ':' && uri[idx] != '/' && uri[idx] != '?' && uri[idx] != '#'; ++idx) {}
        if (idx < len && uri[idx] == ':') {
            this.scheme = rel_uri.substring(pos, idx).trim().toLowerCase();
            pos = idx + 1;
        }
        String final_scheme = this.scheme;
        if (this.scheme == null) {
            if (base == null) {
                throw new ParseException("No scheme found");
            }
            final_scheme = base.scheme;
        }
        this.type = (usesGenericSyntax(final_scheme) ? 2 : (usesSemiGenericSyntax(final_scheme) ? 1 : 0));
        if (this.type != 0) {
            if (pos + 1 < len && uri[pos] == '/' && uri[pos + 1] == '/') {
                pos += 2;
                for (idx = pos; idx < len && uri[idx] != '/' && uri[idx] != '?' && uri[idx] != '#'; ++idx) {}
                this.parse_authority(rel_uri.substring(pos, idx), final_scheme);
                pos = idx;
            }
            if (this.type == 1) {
                this.path = escape(rel_uri.substring(pos), URI.uricChar, true);
                if (this.path.length() > 0 && this.path.charAt(0) != '/') {
                    this.path = String.valueOf('/') + this.path;
                }
            }
            else {
                for (idx = pos; idx < len && uri[idx] != '?' && uri[idx] != '#'; ++idx) {}
                this.path = escape(rel_uri.substring(pos, idx), URI.escpdPathChar, true);
                pos = idx;
                if (pos < len && uri[pos] == '?') {
                    for (idx = ++pos; idx < len && uri[idx] != '#'; ++idx) {}
                    this.query = escape(rel_uri.substring(pos, idx), URI.escpdQueryChar, true);
                    pos = idx;
                }
                if (pos < len && uri[pos] == '#') {
                    this.fragment = escape(rel_uri.substring(pos + 1, len), URI.escpdFragChar, true);
                }
            }
            if (base != null) {
                if (this.scheme != null && !this.scheme.equals(base.scheme)) {
                    return;
                }
                this.scheme = base.scheme;
                if (this.host != null) {
                    return;
                }
                this.userinfo = base.userinfo;
                this.host = base.host;
                this.port = base.port;
                if (this.type == 1) {
                    return;
                }
                if (this.path.length() == 0 && this.query == null) {
                    this.path = base.path;
                    this.query = base.query;
                    return;
                }
                if (this.path.length() == 0 || this.path.charAt(0) != '/') {
                    idx = ((base.path != null) ? base.path.lastIndexOf(47) : -1);
                    if (idx < 0) {
                        this.path = String.valueOf('/') + this.path;
                    }
                    else {
                        this.path = String.valueOf(base.path.substring(0, idx + 1)) + this.path;
                    }
                    this.path = canonicalizePath(this.path);
                }
            }
            return;
        }
        if (base != null && this.scheme == null) {
            throw new ParseException("Can't resolve relative URI for scheme " + final_scheme);
        }
        this.opaque = escape(rel_uri.substring(pos), URI.opaqueChar, true);
        if (this.opaque.length() > 0 && this.opaque.charAt(0) == '/') {
            this.opaque = "%2F" + this.opaque.substring(1);
        }
    }
    
    public static String canonicalizePath(final String path) {
        int len = path.length();
        int idx;
        if ((idx = path.indexOf("/.")) == -1 || (idx != len - 2 && path.charAt(idx + 2) != '/' && (path.charAt(idx + 2) != '.' || (idx != len - 3 && path.charAt(idx + 3) != '/')))) {
            return path;
        }
        final char[] p = new char[path.length()];
        path.getChars(0, p.length, p, 0);
        int beg = 0;
        for (idx = 1; idx < len; ++idx) {
            if (p[idx] == '.' && p[idx - 1] == '/') {
                int end;
                if (idx == len - 1) {
                    end = idx;
                    ++idx;
                }
                else if (p[idx + 1] == '/') {
                    end = idx - 1;
                    ++idx;
                }
                else {
                    if (p[idx + 1] != '.' || (idx != len - 2 && p[idx + 2] != '/')) {
                        continue;
                    }
                    if (idx < beg + 2) {
                        beg = idx + 2;
                        continue;
                    }
                    for (end = idx - 2; end > beg && p[end] != '/'; --end) {}
                    if (p[end] != '/') {
                        continue;
                    }
                    if (idx == len - 2) {
                        ++end;
                    }
                    idx += 2;
                }
                System.arraycopy(p, idx, p, end, len - idx);
                len -= idx - end;
                idx = end;
            }
        }
        return new String(p, 0, len);
    }
    
    private void parse_authority(final String authority, final String scheme) throws ParseException {
        final char[] uri = authority.toCharArray();
        int pos = 0;
        int len;
        int idx;
        for (len = uri.length, idx = pos; idx < len && uri[idx] != '@'; ++idx) {}
        if (idx < len && uri[idx] == '@') {
            this.userinfo = escape(authority.substring(pos, idx), URI.userinfoChar, true);
            pos = idx + 1;
        }
        idx = pos;
        if (idx < len && uri[idx] == '[') {
            while (idx < len && uri[idx] != ']') {
                ++idx;
            }
            if (idx == len) {
                throw new ParseException("No closing ']' found for opening '[' at position " + pos + " in authority `" + authority + "'");
            }
            this.host = authority.substring(pos + 1, idx);
            ++idx;
        }
        else {
            while (idx < len && uri[idx] != ':') {
                ++idx;
            }
            this.host = escape(authority.substring(pos, idx), URI.uricChar, true);
        }
        pos = idx;
        if (pos < len - 1 && uri[pos] == ':') {
            int p;
            try {
                p = Integer.parseInt(unescape(authority.substring(pos + 1, len), null));
                if (p < 0) {
                    throw new NumberFormatException();
                }
            }
            catch (final NumberFormatException ex) {
                throw new ParseException(String.valueOf(authority.substring(pos + 1, len)) + " is an invalid port number");
            }
            if (p == defaultPort(scheme)) {
                this.port = -1;
            }
            else {
                this.port = p;
            }
        }
    }
    
    public URI(final URL url) throws ParseException {
        this((URI)null, url.toExternalForm());
    }
    
    public URI(final String scheme, final String host, final String path) throws ParseException {
        this(scheme, null, host, -1, path, null, null);
    }
    
    public URI(final String scheme, final String host, final int port, final String path) throws ParseException {
        this(scheme, null, host, port, path, null, null);
    }
    
    public URI(final String scheme, final String userinfo, String host, final int port, final String path, final String query, final String fragment) throws ParseException {
        this.port = -1;
        this.hashCode = -1;
        if (scheme == null) {
            throw new ParseException("missing scheme");
        }
        this.scheme = escape(scheme.trim().toLowerCase(), URI.schemeChar, true);
        if (userinfo != null) {
            this.userinfo = escape(userinfo.trim(), URI.userinfoChar, true);
        }
        if (host != null) {
            host = host.trim();
            this.host = (isIPV6Addr(host) ? host : escape(host, URI.hostChar, true));
        }
        if (port != defaultPort(scheme)) {
            this.port = port;
        }
        if (path != null) {
            this.path = escape(path.trim(), URI.escpdPathChar, true);
        }
        if (query != null) {
            this.query = escape(query.trim(), URI.escpdQueryChar, true);
        }
        if (fragment != null) {
            this.fragment = escape(fragment.trim(), URI.escpdFragChar, true);
        }
        this.type = (usesGenericSyntax(scheme) ? 2 : 1);
    }
    
    private static final boolean isIPV6Addr(final String host) {
        if (host.indexOf(58) < 0) {
            return false;
        }
        for (int idx = 0; idx < host.length(); ++idx) {
            final char ch = host.charAt(idx);
            if ((ch < '0' || ch > '9') && ch != ':') {
                return false;
            }
        }
        return true;
    }
    
    public URI(final String scheme, final String opaque) throws ParseException {
        this.port = -1;
        this.hashCode = -1;
        if (scheme == null) {
            throw new ParseException("missing scheme");
        }
        this.scheme = escape(scheme.trim().toLowerCase(), URI.schemeChar, true);
        this.opaque = escape(opaque, URI.opaqueChar, true);
        this.type = 0;
    }
    
    public static boolean usesGenericSyntax(final String scheme) {
        return URI.usesGenericSyntax.containsKey(scheme.trim().toLowerCase());
    }
    
    public static boolean usesSemiGenericSyntax(final String scheme) {
        return URI.usesSemiGenericSyntax.containsKey(scheme.trim().toLowerCase());
    }
    
    public static final int defaultPort(final String protocol) {
        final Integer port = URI.defaultPorts.get(protocol.trim().toLowerCase());
        return (port != null) ? port : 0;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public String getOpaque() {
        return this.opaque;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUserinfo() {
        return this.userinfo;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getQueryString() {
        return this.query;
    }
    
    public String getPathAndQuery() {
        if (this.query == null) {
            return this.path;
        }
        if (this.path == null) {
            return "?" + this.query;
        }
        return String.valueOf(this.path) + "?" + this.query;
    }
    
    public String getFragment() {
        return this.fragment;
    }
    
    public boolean isGenericURI() {
        return this.type == 2;
    }
    
    public boolean isSemiGenericURI() {
        return this.type == 1;
    }
    
    public URL toURL() throws MalformedURLException {
        if (this.url != null) {
            return this.url;
        }
        if (this.opaque != null) {
            return this.url = new URL(String.valueOf(this.scheme) + ":" + this.opaque);
        }
        String hostinfo;
        if (this.userinfo != null && this.host != null) {
            hostinfo = String.valueOf(this.userinfo) + "@" + this.host;
        }
        else if (this.userinfo != null) {
            hostinfo = String.valueOf(this.userinfo) + "@";
        }
        else {
            hostinfo = this.host;
        }
        final StringBuffer file = new StringBuffer(100);
        this.assemblePath(file, true, true, false);
        return this.url = new URL(this.scheme, hostinfo, this.port, file.toString());
    }
    
    private final void assemblePath(final StringBuffer buf, final boolean printEmpty, final boolean incFragment, final boolean unescape) {
        if ((this.path == null || this.path.length() == 0) && printEmpty) {
            buf.append('/');
        }
        if (this.path != null) {
            buf.append(unescape ? unescapeNoPE(this.path, URI.resvdPathChar) : this.path);
        }
        if (this.query != null) {
            buf.append('?');
            buf.append(unescape ? unescapeNoPE(this.query, URI.resvdQueryChar) : this.query);
        }
        if (this.fragment != null && incFragment) {
            buf.append('#');
            buf.append(unescape ? unescapeNoPE(this.fragment, null) : this.fragment);
        }
    }
    
    private final String stringify(final boolean unescape) {
        final StringBuffer uri = new StringBuffer(100);
        if (this.scheme != null) {
            uri.append(unescape ? unescapeNoPE(this.scheme, URI.resvdSchemeChar) : this.scheme);
            uri.append(':');
        }
        if (this.opaque != null) {
            uri.append(unescape ? unescapeNoPE(this.opaque, null) : this.opaque);
            return uri.toString();
        }
        if (this.userinfo != null || this.host != null || this.port != -1) {
            uri.append("//");
        }
        if (this.userinfo != null) {
            uri.append(unescape ? unescapeNoPE(this.userinfo, URI.resvdUIChar) : this.userinfo);
            uri.append('@');
        }
        if (this.host != null) {
            if (this.host.indexOf(58) < 0) {
                uri.append(unescape ? unescapeNoPE(this.host, URI.resvdHostChar) : this.host);
            }
            else {
                uri.append('[').append(this.host).append(']');
            }
        }
        if (this.port != -1) {
            uri.append(':');
            uri.append(this.port);
        }
        this.assemblePath(uri, false, true, unescape);
        return uri.toString();
    }
    
    public String toExternalForm() {
        return this.stringify(false);
    }
    
    public String toString() {
        return this.stringify(true);
    }
    
    public boolean equals(final Object other) {
        if (other instanceof URI) {
            final URI o = (URI)other;
            return this.scheme.equals(o.scheme) && ((this.type == 0 && areEqual(this.opaque, o.opaque)) || (this.type == 1 && areEqual(this.userinfo, o.userinfo) && areEqualIC(this.host, o.host) && this.port == o.port && areEqual(this.path, o.path)) || (this.type == 2 && areEqual(this.userinfo, o.userinfo) && areEqualIC(this.host, o.host) && this.port == o.port && pathsEqual(this.path, o.path) && areEqual(this.query, o.query) && areEqual(this.fragment, o.fragment)));
        }
        if (other instanceof URL) {
            final URL o2 = (URL)other;
            String h;
            if (this.userinfo != null) {
                h = String.valueOf(this.userinfo) + "@" + this.host;
            }
            else {
                h = this.host;
            }
            final String f = this.getPathAndQuery();
            return this.scheme.equalsIgnoreCase(o2.getProtocol()) && ((this.type == 0 && this.opaque.equals(o2.getFile())) || (this.type == 1 && areEqualIC(h, o2.getHost()) && (this.port == o2.getPort() || o2.getPort() == defaultPort(this.scheme)) && areEqual(f, o2.getFile())) || (this.type == 2 && areEqualIC(h, o2.getHost()) && (this.port == o2.getPort() || o2.getPort() == defaultPort(this.scheme)) && pathsEqual(f, o2.getFile()) && areEqual(this.fragment, o2.getRef())));
        }
        return false;
    }
    
    private static final boolean areEqual(final String s1, final String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s2 != null && (s1.equals(s2) || unescapeNoPE(s1, null).equals(unescapeNoPE(s2, null))));
    }
    
    private static final boolean areEqualIC(final String s1, final String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s2 != null && (s1.equalsIgnoreCase(s2) || unescapeNoPE(s1, null).equalsIgnoreCase(unescapeNoPE(s2, null))));
    }
    
    private static final boolean pathsEqual(final String p1, final String p2) {
        if (p1 == null && p2 == null) {
            return true;
        }
        if (p1 == null || p2 == null) {
            return false;
        }
        if (p1.equals(p2)) {
            return true;
        }
        int pos1;
        int end1;
        int pos2;
        int end2;
        for (pos1 = 0, end1 = p1.length(), pos2 = 0, end2 = p2.length(); pos1 < end1 && pos2 < end2; ++pos1, ++pos2) {
            final int start1 = pos1;
            final int start2 = pos2;
            char ch;
            while (pos1 < end1 && (ch = p1.charAt(pos1)) != '/') {
                if (ch == ';') {
                    break;
                }
                ++pos1;
            }
            while (pos2 < end2 && (ch = p2.charAt(pos2)) != '/' && ch != ';') {
                ++pos2;
            }
            if ((pos1 == end1 && pos2 < end2) || (pos2 == end2 && pos1 < end1) || (pos1 < end1 && pos2 < end2 && p1.charAt(pos1) != p2.charAt(pos2))) {
                return false;
            }
            if ((!p1.regionMatches(start1, p2, start2, pos1 - start1) || pos1 - start1 != pos2 - start2) && !unescapeNoPE(p1.substring(start1, pos1), null).equals(unescapeNoPE(p2.substring(start2, pos2), null))) {
                return false;
            }
        }
        return pos1 == end1 && pos2 == end2;
    }
    
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = ((this.scheme != null) ? unescapeNoPE(this.scheme, null).hashCode() : 0) + ((this.type == 0) ? (((this.opaque != null) ? unescapeNoPE(this.opaque, null).hashCode() : 0) * 7) : (((this.host != null) ? unescapeNoPE(this.host, null).toLowerCase().hashCode() : 0) * 7 + ((this.path != null) ? unescapeNoPE(this.path, null).hashCode() : 0) * 13 + ((this.query != null) ? unescapeNoPE(this.query, null).hashCode() : 0) * 17));
        }
        return this.hashCode;
    }
    
    public static String escape(final String elem, final BitSet allowed_char, final boolean utf8) {
        return new String(escape(elem.toCharArray(), allowed_char, utf8));
    }
    
    public static char[] escape(final char[] elem, final BitSet allowed_char, final boolean utf8) {
        int cnt = 0;
        for (int idx = 0; idx < elem.length; ++idx) {
            if (!allowed_char.get(elem[idx])) {
                cnt += 2;
                if (utf8) {
                    if (elem[idx] >= '\u0080') {
                        cnt += 3;
                    }
                    if (elem[idx] >= '\u0800') {
                        cnt += 3;
                    }
                    if ((elem[idx] & '\ufc00') == 0xD800 && idx + 1 < elem.length && (elem[idx + 1] & '\ufc00') == 0xDC00) {
                        cnt -= 6;
                    }
                }
            }
        }
        if (cnt == 0) {
            return elem;
        }
        final char[] tmp = new char[elem.length + cnt];
        int idx2 = 0;
        int pos = 0;
        while (idx2 < elem.length) {
            final char c = elem[idx2];
            if (allowed_char.get(c)) {
                tmp[pos++] = c;
            }
            else if (utf8) {
                if (c <= '\u007f') {
                    pos = enc(tmp, pos, c);
                }
                else if (c <= '\u07ff') {
                    pos = enc(tmp, pos, 0xC0 | (c >> 6 & 0x1F));
                    pos = enc(tmp, pos, 0x80 | (c & '?'));
                }
                else if ((c & '\ufc00') != 0xD800 || idx2 + 1 >= elem.length || (elem[idx2 + 1] & '\ufc00') != 0xDC00) {
                    pos = enc(tmp, pos, 0xE0 | (c >> 12 & 0xF));
                    pos = enc(tmp, pos, 0x80 | (c >> 6 & 0x3F));
                    pos = enc(tmp, pos, 0x80 | (c & '?'));
                }
                else {
                    int ch = (c & '\u03ff') << 10 | (elem[++idx2] & '\u03ff');
                    ch += 65536;
                    pos = enc(tmp, pos, 0xF0 | (ch >> 18 & 0x7));
                    pos = enc(tmp, pos, 0x80 | (ch >> 12 & 0x3F));
                    pos = enc(tmp, pos, 0x80 | (ch >> 6 & 0x3F));
                    pos = enc(tmp, pos, 0x80 | (ch & 0x3F));
                }
            }
            else {
                pos = enc(tmp, pos, c);
            }
            ++idx2;
        }
        return tmp;
    }
    
    private static final int enc(final char[] out, int pos, final int c) {
        out[pos++] = '%';
        out[pos++] = URI.hex[c >> 4 & 0xF];
        out[pos++] = URI.hex[c & 0xF];
        return pos;
    }
    
    public static final String unescape(final String str, final BitSet reserved) throws ParseException {
        if (str == null || str.indexOf(37) == -1) {
            return str;
        }
        final char[] buf = str.toCharArray();
        final char[] res = new char[buf.length];
        final char[] utf = new char[4];
        int utf_idx = 0;
        int utf_len = -1;
        int didx = 0;
        for (int sidx = 0; sidx < buf.length; ++sidx) {
            if (buf[sidx] == '%') {
                int ch;
                try {
                    if (sidx + 3 > buf.length) {
                        throw new NumberFormatException();
                    }
                    ch = Integer.parseInt(str.substring(sidx + 1, sidx + 3), 16);
                    if (ch < 0) {
                        throw new NumberFormatException();
                    }
                    sidx += 2;
                }
                catch (final NumberFormatException ex) {
                    ch = buf[sidx];
                }
                if (utf_len > 0) {
                    if ((ch & 0xC0) != 0x80) {
                        didx = copyBuf(utf, utf_idx, ch, res, didx, reserved, false);
                        utf_len = -1;
                    }
                    else if (utf_idx == utf_len - 1) {
                        if ((utf[0] & '\u00e0') == 0xC0) {
                            ch = ((utf[0] & '\u001f') << 6 | (ch & 0x3F));
                        }
                        else if ((utf[0] & '\u00f0') == 0xE0) {
                            ch = ((utf[0] & '\u000f') << 12 | (utf[1] & '?') << 6 | (ch & 0x3F));
                        }
                        else {
                            ch = ((utf[0] & '\u0007') << 18 | (utf[1] & '?') << 12 | (utf[2] & '?') << 6 | (ch & 0x3F));
                        }
                        if (reserved != null && reserved.get(ch)) {
                            didx = copyBuf(utf, utf_idx, ch, res, didx, null, true);
                        }
                        else if (utf_len < 4) {
                            res[didx++] = (char)ch;
                        }
                        else {
                            ch -= 65536;
                            res[didx++] = (char)(ch >> 10 | 0xD800);
                            res[didx++] = (char)((ch & 0x3FF) | 0xDC00);
                        }
                        utf_len = -1;
                    }
                    else {
                        utf[utf_idx++] = (char)ch;
                    }
                }
                else if ((ch & 0xE0) == 0xC0 || (ch & 0xF0) == 0xE0 || (ch & 0xF8) == 0xF0) {
                    if ((ch & 0xE0) == 0xC0) {
                        utf_len = 2;
                    }
                    else if ((ch & 0xF0) == 0xE0) {
                        utf_len = 3;
                    }
                    else {
                        utf_len = 4;
                    }
                    utf[0] = (char)ch;
                    utf_idx = 1;
                }
                else if (reserved != null && reserved.get(ch)) {
                    res[didx++] = buf[sidx];
                    sidx -= 2;
                }
                else {
                    res[didx++] = (char)ch;
                }
            }
            else if (utf_len > 0) {
                didx = copyBuf(utf, utf_idx, buf[sidx], res, didx, reserved, false);
                utf_len = -1;
            }
            else {
                res[didx++] = buf[sidx];
            }
        }
        if (utf_len > 0) {
            didx = copyBuf(utf, utf_idx, -1, res, didx, reserved, false);
        }
        return new String(res, 0, didx);
    }
    
    private static final int copyBuf(final char[] utf, int utf_idx, final int ch, final char[] res, int didx, final BitSet reserved, final boolean escapeAll) {
        if (ch >= 0) {
            utf[utf_idx++] = (char)ch;
        }
        for (int idx = 0; idx < utf_idx; ++idx) {
            if ((reserved != null && reserved.get(utf[idx])) || escapeAll) {
                didx = enc(res, didx, utf[idx]);
            }
            else {
                res[didx++] = utf[idx];
            }
        }
        return didx;
    }
    
    private static final String unescapeNoPE(final String str, final BitSet reserved) {
        try {
            return unescape(str, reserved);
        }
        catch (final ParseException ex) {
            return str;
        }
    }
    
    public static void main(final String[] args) throws Exception {
        System.err.println();
        System.err.println("*** URI Tests ...");
        URI base = new URI("http://a/b/c/d;p?q");
        testParser(base, "g:h", "g:h");
        testParser(base, "g", "http://a/b/c/g");
        testParser(base, "./g", "http://a/b/c/g");
        testParser(base, "g/", "http://a/b/c/g/");
        testParser(base, "/g", "http://a/g");
        testParser(base, "//g", "http://g");
        testParser(base, "//[23:54]", "http://[23:54]");
        testParser(base, "?y", "http://a/b/c/?y");
        testParser(base, "g?y", "http://a/b/c/g?y");
        testParser(base, "#s", "http://a/b/c/d;p?q#s");
        testParser(base, "g#s", "http://a/b/c/g#s");
        testParser(base, "g?y#s", "http://a/b/c/g?y#s");
        testParser(base, ";x", "http://a/b/c/;x");
        testParser(base, "g;x", "http://a/b/c/g;x");
        testParser(base, "g;x?y#s", "http://a/b/c/g;x?y#s");
        testParser(base, ".", "http://a/b/c/");
        testParser(base, "./", "http://a/b/c/");
        testParser(base, "..", "http://a/b/");
        testParser(base, "../", "http://a/b/");
        testParser(base, "../g", "http://a/b/g");
        testParser(base, "../..", "http://a/");
        testParser(base, "../../", "http://a/");
        testParser(base, "../../g", "http://a/g");
        testParser(base, "", "http://a/b/c/d;p?q");
        testParser(base, "/./g", "http://a/./g");
        testParser(base, "/../g", "http://a/../g");
        testParser(base, "../../../g", "http://a/../g");
        testParser(base, "../../../../g", "http://a/../../g");
        testParser(base, "g.", "http://a/b/c/g.");
        testParser(base, ".g", "http://a/b/c/.g");
        testParser(base, "g..", "http://a/b/c/g..");
        testParser(base, "..g", "http://a/b/c/..g");
        testParser(base, "./../g", "http://a/b/g");
        testParser(base, "./g/.", "http://a/b/c/g/");
        testParser(base, "g/./h", "http://a/b/c/g/h");
        testParser(base, "g/../h", "http://a/b/c/h");
        testParser(base, "g;x=1/./y", "http://a/b/c/g;x=1/y");
        testParser(base, "g;x=1/../y", "http://a/b/c/y");
        testParser(base, "g?y/./x", "http://a/b/c/g?y/./x");
        testParser(base, "g?y/../x", "http://a/b/c/g?y/../x");
        testParser(base, "g#s/./x", "http://a/b/c/g#s/./x");
        testParser(base, "g#s/../x", "http://a/b/c/g#s/../x");
        testParser(base, "http:g", "http://a/b/c/g");
        testParser(base, "http:", "http://a/b/c/d;p?q");
        testParser(base, "./g:h", "http://a/b/c/g:h");
        base = new URI("http://a/b/c/d;p?q=1/2");
        testParser(base, "g", "http://a/b/c/g");
        testParser(base, "./g", "http://a/b/c/g");
        testParser(base, "g/", "http://a/b/c/g/");
        testParser(base, "/g", "http://a/g");
        testParser(base, "//g", "http://g");
        testParser(base, "//[23:54]", "http://[23:54]");
        testParser(base, "?y", "http://a/b/c/?y");
        testParser(base, "g?y", "http://a/b/c/g?y");
        testParser(base, "g?y/./x", "http://a/b/c/g?y/./x");
        testParser(base, "g?y/../x", "http://a/b/c/g?y/../x");
        testParser(base, "g#s", "http://a/b/c/g#s");
        testParser(base, "g#s/./x", "http://a/b/c/g#s/./x");
        testParser(base, "g#s/../x", "http://a/b/c/g#s/../x");
        testParser(base, "./", "http://a/b/c/");
        testParser(base, "../", "http://a/b/");
        testParser(base, "../g", "http://a/b/g");
        testParser(base, "../../", "http://a/");
        testParser(base, "../../g", "http://a/g");
        base = new URI("http://a/b/c/d;p=1/2?q");
        testParser(base, "g", "http://a/b/c/d;p=1/g");
        testParser(base, "./g", "http://a/b/c/d;p=1/g");
        testParser(base, "g/", "http://a/b/c/d;p=1/g/");
        testParser(base, "g?y", "http://a/b/c/d;p=1/g?y");
        testParser(base, ";x", "http://a/b/c/d;p=1/;x");
        testParser(base, "g;x", "http://a/b/c/d;p=1/g;x");
        testParser(base, "g;x=1/./y", "http://a/b/c/d;p=1/g;x=1/y");
        testParser(base, "g;x=1/../y", "http://a/b/c/d;p=1/y");
        testParser(base, "./", "http://a/b/c/d;p=1/");
        testParser(base, "../", "http://a/b/c/");
        testParser(base, "../g", "http://a/b/c/g");
        testParser(base, "../../", "http://a/b/");
        testParser(base, "../../g", "http://a/b/g");
        base = new URI("fred:///s//a/b/c");
        testParser(base, "g:h", "g:h");
        testPE(base, "g");
        base = new URI("http:///s//a/b/c");
        testParser(base, "g:h", "g:h");
        testParser(base, "g", "http:///s//a/b/g");
        testParser(base, "./g", "http:///s//a/b/g");
        testParser(base, "g/", "http:///s//a/b/g/");
        testParser(base, "/g", "http:///g");
        testParser(base, "//g", "http://g");
        testParser(base, "//[23:54]", "http://[23:54]");
        testParser(base, "//g/x", "http://g/x");
        testParser(base, "///g", "http:///g");
        testParser(base, "./", "http:///s//a/b/");
        testParser(base, "../", "http:///s//a/");
        testParser(base, "../g", "http:///s//a/g");
        testParser(base, "../../", "http:///s//");
        testParser(base, "../../g", "http:///s//g");
        testParser(base, "../../../g", "http:///s/g");
        testParser(base, "../../../../g", "http:///g");
        base = new URI("http://s");
        testParser(base, "ftp:h", "ftp:h");
        testParser(base, "ftp://h", "ftp://h");
        testParser(base, "//g", "http://g");
        testParser(base, "//g?h", "http://g?h");
        testParser(base, "g", "http://s/g");
        testParser(base, "./g", "http://s/g");
        testParser(base, "?g", "http://s/?g");
        testParser(base, "#g", "http://s#g");
        base = new URI("http:");
        testParser(base, "ftp:h", "ftp:h");
        testParser(base, "ftp://h", "ftp://h");
        testParser(base, "//g", "http://g");
        testParser(base, "g", "http:/g");
        testParser(base, "?g", "http:/?g");
        testParser(base, "#g", "http:#g");
        base = new URI("http://s/t");
        testParser(base, "ftp:/h", "ftp:/h");
        testParser(base, "http:/h", "http://s/h");
        base = new URI("http://s/g?h/j");
        testParser(base, "k", "http://s/k");
        testParser(base, "k?l", "http://s/k?l");
        base = new URI("ldap:");
        testParser(base, "ldap:", "ldap:");
        testParser(base, "ldap://a", "ldap://a");
        testParser(base, "ldap://a/b", "ldap://a/b");
        testParser(base, "ldap:/b", "ldap:/b");
        testParser(base, "ftp:h", "ftp:h");
        testParser(base, "ftp://h", "ftp://h");
        testParser(base, "//g", "ldap://g");
        testParser(base, "//g?h", "ldap://g/?h");
        testParser(base, "g", "ldap:/g");
        testParser(base, "./g", "ldap:/./g");
        testParser(base, "?g", "ldap:/?g");
        testParser(base, "#g", "ldap:/%23g");
        base = new URI("ldap://s");
        testParser(base, "ldap:", "ldap://s");
        testParser(base, "ldap://a", "ldap://a");
        testParser(base, "ldap://a/b", "ldap://a/b");
        testParser(base, "ldap:/b", "ldap://s/b");
        testParser(base, "ftp:h", "ftp:h");
        testParser(base, "ftp://h", "ftp://h");
        testParser(base, "//g", "ldap://g");
        testParser(base, "//g?h", "ldap://g/?h");
        testParser(base, "g", "ldap://s/g");
        testParser(base, "./g", "ldap://s/./g");
        testParser(base, "?g", "ldap://s/?g");
        testParser(base, "#g", "ldap://s/%23g");
        base = new URI("ldap://s/t");
        testParser(base, "ftp:/h", "ftp:/h");
        testParser(base, "ldap:/h", "ldap://s/h");
        testParser(base, "ldap:", "ldap://s");
        testParser(base, "ldap://a", "ldap://a");
        testParser(base, "ldap://a/b", "ldap://a/b");
        testParser(base, "ftp:h", "ftp:h");
        testParser(base, "ftp://h", "ftp://h");
        testParser(base, "//g", "ldap://g");
        testParser(base, "//g?h", "ldap://g/?h");
        testParser(base, "g", "ldap://s/g");
        testParser(base, "./g", "ldap://s/./g");
        testParser(base, "?g", "ldap://s/?g");
        testParser(base, "#g", "ldap://s/%23g");
        testNotEqual("http://a/", "nntp://a/");
        testNotEqual("http://a/", "https://a/");
        testNotEqual("http://a/", "shttp://a/");
        testEqual("http://a/", "Http://a/");
        testEqual("http://a/", "hTTP://a/");
        testEqual("url:http://a/", "hTTP://a/");
        testEqual("urI:http://a/", "hTTP://a/");
        testEqual("http://a/", "Http://A/");
        testEqual("http://a.b.c/", "Http://A.b.C/");
        testEqual("http:///", "Http:///");
        testEqual("http://[]/", "Http:///");
        testNotEqual("http:///", "Http://a/");
        testNotEqual("http://[]/", "Http://a/");
        testPE(null, "ftp://[23::43:1/");
        testPE(null, "ftp://[/");
        testEqual("http://a.b.c/", "Http://A.b.C:80/");
        testEqual("http://a.b.c:/", "Http://A.b.C:80/");
        testEqual("http://[23::45:::5:]/", "Http://[23::45:::5:]:80/");
        testEqual("http://[23::45:::5:]:/", "Http://[23::45:::5:]:80/");
        testEqual("nntp://a", "nntp://a:119");
        testEqual("nntp://a:", "nntp://a:119");
        testEqual("nntp://a/", "nntp://a:119/");
        testNotEqual("nntp://a", "nntp://a:118");
        testNotEqual("nntp://a", "nntp://a:0");
        testNotEqual("nntp://a:", "nntp://a:0");
        testEqual("telnet://:23/", "telnet:///");
        testPE(null, "ftp://:a/");
        testPE(null, "ftp://:-1/");
        testPE(null, "ftp://::1/");
        testNotEqual("ftp://me@a", "ftp://a");
        testNotEqual("ftp://me@a", "ftp://Me@a");
        testEqual("ftp://Me@a", "ftp://Me@a");
        testEqual("ftp://Me:My@a:21", "ftp://Me:My@a");
        testEqual("ftp://Me:My@a:", "ftp://Me:My@a");
        testNotEqual("ftp://Me:My@a:21", "ftp://Me:my@a");
        testNotEqual("ftp://Me:My@a:", "ftp://Me:my@a");
        testEqual("ftp://a/b%2b/", "ftp://a/b+/");
        testEqual("ftp://a/b%2b/", "ftp://a/b+/");
        testEqual("ftp://a/b%5E/", "ftp://a/b^/");
        testEqual("ftp://a/b%4C/", "ftp://a/bL/");
        testNotEqual("ftp://a/b/", "ftp://a//b/");
        testNotEqual("ftp://a/b/", "ftp://a/b//");
        testNotEqual("ftp://a/b%4C/", "ftp://a/bl/");
        testNotEqual("ftp://a/b%3f/", "ftp://a/b?/");
        testNotEqual("ftp://a/b%2f/", "ftp://a/b//");
        testNotEqual("ftp://a/b%2fc/", "ftp://a/b/c/");
        testNotEqual("ftp://a/bc/", "ftp://a/b//");
        testNotEqual("ftp://a/bc/", "ftp://a/b/");
        testNotEqual("ftp://a/bc//", "ftp://a/b/");
        testNotEqual("ftp://a/b/", "ftp://a/bc//");
        testNotEqual("ftp://a/b/", "ftp://a/bc/");
        testNotEqual("ftp://a/b//", "ftp://a/bc/");
        testNotEqual("ftp://a/b;fc/", "ftp://a/bf;c/");
        testNotEqual("ftp://a/b%3bfc/", "ftp://a/b;fc/");
        testEqual("ftp://a/b;/;/", "ftp://a/b;/;/");
        testNotEqual("ftp://a/b;/", "ftp://a/b//");
        testNotEqual("ftp://a/b//", "ftp://a/b;/");
        testNotEqual("ftp://a/b/;", "ftp://a/b//");
        testNotEqual("ftp://a/b//", "ftp://a/b/;");
        testNotEqual("ftp://a/b;/", "ftp://a/b;//");
        testNotEqual("ftp://a/b;//", "ftp://a/b;/");
        testEscape("hello\u1212there", "hello%E1%88%92there");
        testEscape("hello\u0232there", "hello%C8%B2there");
        testEscape("hello\uda42\udd42there", "hello%F2%A0%A5%82there");
        testEscape("hello\uda42", "hello%ED%A9%82");
        testEscape("hello\uda42there", "hello%ED%A9%82there");
        testUnescape("hello%F2%A0%A5%82there", "hello\uda42\udd42there");
        testUnescape("hello%F2%A0%A5there", "hello\u00f2 ¥there");
        testUnescape("hello%F2%A0there", "hello\u00f2 there");
        testUnescape("hello%F2there", "hello\u00f2there");
        testUnescape("hello%F2%A0%A5%82", "hello\uda42\udd42");
        testUnescape("hello%F2%A0%A5", "hello\u00f2 ¥");
        testUnescape("hello%F2%A0", "hello\u00f2 ");
        testUnescape("hello%F2", "hello\u00f2");
        testUnescape("hello%E1%88%92there", "hello\u1212there");
        testUnescape("hello%E1%88there", "hello\u00e1\u0088there");
        testUnescape("hello%E1there", "hello\u00e1there");
        testUnescape("hello%E1%71there", "hello\u00e1qthere");
        testUnescape("hello%E1%88", "hello\u00e1\u0088");
        testUnescape("hello%E1%71", "hello\u00e1q");
        testUnescape("hello%E1", "hello\u00e1");
        testUnescape("hello%C8%B2there", "hello\u0232there");
        testUnescape("hello%C8there", "hello\u00c8there");
        testUnescape("hello%C8%71there", "hello\u00c8qthere");
        testUnescape("hello%C8%71", "hello\u00c8q");
        testUnescape("hello%C8", "hello\u00c8");
        testUnescape("%71there", "qthere");
        testUnescape("%B1there", "±there");
        System.err.println("*** Tests finished successfuly");
    }
    
    private static void testParser(final URI base, final String relURI, final String result) throws Exception {
        if (!new URI(base, relURI).toExternalForm().equals(result)) {
            throw new Exception("Test failed: " + URI.nl + "  base-URI = <" + base + ">" + URI.nl + "  rel-URI  = <" + relURI + ">" + URI.nl + "  expected   <" + result + ">" + URI.nl + "  but got    <" + new URI(base, relURI) + ">");
        }
    }
    
    private static void testEqual(final String one, final String two) throws Exception {
        final URI u1 = new URI(one);
        final URI u2 = new URI(two);
        if (!u1.equals(u2)) {
            throw new Exception("Test failed: " + URI.nl + "  <" + one + "> != <" + two + ">");
        }
        if (u1.hashCode() != u2.hashCode()) {
            throw new Exception("Test failed: " + URI.nl + "  hashCode <" + one + "> != hashCode <" + two + ">");
        }
    }
    
    private static void testNotEqual(final String one, final String two) throws Exception {
        final URI u1 = new URI(one);
        final URI u2 = new URI(two);
        if (u1.equals(u2)) {
            throw new Exception("Test failed: " + URI.nl + "  <" + one + "> == <" + two + ">");
        }
    }
    
    private static void testPE(final URI base, final String uri) throws Exception {
        boolean got_pe = false;
        try {
            new URI(base, uri);
        }
        catch (final ParseException ex) {
            got_pe = true;
        }
        if (!got_pe) {
            throw new Exception("Test failed: " + URI.nl + "  <" + uri + "> should be invalid");
        }
    }
    
    private static void testEscape(final String raw, final String escaped) throws Exception {
        final String test = new String(escape(raw.toCharArray(), URI.uricChar, true));
        if (!test.equals(escaped)) {
            throw new Exception("Test failed: " + URI.nl + "  raw-string: " + raw + URI.nl + "  escaped:    " + test + URI.nl + "  expected:   " + escaped);
        }
    }
    
    private static void testUnescape(final String escaped, final String raw) throws Exception {
        if (!unescape(escaped, null).equals(raw)) {
            throw new Exception("Test failed: " + URI.nl + "  escaped-string: " + escaped + URI.nl + "  unescaped:      " + unescape(escaped, null) + URI.nl + "  expected:       " + raw);
        }
    }
    
    static {
        defaultPorts = new Hashtable();
        usesGenericSyntax = new Hashtable();
        usesSemiGenericSyntax = new Hashtable();
        URI.defaultPorts.put("http", new Integer(80));
        URI.defaultPorts.put("shttp", new Integer(80));
        URI.defaultPorts.put("http-ng", new Integer(80));
        URI.defaultPorts.put("coffee", new Integer(80));
        URI.defaultPorts.put("https", new Integer(443));
        URI.defaultPorts.put("ftp", new Integer(21));
        URI.defaultPorts.put("telnet", new Integer(23));
        URI.defaultPorts.put("nntp", new Integer(119));
        URI.defaultPorts.put("news", new Integer(119));
        URI.defaultPorts.put("snews", new Integer(563));
        URI.defaultPorts.put("hnews", new Integer(80));
        URI.defaultPorts.put("smtp", new Integer(25));
        URI.defaultPorts.put("gopher", new Integer(70));
        URI.defaultPorts.put("wais", new Integer(210));
        URI.defaultPorts.put("whois", new Integer(43));
        URI.defaultPorts.put("whois++", new Integer(63));
        URI.defaultPorts.put("rwhois", new Integer(4321));
        URI.defaultPorts.put("imap", new Integer(143));
        URI.defaultPorts.put("pop", new Integer(110));
        URI.defaultPorts.put("prospero", new Integer(1525));
        URI.defaultPorts.put("irc", new Integer(194));
        URI.defaultPorts.put("ldap", new Integer(389));
        URI.defaultPorts.put("nfs", new Integer(2049));
        URI.defaultPorts.put("z39.50r", new Integer(210));
        URI.defaultPorts.put("z39.50s", new Integer(210));
        URI.defaultPorts.put("vemmi", new Integer(575));
        URI.defaultPorts.put("videotex", new Integer(516));
        URI.defaultPorts.put("cmp", new Integer(829));
        URI.usesGenericSyntax.put("http", Boolean.TRUE);
        URI.usesGenericSyntax.put("https", Boolean.TRUE);
        URI.usesGenericSyntax.put("shttp", Boolean.TRUE);
        URI.usesGenericSyntax.put("coffee", Boolean.TRUE);
        URI.usesGenericSyntax.put("ftp", Boolean.TRUE);
        URI.usesGenericSyntax.put("file", Boolean.TRUE);
        URI.usesGenericSyntax.put("nntp", Boolean.TRUE);
        URI.usesGenericSyntax.put("news", Boolean.TRUE);
        URI.usesGenericSyntax.put("snews", Boolean.TRUE);
        URI.usesGenericSyntax.put("hnews", Boolean.TRUE);
        URI.usesGenericSyntax.put("imap", Boolean.TRUE);
        URI.usesGenericSyntax.put("wais", Boolean.TRUE);
        URI.usesGenericSyntax.put("nfs", Boolean.TRUE);
        URI.usesGenericSyntax.put("sip", Boolean.TRUE);
        URI.usesGenericSyntax.put("sips", Boolean.TRUE);
        URI.usesGenericSyntax.put("sipt", Boolean.TRUE);
        URI.usesGenericSyntax.put("sipu", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("ldap", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("irc", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("gopher", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("videotex", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("rwhois", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("whois++", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("smtp", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("telnet", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("prospero", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("pop", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("vemmi", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("z39.50r", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("z39.50s", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("stream", Boolean.TRUE);
        URI.usesSemiGenericSyntax.put("cmp", Boolean.TRUE);
        alphanumChar = new BitSet(128);
        for (int ch = 48; ch <= 57; ++ch) {
            URI.alphanumChar.set(ch);
        }
        for (int ch2 = 65; ch2 <= 90; ++ch2) {
            URI.alphanumChar.set(ch2);
        }
        for (int ch3 = 97; ch3 <= 122; ++ch3) {
            URI.alphanumChar.set(ch3);
        }
        (markChar = new BitSet(128)).set(45);
        URI.markChar.set(95);
        URI.markChar.set(46);
        URI.markChar.set(33);
        URI.markChar.set(126);
        URI.markChar.set(42);
        URI.markChar.set(39);
        URI.markChar.set(40);
        URI.markChar.set(41);
        (reservedChar = new BitSet(128)).set(59);
        URI.reservedChar.set(47);
        URI.reservedChar.set(63);
        URI.reservedChar.set(58);
        URI.reservedChar.set(64);
        URI.reservedChar.set(38);
        URI.reservedChar.set(61);
        URI.reservedChar.set(43);
        URI.reservedChar.set(36);
        URI.reservedChar.set(44);
        (unreservedChar = new BitSet(128)).or(URI.alphanumChar);
        URI.unreservedChar.or(URI.markChar);
        (uricChar = new BitSet(128)).or(URI.unreservedChar);
        URI.uricChar.or(URI.reservedChar);
        URI.uricChar.set(37);
        (pcharChar = new BitSet(128)).or(URI.unreservedChar);
        URI.pcharChar.set(37);
        URI.pcharChar.set(58);
        URI.pcharChar.set(64);
        URI.pcharChar.set(38);
        URI.pcharChar.set(61);
        URI.pcharChar.set(43);
        URI.pcharChar.set(36);
        URI.pcharChar.set(44);
        (userinfoChar = new BitSet(128)).or(URI.unreservedChar);
        URI.userinfoChar.set(37);
        URI.userinfoChar.set(59);
        URI.userinfoChar.set(58);
        URI.userinfoChar.set(38);
        URI.userinfoChar.set(61);
        URI.userinfoChar.set(43);
        URI.userinfoChar.set(36);
        URI.userinfoChar.set(44);
        (schemeChar = new BitSet(128)).or(URI.alphanumChar);
        URI.schemeChar.set(43);
        URI.schemeChar.set(45);
        URI.schemeChar.set(46);
        (opaqueChar = new BitSet(128)).or(URI.uricChar);
        (hostChar = new BitSet(128)).or(URI.alphanumChar);
        URI.hostChar.set(45);
        URI.hostChar.set(46);
        (reg_nameChar = new BitSet(128)).or(URI.unreservedChar);
        URI.reg_nameChar.set(36);
        URI.reg_nameChar.set(44);
        URI.reg_nameChar.set(59);
        URI.reg_nameChar.set(58);
        URI.reg_nameChar.set(64);
        URI.reg_nameChar.set(38);
        URI.reg_nameChar.set(61);
        URI.reg_nameChar.set(43);
        (resvdSchemeChar = new BitSet(128)).set(58);
        (resvdUIChar = new BitSet(128)).set(64);
        (resvdHostChar = new BitSet(128)).set(58);
        URI.resvdHostChar.set(47);
        URI.resvdHostChar.set(63);
        URI.resvdHostChar.set(35);
        (resvdPathChar = new BitSet(128)).set(47);
        URI.resvdPathChar.set(59);
        URI.resvdPathChar.set(63);
        URI.resvdPathChar.set(35);
        (resvdQueryChar = new BitSet(128)).set(35);
        (escpdPathChar = new BitSet(128)).or(URI.pcharChar);
        URI.escpdPathChar.set(37);
        URI.escpdPathChar.set(47);
        URI.escpdPathChar.set(59);
        (escpdQueryChar = new BitSet(128)).or(URI.uricChar);
        URI.escpdQueryChar.clear(35);
        (escpdFragChar = new BitSet(128)).or(URI.uricChar);
        hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        nl = System.getProperty("line.separator");
    }
}
