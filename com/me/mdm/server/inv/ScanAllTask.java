package com.me.mdm.server.inv;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScanAllTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public ScanAllTask() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Executing ScanAllTask scan_all API");
        try {
            final String sEventLogRemarks = "dc.mdm.actionlog.inv.scan_success";
            final ArrayList<Object> remarksArgsList = new ArrayList<Object>();
            final Long userId = ((Hashtable<K, Long>)props).get("SCHEDULED_SCAN_USER_ID");
            final Long customerID = ((Hashtable<K, Long>)props).get("customer_id");
            final String userName = ((Hashtable<K, String>)props).get("user_name");
            final HashMap deviceIdNameMap = ManagedDeviceHandler.getInstance().getManagedDeviceIdNameMap(customerID);
            final ArrayList deviceIdList = new ArrayList(deviceIdNameMap.keySet());
            for (final Object deviceId : deviceIdList) {
                final String deviceName = deviceIdNameMap.get(deviceId);
                remarksArgsList.add(deviceName + "@@@" + userName);
            }
            MDMEventLogHandler.getInstance().addEvent(2041, deviceIdList, userName, sEventLogRemarks, remarksArgsList, customerID, MDMUtil.getCurrentTimeInMillis());
            DeviceInvCommandHandler.getInstance().scanDevice(deviceIdList, userId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in ScanAllTask", ex);
        }
        this.logger.log(Level.INFO, "Finished Executing ScanAllTask scan_all API");
    }
}
