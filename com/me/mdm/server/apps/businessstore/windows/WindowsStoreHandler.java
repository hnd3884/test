package com.me.mdm.server.apps.businessstore.windows;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import java.util.Properties;
import java.util.List;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseStoreHandler;

public class WindowsStoreHandler extends BaseStoreHandler
{
    public static final String BSTORE_STORE_SYNC_PARAM = "bstoreFirstSyncPending";
    Logger logger;
    
    public WindowsStoreHandler() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public WindowsStoreHandler(final Long storeID, final Long customerID) {
        super(storeID, customerID);
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.platformType = 3;
        this.serviceType = BusinessStoreSyncConstants.BS_SERVICE_WBS;
        this.storeSyncKey = "bstoreFirstSyncPending";
    }
    
    @Override
    public JSONObject addStoreDetails(final JSONObject message) throws Exception {
        final String userName = APIUtil.getUserName(message);
        final JSONObject jsonObject = message.getJSONObject("msg_body");
        final JSONObject params = new JSONObject();
        final Long userID = APIUtil.getUserID(message);
        params.put("USER_ID", (Object)userID);
        params.put("CustomerID", (Object)this.customerID);
        final String code = jsonObject.optString("code");
        if (code != null) {
            params.put("Code", (Object)code);
        }
        final JSONObject bstore = MDMApiFactoryProvider.getBusinessStoreAccess().configureBusinessStore(params);
        if (!bstore.optBoolean("Error", (boolean)Boolean.FALSE)) {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72506, null, userName, "mdm.bstore.added", "", this.customerID);
            return (JSONObject)this.getStoreDetails(jsonObject);
        }
        return bstore;
    }
    
    @Override
    public Object getStoreDetails(final JSONObject jsonObject) throws Exception {
        JSONObject curBstore = null;
        final JSONObject response = new JSONObject();
        final JSONArray businessStore = new JSONArray();
        curBstore = this.getBusinessStoreDetails();
        if (curBstore.has("Error")) {
            response.put("configured_business_store", (Object)businessStore);
            return response;
        }
        WpAppSettingsHandler.getInstance().putBstoreData(curBstore, this.customerID);
        businessStore.put((Object)curBstore);
        response.put("configured_business_store", (Object)businessStore);
        return response;
    }
    
    @Override
    public JSONObject syncStore(final JSONObject jsonObject) throws Exception {
        WpAppSettingsHandler.getInstance().syncBStoreAppsToRepository(this.customerID);
        return null;
    }
    
    @Override
    public void clearSyncStoreStatus() throws Exception {
        WpAppSettingsHandler.getInstance().clearBstoreDetails(this.customerID);
    }
    
    @Override
    public void validateAppToBusinessStoreProps(final List profileList, final Properties pkgToBusinessStoreProps, final Properties appToBusinessProps) throws Exception {
    }
    
    @Override
    public Object getSyncStoreStatus(final JSONObject jsonObject) throws Exception {
        final JSONObject props = WpAppSettingsHandler.getInstance().getSyncStatus(this.customerID);
        int trashCount = 0;
        if (props.has("BstoreSyncStatus")) {
            String status = String.valueOf(props.get("BstoreSyncStatus"));
            status = status.toLowerCase();
            if (status == "error") {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerID, "Windows_Business_Store_Module", "Sync_without_activation");
            }
            if (status.contains("completed")) {
                trashCount = new AppTrashModeHandler().getAccountAppsInTrash(3, this.customerID);
                props.put("TrashCount", trashCount);
            }
        }
        final JSONObject bStoreDetails = new JSONObject();
        WpAppSettingsHandler.getInstance().putBstoreData(bStoreDetails, this.customerID);
        if (bStoreDetails.has("last_sync") && bStoreDetails.has("next_sync")) {
            final Long lastSyncTime = bStoreDetails.getLong("last_sync");
            final Long nextSyncTime = bStoreDetails.getLong("next_sync");
            final String lastSyncTimeInDateFormat = Utils.getTime(lastSyncTime);
            final String nextSyncTimeInDateFormat = Utils.getTime(nextSyncTime);
            bStoreDetails.put("last_sync", (Object)lastSyncTimeInDateFormat);
            bStoreDetails.put("next_sync", (Object)nextSyncTimeInDateFormat);
        }
        props.put("details", (Object)bStoreDetails);
        super.getSyncStoreStatus(props);
        return props;
    }
    
    @Override
    public JSONObject getAllStoreDetails() throws Exception {
        return null;
    }
    
    @Override
    public JSONObject removeStoreDetails(final JSONObject jsonObject) throws Exception {
        final String userName = APIUtil.getUserName(jsonObject);
        JSONObject responseJSON;
        try {
            responseJSON = WpAppSettingsHandler.getInstance().removeBstoreAccount(this.customerID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72507, null, userName, "mdm.bstore.removed", "", this.customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Issue on removing business store account", e);
            throw e;
        }
        return responseJSON;
    }
}
