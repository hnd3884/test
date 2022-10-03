package com.me.mdm.api.core.roles;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.role.RolesFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AvailableRolesAPIRequestHandler extends ApiRequestHandler
{
    private RolesFacade rolesFacade;
    
    public AvailableRolesAPIRequestHandler() {
        this.rolesFacade = new RolesFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Logger LOGGER = Logger.getLogger("UserManagementLogger");
        try {
            final JSONObject message = apiRequest.toJSONObject();
            final JSONObject res = this.rolesFacade.getRolesForRoleForm();
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)res);
            return response;
        }
        catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while getting roles for role form :", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
