package com.me.mdm.onpremise.api.silentupdate;

import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.APIUtil;
import java.util.Date;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateHelper;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.concurrent.TimeUnit;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SilentUpdateFacade
{
    public Logger logger;
    public static final String SU_APPROVE_AND_WITHOUT_RESTART_QPPMS = "SUApproveWithoutRestartQPPMs";
    public static final String SU_APPROVE_AND_WITHOUT_RESTART_QPPMS_TOTLCUNT = "SUApproveWithoutRestartQPPMsTotlCunt";
    public static final String SU_APPROVE_AND_RESTART_QPPMS = "SUApproveRestartQPPMs";
    public static final String SU_APPROVE_AND_RESTART_QPPMS_TOTLCUNT = "SUApproveRestartQPPMsTotlCunt";
    public static final String SU_APPROVE_AND_REMINDME_LATTER_QPPMS = "SUApproveRemindMeLatterQPPMs";
    public static final String SU_APPROVE_AND_REMINDME_LATTER_QPPMS_TOTLCUNT = "SUApproveRemindMeLatterQPPMsTotlCunt";
    public static final String SU_RESTART_QPPMS = "SURestartQPPMs";
    public static final String SU_RESTART_QPPMS_TOTLCUNT = "SURestartQPPMsTotlcunt";
    
    public SilentUpdateFacade() {
        this.logger = Logger.getLogger("SilentUpdate");
    }
    
    public JSONObject getSilentUpdateAlertMsgDetails() {
        try {
            final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
            final JSONObject outputJson = new JSONObject();
            outputJson.put("is_alertmsg_required", false);
            JSONObject taskDetail = null;
            final org.json.simple.JSONObject alertDetailJSON = silentUpdate.getAlertMsgRequiredQPPMDetail();
            if (alertDetailJSON != null) {
                taskDetail = JSONUtil.getInstance().convertSimpleJSONtoJSON(alertDetailJSON);
            }
            if (taskDetail != null) {
                outputJson.put("alertmsg_content", (Object)taskDetail.get("ALERT_MSG_CONTENT").toString());
                final boolean hideRemindmeLater = Boolean.parseBoolean(String.valueOf(taskDetail.get("HIDE_REMINDME_LATER")));
                if (hideRemindmeLater) {
                    outputJson.put("is_reminderme_applicable", false);
                }
                else {
                    outputJson.put("is_reminderme_applicable", true);
                    outputJson.put("remindme_frequency", TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(taskDetail.get("ALERT_MSG_FREQUENCY").toString())));
                }
                outputJson.put("is_dismiss_applicable", Boolean.parseBoolean(String.valueOf(taskDetail.get("SHOW_DISMISS"))));
                outputJson.put("is_ignorethisfix_applicable", Boolean.parseBoolean(String.valueOf(taskDetail.get("SHOW_IGNORE_THIS_FIX"))));
                outputJson.put("qppm_uniqueid", (Object)taskDetail.get("QPPM_ID").toString());
                outputJson.put("task_id", (Object)taskDetail.get("TASK_ID").toString());
                outputJson.put("is_fixapproved", Boolean.parseBoolean(taskDetail.get("IS_FIX_APPROVED").toString()));
                final int qppmType = Integer.parseInt(taskDetail.get("QPPM_TYPE").toString());
                outputJson.put("is_restartrequired", qppmType != 0);
                outputJson.put("isMSP", CustomerInfoUtil.getInstance().isMSP());
                outputJson.put("is_alertmsg_required", true);
                this.updateIsAlertMsgShownTrack(taskDetail.get("QPPM_ID").toString());
                outputJson.put("is_silentupdate_message", true);
                return outputJson;
            }
            return outputJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting isAutoApproveEnabled details in facade...", ex);
            throw new APIHTTPException("SILENTUPDATESETTIGNS001", new Object[0]);
        }
    }
    
    private void updateIsAlertMsgShownTrack(final String qppmUniqueId) {
        final String trackKey = "AlertMsgShownTrack." + qppmUniqueId;
        try {
            final String value = SilentUpdateHelper.getInstance().customerSpecificProps(trackKey);
            if (value == null) {
                SilentUpdateHelper.getInstance().updateCustomerConfigProps(trackKey, (Object)new Date().toString());
                this.logger.log(Level.INFO, "updateIsAlertMsgShownTrack : Is alert msg shown track has been added : {0}", qppmUniqueId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getting updateIsAlertMsgShownTrack details in facade...", e);
        }
    }
    
    public JSONObject setSilentUpdateSettings(final JSONObject jsonObject) {
        final JSONObject resultData = new JSONObject();
        try {
            final JSONObject silentSettingsDetailsFromReq = jsonObject.getJSONObject("msg_body");
            final JSONObject serverSettingsDetails = new JSONObject();
            final String operationType = silentSettingsDetailsFromReq.get("operation_type").toString();
            final String taskID = silentSettingsDetailsFromReq.get("task_id").toString();
            boolean isAutoApproveChecked = false;
            final APIUtil apiUtil = new APIUtil();
            boolean isValidTaskID = false;
            if (operationType != null && !operationType.equals("restartService") && taskID != null) {
                final Criteria taskIDCriteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskID, 0);
                final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetailsExtn"));
                selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "*"));
                if (taskIDCriteria != null) {
                    selectQuery.setCriteria(taskIDCriteria);
                }
                final DataObject data = DataAccess.get((SelectQuery)selectQuery);
                if (data != null && !data.isEmpty()) {
                    final Iterator rows = data.getRows("SilentUpdateDetailsExtn");
                    while (rows.hasNext()) {
                        final Row row = rows.next();
                        isValidTaskID = true;
                    }
                }
                if (!isValidTaskID) {
                    resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                    resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.invalid_task", new Object[0]));
                    return resultData;
                }
            }
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (operationType != null) {
                if (operationType.equals("approveWithoutRestartQPPM")) {
                    DCEventLogUtil.getInstance().addEvent(10009, userName, (HashMap)null, "mdmp.qpm.approveWithoutRestartQPPM", (Object)null, true);
                    METrackerUtil.incrementMETrackParams("SUApproveWithoutRestartQPPMs." + taskID);
                    isAutoApproveChecked = Boolean.valueOf(silentSettingsDetailsFromReq.get("is_autoapprove_checked").toString());
                    if (this.approveFix(taskID, isAutoApproveChecked)) {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                    }
                    else {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                        resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.fix_approval_error", new Object[0]));
                    }
                }
                else if (operationType.equals("approveAndRestartService")) {
                    DCEventLogUtil.getInstance().addEvent(10013, userName, (HashMap)null, "mdmp.qpm.approveAndRestartService", (Object)null, true);
                    METrackerUtil.incrementMETrackParams("SUApproveRestartQPPMs." + taskID);
                    isAutoApproveChecked = Boolean.valueOf(silentSettingsDetailsFromReq.get("is_autoapprove_checked").toString());
                    if (this.approveFix(taskID, isAutoApproveChecked)) {
                        if (this.restartService()) {
                            resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                        }
                        else {
                            resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                            resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.permission_check", new Object[0]));
                        }
                    }
                    else {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                        resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.fix_approval_error", new Object[0]));
                    }
                }
                else if (operationType.equals("approveAndRemindMeLatter")) {
                    DCEventLogUtil.getInstance().addEvent(10012, userName, (HashMap)null, "mdmp.qpm.approveAndRemindMeLatter", (Object)null, true);
                    METrackerUtil.incrementMETrackParams("SUApproveRemindMeLatterQPPMs." + taskID);
                    isAutoApproveChecked = Boolean.valueOf(silentSettingsDetailsFromReq.get("is_autoapprove_checked").toString());
                    if (this.approveFix(taskID, isAutoApproveChecked)) {
                        final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
                        silentUpdate.remindMeLater(taskID);
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                    }
                    else {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                        resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.fix_approval_error", new Object[0]));
                    }
                }
                else if (operationType.equals("ignoreThisFix")) {
                    DCEventLogUtil.getInstance().addEvent(10011, userName, (HashMap)null, "mdmp.qpm.ignoreThisFix", (Object)null, true);
                    final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
                    silentUpdate.ignoreThisFix(taskID);
                    resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                }
                else if (operationType.equals("restartService")) {
                    DCEventLogUtil.getInstance().addEvent(10014, userName, (HashMap)null, "mdmp.qpm.restartService", (Object)null, true);
                    METrackerUtil.incrementMETrackParams("SURestartQPPMs");
                    if (this.restartService()) {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                    }
                    else {
                        resultData.put("Result", (Object)I18N.getMsg("dc.common.status.failed", new Object[0]));
                        resultData.put("ErrorMsg", (Object)I18N.getMsg("mdmp.silentupdate.permission_check", new Object[0]));
                    }
                }
                else if (operationType.equals("remindMeLater")) {
                    DCEventLogUtil.getInstance().addEvent(10015, userName, (HashMap)null, "mdmp.qpm.remindMeLater", (Object)null, true);
                    final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
                    silentUpdate.remindMeLater(taskID);
                    resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                }
                else if (operationType.equals("dismiss")) {
                    DCEventLogUtil.getInstance().addEvent(10010, userName, (HashMap)null, "mdmp.qpm.dismiss", (Object)null, true);
                    final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
                    silentUpdate.dismiss(taskID);
                    resultData.put("Result", (Object)I18N.getMsg("dc.common.SUCCESS", new Object[0]));
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in update ServerSettings", ex);
            throw new APIHTTPException("SILENTUPDATESETTIGNS002", new Object[0]);
        }
        return resultData;
    }
    
    public boolean approveFix(final String taskId, final boolean isAutoApproveEnabled) {
        try {
            final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
            if (isAutoApproveEnabled) {
                silentUpdate.enableAutoApprove();
                this.logger.log(Level.INFO, "approveFix : Auto approve has been enabled !!!");
            }
            else {
                this.logger.log(Level.INFO, "approveFix: Auto approve has been not enabled !!!");
                SilentUpdateHelper.getInstance().updateDBValue("SilentUpdateDetailsExtn", "IS_FIX_APPROVED", (Object)true, new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskId, 0));
                silentUpdate.processQPPMs();
            }
            return true;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "approveFix : Exception occurred : ", e);
            return false;
        }
    }
    
    public boolean restartService() throws Exception {
        try {
            final APIUtil apiUtil = new APIUtil();
            this.logger.log(Level.INFO, "restartService : SilentUpdateFacade.restartService() action method called...!");
            if (!apiUtil.checkRolesForCurrentUser(new String[] { "Common_Write" })) {
                return false;
            }
            if (new SilentUpdateHandler().isRestartRequired()) {
                this.logger.log(Level.INFO, "restartService : Going to restart the service.");
                SyMUtil.triggerServerRestart();
                return true;
            }
            this.logger.log(Level.INFO, "restartService : Restart not required for silent update, So skipped this request.");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "restartService : Exception occurred while restarting service : ", e);
        }
        return false;
    }
}
