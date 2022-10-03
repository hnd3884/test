package com.adventnet.sym.server.mdm.inv.android;

import com.me.mdm.server.location.LocationDataHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class LocationMDMInventoryImpl implements MDMInventory
{
    private final Logger logger;
    
    public LocationMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        try {
            final JSONObject locationJSON = new JSONObject(inventoryObject.strData);
            if (locationJSON.has("ErrorCode")) {
                this.logger.log(Level.INFO, "Adding location error to resource:{0} errorCode:{1}", new Object[] { inventoryObject.resourceId, locationJSON.getInt("ErrorCode") });
                MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(inventoryObject.resourceId, locationJSON.getInt("ErrorCode"));
            }
            else {
                locationJSON.put("LocationUpdationTime", System.currentTimeMillis());
                MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(inventoryObject.resourceId);
            }
            new LocationDataHandler().deviceLocationUpdates(inventoryObject.resourceId, locationJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred while populating location data from scan response", e);
        }
        return true;
    }
}
