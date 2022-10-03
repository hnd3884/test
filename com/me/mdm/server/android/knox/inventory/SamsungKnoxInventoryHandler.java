package com.me.mdm.server.android.knox.inventory;

import com.me.mdm.server.android.knox.KnoxUtil;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.samsung.SamsungInventory;

public class SamsungKnoxInventoryHandler extends SamsungInventory
{
    private static SamsungKnoxInventoryHandler handler;
    private static final Logger LOGGER;
    
    public static SamsungKnoxInventoryHandler getInstance() {
        return SamsungKnoxInventoryHandler.handler;
    }
    
    @Override
    public void parseInventoryData(final Long resourceID, final String data) {
        try {
            this.isDataPopulationSuccess = true;
            final Map<String, String> parsedData = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(data));
            final String respondData = parsedData.get("ResponseData");
            final JSONObject inventoryData = new JSONObject(respondData);
            final HashMap<String, String> restrictionInfo = this.isolateRestrictionInfo(inventoryData);
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateSamsungRestriction(resourceID, restrictionInfo, 1);
            this.processApps(resourceID, inventoryData, 1);
            this.processContainerStatus(resourceID, inventoryData);
        }
        catch (final Exception ex) {
            SamsungKnoxInventoryHandler.LOGGER.log(Level.SEVERE, ex, () -> "Exception raised on populating KNOX inventory data Resource ID " + n);
            this.isDataPopulationSuccess = false;
        }
    }
    
    private void processContainerStatus(final Long resourceId, final JSONObject inventoryData) {
        try {
            final JSONObject containerStatus = new JSONObject(String.valueOf(inventoryData.get("ContainerStatus")));
            final int status = containerStatus.getInt("containerCurrentStatus");
            KnoxUtil.getInstance().updateStatus(resourceId, -1, null, status);
        }
        catch (final Exception ex) {
            SamsungKnoxInventoryHandler.LOGGER.log(Level.SEVERE, "Error updating container status ", ex);
        }
    }
    
    static {
        SamsungKnoxInventoryHandler.handler = new SamsungKnoxInventoryHandler();
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
