package com.adventnet.persistence.fos;

import java.io.IOException;
import com.zoho.clustering.util.HttpMethod;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpPollUtil
{
    private static HttpPollUtil inst;
    private FOSConfig config;
    private static final Logger LOG;
    
    public static HttpPollUtil getInst() {
        if (HttpPollUtil.inst == null) {
            throw new IllegalStateException("Http poll Util is not yet initialized");
        }
        return HttpPollUtil.inst;
    }
    
    public static void initialize(final FOSConfig configuration) {
        if (HttpPollUtil.inst == null) {
            HttpPollUtil.inst = new HttpPollUtil();
        }
        HttpPollUtil.inst.config = configuration;
    }
    
    public boolean sendMessage(final String peerIP, final String message) throws InterruptedException {
        int consecutiveFailureCount = 0;
        boolean msgStatus = false;
        while (!this.doHttpPing(peerIP, message)) {
            if (++consecutiveFailureCount >= this.config.httpPollRetryCount()) {
                HttpPollUtil.LOG.log(Level.SEVERE, "The 'poll_url' is no longer responding.");
                return msgStatus;
            }
            Thread.sleep(this.config.httpPollIntervalInSecs() * 1000);
        }
        consecutiveFailureCount = 0;
        msgStatus = true;
        return msgStatus;
    }
    
    public boolean isAlive() throws Exception {
        return this.sendMessage(this.config.getPeerIP(), "statuscheck");
    }
    
    private boolean doHttpPing(final String peerIP, final String message) {
        final String url = createURL(this.config.httpProtocol(), peerIP, this.config.connectorPort(), this.config.httpPollURI()) + "?message=" + message;
        final HttpMethod httpMeth = new HttpMethod(url);
        httpMeth.setConnectionTimeout(this.config.httpPollConnTimeout());
        httpMeth.setReadTimeout(this.config.httpPollReadTimeout());
        httpMeth.setHostnameVerifierClassName(this.config.hostnameVerifierClassName());
        int httpStatus;
        try {
            httpStatus = httpMeth.execute();
        }
        catch (final IOException exp) {
            HttpPollUtil.LOG.log(Level.WARNING, "IO ERROR. http_poll request. URL: {0}. Exception: {1}", new Object[] { httpMeth.getURL(), exp.toString() });
            return false;
        }
        if (httpStatus >= 400 && httpStatus < 500) {
            throw new RuntimeException("HTTP ERROR [" + httpStatus + "]. http_poll request [" + httpMeth.getURL() + "]. Fix the http_poll url.");
        }
        if (httpStatus == 503 || httpStatus == 502 || httpStatus == 504) {
            HttpPollUtil.LOG.log(Level.WARNING, "HTTP ERROR [{0}]. http_poll request. URL: {1}", new Object[] { httpStatus, httpMeth.getURL() });
            return false;
        }
        return true;
    }
    
    private static String createURL(final String protocol, final String peerIP, final String connectorPort, final String uriPart) {
        final boolean uriStartWithSlash = uriPart.startsWith("/");
        final String urlPart = protocol + "://" + peerIP + ":" + connectorPort;
        return uriStartWithSlash ? (urlPart + uriPart) : (urlPart + '/' + uriPart);
    }
    
    static {
        HttpPollUtil.inst = null;
        LOG = Logger.getLogger(HttpPollUtil.class.getName());
    }
}
