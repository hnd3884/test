package HTTPClient;

import java.util.Vector;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.net.ProtocolException;
import java.util.Date;

public class Cookie2 extends Cookie
{
    private static final long serialVersionUID = 2208203902820875917L;
    protected int version;
    protected boolean discard;
    protected String comment;
    protected URI comment_url;
    protected int[] port_list;
    protected String port_list_str;
    protected boolean path_set;
    protected boolean port_set;
    protected boolean domain_set;
    
    public Cookie2(final String name, final String value, final String domain, final int[] port_list, final String path, final Date expires, final boolean discard, final boolean secure, final String comment, final URI comment_url) {
        super(name, value, domain, path, expires, secure);
        this.discard = discard;
        this.port_list = port_list;
        this.comment = comment;
        this.comment_url = comment_url;
        this.path_set = true;
        this.domain_set = true;
        if (port_list != null && port_list.length > 0) {
            final StringBuffer tmp = new StringBuffer();
            tmp.append(port_list[0]);
            for (int idx = 1; idx < port_list.length; ++idx) {
                tmp.append(',');
                tmp.append(port_list[idx]);
            }
            this.port_list_str = tmp.toString();
            this.port_set = true;
        }
        this.version = 1;
    }
    
    protected Cookie2(final RoRequest req) {
        super(req);
        super.path = Util.getPath(req.getRequestURI());
        final int slash = super.path.lastIndexOf(47);
        if (slash != -1) {
            super.path = super.path.substring(0, slash + 1);
        }
        if (super.domain.indexOf(46) == -1) {
            super.domain = String.valueOf(super.domain) + ".local";
        }
        this.version = -1;
        this.discard = false;
        this.comment = null;
        this.comment_url = null;
        this.port_list = null;
        this.port_list_str = null;
        this.path_set = false;
        this.port_set = false;
        this.domain_set = false;
    }
    
