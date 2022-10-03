package com.me.mdm.server.service;

import com.adventnet.sym.server.mdm.apps.AppsLicensesHandlingListener;
import com.adventnet.sym.server.mdm.apps.DeviceAppsLicensesHandlingListener;
import com.adventnet.sym.server.mdm.apps.AppsLicensesHandler;
import com.adventnet.sym.server.mdm.macos.event.MacUserLoginInventoryUpdateListener;
import com.adventnet.sym.server.mdm.macos.event.ComputerUserLoginListener;
import com.adventnet.sym.server.mdm.macos.event.MacDeviceCommandsOnUserChannelListener;
import com.adventnet.sym.server.mdm.macos.event.ComputerUserLoginEventsHandler;
import com.me.mdm.server.geofence.listener.GeoFenceListener;
import com.me.mdm.server.compliance.listener.ComplianceGeofenceListener;
import com.me.mdm.server.geofence.listener.GeoFenceListenerHandler;
import com.me.mdm.server.role.MDMMappedRoleListener;
import com.me.devicemanagement.framework.server.authorization.RoleListener;
import com.me.mdm.server.dep.AdminEnrollmentRoleListener;
import com.me.devicemanagement.framework.server.authorization.RoleListenerHandler;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestListener;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import com.me.mdm.server.enrollment.approval.SelfEnrollmentLimitApprover;
import com.me.mdm.server.enrollment.approval.EnrollmentApprover;
import com.me.mdm.server.enrollment.approval.SecurityGroupUsersApprover;
import com.me.mdm.server.enrollment.approval.EnrollmentApprovalHandler;
import com.me.devicemanagement.framework.server.common.MSPUserListenerImpl;
import com.me.devicemanagement.framework.server.authentication.UserListener;
import com.me.mdm.server.dep.DEPTechnicianUserListener;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import com.me.mdm.server.doc.DocCGListener;
import com.adventnet.sym.server.mdm.group.MDMCustomGroupListner;
import com.me.mdm.server.android.knox.core.KnoxCustomGroupListener;
import com.me.mdm.server.android.knox.core.MDMCustomGroupHandler;
import com.me.mdm.server.license.MDMLanguageLicenseListener;
import com.me.mdm.server.license.MDMEditionLicenseListener;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.me.devicemanagement.framework.server.license.LicenseListenerHandler;
import com.me.mdm.server.settings.location.LocationSettingsCGMemberListener;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyCGMemberListener;
import com.me.mdm.api.command.schedule.ScheduledActionsGroupMemberListener;
import com.me.mdm.server.apps.config.AppConfigPolicyCGMemberListener;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsCGMemberListener;
import com.me.mdm.server.announcement.listener.AnnouncementGroupMemberListner;
import com.me.mdm.server.datausage.DataUsagePolicyCGMemberListner;
import com.me.mdm.server.compliance.listener.ComplianceDistributionGroupMemberListener;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyDistributionCGMemberListner;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyDistributionCGMemberListener;
import com.me.mdm.server.role.ScopeModificationCGMemberListener;
import com.adventnet.sym.server.mdm.apps.AppDistributionCGMemberListener;
import com.adventnet.sym.server.mdm.config.ProfileDistributionCGMemberListener;
import com.adventnet.sym.server.mdm.config.DocCGMemberListener;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;
import com.adventnet.sym.server.mdm.config.MDMGroupMemberCountListener;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.enrollment.EnrollmentRequestUserListener;
import com.me.mdm.server.user.GroupManagedUserListener;
import com.me.mdm.server.enrollment.EnrollmentInvitationUserListener;
import com.adventnet.sym.server.mdm.command.CommandManagedUserListener;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedUserListenerImpl;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.apps.handler.InventoryAppCleanManagedDeviceListener;
import com.adventnet.sym.server.mdm.enroll.DeviceManagedDetailsHistoryListener;
import com.adventnet.sym.server.mdm.core.LicensePercentManagedDeviceListener;
import com.me.mdm.uem.ModernMgmtManagedDeviceListener;
import com.me.mdm.server.tracker.mics.MICSManagedDeviceListener;
import com.me.mdm.server.windows.apps.nativeapp.WindowsNativeAppManagedDeviceListner;
import com.me.mdm.server.privacy.PrivacyManagedDeviceListener;
import com.me.mdm.core.user.UserDeviceAppAssociationListener;
import com.me.mdm.core.enrollment.AutoPickUserDeviceListener;
import com.me.mdm.server.compliance.listener.ComplianceManagedDeviceListener;
import com.adventnet.sym.server.mdm.core.UnmanageDeviceListener;
import com.me.mdm.server.doc.DocDeviceListener;
import com.me.mdm.server.settings.MailServerMessageListener;
import com.adventnet.sym.server.mdm.message.MSPLicenseMessageListener;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.mdmmigration.MigrationGroupManagedDeviceListener;
import com.me.mdm.server.settings.location.LocationSettingsUserAssignedListner;
import com.me.mdm.server.conditionalaccess.AzureWinCEAListener;
import com.me.mdm.server.adep.AwaitingConfigRegisterDeviceListener;
import com.me.mdm.server.apps.blacklist.BlacklistEnrollmentListner;
import com.me.mdm.server.enrollment.unmanage.InactiveDeviceListener;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedDeviceListenerImpl;
import com.adventnet.sym.server.mdm.config.UserProfileAssociationListener;
import com.me.mdm.core.user.UserDeviceProfileAssociationListener;
import com.me.mdm.server.easmanagement.EASListener;
import com.me.mdm.server.notification.DeviceToApnsPortBlockDeviceListener;
import com.adventnet.sym.server.mdm.enroll.MacBootstrapTokenListener;
import com.adventnet.sym.server.mdm.enroll.DeviceCountListener;
import com.adventnet.sym.server.mdm.message.LicenseMessageListener;
import com.me.mdm.apps.handler.AppsDeployManagedDeviceListener;
import com.adventnet.sym.server.mdm.iosnativeapp.IOSNativeAppManagedDeviceListener;
import com.adventnet.sym.server.mdm.group.MDMGroupManagedDeviceListener;
import com.me.mdm.core.enrollment.EnrollmentTemplateDeviceListener;
import com.me.mdm.server.apps.android.afw.GoogleForWorkManagedDeviceListenerImpl;
import com.me.mdm.server.android.knox.core.KnoxManagedDeviceListener;
import com.me.mdm.server.security.passcode.AndroidRecoveryPasscodeMangedDeviceListener;
import com.me.mdm.server.settings.battery.BatterySettingsManagedDeviceListener;
import com.me.mdm.server.settings.location.LocationSettingsManagedDeviceListener;
import com.adventnet.sym.server.mdm.command.CommandManagedDeviceListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;
import com.me.mdm.server.apple.listeners.manageddevice.AppleSharediPadConfigurationManagedDeviceListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.files.CustomerFileCleanupTask;
import com.me.devicemanagement.framework.server.admin.MSPCustomerListenerImpl;
import com.me.devicemanagement.framework.server.customer.CustomerListener;
import com.me.mdm.server.dep.AdminEnrollmentCustomerListener;
import com.me.devicemanagement.framework.server.customer.CustomerHandler;
import com.me.mdm.server.customer.GeneralCustomerListenerMDMImpl;
import com.me.devicemanagement.framework.webclient.alert.EmailTemplateChangeListener;
import com.me.devicemanagement.framework.webclient.alert.EmailTemplateListenerHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.api.APIRequestMapper;
import com.me.mdm.server.easmanagement.EASMgmt;
import java.util.logging.Logger;

