package com.me.mdm.server.apps.businessstore;

import org.json.JSONArray;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Level;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.tracker.mics.MICSStoreConfigurationFeatureController;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.windows.WindowsStoreHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import java.util.logging.Logger;

public class StoreFacade
{
    protected static Logger logger;
    
    public StoreInterface getInstance(final int platformType, final Long customerID, final Long businessStoreID) {
        StoreInterface storeInterface = null;
        if (platformType == 1) {
            storeInterface = new IOSStoreHandler(businessStoreID, customerID);
        }
        else if (platformType == 2) {
            storeInterface = new AndroidStoreHandler(businessStoreID, customerID);
        }
        else {
            if (platformType != 3) {
                throw new APIHTTPException("COM0014", new Object[] { "Invalid platform" });
            }
            storeInterface = new WindowsStoreHandler(businessStoreID, customerID);
        }
        return storeInterface;
    }
    
    public Object getStoreAppsFailureDetails(final JSONObject apiRequest, final int platformType, final Long businessStoreID) throws Exception {
        final JSONObject message = new JSONObject();
        message.put("user_id", (Object)APIUtil.getUserID(apiRequest));
        return this.getInstance(platformType, APIUtil.getCustomerID(apiRequest), businessStoreID).getAppsFailureDetails(message);
    }
    
