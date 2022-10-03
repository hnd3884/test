package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2TimeZoneSettings implements DO2Settings
{
    private static final Logger LOGGER;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        try {
            final Row restrictionRow = dataObject.getFirstRow("RestrictionsPolicy");
            final String timeZone = (String)restrictionRow.get("TIME_ZONE");
            if (!MDMStringUtils.isEmpty(timeZone)) {
                settingList.add(this.createTimeZoneSetting(timeZone).getPayloadDict());
            }
        }
        catch (final DataAccessException e) {
            DO2TimeZoneSettings.LOGGER.log(Level.SEVERE, "Exception in restriction Object", (Throwable)e);
        }
        return settingList;
    }
    
    private ManagedSettingItem createTimeZoneSetting(final String timeZone) {
        final ManagedSettingItem managedSettingItem = new ManagedSettingItem("TimeZone");
        managedSettingItem.setTimeZone(timeZone);
        return managedSettingItem;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
