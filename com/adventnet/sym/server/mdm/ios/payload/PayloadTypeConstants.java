package com.adventnet.sym.server.mdm.ios.payload;

import java.util.ArrayList;
import java.util.List;

public class PayloadTypeConstants
{
    private static final List<String> PAYLOADLIST;
    public static final String MDM_PAYLOAD_TYPE = "com.apple.mdm";
    public static final String CONFIGURATION_PAYLOAD_TYPE = "Configuration";
    public static final String RESTRICTIONS_PAYLOAD_TYPE = "com.apple.applicationaccess";
    public static final String PASSCODE_POLICY_PAYLOAD_TYPE = "com.apple.mobiledevice.passwordpolicy";
    public static final String EMAIL_PAYLOAD_TYPE = "com.apple.mail.managed";
    public static final String WEBCLIP_PAYLOAD_TYPE = "com.apple.webClip.managed";
    public static final String LDAP_PAYLOAD_TYPE = "com.apple.ldap.account";
    public static final String CALDAV_PAYLOAD_TYPE = "com.apple.caldav.account";
    public static final String CARDDAV_PAYLOAD_TYPE = "com.apple.carddav.account";
    public static final String CALENDAR_SUBSCRIPTION_PAYLOAD_TYPE = "com.apple.subscribedcalendar.account";
    public static final String EXCHANGE_PAYLOAD_TYPE = "com.apple.eas.account";
    public static final String VPN_PAYLOAD_TYPE = "com.apple.vpn.managed";
    public static final String WIFI_PAYLOAD_TYPE = "com.apple.wifi.managed";
    public static final String CREDENTIAL_PKCS12_PAYLOAD_TYPE = "com.apple.security.pkcs12";
    public static final String CREDENTIAL_ROOT_PAYLOAD_TYPE = "com.apple.security.root";
    public static final String CREDENTIAL_PEM_PAYLOAD_TYPE = "com.apple.security.pem";
    public static final String CREDENTIAL_PKCS1_PAYLOAD_TYPE = "com.apple.security.pkcs1";
    public static final String APP_LOCK_PAYLOAD_TYPE = "com.apple.app.lock";
    public static final String GLOBAL_HTTP_PROXY_PAYLOAD_TYPE = "com.apple.proxy.http.global";
    public static final String APN_PAYLOAD_TYPE = "com.apple.apn.managed";
    public static final String CELLULAR_PAYLOAD_TYPE = "com.apple.cellular";
    public static final String WEBCONTENT_PAYLOAD_TYPE = "com.apple.webcontent-filter";
    public static final String IOS_SCEP_PAYLOAD_TYPE = "com.apple.security.scep";
    public static final String MANAGED_DOMAIN_PAYLOAD_TYPE = "com.apple.domains";
    public static final String IOS_AIRPRINT_PAYLOAD_TYPE = "com.apple.airprint";
    public static final String IOS_SSO_PAYLOAD_TYPE = "com.apple.sso";
    public static final String IOS_PER_APP_VPN_PAYLOAD = "com.apple.vpn.managed.applayer";
    public static final String MAC_PER_APP_VPN_PAYLOAD = "com.apple.vpn.managed.appmapping";
    public static final String APP_NOTIFICATION_POLICY_PAYLOAD_TYPE = "com.apple.notificationsettings";
    public static final String MAC_FILEVAULT_PAYLOAD = "com.apple.MCX.FileVault2";
    public static final String MAC_FILEVAULT_ESCROW_RECOVERY = "com.apple.security.FDERecoveryKeyEscrow";
    public static final String MAC_DIRECTORY_BIND_POLICY = "com.apple.DirectoryService.managed";
    public static final String MAC_GATEKEEPER_SYSTEMPOLICY_CONTROL = "com.apple.systempolicy.control";
    public static final String MAC_GATEKEEPER_SYSTEMPOLICY_MANAGED = "com.apple.systempolicy.managed";
    public static final String MAC_PPPC_POLICY_PAYLOAD_TYPE = "com.apple.TCC.configuration-profile-policy";
    public static final String AD_CERT_PAYLOAD_SETTING = "com.apple.ADCertificate.managed";
    public static final String IOS_SCREEN_LAYOUT = "com.apple.homescreenlayout";
    public static final String MAC_SYSTEM_EXTENSION_PAYLOAD_TYPE = "com.apple.system-extension-policy";
    public static final String MAC_KERNEL_EXTENSION_PAYLOAD_TYPE = "com.apple.syspolicy.kernel-extension-policy";
    public static final String MAC_SYSTEM_PREFERENCE_PAYLOAD_TYPE = "com.apple.systempreferences";
    public static final String MAC_LOGIN_WINDOW_PAYLOAD_TYPE = "com.apple.loginwindow";
    public static final String MAC_GLOBAL_PREFERENCE_PAYLOAD_TYPE = ".GlobalPreferences";
    public static final String MAC_MCX_PAYLOAD_TYPE = "com.apple.MCX";
    public static final String MAC_SCREEN_SAVER_PAYLOAD_TYPE = "com.apple.screensaver";
    public static final String MAC_LOGIN_WINDOW_ITEM_TYPE = "com.apple.loginitems.managed";
    public static final String MAC_LOGIN_WINDOW_ITEM_SETTING_TYPE = "loginwindow";
    public static final String FONT_PAYLOAD_TYPE = "com.apple.font";
    public static final String MAC_ENERGY_SAVER_POLICY_TYPE = "com.apple.MCX";
    
