package com.me.mdm.server.role;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccess;
import org.apache.commons.collections.MultiHashMap;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.persistence.DataAccessException;
import org.apache.commons.collections.MultiMap;
import com.me.mdm.server.customgroup.AllManagedDeviceGroupHandler;
import com.adventnet.ds.query.SelectQueryImpl;
import java.util.HashMap;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Set;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONObject;
import java.util.Collection;
import java.util.TreeSet;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import java.util.Map;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Level;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class RBDAUtil
{
    Logger logger;
    private static RBDAUtil rbdaUtil;
    public static final String ALL_DEVICE_GROUP_LOGIN_CACHAE = "ALL_DEVICE_GROUP_LOGIN_CACHAE";
    
    private RBDAUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static RBDAUtil getInstance() {
        if (RBDAUtil.rbdaUtil == null) {
            RBDAUtil.rbdaUtil = new RBDAUtil();
        }
        return RBDAUtil.rbdaUtil;
    }
    
    public Criteria getUserDeviceMappingCriteria(final Long loginId) {
        final Criteria userDeviceMappingCriteria = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)loginId, 0);
        return userDeviceMappingCriteria;
    }
    
    public Criteria getUserCustomGroupCriteria(final Long loginId) {
        final Criteria userCustomGroupCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0);
        return userCustomGroupCriteria;
    }
    
    private Criteria getUserCustomGroupExtnCreatedByCriteria(final Long userId) {
        final Criteria userCustomGroupExtnCreatedByCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)userId, 0);
        return userCustomGroupExtnCreatedByCriteria;
    }
    
    private Criteria getUserCustomGroupCriteriaWithStaticGroups(final Long loginId) {
        final Criteria userCustomGroupCriteria = this.getUserCustomGroupCriteria(loginId);
        final Long userId = DMUserHandler.getUserIdForLoginId(loginId);
        final Criteria customGroupExtnCreatedByCriteria = this.getUserCustomGroupExtnCreatedByCriteria(userId);
        final Criteria finalCriteria = userCustomGroupCriteria.or(customGroupExtnCreatedByCriteria);
        return finalCriteria;
    }
    
    private Join getCustomGroupExtnJoin(final String baseTableName, final String baseTableColumn) {
        final Join customGroupExtnJoin = new Join(baseTableName, "CustomGroupExtn", new String[] { baseTableColumn }, new String[] { "RESOURCE_ID" }, 2);
        return customGroupExtnJoin;
    }
    
    public String getUserDeviceMappingCriteriaString(final Long loginId) {
        return " AND UserDeviceMapping.LOGIN_ID=" + loginId + " ";
    }
    
    public String getUserCustomGroupCriteriaString(final Long loginId) {
        final Long userId = DMUserHandler.getUserIdForLoginId(loginId);
        return " AND ((UserCustomGroupMapping.LOGIN_ID=" + loginId + ") " + "OR (" + "CustomGroupExtn.CREATED_BY" + "=" + userId + ")) ";
    }
    
    public Join getUserDeviceMappingJoin(final String baseTableName, final String baseTableColumn) {
        final Join userDeviceMappingJoin = new Join(baseTableName, "UserDeviceMapping", new String[] { baseTableColumn }, new String[] { "RESOURCE_ID" }, 2);
        return userDeviceMappingJoin;
    }
    
    public Join getUserCustomGroupMappingJoin(final String baseTableName, final String baseTableColumn) {
        final Join userCustomGroupMappingJoin = new Join(baseTableName, "UserCustomGroupMapping", new String[] { baseTableColumn }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        return userCustomGroupMappingJoin;
    }
    
    public String getUserDeviceMappingJoinString(final String baseTableName, final String baseTableColumn) {
        return " INNER JOIN UserDeviceMapping ON " + baseTableName + "." + baseTableColumn + "=" + "UserDeviceMapping.RESOURCE_ID" + " ";
    }
    
    public String getUserCustomGroupMappingJoinString(final String baseTableName, final String baseTableColumn) {
        return " LEFT JOIN UserCustomGroupMapping ON " + baseTableName + "." + baseTableColumn + "=" + "UserCustomGroupMapping.GROUP_RESOURCE_ID" + " ";
    }
    
    public SelectQuery getRBDAQuery(final SelectQuery selectQuery) {
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    final boolean managedDeviceTableCheck = selectQuery.getTableList().contains(new Table("ManagedDevice"));
                    final boolean managedGroupTableCheck = selectQuery.getTableList().contains(new Table("CustomGroup"));
                    final Boolean isRBDAGroupCheck = this.hasRBDAGroupCheck(loginID, true);
                    Criteria cgCriteria = null;
                    if (managedDeviceTableCheck) {
                        if (!selectQuery.getTableList().contains(new Table("UserDeviceMapping"))) {
                            selectQuery.addJoin(this.getUserDeviceMappingJoin("ManagedDevice", "RESOURCE_ID"));
                        }
                        cgCriteria = this.getUserDeviceMappingCriteria(loginID);
                    }
                    else if (managedGroupTableCheck && !isRBDAGroupCheck) {
                        if (!selectQuery.getTableList().contains(new Table("UserCustomGroupMapping"))) {
                            selectQuery.addJoin(this.getUserCustomGroupMappingJoin("CustomGroup", "RESOURCE_ID"));
                        }
                        if (!selectQuery.getTableList().contains(new Table("CustomGroupExtn"))) {
                            selectQuery.addJoin(this.getCustomGroupExtnJoin("CustomGroup", "RESOURCE_ID"));
                        }
                        cgCriteria = this.getUserCustomGroupCriteriaWithStaticGroups(loginID);
                    }
                    if (cgCriteria != null) {
                        Criteria baseCriteria = selectQuery.getCriteria();
                        if (baseCriteria != null) {
                            baseCriteria = baseCriteria.and(cgCriteria);
                        }
                        else {
                            baseCriteria = cgCriteria;
                        }
                        selectQuery.setCriteria(baseCriteria);
                    }
                }
            }
            return selectQuery;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting LoginIDs for userID : ", e);
            return selectQuery;
        }
    }
    
    public void modifyRBDAQueryByTechnician(final SelectQuery sQuery) throws Exception {
        Criteria criteria = sQuery.getCriteria();
        final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        if (loginID != null) {
            final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
            if (!isMDMAdmin) {
                final Criteria loginCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), 0);
                if (criteria == null) {
                    criteria = loginCriteria;
                }
                else {
                    criteria = criteria.and(loginCriteria);
                }
            }
        }
        sQuery.setCriteria(criteria);
    }
    
    public SelectQuery getModifiedUserRoleCheckQuery(final SelectQuery sQuery, final String baseTableHavingAAAUserColumn, final String aaaUserColumn) {
        final Join aaaLogin = new Join(baseTableHavingAAAUserColumn, "AaaLogin", new String[] { aaaUserColumn }, new String[] { "USER_ID" }, 2);
        final Join aaaAccount = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
        final Join aaaAuthorizedRoles = new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
        final Join aaaRole = new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
        sQuery.addJoin(aaaLogin);
        sQuery.addJoin(aaaAccount);
        sQuery.addJoin(aaaAuthorizedRoles);
        sQuery.addJoin(aaaRole);
        return sQuery;
    }
    
    public Criteria getProfileCreatedOrModifiedByCriteria(final Long loginId) {
        Criteria userIdCrit = null;
        final Boolean showOnlyUserCreatedProfilesApps = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOnlyUserCreatedProfilesApps");
        if (showOnlyUserCreatedProfilesApps != null && showOnlyUserCreatedProfilesApps && !DMUserHandler.isUserInAdminRole(loginId) && !DMUserHandler.isUserInRole(loginId, "MDM_AppMgmt_Admin") && !DMUserHandler.isUserInRole(loginId, "ModernMgmt_AppMgmt_Admin")) {
            final Long userId = DMUserHandler.getUserIdForLoginId(loginId);
            final Criteria createdByCrit = new Criteria(Column.getColumn("Profile", "CREATED_BY"), (Object)userId, 0);
            final Criteria modifiedByCrit = new Criteria(Column.getColumn("Profile", "LAST_MODIFIED_BY"), (Object)userId, 0);
            userIdCrit = createdByCrit.or(modifiedByCrit);
        }
        return userIdCrit;
    }
    
    public String getProfileCreatedOrModifiedByCriteriaString(final Long loginId) {
        String userIdCrit = "";
        final Boolean showOnlyUserCreatedProfilesApps = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOnlyUserCreatedProfilesApps");
        if (showOnlyUserCreatedProfilesApps != null && showOnlyUserCreatedProfilesApps && !DMUserHandler.isUserInAdminRole(loginId) && !DMUserHandler.isUserInRole(loginId, "MDM_AppMgmt_Admin") && !DMUserHandler.isUserInRole(loginId, "ModernMgmt_AppMgmt_Admin")) {
            final Long userId = DMUserHandler.getUserIdForLoginId(loginId);
            userIdCrit = " AND (Profile.CREATED_BY=" + userId + " OR Profile.LAST_MODIFIED_BY=" + userId + ")";
        }
        return userIdCrit;
    }
    
    public void addOrUpdateModernMgmtScope(final Map<Long, Map<Long, List<Long>>> techniciansCustomerDeviceMap, final Boolean deleteAndAddCustomGroupMemberRels) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            Set<Long> technicianLoginIdList = techniciansCustomerDeviceMap.keySet();
            technicianLoginIdList = this.removeTechsWithMDMAdminPermissions(technicianLoginIdList);
            final List<Long> techniciansWithCustomGrpMapping = new ArrayList<Long>();
            final DataObject customGroupDO = this.getTechCustomGroupDetailsDO(technicianLoginIdList);
            final DataObject userDeviceMappingDO = this.getTechUserDeviceMappingDO(technicianLoginIdList);
            for (final Long technicianLoginID : technicianLoginIdList) {
                final Map<Long, List<Long>> technicianCustomerDeviceMap = techniciansCustomerDeviceMap.get(technicianLoginID);
                final Set<Long> technicianCustomerIdList = technicianCustomerDeviceMap.keySet();
                for (final Long customerId : technicianCustomerIdList) {
                    final List<Long> customerDeviceList = technicianCustomerDeviceMap.get(customerId);
                    if (customerDeviceList != null && !customerDeviceList.isEmpty()) {
                        techniciansWithCustomGrpMapping.add(technicianLoginID);
                        final Criteria customerIdCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                        final Criteria groupResType = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
                        final Iterator cgForCustomerIdRows = customGroupDO.getRows("Resource", customerIdCrit.and(groupResType));
                        final List<Long> cgList = new ArrayList<Long>();
                        while (cgForCustomerIdRows.hasNext()) {
                            final Row cgForCustomerIdRow = cgForCustomerIdRows.next();
                            final Long cgResId = (Long)cgForCustomerIdRow.get("RESOURCE_ID");
                            cgList.add(cgResId);
                        }
                        Criteria loginIdCrit = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginID, 0);
                        if (!cgList.isEmpty()) {
                            final Criteria cgResCrit = new Criteria(Column.getColumn("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), (Object)cgList.toArray(new Long[1]), 8);
                            loginIdCrit = loginIdCrit.and(cgResCrit);
                        }
                        Row userCustomGroupMappingForCustomer = customGroupDO.getRow("UserCustomGroupMapping", loginIdCrit);
                        if (userCustomGroupMappingForCustomer == null) {
                            final MDMCustomGroupDetails newCgDetails = new MDMCustomGroupDetails();
                            newCgDetails.resourceId = null;
                            newCgDetails.groupType = 9;
                            newCgDetails.groupCategory = 1;
                            newCgDetails.customerId = customerId;
                            newCgDetails.domainName = "MDM";
                            newCgDetails.resourceIds = customerDeviceList.toArray(new Long[1]);
                            newCgDetails.groupDescription = "Hidden modern mgmt scope group for techLoginId " + technicianLoginID + " and customerId " + customerId;
                            newCgDetails.groupName = "ModernMgmt_" + technicianLoginID + "_" + customerId;
                            newCgDetails.groupOwnedBy = 1;
                            newCgDetails.isEditable = Boolean.FALSE;
                            MDMGroupHandler.getInstance().addGroup(newCgDetails);
                            userCustomGroupMappingForCustomer = new Row("UserCustomGroupMapping");
                            userCustomGroupMappingForCustomer.set("LOGIN_ID", (Object)technicianLoginID);
                            userCustomGroupMappingForCustomer.set("GROUP_RESOURCE_ID", (Object)newCgDetails.resourceId);
                            customGroupDO.addRow(userCustomGroupMappingForCustomer);
                            for (final Long managedDeviceId : customerDeviceList) {
                                final Row userDeviceMappingForCustomer = new Row("UserDeviceMapping");
                                userDeviceMappingForCustomer.set("LOGIN_ID", (Object)technicianLoginID);
                                userDeviceMappingForCustomer.set("RESOURCE_ID", (Object)managedDeviceId);
                                if (customGroupDO.findRow(userDeviceMappingForCustomer) == null && userDeviceMappingDO.findRow(userDeviceMappingForCustomer) == null) {
                                    customGroupDO.addRow(userDeviceMappingForCustomer);
                                }
                            }
                        }
                        else {
                            final Set<Long> tempDeviceList = new TreeSet<Long>(customerDeviceList);
                            final Long groupId = (Long)userCustomGroupMappingForCustomer.get("GROUP_RESOURCE_ID");
                            if (!deleteAndAddCustomGroupMemberRels) {
                                final Iterator customGroupMemberRelRows = customGroupDO.getRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0));
                                while (customGroupMemberRelRows.hasNext()) {
                                    final Row customerGroupMemberRelRow = customGroupMemberRelRows.next();
                                    final Long memberResourceId = (Long)customerGroupMemberRelRow.get("MEMBER_RESOURCE_ID");
                                    if (memberResourceId != null) {
                                        tempDeviceList.add(memberResourceId);
                                    }
                                }
                            }
                            final JSONObject membergroupObjects = new JSONObject();
                            membergroupObjects.put("groupId", (Object)groupId);
                            membergroupObjects.put("resourceId", (Object)tempDeviceList.toArray(new Long[1]));
                            membergroupObjects.put("customerId", (Object)customerId);
                            membergroupObjects.put("isMove", false);
                            MDMGroupHandler.getInstance().addMembertoGroup(membergroupObjects);
                        }
                    }
                }
            }
            MDMUtil.getPersistence().update(customGroupDO);
            final List<Long> technicianWithDCOnlyScope = this.updateGroupMappingForDcOnlyScope();
            techniciansWithCustomGrpMapping.addAll(technicianWithDCOnlyScope);
            final DeleteQuery allManagedMobileDeviceRoleDeleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaAuthorizedRole");
            allManagedMobileDeviceRoleDeleteQuery.addJoin(new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            allManagedMobileDeviceRoleDeleteQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            final Criteria techLoginCri = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)techniciansWithCustomGrpMapping.toArray(new Long[1]), 8);
            final Criteria roleNameCri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"All_Managed_Mobile_Devices", 0);
            allManagedMobileDeviceRoleDeleteQuery.setCriteria(techLoginCri.and(roleNameCri));
            final String str = allManagedMobileDeviceRoleDeleteQuery.toString();
            MDMUtil.getPersistence().delete(allManagedMobileDeviceRoleDeleteQuery);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception ex) {
            MDMUtil.getUserTransaction().rollback();
            throw ex;
        }
    }
    
    public void addAllManagedMobileDeviceGrouptoUsers(final List technicianLoginIdList) {
        final MultiMap customerIdMap = this.getCustomerIdListUsingLoginId(technicianLoginIdList);
        final HashMap techtoGroup = new HashMap();
        final Set<Long> keySet = customerIdMap.keySet();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UserCustomGroupMapping"));
        selectQuery.addJoin(new Join("UserCustomGroupMapping", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "*"));
        final Criteria techCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginIdList.toArray(), 8);
        final Criteria groupNameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)"ALL_MANAGED_MOBILE_DEVICE_GROUP", 0, false);
        selectQuery.setCriteria(techCriteria.and(groupNameCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final AllManagedDeviceGroupHandler allManagedDeviceGroupHandler = new AllManagedDeviceGroupHandler();
            final HashMap customerToAllDevGroup = new HashMap();
            for (final Long technicianLoginID : keySet) {
                final ArrayList<Long> customerIdList = (ArrayList<Long>)customerIdMap.get((Object)technicianLoginID);
                for (final Long customerId : customerIdList) {
                    final Criteria technicianCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginID, 0);
                    Long allMobileGroup = customerToAllDevGroup.get(customerId);
                    if (allMobileGroup == null) {
                        allMobileGroup = allManagedDeviceGroupHandler.getAllDeviceGroup("ALL_MANAGED_MOBILE_DEVICE_GROUP", customerId);
                        customerToAllDevGroup.put(customerId, allMobileGroup);
                    }
                    final Criteria groupCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), (Object)allMobileGroup, 0);
                    Row row = dataObject.getRow("UserCustomGroupMapping", technicianCriteria.and(groupCriteria));
                    if (row == null) {
                        row = new Row("UserCustomGroupMapping");
                        row.set("GROUP_RESOURCE_ID", (Object)allMobileGroup);
                        row.set("LOGIN_ID", (Object)technicianLoginID);
                        dataObject.addRow(row);
                        ArrayList groupList = techtoGroup.get(technicianLoginID);
                        if (groupList == null) {
                            groupList = new ArrayList();
                        }
                        groupList.add(allMobileGroup);
                        techtoGroup.put(technicianLoginID, groupList);
                    }
                }
            }
            MDMUtil.getPersistenceLite().update(dataObject);
            this.addDeviceToTechMapping(techtoGroup);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "could not add CG mappings for all device group for users", ex);
        }
    }
    
    private void addDeviceToTechMapping(final HashMap techToGroup) throws DataAccessException {
        for (final Long techID : techToGroup.keySet()) {
            final List groupList = techToGroup.get(techID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomGroupMemberRel"));
            final Criteria techJoinCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)Column.getColumn("UserDeviceMapping", "LOGIN_ID"), 0);
            final Criteria groupJoinCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("UserDeviceMapping", "RESOURCE_ID"), 0);
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "UserCustomGroupMapping", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "UserDeviceMapping", techJoinCriteria.and(groupJoinCriteria), 1));
            selectQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "*"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("UserDeviceMapping", "*"));
            final Criteria techcriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)techID, 0);
            final Criteria groupIDcriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
            selectQuery.setCriteria(techcriteria.and(groupIDcriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            dataObject.deleteRows("UserDeviceMapping", (Criteria)null);
            final Iterator doItr = dataObject.getRows("CustomGroupMemberRel");
            while (doItr.hasNext()) {
                final Row row = doItr.next();
                final Row userdevRow = new Row("UserDeviceMapping");
                userdevRow.set("RESOURCE_ID", row.get("MEMBER_RESOURCE_ID"));
                userdevRow.set("LOGIN_ID", (Object)techID);
                dataObject.addRow(userdevRow);
            }
            MDMUtil.getPersistenceLite().update(dataObject);
        }
    }
    
    private List<Long> updateGroupMappingForDcOnlyScope() {
        List<Long> technicianList = new ArrayList<Long>();
        Connection conn = null;
        DataSet dataSet = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
            selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "UserCustomGroupMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("UserCustomGroupMapping", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria roleNameCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_%", 3);
            final Criteria encryptionRoleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_Encryption_%", 2);
            selectQuery.setCriteria(roleNameCriteria.or(encryptionRoleCriteria));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            final List<Column> groupByColumnList = new ArrayList<Column>();
            groupByColumnList.add(Column.getColumn("AaaLogin", "LOGIN_ID"));
            groupByColumnList.add(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            selectQuery.setGroupByClause(new GroupByClause((List)groupByColumnList));
            selectQuery.addSortColumn(new SortColumn(new Column("AaaLogin", "LOGIN_ID"), true));
            conn = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, conn);
            final List<Long> loginIdList = new ArrayList<Long>();
            final List<Long> loginIdListWithMapping = new ArrayList<Long>();
            while (dataSet.next()) {
                final boolean groupMappingPresent = false;
                final Long loginId = (Long)dataSet.getValue("LOGIN_ID");
                final int groupType = (int)dataSet.getValue("GROUP_TYPE");
                if (!loginIdList.contains(loginId)) {
                    loginIdList.add(loginId);
                }
                if (groupType == 9) {
                    loginIdListWithMapping.add(loginId);
                }
            }
            loginIdList.removeAll(loginIdListWithMapping);
            technicianList = loginIdList;
            final MultiMap customerIdMap = this.getCustomerIdListUsingLoginId(loginIdList);
            final Set<Long> keySet = customerIdMap.keySet();
            final DataObject customGroupDO = (DataObject)new WritableDataObject();
            for (final Long technicianLoginID : keySet) {
                final ArrayList<Long> customerIdList = (ArrayList<Long>)customerIdMap.get((Object)technicianLoginID);
                for (final Long customerId : customerIdList) {
                    final MDMCustomGroupDetails newCgDetails = new MDMCustomGroupDetails();
                    newCgDetails.resourceId = null;
                    newCgDetails.groupType = 9;
                    newCgDetails.groupCategory = 1;
                    newCgDetails.customerId = customerId;
                    newCgDetails.domainName = "MDM";
                    newCgDetails.resourceIds = new Long[0];
                    newCgDetails.groupDescription = "Hidden modern mgmt scope group for techLoginId " + technicianLoginID + " and customerId " + customerId;
                    newCgDetails.groupName = "ModernMgmt_" + technicianLoginID + "_" + customerId;
                    newCgDetails.groupOwnedBy = 1;
                    newCgDetails.isEditable = Boolean.FALSE;
                    MDMGroupHandler.getInstance().addGroup(newCgDetails);
                    final Row userCustomGroupMappingForCustomer = new Row("UserCustomGroupMapping");
                    userCustomGroupMappingForCustomer.set("LOGIN_ID", (Object)technicianLoginID);
                    userCustomGroupMappingForCustomer.set("GROUP_RESOURCE_ID", (Object)newCgDetails.resourceId);
                    customGroupDO.addRow(userCustomGroupMappingForCustomer);
                }
            }
            MDMUtil.getPersistenceLite().add(customGroupDO);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in updateGroupMappingForDcOnlyScope: ", exp);
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in updateGroupMappingForDcOnlyScope while closing conn: ", e);
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in updateGroupMappingForDcOnlyScope while closing conn: ", e2);
            }
        }
        return technicianList;
    }
    
    private MultiMap getCustomerIdListUsingLoginId(final List<Long> loginIdList) {
        final MultiMap customerIdMap = (MultiMap)new MultiHashMap();
        DataSet dataSet = null;
        Connection conn = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LoginUserCustomerMapping"));
            selectQuery.addJoin(new Join("LoginUserCustomerMapping", "AaaUser", new String[] { "DC_USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginIdList.toArray(), 8));
            final RelationalAPI relationalAPI = RelationalAPI.getInstance();
            conn = relationalAPI.getConnection();
            dataSet = relationalAPI.executeQuery((Query)selectQuery, conn);
            while (dataSet.next()) {
                customerIdMap.put((Object)dataSet.getValue("LOGIN_ID"), (Object)dataSet.getValue("CUSTOMER_ID"));
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getCustomerIdListUsingLoginId", exp);
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in getCustomerIdListUsingLoginId while closing conn", e);
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in getCustomerIdListUsingLoginId while closing conn", e2);
            }
        }
        return customerIdMap;
    }
    
    public void removeModernMgmtDevice(final Map<Long, List<Long>> techniciansDeviceMap) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            Set<Long> technicianLoginIdList = techniciansDeviceMap.keySet();
            technicianLoginIdList = this.removeTechsWithMDMAdminPermissions(technicianLoginIdList);
            Criteria userDeviceMappingRemoveCrit = null;
            final DataObject customGroupDO = this.getTechCustomGroupDetailsDO(technicianLoginIdList);
            final DataObject userCgMemberRelDO = this.getTechCGMemberRelDetailsDO(technicianLoginIdList);
            for (final Long technicianLoginID : technicianLoginIdList) {
                final List<Long> deviceList = techniciansDeviceMap.get(technicianLoginID);
                if (deviceList != null && !deviceList.isEmpty()) {
                    final List<Long> deleteableDevicesListForUserDeviceMapping = this.removeDevicesMappedViaOtherCGAndCloneDeviceList(deviceList, userCgMemberRelDO, technicianLoginID);
                    final Criteria deviceListCrit = new Criteria(Column.getColumn("UserDeviceMapping", "RESOURCE_ID"), (Object)deleteableDevicesListForUserDeviceMapping.toArray(new Long[1]), 8);
                    final Criteria loginIdCrit = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)technicianLoginID, 0);
                    final Criteria userDeviceCrit = deviceListCrit.and(loginIdCrit);
                    if (userDeviceMappingRemoveCrit == null) {
                        userDeviceMappingRemoveCrit = userDeviceCrit;
                    }
                    else {
                        userDeviceMappingRemoveCrit = userDeviceMappingRemoveCrit.or(userDeviceCrit);
                    }
                    final Criteria groupResType = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
                    final Iterator cgForCustomerIdRows = customGroupDO.getRows("Resource", groupResType);
                    final List<Long> cgList = new ArrayList<Long>();
                    while (cgForCustomerIdRows.hasNext()) {
                        final Row cgForCustomerIdRow = cgForCustomerIdRows.next();
                        final Long cgResId = (Long)cgForCustomerIdRow.get("RESOURCE_ID");
                        cgList.add(cgResId);
                    }
                    final Criteria techCGCrit = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)cgList.toArray(new Long[1]), 8);
                    final Criteria cgDeviceListCrit = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceList.toArray(new Long[1]), 8);
                    DataAccess.delete("CustomGroupMemberRel", techCGCrit.and(cgDeviceListCrit));
                }
            }
            if (userDeviceMappingRemoveCrit != null) {
                DataAccess.delete("UserDeviceMapping", userDeviceMappingRemoveCrit);
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception ex) {
            MDMUtil.getUserTransaction().rollback();
            throw ex;
        }
    }
    
    private DataObject getTechCustomGroupDetailsDO(final Set<Long> technicianLoginIdList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Criteria customGroupExtnJoinCri = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)Column.getColumn("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), 0);
        final Criteria customGroupTypeJoinCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)9, 0);
        final Join userCustomGroupJoin = new Join("CustomGroup", "UserCustomGroupMapping", customGroupExtnJoinCri.and(customGroupTypeJoinCri), 2);
        final Join resourceJoin = new Join("UserCustomGroupMapping", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join customGroupMemberRel = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        selectQuery.addJoin(userCustomGroupJoin);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addJoin(customGroupMemberRel);
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "*"));
        selectQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
        final Criteria technicianLoginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginIdList.toArray(new Long[1]), 8);
        selectQuery.setCriteria(technicianLoginIdCriteria);
        final DataObject customGroupDO = MDMUtil.getPersistence().get(selectQuery);
        return customGroupDO;
    }
    
    private DataObject getTechUserDeviceMappingDO(final Set<Long> technicianLoginIdList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UserDeviceMapping"));
        selectQuery.addSelectColumn(Column.getColumn("UserDeviceMapping", "*"));
        final Criteria technicianLoginIdCriteria = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)technicianLoginIdList.toArray(new Long[1]), 8);
        selectQuery.setCriteria(technicianLoginIdCriteria);
        final DataObject userDeviceMappingDO = MDMUtil.getPersistence().get(selectQuery);
        return userDeviceMappingDO;
    }
    
    private DataObject getTechCGMemberRelDetailsDO(final Set<Long> technicianLoginIdList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UserCustomGroupMapping"));
        selectQuery.addJoin(new Join("UserCustomGroupMapping", "CustomGroupMemberRel", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
        final Criteria technicianLoginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginIdList.toArray(new Long[1]), 8);
        selectQuery.setCriteria(technicianLoginIdCriteria);
        final DataObject userCGMemberDO = MDMUtil.getPersistence().get(selectQuery);
        return userCGMemberDO;
    }
    
    private List<Long> removeDevicesMappedViaOtherCGAndCloneDeviceList(final List<Long> deviceList, final DataObject userCGMemberDO, final Long technicianLoginId) throws DataAccessException {
        final List<Long> cgList = new ArrayList<Long>();
        final List<Long> deletableDeviceList = new ArrayList<Long>();
        deletableDeviceList.addAll(deviceList);
        if (!userCGMemberDO.isEmpty()) {
            final Iterator<Row> userCgMappingRows = userCGMemberDO.getRows("UserCustomGroupMapping", new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)technicianLoginId, 0));
            while (userCgMappingRows.hasNext()) {
                final Row userCgMappingRow = userCgMappingRows.next();
                final Long cgId = (Long)userCgMappingRow.get("GROUP_RESOURCE_ID");
                cgList.add(cgId);
            }
            if (!cgList.isEmpty()) {
                for (final Long deviceId : deviceList) {
                    final Criteria deviceCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceId, 0);
                    final Criteria gpCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)cgList.toArray(new Long[1]), 8);
                    final Iterator<Row> cgMemberRelRows = userCGMemberDO.getRows("CustomGroupMemberRel", deviceCri.and(gpCri));
                    int count = 0;
                    while (cgMemberRelRows.hasNext()) {
                        ++count;
                        cgMemberRelRows.next();
                        if (count > 1) {
                            deletableDeviceList.remove(deviceId);
                            break;
                        }
                    }
                }
            }
        }
        return deletableDeviceList;
    }
    
    private Set<Long> removeTechsWithMDMAdminPermissions(final Set<Long> technicianLoginIdList) throws DataAccessException {
        final SelectQuery technicianRoleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccount"));
        technicianRoleQuery.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        technicianRoleQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        final Criteria loginIdCriteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)technicianLoginIdList.toArray(new Long[1]), 8);
        final Criteria roleNameCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)new String[] { "MDM_Settings_Admin", "MDM_Settings_Write", "MDM_Settings_Read" }, 8);
        technicianRoleQuery.setCriteria(loginIdCriteria.and(roleNameCriteria));
        technicianRoleQuery.addSelectColumn(Column.getColumn("AaaAccount", "*"));
        final DataObject roleQueryDO = MDMUtil.getPersistence().get(technicianRoleQuery);
        if (!roleQueryDO.isEmpty()) {
            final Iterator aaaAccountRows = roleQueryDO.getRows("AaaAccount");
            while (aaaAccountRows.hasNext()) {
                final Row aaaAccountRow = aaaAccountRows.next();
                final Long loginId = (Long)aaaAccountRow.get("LOGIN_ID");
                technicianLoginIdList.remove(loginId);
            }
        }
        return technicianLoginIdList;
    }
    
    public List getAllDeviceAccessTechs(final List technicians) {
        final List allDeviceTechList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaAuthorizedRole"));
        selectQuery.addJoin(new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        selectQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        final Criteria techLoginCri = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)technicians.toArray(), 8);
        final Criteria roleNameCri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"All_Managed_Mobile_Devices", 0);
        selectQuery.setCriteria(techLoginCri.and(roleNameCri));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Iterator iterator = dataObject.getRows("AaaAccount");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                allDeviceTechList.add(row.get("LOGIN_ID"));
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "couldnt compute users with all device role, Returning Empty list");
            allDeviceTechList.clear();
        }
        return allDeviceTechList;
    }
    
    public Boolean hasUserAllDeviceScopeGroup(final Long loginId, final Boolean isGroup) {
        final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices");
        final Boolean isRBDAGroupCheck = getInstance().hasRBDAGroupCheck(loginId, isGroup);
        return isMDMAdmin || isRBDAGroupCheck;
    }
    
    public Boolean hasRBDAGroupCheck(final Long loginId, final Boolean isGroup) {
        Boolean hasAllDeviceGroups = Boolean.FALSE;
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableRBDAGroup")) {
            if (isGroup) {
                return hasAllDeviceGroups;
            }
        }
        try {
            final HashMap<Long, String> mdmAllDeviceGroupLoginHash = getLoginAllDeviceGroupMappingFromCahae();
            if (mdmAllDeviceGroupLoginHash.containsKey(loginId)) {
                hasAllDeviceGroups = Boolean.parseBoolean(mdmAllDeviceGroupLoginHash.get(loginId));
            }
            else {
                hasAllDeviceGroups = this.getLoginAllDeviceGroupFromDB(loginId);
                addOrUpdateLoginAllDeviceGroupMappingCahae(loginId, hasAllDeviceGroups);
            }
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception in fetching login details mapped with RBDA groups", exception);
        }
        return hasAllDeviceGroups;
    }
    
    private Boolean getLoginAllDeviceGroupFromDB(final Long loginId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UserCustomGroupMapping"));
        selectQuery.addJoin(new Join("UserCustomGroupMapping", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)"ALL_MANAGED_MOBILE_DEVICE_GROUP", 0);
        criteria = criteria.and(new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID").count());
        final int count = DBUtil.getRecordCount(selectQuery);
        return count == 1;
    }
    
    public static HashMap<Long, String> getLoginAllDeviceGroupMappingFromCahae() {
        HashMap<Long, String> mdmAllDeviceGroupLoginHash = new HashMap<Long, String>();
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("ALL_DEVICE_GROUP_LOGIN_CACHAE", 2);
        if (cacheObject != null) {
            mdmAllDeviceGroupLoginHash = (HashMap)cacheObject;
        }
        return mdmAllDeviceGroupLoginHash;
    }
    
    private static void updateLoginAllDeviceGroupMappingCahae(final HashMap<Long, String> mdmFeatureParamsHash) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("ALL_DEVICE_GROUP_LOGIN_CACHAE", (Object)mdmFeatureParamsHash, 2);
    }
    
    private static void addOrUpdateLoginAllDeviceGroupMappingCahae(final Long loginId, final Boolean paramValue) {
        HashMap<Long, String> mdmFeatureParamsHash = new HashMap<Long, String>();
        try {
            final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("ALL_DEVICE_GROUP_LOGIN_CACHAE", 2);
            if (cacheObject != null) {
                mdmFeatureParamsHash = (HashMap)cacheObject;
            }
            mdmFeatureParamsHash.put(loginId, String.valueOf(paramValue));
            updateLoginAllDeviceGroupMappingCahae(mdmFeatureParamsHash);
        }
        catch (final Exception ex) {
            mdmFeatureParamsHash.remove(loginId);
            updateLoginAllDeviceGroupMappingCahae(mdmFeatureParamsHash);
        }
    }
    
    public static void deleteLoginAllDeviceGroupMappingCahae(final Long loginId) {
        HashMap<Long, String> mdmFeatureParamsHash = new HashMap<Long, String>();
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("ALL_DEVICE_GROUP_LOGIN_CACHAE", 2);
        if (cacheObject != null) {
            mdmFeatureParamsHash = (HashMap)cacheObject;
        }
        mdmFeatureParamsHash.remove(loginId);
        updateLoginAllDeviceGroupMappingCahae(mdmFeatureParamsHash);
    }
    
    static {
        RBDAUtil.rbdaUtil = null;
    }
}
