package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.URIUtil;

public class HttpURL extends URI
{
    public static final char[] DEFAULT_SCHEME;
    public static final char[] _default_scheme;
    public static final int DEFAULT_PORT = 80;
    public static final int _default_port = 80;
    static final long serialVersionUID = -7158031098595039459L;
    
    protected HttpURL() {
    }
    
    public HttpURL(final char[] escaped, final String charset) throws URIException, NullPointerException {
        this.protocolCharset = charset;
        this.parseUriReference(new String(escaped), true);
        this.checkValid();
    }
    
    public HttpURL(final char[] escaped) throws URIException, NullPointerException {
        this.parseUriReference(new String(escaped), true);
        this.checkValid();
    }
    
    public HttpURL(final String original, final String charset) throws URIException {
        this.protocolCharset = charset;
        this.parseUriReference(original, false);
        this.checkValid();
    }
    
    public HttpURL(final String original) throws URIException {
        this.parseUriReference(original, false);
        this.checkValid();
    }
    
    public HttpURL(final String host, final int port, final String path) throws URIException {
        this(null, null, host, port, path, null, null);
    }
    
    public HttpURL(final String host, final int port, final String path, final String query) throws URIException {
        this(null, null, host, port, path, query, null);
    }
    
    public HttpURL(final String user, final String password, final String host) throws URIException {
        this(user, password, host, -1, null, null, null);
    }
    
    public HttpURL(final String user, final String password, final String host, final int port) throws URIException {
        this(user, password, host, port, null, null, null);
    }
    
    public HttpURL(final String user, final String password, final String host, final int port, final String path) throws URIException {
        this(user, password, host, port, path, null, null);
    }
    
    public HttpURL(final String user, final String password, final String host, final int port, final String path, final String query) throws URIException {
        this(user, password, host, port, path, query, null);
    }
    
    public HttpURL(final String host, final String path, final String query, final String fragment) throws URIException {
        this(null, null, host, -1, path, query, fragment);
    }
    
    public HttpURL(final String userinfo, final String host, final String path, final String query, final String fragment) throws URIException {
        this(userinfo, host, -1, path, query, fragment);
    }
    
    public HttpURL(final String userinfo, final String host, final int port, final String path) throws URIException {
        this(userinfo, host, port, path, null, null);
    }
    
    public HttpURL(final String userinfo, final String host, final int port, final String path, final String query) throws URIException {
        this(userinfo, host, port, path, query, null);
    }
    
    public HttpURL(final String userinfo, final String host, final int port, final String path, final String query, final String fragment) throws URIException {
        final StringBuffer buff = new StringBuffer();
        if (userinfo != null || host != null || port != -1) {
            this._scheme = HttpURL.DEFAULT_SCHEME;
            buff.append(HttpURL._default_scheme);
            buff.append("://");
            if (userinfo != null) {
                buff.append(userinfo);
                buff.append('@');
            }
            if (host != null) {
                buff.append(URIUtil.encode(host, URI.allowed_host));
                if (port != -1 || port != 80) {
                    buff.append(':');
                    buff.append(port);
                }
            }
        }
        if (path != null) {
            if (HttpURL.scheme != null && !path.startsWith("/")) {
                throw new URIException(1, "abs_path requested");
            }
            buff.append(URIUtil.encode(path, URI.allowed_abs_path));
        }
        if (query != null) {
            buff.append('?');
            buff.append(URIUtil.encode(query, URI.allowed_query));
        }
        if (fragment != null) {
            buff.append('#');
            buff.append(URIUtil.encode(fragment, URI.allowed_fragment));
        }
        this.parseUriReference(buff.toString(), true);
        this.checkValid();
    }
    
    public HttpURL(final String user, final String password, final String host, final int port, final String path, final String query, final String fragment) throws URIException {
        this(toUserinfo(user, password), host, port, path, query, fragment);
    }
    
