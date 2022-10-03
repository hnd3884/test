package HTTPClient;

import java.net.ProtocolException;
import java.io.IOException;
import java.util.Hashtable;

class AuthorizationModule implements HTTPClientModule
{
    private static Hashtable proxy_cntxt_list;
    private static Hashtable deferred_auth_list;
    private int auth_lst_idx;
    private int prxy_lst_idx;
    private int auth_scm_idx;
    private int prxy_scm_idx;
    private AuthorizationInfo auth_sent;
    private AuthorizationInfo prxy_sent;
    private boolean auth_from_4xx;
    private boolean prxy_from_4xx;
    private int num_tries;
    private Request saved_req;
    private Response saved_resp;
    
    AuthorizationModule() {
        this.auth_lst_idx = 0;
        this.prxy_lst_idx = 0;
        this.auth_scm_idx = 0;
        this.prxy_scm_idx = 0;
        this.auth_sent = null;
        this.prxy_sent = null;
        this.auth_from_4xx = false;
        this.prxy_from_4xx = false;
        this.num_tries = 0;
        this.saved_req = null;
        this.saved_resp = null;
    }
    
    public int requestHandler(final Request req, final Response[] resp) throws IOException, AuthSchemeNotImplException {
        final HTTPConnection con = req.getConnection();
        final AuthorizationHandler auth_handler = AuthorizationInfo.getAuthHandler();
        NVPair[] hdrs = req.getHeaders();
        int rem_idx = -1;
        final HttpOutputStream out = req.getStream();
        if (out != null && AuthorizationModule.deferred_auth_list.get(out) != null) {
            this.copyFrom(AuthorizationModule.deferred_auth_list.remove(out));
            req.copyFrom(this.saved_req);
            Log.write(8, "AuthM: Handling deferred auth challenge");
            this.handle_auth_challenge(req, this.saved_resp);
            if (this.auth_sent != null) {
                Log.write(8, "AuthM: Sending request with Authorization '" + this.auth_sent + "'");
            }
            else {
                Log.write(8, "AuthM: Sending request with Proxy-Authorization '" + this.prxy_sent + "'");
            }
            return 1;
        }
        Label_0382: {
            if (con.getProxyHost() != null && !this.prxy_from_4xx) {
                for (int idx = 0; idx < hdrs.length; ++idx) {
                    if (hdrs[idx].getName().equalsIgnoreCase("Proxy-Authorization")) {
                        rem_idx = idx;
                        break;
                    }
                }
                final Hashtable proxy_auth_list = Util.getList(AuthorizationModule.proxy_cntxt_list, con.getContext());
                AuthorizationInfo guess = proxy_auth_list.get(String.valueOf(con.getProxyHost()) + ":" + con.getProxyPort());
                if (guess != null) {
                    if (auth_handler != null) {
                        try {
                            guess = auth_handler.fixupAuthInfo(guess, req, null, null);
                        }
                        catch (final AuthSchemeNotImplException ex) {
                            break Label_0382;
                        }
                        if (guess == null) {
                            break Label_0382;
                        }
                    }
                    if (rem_idx == -1) {
                        rem_idx = hdrs.length;
                        hdrs = Util.resizeArray(hdrs, rem_idx + 1);
                        req.setHeaders(hdrs);
                    }
                    hdrs[rem_idx] = new NVPair("Proxy-Authorization", guess.toString());
                    rem_idx = -1;
                    this.prxy_sent = guess;
                    this.prxy_from_4xx = false;
                    Log.write(8, "AuthM: Preemptively sending Proxy-Authorization '" + guess + "'");
                }
            }
        }
        if (rem_idx >= 0) {
            System.arraycopy(hdrs, rem_idx + 1, hdrs, rem_idx, hdrs.length - rem_idx - 1);
            hdrs = Util.resizeArray(hdrs, hdrs.length - 1);
            req.setHeaders(hdrs);
        }
        rem_idx = -1;
        Label_0609: {
            if (!this.auth_from_4xx) {
                for (int idx = 0; idx < hdrs.length; ++idx) {
                    if (hdrs[idx].getName().equalsIgnoreCase("Authorization")) {
                        rem_idx = idx;
                        break;
                    }
                }
                AuthorizationInfo guess = AuthorizationInfo.findBest(req);
                if (guess != null) {
                    if (auth_handler != null) {
                        try {
                            guess = auth_handler.fixupAuthInfo(guess, req, null, null);
                        }
                        catch (final AuthSchemeNotImplException ex2) {
                            break Label_0609;
                        }
                        if (guess == null) {
                            break Label_0609;
                        }
                    }
                    if (rem_idx == -1) {
                        rem_idx = hdrs.length;
                        hdrs = Util.resizeArray(hdrs, rem_idx + 1);
                        req.setHeaders(hdrs);
                    }
                    hdrs[rem_idx] = new NVPair("Authorization", guess.toString());
                    rem_idx = -1;
                    this.auth_sent = guess;
                    this.auth_from_4xx = false;
                    Log.write(8, "AuthM: Preemptively sending Authorization '" + guess + "'");
                }
            }
        }
        if (rem_idx >= 0) {
            System.arraycopy(hdrs, rem_idx + 1, hdrs, rem_idx, hdrs.length - rem_idx - 1);
            hdrs = Util.resizeArray(hdrs, hdrs.length - 1);
            req.setHeaders(hdrs);
        }
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest req) throws IOException {
        if (resp.getStatusCode() != 401 && resp.getStatusCode() != 407) {
            if (this.auth_sent != null && this.auth_from_4xx) {
                try {
                    AuthorizationInfo.getAuthorization(this.auth_sent, req, resp, false).addPath(req.getRequestURI());
                }
                catch (final AuthSchemeNotImplException ex) {}
            }
            this.num_tries = 0;
        }
        this.auth_from_4xx = false;
        this.prxy_from_4xx = false;
        if (resp.getHeader("WWW-Authenticate") == null) {
            this.auth_lst_idx = 0;
            this.auth_scm_idx = 0;
        }
        if (resp.getHeader("Proxy-Authenticate") == null) {
            this.prxy_lst_idx = 0;
            this.prxy_scm_idx = 0;
        }
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) throws IOException, AuthSchemeNotImplException {
        final AuthorizationHandler h = AuthorizationInfo.getAuthHandler();
        if (h != null) {
            h.handleAuthHeaders(resp, req, this.auth_sent, this.prxy_sent);
        }
        final int sts = resp.getStatusCode();
        switch (sts) {
            case 401:
            case 407: {
                ++this.num_tries;
                if (this.num_tries > 10) {
                    throw new ProtocolException("Bug in authorization handling: server refused the given info 10 times");
                }
                if (req.getStream() == null) {
                    Log.write(8, "AuthM: Handling status: " + sts + " " + resp.getReasonLine());
                    this.handle_auth_challenge(req, resp);
                    if (this.auth_sent == null) {
                        if (this.prxy_sent == null) {
                            if (req.getStream() != null) {
                                Log.write(8, "AuthM: status " + sts + " not " + "handled - request has an output " + "stream");
                            }
                            else {
                                Log.write(8, "AuthM: No Auth Info found - status " + sts + " not handled");
                            }
                            return 10;
                        }
                    }
                    try {
                        resp.getInputStream().close();
                    }
                    catch (final IOException ex) {}
                    if (this.auth_sent != null) {
                        Log.write(8, "AuthM: Resending request with Authorization '" + this.auth_sent + "'");
                    }
                    else {
                        Log.write(8, "AuthM: Resending request with Proxy-Authorization '" + this.prxy_sent + "'");
                    }
                    return 13;
                }
                if (!HTTPConnection.deferStreamed) {
                    Log.write(8, "AuthM: status " + sts + " not handled - request has " + "an output stream");
                    return 10;
                }
                this.saved_req = (Request)req.clone();
                this.saved_resp = (Response)resp.clone();
                AuthorizationModule.deferred_auth_list.put(req.getStream(), this);
                req.getStream().reset();
                resp.setRetryRequest(true);
                Log.write(8, "AuthM: Handling of status " + sts + " deferred because an " + "output stream was used");
                return 10;
            }
            default: {
                return 10;
            }
        }
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) {
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) throws IOException {
        final AuthorizationHandler h = AuthorizationInfo.getAuthHandler();
        if (h != null) {
            h.handleAuthTrailers(resp, req, this.auth_sent, this.prxy_sent);
        }
    }
    
