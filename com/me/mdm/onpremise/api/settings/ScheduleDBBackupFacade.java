package com.me.mdm.onpremise.api.settings;

import com.me.devicemanagement.onpremise.webclient.authentication.ConfirmPasswordAction;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Map;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ScheduleDBBackupFacade
{
    public Logger logger;
    
    public ScheduleDBBackupFacade() {
        this.logger = Logger.getLogger("ScheduleDBBackup");
    }
    
    public JSONObject getScheduleDBBackupDetails() {
        try {
            final String schedulerName = "ScheduledDBBackupTaskScheduler";
            JSONObject resultJson = new JSONObject();
            resultJson = ScheduleDBBackupUtil.getScheduleDBBackupDetails(resultJson, schedulerName);
            resultJson = ScheduleDBBackupUtil.getDBBackupInfo(resultJson);
            resultJson = ScheduleDBBackupUtil.getEmailAlertConfigInfo(resultJson);
            final boolean ismailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
            final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            resultJson.put("ismailServer_configured", ismailServerConfigured);
            resultJson.put("login_name", (Object)loginName);
            resultJson.put("time_zone", (Object)APIUtil.getJSONObjectFromMap((Map)SyMUtil.getAvailableTimeZone()));
            resultJson.remove("scheduleType");
            resultJson.remove("dailyIntervalType");
            resultJson.remove("dailyDate");
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting scheduledbbackup details...", ex);
            throw new APIHTTPException("SCH001", new Object[0]);
        }
    }
    
    public JSONObject saveScheduledValues(final JSONObject jsonObject) {
        try {
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final JSONObject scheduleDBDetails = new JSONObject();
            scheduleDBDetails.put("ENABLE_DBBACKUP_SCHEDULER", true);
            scheduleDBDetails.put("scheduleType", (Object)"Daily");
            scheduleDBDetails.put("dailyTime", (Object)body.optString("dailytime"));
            scheduleDBDetails.put("dailyIntervalType", (Object)"everyDay");
            scheduleDBDetails.put("noofbackup", body.optInt("noofbackup"));
            scheduleDBDetails.put("backupdir", (Object)body.optString("backupdir"));
            scheduleDBDetails.put("ENABLE_EMAIL_ALERT", body.optBoolean("enable_email_alert"));
            scheduleDBDetails.put("EMAIL_ID", (Object)body.optString("email_id"));
            scheduleDBDetails.put("BackupPasswordHint", (Object)body.optString("password_hint"));
            scheduleDBDetails.put("isPWDChanged", body.optBoolean("is_password_changed"));
            scheduleDBDetails.put("NewBackupPassword", (Object)body.optString("new_password"));
            scheduleDBDetails.put("ENABLE_PASSWORD_PROTECTION", body.optBoolean("enable_password_protection"));
            scheduleDBDetails.put("disablePwd", (Object)Boolean.toString(body.optBoolean("disable_password")));
            if (!body.optString("tasktimezone").isEmpty()) {
                scheduleDBDetails.put("timezone", (Object)body.optString("tasktimezone"));
            }
            else {
                scheduleDBDetails.put("timezone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            }
            final Date date = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            final String dateOfExec = sdf.format(date);
            scheduleDBDetails.put("dailyDate", (Object)dateOfExec);
            final String workflowName = "ScheduledDBBackupTask";
            final String workEngineId = "DesktopCentral";
            final String email = null;
            Long taskId = null;
            final String operationType = String.valueOf(5000);
            final String schedulerName = "ScheduledDBBackupTaskScheduler";
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            scheduleDBDetails.put("customerId", (Object)customerId);
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.ENABLE, schedulerName);
            taskId = ScheduleDBBackupUtil.saveScheduledValues(workEngineId, operationType, workflowName, email, scheduleDBDetails);
            this.logger.log(Level.FINE, "Task_Id for DBBackup : {0}", taskId);
            final String isSuccess = ScheduleDBBackupUtil.updateScheduledInfo(scheduleDBDetails);
            final JSONObject responseJSON = new JSONObject();
            if (isSuccess.equalsIgnoreCase("VALID_PATH")) {
                ScheduleDBBackupUtil.updateEmailAlertInfo(scheduleDBDetails);
                return responseJSON.put("success", true);
            }
            return responseJSON.put("error_description", (Object)I18N.getMsg("dc.admin.scheduleDBBackup.Loc_access_denied", new Object[0]));
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving scheduledbbackup", ex);
            throw new APIHTTPException("SCH002", new Object[0]);
        }
    }
    
    public JSONObject getscheduleddbpassword(final JSONObject jsonObject) {
        boolean isSuccess = false;
        final JSONObject responseJSON = new JSONObject();
        try {
            Boolean getBackupPassword = false;
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final String password = body.optString("password");
            getBackupPassword = body.optBoolean("get_backup_password");
            if (password != null && this.isValidAdminCheck(password)) {
                if (getBackupPassword) {
                    final DataObject data = ScheduleDBBackupUtil.getDbBackupInfoDO();
                    Row dcBackupInfoRow = data.getRow("DBBackupInfo");
                    if (dcBackupInfoRow == null) {
                        dcBackupInfoRow = new Row("DBBackupInfo");
                        data.addRow(dcBackupInfoRow);
                    }
                    final String currentBackupPassword = (String)dcBackupInfoRow.get("BACKUP_PASSWORD");
                    responseJSON.put("current_password", (Object)currentBackupPassword);
                }
                isSuccess = true;
            }
            return responseJSON.put("success", isSuccess);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving scheduledbbackup", ex);
            throw new APIHTTPException("SCH002", new Object[0]);
        }
    }
    
    private boolean isValidAdminCheck(final String password) throws Exception {
        final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        final String domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
        Boolean isValid = Boolean.FALSE;
        final ConfirmPasswordAction checker = new ConfirmPasswordAction();
        if (domainName != null && !domainName.equalsIgnoreCase("-")) {
            isValid = checker.validateADUser(loginName, domainName, password);
        }
        else {
            isValid = checker.validateDCUser(loginName, password);
        }
        return isValid;
    }
    
    public JSONObject checkBackupPath(final JSONObject jsonObject) {
        final boolean isSuccess = false;
        final JSONObject responseJSON = new JSONObject();
        final JSONObject resultToReturn = new JSONObject();
        try {
            final Boolean getBackupPassword = false;
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final String backupdir = body.optString("backupdir");
            final JSONObject result = ScheduleDBBackupUtil.checkBackupPath(backupdir);
            resultToReturn.put("responsecode", result.get("responseCode"));
            resultToReturn.put("responsetext", result.get("responseText"));
            return responseJSON.put("result", (Object)resultToReturn);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in Checking  scheduledbbackup Path", ex);
            throw new APIHTTPException("SCH003", new Object[0]);
        }
    }
}
