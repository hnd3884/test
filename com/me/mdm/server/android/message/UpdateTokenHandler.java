package com.me.mdm.server.android.message;

import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class UpdateTokenHandler
{
    public Logger logger;
    
    public UpdateTokenHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void handleUpdateToken(final Long resourceId, final JSONObject data) {
        try {
            final String tokenType = String.valueOf(data.get("TokenType"));
            final JSONObject tokenDetails = data.getJSONObject("TokenDetails");
            if (tokenType.equalsIgnoreCase("PasscodeReset")) {
                final TokenUpdator updator = new ResetPasscodeTokenUpdator();
                updator.updateToken(resourceId, tokenDetails);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, (Throwable)e, () -> "Unable to persist the reset passcode token for resource " + n);
        }
    }
    
    interface TokenUpdator
    {
        void updateToken(final Long p0, final JSONObject p1);
    }
}
