package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.RestrictionsPayload;
import com.me.mdm.server.config.MDMConfigUtil;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2IOSAccessibilitySettings implements DO2Settings
{
    protected static Logger logger;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settings = new ArrayList<NSDictionary>();
        final ManagedSettingItem accessibilitySetting = new ManagedSettingItem("AccessibilitySettings");
        final NSDictionary dict = accessibilitySetting.getPayloadDict();
        try {
            final Iterator rows = dataObject.getRows("IOSAccessibilitySettings");
            while (rows.hasNext()) {
                final Row setting = rows.next();
                dict.put("BoldTextEnabled", setting.get("BOLD_TEXT_ENABLED"));
                dict.put("IncreaseContrastEnabled", setting.get("INCREASE_CONTRAST_ENABLED"));
                dict.put("ReduceMotionEnabled", setting.get("REDUCE_MOTION_ENABLED"));
                dict.put("ReduceTransparencyEnabled", setting.get("REDUCE_TRANSPARENCY_ENABLED"));
                dict.put("TextSize", setting.get("TEXT_SIZE"));
                dict.put("TouchAccommodationsEnabled", setting.get("TOUCH_ACCOMMODATIONS_ENABLED"));
                final Long collectionID = (Long)dataObject.getRow("CfgDataToCollection").get("COLLECTION_ID");
                final Long customerId = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
                final DataObject DO = kioskHandler.getKioskProfileDetails(collectionID, customerId);
                final Row appLockPolicyRow = DO.getRow("AppLockPolicy");
                boolean isSingleAppKiosk = false;
                if (appLockPolicyRow != null) {
                    final Integer kioskMode = (Integer)appLockPolicyRow.get("KIOSK_MODE");
                    if (kioskMode == 1 || kioskMode == 3) {
                        isSingleAppKiosk = true;
                    }
                }
                if (!isSingleAppKiosk) {
                    dict.put("VoiceOverEnabled", setting.get("VOICE_OVER_ENABLED"));
                    dict.put("ZoomEnabled", setting.get("ZOOM_ENABLED"));
                }
            }
        }
        catch (final Exception e) {
            DO2IOSAccessibilitySettings.logger.log(Level.SEVERE, "Exception while creating DO2IOSAccessibilitySettings", e);
        }
        settings.add(dict);
        return settings;
    }
    
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] payloads = null;
        try {
            final Row row = dataObject.getRow("CfgDataToCollection");
            final Long collectionID = (Long)row.get("COLLECTION_ID");
            final List<DataObject> configIDList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            if (configIDList.size() == 1) {
                payloads = new IOSPayload[] { null };
                final RestrictionsPayload restrictionsPayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.restrictions", "Dummy Restriction Policy for Accessibility settings");
                payloads[0] = restrictionsPayload;
            }
        }
        catch (final Exception e) {
            DO2IOSAccessibilitySettings.logger.log(Level.SEVERE, "Exception while creating dummy restriction payload for AccessibilitySettings", e);
        }
        return payloads;
    }
    
    static {
        DO2IOSAccessibilitySettings.logger = Logger.getLogger("MDMConfigLogger");
    }
}
