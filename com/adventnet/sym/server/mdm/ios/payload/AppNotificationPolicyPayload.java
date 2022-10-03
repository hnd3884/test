package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import java.util.List;
import com.dd.plist.NSArray;

public class AppNotificationPolicyPayload extends IOSPayload
{
    NSArray notificationSettings;
    
    public AppNotificationPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.notificationsettings", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.notificationSettings = null;
    }
    
    public NSArray getNotificationSettings() {
        return this.notificationSettings;
    }
    
    public void setNotificationSettings(final List<NotificationSettingsItem> settingItems) {
        this.notificationSettings = new NSArray(settingItems.size());
        for (int i = 0; i < settingItems.size(); ++i) {
            this.notificationSettings.setValue(i, (Object)settingItems.get(i).notificationSettingsItemDict);
        }
        this.getPayloadDict().put("NotificationSettings", (NSObject)this.notificationSettings);
    }
    
    public class NotificationSettingsItem
    {
        NSDictionary notificationSettingsItemDict;
        
        public NotificationSettingsItem() {
            this.notificationSettingsItemDict = null;
            this.notificationSettingsItemDict = new NSDictionary();
        }
        
        public void setAlertType(final int alertType) {
            this.notificationSettingsItemDict.put("AlertType", (Object)alertType);
        }
        
        public void setBadgesEnabled(final boolean badgesEnabled) {
            this.notificationSettingsItemDict.put("BadgesEnabled", (Object)badgesEnabled);
        }
        
        public void setBundleIdentifier(final String bundleIdentifier) {
            this.notificationSettingsItemDict.put("BundleIdentifier", (Object)bundleIdentifier);
        }
        
        public void setCriticalAlertEnabled(final boolean criticalAlertEnabled) {
            this.notificationSettingsItemDict.put("CriticalAlertEnabled", (Object)criticalAlertEnabled);
        }
        
        public void setGroupingType(final int groupingType) {
            this.notificationSettingsItemDict.put("GroupingType", (Object)groupingType);
        }
        
        public void setNotificationsEnabled(final boolean notificationsEnabled) {
            this.notificationSettingsItemDict.put("NotificationsEnabled", (Object)notificationsEnabled);
        }
        
        public void setPreviewType(final int previewType) {
            this.notificationSettingsItemDict.put("PreviewType", (Object)previewType);
        }
        
        public void setShowInCarPlay(final boolean showInCarPlay) {
            this.notificationSettingsItemDict.put("ShowInCarPlay", (Object)showInCarPlay);
        }
        
        public void setShowInLockScreen(final boolean showInLockScreen) {
            this.notificationSettingsItemDict.put("ShowInLockScreen", (Object)showInLockScreen);
        }
        
        public void setShowInNotificationCenter(final boolean showInNotificationCenter) {
            this.notificationSettingsItemDict.put("ShowInNotificationCenter", (Object)showInNotificationCenter);
        }
        
        public void setSoundsEnabled(final boolean soundsEnabled) {
            this.notificationSettingsItemDict.put("SoundsEnabled", (Object)soundsEnabled);
        }
    }
}
