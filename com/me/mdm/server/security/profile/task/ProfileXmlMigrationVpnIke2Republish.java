package com.me.mdm.server.security.profile.task;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.security.profile.PayloadSecretFieldsMigrationUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ProfileXmlMigrationVpnIke2Republish implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties props) {
        try {
            final Set<Long> collectionIds = props.containsKey("collectionIds") ? ((Hashtable<K, Set<Long>>)props).get("collectionIds") : new HashSet<Long>();
            this.republishVpnIke2Profile(collectionIds);
        }
        catch (final Exception ex) {
            ProfileXmlMigrationVpnIke2Republish.logger.log(Level.SEVERE, "Exception while executing task ProfileXmlMigrationVpnIke2Republish", ex);
        }
    }
    
    private void republishVpnIke2Profile(final Set<Long> collectionIds) throws SyMException, DataAccessException {
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        final List<Integer> configIds = new ArrayList<Integer>();
        configIds.add(176);
        configIds.add(766);
        configIds.add(609);
        if (customerIds != null) {
            for (int i = 0; i < customerIds.length; ++i) {
                final Long customerId = customerIds[i];
                final Set<Long> tempCollectionIdsList = new HashSet<Long>();
                ProfileXmlMigrationVpnIke2Republish.logger.log(Level.INFO, "ProfileXmlMigrationVpnIke2Republish started for customer {0}", customerId);
                final SelectQuery selectQuery = PayloadSecretFieldsMigrationUtil.getCommonProfileConfigurationSelectQuery(customerId);
                selectQuery.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addSelectColumn(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
                selectQuery.addSelectColumn(new Column("CollectionStatus", "COLLECTION_ID"));
                selectQuery.addSelectColumn(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
                final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configIds.toArray(), 8);
                final Criteria publishedCollection = new Criteria(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
                selectQuery.setCriteria(selectQuery.getCriteria().and(configCriteria.and(publishedCollection)));
                final Criteria collectionIdsCri = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
                selectQuery.setCriteria(selectQuery.getCriteria().and(collectionIdsCri));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator profileCollectionIterator = dataObject.getRows("ProfileToCollection");
                    while (profileCollectionIterator.hasNext()) {
                        final Row profileCollectionRow = profileCollectionIterator.next();
                        if (profileCollectionRow != null) {
                            final Long profileId = (Long)profileCollectionRow.get("PROFILE_ID");
                            final Long collectionId = (Long)profileCollectionRow.get("COLLECTION_ID");
                            final Row profileRow = dataObject.getRow("Profile", new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
                            final String profilePayloadIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
                            final int platformType = (int)profileRow.get("PLATFORM_TYPE");
                            final Properties publishProperties = new Properties();
                            ((Hashtable<String, Long>)publishProperties).put("collectionId", collectionId);
                            ((Hashtable<String, Boolean>)publishProperties).put("APP_CONFIG", false);
                            ((Hashtable<String, Long>)publishProperties).put("CUSTOMER_ID", customerId);
                            ((Hashtable<String, String>)publishProperties).put("PROFILE_PAYLOAD_IDENTIFIER", profilePayloadIdentifier);
                            ((Hashtable<String, Long>)publishProperties).put("PROFILE_ID", profileId);
                            ((Hashtable<String, Boolean>)publishProperties).put("installprofileneeded", true);
                            ((Hashtable<String, Boolean>)publishProperties).put("removeprofileneeded", false);
                            if (platformType == 1) {
                                ((Hashtable<String, Boolean>)publishProperties).put("seqNeeded", false);
                            }
                            ProfileXmlMigrationVpnIke2Republish.logger.log(Level.INFO, "[ProfileXmlMigrationIke2IsuHandling] Initiating profile-republish {0}", profileId);
                            MDMConfigHandler.getInstance().republishProfileBasedOnPlatform(platformType, publishProperties);
                            tempCollectionIdsList.add(collectionId);
                        }
                    }
                    PayloadSecretFieldsMigrationUtil.updateDynamicVariableForCommand(tempCollectionIdsList);
                    ProfileXmlMigrationVpnIke2Republish.logger.log(Level.INFO, "ProfileXmlMigrationVpnIke2Republish ended for customer {0}", customerId);
                }
            }
        }
    }
    
    static {
        ProfileXmlMigrationVpnIke2Republish.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
