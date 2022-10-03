package com.me.mdm.api.subscription;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class UserLicenseAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Logger LOGGER = Logger.getLogger("UserManagementLogger");
        try {
            final JSONObject res = new JSONObject();
            res.put("is_user_limit_reached", LicenseProvider.getInstance().isUserLimitReached());
            res.put("technician_purchased_count", (Object)LicenseProvider.getInstance().getNoOfTechnicians());
            res.put("technician_count", DMUserHandler.getUsersCountWithLogin());
            res.put("is_spicework_enabled", MDMUtil.getSyMParameter("isSpiceworksEnabled") != null && MDMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled"));
            res.put("multilang", LicenseProvider.getInstance().isLanguagePackEnabled());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)res);
            return response;
        }
        catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while getting user license details", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
