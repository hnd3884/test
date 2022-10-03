package HTTPClient;

import java.net.ProtocolException;
import java.io.IOException;
import java.util.Hashtable;

class RedirectionModule implements HTTPClientModule
{
    private static Hashtable perm_redir_cntxt_list;
    private static Hashtable deferred_redir_list;
    private int level;
    private URI lastURI;
    private boolean new_con;
    private Request saved_req;
    
    RedirectionModule() {
        this.level = 0;
        this.lastURI = null;
        this.saved_req = null;
    }
    
    @Override
    public int requestHandler(final Request req, final Response[] resp) {
        HTTPConnection con = req.getConnection();
        final HttpOutputStream out = req.getStream();
        if (out != null && RedirectionModule.deferred_redir_list.get(out) != null) {
            this.copyFrom(RedirectionModule.deferred_redir_list.remove(out));
            req.copyFrom(this.saved_req);
            if (this.new_con) {
                return 5;
            }
            return 1;
        }
        else {
            URI cur_loc;
            try {
                cur_loc = new URI(new URI(con.getProtocol(), con.getHost(), con.getPort(), null), req.getRequestURI());
            }
            catch (final ParseException pe) {
                throw new Error("HTTPClient Internal Error: unexpected exception '" + pe + "'");
            }
            final Hashtable perm_redir_list = Util.getList(RedirectionModule.perm_redir_cntxt_list, req.getConnection().getContext());
            final URI new_loc;
            if ((new_loc = perm_redir_list.get(cur_loc)) == null) {
                return 0;
            }
            final String nres = new_loc.getPathAndQuery();
            req.setRequestURI(nres);
            try {
                this.lastURI = new URI(new_loc, nres);
            }
            catch (final ParseException ex) {}
            Log.write(32, "RdirM: matched request in permanent redirection list - redoing request to " + this.lastURI.toExternalForm());
            if (!con.isCompatibleWith(new_loc)) {
                try {
                    con = new HTTPConnection(new_loc);
                }
                catch (final Exception e) {
                    throw new Error("HTTPClient Internal Error: unexpected exception '" + e + "'");
                }
                con.setSSLSocketFactory(req.getConnection().getSSLSocketFactory());
                con.setContext(req.getConnection().getContext());
                req.setConnection(con);
                return 5;
            }
            return 1;
        }
    }
    
    @Override
    public void responsePhase1Handler(final Response resp, final RoRequest req) throws IOException {
        final int sts = resp.getStatusCode();
        if ((sts < 301 || sts > 307 || sts == 304) && this.lastURI != null) {
            resp.setEffectiveURI(this.lastURI);
        }
    }
    
