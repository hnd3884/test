package com.me.mdm.onpremise.api.checkforupdate;

import com.adventnet.persistence.DataObject;
import com.me.mdm.onpremise.server.admin.MDMPFlashMessageConstants;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.ems.onpremise.server.util.NotifyUpdatesUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProductUpdatesSettingsFacade
{
    public Logger logger;
    public static String is_VERSION_NOTIFICATION_ENABLED;
    public static String is_FLASHMSG_NOTIFICATION_ENABLED;
    public static String is_BUILD_UPDATE_AVAILABLE;
    
    public ProductUpdatesSettingsFacade() {
        this.logger = Logger.getLogger(ProductUpdatesSettingsFacade.class.getName());
    }
    
    public JSONObject getUpdatesNotificationsettings() {
        final JSONObject updatesNotificationJSON = new JSONObject();
        try {
            updatesNotificationJSON.put(ProductUpdatesSettingsFacade.is_VERSION_NOTIFICATION_ENABLED, NotifyUpdatesUtil.getProductUpdatesNotificationSettings());
            updatesNotificationJSON.put(ProductUpdatesSettingsFacade.is_FLASHMSG_NOTIFICATION_ENABLED, NotifyUpdatesUtil.getFlashMsgNotificationSettings());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in getting ProductUpdatesSettings details...", ex);
            throw new APIHTTPException("CHKU0010", new Object[0]);
        }
        return updatesNotificationJSON;
    }
    
    public JSONObject isBuildNotificationAvailable() {
        final JSONObject updatesNotificationJSON = new JSONObject();
        try {
            updatesNotificationJSON.put(ProductUpdatesSettingsFacade.is_BUILD_UPDATE_AVAILABLE, NotifyUpdatesUtil.getInstance().isBuildNotificationAvailable().get("show_build_notification"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in getting isBuildNotificationAvailable details...", ex);
            throw new APIHTTPException("CHKU0012", new Object[0]);
        }
        return updatesNotificationJSON;
    }
    
    public JSONObject enableRemindMeLater(final JSONObject jsonObject) {
        final JSONObject updatesNotificationJSON = new JSONObject();
        String result = "Failed";
        try {
            this.logger.log(Level.INFO, "enableRemindMeLater method called");
            final JSONObject updatesNotificationSettings = jsonObject.getJSONObject("msg_body");
            final int message_priority = Integer.parseInt(updatesNotificationSettings.get("updates_msg_priority").toString());
            long notificationFrequency = 0L;
            if (message_priority == 0) {
                notificationFrequency = 86400000L;
            }
            else if (message_priority == 1) {
                notificationFrequency = 10800000L;
            }
            NotifyUpdatesUtil.getInstance().enableRemindMeLater(notificationFrequency);
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            result = "Success";
            DCEventLogUtil.getInstance().addEvent(10022, userName, (HashMap)null, "mdmp.chk_for_update_do_it_later", (Object)null, true);
            this.logger.log(Level.INFO, "enabled RemindMeLater of notification");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in getting ProductUpdatesSettings details...", ex);
            throw new APIHTTPException("CHKU0015", new Object[0]);
        }
        updatesNotificationJSON.put("result", (Object)result);
        return updatesNotificationJSON;
    }
    
    public JSONObject saveUpdatesNotificationsettings(final JSONObject jsonObject) {
        final JSONObject resultData = new JSONObject();
        String result = "Failed";
        try {
            final JSONObject updatesNotificationSettings = jsonObject.getJSONObject("msg_body");
            this.logger.log(Level.INFO, "saveUpdatesNotificationsettings method called");
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final boolean versionUpdateNotificationSettings = updatesNotificationSettings.getBoolean(ProductUpdatesSettingsFacade.is_VERSION_NOTIFICATION_ENABLED);
            NotifyUpdatesUtil.getInstance().updateProductUpdatesNotificationSettings(versionUpdateNotificationSettings);
            final boolean flashMsgUpdateNotificationSettings = updatesNotificationSettings.getBoolean(ProductUpdatesSettingsFacade.is_FLASHMSG_NOTIFICATION_ENABLED);
            NotifyUpdatesUtil.getInstance().updateFlashMsgNotificationSettings(flashMsgUpdateNotificationSettings);
            DCEventLogUtil.getInstance().addEvent(10020, userName, (HashMap)null, "mdmp.chk_for_update_notification_settings", (Object)null, true);
            result = "Success";
            this.logger.log(Level.INFO, "Saved Updates Notification settings ");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in saving  saveUpdatesNotificationsettings", ex);
            throw new APIHTTPException("CHKU0011", new Object[0]);
        }
        resultData.put("result", (Object)result);
        return resultData;
    }
    
    public JSONObject removeUpdatesNotificationsettings(final JSONObject jsonObject) {
        final JSONObject resultData = new JSONObject();
        String result = "Failed";
        try {
            this.logger.log(Level.INFO, "removeUpdatesNotificationsettings called");
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            NotifyUpdatesUtil.getInstance().removeNotificationIcon();
            result = "Success";
            DCEventLogUtil.getInstance().addEvent(10021, userName, (HashMap)null, "mdmp.chk_for_update_remove_notification", (Object)null, true);
            this.logger.log(Level.INFO, "removed Updates Notification settings ");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in saving  saveUpdatesNotificationsettings", ex);
            throw new APIHTTPException("CHKU0014", new Object[0]);
        }
        resultData.put("result", (Object)result);
        return resultData;
    }
    
    public JSONObject getBuildVersionDetails() {
        this.logger.log(Level.INFO, "Inside getBuildVersionDetails");
        JSONObject versionUpdatesJson = new JSONObject();
        try {
            long no_of_clicks = 0L;
            versionUpdatesJson = NotifyUpdatesUtil.checkforVersionUpdate();
            final String msg_status = versionUpdatesJson.get("msg_status").toString();
            final Criteria criteria = new Criteria(Column.getColumn("CheckforUpdatesStatusClicks", "STATUS"), (Object)msg_status, 0, false);
            final DataObject updParamsDO = DataAccess.get("CheckforUpdatesStatusClicks", criteria);
            if (updParamsDO.isEmpty()) {
                final Row r = new Row("CheckforUpdatesStatusClicks");
                r.set("STATUS", (Object)msg_status);
                r.set("COUNT", (Object)(++no_of_clicks));
                updParamsDO.addRow(r);
            }
            else {
                final Row r = updParamsDO.getRow("CheckforUpdatesStatusClicks", criteria);
                no_of_clicks = (long)r.get("COUNT");
                ++no_of_clicks;
                r.set("COUNT", (Object)no_of_clicks);
                updParamsDO.updateRow(r);
            }
            DataAccess.update(updParamsDO);
            if (versionUpdatesJson.has("error_code") && Integer.parseInt(versionUpdatesJson.get("error_code").toString()) == 2) {
                final Properties props = new Properties();
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "MDMPUpdatesCheckerTask");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                taskInfoMap.put("poolName", "mdmPool");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.onpremise.server.admin.task.MDMPUpdatesCheckerTask", taskInfoMap, props, "mdmPool");
                this.logger.info("Setting the value in updateparams");
                UpdatesParamUtil.updateUpdParams("MDMPUpdatesCheckerTask", "started");
                synchronized (MDMPFlashMessageConstants.TASKCHECKER) {
                    try {
                        MDMPFlashMessageConstants.TASKCHECKER.wait(10000L);
                        this.logger.log(Level.INFO, "waiting for a thread signal");
                    }
                    catch (final InterruptedException e) {
                        this.logger.log(Level.WARNING, "Exception occurred inside synchronized block: ", e);
                    }
                }
                this.logger.log(Level.INFO, "Reinvoking the method for updates");
                versionUpdatesJson = NotifyUpdatesUtil.checkforVersionUpdate();
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception occurred in CheckforUpdatesAction: ", e2);
            throw new APIHTTPException("CHKU0013", new Object[0]);
        }
        return versionUpdatesJson;
    }
    
    static {
        ProductUpdatesSettingsFacade.is_VERSION_NOTIFICATION_ENABLED = "is_version_notification_enabled";
        ProductUpdatesSettingsFacade.is_FLASHMSG_NOTIFICATION_ENABLED = "is_flashmsg_notification_enabled";
        ProductUpdatesSettingsFacade.is_BUILD_UPDATE_AVAILABLE = "is_build_update_available";
    }
}
