package com.me.mdm.server.profiles.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;

public class MacVPNConfigHandler extends IOSVPNConfigHandler
{
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            int connectionType = serverJSON.getInt("connection_type");
            if (++connectionType != 6 && connectionType != 7 && connectionType != 11) {
                this.logger.log(Level.INFO, "Unsupported VPN Connection Type:{0}", new Object[] { connectionType });
                throw new APIHTTPException("PAY0012", new Object[0]);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in MacVPN Config handler", (Throwable)e);
        }
    }
}
