package com.me.mdm.server.apps.config;

import java.util.Collection;
import org.json.JSONArray;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.device.DeviceFacade;
import java.util.Set;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import java.util.HashSet;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.profiles.ProfileFacade;

public class AppConfigFacade extends ProfileFacade
{
    private static AppConfigFacade appConfigFacade;
    
    public static AppConfigFacade getInstance() {
        if (AppConfigFacade.appConfigFacade == null) {
            AppConfigFacade.appConfigFacade = new AppConfigFacade();
        }
        return AppConfigFacade.appConfigFacade;
    }
    
    public int getProfileType() {
        return 10;
    }
    
    @Override
    public JSONObject addPayload(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(apiRequest, "profile_id");
            final Long collectionID = APIUtil.getResourceID(apiRequest, "collection_id");
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            JSONObject requestJSON;
            try {
                requestJSON = apiRequest.getJSONObject("msg_body");
            }
            catch (final Exception ex) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long appGroupID = Long.parseLong(requestJSON.get("APP_GROUP_ID".toLowerCase()).toString());
            AppConfigPolicyDBHandler.getInstance().validateAppPayloadNotAlreadyExists(collectionID, appGroupID);
            final Long productionAppID = AppConfigPolicyDBHandler.getInstance().getProductionAppIDFromAppGroupID(appGroupID, customerID);
            final String identifier = AppsUtil.getInstance().getAppIdentifier(productionAppID);
            if (identifier.contains("com.manageengine.mdm.iosagent")) {
                throw new APIHTTPException("COM0014", new Object[] { "Configuration cannot be added/updated for MDM app catalog" });
            }
            requestJSON.put("APP_GROUP_ID", (Object)appGroupID);
            requestJSON.put("APP_ID", (Object)productionAppID);
            apiRequest.put("msg_body", (Object)requestJSON);
            final JSONObject responseJSON = super.addPayload(apiRequest);
            return AppConfigDataPolicyHandler.getInstance(ProfileUtil.getInstance().getPlatformType(profileID)).addAppConfiguration(responseJSON);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in addPayload", ex2);
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject getPayload(final JSONObject message) {
        return message;
    }
    
    public JSONObject getPayloadItem(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(apiRequest, "profile_id");
            final int platformType = ProfileUtil.getInstance().getPlatformType(profileID);
            return AppConfigDataPolicyHandler.getInstance(platformType).getAppConfiguration(apiRequest);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPayloadItem", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject modifyPayload(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(apiRequest, "profile_id");
            final Long configDataItemID = APIUtil.getResourceID(apiRequest, "payloaditem_id");
            JSONObject requestJSON;
            try {
                requestJSON = apiRequest.getJSONObject("msg_body");
            }
            catch (final Exception ex) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject appDetailsJSON = AppConfigPolicyDBHandler.getInstance().getPayloadDetailsJSONFromPayloadID(configDataItemID);
            requestJSON.put("APP_GROUP_ID", appDetailsJSON.get("APP_GROUP_ID"));
            requestJSON.put("APP_ID", appDetailsJSON.get("APP_ID"));
            apiRequest.put("msg_body", (Object)requestJSON);
            final JSONObject responseJSON = super.modifyPayload(apiRequest);
            return AppConfigDataPolicyHandler.getInstance(ProfileUtil.getInstance().getPlatformType(profileID)).modifyAppConfiguration(responseJSON);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in modify payload", ex2);
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void associateProfilesToDevices(final JSONObject message) throws APIHTTPException {
        this.validateAppConfigProfileForDistribution(message);
        super.associateProfilesToDevices(message);
    }
    
    @Override
    public void associateProfilesToGroups(final JSONObject messgae) throws APIHTTPException {
        this.validateAppConfigProfileForDistribution(messgae);
        super.associateProfilesToGroups(messgae);
    }
    
    private void validateAppConfigProfileForDistribution(final JSONObject message) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            List<Long> profileList;
            if (profileID > 0L) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids"));
            }
            if (profileList.isEmpty()) {
                throw new APIHTTPException("ENR00105", new Object[0]);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
            selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            selectQuery.setCriteria(new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)profileList.toArray(), 8));
            selectQuery.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("ManagedAppConfigurationPolicy");
                final Set set = new HashSet();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long appGroupID = (Long)row.get("APP_GROUP_ID");
                    if (set.contains(appGroupID)) {
                        throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.oem.multiple_profile_same_vendor", new Object[0]) });
                    }
                    set.add(appGroupID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in validating app config profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAppConfigurationTemplate(final JSONObject apiRequest) {
        try {
            final Long appGroupID = APIUtil.getResourceID(apiRequest, "app_id");
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            final Long productionAppID = AppConfigPolicyDBHandler.getInstance().getProductionAppIDFromAppGroupID(appGroupID, customerID);
            final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
            final JSONObject configTemplate = new JSONObject(appConfigDataHandler.getAppConfigTemplate(productionAppID));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("APP_CONFIG_FORM", (Object)configTemplate);
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in app configuration template", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAppConfigFeedBacksForDevice(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long resourceID = APIUtil.getResourceID(apiRequest, "device_id");
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            new DeviceFacade().validateIfDeviceExists(resourceID, customerID);
            return AppConfigPolicyDBHandler.getInstance().getAppConfigFeedbackForDevice(apiRequest);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting feedback for specific device", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getConfiguredVendors(final JSONObject apiRequest) {
        try {
            return JSONUtil.toJSON("vendors", AppConfigPolicyDBHandler.getInstance().getConfiguredVendors(apiRequest));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting vendors", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void validateAppGroupID(final Long appGroupID, final Long customerID) throws DataAccessException {
        final Criteria appCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        final DataObject dataObject = DataAccess.get("MdAppGroupDetails", appCriteria.and(customerCriteria));
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "Unknown app ID" });
        }
    }
    
