package com.me.mdm.api.command.schedule;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduledTimeZoneHandler
{
    private static Logger logger;
    private static ScheduledTimeZoneHandler scheduledTimeZoneHandler;
    
    public static ScheduledTimeZoneHandler getInstance() {
        if (ScheduledTimeZoneHandler.scheduledTimeZoneHandler == null) {
            ScheduledTimeZoneHandler.scheduledTimeZoneHandler = new ScheduledTimeZoneHandler();
        }
        return ScheduledTimeZoneHandler.scheduledTimeZoneHandler;
    }
    
    public String getTimeZoneForCollection(final Long collectionID) throws Exception {
        ScheduledTimeZoneHandler.logger.log(Level.INFO, "getting time zone for the collection{0}", collectionID);
        return (String)DBUtil.getValueFromDB("ScheduledTimeZone", "COLLECTION_ID", (Object)collectionID, "TIME_ZONE");
    }
    
    public void deleteTimeZoneForCollection(final Long collectionID) {
        ScheduledTimeZoneHandler.logger.log(Level.INFO, "Deleting TimeZone for the collection{0}", collectionID);
        try {
            final Criteria c = new Criteria(new Column("ScheduledTimeZone", "COLLECTION_ID"), (Object)collectionID, 0);
            DataAccess.delete("ScheduledTimeZone", c);
        }
        catch (final Exception e) {
            ScheduledTimeZoneHandler.logger.log(Level.SEVERE, "Exception while deleting timeZone for the collection{0}", collectionID);
        }
    }
    
    public void addCollectionForTimeZone(final Long collectionID, final String timeZone) throws Exception {
        try {
            ScheduledTimeZoneHandler.logger.log(Level.INFO, "Adding a collectionToTimeZone entry with CollectionID:{0} and TimeZone{1}", new Object[] { collectionID, timeZone });
            final Row r = new Row("ScheduledTimeZone");
            r.set("COLLECTION_ID", (Object)collectionID);
            r.set("TIME_ZONE", (Object)timeZone);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(r);
            DataAccess.add(dataObject);
        }
        catch (final DataAccessException e) {
            ScheduledTimeZoneHandler.logger.log(Level.SEVERE, "Exception in addCollectionForTimeZone", (Throwable)e);
        }
    }
    
    static {
        ScheduledTimeZoneHandler.logger = Logger.getLogger("ActionsLogger");
        ScheduledTimeZoneHandler.scheduledTimeZoneHandler = null;
    }
}
