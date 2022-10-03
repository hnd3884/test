package com.me.mdm.server.windows.cspmeta;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CSPMetaFacade
{
    private static final Logger LOGGER;
    
    public JSONArray getCSPMetaData(final JSONObject apiRequest) {
        final JSONArray resultArray = new JSONArray();
        try {
            if (!apiRequest.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject messageBody = apiRequest.getJSONObject("msg_body");
            if (!messageBody.has("csp_details")) {
                throw new APIHTTPException("COM0005", new Object[] { "csp_details" });
            }
            final JSONArray cspURIArray = messageBody.getJSONArray("csp_details");
            for (int it = 0; it < cspURIArray.length(); ++it) {
                final JSONObject cspURIObject = cspURIArray.getJSONObject(it);
                resultArray.put((Object)CSPMetaDataHandler.getInstance().getMetaDetailsForLocURI(cspURIObject.getString("csp_uri")));
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            CSPMetaFacade.LOGGER.log(Level.SEVERE, "Exception while getting CSP Meta Details : ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
        return resultArray;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
