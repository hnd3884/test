package com.me.mdm.server.role;

import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.authentication.DCUserConstants;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authorization.RoleHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RolesFacade
{
    private static final Logger LOGGER;
    
    public JSONObject getRoles(final JSONObject message) {
        try {
            final Long roleId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "role_id", (Long)null);
            JSONObject res;
            if (roleId != null && roleId > 0L) {
                res = this.getRoleDetails(roleId.toString());
            }
            else {
                res = this.getRoles();
            }
            return res;
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while getting Technician details.", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addRoles(final JSONObject message) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            final JSONObject temp = message.getJSONObject("msg_body");
            final String roleName = String.valueOf(temp.get("role_name"));
            secLog.put((Object)"ROLE_NAME", (Object)roleName);
            final String roleDesc = String.valueOf(temp.get("role_description"));
            final org.json.JSONArray tempJSONArray = temp.getJSONArray("role_list");
            final Map<String, String> aaaRoleIdToName = this.getAaaRoleIdToName();
            final JSONArray aaaRoleNames = new JSONArray();
            for (int i = 0; i < tempJSONArray.length(); ++i) {
                final String aaaRoleId = tempJSONArray.get(i).toString();
                aaaRoleNames.add((Object)aaaRoleIdToName.getOrDefault(aaaRoleId, aaaRoleId));
            }
            secLog.put((Object)"AAA_ROLES", (Object)aaaRoleNames);
            final org.json.JSONArray jsonArray = this.verifyAndFixRoleHierarchy(tempJSONArray);
            String[] roleList = new String[jsonArray.length()];
            for (int j = 0; j < jsonArray.length(); ++j) {
                roleList[j] = jsonArray.get(j).toString();
            }
            roleList = MDMUtil.getInstance().populateRoleListOverridedValue(roleList);
            final RoleHandler rh = new RoleHandler();
            this.validateAAARole(jsonArray);
            final String currentlyLoggedInUserMailId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (!ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified()) {
                throw new APIHTTPException("COM0013", new Object[] { I18N.getMsg("dm.mdm.admin_enroll.authorized", new Object[0]) });
            }
            final JSONObject responseJSON = this.addRole(roleList, roleName, roleDesc, currentlyLoggedInUserMailId);
            final int status = responseJSON.getInt("status");
            switch (status) {
                case 2: {
                    throw new APIHTTPException("USR009", new Object[] { roleName });
                }
                case 3: {
                    throw new APIHTTPException("USR010", new Object[] { roleName });
                }
                default: {
                    RolesFacade.LOGGER.log(Level.INFO, "Role Add status: {0}", status);
                    remarks = "create-success";
                    break;
                }
            }
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while adding Role: ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "Role_Addition", secLog, Level.INFO);
        }
    }
    
    private JSONObject addRole(final String[] roleList, final String roleName, final String desc, final String adminName) throws Exception {
        try {
            final RoleHandler roleHandler = new RoleHandler();
            final int errorCode = roleHandler.addRoles(roleName, desc, adminName, roleList);
            final String errorDesc = (String)DBUtil.getValueFromDB("ErrorCode", "ERROR_CODE", (Object)errorCode, "DETAILED_DESC");
            final String responseString = I18N.getMsg(errorDesc, new Object[] { roleName });
            final JSONObject responseJSON = new JSONObject();
            if (responseString.contains(I18N.getMsg("dc.admin.uac.PRE_DEFINED_ROLE_USED", new Object[] { roleName }))) {
                responseJSON.put("status", 2);
            }
            else if (responseString.contains(I18N.getMsg("dc.admin.uac.ROLE_EXISTS", new Object[] { roleName }))) {
                responseJSON.put("status", 3);
            }
            else if (responseString.contains(I18N.getMsg("dc.admin.uac.ROLE_ADD_SUCCESS", new Object[] { roleName }))) {
                responseJSON.put("status", 1);
            }
            responseJSON.put("status_msg", (Object)responseString);
            return responseJSON;
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while adding role: ", e);
            throw e;
        }
    }
    
    private JSONObject modifyRole(final String roleID, final String[] roleList, final String roleName, final String desc, final String adminName) throws Exception {
        try {
            final RoleHandler roleHandler = new RoleHandler();
            final int errorCode = roleHandler.modifyRoles(roleID, roleName, desc, adminName, roleList);
            final String errorDesc = (String)DBUtil.getValueFromDB("ErrorCode", "ERROR_CODE", (Object)errorCode, "DETAILED_DESC");
            final String responseString = I18N.getMsg(errorDesc, new Object[] { roleName });
            final JSONObject responseJSON = new JSONObject();
            if (responseString.contains(I18N.getMsg("dc.admin.uac.PRE_DEFINED_ROLE_USED", new Object[] { roleName }))) {
                responseJSON.put("status", 2);
            }
            else if (responseString.contains(I18N.getMsg("dc.admin.uac.ROLE_EXISTS", new Object[] { roleName }))) {
                responseJSON.put("status", 3);
            }
            else if (responseString.contains(I18N.getMsg("dc.admin.role.modify_role", new Object[] { roleName }))) {
                responseJSON.put("status", 1);
            }
            responseJSON.put("status_msg", (Object)responseString);
            return responseJSON;
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while modifying role: ", e);
            throw e;
        }
    }
    
    private org.json.JSONArray verifyAndFixRoleHierarchy(final org.json.JSONArray rolesJSONArray) throws DataAccessException, JSONException {
        try {
            final JSONObject tempJSON = this.getRolesForRoleForm();
            final JSONObject availableRolesJSON = APIUtil.invertJSONObject(tempJSON);
            final org.json.JSONArray responseJSONArray = new org.json.JSONArray();
            for (int i = 0; i < rolesJSONArray.length(); ++i) {
                final Long aaaRoleId = JSONUtil.optLongForUVH(rolesJSONArray, i, -1L);
                responseJSONArray.put((Object)aaaRoleId);
                final String roleName = String.valueOf(availableRolesJSON.get(String.valueOf(aaaRoleId)));
                final String[] tempArray = roleName.split("_");
                final String accessKey = tempArray[2];
                final String module = tempArray[1];
                final String lowerCase = accessKey.toLowerCase();
                switch (lowerCase) {
                    case "admin": {
                        responseJSONArray.put((Object)JSONUtil.optLongForUVH(tempJSON, "MDM_" + module + "_Write", Long.valueOf(-1L)));
                    }
                    case "write": {
                        responseJSONArray.put((Object)JSONUtil.optLongForUVH(tempJSON, "MDM_" + module + "_Read", Long.valueOf(-1L)));
                        break;
                    }
                }
            }
            return responseJSONArray;
        }
        catch (final DataAccessException | JSONException e) {
            RolesFacade.LOGGER.log(Level.SEVERE, " -- verifyAndFixRoleHierarchy() > Error, ", e);
            throw e;
        }
    }
    
    public void updateRoles(final JSONObject message) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        final RoleHandler rh = new RoleHandler();
        try {
            final JSONObject temp = message.getJSONObject("msg_body");
            final String roleID = String.valueOf(APIUtil.getResourceID(message, "role_id"));
            final String roleName = String.valueOf(temp.get("role_name"));
            secLog.put((Object)"ROLE_NAME", (Object)roleName);
            final String roleDesc = String.valueOf(temp.get("role_description"));
            final org.json.JSONArray tempJSONArray = temp.getJSONArray("role_list");
            final Map<String, String> aaaRoleIdToName = this.getAaaRoleIdToName();
            final JSONArray aaaRoleNames = new JSONArray();
            for (int i = 0; i < tempJSONArray.length(); ++i) {
                final String aaaRoleId = tempJSONArray.get(i).toString();
                aaaRoleNames.add((Object)aaaRoleIdToName.getOrDefault(aaaRoleId, aaaRoleId));
            }
            secLog.put((Object)"AAA_ROLES", (Object)aaaRoleNames);
            final org.json.JSONArray jsonArray = this.verifyAndFixRoleHierarchy(tempJSONArray);
            final Boolean isValidRole = this.validateRole(roleID);
            if (!isValidRole) {
                throw new APIHTTPException("USR001", new Object[] { roleID });
            }
            this.validateAAARole(jsonArray);
            String[] roleList = new String[jsonArray.length()];
            for (int j = 0; j < jsonArray.length(); ++j) {
                roleList[j] = jsonArray.get(j).toString();
            }
            roleList = MDMUtil.getInstance().populateRoleListOverridedValue(roleList);
            final String currentlyLoggedInUserMailId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (!ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified()) {
                throw new APIHTTPException("COM0013", new Object[] { I18N.getMsg("dm.mdm.admin_enroll.authorized", new Object[0]) });
            }
            final JSONObject responseJSON = this.modifyRole(roleID, roleList, roleName, roleDesc, currentlyLoggedInUserMailId);
            final int status = responseJSON.getInt("status");
            switch (status) {
                case 2: {
                    throw new APIHTTPException("USR009", new Object[] { roleName });
                }
                case 3: {
                    throw new APIHTTPException("USR010", new Object[] { roleName });
                }
                default: {
                    RolesFacade.LOGGER.log(Level.INFO, "Role update Status: {0}", status);
                    remarks = "update-success";
                    break;
                }
            }
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while updating role details:", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "Role_Modification", secLog, Level.INFO);
        }
    }
    
    public Boolean validateRole(final String roleID) throws DataAccessException {
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(this.getRoleDetailsQuery(roleID));
            return !dataObject.isEmpty();
        }
        catch (final DataAccessException e) {
            RolesFacade.LOGGER.log(Level.SEVERE, " -- validateRole() > Error, ", (Throwable)e);
            throw e;
        }
    }
    
    public void validateAAARole(final org.json.JSONArray aaaRoleJSONArray) throws Exception {
        try {
            final ArrayList aaaRoleList = new ArrayList();
            for (int i = 0; i < aaaRoleJSONArray.length(); ++i) {
                final Long aaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSONArray, i, -1L);
                aaaRoleList.add(aaaRoleId);
                final DataObject dataObject = MDMUtil.getPersistence().get("AaaRole", new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)aaaRoleId, 0));
                if (dataObject == null || dataObject.isEmpty()) {
                    throw new APIHTTPException("USR011", new Object[] { aaaRoleId });
                }
            }
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, " -- validateAAARole() > Error, ", e);
            throw new APIHTTPException("USR011", new Object[] { aaaRoleJSONArray.toString() });
        }
    }
    
    public void removeRoles(final JSONObject message) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        final JSONArray removedRoleNames = new JSONArray();
        String remarks = "delete-failed";
        final RoleHandler rh = new RoleHandler();
        try {
            Long roleID = APIUtil.getResourceID(message, "role_id");
            org.json.JSONArray rolesJSONArray = new org.json.JSONArray();
            String tempMsg = "";
            if (roleID == -1L) {
                rolesJSONArray = message.getJSONObject("msg_body").getJSONArray("users");
            }
            else {
                rolesJSONArray.put((Object)roleID);
            }
            for (int i = 0; i < rolesJSONArray.length(); ++i) {
                roleID = JSONUtil.optLongForUVH(rolesJSONArray, i, -1L);
                String roleName = "--";
                try {
                    roleName = (String)DBUtil.getValueFromDB("UMRole", "UM_ROLE_ID", (Object)roleID, "UM_ROLE_NAME");
                }
                catch (final Exception ex) {
                    RolesFacade.LOGGER.log(Level.INFO, "Error while getting the role name", ex);
                }
                removedRoleNames.add((Object)roleName);
                final Boolean isValidRole = this.validateRole(String.valueOf(roleID));
                if (!isValidRole) {
                    throw new APIHTTPException("USR001", new Object[] { String.valueOf(roleID) });
                }
                final String currentlyLoggedInUserMailId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                if (!ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified()) {
                    throw new APIHTTPException("COM0013", new Object[] { I18N.getMsg("dm.mdm.admin_enroll.authorized", new Object[0]) });
                }
                final int errorCode = rh.deleteRoles(String.valueOf(roleID), currentlyLoggedInUserMailId);
                final String errorDesc = (String)DBUtil.getValueFromDB("ErrorCode", "ERROR_CODE", (Object)errorCode, "DETAILED_DESC");
                tempMsg = I18N.getMsg(errorDesc, new Object[0]);
                if (tempMsg.contains(I18N.getMsg("dc.admin.role.user_mapped_to_role", new Object[0]))) {
                    throw new APIHTTPException("USR012", new Object[0]);
                }
            }
            RolesFacade.LOGGER.log(Level.INFO, "Role delete Status: {0}", tempMsg);
            remarks = "delete-success";
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.SEVERE, "Exception while processing remove user :", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            secLog.put((Object)"REMOVED_ROLES", (Object)removedRoleNames);
            SecurityOneLineLogger.log("User_Management", "Role_Deletion", secLog, Level.INFO);
        }
    }
    
    public JSONObject getRoleDetails(final String roleId) throws Exception {
        final JSONObject res = new JSONObject();
        try {
            final SelectQuery query = this.getRoleDetailsQuery(roleId);
            final DataObject dataObj = MDMUtil.getPersistence().get(query);
            if (dataObj.isEmpty()) {
                throw new APIHTTPException("USR001", new Object[] { String.valueOf(roleId) });
            }
            final String roleName = (String)dataObj.getFirstValue("UMRole", "UM_ROLE_NAME");
            final String roleDesc = (String)dataObj.getFirstValue("UMRole", "UM_ROLE_DESCRIPTION");
            final String adminName = (String)dataObj.getFirstValue("UMRole", "ADMIN_NAME");
            final Boolean isEditable = (Boolean)dataObj.getFirstValue("UMRole", "IS_EDITABLE");
            final int status = (int)dataObj.getFirstValue("UMRole", "STATUS");
            final Long creationTime = (Long)dataObj.getFirstValue("UMRole", "CREATION_TIME");
            final Long modifiedTime = (Long)dataObj.getFirstValue("UMRole", "MODIFIED_TIME");
            final org.json.JSONArray roleList = new org.json.JSONArray();
            res.put("role_name", (Object)roleName);
            res.put("role_description", (Object)I18N.getMsg(roleDesc, new Object[0]));
            res.put("admin_name", (Object)adminName);
            res.put("is_editable", (Object)isEditable);
            res.put("status", status);
            res.put("created_time", (Object)creationTime);
            res.put("modified_time", (Object)modifiedTime);
            final Iterator umModuleIterator = dataObj.getRows("UMModule");
            final Iterator aaaRoleIterator = dataObj.getRows("AaaRole");
            if (umModuleIterator != null) {
                while (umModuleIterator.hasNext() && aaaRoleIterator.hasNext()) {
                    final JSONObject umModuleJSON = new JSONObject();
                    final Row umModuleRow = umModuleIterator.next();
                    final Row aaaRoleRow = aaaRoleIterator.next();
                    umModuleJSON.put("role_id", umModuleRow.get("ROLE_ID"));
                    umModuleJSON.put("role_name", aaaRoleRow.get("NAME"));
                    roleList.put((Object)umModuleJSON);
                }
                res.put("role_list", (Object)roleList);
                return res;
            }
            return null;
        }
        catch (final Exception e) {
            RolesFacade.LOGGER.log(Level.WARNING, "Exception while getting Role Details :", e);
            throw e;
        }
    }
    
    private SelectQuery getRoleDetailsQuery(final String roleId) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
        query.addJoin(new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
        query.addJoin(new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2));
        query.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        query.addJoin(new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        query.addJoin(new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        final Criteria c = new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)roleId, 0);
        final Criteria userIdCriteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)MDMUtil.getInstance().getLoggedInUserID(), 0);
        query.setCriteria(c.and(userIdCriteria));
        query.addSelectColumn(new Column("UMRole", "UM_ROLE_ID"));
        query.addSelectColumn(new Column("UMRole", "UM_ROLE_NAME"));
        query.addSelectColumn(new Column("UMRole", "UM_ROLE_DESCRIPTION"));
        query.addSelectColumn(new Column("UMRole", "ADMIN_NAME"));
        query.addSelectColumn(new Column("UMRole", "CREATION_TIME"));
        query.addSelectColumn(new Column("UMRole", "MODIFIED_TIME"));
        query.addSelectColumn(new Column("UMRole", "IS_EDITABLE"));
        query.addSelectColumn(new Column("UMRole", "STATUS"));
        query.addSelectColumn(new Column("UMModule", "UM_MODULE_ID"));
        query.addSelectColumn(new Column("UMModule", "ROLE_ID"));
        query.addSelectColumn(new Column("AaaRole", "ROLE_ID"));
        query.addSelectColumn(new Column("AaaRole", "NAME"));
        return query;
    }
    
    public JSONObject getRoles() {
        final JSONObject res = new JSONObject();
        try {
            final SelectQuery query = this.getMDMRoleQuery();
            final DataObject dataObj = MDMUtil.getPersistence().get(query);
            if (dataObj == null) {
                return null;
            }
            final org.json.JSONArray rolesList = new org.json.JSONArray();
            final Iterator rows = dataObj.getRows("UMRole");
            if (rows != null) {
                while (rows.hasNext()) {
                    final Row dcRow = rows.next();
                    final JSONObject roleDetails = new JSONObject();
                    final String createdByName = (String)dcRow.get("ADMIN_NAME");
                    roleDetails.put("role_id", dcRow.get("UM_ROLE_ID"));
                    roleDetails.put("role_name", dcRow.get("UM_ROLE_NAME"));
                    roleDetails.put("created_by_name", (Object)createdByName);
                    Long createdBy = null;
                    try {
                        createdBy = DMUserHandler.getUserID(createdByName);
                    }
                    catch (final Exception e) {
                        RolesFacade.LOGGER.log(Level.INFO, "user not found for user name: {0}", createdByName);
                    }
                    roleDetails.put("created_by", (Object)createdBy);
                    roleDetails.put("created_time", dcRow.get("CREATION_TIME"));
                    roleDetails.put("modified_time", dcRow.get("MODIFIED_TIME"));
                    roleDetails.put("description", (Object)I18N.getMsg(String.valueOf(dcRow.get("UM_ROLE_DESCRIPTION")), new Object[0]));
                    rolesList.put((Object)roleDetails);
                }
                res.put("roles_list", (Object)rolesList);
                return res;
            }
            return null;
        }
        catch (final Exception e2) {
            RolesFacade.LOGGER.log(Level.WARNING, "Exception while getting Role Details :", e2);
            return null;
        }
    }
    
    private SelectQuery getMDMRoleQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UMRole"));
        final Join umRoleModuleRelationJoin = new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1);
        final Join umModuleJoin = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1);
        final Join dcUserModuleExtnJoin = new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        selectQuery.addJoin(umRoleModuleRelationJoin);
        selectQuery.addJoin(umModuleJoin);
        selectQuery.addJoin(dcUserModuleExtnJoin);
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "ADMIN_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "IS_EDITABLE"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "UM_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "DC_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "LICENSE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "PRECEDENCE_LEVEL"));
        selectQuery.addSelectColumn(Column.getColumn("DCUserModuleExtn", "MODULE_ID"));
        final TreeMap roles = SyMUtil.getInstance().getRoleList(LicenseProvider.getInstance().getProductType());
        Criteria criteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)roles.values().toArray(), 8);
        if (DMApplicationHandler.isMdmProduct()) {
            criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)new String[] { "Mobile Device Manager", "Mobile Device User" }, 9));
        }
        if (CustomerInfoUtil.getInstance().isMSP()) {
            criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "STATUS"), (Object)new int[] { DCUserConstants.VISIBLE_ROLE, DCUserConstants.MSP_ROLE }, 8));
        }
        else {
            criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "STATUS"), (Object)DCUserConstants.VISIBLE_ROLE, 0));
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public JSONObject getRolesForRoleForm() throws DataAccessException, JSONException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
            selectQuery.addJoin(new Join("AaaRole", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            Criteria mdmCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_", 10);
            final String productType = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
            if (productType != null && productType.equalsIgnoreCase("Standard")) {
                mdmCriteria = mdmCriteria.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12, false));
            }
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            selectQuery.setCriteria(mdmCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("AaaRole");
            final JSONObject responseJSON = new JSONObject();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long roleId = (Long)row.get("ROLE_ID");
                final String roleName = (String)row.get("NAME");
                responseJSON.put(roleName, (Object)roleId);
            }
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            RolesFacade.LOGGER.log(Level.SEVERE, " -- getRolesForRoleForm() >   Error ", e);
            throw e;
        }
    }
    
    private Map<String, String> getAaaRoleIdToName() {
        final Map<String, String> aaaRoleIdToName = new HashMap<String, String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("AaaRole");
            while (rows.hasNext()) {
                final Row row = rows.next();
                aaaRoleIdToName.put(String.valueOf(row.get("ROLE_ID")), String.valueOf(row.get("NAME")));
            }
        }
        catch (final Exception ex) {
            RolesFacade.LOGGER.log(Level.WARNING, "Error while getting aaaRoleId to aaaRoleName mappings", ex);
        }
        return aaaRoleIdToName;
    }
    
    static {
        LOGGER = Logger.getLogger(RolesFacade.class.getName());
    }
}