    protected static String toUserinfo(final String user, final String password) throws URIException {
        if (user == null) {
            return null;
        }
        final StringBuffer usrinfo = new StringBuffer(20);
        usrinfo.append(URIUtil.encode(user, URI.allowed_within_userinfo));
        if (password == null) {
            return usrinfo.toString();
        }
        usrinfo.append(':');
        usrinfo.append(URIUtil.encode(password, URI.allowed_within_userinfo));
        return usrinfo.toString();
    }
    
    public HttpURL(final HttpURL base, final String relative) throws URIException {
        this(base, new HttpURL(relative));
    }
    
    public HttpURL(final HttpURL base, final HttpURL relative) throws URIException {
        super(base, relative);
        this.checkValid();
    }
    
    public char[] getRawScheme() {
        return (char[])((this._scheme == null) ? null : HttpURL.DEFAULT_SCHEME);
    }
    
    public String getScheme() {
        return (this._scheme == null) ? null : new String(HttpURL.DEFAULT_SCHEME);
    }
    
    public int getPort() {
        return (this._port == -1) ? 80 : this._port;
    }
    
    public void setRawUserinfo(final char[] escapedUser, final char[] escapedPassword) throws URIException {
        if (escapedUser == null || escapedUser.length == 0) {
            throw new URIException(1, "user required");
        }
        if (!this.validate(escapedUser, HttpURL.within_userinfo) || (escapedPassword != null && !this.validate(escapedPassword, HttpURL.within_userinfo))) {
            throw new URIException(3, "escaped userinfo not valid");
        }
        final String username = new String(escapedUser);
        final String password = (escapedPassword == null) ? null : new String(escapedPassword);
        final String userinfo = username + ((password == null) ? "" : (":" + password));
        final String hostname = new String(this.getRawHost());
        final String hostport = (this._port == -1) ? hostname : (hostname + ":" + this._port);
        final String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }
    
    public void setEscapedUserinfo(final String escapedUser, final String escapedPassword) throws URIException, NullPointerException {
        this.setRawUserinfo(escapedUser.toCharArray(), (char[])((escapedPassword == null) ? null : escapedPassword.toCharArray()));
    }
    
    public void setUserinfo(final String user, final String password) throws URIException, NullPointerException {
        final String charset = this.getProtocolCharset();
        this.setRawUserinfo(URI.encode(user, HttpURL.within_userinfo, charset), (char[])((password == null) ? null : URI.encode(password, HttpURL.within_userinfo, charset)));
    }
    
    public void setRawUser(final char[] escapedUser) throws URIException {
        if (escapedUser == null || escapedUser.length == 0) {
            throw new URIException(1, "user required");
        }
        if (!this.validate(escapedUser, HttpURL.within_userinfo)) {
            throw new URIException(3, "escaped user not valid");
        }
        final String username = new String(escapedUser);
        final char[] rawPassword = this.getRawPassword();
        final String password = (rawPassword == null) ? null : new String(rawPassword);
        final String userinfo = username + ((password == null) ? "" : (":" + password));
        final String hostname = new String(this.getRawHost());
        final String hostport = (this._port == -1) ? hostname : (hostname + ":" + this._port);
        final String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }
    
    public void setEscapedUser(final String escapedUser) throws URIException, NullPointerException {
        this.setRawUser(escapedUser.toCharArray());
    }
    
    public void setUser(final String user) throws URIException, NullPointerException {
        this.setRawUser(URI.encode(user, HttpURL.allowed_within_userinfo, this.getProtocolCharset()));
    }
    
    public char[] getRawUser() {
        if (this._userinfo == null || this._userinfo.length == 0) {
            return null;
        }
        final int to = this.indexFirstOf(this._userinfo, ':');
        if (to == -1) {
            return this._userinfo;
        }
        final char[] result = new char[to];
        System.arraycopy(this._userinfo, 0, result, 0, to);
        return result;
    }
    
    public String getEscapedUser() {
        final char[] user = this.getRawUser();
        return (user == null) ? null : new String(user);
    }
    
    public String getUser() throws URIException {
        final char[] user = this.getRawUser();
        return (user == null) ? null : URI.decode(user, this.getProtocolCharset());
    }
    
