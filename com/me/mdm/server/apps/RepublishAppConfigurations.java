package com.me.mdm.server.apps;

import java.util.Arrays;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class RepublishAppConfigurations implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public RepublishAppConfigurations() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            final Long startTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "RepublishAppConfigurations task initiated at {0}", new Object[] { DateTimeUtil.longdateToString((long)startTime) });
            final List<Long> rePublishediOSAppConfigurationProfiles = this.rePublishIosAppConfigurationProfile();
            this.assignAppConfigurationCommand(rePublishediOSAppConfigurationProfiles);
            this.rePublishWindowsAppWithAppConfiguration();
            this.deleteMalformedCommand();
            this.stopTask();
            this.logger.log(Level.INFO, "RepublishAppConfigurations task ended at {0}", new Object[] { DateTimeUtil.longdateToString(System.currentTimeMillis()) });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in RepublishAppConfigurations", e);
        }
    }
    
    private void deleteMalformedCommand() throws Exception {
        final Criteria requestTypeCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)"ApplicationConfiguration", 0);
        final Criteria commandUUIDCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"Settings;Settings", 0);
        DataAccess.delete("MdCommands", requestTypeCriteria.and(commandUUIDCriteria));
    }
    
    private void stopTask() {
        MDMUtil.updateSyMParameter("AppConfigurationRepublishTask", "false");
        MDMUtil.updateSyMParameter("AppConfigRepublishCompleted", "true");
    }
    
    private List<Long> rePublishAppProfiles(final SelectQuery selectQuery) throws Exception {
        final List<Long> rePublishedAppConfigProfile = new ArrayList<Long>();
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
            rePublishedAppConfigProfile.add(collectionId);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("PROFILE_ID", ds.getValue("PROFILE_ID"));
            jsonObject.put("COLLECTION_ID", (Object)collectionId);
            jsonObject.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
            jsonObject.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
            jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
            jsonObject.put("HAS_APP_CONFIGURATION", true);
            jsonObject.put("APP_ID", ds.getValue("APP_ID"));
            jsonObject.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
            jsonObject.put("PROFILE_TYPE", ds.getValue("PROFILE_TYPE"));
            ProfileConfigHandler.publishProfile(jsonObject);
        }
        this.logger.log(Level.INFO, "{0} app collection ids are republished", new Object[] { rePublishedAppConfigProfile });
        return rePublishedAppConfigProfile;
    }
    
    private List<Long> rePublishIosAppConfigurationProfile() throws Exception {
        this.logger.log(Level.INFO, "************* Republished iOS apps with malformed app configuration **********************");
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria requestTypeCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)"ApplicationConfiguration", 0);
            final Criteria commandUUIDCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"Settings;Settings", 0);
            final Criteria platformTypeCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(requestTypeCriteria.and(commandUUIDCriteria.and(platformTypeCriteria)));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            return this.rePublishAppProfiles(selectQuery);
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception in republishing iosAppConfigurationProfile", exception);
            throw exception;
        }
    }
    
    private void rePublishWindowsAppWithAppConfiguration() throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria platformTypeCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)3, 0);
        selectQuery.setCriteria(platformTypeCriteria);
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        this.rePublishAppProfiles(selectQuery);
    }
    
    private HashMap<Long, List<Long>> getCollectionAssociatedResources(final List<Long> collectionList) throws Exception {
        final HashMap<Long, List<Long>> collnResourceMap = new HashMap<Long, List<Long>>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria appCatalogCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)12, 1);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.setCriteria(collectionCriteria.and(markedForDeleteCriteria).and(appCatalogCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            for (final Long collection : collectionList) {
                final Iterator<Long> resourceList = dataObject.getRows("RecentProfileForResource", new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collection, 0));
                collnResourceMap.put(collection, MDMDBUtil.getColumnValuesAsList((Iterator)resourceList, "RESOURCE_ID"));
            }
        }
        return collnResourceMap;
    }
    
    private void assignAppConfigurationCommand(final List<Long> collectionList) throws Exception {
        if (!collectionList.isEmpty()) {
            final HashSet<Long> set = new HashSet<Long>();
            final HashMap<Long, List<Long>> collectionResourceMap = this.getCollectionAssociatedResources(collectionList);
            this.logger.log(Level.INFO, "Associated app configuration collection resource map {0} ", new Object[] { collectionResourceMap });
            for (final Long collectionId : collectionList) {
                if (!collectionResourceMap.get(collectionId).isEmpty()) {
                    final List collnList = new ArrayList();
                    collnList.add(collectionId);
                    set.addAll((Collection<?>)collectionResourceMap.get(collectionId));
                    final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "ApplicationConfiguration");
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, collectionResourceMap.get(collectionId));
                }
            }
            NotificationHandler.getInstance().SendNotification(Arrays.asList(set.toArray()), 1);
        }
    }
}
