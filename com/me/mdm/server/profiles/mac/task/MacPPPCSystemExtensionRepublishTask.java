package com.me.mdm.server.profiles.mac.task;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MacPPPCSystemExtensionRepublishTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        try {
            final String isNeedToExecute = MDMUtil.getSyMParameter("PPPCRepublishNeeded");
            if (!MDMStringUtils.isEmpty(isNeedToExecute) && Boolean.valueOf(isNeedToExecute)) {
                MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.INFO, "Starting the PPPC-System Extn regenerate task");
                this.republishPPPC();
                MDMUtil.updateSyMParameter("PPPCRepublishNeeded", "false");
            }
        }
        catch (final Exception e) {
            MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.SEVERE, "Exception in PPPC regenerate task", e);
        }
    }
    
    public void republishPPPC() throws Exception {
        try {
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.INFO, "CustomerIDs for PPPC-SystemExtn gerneation:{0}", new Object[] { customerIds });
            if (customerIds != null) {
                for (final Long customerId : customerIds) {
                    final SelectQuery selectQuery = ProfileUtil.getProfileToConfigIdQuery();
                    selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                    selectQuery.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                    selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
                    selectQuery.addSelectColumn(new Column("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
                    selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
                    selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
                    final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
                    final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 754, 755 }, 8);
                    final Criteria publishedCollection = new Criteria(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
                    selectQuery.setCriteria(customerCriteria.and(configCriteria).and(publishedCollection));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Iterator profileCollectionIterator = dataObject.getRows("ProfileToCollection");
                        while (profileCollectionIterator.hasNext()) {
                            final Row profileCollectionRow = profileCollectionIterator.next();
                            if (profileCollectionRow != null) {
                                final Long profileId = (Long)profileCollectionRow.get("PROFILE_ID");
                                final Long collectionId = (Long)profileCollectionRow.get("COLLECTION_ID");
                                final Row profileRow = dataObject.getRow("Profile", new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
                                final String profilePayloadIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
                                final Properties publishProperties = new Properties();
                                ((Hashtable<String, Long>)publishProperties).put("collectionId", collectionId);
                                ((Hashtable<String, Boolean>)publishProperties).put("APP_CONFIG", false);
                                ((Hashtable<String, Long>)publishProperties).put("CUSTOMER_ID", customerId);
                                ((Hashtable<String, String>)publishProperties).put("PROFILE_PAYLOAD_IDENTIFIER", profilePayloadIdentifier);
                                ((Hashtable<String, Long>)publishProperties).put("PROFILE_ID", profileId);
                                MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.INFO, "Going to generate plist for PPPC prop:{0}", new Object[] { publishProperties });
                                MDMConfigHandler.getInstance().createPList(publishProperties);
                            }
                        }
                    }
                    else {
                        MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.INFO, "There is no PPPC configuration for customer:{0}", customerId);
                    }
                }
            }
        }
        catch (final SyMException e) {
            MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.SEVERE, "Exception in getting customerIds", (Throwable)e);
            throw e;
        }
        catch (final DataAccessException e2) {
            MacPPPCSystemExtensionRepublishTask.LOGGER.log(Level.SEVERE, "Exception in getting the dataObject", (Throwable)e2);
            throw e2;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
