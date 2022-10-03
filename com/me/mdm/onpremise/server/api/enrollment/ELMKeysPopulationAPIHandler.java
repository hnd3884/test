package com.me.mdm.onpremise.server.api.enrollment;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ELMKeysPopulationAPIHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public ELMKeysPopulationAPIHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJson = new JSONObject();
            final AndroidAgentSecretsHandler androidAgentSecretsHandler = new AndroidAgentSecretsHandler();
            final JSONObject updateElmKeyInDbResponse = androidAgentSecretsHandler.updateElmKeysInDB();
            final String errorCode = updateElmKeyInDbResponse.optString("error_code");
            if (!errorCode.isEmpty() && errorCode.equals("COM0015")) {
                final String error = updateElmKeyInDbResponse.optString("error");
                throw new APIHTTPException("COM0015", new Object[] { error });
            }
            responseJson.put("status", 200);
            responseJson.put("RESPONSE", (Object)updateElmKeyInDbResponse);
            return responseJson;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "ELM keys update from creator to DB occurred error");
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
