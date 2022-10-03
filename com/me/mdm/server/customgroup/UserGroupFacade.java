package com.me.mdm.server.customgroup;

import com.adventnet.persistence.DataObject;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Map;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.api.APIActionsHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class UserGroupFacade
{
    protected static Logger logger;
    static Long mapping_id;
    
    public JSONObject getGroup(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            if (groupId == null) {
                throw new APIHTTPException("ENR00105", new Object[0]);
            }
            final JSONObject groupMap = new GroupFacade().validateAndGetGroupDetails(groupId, APIUtil.getCustomerID(message));
            if (groupMap == null) {
                throw new APIHTTPException("COM0008", new Object[] { " user_group_id" + groupId });
            }
            if (Integer.valueOf(groupMap.get("GROUP_TYPE").toString()) != 7) {
                throw new APIHTTPException("COM0008", new Object[] { " user_group_id" + groupId });
            }
            final JSONObject json = new JSONObject();
            json.put("NAME", groupMap.get("NAME"));
            json.put("GROUP_ID", (Object)groupMap.get("RESOURCE_ID").toString());
            json.put("DESCRIPTION", groupMap.get("DESCRIPTION"));
            return json;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getGroups(final JSONObject request) throws APIHTTPException {
        final JSONArray result = new JSONArray();
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
            final Join groupResourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            groupQuery.addJoin(groupResourceJoin);
            final Criteria criteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0).and(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0);
            groupQuery.setCriteria(criteria);
            groupQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)groupQuery, conn);
            while (ds.next()) {
                final JSONObject groupMap = new JSONObject();
                groupMap.put("group_id", (Object)ds.getValue("RESOURCE_ID").toString());
                groupMap.put("NAME", ds.getValue("NAME"));
                result.put((Object)groupMap);
            }
            final JSONObject response = new JSONObject();
            response.put("user_groups", (Object)result);
            return response;
        }
        catch (final JSONException | QueryConstructionException | SQLException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    public Object addGroup(final JSONObject message) throws APIHTTPException {
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
            final String domain = requestJSON.optString("domain", "MDM");
            if (MDMCustomGroupUtil.getInstance().checkIfExist(name, domain)) {
                throw new APIHTTPException("COM0010", new Object[] { "name : " + name });
            }
            final String description = requestJSON.optString("description");
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupType = 7;
            cgDetails.groupCategory = 1;
            cgDetails.customerId = APIUtil.getCustomerID(message);
            cgDetails.domainName = domain;
            cgDetails.groupDescription = description;
            cgDetails.groupName = name;
            cgDetails.userId = APIUtil.getUserID(message);
            final JSONObject groupResponseJSON = MDMGroupHandler.getInstance().addOrUpdateMDMGroup(cgDetails);
            final JSONObject resourceJSON = message.getJSONObject("msg_header").getJSONObject("resource_identifier");
            resourceJSON.put("user_group_id", (Object)JSONUtil.optLongForUVH(groupResponseJSON, "RESOURCE_ID", (Long)null));
            final Long resID = JSONUtil.optLongForUVH(groupResponseJSON, "RESOURCE_ID", (Long)null);
            if (resID != null) {
                final List<Long> resIDs = new ArrayList<Long>();
                resIDs.add(resID);
                APIActionsHandler.getInstance().invokeAPIActionResourceListener(resIDs, resIDs, null, 3);
            }
            final JSONObject messageHeader = message.getJSONObject("msg_header");
            messageHeader.put("resource_identifier", (Object)resourceJSON);
            message.put("msg_header", (Object)messageHeader);
            return this.getGroup(message);
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject modifyGroup(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            if (groupId == null) {
                throw new APIHTTPException("ENR00105", new Object[0]);
            }
            final JSONObject groupMap = new GroupFacade().validateAndGetGroupDetails(groupId, APIUtil.getCustomerID(message));
            if (groupMap == null) {
                throw new APIHTTPException("COM0008", new Object[] { "group_id :" + groupId });
            }
            final Integer groupType = Integer.valueOf((String)groupMap.get("GROUP_TYPE"));
            if (groupType != 7) {
                throw new APIHTTPException("COM0008", new Object[] { "user_group_id - " + groupId });
            }
            MDMCustomGroupDetails cgDetails = null;
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            final String domain = requestJSON.optString("domain", "MDM");
            String name = requestJSON.optString("name", (String)null);
            if (name == null) {
                name = groupMap.get("NAME").toString();
            }
            else if (MDMCustomGroupUtil.getInstance().checkIfExist(name, domain) && !name.equalsIgnoreCase(groupMap.get("NAME").toString())) {
                throw new APIHTTPException("COM0010", new Object[] { "name : " + name });
            }
            String description = requestJSON.optString("description", (String)null);
            if (description == null) {
                description = groupMap.get("DESCRIPTION").toString();
            }
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.resourceId = groupId;
            cgDetails.groupType = 7;
            cgDetails.groupCategory = 1;
            cgDetails.customerId = APIUtil.getCustomerID(message);
            cgDetails.domainName = domain;
            cgDetails.groupDescription = description;
            cgDetails.groupName = name;
            cgDetails.resourceIds = this.getGroupMembersAsArray(groupId);
            cgDetails.userId = APIUtil.getUserID(message);
            MDMGroupHandler.getInstance().addOrUpdateMDMGroup(cgDetails);
            return this.getGroup(message);
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Long[] getGroupMembersAsArray(final Long groupId) {
        List members = MDMGroupHandler.getGroupMemberIdList(groupId);
        members = ((members != null) ? members : new ArrayList());
        final List<Long> res = new ArrayList<Long>();
        for (final Object member : members) {
            res.add(Long.valueOf(String.valueOf(member)));
        }
        return res.toArray(new Long[res.size()]);
    }
    
    public void deleteGroups(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            HashSet<Long> groupSet = null;
            if (groupId != 0L) {
                groupSet = new HashSet<Long>(Arrays.asList(groupId));
            }
            else {
                groupSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_group_ids")));
            }
            final Map groupMap = new GroupFacade().validateAndGetMultiGroupDetails(new ArrayList<Long>(groupSet), APIUtil.getCustomerID(message), true);
            MDMGroupHandler.getInstance().deleteGroup(groupSet.toArray(new Long[0]), APIUtil.getUserID(message));
            MDMAppMgmtHandler.getInstance().deleteUpdateConfFromResource(APIUtil.getCustomerID(message));
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getMembers(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            new GroupFacade().validateAndGetGroupDetails(groupId, APIUtil.getCustomerID(message));
            final JSONObject groupDetails = new JSONObject();
            final JSONArray members = JSONUtil.getInstance().convertListToJSONArray(DBUtil.getColumnValuesAsList(MDMUtil.getPersistence().get("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0)).getRows("CustomGroupMemberRel"), "MEMBER_RESOURCE_ID"));
            groupDetails.put("user_ids", (Object)members);
            return groupDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addMembers(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            final MDMGroupHandler handler = MDMGroupHandler.getInstance();
            final Long groupID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            final Long userId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_id", (Long)null);
            final JSONObject groupMap = new GroupFacade().validateAndGetGroupDetails(groupID, APIUtil.getCustomerID(message));
            final Integer groupType = 7;
            List<Long> userSet;
            if (userId == 0L) {
                userSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            else {
                userSet = Arrays.asList(userId);
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupType", (Object)groupType);
            jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
            if (!userSet.isEmpty()) {
                new GroupFacade().validateMemberOperationToGroup(jsonObject, userSet);
            }
            final Long customerId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("filters"), "customer_id", (Long)null);
            if (!MDMGroupHandler.isGroup(groupID) || groupType != 7) {
                throw new APIHTTPException("COM0008", new Object[] { "user_group_id" + groupID });
            }
            final JSONObject membergroupObjects = new JSONObject();
            membergroupObjects.put("groupId", (Object)groupID);
            membergroupObjects.put("resourceId", (Object)userSet.toArray(new Long[userSet.size()]));
            membergroupObjects.put("customerId", (Object)customerId);
            membergroupObjects.put("isMove", false);
            membergroupObjects.put("userId", (Object)APIUtil.getUserID(message));
            if (handler.addMembertoGroup(membergroupObjects)) {
                return this.getMembers(message);
            }
            return result;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteMembers(final JSONObject message) throws APIHTTPException {
        try {
            final MDMGroupHandler handler = MDMGroupHandler.getInstance();
            final Long groupID = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            final Long userId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_id", (Long)null);
            final List<Long> groupSet = new ArrayList<Long>();
            groupSet.add(groupID);
            final Map groupMap = new GroupFacade().validateAndGetMultiGroupDetails(groupSet, APIUtil.getCustomerID(message), false);
            final HashSet groupTypes = new HashSet(groupMap.values());
            final Integer groupType = groupTypes.iterator().next();
            List<Long> userSet;
            if (userId == 0L) {
                userSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            else {
                userSet = Arrays.asList(userId);
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupType", (Object)groupType);
            jsonObject.put("customerId", (Object)APIUtil.getCustomerID(message));
            if (!userSet.isEmpty()) {
                new GroupFacade().validateMemberOperationToGroup(jsonObject, userSet);
            }
            if (!MDMGroupHandler.isGroup(groupID) || groupType != 7) {
                throw new APIHTTPException("COM0008", new Object[] { "user_group_id" + groupID });
            }
            final JSONObject membergroupObjects = new JSONObject();
            membergroupObjects.put("groupId", (Object)groupID);
            membergroupObjects.put("resourceId", (Object)userSet.toArray(new Long[userSet.size()]));
            membergroupObjects.put("isMove", false);
            membergroupObjects.put("userId", (Object)APIUtil.getUserID(message));
            if (!handler.removeMemberfromGroup(membergroupObjects)) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
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
            UserGroupFacade.logger.log(Level.WARNING, "Exception occurred in closeConnection....", ex);
        }
    }
    
    public JSONObject getAppsAssociatedToGroup(final JSONObject request) throws APIHTTPException {
        try {
            final Long userGroupId = JSONUtil.optLongForUVH(request.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", Long.valueOf(0L));
            new GroupFacade().validateAndGetGroupDetails(userGroupId, APIUtil.getCustomerID(request));
            final DataObject appsDO = SyMUtil.getPersistence().get("MdAppCatalogToGroup", new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)userGroupId, 0));
            final Iterator rows = appsDO.getRows("MdAppCatalogToGroup");
            final JSONArray apps = new JSONArray();
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long appID = (Long)row.get("PUBLISHED_APP_ID");
                request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("app_id", (Object)AppsUtil.getInstance().getAppPackageId(appID));
                final JSONObject temp = MDMRestAPIFactoryProvider.getAppFacade().getApp(request);
                apps.put((Object)temp);
            }
            final JSONObject result = new JSONObject();
            result.put("apps", (Object)apps);
            return result;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        UserGroupFacade.logger = Logger.getLogger("UserGroupFacade");
        UserGroupFacade.mapping_id = 1L;
    }
}
