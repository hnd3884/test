package com.me.mdm.server.android.knox;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import java.util.ListIterator;
import java.util.List;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import java.util.Hashtable;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.simple.JSONArray;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnoxUtil
{
    private static KnoxUtil knoxUtil;
    public static final Logger LOGGER;
    
    public static KnoxUtil getInstance() {
        return KnoxUtil.knoxUtil;
    }
    
    public void addOrUpdateManagedKnoxContainer(final String resourceId, final int version, final int status, final String remark, final int apiLevel) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :addOrUpdateManagedKnoxContainer in class {0}", this.getClass().getName());
        final SelectQuery mcQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        mcQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        mcQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        final DataObject dO = SyMUtil.getPersistence().get(mcQuery);
        if (dO.isEmpty()) {
            final Row managedKnoxContainerRow = new Row("ManagedKNOXContainer");
            managedKnoxContainerRow.set("RESOURCE_ID", (Object)resourceId);
            managedKnoxContainerRow.set("CONTAINER_STATUS", (Object)status);
            managedKnoxContainerRow.set("CONTAINER_REMARKS", (Object)remark);
            managedKnoxContainerRow.set("CONTAINER_LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            managedKnoxContainerRow.set("KNOX_VERSION", (Object)version);
            managedKnoxContainerRow.set("KNOX_API_LEVEL", (Object)apiLevel);
            dO.addRow(managedKnoxContainerRow);
        }
        else {
            final Row managedKnoxContainerRow = dO.getFirstRow("ManagedKNOXContainer");
            managedKnoxContainerRow.set("CONTAINER_STATUS", (Object)status);
            managedKnoxContainerRow.set("CONTAINER_REMARKS", (Object)remark);
            managedKnoxContainerRow.set("CONTAINER_LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            managedKnoxContainerRow.set("KNOX_API_LEVEL", (Object)apiLevel);
            dO.updateRow(managedKnoxContainerRow);
        }
        SyMUtil.getPersistence().update(dO);
    }
    
    public void updateStatus(final Long resourceId, final int status, final String remarks, final int state) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :updateStatus in class {0}", this.getClass().getName());
        final SelectQuery mcQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        mcQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        mcQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        final DataObject dO = SyMUtil.getPersistence().get(mcQuery);
        if (!dO.isEmpty()) {
            final Row managedKnoxContainerRow = dO.getFirstRow("ManagedKNOXContainer");
            if (status != -1) {
                managedKnoxContainerRow.set("CONTAINER_STATUS", (Object)status);
            }
            if (remarks != null) {
                managedKnoxContainerRow.set("CONTAINER_REMARKS", (Object)remarks);
            }
            if (state != -1) {
                managedKnoxContainerRow.set("CONTAINER_STATE", (Object)state);
            }
            managedKnoxContainerRow.set("CONTAINER_LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.updateRow(managedKnoxContainerRow);
            SyMUtil.getPersistence().update(dO);
        }
    }
    
    public boolean isRegisteredAsKnox(final Long resourceId) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        sQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        try {
            final DataObject dO = SyMUtil.getPersistence().get(sQuery);
            return !dO.isEmpty();
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, "Persitence get failed ", (Throwable)ex);
            return false;
        }
    }
    
    public boolean isRegisteredAsKnox(final Long[] resArr) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        sQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resArr, 8));
        try {
            final DataObject dO = SyMUtil.getPersistence().get(sQuery);
            return !dO.isEmpty();
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, "Persitence get failed ", (Throwable)ex);
            return false;
        }
    }
    
    public void removeAsKnox(final Long resourceId) throws Exception {
        if (this.isRegisteredAsKnox(resourceId)) {
            KnoxActivationManager.getInstance().removeAssociatedLicense(resourceId);
            final Criteria managedResourceIdCriteria = new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete(managedResourceIdCriteria);
        }
    }
    
    public boolean isKnoxLicenseAvailable(final Long customerId) throws DataAccessException {
        final SelectQuery testLicenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        testLicenseQuery.addSelectColumn(new Column((String)null, "*"));
        testLicenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0));
        return !SyMUtil.getPersistence().get(testLicenseQuery).isEmpty();
    }
    
    public Long getAssignedLicense(final Long resourceId) throws Exception {
        return (Long)DBUtil.getValueFromDB("KNOXDeviceToLicenseRel", "RESOURCE_ID", (Object)resourceId, "LICENSE_ID");
    }
    
    public Long getAssignedLicenseFromHistory(final Long resourceId) throws Exception {
        return (Long)DBUtil.getValueFromDB("UnmanagedKNOXDevToLicRel", "RESOURCE_ID", (Object)resourceId, "LICENSE_ID");
    }
    
    public String getLicense(final Long resourceId) throws Exception {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getLicense in class {0}", this.getClass().getName());
        Long licenseId = this.getAssignedLicense(resourceId);
        if (licenseId != null) {
            return (String)DBUtil.getValueFromDB("KnoxLicenseDetail", "LICENSE_ID", (Object)licenseId, "LICENSE_DATA");
        }
        licenseId = this.getLicenseId(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId));
        if (licenseId == null) {
            return null;
        }
        if (KnoxActivationManager.getInstance().associateLicense(resourceId, licenseId)) {
            return (String)DBUtil.getValueFromDB("KnoxLicenseDetail", "LICENSE_ID", (Object)licenseId, "LICENSE_DATA");
        }
        return null;
    }
    
    public boolean doesContainerActive(final Long resourceId) throws Exception {
        final Integer status = (Integer)DBUtil.getValueFromDB("ManagedKNOXContainer", "RESOURCE_ID", (Object)resourceId, "CONTAINER_STATUS");
        return status != null && status == 20001;
    }
    
    public int getTotalKnoxEnabledDevice(final Long customerId) throws Exception {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getTotalKnoxEnabledDevice in class {0}", this.getClass().getName());
        final SelectQuery knoxCountQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        final Join manageddeviceJoin = new Join("ManagedKNOXContainer", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Criteria managedCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        knoxCountQuery.addJoin(manageddeviceJoin);
        knoxCountQuery.addJoin(resourceJoin);
        knoxCountQuery.setCriteria(managedCriteria.and(new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
        return DBUtil.getRecordCount(knoxCountQuery, "ManagedKNOXContainer", "RESOURCE_ID");
    }
    
    public int getTotalKnoxLicenseAppliedCount() throws Exception {
        KnoxUtil.LOGGER.log(Level.FINE, "Inside method :getTotalKnoxLicenseAppliedCOunt in class {0}", this.getClass().getName());
        final SelectQuery knoxCountQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDeviceToLicenseRel"));
        return DBUtil.getRecordCount(knoxCountQuery, "KNOXDeviceToLicenseRel", "RESOURCE_ID");
    }
    
    public int getTotalLicenseCount(final Long customerId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getTotalLicenseCount in class {0}", this.getClass().getName());
        final SelectQuery knoxCountQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        knoxCountQuery.addSelectColumn(new Column("KnoxLicenseDetail", "*"));
        knoxCountQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0));
        final DataObject dO = SyMUtil.getPersistence().get(knoxCountQuery);
        if (dO.isEmpty()) {
            return 0;
        }
        return (int)dO.getFirstValue("KnoxLicenseDetail", "MAX_COUNT");
    }
    
    public int getUsedLicenseCount(final Long customerId) throws Exception {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getUsedLicenseCount in class {0}", this.getClass().getName());
        final SelectQuery knoxCountQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDeviceToLicenseRel"));
        final Join knoxLicenseJoin = new Join("KNOXDeviceToLicenseRel", "KnoxLicenseDetail", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        knoxCountQuery.addJoin(knoxLicenseJoin);
        knoxCountQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0));
        return DBUtil.getRecordCount(knoxCountQuery, "KNOXDeviceToLicenseRel", "RESOURCE_ID");
    }
    
    public Long getLicenseId(final Long customerId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getLicenseId in class {0}", this.getClass().getName());
        final SelectQuery knoxCountQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        knoxCountQuery.addSelectColumn(new Column("KnoxLicenseDetail", "*"));
        final Criteria custCriteria = new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0);
        knoxCountQuery.setCriteria(custCriteria);
        final DataObject dO = SyMUtil.getPersistence().get(knoxCountQuery);
        if (dO.isEmpty()) {
            return null;
        }
        return (Long)dO.getFirstValue("KnoxLicenseDetail", "LICENSE_ID");
    }
    
    public boolean canApplyKnoxProfile(final Long resourceId) throws Exception {
        return this.getAssignedLicense(resourceId) != null;
    }
    
    public void clearInstalledAppInContainer(final Long resourceId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :clearInstalledAppInContainer in class {0}", this.getClass().getName());
        Criteria criteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
        criteria = criteria.and(new Criteria(new Column("MdInstalledAppResourceRel", "SCOPE"), (Object)1, 0));
        DataAccess.delete("MdInstalledAppResourceRel", criteria);
    }
    
    public void clearBlacklistedAppInContainer(final Long resourceId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :clearBlacklistedAppInContainer in class {0}", this.getClass().getName());
        final Criteria criteria = new Criteria(new Column("MdBlacklistAppInAgent", "RESOURCE_ID"), (Object)resourceId, 0);
        this.clearGivenTableForContainerData(criteria, "MdBlacklistAppInAgent");
        this.clearBlacklistAppFromResourceAndNotify(resourceId);
    }
    
    private void clearGivenTableForContainerData(Criteria criteria, final String tableName) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :clearGivenTableForContainerData in class {0}", this.getClass().getName());
        criteria = criteria.and(new Criteria(new Column(tableName, "SCOPE"), (Object)1, 0));
        DataAccess.delete(tableName, criteria);
    }
    
    private void clearBlacklistAppFromResourceAndNotify(final Long resourceId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :clearBlacklistAppFromResourceAndNotify in class {0}", this.getClass().getName());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdInstalledAppResourceRel", "MDINSTALLEDAPPRESOURCEREL_BASE"));
        Criteria selfJoinCriteria = new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_BASE", "RESOURCE_ID"), (Object)new Column("MDINSTALLEDAPPRESOURCEREL_DER", "RESOURCE_ID"), 0);
        selfJoinCriteria = selfJoinCriteria.and(new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_BASE", "APP_ID"), (Object)new Column("MDINSTALLEDAPPRESOURCEREL_DER", "APP_ID"), 0));
        selfJoinCriteria = selfJoinCriteria.and(new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_BASE", "SCOPE"), (Object)new Column("MDINSTALLEDAPPRESOURCEREL_DER", "SCOPE"), 1));
        final Join selfJoin = new Join(new Table("MdInstalledAppResourceRel", "MDINSTALLEDAPPRESOURCEREL_BASE"), new Table("MdInstalledAppResourceRel", "MDINSTALLEDAPPRESOURCEREL_DER"), selfJoinCriteria, 1);
        final Join blackListResJoin = new Join(new Table("MdInstalledAppResourceRel", "MDINSTALLEDAPPRESOURCEREL_BASE"), new Table("MdBlackListAppInResource"), new String[] { "RESOURCE_ID", "APP_ID" }, new String[] { "RESOURCE_ID", "APP_ID" }, 2);
        final Join blackListNotifyJoin = new Join("MdBlackListAppInResource", "MdAppBlackListNotify", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Criteria scopeCriteria = new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_BASE", "SCOPE"), (Object)1, 0);
        final Criteria resCriteria = new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_BASE", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria invertedScopeNULLCriteria = new Criteria(new Column("MDINSTALLEDAPPRESOURCEREL_DER", "SCOPE"), (Object)null, 0);
        sQuery.addJoin(selfJoin);
        sQuery.addJoin(blackListResJoin);
        sQuery.addJoin(blackListNotifyJoin);
        sQuery.setCriteria(invertedScopeNULLCriteria.and(scopeCriteria).and(resCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            dO.deleteRows("MdBlackListAppInResource", (Criteria)null);
            dO.deleteRows("MdAppBlackListNotify", (Criteria)null);
        }
        SyMUtil.getPersistence().update(dO);
    }
    
    public void clearAssociatedProfileForContainer(final Long resourceId, final String remarks) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
        final Join collnToProfileJoin = new Join("CollnToResources", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Criteria scopeCriteria = new Criteria(new Column("Profile", "SCOPE"), (Object)1, 0);
        final Criteria resCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.addJoin(collnToProfileJoin);
        sQuery.addJoin(profileJoin);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(resCriteria.and(scopeCriteria));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("CollnToResources");
            while (it.hasNext()) {
                final Row row = it.next();
                row.set("STATUS", (Object)8);
                row.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                row.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                row.set("REMARKS", (Object)remarks);
                row.set("REMARKS_EN", (Object)remarks);
                dO.updateRow(row);
            }
            SyMUtil.getPersistence().update(dO);
        }
    }
    
    public JSONArray getGroupListWithKnoxCount() throws JSONException, DataAccessException, Exception {
        final JSONArray groupList = new JSONArray();
        final List groupNameList = MDMGroupHandler.getMDMGroups(2);
        final ListIterator<Hashtable<String, String>> it = groupNameList.listIterator();
        while (it.hasNext()) {
            final JSONObject groupDetail = new JSONObject();
            final String groupName = ((String)it.next().get("CUSTOM_GP_NAME")).toString();
            groupDetail.put((Object)"groupName", (Object)groupName);
            final Long groupId = CustomGroupingHandler.getCustomGroupID(groupName);
            groupDetail.put((Object)"knoxCount", (Object)this.getKnoxCountInGroup(groupId));
            groupDetail.put((Object)"groupId", (Object)groupId);
            groupList.add((Object)groupDetail);
        }
        return groupList;
    }
    
    public int getKnoxCountInGroup(final Long groupId) throws DataAccessException, Exception {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getKnoxCountInGroup in class {0}", this.getClass().getName());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomGroupMemberRel"));
        sQuery.setCriteria(new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0));
        sQuery.addSelectColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        sQuery.addSelectColumn(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        int knoxCount = 0;
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("CustomGroupMemberRel");
            while (it.hasNext()) {
                final Row row = it.next();
                knoxCount = ((DBUtil.getValueFromDB("ManagedKNOXContainer", "RESOURCE_ID", (Object)row.get("MEMBER_RESOURCE_ID"), "CONTAINER_ID") != null) ? (knoxCount + 1) : knoxCount);
            }
        }
        return knoxCount;
    }
    
    public HashMap getDeviceKnoxDetails(final Long resourceId) throws DataAccessException {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getDeviceKnoxDetails in class {0}", this.getClass().getName());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        sQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_RESOURCE_ID"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "KNOX_VERSION"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_STATE"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_STATUS"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "KNOX_API_LEVEL"));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        final HashMap retValue = new HashMap();
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("ManagedKNOXContainer");
            while (it.hasNext()) {
                final Row row = it.next();
                retValue.put("KNOX_VERSION", row.get("KNOX_VERSION"));
                retValue.put("CONTAINER_STATE", row.get("CONTAINER_STATE"));
                retValue.put("CONTAINER_STATE", row.get("CONTAINER_STATE"));
                retValue.put("CONTAINER_STATUS", row.get("CONTAINER_STATUS"));
                retValue.put("KNOX_API_LEVEL", row.get("KNOX_API_LEVEL"));
            }
        }
        return retValue;
    }
    
    public org.json.JSONObject getDeviceKnoxDetailsJSON(final Long resourceId) throws Exception {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :getDeviceKnoxDetailsJSON in class {0}", this.getClass().getName());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        sQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_RESOURCE_ID"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "KNOX_VERSION"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_STATE"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "CONTAINER_STATUS"));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "KNOX_API_LEVEL"));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        final org.json.JSONObject retValue = new org.json.JSONObject();
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("ManagedKNOXContainer");
            while (it.hasNext()) {
                final Row row = it.next();
                retValue.put("KNOX_VERSION", row.get("KNOX_VERSION"));
                retValue.put("CONTAINER_STATE", row.get("CONTAINER_STATE"));
                retValue.put("CONTAINER_STATE", row.get("CONTAINER_STATE"));
                retValue.put("CONTAINER_STATUS", row.get("CONTAINER_STATUS"));
                retValue.put("KNOX_API_LEVEL", row.get("KNOX_API_LEVEL"));
            }
        }
        return retValue;
    }
    
    public boolean isGroupHavingKnoxDevices(final Long[] groupIDList) {
        KnoxUtil.LOGGER.log(Level.INFO, "Inside method :isGroupHavingKnoxDevices in class {0}", this.getClass().getName());
        boolean flag = false;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupIDList, 8);
            final Criteria containerCriteria = new Criteria(Column.getColumn("ManagedKNOXContainer", "CONTAINER_STATUS"), (Object)20000, 0);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Join knoxResJoin = new Join("CustomGroupMemberRel", "ManagedKNOXContainer", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            sQuery.addJoin(knoxResJoin);
            sQuery.addSelectColumn(Column.getColumn("ManagedKNOXContainer", "*"));
            sQuery.setCriteria(criteria.and(containerCriteria));
            final DataObject knoxDevDO = SyMUtil.getPersistence().get(sQuery);
            final Iterator iterator = knoxDevDO.getRows("ManagedKNOXContainer");
            if (iterator.hasNext()) {
                flag = true;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return flag;
    }
    
    public void handleUnmangedKnoxDevice(final Long resourceId) throws Exception {
        if (getInstance().canApplyKnoxProfile(resourceId)) {
            getInstance().clearBlacklistedAppInContainer(resourceId);
            getInstance().clearInstalledAppInContainer(resourceId);
            getInstance().clearAssociatedProfileForContainer(resourceId, "mdm.android.knox.profile.containerRemoved");
            KnoxActivationManager.getInstance().removeAssociatedLicenseButPreserveHistory(resourceId);
            getInstance().updateStatus(resourceId, 20002, "knox.activation.remarks.container.removedbyuser", 0);
        }
    }
    
    public List<Long> getResourcesWithKnoxLicense(final List<Long> resourceList) throws Exception {
        final List knoxList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("KNOXDeviceToLicenseRel"));
        selectQuery.addSelectColumn(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("KNOXDeviceToLicenseRel", "LICENSE_ID"));
        selectQuery.setCriteria(new Criteria(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            knoxList.add(dmDataSetWrapper.getValue("RESOURCE_ID"));
        }
        return knoxList;
    }
    
    public void updateKnoxAPILevel(final Long resourceId, final int apiLevel, final int knoxVersion) throws DataAccessException {
        final SelectQuery mcQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        mcQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        mcQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        final DataObject dO = SyMUtil.getPersistence().get(mcQuery);
        if (!dO.isEmpty()) {
            final Row managedKnoxContainerRow = dO.getFirstRow("ManagedKNOXContainer");
            managedKnoxContainerRow.set("KNOX_API_LEVEL", (Object)apiLevel);
            managedKnoxContainerRow.set("KNOX_VERSION", (Object)knoxVersion);
            managedKnoxContainerRow.set("CONTAINER_LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.updateRow(managedKnoxContainerRow);
            MDMUtil.getPersistence().update(dO);
        }
    }
    
    static {
        KnoxUtil.knoxUtil = new KnoxUtil();
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
