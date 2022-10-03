package com.me.mdm.api.remote;

import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.webclient.mdm.inv.InventoryRoleCheckUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import com.me.mdm.api.ApiRequestHandler;

public class RemoteStatusAPIHandler extends ApiRequestHandler
{
    RemoteSessionManager manager;
    
    public RemoteStatusAPIHandler() {
        this.manager = new RemoteSessionManager();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
            if (resourceID != null || resourceID != -1L) {
                if (!InventoryRoleCheckUtil.getInstance().doesDeviceBelongToCustomer(APIUtil.getCustomerID(requestJSON), resourceID)) {
                    this.logger.log(Level.INFO, "Command Invocation : User is not authorized to perform this remote operation");
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                this.logger.log(Level.INFO, "Command Invocation : User is authorized to perform this operation");
                response.put("RESPONSE", (Object)new JSONObject().put("status", this.manager.getSessionStatus(resourceID)));
            }
            response.put("status", 200);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in doGet - remote/sessionStatus/devices/:id", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long resourceID = APIUtil.getResourceID(apiRequest.toJSONObject(), "device_id");
            this.manager.handleRemoteSessionStatusUpdate(resourceID, 1);
            return new JSONObject().put("status", 204);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in doPut - remote/sessionStatus/devices/:id", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        try {
            final JSONArray deviceIds = (JSONArray)((JSONObject)requestJSON.get("msg_body")).get("device_ids");
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)this.manager.getMultipleSessionStatus(customerId, deviceIds));
            response.put("status", 200);
            return response;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "error in doGet - remote/multipleSessionStatus", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
