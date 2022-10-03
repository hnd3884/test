package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.ios.payload.AppNotificationPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;

public class DO2AppNotificationPolicyPayload implements DO2Payload
{
    private static HashMap DEFAULT_VALUES;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloads = { null };
        final HashMap defaultValues = new HashMap();
        try {
            final AppNotificationPolicyPayload payload = new AppNotificationPolicyPayload(1, "MDM", "com.mdm.mobiledevice.notification", "App Notification Policy");
            final Iterator notificationPolicyItr = dataObject.getRows("MdmAppNotificationPolicy");
            final List<AppNotificationPolicyPayload.NotificationSettingsItem> notificationSettings = new ArrayList<AppNotificationPolicyPayload.NotificationSettingsItem>();
            while (notificationPolicyItr.hasNext()) {
                final Row notificationPolicy = notificationPolicyItr.next();
                final Long notificationPolicyId = (Long)notificationPolicy.get("MDM_APP_NOTIFICATION_POLICY_ID");
                final Criteria appIdCriteria = new Criteria(new Column("MdmAppNotificationPolicyToAppRel", "APP_GROUP_ID"), (Object)new Column("MdAppGroupDetails", "APP_GROUP_ID"), 0);
                final Criteria notificationPolicyIdCriteria = new Criteria(new Column("MdmAppNotificationPolicyToAppRel", "MDM_APP_NOTIFICATION_POLICY_ID"), (Object)notificationPolicyId, 0);
                final Iterator appDetailsItr = dataObject.getRows("MdAppGroupDetails", appIdCriteria.and(notificationPolicyIdCriteria));
                while (appDetailsItr.hasNext()) {
                    final Row app = appDetailsItr.next();
                    final String appBundleId = ((String)app.get("IDENTIFIER")).trim();
                    final int alertType = (int)notificationPolicy.get("ALERT_TYPE");
                    final boolean badgesEnabled = (boolean)notificationPolicy.get("BADGES_ENABLED");
                    final boolean criticalAlertEnabled = (boolean)notificationPolicy.get("CRITICAL_ALERT_ENABLED");
                    final int groupingType = (int)notificationPolicy.get("GROUPING_TYPE");
                    final boolean notificationsEnabled = (boolean)notificationPolicy.get("NOTIFICATIONS_ENABLED");
                    final int previewType = (int)notificationPolicy.get("PREVIEW_TYPE");
                    final boolean showInCarPlay = (boolean)notificationPolicy.get("SHOW_IN_CAR_PLAY");
                    final boolean showInLockScreen = (boolean)notificationPolicy.get("SHOW_IN_LOCK_SCREEN");
                    final boolean showInNotificationCenter = (boolean)notificationPolicy.get("SHOW_IN_NOTIFICATION_CENTER");
                    final boolean soundsEnabled = (boolean)notificationPolicy.get("SOUNDS_ENABLED");
                    final AppNotificationPolicyPayload this$0 = payload;
                    this$0.getClass();
                    final AppNotificationPolicyPayload.NotificationSettingsItem settingsItem = this$0.new NotificationSettingsItem();
                    settingsItem.setBundleIdentifier(appBundleId);
                    settingsItem.setNotificationsEnabled(notificationsEnabled);
                    if (notificationsEnabled) {
                        settingsItem.setBundleIdentifier(appBundleId);
                        settingsItem.setAlertType(alertType);
                        settingsItem.setBadgesEnabled(badgesEnabled);
                        settingsItem.setCriticalAlertEnabled(criticalAlertEnabled);
                        settingsItem.setNotificationsEnabled(notificationsEnabled);
                        settingsItem.setShowInLockScreen(showInLockScreen);
                        settingsItem.setShowInNotificationCenter(showInNotificationCenter);
                        settingsItem.setSoundsEnabled(soundsEnabled);
                        if (groupingType != DO2AppNotificationPolicyPayload.DEFAULT_VALUES.get("GROUPING_TYPE")) {
                            settingsItem.setGroupingType(groupingType);
                        }
                        if (previewType != DO2AppNotificationPolicyPayload.DEFAULT_VALUES.get("PREVIEW_TYPE")) {
                            settingsItem.setPreviewType(previewType);
                        }
                        if (showInCarPlay != DO2AppNotificationPolicyPayload.DEFAULT_VALUES.get("SHOW_IN_CAR_PLAY")) {
                            settingsItem.setShowInCarPlay(showInCarPlay);
                        }
                    }
                    notificationSettings.add(settingsItem);
                }
            }
            payload.setNotificationSettings(notificationSettings);
            payloads[0] = payload;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in App Notification Payload", e);
        }
        return payloads;
    }
    
    static {
        DO2AppNotificationPolicyPayload.DEFAULT_VALUES = new HashMap() {
            {
                this.put("ALERT_TYPE", 1);
                this.put("BADGES_ENABLED", true);
                this.put("CRITICAL_ALERT_ENABLED", false);
                this.put("GROUPING_TYPE", 0);
                this.put("PREVIEW_TYPE", 0);
                this.put("SHOW_IN_CAR_PLAY", true);
                this.put("SHOW_IN_LOCK_SCREEN", true);
                this.put("SHOW_IN_NOTIFICATION_CENTER", true);
                this.put("SOUNDS_ENABLED", true);
            }
        };
    }
}
