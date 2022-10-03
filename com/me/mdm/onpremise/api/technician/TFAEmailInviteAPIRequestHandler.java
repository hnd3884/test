package com.me.mdm.onpremise.api.technician;

import org.json.JSONObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.onpremise.server.user.TwoFactorAuthenticationAPIMDMPImpl;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class TFAEmailInviteAPIRequestHandler extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new TwoFactorAuthenticationAPIMDMPImpl().sendEmailInvitation(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", (Object)202);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Issue on handling TFAEmailInvite {0}", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("RESPONSE", (Object)new TwoFactorAuthenticationAPIMDMPImpl().getQRCodeForGoogleAuthInvite(apiRequest.toJSONObject()));
        responseJSON.put("status", 200);
        return responseJSON;
    }
}
