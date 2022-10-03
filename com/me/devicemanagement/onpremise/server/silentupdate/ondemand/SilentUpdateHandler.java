package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.Hashtable;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import org.json.simple.JSONValue;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONObject;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import org.json.simple.JSONArray;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class SilentUpdateHandler
{
    private static Logger logger;
    private static String sourceClass;
    
    public void syncTaskDetails() {
        try {
            boolean updateLastTasksDetailsFetchTimeFromCRS = false;
            String currentCRSFetchTime = null;
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Going to fetch value from crs!!!");
            final SilentUpdateController silentUpdateController = new SilentUpdateController();
            final JSONObject jsonObject = silentUpdateController.exportJsonFromCRS();
            if (!jsonObject.isEmpty()) {
                final String crsResponseKey = jsonObject.keySet().iterator().next().toString();
                final Object responseValueFromCrs = jsonObject.get((Object)crsResponseKey);
                if (responseValueFromCrs instanceof JSONArray) {
                    currentCRSFetchTime = crsResponseKey;
                    final JSONArray tasksJson = (JSONArray)responseValueFromCrs;
                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Current download count from crs : " + tasksJson.size());
                    updateLastTasksDetailsFetchTimeFromCRS = SilentUpdateHelper.getInstance().updateTasksTODB(tasksJson);
                }
                else if (responseValueFromCrs instanceof String) {
                    SyMLogger.warning(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "It seems failure occurred while fetch task details from CRS : " + responseValueFromCrs);
                    METrackerUtil.addOrUpdateMETrackParams("SUExportFailReq", responseValueFromCrs.toString());
                }
                else {
                    SyMLogger.warning(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Un supported operation : " + responseValueFromCrs);
                }
            }
            if (updateLastTasksDetailsFetchTimeFromCRS) {
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Going to update crs last tasks fetch time : " + currentCRSFetchTime);
                SilentUpdateHelper.getInstance().updateCustomerConfigProps("crs_export_last_modified_since", String.valueOf(currentCRSFetchTime));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void downloadTaskDependencyBinarys() {
        try {
            final Criteria downloadPendingTasksCriteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"), (Object)0, 0);
            final Criteria applicableBuildNumberCriteria = new Criteria(Column.getColumn("SilentUpdateDetails", "BASE_BUILD_NUMBER"), (Object)SilentUpdateHelper.getInstance().getCurrentBuildNumber(), 0);
            final DataObject dataObject = SilentUpdateHelper.getInstance().getQPPMDetails(downloadPendingTasksCriteria.and(applicableBuildNumberCriteria), false);
            if (dataObject != null) {
                if (dataObject.isEmpty()) {
                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "downloadTaskDependencyBinarys", "downloadTaskDependencyBinarys - Currently tasks not available in this setup.");
                }
                else {
                    final SilentUpdateController silentUpdateController = new SilentUpdateController();
                    final Iterator rows = dataObject.getRows("SilentUpdateDetails");
                    final Iterator extnRows = dataObject.getRows("SilentUpdateDetailsExtn");
                    while (rows.hasNext() && extnRows.hasNext()) {
                        final Row row = rows.next();
                        Row extnRow = extnRows.next();
                        final String qppmUniqueId = row.get("QPPM_ID").toString();
                        boolean isQPPMAlreadyApplied = false;
                        extnRow = silentUpdateController.downloadQPPM(extnRow, row.get("QPPM_URL").toString(), row.get("QPPM_CHECKSUM").toString(), qppmUniqueId);
                        final String qPPMFixesIds = SilentUpdateHelper.getInstance().getQPPMFixesIds(qppmUniqueId);
                        SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "", "Fixes id from QPPM : " + qPPMFixesIds);
                        if (qPPMFixesIds.trim().length() > 0) {
                            extnRow.set("QPPM_FIXES_ID", (Object)qPPMFixesIds);
                            isQPPMAlreadyApplied = SilentUpdateHelper.getInstance().isQPPMAlreadyApplied(qppmUniqueId, Arrays.asList(qPPMFixesIds));
                        }
                        if (isQPPMAlreadyApplied) {
                            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "downloadTaskDependencyBinarys", "The QPPM(" + qppmUniqueId + ") is already applied for this setup. So skipped this.");
                            extnRow.set("TASK_STATUS", (Object)3);
                        }
                        else if (row.get("DYNAMIC_CHECKER_CLASS_URL") != null) {
                            silentUpdateController.downloadDynamicCheckerClass(row.get("DYNAMIC_CHECKER_CLASS_URL").toString(), row.get("DYNAMIC_CHECKER_CLASS_CHECKSUM").toString(), qppmUniqueId);
                            final boolean isDynamicCheckerLoaded = SilentUpdateHelper.getInstance().loadDynamicChecker(qppmUniqueId);
                            if (isDynamicCheckerLoaded) {
                                final SilentUpdateDynamicChecker dynamicChecker = new SilentUpdateDynamicChecker();
                                if (!dynamicChecker.isShowAlertMsg(qppmUniqueId)) {
                                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "The shown msg not applicable for this QPPM : " + qppmUniqueId);
                                    extnRow.set("IS_SHOW_ALERT_MSG", (Object)false);
                                }
                                final String dynamicGeneratedAlertMsg = dynamicChecker.getAlertMsg(qppmUniqueId);
                                if (dynamicGeneratedAlertMsg != null) {
                                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "Alert msg content regenerated for this(" + qppmUniqueId + ") QPPM : " + dynamicGeneratedAlertMsg);
                                    row.set("ALERT_MSG_CONTENT", (Object)dynamicGeneratedAlertMsg);
                                }
                                if (!dynamicChecker.isQPPMApplicable(qppmUniqueId)) {
                                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "The QPPM(" + qppmUniqueId + ") is not applicable for this setup.");
                                    extnRow.set("TASK_STATUS", (Object)(-1));
                                }
                            }
                        }
                        dataObject.updateRow(row);
                        dataObject.updateRow(extnRow);
                    }
                    DataAccess.update(dataObject);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "syncTaskDetails", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void processQPPMs() {
        try {
            final Criteria processPendingTasksCriteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"), (Object)1, 0);
            final Criteria fixApprovedTasksCriteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "IS_FIX_APPROVED"), (Object)true, 0);
            final DataObject dataObject = SilentUpdateHelper.getInstance().getQPPMDetails(processPendingTasksCriteria.and(fixApprovedTasksCriteria), false);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("SilentUpdateDetails");
                final Iterator extnRows = dataObject.getRows("SilentUpdateDetailsExtn");
                while (rows.hasNext() && extnRows.hasNext()) {
                    try {
                        final Row row = rows.next();
                        final Row extnRow = extnRows.next();
                        final String qppmUniqueId = row.get("QPPM_ID").toString();
                        final Integer qppmType = Integer.valueOf(row.get("QPPM_TYPE").toString());
                        SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "Going to process QPPM : " + qppmUniqueId);
                        SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "QPPM Type : " + qppmType);
                        SilentUpdateHelper.getInstance().copyQPPMTOQuickFix(qppmUniqueId);
                        switch (qppmType) {
                            case 0: {
                                this.withoutRestartForServer(qppmUniqueId);
                                break;
                            }
                            case 1: {
                                SilentUpdateHelper.getInstance().updateCustomerConfigProps("RestartRequired", true);
                                break;
                            }
                            default: {
                                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "advancedProcessForThisQPPM", "Un supported operation for this task, So skipped this task.");
                                break;
                            }
                        }
                        if (row.get("IMMEDIATE_MOVE_PRODUCT_COMPONENTS") != null) {
                            this.immediateMoveProductComponent(qppmUniqueId, row);
                        }
                        extnRow.set("TASK_STATUS", (Object)2);
                        dataObject.updateRow(extnRow);
                    }
                    catch (final Exception e) {
                        SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "Exception occurred : ", (Throwable)e);
                    }
                }
                DataAccess.update(dataObject);
            }
            else {
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "processQPPMs - Currently tasks not available in this setup.");
            }
        }
        catch (final Exception e2) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "processQPPMs", "Exception occurred : ", (Throwable)e2);
        }
    }
    
    private void immediateMoveProductComponent(final String qppmUniqueId, final Row row) throws Exception {
        try {
            final JSONArray immediateMoveProductComponents = (JSONArray)JSONValue.parse(row.get("IMMEDIATE_MOVE_PRODUCT_COMPONENTS").toString());
            final JSONObject productComponents = (JSONObject)JSONValue.parse(row.get("PRODUCT_COMPONENTS").toString());
            for (final Object component : immediateMoveProductComponents) {
                final String componentName = component.toString();
                final JSONObject componentDetail = (JSONObject)JSONValue.parse(productComponents.get((Object)componentName).toString());
                if (this.moveProductComponentBinarys(qppmUniqueId, componentDetail.get((Object)"ComponentRepoInQPPM").toString(), componentDetail.get((Object)"ComponentRepoInServer").toString())) {
                    final SilentUpdateProductListener productListener = SilentUpdateHelper.getInstance().getProductListenerInstance();
                    if (productListener == null) {
                        continue;
                    }
                    productListener.updateMasterMetadata(componentName);
                }
                else {
                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "immediateMoveProductComponent", "ProductComponents move failed : " + componentName);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "immediateMoveProductComponent", "Exception occurred while move product components : ", (Throwable)e);
        }
    }
    
    public boolean withoutRestartForServer(final String qppmUniqueId) {
        try {
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "withoutRestartForServer", "Without restart operation currently not supported for server.");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "withoutRestartForServer", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public void startupHandling() {
        try {
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "startupHandling", "Silent update startup handling has been called.");
            SilentUpdateHelper.getInstance().updateCustomerConfigProps("RestartRequired", false);
            new SilentUpdateInvoker().addOrUpdateSchedule();
            final Properties customerSpecific = FileAccessUtil.readProperties(SilentUpdateHelper.getInstance().getSilentUpdateUserConfPath() + File.separator + "qppm-status.props");
            final Enumeration<Object> keys = ((Hashtable<Object, V>)customerSpecific).keys();
            while (keys.hasMoreElements()) {
                final String key = String.valueOf(keys.nextElement());
                this.qppmApplied(key, customerSpecific.getProperty(key));
            }
            FileAccessUtil.storeProperties(new Properties(), SilentUpdateHelper.getInstance().getSilentUpdateUserConfPath() + File.separator + "qppm-status.props", false);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "startupHandling", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void qppmApplied(final String qppmUniqueId, final String status) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SilentUpdateDetails", "QPPM_ID"), (Object)qppmUniqueId, 0);
            final DataObject dataObject = SilentUpdateHelper.getInstance().getQPPMDetails(criteria, false);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row extnRow = dataObject.getFirstRow("SilentUpdateDetailsExtn");
                if (status.equalsIgnoreCase("installed")) {
                    final Row row = dataObject.getFirstRow("SilentUpdateDetails");
                    try {
                        if (row.get("PRODUCT_COMPONENTS") != null) {
                            final JSONObject productComponents = (JSONObject)JSONValue.parse(row.get("PRODUCT_COMPONENTS").toString());
                            final ArrayList<String> componentList = new ArrayList<String>(productComponents.keySet());
                            for (final String componentName : componentList) {
                                final JSONObject componentDetail = (JSONObject)JSONValue.parse(productComponents.get((Object)componentName).toString());
                                if (this.moveProductComponentBinarys(qppmUniqueId, componentDetail.get((Object)"ComponentRepoInQPPM").toString(), componentDetail.get((Object)"ComponentRepoInServer").toString())) {
                                    final SilentUpdateProductListener productListener = SilentUpdateHelper.getInstance().getProductListenerInstance();
                                    if (productListener == null) {
                                        continue;
                                    }
                                    productListener.updateMasterMetadata(componentName);
                                }
                                else {
                                    SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "qppmAppliedFinish", "ProductComponents move failed : " + componentName);
                                }
                            }
                        }
                        else {
                            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "qppmAppliedFinish", "Product component moved not required for this QPPM(" + qppmUniqueId + ").");
                        }
                    }
                    catch (final Exception e) {
                        SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "qppmAppliedFinish", "Exception occurred while move product components : ", (Throwable)e);
                    }
                    extnRow.set("TASK_STATUS", (Object)3);
                }
                else if (status.equalsIgnoreCase("reverted")) {
                    extnRow.set("TASK_STATUS", (Object)(-3));
                }
                else {
                    extnRow.set("TASK_STATUS", (Object)(-4));
                }
                dataObject.updateRow(extnRow);
                DataAccess.update(dataObject);
            }
            else {
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "qppmApplied", "Tasks not available for this qppmUniqueId : " + qppmUniqueId);
            }
        }
        catch (final Exception e2) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "qppmApplied", "Exception occurred : ", (Throwable)e2);
        }
    }
    
    private boolean moveProductComponentBinarys(final String qppmUniqueId, final String componentRepoInQPPM, final String componentRepoInServer) {
        try {
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "moveProductComponentBinarys", "Going to move the server component binary's : componentRepoInQPPM - " + componentRepoInQPPM + ", componentRepoInServer - " + componentRepoInServer);
            final String qppmExtractDir = SilentUpdateHelper.getInstance().extractQPPM(qppmUniqueId, componentRepoInQPPM);
            if (qppmExtractDir != null && new File(qppmExtractDir).exists()) {
                final Path componentRepoInServerHome = Paths.get(System.getProperty("server.home") + File.separator + componentRepoInServer, new String[0]);
                final Path componentRepoInQPPMHome = Paths.get(qppmExtractDir + File.separator + componentRepoInQPPM, new String[0]);
                FileAccessUtil.moveFolder(componentRepoInQPPMHome, componentRepoInServerHome, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                return true;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "moveProductComponentBinarys", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public void enableAutoApprove() {
        try {
            final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("proceed.silentupdate.autoapprove", String.valueOf(true));
            FileAccessUtil.storeProperties(properties, systemPropertiesPath, true);
            final ArrayList taskIDList = SilentUpdateHelper.getInstance().getQPPMTaskIDForNonSecurityTask();
            final Criteria taskIDCriteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskIDList.toArray(), 8);
            SilentUpdateHelper.getInstance().updateDBValue("SilentUpdateDetailsExtn", "IS_FIX_APPROVED", true, taskIDCriteria);
            this.processQPPMs();
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "enableAutoApprove", "==================================");
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "enableAutoApprove", "  Auto approve enabled           ");
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "enableAutoApprove", "==================================");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "enableAutoApprove", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void disableAutoApprove() {
        try {
            final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("proceed.silentupdate.autoapprove", String.valueOf(false));
            FileAccessUtil.storeProperties(properties, systemPropertiesPath, true);
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "disableAutoApprove", "==================================");
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "disableAutoApprove", "  Auto approve disabled          ");
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "disableAutoApprove", "==================================");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "disableAutoApprove", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public boolean isAutoApproveEnabled() {
        try {
            final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
            final Properties properties = FileAccessUtil.readProperties(systemPropertiesPath);
            if (properties.containsKey("proceed.silentupdate.autoapprove")) {
                return Boolean.parseBoolean(((Hashtable<K, Object>)properties).get("proceed.silentupdate.autoapprove").toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "updateCustomerConfigProps", "Exception occurred while read property : ", (Throwable)e);
        }
        return false;
    }
    
    public boolean isRestartRequired() {
        try {
            return Boolean.parseBoolean(SilentUpdateHelper.getInstance().customerSpecificProps("RestartRequired"));
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "updateCustomerConfigProps", "Exception occurred while read property : ", (Throwable)e);
            return false;
        }
    }
    
    public JSONObject getAlertMsgRequiredQPPMDetail() {
        try {
            final Criteria quickFixApplyPendingTasks = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"), (Object)2, 0);
            final Criteria processPendingTasks = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"), (Object)1, 0);
            final Criteria approvePendingTasks = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "IS_FIX_APPROVED"), (Object)false, 0);
            final Criteria buildNumber = new Criteria(Column.getColumn("SilentUpdateDetails", "BASE_BUILD_NUMBER"), (Object)SilentUpdateHelper.getInstance().getCurrentBuildNumber(), 0);
            final DataObject dataObject = SilentUpdateHelper.getInstance().getQPPMDetails(buildNumber.and(quickFixApplyPendingTasks.or(processPendingTasks.and(approvePendingTasks))), true);
            if (dataObject != null && !dataObject.isEmpty()) {
                final JSONObject taskDetail = SilentUpdateHelper.getInstance().convertRowTOJson(dataObject.getFirstRow("SilentUpdateDetails"));
                taskDetail.putAll((Map)SilentUpdateHelper.getInstance().convertRowTOJson(dataObject.getFirstRow("SilentUpdateDetailsExtn")));
                final Long lastMsgShownTime = Long.valueOf(taskDetail.get((Object)"ALERT_MSG_LAST_SHOWN_TIME").toString());
                if (Boolean.parseBoolean(taskDetail.get((Object)"IS_SHOW_ALERT_MSG").toString()) && taskDetail.containsKey((Object)"ALERT_MSG_CONTENT") && ((!Boolean.parseBoolean(taskDetail.get((Object)"IS_FIX_APPROVED").toString()) && Integer.parseInt(taskDetail.get((Object)"TASK_STATUS").toString()) == 1) || (Integer.parseInt(taskDetail.get((Object)"QPPM_TYPE").toString()) == 1 && (Boolean.parseBoolean(taskDetail.get((Object)"HIDE_REMINDME_LATER").toString()) || taskDetail.containsKey((Object)"ALERT_MSG_FREQUENCY")) && (lastMsgShownTime == -1L || System.currentTimeMillis() - lastMsgShownTime >= Long.valueOf(taskDetail.get((Object)"ALERT_MSG_FREQUENCY").toString()))))) {
                    return taskDetail;
                }
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "getAlertMsgRequiredQPPMDetail", "Alert message not available");
            }
            else {
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "getAlertMsgRequiredQPPMDetail", "AlertMsgRequiredQPPMDetail - Currently tasks not available in this setup.");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "getAlertMsgRequiredQPPMDetail", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public void remindMeLater(final String taskId) {
        try {
            final long lastShownTime = System.currentTimeMillis();
            final Criteria criteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskId, 0);
            SilentUpdateHelper.getInstance().updateDBValue("SilentUpdateDetailsExtn", "ALERT_MSG_LAST_SHOWN_TIME", lastShownTime, criteria);
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "remindMeLater", "Tasks 'ALERT_MSG_LAST_SHOWN_TIME' updated as '" + lastShownTime + "'");
            METrackerUtil.incrementMETrackParams("SUReminderMELatter." + taskId);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "remindMeLater", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void dismiss(final String taskId) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskId, 0);
            SilentUpdateHelper.getInstance().updateDBValue("SilentUpdateDetailsExtn", "IS_SHOW_ALERT_MSG", false, criteria);
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "dismiss", "Tasks 'IS_SHOW_ALERT_MSG' updated as 'false'");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "dismiss", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void ignoreThisFix(final String taskId) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskId, 0);
            SilentUpdateHelper.getInstance().updateDBValue("SilentUpdateDetailsExtn", "TASK_STATUS", -2, criteria);
            SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "ignoreThisFix", "Tasks 'TASK_STATUS' updated as 'QPPM_IGNORED'");
            final String quickFixFileName = System.getProperty("server.home") + File.separator + "quickfixer" + File.separator + "Ondemand" + File.separator + taskId + ".qpm";
            final String rootFolderCanonicalPath = new File(System.getProperty("server.home") + File.separator + "quickfixer" + File.separator + "Ondemand").getCanonicalPath();
            if (!new File(quickFixFileName).getCanonicalPath().startsWith(rootFolderCanonicalPath)) {
                throw new IOException("Filepath is outside of the target dir: " + quickFixFileName);
            }
            if (Files.deleteIfExists(Paths.get(quickFixFileName, new String[0]))) {
                SyMLogger.info(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "ignoreThisFix", "The task(" + taskId + ") has been deleted from On-demand quickfix directory!");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHandler.logger, SilentUpdateHandler.sourceClass, "ignoreThisFix", "Exception occurred : ", (Throwable)e);
        }
    }
    
    static {
        SilentUpdateHandler.logger = Logger.getLogger("SilentUpdate");
        SilentUpdateHandler.sourceClass = "SilentUpdateHandler";
    }
}
