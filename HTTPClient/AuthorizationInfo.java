package HTTPClient;

import java.util.Vector;
import java.net.ProtocolException;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Hashtable;

public class AuthorizationInfo implements Cloneable
{
    private static Hashtable CntxtList;
    private static AuthorizationHandler AuthHandler;
    private String host;
    private int port;
    private String scheme;
    private String realm;
    private String cookie;
    private NVPair[] auth_params;
    private Object extra_info;
    private String[] paths;
    
    AuthorizationInfo(final String host, final int port) {
        this.auth_params = new NVPair[0];
        this.paths = new String[0];
        this.host = host.trim().toLowerCase();
        this.port = port;
    }
    
    public AuthorizationInfo(final String host, final int port, final String scheme, final String realm, final NVPair[] params, final Object info) {
        this.auth_params = new NVPair[0];
        this.paths = new String[0];
        this.scheme = scheme.trim();
        this.host = host.trim().toLowerCase();
        this.port = port;
        this.realm = realm;
        this.cookie = null;
        if (params != null) {
            this.auth_params = Util.resizeArray(params, params.length);
        }
        this.extra_info = info;
    }
    
    public AuthorizationInfo(final String host, final int port, final String scheme, final String realm, final String cookie) {
        this.auth_params = new NVPair[0];
        this.paths = new String[0];
        this.scheme = scheme.trim();
        this.host = host.trim().toLowerCase();
        this.port = port;
        this.realm = realm;
        if (cookie != null) {
            this.cookie = cookie.trim();
        }
        else {
            this.cookie = null;
        }
    }
    
    AuthorizationInfo(final AuthorizationInfo templ) {
        this.auth_params = new NVPair[0];
        this.paths = new String[0];
        this.scheme = templ.scheme;
        this.host = templ.host;
        this.port = templ.port;
        this.realm = templ.realm;
        this.cookie = templ.cookie;
        this.auth_params = Util.resizeArray(templ.auth_params, templ.auth_params.length);
        this.extra_info = templ.extra_info;
    }
    
    public static AuthorizationHandler setAuthHandler(final AuthorizationHandler handler) {
        final AuthorizationHandler tmp = AuthorizationInfo.AuthHandler;
        AuthorizationInfo.AuthHandler = handler;
        return tmp;
    }
    
    public static AuthorizationHandler getAuthHandler() {
        return AuthorizationInfo.AuthHandler;
    }
    
    public static AuthorizationInfo getAuthorization(final String host, final int port, final String scheme, final String realm) {
        return getAuthorization(host, port, scheme, realm, HTTPConnection.getDefaultContext());
    }
    
    public static synchronized AuthorizationInfo getAuthorization(final String host, final int port, final String scheme, final String realm, final Object context) {
        final Hashtable AuthList = Util.getList(AuthorizationInfo.CntxtList, context);
        final AuthorizationInfo auth_info = new AuthorizationInfo(host, port, scheme, realm, null, null);
        return AuthList.get(auth_info);
    }
    
    static AuthorizationInfo queryAuthHandler(final AuthorizationInfo auth_info, final RoRequest req, final RoResponse resp) throws AuthSchemeNotImplException, IOException {
        if (AuthorizationInfo.AuthHandler == null) {
            return null;
        }
        final AuthorizationInfo new_info = AuthorizationInfo.AuthHandler.getAuthorization(auth_info, req, resp);
        if (new_info != null) {
            if (req != null) {
                addAuthorization((AuthorizationInfo)new_info.clone(), req.getConnection().getContext());
            }
            else {
                addAuthorization((AuthorizationInfo)new_info.clone(), HTTPConnection.getDefaultContext());
            }
        }
        return new_info;
    }
    
    static synchronized AuthorizationInfo getAuthorization(final AuthorizationInfo auth_info, final RoRequest req, final RoResponse resp, final boolean query_auth_h) throws AuthSchemeNotImplException, IOException {
        Hashtable AuthList;
        if (req != null) {
            AuthList = Util.getList(AuthorizationInfo.CntxtList, req.getConnection().getContext());
        }
        else {
            AuthList = Util.getList(AuthorizationInfo.CntxtList, HTTPConnection.getDefaultContext());
        }
        AuthorizationInfo new_info = AuthList.get(auth_info);
        if (new_info == null && query_auth_h) {
            new_info = queryAuthHandler(auth_info, req, resp);
        }
        return new_info;
    }
    
    static AuthorizationInfo getAuthorization(final String host, final int port, final String scheme, final String realm, final RoRequest req, final RoResponse resp, final boolean query_auth_h) throws AuthSchemeNotImplException, IOException {
        return getAuthorization(new AuthorizationInfo(host, port, scheme, realm, null, null), req, resp, query_auth_h);
    }
    
