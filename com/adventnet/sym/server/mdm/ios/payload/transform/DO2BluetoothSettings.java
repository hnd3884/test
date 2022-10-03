package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2BluetoothSettings implements DO2Settings
{
    private static final Logger LOGGER;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        try {
            final Row restrictionRow = dataObject.getFirstRow("RestrictionsPolicy");
            final Integer bluetoothSetting = (Integer)restrictionRow.get("BLUETOOTH_SETTING");
            if (bluetoothSetting != 2) {
                DO2BluetoothSettings.LOGGER.log(Level.INFO, "Bluetooth Setting is configured with {0}", new Object[(int)bluetoothSetting]);
                final boolean bluetoothValue = bluetoothSetting == 1;
                settingList.add(this.createBluetoothSetting(bluetoothValue).getPayloadDict());
            }
        }
        catch (final DataAccessException e) {
            DO2BluetoothSettings.LOGGER.log(Level.SEVERE, "Exception in restriction Object", (Throwable)e);
        }
        return settingList;
    }
    
    private ManagedSettingItem createBluetoothSetting(final boolean value) {
        final ManagedSettingItem managedSettingItem = new ManagedSettingItem("Bluetooth");
        managedSettingItem.setSettingEnabled(value);
        return managedSettingItem;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
