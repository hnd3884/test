package com.me.mdm.onpremise.api.keygen.integrationservice;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.onpremise.server.integration.IntegrationProductUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IntegrationProductsAPIRequestHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("integeration_product", (Object)JSONUtil.getInstance().convertSimpleJSONarToJSONar(IntegrationProductUtil.getNewInstance().getIntegrationProductsList()));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)response);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in doGet...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
