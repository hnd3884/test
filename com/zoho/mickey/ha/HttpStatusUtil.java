package com.zoho.mickey.ha;

import com.zoho.framework.utils.crypto.CryptoUtil;
import java.nio.file.Files;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import com.zoho.clustering.util.HttpMethod;
import java.util.logging.Logger;

public class HttpStatusUtil
{
    private static HttpStatusUtil inst;
    private HAConfig config;
    private static final Logger LOG;
    private static String uniqueID;
    
    public static HttpStatusUtil getInstance() {
        if (HttpStatusUtil.inst == null) {
            throw new IllegalStateException("Http status Util is not yet initialized");
        }
        return HttpStatusUtil.inst;
    }
    
    public static void initialize(final HAConfig config2) {
        if (HttpStatusUtil.inst == null) {
            HttpStatusUtil.inst = new HttpStatusUtil();
        }
        HttpStatusUtil.inst.config = config2;
    }
    
    public boolean sendStatus(final String operationType, final String ipAddr, final Object updValue) {
        this.loadUniqueId();
        final String url = createURL(this.config.httpProtocol(), this.config.getPeerIP(), this.config.connectorPort(), this.config.httpStatusURI());
        final HttpMethod httpMeth = new HttpMethod(url);
        httpMeth.setConnectionTimeout(this.config.httpPollConnTimeout());
        httpMeth.setReadTimeout(this.config.httpPollReadTimeout());
        httpMeth.setHostnameVerifierClassName(this.config.hostnameVerifierClassName());
        httpMeth.setRequestMethod("POST");
        httpMeth.setPostParam("operationType=" + operationType + "&ipAddr=" + ipAddr + "&value=" + updValue);
        httpMeth.setRequestHeader((Map)new HashMap<String, String>() {
            {
                this.put("uniqueID", HttpStatusUtil.uniqueID);
            }
        });
        int httpStatus;
        try {
            httpStatus = httpMeth.execute();
        }
        catch (final IOException exp) {
            HttpStatusUtil.LOG.log(Level.WARNING, "IO ERROR. http status update URL: {0}. Exception: {1}", new Object[] { httpMeth.getURL(), exp.toString() });
            return false;
        }
        if (httpStatus >= 400 && httpStatus < 500) {
            throw new RuntimeException("HTTP ERROR [" + httpStatus + "]. http status update request [" + httpMeth.getURL() + "]. Fix the http_poll url.");
        }
        if (httpStatus == 503 || httpStatus == 502 || httpStatus == 504) {
            HttpStatusUtil.LOG.log(Level.WARNING, "HTTP ERROR [{0}]. http_poll request. URL: {1}", new Object[] { httpStatus, httpMeth.getURL() });
            return false;
        }
        return true;
    }
    
    private void loadUniqueId() {
        if (HttpStatusUtil.uniqueID == null && HAImpl.UNIQUE_ID_FILE.exists()) {
            try {
                HttpStatusUtil.uniqueID = CryptoUtil.decrypt(new String(Files.readAllBytes(HAImpl.UNIQUE_ID_FILE.toPath())));
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static String createURL(final String protocol, final String peerIP, final String connectorPort, final String uriPart) {
        final boolean uriStartWithSlash = uriPart.startsWith("/");
        final String urlPart = protocol + "://" + peerIP + ":" + connectorPort;
        return uriStartWithSlash ? (urlPart + uriPart) : (urlPart + '/' + uriPart);
    }
    
    static {
        HttpStatusUtil.inst = null;
        LOG = Logger.getLogger(HttpStatusUtil.class.getName());
        HttpStatusUtil.uniqueID = null;
    }
}
