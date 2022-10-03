package com.me.mdm.server.apps;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppPermissionFacade
{
    private Logger logger;
    private static final String PLATFORM = "platform";
    private static final String BUNDLE_ID = "bundle_id";
    private static final String APPPERMISSION_ID = "apppermission_id";
    
    public AppPermissionFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAppPermissionDetails(final JSONObject message) {
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final Long permissionConfigID = APIUtil.getResourceID(message, "apppermission_id");
            final Long appGroupID = (Long)DBUtil.getValueFromDB("InvAppGroupToPermission", "APP_PERMISSION_CONFIG_ID", (Object)permissionConfigID, "APP_GROUP_ID");
            if (appGroupID == null) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Integer platformType = AppsUtil.getPlatformTypeForAppGroupID(appGroupID);
            final BaseAppPermissionHandler handler = BaseAppPermissionHandler.getHandler(platformType);
            return handler.getAppPermission(customerID, null, permissionConfigID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to fetch permission", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to fetch permission", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    public JSONObject getAppCentricPermission(final JSONObject message) {
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            Long appGroupID = null;
            final JSONObject filter = message.getJSONObject("msg_header").optJSONObject("filters");
            final Integer platformType = filter.getInt("platform");
            if (filter.has("bundle_id")) {
                final String bundleID = String.valueOf(filter.get("bundle_id"));
                appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(bundleID, platformType, customerID);
            }
            if (appGroupID == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Unknown Bundle Identifier" });
            }
            final BaseAppPermissionHandler handler = BaseAppPermissionHandler.getHandler(platformType);
            return handler.getAppPermission(customerID, appGroupID, null);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to fetch permission", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to fetch permission", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    public void deleteAppConfig(final JSONObject message) {
        try {
            final JSONObject filter = message.getJSONObject("msg_header").optJSONObject("filters");
            final String bundleID = String.valueOf(filter.get("bundle_id"));
            final Integer platformType = filter.getInt("platform");
            final Long customerID = APIUtil.getCustomerID(message);
            final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(bundleID, platformType, customerID);
            final BaseAppPermissionHandler handler = BaseAppPermissionHandler.getHandler(platformType);
            if (appGroupID != null) {
                handler.deleteAppConfig(appGroupID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "FAILED: To delete Mac app permission.");
        }
    }
    
    public void deleteAppPermissionDetails(final JSONObject message) {
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final Long permissionConfigID = APIUtil.getResourceID(message, "apppermission_id");
            final BaseAppPermissionHandler handler = BaseAppPermissionHandler.getHandler(1);
            handler.deleteAppPermissionDetails(customerID, permissionConfigID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "FAILED: To delete Mac app permission.");
        }
    }
    
    public JSONObject addOrModifyAppPermission(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject jsonObject = message.getJSONObject("msg_body");
            final Long customerID = APIUtil.getCustomerID(message);
            final Long permissionConfigID = APIUtil.getResourceID(message, "apppermission_id");
            final Long userID = APIUtil.getUserID(message);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("USER_ID", (Object)userID);
            jsonObject.put("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigID);
            final int platformType = jsonObject.getInt("platform");
            final BaseAppPermissionHandler handler = BaseAppPermissionHandler.getHandler(platformType);
            return handler.addOrModifyAppPermission(jsonObject);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to add/Modify App permission", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
