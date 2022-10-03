package com.me.mdm.server.inv.actions;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import org.json.simple.JSONArray;
import java.util.Set;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import java.util.ArrayList;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.device.resource.Device;

public class AndroidInvActionUtil extends InvActionUtil
{
    @Override
    public InventoryActionList getApplicableActions(final Device device, final Long customerID) {
        final InventoryActionList actionList = new InventoryActionList();
        final List<InventoryAction> actions = new ArrayList<InventoryAction>();
        actionList.actions = actions;
        try {
            JSONObject privacySettings;
            try {
                privacySettings = new PrivacySettingsHandler().getPrivacyDetails(ManagedDeviceHandler.getInstance().getDeviceOwnership(device.getResourceId()), customerID);
            }
            catch (final Exception e) {
                AndroidInvActionUtil.logger.log(Level.SEVERE, "Exception while fetching privacy settings", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(device.getResourceId());
            final boolean hasAccessOutsideContainer = IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.getResourceId());
            final boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final boolean isKnoxLicenseAvailable = KnoxUtil.getInstance().isKnoxLicenseAvailable(customerID);
            final boolean isKnoxEnabled = KnoxUtil.getInstance().isRegisteredAsKnox(device.getResourceId());
            final boolean isLocTrackingEnabledForDevice = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(device.getResourceId());
            final boolean isLocTrackingEnabledForCustomer = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerID);
            final int knoxLicenseStatus = KnoxLicenseHandler.getInstance().shouldAlertForKnoxLicenseExpiry(customerID);
            HashMap knoxDetails = null;
            if (isKnoxEnabled) {
                knoxDetails = KnoxUtil.getInstance().getDeviceKnoxDetails(device.getResourceId());
            }
            final int kioskState = new KioskPauseResumeManager().getDeviceKioskState(device.getResourceId());
            final boolean isRemoteControlCapable = InventoryUtil.getInstance().getRemoteControlCapability(device.getResourceId());
            final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
            for (final String action : ActionConstants.ACTIONS_LIST) {
                final InventoryAction tempAction = new InventoryAction();
                final String s = action;
                switch (s) {
                    case "lock": {
                        if (lostModeStatus != 2 && lostModeStatus != 6 && lostModeStatus != 1) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "scan":
                    case "remote_alarm":
                    case "corporate_wipe": {
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "remote_control": {
                        if (!isProfessional) {
                            break;
                        }
                        if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_RemoteControl_Write" })) {
                            break;
                        }
                        if (privacySettings.getInt("disable_remote_control") == 2 && device.getAgentType() == 3) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.remotecontrol.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            actions.add(tempAction);
                            break;
                        }
                        if (!isRemoteControlCapable && (!isAndroid5AndAbove(device.getOsVersion()) || device.getAgentType() != 3)) {
                            break;
                        }
                        if (!MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerID)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.inv.remotecontrol.not_configured", new Object[] { "/webclient#/uems/mdm/manage/remoteControl" });
                            actions.add(tempAction);
                            break;
                        }
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "remote_view": {
                        if (!isProfessional) {
                            break;
                        }
                        if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_RemoteControl_Write" })) {
                            break;
                        }
                        if (privacySettings.getInt("disable_remote_control") == 2 && device.getAgentType() == 2) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.remotecontrol.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            actions.add(tempAction);
                            break;
                        }
                        if (isRemoteControlCapable || !isAndroid5AndAbove(device.getOsVersion()) || device.getAgentType() != 2) {
                            break;
                        }
                        if (!MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerID)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.inv.remotecontrol.not_configured", new Object[] { "/webclient#/uems/mdm/manage/remoteControl" });
                            actions.add(tempAction);
                            break;
                        }
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "complete_wipe": {
                        if (privacySettings.getInt("disable_wipe") == 2 && lostModeStatus != 2 && lostModeStatus != 1) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.completewipe.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            actions.add(tempAction);
                            break;
                        }
                        if (!device.getProfileowner() || (isKnoxEnabled && isKnoxLicenseAvailable)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "remote_debug": {
                        if (!device.getProfileowner() && device.getSupervised() && new VersionChecker().isGreaterOrEqual(device.getOsVersion(), "7")) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            if (privacySettings.getInt("disable_bug_report") == 2) {
                                tempAction.isEnabled = false;
                                tempAction.remarks = I18N.getMsg("mdm.privacy.bugreport.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            }
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "clear_passcode":
                    case "reset_passcode": {
                        if (lostModeStatus == 2) {
                            break;
                        }
                        if (privacySettings.getInt("disable_clear_passcode") == 2) {
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.profile.passcode.error.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            break;
                        }
                        if (!device.getProfileowner() || (device.getProfileowner() && hasAccessOutsideContainer) || (isKnoxEnabled && isKnoxLicenseAvailable)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "fetch_location": {
                        if (!isGeotrackingFeatureEnabled) {
                            break;
                        }
                        if (privacySettings.getInt("fetch_location") == 2) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.location.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            actions.add(tempAction);
                            break;
                        }
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        if (lostModeStatus == 2 || lostModeStatus == 1) {
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        if (!isLocTrackingEnabledForCustomer) {
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.inv.location.geoloc_disabled", new Object[] { "/webclient#/uems/mdm/inventory/geoTracking" });
                            actions.add(tempAction);
                            break;
                        }
                        if (!isLocTrackingEnabledForDevice) {
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.inv.location.geoloc_disabled_device", new Object[] { "/webclient#/uems/mdm/inventory/geoTracking" });
                            actions.add(tempAction);
                            break;
                        }
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "restart": {
                        if (device.getAgentType() == 3 || (device.getAgentType() == 2 && isAndroid7AndAbove(device.getOsVersion()) && device.getSupervised()) || (isKnoxEnabled && isKnoxLicenseAvailable)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "enable_lost_mode": {
                        if (isGeotrackingFeatureEnabled && lostModeStatus != 2 && lostModeStatus != 1) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "disable_lost_mode": {
                        if (isGeotrackingFeatureEnabled && (lostModeStatus == 2 || lostModeStatus == 1)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "pause_kiosk": {
                        if (kioskState == KioskPauseResumeManager.IN_KIOSK) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "re_apply_kiosk": {
                        if (kioskState == KioskPauseResumeManager.KIOSK_PAUSED) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "clear_app_data": {
                        if (isAndroidXAndAbove(device.getOsVersion(), "9.0") || device.getAgentType() == 3) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                }
            }
            final boolean isContainerActivated = Integer.valueOf(String.valueOf(knoxDetails.get("CONTAINER_STATUS"))) == 20001;
            if (isContainerActivated && isKnoxLicenseAvailable && isKnoxEnabled && !isAndroid10AndAbove(device.getOsVersion()) && !device.getSupervised()) {
                final List<InventoryAction> knoxActions = new ArrayList<InventoryAction>();
                actionList.knoxActions = knoxActions;
                final boolean isActivationContainerFailed = Integer.valueOf(String.valueOf(knoxDetails.get("CONTAINER_STATUS"))) == 20003;
                final boolean isKnoxLicenseExpired = knoxLicenseStatus == 3;
                final boolean isContainerActive = Integer.valueOf(String.valueOf(knoxDetails.get("CONTAINER_STATE"))) == 1;
                final boolean isContainerLocked = Integer.valueOf(String.valueOf(knoxDetails.get("CONTAINER_STATE"))) == 2;
                for (final String action2 : ActionConstants.KNOX_ACTIONS_LIST) {
                    final InventoryAction tempAction2 = new InventoryAction();
                    final String s2 = action2;
                    switch (s2) {
                        case "create_container": {
                            if (!isContainerActivated && isKnoxLicenseAvailable && !isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = true;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = null;
                                tempAction2.localizedActionInfo = null;
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isContainerActivated && isKnoxLicenseAvailable && !isKnoxLicenseExpired) {
                                break;
                            }
                            if (isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.alerts.Knox_license_expired_mail_sub", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.alerts.Knox_license_expired_mail_sub");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("dc.mdm.android.knox.event_log_container_created", device.getDeviceName());
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("dc.mdm.android.knox.event_log_container_created", device.getDeviceName());
                                knoxActions.add(tempAction2);
                                break;
                            }
                            break;
                        }
                        case "remove_container": {
                            if (!isActivationContainerFailed && isKnoxLicenseAvailable && !isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = true;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = null;
                                tempAction2.localizedActionInfo = null;
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isActivationContainerFailed && isKnoxLicenseAvailable && !isKnoxLicenseExpired) {
                                break;
                            }
                            if (isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.alerts.Knox_license_expired_mail_sub", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.alerts.Knox_license_expired_mail_sub");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("dc.mdm.android.knox.eventlog.container_removed", device.getDeviceName());
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("dc.mdm.android.knox.eventlog.container_removed", device.getDeviceName());
                                knoxActions.add(tempAction2);
                                break;
                            }
                            break;
                        }
                        case "lock_container": {
                            if (isContainerActivated && isContainerActive && !isKnoxLicenseExpired && !isContainerLocked) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = true;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = null;
                                tempAction2.localizedActionInfo = null;
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (isContainerActivated && isContainerActive && !isKnoxLicenseExpired) {
                                break;
                            }
                            if (!isContainerActivated && !isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.android.knox.profile.noContainer", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.android.knox.profile.noContainer");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.alerts.Knox_license_expired_mail_sub", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.alerts.Knox_license_expired_mail_sub");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (isContainerLocked) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("dc.mdm.android.knox.locked", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("dc.mdm.android.knox.locked");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            break;
                        }
                        case "unlock_container": {
                            if (isContainerActivated && isContainerLocked && !isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = true;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = null;
                                tempAction2.localizedActionInfo = null;
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (isContainerActivated && isContainerLocked && !isKnoxLicenseExpired) {
                                break;
                            }
                            if (isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.alerts.Knox_license_expired_mail_sub", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.alerts.Knox_license_expired_mail_sub");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isKnoxLicenseExpired && !isContainerActivated) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.android.knox.profile.noContainer", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.android.knox.profile.noContainer");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isContainerLocked) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("dc.mdm.android.knox.unlocked", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("dc.mdm.android.knox.unlocked");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            break;
                        }
                        case "clear_container_password": {
                            if (isContainerActivated && !isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = true;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = null;
                                tempAction2.localizedActionInfo = null;
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (isContainerActivated && !isKnoxLicenseExpired) {
                                break;
                            }
                            if (isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.alerts.Knox_license_expired_mail_sub", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.alerts.Knox_license_expired_mail_sub");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            if (!isKnoxLicenseExpired) {
                                tempAction2.name = action2;
                                tempAction2.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action2));
                                tempAction2.isEnabled = false;
                                tempAction2.statusCode = null;
                                tempAction2.statusDescription = null;
                                tempAction2.localizedStatusDescription = null;
                                tempAction2.actionInfo = APIUtil.getEnglishString("mdm.android.knox.profile.noContainer", new Object[0]);
                                tempAction2.localizedActionInfo = InvActionUtil.getLocalizedString("mdm.android.knox.profile.noContainer");
                                knoxActions.add(tempAction2);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            AndroidInvActionUtil.logger.log(Level.SEVERE, "Exception occured in AndroidInvActionUtil", e2);
        }
        return actionList;
    }
    
    @Override
    public Boolean isCommandApplicable(final JSONObject deviceDatils, final String commandName) {
        try {
            final String osVersion = (String)deviceDatils.get("OS_VERSION");
            final boolean isSupervised = (boolean)deviceDatils.get("IS_SUPERVISED");
            final Integer agentType = (Integer)deviceDatils.get("AGENT_TYPE");
            switch (commandName) {
                case "restart": {
                    return agentType == 3 || (agentType == 2 && isAndroid7AndAbove(osVersion) && isSupervised);
                }
                case "shutdown": {
                    return false;
                }
                case "clear_app_data": {
                    return isAndroidXAndAbove(osVersion, "9.0") || agentType == 3;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            AndroidInvActionUtil.logger.log(Level.SEVERE, "Exception in checking isCommandApplicable", ex);
            return false;
        }
    }
    
    @Override
    public JSONArray getApplicableBulkActionDevices(final Set deviceSet, final String commandName, final Long customerID) {
        SelectQuery deviceQuery = null;
        JSONArray validBulkDeviceArray = new JSONArray();
        try {
            deviceQuery = this.getBulkDeviceQuery(deviceSet, customerID, 2);
            final boolean isKnoxLicenseAvailable = KnoxUtil.getInstance().isKnoxLicenseAvailable(customerID);
            switch (commandName) {
                case "restart": {
                    if (isKnoxLicenseAvailable) {
                        deviceQuery.addJoin(new Join("Resource", "ManagedKNOXContainer", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    }
                    final Criteria agentCriteria1 = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
                    Criteria agentCriteria2 = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)2, 0);
                    agentCriteria2 = agentCriteria2.and(this.getAndroid7AndAboveDevicesCriteria());
                    agentCriteria2 = agentCriteria2.and(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
                    final Criteria overallCriteria = agentCriteria1.or(agentCriteria2);
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(overallCriteria));
                    break;
                }
                case "scheduled_restart": {
                    if (isKnoxLicenseAvailable) {
                        deviceQuery.addJoin(new Join("Resource", "ManagedKNOXContainer", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    }
                    final Criteria agentCriteria1 = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
                    Criteria agentCriteria2 = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)2, 0);
                    agentCriteria2 = agentCriteria2.and(this.getAndroid7AndAboveDevicesCriteria());
                    agentCriteria2 = agentCriteria2.and(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
                    final Criteria overallCriteria = agentCriteria1.or(agentCriteria2);
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(overallCriteria));
                    break;
                }
                case "enable_lost_mode": {
                    final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
                    if (isGeotrackingFeatureEnabled) {
                        deviceQuery.addJoin(new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                        Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)null, 0);
                        statusCriteria = statusCriteria.or(new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1 }, 9));
                        deviceQuery.setCriteria(deviceQuery.getCriteria().and(statusCriteria));
                        break;
                    }
                    return validBulkDeviceArray;
                }
                case "disable_lost_mode": {
                    final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
                    if (isGeotrackingFeatureEnabled) {
                        deviceQuery.addJoin(new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                        final Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1 }, 8);
                        deviceQuery.setCriteria(deviceQuery.getCriteria().and(statusCriteria));
                        break;
                    }
                    return validBulkDeviceArray;
                }
                case "clear_app_data": {
                    final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
                    final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
                    final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
                    final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
                    final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
                    final Criteria agentCriteria3 = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
                    final Criteria osCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria);
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(osCriteria.or(agentCriteria3)));
                    break;
                }
                default: {
                    return validBulkDeviceArray;
                }
            }
            MDMUtil.getInstance();
            validBulkDeviceArray = MDMUtil.executeSelectQuery(deviceQuery);
        }
        catch (final Exception ex) {
            AndroidInvActionUtil.logger.log(Level.SEVERE, "Exception occurred in executeBulkActions");
        }
        return validBulkDeviceArray;
    }
    
    public static boolean isAndroid5AndAbove(final String osVersion) {
        return !osVersion.matches("2.*") && !osVersion.matches("3.*") && !osVersion.matches("4.*");
    }
    
    public static boolean isAndroid7AndAbove(final String osVersion) {
        final VersionChecker versionChecker = new VersionChecker();
        return !osVersion.matches("[^0-9.]") && versionChecker.isGreaterOrEqual(osVersion, "7.0");
    }
    
    public static boolean isAndroidXAndAbove(final String osVersion, final String targetVersion) {
        final VersionChecker versionChecker = new VersionChecker();
        return !osVersion.matches("[^0-9.]") && versionChecker.isGreaterOrEqual(osVersion, targetVersion);
    }
    
    public Criteria getAndroid7AndAboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria);
        return osVersionCriteria;
    }
    
    public static boolean isAndroid10AndAbove(final String osVersion) {
        final VersionChecker versionChecker = new VersionChecker();
        return !osVersion.matches("[^0-9.]") && versionChecker.isGreaterOrEqual(osVersion, "10");
    }
}