    public static void addAuthorization(final AuthorizationInfo auth_info) {
        addAuthorization(auth_info, HTTPConnection.getDefaultContext());
    }
    
    public static void addAuthorization(final AuthorizationInfo auth_info, final Object context) {
        final Hashtable AuthList = Util.getList(AuthorizationInfo.CntxtList, context);
        final AuthorizationInfo old_info = AuthList.get(auth_info);
        if (old_info != null) {
            final int ol = old_info.paths.length;
            final int al = auth_info.paths.length;
            if (al == 0) {
                auth_info.paths = old_info.paths;
            }
            else {
                auth_info.paths = Util.resizeArray(auth_info.paths, al + ol);
                System.arraycopy(old_info.paths, 0, auth_info.paths, al, ol);
            }
        }
        AuthList.put(auth_info, auth_info);
    }
    
    public static void addAuthorization(final String host, final int port, final String scheme, final String realm, final String cookie, final NVPair[] params, final Object info) {
        addAuthorization(host, port, scheme, realm, cookie, params, info, HTTPConnection.getDefaultContext());
    }
    
    public static void addAuthorization(final String host, final int port, final String scheme, final String realm, final String cookie, final NVPair[] params, final Object info, final Object context) {
        final AuthorizationInfo auth = new AuthorizationInfo(host, port, scheme, realm, cookie);
        if (params != null && params.length > 0) {
            auth.auth_params = Util.resizeArray(params, params.length);
        }
        auth.extra_info = info;
        addAuthorization(auth, context);
    }
    
    public static void addBasicAuthorization(final String host, final int port, final String realm, final String user, final String passwd) {
        addAuthorization(host, port, "Basic", realm, Codecs.base64Encode(String.valueOf(user) + ":" + passwd), null, null);
    }
    
    public static void addBasicAuthorization(final String host, final int port, final String realm, final String user, final String passwd, final Object context) {
        addAuthorization(host, port, "Basic", realm, Codecs.base64Encode(String.valueOf(user) + ":" + passwd), null, null, context);
    }
    
    public static void addDigestAuthorization(final String host, final int port, final String realm, final String user, final String passwd) {
        addDigestAuthorization(host, port, realm, user, passwd, HTTPConnection.getDefaultContext());
    }
    
    public static void addDigestAuthorization(final String host, final int port, final String realm, final String user, final String passwd, final Object context) {
        final AuthorizationInfo prev = getAuthorization(host, port, "Digest", realm, context);
        NVPair[] params;
        if (prev == null) {
            params = new NVPair[] { new NVPair("username", user), new NVPair("uri", ""), new NVPair("nonce", ""), new NVPair("response", "") };
        }
        else {
            params = prev.getParams();
            for (int idx = 0; idx < params.length; ++idx) {
                if (params[idx].getName().equalsIgnoreCase("username")) {
                    params[idx] = new NVPair("username", user);
                    break;
                }
            }
        }
        final String[] extra = { MD5.hexDigest(String.valueOf(user) + ":" + realm + ":" + passwd), null, null };
        addAuthorization(host, port, "Digest", realm, null, params, extra, context);
    }
    
    public static void removeAuthorization(final AuthorizationInfo auth_info) {
        removeAuthorization(auth_info, HTTPConnection.getDefaultContext());
    }
    
    public static void removeAuthorization(final AuthorizationInfo auth_info, final Object context) {
        final Hashtable AuthList = Util.getList(AuthorizationInfo.CntxtList, context);
        AuthList.remove(auth_info);
    }
    
    public static void removeAuthorization(final String host, final int port, final String scheme, final String realm) {
        removeAuthorization(new AuthorizationInfo(host, port, scheme, realm, null, null));
    }
    
    public static void removeAuthorization(final String host, final int port, final String scheme, final String realm, final Object context) {
        removeAuthorization(new AuthorizationInfo(host, port, scheme, realm, null, null), context);
    }
    
