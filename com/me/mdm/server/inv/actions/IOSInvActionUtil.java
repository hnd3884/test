package com.me.mdm.server.inv.actions;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONArray;
import java.util.Set;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.me.mdm.server.enrollment.ios.AppleAccessRightsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.apps.IOSAppVersionChecker;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import java.util.ArrayList;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.device.resource.Device;

public class IOSInvActionUtil extends InvActionUtil
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
                IOSInvActionUtil.logger.log(Level.SEVERE, "Exception while fetching privacy settings", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final boolean isKioskProfilePublished = new KioskPauseResumeManager().isKioskProfilePublishedForPlatform(device.getPlatformType());
            final boolean isLocTrackingEnabledForDevice = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(device.getResourceId());
            final boolean isLocTrackingEnabledForCustomer = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerID);
            final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(device.getResourceId());
            final boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final int modelType = MDMUtil.getInstance().getModelTypeFromDB(device.getResourceId());
            final boolean isIOS = modelType == 0 || modelType == 1 || modelType == 2;
            final boolean isMacOS = modelType == 3 || modelType == 4;
            final boolean isTVOs = modelType == 5;
            final boolean isIOS11AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(device.getOsVersion(), 11.0f) && isIOS;
            final boolean isIOS10x3AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(device.getOsVersion(), 10.3f) && isIOS;
            final boolean isMac10x13AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(device.getOsVersion(), 10.13f) && isMacOS;
            final boolean isMac10x13x6AndAbove = isMacOS && new IOSAppVersionChecker().isVersionNumberGreater(device.getOsVersion(), "10.13.6");
            final boolean isTvOS10x2AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(device.getOsVersion(), 10.2f) && isTVOs;
            final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
            for (final String action : ActionConstants.ACTIONS_LIST) {
                final InventoryAction tempAction = new InventoryAction();
                final String s = action;
                switch (s) {
                    case "scan": {
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "lock": {
                        if (modelType != 5 && lostModeStatus != 2 && lostModeStatus != 6 && lostModeStatus != 1) {
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
                    case "unlock_user_account": {
                        if (modelType == 3 || modelType == 4) {
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
                    case "rotate_filevault_personal_key": {
                        if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_EncryptionMgmt_Admin", "ModernMgmt_EncryptionMgmt_Admin" }) && (modelType == 3 || modelType == 4) && MDMFilevaultUtils.isFilevaultEnabled(device.getResourceId()) && MDMFilevaultUtils.isPersonalRecoveryKeyAvailable(device.getResourceId())) {
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
                    case "remote_view": {
                        if (!isProfessional || !apiUtil.checkRolesForCurrentUser(new String[] { "MDM_RemoteControl_Write" })) {
                            break;
                        }
                        if (!isIOS) {
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
                        if (!isIOS11AndAbove) {
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
                        final Integer accessRights = AppleAccessRightsHandler.getInstance().getAccessRightsForResourceId(device.getResourceId());
                        if (!AppleAccessRightsHandler.isAccessRightProvided("DEVICE_ERASE", accessRights)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.completewipe.access_right_error", new Object[] { "https://manageengine.com/mobile-device-management/mdm-apple-byod-privacy-settings.html" });
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
                    case "clear_passcode": {
                        if (lostModeStatus == 2) {
                            IOSInvActionUtil.logger.log(Level.INFO, "Lostmode is enabled , so can''t clear passcode now. resourceID:{0}", device.getResourceId());
                            break;
                        }
                        if (isIOS) {
                            final JSONObject notificationHandler = PushNotificationHandler.getInstance().getNotificationDetails(device.getResourceId(), 1);
                            final String unlockToken = notificationHandler.optString("UNLOCK_TOKEN_ENCRYPTED");
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            if (MDMStringUtils.isEmpty(unlockToken)) {
                                IOSInvActionUtil.logger.log(Level.INFO, "Device doesn''t have unlock token so clear passcode is disabled , resourceID:{0}", device.getResourceId());
                                tempAction.isEnabled = false;
                                tempAction.remarks = I18N.getMsg("mdm.profile.passcode.error.clearFailed", new Object[] { UrlReplacementUtil.replaceUrlAndAppendTrackCode("$(mdmUrl)/kb/mdm-ios-13-update-impacts.html") });
                            }
                            else if (privacySettings.getInt("disable_clear_passcode") == 2) {
                                tempAction.isEnabled = false;
                                tempAction.remarks = I18N.getMsg("mdm.profile.passcode.error.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
                            }
                            else {
                                tempAction.isEnabled = true;
                                tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                                tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                                tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            }
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "fetch_location": {
                        if (!isGeotrackingFeatureEnabled || !isIOS) {
                            IOSInvActionUtil.logger.log(Level.INFO, "Not showing fetch location isGeotrackingFeatureEnabled:{0} ,isMac{1},resourceID:{2}", new Object[] { isGeotrackingFeatureEnabled, isMacOS, device.getResourceId() });
                            break;
                        }
                        if (device.getAgentVersionCode().compareTo("1402") > 0 || isMac10x13x6AndAbove) {
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
                        else {
                            if (!IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.getResourceId())) {
                                tempAction.name = action;
                                tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                                tempAction.isEnabled = false;
                                tempAction.actionInfo = APIUtil.getEnglishString("dc.mdm.geoLoc.find_my_phone.lostmode.memdmapp_notinstalled", new Object[0]);
                                tempAction.localizedActionInfo = InvActionUtil.getLocalizedString("dc.mdm.geoLoc.find_my_phone.lostmode.memdmapp_notinstalled");
                                actions.add(tempAction);
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case "shutdown": {
                        if (isTVOs) {
                            break;
                        }
                    }
                    case "restart": {
                        if (isMac10x13AndAbove || (device.getSupervised() && (isIOS10x3AndAbove || isTvOS10x2AndAbove))) {
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
                        if (isGeotrackingFeatureEnabled && lostModeStatus != 2 && lostModeStatus != 1 && lostModeStatus != 6 && isIOS) {
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
                    case "remote_alarm": {
                        if ((lostModeStatus == 2 || lostModeStatus == 1) && isIOS10x3AndAbove && device.getSupervised()) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), "PlayLostModeSound");
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "disable_lost_mode": {
                        if (isGeotrackingFeatureEnabled && (lostModeStatus == 2 || lostModeStatus == 1 || lostModeStatus == 6)) {
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
                        if (isKioskProfilePublished && !isMacOS) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.kiosk.roadmap_text_ios", new Object[] { UrlReplacementUtil.replaceUrlAndAppendTrackCode("$(mdmUrl)/product-roadmap-add-details.html$(traceurl)&id=41") });
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "logout_user": {
                        if (device.getMultiUser() && device.getModelType() == 2) {
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
        }
        catch (final Exception e2) {
            IOSInvActionUtil.logger.log(Level.SEVERE, "Exception occured in IOSInvActionUtil", e2);
        }
        return actionList;
    }
    
    @Override
    public Boolean isCommandApplicable(final JSONObject deviceDatils, final String commandName) {
        try {
            final Integer modelType = (Integer)deviceDatils.get("MODEL_TYPE");
            final String osVersion = (String)deviceDatils.get("OS_VERSION");
            final boolean isSupervised = (boolean)deviceDatils.get("IS_SUPERVISED");
            final boolean isIOS = modelType == 0 || modelType == 1 || modelType == 2;
            final boolean isMacOS = modelType == 3 || modelType == 4;
            final boolean isTVOs = modelType == 5;
            final boolean isIOS11AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 11.0f) && isIOS;
            final boolean isIOS10x3AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.3f) && isIOS;
            final boolean isMac10x13AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.13f) && isMacOS;
            final boolean isMac10x13x6AndAbove = isMacOS && new IOSAppVersionChecker().isVersionNumberGreater(osVersion, "10.13.6");
            final boolean isTvOS10x2AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.2f) && isTVOs;
            switch (commandName) {
                case "restart":
                case "shutdown": {
                    return isSupervised && (isTVOs || isIOS10x3AndAbove || isMac10x13AndAbove);
                }
                default: {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            IOSInvActionUtil.logger.log(Level.SEVERE, "Exception in checking isCommandApplicable", ex);
            return false;
        }
    }
    
    @Override
    public JSONArray getApplicableBulkActionDevices(final Set deviceSet, final String commandName, final Long customerID) {
        SelectQuery deviceQuery = null;
        JSONArray validBulkDeviceArray = new JSONArray();
        try {
            deviceQuery = this.getBulkDeviceQuery(deviceSet, customerID, 1);
            switch (commandName) {
                case "restart":
                case "shutdown": {
                    final Criteria iosCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Object[] { 0, 1, 2 }, 8);
                    final Criteria tvOSCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
                    final Criteria macOSCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Object[] { 3, 4 }, 8);
                    final Criteria supervisedCriteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
                    final Criteria allCriteria = supervisedCriteria.and(tvOSCriteria.or(this.getIos10_3AboveDevicesCriteria().and(iosCriteria)).or(this.getIos10_13AboveDevicesCriteria().and(macOSCriteria)));
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(allCriteria));
                    break;
                }
                case "enable_lost_mode": {
                    final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
                    final boolean isNotSupportedDevicesLostModeEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowNotSupportedDevicesLostMode");
                    if (isGeotrackingFeatureEnabled) {
                        deviceQuery.addJoin(new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                        Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)null, 0);
                        statusCriteria = statusCriteria.or(new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1, 6 }, 9));
                        final Criteria iosCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Object[] { 0, 1, 2 }, 8);
                        final Criteria supervisedCriteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
                        Criteria overallCriteria = statusCriteria.and(iosCriteria);
                        if (!isNotSupportedDevicesLostModeEnabled) {
                            overallCriteria = overallCriteria.and(supervisedCriteria);
                        }
                        overallCriteria = deviceQuery.getCriteria().and(overallCriteria);
                        deviceQuery.setCriteria(deviceQuery.getCriteria().and(overallCriteria));
                        break;
                    }
                    return validBulkDeviceArray;
                }
                case "disable_lost_mode": {
                    final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
                    if (isGeotrackingFeatureEnabled) {
                        deviceQuery.addJoin(new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                        final Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1, 6 }, 8);
                        deviceQuery.setCriteria(deviceQuery.getCriteria().and(statusCriteria));
                        break;
                    }
                    return validBulkDeviceArray;
                }
                default: {
                    return validBulkDeviceArray;
                }
            }
            MDMUtil.getInstance();
            validBulkDeviceArray = MDMUtil.executeSelectQuery(deviceQuery);
        }
        catch (final Exception e) {
            IOSInvActionUtil.logger.log(Level.SEVERE, "Exception occured in getApplicableBulkActionDevices");
        }
        return validBulkDeviceArray;
    }
    
    public Criteria getIos10_3AboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria osVersion9Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.*", 3);
        final Criteria osVersion10Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"10.0", 3);
        final Criteria osVersion10X1Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"10.1", 3);
        final Criteria osVersion10X2Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"10.2", 3);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria).and(osVersion9Criteria).and(osVersion10Criteria).and(osVersion10X1Criteria).and(osVersion10X2Criteria);
        return osVersionCriteria;
    }
    
    public Criteria getIos10_13AboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria osVersion9Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.*", 3);
        final Object[] osVersion10X13 = { "10.0", "10.1", "10.2", "10.3", "10.4", "10.5", "10.6", "10.7", "10.8", "10.9", "10.10", "10,11", "10.12" };
        final Criteria osVersion10X1Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)osVersion10X13, 13);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria).and(osVersion9Criteria).and(osVersion10X1Criteria);
        return osVersionCriteria;
    }
}
