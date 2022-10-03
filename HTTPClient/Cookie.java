package HTTPClient;

import java.net.ProtocolException;
import java.util.Date;
import java.io.Serializable;

public class Cookie implements Serializable
{
    private static final long serialVersionUID = 8599975325569296615L;
    protected String name;
    protected String value;
    protected Date expires;
    protected String domain;
    protected String path;
    protected boolean secure;
    
    public Cookie(final String name, final String value, final String domain, final String path, final Date expires, final boolean secure) {
        if (name == null) {
            throw new NullPointerException("missing name");
        }
        if (value == null) {
            throw new NullPointerException("missing value");
        }
        if (domain == null) {
            throw new NullPointerException("missing domain");
        }
        if (path == null) {
            throw new NullPointerException("missing path");
        }
        this.name = name;
        this.value = value;
        this.domain = domain.toLowerCase();
        this.path = path;
        this.expires = expires;
        this.secure = secure;
        if (this.domain.indexOf(46) == -1) {
            this.domain = String.valueOf(this.domain) + ".local";
        }
    }
    
    protected Cookie(final RoRequest req) {
        this.name = null;
        this.value = null;
        this.expires = null;
        this.domain = req.getConnection().getHost();
        if (this.domain.indexOf(46) == -1) {
            this.domain = String.valueOf(this.domain) + ".local";
        }
        this.path = Util.getPath(req.getRequestURI());
        final int slash = this.path.lastIndexOf(47);
        if (slash >= 0) {
            this.path = this.path.substring(0, slash);
        }
        this.secure = false;
    }
    
    protected static Cookie[] parse(final String set_cookie, final RoRequest req) throws ProtocolException {
        int beg = 0;
        int end = 0;
        int start = 0;
        final char[] buf = set_cookie.toCharArray();
        final int len = buf.length;
        Cookie[] cookie_arr = new Cookie[0];
        while (true) {
            beg = Util.skipSpace(buf, beg);
            if (beg >= len) {
                return cookie_arr;
            }
            if (buf[beg] == ',') {
                ++beg;
            }
            else {
                final Cookie curr = new Cookie(req);
                start = beg;
                end = set_cookie.indexOf(61, beg);
                if (end == -1) {
                    throw new ProtocolException("Bad Set-Cookie header: " + set_cookie + "\nNo '=' found " + "for token starting at " + "position " + beg);
                }
                curr.name = set_cookie.substring(beg, end).trim();
                beg = Util.skipSpace(buf, end + 1);
                int comma = set_cookie.indexOf(44, beg);
                int semic = set_cookie.indexOf(59, beg);
                if (comma == -1 && semic == -1) {
                    end = len;
                }
                else if (comma == -1) {
                    end = semic;
                }
                else if (semic == -1) {
                    end = comma;
                }
                else if (comma > semic) {
                    end = semic;
                }
                else {
                    final int eq = set_cookie.indexOf(61, comma);
                    if (eq > 0 && eq < semic) {
                        end = set_cookie.lastIndexOf(44, eq);
                    }
                    else {
                        end = semic;
                    }
                }
                curr.value = set_cookie.substring(beg, end).trim();
                beg = end;
                boolean legal = true;
                while (beg < len && buf[beg] != ',') {
                    if (buf[beg] == ';') {
                        beg = Util.skipSpace(buf, beg + 1);
                    }
                    else if (beg + 6 <= len && set_cookie.regionMatches(true, beg, "secure", 0, 6)) {
                        curr.secure = true;
                        beg += 6;
                        beg = Util.skipSpace(buf, beg);
                        if (beg < len && buf[beg] == ';') {
                            beg = Util.skipSpace(buf, beg + 1);
                        }
                        else {
                            if (beg < len && buf[beg] != ',') {
                                throw new ProtocolException("Bad Set-Cookie header: " + set_cookie + "\nExpected " + "';' or ',' at position " + beg);
                            }
                            continue;
                        }
                    }
                    else {
                        end = set_cookie.indexOf(61, beg);
                        if (end == -1) {
                            throw new ProtocolException("Bad Set-Cookie header: " + set_cookie + "\nNo '=' found " + "for token starting at " + "position " + beg);
                        }
                        final String name = set_cookie.substring(beg, end).trim();
                        beg = Util.skipSpace(buf, end + 1);
                        if (name.equalsIgnoreCase("expires")) {
                            if (set_cookie.charAt(beg) == '\"') {
                                beg = Util.skipSpace(buf, beg + 1);
                            }
                            int pos;
                            for (pos = beg; pos < len && ((buf[pos] >= 'a' && buf[pos] <= 'z') || (buf[pos] >= 'A' && buf[pos] <= 'Z')); ++pos) {}
                            pos = Util.skipSpace(buf, pos);
                            if (pos < len && buf[pos] == ',' && pos > beg) {
                                beg = pos + 1;
                            }
                        }
                        comma = set_cookie.indexOf(44, beg);
                        semic = set_cookie.indexOf(59, beg);
                        if (comma == -1 && semic == -1) {
                            end = len;
                        }
                        else if (comma == -1) {
                            end = semic;
                        }
                        else if (semic == -1) {
                            end = comma;
                        }
                        else {
                            end = Math.min(comma, semic);
                        }
                        final String value = set_cookie.substring(beg, end).trim();
                        legal &= setAttribute(curr, name, value, set_cookie);
                        beg = end;
                        if (beg >= len || buf[beg] != ';') {
                            continue;
                        }
                        beg = Util.skipSpace(buf, beg + 1);
                    }
                }
                if (legal) {
                    cookie_arr = Util.resizeArray(cookie_arr, cookie_arr.length + 1);
                    cookie_arr[cookie_arr.length - 1] = curr;
                }
                else {
                    Log.write(16, "Cooki: Ignoring cookie: " + curr);
                }
            }
        }
    }
    
