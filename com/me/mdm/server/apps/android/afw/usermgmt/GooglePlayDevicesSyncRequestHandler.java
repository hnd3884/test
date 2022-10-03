package com.me.mdm.server.apps.android.afw.usermgmt;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import org.json.JSONArray;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONObject;

public class GooglePlayDevicesSyncRequestHandler
{
    private JSONObject playStoreDetails;
    
    public GooglePlayDevicesSyncRequestHandler(final Long customerId) throws DataAccessException, JSONException {
        this.playStoreDetails = null;
        this.playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
    }
    
    public void syncUsers() throws Exception {
        this.syncUser(null);
    }
    
    public void syncUser(final String identifier) throws Exception {
        final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
        userDirectory.initialize(this.playStoreDetails);
        JSONObject users = new JSONObject();
        if (identifier == null) {
            users = userDirectory.getUsers(this.playStoreDetails);
        }
        else {
            final JSONObject user = userDirectory.getUserForIdentifer(identifier);
            final JSONObject userObject = new JSONObject();
            userObject.put(String.valueOf(user.get("EMAIL_ADDRESS")), (Object)user);
            users.put("users", (Object)userObject);
        }
        users.put("businessstore_id", this.playStoreDetails.get("BUSINESSSTORE_ID"));
        final StoreAccountManagementHandler handler = new StoreAccountManagementHandler();
        handler.addOrUpdateStoreUserAccounts(users);
    }
    
    public void syncDevices(JSONArray usersList) throws Exception {
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(this.playStoreDetails);
        final StoreAccountManagementHandler handler = new StoreAccountManagementHandler();
        usersList = ((usersList == null) ? new JSONArray((Collection)handler.getStoreAccountUsers()) : usersList);
        for (int index = 0; index < usersList.length(); index += 50) {
            if (index != 0) {
                Thread.sleep(3000L);
            }
            final JSONObject users = new JSONObject();
            users.put("users", (Object)JSONUtil.getInstance().subJSONArray(usersList, index, 50));
            final JSONObject devices = ebs.getDevices(users);
            devices.put("businessstore_id", this.playStoreDetails.get("BUSINESSSTORE_ID"));
            handler.addOrUpdateStoreUserDevices(devices);
        }
    }
}