    public JSONObject addStoreDetails(final JSONObject message, final int platformType) throws Exception {
        JSONObject response = null;
        try {
            final StoreInterface storeInterface = this.getInstance(platformType, APIUtil.getCustomerID(message), null);
            response = storeInterface.addStoreDetails(message);
            if (response.has("Error")) {
                final String errMessage = response.optString("Message");
                throw new APIHTTPException("APP0003", new Object[] { errMessage });
            }
            storeInterface.updateStoreSyncKey();
            MICSStoreConfigurationFeatureController.addTrackingData(platformType, MICSStoreConfigurationFeatureController.StoreConfigurationOperation.START);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        return response;
    }
    
    public Object getStoreDetails(final JSONObject message, final int platformType, final Long businessStoreID) throws Exception {
        if (platformType != 1) {
            return this.getInstance(platformType, APIUtil.getCustomerID(message), businessStoreID).getStoreDetails(message);
        }
        throw new APIHTTPException("COM0014", new Object[] { "Invalid platform" });
    }
    
    public JSONObject getAllStoreDetails(final JSONObject message, final int platformtype) throws Exception {
        return this.getInstance(platformtype, APIUtil.getCustomerID(message), null).getAllStoreDetails();
    }
    
    public JSONObject getStorePromoStatus(final JSONObject message) {
        final JSONObject responseJSON = new JSONObject();
        try {
            final int platform = APIUtil.getIntegerFilter(message, "platform");
            responseJSON.put("promo", false);
            final Long customerId = APIUtil.getCustomerID(message);
            if (new AppFacade().isNonAccountAppAvailable(platform, customerId)) {
                return this.getInstance(platform, APIUtil.getCustomerID(message), null).getStorePromoStatus(message);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                return responseJSON;
            }
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public JSONObject getAppAccountRemovalMsg(final JSONObject message) throws Exception {
        final int platformType = APIUtil.getIntegerFilter(message, "platform");
        final Long businessStoreID = APIUtil.getLongFilter(message, "businessstore_id");
        return this.getInstance(platformType, APIUtil.getCustomerID(message), businessStoreID).verifyAccountRemoval();
    }
    
    public String getStoreName(final int platform, final Long customerID, final Long businessStoreID) {
        try {
            return this.getInstance(platform, customerID, businessStoreID).getBusinessStoreName(businessStoreID);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            StoreFacade.logger.log(Level.SEVERE, "error in getStoreName - StoreFacade", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getLicenseSyncStatus(final Long appID, final Long customerID, final int platform, final Long businessStoreID) {
        try {
            new AppFacade().validateIfAppFound(appID, customerID);
            final Long appGroupId = AppsUtil.getInstance().getAppGroupId(appID);
            if (appGroupId == null) {
                throw new APIHTTPException("COM0024", new Object[] { appID });
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", (Object)appID);
            jsonObject.put("app_group_id", (Object)appGroupId);
            return this.getInstance(platform, customerID, businessStoreID).getLicenseSyncStatus(jsonObject);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            StoreFacade.logger.log(Level.SEVERE, "error in syncLicense - StoreFacade", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object syncStoreToMDM(final JSONObject message, final int platformType, final Long businessStoreID) throws Exception {
        return this.getInstance(platformType, APIUtil.getCustomerID(message), businessStoreID).syncStore(message);
    }
    
    public JSONObject syncAllStores(final JSONObject message, final int platformType) throws Exception {
        final int serviceType = MDBusinessStoreUtil.getServiceType(platformType);
        if (platformType != 0) {
            final List businessStoreIDList = MDBusinessStoreUtil.getBusinessStoreIDs(APIUtil.getCustomerID(message), serviceType);
            for (int i = 0; i < businessStoreIDList.size(); ++i) {
                final Long businessStoreID = businessStoreIDList.get(i);
                this.syncStoreToMDM(message, platformType, businessStoreID);
            }
        }
        return new JSONObject().put("message", (Object)"sync_initiated");
    }
    
    public Object getSyncStatus(final JSONObject message, final int platformType, final Long businessStoreID) throws Exception {
        if (platformType != 1) {
            return this.getInstance(platformType, APIUtil.getCustomerID(message), businessStoreID).getSyncStoreStatus(message);
        }
        throw new APIHTTPException("COM0014", new Object[] { "Invalid platform" });
    }
    
    public void clearSyncStoreStatus(final JSONObject apiRequest, final int platformType, final Long businessStoreID) throws Exception {
        this.getInstance(platformType, APIUtil.getCustomerID(apiRequest), businessStoreID).clearSyncStoreStatus();
    }
    
    public JSONObject getAllStoreSyncStatus(final JSONObject apiRequest, final int platformType) throws Exception {
        final JSONArray responseArray = this.getInstance(platformType, APIUtil.getCustomerID(apiRequest), null).getAllStoreSyncStatus();
        return new JSONObject().put("VPP_SYNC_DETAILS", (Object)responseArray);
    }
    
    public JSONObject deleteStoreDetails(final JSONObject message, final int platformType, final Long businessStoreID) throws Exception {
        final Long customerID = APIUtil.getCustomerID(message);
        final String userName = APIUtil.getUserName(message);
        message.put("userName", (Object)userName);
        return this.getInstance(platformType, customerID, businessStoreID).removeStoreDetails(message);
    }
    
    public void deleteAllStoreDetails(final JSONObject message, final int platformType) throws Exception {
        final Long customerID = APIUtil.getCustomerID(message);
        final List businessStoreIDList = MDBusinessStoreUtil.getBusinessStoreIDs(customerID, MDBusinessStoreUtil.getServiceType(platformType));
        for (int i = 0; i < businessStoreIDList.size(); ++i) {
            this.deleteStoreDetails(message, platformType, businessStoreIDList.get(i));
        }
    }
    
    public void modifyStoreDetails(final JSONObject apiRequest, final int platformType, final Long businessStoreID) throws Exception {
        final Long userID = APIUtil.getUserID(apiRequest);
        final String userName = APIUtil.getUserName(apiRequest);
        final JSONObject msgBody = apiRequest.optJSONObject("msg_body");
        if (msgBody == null) {
            StoreFacade.logger.log(Level.SEVERE, "Message Body not provided for modifyStoreDetails API");
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        if (userID != -1L) {
            apiRequest.put("userID", (Object)userID);
            apiRequest.put("userName", (Object)userName);
        }
        this.getInstance(platformType, APIUtil.getCustomerID(apiRequest), businessStoreID).modifyStoreDetails(apiRequest);
    }
    
    static {
        StoreFacade.logger = Logger.getLogger("MDMConfigLogger");
    }
}
