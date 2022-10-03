package org.apache.commons.httpclient;

import java.util.Hashtable;
import java.util.Locale;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.util.Arrays;
import java.util.BitSet;
import java.io.Serializable;

public class URI implements Cloneable, Comparable, Serializable
{
    static final long serialVersionUID = 604752400577948726L;
    protected int hash;
    protected char[] _uri;
    protected String protocolCharset;
    protected static String defaultProtocolCharset;
    protected static String defaultDocumentCharset;
    protected static String defaultDocumentCharsetByLocale;
    protected static String defaultDocumentCharsetByPlatform;
    protected char[] _scheme;
    protected char[] _opaque;
    protected char[] _authority;
    protected char[] _userinfo;
    protected char[] _host;
    protected int _port;
    protected char[] _path;
    protected char[] _query;
    protected char[] _fragment;
    protected static final char[] rootPath;
    protected static final BitSet percent;
    protected static final BitSet digit;
    protected static final BitSet alpha;
    protected static final BitSet alphanum;
    protected static final BitSet hex;
    protected static final BitSet escaped;
    protected static final BitSet mark;
    protected static final BitSet unreserved;
    protected static final BitSet reserved;
    protected static final BitSet uric;
    protected static final BitSet fragment;
    protected static final BitSet query;
    protected static final BitSet pchar;
    protected static final BitSet param;
    protected static final BitSet segment;
    protected static final BitSet path_segments;
    protected static final BitSet abs_path;
    protected static final BitSet uric_no_slash;
    protected static final BitSet opaque_part;
    protected static final BitSet path;
    protected static final BitSet port;
    protected static final BitSet IPv4address;
    protected static final BitSet IPv6address;
    protected static final BitSet IPv6reference;
    protected static final BitSet toplabel;
    protected static final BitSet domainlabel;
    protected static final BitSet hostname;
    protected static final BitSet host;
    protected static final BitSet hostport;
    protected static final BitSet userinfo;
    public static final BitSet within_userinfo;
    protected static final BitSet server;
    protected static final BitSet reg_name;
    protected static final BitSet authority;
    protected static final BitSet scheme;
    protected static final BitSet rel_segment;
    protected static final BitSet rel_path;
    protected static final BitSet net_path;
    protected static final BitSet hier_part;
    protected static final BitSet relativeURI;
    protected static final BitSet absoluteURI;
    protected static final BitSet URI_reference;
    public static final BitSet control;
    public static final BitSet space;
    public static final BitSet delims;
    public static final BitSet unwise;
    public static final BitSet disallowed_rel_path;
    public static final BitSet disallowed_opaque_part;
    public static final BitSet allowed_authority;
    public static final BitSet allowed_opaque_part;
    public static final BitSet allowed_reg_name;
    public static final BitSet allowed_userinfo;
    public static final BitSet allowed_within_userinfo;
    public static final BitSet allowed_IPv6reference;
    public static final BitSet allowed_host;
    public static final BitSet allowed_within_authority;
    public static final BitSet allowed_abs_path;
    public static final BitSet allowed_rel_path;
    public static final BitSet allowed_within_path;
    public static final BitSet allowed_query;
    public static final BitSet allowed_within_query;
    public static final BitSet allowed_fragment;
    protected boolean _is_hier_part;
    protected boolean _is_opaque_part;
    protected boolean _is_net_path;
    protected boolean _is_abs_path;
    protected boolean _is_rel_path;
    protected boolean _is_reg_name;
    protected boolean _is_server;
    protected boolean _is_hostname;
    protected boolean _is_IPv4address;
    protected boolean _is_IPv6reference;
    
    protected URI() {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
    }
    
