package com.me.mdm.api.command.schedule;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ScheduledCommandToCollectionHandler
{
    private static Logger logger;
    private static ScheduledCommandToCollectionHandler scheduledCommandToCollectionHandler;
    
    public static ScheduledCommandToCollectionHandler getInstance() {
        if (ScheduledCommandToCollectionHandler.scheduledCommandToCollectionHandler == null) {
            ScheduledCommandToCollectionHandler.scheduledCommandToCollectionHandler = new ScheduledCommandToCollectionHandler();
        }
        return ScheduledCommandToCollectionHandler.scheduledCommandToCollectionHandler;
    }
    
    public List getScheduleIDsForCollections(final List collections) throws Exception {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting the scheduleIDs for the given collectionIDs{0}", collections);
        final Criteria collectionCriteria = new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collections.toArray(), 8);
        final DataObject dataObject = MDMUtil.getPersistence().get("ScheduledCommandToCollection", collectionCriteria);
        return DBUtil.getColumnValuesAsList(dataObject.getRows("ScheduledCommandToCollection"), "SCHEDULE_ID");
    }
    
    public Long getExecutionTimeForCollection(final Long collectionID) throws Exception {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting execution time for the given Collection{0}", collectionID);
        return (Long)DBUtil.getValueFromDB("ScheduledCommandToCollection", "COLLECTION_ID", (Object)collectionID, "EXECUTION_TIME");
    }
    
    public Long getScheduleIDForCollection(final Long collectionID) throws Exception {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting scheduleID for the given Collection{0}", collectionID);
        return (Long)DBUtil.getValueFromDB("ScheduledCommandToCollection", "COLLECTION_ID", (Object)collectionID, "SCHEDULE_ID");
    }
    
    public Long getScheduleIDForProperties(final Long expiry, final Long executionTime, final List commandID) {
        try {
            ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting scheduleID for expiry:{0}, executionTime:{1}, commandID:{2}", new Object[] { expiry, executionTime, commandID });
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCommandToCollection"));
            final Column collectionCol = new Column("ScheduledCommandToCollection", "COLLECTION_ID");
            final Column scheduleColumn = new Column("ScheduledCommandToCollection", "SCHEDULE_ID");
            final Column commandColumn = new Column("ScheduledCommandToCollection", "COMMAND_ID");
            final Column expiryColumn = new Column("ScheduledCommandToCollection", "EXPIRES");
            final Column executionTimeColumn = new Column("ScheduledCommandToCollection", "EXECUTION_TIME");
            final Criteria expiryCriteria = new Criteria(expiryColumn, (Object)expiry, 0);
            final Criteria executionTimeCriteria = new Criteria(executionTimeColumn, (Object)executionTime, 0);
            final Criteria commandCriteria = new Criteria(commandColumn, (Object)commandID.toArray(), 8);
            final Criteria c = expiryCriteria.and(executionTimeCriteria).and(commandCriteria);
            sq.addSelectColumn(collectionCol);
            sq.addSelectColumn(commandColumn);
            sq.addSelectColumn(scheduleColumn);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row firstRow = dataObject.getFirstRow("ScheduledCommandToCollection");
                return (Long)firstRow.get("SCHEDULE_ID");
            }
            return -1L;
        }
        catch (final Exception e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.SEVERE, "Error while fetching scheduleID for expiry:{0} executionTime:{1} commandID:{2}", new Object[] { expiry, executionTime, commandID });
            ScheduledCommandToCollectionHandler.logger.log(Level.SEVERE, "Exception in getScheduleIDforProperties", e);
            return -1L;
        }
    }
    
    public Long getExpiryForCollection(final Long collectionID) throws Exception {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting expiry time for the given collectionID:{0}", collectionID);
        return (Long)DBUtil.getValueFromDB("ScheduledCommandToCollection", "COLLECTION_ID", (Object)collectionID, "EXPIRES");
    }
    
    public Long getCommandForCollection(final Long collectionID) throws Exception {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting command for the given collectionID:{0}", collectionID);
        return (Long)DBUtil.getValueFromDB("ScheduledCommandToCollection", "COLLECTION_ID", (Object)collectionID, "COMMAND_ID");
    }
    
    private DataObject getDataObjectFromScheduleId(final Long scheduleId) {
        DataObject dataObject = null;
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "Getting DataObject for the scheduleID:{0}", scheduleId);
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCommandToCollection"));
            final Column scheduleIDColumn = new Column("ScheduledCommandToCollection", "SCHEDULE_ID");
            final Column collectionIDColumn = new Column("ScheduledCommandToCollection", "COLLECTION_ID");
            final Column commandIDColumn = new Column("ScheduledCommandToCollection", "COMMAND_ID");
            final Criteria c = new Criteria(scheduleIDColumn, (Object)scheduleId, 0);
            sq.addSelectColumn(collectionIDColumn);
            sq.addSelectColumn(scheduleIDColumn);
            sq.addSelectColumn(commandIDColumn);
            sq.setCriteria(c);
            dataObject = MDMUtil.getPersistence().get(sq);
        }
        catch (final DataAccessException e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.SEVERE, "Exception in getDataObjectFromScheduleId", (Throwable)e);
        }
        return dataObject;
    }
    
    public List getCollectionsForSchedule(final Long scheduleID) {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "getting collections with the scheduleID:{0}", scheduleID);
        List collectionIDs = new ArrayList();
        try {
            final DataObject dataObject = this.getDataObjectFromScheduleId(scheduleID);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("ScheduledCommandToCollection");
                collectionIDs = DBUtil.getColumnValuesAsList(iter, "COLLECTION_ID");
            }
        }
        catch (final Exception e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.WARNING, "Exception occurred in getCommandIDsFromScheduleID() ", e);
        }
        return ScheduleCommandService.getUniqueListItems((ArrayList)collectionIDs);
    }
    
    public void addCollectionToCommand(final Properties properties) {
        try {
            ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "adding entry in SCHEDULEDCOMMANDTOCOLLECTION with the properties:{0}", properties);
            final Long collectionID = ((Hashtable<K, Long>)properties).get("COLLECTION_ID");
            final Long commandID = ((Hashtable<K, Long>)properties).get("COMMAND_ID");
            final Long scheduleID = ((Hashtable<K, Long>)properties).get("SCHEDULE_ID");
            final Long expiry = ((Hashtable<K, Long>)properties).get("EXPIRES");
            final int scheduleType = ((Hashtable<K, Integer>)properties).get("execution_type");
            final Row r = new Row("ScheduledCommandToCollection");
            r.set("COLLECTION_ID", (Object)collectionID);
            r.set("COMMAND_ID", (Object)commandID);
            r.set("SCHEDULE_ID", (Object)scheduleID);
            r.set("EXPIRES", (Object)expiry);
            if (scheduleType == 2) {
                final Long executionTime = ((Hashtable<K, Long>)properties).get("EXECUTION_TIME");
                r.set("EXECUTION_TIME", (Object)executionTime);
            }
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(r);
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.SEVERE, "Exception while adding command{0} to collection{1}", new Object[] { ((Hashtable<K, Object>)properties).get("COMMAND_ID"), ((Hashtable<K, Object>)properties).get("COLLECTION_ID") });
        }
    }
    
    public void deleteScheduledCommandToCollectionBySchedule(final Long scheduleID) {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "Deleting from scheduledCommandToCollection for scheduleID:{0}", scheduleID);
        try {
            final Criteria c = new Criteria(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"), (Object)scheduleID, 0);
            DataAccess.delete("ScheduledCommandToCollection", c);
        }
        catch (final Exception e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.WARNING, "Exception occurred in deleteScheduledCommandToCollection()", e);
        }
    }
    
    public void deleteScheduledCommandToCollectionByCollectionID(final Long collectionID) {
        ScheduledCommandToCollectionHandler.logger.log(Level.INFO, "Deleting from scheduledCommandToCollection for CollectionID:{0}", collectionID);
        try {
            final Criteria c = new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            DataAccess.delete("ScheduledCommandToCollection", c);
        }
        catch (final Exception e) {
            ScheduledCommandToCollectionHandler.logger.log(Level.WARNING, "Exception occurred in deleteScheduledCommandToCollection()", e);
        }
    }
    
    static {
        ScheduledCommandToCollectionHandler.logger = Logger.getLogger("ActionsLogger");
        ScheduledCommandToCollectionHandler.scheduledCommandToCollectionHandler = null;
    }
}
