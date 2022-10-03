package com.me.mdm.api.mdmmigration;

import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import com.me.mdm.api.APIRequest;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualConfigAPIRequestHandler extends ApiRequestHandler
{
    protected JSONObject getMsgBody(final JSONObject msgJson) throws JSONException {
        JSONObject msgContent = new JSONObject();
        final String msgContentString = JSONUtil.optString(msgJson, "msg_body");
        if (msgContentString != null) {
            msgContent = new JSONObject(msgContentString);
        }
        return msgContent;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "serviceconfigs";
            responseDetails.put("RESPONSE", (Object)new MigrationServicesFacade().getAPIServiceConfigDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) {
        final JSONObject responseDetails = new JSONObject();
        try {
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "serviceconfigs";
            responseDetails.put("RESPONSE", (Object)new MigrationServicesFacade().editServiceConfig(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "IndividualConfigAPIRequestHandler exception in editServiceConfig ", (Throwable)e);
            throw new APIHTTPException("SCN0002", new Object[] { e });
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "IndividualConfigAPIRequestHandler exception in editServiceConfig ", (Throwable)e2);
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        catch (final APIHTTPException e3) {
            this.logger.log(Level.SEVERE, e3.getMessage());
            throw e3;
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "IndividualConfigAPIRequestHandler exception in editServiceConfig ", e4);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) {
        try {
            final JSONObject responseDetails = new JSONObject();
            apiRequest.urlStartKey = "serviceconfigs";
            new MigrationServicesFacade().deleteServiceConfig(apiRequest.toJSONObject());
            responseDetails.put("status", 204);
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
