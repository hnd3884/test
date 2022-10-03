package com.adventnet.sym.server.mdm.task;

import com.me.mdm.server.location.DummyLocationDataPopulationTask;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.uem.LicenseActionListenerImpl;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.backup.MDMDataBackupRunner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.AuthTokenUtil;
import com.adventnet.sym.server.mdm.util.SensitiveColumnDataMigrator;
import com.me.mdm.uem.ComputerToDeviceMappingUpdater;
import com.me.mdm.agent.handlers.ThrottlingHandler;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.server.settings.wifi.MdDeviceWifiSSIDDBHandler;
import com.adventnet.sym.server.mdm.featuresettings.battery.BatteryHistoryDeletionTask;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.export.ExportRequestDetailsHandler;
import com.me.mdm.server.enrollment.task.ManagedUserCleanupTask;
import com.me.mdm.files.ExpiredTempUploadedFileCleanUpTask;
import com.me.mdm.files.ExpiredAPIFileCleanupTask;
import com.me.mdm.server.ios.task.IOSEnterpriseAppExpiryCheckerTask;
import com.me.mdm.server.datausage.DataUsageUtil;
import com.me.mdm.server.enrollment.task.ResendEnrollmentRequestTask;
import com.me.mdm.server.backup.moduleimpl.LocationHistoryDataBackup;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import com.me.mdm.server.doc.DocMgmt;
import com.me.mdm.server.enrollment.task.EnrollmentCleanupTask;
import com.me.mdm.server.adep.AppleDEPSyncScheduler;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseExpiryCheckerTask;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMGlobalTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties taskProps) {
        try {
            final KnoxLicenseExpiryCheckerTask knoxExpiryTask = new KnoxLicenseExpiryCheckerTask();
            knoxExpiryTask.executeTask();
            final AppleDEPSyncScheduler DEPScheduler = new AppleDEPSyncScheduler();
            DEPScheduler.executeTask();
            final TrashProfileDeletionTask trashprofile = new TrashProfileDeletionTask();
            trashprofile.executeTask();
            final EnrollmentCleanupTask cleanupTask = new EnrollmentCleanupTask();
            cleanupTask.executeTask();
            this.populateDummyLocation();
            moveMobilesToAwaitingForLicense();
            DocMgmt.getInstance().docScheduledTask();
            ScheduledActionsUtils.removeCommandsFromMdCommandsToDevice();
            final MDMDataBackupRunner locationHistoryBackup = new LocationHistoryDataBackup();
            locationHistoryBackup.initBackupForAllCustomer();
            final ResendEnrollmentRequestTask rt = new ResendEnrollmentRequestTask();
            rt.executeTask();
            new DataUsageUtil().DeleteOlderDataUsageEntriesInDB();
            final IOSEnterpriseAppExpiryCheckerTask iosAppExpiryTask = new IOSEnterpriseAppExpiryCheckerTask();
            iosAppExpiryTask.executeTask();
            new ExpiredAPIFileCleanupTask().executeTask();
            new ExpiredTempUploadedFileCleanUpTask().executeTask();
            new ManagedUserCleanupTask().executeTask(null);
            ExportRequestDetailsHandler.getInstance().markExportRequestsInProgressForMoreThanOneDayAsFailed();
            final Long[] list = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int i = 0; i < list.length; ++i) {
                final Long customerID = list[i];
                ProfileCertificateUtil.getInstance().handleAutoRenewalForCustomer(customerID);
            }
            new BatteryHistoryDeletionTask();
            BatteryHistoryDeletionTask.deleteBatteryDetails();
            MdDeviceWifiSSIDDBHandler.getInstance().wifiSSIDHistoryDeletionTask(CustomerInfoUtil.getInstance().getCustomerIdsFromDB());
            new UserAssignmentRuleHandler().postUserAssignmentSettingsforAllCustomers(Boolean.FALSE);
            final Long[] customerIdsFromDB;
            final Long[] customers = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (final Long customer : customerIdsFromDB) {
                new ThrottlingHandler().evaluateAndUpdateMigrationStatusForCustomer(customer);
            }
            ComputerToDeviceMappingUpdater.getInstance().updateEnrolledDevicesToDC();
            SensitiveColumnDataMigrator.sensitiveDataDeleteInDB();
            AuthTokenUtil.checkForAuthTokenDevices();
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMGlobalTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void moveMobilesToAwaitingForLicense() {
        final String setTime = SyMUtil.getSyMParameter("MDM_ADDON_REMOVAL_TIME");
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable() && setTime != null) {
                if (Long.parseLong(setTime) < SyMUtil.getCurrentTime()) {
                    Logger.getLogger(MDMGlobalTask.class.getName()).log(Level.SEVERE, "Moving extra mobile devices to awating for license status");
                    final JSONObject licenseDetails = LicenseActionListenerImpl.getMDMUsageDetails();
                    final Integer managedMobileDevices = (licenseDetails != null) ? licenseDetails.optInt("managedCount", 0) : 0;
                    final Long managedComputerDevices = (Long)LicenseProvider.getInstance().getDCLicenseAPI().getManagedComputersCount();
                    final Long totalCount = managedComputerDevices + managedMobileDevices;
                    final Long allowedMobileDeviceCount = Long.parseLong(LicenseProvider.getInstance().getMDMLicenseAPI().getPurchasedMobileCount());
                    final Long allowedComputerCount = Long.parseLong(LicenseProvider.getInstance().getDCLicenseAPI().getPurchasedComputerCount());
                    final String mdmLicenseType = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
                    final boolean isDCFree = LicenseProvider.getGeneralLicenseAPI().isFreeLicense();
                    final Boolean isFreeMDMLicense = allowedMobileDeviceCount == 25L && mdmLicenseType.equalsIgnoreCase("Professional");
                    MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", CustomerInfoUtil.getInstance().getCustomerId());
                    if (isFreeMDMLicense && isDCFree) {
                        if (totalCount > 25L) {
                            LicenseActionListenerImpl.moveMDMDevicesForAwaitingForLicense();
                        }
                    }
                    else if (isFreeMDMLicense && totalCount > allowedComputerCount) {
                        LicenseActionListenerImpl.moveMDMDevicesForAwaitingForLicense();
                    }
                    SyMUtil.deleteSyMParameter("MDM_ADDON_REMOVAL_TIME");
                }
                else {
                    MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", CustomerInfoUtil.getInstance().getCustomerId());
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMGlobalTask.class.getName()).log(Level.SEVERE, "Error while moving extra mobile devices to awaiting to license", ex);
        }
    }
    
    private void populateDummyLocation() {
        final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
        final String populateDummyLocation = MDMUtil.getSyMParameter("PopulateDummyLocation");
        if (isDemoMode.equalsIgnoreCase("true") || (populateDummyLocation != null && populateDummyLocation.equalsIgnoreCase("true"))) {
            final DummyLocationDataPopulationTask locationPopulationTask = new DummyLocationDataPopulationTask();
            locationPopulationTask.executeTask();
        }
    }
}
