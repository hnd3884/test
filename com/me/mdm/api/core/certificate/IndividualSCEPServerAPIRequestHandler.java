package com.me.mdm.api.core.certificate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.ScepServerFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualSCEPServerAPIRequestHandler extends ApiRequestHandler
{
    ScepServerFacade facade;
    
    public IndividualSCEPServerAPIRequestHandler() {
        this.facade = new ScepServerFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "servers";
            final JSONObject json = apiRequest.toJSONObject();
            final Long serverID = APIUtil.getResourceID(json, "server_id");
            final Long customerID = APIUtil.getCustomerID(json);
            responseDetails.put("RESPONSE", (Object)this.facade.getScepServer(customerID, serverID));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error during get scep server details  ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "servers";
            final JSONObject json = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(json);
            final Long scepServerId = APIUtil.getResourceID(json, "server_id");
            final Long userId = APIUtil.getUserID(json);
            final Long loginId = APIUtil.getLoginID(json);
            responseDetails.put("RESPONSE", (Object)this.facade.modifyScepServer(customerId, userId, loginId, scepServerId, json));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error during update scep server  ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error during update scep server ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            apiRequest.urlStartKey = "servers";
            final JSONObject json = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(json);
            final Long scepServerId = APIUtil.getResourceID(json, "server_id");
            final Long userId = APIUtil.getUserID(json);
            final Long loginId = APIUtil.getLoginID(json);
            responseDetails.put("RESPONSE", (Object)this.facade.deleteScepServer(customerId, userId, loginId, scepServerId, json));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error during delete SCEP server ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error during delete SCEP server ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