    public JSONObject getSpecificAppConfigForDevice(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long resourceID = APIUtil.getResourceID(apiRequest, "device_id");
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            final Long appID = APIUtil.getResourceID(apiRequest, "app_id");
            new DeviceFacade().validateIfDeviceExists(resourceID, customerID);
            this.validateAppGroupID(appID, customerID);
            return JSONUtil.toJSON("config_feedbacks", AppConfigPolicyDBHandler.getInstance().getAppConfigFeedbackOfSpecificApp(resourceID, appID));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting feedback for specific app", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getOEMApps(final JSONObject apiRequest) {
        try {
            final String searchParam = APIUtil.getStringFilter(apiRequest, "search");
            final List OEMApps = AppConfigPolicyDBHandler.getInstance().getOEMApps(Boolean.FALSE, searchParam == null);
            if (searchParam != null) {
                for (int i = 0; i < OEMApps.size(); ++i) {
                    final HashMap temp = OEMApps.get(i);
                    final String appName = temp.get("app_name");
                    if (!appName.toLowerCase().startsWith(searchParam.toLowerCase())) {
                        OEMApps.remove(temp);
                        --i;
                    }
                }
            }
            return JSONUtil.toJSON("oem_apps", new JSONArray((Collection)OEMApps));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting oem apps", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getConfiguredAppsUnderPolicies(final JSONObject apiRequest) throws APIHTTPException {
        try {
            JSONUtil.getInstance();
            return JSONUtil.toJSON("app_ids", AppConfigPolicyDBHandler.getInstance().getConfiguredAppsUnderPolicies(apiRequest));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getConfiguredAppsUnderPolicies", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject verifyOEMAppIfPresent(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final String identifier = APIUtil.getStringFilter(apiRequest, "identifier");
            return AppConfigPolicyDBHandler.getInstance().verifyOEMAppIfPresent(identifier, APIUtil.getCustomerID(apiRequest));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in verifyOEMAppIfPresent", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        AppConfigFacade.appConfigFacade = null;
    }
}
