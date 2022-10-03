package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ProfileListTransformer extends DefaultTransformer
{
    public Logger logger;
    
    public ProfileListTransformer() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final String viewname = vc.getUniqueId();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (!columnalais.equals("checkbox") && !columnalais.equals("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return Boolean.FALSE;
        }
        final boolean isMDMConfigAdmin = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Configurations_Admin");
        final boolean isModernMgmtAdmin = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Configurations_Admin");
        final boolean hasConfigurationAdminPrivilage = isMDMConfigAdmin || isModernMgmtAdmin;
        if (viewname.equalsIgnoreCase("ProfileList")) {
            boolean isRestrictedProfileListProfileWriteRole = Boolean.FALSE;
            if (!hasConfigurationAdminPrivilage) {
                final boolean isMDMConfigWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Configurations_Write");
                final boolean isModernMgmtrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Configurations_Write");
                final Boolean showOnlyUserCreatedProfilesApps = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOnlyUserCreatedProfilesApps");
                if (showOnlyUserCreatedProfilesApps != null && showOnlyUserCreatedProfilesApps) {
                    isRestrictedProfileListProfileWriteRole = ((isMDMConfigWrite || isModernMgmtrite) && showOnlyUserCreatedProfilesApps);
                }
            }
            return hasConfigurationAdminPrivilage || isRestrictedProfileListProfileWriteRole;
        }
        return hasConfigurationAdminPrivilage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            final Object data = tableContext.getPropertyValue();
            final String viewName = tableContext.getViewContext().getUniqueId();
            final int reportType = tableContext.getViewContext().getRenderType();
            if (columnalais.equals("Profile.SCOPE")) {
                final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("Profile.PLATFORM_TYPE");
                final Integer type = (Integer)data;
                final String pType = ProfileUtil.getProfileScopeName(type, platform);
                columnProperties.put("VALUE", pType);
            }
            if (columnalais.equals("checkbox") || columnalais.equals("Profile.PROFILE_NAME")) {
                final JSONObject payload = new JSONObject();
                final Integer profileSharedScope = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_SHARED_SCOPE");
                final Boolean isForAllCustomers = profileSharedScope == 1;
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                if (isForAllCustomers && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                    payload.put("isDisabled", !DMUserHandler.isUserInAdminRole(loginID));
                }
                else {
                    payload.put("isDisabled", false);
                }
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                    payload.put("isUserInRole", DMUserHandler.isUserInAdminRole(loginID));
                }
                else {
                    payload.put("isUserInRole", true);
                }
                payload.put("is_for_all_customers", (Object)isForAllCustomers);
                columnProperties.put("PAYLOAD", payload);
            }
            if (reportType != 4 && columnalais.equals("Profile.PLATFORM_TYPE")) {
                String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
                final Integer platformType = (Integer)data;
                if (platformType == 1) {
                    platformName = I18N.getMsg("dc.mdm.ios", new Object[0]);
                }
                else if (platformType == 2) {
                    platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
                }
                else if (platformType == 3) {
                    platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
                }
                else if (platformType == 4) {
                    platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
                }
                else if (platformType == 6) {
                    platformName = I18N.getMsg("mdm.os.MacOS", new Object[0]);
                }
                else if (platformType == 7) {
                    platformName = I18N.getMsg("mdm.os.TVos", new Object[0]);
                }
                columnProperties.put("VALUE", platformName);
            }
            if (columnalais.equals("CollectionStatus.PROFILE_COLLECTION_STATUS")) {
                final Integer statusId = (Integer)data;
                String statusName = "";
                String statusClass = "";
                if (statusId == 1) {
                    statusName = I18N.getMsg("dc.db.config.status.draft", new Object[0]);
                    statusClass = "ucs-table-status-text__not-applicable";
                }
                else if (statusId == 110) {
                    statusName = I18N.getMsg("dc.db.mdm.scanStaus.Published", new Object[0]);
                    statusClass = "ucs-table-status-text__success";
                }
                if (reportType != 4) {
                    columnProperties.put("VALUE", statusName);
                }
                else {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("statusClass", (Object)statusClass);
                    payload2.put("statusName", (Object)statusName);
                    columnProperties.put("PAYLOAD", payload2);
                }
            }
            if (columnalais.equals("Action")) {
                final Integer profileStatus = (Integer)tableContext.getAssociatedPropertyValue("CollectionStatus.PROFILE_COLLECTION_STATUS");
                final Integer profileType = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
                final JSONObject payload3 = new JSONObject();
                if (profileStatus != null && profileStatus == 1) {
                    payload3.put("yetToPublish", true);
                }
                else {
                    payload3.put("yetToPublish", false);
                }
                payload3.put("profile_type", (Object)profileType);
                final Integer profileSharedScope2 = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_SHARED_SCOPE");
                final Boolean isForAllCustomers2 = profileSharedScope2 == 1;
                if (isForAllCustomers2) {
                    payload3.put("isMoveToAllApplicable", false);
                }
                else {
                    final Boolean isMoveToAllApplicable = (Boolean)tableContext.getAssociatedPropertyValue("Profile.IS_MOVE_TO_ALL_APPLICABLE");
                    payload3.put("isMoveToAllApplicable", (Object)isMoveToAllApplicable);
                }
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                    final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    payload3.put("isUserInRole", DMUserHandler.isUserInAdminRole(loginId));
                }
                else {
                    payload3.put("isUserInRole", true);
                }
                payload3.put("is_for_all_customers", (Object)isForAllCustomers2);
                columnProperties.put("PAYLOAD", payload3);
            }
            if (columnalais.equals("Configuration.CONFIG_NAME") && isExport == null) {
                final Integer scope = (Integer)tableContext.getAssociatedPropertyValue("Profile.SCOPE");
                String configName = (String)data;
                if (!configName.equalsIgnoreCase("APP_POLICY")) {
                    final String policy = this.getPolicyName(configName);
                    configName = "<a href='javascript:showRead(\"" + configName + "\",\"" + scope + "\",\"" + policy + "\")'>" + policy + "</a>";
                }
                columnProperties.put("VALUE", configName);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in ProfileListTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
    
    private String getPolicyName(final String configName) throws Exception {
        String policyName = configName;
        switch (configName) {
            case "PASSCODE_POLICY":
            case "ANDROID_PASSCODE_POLICY":
            case "WINDOWS_PASSCODE_POLICY": {
                policyName = I18N.getMsg("dc.mdm.enroll.passcode", new Object[0]);
                break;
            }
            case "RESTRICTIONS_POLICY":
            case "ANDROID_RESTRICTIONS_POLICY":
            case "WINDOWS_RESTRICTION_POLICY":
            case "CHROME_RESTRICTIONS_POLICY":
            case "CHROME_USER_RESTRICTIONS": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.restrictions", new Object[0]);
                break;
            }
            case "WIFI_POLICY":
            case "ANDROID_WIFI_POLICY":
            case "WINDOWS_WIFI_POLICY":
            case "CHROME_WIFI_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.wi_fi", new Object[0]);
                break;
            }
            case "VPN_POLICY":
            case "WINDOWS_VPN_POLICY":
            case "CHROME_VPN_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.allow_vpn", new Object[0]);
                break;
            }
            case "IOS_PER_APP_VPN": {
                policyName = I18N.getMsg("mdm.device_mgmt.per_app_vpn", new Object[0]);
                break;
            }
            case "EMAIL_POLICY":
            case "ANDROID_EMAIL_POLICY":
            case "WINDOWS_EMAIL_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.email", new Object[0]);
                break;
            }
            case "EXCHANGE_ACTIVE_SYNC_POLICY":
            case "ANDROID_EXCHANGE_ACTIVE_SYNC_POLICY":
            case "WINDOWS_EXCHANGE_ACTIVE_SYNC_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.exchange_activesync", new Object[0]);
                break;
            }
            case "APP_LOCK_POLICY":
            case "ANDROID_KIOSK_POLICY":
            case "WINDOWS_LOCKDOWN_POLICY":
            case "CHROME_KIOSK_POLICY": {
                policyName = I18N.getMsg("dc.mdm.profile.android.kiosk.mode", new Object[0]);
                break;
            }
            case "WEBCLIPS_POLICY":
            case "ANDROID_WEBCLIPS_POLICY": {
                policyName = I18N.getMsg("dc.conf.webclip.web_clips", new Object[0]);
                break;
            }
            case "WEB_CONTENT_FILTER_POLICY":
            case "ANDROID_WEB_CONTENT_FILTER":
            case "CHROME_WEB_CONTENT_FILTER": {
                policyName = I18N.getMsg("dc.conf.webclip.web_content_filter", new Object[0]);
                break;
            }
            case "MANAGED_WEB_DOMAIN_POLICY": {
                policyName = I18N.getMsg("mdm.web_managed_domain_name", new Object[0]);
                break;
            }
            case "IOS_WALLPAPER_POLICY":
            case "ANDROID_WALLPAPER_POLICY": {
                policyName = I18N.getMsg("dc.conf.dispConf.wallpaper", new Object[0]);
                break;
            }
            case "IOS_AIRPRINT_POLICY": {
                policyName = I18N.getMsg("mdm.profile.airprint", new Object[0]);
                break;
            }
            case "GLOBAL_HTTP_PROXY_POLICY":
            case "ANDROID_HTTP_PROXY_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.global_http_proxy", new Object[0]);
                break;
            }
            case "IOS_SSO_POLICY": {
                policyName = I18N.getMsg("mdm.profile.ssoPolicy", new Object[0]);
                break;
            }
            case "IOS_CERTIFICATE_POLICY":
            case "ANDROID_CERTIFICATE_POLICY":
            case "WINDOWS_CERTIFICATE_POLICY":
            case "CHROME_CERTIFICATE_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.certificate", new Object[0]);
                break;
            }
            case "iOS_SCEP_POLICY":
            case "WINDOWS_SCEP_POLICY":
            case "ANDROID_SCEP_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.scep", new Object[0]);
                break;
            }
            case "LDAP_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.ldap", new Object[0]);
                break;
            }
            case "CARDDAV_POLICY": {
                policyName = I18N.getMsg("dc.mdm.deviceMgmt.carddav", new Object[0]);
                break;
            }
            case "CALDAV_POLICY": {
                policyName = I18N.getMsg("dc.mdm.deviceMgmt.caldav", new Object[0]);
                break;
            }
            case "SUBSCRIBED_CALENDARS_POLICY": {
                policyName = I18N.getMsg("dc.mdm.device_mgmt.subscribed_calendars", new Object[0]);
                break;
            }
            case "APN_POLICY":
            case "ANDROID_APN_POLICY": {
                policyName = I18N.getMsg("dc.mdm.deviceMgmt.access_point_name", new Object[0]);
                break;
            }
            case "ANDROID_AGENT_MIGRATION": {
                policyName = I18N.getMsg("mdm.deviceMgmt.agent_migration", new Object[0]);
                break;
            }
            case "ANDROID_EFRP_POLICY": {
                policyName = I18N.getMsg("mdm.profile.EFRP", new Object[0]);
                break;
            }
            case "IOS_LOCK_SCREEN_MESSAGE": {
                policyName = I18N.getMsg("mdm.profile.assetTagging", new Object[0]);
                break;
            }
            case "MAC_FILE_VAULT": {
                policyName = I18N.getMsg("mdm.settings.fileVault", new Object[0]);
                break;
            }
            case "CHROME_ETHERNET_POLICY": {
                policyName = I18N.getMsg("mdm.profile.chrome.ethernet", new Object[0]);
                break;
            }
            case "CHROME_POWER_SETTINGS": {
                policyName = I18N.getMsg("mdm.profile.power_mgmt", new Object[0]);
                break;
            }
            case "CHROME_MANAGED_BOOKMARKS": {
                policyName = I18N.getMsg("mdm.profile.managed_bookmarks", new Object[0]);
                break;
            }
            case "CHROME_VERIFY_ACCESS_API": {
                policyName = I18N.getMsg("mdm.profile.verify_access_api", new Object[0]);
                break;
            }
            case "CHROME_BROWSER_MANAGEMENT": {
                policyName = I18N.getMsg("mdm.profile.chrome_browser_settings", new Object[0]);
                break;
            }
            case "CHROME_APPLICATION_POLICY": {
                policyName = I18N.getMsg("mdm.profile.applicataion_polices", new Object[0]);
                break;
            }
            case "CHROME_MANAGED_GUEST_SESSION": {
                policyName = I18N.getMsg("mdm.profile.managed_guest_session", new Object[0]);
                break;
            }
            case "MAC_RESTRICTIONS_POLICY": {
                policyName = I18N.getMsg("mdm.device_mgmt.mac.restrictions", new Object[0]);
                break;
            }
            case "TVOS_RESTRICTIONS_POLICY": {
                policyName = I18N.getMsg("mdm.device_mgmt.tvos.restrictions", new Object[0]);
                break;
            }
            case "MAC_SYSTEM_PREFERENCE": {
                policyName = I18N.getMsg("mdm.devicemgmt.mac.syspreference", new Object[0]);
                break;
            }
            case "MAC_ENERGY_SAVER_POLICY": {
                policyName = I18N.getMsg("mdm.devicemgmt.mac.energysaver", new Object[0]);
                break;
            }
        }
        return policyName;
    }
}
