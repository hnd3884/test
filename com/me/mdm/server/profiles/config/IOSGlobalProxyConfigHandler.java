package com.me.mdm.server.profiles.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;

public class IOSGlobalProxyConfigHandler extends DefaultConfigHandler
{
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            if (serverJSON.has("PROXY_TYPE")) {
                final Integer proxyType = serverJSON.getInt("PROXY_TYPE");
                if (proxyType == 1) {
                    serverJSON.get("PROXY_SERVER");
                    serverJSON.get("PROXY_SERVER_PORT");
                }
                else if (proxyType == 2) {
                    serverJSON.get("PROXY_PAC_URL");
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Invalid json", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
}
