package HTTPClient;

import java.io.IOException;

class RetryModule implements HTTPClientModule, GlobalConstants
{
    public int requestHandler(final Request req, final Response[] resp) {
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest roreq) throws IOException, ModuleException {
        try {
            resp.getStatusCode();
        }
        catch (final RetryException re) {
            Log.write(32, "RtryM: Caught RetryException");
            boolean got_lock = false;
            try {
                synchronized (re.first) {
                    got_lock = true;
                    final IdempotentSequence seq = new IdempotentSequence();
                    for (RetryException e = re.first; e != null; e = e.next) {
                        seq.add(e.request);
                    }
                    for (RetryException e2 = re.first; e2 != null; e2 = e2.next) {
                        Log.write(32, "RtryM: handling exception ", e2);
                        final Request req = e2.request;
                        final HTTPConnection con = req.getConnection();
                        if (!seq.isIdempotent(req) || (con.ServProtVersKnown && con.ServerProtocolVersion >= 65537 && req.num_retries > 0) || ((!con.ServProtVersKnown || con.ServerProtocolVersion <= 65536) && req.num_retries > 4) || e2.response.got_headers) {
                            e2.first = null;
                        }
                        else if (req.getStream() != null) {
                            if (HTTPConnection.deferStreamed) {
                                req.getStream().reset();
                                e2.response.setRetryRequest(true);
                            }
                            e2.first = null;
                        }
                        else {
                            if (req.getData() != null && e2.conn_reset) {
                                if (con.ServProtVersKnown && con.ServerProtocolVersion >= 65537) {
                                    this.addToken(req, "Expect", "100-continue");
                                }
                                else {
                                    req.delay_entity = 5000L << req.num_retries;
                                }
                            }
                            if (e2.next != null && e2.next.request.getData() != null && (!con.ServProtVersKnown || con.ServerProtocolVersion < 65537) && e2.conn_reset) {
                                this.addToken(req, "Connection", "close");
                            }
                            if (con.ServProtVersKnown && con.ServerProtocolVersion >= 65537 && e2.conn_reset) {
                                req.dont_pipeline = true;
                            }
                            req.dont_pipeline = true;
                            Log.write(32, "RtryM: Retrying request '" + req.getMethod() + " " + req.getRequestURI() + "'");
                            if (e2.conn_reset) {
                                final Request request = req;
                                ++request.num_retries;
                            }
                            e2.response.http_resp.set(req, con.sendRequest(req, e2.response.timeout));
                            e2.exception = null;
                            e2.first = null;
                        }
                    }
                    monitorexit(re.first);
                }
            }
            catch (final NullPointerException npe) {
                if (got_lock) {
                    throw npe;
                }
            }
            catch (final ParseException pe) {
                throw new IOException(pe.getMessage());
            }
            if (re.exception != null) {
                throw re.exception;
            }
            re.restart = true;
            throw re;
        }
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) {
        req.delay_entity = 0L;
        req.dont_pipeline = false;
        req.num_retries = 0;
        return 10;
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) {
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
    
    private void addToken(final Request req, final String hdr, final String tok) throws ParseException {
        NVPair[] hdrs;
        int idx;
        for (hdrs = req.getHeaders(), idx = 0; idx < hdrs.length && !hdrs[idx].getName().equalsIgnoreCase(hdr); ++idx) {}
        if (idx == hdrs.length) {
            hdrs = Util.resizeArray(hdrs, idx + 1);
            hdrs[idx] = new NVPair(hdr, tok);
            req.setHeaders(hdrs);
        }
        else if (!Util.hasToken(hdrs[idx].getValue(), tok)) {
            hdrs[idx] = new NVPair(hdr, String.valueOf(hdrs[idx].getValue()) + ", " + tok);
        }
    }
}
