package com.me.mdm.api.command.schedule;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DeviceActionScheduleUtils
{
    private static Logger logger;
    
    private static DataObject getDataObjectForDeviceAction(final Long deviceActionID) throws DataAccessException {
        DeviceActionScheduleUtils.logger.log(Level.INFO, "getting DataObject for deviceActionID:{0}", deviceActionID);
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DeviceActionHistory"));
            final Column deviceActionCommandHistoryIDCol = new Column("DeviceActionHistory", "COMMAND_HISTORY_ID");
            final Column deviceActionIDCol = new Column("DeviceActionHistory", "DEVICE_ACTION_ID");
            final Column commandHistoryIdCol = new Column("CommandHistory", "COMMAND_HISTORY_ID");
            final Column resourceIdCol = new Column("CommandHistory", "RESOURCE_ID");
            final Join join = new Join("DeviceActionHistory", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
            sq.addSelectColumn(deviceActionCommandHistoryIDCol);
            sq.addSelectColumn(deviceActionIDCol);
            sq.addSelectColumn(commandHistoryIdCol);
            sq.addSelectColumn(resourceIdCol);
            sq.addSelectColumn(new Column("DeviceActionToCollection", "DEVICE_ACTION_ID"));
            sq.addSelectColumn(new Column("DeviceActionToCollection", "COLLECTION_ID"));
            sq.addJoin(join);
            final Criteria c = new Criteria(deviceActionIDCol, (Object)deviceActionID, 0);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            return dataObject;
        }
        catch (final Exception e) {
            DeviceActionScheduleUtils.logger.log(Level.SEVERE, "Error while getting dataObject for deviceActionID{0}", deviceActionID);
            DeviceActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getDataObjectForDeviceAction", e);
            throw e;
        }
    }
    
    public static List getResourceListForDeviceAction(final Long deviceActionID) {
        List resourceList = new ArrayList();
        DeviceActionScheduleUtils.logger.log(Level.INFO, "Getting device ID for the given deviceActionID{0}", new Object[] { deviceActionID });
        try {
            final DataObject dataObject = getDataObjectForDeviceAction(deviceActionID);
            final Iterator iter = dataObject.getRows("CommandHistory");
            resourceList = DBUtil.getColumnValuesAsList(iter, "RESOURCE_ID");
            return resourceList;
        }
        catch (final Exception e) {
            DeviceActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getResourceListForDeviceAction", e);
            DeviceActionScheduleUtils.logger.log(Level.SEVERE, "Error while fetching resourceID for deviceActionID{0}", deviceActionID);
            return resourceList;
        }
    }
    
    static {
        DeviceActionScheduleUtils.logger = Logger.getLogger("ActionsLogger");
    }
}