public class MDMHandler
{
    private static Logger logger;
    
    public static void initiate() {
        registerMDMCustomerListener();
        registerManagedDeviceListeners();
        registerManagedUserListeners();
        registerCGMemberListeners();
        registerLicenseListeners();
        registerLanguageLiceseListener();
        registerCustomGroupListener();
        registerUserListener();
        registerEnrollmentApprovers();
        registerEnrollmentRequestListeners();
        registerEmailAlertListeners();
        registerMDMRoleListener();
        registerGeoFenceListener();
        EASMgmt.getInstance().handleServerStartStop();
        APIRequestMapper.createRequestMapper();
        registerComputerUserLoginListeners();
        registerAppsLicensesHandlersListeners();
    }
    
    private static void registerEmailAlertListeners() {
        if (CustomerInfoUtil.isSAS) {
            try {
                EmailTemplateListenerHandler.getInstance().addEmailTemplateListener((EmailTemplateChangeListener)Class.forName("com.me.mdmcloud.webclient.alert.EmailAlertChangeParamsListener").newInstance());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void destroy() {
        EASMgmt.getInstance().handleServerStartStop();
    }
    
    private static void registerMDMCustomerListener() {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        CustomerInfoUtil.getInstance();
        final boolean isSAS = CustomerInfoUtil.isSAS();
        if (isMSP || isSAS) {
            final CustomerListener generalCustomerListener = (CustomerListener)new GeneralCustomerListenerMDMImpl();
            CustomerHandler.getInstance().addCustomerListener(generalCustomerListener);
            CustomerHandler.getInstance().addCustomerListener((CustomerListener)new AdminEnrollmentCustomerListener());
            CustomerHandler.getInstance().addCustomerListener((CustomerListener)new MSPCustomerListenerImpl());
            CustomerHandler.getInstance().addCustomerListener((CustomerListener)new CustomerFileCleanupTask());
        }
    }
    
    private static void registerManagedDeviceListeners() {
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AppleSharediPadConfigurationManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new CommandManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new LocationSettingsManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new BatterySettingsManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AndroidRecoveryPasscodeMangedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new KnoxManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new GoogleForWorkManagedDeviceListenerImpl());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new EnrollmentTemplateDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MDMGroupManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new IOSNativeAppManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AppsDeployManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new LicenseMessageListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new DeviceCountListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MacBootstrapTokenListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new DeviceToApnsPortBlockDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new EASListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UserDeviceProfileAssociationListener(1));
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UserProfileAssociationListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new VPPManagedDeviceListenerImpl());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new InactiveDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new BlacklistEnrollmentListner());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AwaitingConfigRegisterDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AzureWinCEAListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new LocationSettingsUserAssignedListner());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MigrationGroupManagedDeviceListener());
        final ManagedDeviceListener managedDeviceListener = MDMApiFactoryProvider.getSDPIntegrationListenerAPI();
        if (managedDeviceListener != null) {
            ManagedDeviceHandler.getInstance().addManagedDeviceListener(managedDeviceListener);
        }
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MSPLicenseMessageListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MailServerMessageListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new DocDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UnmanageDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new ComplianceManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new AutoPickUserDeviceListener());
        try {
            ManagedDeviceHandler.getInstance().addManagedDeviceListener(MDMApiFactoryProvider.getEnrollmentRequestManagedDeviceListener());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UserDeviceAppAssociationListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new PrivacyManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new WindowsNativeAppManagedDeviceListner());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UserDeviceProfileAssociationListener(8));
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new MICSManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new ModernMgmtManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new LicensePercentManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new DeviceManagedDetailsHistoryListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new InventoryAppCleanManagedDeviceListener());
        ManagedDeviceHandler.getInstance().addManagedDeviceListener(new UserDeviceProfileAssociationListener(12));
    }
    
    private static void registerManagedUserListeners() {
        ManagedUserHandler.getInstance().addManagedUserListener(new VPPManagedUserListenerImpl());
        ManagedUserHandler.getInstance().addManagedUserListener(new CommandManagedUserListener());
        ManagedUserHandler.getInstance().addManagedUserListener(new UserProfileAssociationListener());
        ManagedUserHandler.getInstance().addManagedUserListener(new EnrollmentInvitationUserListener());
        ManagedUserHandler.getInstance().addManagedUserListener(new EASListener());
        ManagedUserHandler.getInstance().addManagedUserListener(new GroupManagedUserListener());
        ManagedUserHandler.getInstance().addManagedUserListener(new EnrollmentRequestUserListener());
    }
    
    private static void registerCGMemberListeners() {
        MDMGroupHandler.getInstance().addGroupMemberListener(new MDMGroupMemberCountListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new DocCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new ProfileDistributionCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new AppDistributionCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new ScopeModificationCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new OSUpdatePolicyDistributionCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new BlacklistPolicyDistributionCGMemberListner());
        MDMGroupHandler.getInstance().addGroupMemberListener(new ComplianceDistributionGroupMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new DataUsagePolicyCGMemberListner());
        MDMGroupHandler.getInstance().addGroupMemberListener(new AnnouncementGroupMemberListner());
        MDMGroupHandler.getInstance().addGroupMemberListener(new MDMFeatureSettingsCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new AppConfigPolicyCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new ScheduledActionsGroupMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new AppUpdatePolicyCGMemberListener());
        MDMGroupHandler.getInstance().addGroupMemberListener(new LocationSettingsCGMemberListener());
    }
    
    private static void registerLicenseListeners() {
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new LicenseMessageListener());
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new MSPLicenseMessageListener());
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new MailServerMessageListener());
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new MDMEditionLicenseListener());
    }
    
    private static void registerLanguageLiceseListener() {
        final MDMLanguageLicenseListener languageLicenseListener = new MDMLanguageLicenseListener();
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)languageLicenseListener);
    }
    
    private static void registerCustomGroupListener() {
        MDMCustomGroupHandler.getInstance().addCustomGroupListener(new KnoxCustomGroupListener());
        MDMCustomGroupHandler.getInstance().addCustomGroupListener(new DocCGListener());
    }
    
    private static void registerUserListener() {
        UserListenerHandler.getInstance().addUserListener((UserListener)new DEPTechnicianUserListener());
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                return;
            }
        }
        UserListenerHandler.getInstance().addUserListener((UserListener)new MSPUserListenerImpl());
    }
    
    private static void registerEnrollmentApprovers() {
        EnrollmentApprovalHandler.getInstance().addEnrollmentApprover(new SecurityGroupUsersApprover());
        EnrollmentApprovalHandler.getInstance().addEnrollmentApprover(new SelfEnrollmentLimitApprover());
    }
    
    private static void registerEnrollmentRequestListeners() {
        try {
            EnrollmentRequestHandler.getInstance().addEnrollmentListener(MDMApiFactoryProvider.getInvitationEnrollmentRequestListener());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void registerMDMRoleListener() {
        RoleListenerHandler.getInstance().addRoleListener((RoleListener)new AdminEnrollmentRoleListener());
        RoleListenerHandler.getInstance().addRoleListener((RoleListener)new MDMMappedRoleListener());
    }
    
    private static void registerGeoFenceListener() {
        GeoFenceListenerHandler.getInstance().addGeoFenceListener(new ComplianceGeofenceListener());
    }
    
    private static void registerComputerUserLoginListeners() {
        ComputerUserLoginEventsHandler.getInstance().addUserLoggedInComputerListener(new MacDeviceCommandsOnUserChannelListener());
        ComputerUserLoginEventsHandler.getInstance().addUserLoggedInComputerListener(new MacUserLoginInventoryUpdateListener());
    }
    
    private static void registerAppsLicensesHandlersListeners() {
        AppsLicensesHandler.getInstance().addAppLicenseHandlingListeners(new DeviceAppsLicensesHandlingListener());
    }
    
    static {
        MDMHandler.logger = Logger.getLogger("DCServiceLogger");
    }
}
