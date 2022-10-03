package com.me.idps.core.service.azure;

import com.me.idps.core.api.IdpsAPIException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.oauth.OauthUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.idps.core.oauth.OauthException;
import org.json.JSONObject;

public class AzureOauthHandler
{
    private static AzureOauthHandler azureOauthHandler;
    
    public static AzureOauthHandler getInstance() {
        if (AzureOauthHandler.azureOauthHandler == null) {
            AzureOauthHandler.azureOauthHandler = new AzureOauthHandler();
        }
        return AzureOauthHandler.azureOauthHandler;
    }
    
    public Long getAccessToken(final Long customerID, final Long userID, final JSONObject j) {
        try {
            if (j.has("error_msg")) {
                throw new OauthException("invalid_request");
            }
            final int domain = j.optInt("domain_type", -1);
            final JSONObject oauth = new JSONObject();
            oauth.put("USER_ID", (Object)userID);
            oauth.put("domain", domain);
            oauth.put("OAUTH_TYPE", 201);
            if (j.has("code")) {
                oauth.put("code", j.get("code"));
                oauth.put("scope", (Object)"https://graph.windows.net/Directory.Read.All offline_access");
            }
            MessageProvider.getInstance().hideMessage("IDP_RE_OAUTH");
            return OauthUtil.getInstance().register(customerID, oauth);
        }
        catch (final OauthException e) {
            final String eMsg = e.getMessage();
            IDPSlogger.ERR.log(Level.SEVERE, "getting OAuthException the Authorize URL", e);
            if (eMsg.equalsIgnoreCase("unavailable")) {
                throw new IdpsAPIException("AD009");
            }
            if (eMsg.equalsIgnoreCase("invalid_grant")) {
                throw new IdpsAPIException("AD011");
            }
            if (eMsg.equalsIgnoreCase("invalid_client")) {
                MessageProvider.getInstance().unhideMessage("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
                throw new IdpsAPIException("AD021");
            }
            throw new IdpsAPIException("AD011");
        }
        catch (final Exception e2) {
            IDPSlogger.SOM.log(Level.SEVERE, "getting Exception the Authorize URL", e2);
            throw new IdpsAPIException("COM0004");
        }
    }
    
    static {
        AzureOauthHandler.azureOauthHandler = null;
    }
}
