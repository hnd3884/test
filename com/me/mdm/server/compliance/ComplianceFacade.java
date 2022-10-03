package com.me.mdm.server.compliance;

import com.me.mdm.server.compliance.dbutil.ActionEngineDBUtil;
import com.me.mdm.server.compliance.dbutil.RuleEngineDBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.List;
import java.util.Iterator;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.role.RBDAUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import java.util.Collections;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.profiles.ProfileFacade;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import javax.transaction.SystemException;
import com.me.mdm.server.profiles.ProfileException;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ComplianceFacade
{
    private Logger logger;
    
    public ComplianceFacade() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public JSONObject getComplianceProfile(final JSONObject messageJSON) throws APIHTTPException {
        try {
            final Long profileId = APIUtil.getResourceID(messageJSON, "complianc_id");
            JSONObject requestJSON = new JSONObject();
            requestJSON.put("compliance_id", (Object)profileId);
            requestJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            this.checkComplianceProfileExistence(requestJSON);
            requestJSON = ComplianceDBUtil.getInstance().getComplianceProfile(requestJSON);
            return requestJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getComplianceProfile() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getComplianceProfile() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject removeComplianceProfile(final JSONObject messageJSON) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONObject requestJSON = new JSONObject();
            final Long userId = APIUtil.getUserID(messageJSON);
            Long profileId = APIUtil.getResourceID(messageJSON, "complianc_id");
            final Long customerId = APIUtil.getCustomerID(messageJSON);
            final String userName = APIUtil.getUserName(messageJSON);
            JSONArray complianceList = new JSONArray();
            if (profileId != -1L) {
                requestJSON.put("compliance_id", (Object)profileId);
                requestJSON.put("customer_id", (Object)customerId);
                this.checkComplianceProfileExistence(requestJSON);
                complianceList.put((Object)profileId);
            }
            else {
                complianceList = messageJSON.getJSONObject("msg_body").getJSONArray("compliance_ids");
                for (int i = 0; i < complianceList.length(); ++i) {
                    profileId = JSONUtil.optLongForUVH(complianceList, i, -1L);
                    requestJSON.put("compliance_id", (Object)profileId);
                    requestJSON.put("customer_id", (Object)customerId);
                    this.checkComplianceProfileExistence(requestJSON);
                }
            }
            secLog.put((Object)"COMPLIANCE_IDs", (Object)complianceList);
            if (!ProfileUtil.getInstance().isCustomerEligible(customerId, profileId)) {
                this.logger.log(Level.SEVERE, " -- removeComplianceProfile() >   Customer Id is invalid for profile");
                throw new ProfileException();
            }
            final JSONObject profileIdJSON = new JSONObject();
            requestJSON.put("profile_list", (Object)complianceList);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            ComplianceDBUtil.getInstance().removeComplianceProfile(requestJSON);
            profileIdJSON.put("compliance_id", (Object)profileId);
            profileIdJSON.put("status", (Object)"success");
            MDMUtil.getUserTransaction().commit();
            remarks = "delete-success";
            return profileIdJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removeComplianceProfile() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removeComplianceProfile() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_COMPLIANCE", secLog);
        }
    }
    
    public JSONObject addComplianceProfile(final JSONObject messageJSON) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            MDMUtil.getUserTransaction().begin();
            if (!messageJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = messageJSON.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            requestJSON.put("user_id", (Object)APIUtil.getUserID(messageJSON));
            requestJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            this.prerequisiteCheck(requestJSON);
            final JSONObject profileJSON = ComplianceDBUtil.getInstance().addOrUpdateComplianceProfile(requestJSON);
            profileJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            final JSONObject responseJSON = ComplianceDBUtil.getInstance().getComplianceProfile(profileJSON);
            secLog.put((Object)"COMPLIANCE_ID", responseJSON.opt("compliance_id"));
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72401);
            eventLogJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            eventLogJSON.put("remarks", (Object)"mdm.compliance.created");
            eventLogJSON.put("remarks_args", (Object)(responseJSON.get("compliance_name") + "@@@" + responseJSON.get("created_by_name")));
            eventLogJSON.put("user_name", (Object)APIUtil.getUserName(messageJSON));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            MDMUtil.getUserTransaction().commit();
            final JSONObject publishProfileJSON = new JSONObject();
            publishProfileJSON.put("compliance_id", responseJSON.get("compliance_id"));
            publishProfileJSON.put("collection_id", responseJSON.get("collection_id"));
            publishProfileJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            publishProfileJSON.put("user_id", (Object)APIUtil.getUserID(messageJSON));
            publishProfileJSON.put("compliance_name", responseJSON.get("compliance_name"));
            publishProfileJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(APIUtil.getUserID(messageJSON)));
            ComplianceHandler.getInstance().publishComplianceProfile(publishProfileJSON);
            final Long profileCount = ComplianceDBUtil.getInstance().getComplianceProfileCount(APIUtil.getCustomerID(messageJSON));
            if (profileCount == 1L) {
                final MDMAgentSettingsHandler agentHandler = new MDMAgentSettingsHandler();
                final JSONObject iosData = new JSONObject();
                iosData.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(messageJSON));
                iosData.put("IS_NATIVE_APP_ENABLE", true);
                iosData.put("CORPORATE_WIPE_ROOTED_DEVICES", false);
                iosData.put("UPDATED_BY", (Object)APIUtil.getUserID(messageJSON));
                agentHandler.processiOSSettings(iosData);
            }
            remarks = "create-success";
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "error in addComplianceProfile()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in addComplianceProfile()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_COMPLIANCE", secLog);
        }
    }
    
    private void prerequisiteCheck(final JSONObject requestJSON) throws Exception {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final String request = requestJSON.toString();
        if (request.contains("alert_email_ids") && request.contains("body_message") && request.contains("subject")) {
            final Boolean isMailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
            if (!isMailServerConfigured) {
                throw new APIHTTPException("MAS001", new Object[0]);
            }
        }
    }
    
    public JSONObject modifyComplianceProfile(final JSONObject messageJSON) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            JSONObject responseJSON = new JSONObject();
            final Long profileId = APIUtil.getResourceID(messageJSON, "complianc_id");
            secLog.put((Object)"COMPLIANCE_ID", (Object)profileId);
            JSONObject requestJSON = new JSONObject();
            requestJSON.put("compliance_id", (Object)profileId);
            MDMUtil.getUserTransaction().begin();
            final Long customerId = APIUtil.getCustomerID(messageJSON);
            requestJSON.put("customer_id", (Object)customerId);
            this.checkComplianceProfileExistence(requestJSON);
            requestJSON = messageJSON.getJSONObject("msg_body");
            requestJSON.put("compliance_id", (Object)profileId);
            requestJSON.put("user_id", (Object)APIUtil.getUserID(messageJSON));
            requestJSON.put("customer_id", (Object)customerId);
            this.prerequisiteCheck(requestJSON);
            responseJSON = ComplianceDBUtil.getInstance().addOrUpdateComplianceProfile(requestJSON);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72402);
            eventLogJSON.put("customer_id", (Object)APIUtil.getCustomerID(messageJSON));
            eventLogJSON.put("remarks", (Object)"mdm.compliance.updated");
            eventLogJSON.put("remarks_args", (Object)(responseJSON.get("compliance_name") + "@@@" + responseJSON.get("last_modified_by_name")));
            eventLogJSON.put("user_name", (Object)APIUtil.getUserName(messageJSON));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            final JSONObject profileJSON = new JSONObject();
            profileJSON.put("customer_id", (Object)customerId);
            profileJSON.put("compliance_id", (Object)profileId);
            MDMUtil.getUserTransaction().commit();
            final JSONObject publishProfileJSON = new JSONObject();
            publishProfileJSON.put("compliance_id", (Object)profileId);
            publishProfileJSON.put("collection_id", (Object)ProfileHandler.getRecentProfileCollectionID(profileId));
            publishProfileJSON.put("customer_id", (Object)customerId);
            publishProfileJSON.put("user_id", (Object)APIUtil.getUserID(messageJSON));
            publishProfileJSON.put("compliance_name", responseJSON.get("compliance_name"));
            publishProfileJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(APIUtil.getUserID(messageJSON)));
            ComplianceHandler.getInstance().publishComplianceProfile(publishProfileJSON);
            remarks = "update-success";
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "error in modifyComplianceProfile()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in modifyComplianceProfile()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "MODIFY_COMPLIANCE", secLog);
        }
    }
    
    private void checkComplianceProfileExistence(final JSONObject requestJSON) throws Exception {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final Long profileId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
        final ProfileFacade profileFacade = new ProfileFacade();
        profileFacade.validateIfProfileExists(profileId, customerId);
        this.checkComplianceTrashStatus(profileId);
    }
    
    private void checkComplianceTrashStatus(final Long profileId) throws Exception {
        try {
            final SelectQuery profileQuery = this.getComplianceQuery();
            profileQuery.setCriteria(new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
            profileQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            profileQuery.addSelectColumn(new Column("Profile", "IS_MOVED_TO_TRASH"));
            final DataObject dataObject = MDMUtil.getPersistence().get(profileQuery);
            final Boolean isMovedToTrash = (Boolean)dataObject.getRow("Profile").get("IS_MOVED_TO_TRASH");
            if (isMovedToTrash) {
                final String remarks = I18N.getMsg("mdm.compliance.policy_trashed", new Object[] { "1" });
                throw new APIHTTPException("COM0008", new Object[] { remarks });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- checkComplianceTrashStatus()  >   Error ", e);
            throw e;
        }
    }
    
    public void associateOrDisassociateComplianceToDevices(final JSONObject requestJSON) throws APIHTTPException, SystemException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isOperationSuccess = false;
        boolean isAssociate = false;
        try {
            MDMUtil.getUserTransaction().begin();
            final Long profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
            secLog.put((Object)"COMPLIANCE_ID", (Object)profileId);
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            JSONObject complianceJSON = new JSONObject();
            complianceJSON.put("compliance_id", (Object)profileId);
            complianceJSON.put("customer_id", (Object)customerId);
            this.checkComplianceProfileExistence(complianceJSON);
            complianceJSON.put("profile_id", (Object)profileId);
            complianceJSON.put("user_name", (Object)userName);
            complianceJSON.put("user_id", (Object)userId);
            complianceJSON = ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(complianceJSON);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final Long deviceId = APIUtil.getResourceID(requestJSON, "device_id");
            JSONArray resourceList = new JSONArray();
            if (deviceId != -1L) {
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(requestJSON));
                resourceList.put((Object)deviceId);
            }
            else {
                resourceList = requestJSON.getJSONObject("msg_body").getJSONArray("device_ids");
                new DeviceFacade().validateIfDevicesExists(JSONUtil.getInstance().convertLongJSONArrayTOList(resourceList), customerId);
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            complianceJSON.put("user_id", (Object)APIUtil.getUserID(requestJSON));
            complianceJSON.put("resource_list", (Object)resourceList);
            complianceJSON.put("collection_id", (Object)collectionId);
            final String distributionKey = String.valueOf(requestJSON.get("distribution_key"));
            if (distributionKey.equalsIgnoreCase("associate")) {
                isAssociate = true;
                ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
            }
            else if (distributionKey.equalsIgnoreCase("disassociate")) {
                ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(complianceJSON);
            }
            MDMUtil.getUserTransaction().commit();
            isOperationSuccess = true;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "associateOrDisassociateComplianceToDevices()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "associateOrDisassociateComplianceToDevices()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operationName = isAssociate ? "ASSOCIATE_COMPLIANCE" : "DISSOCIATE_COMPLIANCE";
            final String remarks = isAssociate ? (isOperationSuccess ? "associate-success" : "associate-failed") : (isOperationSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operationName, secLog);
        }
    }
    
    public void associateOrDisassociateComplianceToDeviceGroups(final JSONObject requestJSON) throws APIHTTPException, SystemException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isOperationSuccess = false;
        boolean isAssociate = false;
        try {
            MDMUtil.getUserTransaction().begin();
            final Long profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
            secLog.put((Object)"COMPLIANCE_ID", (Object)profileId);
            JSONObject complianceJSON = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            complianceJSON.put("compliance_id", (Object)profileId);
            complianceJSON.put("customer_id", (Object)APIUtil.getCustomerID(requestJSON));
            complianceJSON.put("user_name", (Object)userName);
            complianceJSON.put("user_id", (Object)userId);
            complianceJSON.put("customer_id", (Object)customerId);
            this.checkComplianceProfileExistence(complianceJSON);
            complianceJSON.put("profile_id", (Object)profileId);
            complianceJSON = ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(complianceJSON);
            JSONArray groupJSONArray = new JSONArray();
            final Long groupId = APIUtil.getResourceID(requestJSON, "group_id");
            if (groupId != -1L) {
                new GroupFacade().validateIfGroupsExists(Collections.singleton(groupId), customerId);
                groupJSONArray.put((Object)groupId);
            }
            else {
                groupJSONArray = requestJSON.getJSONObject("msg_body").getJSONArray("group_ids");
                new GroupFacade().validateIfGroupsExists(JSONUtil.getInstance().convertLongJSONArrayTOList(groupJSONArray), customerId);
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupJSONArray);
            complianceJSON.put("resource_list", (Object)groupJSONArray);
            complianceJSON.put("user_id", (Object)APIUtil.getUserID(requestJSON));
            final String distributionKey = String.valueOf(requestJSON.get("distribution_key"));
            if (distributionKey.equalsIgnoreCase("associate")) {
                isAssociate = true;
                ComplianceDistributionHandler.getInstance().associateComplianceToGroups(complianceJSON);
            }
            else if (distributionKey.equalsIgnoreCase("disassociate")) {
                ComplianceDistributionHandler.getInstance().disassociateComplianceToGroups(complianceJSON);
            }
            MDMUtil.getUserTransaction().commit();
            isOperationSuccess = true;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "associateOrDisassociateComplianceToDeviceGroups()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "associateOrDisassociateComplianceToDeviceGroups()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operationName = isAssociate ? "ASSOCIATE_COMPLIANCE" : "DISSOCIATE_COMPLIANCE";
            final String remarks = isAssociate ? (isOperationSuccess ? "associate-success" : "associate-failed") : (isOperationSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operationName, secLog);
        }
    }
    
    public JSONObject getComplianceToDevicesDistributionDetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
            final String platform = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("platform", "--");
            final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
            final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            Long profileId = -1L;
            if (requestJSON.has("msg_header")) {
                profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
                requestJSON.put("customer_id", (Object)APIUtil.getCustomerID(requestJSON));
                requestJSON.put("compliance_id", (Object)profileId);
                requestJSON.put("filters", (Object)APIUtil.getStringFilter(requestJSON, "compliance_state"));
            }
            else {
                profileId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
            }
            this.checkComplianceProfileExistence(requestJSON);
            final JSONObject complianceJSON = ComplianceDBUtil.getInstance().getComplianceProfile(requestJSON);
            final String complianceName = String.valueOf(complianceJSON.get("compliance_name"));
            JSONObject summaryJSON = new JSONObject();
            final Table recentProfile = new Table("RecentProfileForResource");
            final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            SelectQuery deviceProfileQuery = (SelectQuery)new SelectQueryImpl(recentProfile);
            deviceProfileQuery.addJoin(managedDeviceJoin);
            final Join managedDeviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            deviceProfileQuery.addJoin(managedDeviceExtnJoin);
            deviceProfileQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria removeCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            deviceProfileQuery.setCriteria(profileCriteria.and(removeCriteria).and(managedDeviceCriteria));
            SelectQuery countQuery = (SelectQuery)deviceProfileQuery.clone();
            final ArrayList<Column> selectColumnsList = (ArrayList<Column>)deviceProfileQuery.getSelectColumns();
            for (final Column selectColumn : selectColumnsList) {
                countQuery.removeSelectColumn(selectColumn);
            }
            Column countColumn = new Column("ManagedDevice", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countQuery.addSelectColumn(countColumn);
            if (!platform.equalsIgnoreCase("--")) {
                final String[] filters = platform.split(",");
                final List platFormList = new ArrayList();
                for (final String filterKey : filters) {
                    final int platformType = DeviceFacade.getPlatformType(filterKey);
                    platFormList.add(platformType);
                }
                Criteria platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
                platformTypeCriteria = deviceProfileQuery.getCriteria().and(platformTypeCriteria);
                deviceProfileQuery.setCriteria(platformTypeCriteria);
                platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
                platformTypeCriteria = countQuery.getCriteria().and(platformTypeCriteria);
                countQuery.setCriteria(platformTypeCriteria);
            }
            if (search != null) {
                final Criteria searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)search, 12, false);
                Criteria criteria = deviceProfileQuery.getCriteria().and(searchCriteria);
                deviceProfileQuery.setCriteria(criteria);
                criteria = countQuery.getCriteria().and(searchCriteria);
                countQuery.setCriteria(criteria);
            }
            countQuery = RBDAUtil.getInstance().getRBDAQuery(countQuery);
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            summaryJSON.put("metadata", (Object)meta);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    summaryJSON.put("paging", (Object)pagingJSON);
                }
                if (!selectAll) {
                    deviceProfileQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                    if (orderByJSON != null && orderByJSON.has("orderby")) {
                        final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                        if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                            deviceProfileQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", (boolean)isSortOrderASC));
                        }
                    }
                    else {
                        deviceProfileQuery.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
                    }
                }
                deviceProfileQuery = RBDAUtil.getInstance().getRBDAQuery(deviceProfileQuery);
                final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(deviceProfileQuery);
                final JSONArray deviceJSONArray = new JSONArray();
                for (int i = 0; i < resultJSONArray.size(); ++i) {
                    final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                    deviceJSONArray.put(tempJSON.get((Object)"RESOURCE_ID"));
                }
                summaryJSON.put("device_ids", (Object)deviceJSONArray);
                summaryJSON.put("compliance_id", (Object)profileId);
                if (requestJSON.has("filters")) {
                    summaryJSON.put("filters", (Object)String.valueOf(requestJSON.get("filters")));
                }
                summaryJSON.put("collection_id", (Object)JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L)));
                summaryJSON.put("compliance_name", (Object)complianceName);
                summaryJSON.put("compliance_profile", (Object)complianceJSON);
                summaryJSON = ComplianceStatusUpdateDataHandler.getInstance().getAllDeviceComplianceSummary(summaryJSON);
            }
            else {
                summaryJSON.put("devices", (Object)new JSONArray());
            }
            return summaryJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "getComplianceToDevicesDistributionDetails()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "getComplianceToDevicesDistributionDetails()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getComplianceToGroupsDistributionDetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
            final String groupTypeString = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("group_type", (String)null);
            final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
            final Integer listType = APIUtil.getIntegerFilter(requestJSON, "list_type");
            final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            final Long profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
            requestJSON.put("compliance_id", (Object)profileId);
            requestJSON.put("customer_id", (Object)APIUtil.getCustomerID(requestJSON));
            this.checkComplianceProfileExistence(requestJSON);
            final Table recentProfile = new Table("RecentProfileForGroup");
            SelectQuery groupProfileQuery = (SelectQuery)new SelectQueryImpl(recentProfile);
            final JSONObject responseJSON = new JSONObject();
            final JSONArray groupJSONArray = new JSONArray();
            final Table subTable = Table.getTable("CustomGroup", "GROUP");
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(subTable);
            final Join groupMemberJoin = new Join(subTable, Table.getTable("CustomGroupMemberRel"), new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
            subQuery.addJoin(groupMemberJoin);
            subQuery.addSelectColumn(Column.getColumn("GROUP", "RESOURCE_ID"));
            final Column groupMemberCountColumn = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").count();
            groupMemberCountColumn.setColumnAlias("COUNT");
            subQuery.addSelectColumn(groupMemberCountColumn);
            final List columns = new ArrayList();
            columns.add(Column.getColumn("GROUP", "RESOURCE_ID"));
            final GroupByClause groupByClause = new GroupByClause(columns);
            subQuery.setGroupByClause(groupByClause);
            final DerivedTable derivedTable = new DerivedTable("GROUP", (Query)subQuery);
            groupProfileQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Table baseTable = Table.getTable("CustomGroup");
            final Join derivedTableJoin = new Join(baseTable, (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            groupProfileQuery.addJoin(derivedTableJoin);
            groupProfileQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupProfileQuery.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
            groupProfileQuery.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
            groupProfileQuery.addSelectColumn(new Column("CustomGroup", "RESOURCE_ID"));
            groupProfileQuery.addSelectColumn(new Column("CustomGroup", "GROUP_TYPE"));
            groupProfileQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
            groupProfileQuery.addSelectColumn(new Column("Resource", "NAME"));
            groupProfileQuery.addSelectColumn(new Column("Resource", "DOMAIN_NETBIOS_NAME"));
            groupProfileQuery.addSelectColumn(Column.getColumn("GROUP", "COUNT"));
            groupProfileQuery.setCriteria(new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0));
            groupProfileQuery = RBDAUtil.getInstance().getRBDAQuery(groupProfileQuery);
            final SelectQuery countQuery = (SelectQuery)groupProfileQuery.clone();
            final ArrayList<Column> selectColumnsList = (ArrayList<Column>)countQuery.getSelectColumns();
            for (final Column selectColumn : selectColumnsList) {
                countQuery.removeSelectColumn(selectColumn);
            }
            Column countColumn = new Column("CustomGroup", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countQuery.addSelectColumn(countColumn);
            if (!MDMUtil.isStringEmpty(groupTypeString)) {
                final String[] groupTypeStringArray = groupTypeString.split(",");
                final int[] groupTypeArray = new int[groupTypeStringArray.length];
                for (int i = 0; i < groupTypeStringArray.length; ++i) {
                    final int groupType = GroupFacade.getGroupType(groupTypeStringArray[i]);
                    groupTypeArray[i] = groupType;
                }
                Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeArray, 8);
                groupTypeCriteria = groupProfileQuery.getCriteria().and(groupTypeCriteria);
                groupProfileQuery.setCriteria(groupTypeCriteria);
                groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeArray, 8);
                groupTypeCriteria = countQuery.getCriteria().and(groupTypeCriteria);
                countQuery.setCriteria(groupTypeCriteria);
            }
            if (search != null) {
                final Criteria searchCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false);
                Criteria criteria = groupProfileQuery.getCriteria().and(searchCriteria);
                groupProfileQuery.setCriteria(criteria);
                criteria = countQuery.getCriteria().and(searchCriteria);
                countQuery.setCriteria(criteria);
            }
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            responseJSON.put("metadata", (Object)meta);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
                if (!selectAll) {
                    groupProfileQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                    if (orderByJSON != null && orderByJSON.has("orderby")) {
                        final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                        if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("groupname")) {
                            groupProfileQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                        }
                    }
                    else {
                        groupProfileQuery.addSortColumn(new SortColumn("CustomGroup", "RESOURCE_ID", true));
                    }
                }
                final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(groupProfileQuery);
                for (int j = 0; j < resultJSONArray.size(); ++j) {
                    final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(j);
                    groupJSONArray.put((Object)GroupFacade.getGroupJSON(tempJSON));
                }
            }
            responseJSON.put("group_list", (Object)groupJSONArray);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "getComplianceToGroupsDistributionDetails()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "getComplianceToGroupsDistributionDetails()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getComplianceProfiles(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final String filter = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("compliance_state", "--");
            final SelectQuery profileQuery = this.getComplianceQuery();
            this.addSelectColumns(profileQuery);
            Criteria filterCriteria = null;
            final Criteria profileCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0);
            final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            Criteria selectCriteria = profileCriteria.and(customerCriteria);
            if (!filter.equalsIgnoreCase("--")) {
                final String[] filters = filter.split(",");
                final List filterList = new ArrayList();
                for (final String s : filters) {
                    final String filterKey = s;
                    switch (s) {
                        case "compliant": {
                            filterList.add(902);
                            filterList.add(904);
                            break;
                        }
                        case "non_compliant": {
                            filterList.add(903);
                            break;
                        }
                        case "in_progress": {
                            filterList.add(901);
                            filterList.add(905);
                            break;
                        }
                    }
                }
                filterCriteria = new Criteria(new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATE"), (Object)filterList.toArray(), 8);
                selectCriteria = selectCriteria.and(filterCriteria);
            }
            this.setComplianceQueryCriteria(profileQuery, selectCriteria);
            final DataObject profileDO = MDMUtil.getPersistence().get(profileQuery);
            final Iterator profileIterator = profileDO.getRows("Profile");
            final Iterator collectionIterator = profileDO.getRows("ProfileToCollection");
            final Iterator collectionStatusIterator = profileDO.getRows("CollectionStatus");
            final JSONArray complianceJSONArray = new JSONArray();
            while (profileIterator.hasNext()) {
                final Row profileRow = profileIterator.next();
                final Row collectionRow = collectionIterator.next();
                final Row collectionStatusRow = collectionStatusIterator.next();
                final JSONObject complianceJSON = new JSONObject();
                final Long createdBy = (Long)profileRow.get("CREATED_BY");
                final Long modifiedBy = (Long)profileRow.get("LAST_MODIFIED_BY");
                final Long profileId = (Long)profileRow.get("PROFILE_ID");
                final Long collectionId = (Long)collectionRow.get("COLLECTION_ID");
                JSONObject summaryJSON = new JSONObject();
                summaryJSON.put("customer_id", (Object)customerId);
                summaryJSON.put("compliance_id", (Object)profileId);
                summaryJSON.put("collection_id", (Object)collectionId);
                summaryJSON = ComplianceDBUtil.getInstance().getComplianceSummary(summaryJSON);
                complianceJSON.put("compliance_id", (Object)profileId);
                complianceJSON.put("collection_id", (Object)collectionId);
                complianceJSON.put("compliance_name", profileRow.get("PROFILE_NAME"));
                complianceJSON.put("platform_type", profileRow.get("PLATFORM_TYPE"));
                complianceJSON.put("created_by", (Object)createdBy);
                complianceJSON.put("creation_time", profileRow.get("CREATION_TIME"));
                complianceJSON.put("last_modified_by", (Object)modifiedBy);
                complianceJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(modifiedBy));
                complianceJSON.put("created_by_name", (Object)DMUserHandler.getUserNameFromUserID(createdBy));
                complianceJSON.put("last_modified_time", profileRow.get("LAST_MODIFIED_TIME"));
                complianceJSON.put("profile_collection_status", collectionStatusRow.get("PROFILE_COLLECTION_STATUS"));
                complianceJSON.put("notification_sent_count", summaryJSON.getInt("notification_sent_count"));
                complianceJSON.put("not_applicable_count", summaryJSON.getInt("not_applicable_count"));
                complianceJSON.put("yet_to_evaluate_count", summaryJSON.getInt("yet_to_evaluate_count"));
                complianceJSON.put("non_compliant_devices_count", summaryJSON.getInt("non_compliant_devices_count"));
                complianceJSON.put("compliant_devices_count", summaryJSON.getInt("compliant_devices_count"));
                complianceJSON.put("total_count", summaryJSON.getInt("total_count"));
                complianceJSONArray.put((Object)complianceJSON);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("compliance", (Object)complianceJSONArray);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "getComplianceProfiles()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "getComplianceProfiles()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addSelectColumns(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "CREATED_BY"));
        selectQuery.addSelectColumn(new Column("Profile", "LAST_MODIFIED_BY"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
        selectQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("Profile", "CREATION_TIME"));
        selectQuery.addSelectColumn(new Column("Profile", "LAST_MODIFIED_TIME"));
        selectQuery.addSelectColumn(new Column("Profile", "IS_MOVED_TO_TRASH"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("CollectionStatus", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
        selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
        selectQuery.addSelectColumn(new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATUS_ID"));
    }
    
    private void setComplianceQueryCriteria(final SelectQuery selectQuery, final Criteria criteria) {
        Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
        if (criteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(criteria);
        }
        selectQuery.setCriteria(profileTypeCriteria);
    }
    
    private SelectQuery getComplianceQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "ComplianceToResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("ComplianceToResource", "ComplianceToDeviceStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 1));
        return selectQuery;
    }
    
    public boolean checkComplianceNameExists(final Long customerId, final String complianceName) {
        boolean isExist = false;
        this.logger.log(Level.INFO, "*** checkComplianceNameExists inputs: CustomerId:{0}; Profilename:{1}", new Object[] { customerId, complianceName });
        if (customerId != null && complianceName != null && !complianceName.isEmpty()) {
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria complianceNameCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)complianceName, 2, (boolean)Boolean.FALSE);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
            final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0);
            final Criteria criteria = customerCriteria.and(complianceNameCriteria).and(profileTypeCriteria).and(trashCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            final Join customerToProfJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(customerToProfJoin);
            selectQuery.setCriteria(criteria);
            try {
                final DataObject profNameDO = MDMUtil.getPersistence().get(selectQuery);
                if (profNameDO != null && !profNameDO.isEmpty()) {
                    isExist = true;
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while checkComplianceNameExists {0}", ex);
            }
            this.logger.log(Level.INFO, "**checkComplianceNameExists ** IS PROFILE NAME EXIST: {0}", isExist);
        }
        return isExist;
    }
    
    public String getComplianceNameForComplianceId(final Long customerId, final Long complianceId) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria complianceIdCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)complianceId, 0);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
            final Criteria criteria = customerCriteria.and(complianceIdCriteria).and(profileTypeCriteria);
            final Join customerToProfJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(customerToProfJoin);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            selectQuery.setCriteria(criteria);
            final DataObject profNameDO = MDMUtil.getPersistence().get(selectQuery);
            return (String)profNameDO.getRow("Profile").get("PROFILE_NAME");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceNameForComplianceId() >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getComplianceRulesDistributionDetails(JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            requestJSON.put("compliance_id", (Object)profileId);
            requestJSON.put("customer_id", (Object)customerId);
            this.checkComplianceProfileExistence(requestJSON);
            requestJSON = ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(requestJSON);
            JSONObject summaryJSON = new JSONObject();
            summaryJSON.put("collection_id", (Object)JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L)));
            summaryJSON = ComplianceDBUtil.getInstance().getComplianceRulesSummary(summaryJSON);
            summaryJSON.put("compliance_id", (Object)profileId);
            summaryJSON.put("compliance_name", (Object)this.getComplianceNameForComplianceId(customerId, profileId));
            return summaryJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "getComplianceRulesDistributionDetails()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "getComplianceRulesDistributionDetails()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getRulePolicyDetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long profileId = APIUtil.getResourceID(requestJSON, "complianc_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long ruleId = APIUtil.getResourceID(requestJSON, "rule_id");
            JSONObject ruleSummaryJSON = new JSONObject();
            ruleSummaryJSON.put("compliance_id", (Object)profileId);
            ruleSummaryJSON.put("customer_id", (Object)customerId);
            ruleSummaryJSON.put("rule_id", (Object)ruleId);
            this.checkComplianceProfileExistence(ruleSummaryJSON);
            ruleSummaryJSON = ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(ruleSummaryJSON);
            JSONObject ruleJSON = new JSONObject();
            ruleJSON.put("rule_id", (Object)ruleId);
            ruleJSON.put("customer_id", (Object)customerId);
            ruleJSON = RuleEngineDBUtil.getInstance().getRule(ruleJSON, 1);
            final JSONObject actionJSON = ActionEngineDBUtil.getInstance().getAction(ruleJSON);
            ruleSummaryJSON.put("compliance_name", (Object)this.getComplianceNameForComplianceId(customerId, profileId));
            ruleSummaryJSON.remove("rule_id");
            ruleSummaryJSON.remove("customer_id");
            ruleJSON.remove("customer_id");
            ruleSummaryJSON.put("rule", (Object)ruleJSON);
            ruleSummaryJSON.put("action", (Object)actionJSON);
            return ruleSummaryJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "getComplianceRulesDistributionDetails()...", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "getComplianceRulesDistributionDetails()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