    public static List<String> getSupportedPayloadTypes() {
        return PayloadTypeConstants.PAYLOADLIST;
    }
    
    static {
        PAYLOADLIST = new ArrayList<String>() {
            {
                this.add("com.apple.mobiledevice.passwordpolicy");
                this.add("com.apple.applicationaccess");
                this.add("com.apple.mail.managed");
                this.add("com.apple.webClip.managed");
                this.add("com.apple.ldap.account");
                this.add("com.apple.caldav.account");
                this.add("com.apple.carddav.account");
                this.add("com.apple.subscribedcalendar.account");
                this.add("com.apple.eas.account");
                this.add("com.apple.vpn.managed");
                this.add("com.apple.wifi.managed");
                this.add("com.apple.security.pkcs12");
                this.add("com.apple.security.root");
                this.add("com.apple.security.pem");
                this.add("com.apple.security.pkcs1");
                this.add("com.apple.app.lock");
                this.add("com.apple.proxy.http.global");
                this.add("com.apple.apn.managed");
                this.add("com.apple.cellular");
                this.add("com.apple.webcontent-filter");
                this.add("com.apple.domains");
                this.add("com.apple.security.scep");
                this.add("com.apple.airprint");
                this.add("com.apple.sso");
                this.add("com.apple.vpn.managed.applayer");
                this.add("com.apple.MCX.FileVault2");
                this.add("com.apple.security.FDERecoveryKeyEscrow");
                this.add("com.apple.DirectoryService.managed");
                this.add("com.apple.systempolicy.control");
                this.add("com.apple.systempolicy.managed");
                this.add("com.apple.vpn.managed.appmapping");
                this.add("com.apple.ADCertificate.managed");
                this.add("com.apple.system-extension-policy");
                this.add("com.apple.syspolicy.kernel-extension-policy");
                this.add("com.apple.TCC.configuration-profile-policy");
                this.add("com.apple.MCX");
                this.add("com.apple.font");
                this.add("loginwindow");
                this.add("com.apple.loginitems.managed");
                this.add("com.apple.screensaver");
                this.add("com.apple.MCX");
                this.add(".GlobalPreferences");
                this.add("com.apple.loginwindow");
                this.add("com.apple.systempreferences");
                this.add("com.apple.homescreenlayout");
                this.add("com.apple.notificationsettings");
            }
        };
    }
}
