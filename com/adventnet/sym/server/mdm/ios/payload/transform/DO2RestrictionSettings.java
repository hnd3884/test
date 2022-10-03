package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Collection;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;

public class DO2RestrictionSettings implements DO2Settings
{
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        final DO2BluetoothSettings bluetoothSettings = new DO2BluetoothSettings();
        final List bluetoothList = bluetoothSettings.createSettingCommand(dataObject, params);
        settingList.addAll(bluetoothList);
        final DO2HotspotSettings hotspotSettings = new DO2HotspotSettings();
        final List hotspotList = hotspotSettings.createSettingCommand(dataObject, params);
        settingList.addAll(hotspotList);
        final DO2TimeZoneSettings timeZoneSettings = new DO2TimeZoneSettings();
        settingList.addAll(timeZoneSettings.createSettingCommand(dataObject, params));
        return settingList;
    }
}
