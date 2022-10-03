package com.me.mdm.api.core.search;

import org.json.JSONArray;
import com.me.mdm.server.user.ManagedUserFacade;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.server.device.DeviceFacade;
import java.util.HashMap;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class APISearchHandler
{
    public static final String DEVICE_SCOPE = "devices";
    public static final String GROUP_SCOPE = "groups";
    public static final String USER_SCOPE = "users";
    private Logger logger;
    String[] validScopes;
    String paging;
    String metadata;
    
    public APISearchHandler() {
        this.logger = Logger.getLogger("MDMApiLogger");
        this.validScopes = new String[] { "groups", "devices", "users" };
        this.paging = "paging";
        this.metadata = "metadata";
    }
    
    public JSONObject searchResource(final JSONObject request) throws APIHTTPException {
        final JSONObject result = new JSONObject();
        try {
            final String temp = APIUtil.getStringFilter(request, "scope");
            if (temp != null) {
                final String[] split;
                final String[] scopes = split = temp.split(",");
                for (final String scope : split) {
                    final HashMap searcResult = this.search(scope, request);
                    result.put(scope, searcResult.get("result"));
                    result.put(this.paging, searcResult.get(this.paging));
                    result.put(this.metadata, searcResult.get(this.metadata));
                }
            }
            else {
                for (final String scope2 : this.validScopes) {
                    result.put(scope2, this.search(scope2, request).get("result"));
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in searchResource", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    private HashMap search(final String scope, final JSONObject request) throws JSONException, APIHTTPException {
        final HashMap<String, Object> searchResult = new HashMap<String, Object>();
        JSONArray result = null;
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final String searchTerm = APIUtil.getStringFilter(request, "q");
        final JSONObject filters = request.getJSONObject("msg_header").getJSONObject("filters");
        if (searchTerm != null) {
            filters.put("search", (Object)searchTerm);
        }
        switch (scope) {
            case "devices": {
                if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Inventory_Read", "MDM_Enrollment_Read", "MDM_AppMgmt_Read", "MDM_Configurations_Read", "MDM_ContentMgmt_Read" })) {
                    final DeviceFacade deviceFacade = new DeviceFacade();
                    final JSONObject devicesObj = (JSONObject)deviceFacade.getDevices(request);
                    result = devicesObj.getJSONArray("devices");
                    if (devicesObj.has(this.paging)) {
                        searchResult.put(this.paging, devicesObj.getJSONObject(this.paging));
                    }
                    searchResult.put(this.metadata, devicesObj.getJSONObject(this.metadata));
                    break;
                }
                break;
            }
            case "groups": {
                if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Settings_Read", "CA_Read", "MDM_AppMgmt_Read", "MDM_Configurations_Read", "MDM_ContentMgmt_Read", "MDM_Enrollment_Read", "MDM_Inventory_Read" })) {
                    final GroupFacade groupFacade = new GroupFacade();
                    final JSONObject groupObject = (JSONObject)groupFacade.getGroups(request);
                    result = groupObject.getJSONArray("groups");
                    break;
                }
                break;
            }
            case "users": {
                if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Inventory_Read", "MDM_Enrollment_Read", "MDM_AppMgmt_Read", "MDM_Configurations_Read", "MDM_ContentMgmt_Read" })) {
                    final ManagedUserFacade managedUserFacade = new ManagedUserFacade();
                    final JSONObject devicesObj = managedUserFacade.getUsers(request);
                    result = devicesObj.getJSONArray("users");
                    break;
                }
                break;
            }
        }
        searchResult.put("result", result);
        return searchResult;
    }
}