    @Override
    public int responsePhase2Handler(final Response resp, final Request req) throws IOException {
        int sts = resp.getStatusCode();
        switch (sts) {
            case 302: {
                if (req.getMethod().equals("POST") || req.getMethod().equals("PUT")) {
                    Log.write(32, "RdirM: Received status: " + sts + " " + resp.getReasonLine() + " - treating as 303");
                    sts = 303;
                }
            }
            case 301:
            case 303:
            case 307: {
                Log.write(32, "RdirM: Handling status: " + sts + " " + resp.getReasonLine());
                if (!req.getMethod().equals("GET") && !req.getMethod().equals("HEAD") && sts != 303) {
                    Log.write(32, "RdirM: not redirected because method is neither HEAD nor GET");
                    if (sts == 301 && resp.getHeader("Location") != null) {
                        update_perm_redir_list(req, this.resLocHdr(resp.getHeader("Location"), req));
                    }
                    resp.setEffectiveURI(this.lastURI);
                    return 10;
                }
            }
            case 305:
            case 306: {
                if (sts == 305 || sts == 306) {
                    Log.write(32, "RdirM: Handling status: " + sts + " " + resp.getReasonLine());
                }
                if (sts == 305 && req.getConnection().getProxyHost() != null) {
                    Log.write(32, "RdirM: 305 ignored because a proxy is already in use");
                    resp.setEffectiveURI(this.lastURI);
                    return 10;
                }
                if (this.level >= 15 || resp.getHeader("Location") == null) {
                    if (this.level >= 15) {
                        Log.write(32, "RdirM: not redirected because of too many levels of redirection");
                    }
                    else {
                        Log.write(32, "RdirM: not redirected because no Location header was present");
                    }
                    resp.setEffectiveURI(this.lastURI);
                    return 10;
                }
                ++this.level;
                final URI loc = this.resLocHdr(resp.getHeader("Location"), req);
                this.new_con = false;
                HTTPConnection mvd;
                String nres;
                if (sts == 305) {
                    mvd = new HTTPConnection(req.getConnection().getProtocol(), req.getConnection().getHost(), req.getConnection().getPort());
                    mvd.setCurrentProxy(loc.getHost(), loc.getPort());
                    mvd.setSSLSocketFactory(req.getConnection().getSSLSocketFactory());
                    mvd.setContext(req.getConnection().getContext());
                    this.new_con = true;
                    nres = req.getRequestURI();
                    req.setMethod("GET");
                    req.setData(null);
                    req.setStream(null);
                }
                else {
                    if (sts == 306) {
                        return 10;
                    }
                    if (sts == 301 || sts == 302) {
                        mvd = new HTTPConnection(loc);
                        final String proxyHost = req.getConnection().getProxyHost();
                        final Integer proxyPort = req.getConnection().getProxyPort();
                        if (proxyHost != null && proxyPort != null) {
                            Log.write(32, "Connection " + mvd + "," + proxyHost + "," + proxyPort);
                            mvd.setCurrentProxy(proxyHost, new Integer(proxyPort));
                        }
                        mvd.setSSLSocketFactory(req.getConnection().getSSLSocketFactory());
                        mvd.setContext(req.getConnection().getContext());
                        nres = loc.getPathAndQuery();
                        this.new_con = true;
                    }
                    else {
                        if (req.getConnection().isCompatibleWith(loc)) {
                            mvd = req.getConnection();
                            nres = loc.getPathAndQuery();
                        }
                        else {
                            try {
                                mvd = new HTTPConnection(loc);
                                nres = loc.getPathAndQuery();
                            }
                            catch (final Exception e) {
                                if (req.getConnection().getProxyHost() == null || !loc.getScheme().equalsIgnoreCase("ftp")) {
                                    return 10;
                                }
                                mvd = new HTTPConnection("http", req.getConnection().getProxyHost(), req.getConnection().getProxyPort());
                                mvd.setCurrentProxy(null, 0);
                                nres = loc.toExternalForm();
                            }
                            mvd.setSSLSocketFactory(req.getConnection().getSSLSocketFactory());
                            mvd.setContext(req.getConnection().getContext());
                            this.new_con = true;
                        }
                        if (sts == 303) {
                            if (!req.getMethod().equals("HEAD")) {
                                req.setMethod("GET");
                            }
                            req.setData(null);
                            req.setStream(null);
                        }
                        else {
                            if (req.getStream() != null) {
                                if (!HTTPConnection.deferStreamed) {
                                    Log.write(32, "RdirM: status " + sts + " not handled - request " + "has an output stream");
                                    return 10;
                                }
                                this.saved_req = (Request)req.clone();
                                RedirectionModule.deferred_redir_list.put(req.getStream(), this);
                                req.getStream().reset();
                                resp.setRetryRequest(true);
                            }
                            if (sts == 301) {
                                try {
                                    update_perm_redir_list(req, new URI(loc, nres));
                                }
                                catch (final ParseException pe) {
                                    throw new Error("HTTPClient Internal Error: unexpected exception '" + pe + "'");
                                }
                            }
                        }
                        final NVPair[] hdrs = req.getHeaders();
                        for (int idx = 0; idx < hdrs.length; ++idx) {
                            if (hdrs[idx].getName().equalsIgnoreCase("Referer")) {
                                final HTTPConnection con = req.getConnection();
                                hdrs[idx] = new NVPair("Referer", con + req.getRequestURI());
                                break;
                            }
                        }
                    }
                }
                req.setConnection(mvd);
                req.setRequestURI(nres);
                try {
                    resp.getInputStream().close();
                }
                catch (final IOException ex) {}
                if (sts != 305 && sts != 306) {
                    try {
                        this.lastURI = new URI(loc, nres);
                    }
                    catch (final ParseException ex2) {}
                    Log.write(32, "RdirM: request redirected to " + this.lastURI.toExternalForm() + " using method " + req.getMethod());
                }
                else {
                    Log.write(32, "RdirM: resending request using proxy " + mvd.getProxyHost() + ":" + mvd.getProxyPort());
                }
                if (req.getStream() != null) {
                    return 10;
                }
                if (this.new_con) {
                    return 15;
                }
                return 13;
            }
            default: {
                return 10;
            }
        }
    }
    
    @Override
    public void responsePhase3Handler(final Response resp, final RoRequest req) {
    }
    
    @Override
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
    
    private static void update_perm_redir_list(final RoRequest req, final URI new_loc) {
        final HTTPConnection con = req.getConnection();
        URI cur_loc = null;
        try {
            cur_loc = new URI(new URI(con.getProtocol(), con.getHost(), con.getPort(), null), req.getRequestURI());
        }
        catch (final ParseException ex) {}
        if (!cur_loc.equals(new_loc)) {
            final Hashtable perm_redir_list = Util.getList(RedirectionModule.perm_redir_cntxt_list, con.getContext());
            perm_redir_list.put(cur_loc, new_loc);
        }
    }
    
    private URI resLocHdr(final String loc, final RoRequest req) throws ProtocolException {
        try {
            URI base = new URI(req.getConnection().getProtocol(), req.getConnection().getHost(), req.getConnection().getPort(), null);
            base = new URI(base, req.getRequestURI());
            final URI res = new URI(base, loc);
            if (res.getHost() == null) {
                throw new ProtocolException("Malformed URL in Location header: `" + loc + "' - missing host field");
            }
            return res;
        }
        catch (final ParseException pe) {
            throw new ProtocolException("Malformed URL in Location header: `" + loc + "' - exception was: " + pe.getMessage());
        }
    }
    
    private void copyFrom(final RedirectionModule other) {
        this.level = other.level;
        this.lastURI = other.lastURI;
        this.saved_req = other.saved_req;
    }
    
    static {
        RedirectionModule.perm_redir_cntxt_list = new Hashtable();
        RedirectionModule.deferred_redir_list = new Hashtable();
    }
}
