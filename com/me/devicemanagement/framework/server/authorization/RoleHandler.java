package com.me.devicemanagement.framework.server.authorization;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DCUserConstants;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import com.adventnet.db.api.RelationalAPI;

public class RoleHandler
{
    RelationalAPI relapi;
    protected static Logger logger;
    
    public RoleHandler() {
        this.relapi = RelationalAPI.getInstance();
    }
    
    @Deprecated
    public String addRole(final String roleName, final String desc, final String adminName, final String[] roleList) throws Exception {
        String message = "";
        final int errorCode = this.addRoles(roleName, desc, adminName, roleList);
        final DataObject errorDO = SyMUtil.getErrorMessage(errorCode);
        final Row errorRow = errorDO.getRow("ErrorCode");
        final String messageKey = errorRow.get("DETAILED_DESC").toString();
        if (errorCode == 40015) {
            message = I18N.getMsg(messageKey, new Object[] { roleName });
        }
        else {
            message = "Error:" + I18N.getMsg(messageKey, new Object[] { roleName });
        }
        return message;
    }
    
    public int addRoles(final String roleName, final String desc, final String adminName, final String[] roleList) throws Exception {
        try {
            final Criteria checkCriteria = new Criteria(new Column("UMRole", "UM_ROLE_NAME"), (Object)roleName.trim(), 0, false);
            final DataObject checkDobj = SyMUtil.getPersistence().get("UMRole", checkCriteria);
            final Row row = checkDobj.getRow("UMRole", checkCriteria);
            if (row != null) {
                final int errorCode = row.get("STATUS").equals(DCUserConstants.HIDDEN_ROLE) ? 40017 : 40018;
                RoleHandler.logger.log(Level.WARNING, "Role name (" + roleName + ")already exists");
                return errorCode;
            }
            final WritableDataObject dobj = new WritableDataObject();
            final Row roleRow = new Row("UMRole");
            roleRow.set("UM_ROLE_NAME", (Object)roleName);
            roleRow.set("ADMIN_NAME", (Object)adminName);
            roleRow.set("UM_ROLE_DESCRIPTION", (Object)desc);
            roleRow.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            roleRow.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            dobj.addRow(roleRow);
            final Criteria criteria = new Criteria(new Column("UMModule", "ROLE_ID"), (Object)roleList, 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMModule"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator ummoduleRow = dataObject.getRows("UMModule");
            final ArrayList dcModuleID = new ArrayList();
            while (ummoduleRow.hasNext()) {
                final Row moduleRow = ummoduleRow.next();
                final Long moduleID = (Long)moduleRow.get("UM_MODULE_ID");
                dcModuleID.add(moduleRow.get("DC_MODULE_ID"));
                final Row roleMapping = new Row("UMRoleModuleRelation");
                roleMapping.set("UM_MODULE_ID", (Object)moduleID);
                roleMapping.set("UM_ROLE_ID", roleRow.get("UM_ROLE_ID"));
                dobj.addRow(roleMapping);
            }
            this.getHomePageViewDetails((DataObject)dobj, dcModuleID);
            SyMUtil.getPersistence().add((DataObject)dobj);
            final RoleEvent roleEvent = new RoleEvent((Long)dobj.getFirstValue("UMRole", 1));
            RoleListenerHandler.getInstance().invokeRoleListeners(roleEvent, 1000);
            RoleHandler.logger.log(Level.WARNING, "Role : '" + roleName + "' has been successfully added");
            DCEventLogUtil.getInstance().addEvent(707, adminName, null, "dc.admin.uac.ROLE_ADD_SUCCESS", roleName, true);
            return 40015;
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while create role time :", e);
            return 40016;
        }
    }
    
    @Deprecated
    public String modifyRole(final String roleID, final String roleName, final String desc, final String adminName, final String[] roleList) throws Exception {
        String message = "";
        final int errorCode = this.modifyRoles(roleID, roleName, desc, adminName, roleList);
        final DataObject errorDO = SyMUtil.getErrorMessage(errorCode);
        final Row errorRow = errorDO.getRow("ErrorCode");
        final String messageKey = errorRow.get("DETAILED_DESC").toString();
        if (errorCode == 40019) {
            message = I18N.getMsg(messageKey, new Object[] { roleName });
        }
        else {
            message = "Error:" + I18N.getMsg(messageKey, new Object[] { roleName });
        }
        return message;
    }
    
    public int modifyRoles(final String roleID, final String roleName, final String desc, final String adminName, final String[] roleList) throws Exception {
        try {
            Criteria checkCriteria = new Criteria(new Column("UMRole", "UM_ROLE_NAME"), (Object)roleName.trim(), 0, false);
            checkCriteria = checkCriteria.and(new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 1));
            final DataObject checkDobj = SyMUtil.getPersistence().get("UMRole", checkCriteria);
            final Row row = checkDobj.getRow("UMRole", checkCriteria);
            if (row != null) {
                final int errorCode = row.get("STATUS").equals(DCUserConstants.HIDDEN_ROLE) ? 40017 : 40018;
                RoleHandler.logger.log(Level.WARNING, "Role name (" + roleName + ")already exits");
                return errorCode;
            }
            final DataObject dobj = SyMUtil.getPersistence().constructDataObject();
            Criteria roleCriteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 0);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("UMRole");
            updateQuery.setUpdateColumn("UM_ROLE_NAME", (Object)roleName);
            updateQuery.setUpdateColumn("UM_ROLE_DESCRIPTION", (Object)desc);
            updateQuery.setUpdateColumn("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            updateQuery.setCriteria(roleCriteria);
            SyMUtil.getPersistence().update(updateQuery);
            roleCriteria = new Criteria(new Column("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)roleID, 0);
            SyMUtil.getPersistence().delete(roleCriteria);
            final Criteria criteria = new Criteria(new Column("UMModule", "ROLE_ID"), (Object)roleList, 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMModule"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator ummoduleRow = dataObject.getRows("UMModule");
            final ArrayList dcModuleID = new ArrayList();
            while (ummoduleRow.hasNext()) {
                final Row moduleRow = ummoduleRow.next();
                final Long moduleID = (Long)moduleRow.get("UM_MODULE_ID");
                dcModuleID.add(moduleRow.get("DC_MODULE_ID"));
                final Row roleMapping = new Row("UMRoleModuleRelation");
                roleMapping.set("UM_ROLE_ID", (Object)new Long(roleID));
                roleMapping.set("UM_MODULE_ID", (Object)moduleID);
                dobj.addRow(roleMapping);
            }
            this.removeHomePageViewDetails(roleID);
            this.getHomePageViewDetails(dobj, dcModuleID);
            SyMUtil.getPersistence().add(dobj);
            final DataObject dObj = SyMUtil.getPersistence().constructDataObject();
            final RoleEvent roleEvent = new RoleEvent(new Long(roleID));
            RoleListenerHandler.getInstance().invokeRoleListeners(roleEvent, 1003);
            this.removeandCreateAAAEntry(dobj, dObj, roleID);
            SyMUtil.getPersistence().update(dObj);
            RoleHandler.logger.log(Level.WARNING, "Role : '" + roleName + "' has been successfully modified ");
            DCEventLogUtil.getInstance().addEvent(709, adminName, null, "dc.admin.uac.ROLE_MOD_SUCCESS", roleName, true);
            return 40019;
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while Modify role time :", e);
            return 40020;
        }
    }
    
    @Deprecated
    public String deleteRole(final String roleID, final String adminName) throws Exception {
        String message = "";
        String roleName = "";
        final Criteria roleCriteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 0);
        final DataObject dObj = SyMUtil.getPersistence().get("UMRole", roleCriteria);
        if (!dObj.isEmpty()) {
            roleName = (String)dObj.getFirstRow("UMRole").get("UM_ROLE_NAME");
        }
        final int errorCode = this.deleteRoles(roleID, adminName);
        final DataObject errorDO = SyMUtil.getErrorMessage(errorCode);
        final Row errorRow = errorDO.getRow("ErrorCode");
        final String messageKey = errorRow.get("DETAILED_DESC").toString();
        if (errorCode == 40021) {
            message = I18N.getMsg(messageKey, new Object[] { roleName }) + "+true";
        }
        else if (errorCode == 40022) {
            message = I18N.getMsg(messageKey, new Object[] { roleName }) + "+false";
        }
        else {
            message = I18N.getMsg(messageKey, new Object[] { roleName });
        }
        return message;
    }
    
    public int deleteRoles(final String roleID, final String adminName) throws Exception {
        try {
            String roleName = "";
            Criteria roleCriteria = new Criteria(new Column("UsersRoleMapping", "UM_ROLE_ID"), (Object)roleID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "LOGIN_ID"));
            selectQuery.setCriteria(roleCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            roleCriteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 0);
            final DataObject umRoleDO = SyMUtil.getPersistence().get("UMRole", roleCriteria);
            if (umRoleDO == null || umRoleDO.isEmpty() || umRoleDO.size("UMRole") == 0) {
                RoleHandler.logger.log(Level.WARNING, "Role  (" + roleID + ") does not exist for deletion");
                return 40024;
            }
            if (dataObject != null && dataObject.getRow("UsersRoleMapping") == null) {
                if (!umRoleDO.isEmpty()) {
                    final String creatorName = (String)umRoleDO.getFirstRow("UMRole").get("ADMIN_NAME");
                    roleName = (String)umRoleDO.getFirstRow("UMRole").get("UM_ROLE_NAME");
                    if (creatorName.equalsIgnoreCase(EventConstant.DC_SYSTEM_USER)) {
                        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("UMRole");
                        updateQuery.setUpdateColumn("STATUS", (Object)DCUserConstants.HIDDEN_ROLE);
                        updateQuery.setCriteria(roleCriteria);
                        SyMUtil.getPersistence().update(updateQuery);
                        DCEventLogUtil.getInstance().addEvent(711, adminName, null, "dc.admin.uac.DELETE_SUCCESS", roleName, true);
                        RoleHandler.logger.log(Level.WARNING, "Role : '" + roleName + "' disabled Successfully");
                        return 40021;
                    }
                }
                SyMUtil.getPersistence().delete(roleCriteria);
                roleCriteria = new Criteria(new Column("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)roleID, 0);
                SyMUtil.getPersistence().delete(roleCriteria);
                this.removeHomePageViewDetails(roleID);
                RoleHandler.logger.log(Level.WARNING, "Role : '" + roleID + "' is  Successfully deleted ");
                final RoleEvent roleEvent = new RoleEvent(new Long(roleID));
                RoleListenerHandler.getInstance().invokeRoleListeners(roleEvent, 1001);
                DCEventLogUtil.getInstance().addEvent(711, adminName, null, "dc.admin.uac.DELETE_SUCCESS", roleName, true);
                return 40021;
            }
            roleName = (String)DBUtil.getValueFromDB("UMRole", "UM_ROLE_ID", roleID, "UM_ROLE_NAME");
            DCEventLogUtil.getInstance().addEvent(712, adminName, null, "dc.admin.uac.USERS_MAPPED", roleName, true);
            return 40023;
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while delete role time :", e);
            return 40022;
        }
    }
    
    protected void getHomePageViewDetails(final DataObject dobj, final ArrayList dcModuleID) {
        try {
            final Row summaryRow = new Row("Summary");
            summaryRow.set("SUMMARY_CREATE_BY", (Object)"DC");
            dobj.addRow(summaryRow);
            final Criteria criteria = new Criteria(new Column("HomePageSummary", "MODULE_ID"), (Object)dcModuleID.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("HomePageSummary"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(criteria);
            selectQuery.addJoin(new Join("HomePageSummary", "HomeTabModuleOrder", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            final SortColumn sortColumn = new SortColumn(new Column("HomeTabModuleOrder", "DISPLAY_ORDER"), true);
            final SortColumn sortColumn2 = new SortColumn(new Column("HomePageSummary", "DISPLAY_ORDER"), true);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.addSortColumn(sortColumn2);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.size("HomePageSummary") > 0) {
                final Iterator itr = dataObject.getRows("HomePageSummary");
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    final Row summaryGroupRow = new Row("SummaryGroup");
                    summaryGroupRow.set("SUMMARYGROUP_ID", summaryRow.get("SUMMARY_ID"));
                    summaryGroupRow.set("SUMMARY_ID", r.get("SUMMARY_ID"));
                    dobj.addRow(summaryGroupRow);
                }
            }
            final Row r2 = dobj.getFirstRow("UMRoleModuleRelation");
            if (r2 != null) {
                final Row roleSummaryRow = new Row("RoleSummaryGroupMapping");
                roleSummaryRow.set("UM_ROLE_ID", r2.get("UM_ROLE_ID"));
                roleSummaryRow.set("SUMMARYGROUP_ID", summaryRow.get("SUMMARY_ID"));
                dobj.addRow(roleSummaryRow);
            }
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while getHomePageViewDetails time :", e);
        }
    }
    
    private void removeHomePageViewDetails(final String roleID) {
        try {
            final Criteria roleCriteria = new Criteria(new Column("RoleSummaryGroupMapping", "UM_ROLE_ID"), (Object)roleID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RoleSummaryGroupMapping"));
            selectQuery.addSelectColumn(new Column("RoleSummaryGroupMapping", "SUMMARYGROUP_ID"));
            selectQuery.addSelectColumn(new Column("RoleSummaryGroupMapping", "UM_ROLE_ID"));
            selectQuery.setCriteria(roleCriteria);
            DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.getRow("RoleSummaryGroupMapping") != null) {
                final Row r = dataObject.getRow("RoleSummaryGroupMapping");
                final Long summaryId = (Long)r.get("SUMMARYGROUP_ID");
                final Criteria summaryCriteria = new Criteria(new Column("Summary", "SUMMARY_ID"), (Object)summaryId, 0);
                dataObject = SyMUtil.getPersistence().get("Summary", summaryCriteria);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final String creatorName = (String)dataObject.getFirstRow("Summary").get("SUMMARY_CREATE_BY");
                    if (creatorName.equalsIgnoreCase(EventConstant.DC_SYSTEM_USER)) {
                        final Criteria roleSummaryGroupCriteria = new Criteria(new Column("RoleSummaryGroupMapping", "SUMMARYGROUP_ID"), (Object)summaryId, 0);
                        final Criteria userSummaryCriteria = new Criteria(new Column("UserSummaryMapping", "SUMMARYGROUP_ID"), (Object)summaryId, 0);
                        SyMUtil.getPersistence().delete(roleSummaryGroupCriteria);
                        SyMUtil.getPersistence().delete(userSummaryCriteria);
                        return;
                    }
                }
                SyMUtil.getPersistence().delete(summaryCriteria);
            }
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while removeHomePageViewDetails time :", e);
        }
    }
    
    private void removeandCreateAAAEntry(final DataObject dataObj, final DataObject dObj, final String roleID) {
        try {
            final Criteria roleCriteria = new Criteria(new Column("UsersRoleMapping", "UM_ROLE_ID"), (Object)roleID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(roleCriteria);
            selectQuery.addJoin(new Join("UsersRoleMapping", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final String addFeatureRoles = UserMgmtUtil.getUserMgmtParameter("EPR_FEATURE_VERSION");
            if (dataObject.size("AaaAuthorizedRole") > 0) {
                Criteria criteria = new Criteria(new Column("AaaRole", "NAME"), (Object)"All_Managed_Computer", 0);
                final Object allCompRoleID = dataObject.getValue("AaaRole", "ROLE_ID", criteria);
                final Criteria allManagedMobileRoleNameCri = new Criteria(new Column("AaaRole", "NAME"), (Object)"All_Managed_Mobile_Devices", 0);
                final Object allManangedDevicesRoleId = dataObject.getValue("AaaRole", "ROLE_ID", allManagedMobileRoleNameCri);
                final Criteria allManagedNWDeviceRoleNameCri = new Criteria(new Column("AaaRole", "NAME"), (Object)"All_Managed_NetworkDevices", 0);
                final Object allManangedNWDevicesRoleId = dataObject.getValue("AaaRole", "ROLE_ID", allManagedNWDeviceRoleNameCri);
                criteria = new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), allCompRoleID, 1);
                if (allManangedDevicesRoleId != null) {
                    criteria = criteria.and(new Criteria(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), allManangedDevicesRoleId, 1));
                }
                if (allManangedNWDevicesRoleId != null) {
                    criteria = criteria.and(new Criteria(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), allManangedNWDevicesRoleId, 1));
                }
                final String productType = LicenseProvider.getInstance().getProductType();
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                if (productType.equalsIgnoreCase("Patch")) {
                    final Criteria patcheditionRoleCri = new Criteria(new Column("AaaRole", "NAME"), (Object)"Patch_Edition_Role", 0);
                    final Object patchEditionRoleIDObj = dataObject.getValue("AaaRole", "ROLE_ID", patcheditionRoleCri);
                    if (patchEditionRoleIDObj != null) {
                        final Criteria patchEditionCri = new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), patchEditionRoleIDObj, 1);
                        criteria = criteria.and(patchEditionCri);
                    }
                }
                if (((productType.equalsIgnoreCase("Enterprise") || productType.equalsIgnoreCase("UEM")) && licenseType.equals("R")) || (productType.equalsIgnoreCase("Tools_Professional") && licenseType.equals("R")) || productType.equalsIgnoreCase("Patch") || licenseType.equals("F") || productType.equalsIgnoreCase("TOOLSADDON") || licenseType.equals("T") || addFeatureRoles != null) {
                    final List<Long> featureIds = DMUserHandler.getEnterpriseRoleIds();
                    if (!featureIds.isEmpty()) {
                        criteria = criteria.and(new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), (Object)featureIds.toArray(new Long[featureIds.size()]), 9));
                    }
                }
                final Criteria restrictRoleCrit = new Criteria(new Column("AaaRole", "NAME"), (Object)"RESTRICT_USER_TASKS", 0);
                final Object restrictTaskRole = dataObject.getValue("AaaRole", "ROLE_ID", restrictRoleCrit);
                if (restrictTaskRole != null) {
                    criteria = criteria.and(new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), (Object)restrictTaskRole, 1));
                }
                if (dataObject.getRow("AaaAuthorizedRole") != null) {
                    dataObject.deleteRows("AaaAuthorizedRole", criteria);
                }
            }
            final List<Long> list = DMUserHandler.getRoleList(roleID);
            final Iterator itr = dataObject.getRows("AaaAccount");
            final Row summaryID = dataObj.getRow("Summary");
            final Object sID = summaryID.get("SUMMARY_ID");
            while (itr.hasNext()) {
                final Row r = itr.next();
                for (int i = 0; i < list.size(); ++i) {
                    final Long id = list.get(i);
                    final Row accAuthRow = new Row("AaaAuthorizedRole");
                    accAuthRow.set("ACCOUNT_ID", r.get("ACCOUNT_ID"));
                    accAuthRow.set("ROLE_ID", (Object)id);
                    dObj.addRow(accAuthRow);
                }
                final Row UserSummaryMappingRow = new Row("UserSummaryMapping");
                UserSummaryMappingRow.set("SUMMARYGROUP_ID", sID);
                UserSummaryMappingRow.set("LOGIN_ID", r.get("LOGIN_ID"));
                dObj.addRow(UserSummaryMappingRow);
                final Long loginId = (Long)r.get("LOGIN_ID");
                DMUserHandler.addOrUpdateAPIKeyForLoginId(loginId);
                final Criteria deleteDisplayOrderCri = new Criteria(new Column("HomePageSummaryDisplayOrder", "LOGIN_ID"), r.get("LOGIN_ID"), 0);
                SyMUtil.getPersistence().delete(deleteDisplayOrderCri);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while removeandCreateAAAEntry time :", e);
        }
    }
    
    public long getDCModuleId(final String roleName) {
        long mouleId = 0L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMModule"));
            selectQuery.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria roleCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)roleName, 0);
            selectQuery.setCriteria(roleCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("UMModule");
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    mouleId = (long)r.get("DC_MODULE_ID");
                }
            }
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.WARNING, "Exception while getting value in UMModule table, Methodname : getDCModuleId , Exception :", e);
        }
        return mouleId;
    }
    
    public Boolean hasUmRoleApplicationPermission(final String applicationName, final Long umRoleId) throws DataAccessException {
        final SelectQuery dmApplicationQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMApplication"));
        final Join dcUserModToApplJoin = new Join("DMApplication", "DMApplnToDCUserModRel", new String[] { "DMAPPLICATION_ID" }, new String[] { "DMAPPLICATION_ID" }, 2);
        final Join umModuleJoin = new Join("DMApplnToDCUserModRel", "UMModule", new String[] { "DC_MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2);
        final Join umRoleModuleRelJoin = new Join("UMModule", "UMRoleModuleRelation", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2);
        dmApplicationQuery.addJoin(dcUserModToApplJoin);
        dmApplicationQuery.addJoin(umModuleJoin);
        dmApplicationQuery.addJoin(umRoleModuleRelJoin);
        final Criteria applicationNameCriteria = new Criteria(Column.getColumn("DMApplication", "DMAPPLICATION_NAME"), (Object)applicationName, 0, (boolean)Boolean.FALSE);
        final Criteria applicationStatusCriteria = new Criteria(Column.getColumn("DMApplication", "DMAPPLICATION_STATUS"), (Object)(boolean)Boolean.TRUE, 0, (boolean)Boolean.FALSE);
        final Criteria umRoleIdCriteria = new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)umRoleId, 0);
        dmApplicationQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "*"));
        dmApplicationQuery.setCriteria(applicationNameCriteria.and(applicationStatusCriteria).and(umRoleIdCriteria));
        final DataObject dao = SyMUtil.getPersistence().get(dmApplicationQuery);
        return !dao.isEmpty();
    }
    
    public Long getRoleID(final String roleName) {
        try {
            return (Long)DBUtil.getValueFromDB("AaaRole", "NAME", roleName, "ROLE_ID");
        }
        catch (final Exception e) {
            RoleHandler.logger.log(Level.SEVERE, "Exception while getting Role ID..." + e);
            return null;
        }
    }
    
    static {
        RoleHandler.logger = Logger.getLogger("UserManagementLogger");
    }
}
