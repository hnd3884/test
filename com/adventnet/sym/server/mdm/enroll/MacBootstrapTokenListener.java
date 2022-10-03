package com.adventnet.sym.server.mdm.enroll;

import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import com.me.mdm.server.enrollment.ios.MacBootstrapTokenHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MacBootstrapTokenListener extends ManagedDeviceListener
{
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Entering MacBootstrapTokenListener:deviceRegistered, with data = {0}", deviceEvent.toString());
        try {
            final JSONObject resourceJSON = deviceEvent.resourceJSON;
            if (resourceJSON.getInt("PLATFORM_TYPE") == 1) {
                final int modelType = resourceJSON.optJSONObject("MdModelInfo").optInt("MODEL_TYPE");
                final boolean isMacOS = modelType == 3 || modelType == 4;
                if (isMacOS) {
                    final String udid = deviceEvent.udid;
                    final Long customerID = deviceEvent.customerID;
                    final Long resourceID = deviceEvent.resourceID;
                    MacBootstrapTokenHandler.getInstance().addOrUpdateMacBootstrapToken(resourceID, customerID, udid, new HashMap());
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception in MacBootstrapTokenListener:deviceRegistered:-", e);
        }
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Exiting MacBootstrapTokenListener:deviceRegistered");
    }
}
