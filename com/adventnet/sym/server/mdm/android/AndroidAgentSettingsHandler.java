package com.adventnet.sym.server.mdm.android;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Properties;
import java.util.logging.Logger;

public class AndroidAgentSettingsHandler
{
    public Logger logger;
    private static AndroidAgentSettingsHandler settingsHandler;
    
    public AndroidAgentSettingsHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static AndroidAgentSettingsHandler getInstance() {
        if (AndroidAgentSettingsHandler.settingsHandler == null) {
            AndroidAgentSettingsHandler.settingsHandler = new AndroidAgentSettingsHandler();
        }
        return AndroidAgentSettingsHandler.settingsHandler;
    }
    
    public Properties getAndroidSettings(final Long customerID) throws Exception {
        final Properties prop = new Properties();
        final Criteria custCri = new Criteria(new Column("AndroidAgentSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        final DataObject settingsDO = MDMUtil.getPersistence().get("AndroidAgentSettings", custCri);
        if (!settingsDO.isEmpty()) {
            final Row row = settingsDO.getFirstRow("AndroidAgentSettings");
            ((Hashtable<String, Object>)prop).put("GRACE_TIME", row.get("GRACE_TIME"));
            ((Hashtable<String, Object>)prop).put("USER_REM_TIME", row.get("USER_REM_TIME"));
            ((Hashtable<String, Object>)prop).put("USER_REM_COUNT", row.get("USER_REM_COUNT"));
            ((Hashtable<String, String>)prop).put("DEACTIVATION_MESSAGE", I18N.getMsg((String)row.get("DEACTIVATION_MESSAGE"), new Object[0]));
            ((Hashtable<String, Object>)prop).put("ALLOW_ADMIN_DISABLE", row.get("ALLOW_ADMIN_DISABLE"));
            ((Hashtable<String, Object>)prop).put("HIDE_SERVER_DETAILS", row.get("HIDE_SERVER_DETAILS"));
            ((Hashtable<String, Object>)prop).put("HIDE_MDM_APP", row.get("HIDE_MDM_APP"));
            ((Hashtable<String, Object>)prop).put("HIDE_SERVER_INFO", row.get("HIDE_SERVER_INFO"));
            ((Hashtable<String, Object>)prop).put("RECOVERY_PASSWORD_ENCRYPTED", row.get("RECOVERY_PASSWORD_ENCRYPTED"));
        }
        return prop;
    }
    
    public void addOrUpdateAndroidSettings(final Properties prop) throws Exception {
        final Criteria custCri = new Criteria(new Column("AndroidAgentSettings", "CUSTOMER_ID"), (Object)((Hashtable<K, Long>)prop).get("CUSTOMER_ID"), 0);
        final DataObject settingsDO = MDMUtil.getPersistence().get("AndroidAgentSettings", custCri);
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String passEncrypted = MDMUtil.generateNewRandomToken("AndroidAgentSettings", "RECOVERY_PASSWORD_ENCRYPTED", "SETTINGS_ID");
        if (settingsDO.isEmpty()) {
            final Row row = new Row("AndroidAgentSettings");
            row.set("GRACE_TIME", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("GRACE_TIME")));
            row.set("USER_REM_TIME", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("USER_REM_TIME")));
            row.set("USER_REM_COUNT", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("USER_REM_COUNT")));
            row.set("DEACTIVATION_MESSAGE", ((Hashtable<K, Object>)prop).get("DEACTIVATION_MESSAGE"));
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("ALLOW_ADMIN_DISABLE", (Object)Boolean.valueOf(((Hashtable<K, String>)prop).get("DEVICEADMIN_DISABLE")));
            row.set("SHORT_SUPPORT_MESSAGE", ((Hashtable<K, Object>)prop).get("SHORT_SUPPORT_MESSAGE"));
            row.set("LONG_SUPPORT_MESSAGE", ((Hashtable<K, Object>)prop).get("LONG_SUPPORT_MESSAGE"));
            row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)passEncrypted);
            settingsDO.addRow(row);
            MDMUtil.getPersistence().add(settingsDO);
        }
        else {
            final Row row = settingsDO.getFirstRow("AndroidAgentSettings");
            row.set("GRACE_TIME", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("GRACE_TIME")));
            row.set("USER_REM_TIME", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("USER_REM_TIME")));
            row.set("USER_REM_COUNT", (Object)Integer.parseInt(((Hashtable<K, String>)prop).get("USER_REM_COUNT")));
            row.set("DEACTIVATION_MESSAGE", ((Hashtable<K, Object>)prop).get("DEACTIVATION_MESSAGE"));
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("ALLOW_ADMIN_DISABLE", (Object)Boolean.valueOf(((Hashtable<K, String>)prop).get("DEVICEADMIN_DISABLE")));
            row.set("SHORT_SUPPORT_MESSAGE", ((Hashtable<K, Object>)prop).get("SHORT_SUPPORT_MESSAGE"));
            row.set("LONG_SUPPORT_MESSAGE", ((Hashtable<K, Object>)prop).get("LONG_SUPPORT_MESSAGE"));
            if (row.get("RECOVERY_PASSWORD_ENCRYPTED") == null) {
                row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)passEncrypted);
            }
            else {
                row.set("RECOVERY_PASSWORD_ENCRYPTED", ((Hashtable<K, Object>)prop).get("RECOVERY_PASSWORD_ENCRYPTED"));
            }
            settingsDO.updateRow(row);
            MDMUtil.getPersistence().update(settingsDO);
        }
        final List resList = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerId);
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resList);
        NotificationHandler.getInstance().SendNotification(resList, 2);
    }
    
    public void addDefaultAndroidSettings(final Long customerId) {
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("GRACE_TIME", "5");
        ((Hashtable<String, String>)prop).put("USER_REM_TIME", "30");
        ((Hashtable<String, String>)prop).put("USER_REM_COUNT", "5");
        ((Hashtable<String, String>)prop).put("DEACTIVATION_MESSAGE", "dc.mdm.android.agent_settings.deactivation_msg");
        ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerId);
        ((Hashtable<String, Boolean>)prop).put("VALIDATE_CHECKSUM", true);
        ((Hashtable<String, String>)prop).put("SHORT_SUPPORT_MESSAGE", "mdm.android.short.support_message");
        ((Hashtable<String, String>)prop).put("LONG_SUPPORT_MESSAGE", "mdm.android.long.support_message");
        try {
            this.addOrUpdateAndroidSettings(prop);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while adding android settings for customer id " + n);
        }
    }
    
    static {
        AndroidAgentSettingsHandler.settingsHandler = null;
    }
}
