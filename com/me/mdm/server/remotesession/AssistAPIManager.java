package com.me.mdm.server.remotesession;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import java.util.Iterator;
import java.io.IOException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class AssistAPIManager
{
    private static final Logger LOGGER;
    private static final String ASSISTSESSIONAPIURL = "AssistSessionApiUrl";
    
    public JSONObject generateSession(final HashMap<String, String> headerMap, final HashMap<String, String> queryMap, final JSONObject assistProperties) throws JSONException {
        final JSONObject responseJSON = new JSONObject();
        try {
            final String type = "application/x-www-form-urlencoded";
            String assistURL = MDMApiFactoryProvider.getAssistAuthTokenHandler().getAssistSessionUrl(assistProperties.getLong("customerId"));
            if (queryMap.size() > 0) {
                assistURL += "?";
                for (final String key : queryMap.keySet()) {
                    assistURL += "&";
                    assistURL = assistURL + key + "=" + queryMap.get(key);
                }
            }
            final boolean urlCheck = this.assistUrlDomainCheck(assistURL);
            if (!urlCheck) {
                AssistAPIManager.LOGGER.log(Level.SEVERE, "Assist url is not valid URL: {0}", assistURL);
                throw new SecurityException("Assist url is not valid URL: " + assistURL);
            }
            final URL assistUrl = new URL(assistURL);
            HttpURLConnection con = null;
            BufferedReader in = null;
            responseJSON.put("Status", (Object)"Failure");
            try {
                final boolean useProxy = (boolean)assistProperties.optBoolean("useProxy", (boolean)Boolean.TRUE);
                Proxy proxy = null;
                if (useProxy) {
                    proxy = this.getProxy(assistUrl.toString());
                }
                con = (HttpURLConnection)assistUrl.openConnection((proxy == null) ? Proxy.NO_PROXY : proxy);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                for (final String key2 : headerMap.keySet()) {
                    final String value = headerMap.get(key2);
                    con.addRequestProperty(key2, value);
                }
                final int responseCode = con.getResponseCode();
                in = new BufferedReader(new InputStreamReader((responseCode == 200) ? con.getInputStream() : con.getErrorStream()));
                final StringBuffer response = new StringBuffer();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                if (responseCode == 200) {
                    final JSONObject json = new JSONObject(response.toString());
                    final String assisturl = String.valueOf(json.getJSONObject("representation").get("technician_url"));
                    final String sessionkey = String.valueOf(json.getJSONObject("representation").get("session_id"));
                    final JSONObject sessionInfo = new JSONObject();
                    sessionInfo.put("SESSION_KEY", (Object)sessionkey);
                    sessionInfo.put("SESSION_URL", (Object)assisturl);
                    responseJSON.put("Status", (Object)"Success");
                    responseJSON.put("SessionDetails", (Object)sessionInfo);
                    AssistAPIManager.LOGGER.log(Level.FINE, "Assist session key response{0}", json);
                }
                else if (responseCode == 400) {
                    final JSONObject json = new JSONObject(response.toString());
                    AssistAPIManager.LOGGER.log(Level.SEVERE, "Assist session key response{0}", json);
                    final JSONObject errorJSON = json.getJSONObject("error");
                    if (errorJSON != null) {
                        responseJSON.put("Status", (Object)"Failure");
                        responseJSON.put("Remarks", (Object)"dc.mdm.inv.remote.authentication_failed");
                        responseJSON.put("ErrorCode", errorJSON.getInt("code"));
                        responseJSON.put("Reason", (Object)String.valueOf(errorJSON.get("message")));
                        MessageProvider.getInstance().unhideMessage("ASSIST_AUTH_FAILED", Long.valueOf(assistProperties.optLong("customerId", -1L)));
                    }
                    else {
                        responseJSON.put("Status", (Object)"Failure");
                        responseJSON.put("Remarks", (Object)"Failed to contact Zoho Assist :: Bad request");
                    }
                    AssistAPIManager.LOGGER.log(Level.SEVERE, "Generate session bad request:: response:{0}", response.toString());
                }
                else {
                    responseJSON.put("Status", (Object)"Failure");
                    responseJSON.put("Remarks", (Object)("Failed to contact Zoho Assist :: HTTP Code :" + responseCode));
                    AssistAPIManager.LOGGER.log(Level.SEVERE, "Generate session failed::response code: {0}; response :{1}", new Object[] { String.valueOf(responseCode), response.toString() });
                }
            }
            catch (final IOException e) {
                AssistAPIManager.LOGGER.log(Level.SEVERE, "Exception occured", e);
                responseJSON.put("Status", (Object)"Failure");
                responseJSON.put("Remarks", (Object)("Unable to reach " + assistUrl.getHost() + " :: " + e.getLocalizedMessage()));
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (final IOException ex) {
                    AssistAPIManager.LOGGER.log(Level.SEVERE, null, ex);
                }
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        catch (final Exception ex2) {
            AssistAPIManager.LOGGER.log(Level.SEVERE, null, ex2);
            responseJSON.put("Status", (Object)"Failure");
            responseJSON.put("Remarks", (Object)("Unknown error :: " + ex2.getLocalizedMessage()));
        }
        AssistAPIManager.LOGGER.log(Level.INFO, "AssistAPIManger.generateSession() response {0}", responseJSON);
        return responseJSON;
    }
    
    private Proxy getProxy(final String url) throws Exception {
        Proxy proxy = null;
        String proxyHost = null;
        Integer proxyPort = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (proxyDetails != null) {
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
                proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)pacProps).get("proxyPort"));
            }
            else if (proxyType == 2) {
                proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)proxyDetails).get("proxyPort"));
            }
            final String proxyUsername = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
            final String proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                if (proxyUsername != null) {
                    final Authenticator authenticator = new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                        }
                    };
                    Authenticator.setDefault(authenticator);
                }
            }
        }
        return proxy;
    }
    
    private boolean assistUrlDomainCheck(final String assistURL) {
        boolean urlCheck = false;
        try {
            final List<String> urlDomain = new ArrayList<String>();
            urlDomain.add("https://assist.zoho.com");
            urlDomain.add("https://assist.zoho.eu");
            urlDomain.add("https://assist.zoho.in");
            urlDomain.add("https://assist.zoho.jp");
            urlDomain.add("https://assist.zoho.com.au");
            urlDomain.add("https://assist.zoho.com.cn");
            urlDomain.add("https://assistdev.localzoho.com");
            for (int i = 0; i < urlDomain.size(); ++i) {
                if (assistURL.startsWith(urlDomain.get(i))) {
                    urlCheck = true;
                    break;
                }
            }
        }
        catch (final Exception e) {
            AssistAPIManager.LOGGER.log(Level.INFO, "Error in checking assist Url domain ", e);
        }
        return urlCheck;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
