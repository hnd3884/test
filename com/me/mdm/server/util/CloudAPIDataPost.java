package com.me.mdm.server.util;

import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import com.me.mdm.certificate.CryptographyUtil;
import java.util.Base64;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;

public class CloudAPIDataPost
{
    private String className;
    private static final Logger LOGGER;
    private String authKey;
    public String response;
    public Integer status;
    
    public CloudAPIDataPost() {
        this.className = "CloudAPIDataPost";
        this.authKey = null;
        this.response = null;
        this.status = null;
    }
    
    private String appendAuthKeyToURL(final String url) {
        String appendChar = "?";
        if (url.contains("?")) {
            appendChar = "&";
        }
        final String authKey = MDMApiFactoryProvider.getCloudCSRSignAuthAPI().getAuthKey();
        return url + appendChar + "encapiKey=" + authKey + "&service=mdm";
    }
    
    private JSONObject getPublicKey() {
        final JSONObject publicKey = MDMApiFactoryProvider.getCloudCSRSignAuthAPI().getPublicKeyLatestVersion();
        return publicKey;
    }
    
    private void decryptResponseData() {
        try {
            final JSONObject publicKeyJSON = this.getPublicKey();
            final String publicKey = (String)publicKeyJSON.get("PUBLIC_KEY");
            final String responseData = new JSONObject(this.response).getString("RESPONSE");
            this.response = new String(CryptographyUtil.decryptWithPublicKey(publicKey, Base64.getDecoder().decode(responseData), "RSA"));
        }
        catch (final Exception ex) {
            CloudAPIDataPost.LOGGER.log(Level.SEVERE, "Exception in decryptResponseData", ex);
        }
    }
    
    public void encryptAndPostDataToCloud(final String url, final JSONObject bodyData, final String key) {
        try {
            final JSONObject publicKeyJSON = this.getPublicKey();
            final String publicKey = (String)publicKeyJSON.get("PUBLIC_KEY");
            final Integer publicKeyVersion = (Integer)publicKeyJSON.get("PUBLIC_KEY_VERSION");
            bodyData.put("encryptkey_version", (Object)publicKeyVersion);
            final String inputKey = bodyData.getString(key);
            final String keywithTimeStamp = inputKey + "::" + System.currentTimeMillis();
            final byte[] encryptedText = CryptographyUtil.encryptWithPublicKey(publicKey, keywithTimeStamp, "RSA");
            bodyData.put(key, (Object)new String(Base64.getEncoder().encode(encryptedText)));
            this.postDataToCloud(url, bodyData);
            this.decryptResponseData();
        }
        catch (final Exception ex) {
            CloudAPIDataPost.LOGGER.log(Level.SEVERE, "Exception in encryptAndPostDataToCloud", ex);
        }
    }
    
    public Properties getCodeForSecureKeys() {
        final Properties secureKeysCodeFromAPI = new Properties();
        try {
            CloudAPIDataPost.LOGGER.log(Level.INFO, "Getting Secure Keys hash code");
            final CloudAPIDataPost hashCodeData = new CloudAPIDataPost();
            hashCodeData.getDataFromCloud("https://mdmdatabase.manageengine.com/MISC/op_secure_key/op-code.txt");
            String hashCodeFromAPI = null;
            if (hashCodeData.status.toString().startsWith("20")) {
                hashCodeFromAPI = hashCodeData.response.toString();
                secureKeysCodeFromAPI.load(new StringReader(hashCodeFromAPI));
                CloudAPIDataPost.LOGGER.log(Level.INFO, "Got  20x respose for getCodeForSecureKeys");
            }
            else {
                CloudAPIDataPost.LOGGER.log(Level.INFO, "Did not get  20x respose for getCodeForSecureKeys. Current status is {0}", hashCodeData.status.toString());
            }
        }
        catch (final Exception ex) {
            CloudAPIDataPost.LOGGER.log(Level.SEVERE, "Exception in getCodeForSecureKeys", ex);
        }
        return secureKeysCodeFromAPI;
    }
    
    public void getDataFromCloud(final String url) {
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = url;
        dmHttpRequest.method = "GET";
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        final String responseString = dmHttpResponse.responseBodyAsString;
        this.status = dmHttpResponse.status;
        if (responseString == null) {
            this.status = 404;
        }
        this.response = responseString;
    }
    
    public void postDataToCloud(final String url, final JSONObject bodyData) {
        try {
            final String postUrl = this.appendAuthKeyToURL(url);
            final JSONObject headers = new JSONObject().put("Content-Length", bodyData.toString().getBytes().length);
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            dmHttpRequest.url = postUrl;
            dmHttpRequest.method = "POST";
            dmHttpRequest.headers = headers;
            dmHttpRequest.data = bodyData.toString().getBytes();
            final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
            final String responseString = dmHttpResponse.responseBodyAsString;
            this.status = dmHttpResponse.status;
            if (responseString == null) {
                this.status = 404;
            }
            this.response = responseString;
        }
        catch (final Exception ex) {
            final JSONObject errData = new JSONObject();
            errData.put("ErrorMsg", (Object)"Unknown Error. Contact support with logs");
            this.status = -1;
            this.response = errData.toString();
            CloudAPIDataPost.LOGGER.log(Level.SEVERE, "Exception in getting data from CLOUD API", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