    static AuthorizationInfo findBest(final RoRequest req) {
        final String path = Util.getPath(req.getRequestURI());
        final String host = req.getConnection().getHost();
        final int port = req.getConnection().getPort();
        final Hashtable AuthList = Util.getList(AuthorizationInfo.CntxtList, req.getConnection().getContext());
        Enumeration list = AuthList.elements();
        while (list.hasMoreElements()) {
            final AuthorizationInfo info = list.nextElement();
            if (info.host.equals(host)) {
                if (info.port != port) {
                    continue;
                }
                final String[] paths = info.paths;
                for (int idx = 0; idx < paths.length; ++idx) {
                    if (path.equals(paths[idx])) {
                        return info;
                    }
                }
            }
        }
        AuthorizationInfo best = null;
        final String base = path.substring(0, path.lastIndexOf(47) + 1);
        int min = Integer.MAX_VALUE;
        list = AuthList.elements();
        while (list.hasMoreElements()) {
            final AuthorizationInfo info2 = list.nextElement();
            if (info2.host.equals(host)) {
                if (info2.port != port) {
                    continue;
                }
                final String[] paths2 = info2.paths;
                for (int idx2 = 0; idx2 < paths2.length; ++idx2) {
                    final String ibase = paths2[idx2].substring(0, paths2[idx2].lastIndexOf(47) + 1);
                    if (base.equals(ibase)) {
                        return info2;
                    }
                    if (base.startsWith(ibase)) {
                        int num_seg = 0;
                        int pos = ibase.length() - 1;
                        while ((pos = base.indexOf(47, pos + 1)) != -1) {
                            ++num_seg;
                        }
                        if (num_seg < min) {
                            min = num_seg;
                            best = info2;
                        }
                    }
                    else if (ibase.startsWith(base)) {
                        int num_seg = 0;
                        int pos = base.length();
                        while ((pos = ibase.indexOf(47, pos + 1)) != -1) {
                            ++num_seg;
                        }
                        if (num_seg < min) {
                            min = num_seg;
                            best = info2;
                        }
                    }
                }
            }
        }
        return best;
    }
    
    public synchronized void addPath(final String resource) {
        final String path = Util.getPath(resource);
        for (int idx = 0; idx < this.paths.length; ++idx) {
            if (this.paths[idx].equals(path)) {
                return;
            }
        }
        (this.paths = Util.resizeArray(this.paths, this.paths.length + 1))[this.paths.length - 1] = path;
    }
    
    static AuthorizationInfo[] parseAuthString(final String challenge, final RoRequest req, final RoResponse resp) throws ProtocolException {
        int beg = 0;
        int end = 0;
        final char[] buf = challenge.toCharArray();
        int len = buf.length;
        final int[] pos_ref = new int[2];
        AuthorizationInfo[] auth_arr = new AuthorizationInfo[0];
        while (Character.isWhitespace(buf[len - 1])) {
            --len;
        }
        while (true) {
            beg = Util.skipSpace(buf, beg);
            if (beg == len) {
                break;
            }
            end = Util.findSpace(buf, beg + 1);
            int sts;
            try {
                sts = resp.getStatusCode();
            }
            catch (final IOException ioe) {
                throw new ProtocolException(ioe.toString());
            }
            AuthorizationInfo curr;
            if (sts == 401) {
                curr = new AuthorizationInfo(req.getConnection().getHost(), req.getConnection().getPort());
            }
            else {
                curr = new AuthorizationInfo(req.getConnection().getProxyHost(), req.getConnection().getProxyPort());
            }
            if (buf[end - 1] == ',') {
                curr.scheme = challenge.substring(beg, end - 1);
                beg = end;
            }
            else {
                curr.scheme = challenge.substring(beg, end);
                pos_ref[0] = beg;
                pos_ref[1] = end;
                final Vector params = parseParams(challenge, buf, pos_ref, len, curr);
                beg = pos_ref[0];
                end = pos_ref[1];
                if (!params.isEmpty()) {
                    params.copyInto(curr.auth_params = new NVPair[params.size()]);
                }
            }
            if (curr.realm == null) {
                curr.realm = "";
            }
            auth_arr = Util.resizeArray(auth_arr, auth_arr.length + 1);
            auth_arr[auth_arr.length - 1] = curr;
        }
        return auth_arr;
    }
    
