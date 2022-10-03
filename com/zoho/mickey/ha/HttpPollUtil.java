package com.zoho.mickey.ha;

import com.zoho.framework.utils.crypto.CryptoUtil;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import com.zoho.clustering.util.HttpMethod;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpPollUtil
{
    private static HttpPollUtil inst;
    private HAConfig config;
    private static final Logger LOG;
    private static String uniqueID;
    
    public static HttpPollUtil getInst() {
        if (HttpPollUtil.inst == null) {
            throw new IllegalStateException("Http poll Util is not yet initialized");
        }
        return HttpPollUtil.inst;
    }
    
    public static void initialize(final HAConfig configuration) {
        if (HttpPollUtil.inst == null) {
            HttpPollUtil.inst = new HttpPollUtil();
        }
        HttpPollUtil.inst.config = configuration;
    }
    
    public boolean sendMessage(final String peerIP, final String message) throws InterruptedException {
        return this.sendData(peerIP, message, this.config.httpPollURI());
    }
    
    private boolean sendData(final String peerIP, final String message, final String uri) throws InterruptedException {
        int consecutiveFailureCount = 0;
        boolean msgStatus = false;
        while (!this.doHttpPing(peerIP, message, uri)) {
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
    
    private boolean doHttpPing(final String peerIP, final String message, final String uri) {
        this.loadUniqueId();
        final String url = createURL(this.config.httpProtocol(), peerIP, this.config.connectorPort(), uri) + "?message=" + message;
        final HttpMethod httpMeth = new HttpMethod(url);
        httpMeth.setConnectionTimeout(this.config.httpPollConnTimeout());
        httpMeth.setReadTimeout(this.config.httpPollReadTimeout());
        httpMeth.setHostnameVerifierClassName(this.config.hostnameVerifierClassName());
        httpMeth.setRequestHeader((Map)new HashMap<String, String>() {
            {
                this.put("uniqueID", HttpPollUtil.uniqueID);
            }
        });
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
    
    private void loadUniqueId() {
        if (HttpPollUtil.uniqueID == null && HAImpl.UNIQUE_ID_FILE.exists()) {
            try {
                HttpPollUtil.uniqueID = CryptoUtil.decrypt(new String(Files.readAllBytes(HAImpl.UNIQUE_ID_FILE.toPath())));
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    static {
        HttpPollUtil.inst = null;
        LOG = Logger.getLogger(HttpPollUtil.class.getName());
        HttpPollUtil.uniqueID = null;
    }
}
