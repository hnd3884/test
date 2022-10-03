package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class BluetoothRestrictionMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String ALLOW_BT_DISCOVERABLE = "setDiscoverableState";
    private static final String ALLOW_BT_PAIRING = "setPairingState";
    private static final String ALLOW_BT_OUTGOING_CALLS = "allowOutgoingCallsViaBluetooth";
    private static final String ALLOW_BT_PC_CONNECTION = "setDesktopConnectivityState";
    private static final String ALLOW_BT_DATA_TRANSFER = "setAllowBluetoothDataTransfer";
    
    public BluetoothRestrictionMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject bluetoothRestriction = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("ALLOW_BT_DATA_TRANSFER", bluetoothRestriction.optString("setAllowBluetoothDataTransfer", "-1"));
            restrictionInfo.put("ALLOW_BT_DISCOVERABLE", bluetoothRestriction.optString("setDiscoverableState", "-1"));
            restrictionInfo.put("ALLOW_BT_OUTGOING_CALLS", bluetoothRestriction.optString("allowOutgoingCallsViaBluetooth", "-1"));
            restrictionInfo.put("ALLOW_BT_PAIRING", bluetoothRestriction.optString("setPairingState", "-1"));
            restrictionInfo.put("ALLOW_BT_PC_CONNECTION", bluetoothRestriction.optString("setDesktopConnectivityState", "-1"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateAndroidSamsungRestriction(inventoryObject.resourceId, restrictionInfo, inventoryObject.scope);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating device details from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
