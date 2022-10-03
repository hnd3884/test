package com.me.ems.onpremise.server.util;

import java.util.Date;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.io.Reader;
import java.io.FileReader;
import java.util.Properties;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;

public class NotifyUpdatesUtil extends com.me.devicemanagement.framework.server.util.NotifyUpdatesUtil
{
    private static Logger logger;
    public static final String STOP_VERSION_MSG = "stop_version_msg";
    public static final String ERROR_CODE = "error_code";
    public static final String ERROR_CODE_DESC = "error_code_desc";
    public static final String MSG_STATUS = "msg_status";
    public static final String URL = "URL";
    public static final String HAS_CUSTOM_PPM = "has_custom_ppm";
    public static final String SHOW_MESSAGE = "show_message";
    public static final String DIFFERENCE = "difference";
    public static final String BUILD_VERSION = "build_version";
    public static final String UPDATES_MSG = "updates_msg";
    public static final String UPDATES_MSG_PRIORITY = "updates_msg_priority";
    public static final String IN_DB = "In_DB";
    public static final String ERROR = "error";
    private static NotifyUpdatesUtil notifyUtil;
    
    private NotifyUpdatesUtil() {
    }
    
    public static NotifyUpdatesUtil getInstance() {
        if (NotifyUpdatesUtil.notifyUtil == null) {
            NotifyUpdatesUtil.notifyUtil = new NotifyUpdatesUtil();
        }
        return NotifyUpdatesUtil.notifyUtil;
    }
    