    public void setRawPassword(final char[] escapedPassword) throws URIException {
        if (escapedPassword != null && !this.validate(escapedPassword, HttpURL.within_userinfo)) {
            throw new URIException(3, "escaped password not valid");
        }
        if (this.getRawUser() == null || this.getRawUser().length == 0) {
            throw new URIException(1, "username required");
        }
        final String username = new String(this.getRawUser());
        final String password = (escapedPassword == null) ? null : new String(escapedPassword);
        final String userinfo = username + ((password == null) ? "" : (":" + password));
        final String hostname = new String(this.getRawHost());
        final String hostport = (this._port == -1) ? hostname : (hostname + ":" + this._port);
        final String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }
    
    public void setEscapedPassword(final String escapedPassword) throws URIException {
        this.setRawPassword((char[])((escapedPassword == null) ? null : escapedPassword.toCharArray()));
    }
    
    public void setPassword(final String password) throws URIException {
        this.setRawPassword((char[])((password == null) ? null : URI.encode(password, HttpURL.allowed_within_userinfo, this.getProtocolCharset())));
    }
    
    public char[] getRawPassword() {
        final int from = this.indexFirstOf(this._userinfo, ':');
        if (from == -1) {
            return null;
        }
        final int len = this._userinfo.length - from - 1;
        final char[] result = new char[len];
        System.arraycopy(this._userinfo, from + 1, result, 0, len);
        return result;
    }
    
    public String getEscapedPassword() {
        final char[] password = this.getRawPassword();
        return (password == null) ? null : new String(password);
    }
    
    public String getPassword() throws URIException {
        final char[] password = this.getRawPassword();
        return (password == null) ? null : URI.decode(password, this.getProtocolCharset());
    }
    
    public char[] getRawCurrentHierPath() throws URIException {
        return (this._path == null || this._path.length == 0) ? HttpURL.rootPath : super.getRawCurrentHierPath(this._path);
    }
    
    public char[] getRawAboveHierPath() throws URIException {
        final char[] path = this.getRawCurrentHierPath();
        return (path == null || path.length == 0) ? HttpURL.rootPath : this.getRawCurrentHierPath(path);
    }
    
    public char[] getRawPath() {
        final char[] path = super.getRawPath();
        return (path == null || path.length == 0) ? HttpURL.rootPath : path;
    }
    
    public void setQuery(final String queryName, final String queryValue) throws URIException, NullPointerException {
        final StringBuffer buff = new StringBuffer();
        final String charset = this.getProtocolCharset();
        buff.append(URI.encode(queryName, HttpURL.allowed_within_query, charset));
        buff.append('=');
        buff.append(URI.encode(queryValue, HttpURL.allowed_within_query, charset));
        this._query = buff.toString().toCharArray();
        this.setURI();
    }
    
    public void setQuery(final String[] queryName, final String[] queryValue) throws URIException, NullPointerException {
        final int length = queryName.length;
        if (length != queryValue.length) {
            throw new URIException("wrong array size of query");
        }
        final StringBuffer buff = new StringBuffer();
        final String charset = this.getProtocolCharset();
        for (int i = 0; i < length; ++i) {
            buff.append(URI.encode(queryName[i], HttpURL.allowed_within_query, charset));
            buff.append('=');
            buff.append(URI.encode(queryValue[i], HttpURL.allowed_within_query, charset));
            if (i + 1 < length) {
                buff.append('&');
            }
        }
        this._query = buff.toString().toCharArray();
        this.setURI();
    }
    
    protected void checkValid() throws URIException {
        if (!this.equals(this._scheme, HttpURL.DEFAULT_SCHEME) && this._scheme != null) {
            throw new URIException(1, "wrong class use");
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
                if (this._userinfo != null) {
                    if (this._host != null) {
                        buf.append(this._host);
                        if (this._port != -1) {
                            buf.append(':');
                            buf.append(this._port);
                        }
                    }
                }
                else {
                    buf.append(this._authority);
                }
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
    
    static {
        DEFAULT_SCHEME = new char[] { 'h', 't', 't', 'p' };
        _default_scheme = HttpURL.DEFAULT_SCHEME;
    }
}
