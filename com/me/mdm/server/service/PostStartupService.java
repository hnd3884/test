package com.me.mdm.server.service;

import java.util.Hashtable;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.mdm.server.util.UploadUtil;
import com.me.mdm.server.metracker.MEMDMTrackParamsPersistTask;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import java.util.List;
import com.me.mdm.server.doc.DocSummaryHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.Criteria;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.APIRequestMapper;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class PostStartupService implements Service
{
    private Logger logger;
    private String sourceClass;
    
    public PostStartupService() {
        this.logger = Logger.getLogger(PostStartupService.class.getName());
        this.sourceClass = PostStartupService.class.getName();
    }
    
    public void create(final DataObject d) throws Exception {
        final String sourceMethod = "create";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Creating MDMControllerService Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
    
    public void start() throws Exception {
        final String sourceMethod = "start";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Starting MDMControllerService Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MDMAgentUpgradeTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.MDMAgentUpgradeTask", taskInfoMap, new Properties());
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : ", exp);
        }
        taskInfoMap.put("taskName", "AndroidAdminEnrollCompletionTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.task.AndroidAdminEnrollCompletionTask", new HashMap(), new Properties());
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : ", exp);
        }
        taskInfoMap.put("taskName", "ManagedUserCleanupTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.enrollment.task.ManagedUserCleanupTask", new HashMap(), new Properties());
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : ", exp);
        }
        taskInfoMap.put("taskName", "GoogleAccountEnableTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        final Properties GoogleAccTaskProps = new Properties();
        ((Hashtable<String, String>)GoogleAccTaskProps).put("TASK_TYPE", "StartMissedTaskToEnableState");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.android.afw.GoogleAccountEnableTask", new HashMap(), GoogleAccTaskProps);
        }
        catch (final Exception exp2) {
            this.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : ", exp2);
        }
        try {
            taskInfoMap.put("taskName", "AddBulkAFWCommandTaskSasmung");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            final Properties PFWSamsungTaskProp = new Properties();
            final JSONObject propJson = new JSONObject();
            propJson.put("DeviceType", (Object)"SamsungOnUpgrade");
            ((Hashtable<String, String>)PFWSamsungTaskProp).put("jsonParams", propJson.toString());
            ((Hashtable<String, String>)PFWSamsungTaskProp).put("taskName", "AddBulkAFWCommandTaskSasmung");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.android.afw.AddBulkAFWCommandTask", taskInfoMap, PFWSamsungTaskProp);
            final Properties PFWLegacyTaskProp = new Properties();
            final JSONObject propJson2 = new JSONObject();
            propJson2.put("DeviceType", (Object)"LegacyOnUpgrade");
            ((Hashtable<String, String>)PFWLegacyTaskProp).put("jsonParams", propJson2.toString());
            ((Hashtable<String, String>)PFWLegacyTaskProp).put("taskName", "AddBulkAFWCommandTaskLegacy");
            taskInfoMap.put("taskName", "AddBulkAFWCommandTaskLegacy");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.android.afw.AddBulkAFWCommandTask", taskInfoMap, PFWLegacyTaskProp);
        }
        catch (final Exception exp2) {
            this.logger.log(Level.WARNING, "Exception occurred when calling AddBulkAFWCommandTask scheduler: ", exp2);
        }
        taskInfoMap.put("taskName", "DistributePublicKey");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.command.smscommand.SmsStartupHandler", taskInfoMap, new Properties());
        }
        catch (final Exception exp2) {
            this.logger.log(Level.WARNING, "Exception occurred when calling DistributePublicKey scheduler: ", exp2);
        }
        taskInfoMap.put("taskName", "MigrateQueue");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final Properties taskProps = new Properties();
        ((Hashtable<String, String>)taskProps).put("taskName", "MigrateQueue");
        ((Hashtable<String, String>)taskProps).put("schedulerTime", String.valueOf(System.currentTimeMillis()));
        ((Hashtable<String, String>)taskProps).put("poolName", "asynchThreadPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.queue.QueueMigrationHandler", taskInfoMap, taskProps);
        }
        catch (final Exception exp3) {
            this.logger.log(Level.WARNING, "Exception occurred when pausing old queues: ", exp3);
        }
        final String isDeleteMDMConfigSchedule = SyMUtil.getSyMParameter("isDeleteMDMConfigSchedule");
        this.logger.log(Level.INFO, "isDeleteMDMConfigSchedule value is {0}", isDeleteMDMConfigSchedule);
        if (isDeleteMDMConfigSchedule != null && isDeleteMDMConfigSchedule.trim().length() > 0 && Boolean.valueOf(isDeleteMDMConfigSchedule)) {
            ApiFactoryProvider.getSchedulerAPI().removeScheduler("MDMConfigStatusUpdate");
            SyMUtil.deleteSyMParameter("isDeleteMDMConfigSchedule");
        }
        try {
            final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
            final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
            final boolean isUemLicenseSatisfied = CustomerInfoUtil.getInstance().isMSP() || isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
            if (isUemLicenseSatisfied && MDMUtil.getSyMParameter("auto_assign_mac_devices").equalsIgnoreCase("true")) {
                MDMApiFactoryProvider.getMDMUtilAPI().addAutoUserAssignRule(new JSONObject());
                new DeviceForEnrollmentHandler().applyAssignUserRulesForPendingDevices();
                new UserAssignmentRuleHandler().postUserAssignmentSettingsforAllCustomers(Boolean.FALSE);
                MDMUtil.updateSyMParameter("auto_assign_mac_devices", "false");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Failed to make changes for auto user assignment ", e);
        }
        final String isDeleteBlacklistSchedulers = SyMUtil.getSyMParameter("isDeleteBlacklistSchedulers");
        this.logger.log(Level.INFO, "isDeleteBlacklistSchedulers value is {0}", isDeleteBlacklistSchedulers);
        if (isDeleteBlacklistSchedulers != null && isDeleteBlacklistSchedulers.trim().length() > 0 && Boolean.valueOf(isDeleteBlacklistSchedulers)) {
            ApiFactoryProvider.getSchedulerAPI().removeScheduler("BlacklistWhitelistCommandTask");
            SyMUtil.deleteSyMParameter("isDeleteBlacklistSchedulers");
        }
        try {
            taskInfoMap.put("taskName", "OSUpdateSeqCmdTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.updates.osupdates.task.OSUpdateSeqCmdTask", taskInfoMap, new Properties());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding sequential cmd task for os update", ex);
        }
        try {
            APIRequestMapper.createRequestMapper();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while creating request mapper", ex);
        }
        try {
            taskInfoMap.put("taskName", "OSUpdateLicProfileRemoval");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.updates.osupdates.task.OSUpdateStandardLicenseProfileRemovalTask", taskInfoMap, new Properties());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred OSUpdate License removal", ex);
        }
        try {
            final String isNeedToExecute = MDMUtil.getSyMParameter("FilevaultRepublishNeeded");
            if (!MDMStringUtils.isEmpty(isNeedToExecute) && Boolean.valueOf(isNeedToExecute)) {
                taskInfoMap.put("taskName", "FilevaultRepublishTask");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                taskInfoMap.put("poolName", "mdmPool");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.profiles.mac.task.MDMFilevaultRepublishtask", taskInfoMap, new Properties());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding the filevault regeneration", ex);
        }
        try {
            this.webserverCount();
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in method");
        }
        taskInfoMap.put("taskName", "IOSExtarctProvisioningDetails");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.ios.task.IOSExtarctProvisioningDetails", new HashMap(), new Properties());
        }
        catch (final Exception exp4) {
            this.logger.log(Level.WARNING, "Exception occurred during the schdule IOSExtarctProvisioningDetails : ", exp4);
        }
        try {
            final String taskName = "migrateMDMstaticFiles";
            final String needToMigrate = MDMUtil.getSyMParameter(taskName);
            if (!MDMStringUtils.isEmpty(needToMigrate)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "mdmPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.files.FileMigrator", taskInfoMap, taskProperties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in migrateMDMstaticFiles", ex);
        }
        try {
            final String taskName = "CapabilitesInfoCommand";
            taskInfoMap.put("taskName", taskName);
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "asynchThreadPool");
            final Properties taskProperties2 = new Properties();
            ((Hashtable<String, String>)taskProperties2).put("taskName", taskName);
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.remotesession.RemoteControlCapabilitiesStartupUtil", taskInfoMap, taskProperties2);
        }
        catch (final Exception exp4) {
            this.logger.log(Level.SEVERE, "Exception while adding capability info command ", exp4);
        }
        try {
            final String isAppGroupToCollnAlterTableCompleted = MDMUtil.getSyMParameter("isAppGroupToCollnAlterTableCompleted");
            if (!MDMStringUtils.isEmpty(isAppGroupToCollnAlterTableCompleted) && !Boolean.valueOf(isAppGroupToCollnAlterTableCompleted)) {
                this.logger.log(Level.INFO, "Going to update AppGroupToCollection.RELEASE_LABEL_ID with FK constraint and set Nullable=False state");
                final AlterTableQuery relLabelColAlterQuery = (AlterTableQuery)new AlterTableQueryImpl("AppGroupToCollection");
                final ColumnDefinition columnDefinition = new ColumnDefinition();
                columnDefinition.setTableName("AppGroupToCollection");
                columnDefinition.setColumnName("RELEASE_LABEL_ID");
                columnDefinition.setNullable((boolean)Boolean.FALSE);
                columnDefinition.setDataType("BIGINT");
                relLabelColAlterQuery.modifyColumn("RELEASE_LABEL_ID", columnDefinition);
                final ForeignKeyColumnDefinition foreignKeyColumnDefinition = new ForeignKeyColumnDefinition();
                final ColumnDefinition localColDefinition = new ColumnDefinition();
                localColDefinition.setTableName("AppGroupToCollection");
                localColDefinition.setColumnName("RELEASE_LABEL_ID");
                localColDefinition.setDataType("BIGINT");
                foreignKeyColumnDefinition.setLocalColumnDefinition(localColDefinition);
                final ColumnDefinition referencedColDefinition = new ColumnDefinition();
                referencedColDefinition.setTableName("AppReleaseLabel");
                referencedColDefinition.setColumnName("RELEASE_LABEL_ID");
                referencedColDefinition.setDataType("BIGINT");
                foreignKeyColumnDefinition.setReferencedColumnDefinition(referencedColDefinition);
                final ForeignKeyDefinition foreignKeyDefinition = new ForeignKeyDefinition();
                foreignKeyDefinition.setBidirectional((boolean)Boolean.FALSE);
                foreignKeyDefinition.setName("AppGroupToCollection_FK3");
                foreignKeyDefinition.addForeignKeyColumns(foreignKeyColumnDefinition);
                foreignKeyDefinition.setConstraints(1);
                foreignKeyDefinition.setMasterTableName("AppReleaseLabel");
                relLabelColAlterQuery.addForeignKey(foreignKeyDefinition);
                DataAccess.alterTable(relLabelColAlterQuery);
                this.logger.log(Level.INFO, "Successfully updated AppGroupToCollection.RELEASE_LABEL_ID with FK and Nullable=False state");
                MDMUtil.deleteSyMParameter("isAppGroupToCollnAlterTableCompleted");
            }
            final String isAppConfigTemplateAlterTableCompleted = MDMUtil.getSyMParameter("isAppConfigTemplateAlterTableCompleted");
            if (!MDMStringUtils.isEmpty(isAppConfigTemplateAlterTableCompleted) && !Boolean.valueOf(isAppConfigTemplateAlterTableCompleted)) {
                this.logger.log(Level.INFO, "Going to update AppConfigTemplate.APP_ID  with FK constraint and Nullable=False state");
                final AlterTableQuery appConfigTemplateAppIdColAlterQuery = (AlterTableQuery)new AlterTableQueryImpl("AppConfigTemplate");
                final ColumnDefinition columnDefinition2 = new ColumnDefinition();
                columnDefinition2.setTableName("AppConfigTemplate");
                columnDefinition2.setColumnName("APP_ID");
                columnDefinition2.setDataType("BIGINT");
                columnDefinition2.setNullable((boolean)Boolean.FALSE);
                appConfigTemplateAppIdColAlterQuery.modifyColumn("APP_ID", columnDefinition2);
                final ForeignKeyColumnDefinition foreignKeyColumnDefinition2 = new ForeignKeyColumnDefinition();
                final ColumnDefinition localColDefinition2 = new ColumnDefinition();
                localColDefinition2.setTableName("AppConfigTemplate");
                localColDefinition2.setColumnName("APP_ID");
                localColDefinition2.setDataType("BIGINT");
                foreignKeyColumnDefinition2.setLocalColumnDefinition(localColDefinition2);
                final ColumnDefinition referencedColDefinition2 = new ColumnDefinition();
                referencedColDefinition2.setTableName("MdAppDetails");
                referencedColDefinition2.setColumnName("APP_ID");
                referencedColDefinition2.setDataType("BIGINT");
                foreignKeyColumnDefinition2.setReferencedColumnDefinition(referencedColDefinition2);
                final ForeignKeyDefinition foreignKeyDefinition2 = new ForeignKeyDefinition();
                foreignKeyDefinition2.setBidirectional((boolean)Boolean.FALSE);
                foreignKeyDefinition2.setName("AppConfigTemplate_FK0");
                foreignKeyDefinition2.addForeignKeyColumns(foreignKeyColumnDefinition2);
                foreignKeyDefinition2.setConstraints(1);
                foreignKeyDefinition2.setMasterTableName("MdAppDetails");
                appConfigTemplateAppIdColAlterQuery.addForeignKey(foreignKeyDefinition2);
                DataAccess.alterTable(appConfigTemplateAppIdColAlterQuery);
                this.logger.log(Level.INFO, "Successfully updated AppConfigTemplate.APP_ID with FK and Nullable=False state");
                MDMUtil.deleteSyMParameter("isAppConfigTemplateAlterTableCompleted");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while performing alter table for AppGroupToCollection and AppConfigTemplate", ex);
        }
        try {
            final String taskName = "RePublishAppProfiles";
            final String needToRePublish = MDMUtil.getSyMParameter(taskName);
            if (!MDMStringUtils.isEmpty(needToRePublish) && Boolean.valueOf(needToRePublish)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.app.RepublishAppProfileTask", taskInfoMap, taskProperties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in RePublishAppProfiles", ex);
        }
        try {
            final String taskName = "RepublishMSIAppProfiles";
            final String needTORepublish = MDMUtil.getSyMParameter(taskName);
            if (!MDMStringUtils.isEmpty(needTORepublish) && Boolean.valueOf(needTORepublish)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.app.RepublishAppProfileTask", taskInfoMap, taskProperties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in RepublishMSIAppProfiles", ex);
        }
        try {
            final String taskName = "RePublishMacAgents";
            final String needToRepublish = MDMUtil.getSyMParameter(taskName);
            if (!MDMStringUtils.isEmpty(needToRepublish) && Boolean.valueOf(needToRepublish)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.RePublishMacAgents", taskInfoMap, taskProperties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception invoking RePublishMacAgents", ex);
        }
        try {
            final String taskName = "AppConfigurationRepublishTask";
            final String completion = "AppConfigRepublishCompleted";
            final String isRepublishNeeded = MDMUtil.getSyMParameter(taskName);
            final String isTaskCompleted = MDMUtil.getSyMParameter(completion);
            if (MDMStringUtils.isEmpty(isTaskCompleted) && !MDMStringUtils.isEmpty(isRepublishNeeded) && Boolean.valueOf(isRepublishNeeded)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties3 = new Properties();
                ((Hashtable<String, String>)taskProperties3).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.RepublishAppConfigurations", taskInfoMap, taskProperties3);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception invoking AppConfigurationRepublishTask");
        }
        try {
            final String taskName = "migrateAppStaticFiles";
            final String needToMigrate = MDMUtil.getSyMParameter(taskName);
            if (!MDMStringUtils.isEmpty(needToMigrate)) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "mdmPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.files.FileMigrator", taskInfoMap, taskProperties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in migrateAppStaticFiles", ex);
        }
        try {
            final String taskName = "ELMNewKeyUpdate";
            if (AndroidAgentSecretsHandler.getBackwardCompatibilityElmKey() == null) {
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("poolName", "mdmPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties2 = new Properties();
                ((Hashtable<String, String>)taskProperties2).put("taskName", taskName);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.task.ELMKeysUpdateHandlerTask", taskInfoMap, taskProperties2);
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in ELMNewKeyUpdate ", e2);
        }
        try {
            final String addLocationAddress = SyMUtil.getSyMParameterFromDB("addLocationAddress");
            if (addLocationAddress != null && addLocationAddress.equalsIgnoreCase("true")) {
                taskInfoMap.put("taskName", "AddLocationAddress");
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties2 = new Properties();
                ((Hashtable<String, String>)taskProperties2).put("OPERATION", "sendBatchReq");
                ((Hashtable<String, String>)taskProperties2).put("taskName", "AddLocationAddress");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.location.LocationAddressPopulateTask", taskInfoMap, taskProperties2);
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in LocationAddressPopulateTask ", e2);
        }
        try {
            final String deleteUnwantedLocations = SyMUtil.getSyMParameterFromDB("deleteUnwantedLocations");
            if (deleteUnwantedLocations != null && deleteUnwantedLocations.equalsIgnoreCase("true")) {
                taskInfoMap.put("taskName", "DeleteUnwantedLocations");
                taskInfoMap.put("poolName", "asynchThreadPool");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties taskProperties2 = new Properties();
                ((Hashtable<String, String>)taskProperties2).put("Customer_id", "all");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.location.DeleteUnwantedLocationTask", taskInfoMap, taskProperties2, "asynchThreadPool");
                MDMUtil.deleteSyMParameter("deleteUnwantedLocations");
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in DeleteUnwantedLocationTask ", e2);
        }
        try {
            this.logger.log(Level.INFO, "Getting FCM Keys for IOS Announcement Notification in server startup");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentSecrets"));
            selectQuery.addSelectColumn(new Column("AgentSecrets", "*"));
            final ArrayList seceret_type = new ArrayList();
            seceret_type.add(1);
            seceret_type.add(2);
            final Criteria agentSecretCri = new Criteria(Column.getColumn("AgentSecrets", "AGENT_SECRET_TYPE"), (Object)seceret_type.toArray(), 8);
            selectQuery.setCriteria(agentSecretCri);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject.isEmpty()) {
                final HashMap fcmTaskInfoMap = new HashMap();
                fcmTaskInfoMap.put("taskName", "IOSFCMCreatorTask");
                fcmTaskInfoMap.put("poolName", "mdmPool");
                fcmTaskInfoMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.onpremise.notification.IOSFCMNotificationCreatorHandler", taskInfoMap, new Properties());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in ios fcm create task", ex);
        }
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("HANDLE_CMS_ACK")) {
            DocSummaryHandler.getInstance().reviseDocSummary((List)null);
        }
        try {
            final String checkSumCalculationNeeded = SyMUtil.getSyMParameterFromDB("checkSumCalculationNeeded");
            if (checkSumCalculationNeeded != null && checkSumCalculationNeeded.equalsIgnoreCase("true")) {
                final HashMap checkSumInfoMap = new HashMap();
                checkSumInfoMap.put("taskName", "FileCheckSumAsyncTask");
                checkSumInfoMap.put("poolName", "mdmPool");
                checkSumInfoMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.files.FileCheckSumAsyncTask", checkSumInfoMap, new Properties());
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in calculating checksum for tasks", e2);
        }
        try {
            final String isVPPSyncNeeded = MDMUtil.getSyMParameter("IsVPPSyncNeeded");
            if (!MDMStringUtils.isEmpty(isVPPSyncNeeded) && Boolean.valueOf(isVPPSyncNeeded)) {
                this.addVPPSyncTask();
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in adding VPPSyncTskToQueue", e2);
        }
        finally {
            MDMUtil.updateSyMParameter("IsVPPSyncNeeded", "false");
        }
        try {
            final String republishNeeded = SyMUtil.getSyMParameterFromDB("LockScreenRepublishNeeded");
            if (republishNeeded != null && republishNeeded.equalsIgnoreCase("true")) {
                final HashMap propsMap = new HashMap();
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("sysParam", "LockScreenRepublishNeeded");
                ((Hashtable<String, Integer>)taskProperties).put("configId", 567);
                propsMap.put("taskName", "AndroidProfileRepublishTask");
                propsMap.put("poolName", "mdmPool");
                propsMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.profiles.AndroidProfileRepublishTask", propsMap, taskProperties);
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in republishing the Android lock screen profile", e2);
        }
        try {
            final String republishNeeded = SyMUtil.getSyMParameterFromDB("PasscodeComplexityRepublishNeeded");
            if (republishNeeded != null && republishNeeded.equalsIgnoreCase("true")) {
                final HashMap propsMap = new HashMap();
                final Properties taskProperties = new Properties();
                ((Hashtable<String, String>)taskProperties).put("sysParam", "PasscodeComplexityRepublishNeeded");
                ((Hashtable<String, Integer>)taskProperties).put("configId", 185);
                propsMap.put("taskName", "AndroidProfileRepublishTask");
                propsMap.put("poolName", "mdmPool");
                propsMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.profiles.AndroidProfileRepublishTask", propsMap, taskProperties);
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in republishing the Android Passcode profile", e2);
        }
        try {
            final String migrateClientDataFolder = SyMUtil.getSyMParameterFromDB("migrateClientDataFolder");
            if (migrateClientDataFolder != null && migrateClientDataFolder.equalsIgnoreCase("true")) {
                final HashMap taskMap = new HashMap();
                taskMap.put("taskName", "ClientDataFolderMigrationTask");
                taskMap.put("poolName", "asynchThreadPool");
                taskMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.files.foldermigration.ClientDataFolderMigrationTask", taskMap, new Properties());
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Cannot migrate client data files", e2);
        }
        final String deleteOldEnrollmentAPIKeys = SyMUtil.getSyMParameterFromDB("deleteOldEnrollmentAPIKeys");
        if (deleteOldEnrollmentAPIKeys != null && deleteOldEnrollmentAPIKeys.equalsIgnoreCase("true")) {
            final Criteria criteria = new Criteria(Column.getColumn("APIKeyDetails", "SERVICE_TYPE"), (Object)201, 0);
            DataAccess.delete(criteria);
            MDMUtil.deleteSyMParameter("deleteOldEnrollmentAPIKeys");
        }
        try {
            final String taskName2 = "resetSecretFieldsAndRepublishProfileTask";
            final String needToRepublish2 = MDMUtil.getSyMParameter(taskName2);
            if (!MDMStringUtils.isEmpty(needToRepublish2) && Boolean.valueOf(needToRepublish2)) {
                final HashMap secretFieldsMigrationMap = new HashMap();
                secretFieldsMigrationMap.put("taskName", taskName2);
                secretFieldsMigrationMap.put("poolName", "mdmPool");
                secretFieldsMigrationMap.put("schedulerTime", System.currentTimeMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.security.profile.task.PayloadSecretFieldsMigrationTask", secretFieldsMigrationMap, new Properties());
            }
        }
        catch (final Exception e3) {
            MDMUtil.updateSyMParameter("resetSecretFieldsAndRepublishProfileTask", "true");
            this.logger.log(Level.SEVERE, "Exception while initiating resetSecretFieldsAndRepublishProfile task", e3);
        }
        try {
            final ArrayList<Long> customerIDs = CustomerInfoUtil.getInstance().getCustomerIdList();
            for (final Long customerID : customerIDs) {
                MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", customerID);
                MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", customerID);
            }
        }
        catch (final Exception e3) {
            this.logger.log(Level.WARNING, "Error while showing endpoint central warning message boxes");
        }
        this.updateAndroidAgentRecoveryPasscode();
        try {
            final String migrateEMMAPI = MDMUtil.getSyMParameter("migrateEMMAPI");
            if (!MDMStringUtils.isEmpty(migrateEMMAPI) && Boolean.valueOf(migrateEMMAPI)) {
                this.migrateEMMAPITask();
            }
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in adding migrateEMMAPI", e3);
        }
    }
    
    public void stop() throws Exception {
        final String sourceMethod = "stop";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Stopping MDMControllerService Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        new MEMDMTrackParamsPersistTask().executeTask(new Properties());
        new UploadUtil().deleteTemporaryFolder();
    }
    
    public void destroy() throws Exception {
        final String sourceMethod = "destroy";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Destroying MDMControllerService Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
    
    public void webserverCount() {
        this.logger.log(Level.INFO, "Inside MDMHandler destroy method");
        try {
            final String confFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "install.conf";
            final Properties props = FileAccessUtil.readProperties(confFileName);
            final String webservrname = WebServerUtil.getWebServerName();
            this.logger.log(Level.INFO, "Webserver name is {0}", webservrname);
            if (props.containsKey("apache_start_count") && props.containsKey("nginx_start_count")) {
                if (webservrname.equalsIgnoreCase("apache")) {
                    int apachecount = Integer.parseInt(props.getProperty("apache_start_count"));
                    ++apachecount;
                    props.remove("apache_start_count");
                    ((Hashtable<String, String>)props).put("apache_start_count", String.valueOf(apachecount));
                }
                if (webservrname.equalsIgnoreCase("nginx")) {
                    int nginxcount = Integer.parseInt(props.getProperty("nginx_start_count"));
                    ++nginxcount;
                    props.remove("nginx_start_count");
                    ((Hashtable<String, String>)props).put("nginx_start_count", String.valueOf(nginxcount));
                }
            }
            FileAccessUtil.storeProperties(props, confFileName, false);
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while calculating nginx failure count", ex);
        }
    }
    
    public void addVPPSyncTask() {
        try {
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null) {
                for (int i = 0; i < customerIds.length; ++i) {
                    final Long customerID = customerIds[i];
                    final List<Long> businessStoreIDs = MDBusinessStoreUtil.getBusinessStoreIDs(customerID, (int)BusinessStoreSyncConstants.BS_SERVICE_VPP);
                    if (businessStoreIDs != null) {
                        for (int j = 0; j < businessStoreIDs.size(); ++j) {
                            final Long businessStoreID = businessStoreIDs.get(j);
                            final Row bSRow = DBUtil.getRowFromDB("ManagedBusinessStore", "BUSINESSSTORE_ID", (Object)businessStoreID);
                            if (bSRow != null) {
                                final Long userID = (Long)bSRow.get("BUSINESSSTORE_ADDED_BY");
                                final Properties taskProps = new Properties();
                                final JSONObject jsonParams = new JSONObject();
                                jsonParams.put("userID", (Object)userID);
                                jsonParams.put("PlatformType", 1);
                                jsonParams.put("BUSINESSSTORE_ID", (Object)businessStoreID);
                                ((Hashtable<String, Long>)taskProps).put("customerID", customerID);
                                ((Hashtable<String, String>)taskProps).put("jsonParams", jsonParams.toString());
                                final HashMap taskInfoMap = new HashMap();
                                taskInfoMap.put("taskName", "SyncVPPAppsTask");
                                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                                ((Hashtable<String, Long>)taskProps).put("customerID", customerID);
                                this.logger.log(Level.INFO, "Starting Task to Sync IOS Business Store: {0} details for customer : {1}", new Object[] { businessStoreID, customerID });
                                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.businessstore.SyncAppsTask", taskInfoMap, taskProps, "mdmPool");
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addVPPSyncTask", e);
        }
    }
    
    public void updateAndroidAgentRecoveryPasscode() {
        try {
            final String needToUpdateRecoveryKey = MDMUtil.getSyMParameter("updateRecoveryKeyNeeded");
            if (!MDMStringUtils.isEmpty(needToUpdateRecoveryKey) && Boolean.valueOf(needToUpdateRecoveryKey)) {
                this.logger.log(Level.INFO, "Going to udpate android agent recovery passcode");
                final List<Long> customerIds = new ArrayList<Long>();
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AndroidAgentSettings"));
                selectQuery.addSelectColumn(new Column("AndroidAgentSettings", "SETTINGS_ID"));
                selectQuery.addSelectColumn(new Column("AndroidAgentSettings", "RECOVERY_PASSWORD_ENCRYPTED"));
                selectQuery.addSelectColumn(new Column("AndroidAgentSettings", "CUSTOMER_ID"));
                selectQuery.setCriteria(new Criteria(new Column("AndroidAgentSettings", "RECOVERY_PASSWORD_ENCRYPTED"), (Object)null, 0));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final SecureRandom random = new SecureRandom();
                    final String randomToken = new BigInteger(32, random).toString(16);
                    final Iterator<Row> iter = dataObject.getRows("AndroidAgentSettings");
                    while (iter.hasNext()) {
                        final Row row = iter.next();
                        row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)randomToken);
                        dataObject.updateRow(row);
                        customerIds.add((Long)row.get("CUSTOMER_ID"));
                    }
                    MDMUtil.getPersistence().update(dataObject);
                }
                MDMUtil.deleteSyMParameter("updateRecoveryKeyNeeded");
                for (final Long customerId : customerIds) {
                    MDMAgentSettingsHandler.getInstance().wakeupAndroidDevicesForAgentSettings(customerId);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot update recovery password for", e);
        }
    }
    
    void migrateEMMAPITask() {
        try {
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null) {
                for (int i = 0; i < customerIds.length; ++i) {
                    final Long customerID = customerIds[i];
                    final Properties taskProps = new Properties();
                    ((Hashtable<String, Long>)taskProps).put("customerId", customerID);
                    final HashMap taskInfoMap = new HashMap();
                    taskInfoMap.put("taskName", "AndroidEMMAPIMigrationTask");
                    taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                    ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.android.afw.AndroidEMMAPIMigrationTask", taskInfoMap, taskProps);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in migrateEMMAPITask", e);
        }
    }
}
