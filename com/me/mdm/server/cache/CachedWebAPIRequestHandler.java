package com.me.mdm.server.cache;

import java.io.IOException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.io.StringWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.HashMap;
import java.util.logging.Logger;

public class CachedWebAPIRequestHandler
{
    protected String cacheName;
    public static final String OS_UPDATE_CACHED_KEY = "OS_UPDATE_CACHED_KEY";
    private Logger mdmlogger;
    private HashMap cacheWebRequest;
    
    public CachedWebAPIRequestHandler() {
        this.cacheName = "MDM_WEB_REQUEST_RESPONSE";
        this.mdmlogger = Logger.getLogger("MDMLogger");
    }
    
    public DMHttpResponse getCachedURLGETRequest(final DMHttpRequest request, final String cachedKey) {
        DMHttpResponse serviceResponse = null;
        try {
            if (!this.isAlreadyCached(cachedKey)) {
                final DMHttpClient client = new DMHttpClient();
                serviceResponse = new DMHttpResponse();
                serviceResponse = client.execute(request);
                if (serviceResponse.status != 200) {
                    return serviceResponse;
                }
                if (this.cacheWebRequest == null) {
                    this.cacheWebRequest = new HashMap();
                }
                this.cacheWebRequest.put(cachedKey, serviceResponse);
                ApiFactoryProvider.getCacheAccessAPI().putCache(this.cacheName, (Object)this.cacheWebRequest, 1);
            }
            else {
                serviceResponse = this.cacheWebRequest.get(cachedKey);
            }
        }
        catch (final Exception e) {
            this.mdmlogger.log(Level.SEVERE, "Exception in getCached webrequest handler", e);
        }
        return serviceResponse;
    }
    
    private boolean isAlreadyCached(final String cachedKey) {
        boolean isAlreadyCached = false;
        this.cacheWebRequest = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache(this.cacheName, 1);
        if (this.cacheWebRequest != null) {
            final String cachedAddedKey = cachedKey + "ADDED_TIME";
            final Long requestCachedMillisec = this.cacheWebRequest.get(cachedAddedKey);
            final Long currentMillisec = System.currentTimeMillis();
            final Long thresholdTime = 43200000L;
            if (currentMillisec - requestCachedMillisec < thresholdTime) {
                isAlreadyCached = true;
            }
        }
        return isAlreadyCached;
    }
    
    public String getCachedURLGETRequest(final HttpURLConnection httpURLConnection, final String cachedKey) {
        String response = "";
        StringWriter out = null;
        BufferedReader reader = null;
        try {
            if (!this.isAlreadyCached(cachedKey)) {
                this.mdmlogger.log(Level.INFO, "Going to request to using cached get request");
                final int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    this.mdmlogger.log(Level.INFO, "Response received from server");
                    reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line = null;
                    out = new StringWriter((httpURLConnection.getContentLength() > 0) ? httpURLConnection.getContentLength() : 2048);
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                    }
                    response = out.toString();
                    if (this.cacheWebRequest == null) {
                        this.cacheWebRequest = new HashMap();
                    }
                    this.cacheWebRequest.put(cachedKey, response);
                    this.cacheWebRequest.put(cachedKey + "ADDED_TIME", System.currentTimeMillis());
                    ApiFactoryProvider.getCacheAccessAPI().putCache(this.cacheName, (Object)this.cacheWebRequest, 1);
                }
                else {
                    this.mdmlogger.log(Level.INFO, "Request failed for Apple Service:{0}", new Object[] { responseCode });
                }
            }
            if (MDMStringUtils.isEmpty(response)) {
                response = this.cacheWebRequest.get(cachedKey);
            }
        }
        catch (final IOException e) {
            this.mdmlogger.log(Level.SEVERE, "Exception in connecting apple service", e);
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e) {
                this.mdmlogger.log(Level.SEVERE, "Exception in closing io", e);
            }
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e2) {
                this.mdmlogger.log(Level.SEVERE, "Exception in closing io", e2);
            }
        }
        return response;
    }
}
