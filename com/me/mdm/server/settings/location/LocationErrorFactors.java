package com.me.mdm.server.settings.location;

import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAgentSettingsHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Logger;
import java.util.HashMap;

public class LocationErrorFactors
{
    private Long resourceId;
    private Long customerId;
    private int trackingStatus;
    private int lostModeStatus;
    private int platform;
    private boolean isLocationTrackingEnabledForThisDevice;
    private boolean isDeviceInLostMode;
    private HashMap locationErrorMap;
    private HashMap locationMap;
    private String isErrorOnRenderedMap;
    private String isDeviceNeedInitLostModeFromMap;
    private String geoErrorTitle;
    private String geoErrorDescription;
    private String footerData;
    private Logger logger;
    
    private LocationErrorFactors() {
        this.isErrorOnRenderedMap = "isErrorOnRenderedMap";
        this.isDeviceNeedInitLostModeFromMap = "isDeviceNeedInitLostModeFromMap";
        this.geoErrorTitle = "geoErrorTitle";
        this.geoErrorDescription = "geoErrorDescription";
        this.footerData = "footerData";
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public LocationErrorFactors(final Long resourceId, final Long customerId) {
        this.isErrorOnRenderedMap = "isErrorOnRenderedMap";
        this.isDeviceNeedInitLostModeFromMap = "isDeviceNeedInitLostModeFromMap";
        this.geoErrorTitle = "geoErrorTitle";
        this.geoErrorDescription = "geoErrorDescription";
        this.footerData = "footerData";
        this.logger = Logger.getLogger("MDMLogger");
        this.resourceId = resourceId;
        this.customerId = customerId;
        final LocationSettingsDataHandler locationSettingsDataHandler = new LocationSettingsDataHandler();
        this.trackingStatus = locationSettingsDataHandler.getLocationTrackingStatus(customerId);
        this.isLocationTrackingEnabledForThisDevice = locationSettingsDataHandler.isLocationTrackingEnabledforDevice(resourceId);
        this.platform = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
        this.locationErrorMap = MDMGeoLocationHandler.getInstance().getLocationErrorMap(resourceId);
        this.locationMap = MDMGeoLocationHandler.getInstance().getRecentDeviceLocationDetails(resourceId);
        final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
        this.lostModeStatus = lostModeDataHandler.getLostModeStatus(resourceId);
        this.isDeviceInLostMode = lostModeDataHandler.isDeviceInLostMode(resourceId);
    }
    
    public HashMap getDeviceGeoTrackingErrorRenderingProperties() {
        final HashMap renderData = new HashMap();
        try {
            final JSONObject geoRendrerError = this.getGeoRenderError();
            if (geoRendrerError.length() > 0) {
                renderData.put(this.geoErrorTitle, geoRendrerError.optString(this.geoErrorTitle));
                renderData.put(this.geoErrorDescription, geoRendrerError.optString(this.geoErrorDescription));
                if (geoRendrerError.has(this.isErrorOnRenderedMap)) {
                    renderData.put(this.isErrorOnRenderedMap, geoRendrerError.optBoolean(this.isErrorOnRenderedMap));
                }
                if (geoRendrerError.has(this.isDeviceNeedInitLostModeFromMap)) {
                    renderData.put(this.isDeviceNeedInitLostModeFromMap, geoRendrerError.optBoolean(this.isDeviceNeedInitLostModeFromMap));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getDeviceGeoTrackingRenderingProperties", ex);
        }
        return renderData;
    }
    
    private JSONObject getGeoRenderError() {
        JSONObject geoErr = new JSONObject();
        try {
            if (this.trackingStatus == 3) {
                geoErr = this.buildGeoErrObj(geoErr, "dc.mdm.geoLoc.find_my_phone.status.disabled_title", "dc.mdm.geoLoc.find_my_phone.status.disabled_description");
                geoErr.put(this.isErrorOnRenderedMap, false);
                geoErr.put(this.isDeviceNeedInitLostModeFromMap, true);
                return geoErr;
            }
            if (this.platform == 1) {
                final Boolean isMac = MDMUtil.getInstance().isMacDevice(this.resourceId);
                if (!isMac) {
                    return this.getiOSErrorMessages(geoErr);
                }
                return this.getMacErrorMessage(geoErr);
            }
            else if (this.platform == 2) {
                return this.getAndroidErrorMessage(geoErr);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getGeoRenderError", ex);
        }
        return geoErr;
    }
    
    public JSONObject getAndroidErrorMessage(JSONObject geoErrorData) {
        final boolean isLocationError = this.isLocationError();
        final boolean isLocationAdded = this.isLocationAdded();
        try {
            if (this.trackingStatus == 2 && (this.lostModeStatus == -1 || this.lostModeStatus == 5)) {
                geoErrorData.put(this.isErrorOnRenderedMap, false);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, false);
                geoErrorData = this.buildGeoErrObj(geoErrorData, "", "");
            }
            else if (this.trackingStatus == 1 && (this.lostModeStatus == -1 || this.lostModeStatus == 5) && !this.isLocationTrackingEnabledForThisDevice && !this.isDeviceInLostMode) {
                geoErrorData.put(this.isErrorOnRenderedMap, false);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, false);
                geoErrorData = this.buildGeoErrObj(geoErrorData, "", "");
            }
            else if (this.trackingStatus == 1 && isLocationError && !this.isDeviceInLostMode) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "mdm.lm.devicelocenabled");
                String description = geoErrorData.optString(this.geoErrorDescription, "");
                final String mdmUrl = ProductUrlLoader.getInstance().getValue("mdmUrl");
                final String trackignCode = ProductUrlLoader.getInstance().getValue("trackingcode");
                final Object did = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
                final String helpUrl = mdmUrl + "/help/security_management/location_tracking.html?" + trackignCode + "&did=" + did.toString() + "#Enabling_Location_Services_On_Android_Devices";
                final String helpLink = " <a class=\"bodytext\" target=\"_blank\" href=\"" + helpUrl + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
                final String helpUrlToPreventLocInDevice = mdmUrl + "/how-to/mdm-prevent-user-disable-location-services.html?" + trackignCode + "&did=" + did + "&pgSrc=androidPerDevicePage";
                final String descToPreventLocInDevice = I18N.getMsg("dc.mdm.geoLoc_prevent_users_from_turning_off_location_desc", new Object[] { helpUrlToPreventLocInDevice });
                description = description + helpLink + "<br>" + descToPreventLocInDevice;
                geoErrorData.put(this.geoErrorDescription, (Object)description);
            }
            else if (this.lostModeStatus == 1) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.error.awaiting_activation_lostmode", "mdm.lm.error.awaitinglostmodedesc");
            }
            else if ((this.lostModeStatus == 2 || this.lostModeStatus == 4) && isLocationError) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                geoErrorData = this.buildGeoErrObj(geoErrorData, this.locationErrorMap.get("SHORT_DESC"), this.locationErrorMap.get("DETAILED_DESC"));
                String desciption = geoErrorData.optString(this.geoErrorDescription, "");
                final String helpLink2 = "<a class=\"bodytext\" target=\"_blank\" href=\"" + this.locationErrorMap.get("KB_URL") + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
                desciption += helpLink2;
                geoErrorData.put(this.geoErrorDescription, (Object)desciption);
            }
            else if (this.lostModeStatus == 2 && !isLocationAdded) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                geoErrorData = this.buildGeoErrObj(geoErrorData, "mdm.lostmode.TEXT", "mdm.lostmode.findcurrloc");
            }
            else if (isLocationError) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                geoErrorData = this.buildGeoErrObj(geoErrorData, this.locationErrorMap.get("SHORT_DESC"), this.locationErrorMap.get("DETAILED_DESC"));
                String desciption = geoErrorData.optString(this.geoErrorDescription, "");
                final String helpLink2 = "<a class=\"bodytext\" target=\"_blank\" href=\"" + this.locationErrorMap.get("KB_URL") + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
                desciption += helpLink2;
                geoErrorData.put(this.geoErrorDescription, (Object)desciption);
            }
            else if (!isLocationAdded) {
                geoErrorData.put(this.isErrorOnRenderedMap, true);
                geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
                final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(this.resourceId);
                final String title = I18N.getMsg("dc.mdm.geoLoc_device_unable_title", new Object[] { deviceName });
                geoErrorData = this.buildGeoErrObj(geoErrorData, title, "dc.mdm.geoLoc_device_unable_desc");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidErrorMessage", ex);
        }
        return geoErrorData;
    }
    
    private JSONObject getiOSErrorMessages(JSONObject geoErrorData) throws Exception {
        final boolean isLocationError = this.isLocationError();
        final boolean isLocationAdded = this.isLocationAdded();
        final boolean isLostModeComptSuperVised = ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(this.resourceId);
        final boolean iOSNativeAgentEnabled = IosNativeAgentSettingsHandler.getInstance().isIOSNativeAgentEnable(this.customerId);
        final boolean isIOSNativeInstalled = new IOSAppUtils().isNativeAppInstalledInDevice(this.resourceId, false);
        final boolean isIOSNativeAppRegistered = IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(this.resourceId);
        if (this.trackingStatus == 2 && !isLostModeComptSuperVised && !iOSNativeAgentEnabled) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "dc.mdm.geoLoc.findMyPhone.prereq.mdmappInstalled");
        }
        else if (this.trackingStatus == 2 && !isLostModeComptSuperVised && !isIOSNativeInstalled && !isIOSNativeAppRegistered) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "dc.mdm.geoLoc.findMyPhone.prereq.mdmappInstalled");
            final String mdmUrl = ProductUrlLoader.getInstance().getValue("mdmUrl");
            final String trackignCode = ProductUrlLoader.getInstance().getValue("trackingcode");
            final Object didObj = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
            final String did = didObj.toString();
            final String helpUrl = mdmUrl + "/how-to/silent-installation-ios-apps.html?" + trackignCode + "&did=" + did;
            final String link = I18N.getMsg("dc.mdm.geoLoc.findMyPhone.prereq.mdmappInstalled", new Object[0]) + " " + I18N.getMsg("mdm.msg.silentDistribution_memdmapp", new Object[] { "<a href=" + helpUrl + ">", "</a>" });
            geoErrorData.put(this.geoErrorDescription, (Object)link);
        }
        else if (this.trackingStatus == 2 && !isLostModeComptSuperVised && isLocationError) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "mdm.lm.devicelocenabled");
            String description = geoErrorData.optString(this.geoErrorDescription, "");
            final String mdmUrl2 = ProductUrlLoader.getInstance().getValue("mdmUrl");
            final String trackignCode2 = ProductUrlLoader.getInstance().getValue("trackingcode");
            final Object didObj2 = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
            final String did2 = didObj2.toString();
            final String helpUrl2 = mdmUrl2 + "/help/security_management/location_tracking.html?" + trackignCode2 + "&did=" + did2 + "#Enabling_Location_Tracking_for_iOS_devices";
            final String helpLink = "<a class=\"bodytext\" target=\"_blank\" href=\"" + helpUrl2 + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
            description += helpLink;
            geoErrorData.put(this.geoErrorDescription, (Object)description);
        }
        else if (this.trackingStatus == 2 && (this.lostModeStatus == -1 || this.lostModeStatus == 5)) {
            geoErrorData.put(this.isErrorOnRenderedMap, false);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, false);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "", "");
        }
        else if (this.trackingStatus == 1 && isLostModeComptSuperVised && !this.isLocationTrackingEnabledForThisDevice && (this.lostModeStatus == -1 || this.lostModeStatus == 5) && !this.isDeviceInLostMode) {
            geoErrorData.put(this.isErrorOnRenderedMap, false);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, false);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "", "");
        }
        else if (this.trackingStatus == 1 && !isLostModeComptSuperVised && !this.isLocationTrackingEnabledForThisDevice && !isIOSNativeInstalled && !isIOSNativeAppRegistered && !this.isDeviceInLostMode) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.lostmode.devicelocation_not_displayed", "dc.mdm.geoLoc.find_my_phone.lostmode.devicelocation_not_displayed_details");
            geoErrorData.put(this.footerData, (Object)I18N.getMsg("dc.mdm.geoLoc.find_my_phone.lostmode.devicelocation_not_displayed_details_footer", new Object[0]));
        }
        else if (this.trackingStatus == 1 && !isIOSNativeInstalled && !isIOSNativeAppRegistered && !this.isDeviceInLostMode) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "dc.mdm.geoLoc.findMyPhone.prereq.mdmappInstalled");
            final String mdmUrl = ProductUrlLoader.getInstance().getValue("mdmUrl");
            final String trackignCode = ProductUrlLoader.getInstance().getValue("trackingcode");
            final Object didObj = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
            final String did = didObj.toString();
            final String helpUrl = mdmUrl + "/how-to/silent-installation-ios-apps.html?" + trackignCode + "&did=" + did;
            final String link = I18N.getMsg("dc.mdm.geoLoc.findMyPhone.prereq.mdmappInstalled", new Object[0]) + " " + I18N.getMsg("mdm.msg.silentDistribution_memdmapp", new Object[] { "<a href=" + helpUrl + ">", "</a>" });
            geoErrorData.put(this.geoErrorDescription, (Object)link);
        }
        else if (this.trackingStatus == 1 && isLocationError && !this.isDeviceInLostMode) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", "mdm.lm.devicelocenabled");
            String description = geoErrorData.optString(this.geoErrorDescription, "");
            final String mdmUrl2 = ProductUrlLoader.getInstance().getValue("mdmUrl");
            final String trackignCode2 = ProductUrlLoader.getInstance().getValue("trackingcode");
            final Object didObj2 = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
            final String did2 = didObj2.toString();
            final String helpUrl2 = mdmUrl2 + "/help/security_management/location_tracking.html?" + trackignCode2 + "&did=" + did2 + "#Enabling_Location_Tracking_for_iOS_devices";
            final String helpLink = "<a class=\"bodytext\" target=\"_blank\" href=\"" + helpUrl2 + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
            final String helpUrlToPreventLocInDevice = mdmUrl2 + "/how-to/mdm-prevent-user-disable-location-services.html?" + trackignCode2 + "&did=" + did2 + "&pgSrc=iOSPerDevicePage";
            final String descToPreventLocInDevice = I18N.getMsg("dc.mdm.geoLoc_prevent_users_from_turning_off_location_desc", new Object[] { helpUrlToPreventLocInDevice });
            description = description + helpLink + "<br>" + descToPreventLocInDevice;
            geoErrorData.put(this.geoErrorDescription, (Object)description);
        }
        else if (this.trackingStatus == 1 && !isLostModeComptSuperVised && !this.isLocationTrackingEnabledForThisDevice && ((this.lostModeStatus == -1 || this.lostModeStatus == 5) & !this.isDeviceInLostMode)) {
            geoErrorData.put(this.isErrorOnRenderedMap, false);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, false);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "", "");
        }
        else if (this.lostModeStatus == 1) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.error.awaiting_activation_lostmode", "mdm.lm.error.awaitinglostmodedesc");
        }
        else if ((this.lostModeStatus == 2 || this.lostModeStatus == 4) && !isLostModeComptSuperVised && !isIOSNativeInstalled && !isIOSNativeAppRegistered) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.lostmode_enabled", "dc.mdm.geoLoc.find_my_phone.lostmode.memdmapp_notinstalled");
        }
        else if ((this.lostModeStatus == 2 || this.lostModeStatus == 4) && !isLostModeComptSuperVised && !isIOSNativeAppRegistered) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.lostmode_enabled", "dc.mdm.geoLoc.find_my_phone.lostmode.devicelocationservice_unavailable");
        }
        else if ((this.lostModeStatus == 2 || this.lostModeStatus == 4) && isLocationError) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, this.locationErrorMap.get("SHORT_DESC"), this.locationErrorMap.get("DETAILED_DESC"));
            String description = geoErrorData.optString(this.geoErrorDescription, "");
            final String helpLink2 = "<a class=\"bodytext\" target=\"_blank\" href=\"" + this.locationErrorMap.get("KB_URL") + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
            description += helpLink2;
            geoErrorData.put(this.geoErrorDescription, (Object)description);
            geoErrorData.put(this.footerData, (Object)I18N.getMsg("dc.mdm.geoLoc.find_my_phone.when_lost.track_always_config_desc", new Object[] { "#/uems/mdm/inventory/geoTracking" }));
        }
        else if (this.lostModeStatus == 2 && isLocationAdded) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "mdm.lostmode.TEXT", "mdm.lostmode.findcurrloc");
            geoErrorData.optString(this.geoErrorDescription, "");
        }
        else if (this.lostModeStatus == 3) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.lostmode.failed", "dc.mdm.geoLoc.find_my_phone.lostmode.failed.locked_device");
        }
        else if (isLocationError) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, this.locationErrorMap.get("SHORT_DESC"), this.locationErrorMap.get("DETAILED_DESC"));
            String desciption = geoErrorData.optString(this.geoErrorDescription, "");
            final String helpLink2 = "<a class=\"bodytext\" target=\"_blank\" href=\"" + this.locationErrorMap.get("KB_URL") + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
            desciption += helpLink2;
            geoErrorData.put(this.geoErrorDescription, (Object)desciption);
        }
        else if (!isLocationAdded) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(this.resourceId);
            final String title = I18N.getMsg("dc.mdm.geoLoc_device_unable_title", new Object[] { deviceName });
            geoErrorData = this.buildGeoErrObj(geoErrorData, title, "dc.mdm.geoLoc_device_unable_desc");
        }
        return geoErrorData;
    }
    
    private JSONObject getMacErrorMessage(JSONObject geoErrorData) throws Exception {
        final boolean isLocationError = this.isLocationError();
        final boolean isLocationAdded = this.isLocationAdded();
        final String mdmUrl = ProductUrlLoader.getInstance().getValue("mdmUrl");
        final boolean isNativeAppInstalled = new IOSAppUtils().isNativeAppInstalledInDevice(this.resourceId, true);
        final Boolean isTrackingAlways = this.trackingStatus == 1;
        final Boolean isTrackingWhenLost = this.trackingStatus == 2;
        if (isTrackingWhenLost) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            final String helpURL = mdmUrl + "/help/security_management/location_tracking.html#Enabling_location_tracking_Mac_devices";
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", I18N.getMsg("dc.mdm.geoLoc.findMyMac.track.always", new Object[] { helpURL }) + "\n" + I18N.getMsg("dc.mdm.geoLoc.modify.link", new Object[] { "#/uems/mdm/inventory/geoTracking" }));
        }
        else if (!isNativeAppInstalled && isTrackingAlways) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            final String helpUrl = mdmUrl + "/help/security_management/location_tracking.html#Enabling_location_tracking_Mac_devices";
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", I18N.getMsg("dc.mdm.geoLoc.findMyMac.prereq.mdmappInstalled", new Object[] { helpUrl }));
            final String trackignCode = ProductUrlLoader.getInstance().getValue("trackingcode");
            final Object didObj = ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
            final String did = didObj.toString();
            final String link = I18N.getMsg("dc.mdm.geoLoc.findMyMac.prereq.mdmappInstalled", new Object[] { helpUrl }) + "\n" + I18N.getMsg("dc.mdm.geoLoc.distribute.app", new Object[] { "#/uems/mdm/enrollment/ios/memdmSettings" });
            geoErrorData.put(this.geoErrorDescription, (Object)link);
        }
        else if (isLocationError) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            geoErrorData = this.buildGeoErrObj(geoErrorData, this.locationErrorMap.get("SHORT_DESC"), this.locationErrorMap.get("DETAILED_DESC"));
            String desciption = geoErrorData.optString(this.geoErrorDescription, "");
            final String helpLink = "<a class=\"bodytext\" target=\"_blank\" href=\"" + this.locationErrorMap.get("KB_URL") + "\">" + I18N.getMsg("dc.common.LEARN_MORE", new Object[0]) + "</a>";
            desciption += helpLink;
            geoErrorData.put(this.geoErrorDescription, (Object)desciption);
        }
        else if (isNativeAppInstalled && isTrackingAlways) {
            geoErrorData.put(this.isErrorOnRenderedMap, true);
            geoErrorData.put(this.isDeviceNeedInitLostModeFromMap, true);
            final String helpURL = mdmUrl + "/help/security_management/location_tracking.html#Enabling_location_tracking_Mac_devices";
            geoErrorData = this.buildGeoErrObj(geoErrorData, "dc.mdm.geoLoc.find_my_phone.prereq.mdmapp_needed_title", I18N.getMsg("dc.mdm.geoLoc.findMyMac.location.permission", new Object[] { helpURL }));
        }
        return geoErrorData;
    }
    
    private JSONObject buildGeoErrObj(final JSONObject errDataObj, final String errTitle, final String errDesc) throws Exception {
        try {
            errDataObj.put(this.geoErrorTitle, (Object)I18N.getMsg(errTitle, new Object[0]));
            errDataObj.put(this.geoErrorDescription, (Object)I18N.getMsg(errDesc, new Object[0]));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while buildGeoErrObj", ex);
        }
        return errDataObj;
    }
    
    private boolean isLocationError() {
        return this.locationErrorMap != null && this.locationErrorMap.containsKey("ERROR_CODE");
    }
    
    private boolean isLocationAdded() {
        return this.locationMap != null && this.locationMap.containsKey("ADDED_TIME");
    }
}
