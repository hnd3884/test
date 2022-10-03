package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.sym.server.mdm.ios.payload.RestrictionsPayload;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.ios.payload.AppLockPayload;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AppLockPayload implements DO2Payload
{
    public Logger logger;
    public static final int KIOSK_MODE_NONE = 0;
    public static final int KIOSK_MODE_SINGLE = 1;
    public static final int KIOSK_MODE_MULTIPLE = 2;
    public static final int KIOSK_MODE_SINGLE_WEBAPP = 3;
    private int mode;
    
    public DO2AppLockPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] payloadArray = null;
        IOSPayload payload = null;
        try {
            this.mode = (int)dataObject.getFirstRow("AppLockPolicy").get("KIOSK_MODE");
            final int size = this.computePayloadSize(dataObject);
            payloadArray = new IOSPayload[size];
            if (this.mode == 1 || this.mode == 3) {
                payload = this.createAppLockPayload(dataObject);
                payloadArray[0] = payload;
            }
            else {
                int count = 0;
                payload = this.createRestrictionsPayload(dataObject);
                payloadArray[count++] = payload;
                if (dataObject.containsTable("ScreenLayout")) {
                    payloadArray[count++] = new DO2HomeScreenLayoutPayload().createPayload(dataObject)[0];
                }
                if (dataObject.containsTable("WebClipToConfigRel")) {
                    final IOSPayload[] array = new DO2WebClipsPayload().createPayload(dataObject);
                    for (int i = 0; i < array.length; ++i) {
                        payloadArray[count++] = array[i];
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "DO2AppLockPayload: Error while createPayload() ", ex);
        }
        return payloadArray;
    }
    
    private int computePayloadSize(final DataObject dataObject) throws Exception {
        int size = 1;
        if (this.mode == 2) {
            if (dataObject.containsTable("ScreenLayout")) {
                ++size;
            }
            if (dataObject.containsTable("WebClipToConfigRel")) {
                size += dataObject.size("WebClipToConfigRel");
            }
        }
        return size;
    }
    
    private IOSPayload createAppLockPayload(final DataObject dataObject) throws Exception {
        AppLockPayload appLockPayload = null;
        final Iterator iterator = dataObject.getRows("AppLockPolicy");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long configDataItemID = (Long)row.get("CONFIG_DATA_ITEM_ID");
            String identifier = null;
            final Iterator<Row> rows = getPolicyAppsRows(dataObject, configDataItemID);
            if (rows.hasNext()) {
                final Row appGrpRow = rows.next();
                identifier = (String)appGrpRow.get("IDENTIFIER");
                final int platformType = (int)appGrpRow.get("PLATFORM_TYPE");
                if (platformType == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                    identifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(identifier);
                }
            }
            if (identifier == null) {
                identifier = (String)row.get("IDENTIFIER");
            }
            appLockPayload = new AppLockPayload(1, "MDM", "com.mdm.mobiledevice.applock", "AppLock Policy");
            appLockPayload.setAppIdentifier(identifier);
            appLockPayload.setDisableTouchOption((boolean)row.get("DISABLE_TOUCH"));
            appLockPayload.setDisableDeviceRotationOption((boolean)row.get("DISABLE_DEVICE_ROTATION"));
            appLockPayload.setDisableVolumeButtonsOption((boolean)row.get("DISABLE_VOLUME_BUTTONS"));
            appLockPayload.setDisableRingerSwitchOption((boolean)row.get("DISABLE_RINGER_SWITCH"));
            appLockPayload.setDisableSleepWakeButtonOption((boolean)row.get("DISABLE_SLEEP_BUTTON"));
            appLockPayload.setDisableAutoLockOption((boolean)row.get("DISABLE_AUTO_LOCK"));
            appLockPayload.setVoiceOverConfiguration((boolean)row.get("VOICE_OVER"));
            appLockPayload.setZoomConfiguration((boolean)row.get("ZOOM"));
            appLockPayload.setInvertColorsConfiguration((boolean)row.get("INVERT_COLORS"));
            appLockPayload.setAssistiveTouchConfiguration((boolean)row.get("ASST_TOUCH"));
            appLockPayload.setEnableSpeakSelectionOption((boolean)row.get("ENABLE_SPEAK_SELECTION"));
            appLockPayload.setEnableMonoAudioOption((boolean)row.get("ENABLE_MONO_AUDIO"));
            appLockPayload.setEnableVoiceControl((boolean)row.get("ENABLE_VOICE_CONTROL"));
        }
        return appLockPayload;
    }
    
    private IOSPayload createRestrictionsPayload(final DataObject dataObject) throws Exception {
        RestrictionsPayload restrictionPayload = null;
        final Iterator iterator = dataObject.getRows("AppLockPolicy");
        while (iterator.hasNext()) {
            final Row policyRow = iterator.next();
            final Long configDataItemID = (Long)policyRow.get("CONFIG_DATA_ITEM_ID");
            final List<String> whitelistApps = new ArrayList<String>();
            final List<String> autonomousApps = new ArrayList<String>();
            final Iterator<Row> appRows = getPolicyAppsRows(dataObject, configDataItemID);
            while (appRows.hasNext()) {
                final Row appGrpRow = appRows.next();
                final Long appGrpId = (Long)appGrpRow.get("APP_GROUP_ID");
                final int platformType = (int)appGrpRow.get("PLATFORM_TYPE");
                String identifier = (String)appGrpRow.get("IDENTIFIER");
                if (platformType == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                    identifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(identifier);
                }
                if (!whitelistApps.contains(identifier)) {
                    whitelistApps.add(identifier);
                }
                if (this.isAutonomousEnabled(dataObject, configDataItemID, appGrpId)) {
                    autonomousApps.add(identifier);
                }
            }
            restrictionPayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.applock", "AppLock Whitelist Policy");
            if (this.mode == 0 && !autonomousApps.isEmpty()) {
                restrictionPayload.setAutonomousSingleAppModePermittedAppIDs(autonomousApps);
            }
            if (this.mode == 2) {
                if (!whitelistApps.isEmpty()) {
                    final boolean showMEMDM = (boolean)policyRow.get("SHOW_ME_MDM_APP");
                    if (showMEMDM) {
                        whitelistApps.add("com.manageengine.mdm.iosagent");
                    }
                    whitelistApps.add("com.apple.webapp");
                    restrictionPayload.setWhitelistedAppBundleIDs(whitelistApps);
                }
                if (autonomousApps.isEmpty()) {
                    continue;
                }
                restrictionPayload.setAutonomousSingleAppModePermittedAppIDs(autonomousApps);
            }
        }
        return restrictionPayload;
    }
    
    public static Iterator<Row> getPolicyAppsRows(final DataObject dataObject, final Long configDataItemID) throws DataAccessException {
        final Criteria cfgCriteria = new Criteria(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
        final Join appGrpJoin = new Join("AppLockPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        return dataObject.getRows("MdAppGroupDetails", cfgCriteria, appGrpJoin);
    }
    
    private boolean isAutonomousEnabled(final DataObject dataObject, final Long configDataItemID, final Long appGrpID) throws DataAccessException {
        final Criteria cfgCriteria = new Criteria(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
        final Criteria appCriteria = new Criteria(new Column("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appGrpID, 0);
        return (boolean)dataObject.getRow("AppLockPolicyApps", cfgCriteria.and(appCriteria)).get("IS_AUTO_KIOSK_ALLOWED");
    }
    
    public IOSPayload createDefaultMDMKiosk() {
        final AppLockPayload appLock = new AppLockPayload(1, "MDM", "com.mdm.mobiledevice.applock", "AppLock Policy");
        appLock.setAppIdentifier("com.manageengine.mdm.iosagent");
        return appLock;
    }
}
