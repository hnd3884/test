package com.me.mdm.api.command.schedule;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceActionToCollectionHandler
{
    private static Logger logger;
    private static DeviceActionToCollectionHandler deviceActionToCollectionHandler;
    
    public static DeviceActionToCollectionHandler getInstance() {
        if (DeviceActionToCollectionHandler.deviceActionToCollectionHandler == null) {
            DeviceActionToCollectionHandler.deviceActionToCollectionHandler = new DeviceActionToCollectionHandler();
        }
        return DeviceActionToCollectionHandler.deviceActionToCollectionHandler;
    }
    
    public void updateCollectionForDeviceAction(final Long deviceActionID, final Long collectionID) {
        DeviceActionToCollectionHandler.logger.log(Level.INFO, "Updating the collectionID{0} for deviceActionID{1}", new Object[] { collectionID, deviceActionID });
        try {
            this.removeDeviceActionToCollection(deviceActionID);
            this.addDeviceActionToCollectionEntry(deviceActionID, collectionID);
        }
        catch (final Exception e) {
            DeviceActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while updating the collectionID{1} for the deviceActionID{0}", new Object[] { deviceActionID, collectionID });
        }
    }
    
    public Long getCollectionForDeviceAction(final Long deviceActionID) throws Exception {
        DeviceActionToCollectionHandler.logger.log(Level.INFO, "Getting collectionID for the deviceActionID:{0}", deviceActionID);
        return (Long)DBUtil.getValueFromDB("DeviceActionToCollection", "DEVICE_ACTION_ID", (Object)deviceActionID, "COLLECTION_ID");
    }
    
    private void removeDeviceActionToCollection(final Long deviceActionID) {
        try {
            DeviceActionToCollectionHandler.logger.log(Level.INFO, "Removing DeviceActionToCollection entry for the given deviceActionID:{0}", deviceActionID);
            final Criteria c = new Criteria(new Column("DeviceActionToCollection", "DEVICE_ACTION_ID"), (Object)deviceActionID, 0);
            MDMUtil.getPersistence().delete(c);
        }
        catch (final Exception e) {
            DeviceActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while deleting the deviceActionID{0}", new Object[] { deviceActionID });
        }
    }
    
    public void addDeviceActionToCollectionEntry(final Long deviceActionID, final Long collectionID) {
        DeviceActionToCollectionHandler.logger.log(Level.INFO, "adding the mapping between deviceActionID:{0} and collection{1}", new Object[] { deviceActionID, collectionID });
        try {
            final Row r = new Row("DeviceActionToCollection");
            r.set("COLLECTION_ID", (Object)collectionID);
            r.set("DEVICE_ACTION_ID", (Object)deviceActionID);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(r);
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            DeviceActionToCollectionHandler.logger.log(Level.SEVERE, "Exception in addDeviceActionToCollectionEntry", e);
        }
    }
    
    static {
        DeviceActionToCollectionHandler.logger = Logger.getLogger("ActionsLogger");
        DeviceActionToCollectionHandler.deviceActionToCollectionHandler = null;
    }
}
