package com.me.mdm.mdmmigration;

import java.util.Iterator;
import HTTPClient.NVPair;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class APIRequestHandler
{
    private static Logger logger;
    
    public abstract JSONObject processRequest(final JSONObject p0);
    
    protected JSONObject getMsgContent(final JSONObject msgJson) throws JSONException {
        JSONObject msgContent = new JSONObject();
        final String msgContentString = (msgJson.get("msgContent") == null) ? null : msgJson.get("msgContent").toString();
        if (msgContentString != null) {
            msgContent = new JSONObject(msgContentString);
        }
        return msgContent;
    }
    
    public static Properties getPropertiesFromconf() throws Exception {
        final Properties confUrls = new Properties();
        FileInputStream inputStream = null;
        try {
            final String confDir = System.getProperty("server.home") + File.separator + "conf";
            final File confContent = new File(confDir + File.separator + "mdmApplication.conf");
            inputStream = new FileInputStream(confContent.getCanonicalPath());
            confUrls.load(inputStream);
        }
        catch (final Exception e) {
            APIRequestHandler.logger.log(Level.SEVERE, "APIRequestHandler getPropertiesFromconf() error while mdmapplicationconf props loading", e);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return confUrls;
    }
    
    protected JSONObject executeHTTPRequest(final DMHttpRequest request) throws Exception {
        return this.executeHTTPRequest(request, true);
    }
    
    protected JSONObject executeHTTPRequest(final DMHttpRequest request, final boolean isProxyRequired) throws Exception {
        final JSONObject responseJson = new JSONObject();
        final String allowedUrlsListStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("migration_allowed_urls");
        final String[] allowedUrlsStrArr = allowedUrlsListStr.split(",");
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("migrationTestAccount")) {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                APIRequestHandler.logger.log(Level.INFO, "Request URL : {0}, Allowed URL : {1}", new Object[] { request.url, allowedUrlsListStr });
                if (request.url == null || !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("proxyEnabledForMigration")) {
                    APIRequestHandler.logger.log(Level.INFO, "Migration APIError : proxyEnabledForMigration value {0} and request url value {1}", new Object[] { MDMFeatureParamsHandler.getInstance().isFeatureEnabled("proxyEnabledForMigration"), request.url });
                    return new JSONObject();
                }
                Boolean vaildUrl = false;
                for (final String allowedUrlsStr : allowedUrlsStrArr) {
                    if (request.url.startsWith(allowedUrlsStr)) {
                        vaildUrl = true;
                    }
                }
                if (!vaildUrl) {
                    APIRequestHandler.logger.log(Level.INFO, "Migration APIError : Allowed URLs is not valid  or request url is null");
                    return new JSONObject();
                }
            }
        }
        APIRequestHandler.logger.log(Level.INFO, "Going to request server for Migration");
        final DMHttpClient client = new DMHttpClient();
        DMHttpResponse response = new DMHttpResponse();
        client.setUseProxyIfConfigured(isProxyRequired);
        response = client.execute(request);
        final int httpstatus = response.status;
        responseJson.put("StatusCode", httpstatus);
        String responseStr = null;
        if (!String.valueOf(httpstatus).startsWith("20")) {
            APIRequestHandler.logger.log(Level.SEVERE, "MDMMigration APIRequestHandler executeHTTPRequest() HTTP Code is not 20x: {0}", httpstatus);
        }
        final JSONObject headerObject = response.responseHeaders;
        try {
            responseStr = response.responseBodyAsString;
            if (!responseStr.equalsIgnoreCase("")) {
                final JSONObject responseMsgJson = new JSONObject(responseStr);
                responseJson.put("ResponseJson", (Object)responseMsgJson);
            }
            else {
                responseJson.put("ResponseJson", (Object)new JSONObject());
            }
        }
        catch (final JSONException je) {
            try {
                final JSONArray responseMsgJson2 = new JSONArray(responseStr);
                responseJson.put("ResponseJson", (Object)responseMsgJson2);
            }
            catch (final Exception e) {
                APIRequestHandler.logger.log(Level.SEVERE, "MDMMigration APIRequestHandler executeHTTPRequest() Error! Response data is not Json: {0}", responseStr);
                responseJson.put("ResponseError", (Object)"JSONException");
                responseJson.put("ResponseJson", (Object)new JSONObject());
            }
        }
        catch (final Exception e2) {
            APIRequestHandler.logger.log(Level.SEVERE, "MDMMigration APIRequestHandler executeHTTPRequest() Error! Response data is not Json: {0}", responseStr);
            responseJson.put("ResponseError", (Object)"JSONException");
            responseJson.put("ResponseJson", (Object)new JSONObject());
        }
        responseJson.put("ResponseHeader", (Object)headerObject);
        return responseJson;
    }
    
    private NVPair[] getHeadersAsNVPairs(final DMHttpRequest request) throws Exception {
        final NVPair[] headersNVPair = new NVPair[request.headers.length() + 1];
        final Iterator<String> headerKeys = request.headers.keys();
        for (int i = 0; i < request.headers.length(); ++i) {
            final String headerKey = headerKeys.next();
            headersNVPair[i] = new NVPair(headerKey, request.headers.get(headerKey).toString());
        }
        headersNVPair[request.headers.length()] = new NVPair("Content-Type", "application/json");
        return headersNVPair;
    }
    
    static {
        APIRequestHandler.logger = Logger.getLogger("MDMLogger");
    }
}
