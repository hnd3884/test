package com.me.mdm.api.core.misc;

import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class EvaluatorTrackingAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public EvaluatorTrackingAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Evaluator Tracking request start...");
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            final JSONObject requestBody = apiRequest.toJSONObject().getJSONObject("msg_body");
            final String module = String.valueOf(requestBody.get("module"));
            final String page = String.valueOf(requestBody.get("page"));
            evaluatorApi.addOrIncrementClickCountForTrialUsers(module, page);
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            this.logger.log(Level.INFO, "Evaluator Tracking request end...");
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in EvaluatorTrackingAPIRequestHandler");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
