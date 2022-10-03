package com.me.mdm.onpremise.server.chrome;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.util.CloudAPIDataPost;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class ChromeOAuthOPHandler
{
    public Logger logger;
    
    public ChromeOAuthOPHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public String getChromeClientDetails(final String key) {
        HashMap chromeClientKeys = new HashMap();
        try {
            chromeClientKeys = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("CHROME_CLIENT_KEYS", 0);
            if (chromeClientKeys == null || chromeClientKeys.isEmpty()) {
                final Properties props = new MDMUtil().getMDMApplicationProperties();
                chromeClientKeys = new HashMap();
                final String postUrl = "https://mdm.manageengine.com/api/v1/mdm/secretkeys";
                final CloudAPIDataPost postData = new CloudAPIDataPost();
                final JSONObject submitJSONObject = new JSONObject();
                submitJSONObject.put("keys", (Object)"CHROME_CLIENT_KEYS");
                postData.encryptAndPostDataToCloud(postUrl, submitJSONObject, "keys");
                if (postData.status != null && postData.status.toString().startsWith("20")) {
                    final String responseContent = postData.response;
                    this.logger.log(Level.INFO, "Data for Chrome keys obtained from cloud successfully.");
                    if (SyMUtil.isValidJSON(responseContent)) {
                        final JSONObject responseJSONObject = new JSONObject(responseContent);
                        chromeClientKeys.put("CHROMEMGMT_CLIENT_ID", responseJSONObject.optString("CHROMEMGMT_CLIENT_ID", (String)((Hashtable<K, String>)props).get("ChromeMgmt.ClientId")));
                        chromeClientKeys.put("CHROMEMGMT_CLIENT_SECRET", responseJSONObject.optString("CHROMEMGMT_CLIENT_SECRET", (String)((Hashtable<K, String>)props).get("ChromeMgmt.ClientSecret")));
                        ApiFactoryProvider.getCacheAccessAPI().putCache("CHROME_CLIENT_KEYS", (Object)chromeClientKeys, 0);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "Chrome client keys could not be found from cloud,so fetching from app conf");
                    chromeClientKeys.put("CHROMEMGMT_CLIENT_ID", ((Hashtable<K, Object>)props).get("ChromeMgmt.ClientId"));
                    chromeClientKeys.put("CHROMEMGMT_CLIENT_SECRET", ((Hashtable<K, Object>)props).get("ChromeMgmt.ClientSecret"));
                    ApiFactoryProvider.getCacheAccessAPI().putCache("CHROME_CLIENT_KEYS", (Object)chromeClientKeys, 0);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occurred in getting Chrome client keys: ", e);
        }
        return chromeClientKeys.getOrDefault(key, "");
    }
}