    private static final Vector parseParams(final String challenge, final char[] buf, final int[] pos_ref, final int len, final AuthorizationInfo curr) throws ProtocolException {
        int beg = pos_ref[0];
        int end = pos_ref[1];
        boolean first = true;
        final Vector params = new Vector();
        while (true) {
            beg = Util.skipSpace(buf, end);
            if (beg == len) {
                break;
            }
            if (!first) {
                if (buf[beg] != ',') {
                    throw new ProtocolException("Bad Authentication header format: '" + challenge + "'\nExpected \",\" at position " + beg);
                }
                beg = Util.skipSpace(buf, beg + 1);
                if (beg == len) {
                    break;
                }
                if (buf[beg] == ',') {
                    end = beg;
                    continue;
                }
            }
            final int pstart = beg;
            for (end = beg + 1; end < len && !Character.isWhitespace(buf[end]) && buf[end] != '=' && buf[end] != ','; ++end) {}
            if (first && (end == len || (buf[end] == '=' && (end + 1 == len || (buf[end + 1] == '=' && end + 2 == len))))) {
                curr.cookie = challenge.substring(beg, len);
                beg = len;
                break;
            }
            final String param_name = challenge.substring(beg, end);
            beg = Util.skipSpace(buf, end);
            if ((beg < len && buf[beg] != '=' && buf[beg] != ',') || (!first && (beg == len || buf[beg] == ','))) {
                beg = pstart;
                break;
            }
            String param_value;
            if (beg < len && buf[beg] == '=') {
                beg = Util.skipSpace(buf, beg + 1);
                if (beg == len) {
                    throw new ProtocolException("Bad Authentication header format: " + challenge + "\nUnexpected EOL after token" + " at position " + (end - 1));
                }
                if (buf[beg] != '\"') {
                    end = Util.skipToken(buf, beg);
                    if (end == beg) {
                        throw new ProtocolException("Bad Authentication header format: " + challenge + "\nToken expected at " + "position " + beg);
                    }
                    param_value = challenge.substring(beg, end);
                }
                else {
                    end = beg++;
                    do {
                        end = challenge.indexOf(34, end + 1);
                    } while (end != -1 && challenge.charAt(end - 1) == '\\');
                    if (end == -1) {
                        throw new ProtocolException("Bad Authentication header format: " + challenge + "\nClosing <\"> for " + "quoted-string starting at position " + beg + " not found");
                    }
                    param_value = Util.dequoteString(challenge.substring(beg, end));
                    ++end;
                }
            }
            else {
                param_value = null;
            }
            if (param_name.equalsIgnoreCase("realm")) {
                curr.realm = param_value;
            }
            else {
                params.addElement(new NVPair(param_name, param_value));
            }
            first = false;
        }
        pos_ref[0] = beg;
        pos_ref[1] = end;
        return params;
    }
    
    public final String getHost() {
        return this.host;
    }
    
    public final int getPort() {
        return this.port;
    }
    
    public final String getScheme() {
        return this.scheme;
    }
    
    public final String getRealm() {
        return this.realm;
    }
    
    public final String getCookie() {
        return this.cookie;
    }
    
    public final void setCookie(final String cookie) {
        this.cookie = cookie;
    }
    
    public final NVPair[] getParams() {
        return Util.resizeArray(this.auth_params, this.auth_params.length);
    }
    
    public final void setParams(final NVPair[] params) {
        if (params != null) {
            this.auth_params = Util.resizeArray(params, params.length);
        }
        else {
            this.auth_params = new NVPair[0];
        }
    }
    
    public final Object getExtraInfo() {
        return this.extra_info;
    }
    
    public final void setExtraInfo(final Object info) {
        this.extra_info = info;
    }
    
    public String toString() {
        final StringBuffer field = new StringBuffer(100);
        field.append(this.scheme);
        field.append(" ");
        if (this.cookie != null) {
            field.append(this.cookie);
        }
        else {
            if (this.realm.length() > 0) {
                field.append("realm=\"");
                field.append(Util.quoteString(this.realm, "\\\""));
                field.append('\"');
            }
            for (int idx = 0; idx < this.auth_params.length; ++idx) {
                field.append(',');
                field.append(this.auth_params[idx].getName());
                if (this.auth_params[idx].getValue() != null) {
                    field.append("=\"");
                    field.append(Util.quoteString(this.auth_params[idx].getValue(), "\\\""));
                    field.append('\"');
                }
            }
        }
        return field.toString();
    }
    
    public int hashCode() {
        return (String.valueOf(this.host) + this.scheme.toLowerCase() + this.realm).hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof AuthorizationInfo) {
            final AuthorizationInfo auth = (AuthorizationInfo)obj;
            if (this.host.equals(auth.host) && this.port == auth.port && this.scheme.equalsIgnoreCase(auth.scheme) && this.realm.equals(auth.realm)) {
                return true;
            }
        }
        return false;
    }
    
    public Object clone() {
        AuthorizationInfo ai;
        try {
            ai = (AuthorizationInfo)super.clone();
            ai.auth_params = Util.resizeArray(this.auth_params, this.auth_params.length);
            try {
                ai.extra_info = this.extra_info.getClass().getMethod("clone", (Class<?>[])null).invoke(this.extra_info, (Object[])null);
            }
            catch (final Throwable t) {}
            ai.paths = new String[this.paths.length];
            System.arraycopy(this.paths, 0, ai.paths, 0, this.paths.length);
        }
        catch (final CloneNotSupportedException cnse) {
            throw new InternalError(cnse.toString());
        }
        return ai;
    }
    
    static {
        AuthorizationInfo.CntxtList = new Hashtable();
        AuthorizationInfo.AuthHandler = new DefaultAuthHandler();
        AuthorizationInfo.CntxtList.put(HTTPConnection.getDefaultContext(), new Hashtable<Object, Hashtable>());
    }
}
