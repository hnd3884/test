package com.me.mdm.server.apps.businessstore.service;

import org.json.JSONArray;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppAssociationHandler;
import java.util.Map;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.businessstore.model.BusinessStoreModel;
import com.me.mdm.server.apps.businessstore.model.ios.IOSBusinessStoreSyncModel;
import com.me.mdm.server.apps.businessstore.model.BusinessStoresSyncModel;
import java.util.Collection;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.apps.businessstore.model.android.EnterpriseAppToPlaystoreAppConversionModel;
import java.util.logging.Level;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.ManagedBusinessStoreHandler;
import com.me.mdm.api.model.BaseAPIModel;
import com.me.mdm.server.apps.businessstore.windows.WindowsStoreHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import com.me.mdm.server.apps.businessstore.StoreInterface;
import java.util.logging.Logger;

public class BusinessStoreService
{
    public Logger logger;
    public static final String CUSTOMER_ID = "customerID";
    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String BUSINESSSTORE_ID = "businessstore_id";
    
    public BusinessStoreService() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
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
    
    public Object getBusinessStoreAppsFailureDetails(final Long businessStoreID, final int platform, final BaseAPIModel baseAPIModel) {
        try {
            MDBusinessStoreUtil.validateIfStoreFound(businessStoreID, baseAPIModel.getCustomerId(), ManagedBusinessStoreHandler.getServiceTypeBasedOnPlatform(platform));
            final JSONObject apiParams = new JSONObject();
            apiParams.put("customerID", (Object)baseAPIModel.getCustomerId());
            apiParams.put("userID", (Object)baseAPIModel.getUserId());
            apiParams.put("userName", (Object)baseAPIModel.getUserName());
            apiParams.put("businessstore_id", (Object)businessStoreID);
            return new StoreFacade().getInstance(platform, baseAPIModel.getCustomerId(), businessStoreID).getAppsFailureDetails(apiParams);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "Exception in getBusinessStoreAppsFailureDetails()", ex);
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in getBusinessStoreAppsFailureDetails()", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void convertEnterpriseAppToPlaystoreApp(final Long businessStoreID, final int platform, final EnterpriseAppToPlaystoreAppConversionModel enterpriseAppToPlaystoreAppConversionModel) {
        try {
            MDBusinessStoreUtil.validateIfStoreFound(businessStoreID, enterpriseAppToPlaystoreAppConversionModel.getCustomerId(), ManagedBusinessStoreHandler.getServiceTypeBasedOnPlatform(platform));
            new AppFacade().validateIfAppsFound(enterpriseAppToPlaystoreAppConversionModel.getAppIds(), enterpriseAppToPlaystoreAppConversionModel.getCustomerId());
            final JSONObject apiParams = new JSONObject();
            apiParams.put("userID", (Object)enterpriseAppToPlaystoreAppConversionModel.getUserId());
            apiParams.put("userName", (Object)enterpriseAppToPlaystoreAppConversionModel.getUserName());
            new AndroidStoreHandler(businessStoreID, enterpriseAppToPlaystoreAppConversionModel.getCustomerId()).convertEnterpriseAppToPlaystoreApp(apiParams, enterpriseAppToPlaystoreAppConversionModel.getAppIds());
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "Exception in convertEnterpriseAppToPlaystoreApp()", ex);
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in convertEnterpriseAppToPlaystoreApp()", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void modifyStoreDetails(final Long businessStoreID, final int platform, final JSONObject jsonObject) {
        try {
            if (jsonObject.optJSONObject("msg_body") == null) {
                this.logger.log(Level.SEVERE, "Message Body not provided for modifyStoreDetails API");
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            new StoreFacade().getInstance(platform, jsonObject.getLong("CUSTOMER_ID"), businessStoreID).modifyStoreDetails(jsonObject);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public BusinessStoresSyncModel getSyncStatus(final Long businessStoreID, final Long customerID, final int platformType) {
        try {
            return (IOSBusinessStoreSyncModel)this.getInstance(platformType, customerID, businessStoreID).getSyncStoreStatus(new JSONObject());
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in getSyncStatus", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void syncStoreToMDM(final JSONObject jsonObject, final Long customerID, final Long businessStoreID, final int platformType) {
        try {
            this.getInstance(platformType, customerID, businessStoreID).syncStore(jsonObject);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in syncStoreToMDM", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public BusinessStoreModel getStoreDetails(final Long businessStoreID, final Long customerID, final int platformType) {
        try {
            return (BusinessStoreModel)this.getInstance(platformType, customerID, businessStoreID).getStoreDetails(new JSONObject());
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in getStoreDetails", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void syncLicense(final Long appID, final Long usedID, final Long businessStoreID, final Long customerID, final int platformType) {
        try {
            new AppFacade().validateIfAppFound(appID, customerID);
            final Long appGroupId = AppsUtil.getInstance().getAppGroupId(appID);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("APP_GROUP_ID", (Object)appGroupId);
            requestJSON.put("PACKAGE_ID", (Object)appID);
            requestJSON.put("USER_ID", (Object)usedID);
            this.getInstance(platformType, customerID, businessStoreID).syncLicense(requestJSON);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in syncLicense - StoreFacade", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteStoreDetails(final int platformType, final Long customerID, final long businessStoreID, final String userName) {
        try {
            this.getInstance(platformType, customerID, businessStoreID).removeStoreDetails(new JSONObject().put("userName", (Object)userName));
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in deleteStoreDetails", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Map validatePreAssociation(final Long targetBusinessStoreID, final JSONObject jsonObject, final Long customerID) {
        try {
            final JSONArray associationAppList = jsonObject.optJSONObject("msg_body").optJSONArray("app_ids");
            final JSONArray targetResourceList = jsonObject.optJSONObject("msg_body").optJSONArray("resource_ids");
            final JSONObject responseJSON = VPPAppAssociationHandler.getInstance().validatePreassociation(JSONUtil.convertJSONArrayToList(associationAppList), JSONUtil.convertJSONArrayToList(targetResourceList), targetBusinessStoreID);
            return JSONUtil.convertJSONtoMap(responseJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleteStoreDetails", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