    public void deleteLoginIdsFromNotifyTable() throws DataAccessException {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get("NotifiedUserForUpdates", (Criteria)null);
            if (!dataObject.isEmpty()) {
                dataObject.deleteRows("NotifiedUserForUpdates", (Criteria)null);
                DataAccess.update(dataObject);
            }
        }
        catch (final DataAccessException ex) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception got while clearing NotifiedUserForUpdates table ", (Throwable)ex);
        }
    }
    
    public void deleteLoginIdsFromEosNotifyTable() throws DataAccessException {
        try {
            DataAccess.delete("NotifiedUserForEOS", (Criteria)null);
        }
        catch (final DataAccessException ex) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception got while clearing NotifiedUserForEOS table ", (Throwable)ex);
        }
    }
    
    public static boolean getProductUpdatesNotificationSettings() {
        final String productUpdatesNotification = UpdatesParamUtil.getUpdParameter("PRODUCT_UPDATE_NOTIFICATION_ENABLED");
        Boolean isproductUpdatesNotificationEnabled = Boolean.TRUE;
        if (productUpdatesNotification != null) {
            isproductUpdatesNotificationEnabled = Boolean.valueOf(productUpdatesNotification);
        }
        NotifyUpdatesUtil.logger.log(Level.INFO, "Is Product Updates Notification Enabled ", isproductUpdatesNotificationEnabled);
        return isproductUpdatesNotificationEnabled;
    }
    
    public static boolean getFlashMsgNotificationSettings() {
        final String flashMsgNotification = UpdatesParamUtil.getUpdParameter("FLASH_NEWS_NOTIFICATION_ENABLED");
        Boolean isFlashUpdatesNotificationEnabled = Boolean.TRUE;
        if (flashMsgNotification != null) {
            isFlashUpdatesNotificationEnabled = Boolean.valueOf(flashMsgNotification);
        }
        NotifyUpdatesUtil.logger.log(Level.INFO, "Is Flash Updates Notification Enabled ", isFlashUpdatesNotificationEnabled);
        return isFlashUpdatesNotificationEnabled;
    }
    
    public static boolean getUpdatesNotificationAdminOnlySettings() {
        final String updateNotificationForAdminOnly = UpdatesParamUtil.getUpdParameter("ENABLE_UPDATE_NOTIFICATION_ONLY_FOR_ADMIN");
        final Boolean isUpdateNotificationEnabledForAdminOnly = (updateNotificationForAdminOnly == null) ? Boolean.FALSE : Boolean.valueOf(updateNotificationForAdminOnly);
        NotifyUpdatesUtil.logger.log(Level.INFO, "Is Update Notification Enabled For Admin Only :  ", isUpdateNotificationEnabledForAdminOnly);
        return isUpdateNotificationEnabledForAdminOnly;
    }
    
    public void updateFlashMsgNotificationSettings(final boolean enableFlashMsgNotification) {
        if (enableFlashMsgNotification) {
            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_NOTIFICATION_ENABLED", "true");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateFlashMsgNotificationSettings: FlashMessgaeNotification is Enabled");
        }
        else {
            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_NOTIFICATION_ENABLED", "false");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateFlashMsgNotificationSettings: FlashMessgaeNotification is Disabled");
        }
    }
    
    public void updateUpdatesMsgNotificationSettings(final boolean isEnabledUpdateNotificationAdminOnly) {
        if (isEnabledUpdateNotificationAdminOnly) {
            UpdatesParamUtil.updateUpdParams("ENABLE_UPDATE_NOTIFICATION_ONLY_FOR_ADMIN", "true");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateUpdatesMsgNotificationSettings: UpdatesMessageNotificationAdminOnly option is Enabled");
        }
        else {
            UpdatesParamUtil.updateUpdParams("ENABLE_UPDATE_NOTIFICATION_ONLY_FOR_ADMIN", "false");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateUpdatesMsgNotificationSettings: UpdatesMessageNotificationAdminOnly option is Disabled");
        }
    }
    
    public void updateProductUpdatesNotificationSettings(final boolean enableProductUpdatesNotification) {
        if (enableProductUpdatesNotification) {
            UpdatesParamUtil.updateUpdParams("PRODUCT_UPDATE_NOTIFICATION_ENABLED", "true");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateProductUpdatesNotificationSettings: ProductUpdatesNotification is Enabled");
        }
        else {
            UpdatesParamUtil.updateUpdParams("PRODUCT_UPDATE_NOTIFICATION_ENABLED", "false");
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateProductUpdatesNotificationSettings: ProductUpdatesNotification is Disabled");
        }
    }
    
    private void updateCheckForUpdateUserClicks(final boolean incrementDoItlaterClickCount) {
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            long no_of_clicks = 0L;
            long no_of_do_it_later_clicks = 0L;
            final Criteria criteria = new Criteria(Column.getColumn("CheckforUpdatesUserClicks", "USER_ID"), (Object)userID, 0, false);
            final DataObject updParamsDO = DataAccess.get("CheckforUpdatesUserClicks", criteria);
            if (updParamsDO.isEmpty()) {
                final Row row = new Row("CheckforUpdatesUserClicks");
                row.set("USER_ID", (Object)userID);
                row.set("NO_OF_CLICKS", (Object)(++no_of_clicks));
                if (incrementDoItlaterClickCount) {
                    row.set("NO_OF_DO_IT_LATER_CLICKS", (Object)(++no_of_do_it_later_clicks));
                }
                updParamsDO.addRow(row);
            }
            else {
                final Row row = updParamsDO.getRow("CheckforUpdatesUserClicks", criteria);
                no_of_clicks = (long)row.get("NO_OF_CLICKS");
                ++no_of_clicks;
                row.set("NO_OF_CLICKS", (Object)no_of_clicks);
                if (incrementDoItlaterClickCount) {
                    no_of_do_it_later_clicks = (long)row.get("NO_OF_DO_IT_LATER_CLICKS");
                    ++no_of_do_it_later_clicks;
                    row.set("NO_OF_DO_IT_LATER_CLICKS", (Object)no_of_do_it_later_clicks);
                }
                updParamsDO.updateRow(row);
            }
            DataAccess.update(updParamsDO);
            NotifyUpdatesUtil.logger.log(Level.INFO, "updateCheckForUpdateUserClicks: UserClicks and Status are updated in DB");
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "exception in updateCheckForUpdateUserClicks ", e);
        }
    }
    
    public void enableRemindMeLater(final long notificationFrequency) {
        try {
            final String showUpdate = UpdatesParamUtil.getUpdParameter("showVersionMsg");
            this.updateCheckForUpdateUserClicks(true);
            DataObject updParamsDO = null;
            if (showUpdate != null && !showUpdate.equalsIgnoreCase("false")) {
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Criteria criteria = new Criteria(Column.getColumn("NotifiedUserForUpdates", "LOGIN_ID"), (Object)loginID, 0, false);
                updParamsDO = DataAccess.get("NotifiedUserForUpdates", criteria);
                final long lastShownTime = System.currentTimeMillis();
                if (updParamsDO.isEmpty()) {
                    final Row row = new Row("NotifiedUserForUpdates");
                    row.set("LOGIN_ID", (Object)loginID);
                    row.set("DO_IT_LATER", (Object)Boolean.TRUE);
                    row.set("NOTIFICATION_LAST_SHOWN_TIME", (Object)lastShownTime);
                    row.set("NOTIFICATION_FREQUENCY", (Object)notificationFrequency);
                    updParamsDO.addRow(row);
                }
                else {
                    final Row row = updParamsDO.getRow("NotifiedUserForUpdates", criteria);
                    row.set("LOGIN_ID", (Object)loginID);
                    row.set("NOTIFICATION_LAST_SHOWN_TIME", (Object)lastShownTime);
                    row.set("DO_IT_LATER", (Object)Boolean.TRUE);
                    row.set("NOTIFICATION_FREQUENCY", (Object)notificationFrequency);
                    updParamsDO.updateRow(row);
                }
                DataAccess.update(updParamsDO);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "exception in enableRemindMeLater ", e);
        }
    }
    
    public void enableRemindMeLaterForEOS(final long notificationFrequency) {
        try {
            final String stopEOSAlert = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            DataObject updParamsDO = null;
            if (stopEOSAlert == null || stopEOSAlert.equalsIgnoreCase("false")) {
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Criteria criteria = new Criteria(Column.getColumn("NotifiedUserForEOS", "LOGIN_ID"), (Object)loginID, 0, false);
                updParamsDO = DataAccess.get("NotifiedUserForEOS", criteria);
                final long lastShownTime = System.currentTimeMillis();
                if (updParamsDO.isEmpty()) {
                    final Row row = new Row("NotifiedUserForEOS");
                    row.set("LOGIN_ID", (Object)loginID);
                    row.set("REMIND_ME_LATER", (Object)Boolean.TRUE);
                    row.set("NOTIFICATION_LAST_SHOWN_TIME", (Object)lastShownTime);
                    row.set("NOTIFICATION_FREQUENCY", (Object)notificationFrequency);
                    updParamsDO.addRow(row);
                }
                else {
                    final Row row = updParamsDO.getRow("NotifiedUserForEOS", criteria);
                    row.set("LOGIN_ID", (Object)loginID);
                    row.set("NOTIFICATION_LAST_SHOWN_TIME", (Object)lastShownTime);
                    row.set("REMIND_ME_LATER", (Object)Boolean.TRUE);
                    row.set("NOTIFICATION_FREQUENCY", (Object)notificationFrequency);
                    updParamsDO.updateRow(row);
                }
                DataAccess.update(updParamsDO);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "exception in enableRemindMeLaterForEOS ", e);
        }
    }
    
    public void removeNotificationIcon() {
        try {
            final String showUpdate = UpdatesParamUtil.getUpdParameter("showVersionMsg");
            this.updateCheckForUpdateUserClicks(false);
            DataObject updParamsDO = null;
            if (showUpdate != null && !showUpdate.equalsIgnoreCase("false")) {
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Criteria criteria = new Criteria(Column.getColumn("NotifiedUserForUpdates", "LOGIN_ID"), (Object)loginID, 0, false);
                updParamsDO = DataAccess.get("NotifiedUserForUpdates", criteria);
                if (updParamsDO.isEmpty()) {
                    final Row row = new Row("NotifiedUserForUpdates");
                    row.set("LOGIN_ID", (Object)loginID);
                    updParamsDO.addRow(row);
                }
                else {
                    final long lastShownTime = System.currentTimeMillis();
                    final Row row = updParamsDO.getRow("NotifiedUserForUpdates", criteria);
                    row.set("LOGIN_ID", (Object)loginID);
                    row.set("NOTIFICATION_LAST_SHOWN_TIME", (Object)lastShownTime);
                    row.set("DO_IT_LATER", (Object)Boolean.FALSE);
                    updParamsDO.updateRow(row);
                }
                DataAccess.update(updParamsDO);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "exception in removeNotificationIcon ", e);
        }
    }
    
    public static boolean hasCustomPPM() {
        Boolean hasCustomPPM = Boolean.FALSE;
        try {
            final String uniqueIDFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "JarTracker" + File.separator + "fixes_id.properties";
            final Properties unique_props = new Properties();
            final File uniqueIDFile = new File(uniqueIDFileName);
            if (uniqueIDFile.exists()) {
                unique_props.load(new FileReader(uniqueIDFile));
                final String issueIdsString = unique_props.getProperty("unique_id");
                if (issueIdsString != null && !issueIdsString.isEmpty()) {
                    hasCustomPPM = Boolean.TRUE;
                }
            }
            NotifyUpdatesUtil.logger.log(Level.INFO, "hasCustomPPM ", hasCustomPPM);
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  hasCustomPPM ", e);
        }
        return hasCustomPPM;
    }
    
    public JSONObject isBuildNotificationAvailable() {
        final JSONObject responseJSON = new JSONObject();
        Boolean hasCustomPPM = Boolean.FALSE;
        try {
            final String stopEosMsg = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
            if (((stopEosMsg == null || stopEosMsg.equalsIgnoreCase("false")) && this.getEOSState(6L) != 0) || (!isAdmin && getUpdatesNotificationAdminOnlySettings())) {
                responseJSON.put("show_build_notification", false);
                return responseJSON;
            }
            hasCustomPPM = hasCustomPPM();
            if (!hasCustomPPM) {
                final String showUpdate = UpdatesParamUtil.getUpdParameter("showVersionMsg");
                if (showUpdate != null && showUpdate.equalsIgnoreCase("true")) {
                    final DataObject updParamsDO = DataAccess.get("NotifiedUserForUpdates", (Criteria)null);
                    final Criteria criteria = new Criteria(Column.getColumn("NotifiedUserForUpdates", "LOGIN_ID"), (Object)loginID, 0, false);
                    final Row updParamRow = (updParamsDO == null) ? null : updParamsDO.getRow("NotifiedUserForUpdates", criteria);
                    if (updParamRow == null) {
                        responseJSON.put("show_build_notification", true);
                    }
                    else {
                        final Boolean isDoItLaterEnabled = Boolean.valueOf(updParamRow.get("DO_IT_LATER").toString());
                        final long lastMsgShownTime = Long.valueOf(updParamRow.get("NOTIFICATION_LAST_SHOWN_TIME").toString());
                        final long notificationFrequency = Long.valueOf(updParamRow.get("NOTIFICATION_FREQUENCY").toString());
                        if (isDoItLaterEnabled) {
                            if (lastMsgShownTime == -1L || System.currentTimeMillis() - lastMsgShownTime >= notificationFrequency) {
                                responseJSON.put("show_build_notification", true);
                            }
                            else {
                                responseJSON.put("show_build_notification", false);
                            }
                        }
                        else {
                            responseJSON.put("show_build_notification", false);
                        }
                    }
                }
                else {
                    responseJSON.put("show_build_notification", false);
                }
            }
            else {
                responseJSON.put("show_build_notification", false);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  isBuildNotificationAvailable ", e);
        }
        return responseJSON;
    }
    
    public int getEOSState(final Long lastMonths) {
        final String eolDateStr = UpdatesParamUtil.getUpdParameter("EOL_DATE");
        if (eolDateStr != null) {
            final LocalDateTime eolTime = Instant.ofEpochMilli(Long.parseLong(eolDateStr)).atZone(ZoneId.systemDefault()).toLocalDateTime();
            final LocalDateTime currentTime = Instant.ofEpochMilli(SyMUtil.getCurrentTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (currentTime.compareTo((ChronoLocalDateTime<?>)eolTime) > 0) {
                return 2;
            }
            if (currentTime.compareTo((ChronoLocalDateTime<?>)eolTime.minusMonths(lastMonths)) > 0) {
                return 1;
            }
        }
        return 0;
    }
    
    private boolean showEosTopBanner(final boolean isAdmin) {
        boolean showEosTBAlert = true;
        if (this.getEOSState(1L) != 0) {
            if (!isAdmin && getUpdatesNotificationAdminOnlySettings()) {
                showEosTBAlert = false;
            }
        }
        else {
            showEosTBAlert = false;
        }
        return showEosTBAlert;
    }
    
    private boolean isEOSPopupNotificationAvailable(final Long loginID, final boolean isAdmin) throws Exception {
        boolean showEosPopup = false;
        if (!isAdmin && getUpdatesNotificationAdminOnlySettings()) {
            return false;
        }
        final DataObject eosNotifiedUserDO = DataAccess.get("NotifiedUserForEOS", (Criteria)null);
        final Criteria criteria = new Criteria(Column.getColumn("NotifiedUserForEOS", "LOGIN_ID"), (Object)loginID, 0, false);
        final Row eosNotifiedUserRow = eosNotifiedUserDO.isEmpty() ? null : eosNotifiedUserDO.getRow("NotifiedUserForEOS", criteria);
        if (eosNotifiedUserRow == null) {
            showEosPopup = true;
        }
        else {
            final Boolean isRemindMeLaterEnabled = Boolean.valueOf(eosNotifiedUserRow.get("REMIND_ME_LATER").toString());
            final long lastMsgShownTime = Long.parseLong(eosNotifiedUserRow.get("NOTIFICATION_LAST_SHOWN_TIME").toString());
            final long notificationFrequency = Long.parseLong(eosNotifiedUserRow.get("NOTIFICATION_FREQUENCY").toString());
            if (isRemindMeLaterEnabled && notificationFrequency != -1L && (lastMsgShownTime == -1L || System.currentTimeMillis() - lastMsgShownTime >= notificationFrequency)) {
                showEosPopup = true;
            }
        }
        return showEosPopup;
    }
    
    public Map<String, Object> getEOSPopupNotificationDetail() {
        final Map<String, Object> response = new HashMap<String, Object>();
        try {
            final String stopEOSMsg = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            if (stopEOSMsg == null || stopEOSMsg.equalsIgnoreCase("false")) {
                final int eolState = this.getEOSState(6L);
                response.put("eosState", eolState);
                if (eolState != 0) {
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
                    boolean showEosPopup = false;
                    showEosPopup = this.isEOSPopupNotificationAvailable(loginID, isAdmin);
                    response.put("showEosPopupAlert", showEosPopup);
                    if (showEosPopup) {
                        final String eosPopupTitleHtml = UpdatesParamUtil.getUpdParameter("EOS_POPUP_TITLE");
                        String eosPopupMsgLabel = "EOS_POPUP_MSG_ADMIN";
                        if (!isAdmin) {
                            eosPopupMsgLabel = "EOS_POPUP_MSG_TECH";
                        }
                        final String eosPopupMsgHtml = UpdatesParamUtil.getUpdParameter(eosPopupMsgLabel);
                        response.put("eosPopupTitleHtml", eosPopupTitleHtml);
                        response.put("eosPopupMsgHtml", eosPopupMsgHtml);
                        response.put("servicePackUrl", UpdatesParamUtil.getUpdParameter("UPDATE_DOWNLOAD_URL"));
                    }
                }
            }
            else {
                response.put("eosState", 0);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  getEOSNotificationDetail ", e);
            response.put("eosState", 0);
        }
        return response;
    }
    
    public Map<String, Object> getEOSVHDetail() {
        final Map<String, Object> response = new HashMap<String, Object>();
        try {
            final String stopEOSMsg = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            if (stopEOSMsg == null || stopEOSMsg.equalsIgnoreCase("false")) {
                final int eolState = this.getEOSState(6L);
                response.put("eosState", eolState);
                if (eolState != 0) {
                    boolean showEosVHAlert = true;
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
                    if (!isAdmin && getUpdatesNotificationAdminOnlySettings()) {
                        showEosVHAlert = false;
                    }
                    response.put("showEosVHAlert", showEosVHAlert);
                    if (showEosVHAlert) {
                        String eosVHMsgLabel = "EOS_VH_MSG_ADMIN";
                        if (!isAdmin) {
                            eosVHMsgLabel = "EOS_VH_MSG_TECH";
                        }
                        final String eosVHMsgHtml = UpdatesParamUtil.getUpdParameter(eosVHMsgLabel);
                        response.put("eosVHMsgHtml", eosVHMsgHtml);
                    }
                }
            }
            else {
                response.put("eosState", 0);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  getEOSVHDetail ", e);
            response.put("eosState", 0);
        }
        return response;
    }
    
    public Map<String, Object> getEOSTBDetail() {
        final Map<String, Object> response = new HashMap<String, Object>();
        try {
            final String stopEOSMsg = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            if (stopEOSMsg == null || stopEOSMsg.equalsIgnoreCase("false")) {
                final int eolState = this.getEOSState(6L);
                response.put("eosState", eolState);
                if (eolState != 0) {
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
                    final boolean showTopBanner = this.showEosTopBanner(isAdmin);
                    response.put("showEosTBAlert", showTopBanner);
                    if (showTopBanner) {
                        String eosTBMsgLabel = "EOS_TB_MSG_ADMIN";
                        if (!isAdmin) {
                            eosTBMsgLabel = "EOS_TB_MSG_TECH";
                        }
                        final String eosTBMsgHtml = UpdatesParamUtil.getUpdParameter(eosTBMsgLabel);
                        response.put("eosTBMsgHtml", eosTBMsgHtml);
                    }
                }
            }
            else {
                response.put("eosState", 0);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  getEOSTBDetail ", e);
            response.put("eosState", 0);
        }
        return response;
    }
    
    public Map<String, Object> getEosSupportDetail() {
        final Map<String, Object> response = new HashMap<String, Object>();
        try {
            final String stopEOSMsg = UpdatesParamUtil.getUpdParameter("STOP_EOS_MESSAGE");
            if (stopEOSMsg == null || stopEOSMsg.equalsIgnoreCase("false")) {
                final int eolState = this.getEOSState(6L);
                response.put("eosState", eolState);
                if (eolState != 0) {
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
                    boolean showEosSupportAlert = true;
                    if (!isAdmin && getUpdatesNotificationAdminOnlySettings()) {
                        showEosSupportAlert = false;
                    }
                    response.put("showEosSupportAlert", showEosSupportAlert);
                    if (showEosSupportAlert) {
                        final String eosSupportTitleHtml = UpdatesParamUtil.getUpdParameter("EOS_SUPPORT_TITLE");
                        final String eosSupportMsgHtml = UpdatesParamUtil.getUpdParameter("EOS_SUPPORT_MSG");
                        response.put("eosSupportTitleHtml", eosSupportTitleHtml);
                        response.put("eosSupportMsgHtml", eosSupportMsgHtml);
                    }
                }
            }
            else {
                response.put("eosState", 0);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception in  getEosSupportDetail ", e);
            response.put("eosState", 0);
        }
        return response;
    }
    
    public static JSONObject checkforVersionUpdate() {
        NotifyUpdatesUtil.logger.log(Level.INFO, "checkforVersionUpdate : Going to check for updates");
        boolean hasCustomPPM = Boolean.FALSE;
        hasCustomPPM = hasCustomPPM();
        final JSONObject versionUpdatesJson = new JSONObject();
        try {
            final String tracking_code = ProductUrlLoader.getInstance().getValue("tracking-checkforupdates");
            String updates_url = new String();
            String update_version = new String();
            String update_type = new String();
            String showUpdate = new String();
            String checkForUpdatesMsg = new String();
            String updatesMsgPriority = new String();
            Long lastModified = 0L;
            if (UpdatesParamUtil.getUpdParameter("updatesLastModifiedAt") != null) {
                lastModified = Long.parseLong(UpdatesParamUtil.getUpdParameter("updatesLastModifiedAt"));
                NotifyUpdatesUtil.logger.log(Level.INFO, "checkforVersionUpdate : Last Modified time of Flash JSON Download : ", lastModified);
            }
            long threshold = 120L;
            if (System.getProperty("downloadThreshold") != null) {
                threshold = Integer.parseInt(System.getProperty("downloadThreshold"));
            }
            if (!hasCustomPPM) {
                final String proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
                if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                    final String stopVersionMsg = UpdatesParamUtil.getUpdParameter("STOP_VERSION_MESSAGE");
                    if (lastModified != 0L || stopVersionMsg != null) {
                        if (stopVersionMsg.equalsIgnoreCase("true")) {
                            versionUpdatesJson.put("stop_version_msg", (Object)Boolean.TRUE);
                            versionUpdatesJson.put("msg_status", (Object)"error");
                            versionUpdatesJson.put("error_code", 4);
                            versionUpdatesJson.put("error_code_desc", (Object)"NO_LATEST_BUILD_AVAILABLE");
                        }
                        else {
                            versionUpdatesJson.put("stop_version_msg", (Object)Boolean.FALSE);
                            showUpdate = UpdatesParamUtil.getUpdParameter("showVersionMsg");
                            updates_url = UpdatesParamUtil.getUpdParameter("UPDATE_DOWNLOAD_URL");
                            update_version = UpdatesParamUtil.getUpdParameter("LATEST_BUILD_VERSION");
                            update_type = UpdatesParamUtil.getUpdParameter("UPDATE_VERSION_TYPE");
                            checkForUpdatesMsg = UpdatesParamUtil.getUpdParameter("PRODUCT_UPDATE_MSG");
                            updatesMsgPriority = UpdatesParamUtil.getUpdParameter("UPDATE_MESSAGE_PRIORITY");
                            if (updates_url != null && showUpdate != null && update_type != null) {
                                versionUpdatesJson.put("In_DB", (Object)"true");
                                if (showUpdate.equalsIgnoreCase("true")) {
                                    final Date currentDate = new Date();
                                    final Date fileLastModifiedDate = new Date(lastModified);
                                    final long diffInMilliSeconds = (currentDate.getTime() - fileLastModifiedDate.getTime()) / 1000L;
                                    final long diffInMinutes = diffInMilliSeconds / 60L;
                                    if (diffInMinutes >= threshold) {
                                        versionUpdatesJson.put("error_code", 1);
                                        versionUpdatesJson.put("URL", (Object)(updates_url + "&" + tracking_code));
                                        versionUpdatesJson.put("msg_status", (Object)"error");
                                        versionUpdatesJson.put("error_code_desc", (Object)"DOWNLOAD_FAILURE");
                                    }
                                    else {
                                        versionUpdatesJson.put("difference", (Object)update_type);
                                        if (update_version != null) {
                                            versionUpdatesJson.put("build_version", (Object)update_version);
                                        }
                                        versionUpdatesJson.put("URL", (Object)(updates_url + "&" + tracking_code));
                                        versionUpdatesJson.put("show_message", (Object)Boolean.TRUE);
                                        versionUpdatesJson.put("updates_msg", (Object)checkForUpdatesMsg);
                                        versionUpdatesJson.put("updates_msg_priority", (Object)updatesMsgPriority);
                                        versionUpdatesJson.put("msg_status", (Object)"Updates Available");
                                    }
                                }
                                else {
                                    versionUpdatesJson.put("show_message", (Object)Boolean.FALSE);
                                    versionUpdatesJson.put("msg_status", (Object)"error");
                                    versionUpdatesJson.put("error_code", 4);
                                    versionUpdatesJson.put("error_code_desc", (Object)"NO_LATEST_BUILD_AVAILABLE");
                                }
                            }
                            else {
                                versionUpdatesJson.put("In_DB", (Object)"false");
                                final Date currentDate = new Date();
                                final Date fileLastModifiedDate = new Date(lastModified);
                                final long diffInMilliSeconds = (currentDate.getTime() - fileLastModifiedDate.getTime()) / 1000L;
                                final long diffInMinutes = diffInMilliSeconds / 60L;
                                if (diffInMinutes >= threshold) {
                                    versionUpdatesJson.put("error_code", 10008);
                                    versionUpdatesJson.put("error_code_desc", (Object)"ERROR_DOWNLOAD_FAILED");
                                    versionUpdatesJson.put("msg_status", (Object)"error");
                                }
                                else {
                                    versionUpdatesJson.put("difference", (Object)"None");
                                    versionUpdatesJson.put("build_version", (Object)"Nil");
                                    versionUpdatesJson.put("msg_status", (Object)"Up-to-Date");
                                }
                            }
                        }
                    }
                    else {
                        versionUpdatesJson.put("error_code", 2);
                        versionUpdatesJson.put("error_code_desc", (Object)"NO_INTERNET_CONNECTION");
                        versionUpdatesJson.put("msg_status", (Object)"error");
                    }
                }
                else {
                    NotifyUpdatesUtil.logger.log(Level.INFO, "Proxy is Not Defined, Hence couldn't download Updates JSON");
                    versionUpdatesJson.put("error_code", 0);
                    versionUpdatesJson.put("error_code_desc", (Object)"PROXY_NOT_CONFIGURED");
                    versionUpdatesJson.put("msg_status", (Object)"error");
                }
            }
            else {
                versionUpdatesJson.put("has_custom_ppm", (Object)Boolean.TRUE);
                versionUpdatesJson.put("msg_status", (Object)"error");
                versionUpdatesJson.put("error_code", 3);
                versionUpdatesJson.put("error_code_desc", (Object)"HAS_CUSTOM_PPM");
            }
            NotifyUpdatesUtil.logger.log(Level.INFO, "checkforVersionUpdate : UPdates Details JSON  " + versionUpdatesJson);
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception occured while checking for updates: ", e);
        }
        return versionUpdatesJson;
    }
    
    static {
        NotifyUpdatesUtil.logger = Logger.getLogger(NotifyUpdatesUtil.class.getName());
        NotifyUpdatesUtil.notifyUtil = null;
    }
}
