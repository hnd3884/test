package com.me.mdm.server.inv;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.List;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class CustomDeviceDetailsTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        CustomDeviceDetailsTask.LOGGER.log(Level.INFO, "Beginning to execute CustomDeviceDetailsTask");
        try {
            final JSONObject params = new JSONObject((String)((Hashtable<K, String>)props).get("params"));
            final Long resourceId = params.optLong("MANAGED_DEVICE_ID");
            final Long customerId = JSONUtil.optLongForUVH(params, "CUSTOMER_ID", Long.valueOf(-1L));
            if (params.has("CUSTOMER_ID")) {
                params.remove("CUSTOMER_ID");
            }
            if (resourceId != null) {
                final String deviceName = params.optString("NAME");
                if (!MDMStringUtils.isEmpty(deviceName)) {
                    final List<Long> resourceList = new ArrayList<Long>();
                    resourceList.add(resourceId);
                    MDCustomDetailsRequestHandler.getInstance().checkAndSendDeviceNameUpdateCommand(resourceList);
                }
                params.put("DEVICE_NAME", (Object)params.optString("NAME"));
                params.remove("NAME");
                final DeviceEvent deviceEvent = new DeviceEvent(resourceId);
                deviceEvent.resourceJSON = params;
                deviceEvent.customerID = customerId;
                ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 7);
            }
        }
        catch (final Exception ex) {
            CustomDeviceDetailsTask.LOGGER.log(Level.SEVERE, "Exception in adding custom device detail task", ex);
        }
        CustomDeviceDetailsTask.LOGGER.log(Level.INFO, "Successfully executed CustomDeviceDetailsTask");
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
