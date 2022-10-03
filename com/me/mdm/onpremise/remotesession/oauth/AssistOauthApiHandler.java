package com.me.mdm.onpremise.remotesession.oauth;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.idps.core.oauth.OauthException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.onpremise.remotesession.AuthTokenHandlerOnPremiseImpl;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.idps.core.oauth.OauthUtil;
import java.util.HashMap;
import org.json.JSONObject;
import com.me.mdm.onpremise.remotesession.AssistApiHandlerImpl;

public class AssistOauthApiHandler implements AssistApiHandlerImpl
{
    @Override
    public HashMap getAssistHeaderMap(final JSONObject assitsDetails) {
        HashMap<String, String> headerParams = null;
        try {
            final Long oauthTokensId = (Long)assitsDetails.get("AUTH_TOKEN_ID");
            final String authToken = OauthUtil.getInstance().fetchAccessTokenFromOauthId(oauthTokensId);
            headerParams = new HashMap<String, String>();
            headerParams.put("Authorization", "Zoho-oauthtoken " + authToken);
            headerParams.put("customer_email", ProductUrlLoader.getInstance().getValue("productcode") + "+assist@manageengine.com");
            headerParams.put("za-signature", "MDM-ON-PREMISES-b6325d6efa3b602f3e19229fc9fa16779092e8602ec26c88387c3799d6294ad7");
            headerParams.put("src", ProductUrlLoader.getInstance().getValue("productcode"));
        }
        catch (final OauthException e) {
            Logger.getLogger(AuthTokenHandlerOnPremiseImpl.class.getName()).log(Level.SEVERE, null, (Throwable)e);
        }
        return headerParams;
    }
    
    @Override
    public String getAssistSessionUrl(final String domain) {
        String sessionUrl = null;
        try {
            if (domain.equalsIgnoreCase("com")) {
                sessionUrl = new MDMUtil().getMDMApplicationProperties().getProperty("AssistSessionApi2Url");
            }
            else {
                sessionUrl = new MDMUtil().getMDMApplicationProperties().getProperty("AssistSessionApi2Url").replaceFirst("com", domain);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(AuthTokenHandlerOnPremiseImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return sessionUrl;
    }
}
