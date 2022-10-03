package com.me.mdm.server.customgroup;

import java.util.Hashtable;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.UnionQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.user.ManagedUserFacade;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.apps.AppStatusRefreshHandler;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.profiles.ProfileFacade;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import java.util.Set;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SortColumn;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.command.schedule.GroupActionToCollectionHandler;
import java.util.HashSet;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.mics.MICSGroupFeatureController;
import java.util.Collection;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.api.paging.PagingUtil;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.group.MDMGroupDeviceProfileAppHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.net.URLDecoder;
import com.me.idps.core.util.DirectoryAttributeConstants;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.role.RBDAUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Arrays;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class GroupFacade
{
    protected static Logger logger;
    protected static List<Integer> allMDMGroupTypes;
    
    public JSONObject getGroup(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "group_id", (Long)null);
            final String include = APIUtil.optStringFilter(message, "include", "");
            if (!include.isEmpty()) {
                final List<String> includeValues = Arrays.asList("summary", "member", "memberdetails", "available");
                if (!includeValues.contains(include)) {
                    throw new APIHTTPException("COM0008", new Object[] { "include :" + include });
                }
            }
            GroupFacade.logger.log(Level.INFO, "get group group id: {0} and include : {1}", new Object[] { groupId, include });
            final JSONObject groupMap = this.validateAndGetGroupDetails(groupId, APIUtil.getCustomerID(message));
            if (groupMap == null) {
                throw new APIHTTPException("COM0008", new Object[] { "group_id :" + groupId });
            }
            final Integer groupType = Integer.valueOf((String)groupMap.get("GROUP_TYPE"));
            Long createdLogInId = null;
            if (groupMap.get("LOGIN_ID") != null && !groupMap.get("LOGIN_ID").equals("null")) {
                createdLogInId = Long.parseLong(String.valueOf(groupMap.get("LOGIN_ID")));
            }
            final Boolean isEdiatble = groupMap.getBoolean("IS_EDITABLE");
            String groupName = String.valueOf(groupMap.get("NAME"));
            final Long currentLoginId = APIUtil.getLoginID(message);
            final Boolean createdAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdLogInId, true);
            final Boolean currentAdmin = APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(message, true);
            Boolean addDelAction = false;
            Boolean addModAction = false;
            if (String.valueOf(groupMap.get("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                final boolean hasSettingsWritePrivillage = DMUserHandler.isUserInRole(currentLoginId, "MDM_Settings_Write") || DMUserHandler.isUserInRole(currentLoginId, "ModernMgmt_Settings_Write");
                final boolean hasCAWritePrivillage = DMUserHandler.isUserInRole(currentLoginId, "CA_Write");
                final boolean hasGroupMgmtWritePrivillage = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_GroupMgmt_Write" }) || APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "ModernMgmt_MDMGroupMgmt_Write" });
                final boolean hasGroupMgmtAdminPrivillage = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_GroupMgmt_Admin" }) || APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "ModernMgmt_MDMGroupMgmt_Admin" });
                if (hasGroupMgmtAdminPrivillage && ((createdLogInId == null && currentAdmin) || currentLoginId.equals(createdLogInId) || (createdAdmin && currentAdmin && !MDMGroupHandler.getInstance().isDefaultGroup(groupName)))) {
                    if (hasSettingsWritePrivillage || hasCAWritePrivillage || hasGroupMgmtWritePrivillage) {
                        addDelAction = true;
                        addModAction = true;
                    }
                }
                else if (hasGroupMgmtWritePrivillage && ((createdLogInId == null && currentAdmin) || (currentLoginId.equals(createdLogInId) && !MDMGroupHandler.getInstance().isDefaultGroup(groupName)))) {
                    addDelAction = true;
                    addModAction = true;
                }
            }
            else {
                final Properties groupObjProps = DirectoryUtil.getInstance().getObjectAttributes(new Criteria(Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"), (Object)groupId, 0));
                final Object status = ((Hashtable<K, Object>)groupObjProps).get(DirectoryAttributeConstants.getAttrKey(Long.valueOf(118L)));
                if (status != null) {
                    final int statusint = (int)status;
                    if (statusint == 2 || statusint == 5 || (isEdiatble && (currentLoginId.equals(createdLogInId) || currentAdmin) && !MDMGroupHandler.getInstance().isDefaultGroup(groupName))) {
                        groupName = URLDecoder.decode(groupName, "UTF-8");
                        groupName = SyMUtil.getInstance().decodeURIComponentEquivalent(groupName);
                        addDelAction = true;
                    }
                }
            }
            final JSONObject json = new JSONObject();
            json.put("name", (Object)groupName);
            json.put("group_id", (Object)String.valueOf(groupMap.get("RESOURCE_ID")));
            json.put("group_type", (Object)String.valueOf(groupMap.get("GROUP_TYPE")));
            json.put("group_category", (Object)String.valueOf(groupMap.get("GROUP_CATEGORY")));
            json.put("is_deletable", (Object)addDelAction);
            json.put("is_editable", (Object)addModAction);
            json.put("created_time", groupMap.getLong("DB_ADDED_TIME"));
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                json.put("updated_time", groupMap.getLong("DB_UPDATED_TIME"));
            }
            if (!String.valueOf(groupMap.get("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                json.put("domain", (Object)String.valueOf(groupMap.get("DOMAIN_NETBIOS_NAME")));
            }
            json.put("description", groupMap.get("DESCRIPTION"));
            if (include.equalsIgnoreCase("summary")) {
                final JSONObject summary = new JSONObject();
                final HashMap groupDetails = MDMGroupDeviceProfileAppHandler.getInstance().getGroupAssociatedProfileAppDetails(groupId);
                final int action_count = this.getGroupActionCount(groupId);
                summary.put("member_cout", groupDetails.get("MEMBER_COUNT"));
                summary.put("profile_cout", groupDetails.get("PROFILE_COUNT"));
                summary.put("app_count", groupDetails.get("APP_DISTRIBUTED_COUNT"));
                summary.put("app_installed_cout", groupDetails.get("APP_INSTALLED_COUNT"));
                summary.put("doc_cout", groupDetails.get("DOC_DISTRIBUTED_COUNT"));
                summary.put("action_cout", action_count);
                summary.put("created_by", groupDetails.get("CREATED_BY"));
                summary.put("modified_by", groupDetails.get("LAST_MODIFIED_BY"));
                json.put("summary", (Object)summary);
            }
            else if (include.equalsIgnoreCase("member")) {
                List memberIdList = MDMGroupHandler.getGroupMemberIdList(groupId);
                memberIdList = ((memberIdList != null) ? memberIdList : new ArrayList());
                final JSONArray memberArrays = JSONUtil.getInstance().convertListToStringJSONArray(memberIdList);
                json.put("member_ids", (Object)memberArrays);
            }
            else {
                final String searchValue = APIUtil.optStringFilter(message, "search", null);
                final Integer groupCategory = APIUtil.getIntegerFilter(message, "group_category");
                final Boolean selectAllValue = Boolean.valueOf(APIUtil.optStringFilter(message, "select_all", "false"));
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
                final Integer offset = pagingUtil.getStartIndex();
                final Integer limit = pagingUtil.getLimit();
                final Boolean deviceGroupUnassigned = Boolean.valueOf(APIUtil.optStringFilter(message, "device_group_unassigned", "false"));
                final String platformTypeStr = APIUtil.getStringFilter(message, "platform_type");
                final String osVersionStr = APIUtil.getStringFilter(message, "os_version");
                final String modelTypeStr = APIUtil.getStringFilter(message, "device_type");
                final String userTypeStr = APIUtil.getStringFilter(message, "user_type");
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupId", (Object)groupId);
                jsonObject.put("searchValue", (Object)searchValue);
                jsonObject.put("groupCategory", (Object)groupCategory);
                jsonObject.put("selectAllValue", (Object)selectAllValue);
                jsonObject.put("offset", (Object)offset);
                jsonObject.put("limit", (Object)limit);
                jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
                if (deviceGroupUnassigned != null) {
                    jsonObject.put("deviceGroupUnassigned", (Object)deviceGroupUnassigned);
                }
                if (platformTypeStr != null) {
                    jsonObject.put("platformType", (Object)platformTypeStr);
                }
                if (osVersionStr != null) {
                    jsonObject.put("osVersion", (Object)osVersionStr);
                }
                if (modelTypeStr != null) {
                    jsonObject.put("modelType", (Object)modelTypeStr);
                }
                if (userTypeStr != null) {
                    jsonObject.put("userType", (Object)userTypeStr);
                }
                JSONObject memberDetails = null;
                SelectQuery groupMemberQuery = null;
                if (include.equalsIgnoreCase("memberdetails")) {
                    jsonObject.put("associated", true);
                }
                else if (include.equalsIgnoreCase("available")) {
                    jsonObject.put("associated", false);
                }
                int totalCount = 0;
                if (MDMGroupHandler.getMDMGroupType().contains(groupType)) {
                    groupMemberQuery = this.getMemberDeviceDetailsQuery(jsonObject);
                    final SelectQuery countQuery = (SelectQuery)groupMemberQuery.clone();
                    while (countQuery.getSelectColumns().size() > 0) {
                        countQuery.removeSelectColumn(0);
                    }
                    countQuery.removeSortColumn(0);
                    totalCount = DBUtil.getRecordCount(countQuery, "Resource", "RESOURCE_ID");
                    if (!selectAllValue) {
                        final Range deviceRange = new Range((int)offset, (int)limit);
                        groupMemberQuery.setRange(deviceRange);
                    }
                    memberDetails = this.getMemberDeviceDetails(groupMemberQuery);
                }
                else if (groupType.equals(7)) {
                    groupMemberQuery = this.getMemberUserDetailsQuery(jsonObject);
                    final SelectQuery countQuery = (SelectQuery)groupMemberQuery.clone();
                    while (countQuery.getSelectColumns().size() > 0) {
                        countQuery.removeSelectColumn(0);
                    }
                    countQuery.removeSortColumn(0);
                    totalCount = DBUtil.getRecordCount(countQuery, "Resource", "RESOURCE_ID");
                    if (!selectAllValue) {
                        final Range deviceRange = new Range((int)offset, (int)limit);
                        groupMemberQuery.setRange(deviceRange);
                    }
                    memberDetails = this.getMemberUserDetails(groupMemberQuery);
                }
                if (include.equalsIgnoreCase("memberdetails")) {
                    final JSONObject associatedDetails = new JSONObject();
                    associatedDetails.put("members", memberDetails.get("members"));
                    if (!selectAllValue) {
                        associatedDetails.put("paging", (Object)pagingUtil.getPagingJSON(totalCount));
                    }
                    final JSONObject meta = new JSONObject();
                    meta.put("total_record_count", totalCount);
                    associatedDetails.put("metadata", (Object)meta);
                    json.put("associated", (Object)associatedDetails);
                }
                else if (include.equalsIgnoreCase("available")) {
                    final JSONObject availabaleMembers = new JSONObject();
                    availabaleMembers.put("members", memberDetails.get("available"));
                    if (!selectAllValue) {
                        availabaleMembers.put("paging", (Object)pagingUtil.getPagingJSON(totalCount));
                    }
                    final JSONObject meta = new JSONObject();
                    meta.put("total_record_count", totalCount);
                    availabaleMembers.put("metadata", (Object)meta);
                    json.put("available", (Object)availabaleMembers);
                }
            }
            return json;
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception occurred in getGroup", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addGroup(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "add-failed";
        try {
            MDMCustomGroupDetails cgDetails = null;
            JSONObject requestJSON;
            try {
                requestJSON = message.getJSONObject("msg_body");
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            String name;
            try {
                name = String.valueOf(requestJSON.get("name"));
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0005", new Object[] { "name" });
            }
            GroupFacade.logger.log(Level.INFO, "add group name :{0}", name);
            logJson.put((Object)"GROUP_NAME", (Object)name);
            final String domain = requestJSON.optString("domain", "MDM");
            if (MDMCustomGroupUtil.getInstance().checkIfExist(name, domain)) {
                throw new APIHTTPException("COM0010", new Object[] { "name : " + name });
            }
            final String description = requestJSON.optString("description");
            final Integer groupType = requestJSON.optInt("group_type", -1);
            logJson.put((Object)"GROUP_TYPE", (Object)groupType);
            if (!GroupFacade.allMDMGroupTypes.contains(groupType)) {
                throw new APIHTTPException("COM0005", new Object[] { "group_type" });
            }
            if (requestJSON.has("member_ids")) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupType", (Object)groupType);
                jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
                final List memberList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("member_ids"));
                logJson.put((Object)"MEMBER_IDs", (Object)memberList);
                if (!memberList.isEmpty()) {
                    this.validateMemberOperationToGroup(jsonObject, memberList);
                }
            }
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupType = ((groupType == 6 || groupType == 7) ? groupType : 6);
            cgDetails.groupCategory = requestJSON.optInt("group_category", 1);
            cgDetails.customerId = CustomerInfoUtil.getInstance().getCustomerId();
            cgDetails.domainName = domain;
            cgDetails.groupDescription = description;
            cgDetails.groupName = name;
            cgDetails.userId = APIUtil.getUserID(message);
            final Long currentTime = System.currentTimeMillis();
            cgDetails.createdTime = currentTime;
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && requestJSON.has("created_time")) {
                cgDetails.createdTime = requestJSON.getLong("created_time");
            }
            cgDetails.lastUpdatedTime = currentTime;
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && requestJSON.has("updated_time")) {
                cgDetails.lastUpdatedTime = requestJSON.getLong("updated_time");
            }
            cgDetails.integrationJson = (requestJSON.has("AdditionalDetailsJson") ? new JSONObject().put("AdditionalDetailsJson", (Object)requestJSON.getJSONObject("AdditionalDetailsJson")) : null);
            final JSONObject groupResponseJSON = MDMGroupHandler.getInstance().addOrUpdateMDMGroup(cgDetails);
            final JSONObject resourceJSON = message.getJSONObject("msg_header").getJSONObject("resource_identifier");
            final Long groupId = JSONUtil.optLongForUVH(groupResponseJSON, "RESOURCE_ID", (Long)null);
            logJson.put((Object)"GROUP_ID", (Object)groupId);
            resourceJSON.put("group_id", (Object)groupId);
            final JSONObject messageHeader = message.getJSONObject("msg_header");
            messageHeader.put("resource_identifier", (Object)resourceJSON);
            message.put("msg_header", (Object)messageHeader);
            if (requestJSON.has("member_ids")) {
                GroupFacade.logger.log(Level.INFO, "Member adding to the group member ids : {0}", requestJSON.get("member_ids"));
                message.put("new_group", true);
                this.addMembers(message);
                messageHeader.getJSONObject("filters").put("include", (Object)"member");
                message.put("msg_header", (Object)messageHeader);
            }
            remarks = "add-success";
            return this.getGroup(message);
        }
        catch (final JSONException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception occurred in addGroup", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_GROUP", logJson);
        }
    }
    
    public JSONObject modifyGroup(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "group_id", (Long)null);
            logJson.put((Object)"GROUP_ID", (Object)groupId);
            final Long customerID = APIUtil.getCustomerID(message);
            final JSONObject groupMap = this.validateAndGetGroupDetails(groupId, customerID);
            Long createdLogInId = null;
            if (groupMap.get("LOGIN_ID") != null && !groupMap.get("LOGIN_ID").equals("null")) {
                createdLogInId = Long.parseLong(String.valueOf(groupMap.get("LOGIN_ID")));
            }
            final String groupName = String.valueOf(groupMap.get("NAME"));
            final Long currentLoginId = APIUtil.getLoginID(message);
            final Boolean createdAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdLogInId, true);
            final Boolean currentAdmin = APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(message, true);
            Boolean addModAction = false;
            if (String.valueOf(groupMap.get("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM") && ((createdLogInId == null && currentAdmin) || currentLoginId.equals(createdLogInId) || (createdAdmin && currentAdmin && !MDMGroupHandler.getInstance().isDefaultGroup(groupName)))) {
                addModAction = true;
            }
            if (!addModAction) {
                throw new APIHTTPException("GRP0003", new Object[] { groupId });
            }
            GroupFacade.logger.log(Level.INFO, "modify the group with group id: {0}", groupId);
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            final Integer groupType = Integer.valueOf((String)groupMap.get("GROUP_TYPE"));
            logJson.put((Object)"GROUP_TYPE", (Object)groupType);
            List groupMembers = MDMGroupHandler.getGroupMemberIdList(groupId);
            final String domain = groupMap.optString("DOMAIN_NETBIOS_NAME", "MDM");
            final String name = requestJSON.optString("name", (String)null);
            if (name != null && MDMCustomGroupUtil.getInstance().checkIfExist(name, domain) && !name.equalsIgnoreCase(String.valueOf(groupMap.get("NAME")))) {
                throw new APIHTTPException("COM0010", new Object[] { "name : " + name });
            }
            if (requestJSON.has("member_ids")) {
                final JSONArray members = requestJSON.optJSONArray("member_ids");
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupType", (Object)groupType);
                jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
                final List<Long> memberList = JSONUtil.getInstance().convertLongJSONArrayTOList(members);
                logJson.put((Object)"MEMBER_IDs", (Object)memberList);
                if (!memberList.isEmpty()) {
                    this.validateMemberOperationToGroup(jsonObject, memberList);
                }
                groupMembers = ((groupMembers != null) ? groupMembers : new ArrayList());
                final List<Long> removeMemberList = new ArrayList<Long>(groupMembers);
                removeMemberList.removeAll(memberList);
                final List<Long> addMemberList = new ArrayList<Long>(memberList);
                addMemberList.removeAll(groupMembers);
                GroupFacade.logger.log(Level.INFO, "member addition in modify group {0}", addMemberList);
                final MDMGroupHandler handler = MDMGroupHandler.getInstance();
                final JSONObject membergroupObjects = new JSONObject();
                membergroupObjects.put("groupId", (Object)groupId);
                membergroupObjects.put("resourceId", (Object)addMemberList.toArray(new Long[0]));
                membergroupObjects.put("customerId", (Object)customerID);
                membergroupObjects.put("isMove", false);
                membergroupObjects.put("userId", (Object)APIUtil.getUserID(message));
                if (!addMemberList.isEmpty()) {
                    handler.addMembertoGroup(membergroupObjects);
                }
                GroupFacade.logger.log(Level.INFO, "member removing in modify group {0}", removeMemberList);
                if (!removeMemberList.isEmpty()) {
                    handler.removeMemberfromGroup(groupId, removeMemberList.toArray(new Long[0]));
                }
                final JSONObject messageHeader = message.getJSONObject("msg_header");
                messageHeader.getJSONObject("filters").put("include", (Object)"member");
                message.put("msg_header", (Object)messageHeader);
            }
            if (name != null) {
                MDMGroupHandler.getInstance().renameGroup(groupId, name);
            }
            final String description = requestJSON.optString("description", (String)null);
            if (description != null) {
                MDMGroupHandler.getInstance().updateGroupDescription(groupId, description);
            }
            MICSGroupFeatureController.addTrackingData(groupType, MICSGroupFeatureController.GroupOperation.EDIT, "MDM".equalsIgnoreCase(domain));
            remarks = "update-success";
            return this.getGroup(message);
        }
        catch (final JSONException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception occurred in modifyGroup", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "MODIFY_GROUP", logJson);
        }
    }
    
    public JSONObject validateAndGetGroupDetails(final Long groupId, final Long customerID) {
        if (groupId == null || groupId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        Connection conn = null;
        DataSet ds = null;
        try {
            SelectQuery selectQuery = this.getGroupValidationBaseQuery();
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "DESCRIPTION"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "IS_EDITABLE"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "DB_ADDED_TIME"));
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                selectQuery.addSelectColumn(Column.getColumn("Resource", "DB_UPDATED_TIME"));
            }
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)selectQuery, conn);
            if (ds.next()) {
                return APIUtil.getNewInstance().getJSONObjectFromDS(ds, selectQuery);
            }
            throw new APIHTTPException("COM0008", new Object[] { groupId });
        }
        catch (final SQLException | QueryConstructionException | JSONException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception occurred in validateAndGetGroupDetails", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            APIUtil.closeConnection(conn, ds);
        }
    }
    
    public void deleteGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            HashSet<Long> groupIds = null;
            if (groupId != -1L) {
                groupIds = new HashSet<Long>();
                groupIds.add(groupId);
            }
            else {
                final JSONObject msgBody = message.optJSONObject("msg_body");
                if (msgBody != null) {
                    groupIds = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(msgBody.optJSONArray("group_ids")));
                }
                if (groupIds == null || groupIds.isEmpty()) {
                    throw new APIHTTPException("COM0005", new Object[] { "group_id" });
                }
            }
            logJson.put((Object)"GROUP_IDs", (Object)groupIds);
            this.validateGroupsforDelete(message, groupIds);
            GroupFacade.logger.log(Level.INFO, "deleting group {0}", groupIds);
            GroupActionToCollectionHandler.getInstance().removeGroupActionToCollectionForGroupID(Arrays.asList(groupIds.toArray()), APIUtil.getCustomerID(message), APIUtil.getUserID(message));
            MDMGroupHandler.getInstance().deleteGroup(groupIds.toArray(new Long[0]), APIUtil.getUserID(message));
            MDMAppMgmtHandler.getInstance().deleteUpdateConfFromResource(APIUtil.getCustomerID(message));
            remarks = "delete-success";
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "Exception while deleting the group {0}", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_GROUP", logJson);
        }
    }
    
    public JSONObject getMembers(final JSONObject message) throws APIHTTPException {
        try {
            final String include = APIUtil.optStringFilter(message, "include", "");
            if (!include.isEmpty()) {
                final List<String> includeValues = Arrays.asList("summary", "member", "memberdetails", "available", "--");
                if (!includeValues.contains(include)) {
                    throw new APIHTTPException("COM0008", new Object[] { "include :" + include });
                }
            }
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "group_id", (Long)null);
            GroupFacade.logger.log(Level.INFO, "get members, group id:{0} and include : {1}", new Object[] { groupId, include });
            JSONObject groupDetails = this.validateAndGetGroupDetails(groupId, APIUtil.getCustomerID(message));
            final Integer groupType = groupDetails.optInt("GROUP_TYPE");
            groupDetails = new JSONObject();
            final List memberIdList = MDMGroupHandler.getGroupMemberIdList(groupId);
            final JSONArray memberArrays = (memberIdList != null) ? JSONUtil.getInstance().convertListToStringJSONArray(memberIdList) : new JSONArray();
            if (include.equalsIgnoreCase("memberdetails")) {
                final String searchValue = APIUtil.optStringFilter(message, "search", null);
                final Integer groupCategory = APIUtil.getIntegerFilter(message, "group_category");
                final Boolean selectAllValue = Boolean.valueOf(APIUtil.optStringFilter(message, "select_all", "false"));
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
                final Integer offset = pagingUtil.getStartIndex();
                final Integer limit = pagingUtil.getLimit();
                final Boolean deviceGroupUnassigned = Boolean.valueOf(APIUtil.optStringFilter(message, "device_group_unassigned", "false"));
                final String platformTypeStr = APIUtil.getStringFilter(message, "platform_type");
                final String osVersionStr = APIUtil.getStringFilter(message, "os_version");
                final String modelTypeStr = APIUtil.getStringFilter(message, "device_type");
                final String userTypeStr = APIUtil.getStringFilter(message, "user_type");
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupId", (Object)groupId);
                jsonObject.put("searchValue", (Object)searchValue);
                jsonObject.put("groupCategory", (Object)groupCategory);
                jsonObject.put("selectAllValue", (Object)selectAllValue);
                jsonObject.put("offset", (Object)offset);
                jsonObject.put("limit", (Object)limit);
                jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
                if (deviceGroupUnassigned != null) {
                    jsonObject.put("deviceGroupUnassigned", (Object)deviceGroupUnassigned);
                }
                if (platformTypeStr != null) {
                    jsonObject.put("platformType", (Object)platformTypeStr);
                }
                if (osVersionStr != null) {
                    jsonObject.put("osVersion", (Object)osVersionStr);
                }
                if (modelTypeStr != null) {
                    jsonObject.put("modelType", (Object)modelTypeStr);
                }
                if (userTypeStr != null) {
                    jsonObject.put("userType", (Object)userTypeStr);
                }
                jsonObject.put("associated", true);
                JSONObject memberDetails = null;
                SelectQuery groupMemberQuery = null;
                int totalCount = 0;
                if (MDMGroupHandler.getMDMGroupType().contains(groupType)) {
                    groupMemberQuery = this.getMemberDeviceDetailsQuery(jsonObject);
                    final SelectQuery countQuery = (SelectQuery)groupMemberQuery.clone();
                    while (countQuery.getSelectColumns().size() > 0) {
                        countQuery.removeSelectColumn(0);
                    }
                    countQuery.removeSortColumn(0);
                    totalCount = DBUtil.getRecordCount(countQuery, "Resource", "RESOURCE_ID");
                    if (!selectAllValue) {
                        final Range deviceRange = new Range((int)offset, (int)limit);
                        groupMemberQuery.setRange(deviceRange);
                    }
                    memberDetails = this.getMemberDeviceDetails(groupMemberQuery);
                }
                else if (groupType.equals(7)) {
                    groupMemberQuery = this.getMemberUserDetailsQuery(jsonObject);
                    final SelectQuery countQuery = (SelectQuery)groupMemberQuery.clone();
                    while (countQuery.getSelectColumns().size() > 0) {
                        countQuery.removeSelectColumn(0);
                    }
                    countQuery.removeSortColumn(0);
                    totalCount = DBUtil.getRecordCount(countQuery, "Resource", "RESOURCE_ID");
                    if (!selectAllValue) {
                        final Range deviceRange = new Range((int)offset, (int)limit);
                        groupMemberQuery.setRange(deviceRange);
                    }
                    memberDetails = this.getMemberUserDetails(groupMemberQuery);
                }
                final JSONObject associatedDetails = new JSONObject();
                associatedDetails.put("members", memberDetails.get("members"));
                if (!selectAllValue) {
                    associatedDetails.put("paging", (Object)pagingUtil.getPagingJSON(totalCount));
                }
                final JSONObject meta = new JSONObject();
                meta.put("total_record_count", totalCount);
                associatedDetails.put("metadata", (Object)meta);
                groupDetails.put("member_details", (Object)associatedDetails);
            }
            else {
                groupDetails.put("member_ids", (Object)memberArrays);
            }
            return groupDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public List getMemberIds(final Long groupId) throws DataAccessException {
        final Criteria groupIdCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
        final Iterator<Row> customGroupMemberRelRows = MDMUtil.getPersistence().get("CustomGroupMemberRel", groupIdCriteria).getRows("CustomGroupMemberRel");
        return DBUtil.getColumnValuesAsList((Iterator)customGroupMemberRelRows, "MEMBER_RESOURCE_ID");
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.WARNING, "Exception occurred in closeConnection....", ex);
        }
    }
    
    public JSONObject addMembers(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "add-failed";
        try {
            final MDMGroupHandler handler = MDMGroupHandler.getInstance();
            final Long groupID = APIUtil.getResourceID(message, "group_id");
            final Long memberId = APIUtil.getResourceID(message, "member_id");
            List<Long> groupSet;
            if (groupID == -1L) {
                groupSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            else {
                groupSet = new ArrayList<Long>();
                groupSet.add(groupID);
            }
            logJson.put((Object)"GROUP_IDs", (Object)groupSet);
            final Map groupMap = this.validateAndGetMultiGroupDetails(groupSet, APIUtil.getCustomerID(message), true);
            List<Long> memberSet;
            if (memberId == -1L) {
                memberSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("member_ids"));
            }
            else {
                memberSet = new ArrayList<Long>();
                memberSet.add(memberId);
            }
            logJson.put((Object)"MEMBER_IDs", (Object)memberSet);
            GroupFacade.logger.log(Level.INFO, "add members , group id: {0} and member id:{1}", new Object[] { groupSet, memberSet });
            final Long customerId = APIUtil.getCustomerID(message);
            final HashSet groupTypes = new HashSet(groupMap.values());
            if (groupTypes.size() != 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Groups are not with the unique group type" });
            }
            final Integer groupType = groupTypes.iterator().next();
            logJson.put((Object)"GROUP_TYPE", (Object)groupType);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupType", (Object)groupType);
            jsonObject.put("customerId", (Object)customerId);
            if (!memberSet.isEmpty()) {
                this.validateMemberOperationToGroup(jsonObject, memberSet);
            }
            final Long userId = APIUtil.getUserID(message);
            for (final Long groupIdLong : groupSet) {
                final List groupMembers = MDMGroupHandler.getGroupMemberIdList(groupIdLong);
                final List duplicateAddedList = new ArrayList();
                for (int index = 0; groupMembers != null && index < memberSet.size(); ++index) {
                    final Object value = memberSet.get(index);
                    if (groupMembers.contains(value)) {
                        duplicateAddedList.add(value);
                    }
                }
                final Boolean allowMember = message.optBoolean("allow_member", false);
                if (!allowMember && !duplicateAddedList.isEmpty()) {
                    throw new APIHTTPException("GRP0001", new Object[] { APIUtil.getCommaSeperatedString(duplicateAddedList) });
                }
            }
            for (final Long groupIdLong : groupSet) {
                final JSONObject membergroupObjects = new JSONObject();
                membergroupObjects.put("groupId", (Object)groupIdLong);
                membergroupObjects.put("resourceId", (Object)memberSet.toArray(new Long[memberSet.size()]));
                membergroupObjects.put("customerId", (Object)customerId);
                membergroupObjects.put("isMove", false);
                membergroupObjects.put("userId", (Object)userId);
                final Boolean newGroup = message.optBoolean("new_group");
                membergroupObjects.put("newGroup", (Object)newGroup);
                handler.addMembertoGroup(membergroupObjects);
            }
            final JSONObject group = new JSONObject();
            if (groupID != -1L) {
                List groupMembers2 = MDMGroupHandler.getGroupMemberIdList(groupID);
                groupMembers2 = ((groupMembers2 != null) ? groupMembers2 : new ArrayList());
                group.put("member_ids", (Object)JSONUtil.getInstance().convertListToStringJSONArray(groupMembers2));
            }
            remarks = "add-success";
            return group;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            GroupFacade.logger.log(Level.SEVERE, "exception in addMembers", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_GROUP_MEMBER", logJson);
        }
    }
    
    public JSONObject deleteMembers(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final MDMGroupHandler handler = MDMGroupHandler.getInstance();
            final Long groupID = APIUtil.getResourceID(message, "group_id");
            final Long memberId = APIUtil.getResourceID(message, "member_id");
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            List<Long> groupSet;
            if (groupID == -1L) {
                groupSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            else {
                groupSet = new ArrayList<Long>();
                groupSet.add(groupID);
            }
            final Map groupMap = this.validateAndGetMultiGroupDetails(groupSet, customerId, true);
            final HashSet groupTypes = new HashSet(groupMap.values());
            if (groupTypes.size() != 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Groups are not with the unique group type" });
            }
            List<Long> memberIds;
            if (memberId == -1L) {
                memberIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("member_ids"));
            }
            else {
                memberIds = Arrays.asList(memberId);
            }
            GroupFacade.logger.log(Level.INFO, "deleting members, group id: {0} and member ids: {1}", new Object[] { groupSet, memberIds });
            final Integer groupType = groupTypes.iterator().next();
            logJson.put((Object)"GROUP_ID", (Object)groupID);
            logJson.put((Object)"GROUP_TYPE", (Object)groupType);
            logJson.put((Object)"MEMBER_IDs", (Object)memberIds);
            if (groupMap != null) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupType", (Object)groupType);
                jsonObject.put("customerId", (Object)customerId);
                if (!memberIds.isEmpty()) {
                    this.validateMemberOperationToGroup(jsonObject, memberIds);
                }
                for (final Long groupIdLong : groupSet) {
                    final List groupMembers = MDMGroupHandler.getGroupMemberIdList(groupIdLong);
                    final List notAvailableList = new ArrayList(memberIds);
                    if (groupMembers != null && !groupMembers.isEmpty()) {
                        notAvailableList.removeAll(groupMembers);
                    }
                    if (!notAvailableList.isEmpty()) {
                        throw new APIHTTPException("GRP0002", new Object[] { APIUtil.getCommaSeperatedString(notAvailableList), groupIdLong });
                    }
                }
                for (final Long groupIdLong : groupSet) {
                    final JSONObject membergroupObjects = new JSONObject();
                    membergroupObjects.put("groupId", (Object)groupIdLong);
                    membergroupObjects.put("resourceId", (Object)memberIds.toArray(new Long[0]));
                    membergroupObjects.put("customerId", (Object)customerId);
                    membergroupObjects.put("isMove", false);
                    membergroupObjects.put("userId", (Object)userId);
                    handler.removeMemberfromGroup(membergroupObjects);
                }
                final JSONObject group = new JSONObject();
                group.put("group_ids", (Object)JSONUtil.getInstance().convertListToStringJSONArray(groupSet));
                group.put("member_ids", (Object)JSONUtil.getInstance().convertListToStringJSONArray(memberIds));
                remarks = "delete-success";
                return group;
            }
            throw new APIHTTPException("COM0008", new Object[] { "group_id : " + groupSet });
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            GroupFacade.logger.log(Level.SEVERE, "exception in deleteMembers", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_GROUP_MEMBER", logJson);
        }
    }
    
    public Object getGroups(final JSONObject request) throws APIHTTPException {
        final JSONArray result = new JSONArray();
        final JSONObject response = new JSONObject();
        Connection conn = null;
        DataSet ds = null;
        try {
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final SelectQuery groupQuery = this.getGroupsBaseQuery(request);
            Column groupIdColumn = Column.getColumn("CustomGroup", "RESOURCE_ID");
            groupIdColumn = groupIdColumn.distinct();
            groupIdColumn.setColumnAlias("RESOURCE_ID");
            groupQuery.addSelectColumn(groupIdColumn);
            groupQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID", "RES_ID"));
            groupQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            groupQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "MEMBER_COUNT"));
            groupQuery.addSelectColumn(Column.getColumn("Resource", "DB_ADDED_TIME"));
            final SelectQuery countQuery = this.getGroupsBaseQuery(request);
            countQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID").distinct().count());
            final String groupType = APIUtil.getStringFilter(request, "group_type");
            final Boolean selectAll = APIUtil.getBooleanFilter(request, "select_all");
            if (groupType != null) {
                final String[] groupTypes = groupType.split(",");
                final ArrayList<Integer> values = new ArrayList<Integer>();
                for (int i = 0; i < groupTypes.length; ++i) {
                    final int temp = Integer.parseInt(groupTypes[i]);
                    if (temp == 6 || temp == 7) {
                        values.add(temp);
                    }
                }
                if (values.size() != 0) {
                    final Criteria groupTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)values.toArray(), 8);
                    Criteria criteria = groupQuery.getCriteria();
                    if (criteria != null) {
                        groupQuery.setCriteria(criteria.and(groupTypeCri));
                    }
                    else {
                        groupQuery.setCriteria(groupTypeCri);
                    }
                    criteria = countQuery.getCriteria();
                    if (criteria != null) {
                        countQuery.setCriteria(criteria.and(groupTypeCri));
                    }
                    else {
                        countQuery.setCriteria(groupTypeCri);
                    }
                }
            }
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            if (count != 0) {
                if (!selectAll) {
                    final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                    if (pagingJSON != null) {
                        response.put("paging", (Object)pagingJSON);
                    }
                    groupQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                groupQuery.addSortColumn(new SortColumn("CustomGroup", "RESOURCE_ID", true));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)groupQuery, conn);
                while (ds.next()) {
                    final JSONObject groupMap = new JSONObject();
                    groupMap.put("group_id", (Object)String.valueOf(ds.getValue("RES_ID")));
                    groupMap.put("NAME".toLowerCase(), ds.getValue("NAME"));
                    if (!String.valueOf(ds.getValue("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                        groupMap.put("domain", ds.getValue("DOMAIN_NETBIOS_NAME"));
                    }
                    groupMap.put("GROUP_TYPE".toLowerCase(), ds.getValue("GROUP_TYPE"));
                    groupMap.put("MEMBER_COUNT", ds.getValue("MEMBER_COUNT"));
                    groupMap.put("created_time", ds.getValue("DB_ADDED_TIME"));
                    result.put((Object)groupMap);
                }
            }
            response.put("groups", (Object)result);
            return response;
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, "exception in getGroups", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    private SelectQuery getGroupsBaseQuery(final JSONObject request) {
        SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Join groupResourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join groupProfileSummaryJoin = new Join("CustomGroup", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join customGroupMemberRelJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        final Join managedDeviceJoin = new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join managedUserJoin = new Join("CustomGroupMemberRel", "ManagedUser", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join managedUserToDevice = new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join managedDeviceJoin2 = new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "USERMANAGEDDEVICE", 1);
        final Join lostModeTrackInfoJoin = new Join("ManagedDevice", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join lostModeTrackInforJoin2 = new Join("ManagedDevice", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "USERMANAGEDDEVICE", "USERLOSTDEVICE", 1);
        groupQuery.addJoin(groupResourceJoin);
        groupQuery.addJoin(groupProfileSummaryJoin);
        groupQuery = RBDAUtil.getInstance().getRBDAQuery(groupQuery);
        groupQuery.addJoin(customGroupMemberRelJoin);
        groupQuery.addJoin(managedDeviceJoin);
        groupQuery.addJoin(lostModeTrackInfoJoin);
        groupQuery.addJoin(managedUserJoin);
        groupQuery.addJoin(managedUserToDevice);
        groupQuery.addJoin(managedDeviceJoin2);
        groupQuery.addJoin(lostModeTrackInforJoin2);
        Criteria criteria = groupQuery.getCriteria();
        final String groupTypeString = APIUtil.getStringFilter(request, "group_type");
        final List<Integer> groupTypeList = new ArrayList<Integer>();
        if (groupTypeString != null) {
            final Set<String> groupTypeSet = new HashSet<String>(Arrays.asList(groupTypeString.split(",")));
            for (final String group : groupTypeSet) {
                try {
                    final int groupType = Integer.parseInt(group);
                    if (groupType != -1 && !GroupFacade.allMDMGroupTypes.contains(groupType)) {
                        throw new APIHTTPException("COM0024", new Object[] { "group_type" });
                    }
                    groupTypeList.addAll(this.getGroupTypeList(groupType));
                }
                catch (final NumberFormatException e) {
                    throw new APIHTTPException("COM0024", new Object[] { "group_type" });
                }
            }
        }
        Criteria mdmDeviceGroupCriteria;
        if (!groupTypeList.isEmpty()) {
            mdmDeviceGroupCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        }
        else {
            mdmDeviceGroupCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getAllMDMGroupTypes(), 8);
        }
        criteria = ((criteria == null) ? mdmDeviceGroupCriteria : criteria.and(mdmDeviceGroupCriteria));
        final boolean isLost = APIUtil.getBooleanFilter(request, "is_lost", false);
        final String search = APIUtil.optStringFilter(request, "search", null);
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false).and(mdmDeviceGroupCriteria);
            criteria = criteria.and(searchCriteria);
        }
        if (isLost) {
            final Criteria lostCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8).or(new Criteria(Column.getColumn("USERLOSTDEVICE", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8));
            criteria = criteria.and(lostCriteria);
        }
        final Long customerId = APIUtil.getCustomerID(request);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        criteria = criteria.and(customerCriteria);
        groupQuery.setCriteria(criteria);
        return groupQuery;
    }
    
    private List<Integer> getGroupTypeList(final String groupTypeString) {
        List<Integer> groupType = new ArrayList<Integer>();
        switch (groupTypeString) {
            case "7": {
                groupType = Arrays.asList(7);
                break;
            }
            case "6": {
                groupType = Arrays.asList(6, 3, 4, 5);
                break;
            }
        }
        return groupType;
    }
    
    public SelectQuery getGroupValidationQuery(final Collection<Long> groupIDs, final Long customerID) {
        SelectQuery selectQuery = this.getGroupValidationBaseQuery();
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupIDs.toArray(new Long[groupIDs.size()]), 8)));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    public void validateIfGroupsExists(final Collection<Long> groupIDs, final Long customerID) throws APIHTTPException {
        if (groupIDs.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final HashSet<Long> groups = new HashSet<Long>(groupIDs);
            final DataObject dataObject = DataAccess.get(this.getGroupValidationQuery(groupIDs, customerID));
            final Iterator<Row> rows = dataObject.getRows("CustomGroup");
            final ArrayList<Long> correctGroups = new ArrayList<Long>();
            while (rows.hasNext()) {
                correctGroups.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            groups.removeAll(correctGroups);
            if (groups.size() != 0) {
                final String remarkMsg = "Group Id : " + APIUtil.getCommaSeperatedString(groups);
                throw new APIHTTPException("COM0008", new Object[] { remarkMsg });
            }
        }
        catch (final DataAccessException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception in validateIfGroupsExists", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateGroupsforDelete(final JSONObject message, final Collection<Long> groupIDs) throws APIHTTPException {
        try {
            final Long currentLoginId = APIUtil.getLoginID(message);
            final Long customerID = APIUtil.getCustomerID(message);
            final Boolean currentAdmin = APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(message, true);
            final Boolean groupAdmin = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_GroupMgmt_Admin" }) || APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "ModernMgmt_MDMGroupMgmt_Admin" });
            final Boolean groupWrite = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_GroupMgmt_Write" }) || APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "ModernMgmt_MDMGroupMgmt_Write" });
            final SelectQuery selectQuery = this.getGroupValidationQuery(groupIDs, customerID);
            selectQuery.addJoin(new Join("Resource", "DMDomain", new String[] { "DOMAIN_NETBIOS_NAME" }, new String[] { "NAME" }, 1));
            selectQuery.addJoin(new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("DMDomain", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> resourceRows = dataObject.getRows("Resource");
            final List resourceIdList = new ArrayList();
            final List domainNameList = new ArrayList();
            while (resourceRows.hasNext()) {
                final Row row = resourceRows.next();
                final Object resourceId = row.get("RESOURCE_ID");
                final Object domainName = row.get("DOMAIN_NETBIOS_NAME");
                resourceIdList.add(resourceId);
                domainNameList.add(domainName);
            }
            final List unknownIds = new ArrayList(groupIDs);
            unknownIds.removeAll(resourceIdList);
            if (!unknownIds.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { "Group Ids: " + unknownIds.toString() });
            }
            final List createdLogInIdList = DBUtil.getColumnValuesAsList(dataObject.getRows("AaaLogin"), "LOGIN_ID");
            if (createdLogInIdList.isEmpty() && currentAdmin && groupAdmin) {
                return;
            }
            final HashSet<String> domainSet = new HashSet<String>(domainNameList);
            Boolean isDeletable = false;
            List adminAccessLoginId = new ArrayList();
            final Criteria aaaLoginIDCrit = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)createdLogInIdList.toArray(), 8);
            final DataObject adminDO = DMUserHandler.getLoginDOForAAARoleName("All_Managed_Mobile_Devices", aaaLoginIDCrit);
            if (adminDO != null) {
                final Iterator accountRows = adminDO.getRows("AaaAccount");
                adminAccessLoginId = DBUtil.getColumnValuesAsList(accountRows, "LOGIN_ID");
            }
            domainSet.remove("MDM");
            if (!domainSet.isEmpty()) {
                final Iterator<Row> dmDomainRows = dataObject.getRows("DMDomain");
                if (!dmDomainRows.hasNext()) {
                    return;
                }
                Criteria domainNameCriteria = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)domainSet.toArray(), 8, false);
                final Criteria statusCriteria = new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)new Integer[] { 300, 3 }, 8);
                domainNameCriteria = domainNameCriteria.and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), 0));
                final Iterator<Row> domainRows = dataObject.getRows("DMDomain", statusCriteria, new Join("DMDomain", "DMDomainSyncDetails", domainNameCriteria, 2));
                if (domainRows.hasNext()) {
                    throw new APIHTTPException("COM0015", new Object[] { "Can not Delete AD group while syncing" });
                }
                final Iterator<Row> userGroupRows = dataObject.getRows("CustomGroup", new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0));
                final List userGroupIds = DBUtil.getColumnValuesAsList((Iterator)userGroupRows, "RESOURCE_ID");
                if ((groupAdmin || groupWrite) && ((createdLogInIdList.size() == 1 && createdLogInIdList.get(0).equals(currentLoginId)) || currentAdmin)) {
                    isDeletable = true;
                }
                final List deviceGroupIds = new ArrayList(resourceIdList);
                deviceGroupIds.removeAll(userGroupIds);
                if (!isDeletable) {
                    throw new APIHTTPException("GRP0005", new Object[0]);
                }
            }
            if (groupAdmin && ((createdLogInIdList.size() == 1 && createdLogInIdList.get(0).equals(currentLoginId)) || (currentAdmin && adminAccessLoginId.size() == createdLogInIdList.size()))) {
                isDeletable = true;
                return;
            }
            if (groupWrite && createdLogInIdList.size() == 1 && createdLogInIdList.get(0).equals(currentLoginId)) {
                isDeletable = true;
                return;
            }
            if (!isDeletable) {
                throw new APIHTTPException("GRP0005", new Object[0]);
            }
        }
        catch (final DataAccessException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception in validateIfGroupsExists", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private SelectQuery getGroupValidationBaseQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("CustomGroupExtn", "AaaLogin", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, 1));
        final Criteria criteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getAllMDMGroupTypes(), 8);
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public void validateGroupsIfExists(Collection<Long> groups, final Long customerID) throws APIHTTPException {
        if (groups.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            GroupFacade.logger.log(Level.INFO, "validate the group, group ids: {0}", groups);
            groups = new HashSet<Long>(groups);
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
            selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groups.toArray(), 8).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.setCriteria(criteria);
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("CustomGroup");
            final ArrayList<Long> groupIDS = new ArrayList<Long>();
            while (rows.hasNext()) {
                groupIDS.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            groups.removeAll(groupIDS);
            if (groups.size() > 0) {
                final String remarkMsg = "Group Id : " + APIUtil.getCommaSeperatedString(groups);
                throw new APIHTTPException("COM0008", new Object[] { remarkMsg });
            }
        }
        catch (final DataAccessException ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception in validateGroupsIfExists", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getGroupDistributionListForProfile(final JSONObject requestJSON) throws Exception {
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
            final JSONArray groupsJSONArray = new JSONArray();
            final JSONObject responseJSON = new JSONObject();
            final Long profileId = APIUtil.getResourceID(requestJSON, "distribute_profile_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long loginId = APIUtil.getLoginID(requestJSON);
            final Long businessStoreID = APIUtil.getLongFilter(requestJSON, "businessstore_id");
            new ProfileFacade().validateIfProfileExists(profileId, customerId);
            final String groupTypeString = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("group_type", (String)null);
            final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
            Integer listType = APIUtil.getIntegerFilter(requestJSON, "list_type");
            final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            if (listType == -1) {
                listType = 1;
            }
            final int profileType = ProfileUtil.getProfileType(profileId);
            Long collectionId = null;
            if (profileType == 2) {
                collectionId = APIUtil.getResourceID(requestJSON, "collection_id");
            }
            else {
                collectionId = ProfileHandler.getRecentProfileCollectionID(profileId);
            }
            SelectQuery selectQuery;
            SelectQuery countQuery;
            if (listType == 1) {
                selectQuery = this.getYetToApplyGroupQuery(profileId, collectionId, customerId, profileType, businessStoreID);
                countQuery = this.getYetToApplyGroupCountQuery(profileId, collectionId, customerId, profileType, businessStoreID);
            }
            else if (listType == 3) {
                selectQuery = this.getAlreadyDistributedGroupQuery(profileId, collectionId, customerId);
                countQuery = this.getAlreadyDistributedGroupCountQuery(profileId, collectionId, customerId);
            }
            else {
                selectQuery = this.getYetToUpdateGroupQuery(profileId, collectionId, customerId, profileType, businessStoreID);
                countQuery = this.getYetToUpdateGroupCountQuery(profileId, collectionId, customerId, profileType, businessStoreID);
            }
            if (!MDMUtil.isStringEmpty(groupTypeString)) {
                final String[] groupTypeStringArray = groupTypeString.split(",");
                final int[] groupTypeArray = new int[groupTypeStringArray.length];
                for (int i = 0; i < groupTypeStringArray.length; ++i) {
                    final int groupType = getGroupType(groupTypeStringArray[i]);
                    groupTypeArray[i] = groupType;
                }
                Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeArray, 8);
                groupTypeCriteria = selectQuery.getCriteria().and(groupTypeCriteria);
                selectQuery.setCriteria(groupTypeCriteria);
                groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeArray, 8);
                groupTypeCriteria = countQuery.getCriteria().and(groupTypeCriteria);
                countQuery.setCriteria(groupTypeCriteria);
            }
            if (search != null) {
                final Criteria searchCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false);
                Criteria criteria = selectQuery.getCriteria().and(searchCriteria);
                selectQuery.setCriteria(criteria);
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
                    selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                    if (orderByJSON != null && orderByJSON.has("orderby")) {
                        final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                        if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("groupname")) {
                            selectQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                        }
                    }
                    else {
                        selectQuery.addSortColumn(new SortColumn("CustomGroup", "RESOURCE_ID", true));
                    }
                }
                final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
                if (profileType == 2) {
                    final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                    final int platform = ProfileUtil.getInstance().getPlatformType(profileId);
                    final ProfileDistributionListHandler profileDistributionListHandler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                    final HashMap remainingLicenseCountMap = profileDistributionListHandler.getRemainingLicenseCountMap(customerId, null);
                    final Integer assignmentType = VPPAppMgmtHandler.getInstance().getVppAppAssignmentType(appGroupId);
                    for (int j = 0; j < resultJSONArray.size(); ++j) {
                        final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(j);
                        groupsJSONArray.put((Object)getGroupJSONForApp(tempJSON, appGroupId, remainingLicenseCountMap, platform, assignmentType));
                    }
                }
                else {
                    for (int k = 0; k < resultJSONArray.size(); ++k) {
                        final org.json.simple.JSONObject tempJSON2 = (org.json.simple.JSONObject)resultJSONArray.get(k);
                        groupsJSONArray.put((Object)getGroupJSON(tempJSON2));
                    }
                }
            }
            if (profileType == 5) {
                final JSONObject complianceJSON = new JSONObject();
                complianceJSON.put("compliance_id", (Object)profileId);
                complianceJSON.put("customer_id", (Object)customerId);
                final JSONObject policyJSON = ComplianceDBUtil.getInstance().getComplianceProfile(complianceJSON);
                final JSONObject privacyJSON = new PrivacySettingsHandler().getPrivacyDetails(customerId);
                Boolean isGeoFence = false;
                Boolean isWipe = false;
                if (policyJSON.toString().contains("geo_fence_id")) {
                    isGeoFence = true;
                }
                if (policyJSON.toString().contains("wipe_sd_card")) {
                    isWipe = true;
                }
                privacyJSON.put("EraseDevice", (Object)isWipe);
                privacyJSON.put("geo_fences", (Object)isGeoFence);
                final SelectQuery deviceGroupMemberQuery = this.getDeviceGroupMemberQuery();
                final SelectQuery userGroupMemberQuery = this.getUserGroupMemberQuery();
                final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                deviceGroupMemberQuery.setCriteria(customerCriteria);
                userGroupMemberQuery.setCriteria(customerCriteria);
                final DataObject deviceDO = MDMUtil.getPersistence().get(deviceGroupMemberQuery);
                final DataObject userDO = MDMUtil.getPersistence().get(userGroupMemberQuery);
                this.updateOwnedByCount(deviceDO, userDO, groupsJSONArray, privacyJSON);
            }
            if (listType == 1) {
                responseJSON.put("yet_to_apply_list", (Object)groupsJSONArray);
            }
            else if (listType == 3) {
                responseJSON.put("distributed_list", (Object)groupsJSONArray);
            }
            else {
                responseJSON.put("yet_to_update_list", (Object)groupsJSONArray);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getGroupDistributionListForProfile()  >   Error ", e);
            throw e;
        }
    }
    
    private SelectQuery getUserGroupMemberQuery() {
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Join groupMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
        groupQuery.addJoin(groupMemberJoin);
        final Join managedUserJoin = new Join("CustomGroupMemberRel", "ManagedUserToDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        groupQuery.addJoin(managedUserJoin);
        final Join managedUserToDeviceJoin = new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        groupQuery.addJoin(managedUserToDeviceJoin);
        final Join enrollmentRequestToDeviceJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        groupQuery.addJoin(enrollmentRequestToDeviceJoin);
        final Join deviceEnrollmentRequestJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        groupQuery.addJoin(deviceEnrollmentRequestJoin);
        final Join customerJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        groupQuery.addJoin(customerJoin);
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "OWNED_BY"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        groupQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        groupQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        groupQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        return groupQuery;
    }
    
    private void updateOwnedByCount(final DataObject deviceDO, final DataObject userDO, final JSONArray groupsJSONArray, final JSONObject privacyJSON) throws Exception {
        try {
            for (int i = 0; i < groupsJSONArray.length(); ++i) {
                final JSONObject tempJSON = groupsJSONArray.getJSONObject(i);
                final Long groupId = JSONUtil.optLongForUVH(tempJSON, "group_id", Long.valueOf(-1L));
                final int groupType = tempJSON.getInt("GROUP_TYPE");
                JSONObject countJSON = new JSONObject();
                if (groupType == 7) {
                    countJSON = this.getOwnedByCountForUserGroup(userDO, groupId);
                }
                else if (groupType == 6) {
                    countJSON = this.getOwnedByCountForDeviceGroup(deviceDO, groupId);
                }
                tempJSON.put("corp_count", countJSON.optInt("corp_count", 0));
                tempJSON.put("personal_count", countJSON.optInt("personal_count", 0));
                this.processPrivacySettings(privacyJSON, tempJSON);
                groupsJSONArray.put(i, (Object)tempJSON);
            }
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, " -- updateOwnedByCount()  >   Error ", e);
            throw e;
        }
    }
    
    private void processPrivacySettings(final JSONObject privacyJSON, final JSONObject tempJSON) throws Exception {
        try {
            JSONArray applicableForJSONArray = new JSONArray();
            if (privacyJSON.has("applicable_for")) {
                applicableForJSONArray = privacyJSON.optJSONArray("applicable_for");
            }
            else {
                applicableForJSONArray.put(1);
                applicableForJSONArray.put(2);
            }
            final int fetchLocation = privacyJSON.optInt("fetch_location", 0);
            if (applicableForJSONArray.length() == 2) {
                if (fetchLocation == 0) {
                    tempJSON.put("collect_location", (Object)Boolean.TRUE);
                }
                else {
                    tempJSON.put("collect_location", (Object)Boolean.FALSE);
                    tempJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.privacy.location_groups", new Object[0]));
                }
            }
            else {
                final int applicableFor = applicableForJSONArray.getInt(0);
                final int corpCount = tempJSON.getInt("corp_count");
                final int personalCount = tempJSON.getInt("personal_count");
                if (applicableFor == 1) {
                    if (fetchLocation != 0 && corpCount > 0) {
                        tempJSON.put("collect_location", (Object)Boolean.FALSE);
                        tempJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.privacy.location_corp_groups", new Object[0]));
                    }
                    else {
                        tempJSON.put("collect_location", (Object)Boolean.TRUE);
                    }
                }
                else if (applicableFor == 2) {
                    if (fetchLocation != 0 && personalCount > 0) {
                        tempJSON.put("collect_location", (Object)Boolean.FALSE);
                        tempJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.privacy.location_personal_groups", new Object[0]));
                    }
                    else {
                        tempJSON.put("collect_location", (Object)Boolean.TRUE);
                    }
                }
            }
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getOwnedByCountForUserGroup()  >   Error ", e);
            throw e;
        }
    }
    
    private JSONObject getOwnedByCountForUserGroup(final DataObject dataObject, final Long groupId) throws DataAccessException, JSONException {
        try {
            int corpCount = 0;
            int personalCount = 0;
            final Iterator iterator = dataObject.getRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0));
            while (iterator.hasNext()) {
                final Row groupMemberRow = iterator.next();
                final Long userId = (Long)groupMemberRow.get("MEMBER_RESOURCE_ID");
                final Criteria userCriteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userId, 0);
                final Iterator userToDeviceIterator = dataObject.getRows("ManagedUserToDevice", userCriteria);
                while (userToDeviceIterator.hasNext()) {
                    final Row userToDeviceRow = userToDeviceIterator.next();
                    final Long deviceId = (Long)userToDeviceRow.get("MANAGED_DEVICE_ID");
                    final Criteria deviceCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
                    final Row enrollmentReqToDeviceRow = dataObject.getRow("EnrollmentRequestToDevice", deviceCriteria);
                    final Long enrollmentRequestId = (Long)enrollmentReqToDeviceRow.get("ENROLLMENT_REQUEST_ID");
                    final Row enrollmentRequestRow = dataObject.getRow("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0));
                    final int ownedBy = (int)enrollmentRequestRow.get("OWNED_BY");
                    switch (ownedBy) {
                        case 1: {
                            ++corpCount;
                            continue;
                        }
                        case 2: {
                            ++personalCount;
                            continue;
                        }
                    }
                }
            }
            final JSONObject countJSON = new JSONObject();
            countJSON.put("corp_count", corpCount);
            countJSON.put("personal_count", personalCount);
            return countJSON;
        }
        catch (final DataAccessException | JSONException e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getOwnedByCountForUserGroup()  >   Error ", e);
            throw e;
        }
    }
    
    private JSONObject getOwnedByCountForDeviceGroup(final DataObject dataObject, final Long groupId) throws DataAccessException, JSONException {
        try {
            int corpCount = 0;
            int personalCount = 0;
            final Iterator iterator = dataObject.getRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0));
            while (iterator.hasNext()) {
                final Row groupMemberRow = iterator.next();
                final Long deviceId = (Long)groupMemberRow.get("MEMBER_RESOURCE_ID");
                final Criteria deviceCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
                final Row enrollmentReqToDeviceRow = dataObject.getRow("EnrollmentRequestToDevice", deviceCriteria);
                final Long enrollmentRequestId = (Long)enrollmentReqToDeviceRow.get("ENROLLMENT_REQUEST_ID");
                final Row enrollmentRequestRow = dataObject.getRow("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0));
                final int ownedBy = (int)enrollmentRequestRow.get("OWNED_BY");
                switch (ownedBy) {
                    case 1: {
                        ++corpCount;
                        continue;
                    }
                    case 2: {
                        ++personalCount;
                        continue;
                    }
                }
            }
            final JSONObject countJSON = new JSONObject();
            countJSON.put("corp_count", corpCount);
            countJSON.put("personal_count", personalCount);
            return countJSON;
        }
        catch (final DataAccessException | JSONException e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getOwnedByCountForDeviceGroup()  >   Error ", e);
            throw e;
        }
    }
    
    public static JSONObject getGroupJSON(final org.json.simple.JSONObject tempJSON) throws JSONException {
        try {
            final JSONObject groupJSON = new JSONObject();
            groupJSON.put("group_id", (Object)String.valueOf(tempJSON.get((Object)"RESOURCE_ID")));
            groupJSON.put("NAME", tempJSON.get((Object)"NAME"));
            if (!String.valueOf(tempJSON.get((Object)"DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                groupJSON.put("domain", tempJSON.get((Object)"DOMAIN_NETBIOS_NAME"));
            }
            groupJSON.put("member_count", tempJSON.get((Object)"COUNT"));
            groupJSON.put("GROUP_TYPE", tempJSON.get((Object)"GROUP_TYPE"));
            final Integer iOSDeviceCount = (int)tempJSON.getOrDefault((Object)"iOSDeviceCount", (Object)0) + (int)tempJSON.getOrDefault((Object)"iOSDeviceAssociatedToUserCount", (Object)0);
            final Integer androidDeviceCount = (int)tempJSON.getOrDefault((Object)"androidDeviceCount", (Object)0) + (int)tempJSON.getOrDefault((Object)"androidDeviceAssociatedToUserCount", (Object)0);
            final Integer windowsDeviceCount = (int)tempJSON.getOrDefault((Object)"windowsDeviceCount", (Object)0) + (int)tempJSON.getOrDefault((Object)"windowsDeviceAssociatedToUserCount", (Object)0);
            final Integer chromeDeviceCount = (int)tempJSON.getOrDefault((Object)"chromeDeviceCount", (Object)0) + (int)tempJSON.getOrDefault((Object)"chromeDeviceAssociatedToUserCount", (Object)0);
            groupJSON.put(String.valueOf(1), (Object)iOSDeviceCount);
            groupJSON.put(String.valueOf(2), (Object)androidDeviceCount);
            groupJSON.put(String.valueOf(3), (Object)windowsDeviceCount);
            groupJSON.put(String.valueOf(4), (Object)chromeDeviceCount);
            return groupJSON;
        }
        catch (final JSONException e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getGroupJSON()  >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public static JSONObject getGroupJSONForApp(final org.json.simple.JSONObject tempJSON, final Long appGroupId, final Map remainingLicenseCountMap, final int platform, final Integer assignmentType) throws JSONException, Exception {
        try {
            final JSONObject groupJSON = new JSONObject();
            groupJSON.put("group_id", (Object)String.valueOf(tempJSON.get((Object)"RESOURCE_ID")));
            groupJSON.put("NAME", tempJSON.get((Object)"NAME"));
            if (!String.valueOf(tempJSON.get((Object)"DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                groupJSON.put("domain", tempJSON.get((Object)"DOMAIN_NETBIOS_NAME"));
            }
            groupJSON.put("member_count", tempJSON.get((Object)"COUNT"));
            groupJSON.put("GROUP_TYPE", tempJSON.get((Object)"GROUP_TYPE"));
            final Integer groupType = (Integer)tempJSON.get((Object)"GROUP_TYPE");
            final Integer totalUserCount = (Integer)((groupType == 7) ? tempJSON.get((Object)"COUNT") : ((Integer)tempJSON.get((Object)"totalUsersInTheGroup")));
            final Integer iOSDeviceCount = (int)tempJSON.get((Object)"iOSDeviceCount") + (int)tempJSON.get((Object)"iOSDeviceAssociatedToUserCount");
            final Integer androidDeviceCount = (int)tempJSON.get((Object)"androidDeviceCount") + (int)tempJSON.get((Object)"androidDeviceAssociatedToUserCount");
            final Integer windowsDeviceCount = (int)tempJSON.get((Object)"windowsDeviceCount") + (int)tempJSON.get((Object)"windowsDeviceAssociatedToUserCount");
            final Integer chromeDeviceCount = (int)tempJSON.get((Object)"chromeDeviceCount") + (int)tempJSON.get((Object)"chromeDeviceAssociatedToUserCount");
            Integer totalDeviceCount = 0;
            switch (platform) {
                case 1: {
                    if (assignmentType == 1) {
                        totalDeviceCount = totalUserCount;
                        break;
                    }
                    totalDeviceCount = iOSDeviceCount;
                    break;
                }
                case 2: {
                    totalDeviceCount = androidDeviceCount;
                    break;
                }
                case 3: {
                    totalDeviceCount = windowsDeviceCount;
                    break;
                }
                case 4: {
                    totalDeviceCount = chromeDeviceCount;
                    break;
                }
            }
            if (remainingLicenseCountMap.containsKey(appGroupId)) {
                final JSONObject licenseSummaryJSON = remainingLicenseCountMap.get(appGroupId);
                final Integer remainingLicenseCount = licenseSummaryJSON.optInt("AVAILABLE_LICENSE_COUNT");
                if (remainingLicenseCount < totalDeviceCount) {
                    groupJSON.put("inSufficientLicense", true);
                    groupJSON.put("remarks", (Object)I18N.getMsg("dc.mdm.group.insufficient_license", new Object[] { totalDeviceCount, remainingLicenseCount }));
                }
                else {
                    groupJSON.put("inSufficientLicense", false);
                }
            }
            else {
                groupJSON.put("inSufficientLicense", false);
            }
            groupJSON.put("total_members", (Object)totalDeviceCount);
            return groupJSON;
        }
        catch (final JSONException e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getGroupJSONForApp()  >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public static int getGroupType(final String groupTypeString) {
        int groupType = -1;
        switch (groupTypeString) {
            case "user_group": {
                groupType = 7;
                break;
            }
            case "device_group": {
                groupType = 6;
                break;
            }
        }
        return groupType;
    }
    
    private List<Integer> getGroupTypeList(final Integer groupType) {
        List<Integer> groupTypes = new ArrayList<Integer>();
        switch (groupType) {
            case 7: {
                groupTypes = Arrays.asList(7);
                break;
            }
            case 6: {
                groupTypes = Arrays.asList(6, 3, 4, 5);
                break;
            }
        }
        return groupTypes;
    }
    
    private SelectQuery getYetToApplyGroupQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessstoreID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = this.getBaseGroupQuery();
        final List groupList = this.getDistributedGroupListForProfile(profileId, collectionId, customerId, profileType, businessstoreID);
        Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupList.toArray(), 9);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria baseCriteria = selectQuery.getCriteria();
        if (baseCriteria != null) {
            groupCriteria = groupCriteria.and(baseCriteria);
        }
        selectQuery.setCriteria(groupCriteria.and(customerCriteria).and(groupTypeCriteria));
        return selectQuery;
    }
    
    private SelectQuery getYetToApplyGroupCountQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessstoreID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = this.getBaseGroupQuery();
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)selectQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            selectQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = Column.getColumn("CustomGroup", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        selectQuery.addSelectColumn(countColumn);
        final List groupList = this.getDistributedGroupListForProfile(profileId, collectionId, customerId, profileType, businessstoreID);
        Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupList.toArray(), 9);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria baseCriteria = selectQuery.getCriteria();
        if (baseCriteria != null) {
            groupCriteria = groupCriteria.and(baseCriteria);
        }
        selectQuery.setCriteria(groupCriteria.and(customerCriteria).and(groupTypeCriteria));
        return selectQuery;
    }
    
    private SelectQuery getDeviceGroupMemberQuery() {
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Join groupMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
        groupQuery.addJoin(groupMemberJoin);
        final Join managedDeviceJoin = new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        groupQuery.addJoin(managedDeviceJoin);
        final Join enrollmentRequestToDeviceJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        groupQuery.addJoin(enrollmentRequestToDeviceJoin);
        final Join deviceEnrollmentRequestJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        groupQuery.addJoin(deviceEnrollmentRequestJoin);
        final Join customerJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        groupQuery.addJoin(customerJoin);
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "OWNED_BY"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        groupQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        groupQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        groupQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        return groupQuery;
    }
    
    private void addProfileQuery(final SelectQuery selectQuery) {
        final Join recentProfileForGroupJoin = new Join("Resource", "RecentProfileForGroup", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_ID" }, 2);
        final Join profileToCustomerRelJoin = new Join("RecentProfileForGroup", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profileJoin = new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        selectQuery.addJoin(recentProfileForGroupJoin);
        selectQuery.addJoin(profileToCustomerRelJoin);
        selectQuery.addJoin(profileJoin);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
    }
    
    private SelectQuery getAlreadyDistributedGroupQuery(final Long profileID, final Long collectionID, final Long customerID) {
        final SelectQuery alreadyDistributedCollectionQuery = this.getBaseGroupQuery();
        this.addProfileQuery(alreadyDistributedCollectionQuery);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileID, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        alreadyDistributedCollectionQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(deleteCriteria).and(groupTypeCriteria));
        return alreadyDistributedCollectionQuery;
    }
    
    private SelectQuery getAlreadyDistributedGroupCountQuery(final Long profileID, final Long collectionID, final Long customerID) {
        final SelectQuery alreadyDistributedCollectionCountQuery = this.getBaseGroupQuery();
        this.addProfileQuery(alreadyDistributedCollectionCountQuery);
        final ArrayList<Column> columnList = (ArrayList<Column>)alreadyDistributedCollectionCountQuery.getSelectColumns();
        for (final Column column : columnList) {
            alreadyDistributedCollectionCountQuery.removeSelectColumn(column);
        }
        Column countColumn = Column.getColumn("CustomGroup", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        alreadyDistributedCollectionCountQuery.addSelectColumn(countColumn);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileID, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        alreadyDistributedCollectionCountQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(deleteCriteria).and(groupTypeCriteria));
        return alreadyDistributedCollectionCountQuery;
    }
    
    private SelectQuery getYetToUpdateGroupQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) {
        final SelectQuery selectQuery = this.getBaseGroupQuery();
        this.addProfileQuery(selectQuery);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionId, 1);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria baseCriteria = selectQuery.getCriteria();
        if (baseCriteria != null) {
            groupTypeCriteria = groupTypeCriteria.and(baseCriteria);
        }
        selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(profileTypeCriteria).and(deleteCriteria).and(groupTypeCriteria));
        if (profileType == 2) {
            selectQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            final Long releaseLabelIdForAppCollectionId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionId);
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelIdForAppCollectionId, 0)));
        }
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(selectQuery, businessStoreID);
        }
        return selectQuery;
    }
    
    private SelectQuery getYetToUpdateGroupCountQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) {
        final SelectQuery selectQuery = this.getBaseGroupQuery();
        this.addProfileQuery(selectQuery);
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)selectQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            selectQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = Column.getColumn("CustomGroup", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        selectQuery.addSelectColumn(countColumn);
        final List groupTypeList = Arrays.asList(MDMGroupHandler.getAllMDMGroupTypes());
        Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionId, 1);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria baseCriteria = selectQuery.getCriteria();
        if (baseCriteria != null) {
            groupTypeCriteria = groupTypeCriteria.and(baseCriteria);
        }
        selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(profileTypeCriteria).and(deleteCriteria).and(groupTypeCriteria));
        if (profileType == 2) {
            selectQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            final Long releaseLabelIdForAppCollectionId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionId);
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelIdForAppCollectionId, 0)));
        }
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(selectQuery, businessStoreID);
        }
        return selectQuery;
    }
    
    private SelectQuery getBaseGroupQuery() {
        final Table baseTable = Table.getTable("CustomGroup");
        SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        final Join groupResourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Table subTable = Table.getTable("CustomGroup", "GROUP");
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(subTable);
        final Join groupMemberJoin = new Join(subTable, Table.getTable("CustomGroupMemberRel"), new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        subQuery.addJoin(groupMemberJoin);
        subQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        subQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, "ManagedDevice", "UserAssociatedToMangedDevice", 1));
        subQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedUserToDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        subQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "ManagedDeviceAssociatedToUser", 1));
        subQuery.addSelectColumn(Column.getColumn("GROUP", "RESOURCE_ID"));
        final Column count = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").distinct().count();
        count.setColumnAlias("COUNT");
        subQuery.addSelectColumn(count);
        final Criteria deviceGroupCriteria = new Criteria(Column.getColumn("GROUP", "GROUP_TYPE"), (Object)6, 0);
        final Criteria userGroupCriteria = new Criteria(Column.getColumn("GROUP", "GROUP_TYPE"), (Object)7, 0);
        final Criteria iOSDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria iOSDeviceAssociatedToUserCriteria = new Criteria(Column.getColumn("ManagedDeviceAssociatedToUser", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria androidDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria androidDeviceAssociatedToUserCriteria = new Criteria(Column.getColumn("ManagedDeviceAssociatedToUser", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria windowsDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria windowsDeviceAssociatedToUserCriteria = new Criteria(Column.getColumn("ManagedDeviceAssociatedToUser", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria chromeDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)4, 0);
        final Criteria chromeDeviceAssociatedToUserCriteria = new Criteria(Column.getColumn("ManagedDeviceAssociatedToUser", "PLATFORM_TYPE"), (Object)4, 0);
        final CaseExpression iOSDeviceMembers = new CaseExpression("iOSDeviceCount");
        iOSDeviceMembers.addWhen(deviceGroupCriteria.and(iOSDeviceCriteria), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        Column iOSMembersCount = (Column)Column.createFunction("DISTINCT", new Object[] { iOSDeviceMembers });
        iOSMembersCount = (Column)Column.createFunction("COUNT", new Object[] { iOSMembersCount });
        iOSMembersCount.setColumnAlias("iOSDeviceCount");
        iOSMembersCount.setType(4);
        subQuery.addSelectColumn(iOSMembersCount);
        final CaseExpression iOSDeviceMembersAssociatedToUser = new CaseExpression("iOSDeviceAssociatedToUserCount");
        iOSDeviceMembersAssociatedToUser.addWhen(userGroupCriteria.and(iOSDeviceAssociatedToUserCriteria), (Object)Column.getColumn("ManagedDeviceAssociatedToUser", "RESOURCE_ID"));
        Column iOSMembersAssociatedToUserCount = (Column)Column.createFunction("DISTINCT", new Object[] { iOSDeviceMembersAssociatedToUser });
        iOSMembersAssociatedToUserCount = (Column)Column.createFunction("COUNT", new Object[] { iOSMembersAssociatedToUserCount });
        iOSMembersAssociatedToUserCount.setColumnAlias("iOSDeviceAssociatedToUserCount");
        iOSMembersAssociatedToUserCount.setType(4);
        subQuery.addSelectColumn(iOSMembersAssociatedToUserCount);
        final CaseExpression androidDeviceMembers = new CaseExpression("androidDeviceCount");
        androidDeviceMembers.addWhen(deviceGroupCriteria.and(androidDeviceCriteria), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        Column androidMembersCount = (Column)Column.createFunction("DISTINCT", new Object[] { androidDeviceMembers });
        androidMembersCount = (Column)Column.createFunction("COUNT", new Object[] { androidMembersCount });
        androidMembersCount.setColumnAlias("androidDeviceCount");
        androidMembersCount.setType(4);
        subQuery.addSelectColumn(androidMembersCount);
        final CaseExpression androidDeviceMembersAssociatedToUser = new CaseExpression("androidDeviceAssociatedToUserCount");
        androidDeviceMembersAssociatedToUser.addWhen(userGroupCriteria.and(androidDeviceAssociatedToUserCriteria), (Object)Column.getColumn("ManagedDeviceAssociatedToUser", "RESOURCE_ID"));
        Column androidMembersAssociatedToUserCount = (Column)Column.createFunction("DISTINCT", new Object[] { androidDeviceMembersAssociatedToUser });
        androidMembersAssociatedToUserCount = (Column)Column.createFunction("COUNT", new Object[] { androidMembersAssociatedToUserCount });
        androidMembersAssociatedToUserCount.setColumnAlias("androidDeviceAssociatedToUserCount");
        androidMembersAssociatedToUserCount.setType(4);
        subQuery.addSelectColumn(androidMembersAssociatedToUserCount);
        final CaseExpression windowsDeviceMembers = new CaseExpression("windowsDeviceCount");
        windowsDeviceMembers.addWhen(deviceGroupCriteria.and(windowsDeviceCriteria), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        Column windowsMembersCount = (Column)Column.createFunction("DISTINCT", new Object[] { windowsDeviceMembers });
        windowsMembersCount = (Column)Column.createFunction("COUNT", new Object[] { windowsMembersCount });
        windowsMembersCount.setColumnAlias("windowsDeviceCount");
        windowsMembersCount.setType(4);
        subQuery.addSelectColumn(windowsMembersCount);
        final CaseExpression windowsDeviceMembersAssociatedToUser = new CaseExpression("windowsDeviceAssociatedToUserCount");
        windowsDeviceMembersAssociatedToUser.addWhen(userGroupCriteria.and(windowsDeviceAssociatedToUserCriteria), (Object)Column.getColumn("ManagedDeviceAssociatedToUser", "RESOURCE_ID"));
        Column windowsMembersAssociatedToUserCount = (Column)Column.createFunction("DISTINCT", new Object[] { windowsDeviceMembersAssociatedToUser });
        windowsMembersAssociatedToUserCount = (Column)Column.createFunction("COUNT", new Object[] { windowsMembersAssociatedToUserCount });
        windowsMembersAssociatedToUserCount.setColumnAlias("windowsDeviceAssociatedToUserCount");
        windowsMembersAssociatedToUserCount.setType(4);
        subQuery.addSelectColumn(windowsMembersAssociatedToUserCount);
        final CaseExpression chromeDeviceMembers = new CaseExpression("chromeDeviceCount");
        chromeDeviceMembers.addWhen(deviceGroupCriteria.and(chromeDeviceCriteria), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        Column chromeMembersCount = (Column)Column.createFunction("DISTINCT", new Object[] { chromeDeviceMembers });
        chromeMembersCount = (Column)Column.createFunction("COUNT", new Object[] { chromeMembersCount });
        chromeMembersCount.setColumnAlias("chromeDeviceCount");
        chromeMembersCount.setType(4);
        subQuery.addSelectColumn(chromeMembersCount);
        final CaseExpression chromeDeviceMembersAssociatedToUser = new CaseExpression("chromeDeviceAssociatedToUserCount");
        chromeDeviceMembersAssociatedToUser.addWhen(userGroupCriteria.and(chromeDeviceAssociatedToUserCriteria), (Object)Column.getColumn("ManagedDeviceAssociatedToUser", "RESOURCE_ID"));
        Column chromeMembersAssociatedToUserCount = (Column)Column.createFunction("DISTINCT", new Object[] { chromeDeviceMembersAssociatedToUser });
        chromeMembersAssociatedToUserCount = (Column)Column.createFunction("COUNT", new Object[] { chromeMembersAssociatedToUserCount });
        chromeMembersAssociatedToUserCount.setColumnAlias("chromeDeviceAssociatedToUserCount");
        chromeMembersAssociatedToUserCount.setType(4);
        subQuery.addSelectColumn(chromeMembersAssociatedToUserCount);
        final CaseExpression totalUsersIntheGroup = new CaseExpression("totalUsersInTheGroup");
        totalUsersIntheGroup.addWhen(deviceGroupCriteria, (Object)Column.getColumn("UserAssociatedToMangedDevice", "MANAGED_USER_ID"));
        Column totalUsersCount = (Column)Column.createFunction("DISTINCT", new Object[] { totalUsersIntheGroup });
        totalUsersCount = (Column)Column.createFunction("COUNT", new Object[] { totalUsersCount });
        totalUsersCount.setColumnAlias("totalUsersInTheGroup");
        totalUsersCount.setType(4);
        subQuery.addSelectColumn(totalUsersCount);
        final List columns = new ArrayList();
        columns.add(Column.getColumn("GROUP", "RESOURCE_ID"));
        final GroupByClause groupByClause = new GroupByClause(columns);
        subQuery.setGroupByClause(groupByClause);
        final DerivedTable derivedTable = new DerivedTable("GROUP", (Query)subQuery);
        groupQuery.addJoin(groupResourceJoin);
        final Join derivedTableJoin = new Join(baseTable, (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        groupQuery.addJoin(derivedTableJoin);
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "COUNT"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "iOSDeviceCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "iOSDeviceAssociatedToUserCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "androidDeviceCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "androidDeviceAssociatedToUserCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "windowsDeviceCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "windowsDeviceAssociatedToUserCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "chromeDeviceCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "chromeDeviceAssociatedToUserCount"));
        groupQuery.addSelectColumn(Column.getColumn("GROUP", "totalUsersInTheGroup"));
        groupQuery = RBDAUtil.getInstance().getRBDAQuery(groupQuery);
        return groupQuery;
    }
    
    private List getDistributedGroupListForProfile(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessstoreID) throws DataAccessException, Exception {
        try {
            final List groupList = new ArrayList();
            final int platformType = ProfileUtil.getInstance().getPlatformType(profileId);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            final Join profileToCustomerRelJoin = new Join("RecentProfileForGroup", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileJoin = new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(profileToCustomerRelJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(profileTypeCriteria).and(deleteCriteria));
            if (profileType == 2) {
                selectQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
                selectQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0);
                final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)new Column("MdAppCatalogToGroup", "RESOURCE_ID"), 0);
                selectQuery.addJoin(new Join("RecentProfileForGroup", "MdAppCatalogToGroup", appGroupCriteria.and(resourceCriteria), 2));
                selectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppToColln", 2));
                final Criteria platformBasedCriteria = AppVersionHandler.getInstance(platformType).getDistributedGroupListForAppCriteria(collectionId, profileId);
                selectQuery.setCriteria(selectQuery.getCriteria().and(platformBasedCriteria));
                selectQuery.setDistinct((boolean)Boolean.TRUE);
            }
            if (businessstoreID != -1L) {
                this.addBusinesssStoreCriteria(selectQuery, businessstoreID);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("RecentProfileForGroup");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long groupId = (Long)row.get("GROUP_ID");
                groupList.add(groupId);
            }
            return groupList;
        }
        catch (final DataAccessException e) {
            GroupFacade.logger.log(Level.SEVERE, " -- getDistributedGroupListForProfile()  >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    private void addBusinesssStoreCriteria(final SelectQuery query, final Long businessStoreID) {
        final Criteria joinCri1 = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
        final Criteria joinCri2 = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0);
        final Criteria joinCri3 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
        query.addJoin(new Join("RecentProfileForGroup", "MDMResourceToDeploymentConfigs", joinCri1.and(joinCri2).and(joinCri3), 2));
    }
    
    public void refreshAppStatusForDeviceGroup(final JSONObject apiRequest) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequest);
            final Long appGroupId = APIUtil.getResourceID(apiRequest, "app_id");
            final Integer status = APIUtil.getIntegerFilter(apiRequest, "status");
            final JSONArray appGroupIds = new JSONArray();
            appGroupIds.put((Object)appGroupId);
            final Long groupId = APIUtil.getResourceID(apiRequest, "group_id");
            final JSONArray resourceIds = new JSONArray();
            resourceIds.put((Object)groupId);
            final JSONArray deviceIds = apiRequest.getJSONObject("msg_body").getJSONArray("device_ids");
            final JSONObject appRefreshJSON = new JSONObject();
            appRefreshJSON.put("CUSTOMER_ID", (Object)customerId);
            appRefreshJSON.put("APP_IDS", (Object)appGroupIds);
            appRefreshJSON.put("GROUP_IDS", (Object)resourceIds);
            appRefreshJSON.put("DEVICE_IDS", (Object)deviceIds);
            if (status != -1) {
                appRefreshJSON.put("STATUS", (Object)status);
            }
            new AppStatusRefreshHandler().refreshAppStatusForGroup(appRefreshJSON);
        }
        catch (final JSONException e) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in Processing refreshAppStatusForDeviceGroup", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in Processing refreshAppStatusForDeviceGroup", e2);
            throw e2;
        }
    }
    
    public JSONObject getMemberDeviceDetails(final SelectQuery groupMemberQuery) throws Exception {
        final JSONArray memberDetails = new JSONArray();
        final JSONArray availableMemberDetails = new JSONArray();
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)groupMemberQuery);
        while (dataSetWrapper.next()) {
            final JSONObject deviceJSON = new JSONObject();
            final JSONObject user = new JSONObject();
            deviceJSON.put("device_id", (Object)dataSetWrapper.getValue("RESOURCE_ID").toString());
            deviceJSON.put("udid", (Object)dataSetWrapper.getValue("UDID").toString());
            if (dataSetWrapper.getValue("SERIAL_NUMBER") != null) {
                deviceJSON.put("serial_number", (Object)dataSetWrapper.getValue("SERIAL_NUMBER").toString());
            }
            final int platformType = Integer.valueOf(dataSetWrapper.getValue("PLATFORM_TYPE").toString());
            deviceJSON.put("platform_type_id", platformType);
            if (platformType == 1) {
                deviceJSON.put("platform_type", (Object)"iOS");
            }
            else if (platformType == 2) {
                deviceJSON.put("platform_type", (Object)"android");
            }
            else if (platformType == 3) {
                deviceJSON.put("platform_type", (Object)"windows");
            }
            else if (platformType == 4) {
                deviceJSON.put("platform_type", (Object)"ChromeOS");
            }
            deviceJSON.put("model", dataSetWrapper.getValue("MODEL"));
            deviceJSON.put("MODEL_TYPE", dataSetWrapper.getValue("MODEL_TYPE"));
            deviceJSON.put("product_name", dataSetWrapper.getValue("PRODUCT_NAME"));
            deviceJSON.put("os_version", dataSetWrapper.getValue("OS_VERSION"));
            user.put("user_name", dataSetWrapper.getValue("ManagedUser"));
            user.put("user_id", (Object)String.valueOf(dataSetWrapper.getValue("MANAGED_USER_ID")));
            deviceJSON.put("device_name", dataSetWrapper.getValue("ManagedDeviceExtn.NAME"));
            deviceJSON.put("last_contact_time", dataSetWrapper.getValue("LAST_CONTACT_TIME"));
            deviceJSON.put("user", (Object)user);
            deviceJSON.put("is_removed", (Object)Boolean.FALSE);
            if (dataSetWrapper.getValue("GROUP_RESOURCE_ID") != null) {
                memberDetails.put((Object)deviceJSON);
            }
            else {
                availableMemberDetails.put((Object)deviceJSON);
            }
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("available", (Object)availableMemberDetails);
        jsonObject.put("members", (Object)memberDetails);
        return jsonObject;
    }
    
    public SelectQuery getMemberDeviceDetailsQuery(final JSONObject jsonObject) throws JSONException, DataAccessException, APIHTTPException {
        GroupFacade.logger.log(Level.INFO, "get Member device details query {0}", jsonObject);
        final Long groupId = jsonObject.optLong("groupId");
        final String searchValue = jsonObject.optString("searchValue");
        final Integer groupCategory = jsonObject.optInt("groupCategory", 1);
        final Boolean associated = jsonObject.optBoolean("associated");
        final SelectQuery groupQuery = MDMUtil.getInstance().getMDMDeviceResourceQuery();
        groupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        Criteria cri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        Criteria groupMemberCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
        if (associated) {
            cri = cri.and(groupCriteria);
        }
        else {
            groupMemberCriteria = groupMemberCriteria.and(groupCriteria);
            cri = cri.and(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0));
        }
        final Join groupJoin = new Join("ManagedDevice", "CustomGroupMemberRel", groupMemberCriteria, 1);
        groupQuery.addJoin(groupJoin);
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        if (searchValue != null && !searchValue.equals("")) {
            final Criteria searchUserCri = new Criteria(Column.getColumn("ManagedUser", "NAME"), (Object)searchValue, 12, false);
            final Criteria searchDeviceCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchValue, 12, false);
            final Criteria searchCri = searchUserCri.or(searchDeviceCri);
            cri = cri.and(searchCri);
        }
        if (jsonObject.optBoolean("deviceGroupUnassigned")) {
            final Criteria deviceCriteria = this.getDeviceGroupAssignedFilterCriteria(groupId);
            if (deviceCriteria != null) {
                cri = cri.and(deviceCriteria);
            }
        }
        if (jsonObject.optString("platformType") != "") {
            final String[] platformType = jsonObject.optString("platformType").split(",");
            final List platformTypeList = Arrays.asList(platformType);
            final Criteria platformTypeCriteria = this.getPlatformCriteria(platformTypeList);
            cri = cri.and(platformTypeCriteria);
        }
        if (jsonObject.optString("osVersion") != "") {
            final String[] osVersion = jsonObject.optString("osVersion").split(",");
            final Criteria osVersionCriteria = this.getOSVersionCriteria(osVersion);
            cri = cri.and(osVersionCriteria);
        }
        if (jsonObject.optString("modelType") != "") {
            final String[] modelType = jsonObject.optString("modelType").split(",");
            final List modelTypeList = Arrays.asList(modelType);
            final Criteria modelTypeCriteria = this.getModelTypeCriteria(modelTypeList);
            cri = cri.and(modelTypeCriteria);
        }
        if (groupQuery.getCriteria() != null) {
            cri = cri.and(groupQuery.getCriteria());
        }
        groupQuery.setCriteria(cri);
        if (groupCategory == 5) {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Join customGroup = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            subQuery.addJoin(customGroup);
            final Criteria groupCategoryCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)5, 0);
            subQuery.setCriteria(groupCategoryCriteria);
            subQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            final DataObject dao = MDMUtil.getPersistence().get(subQuery);
            final List excludeMemberResourceIds = DBUtil.getColumnValuesAsList(dao.getRows("CustomGroupMemberRel"), "MEMBER_RESOURCE_ID");
            final Criteria excludeMemberResIdCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)excludeMemberResourceIds.toArray(), 9);
            groupQuery.setCriteria(groupQuery.getCriteria().and(excludeMemberResIdCri));
        }
        RBDAUtil.getInstance().getRBDAQuery(groupQuery);
        final SortColumn sortCol = new SortColumn(Column.getColumn("ManagedUser", "NAME"), true);
        groupQuery.addSortColumn(sortCol);
        return groupQuery;
    }
    
    public Criteria getDeviceGroupAssignedFilterCriteria(final Long groupId) {
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        final Criteria currentGroupExcludeCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 1);
        subQuery.setCriteria(currentGroupExcludeCri);
        subQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        final DerivedColumn dResCol = new DerivedColumn("MEMBER_RESOURCE_ID", subQuery);
        final Criteria groupUnssignedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)dResCol, 9);
        return groupUnssignedCri;
    }
    
    private Criteria getModelTypeCriteria(final List<Integer> modelType) {
        final Criteria modelTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)modelType.toArray(), 8);
        return modelTypeCri;
    }
    
    private Criteria getOSVersionCriteria(final String[] filterMemberIds) {
        final Criteria osVerCri = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)filterMemberIds, 8);
        return osVerCri;
    }
    
    private Criteria getPlatformCriteria(final List<Integer> filterMemberId) {
        final Criteria osVerCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)filterMemberId.toArray(), 8);
        return osVerCri;
    }
    
    public JSONObject getMemberUserDetails(final SelectQuery groupMemberQuery) throws Exception {
        GroupFacade.logger.log(Level.INFO, "get Member user details query {0}", groupMemberQuery);
        final JSONArray memberDetails = new JSONArray();
        final JSONArray availableMemberDetails = new JSONArray();
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)groupMemberQuery);
        while (dataSetWrapper.next()) {
            final JSONObject deviceJSON = new JSONObject();
            deviceJSON.put("user_name", dataSetWrapper.getValue("NAME"));
            deviceJSON.put("user_id", (Object)String.valueOf(dataSetWrapper.getValue("RESOURCE_ID")));
            deviceJSON.put("domain_name", dataSetWrapper.getValue("DOMAIN_NETBIOS_NAME"));
            deviceJSON.put("is_removed", (Object)Boolean.FALSE);
            if (dataSetWrapper.getValue("GROUP_RESOURCE_ID") != null) {
                memberDetails.put((Object)deviceJSON);
            }
            else {
                availableMemberDetails.put((Object)deviceJSON);
            }
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("available", (Object)availableMemberDetails);
        jsonObject.put("members", (Object)memberDetails);
        return jsonObject;
    }
    
    public SelectQuery getMemberUserDetailsQuery(final JSONObject jsonObject) throws JSONException {
        GroupFacade.logger.log(Level.INFO, "get member user details query, jsonObject:{0}", jsonObject);
        final Long groupId = jsonObject.optLong("groupId");
        final Long customerId = jsonObject.optLong("customerId");
        final String searchValue = jsonObject.optString("searchValue", (String)null);
        final Integer groupCategory = jsonObject.optInt("groupCategory", 1);
        final Boolean associated = jsonObject.optBoolean("associated");
        Criteria cri = null;
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        groupQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 2 }, 8);
        final Criteria additionalCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        groupQuery.setCriteria(resTypeCri.and(additionalCriteria));
        Criteria groupMemberCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), 0);
        final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
        if (associated) {
            cri = groupCriteria;
        }
        else {
            groupMemberCriteria = groupMemberCriteria.and(groupCriteria);
            cri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0);
        }
        final Join groupJoin = new Join("Resource", "CustomGroupMemberRel", groupMemberCriteria, 1);
        groupQuery.addJoin(groupJoin);
        groupQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        if (searchValue != null) {
            final Criteria searchUserCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
            cri = cri.and(searchUserCri.and(groupQuery.getCriteria()));
        }
        final Criteria nonDirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 0, false);
        final Criteria dirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1, false);
        if (jsonObject.optString("userType") != "") {
            final String[] userTypes = jsonObject.optString("userType").split(",");
            final List userTypeList = Arrays.asList(userTypes);
            if (userTypeList.contains("901") && !userTypeList.contains("902")) {
                cri = cri.and(nonDirUseCri.and(groupQuery.getCriteria()));
            }
            else if (!userTypeList.contains("901") && userTypeList.contains("902")) {
                cri = cri.and(dirUseCri.and(groupQuery.getCriteria()));
            }
        }
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        if (cri != null) {
            groupQuery.setCriteria(groupQuery.getCriteria().and(cri).and(userNotInTrashCriteria));
        }
        else {
            groupQuery.setCriteria(userNotInTrashCriteria);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
        groupQuery.addSortColumn(sortCol);
        return groupQuery;
    }
    
    public void validateMemberOperationToGroup(final JSONObject jsonObject, final List memberSet) {
        GroupFacade.logger.log(Level.INFO, "validatem member operation, jsonObject:{0} and member list:{1}", new Object[] { jsonObject, memberSet });
        final Integer groupType = jsonObject.optInt("groupType");
        final Long customerId = jsonObject.optLong("customerId");
        switch (groupType) {
            case 3:
            case 4:
            case 5:
            case 6: {
                new DeviceFacade().validateIfDevicesExists(memberSet, customerId);
                break;
            }
            case 7: {
                new ManagedUserFacade().validateIfUserExists(memberSet, customerId);
                break;
            }
        }
    }
    
    public JSONObject moveToGroup(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            final JSONObject groupProperty = this.validateAndGetGroupDetails(groupId, customerId);
            final int groupType = groupProperty.optInt("GROUP_TYPE");
            logJson.put((Object)"GROUP_TYPE", (Object)groupType);
            logJson.put((Object)"FROM_GROUP_ID", (Object)groupId);
            if (groupType != 6) {
                throw new APIHTTPException("COM0015", new Object[] { "Group should be in device group type" });
            }
            final List memberList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("member_ids"));
            logJson.put((Object)"MEMBER_IDs", (Object)memberList);
            final List<Long> targetGroupIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("target_group_ids"));
            logJson.put((Object)"TARGET_GROUP_IDs", (Object)targetGroupIds);
            final Map groupMap = this.validateAndGetMultiGroupDetails(targetGroupIds, customerId, true);
            final HashSet groupTypes = new HashSet(groupMap.values());
            if (groupTypes.size() != 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Groups are not with the unique group type" });
            }
            if (groupTypes.size() == 1 && !groupTypes.contains(6)) {
                throw new APIHTTPException("COM0015", new Object[] { "Groups are not with the unique group type" });
            }
            GroupFacade.logger.log(Level.INFO, "move group, source group id:{0}, target group id:{1} and member ids:{2}", new Object[] { groupId, targetGroupIds, memberList });
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupType", groupProperty.get("GROUP_TYPE"));
            jsonObject.put("customerId", (Object)customerId);
            if (!memberList.isEmpty()) {
                this.validateMemberOperationToGroup(jsonObject, memberList);
            }
            List groupMembers = MDMGroupHandler.getGroupMemberIdList(groupId);
            groupMembers = ((groupMembers != null) ? groupMembers : new ArrayList());
            final List notAvailableList = new ArrayList(memberList);
            notAvailableList.removeAll(groupMembers);
            if (!notAvailableList.isEmpty()) {
                throw new APIHTTPException("GRP0002", new Object[] { APIUtil.getCommaSeperatedString(notAvailableList), groupId });
            }
            Boolean isSucess = false;
            for (final Long targetGroupId : targetGroupIds) {
                final JSONObject membergroupObjects = new JSONObject();
                membergroupObjects.put("groupId", (Object)targetGroupId);
                membergroupObjects.put("resourceId", (Object)memberList.toArray(new Long[memberList.size()]));
                membergroupObjects.put("customerId", (Object)customerId);
                membergroupObjects.put("isMove", true);
                membergroupObjects.put("userId", (Object)APIUtil.getUserID(message));
                isSucess = MDMGroupHandler.getInstance().addMembertoGroup(membergroupObjects);
            }
            if (isSucess) {
                final JSONObject removemembergroupObjects = new JSONObject();
                removemembergroupObjects.put("groupId", (Object)groupId);
                removemembergroupObjects.put("resourceId", (Object)memberList.toArray(new Long[memberList.size()]));
                removemembergroupObjects.put("customerId", (Object)customerId);
                removemembergroupObjects.put("isMove", true);
                removemembergroupObjects.put("userId", (Object)APIUtil.getUserID(message));
                isSucess = MDMGroupHandler.getInstance().removeMemberfromGroup(removemembergroupObjects);
                if (isSucess) {
                    final String sLoggedOnUserName = APIUtil.getUserName(message);
                    final JSONObject moveGroupJson = new JSONObject();
                    moveGroupJson.put("sourceGroupId", (Object)groupId);
                    moveGroupJson.put("customerId", (Object)customerId);
                    moveGroupJson.put("UserName", (Object)sLoggedOnUserName);
                    final JSONArray resourceArray = JSONUtil.getInstance().convertListToStringJSONArray(memberList);
                    moveGroupJson.put("memberIds", (Object)resourceArray);
                    moveGroupJson.put("groupType", groupType);
                    for (final Long targetGroupId2 : targetGroupIds) {
                        moveGroupJson.put("targetGroupId", (Object)targetGroupId2);
                        MDMGroupHandler.getInstance().moveMemberToGroupEventLogEntry(moveGroupJson);
                    }
                }
            }
            final JSONObject resultJson = new JSONObject();
            resultJson.put("success", (Object)isSucess);
            remarks = "update-success";
            return resultJson;
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "error in moveToGroup()", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "MOVE_GROUP_MEMBER", logJson);
        }
    }
    
    public Map validateAndGetMultiGroupDetails(final List<Long> groupIds, final Long customerID, final boolean memberOperation) throws APIHTTPException {
        if (groupIds.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        GroupFacade.logger.log(Level.INFO, "validate the group list , group ids:{0]", groupIds);
        final Map<Long, Integer> groupTypeMap = new HashMap<Long, Integer>();
        try {
            SelectQuery selectQuery = this.getGroupValidationBaseQuery();
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupIds.toArray(), 8)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupExtn", "CREATED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final List<String> roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            final Boolean isGroupWrite = roles.contains("MDM_GroupMgmt_Write");
            final Boolean isGroupAdmin = roles.contains("MDM_GroupMgmt_Admin");
            final Boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, true);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> customGroupRows = dataObject.getRows("CustomGroup");
            final Iterator<Row> resourceList = dataObject.getRows("Resource", new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1));
            if (memberOperation && resourceList.hasNext()) {
                final List adGroupList = DBUtil.getColumnValuesAsList((Iterator)resourceList, "DOMAIN_NETBIOS_NAME");
                throw new APIHTTPException("GRP0003", new Object[] { adGroupList });
            }
            while (customGroupRows.hasNext()) {
                final Row customGroupRow = customGroupRows.next();
                final Long groupId = (Long)customGroupRow.get("RESOURCE_ID");
                if (memberOperation && isMDMAdmin) {
                    final Long userId = DMUserHandler.getUserIdForLoginId(loginID);
                    final Criteria customGroupExtnCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"), (Object)groupId, 0);
                    final Row customGroupExtnRow = dataObject.getRow("CustomGroupExtn", customGroupExtnCriteria);
                    if (customGroupExtnRow != null) {
                        final Long createdUserId = (Long)customGroupExtnRow.get("CREATED_BY");
                        final Long createdByLoginId = DMUserHandler.getLoginIdForUserId(createdUserId);
                        if (createdByLoginId != null) {
                            final Boolean isCreatedByMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdByLoginId, true);
                            if (!createdUserId.equals(userId) && !isCreatedByMDMAdmin) {
                                throw new APIHTTPException("GRP0003", new Object[] { groupId });
                            }
                        }
                    }
                }
                else if (memberOperation && isGroupWrite) {
                    final Criteria customGroupExtnCriteria2 = new Criteria(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"), (Object)groupId, 0);
                    final Row customGroupExtnRow2 = dataObject.getRow("CustomGroupExtn", customGroupExtnCriteria2);
                    if (customGroupExtnRow2 != null) {
                        final Long createdUserId2 = (Long)customGroupExtnRow2.get("CREATED_BY");
                        final Long createdByLoginId2 = DMUserHandler.getLoginIdForUserId(createdUserId2);
                        if (createdByLoginId2 != null && !createdByLoginId2.equals(loginID)) {
                            throw new APIHTTPException("GRP0003", new Object[] { groupId });
                        }
                    }
                }
                groupTypeMap.put(groupId, (Integer)customGroupRow.get("GROUP_TYPE"));
            }
            final List<Long> validateGroupIds = new ArrayList<Long>(groupIds);
            validateGroupIds.removeAll(groupTypeMap.keySet());
            if (!validateGroupIds.isEmpty()) {
                final String remark = "Group Id : " + APIUtil.getCommaSeperatedString(validateGroupIds);
                throw new APIHTTPException("COM0008", new Object[] { remark });
            }
            return groupTypeMap;
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "exception occurred in validateAndGetMultiGroupDetails", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject updateMembersToTempgroup(final JSONObject message) throws APIHTTPException {
        final JSONObject groupResponseJSON = new JSONObject();
        try {
            final JSONObject messageBody = message.optJSONObject("msg_body");
            final JSONArray memberIdArray = messageBody.getJSONArray("member_ids");
            final Integer groupType = messageBody.optInt("group_type");
            final List<Long> memberIds = JSONUtil.getInstance().convertLongJSONArrayTOList(memberIdArray);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupType", (Object)groupType);
            jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
            if (!memberIds.isEmpty()) {
                this.validateMemberOperationToGroup(jsonObject, memberIds);
            }
            MDMGroupHandler.getInstance().addorUpdateTempGroupMemberIds(memberIds.toArray(new Long[memberIds.size()]), 0);
            groupResponseJSON.put("success", true);
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, "Exception occoured in updateMembersToTempgroup", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return groupResponseJSON;
    }
    
    public JSONObject getADGroupDetails(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject messageBody = message.optJSONObject("msg_body");
            final JSONArray groupIdArray = messageBody.getJSONArray("group_ids");
            final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
            final Long customerId = APIUtil.getCustomerID(message);
            this.validateIfGroupsExists(groupIdList, customerId);
            Boolean isSyncing = false;
            final JSONObject resp = new JSONObject();
            resp.put("is_cyclic", MDMGroupHandler.getInstance().isInCycle(groupIdList));
            isSyncing = MDMGroupHandler.getInstance().isGroupInSync(groupIdList.toArray(new Long[groupIdList.size()]), customerId);
            resp.put("is_syncing", (Object)isSyncing);
            return resp;
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in getting ADGroupdetails :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject filterStaticUniqueGroupDevices(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject messageBody = message.optJSONObject("msg_body");
            final JSONArray resourceIds = messageBody.getJSONArray("member_ids");
            if (resourceIds == null) {
                throw new APIHTTPException("COM0005", new Object[] { "member_ids" });
            }
            final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            final Long groupId = messageBody.getLong("group_id");
            if (groupId == null) {
                throw new APIHTTPException("COM0005", new Object[] { "group_id" });
            }
            this.validateIfGroupsExists(Arrays.asList(groupId), APIUtil.getCustomerID(message));
            new DeviceFacade().validateIfDevicesExists(deviceIdList, APIUtil.getCustomerID(message));
            GroupFacade.logger.log(Level.INFO, "GroupID:{0}; Filter static unique group members from {1}", new Object[] { groupId, resourceIds });
            JSONObject staticUniqueData = MDMGroupHandler.getInstance().filterStaticUniqueGroupDevices(groupId, deviceIdList.toArray(new Long[deviceIdList.size()]));
            staticUniqueData = ((staticUniqueData != null) ? staticUniqueData : new JSONObject());
            GroupFacade.logger.log(Level.INFO, "Filter static unique group members from {0}; Static Unique group membters {1}", new Object[] { groupId, staticUniqueData.toString() });
            return staticUniqueData;
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.WARNING, "Exception occurred while filterStaticUniqueGroupDevices", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private int getGroupActionCount(final Long groupId) {
        int grpActionCount = 0;
        try {
            final UnionQuery unionQuery = this.getGroupActionHistoryUnionQuery(groupId);
            final String uqString = RelationalAPI.getInstance().getSelectSQL((Query)unionQuery);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)uqString);
            while (dmDataSetWrapper.next()) {
                ++grpActionCount;
            }
        }
        catch (final Exception ex) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in getting GroupActionCount :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return grpActionCount;
    }
    
    public UnionQuery getGroupActionHistoryUnionQuery(final Long groupId) {
        final Criteria grpCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)groupId, 0);
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
        final Column recent_action_id = new Column("GroupActionHistory", "GROUP_ACTION_ID").maximum();
        recent_action_id.setColumnAlias("GROUP_ACTION_ID");
        selectQuery1.addSelectColumn(recent_action_id);
        final Column action = new Column("GroupActionHistory", "ACTION_ID");
        selectQuery1.setCriteria(grpCriteria);
        selectQuery1.addSelectColumn(action);
        final List groupByList = new ArrayList();
        groupByList.add(action);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        selectQuery1.setGroupByClause(groupByClause);
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
        final Column actionId = new Column("GroupActionHistory", "GROUP_ACTION_ID");
        actionId.setColumnAlias("GROUP_ACTION_ID");
        selectQuery2.addSelectColumn(actionId);
        selectQuery2.addSelectColumn(action);
        selectQuery2.setCriteria(new Criteria(new Column("GroupActionHistory", "LAST_MODIFIED_TIME"), (Object)(MDMUtil.getCurrentTimeInMillis() - 604800000), 4).and(grpCriteria));
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)selectQuery1, (Query)selectQuery2, false);
        return unionQuery;
    }
    
    public HashMap getAssociatedGroupsForResList(final List resourceList) {
        DMDataSetWrapper ds = null;
        final HashMap<Long, ArrayList> hashMap = new HashMap<Long, ArrayList>();
        try {
            SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Join resourceJoin = new Join("CustomGroupMemberRel", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            query = RBDAUtil.getInstance().getRBDAQuery(query);
            query.addJoin(resourceJoin);
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "NAME"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            Criteria cRes = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            cRes = cRes.and(new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getMDMGroupType().toArray(), 8));
            query.setCriteria(cRes);
            final long stime = System.currentTimeMillis();
            ds = DMDataSetWrapper.executeQuery((Object)query);
            GroupFacade.logger.log(Level.INFO, "processPreRendering(): Query Execution Time - {0}", System.currentTimeMillis() - stime);
            while (ds.next()) {
                ArrayList groupNames = null;
                final Long resourceID = (Long)ds.getValue("MEMBER_RESOURCE_ID");
                if (hashMap.containsKey(resourceID)) {
                    groupNames = hashMap.get(resourceID);
                }
                else {
                    groupNames = new ArrayList();
                }
                groupNames.add(ds.getValue("NAME"));
                hashMap.put(resourceID, groupNames);
            }
        }
        catch (final Exception e) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in getAssociatedGroupsForResList", e);
        }
        return hashMap;
    }
    
    public JSONArray getAssociatedGroupNamesWithResId(final Long resId, final long customerID) {
        final JSONArray groups = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Criteria deviceToUserJoinCri = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria deviceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resId, 0);
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", deviceToUserJoinCri.and(deviceCri), 2));
        final Criteria deviceJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria userJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), 0);
        selectQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", deviceJoinCri.or(userJoinCri), 1));
        selectQuery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroupMemberRel", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria groupTypeCri1 = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 8, 9 }, 9);
        final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(groupTypeCri1.and(customerCri));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        try {
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator rows = dataObject.getRows("Resource");
            while (rows.hasNext()) {
                final Row row = rows.next();
                groups.put(row.get("NAME"));
            }
        }
        catch (final DataAccessException e) {
            GroupFacade.logger.log(Level.SEVERE, "Exception in getAssociatedGroupNamesWithResId", (Throwable)e);
        }
        return groups;
    }
    
    static {
        GroupFacade.logger = Logger.getLogger("MDMApiLogger");
        GroupFacade.allMDMGroupTypes = Arrays.asList(7, 6);
    }
}
