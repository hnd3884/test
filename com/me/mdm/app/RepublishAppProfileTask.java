package com.me.mdm.app;

import java.util.Hashtable;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.apps.ios.task.IOSExtarctProvisioningDetails;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class RepublishAppProfileTask implements SchedulerExecutionInterface
{
    private Logger logger;
    private List platformTypesToRepublishProfile;
    
    public RepublishAppProfileTask() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.platformTypesToRepublishProfile = new ArrayList(Arrays.asList(3, 1));
    }
    
    public void executeTask(final Properties props) {
        try {
            final Long startTime = System.currentTimeMillis();
            final String taskName = String.valueOf(((Hashtable<K, Object>)props).get("taskName"));
            this.logger.log(Level.INFO, "{0} Task Initiated at {0}", new Object[] { taskName, DateTimeUtil.longdateToString((long)startTime) });
            Criteria criteria = null;
            if (taskName.equalsIgnoreCase("RepublishMSIAppProfiles")) {
                criteria = new Criteria(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"), (Object)".msi", 11, false);
            }
            this.rePublishAppProfile(criteria);
            final Long endTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "{0} Task end at {0} with the duration of ", new Object[] { taskName, DateTimeUtil.longdateToString((long)endTime), DurationFormatUtils.formatDurationHMS(endTime - startTime) });
            MDMUtil.deleteSyMParameter(taskName);
            new IOSExtarctProvisioningDetails().executeTask((Properties)null);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in RepublishAppProfileTask", ex);
        }
    }
    
    private List getSelectColumnList() {
        final List<Column> selectColumns = new ArrayList<Column>();
        selectColumns.add(Column.getColumn("Profile", "PROFILE_ID"));
        selectColumns.add(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectColumns.add(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectColumns.add(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
        selectColumns.add(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectColumns.add(Column.getColumn("MdAppToCollection", "APP_ID"));
        selectColumns.add(Column.getColumn("ManagedAppConfigurationData", "APP_CONFIG_ID"));
        return selectColumns;
    }
    
    private SelectQuery getBaseQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "InstallAppPolicy", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        selectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
        return selectQuery;
    }
    
    private Criteria getCriteria() {
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)this.platformTypesToRepublishProfile.toArray(), 8);
        final Criteria appTypeCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
        return platformCriteria.and(appTypeCriteria);
    }
    
    private SelectQuery getLiveAppsQuery(final Criteria criteria) {
        final SelectQuery liveAppsQuery = this.getBaseQuery();
        liveAppsQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        liveAppsQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        liveAppsQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        liveAppsQuery.setCriteria(this.getCriteria());
        if (criteria != null) {
            liveAppsQuery.setCriteria(liveAppsQuery.getCriteria().and(criteria));
        }
        liveAppsQuery.addSelectColumns(this.getSelectColumnList());
        return liveAppsQuery;
    }
    
    private SelectQuery getRecentProfileForResourceQuery(final Criteria criteria) {
        final SelectQuery selectQuery = this.getBaseQuery();
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.setCriteria(this.getCriteria());
        if (criteria != null) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(criteria));
        }
        selectQuery.addSelectColumns(this.getSelectColumnList());
        return selectQuery;
    }
    
    private SelectQuery getRecentProfileForGroupQuery(final Criteria criteria) {
        final SelectQuery selectQuery = this.getBaseQuery();
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.setCriteria(this.getCriteria());
        if (criteria != null) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(criteria));
        }
        selectQuery.addSelectColumns(this.getSelectColumnList());
        return selectQuery;
    }
    
    private void rePublishAppProfile(final Criteria criteria) throws Exception {
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)this.getLiveAppsQuery(criteria), (Query)this.getRecentProfileForResourceQuery(criteria), false);
        unionQuery.union((Query)this.getRecentProfileForGroupQuery(criteria), false);
        Connection conn = null;
        try {
            final RelationalAPI relationalAPI = RelationalAPI.getInstance();
            conn = relationalAPI.getConnection();
            final DataSet dataSet = relationalAPI.executeQuery((Query)unionQuery, conn);
            while (dataSet.next()) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("PROFILE_ID", dataSet.getValue("PROFILE_ID"));
                jsonObject.put("COLLECTION_ID", dataSet.getValue("COLLECTION_ID"));
                jsonObject.put("CUSTOMER_ID", dataSet.getValue("CUSTOMER_ID"));
                jsonObject.put("PLATFORM_TYPE", dataSet.getValue("PLATFORM_TYPE"));
                jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
                jsonObject.put("HAS_APP_CONFIGURATION", dataSet.getValue("APP_CONFIG_ID") != null);
                jsonObject.put("APP_ID", dataSet.getValue("APP_ID"));
                jsonObject.put("LAST_MODIFIED_BY", dataSet.getValue("LAST_MODIFIED_BY"));
                ProfileConfigHandler.publishProfile(jsonObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in rePublishAppProfile()..", ex);
            throw ex;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception in finally block", e);
            }
        }
    }
}
