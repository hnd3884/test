package com.me.mdm.api.core.misc;

import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class UserParamsApiRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public UserParamsApiRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        String paramName = null;
        try {
            final Long userID = APIUtil.getUserID(apiRequest.toJSONObject());
            paramName = APIUtil.getStringFilter(apiRequest.toJSONObject(), "params");
            final String[] params = paramName.split(",");
            final JSONObject result = MDMUtil.getUserParameters(userID, params);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)result);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in UserParamsApiRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final SyMException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in UserParamsApiRequestHandler", (Throwable)e2);
            throw new APIHTTPException("COM0024", new Object[] { paramName });
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final Long userID = APIUtil.getUserID(apiRequest.toJSONObject());
            final JSONObject jsonObject = apiRequest.toJSONObject().getJSONObject("msg_body");
            final JSONArray params = (JSONArray)jsonObject.get("params");
            final JSONObject paramsToUpdate = new JSONObject();
            for (int i = 0; i < params.length(); ++i) {
                final JSONObject param = params.getJSONObject(i);
                paramsToUpdate.put(String.valueOf(param.get("key")), (Object)String.valueOf(param.get("value")));
            }
            MDMUtil.updateUserParameters(userID, paramsToUpdate);
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)paramsToUpdate);
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in UserParamsApiRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final SyMException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in UserParamsApiRequestHandler", (Throwable)e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