    public URI(final String s, final boolean escaped, final String charset) throws URIException, NullPointerException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.protocolCharset = charset;
        this.parseUriReference(s, escaped);
    }
    
    public URI(final String s, final boolean escaped) throws URIException, NullPointerException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.parseUriReference(s, escaped);
    }
    
    public URI(final char[] escaped, final String charset) throws URIException, NullPointerException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.protocolCharset = charset;
        this.parseUriReference(new String(escaped), true);
    }
    
    public URI(final char[] escaped) throws URIException, NullPointerException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.parseUriReference(new String(escaped), true);
    }
    
    public URI(final String original, final String charset) throws URIException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.protocolCharset = charset;
        this.parseUriReference(original, false);
    }
    
    public URI(final String original) throws URIException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        this.parseUriReference(original, false);
    }
    
    public URI(final String scheme, final String schemeSpecificPart, final String fragment) throws URIException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        if (scheme == null) {
            throw new URIException(1, "scheme required");
        }
        final char[] s = scheme.toLowerCase().toCharArray();
        if (this.validate(s, URI.scheme)) {
            this._scheme = s;
            this._opaque = encode(schemeSpecificPart, URI.allowed_opaque_part, this.getProtocolCharset());
            this._is_opaque_part = true;
            this._fragment = (char[])((fragment == null) ? null : fragment.toCharArray());
            this.setURI();
            return;
        }
        throw new URIException(1, "incorrect scheme");
    }
    
    public URI(final String scheme, final String authority, final String path, final String query, final String fragment) throws URIException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        final StringBuffer buff = new StringBuffer();
        if (scheme != null) {
            buff.append(scheme);
            buff.append(':');
        }
        if (authority != null) {
            buff.append("//");
            buff.append(authority);
        }
        if (path != null) {
            if ((scheme != null || authority != null) && !path.startsWith("/")) {
                throw new URIException(1, "abs_path requested");
            }
            buff.append(path);
        }
        if (query != null) {
            buff.append('?');
            buff.append(query);
        }
        if (fragment != null) {
            buff.append('#');
            buff.append(fragment);
        }
        this.parseUriReference(buff.toString(), false);
    }
    
    public URI(final String scheme, final String userinfo, final String host, final int port) throws URIException {
        this(scheme, userinfo, host, port, null, null, null);
    }
    
    public URI(final String scheme, final String userinfo, final String host, final int port, final String path) throws URIException {
        this(scheme, userinfo, host, port, path, null, null);
    }
    
    public URI(final String scheme, final String userinfo, final String host, final int port, final String path, final String query) throws URIException {
        this(scheme, userinfo, host, port, path, query, null);
    }
    
    public URI(final String scheme, final String userinfo, final String host, final int port, final String path, final String query, final String fragment) throws URIException {
        this(scheme, (host == null) ? null : (((userinfo != null) ? (userinfo + '@') : "") + host + ((port != -1) ? (":" + port) : "")), path, query, fragment);
    }
    
    public URI(final String scheme, final String host, final String path, final String fragment) throws URIException {
        this(scheme, host, path, null, fragment);
    }
    
    public URI(final URI base, final String relative) throws URIException {
        this(base, new URI(relative));
    }
    
    public URI(final URI base, final String relative, final boolean escaped) throws URIException {
        this(base, new URI(relative, escaped));
    }
    
    public URI(final URI base, final URI relative) throws URIException {
        this.hash = 0;
        this._uri = null;
        this.protocolCharset = null;
        this._scheme = null;
        this._opaque = null;
        this._authority = null;
        this._userinfo = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._query = null;
        this._fragment = null;
        if (base._scheme == null) {
            throw new URIException(1, "base URI required");
        }
        if (base._scheme != null) {
            this._scheme = base._scheme;
            this._authority = base._authority;
            this._is_net_path = base._is_net_path;
        }
        if (base._is_opaque_part || relative._is_opaque_part) {
            this._scheme = base._scheme;
            this._is_opaque_part = (base._is_opaque_part || relative._is_opaque_part);
            this._opaque = relative._opaque;
            this._fragment = relative._fragment;
            this.setURI();
            return;
        }
        final boolean schemesEqual = Arrays.equals(base._scheme, relative._scheme);
        if (relative._scheme != null && (!schemesEqual || relative._authority != null)) {
            this._scheme = relative._scheme;
            this._is_net_path = relative._is_net_path;
            this._authority = relative._authority;
            if (relative._is_server) {
                this._is_server = relative._is_server;
                this._userinfo = relative._userinfo;
                this._host = relative._host;
                this._port = relative._port;
            }
            else if (relative._is_reg_name) {
                this._is_reg_name = relative._is_reg_name;
            }
            this._is_abs_path = relative._is_abs_path;
            this._is_rel_path = relative._is_rel_path;
            this._path = relative._path;
        }
        else if (base._authority != null && relative._scheme == null) {
            this._is_net_path = base._is_net_path;
            this._authority = base._authority;
            if (base._is_server) {
                this._is_server = base._is_server;
                this._userinfo = base._userinfo;
                this._host = base._host;
                this._port = base._port;
            }
            else if (base._is_reg_name) {
                this._is_reg_name = base._is_reg_name;
            }
        }
        if (relative._authority != null) {
            this._is_net_path = relative._is_net_path;
            this._authority = relative._authority;
            if (relative._is_server) {
                this._is_server = relative._is_server;
                this._userinfo = relative._userinfo;
                this._host = relative._host;
                this._port = relative._port;
            }
            else if (relative._is_reg_name) {
                this._is_reg_name = relative._is_reg_name;
            }
            this._is_abs_path = relative._is_abs_path;
            this._is_rel_path = relative._is_rel_path;
            this._path = relative._path;
        }
        if (relative._authority == null && (relative._scheme == null || schemesEqual)) {
            if ((relative._path == null || relative._path.length == 0) && relative._query == null) {
                this._path = base._path;
                this._query = base._query;
            }
            else {
                this._path = this.resolvePath(base._path, relative._path);
            }
        }
        if (relative._query != null) {
            this._query = relative._query;
        }
        if (relative._fragment != null) {
            this._fragment = relative._fragment;
        }
        this.setURI();
        this.parseUriReference(new String(this._uri), true);
    }
    
    protected static char[] encode(final String original, final BitSet allowed, final String charset) throws URIException {
        if (original == null) {
            throw new IllegalArgumentException("Original string may not be null");
        }
        if (allowed == null) {
            throw new IllegalArgumentException("Allowed bitset may not be null");
        }
        final byte[] rawdata = URLCodec.encodeUrl(allowed, EncodingUtil.getBytes(original, charset));
        return EncodingUtil.getAsciiString(rawdata).toCharArray();
    }
    
    protected static String decode(final char[] component, final String charset) throws URIException {
        if (component == null) {
            throw new IllegalArgumentException("Component array of chars may not be null");
        }
        return decode(new String(component), charset);
    }
    
    protected static String decode(final String component, final String charset) throws URIException {
        if (component == null) {
            throw new IllegalArgumentException("Component array of chars may not be null");
        }
        byte[] rawdata = null;
        try {
            rawdata = URLCodec.decodeUrl(EncodingUtil.getAsciiBytes(component));
        }
        catch (final DecoderException e) {
            throw new URIException(e.getMessage());
        }
        return EncodingUtil.getString(rawdata, charset);
    }
    
    protected boolean prevalidate(final String component, final BitSet disallowed) {
        if (component == null) {
            return false;
        }
        final char[] target = component.toCharArray();
        for (int i = 0; i < target.length; ++i) {
            if (disallowed.get(target[i])) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean validate(final char[] component, final BitSet generous) {
        return this.validate(component, 0, -1, generous);
    }
    
    protected boolean validate(final char[] component, final int soffset, int eoffset, final BitSet generous) {
        if (eoffset == -1) {
            eoffset = component.length - 1;
        }
        for (int i = soffset; i <= eoffset; ++i) {
            if (!generous.get(component[i])) {
                return false;
            }
        }
        return true;
    }
    
    protected void parseUriReference(final String original, final boolean escaped) throws URIException {
        if (original == null) {
            throw new URIException("URI-Reference required");
        }
        String tmp = original.trim();
        int length = tmp.length();
        if (length > 0) {
            final char[] firstDelimiter = { tmp.charAt(0) };
            if (this.validate(firstDelimiter, URI.delims) && length >= 2) {
                final char[] lastDelimiter = { tmp.charAt(length - 1) };
                if (this.validate(lastDelimiter, URI.delims)) {
                    tmp = tmp.substring(1, length - 1);
                    length -= 2;
                }
            }
        }
        int from = 0;
        boolean isStartedFromPath = false;
        final int atColon = tmp.indexOf(58);
        final int atSlash = tmp.indexOf(47);
        if ((atColon <= 0 && !tmp.startsWith("//")) || (atSlash >= 0 && atSlash < atColon)) {
            isStartedFromPath = true;
        }
        int at = this.indexFirstOf(tmp, isStartedFromPath ? "/?#" : ":/?#", from);
        if (at == -1) {
            at = 0;
        }
        if (at > 0 && at < length && tmp.charAt(at) == ':') {
            final char[] target = tmp.substring(0, at).toLowerCase().toCharArray();
            if (!this.validate(target, URI.scheme)) {
                throw new URIException("incorrect scheme");
            }
            this._scheme = target;
            from = ++at;
        }
        final boolean b = false;
        this._is_hier_part = b;
        this._is_rel_path = b;
        this._is_abs_path = b;
        this._is_net_path = b;
        if (0 <= at && at < length && tmp.charAt(at) == '/') {
            this._is_hier_part = true;
            if (at + 2 < length && tmp.charAt(at + 1) == '/' && !isStartedFromPath) {
                int next = this.indexFirstOf(tmp, "/?#", at + 2);
                if (next == -1) {
                    next = ((tmp.substring(at + 2).length() == 0) ? (at + 2) : tmp.length());
                }
                this.parseAuthority(tmp.substring(at + 2, next), escaped);
                at = (from = next);
                this._is_net_path = true;
            }
            if (from == at) {
                this._is_abs_path = true;
            }
        }
        if (from < length) {
            int next = this.indexFirstOf(tmp, "?#", from);
            if (next == -1) {
                next = tmp.length();
            }
            if (!this._is_abs_path) {
                if ((!escaped && this.prevalidate(tmp.substring(from, next), URI.disallowed_rel_path)) || (escaped && this.validate(tmp.substring(from, next).toCharArray(), URI.rel_path))) {
                    this._is_rel_path = true;
                }
                else if ((!escaped && this.prevalidate(tmp.substring(from, next), URI.disallowed_opaque_part)) || (escaped && this.validate(tmp.substring(from, next).toCharArray(), URI.opaque_part))) {
                    this._is_opaque_part = true;
                }
                else {
                    this._path = null;
                }
            }
            final String s = tmp.substring(from, next);
            if (escaped) {
                this.setRawPath(s.toCharArray());
            }
            else {
                this.setPath(s);
            }
            at = next;
        }
        final String charset = this.getProtocolCharset();
        if (0 <= at && at + 1 < length && tmp.charAt(at) == '?') {
            int next2 = tmp.indexOf(35, at + 1);
            if (next2 == -1) {
                next2 = tmp.length();
            }
            if (escaped) {
                this._query = tmp.substring(at + 1, next2).toCharArray();
                if (!this.validate(this._query, URI.uric)) {
                    throw new URIException("Invalid query");
                }
            }
            else {
                this._query = encode(tmp.substring(at + 1, next2), URI.allowed_query, charset);
            }
            at = next2;
        }
        if (0 <= at && at + 1 <= length && tmp.charAt(at) == '#') {
            if (at + 1 == length) {
                this._fragment = "".toCharArray();
            }
            else {
                this._fragment = (escaped ? tmp.substring(at + 1).toCharArray() : encode(tmp.substring(at + 1), URI.allowed_fragment, charset));
            }
        }
        this.setURI();
    }
    
    protected int indexFirstOf(final String s, final String delims) {
        return this.indexFirstOf(s, delims, -1);
    }
    
    protected int indexFirstOf(final String s, final String delims, int offset) {
        if (s == null || s.length() == 0) {
            return -1;
        }
        if (delims == null || delims.length() == 0) {
            return -1;
        }
        if (offset < 0) {
            offset = 0;
        }
        else if (offset > s.length()) {
            return -1;
        }
        int min = s.length();
        final char[] delim = delims.toCharArray();
        for (int i = 0; i < delim.length; ++i) {
            final int at = s.indexOf(delim[i], offset);
            if (at >= 0 && at < min) {
                min = at;
            }
        }
        return (min == s.length()) ? -1 : min;
    }
    
    protected int indexFirstOf(final char[] s, final char delim) {
        return this.indexFirstOf(s, delim, 0);
    }
    
    protected int indexFirstOf(final char[] s, final char delim, int offset) {
        if (s == null || s.length == 0) {
            return -1;
        }
        if (offset < 0) {
            offset = 0;
        }
        else if (offset > s.length) {
            return -1;
        }
        for (int i = offset; i < s.length; ++i) {
            if (s[i] == delim) {
                return i;
            }
        }
        return -1;
    }
    
    protected void parseAuthority(final String original, final boolean escaped) throws URIException {
        final boolean is_reg_name = false;
        this._is_IPv6reference = is_reg_name;
        this._is_IPv4address = is_reg_name;
        this._is_hostname = is_reg_name;
        this._is_server = is_reg_name;
        this._is_reg_name = is_reg_name;
        final String charset = this.getProtocolCharset();
        boolean hasPort = true;
        int from = 0;
        int next = original.indexOf(64);
        if (next != -1) {
            this._userinfo = (escaped ? original.substring(0, next).toCharArray() : encode(original.substring(0, next), URI.allowed_userinfo, charset));
            from = next + 1;
        }
        next = original.indexOf(91, from);
        if (next >= from) {
            next = original.indexOf(93, from);
            if (next == -1) {
                throw new URIException(1, "IPv6reference");
            }
            ++next;
            this._host = (escaped ? original.substring(from, next).toCharArray() : encode(original.substring(from, next), URI.allowed_IPv6reference, charset));
            this._is_IPv6reference = true;
        }
        else {
            next = original.indexOf(58, from);
            if (next == -1) {
                next = original.length();
                hasPort = false;
            }
            this._host = original.substring(from, next).toCharArray();
            if (this.validate(this._host, URI.IPv4address)) {
                this._is_IPv4address = true;
            }
            else if (this.validate(this._host, URI.hostname)) {
                this._is_hostname = true;
            }
            else {
                this._is_reg_name = true;
            }
        }
        if (this._is_reg_name) {
            final boolean b = false;
            this._is_IPv6reference = b;
            this._is_IPv4address = b;
            this._is_hostname = b;
            this._is_server = b;
            if (escaped) {
                this._authority = original.toCharArray();
                if (!this.validate(this._authority, URI.reg_name)) {
                    throw new URIException("Invalid authority");
                }
            }
            else {
                this._authority = encode(original, URI.allowed_reg_name, charset);
            }
        }
        else {
            if (original.length() - 1 > next && hasPort && original.charAt(next) == ':') {
                from = next + 1;
                try {
                    this._port = Integer.parseInt(original.substring(from));
                }
                catch (final NumberFormatException error) {
                    throw new URIException(1, "invalid port number");
                }
            }
            final StringBuffer buf = new StringBuffer();
            if (this._userinfo != null) {
                buf.append(this._userinfo);
                buf.append('@');
            }
            if (this._host != null) {
                buf.append(this._host);
                if (this._port != -1) {
                    buf.append(':');
                    buf.append(this._port);
                }
            }
            this._authority = buf.toString().toCharArray();
            this._is_server = true;
        }
    }
    
    protected void setURI() {
        final StringBuffer buf = new StringBuffer();
        if (this._scheme != null) {
            buf.append(this._scheme);
            buf.append(':');
        }
        if (this._is_net_path) {
            buf.append("//");
            if (this._authority != null) {
                buf.append(this._authority);
            }
        }
        if (this._opaque != null && this._is_opaque_part) {
            buf.append(this._opaque);
        }
        else if (this._path != null && this._path.length != 0) {
            buf.append(this._path);
        }
        if (this._query != null) {
            buf.append('?');
            buf.append(this._query);
        }
        this._uri = buf.toString().toCharArray();
        this.hash = 0;
    }
    
    public boolean isAbsoluteURI() {
        return this._scheme != null;
    }
    
    public boolean isRelativeURI() {
        return this._scheme == null;
    }
    
    public boolean isHierPart() {
        return this._is_hier_part;
    }
    
    public boolean isOpaquePart() {
        return this._is_opaque_part;
    }
    
    public boolean isNetPath() {
        return this._is_net_path || this._authority != null;
    }
    
    public boolean isAbsPath() {
        return this._is_abs_path;
    }
    
    public boolean isRelPath() {
        return this._is_rel_path;
    }
    
    public boolean hasAuthority() {
        return this._authority != null || this._is_net_path;
    }
    
    public boolean isRegName() {
        return this._is_reg_name;
    }
    
    public boolean isServer() {
        return this._is_server;
    }
    
    public boolean hasUserinfo() {
        return this._userinfo != null;
    }
    
    public boolean isHostname() {
        return this._is_hostname;
    }
    
    public boolean isIPv4address() {
        return this._is_IPv4address;
    }
    
    public boolean isIPv6reference() {
        return this._is_IPv6reference;
    }
    
    public boolean hasQuery() {
        return this._query != null;
    }
    
    public boolean hasFragment() {
        return this._fragment != null;
    }
    
    public static void setDefaultProtocolCharset(final String charset) throws DefaultCharsetChanged {
        URI.defaultProtocolCharset = charset;
        throw new DefaultCharsetChanged(1, "the default protocol charset changed");
    }
    
    public static String getDefaultProtocolCharset() {
        return URI.defaultProtocolCharset;
    }
    
    public String getProtocolCharset() {
        return (this.protocolCharset != null) ? this.protocolCharset : URI.defaultProtocolCharset;
    }
    
    public static void setDefaultDocumentCharset(final String charset) throws DefaultCharsetChanged {
        URI.defaultDocumentCharset = charset;
        throw new DefaultCharsetChanged(2, "the default document charset changed");
    }
    
    public static String getDefaultDocumentCharset() {
        return URI.defaultDocumentCharset;
    }
    
    public static String getDefaultDocumentCharsetByLocale() {
        return URI.defaultDocumentCharsetByLocale;
    }
    
    public static String getDefaultDocumentCharsetByPlatform() {
        return URI.defaultDocumentCharsetByPlatform;
    }
    
    public char[] getRawScheme() {
        return this._scheme;
    }
    
    public String getScheme() {
        return (this._scheme == null) ? null : new String(this._scheme);
    }
    
    public void setRawAuthority(final char[] escapedAuthority) throws URIException, NullPointerException {
        this.parseAuthority(new String(escapedAuthority), true);
        this.setURI();
    }
    
    public void setEscapedAuthority(final String escapedAuthority) throws URIException {
        this.parseAuthority(escapedAuthority, true);
        this.setURI();
    }
    
    public char[] getRawAuthority() {
        return this._authority;
    }
    
    public String getEscapedAuthority() {
        return (this._authority == null) ? null : new String(this._authority);
    }
    
    public String getAuthority() throws URIException {
        return (this._authority == null) ? null : decode(this._authority, this.getProtocolCharset());
    }
    
    public char[] getRawUserinfo() {
        return this._userinfo;
    }
    
    public String getEscapedUserinfo() {
        return (this._userinfo == null) ? null : new String(this._userinfo);
    }
    
    public String getUserinfo() throws URIException {
        return (this._userinfo == null) ? null : decode(this._userinfo, this.getProtocolCharset());
    }
    
    public char[] getRawHost() {
        return this._host;
    }
    
    public String getHost() throws URIException {
        if (this._host != null) {
            return decode(this._host, this.getProtocolCharset());
        }
        return null;
    }
    
    public int getPort() {
        return this._port;
    }
    
    public void setRawPath(char[] escapedPath) throws URIException {
        if (escapedPath == null || escapedPath.length == 0) {
            final char[] array = escapedPath;
            this._opaque = array;
            this._path = array;
            this.setURI();
            return;
        }
        escapedPath = this.removeFragmentIdentifier(escapedPath);
        if (this._is_net_path || this._is_abs_path) {
            if (escapedPath[0] != '/') {
                throw new URIException(1, "not absolute path");
            }
            if (!this.validate(escapedPath, URI.abs_path)) {
                throw new URIException(3, "escaped absolute path not valid");
            }
            this._path = escapedPath;
        }
        else if (this._is_rel_path) {
            final int at = this.indexFirstOf(escapedPath, '/');
            if (at == 0) {
                throw new URIException(1, "incorrect path");
            }
            if ((at > 0 && !this.validate(escapedPath, 0, at - 1, URI.rel_segment) && !this.validate(escapedPath, at, -1, URI.abs_path)) || (at < 0 && !this.validate(escapedPath, 0, -1, URI.rel_segment))) {
                throw new URIException(3, "escaped relative path not valid");
            }
            this._path = escapedPath;
        }
        else {
            if (!this._is_opaque_part) {
                throw new URIException(1, "incorrect path");
            }
            if (!URI.uric_no_slash.get(escapedPath[0]) && !this.validate(escapedPath, 1, -1, URI.uric)) {
                throw new URIException(3, "escaped opaque part not valid");
            }
            this._opaque = escapedPath;
        }
        this.setURI();
    }
    
    public void setEscapedPath(final String escapedPath) throws URIException {
        if (escapedPath == null) {
            final char[] array = null;
            this._opaque = array;
            this._path = array;
            this.setURI();
            return;
        }
        this.setRawPath(escapedPath.toCharArray());
    }
    
    public void setPath(final String path) throws URIException {
        if (path == null || path.length() == 0) {
            final char[] array = (char[])((path == null) ? null : path.toCharArray());
            this._opaque = array;
            this._path = array;
            this.setURI();
            return;
        }
        final String charset = this.getProtocolCharset();
        if (this._is_net_path || this._is_abs_path) {
            this._path = encode(path, URI.allowed_abs_path, charset);
        }
        else if (this._is_rel_path) {
            final StringBuffer buff = new StringBuffer(path.length());
            final int at = path.indexOf(47);
            if (at == 0) {
                throw new URIException(1, "incorrect relative path");
            }
            if (at > 0) {
                buff.append(encode(path.substring(0, at), URI.allowed_rel_path, charset));
                buff.append(encode(path.substring(at), URI.allowed_abs_path, charset));
            }
            else {
                buff.append(encode(path, URI.allowed_rel_path, charset));
            }
            this._path = buff.toString().toCharArray();
        }
        else {
            if (!this._is_opaque_part) {
                throw new URIException(1, "incorrect path");
            }
            final StringBuffer buf = new StringBuffer();
            buf.insert(0, encode(path.substring(0, 1), URI.uric_no_slash, charset));
            buf.insert(1, encode(path.substring(1), URI.uric, charset));
            this._opaque = buf.toString().toCharArray();
        }
        this.setURI();
    }
    
    protected char[] resolvePath(char[] basePath, final char[] relPath) throws URIException {
        final String base = (basePath == null) ? "" : new String(basePath);
        if (relPath == null || relPath.length == 0) {
            return this.normalize(basePath);
        }
        if (relPath[0] == '/') {
            return this.normalize(relPath);
        }
        final int at = base.lastIndexOf(47);
        if (at != -1) {
            basePath = base.substring(0, at + 1).toCharArray();
        }
        final StringBuffer buff = new StringBuffer(base.length() + relPath.length);
        buff.append((at != -1) ? base.substring(0, at + 1) : "/");
        buff.append(relPath);
        return this.normalize(buff.toString().toCharArray());
    }
    
    protected char[] getRawCurrentHierPath(final char[] path) throws URIException {
        if (this._is_opaque_part) {
            throw new URIException(1, "no hierarchy level");
        }
        if (path == null) {
            throw new URIException(1, "empty path");
        }
        final String buff = new String(path);
        final int first = buff.indexOf(47);
        final int last = buff.lastIndexOf(47);
        if (last == 0) {
            return URI.rootPath;
        }
        if (first != last && last != -1) {
            return buff.substring(0, last).toCharArray();
        }
        return path;
    }
    
    public char[] getRawCurrentHierPath() throws URIException {
        return (char[])((this._path == null) ? null : this.getRawCurrentHierPath(this._path));
    }
    
    public String getEscapedCurrentHierPath() throws URIException {
        final char[] path = this.getRawCurrentHierPath();
        return (path == null) ? null : new String(path);
    }
    
    public String getCurrentHierPath() throws URIException {
        final char[] path = this.getRawCurrentHierPath();
        return (path == null) ? null : decode(path, this.getProtocolCharset());
    }
    
    public char[] getRawAboveHierPath() throws URIException {
        final char[] path = this.getRawCurrentHierPath();
        return (char[])((path == null) ? null : this.getRawCurrentHierPath(path));
    }
    
    public String getEscapedAboveHierPath() throws URIException {
        final char[] path = this.getRawAboveHierPath();
        return (path == null) ? null : new String(path);
    }
    
    public String getAboveHierPath() throws URIException {
        final char[] path = this.getRawAboveHierPath();
        return (path == null) ? null : decode(path, this.getProtocolCharset());
    }
    
    public char[] getRawPath() {
        return this._is_opaque_part ? this._opaque : this._path;
    }
    
    public String getEscapedPath() {
        final char[] path = this.getRawPath();
        return (path == null) ? null : new String(path);
    }
    
    public String getPath() throws URIException {
        final char[] path = this.getRawPath();
        return (path == null) ? null : decode(path, this.getProtocolCharset());
    }
    
    public char[] getRawName() {
        if (this._path == null) {
            return null;
        }
        int at = 0;
        for (int i = this._path.length - 1; i >= 0; --i) {
            if (this._path[i] == '/') {
                at = i + 1;
                break;
            }
        }
        final int len = this._path.length - at;
        final char[] basename = new char[len];
        System.arraycopy(this._path, at, basename, 0, len);
        return basename;
    }
    
    public String getEscapedName() {
        final char[] basename = this.getRawName();
        return (basename == null) ? null : new String(basename);
    }
    
    public String getName() throws URIException {
        final char[] basename = this.getRawName();
        return (basename == null) ? null : decode(this.getRawName(), this.getProtocolCharset());
    }
    
    public char[] getRawPathQuery() {
        if (this._path == null && this._query == null) {
            return null;
        }
        final StringBuffer buff = new StringBuffer();
        if (this._path != null) {
            buff.append(this._path);
        }
        if (this._query != null) {
            buff.append('?');
            buff.append(this._query);
        }
        return buff.toString().toCharArray();
    }
    
    public String getEscapedPathQuery() {
        final char[] rawPathQuery = this.getRawPathQuery();
        return (rawPathQuery == null) ? null : new String(rawPathQuery);
    }
    
    public String getPathQuery() throws URIException {
        final char[] rawPathQuery = this.getRawPathQuery();
        return (rawPathQuery == null) ? null : decode(rawPathQuery, this.getProtocolCharset());
    }
    
    public void setRawQuery(char[] escapedQuery) throws URIException {
        if (escapedQuery == null || escapedQuery.length == 0) {
            this._query = escapedQuery;
            this.setURI();
            return;
        }
        escapedQuery = this.removeFragmentIdentifier(escapedQuery);
        if (!this.validate(escapedQuery, URI.query)) {
            throw new URIException(3, "escaped query not valid");
        }
        this._query = escapedQuery;
        this.setURI();
    }
    
    public void setEscapedQuery(final String escapedQuery) throws URIException {
        if (escapedQuery == null) {
            this._query = null;
            this.setURI();
            return;
        }
        this.setRawQuery(escapedQuery.toCharArray());
    }
    
    public void setQuery(final String query) throws URIException {
        if (query == null || query.length() == 0) {
            this._query = (char[])((query == null) ? null : query.toCharArray());
            this.setURI();
            return;
        }
        this.setRawQuery(encode(query, URI.allowed_query, this.getProtocolCharset()));
    }
    
    public char[] getRawQuery() {
        return this._query;
    }
    
    public String getEscapedQuery() {
        return (this._query == null) ? null : new String(this._query);
    }
    
    public String getQuery() throws URIException {
        return (this._query == null) ? null : decode(this._query, this.getProtocolCharset());
    }
    
    public void setRawFragment(final char[] escapedFragment) throws URIException {
        if (escapedFragment == null || escapedFragment.length == 0) {
            this._fragment = escapedFragment;
            this.hash = 0;
            return;
        }
        if (!this.validate(escapedFragment, URI.fragment)) {
            throw new URIException(3, "escaped fragment not valid");
        }
        this._fragment = escapedFragment;
        this.hash = 0;
    }
    
    public void setEscapedFragment(final String escapedFragment) throws URIException {
        if (escapedFragment == null) {
            this._fragment = null;
            this.hash = 0;
            return;
        }
        this.setRawFragment(escapedFragment.toCharArray());
    }
    
    public void setFragment(final String fragment) throws URIException {
        if (fragment == null || fragment.length() == 0) {
            this._fragment = (char[])((fragment == null) ? null : fragment.toCharArray());
            this.hash = 0;
            return;
        }
        this._fragment = encode(fragment, URI.allowed_fragment, this.getProtocolCharset());
        this.hash = 0;
    }
    
    public char[] getRawFragment() {
        return this._fragment;
    }
    
    public String getEscapedFragment() {
        return (this._fragment == null) ? null : new String(this._fragment);
    }
    
    public String getFragment() throws URIException {
        return (this._fragment == null) ? null : decode(this._fragment, this.getProtocolCharset());
    }
    
    protected char[] removeFragmentIdentifier(char[] component) {
        if (component == null) {
            return null;
        }
        final int lastIndex = new String(component).indexOf(35);
        if (lastIndex != -1) {
            component = new String(component).substring(0, lastIndex).toCharArray();
        }
        return component;
    }
    
    protected char[] normalize(final char[] path) throws URIException {
        if (path == null) {
            return null;
        }
        String normalized = new String(path);
        if (normalized.startsWith("./")) {
            normalized = normalized.substring(1);
        }
        else if (normalized.startsWith("../")) {
            normalized = normalized.substring(2);
        }
        else if (normalized.startsWith("..")) {
            normalized = normalized.substring(2);
        }
        for (int index = -1; (index = normalized.indexOf("/./")) != -1; normalized = normalized.substring(0, index) + normalized.substring(index + 2)) {}
        if (normalized.endsWith("/.")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        int startIndex = 0;
        int index;
        while ((index = normalized.indexOf("/../", startIndex)) != -1) {
            final int slashIndex = normalized.lastIndexOf(47, index - 1);
            if (slashIndex >= 0) {
                normalized = normalized.substring(0, slashIndex) + normalized.substring(index + 3);
            }
            else {
                startIndex = index + 3;
            }
        }
        if (normalized.endsWith("/..")) {
            final int slashIndex = normalized.lastIndexOf(47, normalized.length() - 4);
            if (slashIndex >= 0) {
                normalized = normalized.substring(0, slashIndex + 1);
            }
        }
        while ((index = normalized.indexOf("/../")) != -1) {
            final int slashIndex = normalized.lastIndexOf(47, index - 1);
            if (slashIndex >= 0) {
                break;
            }
            normalized = normalized.substring(index + 3);
        }
        if (normalized.endsWith("/..")) {
            final int slashIndex = normalized.lastIndexOf(47, normalized.length() - 4);
            if (slashIndex < 0) {
                normalized = "/";
            }
        }
        return normalized.toCharArray();
    }
    
    public void normalize() throws URIException {
        if (this.isAbsPath()) {
            this._path = this.normalize(this._path);
            this.setURI();
        }
    }
    
    protected boolean equals(final char[] first, final char[] second) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof URI)) {
            return false;
        }
        final URI another = (URI)obj;
        return this.equals(this._scheme, another._scheme) && this.equals(this._opaque, another._opaque) && this.equals(this._authority, another._authority) && this.equals(this._path, another._path) && this.equals(this._query, another._query) && this.equals(this._fragment, another._fragment);
    }
    
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }
    
    public int hashCode() {
        if (this.hash == 0) {
            char[] c = this._uri;
            if (c != null) {
                for (int i = 0, len = c.length; i < len; ++i) {
                    this.hash = 31 * this.hash + c[i];
                }
            }
            c = this._fragment;
            if (c != null) {
                for (int i = 0, len = c.length; i < len; ++i) {
                    this.hash = 31 * this.hash + c[i];
                }
            }
        }
        return this.hash;
    }
    
    public int compareTo(final Object obj) throws ClassCastException {
        final URI another = (URI)obj;
        if (!this.equals(this._authority, another.getRawAuthority())) {
            return -1;
        }
        return this.toString().compareTo(another.toString());
    }
    
    public synchronized Object clone() throws CloneNotSupportedException {
        final URI instance = (URI)super.clone();
        instance._uri = this._uri;
        instance._scheme = this._scheme;
        instance._opaque = this._opaque;
        instance._authority = this._authority;
        instance._userinfo = this._userinfo;
        instance._host = this._host;
        instance._port = this._port;
        instance._path = this._path;
        instance._query = this._query;
        instance._fragment = this._fragment;
        instance.protocolCharset = this.protocolCharset;
        instance._is_hier_part = this._is_hier_part;
        instance._is_opaque_part = this._is_opaque_part;
        instance._is_net_path = this._is_net_path;
        instance._is_abs_path = this._is_abs_path;
        instance._is_rel_path = this._is_rel_path;
        instance._is_reg_name = this._is_reg_name;
        instance._is_server = this._is_server;
        instance._is_hostname = this._is_hostname;
        instance._is_IPv4address = this._is_IPv4address;
        instance._is_IPv6reference = this._is_IPv6reference;
        return instance;
    }
    
    public char[] getRawURI() {
        return this._uri;
    }
    
    public String getEscapedURI() {
        return (this._uri == null) ? null : new String(this._uri);
    }
    
    public String getURI() throws URIException {
        return (this._uri == null) ? null : decode(this._uri, this.getProtocolCharset());
    }
    
    public char[] getRawURIReference() {
        if (this._fragment == null) {
            return this._uri;
        }
        if (this._uri == null) {
            return this._fragment;
        }
        final String uriReference = new String(this._uri) + "#" + new String(this._fragment);
        return uriReference.toCharArray();
    }
    
    public String getEscapedURIReference() {
        final char[] uriReference = this.getRawURIReference();
        return (uriReference == null) ? null : new String(uriReference);
    }
    
    public String getURIReference() throws URIException {
        final char[] uriReference = this.getRawURIReference();
        return (uriReference == null) ? null : decode(uriReference, this.getProtocolCharset());
    }
    
    public String toString() {
        return this.getEscapedURI();
    }
    
    static {
        URI.defaultProtocolCharset = "UTF-8";
        URI.defaultDocumentCharset = null;
        URI.defaultDocumentCharsetByLocale = null;
        URI.defaultDocumentCharsetByPlatform = null;
        final Locale locale = Locale.getDefault();
        if (locale != null) {
            URI.defaultDocumentCharsetByLocale = LocaleToCharsetMap.getCharset(locale);
            URI.defaultDocumentCharset = URI.defaultDocumentCharsetByLocale;
        }
        try {
            URI.defaultDocumentCharsetByPlatform = System.getProperty("file.encoding");
        }
        catch (final SecurityException ex) {}
        if (URI.defaultDocumentCharset == null) {
            URI.defaultDocumentCharset = URI.defaultDocumentCharsetByPlatform;
        }
        rootPath = new char[] { '/' };
        (percent = new BitSet(256)).set(37);
        digit = new BitSet(256);
        for (int i = 48; i <= 57; ++i) {
            URI.digit.set(i);
        }
        alpha = new BitSet(256);
        for (int i = 97; i <= 122; ++i) {
            URI.alpha.set(i);
        }
        for (int i = 65; i <= 90; ++i) {
            URI.alpha.set(i);
        }
        (alphanum = new BitSet(256)).or(URI.alpha);
        URI.alphanum.or(URI.digit);
        (hex = new BitSet(256)).or(URI.digit);
        for (int i = 97; i <= 102; ++i) {
            URI.hex.set(i);
        }
        for (int i = 65; i <= 70; ++i) {
            URI.hex.set(i);
        }
        (escaped = new BitSet(256)).or(URI.percent);
        URI.escaped.or(URI.hex);
        (mark = new BitSet(256)).set(45);
        URI.mark.set(95);
        URI.mark.set(46);
        URI.mark.set(33);
        URI.mark.set(126);
        URI.mark.set(42);
        URI.mark.set(39);
        URI.mark.set(40);
        URI.mark.set(41);
        (unreserved = new BitSet(256)).or(URI.alphanum);
        URI.unreserved.or(URI.mark);
        (reserved = new BitSet(256)).set(59);
        URI.reserved.set(47);
        URI.reserved.set(63);
        URI.reserved.set(58);
        URI.reserved.set(64);
        URI.reserved.set(38);
        URI.reserved.set(61);
        URI.reserved.set(43);
        URI.reserved.set(36);
        URI.reserved.set(44);
        (uric = new BitSet(256)).or(URI.reserved);
        URI.uric.or(URI.unreserved);
        URI.uric.or(URI.escaped);
        fragment = URI.uric;
        query = URI.uric;
        (pchar = new BitSet(256)).or(URI.unreserved);
        URI.pchar.or(URI.escaped);
        URI.pchar.set(58);
        URI.pchar.set(64);
        URI.pchar.set(38);
        URI.pchar.set(61);
        URI.pchar.set(43);
        URI.pchar.set(36);
        URI.pchar.set(44);
        param = URI.pchar;
        (segment = new BitSet(256)).or(URI.pchar);
        URI.segment.set(59);
        URI.segment.or(URI.param);
        (path_segments = new BitSet(256)).set(47);
        URI.path_segments.or(URI.segment);
        (abs_path = new BitSet(256)).set(47);
        URI.abs_path.or(URI.path_segments);
        (uric_no_slash = new BitSet(256)).or(URI.unreserved);
        URI.uric_no_slash.or(URI.escaped);
        URI.uric_no_slash.set(59);
        URI.uric_no_slash.set(63);
        URI.uric_no_slash.set(59);
        URI.uric_no_slash.set(64);
        URI.uric_no_slash.set(38);
        URI.uric_no_slash.set(61);
        URI.uric_no_slash.set(43);
        URI.uric_no_slash.set(36);
        URI.uric_no_slash.set(44);
        (opaque_part = new BitSet(256)).or(URI.uric_no_slash);
        URI.opaque_part.or(URI.uric);
        (path = new BitSet(256)).or(URI.abs_path);
        URI.path.or(URI.opaque_part);
        port = URI.digit;
        (IPv4address = new BitSet(256)).or(URI.digit);
        URI.IPv4address.set(46);
        (IPv6address = new BitSet(256)).or(URI.hex);
        URI.IPv6address.set(58);
        URI.IPv6address.or(URI.IPv4address);
        (IPv6reference = new BitSet(256)).set(91);
        URI.IPv6reference.or(URI.IPv6address);
        URI.IPv6reference.set(93);
        (toplabel = new BitSet(256)).or(URI.alphanum);
        URI.toplabel.set(45);
        domainlabel = URI.toplabel;
        (hostname = new BitSet(256)).or(URI.toplabel);
        URI.hostname.set(46);
        (host = new BitSet(256)).or(URI.hostname);
        URI.host.or(URI.IPv6reference);
        (hostport = new BitSet(256)).or(URI.host);
        URI.hostport.set(58);
        URI.hostport.or(URI.port);
        (userinfo = new BitSet(256)).or(URI.unreserved);
        URI.userinfo.or(URI.escaped);
        URI.userinfo.set(59);
        URI.userinfo.set(58);
        URI.userinfo.set(38);
        URI.userinfo.set(61);
        URI.userinfo.set(43);
        URI.userinfo.set(36);
        URI.userinfo.set(44);
        (within_userinfo = new BitSet(256)).or(URI.userinfo);
        URI.within_userinfo.clear(59);
        URI.within_userinfo.clear(58);
        URI.within_userinfo.clear(64);
        URI.within_userinfo.clear(63);
        URI.within_userinfo.clear(47);
        (server = new BitSet(256)).or(URI.userinfo);
        URI.server.set(64);
        URI.server.or(URI.hostport);
        (reg_name = new BitSet(256)).or(URI.unreserved);
        URI.reg_name.or(URI.escaped);
        URI.reg_name.set(36);
        URI.reg_name.set(44);
        URI.reg_name.set(59);
        URI.reg_name.set(58);
        URI.reg_name.set(64);
        URI.reg_name.set(38);
        URI.reg_name.set(61);
        URI.reg_name.set(43);
        (authority = new BitSet(256)).or(URI.server);
        URI.authority.or(URI.reg_name);
        (scheme = new BitSet(256)).or(URI.alpha);
        URI.scheme.or(URI.digit);
        URI.scheme.set(43);
        URI.scheme.set(45);
        URI.scheme.set(46);
        (rel_segment = new BitSet(256)).or(URI.unreserved);
        URI.rel_segment.or(URI.escaped);
        URI.rel_segment.set(59);
        URI.rel_segment.set(64);
        URI.rel_segment.set(38);
        URI.rel_segment.set(61);
        URI.rel_segment.set(43);
        URI.rel_segment.set(36);
        URI.rel_segment.set(44);
        (rel_path = new BitSet(256)).or(URI.rel_segment);
        URI.rel_path.or(URI.abs_path);
        (net_path = new BitSet(256)).set(47);
        URI.net_path.or(URI.authority);
        URI.net_path.or(URI.abs_path);
        (hier_part = new BitSet(256)).or(URI.net_path);
        URI.hier_part.or(URI.abs_path);
        URI.hier_part.or(URI.query);
        (relativeURI = new BitSet(256)).or(URI.net_path);
        URI.relativeURI.or(URI.abs_path);
        URI.relativeURI.or(URI.rel_path);
        URI.relativeURI.or(URI.query);
        (absoluteURI = new BitSet(256)).or(URI.scheme);
        URI.absoluteURI.set(58);
        URI.absoluteURI.or(URI.hier_part);
        URI.absoluteURI.or(URI.opaque_part);
        (URI_reference = new BitSet(256)).or(URI.absoluteURI);
        URI.URI_reference.or(URI.relativeURI);
        URI.URI_reference.set(35);
        URI.URI_reference.or(URI.fragment);
        control = new BitSet(256);
        for (int i = 0; i <= 31; ++i) {
            URI.control.set(i);
        }
        URI.control.set(127);
        (space = new BitSet(256)).set(32);
        (delims = new BitSet(256)).set(60);
        URI.delims.set(62);
        URI.delims.set(35);
        URI.delims.set(37);
        URI.delims.set(34);
        (unwise = new BitSet(256)).set(123);
        URI.unwise.set(125);
        URI.unwise.set(124);
        URI.unwise.set(92);
        URI.unwise.set(94);
        URI.unwise.set(91);
        URI.unwise.set(93);
        URI.unwise.set(96);
        (disallowed_rel_path = new BitSet(256)).or(URI.uric);
        URI.disallowed_rel_path.andNot(URI.rel_path);
        (disallowed_opaque_part = new BitSet(256)).or(URI.uric);
        URI.disallowed_opaque_part.andNot(URI.opaque_part);
        (allowed_authority = new BitSet(256)).or(URI.authority);
        URI.allowed_authority.clear(37);
        (allowed_opaque_part = new BitSet(256)).or(URI.opaque_part);
        URI.allowed_opaque_part.clear(37);
        (allowed_reg_name = new BitSet(256)).or(URI.reg_name);
        URI.allowed_reg_name.clear(37);
        (allowed_userinfo = new BitSet(256)).or(URI.userinfo);
        URI.allowed_userinfo.clear(37);
        (allowed_within_userinfo = new BitSet(256)).or(URI.within_userinfo);
        URI.allowed_within_userinfo.clear(37);
        (allowed_IPv6reference = new BitSet(256)).or(URI.IPv6reference);
        URI.allowed_IPv6reference.clear(91);
        URI.allowed_IPv6reference.clear(93);
        (allowed_host = new BitSet(256)).or(URI.hostname);
        URI.allowed_host.or(URI.allowed_IPv6reference);
        (allowed_within_authority = new BitSet(256)).or(URI.server);
        URI.allowed_within_authority.or(URI.reg_name);
        URI.allowed_within_authority.clear(59);
        URI.allowed_within_authority.clear(58);
        URI.allowed_within_authority.clear(64);
        URI.allowed_within_authority.clear(63);
        URI.allowed_within_authority.clear(47);
        (allowed_abs_path = new BitSet(256)).or(URI.abs_path);
        URI.allowed_abs_path.andNot(URI.percent);
        URI.allowed_abs_path.clear(43);
        (allowed_rel_path = new BitSet(256)).or(URI.rel_path);
        URI.allowed_rel_path.clear(37);
        URI.allowed_rel_path.clear(43);
        (allowed_within_path = new BitSet(256)).or(URI.abs_path);
        URI.allowed_within_path.clear(47);
        URI.allowed_within_path.clear(59);
        URI.allowed_within_path.clear(61);
        URI.allowed_within_path.clear(63);
        (allowed_query = new BitSet(256)).or(URI.uric);
        URI.allowed_query.clear(37);
        (allowed_within_query = new BitSet(256)).or(URI.allowed_query);
        URI.allowed_within_query.andNot(URI.reserved);
        (allowed_fragment = new BitSet(256)).or(URI.uric);
        URI.allowed_fragment.clear(37);
    }
    
    public static class DefaultCharsetChanged extends RuntimeException
    {
        public static final int UNKNOWN = 0;
        public static final int PROTOCOL_CHARSET = 1;
        public static final int DOCUMENT_CHARSET = 2;
        private int reasonCode;
        private String reason;
        
        public DefaultCharsetChanged(final int reasonCode, final String reason) {
            super(reason);
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
        
        public int getReasonCode() {
            return this.reasonCode;
        }
        
        public String getReason() {
            return this.reason;
        }
    }
    
    public static class LocaleToCharsetMap
    {
        private static final Hashtable LOCALE_TO_CHARSET_MAP;
        
        public static String getCharset(final Locale locale) {
            String charset = LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.get(locale.toString());
            if (charset != null) {
                return charset;
            }
            charset = LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.get(locale.getLanguage());
            return charset;
        }
        
        static {
            (LOCALE_TO_CHARSET_MAP = new Hashtable()).put("ar", "ISO-8859-6");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("be", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("bg", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("ca", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("cs", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("da", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("de", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("el", "ISO-8859-7");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("en", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("es", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("et", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("fi", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("fr", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("hr", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("hu", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("is", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("it", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("iw", "ISO-8859-8");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("ja", "Shift_JIS");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("ko", "EUC-KR");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("lt", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("lv", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("mk", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("nl", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("no", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("pl", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("pt", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("ro", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("ru", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sh", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sk", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sl", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sq", "ISO-8859-2");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sr", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("sv", "ISO-8859-1");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("tr", "ISO-8859-9");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("uk", "ISO-8859-5");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("zh", "GB2312");
            LocaleToCharsetMap.LOCALE_TO_CHARSET_MAP.put("zh_TW", "Big5");
        }
    }
}
