package com.me.mdm.server.windows;

import java.util.Hashtable;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import java.util.Properties;
import com.me.idps.core.factory.IdpsFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.factory.BusinessStoreAccessInterface;

public class BusinessStoreAccessAPI implements BusinessStoreAccessInterface
{
    public static final String APP_AUTH = "app_authentication";
    public static final String SSO_AUTH = "is_sso_user";
    public static final String ADMIN_APP_AUTH = "is_authenticated";
    public static final String REDIRECT_URL = "redirect_url";
    
    @Override
    public JSONObject getSaaSAppDetails(final Long customerID, final Long userID) {
        final Properties azureAppDetails = IdpsFactoryProvider.getOauthImpl(201).fetchMetadata(customerID, userID);
        final JSONObject appDetails = new JSONObject();
        if (azureAppDetails.containsKey("OAUTH_CLIENT_ID") && azureAppDetails.containsKey("OAUTH_CLIENT_SECRET")) {
            appDetails.put("ClientID", ((Hashtable<K, Object>)azureAppDetails).get("OAUTH_CLIENT_ID"));
            appDetails.put("ClientSecret", ((Hashtable<K, Object>)azureAppDetails).get("OAUTH_CLIENT_SECRET"));
        }
        return appDetails;
    }
    
    @Override
    public JSONObject getBusinessStoreRedirectURL(final Long customerID, final Long userID) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_authentication", true);
        jsonObject.put("is_sso_user", false);
        jsonObject.put("is_authenticated", false);
        String url = null;
        url = "https://login.microsoftonline.com/common/oauth2/authorize?client_id=" + this.getSaaSAppDetails(customerID, userID).get("ClientID") + "&response_type=code&prompt=admin_consent";
        jsonObject.put("redirect_url", (Object)url);
        return jsonObject;
    }
    
    @Override
    public JSONObject configureBusinessStore(final JSONObject jsonObject) throws Exception {
        JSONObject response = new JSONObject();
        if (jsonObject.optString("code") == null) {
            response.put("Error", true);
            response.put("Message", (Object)"Code required");
        }
        response = WpAppSettingsHandler.getInstance().addBusinessStoreDetails(jsonObject);
        return response;
    }
}