    private void handle_auth_challenge(final Request req, final Response resp) throws AuthSchemeNotImplException, IOException {
        final int[] idx_arr = { this.auth_lst_idx, this.auth_scm_idx };
        this.auth_sent = this.setAuthHeaders(resp.getHeader("WWW-Authenticate"), req, resp, "Authorization", idx_arr, this.auth_sent);
        if (this.auth_sent != null) {
            this.auth_from_4xx = true;
            this.auth_lst_idx = idx_arr[0];
            this.auth_scm_idx = idx_arr[1];
        }
        else {
            this.auth_lst_idx = 0;
            this.auth_scm_idx = 0;
        }
        idx_arr[0] = this.prxy_lst_idx;
        idx_arr[1] = this.prxy_scm_idx;
        this.prxy_sent = this.setAuthHeaders(resp.getHeader("Proxy-Authenticate"), req, resp, "Proxy-Authorization", idx_arr, this.prxy_sent);
        if (this.prxy_sent != null) {
            this.prxy_from_4xx = true;
            this.prxy_lst_idx = idx_arr[0];
            this.prxy_scm_idx = idx_arr[1];
        }
        else {
            this.prxy_lst_idx = 0;
            this.prxy_scm_idx = 0;
        }
        if (this.prxy_sent != null) {
            final HTTPConnection con = req.getConnection();
            Util.getList(AuthorizationModule.proxy_cntxt_list, con.getContext()).put(String.valueOf(con.getProxyHost()) + ":" + con.getProxyPort(), this.prxy_sent);
        }
        if (this.auth_sent != null || this.prxy_sent != null || resp.getHeader("WWW-Authenticate") != null || resp.getHeader("Proxy-Authenticate") != null) {
            return;
        }
        if (resp.getStatusCode() == 401) {
            throw new ProtocolException("Missing WWW-Authenticate header");
        }
        throw new ProtocolException("Missing Proxy-Authenticate header");
    }
    
