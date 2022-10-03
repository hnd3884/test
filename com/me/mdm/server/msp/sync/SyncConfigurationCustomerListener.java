package com.me.mdm.server.msp.sync;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SyncConfigurationCustomerListener implements SchedulerExecutionInterface
{
    private static Logger logger;
    Long customerId;
    
    public SyncConfigurationCustomerListener() {
        this.customerId = -1L;
    }
    
    public void executeTask(final Properties props) {
        try {
            this.customerId = Long.valueOf(((Hashtable<K, Object>)props).get("customerId").toString());
            SyncConfigurationCustomerListener.logger.log(Level.INFO, "SyncConfigurationCustomerListener invoked for customer {0}", new Object[] { this.customerId });
            this.addAllCustomerProfiles();
            this.addAllCustomerApps();
        }
        catch (final Exception ex) {
            SyncConfigurationCustomerListener.logger.log(Level.SEVERE, "Exception in SyncConfigurationCustomerListener", ex);
        }
    }
    
    private Long getRandomCustomerId() throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)this.customerId, 1);
        selectQuery.setCriteria(customerCriteria);
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), true));
        selectQuery.addSelectColumn(new Column("CustomerInfo", "CUSTOMER_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row customerInfoRow = dataObject.getFirstRow("CustomerInfo");
            return (Long)customerInfoRow.get("CUSTOMER_ID");
        }
        SyncConfigurationCustomerListener.logger.log(Level.SEVERE, "No customerId found");
        throw new DataAccessException();
    }
    
    private List getAllCustomerProfileList() throws DataAccessException {
        final List<Long> allCustomerProfileList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
        final Criteria profileScopeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_SHARED_SCOPE"), (Object)1, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)this.getRandomCustomerId(), 0);
        final Criteria collectionStatusCrit = new Criteria(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
        selectQuery.setCriteria(profileScopeCriteria.and(customerCriteria.and(collectionStatusCrit)));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("Profile");
            while (iterator.hasNext()) {
                final Row profileRow = iterator.next();
                allCustomerProfileList.add((Long)profileRow.get("PROFILE_ID"));
            }
        }
        return allCustomerProfileList;
    }
    
    private JSONObject getNecessaryDetailsToClone(final Long profileId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "AaaLogin", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final JSONObject jsonObject = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Row profileCustomerRow = dataObject.getFirstRow("ProfileToCustomerRel");
            final Row aaaLoginRow = dataObject.getFirstRow("AaaLogin");
            jsonObject.put("PROFILE_PAYLOAD_IDENTIFIER", profileRow.get("PROFILE_PAYLOAD_IDENTIFIER"));
            jsonObject.put("PROFILE_TYPE", profileRow.get("PROFILE_TYPE"));
            jsonObject.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
            jsonObject.put("CUSTOMER_ID", profileCustomerRow.get("CUSTOMER_ID"));
            jsonObject.put("LAST_MODIFIED_BY", profileRow.get("LAST_MODIFIED_BY"));
            jsonObject.put("LOGIN_ID", aaaLoginRow.get("LOGIN_ID"));
            jsonObject.put("IS_MOVED_TO_TRASH", profileRow.get("IS_MOVED_TO_TRASH"));
        }
        return jsonObject;
    }
    
    private String getQueueFileName(final int profileType, final Long customerId) {
        String fileName = customerId + "_" + System.currentTimeMillis();
        if (profileType == 1) {
            fileName += "_customer_listener_profile_sync_qdata";
        }
        return fileName;
    }
    
    private void addAllCustomerProfiles() throws Exception {
        final List<Long> allCustomerProfileList = this.getAllCustomerProfileList();
        for (final Long profileId : allCustomerProfileList) {
            final JSONObject jsonObject = this.getNecessaryDetailsToClone(profileId);
            jsonObject.put("childCustomerId", (Object)this.customerId);
            final DCQueueData dcQueueData = new DCQueueData();
            dcQueueData.fileName = this.getQueueFileName(1, this.customerId);
            dcQueueData.queueData = jsonObject.toString();
            dcQueueData.postTime = System.currentTimeMillis();
            dcQueueData.queueDataType = 100;
            final DCQueue dcQueue = DCQueueHandler.getQueue("sync-configurations-processor");
            dcQueue.addToQueue(dcQueueData);
        }
    }
    
    private List getAllCustomerAppsList() throws DataAccessException {
        final List<Long> packageList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)this.getRandomCustomerId(), 0));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0)));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdPackage");
            while (iterator.hasNext()) {
                final Row packageRow = iterator.next();
                packageList.add((Long)packageRow.get("PACKAGE_ID"));
            }
        }
        return packageList;
    }
    
    private void addOtherAppVersionsIfExists(final Long packageId) throws Exception {
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        selectQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("MdPackage", "AaaLogin", new String[] { "PACKAGE_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria appVersionLabelCriteria = AppVersionDBUtil.getInstance().getNonApprovedAppVersionCriteria();
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(appCriteria.and(appVersionLabelCriteria));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            SyncConfigurationCustomerListener.logger.log(Level.INFO, "Adding non approved app version {0} {1} {2}", new Object[] { this.customerId, dmDataSetWrapper.getValue("PACKAGE_ID"), dmDataSetWrapper.getValue("RELEASE_LABEL_ID") });
            SyncConfigurationsUtil.addNonApprovedAppVersions((Long)dmDataSetWrapper.getValue("PACKAGE_ID"), (Long)dmDataSetWrapper.getValue("RELEASE_LABEL_ID"), this.customerId);
        }
    }
    
    private List<Long> filterOutTrashedApps(final List appsIds) throws Exception {
        final List<Long> trashedApps = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)appsIds.toArray(), 8));
        selectQuery.setCriteria(selectQuery.getCriteria().and(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "APP_GROUP_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator<Row> iterator = dataObject.getRows("MdPackageToAppGroup");
        while (iterator.hasNext()) {
            final Row mdAppGroupRow = iterator.next();
            trashedApps.add((Long)mdAppGroupRow.get("PACKAGE_ID"));
        }
        return trashedApps;
    }
    
    private void addAllCustomerApps() throws Exception {
        SyncConfigurationCustomerListener.logger.log(Level.INFO, "addAllCustomerApps invoked for customer {0}", new Object[] { this.customerId });
        final List<Long> allCustomerAppsList = this.getAllCustomerAppsList();
        final List<Long> trashedAllCustomerApps = this.filterOutTrashedApps(allCustomerAppsList);
        for (final Long packageId : allCustomerAppsList) {
            SyncConfigurationCustomerListener.logger.log(Level.INFO, "Adding following all customer apps {0} for customer", new Object[] { packageId, this.customerId });
            SyncConfigurationsUtil.addApprovedAppVersion(packageId, this.customerId);
            this.addOtherAppVersionsIfExists(packageId);
            if (trashedAllCustomerApps.contains(packageId)) {
                SyncConfigurationsUtil.moveAllCustomerAppToTrash(packageId, this.customerId);
            }
        }
    }
    
    static {
        SyncConfigurationCustomerListener.logger = Logger.getLogger("MDMConfigLogger");
    }
}
