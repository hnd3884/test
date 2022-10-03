package com.me.mdm.server.profiles;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.DerivedTable;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.role.RBDAUtil;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.adventnet.ds.query.GroupByClause;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Locale;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.server.user.ManagedUserFacade;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Set;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import java.util.Map;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.i18n.I18N;
import java.util.Collection;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.List;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import org.apache.commons.lang3.RandomStringUtils;
import com.me.mdm.server.config.MDMConfigUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationListeners;
import com.me.mdm.server.msp.sync.SyncConfigurationsUtil;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import com.me.mdm.server.profiles.config.ConfigHandler;
import com.me.mdm.server.util.MDMTransactionManager;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.core.management.ManagementConstants;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.HashMap;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashSet;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProfileFacade
{
    protected Logger logger;
    public static final String PAYLOADID = "payload_id";
    
    public ProfileFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getProfile(final JSONObject message) throws APIHTTPException {
        Connection conn = null;
        DataSet ds = null;
        try {
            final Long profileId = APIUtil.getResourceID(message, "profile_id");
            this.validateIfProfileExists(profileId, APIUtil.getCustomerID(message));
            final HashSet set = new HashSet();
            set.add(profileId);
            final SelectQuery profileQuery = this.getProfileQuery();
            this.setProfileQueryCriteria(profileQuery, new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
            this.addSelectColumns(profileQuery);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
            }
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)profileQuery, conn);
            JSONObject profileDetails = null;
            if (ds.next()) {
                profileDetails = APIUtil.getNewInstance().getJSONObjectFromDS(ds, profileQuery);
                final int profileSharedScope = profileDetails.optInt("profile_shared_scope", 0);
                profileDetails.put("is_for_all_customers", profileSharedScope == 1);
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    profileDetails.put("is_user_in_role", DMUserHandler.isUserInAdminRole(loginID));
                }
                else {
                    profileDetails.put("is_user_in_role", true);
                }
            }
            if (profileDetails == null) {
                throw new APIHTTPException("COM0008", new Object[] { "profile_id - " + profileId });
            }
            final Long collectionID = JSONUtil.optLongForUVH(profileDetails, "collection_id", Long.valueOf(-1L));
            final JSONArray payloads = this.getPayloadNameArray(collectionID);
            profileDetails.put("payloads", (Object)payloads);
            final HashMap deviceCounts = this.getProfileDeviceAssociatedCount(message);
            profileDetails.put("profile_associated_devices_count", deviceCounts.get("total"));
            profileDetails.put("profile_to_update_devices_count", deviceCounts.get("toUpdate"));
            profileDetails.put("profile_associated_groups_count", this.getProfileGroupAssociatedCount(message));
            final String include = APIUtil.optStringFilter(message, "include", "");
            if (!"".equals(include) && include.equals("payloaditems")) {
                this.getDetailedProfile(profileDetails, APIUtil.getCustomerID(message));
            }
            return profileDetails;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in getProfile()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            APIUtil.closeConnection(conn, ds);
        }
    }
    
    public JSONObject createProfile(final JSONObject message) throws APIHTTPException, PayloadException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject json = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            final String profileName = String.valueOf(requestJSON.get("profile_name"));
            if (profileName.length() > 100) {
                throw new APIHTTPException("COM0015", new Object[] { "Profile Name cannot exceed 100 characters" });
            }
            final Integer platformType = requestJSON.getInt("platform_type");
            secLog.put((Object)"PLATFORM_TYPE", (Object)platformType);
            json.put("PROFILE_DESCRIPTION", (Object)requestJSON.optString("profile_description", ""));
            final Boolean isForAllCustomers = requestJSON.optBoolean("is_for_all_customers");
            if (new ProfileHandler().checkProfileNameExist(customerId, String.valueOf(requestJSON.get("profile_name")), requestJSON.optInt("profile_type", this.getProfileType()), null, isForAllCustomers)) {
                throw new APIHTTPException("COM0010", new Object[] { "profile_name - " + requestJSON.get("profile_name") });
            }
            final int scope = requestJSON.optInt("scope", 0);
            secLog.put((Object)"SCOPE", (Object)scope);
            json.put("PROFILE_NAME", (Object)profileName);
            json.put("PROFILE_TYPE", requestJSON.optInt("profile_type", this.getProfileType()));
            json.put("PLATFORM_TYPE", (Object)platformType);
            json.put("SCOPE", scope);
            json.put("CUSTOMER_ID", (Object)customerId);
            json.put("CREATED_BY", (Object)userId);
            json.put("LAST_MODIFIED_BY", (Object)userId);
            json.put("PROFILE_SHARED_SCOPE", (int)(((boolean)isForAllCustomers) ? 1 : 0));
            json.put("SECURITY_TYPE", -1);
            json.put("management_type", requestJSON.optInt("management_type", (int)ManagementConstants.Types.MOBILE_MGMT));
            if (platformType == 1 || platformType == 6 || platformType == 7) {
                json.put("SECURITY_TYPE", requestJSON.optInt("security_type", (int)MDMProfileConstants.PROFILE_SECURITY_TYPE_REMOVE_RESTICTED));
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget")) {
                if (requestJSON.has("profile_payload_identifier")) {
                    json.put("PROFILE_PAYLOAD_IDENTIFIER", requestJSON.get("profile_payload_identifier"));
                }
                if (requestJSON.has("creation_time")) {
                    json.put("CREATION_TIME", requestJSON.getLong("creation_time"));
                }
                if (requestJSON.has("last_modified_time")) {
                    json.put("LAST_MODIFIED_TIME", requestJSON.getLong("last_modified_time"));
                }
            }
            json.put("SCOPE", requestJSON.optInt("scope", 0));
            MDMUtil.getUserTransaction().begin();
            ProfileConfigHandler.addProfileCollection(json);
            MDMMessageHandler.getInstance().messageAction("NO_PROFILE_ADDED", APIUtil.getCustomerID(message));
            final Long collectionID = JSONUtil.optLongForUVH(json, "COLLECTION_ID", Long.valueOf(-1L));
            if (requestJSON.has("payloads")) {
                final JSONObject payloads = requestJSON.getJSONObject("payloads");
                final Iterator payloadIds = payloads.keys();
                while (payloadIds.hasNext()) {
                    final String payloadName = String.valueOf(payloadIds.next());
                    final JSONArray configArray = payloads.getJSONArray(payloadName);
                    for (int i = 0; i < configArray.length(); ++i) {
                        final JSONObject payloadJSON = configArray.getJSONObject(i);
                        payloadJSON.put("payload_name", (Object)payloadName);
                        payloadJSON.put("PLATFORM_TYPE", (Object)platformType);
                        payloadJSON.put("PROFILE_COLLECTION_STATUS", 0);
                        this.addOrModifyConfigDataItem(payloadJSON, collectionID, customerId, userId);
                    }
                }
            }
            MDMUtil.getUserTransaction().commit();
            final JSONObject idJSON = new JSONObject();
            final Long createdProfileID = JSONUtil.optLongForUVH(json, "PROFILE_ID", (Long)null);
            idJSON.put("profile_id", (Object)createdProfileID);
            final JSONObject headerJSON = new JSONObject();
            headerJSON.put("filters", (Object)message.getJSONObject("msg_header").getJSONObject("filters"));
            headerJSON.put("resource_identifier", (Object)idJSON);
            final JSONObject messageJSON = new JSONObject();
            final JSONObject filterJson = new JSONObject();
            filterJson.put("customer_id", (Object)customerId);
            headerJSON.put("filters", (Object)filterJson);
            messageJSON.put("msg_header", (Object)headerJSON);
            secLog.put((Object)"PROFILE_ID", (Object)createdProfileID);
            remarks = "create-success";
            return this.getProfile(messageJSON);
        }
        catch (final PayloadException ex) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "transaction rollback failed....", e);
            }
            throw ex;
        }
        catch (final Exception e2) {
            try {
                SyMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, "transaction rollback failed....", ex2);
            }
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            this.logger.log(Level.SEVERE, "error in createProfile()...", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "CREATE_PROFILE", secLog);
        }
    }
    
    public JSONObject modifyCollection(final JSONObject message) throws APIHTTPException, PayloadException {
        try {
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            final Integer platformType = requestJSON.getInt("platform_type");
            final Long collectionID = requestJSON.getLong("COLLECTION_ID".toLowerCase());
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionID);
            final Row profileRow = configDetails.getRow("Profile");
            final Row collectionStatusRow = configDetails.getRow("CollectionStatus");
            final JSONObject profileDetails = ProfileConfigHandler.getProfileDetailsForCloning(collectionID, customerId);
            final Long clonedCollectionID = ProfileHandler.addOrUpdateProfileCollectionDO(profileDetails);
            MDMUtil.getUserTransaction().begin();
            MDMMessageHandler.getInstance().messageAction("NO_PROFILE_ADDED", APIUtil.getCustomerID(message));
            if (requestJSON.has("payloads")) {
                final JSONObject payloads = requestJSON.getJSONObject("payloads");
                final Iterator payloadIds = payloads.keys();
                while (payloadIds.hasNext()) {
                    final String payloadName = String.valueOf(payloadIds.next());
                    final JSONArray configArray = payloads.getJSONArray(payloadName);
                    for (int i = 0; i < configArray.length(); ++i) {
                        final JSONObject payloadJSON = configArray.getJSONObject(i);
                        payloadJSON.put("payload_name", (Object)payloadName);
                        payloadJSON.put("PLATFORM_TYPE", (Object)platformType);
                        payloadJSON.put("PROFILE_COLLECTION_STATUS", 0);
                        this.addOrModifyConfigDataItem(payloadJSON, clonedCollectionID, customerId, userId);
                    }
                }
            }
            MDMUtil.getUserTransaction().commit();
            final JSONObject idJSON = new JSONObject();
            idJSON.put("PROFILE_ID".toLowerCase(), profileRow.get("PROFILE_ID"));
            final JSONObject headerJSON = new JSONObject();
            headerJSON.put("filters", (Object)message.getJSONObject("msg_header").getJSONObject("filters"));
            headerJSON.put("resource_identifier", (Object)idJSON);
            final JSONObject messageJSON = new JSONObject();
            final JSONObject filterJson = new JSONObject();
            filterJson.put("customer_id", (Object)customerId);
            headerJSON.put("filters", (Object)filterJson);
            messageJSON.put("msg_header", (Object)headerJSON);
            return this.getProfile(messageJSON);
        }
        catch (final PayloadException ex) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "transaction rollback failed....", e);
            }
            throw ex;
        }
        catch (final Exception e2) {
            try {
                SyMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, "transaction rollback failed....", ex2);
            }
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            this.logger.log(Level.SEVERE, "error in createProfile()...", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrModifyConfigDataItem(JSONObject payloadJSON, Long collectionID, final Long customerID, final Long userID) throws Exception {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            this.logger.log(Level.SEVERE, "entered addConfigDataItem");
            final String payloadName = String.valueOf(payloadJSON.get("payload_name"));
            payloadJSON.put("CUSTOMER_ID", (Object)customerID);
            payloadJSON.put("LAST_MODIFIED_BY", (Object)userID);
            final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
            payloadJSON = configHandler.apiJSONToServerJSON(payloadName, payloadJSON);
            payloadJSON.put("COLLECTION_ID", (Object)collectionID);
            payloadJSON.put("CUSTOMER_ID", (Object)customerID);
            payloadJSON.put("PLATFORM_TYPE", payloadJSON.getInt("PLATFORM_TYPE"));
            configHandler.validateServerJSON(payloadJSON);
            int currentCollectionStatus = 0;
            if (collectionID != -1L) {
                currentCollectionStatus = payloadJSON.getInt("PROFILE_COLLECTION_STATUS");
            }
            else {
                collectionID = 0L;
            }
            final String configName = (String)payloadJSON.get("CONFIG_NAME");
            final JSONObject individualPayloadJSON = new JSONObject();
            individualPayloadJSON.put("CONFIG_NAME", (Object)configName);
            individualPayloadJSON.put("CURRENT_CONFIG", (Object)configName);
            individualPayloadJSON.put("COLLECTION_ID", (Object)collectionID);
            individualPayloadJSON.put(configName, (Object)payloadJSON);
            individualPayloadJSON.put("APP_CONFIG", false);
            individualPayloadJSON.put("CUSTOMER_ID", (Object)customerID);
            if (payloadJSON.has("CONFIG_DATA_ITEM_ID")) {
                individualPayloadJSON.put("CONFIG_DATA_ITEM_ID", payloadJSON.get("CONFIG_DATA_ITEM_ID"));
            }
            mdmTransactionManager.begin();
            if (currentCollectionStatus == 110) {
                collectionID = ProfileConfigHandler.cloneConfigurationsOnModification(individualPayloadJSON);
                individualPayloadJSON.put("COLLECTION_ID", (Object)collectionID);
            }
            ProfileConfigHandler.addOrModifyConfiguration(individualPayloadJSON);
            mdmTransactionManager.commit();
            this.logger.log(Level.SEVERE, "completed addConfigDataItem");
            return individualPayloadJSON;
        }
        catch (final Exception e) {
            try {
                mdmTransactionManager.rollBack();
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "transaction rollback failed....", ex);
            }
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            if (e instanceof PayloadException) {
                throw e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in addConfigDataItem", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void publishProfile(final JSONObject message) throws Exception {
        final Long userId = APIUtil.getUserID(message);
        final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
        final Long customerId = APIUtil.getCustomerID(message);
        final String sUserName = DMUserHandler.getUserName(loginId);
        String remarksArgs = null;
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "publish-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final String profileName = remarksArgs = (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)profileID, "PROFILE_NAME");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CUSTOMER_ID", (Object)customerId);
            final Long collectionID = ProfileHandler.getRecentProfileCollectionID(profileID);
            if (!new ProfileHandler().checkIfEmptyProfile(collectionID)) {
                throw new APIHTTPException("PRO0001", new Object[0]);
            }
            final int currentCollectionStatus = (int)DBUtil.getValueFromDB("CollectionStatus", "COLLECTION_ID", (Object)collectionID, "PROFILE_COLLECTION_STATUS");
            jsonObject.put("COLLECTION_ID", (Object)collectionID);
            jsonObject.put("PROFILE_ID", (Object)profileID);
            jsonObject.put("PLATFORM_TYPE", new ProfileUtil().getPlatformType(profileID));
            jsonObject.put("APP_CONFIG", false);
            jsonObject.put("LAST_MODIFIED_BY", (Object)DMUserHandler.getDCUserID(loginId));
            jsonObject.put("PROFILE_TYPE", this.getProfileType());
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && message.has("msg_body")) {
                final JSONObject messageBody = message.getJSONObject("msg_body");
                if (messageBody.has("creation_time")) {
                    jsonObject.put("CREATION_TIME", messageBody.getLong("creation_time"));
                }
                if (messageBody.has("last_modified_time")) {
                    jsonObject.put("LAST_MODIFIED_TIME", messageBody.getLong("last_modified_time"));
                }
            }
            jsonObject.put("LOGIN_ID", (Object)loginId);
            SyncConfigurationsUtil.updateIsMoveToAllApplicable(profileID);
            ProfileConfigHandler.publishProfile(jsonObject);
            SyncConfigurationListeners.invokeListeners(jsonObject, 100);
            final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.publish_success";
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
            this.setUpdateCountMessageStatus(customerId);
            remarks = "publish-success";
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.create_failure";
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
            this.logger.log(Level.SEVERE, "Exception in publishing profile", ex);
            throw ex;
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "PUBLISH_PROFILE", secLog);
        }
    }
    
    public JSONObject cloneProfile(final JSONObject message) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final String sUserName = DMUserHandler.getUserName(loginId);
            final Long profileId = APIUtil.getResourceID(message, "profile_id");
            secLog.put((Object)"COPY_PROFILE_FROM", (Object)profileId);
            final JSONObject profileDetails = this.validateAndGetIfProfileExists(profileId, APIUtil.getCustomerID(message));
            final int currentCollectionStatus = profileDetails.getInt("PROFILE_COLLECTION_STATUS");
            if (currentCollectionStatus != 110) {
                throw new APIHTTPException("COM0015", new Object[] { "Cannot clone unpublished profile." });
            }
            final Long collectionID = profileDetails.getLong("COLLECTION_ID");
            final String profileName = String.valueOf(profileDetails.get("PROFILE_NAME"));
            final String profileDescription = String.valueOf(profileDetails.get("PROFILE_DESCRIPTION"));
            final Integer securityType = profileDetails.getInt("SECURITY_TYPE");
            final Integer platformType = profileDetails.getInt("PLATFORM_TYPE");
            final Integer profileScope = profileDetails.getInt("SCOPE");
            final String sEventLogRemarks = "mdm.actionlog.profilemgmt.clone_success";
            final JSONObject json = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            json.put("PROFILE_DESCRIPTION", (Object)profileDescription);
            final String clonnedProfileName = profileName + "_copy";
            json.put("PROFILE_NAME", (Object)clonnedProfileName);
            json.put("PROFILE_TYPE", this.getProfileType());
            json.put("PLATFORM_TYPE", (Object)platformType);
            json.put("SCOPE", (Object)profileScope);
            json.put("CUSTOMER_ID", (Object)customerId);
            json.put("CREATED_BY", (Object)userId);
            json.put("LAST_MODIFIED_BY", (Object)userId);
            json.put("SECURITY_TYPE", (Object)securityType);
            ProfileConfigHandler.addProfileCollection(json);
            final Long clonedCollectionID = JSONUtil.optLongForUVH(json, "COLLECTION_ID", Long.valueOf(-1L));
            final DataObject clonnedConfigData = ProfileConfigHandler.cloneConfigurations(collectionID, clonedCollectionID);
            final Iterator<Row> clonnedConfigDataItemExtnRows = clonnedConfigData.getRows("MdConfigDataItemExtn");
            final Row profileRow = clonnedConfigData.getRow("Profile");
            final String profileDataIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
            while (clonnedConfigDataItemExtnRows.hasNext()) {
                final Row configDataItemExtnRow = clonnedConfigDataItemExtnRows.next();
                final Long configDataItemId = (Long)configDataItemExtnRow.get("CONFIG_DATA_ITEM_ID");
                final Row configDataRow = clonnedConfigData.getRow("ConfigData", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0), new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                final Integer configID = (Integer)configDataRow.get("CONFIG_ID");
                final String configName = MDMConfigUtil.getConfigLabel(configID);
                final String payloadName = ProfileConfigurationUtil.getInstance().getPayloadName(configName);
                final String configDataIdentifier = ProfileConfigurationUtil.getInstance().getConfigDataIdentifier(payloadName);
                final String payloadIdentifier = profileDataIdentifier + "." + configDataIdentifier + "." + RandomStringUtils.randomAlphanumeric(3);
                configDataItemExtnRow.set("CONFIG_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
                clonnedConfigData.updateRow(configDataItemExtnRow);
            }
            MDMUtil.getPersistence().update(clonnedConfigData);
            final String remarksArgs = profileName + "@@@" + clonnedProfileName;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Profile_Module", "Profile_Cloning_Count");
            final Long createdProfileID = JSONUtil.optLongForUVH(json, "PROFILE_ID", (Long)null);
            message.getJSONObject("msg_header").getJSONObject("resource_identifier").put("profile_id", (Object)createdProfileID);
            secLog.put((Object)"CREATED_PROFILE_ID", (Object)createdProfileID);
            remarks = "create-success";
            return this.getProfile(message);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "CREATE_PROFILE", secLog);
        }
    }
    
    public void deletePayload(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long configDataItemID = APIUtil.getResourceID(message, "payloaditem_id");
            final Long customerId = APIUtil.getCustomerID(message);
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            secLog.put((Object)"PAYLOAD_NAME", (Object)payloadName);
            secLog.put((Object)"PAYLOAD_ITEM_ID", (Object)configDataItemID);
            this.validateIfProfileExists(profileID, customerId);
            final Integer configID = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (configID == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionID);
            final Criteria configDataItemCriteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configID, 0);
            final Criteria joinCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), 0);
            final Row configDataRow = configDetails.getRow("ConfigData", configDataItemCriteria, new Join("ConfigData", "ConfigDataItem", joinCriteria.and(configIdCriteria), 2));
            if (configDataRow == null) {
                throw new APIHTTPException("COM0008", new Object[] { " payloaditem_id - " + configDataItemID });
            }
            final Row collectionStatusRow = configDetails.getRow("CollectionStatus");
            final Long configDataId = (Long)configDataRow.get("CONFIG_DATA_ID");
            final JSONObject individualPayloadJSON = new JSONObject();
            individualPayloadJSON.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
            individualPayloadJSON.put("CONFIG_DATA_ID", (Object)configDataId);
            final int currentCollectionStatus = (int)collectionStatusRow.get("PROFILE_COLLECTION_STATUS");
            if (currentCollectionStatus == 110) {
                final String configLabel = (String)configDataRow.get("LABEL");
                individualPayloadJSON.put("CURRENT_CONFIG", (Object)configLabel);
                individualPayloadJSON.put("CONFIG_NAME", (Object)configLabel);
                individualPayloadJSON.put("CONFIG_ID", configDataRow.get("CONFIG_ID"));
                individualPayloadJSON.put("COLLECTION_ID", (Object)collectionID);
                individualPayloadJSON.put("APP_CONFIG", false);
                final JSONObject tempJSON = new JSONObject();
                tempJSON.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
                individualPayloadJSON.put(configLabel, (Object)tempJSON);
                individualPayloadJSON.put("CUSTOMER_ID", (Object)customerId);
                collectionID = ProfileConfigHandler.cloneConfigurationsOnModification(individualPayloadJSON);
            }
            final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
            configHandler.deletePayloadItems(collectionID, individualPayloadJSON, customerId);
            remarks = "delete-success";
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in deletePayload", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_PAYLOAD_ITEM", secLog);
        }
    }
    
    public void deletePayloads(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long customerId = APIUtil.getCustomerID(message);
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            secLog.put((Object)"PAYLOAD_NAME", (Object)payloadName);
            this.deletePayloads(profileID, collectionID, customerId, payloadName);
            remarks = "delete-success";
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in deletePayload", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_PAYLOAD", secLog);
        }
    }
    
    private void deletePayloads(final Long profileID, Long collectionID, final Long customerId, final String payloadName) {
        try {
            this.validateIfProfileExists(profileID, customerId);
            final Integer configId = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (configId == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            final List configDetails = ProfileConfigHandler.getConfigDataIds(collectionID, configId);
            if (configDetails.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload Items not found for Payload Key " + payloadName });
            }
            final int currentCollectionStatus = (int)DBUtil.getValueFromDB("CollectionStatus", "COLLECTION_ID", (Object)collectionID, "PROFILE_COLLECTION_STATUS");
            Long configDataId = configDetails.get(0);
            if (currentCollectionStatus == 110) {
                final String configName = ProfileConfigurationUtil.getInstance().getConfigurationName(payloadName);
                final JSONObject individualPayloadJSON = new JSONObject();
                individualPayloadJSON.put("CURRENT_CONFIG", (Object)configName);
                individualPayloadJSON.put("CONFIG_ID", (Object)configId);
                individualPayloadJSON.put("COLLECTION_ID", (Object)collectionID);
                individualPayloadJSON.put("APP_CONFIG", false);
                final JSONObject tempJSON = new JSONObject();
                individualPayloadJSON.put(configName, (Object)tempJSON);
                individualPayloadJSON.put("CUSTOMER_ID", (Object)customerId);
                collectionID = ProfileConfigHandler.cloneConfigurationsOnModification(individualPayloadJSON);
                configDataId = individualPayloadJSON.getLong("CONFIG_DATA_ID");
            }
            final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
            configHandler.deletePayloads(configDataId);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in deletePayload", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addPayload(final JSONObject message) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userID = APIUtil.getUserID(message);
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            secLog.put((Object)"PAYLOAD_NAME", (Object)payloadName);
            final Integer configId = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (this.isProfessionalPayload(configId) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowProfFeature") && !LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition()) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            final JSONObject msgBody = message.getJSONObject("msg_body");
            final JSONArray payloadArray = new JSONArray();
            payloadArray.put((Object)msgBody);
            final HashMap<String, Object> payloadItems = this.addPayloadItems(profileID, collectionID, customerId, userID, payloadName, payloadArray, true);
            final List<Long> configDataItemIds = payloadItems.get("CONFIG_DATA_ITEM_ID");
            final Long configDataItemId = configDataItemIds.get(0);
            collectionID = payloadItems.get("COLLECTION_ID");
            final JSONObject resourceJSON = message.getJSONObject("msg_header").getJSONObject("resource_identifier");
            resourceJSON.put("payloaditem_id", (Object)configDataItemId);
            resourceJSON.put("collection_id", (Object)collectionID);
            final JSONObject messageHeader = message.getJSONObject("msg_header");
            messageHeader.put("resource_identifier", (Object)resourceJSON);
            message.put("msg_header", (Object)messageHeader);
            remarks = "create-success";
            return this.getPayload(message);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_PAYLOAD", secLog);
        }
    }
    
    private boolean isProfessionalPayload(final Integer configId) {
        return configId == 612;
    }
    
    public JSONObject modifyPayload(final JSONObject message) throws APIHTTPException, PayloadException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            this.logger.log(Level.INFO, "entered modifyPayload");
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            Long configDataItemID = APIUtil.getResourceID(message, "payloaditem_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userID = APIUtil.getUserID(message);
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            secLog.put((Object)"PAYLOAD_NAME", (Object)payloadName);
            secLog.put((Object)"PAYLOAD_ITEM_ID", (Object)configDataItemID);
            final JSONObject msgBody = message.getJSONObject("msg_body");
            msgBody.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
            final JSONArray payloadArray = new JSONArray();
            payloadArray.put((Object)msgBody);
            final HashMap<String, Object> payloadObject = this.modifyPayloadItems(profileID, collectionID, customerId, userID, payloadName, payloadArray, true);
            collectionID = payloadObject.get("COLLECTION_ID");
            final List<Long> configDataItemIds = payloadObject.get("CONFIG_DATA_ITEM_ID");
            configDataItemID = configDataItemIds.get(0);
            APIUtil.addResourceID(message, "collection_id", collectionID);
            APIUtil.addResourceID(message, "payloaditem_id", configDataItemID);
            this.logger.log(Level.INFO, "finished modifyPayload");
            remarks = "update-success";
            return this.getPayload(message);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            if (e instanceof PayloadException) {
                throw (PayloadException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in modifyPayload", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "MODIFY_PAYLOAD", secLog);
        }
    }
    
    public void deleteOrTrashProfile(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            HashSet<Long> profileSet = null;
            if (profileID != -1L) {
                profileSet = new HashSet<Long>(Arrays.asList(profileID));
            }
            else {
                profileSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids")));
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileSet);
            this.validateIfProfilesExists(profileSet, APIUtil.getCustomerID(message));
            final HashSet<Long> trashedProfiles = this.getTrashedProfiles(profileSet);
            final Boolean moveToTrash = message.optBoolean("move_to_trash");
            final Boolean permenantDelete = message.optBoolean("permanent_delete");
            secLog.put((Object)"PERMANENT_DELETE", (Object)Boolean.TRUE.equals(permenantDelete));
            if (trashedProfiles.size() > 1 && moveToTrash) {
                throw new APIHTTPException("COM0015", new Object[] { "Profiles [" + APIUtil.getCommaSeperatedString(trashedProfiles) + "] are already get trashed" });
            }
            if (trashedProfiles.size() != profileSet.size() && permenantDelete) {
                profileSet.removeAll(trashedProfiles);
                throw new APIHTTPException("COM0015", new Object[] { "Profiles [" + APIUtil.getCommaSeperatedString(profileSet) + "] must be moved to trash before deleting it permanently" });
            }
            final ProfileUtil profileUtil = new ProfileUtil();
            if (profileSet.size() != 0 && moveToTrash) {
                profileUtil.moveProfilesToTrash(APIUtil.getCommaSeperatedString(profileSet), APIUtil.getCustomerID(message), APIUtil.getLoginID(message), this.getProfileType());
                final JSONObject propertiesToClone = new JSONObject();
                propertiesToClone.put("profilesIds", (Object)profileSet.toArray());
                propertiesToClone.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
                propertiesToClone.put("PROFILE_TYPE", this.getProfileType());
                propertiesToClone.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
                propertiesToClone.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
                final int actionType = 101;
                SyncConfigurationListeners.invokeListeners(propertiesToClone, actionType);
            }
            if (permenantDelete) {
                final String profileIDs = APIUtil.getCommaSeperatedString(trashedProfiles);
                if (!profileUtil.isProfileDeleteSafe(profileIDs)) {
                    throw new APIHTTPException("APP0002", new Object[] { I18N.getMsg("mdm.api.error.delete.app.reason", new Object[] { profileIDs }) });
                }
                final JSONObject propertiesToClone2 = new JSONObject();
                propertiesToClone2.put("profilesIds", (Object)trashedProfiles.toArray());
                propertiesToClone2.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
                propertiesToClone2.put("PROFILE_TYPE", this.getProfileType());
                propertiesToClone2.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
                propertiesToClone2.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
                final int actionType2 = 102;
                SyncConfigurationListeners.invokeListeners(propertiesToClone2, actionType2);
                final JSONObject requestJSON = new JSONObject();
                requestJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
                requestJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
                requestJSON.put("profileIDs", (Object)profileIDs);
                profileUtil.deleteProfile(requestJSON);
            }
            this.setUpdateCountMessageStatus(APIUtil.getCustomerID(message));
            remarks = "success";
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException occurred", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in deleteOrTrashProfile", e2);
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "TRASH_PROFILE", secLog);
        }
    }
    
    private HashSet<Long> getTrashedProfiles(final HashSet<Long> profileSet) throws DataAccessException {
        final HashSet<Long> trashedProfiles = new HashSet<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileSet.toArray(), 8).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0)));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("Profile");
            while (rows.hasNext()) {
                trashedProfiles.add(Long.valueOf(String.valueOf(rows.next().get("PROFILE_ID"))));
            }
        }
        return trashedProfiles;
    }
    
    public JSONObject getPayloads(final JSONObject message) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "entered getPayloads");
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final String include = APIUtil.getStringFilter(message, "include");
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final ProfileConfigurationUtil profileConfigUtil = ProfileConfigurationUtil.getInstance();
            final Integer configID = profileConfigUtil.getConfigID(payloadName);
            if (configID == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            if (collectionID != -1L) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("payload_type", (Object)configID);
                jsonObject.put("payload_name", (Object)payloadName);
                final DataObject configDO = MDMConfigUtil.getConfigDataItemDOByCollectionId(configID, collectionID);
                if (!configDO.containsTable("ConfigDataItem")) {
                    throw new APIHTTPException("COM0008", new Object[] { " Payload Items not found for the Payload key " + payloadName });
                }
                final ConfigHandler configHandler = (ConfigHandler)profileConfigUtil.getPayloadConfigurationHandler(payloadName);
                JSONArray payloadJSON = new JSONArray();
                if (include != null && include.equalsIgnoreCase("payloaditems")) {
                    payloadJSON = configHandler.DOToAPIJSON(configDO, payloadName);
                }
                else if (include == null) {
                    final Iterator<Row> configDataItemIterator = configDO.getRows("ConfigDataItem");
                    while (configDataItemIterator.hasNext()) {
                        final Row configDataItem = configDataItemIterator.next();
                        payloadJSON.put((Object)String.valueOf(configDataItem.get("CONFIG_DATA_ITEM_ID")));
                    }
                }
                jsonObject.put("payloaditems", (Object)payloadJSON);
                this.logger.log(Level.INFO, "finished getPayloads");
                return jsonObject;
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException occurred", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final SyMException | DataAccessException e2) {
            this.logger.log(Level.SEVERE, "SyMException occurred", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONObject getPayloadsFromCollectionID(final JSONObject request) throws Exception {
        final Long collectionID = APIUtil.getResourceID(request, "COLLECTION_ID".toLowerCase());
        final Long profileID = (Long)DBUtil.getValueFromDB("ProfileToCollection", "COLLECTION_ID", (Object)collectionID, "PROFILE_ID");
        request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("profile_id", (Object)profileID);
        return this.getPayloads(request);
    }
    
    public JSONObject getPayload(final JSONObject message) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "entered getPayload");
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long configDataItemID = APIUtil.getResourceID(message, "payloaditem_id");
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final Integer configID = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (configID == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionID);
            final Criteria configDataItemCriteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configID, 0);
            final Criteria joinCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), 0);
            final Row configDataRow = configDetails.getRow("ConfigData", configDataItemCriteria, new Join("ConfigData", "ConfigDataItem", joinCriteria.and(configIdCriteria), 2));
            if (configDataRow == null) {
                throw new APIHTTPException("COM0008", new Object[] { " payloaditem_id - " + configDataItemID });
            }
            if (collectionID != -1L) {
                final DataObject DO = MDMConfigUtil.getConfigDataItemsDO(configID, configDataItemID);
                final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
                final JSONArray payloadJSON = configHandler.DOToAPIJSON(DO, payloadName);
                if (payloadJSON.length() != 0) {
                    final JSONObject result = payloadJSON.getJSONObject(0);
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                        final Row configDataItemRow = DO.getRow("MdConfigDataItemExtn");
                        if (configDataItemRow != null) {
                            result.put("payload_identifier", configDataItemRow.get("CONFIG_PAYLOAD_IDENTIFIER"));
                        }
                    }
                    return result;
                }
            }
            this.logger.log(Level.INFO, "finished getPayload");
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException occurred", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final SyMException | DataAccessException e2) {
            this.logger.log(Level.SEVERE, "SyMException occurred", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public void modifyProfile(final JSONObject message) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            final HashMap profileMap = MDMUtil.getInstance().getProfileDetails(profileID);
            if (requestJSON.has("profile_name") && !String.valueOf(requestJSON.get("profile_name")).equalsIgnoreCase(String.valueOf(profileMap.get("PROFILE_NAME"))) && new ProfileHandler().checkProfileNameExist(APIUtil.getCustomerID(message), String.valueOf(requestJSON.get("profile_name")), this.getProfileType(), null)) {
                throw new APIHTTPException("COM0010", new Object[] { "profile_name - " + requestJSON.optString("profile_name") });
            }
            final JSONObject current = new JSONObject((Map)profileMap);
            final JSONObject json = this.convertAPIBasicProfileJSON(requestJSON, current);
            json.put("PROFILE_ID", (Object)profileID);
            json.put("LAST_MODIFIED_BY", (Object)DMUserHandler.getDCUserID(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
            ProfileConfigHandler.modifyProfileCollection(json);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in modifyProfile()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject convertAPIBasicProfileJSON(final JSONObject requestJSON, final JSONObject current) throws JSONException {
        final JSONObject result = new JSONObject(current.toString());
        try {
            final Iterator<String> keys = requestJSON.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                result.put(key.toUpperCase(), requestJSON.get(key));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error in convertAPIBasicProfileJSON", (Throwable)e);
            throw e;
        }
        return result;
    }
    
    public void validateIfProfilesExists(Collection<Long> profileIDs, final Long customerID) throws APIHTTPException {
        try {
            if (profileIDs.isEmpty()) {
                throw new APIHTTPException("ENR00105", new Object[0]);
            }
            profileIDs = new HashSet<Long>(profileIDs);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIDs.toArray(), 8).and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("Profile");
            final ArrayList<Long> profiles = new ArrayList<Long>();
            while (rows.hasNext()) {
                profiles.add(Long.valueOf(String.valueOf(rows.next().get("PROFILE_ID"))));
            }
            profileIDs.removeAll(profiles);
            if (profileIDs.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(profileIDs) });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateIfProfileExists(final Long profileID, final Long customerID) throws APIHTTPException {
        if (profileID == null || profileID == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0).and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("Profile");
            final ArrayList<Long> profiles = new ArrayList<Long>();
            while (rows.hasNext()) {
                profiles.add(Long.valueOf(String.valueOf(rows.next().get("PROFILE_ID"))));
            }
            if (profiles.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { String.valueOf(profileID) });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void associateProfilesToDevices(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long customerID = APIUtil.getCustomerID(message);
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            List<Long> resourceList;
            if (deviceId != null && deviceId != -1L) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"));
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            final HashMap<Integer, ArrayList> platformDeviceMap = new DeviceFacade().validateIfDevicesExists(resourceList, APIUtil.getCustomerID(message));
            if (platformDeviceMap.size() > 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Devices are not with the unique platform type" });
            }
            final Integer platformType = platformDeviceMap.keySet().iterator().next();
            List<Long> profileList;
            if (profileID > 0L) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids"));
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = this.validateIfProfilesDistribute(profileList, APIUtil.getCustomerID(message), platformType);
            this.logger.log(Level.INFO, "associate profiles : [{0}] to device : [{0}]", new Object[] { profileList, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)properties).put("isGroup", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in associateProfilesToDevices()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    public void disassociateProfilesToDevices(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            List<Long> resourceList;
            if (deviceId != null && deviceId != -1L) {
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"));
                new DeviceFacade().validateIfDevicesExists(resourceList, APIUtil.getCustomerID(message));
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            List<Long> profileList;
            if (profileID > 0L) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids"));
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = new HashMap();
            final Map deviceProfileMap = this.getProfileForDevice(resourceList, profileList, this.getProfileType());
            final Set<Long> deviceIdSet = deviceProfileMap.keySet();
            for (final Long deviceIdLong : deviceIdSet) {
                final ArrayList validateProfile = new ArrayList((Collection<? extends E>)profileList);
                final Map deviceProfileCollectionMap = deviceProfileMap.get(deviceIdLong);
                final Set<Long> profileIdSet = deviceProfileCollectionMap.keySet();
                validateProfile.removeAll(profileIdSet);
                if (!validateProfile.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(validateProfile) });
                }
                profileCollectionMap.putAll(deviceProfileCollectionMap);
            }
            this.logger.log(Level.INFO, "disassociate profiles : [{0}] to devices : [{0}]", new Object[] { profileList, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in disassociateProfilesToDevices()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    public void associateProfilesToGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long customerID = APIUtil.getCustomerID(message);
            List profileList;
            if (profileID > 0L) {
                profileList = new ArrayList();
                profileList.add(profileID);
            }
            else {
                profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids"));
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = this.validateIfProfilesDistribute(profileList, APIUtil.getCustomerID(message), null);
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            List<Long> groupList;
            if (groupId > 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, customerID);
            this.logger.log(Level.INFO, "associate profiles : [{0}] to groups : [{0}]", new Object[] { profileList, groupList });
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in associateProfilesToGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    public void disassociateProfilesToGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long customerID = APIUtil.getCustomerID(message);
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            List<Long> groupList;
            if (groupId > 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            final GroupFacade group = new GroupFacade();
            group.validateGroupsIfExists(groupList, customerID);
            List<Long> profileList;
            if (profileID > 0L) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids"));
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = new HashMap();
            final Map groupProfileMap = this.getProfileForGroup(groupList, profileList, this.getProfileType(), false);
            if (groupProfileMap.isEmpty() && !profileList.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(profileList) });
            }
            final Set<Long> groupIdSet = groupProfileMap.keySet();
            for (final Long groupIdLong : groupIdSet) {
                final Map deviceProfileCollectionMap = groupProfileMap.get(groupIdLong);
                final ArrayList validateProfile = new ArrayList((Collection<? extends E>)profileList);
                final Set<Long> profileIdSet = deviceProfileCollectionMap.keySet();
                validateProfile.removeAll(profileIdSet);
                if (!validateProfile.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(profileList) });
                }
                profileCollectionMap.putAll(deviceProfileCollectionMap);
            }
            this.logger.log(Level.INFO, "disassociate profiles : [{0}] to groups : [{1}]", new Object[] { profileList, groupList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in disassociateProfilesToGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    @Deprecated
    public void associateProfilesToUserGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            if (profileID == 0L) {
                profileID = null;
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileID);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final Properties properties = new Properties();
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_group_ids"));
            }
            secLog.put((Object)"USER_GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
            ((Hashtable<String, Integer>)properties).put("groupType", 7);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in associateProfilesToUserGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    @Deprecated
    public void disassociateProfilesToUserGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            if (profileID == 0L) {
                profileID = null;
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileID);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final Properties properties = new Properties();
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else if (message.getJSONObject("msg_header").getJSONObject("filters").has("user_group_ids")) {
                groupList = new ArrayList<Long>();
                final String temp = String.valueOf(message.getJSONObject("msg_header").getJSONObject("filters").get("user_group_ids"));
                final String[] split;
                final String[] ids = split = temp.split(",");
                for (final String id : split) {
                    groupList.add(Long.valueOf(id));
                }
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_group_ids"));
            }
            secLog.put((Object)"USER_GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, Integer>)properties).put("groupType", 7);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in disassociateProfilesToUserGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    public void associateProfilesToUsers(final JSONObject message) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            if (profileID == -1L) {
                profileID = null;
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileID);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final Properties properties = new Properties();
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            final Long userID = APIUtil.getResourceID(message, "user_id");
            List<Long> resourceList;
            if (userID != -1L) {
                resourceList = new ArrayList<Long>();
                resourceList.add(userID);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            secLog.put((Object)"USER_IDs", (Object)resourceList);
            new ManagedUserFacade().validateIfUserExists(resourceList, APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)properties).put("isGroup", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Integer>)properties).put("resourceType", 2);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in associateCollectionToMDMResource()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    public void disassociateProfilesToUsers(final JSONObject message) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            Long profileID = APIUtil.getResourceID(message, "profile_id");
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            if (profileID == -1L) {
                profileID = null;
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileID);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final Properties properties = new Properties();
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            final Long userID = APIUtil.getResourceID(message, "user_id");
            List<Long> resourceList;
            if (userID != -1L) {
                resourceList = new ArrayList<Long>();
                resourceList.add(userID);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            secLog.put((Object)"USER_IDs", (Object)resourceList);
            new ManagedUserFacade().validateIfUserExists(resourceList, APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
            ((Hashtable<String, Integer>)properties).put("resourceType", 2);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in disassociateCollectionFromMDMResource()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    public JSONObject getProfiles(final JSONObject request) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        final JSONArray profiles = new JSONArray();
        Connection conn = null;
        DataSet ds = null;
        try {
            SelectQuery selectQuery = this.getProfileQuery();
            this.addSelectColumns(selectQuery);
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final String platform = request.getJSONObject("msg_header").getJSONObject("filters").optString("platform");
            final Boolean trashed = APIUtil.getBooleanFilter(request, "trashed");
            final Boolean trashOnly = APIUtil.getBooleanFilter(request, "trash_only");
            final Boolean distributionSummary = APIUtil.getBooleanFilter(request, "summary");
            final Integer status_id = APIUtil.getIntegerFilter(request, "profile_status_id");
            Criteria finalCriteria = null;
            final String search = APIUtil.getStringFilter(request, "search");
            finalCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0);
            Criteria platformCriteria = null;
            if (platform.length() != 0) {
                int p = -1;
                if (platform.equalsIgnoreCase("android")) {
                    p = 2;
                }
                else if (platform.equalsIgnoreCase("ios")) {
                    p = 1;
                }
                else if (platform.equalsIgnoreCase("windows")) {
                    p = 3;
                }
                else if (platform.equalsIgnoreCase("chrome")) {
                    p = 4;
                }
                else if (platform.equalsIgnoreCase("macos")) {
                    p = 6;
                }
                else if (platform.equalsIgnoreCase("tvos")) {
                    p = 7;
                }
                else {
                    final String[] platformTypes = platform.split(",");
                    final ArrayList<Integer> values = new ArrayList<Integer>();
                    for (int i = 0; i < platformTypes.length; ++i) {
                        final int temp = Integer.parseInt(platformTypes[i]);
                        if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                            values.add(temp);
                        }
                    }
                    if (values.size() != 0) {
                        platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)values.toArray(), 8);
                    }
                }
                if (p != -1) {
                    platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)p, 0);
                }
            }
            if (trashOnly != null && trashOnly) {
                finalCriteria = finalCriteria.and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0));
            }
            else if (trashed != null && !trashed) {
                finalCriteria = finalCriteria.and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0));
            }
            Criteria searchCriteria = null;
            if (search != null) {
                searchCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)search, 12, false);
            }
            Criteria statusCriteria = null;
            if (status_id != -1) {
                statusCriteria = new Criteria(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)status_id, 0);
            }
            if (statusCriteria != null) {
                finalCriteria = finalCriteria.and(statusCriteria);
            }
            if (searchCriteria != null) {
                finalCriteria = finalCriteria.and(searchCriteria);
            }
            if (platformCriteria != null) {
                finalCriteria = finalCriteria.and(platformCriteria);
            }
            if (distributionSummary) {
                selectQuery = this.getProfileAssociationDeviceCount(selectQuery);
                selectQuery = this.getProfileAssociationGroupCount(selectQuery);
            }
            this.setProfileQueryCriteria(selectQuery, finalCriteria);
            final SelectQuery cQuery = this.getProfileQuery();
            cQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID").distinct().count());
            this.setProfileQueryCriteria(cQuery, finalCriteria);
            final int count = DBUtil.getRecordCount(cQuery);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)selectQuery, conn);
                while (ds.next()) {
                    final JSONObject profile = APIUtil.getNewInstance().getJSONObjectFromDS(ds, selectQuery);
                    final int profileSharedScope = profile.optInt("profile_shared_scope", 0);
                    profile.put("is_for_all_customers", profileSharedScope == 1);
                    profiles.put((Object)profile);
                }
            }
            response.put("profiles", (Object)profiles);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getProfiles()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            APIUtil.closeConnection(conn, ds);
        }
    }
    
    private void addSelectColumns(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID", "profile_id"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE", "platform_type"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION", "profile_description"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME", "profile_name"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME", "last_modified_time"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY", "last_modified_by"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATED_BY", "created_by"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME", "creation_time"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "IS_MOVED_TO_TRASH", "is_moved_to_trash"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE", "scope"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_SHARED_SCOPE", "profile_shared_scope"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "profile_version"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID", "collection_id"));
        selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS", "profile_status_id"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_NAME", "profile_status"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        selectQuery.addSelectColumn(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
    }
    
    private void setProfileQueryCriteria(final SelectQuery selectQuery, final Criteria criteria) {
        Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)this.getProfileType(), 0);
        if (criteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(criteria);
        }
        selectQuery.setCriteria(profileTypeCriteria);
    }
    
    private SelectQuery getProfileQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CollectionStatus", "ConfigStatusDefn", new String[] { "PROFILE_COLLECTION_STATUS" }, new String[] { "STATUS_ID" }, 1));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "Profile", "CREATED_USER", 2));
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "Profile", "LAST_MODIFIED_USER", 2));
        return selectQuery;
    }
    
    public Object getPayloadNames(final JSONObject message) throws APIHTTPException {
        try {
            this.validateIfProfileExists(APIUtil.getResourceID(message, "profile_id"), APIUtil.getCustomerID(message));
            final Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final JSONObject profileDetails = new JSONObject();
            final JSONArray configNameArray = this.getPayloadNameArray(collectionID);
            profileDetails.put("payloads", (Object)configNameArray);
            return profileDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void restoreProfileFromTrash(final JSONObject message) throws APIHTTPException {
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            HashSet<Long> profileSet = null;
            if (profileID != -1L) {
                profileSet = new HashSet<Long>(Arrays.asList(profileID));
            }
            else {
                profileSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("profile_ids")));
            }
            this.validateIfProfilesExists(profileSet, APIUtil.getCustomerID(message));
            final HashSet<Long> trashedProfiles = this.getTrashedProfiles(profileSet);
            profileSet.removeAll(trashedProfiles);
            if (!profileSet.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { "Trashed profiles - " + profileSet });
            }
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("Profile");
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)trashedProfiles.toArray(), 8);
            query.setCriteria(criteria);
            query.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)Boolean.FALSE);
            query.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            MDMUtil.getPersistence().update(query);
            final JSONObject synDetails = new JSONObject();
            synDetails.put("profilesIds", (Object)trashedProfiles.toArray());
            synDetails.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
            synDetails.put("PROFILE_TYPE", this.getProfileType());
            synDetails.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
            synDetails.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
            SyncConfigurationListeners.invokeListeners(synDetails, 103);
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "error in restoreProfileFromTrash", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceDistributionDetails(final JSONObject message) throws APIHTTPException {
        Connection conn = null;
        DataSet ds = null;
        final JSONObject response = new JSONObject();
        try {
            final JSONArray resp = new JSONArray();
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final SelectQuery selectQuery = this.getBaseDeviceDistributionQuery(message);
            final SelectQuery countQuery = this.getBaseDeviceDistributionCountQuery(message);
            final int count = DBUtil.getRecordCount(countQuery);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)selectQuery, conn);
                while (ds.next()) {
                    final List<Column> columns = selectQuery.getSelectColumns();
                    final JSONObject profile = new JSONObject();
                    for (final Column column : columns) {
                        if (column.getColumnAlias() != null) {
                            if (column.getColumnAlias().equalsIgnoreCase("remarks")) {
                                profile.put(column.getColumnAlias(), (Object)APIUtil.getEnglishString(String.valueOf(ds.getValue(column.getColumnAlias())), new Object[0]));
                                profile.put("localized_remarks", (Object)APIUtil.getLocalizedString(String.valueOf(ds.getValue(column.getColumnAlias())), null, new Object[0]));
                            }
                            else {
                                profile.put(column.getColumnAlias(), (Object)String.valueOf(ds.getValue(column.getColumnAlias())));
                            }
                        }
                        else {
                            profile.put(column.getColumnName(), (Object)String.valueOf(ds.getValue(column.getColumnName())));
                        }
                    }
                    resp.put((Object)profile);
                }
            }
            response.put("devices", (Object)resp);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            APIUtil.closeConnection(conn, ds);
        }
    }
    
    private SelectQuery getBaseDeviceDistributionQuery(final JSONObject request) throws JSONException {
        final String include = APIUtil.getStringFilter(request, "include");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CollnToResources", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        if (include != null && include.equalsIgnoreCase("devicedetails")) {
            selectQuery.addJoin(new Join("RecentProfileForResource", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentPubProfileToColln", "LATEST_PROFILE_COLLECTION", 2));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("RecentProfileForResource", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USER_RESOURCE", 1));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "device_name"));
            selectQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "APPLIED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME", "user_name"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS", "user_email"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID", "user_id"));
            selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE", "device_type"));
            selectQuery.addSelectColumn(Column.getColumn("LATEST_PROFILE_COLLECTION", "PROFILE_VERSION", "latest_version"));
        }
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID", "device_id"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE", "platform_type"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "executed_version"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        selectQuery.addSortColumn(new SortColumn("RecentProfileForResource", "RESOURCE_ID", true));
        Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)APIUtil.getResourceID(request, "profile_id"), 0);
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Object[] { 120, 121 }, 8));
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1));
        final Long collectionID = APIUtil.getResourceID(request, "collection_id");
        final Long versionId = APIUtil.getResourceID(request, "version_id");
        if (collectionID != -1L && versionId != -1L) {
            criteria = criteria.and(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0));
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    private SelectQuery getBaseDeviceDistributionCountQuery(final JSONObject request) throws JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CollnToResources", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID", "device_id").count());
        Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)APIUtil.getResourceID(request, "profile_id"), 0);
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Object[] { 120, 121 }, 8));
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1));
        final Long collectionID = APIUtil.getResourceID(request, "collection_id");
        final Long versionId = APIUtil.getResourceID(request, "version_id");
        if (collectionID != -1L && versionId != -1L) {
            criteria = criteria.and(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0));
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public JSONObject getGroupDistributionDetails(final JSONObject message) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            final JSONArray resp = new JSONArray();
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
            this.validateIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final SelectQuery selectQuery = this.getBaseGroupDistributionQuery(message);
            final SelectQuery countQuery = this.getBaseGroupDistributionCountQuery(message);
            final int count = DBUtil.getRecordCount(countQuery);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (ds.next()) {
                    final List<Column> columns = selectQuery.getSelectColumns();
                    final JSONObject profile = new JSONObject();
                    for (final Column column : columns) {
                        if (column.getColumnAlias() != null) {
                            if (column.getColumnAlias().equalsIgnoreCase("remarks") || column.getColumnAlias().equalsIgnoreCase("label")) {
                                profile.put(column.getColumnAlias(), (Object)APIUtil.getEnglishString(String.valueOf(ds.getValue(column.getColumnAlias())), new Object[0]));
                                profile.put("localized_remarks", (Object)APIUtil.getLocalizedString(String.valueOf(ds.getValue(column.getColumnAlias())), null, new Object[0]));
                            }
                            else {
                                profile.put(column.getColumnAlias(), (Object)String.valueOf(ds.getValue(column.getColumnAlias())));
                            }
                        }
                        else {
                            profile.put(column.getColumnName(), (Object)String.valueOf(ds.getValue(column.getColumnName())));
                        }
                    }
                    resp.put((Object)profile);
                }
            }
            response.put("groups", (Object)resp);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private SelectQuery getBaseGroupDistributionQuery(final JSONObject request) throws JSONException {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("GroupToProfileHistory", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentPubProfileToColln", "LATEST_PROFILE_COLLECTION", 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("GroupToProfileHistory", "ConfigStatusDefn", new String[] { "COLLECTION_STATUS" }, new String[] { "STATUS_ID" }, 2));
        selectQuery = MDMCustomGroupUtil.getInstance().getQueryforGroupControllers(selectQuery, true);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME", "group_name"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "executed_version"));
        selectQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL", "label"));
        selectQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "REMARKS", "remarks"));
        selectQuery.addSelectColumn(Column.getColumn("LATEST_PROFILE_COLLECTION", "PROFILE_VERSION", "latest_version"));
        selectQuery.addSortColumn(new SortColumn("RecentProfileForGroup", "GROUP_ID", true));
        final Column maxCountCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
        maxCountCol.setColumnAlias("MEMBER_COUNT");
        selectQuery.addSelectColumn(maxCountCol);
        Criteria criteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)APIUtil.getResourceID(request, "profile_id"), 0);
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        final Long collectionID = APIUtil.getResourceID(request, "collection_id");
        final Long versionId = APIUtil.getResourceID(request, "version_id");
        if (collectionID != -1L && versionId != -1L) {
            criteria = criteria.and(new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0));
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    private SelectQuery getBaseGroupDistributionCountQuery(final JSONObject request) throws JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("GroupToProfileHistory", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("GroupToProfileHistory", "ConfigStatusDefn", new String[] { "COLLECTION_STATUS" }, new String[] { "STATUS_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID").count());
        Criteria criteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)APIUtil.getResourceID(request, "profile_id"), 0);
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0));
        final Long collectionID = APIUtil.getResourceID(request, "collection_id");
        final Long versionId = APIUtil.getResourceID(request, "version_id");
        if (collectionID != -1L && versionId != -1L) {
            criteria = criteria.and(new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0));
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    private JSONArray getPayloadNameArray(final Long collectionId) throws DataAccessException, SyMException {
        final List<DataObject> DOList = MDMConfigUtil.getConfigurations(collectionId);
        final JSONArray payloadNameArray = new JSONArray();
        for (final DataObject DO : DOList) {
            if (DO.containsTable("ConfigData")) {
                final Row row = DO.getFirstRow("ConfigData");
                final String configName = String.valueOf(row.get("LABEL"));
                payloadNameArray.put((Object)ProfileConfigurationUtil.getInstance().getPayloadName(configName));
            }
        }
        return payloadNameArray;
    }
    
    private JSONObject getPayloadDetails(final Long collectionID) throws JSONException {
        final JSONObject payloads = new JSONObject();
        try {
            if (collectionID != -1L) {
                final List<DataObject> DOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
                for (final DataObject DO : DOList) {
                    String configName = null;
                    if (DO.containsTable("ConfigData")) {
                        final Row row = DO.getFirstRow("ConfigData");
                        configName = String.valueOf(row.get("LABEL"));
                    }
                    if (configName == null) {
                        continue;
                    }
                    final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(configName);
                    final JSONArray payloadJSON = configHandler.DOToAPIJSON(DO, configName);
                    if (payloadJSON.length() == 0) {
                        continue;
                    }
                    if (payloads.has(configName)) {
                        final JSONArray jsonArray = payloads.getJSONArray(configName);
                        for (int index = 0; index < jsonArray.length(); ++index) {
                            payloadJSON.put(jsonArray.get(index));
                        }
                    }
                    payloads.put(configName, (Object)payloadJSON);
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return payloads;
    }
    
    public JSONObject getContentRating(final JSONObject request) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            final String search = APIUtil.getStringFilter(request, "search");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdContentCountry"));
            selectQuery.addJoin(new Join("MdContentCountry", "MdMoviesRating", new String[] { "COUNTRY_ID" }, new String[] { "COUNTRY_ID" }, 2));
            selectQuery.addJoin(new Join("MdContentCountry", "MdTvShowsRating", new String[] { "COUNTRY_ID" }, new String[] { "COUNTRY_ID" }, 2));
            selectQuery.addJoin(new Join("MdContentCountry", "MdAppsRating", new String[] { "COUNTRY_ID" }, new String[] { "COUNTRY_ID" }, 2));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            if (search != null) {
                final Criteria searchCriteria = new Criteria(new Column("MdContentCountry", "COUNTRY_CODE"), (Object)search, 12, false);
                selectQuery.setCriteria(searchCriteria);
            }
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MdContentCountry");
            final SortColumn movieColumn = new SortColumn(new Column("MdMoviesRating", "MOVIES_RATING_VALUE"), true);
            final SortColumn tvShowColumn = new SortColumn(new Column("MdTvShowsRating", "TV_SHOWS_RATING_VALUE"), true);
            final SortColumn appColumn = new SortColumn(new Column("MdAppsRating", "APPS_RATING_VALUE"), true);
            dataObject.sortRows("MdMoviesRating", new SortColumn[] { movieColumn });
            dataObject.sortRows("MdTvShowsRating", new SortColumn[] { tvShowColumn });
            dataObject.sortRows("MdAppsRating", new SortColumn[] { appColumn });
            final JSONArray countryArray = new JSONArray();
            final JSONObject countryContentJSON = new JSONObject();
            while (iterator.hasNext()) {
                final Row countryRow = iterator.next();
                final JSONObject countryJSON = new JSONObject();
                final String countryCode = (String)countryRow.get("COUNTRY_CODE");
                countryJSON.put("country_name", (Object)countryRow.get("COUNTRY_DISPLAY_NAME"));
                countryJSON.put("country_code", (Object)countryCode);
                countryArray.put((Object)countryJSON);
                final Long countryId = (Long)countryRow.get("COUNTRY_ID");
                final Iterator movieIterator = dataObject.getRows("MdMoviesRating", new Criteria(new Column("MdMoviesRating", "COUNTRY_ID"), (Object)countryId, 0));
                final JSONArray movieArray = new JSONArray();
                while (movieIterator.hasNext()) {
                    final JSONObject movieJSON = new JSONObject();
                    final Row movieRow = movieIterator.next();
                    movieJSON.put("movies_rating_name", (Object)I18N.getMsg((String)movieRow.get("MOVIES_RATING"), new Object[0]));
                    movieJSON.put("movies_rating_value", movieRow.get("MOVIES_RATING_VALUE"));
                    movieArray.put((Object)movieJSON);
                }
                final JSONArray tvShowArray = new JSONArray();
                final Iterator tvShowIterator = dataObject.getRows("MdTvShowsRating", new Criteria(new Column("MdTvShowsRating", "COUNTRY_ID"), (Object)countryId, 0));
                while (tvShowIterator.hasNext()) {
                    final JSONObject tvShowJSON = new JSONObject();
                    final Row tvShowRow = tvShowIterator.next();
                    tvShowJSON.put("tv_show_name", (Object)I18N.getMsg((String)tvShowRow.get("TV_SHOWS_RATING"), new Object[0]));
                    tvShowJSON.put("tv_show_value", tvShowRow.get("TV_SHOWS_RATING_VALUE"));
                    tvShowArray.put((Object)tvShowJSON);
                }
                final JSONArray appArray = new JSONArray();
                final Iterator appIterator = dataObject.getRows("MdAppsRating", new Criteria(new Column("MdAppsRating", "COUNTRY_ID"), (Object)countryId, 0));
                while (appIterator.hasNext()) {
                    final JSONObject appJSON = new JSONObject();
                    final Row appRow = appIterator.next();
                    appJSON.put("app_rating_name", (Object)I18N.getMsg((String)appRow.get("APPS_RATING"), new Object[0]));
                    appJSON.put("app_rating_value", appRow.get("APPS_RATING_VALUE"));
                    appArray.put((Object)appJSON);
                }
                final JSONObject countryContentDetailJSON = new JSONObject();
                countryContentDetailJSON.put("movies", (Object)movieArray);
                countryContentDetailJSON.put("tvshows", (Object)tvShowArray);
                countryContentDetailJSON.put("apps", (Object)appArray);
                countryContentJSON.put(countryCode, (Object)countryContentDetailJSON);
            }
            responseJSON.put("countrydetails", (Object)countryArray);
            responseJSON.put("countrycontent", (Object)countryContentJSON);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in content rating", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception in content rating", (Throwable)e2);
            throw e2;
        }
        return responseJSON;
    }
    
    public void cancelProfileCreation(final JSONObject message) {
        try {
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            final JSONObject profileObject = this.validateAndGetIfProfileExists(profileID, APIUtil.getCustomerID(message));
            final int version = profileObject.getInt("PROFILE_VERSION");
            final Long collectionId = profileObject.getLong("COLLECTION_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
            selectQuery.addJoin(new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.setCriteria(new Criteria(new Column("RecentProfileToColln", "PROFILE_ID"), (Object)profileID, 0));
            final int configurationcount = DBUtil.getRecordCount(selectQuery, "CfgDataToCollection", "COLLECTION_ID");
            if (configurationcount == 0) {
                if (version == 1) {
                    this.logger.log(Level.INFO, "No Configurations is mapped to that collection (profileID:{0})So we can delete the profile on cancelling", profileID);
                    ProfileConfigHandler.deleteProfile(profileID.toString(), APIUtil.getCustomerID(message));
                    this.logger.log(Level.INFO, "Empty Profile (ProfileIds:{0})is deleted.", profileID);
                }
                else {
                    this.logger.log(Level.INFO, "The Profile is cancelled without configuration so we are moving back to previous version, collection id {0}", collectionId);
                    final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                    final Join profileVerJoin = new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                    final Join profileCollnJoin = new Join("RecentProfileToColln", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                    final Join collnJoin = new Join("RecentProfileToColln", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
                    profileQuery.addJoin(profileVerJoin);
                    profileQuery.addJoin(profileCollnJoin);
                    profileQuery.addJoin(collnJoin);
                    profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                    profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
                    profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
                    profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
                    profileQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
                    profileQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
                    profileQuery.addSelectColumn(Column.getColumn("Collection", "COLLECTION_ID"));
                    final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0).and(new Criteria(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"), (Object)collectionId, 0));
                    profileQuery.setCriteria(criteria);
                    final DataObject dataObject = MDMUtil.getPersistence().get(profileQuery);
                    dataObject.sortRows("ProfileToCollection", new SortColumn[] { new SortColumn("ProfileToCollection", "PROFILE_VERSION", false) });
                    final Row oldProfileToCollection = dataObject.getRow("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 1));
                    final Long oldCollectionId = (Long)oldProfileToCollection.get("COLLECTION_ID");
                    this.logger.log(Level.INFO, "Profile are updated with previous collection id {0}", oldCollectionId);
                    final Row recentProfileToCollection = dataObject.getRow("RecentProfileToColln");
                    recentProfileToCollection.set("COLLECTION_ID", (Object)oldCollectionId);
                    dataObject.updateRow(recentProfileToCollection);
                    dataObject.deleteRows("Collection", new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
                    MDMUtil.getPersistence().update(dataObject);
                    this.logger.log(Level.INFO, "Profile was successfully updated with previous collection id {0} to the profile id {1}", new Object[] { oldCollectionId, profileID });
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException occurred", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in cancelling the profile while creation", e2);
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public HashMap validateIfProfilesDistribute(Collection<Long> profileIDs, final Long customerID, final Integer platformType) throws APIHTTPException {
        if (profileIDs.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        final HashMap<Long, Long> profileCollectionMap = new HashMap<Long, Long>();
        try {
            this.logger.log(Level.INFO, "validate profile can distribute, profile ids:{0}", profileIDs);
            profileIDs = new HashSet<Long>(profileIDs);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            selectQuery.addJoin(new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentPubProfileToColln", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentPubProfileToColln", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
            Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIDs.toArray(), 8).and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
            if (platformType != null) {
                final Criteria platformNeutralCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)0, 0);
                if (platformType.equals(1)) {
                    criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 7, 6, 1 }, 8).or(platformNeutralCriteria));
                }
                else {
                    criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0).or(platformNeutralCriteria));
                }
            }
            this.setProfileQueryCriteria(selectQuery, criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final List profileCheckIds = new ArrayList(profileIDs);
            final Iterator<Row> rows = dataObject.getRows("RecentProfileToColln");
            final Set draftProfiles = new HashSet();
            while (rows.hasNext()) {
                final Row profileCollectionRow = rows.next();
                final Long profileId = (Long)profileCollectionRow.get("PROFILE_ID");
                final Long recentCollectionId = (Long)profileCollectionRow.get("COLLECTION_ID");
                final Row recentPubProfileToCooln = dataObject.getRow("RecentPubProfileToColln", new Criteria(Column.getColumn("RecentPubProfileToColln", "PROFILE_ID"), (Object)profileId, 0));
                if (recentPubProfileToCooln == null) {
                    draftProfiles.add(profileId);
                    profileCollectionMap.put(profileId, recentCollectionId);
                }
                else {
                    final Long collectionId = (Long)recentPubProfileToCooln.get("COLLECTION_ID");
                    profileCollectionMap.put(profileId, collectionId);
                }
            }
            final Set availableProfileIds = profileCollectionMap.keySet();
            profileCheckIds.removeAll(availableProfileIds);
            if (profileCheckIds.size() > 0) {
                final String remark = "Profile Id :" + APIUtil.getCommaSeperatedString(profileCheckIds);
                throw new APIHTTPException("COM0008", new Object[] { remark });
            }
            if (draftProfiles.size() > 0) {
                throw new APIHTTPException("COM0015", new Object[] { "Profiles [" + APIUtil.getCommaSeperatedString(draftProfiles) + "] should be in published status" });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return profileCollectionMap;
    }
    
    public JSONObject getGroupProfiles(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final String include = APIUtil.getStringFilter(message, "include");
            this.logger.log(Level.INFO, "get group profile group id:{0}", groupId);
            new GroupFacade().validateAndGetGroupDetails(groupId, customerId);
            final JSONObject jsonObject = new JSONObject();
            JSONArray profileIds = new JSONArray();
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
            if (include != null && include.equalsIgnoreCase("details")) {
                SelectQuery selectQuery = this.getGroupProfilesBaseQuery(groupId, customerId, true);
                selectQuery = this.getProfileAssociationGroupCount(selectQuery);
                selectQuery = this.getProfileAssociationDeviceCount(selectQuery);
                final SelectQuery countQuery = this.getGroupProfilesBaseQuery(groupId, customerId, true);
                while (countQuery.getSelectColumns().size() > 0) {
                    countQuery.removeSelectColumn(0);
                }
                countQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID").distinct().count());
                final int count = DBUtil.getRecordCount(countQuery);
                if (count != 0) {
                    final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                    if (pagingJSON != null) {
                        jsonObject.put("paging", (Object)pagingJSON);
                    }
                    selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
                    selectQuery.setGroupByClause(new GroupByClause(selectQuery.getSelectColumns()));
                    final Column associatedTimeColumn = Column.getColumn("GroupToProfileHistory", "ASSOCIATED_TIME").maximum();
                    associatedTimeColumn.setColumnAlias("ASSOCIATED_TIME");
                    selectQuery.addSelectColumn(associatedTimeColumn);
                    final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                    while (dataSetWrapper.next()) {
                        final int latestVer = (int)dataSetWrapper.getValue("PROFILE_VERSION");
                        final int executionVer = (int)dataSetWrapper.getValue("ProfileColln.PROFILE_VERSION");
                        final String associatedByUserName = (String)dataSetWrapper.getValue("associatedByUserName");
                        final Long associatedOn = (Long)dataSetWrapper.getValue("ASSOCIATED_TIME");
                        final JSONObject profileJson = new JSONObject();
                        profileJson.put("profile_id", dataSetWrapper.getValue("PROFILE_ID"));
                        profileJson.put("profile_name", dataSetWrapper.getValue("PROFILE_NAME"));
                        profileJson.put("profile_description", dataSetWrapper.getValue("PROFILE_DESCRIPTION"));
                        profileJson.put("platform_type", dataSetWrapper.getValue("PLATFORM_TYPE"));
                        profileJson.put("last_modified_time", dataSetWrapper.getValue("LAST_MODIFIED_TIME"));
                        profileJson.put("last_modified_by", dataSetWrapper.getValue("LAST_MODIFIED_BY"));
                        profileJson.put("last_modified_by_user", dataSetWrapper.getValue("last_modified_by_user"));
                        profileJson.put("creation_time", dataSetWrapper.getValue("CREATION_TIME"));
                        profileJson.put("created_by", dataSetWrapper.getValue("CREATED_BY"));
                        profileJson.put("created_by_user", dataSetWrapper.getValue("created_by_user"));
                        profileJson.put("scope", dataSetWrapper.getValue("SCOPE"));
                        profileJson.put("associated_by", (Object)associatedByUserName);
                        profileJson.put("associated_on", (Object)associatedOn);
                        profileJson.put("latest_version", latestVer);
                        profileJson.put("executed_version", executionVer);
                        profileJson.put("status", dataSetWrapper.getValue("STATUS"));
                        profileJson.put("remarks", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS")), new Object[0]));
                        profileIds.put((Object)profileJson);
                    }
                }
            }
            else {
                final Map<Long, Map> groupProfileMap = this.getProfileForGroup(Arrays.asList(groupId), null, this.getProfileType(), true);
                if (!groupProfileMap.isEmpty()) {
                    final HashMap profileCollectionMap = groupProfileMap.get(groupId);
                    profileIds = JSONUtil.getInstance().convertListToStringJSONArray(new ArrayList(profileCollectionMap.keySet()));
                }
            }
            jsonObject.put("profiles", (Object)profileIds);
            return jsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getGroupProfileDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getGroupProfileDetail(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            final Long customerId = APIUtil.getCustomerID(message);
            this.logger.log(Level.INFO, "get group profile details, group id:{0}", groupId);
            new GroupFacade().validateAndGetGroupDetails(groupId, customerId);
            final Long profileId = APIUtil.getResourceID(message, "profile_id");
            final JSONObject jsonObject = this.validateAndGetIfProfileinGroup(profileId, groupId, customerId, false);
            return jsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getGroupProfileDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject validateAndGetIfProfileinGroup(final Long profileId, final Long groupId, final Long customerId, final Boolean excludedDeleted) throws Exception {
        if (profileId == null || profileId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        this.logger.log(Level.INFO, "validate and get profile , profile id:{0} and group id:{1}", new Object[] { profileId, groupId });
        final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupId, customerId);
        profileQuery.addJoin(new Join("RecentProfileForGroup", "CollnToResources", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
        Criteria criteria = profileQuery.getCriteria().and(profileCriteria);
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 10, 1 }, 8);
        criteria = criteria.and(profileTypeCri);
        if (excludedDeleted) {
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            criteria = criteria.and(markedForDelete);
        }
        profileQuery.setCriteria(criteria);
        profileQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)profileQuery);
        if (!dataSetWrapper.next()) {
            final String remark = "group Id : " + groupId.toString();
            throw new APIHTTPException("COM0008", new Object[] { remark });
        }
        final int latestVer = (int)dataSetWrapper.getValue("PROFILE_VERSION");
        final int executionVer = (int)dataSetWrapper.getValue("ProfileColln.PROFILE_VERSION");
        final Long associatedByUser = (Long)dataSetWrapper.getValue("associatedByUser");
        final String associatedByUserName = (String)dataSetWrapper.getValue("associatedByUserName");
        final Long associatedOn = (Long)dataSetWrapper.getValue("LAST_MODIFIED_TIME");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("profile_id", dataSetWrapper.getValue("PROFILE_ID"));
        jsonObject.put("profile_name", dataSetWrapper.getValue("PROFILE_NAME"));
        jsonObject.put("profile_description", dataSetWrapper.getValue("PROFILE_DESCRIPTION"));
        jsonObject.put("platform_type", dataSetWrapper.getValue("PLATFORM_TYPE"));
        jsonObject.put("associated_by", (Object)associatedByUserName);
        jsonObject.put("associated_on", (Object)associatedOn);
        jsonObject.put("latest_version", latestVer);
        jsonObject.put("executed_version", executionVer);
        jsonObject.put("status", dataSetWrapper.getValue("STATUS"));
        jsonObject.put("remarks", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS")), new Object[0]));
        if (executionVer < latestVer) {
            jsonObject.put("isLatestVer", false);
        }
        else {
            jsonObject.put("isLatestVer", true);
        }
        return jsonObject;
    }
    
    public Map getProfileForGroup(final List<Long> groupIds, final List<Long> profileList, final Integer profileType, final Boolean excludedDeleted) throws DataAccessException {
        this.logger.log(Level.INFO, "get profile for group, group ids:{0}, profile ids:{1}", new Object[] { groupIds, profileList });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Join profileforGroupJoin = new Join("Profile", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        selectQuery.addJoin(profileforGroupJoin);
        Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        if (excludedDeleted) {
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            profileTypeCri = profileTypeCri.and(markedForDelete);
        }
        if (profileList != null && !profileList.isEmpty()) {
            final Criteria profileIdCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            profileTypeCri = profileTypeCri.and(profileIdCri);
        }
        final Criteria groupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        selectQuery.setCriteria(groupCri.and(profileTypeCri));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> profileToGroupRows = dataObject.getRows("RecentProfileForGroup");
        final Map<Long, HashMap> groupProfileMap = new HashMap<Long, HashMap>();
        while (profileToGroupRows.hasNext()) {
            final Row profileToGroupRow = profileToGroupRows.next();
            final Long groupId = (Long)profileToGroupRow.get("GROUP_ID");
            final Long groupProfileId = (Long)profileToGroupRow.get("PROFILE_ID");
            final Long groupProfileCollectionId = (Long)profileToGroupRow.get("COLLECTION_ID");
            if (!groupProfileMap.containsKey(groupId)) {
                groupProfileMap.put(groupId, new HashMap());
            }
            groupProfileMap.get(groupId).put(groupProfileId, groupProfileCollectionId);
        }
        return groupProfileMap;
    }
    
    public Map getProfileForDevice(final List<Long> deviceIds, final List<Long> profileList, final Integer profileType) throws DataAccessException {
        this.logger.log(Level.INFO, "get profile for device, device ids:{0}, profile ids:{1}", new Object[] { deviceIds, profileList });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        profileTypeCri = profileTypeCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Object[] { 120, 121 }, 8));
        if (profileList != null && !profileList.isEmpty()) {
            final Criteria profileIdCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            profileTypeCri = profileTypeCri.and(profileIdCri);
        }
        profileTypeCri = profileTypeCri.and(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8));
        selectQuery.setCriteria(profileTypeCri);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> profileToGroupRows = dataObject.getRows("RecentProfileForResource");
        final Map<Long, HashMap> deviceProfileMap = new HashMap<Long, HashMap>();
        while (profileToGroupRows.hasNext()) {
            final Row profileToGroupRow = profileToGroupRows.next();
            final Long deviceId = (Long)profileToGroupRow.get("RESOURCE_ID");
            final Long deviceProfileId = (Long)profileToGroupRow.get("PROFILE_ID");
            final Long deviceCollectionId = (Long)profileToGroupRow.get("COLLECTION_ID");
            if (!deviceProfileMap.containsKey(deviceId)) {
                deviceProfileMap.put(deviceId, new HashMap());
            }
            deviceProfileMap.get(deviceId).put(deviceProfileId, deviceCollectionId);
        }
        return deviceProfileMap;
    }
    
    public JSONObject getDeviceProfileDetails(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.logger.log(Level.INFO, "get device profile details, device id:{0}", deviceId);
            final Long customerId = APIUtil.getCustomerID(message);
            final Long profileId = APIUtil.getResourceID(message, "profile_id");
            final DeviceFacade deviceFacade = new DeviceFacade();
            new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
            final SelectQuery selectQuery = deviceFacade.getDeviceProfilesBaseQuery(deviceId);
            deviceFacade.addSelectColumnsForDeviceProfiles(selectQuery);
            Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
            profileCriteria = profileCriteria.and(selectQuery.getCriteria());
            selectQuery.setCriteria(profileCriteria);
            final JSONArray profileArray = MDMUtil.executeSelectQueryAndGetOrgJSONArray(selectQuery);
            JSONObject profile = new JSONObject();
            if (profileArray != null && profileArray.length() > 0) {
                profile = profileArray.getJSONObject(0);
                if (profile.has("localized_remarks")) {
                    profile.put("localized_remarks", (Object)I18N.getMsg(String.valueOf(profile.get("localized_remarks")), new Object[0]));
                }
                if (profile.has("remarks")) {
                    profile.put("remarks", (Object)I18N.getMsg(String.valueOf(profile.get("remarks")), new Object[0]));
                }
                return profile;
            }
            throw new APIHTTPException("COM0008", new Object[] { "Profile Id: " + profileId });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getGroupProfileDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject validateKioskForWindowsPhone(final JSONObject message) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            final JSONObject requestBody = message.getJSONObject("msg_body");
            final List profileIds = JSONUtil.getInstance().convertLongJSONArrayTOList(requestBody.getJSONArray("profile_ids"));
            final Map profileCollectionMap = this.validateIfProfilesDistribute(profileIds, customerId, null);
            final List resourceIds = JSONUtil.getInstance().convertLongJSONArrayTOList(requestBody.getJSONArray("resource_ids"));
            final List collectionList = new ArrayList(profileCollectionMap.values());
            final Boolean isGroup = requestBody.optBoolean("is_group");
            final JSONObject hasKioskStatus = new JSONObject();
            final boolean hasKiosk = WpAppSettingsHandler.getInstance().hasKioskProfileForPhones(resourceIds, collectionList, isGroup);
            hasKioskStatus.put("hasKiosk", hasKiosk);
            return hasKioskStatus;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getting kiosk profile is associated for phone", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject validateAndGetIfProfileExists(Long profileID, final Long customerID) throws APIHTTPException {
        if (profileID == null || profileID == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CollectionStatus", "IOSCollectionPayload", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
            selectQuery.addSelectColumn(Column.getColumn("IOSCollectionPayload", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("IOSCollectionPayload", "SECURITY_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
            Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 1, 10 }, 8));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Row profileROw = dataObject.getRow("Profile");
            if (profileROw == null) {
                throw new APIHTTPException("COM0008", new Object[] { String.valueOf(profileID) });
            }
            final Row iOSCollnROw = dataObject.getFirstRow("IOSCollectionPayload");
            final Row collectionStatusRow = dataObject.getFirstRow("CollectionStatus");
            final Row profileCollectionRow = dataObject.getFirstRow("ProfileToCollection");
            final JSONObject profileDetails = new JSONObject();
            profileID = (Long)profileROw.get("PROFILE_ID");
            profileDetails.put("PROFILE_NAME", profileROw.get("PROFILE_NAME"));
            profileDetails.put("PROFILE_DESCRIPTION", profileROw.get("PROFILE_DESCRIPTION"));
            profileDetails.put("CUSTOMER_ID", (Object)customerID);
            profileDetails.put("PROFILE_ID", profileROw.get("PROFILE_ID"));
            profileDetails.put("PROFILE_PAYLOAD_IDENTIFIER", profileROw.get("PROFILE_PAYLOAD_IDENTIFIER"));
            profileDetails.put("SCOPE", profileROw.get("SCOPE"));
            profileDetails.put("PLATFORM_TYPE", profileROw.get("PLATFORM_TYPE"));
            profileDetails.put("PROFILE_TYPE", profileROw.get("PROFILE_TYPE"));
            profileDetails.put("SECURITY_TYPE", iOSCollnROw.get("SECURITY_TYPE"));
            profileDetails.put("COLLECTION_ID", collectionStatusRow.get("COLLECTION_ID"));
            profileDetails.put("PROFILE_COLLECTION_STATUS", collectionStatusRow.get("PROFILE_COLLECTION_STATUS"));
            profileDetails.put("PROFILE_VERSION", profileCollectionRow.get("PROFILE_VERSION"));
            return profileDetails;
        }
        catch (final JSONException | DataAccessException ex) {
            this.logger.log(Level.INFO, "Exception while getting data for the profile : validateAndGetIfProfileExists {0}", profileID);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected int getProfileType() {
        return 1;
    }
    
    private int getProfileGroupAssociatedCount(final JSONObject message) throws Exception {
        final Long profileId = APIUtil.getResourceID(message, "profile_id");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0));
        RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return DBUtil.getRecordCount(selectQuery, "RecentProfileForGroup", "GROUP_ID");
    }
    
    private HashMap getProfileDeviceAssociatedCount(final JSONObject message) throws Exception {
        final HashMap deviceCounts = new HashMap();
        final Long profileId = APIUtil.getResourceID(message, "profile_id");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("Profile", "PLATFORM_TYPE"), 0);
        Criteria appleDeviceTempCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 7, 6 }, 8);
        appleDeviceTempCriteria = appleDeviceTempCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer[] { 1, 6, 7 }, 8));
        platformCriteria = platformCriteria.or(appleDeviceTempCriteria);
        profileCriteria = profileCriteria.and(platformCriteria);
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        selectQuery.setCriteria(profileCriteria);
        final CaseExpression ce = new CaseExpression("YET_TO_UPDATE");
        Criteria yetToUpdateCri = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        yetToUpdateCri = yetToUpdateCri.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)Column.getColumn("RecentPubProfileToColln", "COLLECTION_ID"), 1));
        ce.addWhen(yetToUpdateCri, (Object)1);
        final Column updateCount = MDMUtil.getInstance().getCountCaseExpressionColumn(ce, 4, "update_count");
        selectQuery.addSelectColumn(updateCount);
        final Column totalCount = Column.getColumn("RecentProfileForResource", "PROFILE_ID").count();
        totalCount.setColumnAlias("total");
        selectQuery.addSelectColumn(totalCount);
        RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                deviceCounts.put("toUpdate", Integer.valueOf(ds.getValue("update_count").toString()));
                deviceCounts.put("total", Integer.valueOf(ds.getValue("total").toString()));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error fetching associated device count", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceCounts;
    }
    
    public void updateRecentProfileForAll(final JSONObject request) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            final Long profileID = APIUtil.getResourceID(request, "profile_id");
            final Long customerID = APIUtil.getCustomerID(request);
            secLog.put((Object)"PROFILE_ID", (Object)profileID);
            secLog.put((Object)"OPERATION", (Object)"Update all associated groups and devices to latest");
            final Long loginID = APIUtil.getLoginID(request);
            final Long userID = APIUtil.getUserID(request);
            int platformTYpe = 0;
            String profileName = "";
            try {
                platformTYpe = new ProfileUtil().getPlatformType(profileID);
                profileName = (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)profileID, "PROFILE_NAME");
            }
            catch (final NullPointerException | DataAccessException e) {
                this.logger.log(Level.SEVERE, "Invalid profile ID", e);
                throw new APIHTTPException("COM0008", new Object[] { profileID });
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Invalid profile ID", e);
                throw new APIHTTPException("COM0008", new Object[] { profileID });
            }
            final HashMap profileCollectionMap = this.validateIfProfilesDistribute(Arrays.asList(profileID), customerID, platformTYpe);
            boolean updated = this.updateRecentProfileForAllDevices(profileID, customerID, userID, profileCollectionMap);
            updated = (this.updateRecentProfileForAllGroups(profileID, customerID, userID, loginID, profileCollectionMap) || updated);
            if (!updated) {
                throw new APIHTTPException("PRO0002", new Object[0]);
            }
            remarks = "associate-success";
            final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.update_all_success";
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, DMUserHandler.getUserName(loginID), sEventLogRemarks, profileName, customerID);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    private boolean updateRecentProfileForAllDevices(final Long profileID, final Long customerID, final Long userID, final HashMap profileCollectionMap) throws APIHTTPException {
        final List<Long> resourceList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria cri = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
            cri = cri.and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0));
            cri = cri.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), profileCollectionMap.get(profileID), 1));
            selectQuery.setCriteria(cri);
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator rows = dataObject.getRows("RecentProfileForResource");
            while (rows.hasNext()) {
                final Row row = rows.next();
                resourceList.add((long)row.get("RESOURCE_ID"));
            }
            if (resourceList.size() > 0) {
                final Properties properties = new Properties();
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
                ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
                ((Hashtable<String, Long>)properties).put("customerId", customerID);
                ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
                ((Hashtable<String, Boolean>)properties).put("isGroup", false);
                ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
                ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                ((Hashtable<String, Long>)properties).put("loggedOnUser", userID);
                ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
                return true;
            }
            return false;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error updating all devices", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean updateRecentProfileForAllGroups(final Long profileID, final Long customerID, final Long userID, final Long lodinID, final HashMap profileCollectionMap) throws APIHTTPException {
        final List<Long> resourceList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            selectQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria cri = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileID, 0);
            cri = cri.and(new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0));
            cri = cri.and(new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), profileCollectionMap.get(profileID), 1));
            selectQuery.setCriteria(cri);
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator rows = dataObject.getRows("RecentProfileForGroup");
            while (rows.hasNext()) {
                final Row row = rows.next();
                resourceList.add((long)row.get("GROUP_ID"));
            }
            if (resourceList.size() > 0) {
                final Properties properties = new Properties();
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
                ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
                ((Hashtable<String, Long>)properties).put("customerId", customerID);
                ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
                ((Hashtable<String, Boolean>)properties).put("isGroup", true);
                ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                ((Hashtable<String, Long>)properties).put("loggedOnUser", userID);
                ((Hashtable<String, Boolean>)properties).put("associateToDevice", false);
                ((Hashtable<String, Integer>)properties).put("groupType", 6);
                ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(lodinID));
                ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
                return true;
            }
            return false;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error updating all groups", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public ArrayList getUpdateAvailableProfiles(final Long customerID) throws Exception {
        final ArrayList profileUpdate = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria profileCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0));
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0));
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0));
        selectQuery.setCriteria(profileCriteria);
        final CaseExpression ce = new CaseExpression("update_count");
        ce.addWhen(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)Column.getColumn("RecentPubProfileToColln", "COLLECTION_ID"), 1), (Object)1);
        final Column updateCount = MDMUtil.getInstance().getCountCaseExpressionColumn(ce, 4, "update_count");
        selectQuery.addSelectColumn(updateCount);
        final Column profileID = Column.getColumn("RecentProfileForResource", "PROFILE_ID");
        final Column totalCount = profileID.count();
        totalCount.setColumnAlias("total");
        selectQuery.addSelectColumn(totalCount);
        RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final ArrayList groupByColList = new ArrayList();
        final GroupByColumn groupByColumn = new GroupByColumn(profileID, false);
        groupByColList.add(groupByColumn);
        final GroupByClause groupByClause = new GroupByClause((List)groupByColList);
        selectQuery.setGroupByClause(groupByClause);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                if (Integer.valueOf(ds.getValue("update_count").toString()) == Integer.valueOf(ds.getValue("total").toString())) {
                    profileUpdate.add(ds.getValue("PROFILE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error fetching update available profiles", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return profileUpdate;
    }
    
    public int getUpdateAvailableCount(final Long customerID) throws APIHTTPException {
        try {
            return this.getUpdateAvailableProfiles(customerID).size();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error fetching update available profile count", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void setUpdateCountMessageStatus(final Long customerID) {
        try {
            final int updateCount = this.getUpdateAvailableCount(customerID);
            if (updateCount > 0) {
                MessageProvider.getInstance().unhideMessage("PROFILE_UPDATE_COUNT", customerID);
            }
            else {
                MessageProvider.getInstance().hideMessage("PROFILE_UPDATE_COUNT", customerID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error getting the available updateCount", e);
        }
    }
    
    public SelectQuery getProfileAssociationDeviceCount(final SelectQuery selectQuery) {
        final Table profileTable = Table.getTable("Profile");
        final SelectQuery deviceSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        final Column deviceCount = Column.getColumn("RecentProfileForResource", "RESOURCE_ID").count();
        deviceCount.setColumnAlias("RESOURCE_ID");
        deviceSubQuery.addSelectColumn(deviceCount);
        deviceSubQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        deviceSubQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        deviceSubQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("Profile", "PLATFORM_TYPE"), 0));
        profileCriteria = profileCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        deviceSubQuery.setCriteria(profileCriteria);
        final List list = new ArrayList();
        final Column groupByCol = Column.getColumn("RecentProfileForResource", "PROFILE_ID");
        list.add(groupByCol);
        final GroupByClause deviceGroupBy = new GroupByClause(list);
        deviceSubQuery.setGroupByClause(deviceGroupBy);
        final DerivedTable deviceDerievedTab = new DerivedTable("DEVICE_COUNT_QUERY", (Query)deviceSubQuery);
        selectQuery.addJoin(new Join(profileTable, (Table)deviceDerievedTab, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        final Column maxCountCol = new Column("DEVICE_COUNT_QUERY", "RESOURCE_ID");
        maxCountCol.setColumnAlias("device_count");
        selectQuery.addSelectColumn(maxCountCol);
        return selectQuery;
    }
    
    public SelectQuery getProfileAssociationGroupCount(final SelectQuery selectQuery) {
        final Table profileTable = Table.getTable("Profile");
        final SelectQuery groupSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Column groupcount = Column.getColumn("RecentProfileForGroup", "GROUP_ID").count();
        groupcount.setColumnAlias("GROUP_ID");
        groupSubQuery.addSelectColumn(groupcount);
        groupSubQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        groupSubQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria groupProfileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        groupSubQuery.setCriteria(groupProfileCriteria);
        final List groupList = new ArrayList();
        final Column profilegroupByCol = Column.getColumn("RecentProfileForGroup", "PROFILE_ID");
        groupList.add(profilegroupByCol);
        final GroupByClause groupsGroupBy = new GroupByClause(groupList);
        groupSubQuery.setGroupByClause(groupsGroupBy);
        final DerivedTable groupDerievedTab = new DerivedTable("GROUP_COUNT_QUERY", (Query)groupSubQuery);
        selectQuery.addJoin(new Join(profileTable, (Table)groupDerievedTab, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        final Column groupCountCol = new Column("GROUP_COUNT_QUERY", "GROUP_ID");
        groupCountCol.setColumnAlias("group_count");
        selectQuery.addSelectColumn(groupCountCol);
        return selectQuery;
    }
    
    private void getDetailedProfile(final JSONObject profile, final Long customer_id) {
        try {
            final JSONObject requestJson = new JSONObject();
            final JSONArray profileNameArray = profile.getJSONArray("payloads");
            requestJson.put("msg_header", (Object)new JSONObject().put("filters", (Object)new JSONObject().put("customer_id", (Object)customer_id).put("include", (Object)"payloaditems")));
            requestJson.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("profile_id", (Object)profile.getString("profile_id")).put("collection_id", (Object)profile.getString("collection_id")));
            final JSONArray payloadArray = new JSONArray();
            for (int i = 0; i < profileNameArray.length(); ++i) {
                requestJson.getJSONObject("msg_header").getJSONObject("resource_identifier").put("payload_id", profileNameArray.get(i));
                final JSONObject payload = this.getPayloads(requestJson);
                payloadArray.put((Object)payload);
            }
            profile.put("payloads", (Object)payloadArray);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in getDetailedProfile()", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public long validateProfilePayloadForModification(final Long profileID, final Long configDataItemID, final Long customerID, final String payloadName) throws APIHTTPException {
        Long collectionID = 0L;
        try {
            this.validateIfProfileExists(profileID, customerID);
            collectionID = ProfileHandler.getRecentProfileCollectionID(profileID);
            final Integer configID = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionID);
            if (configDataItemID > 0L) {
                final Criteria configDataItemCriteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configID, 0);
                final Criteria joinCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), 0);
                final Row configDataRow = configDetails.getRow("ConfigData", configDataItemCriteria, new Join("ConfigData", "ConfigDataItem", joinCriteria.and(configIdCriteria), 2));
                if (configDataRow == null) {
                    throw new APIHTTPException("COM0008", new Object[] { " payloaditem_id - " + configDataItemID });
                }
            }
            else {
                final Criteria configIDCri = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configID, 0);
                final Row configDataRow2 = configDetails.getRow("ConfigData", configIDCri);
                if (configDataRow2 != null) {
                    throw new APIHTTPException("PAY0006", new Object[] { 1, payloadName });
                }
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "error in validating payload item", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "error in validating payload item", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return collectionID;
    }
    
    public SelectQuery getGroupProfilesBaseQuery(final Long groupId, final Long customerId, final boolean excludedDeleted) {
        final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupId, customerId);
        profileQuery.addJoin(new Join("RecentProfileForGroup", "CollnToResources", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        profileQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "Profile", "CREATED_USER", 2));
        profileQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "Profile", "LAST_MODIFIED_USER", 2));
        Criteria criteria = profileQuery.getCriteria();
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)this.getProfileType(), 0);
        criteria = criteria.and(profileTypeCri);
        if (excludedDeleted) {
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            criteria = criteria.and(markedForDelete);
        }
        profileQuery.setCriteria(criteria);
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "CREATED_BY"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
        profileQuery.addSelectColumn(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        profileQuery.addSelectColumn(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
        return profileQuery;
    }
    
    public JSONObject addPayloadItems(final JSONObject message) throws Exception {
        try {
            this.logger.log(Level.INFO, "Adding bulk payload items");
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userID = APIUtil.getUserID(message);
            final JSONObject msgBody = message.getJSONObject("msg_body");
            final JSONArray requestArray = msgBody.getJSONArray("payloaditems");
            final HashMap<String, Object> payloadObject = this.addPayloadItems(profileID, collectionID, customerId, userID, payloadName, requestArray, false);
            collectionID = payloadObject.get("COLLECTION_ID");
            message.getJSONObject("msg_header").getJSONObject("resource_identifier").put("collection_id", (Object)collectionID);
            message.getJSONObject("msg_header").getJSONObject("filters").put("include", (Object)"payloaditems");
            this.logger.log(Level.INFO, "Completed adding bulk payload items");
            return this.getPayloads(message);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject modifyPayloadItems(final JSONObject message) throws Exception {
        try {
            this.logger.log(Level.INFO, "Modifying bulk payload items");
            final Long profileID = APIUtil.getResourceID(message, "profile_id");
            Long collectionID = APIUtil.getResourceID(message, "collection_id");
            final String payloadName = APIUtil.getResourceIDString(message, "payload_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userID = APIUtil.getUserID(message);
            final JSONObject msgBody = message.getJSONObject("msg_body");
            final JSONArray requestArray = msgBody.getJSONArray("payloaditems");
            final HashMap<String, Object> payloadObject = this.modifyPayloadItems(profileID, collectionID, customerId, userID, payloadName, requestArray, false);
            collectionID = payloadObject.get("COLLECTION_ID");
            message.getJSONObject("msg_header").getJSONObject("resource_identifier").put("collection_id", (Object)collectionID);
            message.getJSONObject("msg_header").getJSONObject("filters").put("include", (Object)"payloaditems");
            this.logger.log(Level.INFO, "Completed modifying bulk payload items");
            return this.getPayloads(message);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public HashMap<String, Object> addPayloadItems(final Long profileId, Long collectionId, final Long customerId, final Long userId, final String payloadName, final JSONArray payloadArray, final boolean isSinglePayload) {
        final HashMap<String, Object> payloadObject = new HashMap<String, Object>();
        final List<Long> configDataItemIds = new ArrayList<Long>();
        try {
            this.validateIfProfileExists(profileId, customerId);
            final Integer configId = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (configId == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionId);
            if (!configDetails.isEmpty() && !isSinglePayload) {
                final Criteria configIdCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configId, 0);
                final int size = MDMDBUtil.getDOSize(configDetails, "ConfigData", configIdCriteria);
                if (size > 0) {
                    this.logger.log(Level.INFO, "Payload already exist so can't able to add profileId:{0} collectionId:{1} PayloadName:{2} - {3}", new Object[] { profileId, collectionId, payloadName, isSinglePayload });
                    throw new APIHTTPException("COM0010", new Object[0]);
                }
            }
            final List<Integer> supportedPlatforms = ProfileConfigurationUtil.getInstance().getSupportedPlatform(payloadName);
            final Row profileRow = configDetails.getRow("Profile");
            if (!supportedPlatforms.contains(profileRow.get("PLATFORM_TYPE"))) {
                throw new APIHTTPException("COM0008", new Object[] { payloadName + " not belongs to this platform type " + profileRow.get("PLATFORM_TYPE") });
            }
            final Row collectionStatusRow = configDetails.getRow("CollectionStatus");
            for (int i = 0; i < payloadArray.length(); ++i) {
                final JSONObject requestJSON = payloadArray.getJSONObject(i);
                if (i == 0) {
                    requestJSON.put("PROFILE_COLLECTION_STATUS", (int)collectionStatusRow.get("PROFILE_COLLECTION_STATUS"));
                }
                else {
                    requestJSON.put("PROFILE_COLLECTION_STATUS", 0);
                }
                requestJSON.put("payload_name", (Object)payloadName);
                requestJSON.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
                final JSONObject configDataItemJson = this.addOrModifyConfigDataItem(requestJSON, collectionId, customerId, userId);
                collectionId = configDataItemJson.getLong("COLLECTION_ID");
                final Long configDataItem = configDataItemJson.getLong("CONFIG_DATA_ITEM_ID");
                configDataItemIds.add(configDataItem);
            }
            payloadObject.put("COLLECTION_ID", collectionId);
            payloadObject.put("CONFIG_DATA_ITEM_ID", configDataItemIds);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return payloadObject;
    }
    
    public HashMap<String, Object> modifyPayloadItems(final Long profileId, Long collectionId, final Long customerId, final Long userId, final String payloadName, final JSONArray payloadArray, final boolean isSinglePayload) {
        final HashMap<String, Object> payloadObject = new HashMap<String, Object>();
        final List<Long> configDataItemIds = new ArrayList<Long>();
        try {
            this.validateIfProfileExists(profileId, customerId);
            final Integer configId = ProfileConfigurationUtil.getInstance().getConfigID(payloadName);
            if (configId == null) {
                throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
            }
            final DataObject configDetails = ProfileConfigHandler.getConfigDetailsForCollectionId(collectionId);
            if (configDetails.isEmpty()) {
                this.logger.log(Level.INFO, "DataObject is empty to modify the payload profileId:{0} collectionId:{1} PayloadName:{2} - {3}", new Object[] { profileId, collectionId, payloadName, isSinglePayload });
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Criteria configIdCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configId, 0);
            final int size = MDMDBUtil.getDOSize(configDetails, "ConfigData", configIdCriteria);
            if (size < 0) {
                this.logger.log(Level.INFO, "Payload is not available so can't able to modify profileId:{0} collectionId:{1} PayloadName:{2} - {3}", new Object[] { profileId, collectionId, payloadName, isSinglePayload });
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final List<Integer> supportedPlatforms = ProfileConfigurationUtil.getInstance().getSupportedPlatform(payloadName);
            final Row profileRow = configDetails.getRow("Profile");
            if (!supportedPlatforms.contains(profileRow.get("PLATFORM_TYPE"))) {
                throw new APIHTTPException("COM0008", new Object[] { payloadName + " not belongs to this platform type " + profileRow.get("PLATFORM_TYPE") });
            }
            final Row collectionStatusRow = configDetails.getRow("CollectionStatus");
            final int currentCollectionStatus = (int)collectionStatusRow.get("PROFILE_COLLECTION_STATUS");
            if (payloadArray.length() < 0) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            if (currentCollectionStatus == 110) {
                final JSONObject individualPayloadJSON = new JSONObject();
                final String configurationName = ProfileConfigurationUtil.getInstance().getConfigurationName(payloadName);
                individualPayloadJSON.put("CONFIG_NAME", (Object)configurationName);
                individualPayloadJSON.put("CURRENT_CONFIG", (Object)configurationName);
                individualPayloadJSON.put("COLLECTION_ID", (Object)collectionId);
                individualPayloadJSON.put("APP_CONFIG", false);
                individualPayloadJSON.put("CUSTOMER_ID", (Object)customerId);
                if (isSinglePayload) {
                    final JSONObject singlePayloadObject = payloadArray.getJSONObject(0);
                    individualPayloadJSON.put(configurationName, (Object)singlePayloadObject);
                }
                collectionId = ProfileConfigHandler.cloneConfigurationsOnModification(individualPayloadJSON);
                if (isSinglePayload) {
                    final JSONObject singlePayloadObject = payloadArray.getJSONObject(0);
                    singlePayloadObject.put("CONFIG_DATA_ITEM_ID", individualPayloadJSON.get("CONFIG_DATA_ITEM_ID"));
                }
            }
            if (!isSinglePayload) {
                this.logger.log(Level.INFO, "Going to delete payloads for bulk profile profileId:{0} collectionId:{1} PayloadName:{2}", new Object[] { profileId, collectionId, payloadName });
                this.deletePayloads(profileId, collectionId, customerId, payloadName);
            }
            for (int i = 0; i < payloadArray.length(); ++i) {
                final JSONObject requestJSON = payloadArray.getJSONObject(i);
                requestJSON.put("payload_name", (Object)payloadName);
                requestJSON.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
                requestJSON.put("PROFILE_COLLECTION_STATUS", 0);
                final JSONObject configDataItemJson = this.addOrModifyConfigDataItem(requestJSON, collectionId, customerId, userId);
                collectionId = configDataItemJson.getLong("COLLECTION_ID");
                configDataItemIds.add(configDataItemJson.getLong("CONFIG_DATA_ITEM_ID"));
            }
            payloadObject.put("COLLECTION_ID", collectionId);
            payloadObject.put("CONFIG_DATA_ITEM_ID", configDataItemIds);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return payloadObject;
    }
}