    private AuthorizationInfo setAuthHeaders(final String auth_str, final Request req, final RoResponse resp, final String header, final int[] idx_arr, final AuthorizationInfo prev) throws ProtocolException, AuthSchemeNotImplException, IOException {
        if (auth_str == null) {
            return null;
        }
        final AuthorizationInfo[] challenges = AuthorizationInfo.parseAuthString(auth_str, req, resp);
        if (Log.isEnabled(8)) {
            Log.write(8, "AuthM: parsed " + challenges.length + " challenges:");
            for (int idx = 0; idx < challenges.length; ++idx) {
                Log.write(8, "AuthM: Challenge " + challenges[idx]);
            }
        }
        if (challenges.length == 0) {
            return null;
        }
        if (prev != null && prev.getScheme().equalsIgnoreCase("Basic")) {
            for (int idx = 0; idx < challenges.length; ++idx) {
                if (prev.getRealm().equals(challenges[idx].getRealm()) && prev.getScheme().equalsIgnoreCase(challenges[idx].getScheme())) {
                    AuthorizationInfo.removeAuthorization(prev, req.getConnection().getContext());
                }
            }
        }
        AuthorizationInfo credentials = null;
        final AuthorizationHandler auth_handler = AuthorizationInfo.getAuthHandler();
        while (credentials == null && idx_arr[0] != -1 && idx_arr[0] < challenges.length) {
            credentials = AuthorizationInfo.getAuthorization(challenges[idx_arr[0]], req, resp, false);
            if (auth_handler != null && credentials != null) {
                credentials = auth_handler.fixupAuthInfo(credentials, req, challenges[idx_arr[0]], resp);
            }
            if (++idx_arr[0] == challenges.length) {
                idx_arr[0] = -1;
            }
        }
        if (credentials == null) {
            for (int idx2 = 0; idx2 < challenges.length; ++idx2) {
                if (idx_arr[1] >= challenges.length) {
                    idx_arr[1] = 0;
                }
                try {
                    credentials = AuthorizationInfo.queryAuthHandler(challenges[idx_arr[1]], req, resp);
                    break;
                }
                catch (final AuthSchemeNotImplException asnie) {
                    if (idx2 == challenges.length - 1) {
                        throw asnie;
                    }
                }
                finally {
                    final int n = 1;
                    ++idx_arr[n];
                }
            }
        }
        if (credentials == null) {
            return null;
        }
        NVPair[] hdrs;
        int auth_idx;
        for (hdrs = req.getHeaders(), auth_idx = 0; auth_idx < hdrs.length && !hdrs[auth_idx].getName().equalsIgnoreCase(header); ++auth_idx) {}
        if (auth_idx == hdrs.length) {
            hdrs = Util.resizeArray(hdrs, auth_idx + 1);
            req.setHeaders(hdrs);
        }
        hdrs[auth_idx] = new NVPair(header, credentials.toString());
        return credentials;
    }
    
    private void copyFrom(final AuthorizationModule other) {
        this.auth_lst_idx = other.auth_lst_idx;
        this.prxy_lst_idx = other.prxy_lst_idx;
        this.auth_scm_idx = other.auth_scm_idx;
        this.prxy_scm_idx = other.prxy_scm_idx;
        this.auth_sent = other.auth_sent;
        this.prxy_sent = other.prxy_sent;
        this.auth_from_4xx = other.auth_from_4xx;
        this.prxy_from_4xx = other.prxy_from_4xx;
        this.num_tries = other.num_tries;
        this.saved_req = other.saved_req;
        this.saved_resp = other.saved_resp;
    }
    
    static {
        AuthorizationModule.proxy_cntxt_list = new Hashtable();
        AuthorizationModule.deferred_auth_list = new Hashtable();
    }
}
