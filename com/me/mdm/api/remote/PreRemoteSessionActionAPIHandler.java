package com.me.mdm.api.remote;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.webclient.mdm.inv.InventoryRoleCheckUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import com.me.mdm.api.ApiRequestHandler;

public class PreRemoteSessionActionAPIHandler extends ApiRequestHandler
{
    RemoteSessionManager manager;
    
    public PreRemoteSessionActionAPIHandler() {
        this.manager = new RemoteSessionManager();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
            if (resourceID != null && resourceID != -1L) {
                if (!InventoryRoleCheckUtil.getInstance().doesDeviceBelongToCustomer(APIUtil.getCustomerID(requestJSON), resourceID)) {
                    this.logger.log(Level.INFO, "Command Invocation : User is not authorized to perform this remote operation");
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                this.logger.log(Level.INFO, "Command Invocation : User is authorized to perform this operation");
                final String action = (String)((JSONObject)((JSONObject)apiRequest.toJSONObject().get("msg_header")).get("filters")).get("action");
                this.manager.PreRemoteSessionCommand(action, APIUtil.getCustomerID(requestJSON), resourceID);
            }
            response.put("status", 202);
            return response;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "error in doGet - remote/preRemoteSessionCommand/devices/:id", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
