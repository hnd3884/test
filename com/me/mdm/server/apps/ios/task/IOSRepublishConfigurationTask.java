package com.me.mdm.server.apps.ios.task;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class IOSRepublishConfigurationTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties props) {
        IOSRepublishConfigurationTask.logger.log(Level.INFO, "DATA-IN: IOS Republish configuration props: {0}", new Object[] { props });
        final String republishReasonKey = ((Hashtable<K, String>)props).get("reason_for_republish");
        final String rePublishReasonValue = MDMUtil.getSyMParameter(republishReasonKey);
        final Long startedAt = System.currentTimeMillis();
        boolean isRepublished = false;
        if (rePublishReasonValue != null && !rePublishReasonValue.isEmpty()) {
            isRepublished = Boolean.parseBoolean(rePublishReasonValue);
        }
        if (!isRepublished) {
            IOSRepublishConfigurationTask.logger.log(Level.INFO, "Republishing the given configuration for reason: {0}", new Object[] { republishReasonKey });
            final List configIdList = ((Hashtable<K, List>)props).get("configIds");
            final boolean seqNeeded = ((Hashtable<K, Boolean>)props).get("seqNeeded");
            final boolean installProfileNeeded = ((Hashtable<K, Boolean>)props).get("installprofileneeded");
            final boolean isRemoveProfileNeeded = ((Hashtable<K, Boolean>)props).get("removeprofileneeded");
            try {
                final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                if (customerIds != null) {
                    for (final Long customerId : customerIds) {
                        IOSRepublishConfigurationTask.logger.log(Level.INFO, "republishConfiguration(): CustomerID:{0}", new Object[] { customerId });
                        final Criteria configIdCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configIdList.toArray(), 8);
                        final SelectQuery columnQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
                        columnQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                        columnQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                        columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
                        columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
                        columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
                        final List<Column> groupColumn = new ArrayList<Column>();
                        groupColumn.add(new Column("ProfileToCollection", "COLLECTION_ID"));
                        final GroupByClause groupByClause = new GroupByClause((List)groupColumn);
                        columnQuery.setGroupByClause(groupByClause);
                        columnQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
                        final Criteria recentCriteria = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileToColln", "COLLECTION_ID"), (Object)null, 1)));
                        columnQuery.setCriteria(recentCriteria.and(configIdCriteria));
                        final DerivedColumn derivedColumn = new DerivedColumn("profileCollection", columnQuery);
                        final SelectQuery profileSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
                        profileSelectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                        profileSelectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                        profileSelectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                        profileSelectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                        profileSelectQuery.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                        final Criteria collectionCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)derivedColumn, 8);
                        final Criteria customerIdCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
                        final Criteria publishedCollection = new Criteria(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
                        profileSelectQuery.setCriteria(collectionCriteria.and(configIdCriteria).and(customerIdCriteria).and(publishedCollection));
                        profileSelectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
                        profileSelectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
                        profileSelectQuery.addSelectColumn(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
                        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)profileSelectQuery);
                        while (dataSetWrapper.next()) {
                            final Long collectionId = (Long)dataSetWrapper.getValue("COLLECTION_ID");
                            final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
                            final String profileIdentifier = (String)dataSetWrapper.getValue("PROFILE_PAYLOAD_IDENTIFIER");
                            final Properties publishProperties = new Properties();
                            ((Hashtable<String, Long>)publishProperties).put("collectionId", collectionId);
                            ((Hashtable<String, Boolean>)publishProperties).put("APP_CONFIG", false);
                            ((Hashtable<String, Long>)publishProperties).put("CUSTOMER_ID", customerId);
                            ((Hashtable<String, String>)publishProperties).put("PROFILE_PAYLOAD_IDENTIFIER", profileIdentifier);
                            ((Hashtable<String, Long>)publishProperties).put("PROFILE_ID", profileId);
                            ((Hashtable<String, Boolean>)publishProperties).put("seqNeeded", seqNeeded);
                            ((Hashtable<String, Boolean>)publishProperties).put("installprofileneeded", installProfileNeeded);
                            ((Hashtable<String, Boolean>)publishProperties).put("removeprofileneeded", isRemoveProfileNeeded);
                            IOSRepublishConfigurationTask.logger.log(Level.INFO, "Going to generate plist for profileId={0} with Prop={1}", new Object[] { profileId, publishProperties });
                            MDMConfigHandler.getInstance().createPList(publishProperties);
                        }
                    }
                    MDMUtil.updateSyMParameter(republishReasonKey, "true");
                    IOSRepublishConfigurationTask.logger.log(Level.INFO, "RePublish task completed at:{0}", new Object[] { startedAt - System.currentTimeMillis() });
                }
            }
            catch (final Exception e) {
                IOSRepublishConfigurationTask.logger.log(Level.SEVERE, "Exception in RepublishConfigurationTask");
            }
        }
    }
    
    static {
        IOSRepublishConfigurationTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
