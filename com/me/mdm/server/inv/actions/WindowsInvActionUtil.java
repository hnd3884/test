package com.me.mdm.server.inv.actions;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONArray;
import java.util.Set;
import org.json.JSONObject;
import java.util.List;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import java.util.ArrayList;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.device.resource.Device;

public class WindowsInvActionUtil extends InvActionUtil
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
                WindowsInvActionUtil.logger.log(Level.SEVERE, "Exception while fetching privacy settings", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final boolean isKioskProfilePublished = new KioskPauseResumeManager().isKioskProfilePublishedForPlatform(device.getPlatformType());
            final boolean isLocTrackingEnabledForDevice = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(device.getResourceId());
            final boolean isLocTrackingEnabledForCustomer = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerID);
            final boolean isNativeAgent = IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.getResourceId());
            final boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final int modelType = MDMUtil.getInstance().getModelType(device.getResourceId());
            final String osVersion = device.getOsVersion();
            final boolean isWin8AndAbove = !osVersion.matches("7.*") && (!osVersion.matches("8.*") || !osVersion.contains("8.0")) && !osVersion.matches("9.*");
            final boolean isWin810AndAbove = ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(device.getOsVersion());
            final boolean isWin10AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f);
            final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
            for (final String action : ActionConstants.ACTIONS_LIST) {
                final InventoryAction tempAction = new InventoryAction();
                final String s = action;
                switch (s) {
                    case "scan": {
                        if (isWin810AndAbove) {
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
                    case "lock":
                    case "remote_alarm":
                    case "reset_passcode": {
                        if (isWin8AndAbove && modelType == 2) {
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
                    case "complete_wipe": {
                        if (privacySettings.getInt("disable_wipe") == 2) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.privacy.completewipe.privacy_error", new Object[] { "/webclient#/uems/mdm/admin/privacy/deviceprivacy/dpdetails" });
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
                        if (isWin10AndAbove || (isWin8AndAbove && isNativeAgent && device.getAgentVersion().startsWith("9.2."))) {
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
                    case "restart": {
                        if (isWin10AndAbove) {
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
                        if (isKioskProfilePublished) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = false;
                            tempAction.remarks = I18N.getMsg("mdm.kiosk.roadmap_text_windows", new Object[] { UrlReplacementUtil.replaceUrlAndAppendTrackCode("$(mdmUrl)/product-roadmap-add-details.html$(traceurl)&id=41") });
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        catch (final Exception e2) {
            WindowsInvActionUtil.logger.log(Level.SEVERE, "Exception occured in WindowsInvActionUtil", e2);
        }
        return actionList;
    }
    
    @Override
    public Boolean isCommandApplicable(final JSONObject deviceDatils, final String commandName) {
        try {
            final String osVersion = (String)deviceDatils.get("OS_VERSION");
            final boolean isWin10AndAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f);
            switch (commandName) {
                case "restart": {
                    return isWin10AndAbove;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            WindowsInvActionUtil.logger.log(Level.SEVERE, "Exception in checking isCommandApplicable", ex);
            return false;
        }
    }
    
    @Override
    public JSONArray getApplicableBulkActionDevices(final Set deviceSet, final String commandName, final Long customerID) {
        JSONArray validBulkDeviceArray = new JSONArray();
        try {
            final SelectQuery deviceQuery = this.getBulkDeviceQuery(deviceSet, customerID, 3);
            switch (commandName) {
                case "restart": {
                    final Criteria winCriteria = deviceQuery.getCriteria().and(this.getWindows10AndAboveDevicesCriteria());
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(winCriteria));
                    MDMUtil.getInstance();
                    validBulkDeviceArray = MDMUtil.executeSelectQuery(deviceQuery);
                    break;
                }
                default: {
                    return validBulkDeviceArray;
                }
            }
        }
        catch (final Exception e) {
            WindowsInvActionUtil.logger.log(Level.SEVERE, "Exception occurred in getApplicableBulkActionDevices");
        }
        return validBulkDeviceArray;
    }
    
    private Criteria getWindows10AndAboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria osVersion9Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.*", 3);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria).and(osVersion9Criteria);
        return osVersionCriteria;
    }
}
