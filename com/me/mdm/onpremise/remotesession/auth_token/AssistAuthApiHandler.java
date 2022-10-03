package com.me.mdm.onpremise.remotesession.auth_token;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.HashMap;
import org.json.JSONObject;
import com.me.mdm.onpremise.remotesession.AssistApiHandlerImpl;

public class AssistAuthApiHandler implements AssistApiHandlerImpl
{
    @Override
    public HashMap getAssistHeaderMap(final JSONObject assitsDetails) {
        HashMap<String, String> headerParams = null;
        final String authTokens = (String)assitsDetails.get("AUTH_TOKEN");
        headerParams = new HashMap<String, String>();
        headerParams.put("Authorization", "Zoho-authtoken " + authTokens);
        headerParams.put("customer_email", ProductUrlLoader.getInstance().getValue("productcode") + "+assist@manageengine.com");
        headerParams.put("za-signature", "MDM-ON-PREMISES-b6325d6efa3b602f3e19229fc9fa16779092e8602ec26c88387c3799d6294ad7");
        headerParams.put("src", ProductUrlLoader.getInstance().getValue("productcode"));
        return headerParams;
    }
    
    @Override
    public String getAssistSessionUrl(final String domain) {
        String sessionUrl = null;
        try {
            sessionUrl = (domain.equals("com") ? new MDMUtil().getMDMApplicationProperties().getProperty("AssistSessionApi1Url") : new MDMUtil().getMDMApplicationProperties().getProperty("AssistSessionApi1Url").replaceFirst("com", domain));
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, null, e);
        }
        return sessionUrl;
    }
}