    protected static Cookie[] parse(final String set_cookie, final RoRequest req) throws ProtocolException {
        Vector cookies;
        try {
            cookies = Util.parseHeader(set_cookie);
        }
        catch (final ParseException pe) {
            throw new ProtocolException(pe.getMessage());
        }
        Cookie[] cookie_arr = new Cookie[cookies.size()];
        int cidx = 0;
        for (int idx = 0; idx < cookie_arr.length; ++idx) {
            final HttpHeaderElement c_elem = cookies.elementAt(idx);
            if (c_elem.getValue() == null) {
                throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nMissing value " + "for cookie '" + c_elem.getName() + "'");
            }
            final Cookie2 curr = new Cookie2(req);
            curr.name = c_elem.getName();
            curr.value = c_elem.getValue();
            final NVPair[] params = c_elem.getParams();
            boolean discard_set = false;
            boolean secure_set = false;
            for (int idx2 = 0; idx2 < params.length; ++idx2) {
                final String name = params[idx2].getName().toLowerCase();
                if ((name.equals("version") || name.equals("max-age") || name.equals("domain") || name.equals("path") || name.equals("comment") || name.equals("commenturl")) && params[idx2].getValue() == null) {
                    throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nMissing value " + "for " + params[idx2].getName() + " attribute in cookie '" + c_elem.getName() + "'");
                }
                if (name.equals("version")) {
                    if (curr.version != -1) {
                        continue;
                    }
                    try {
                        curr.version = Integer.parseInt(params[idx2].getValue());
                        continue;
                    }
                    catch (final NumberFormatException ex) {
                        throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nVersion '" + params[idx2].getValue() + "' not a number");
                    }
                }
                if (name.equals("path")) {
                    if (!curr.path_set) {
                        curr.path = params[idx2].getValue();
                        curr.path_set = true;
                    }
                }
                else if (name.equals("domain")) {
                    if (!curr.domain_set) {
                        final String d = params[idx2].getValue().toLowerCase();
                        if (d.charAt(0) != '.' && !d.equals(curr.domain)) {
                            curr.domain = "." + d;
                        }
                        else {
                            curr.domain = d;
                        }
                        curr.domain_set = true;
                    }
                }
                else if (name.equals("max-age")) {
                    if (curr.expires == null) {
                        int age;
                        try {
                            age = Integer.parseInt(params[idx2].getValue());
                        }
                        catch (final NumberFormatException ex2) {
                            throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nMax-Age '" + params[idx2].getValue() + "' not a number");
                        }
                        curr.expires = new Date(System.currentTimeMillis() + age * 1000L);
                    }
                }
                else if (name.equals("port")) {
                    if (!curr.port_set) {
                        if (params[idx2].getValue() == null) {
                            (curr.port_list = new int[1])[0] = req.getConnection().getPort();
                            curr.port_set = true;
                        }
                        else {
                            curr.port_list_str = params[idx2].getValue();
                            final StringTokenizer tok = new StringTokenizer(params[idx2].getValue(), ",");
                            curr.port_list = new int[tok.countTokens()];
                            for (int idx3 = 0; idx3 < curr.port_list.length; ++idx3) {
                                final String port = tok.nextToken().trim();
                                try {
                                    curr.port_list[idx3] = Integer.parseInt(port);
                                }
                                catch (final NumberFormatException ex3) {
                                    throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nPort '" + port + "' not a number");
                                }
                            }
                            curr.port_set = true;
                        }
                    }
                }
                else if (name.equals("discard")) {
                    if (!discard_set) {
                        curr.discard = true;
                        discard_set = true;
                    }
                }
                else if (name.equals("secure")) {
                    if (!secure_set) {
                        curr.secure = true;
                        secure_set = true;
                    }
                }
                else {
                    if (name.equals("comment")) {
                        if (curr.comment != null) {
                            continue;
                        }
                        try {
                            curr.comment = new String(params[idx2].getValue().getBytes("8859_1"), "UTF8");
                            continue;
                        }
                        catch (final UnsupportedEncodingException usee) {
                            throw new Error(usee.toString());
                        }
                    }
                    if (name.equals("commenturl") && curr.comment_url == null) {
                        try {
                            curr.comment_url = new URI(params[idx2].getValue());
                        }
                        catch (final ParseException ex4) {
                            throw new ProtocolException("Bad Set-Cookie2 header: " + set_cookie + "\nCommentURL '" + params[idx2].getValue() + "' not a valid URL");
                        }
                    }
                }
            }
            if (curr.version != -1) {
                if (curr.expires == null) {
                    curr.discard = true;
                }
                if (!Util.getPath(req.getRequestURI()).startsWith(curr.path)) {
                    Log.write(16, "Cook2: Bad Set-Cookie2 header: " + set_cookie + "\n       path `" + curr.path + "' is not a prefix of the " + "request uri `" + req.getRequestURI() + "'");
                }
                else {
                    String eff_host = req.getConnection().getHost();
                    if (eff_host.indexOf(46) == -1) {
                        eff_host = String.valueOf(eff_host) + ".local";
                    }
                    if (!curr.domain.equals(".local") && curr.domain.indexOf(46, 1) == -1) {
                        Log.write(16, "Cook2: Bad Set-Cookie2 header: " + set_cookie + "\n       domain `" + curr.domain + "' is not `.local' and " + "doesn't contain two `.'s");
                    }
                    else if (!eff_host.endsWith(curr.domain)) {
                        Log.write(16, "Cook2: Bad Set-Cookie2 header: " + set_cookie + "\n       domain `" + curr.domain + "' does not match current" + "host `" + eff_host + "'");
                    }
                    else if (eff_host.substring(0, eff_host.length() - curr.domain.length()).indexOf(46) != -1) {
                        Log.write(16, "Cook2: Bad Set-Cookie2 header: " + set_cookie + "\n       domain `" + curr.domain + "' is more than one `.'" + "away from host `" + eff_host + "'");
                    }
                    else {
                        if (curr.port_set) {
                            int idx4;
                            for (idx4 = 0, idx4 = 0; idx4 < curr.port_list.length && curr.port_list[idx4] != req.getConnection().getPort(); ++idx4) {}
                            if (idx4 == curr.port_list.length) {
                                Log.write(16, "Cook2: Bad Set-Cookie2 header: " + set_cookie + "\n       port list " + "does include current port " + req.getConnection().getPort());
                                continue;
                            }
                        }
                        cookie_arr[cidx++] = curr;
                    }
                }
            }
        }
        if (cidx < cookie_arr.length) {
            cookie_arr = Util.resizeArray(cookie_arr, cidx);
        }
        return cookie_arr;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public URI getCommentURL() {
        return this.comment_url;
    }
    
    public int[] getPorts() {
        return this.port_list;
    }
    
    public boolean discard() {
        return this.discard;
    }
    
    protected boolean sendWith(final RoRequest req) {
        final HTTPConnection con = req.getConnection();
        boolean port_match = !this.port_set;
        if (this.port_set) {
            for (int idx = 0; idx < this.port_list.length; ++idx) {
                if (this.port_list[idx] == con.getPort()) {
                    port_match = true;
                    break;
                }
            }
        }
        String eff_host = con.getHost();
        if (eff_host.indexOf(46) == -1) {
            eff_host = String.valueOf(eff_host) + ".local";
        }
        return ((super.domain.charAt(0) == '.' && eff_host.endsWith(super.domain)) || (super.domain.charAt(0) != '.' && eff_host.equals(super.domain))) && port_match && Util.getPath(req.getRequestURI()).startsWith(super.path) && (!super.secure || con.getProtocol().equals("https") || con.getProtocol().equals("shttp"));
    }
    
    protected String toExternalForm() {
        final StringBuffer cookie = new StringBuffer();
        if (this.version == 1) {
            cookie.append(super.name);
            cookie.append("=");
            cookie.append(super.value);
            if (this.path_set) {
                cookie.append("; ");
                cookie.append("$Path=");
                cookie.append(super.path);
            }
            if (this.domain_set) {
                cookie.append("; ");
                cookie.append("$Domain=");
                cookie.append(super.domain);
            }
            if (this.port_set) {
                cookie.append("; ");
                cookie.append("$Port");
                if (this.port_list_str != null) {
                    cookie.append("=\"");
                    cookie.append(this.port_list_str);
                    cookie.append('\"');
                }
            }
            return cookie.toString();
        }
        throw new Error("Internal Error: unknown version " + this.version);
    }
    
    public String toString() {
        final StringBuffer res = new StringBuffer(super.name.length() + super.value.length() + 50);
        res.append(super.name).append('=').append(super.value);
        if (this.version == 1) {
            res.append("; Version=").append(this.version);
            res.append("; Path=").append(super.path);
            res.append("; Domain=").append(super.domain);
            if (this.port_set) {
                res.append("; Port=\"").append(this.port_list[0]);
                for (int idx = 1; idx < this.port_list.length; ++idx) {
                    res.append(',').append(this.port_list[idx]);
                }
                res.append('\"');
            }
            if (super.expires != null) {
                res.append("; Max-Age=").append((super.expires.getTime() - System.currentTimeMillis()) / 1000L);
            }
            if (this.discard) {
                res.append("; Discard");
            }
            if (super.secure) {
                res.append("; Secure");
            }
            if (this.comment != null) {
                res.append("; Comment=\"").append(this.comment).append('\"');
            }
            if (this.comment_url != null) {
                res.append("; CommentURL=\"").append(this.comment_url).append('\"');
            }
            return res.toString();
        }
        throw new Error("Internal Error: unknown version " + this.version);
    }
}
