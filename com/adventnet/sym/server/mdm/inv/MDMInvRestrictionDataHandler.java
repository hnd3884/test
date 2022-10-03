package com.adventnet.sym.server.mdm.inv;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.JSONUtil;

public class MDMInvRestrictionDataHandler implements MDMInventory
{
    private static final String DEVICE_RESTRICTION = "DeviceRestriction";
    private static final String ROAMING_RESTRICTION = "RoamingRestriction";
    private static final String BROWSER_RESTRICTION = "BrowserRestriction";
    private static final String APPLICATION_RESTRICTION = "ApplicationRestriction";
    private static final String PHONE_RESTRICTION = "PhoneRestriction";
    private static final String BLUETOOTH_RESTRICTION = "BluetoothRestriction";
    private static final String DEVICE_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.DeviceRestrictionMDMInventoryImpl";
    private static final String ROAMING_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.RoamingRestrictionMDMImventoryImpl";
    private static final String BROWSER_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.BrowserRestrictionMDMInventoryImpl";
    private static final String APPLICATION_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.ApplicationRestrictionMDMInventoryImpl";
    private static final String PHONE_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.PhoneRestrictionMDMInventoryImpl";
    private static final String BLUETOOTH_RESTRICTION_IMPL = "com.adventnet.sym.server.mdm.inv.android.BluetoothRestrictionMDMInventoryImpl";
    private JSONUtil jsonUtil;
    private final HashMap<String, String> instiantiateInventoryImpl;
    private static MDMInventory mdmInvDataPopulator;
    private Logger logger;
    
    protected MDMInvRestrictionDataHandler() {
        this.jsonUtil = JSONUtil.getInstance();
        this.instiantiateInventoryImpl = new HashMap<String, String>();
        this.logger = null;
        this.instiantiateInventoryImpl.put("DeviceRestriction", "com.adventnet.sym.server.mdm.inv.android.DeviceRestrictionMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("RoamingRestriction", "com.adventnet.sym.server.mdm.inv.android.RoamingRestrictionMDMImventoryImpl");
        this.instiantiateInventoryImpl.put("BrowserRestriction", "com.adventnet.sym.server.mdm.inv.android.BrowserRestrictionMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("ApplicationRestriction", "com.adventnet.sym.server.mdm.inv.android.ApplicationRestrictionMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("PhoneRestriction", "com.adventnet.sym.server.mdm.inv.android.PhoneRestrictionMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("BluetoothRestriction", "com.adventnet.sym.server.mdm.inv.android.BluetoothRestrictionMDMInventoryImpl");
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean dataPopulation = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            final Iterator<String> iter = inventoryData.keys();
            while (iter.hasNext()) {
                final String key = iter.next();
                final String className = this.instiantiateInventoryImpl.get(key);
                final String invData = String.valueOf(inventoryData.get(key));
                final MDMInvdetails inventoryRestrictionObject = new MDMInvdetails(inventoryObject.resourceId, invData, inventoryObject.scope);
                (MDMInvRestrictionDataHandler.mdmInvDataPopulator = (MDMInventory)Class.forName(className).newInstance()).populateInventoryData(inventoryRestrictionObject);
            }
            dataPopulation = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred while handling restriction data.. {0}", exp);
            dataPopulation = false;
        }
        return dataPopulation;
    }
    
    static {
        MDMInvRestrictionDataHandler.mdmInvDataPopulator = null;
    }
}