    private static boolean setAttribute(final Cookie cookie, final String name, String value, final String set_cookie) throws ProtocolException {
        if (name.equalsIgnoreCase("expires")) {
            if (value.charAt(value.length() - 1) == '\"') {
                value = value.substring(0, value.length() - 1).trim();
            }
            try {
                cookie.expires = new Date(value);
            }
            catch (final IllegalArgumentException ex) {
                Log.write(16, "Cooki: Bad Set-Cookie header: " + set_cookie + "\n       Invalid date `" + value + "'");
            }
        }
        else if (name.equals("max-age")) {
            if (cookie.expires != null) {
                return true;
            }
            if (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
                value = value.substring(1, value.length() - 1).trim();
            }
            int age;
            try {
                age = Integer.parseInt(value);
            }
            catch (final NumberFormatException ex2) {
                throw new ProtocolException("Bad Set-Cookie header: " + set_cookie + "\nMax-Age '" + value + "' not a number");
            }
            cookie.expires = new Date(System.currentTimeMillis() + age * 1000L);
        }
        else if (name.equalsIgnoreCase("domain")) {
            if (value.length() == 0) {
                Log.write(16, "Cooki: Bad Set-Cookie header: " + set_cookie + "\n       domain is empty - ignoring domain");
                return true;
            }
            value = value.toLowerCase();
            if (value.length() != 0 && value.charAt(0) != '.' && !value.equals(cookie.domain)) {
                value = String.valueOf('.') + value;
            }
            if (!cookie.domain.endsWith(value)) {
                Log.write(16, "Cooki: Bad Set-Cookie header: " + set_cookie + "\n       Current domain " + cookie.domain + " does not match given parsed " + value);
                return false;
            }
            if (!value.equals(".local") && value.indexOf(46, 1) == -1) {
                Log.write(16, "Cooki: Bad Set-Cookie header: " + set_cookie + "\n       Domain attribute " + value + "isn't .local and doesn't have at " + "least 2 dots");
                return false;
            }
            String top = null;
            if (value.length() > 3) {
                top = value.substring(value.length() - 4);
            }
            if (top == null || (!top.equalsIgnoreCase(".com") && !top.equalsIgnoreCase(".edu") && !top.equalsIgnoreCase(".net") && !top.equalsIgnoreCase(".org") && !top.equalsIgnoreCase(".gov") && !top.equalsIgnoreCase(".mil") && !top.equalsIgnoreCase(".int"))) {
                final int dl = cookie.domain.length();
                final int vl = value.length();
                if (dl > vl && cookie.domain.substring(0, dl - vl).indexOf(46) != -1) {
                    Log.write(16, "Cooki: Bad Set-Cookie header: " + set_cookie + "\n       Domain attribute " + value + "is more than one level below " + "current domain " + cookie.domain);
                    return false;
                }
            }
            cookie.domain = value;
        }
        else if (name.equalsIgnoreCase("path")) {
            cookie.path = value;
        }
        return true;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Date expires() {
        return this.expires;
    }
    
    public boolean discard() {
        return this.expires == null;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public boolean hasExpired() {
        return this.expires != null && this.expires.getTime() <= System.currentTimeMillis();
    }
    
    protected boolean sendWith(final RoRequest req) {
        final HTTPConnection con = req.getConnection();
        String eff_host = con.getHost();
        if (eff_host.indexOf(46) == -1) {
            eff_host = String.valueOf(eff_host) + ".local";
        }
        return ((this.domain.charAt(0) == '.' && eff_host.endsWith(this.domain)) || (this.domain.charAt(0) != '.' && eff_host.equals(this.domain))) && Util.getPath(req.getRequestURI()).startsWith(this.path) && (!this.secure || con.getProtocol().equals("https") || con.getProtocol().equals("shttp"));
    }
    
    public int hashCode() {
        return this.name.hashCode() + this.path.hashCode() + this.domain.hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof Cookie) {
            final Cookie other = (Cookie)obj;
            return this.name.equals(other.name) && this.path.equals(other.path) && this.domain.equals(other.domain);
        }
        return false;
    }
    
    protected String toExternalForm() {
        return String.valueOf(this.name) + "=" + this.value;
    }
    
    public String toString() {
        final StringBuffer res = new StringBuffer(this.name.length() + this.value.length() + 30);
        res.append(this.name).append('=').append(this.value);
        if (this.expires != null) {
            res.append("; expires=").append(this.expires);
        }
        if (this.path != null) {
            res.append("; path=").append(this.path);
        }
        if (this.domain != null) {
            res.append("; domain=").append(this.domain);
        }
        if (this.secure) {
            res.append("; secure");
        }
        return res.toString();
    }
}
