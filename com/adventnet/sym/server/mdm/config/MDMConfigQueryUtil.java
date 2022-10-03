package com.adventnet.sym.server.mdm.config;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MDMConfigQueryUtil
{
    protected static Logger logger;
    
    private MDMConfigQueryUtil() {
    }
    
    public static DataObject getConfigDataObject(final MDMConfigQuery configQueryObject) throws DataAccessException {
        final String baseTableName = "ConfigData";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
        query.addJoin(new Join(baseTableName, "ConfigDataStatus", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join(baseTableName, "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("CfgDataToCollection", "CollSchExecution", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        query.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        query.addJoin(new Join(baseTableName, "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
        for (final int configId : configQueryObject.getConfigIds()) {
            switch (configId) {
                case 172:
                case 757: {
                    query.addJoin(new Join("ConfigDataItem", "PasscodePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 765: {
                    query.addJoin(new Join("ConfigDataItem", "ADCertPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ADCertPolicy", "ADCertConfiguration", new String[] { "AD_CONFIG_ID" }, new String[] { "AD_CONFIG_ID" }, 1));
                    continue;
                }
                case 568: {
                    query.addJoin(new Join("ConfigDataItem", "WorkDataSecurityPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WorkDataSecurityPolicy", "MDConfigToApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MDConfigToApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    continue;
                }
                case 173:
                case 751:
                case 951: {
                    query.addJoin(new Join("ConfigDataItem", "RestrictionsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 174: {
                    query.addJoin(new Join("ConfigDataItem", "EMailPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 759: {
                    query.addJoin(new Join("ConfigDataItem", "MacSystemPreferencePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacSystemPreferencePolicy", "MacSystemPreferences", new String[] { "MAC_SYSTEM_PREFERENCE_ID" }, new String[] { "MAC_SYSTEM_PREFERENCE_ID" }, 1));
                    continue;
                }
                case 175: {
                    query.addJoin(new Join("ConfigDataItem", "ExchangeActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 176:
                case 521:
                case 756:
                case 766: {
                    query.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnL2TP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnPPTP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnIPSec", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCisco", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnJuniperSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnF5SSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCustomSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCustomData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppLockPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    final Table vpnIkev2 = new Table("VpnIKEv2");
                    final Table ikesecurityAssociation = new Table("IKESAParams", "IKE_SA_ID");
                    final Table childSecurityAssociation = new Table("IKESAParams", "CHILD_SA_ID");
                    query.addJoin(new Join("VpnPolicy", "VpnToPolicyRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnToPolicyRel", "VpnIKEv2", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
                    query.addJoin(new Join(vpnIkev2, ikesecurityAssociation, new String[] { "IKE_SA_ID" }, new String[] { "SECURITY_ASSOCIATION_ID" }, 1));
                    query.addJoin(new Join(vpnIkev2, childSecurityAssociation, new String[] { "CHILD_SA_ID" }, new String[] { "SECURITY_ASSOCIATION_ID" }, 1));
                    query.addJoin(new Join("VpnToPolicyRel", "VpnPolicyToCertificate", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VPNOnDemandPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VPNOnDemandPolicy", "VpnNWRuleToPolicyRel", new String[] { "VPN_OD_POLICY_ID" }, new String[] { "VPN_OD_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnNWRuleToPolicyRel", "VpnODRulesForNWChange", new String[] { "NW_CHANGE_RULE_ID" }, new String[] { "NW_CHANGE_RULE_ID" }, 1));
                    query.addJoin(new Join("VPNOnDemandPolicy", "VpnConEvalRuleToPolicyRel", new String[] { "VPN_OD_POLICY_ID" }, new String[] { "VPN_OD_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnConEvalRuleToPolicyRel", "VpnODRulesForConEval", new String[] { "CONN_RULE_ID" }, new String[] { "CONN_RULE_ID" }, 1));
                    final SortColumn sortCol = new SortColumn(new Column("VPNOnDemandPolicy", "ON_DEMAND_RULE_ORDER"), true);
                    query.addSortColumn(sortCol);
                    continue;
                }
                case 177:
                case 774: {
                    query.addJoin(new Join("ConfigDataItem", "WifiPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiNonEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "AppleWifiPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 528:
                case 775: {
                    query.addJoin(new Join("ConfigDataItem", "MdmAppNotificationPolicyToConfigRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MdmAppNotificationPolicyToConfigRel", "MdmAppNotificationPolicy", new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, 1));
                    query.addJoin(new Join("MdmAppNotificationPolicy", "MdmAppNotificationPolicyToAppRel", new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, 1));
                    query.addJoin(new Join("MdmAppNotificationPolicyToAppRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    continue;
                }
                case 771: {
                    query.addJoin(new Join("ConfigDataItem", "DirectoryBindConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("DirectoryBindConfig", "ADBindPrivilegeGroup", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CFG_DATA_ITEM" }, 1));
                    query.addJoin(new Join("DirectoryBindConfig", "ADBindOU", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CFG_DATA_ITEM" }, 1));
                    query.addJoin(new Join("DirectoryBindConfig", "DirectoryBindPolicyTemplate", new String[] { "BIND_POLICY_ID" }, new String[] { "BIND_POLICY_ID" }, 1));
                    query.addJoin(new Join("DirectoryBindPolicyTemplate", "DMDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
                    query.addJoin(new Join("DMDomain", "DMManagedDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
                    query.addJoin(new Join("DMManagedDomain", "DMManagedDomainCredentialRel", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
                    query.addJoin(new Join("DMManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 1));
                    query.addJoin(new Join("DirectoryBindPolicyTemplate", "ADBindPolicyTemplate", new String[] { "BIND_POLICY_ID" }, new String[] { "BIND_POLICY_ID" }, 1));
                    query.addJoin(new Join("ADBindPolicyTemplate", "ADBindRestrictedDDNS", new String[] { "BIND_POLICY_ID" }, new String[] { "BIND_POLICY_ID" }, 1));
                    continue;
                }
                case 753: {
                    query.addJoin(new Join("ConfigDataItem", "MacFirmwarePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 752: {
                    query.addJoin(new Join("ConfigDataItem", "MdMacAccountConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MdMacAccountConfigPolicy", "MdMacAccountConfigSettings", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
                    query.addJoin(new Join("MdMacAccountConfigSettings", "MdMacAccountToConfig", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
                    query.addJoin(new Join("MdMacAccountToConfig", "MdComputerAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
                    continue;
                }
                case 754: {
                    query.addJoin(new Join("ConfigDataItem", "MacPPPCPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacPPPCPolicy", "AppPermissionConfig", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
                    query.addJoin(new Join("AppPermissionConfig", "InvAppGroupToPermission", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
                    query.addJoin(new Join("AppPermissionConfig", "AppPermissionConfigDetails", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
                    query.addJoin(new Join("AppPermissionConfigDetails", "AppleEventPreference", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 1));
                    query.addJoin(new Join("InvAppGroupToPermission", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "InvAppGroupToPermission", "INV_APPGROUPDETAILS_ALIAS", 2));
                    query.addJoin(new Join("AppleEventPreference", "MdAppGroupDetails", new String[] { "RECEIVER_APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "AppleEventPreference", "RECEIVER_APPGROUPDETAILS_ALIAS", 1));
                    query.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "RECEIVER_APPGROUPDETAILS_ALIAS", "RECEIVER_MACAPPPROPERTIES_ALIAS", 1));
                    query.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "INV_APPGROUPDETAILS_ALIAS", "INV_MACAPPPROPERTIES_ALIAS", 2));
                    query.addJoin(new Join("AppPermissionConfigDetails", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
                    query.addJoin(new Join("AppPermissionConfigDetails", "MacAppPermissionProps", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 2));
                    continue;
                }
                case 755: {
                    query.addJoin(new Join("ConfigDataItem", "MacSystemExtnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacSystemExtnPolicy", "MacSystemExtnConfig", new String[] { "EXTENSION_POLICY_ID" }, new String[] { "EXTENSION_POLICY_ID" }, 2));
                    query.addJoin(new Join("MacSystemExtnConfig", "MacSystemExtnPreference", new String[] { "EXTENSION_POLICY_ID" }, new String[] { "EXTENSION_POLICY_ID" }, 2));
                    query.addJoin(new Join("MacSystemExtnPreference", "AppleProvProfilesDetails", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
                    query.addJoin(new Join("AppleProvProfilesDetails", "AppleProvProfilesExtn", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
                    continue;
                }
                case 760: {
                    query.addJoin(new Join("ConfigDataItem", "MacEnergySettingsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacEnergySettingsPolicy", "MacEnergyConfigurations", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacEnergySettingsPolicy", "MacEnergySchedule", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 178: {
                    query.addJoin(new Join("ConfigDataItem", "LdapPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 179: {
                    query.addJoin(new Join("ConfigDataItem", "CalDAVPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 180: {
                    query.addJoin(new Join("ConfigDataItem", "SubscibedCalendarPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 181: {
                    query.addJoin(new Join("ConfigDataItem", "CardDAVPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 182:
                case 560: {
                    query.addJoin(new Join("ConfigDataItem", "WebClipToConfigRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WebClipToConfigRel", "WebClipPolicies", new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
                    continue;
                }
                case 183: {
                    query.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppLockPolicy", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppLockPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    continue;
                }
                case 184:
                case 559:
                case 768: {
                    query.addJoin(new Join("ConfigDataItem", "GlobalHttpProxyPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 187: {
                    query.addJoin(new Join("ConfigDataItem", "ApnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 301: {
                    query.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("InstallAppPolicy", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("MdPackageToAppData", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
                    query.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
                    query.addJoin(new Join("InstallAppPolicy", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("InstallAppPolicy", "AppConfigTemplate", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("AppConfigTemplate", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 1));
                    query.addJoin(new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("InstallAppPolicy", "AppDependencyPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppDependencyPolicy", "AppDependency", new String[] { "DEPENDENCY_ID" }, new String[] { "DEPENDENCY_ID" }, 1));
                    query.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
                    query.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
                    continue;
                }
                case 302: {
                    query.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ManagedAppConfigurationPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ManagedAppConfigurationPolicy", "AppConfigTemplate", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("AppConfigTemplate", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 1));
                    query.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
                    query.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
                    continue;
                }
                case 188:
                case 561:
                case 758: {
                    query.addJoin(new Join("ConfigDataItem", "IOSWebContentPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("IOSWebContentPolicy", "URLRestrictionDetails", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("URLRestrictionDetails", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    query.addJoin(new Join("IOSWebContentPolicy", "AppleWCFConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppleWCFConfig", "MacWCFKext", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("IOSWebContentPolicy", "AppleWCFPermittedURL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AppleWCFPermittedURL", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, "AppleWCFPermittedURL", "PERMITTED_URL_DETAILS_ALIAS", 1));
                    query.addJoin(new Join("IOSWebContentPolicy", "MDMConfigCustomData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 515:
                case 555:
                case 607:
                case 703:
                case 772: {
                    query.addJoin(new Join("ConfigDataItem", "CertificatePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CertificatePolicy", "CredentialCertificateInfo", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 1));
                    continue;
                }
                case 516:
                case 566:
                case 606:
                case 773: {
                    query.addJoin(new Join("ConfigDataItem", "SCEPPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("SCEPPolicy", "SCEPConfigurations", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
                    query.addJoin(new Join("SCEPPolicy", "SCEPServerToTemplate", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
                    query.addJoin(new Join("SCEPServerToTemplate", "SCEPServers", new String[] { "SCEP_SERVER_ID" }, new String[] { "SERVER_ID" }, 1));
                    query.addJoin(new Join("SCEPServers", "CredentialCertificateInfo", new String[] { "CA_CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, "SCEPServers", "CASERVERCERTIFICATES", 1));
                    continue;
                }
                case 185: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidPasscodePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 186: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidRestrictionsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 556:
                case 605: {
                    query.addJoin(new Join("ConfigDataItem", "WifiPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiNonEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 553: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidEMailPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 554: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 557: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AndroidKioskPolicy", "AndroidKioskPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AndroidKioskPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("AndroidKioskPolicy", "KioskCustomSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 518:
                case 558: {
                    query.addJoin(new Join("ConfigDataItem", "MDMWallpaperPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 562: {
                    query.addJoin(new Join("ConfigDataItem", "ApnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ApnPolicy", "AndroidApnPolicyExtn", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 601: {
                    query.addJoin(new Join("ConfigDataItem", "WpPasscodePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 602: {
                    query.addJoin(new Join("ConfigDataItem", "WpEmailPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 603: {
                    query.addJoin(new Join("ConfigDataItem", "WpExchangeActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 604: {
                    query.addJoin(new Join("ConfigDataItem", "WpRestrictionsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 517: {
                    query.addJoin(new Join("ConfigDataItem", "ManagedWebDomainPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ManagedWebDomainPolicy", "ManagedWebDomainURLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    continue;
                }
                case 519:
                case 769: {
                    query.addJoin(new Join("ConfigDataItem", "AirPrintPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 770: {
                    query.addJoin(new Join("ConfigDataItem", "MacFileVault2Policy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 563: {
                    query.addJoin(new Join("ConfigDataItem", "AgentMigrationDetails", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 520: {
                    query.addJoin(new Join("ConfigDataItem", "SSOAccountPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "SSOKerberosAccount", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "SSOApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "SSODomains", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("SSODomains", "ManagedWebDomainURLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "SSOToCertificateRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 564: {
                    query.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnL2TP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnPPTP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnIPSec", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCisco", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnJuniperSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnF5SSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnPaloAlto", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 522:
                case 567: {
                    query.addJoin(new Join("ConfigDataItem", "LockScreenToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("LockScreenToCfgDataItem", "LockScreenConfiguration", new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, 1));
                    query.addJoin(new Join("LockScreenConfiguration", "LockScreenToMsgInfo", new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, 1));
                    query.addJoin(new Join("LockScreenToMsgInfo", "LockScreenMessages", new String[] { "MESSAGE_ID" }, new String[] { "MESSAGE_ID" }, 1));
                    continue;
                }
                case 608: {
                    query.addJoin(new Join("ConfigDataItem", "WindowsKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "WindowsKioskPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "WindowsKioskPolicySystemApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WindowsKioskPolicySystemApps", "WindowsSystemApps", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    query.addJoin(new Join("WindowsKioskPolicyApps", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("MdAppToGroupRel", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    continue;
                }
                case 610: {
                    query.addJoin(new Join("ConfigDataItem", "DataProtectionPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                    query.addJoin(new Join("DataProtectionPolicy", "CorporatePolicy", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
                    query.addJoin(new Join("CorporatePolicy", "RuleToPolicy", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
                    query.addJoin(new Join("RuleToPolicy", "EnterpriseRules", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2));
                    query.addJoin(new Join("EnterpriseRules", "EnterpriseNetworkLimit", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    query.addJoin(new Join("EnterpriseRules", "EnterpriseApplications", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    query.addJoin(new Join("EnterpriseRules", "EnterpriseConfiguration", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    query.addJoin(new Join("EnterpriseConfiguration", "WindowsEnterpriseConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    continue;
                }
                case 611: {
                    query.addJoin(new Join("ConfigDataItem", "WindowsLockdownPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                    query.addJoin(new Join("WindowsLockdownPolicy", "LockdownPolicy", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
                    query.addJoin(new Join("LockdownPolicy", "LockdownRules", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
                    query.addJoin(new Join("LockdownRules", "LockdownRuleToApp", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    query.addJoin(new Join("LockdownRules", "WindowsLockdownConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
                    query.addJoin(new Join("LockdownRuleToApp", "LockdownApplications", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    continue;
                }
                case 702: {
                    query.addJoin(new Join("ConfigDataItem", "EthernetConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "PayloadProxyConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "PayloadWifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 704: {
                    query.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnL2TP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "OpenVPNPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "PayloadProxyConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 705: {
                    query.addJoin(new Join("ConfigDataItem", "ChromeKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ChromeKioskPolicy", "ChromeKioskPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ChromeKioskPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("ChromeKioskPolicyApps", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("ChromeKioskPolicyApps", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    continue;
                }
                case 706: {
                    query.addJoin(new Join("ConfigDataItem", "ChromeRestrictionPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 708: {
                    query.addJoin(new Join("ConfigDataItem", "PowerManagementSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 709: {
                    query.addJoin(new Join("ConfigDataItem", "ManagedBookmarksPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ManagedBookmarksPolicy", "CfgDataItemToUrl", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CfgDataItemToUrl", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    continue;
                }
                case 710: {
                    query.addJoin(new Join("ConfigDataItem", "ChromeUserRestrictions", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 707: {
                    query.addJoin(new Join("ConfigDataItem", "IOSWebContentPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("IOSWebContentPolicy", "CfgDataItemToUrl", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CfgDataItemToUrl", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    continue;
                }
                case 701: {
                    query.addJoin(new Join("ConfigDataItem", "WifiPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiNonEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "WifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WifiPolicy", "PayloadProxyConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 711: {
                    query.addJoin(new Join("ConfigDataItem", "VerifyAccessAPIConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 712: {
                    query.addJoin(new Join("ConfigDataItem", "BrowserConfiguration", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 713: {
                    query.addJoin(new Join("ConfigDataItem", "ApplicationPolicyConfig", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ApplicationPolicyConfig", "CfgDataItemToUrl", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CfgDataItemToUrl", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    continue;
                }
                case 714: {
                    query.addJoin(new Join("ConfigDataItem", "ManagedGuestSession", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ManagedGuestSession", "ManagedGuestSessionToInnerPolicies", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "MGS_CONFIG_DATA_ITEM_ID" }, 1));
                    final Table managedGuestSessionToInnerPolicies = Table.getTable("ManagedGuestSessionToInnerPolicies");
                    final Table subConfigData = Table.getTable("ConfigData", "ManagedGuestSessionSubConfigData");
                    final Table subConfigDataItem = Table.getTable("ConfigDataItem", "ManagedGuestSessionSubConfigDataItem");
                    final Table restrictionPayload = Table.getTable("ChromeUserRestrictions");
                    final Table browserRestrictionPayload = Table.getTable("BrowserConfiguration");
                    final Table webContentPayload = Table.getTable("IOSWebContentPolicy");
                    final Table webContentToURL = Table.getTable("CfgDataItemToUrl", "ManagedGuestSessionWebContentToUrl");
                    final Table webContentUrlDetails = Table.getTable("URLDetails", "ManagedGuestSessionWebContentUrlDetails");
                    final Table bookmarksPayload = Table.getTable("ManagedBookmarksPolicy");
                    final Table bookmarksToURL = Table.getTable("CfgDataItemToUrl", "ManagedGuestSessionBookmarksToUrl");
                    final Table bookmarksURLDetails = Table.getTable("URLDetails", "ManagedGuestSessionBookmarksUrlDetails");
                    query.addJoin(new Join(managedGuestSessionToInnerPolicies, subConfigData, new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
                    query.addJoin(new Join(subConfigData, subConfigDataItem, new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
                    query.addJoin(new Join(subConfigDataItem, restrictionPayload, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(subConfigDataItem, browserRestrictionPayload, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(subConfigDataItem, webContentPayload, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(webContentPayload, webContentToURL, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(webContentToURL, webContentUrlDetails, new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    query.addJoin(new Join(subConfigDataItem, bookmarksPayload, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(bookmarksPayload, bookmarksToURL, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join(bookmarksToURL, bookmarksURLDetails, new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 1));
                    continue;
                }
                case 565: {
                    query.addJoin(new Join("ConfigDataItem", "AndroidEFRPPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("AndroidEFRPPolicy", "EFRPAccDetails", new String[] { "EFRP_ACC_ID" }, new String[] { "EFRP_ACC_ID" }, 1));
                    continue;
                }
                case 609: {
                    query.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnL2TP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnPPTP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnIPSec", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCisco", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnJuniperSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnF5SSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCustomSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VpnCustomData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "WindowsKioskPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WindowsKioskPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "WindowsKioskPolicySystemApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("WindowsKioskPolicySystemApps", "WindowsSystemApps", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                    final Table vpnIkev2 = new Table("VpnIKEv2");
                    final Table ikesecurityAssociation = new Table("IKESAParams", "IKE_SA_ID");
                    final Table childSecurityAssociation = new Table("IKESAParams", "CHILD_SA_ID");
                    query.addJoin(new Join("VpnPolicy", "VpnToPolicyRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VpnToPolicyRel", "VpnIKEv2", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
                    query.addJoin(new Join(vpnIkev2, ikesecurityAssociation, new String[] { "IKE_SA_ID" }, new String[] { "SECURITY_ASSOCIATION_ID" }, 1));
                    query.addJoin(new Join(vpnIkev2, childSecurityAssociation, new String[] { "CHILD_SA_ID" }, new String[] { "SECURITY_ASSOCIATION_ID" }, 1));
                    query.addJoin(new Join("VpnToPolicyRel", "VpnPolicyToCertificate", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnPolicy", "VPNOnDemandPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("VPNOnDemandPolicy", "VpnNWRuleToPolicyRel", new String[] { "VPN_OD_POLICY_ID" }, new String[] { "VPN_OD_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnNWRuleToPolicyRel", "VpnODRulesForNWChange", new String[] { "NW_CHANGE_RULE_ID" }, new String[] { "NW_CHANGE_RULE_ID" }, 1));
                    query.addJoin(new Join("VPNOnDemandPolicy", "VpnConEvalRuleToPolicyRel", new String[] { "VPN_OD_POLICY_ID" }, new String[] { "VPN_OD_POLICY_ID" }, 1));
                    query.addJoin(new Join("VpnConEvalRuleToPolicyRel", "VpnODRulesForConEval", new String[] { "CONN_RULE_ID" }, new String[] { "CONN_RULE_ID" }, 1));
                    final SortColumn sortCol = new SortColumn(new Column("VPNOnDemandPolicy", "ON_DEMAND_RULE_ORDER"), true);
                    query.addSortColumn(sortCol);
                    continue;
                }
                case 525:
                case 767: {
                    query.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
                    query.addJoin(new Join("CustomProfileDetails", "AppleCustomProfilesDataExtn", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
                    continue;
                }
                case 612: {
                    query.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
                    query.addJoin(new Join("CustomProfileDetails", "WindowsCustomProfilesData", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
                    query.addJoin(new Join("WindowsCustomProfilesData", "WindowsCustomProfilesDataExtn", new String[] { "CUSTOM_PROFILE_DATA_ID" }, new String[] { "CUSTOM_PROFILE_DATA_ID" }, 1));
                    query.addSortColumn(new SortColumn(Column.getColumn("WindowsCustomProfilesData", "POSITION"), true));
                    continue;
                }
                case 613: {
                    query.addJoin(new Join("ConfigDataItem", "BitlockerPolicyToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("BitlockerPolicyToCfgDataItem", "BitlockerPolicy", new String[] { "BITLOCKER_POLICY_ID" }, new String[] { "BITLOCKER_POLICY_ID" }, 1));
                    query.addJoin(new Join("BitlockerPolicy", "ADMXBackedPolicyGroup", new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
                    query.addJoin(new Join("ADMXBackedPolicyGroup", "ADMXGroupToADMXPolicy", new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
                    query.addJoin(new Join("ADMXGroupToADMXPolicy", "ADMXBackedPolicyConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
                    query.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicy", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
                    query.addJoin(new Join("ADMXBackedPolicy", "ADMXBackedPolicyData", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
                    query.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicyDataConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
                    continue;
                }
                case 901: {
                    query.addJoin(new Join("ConfigDataItem", "DataTrackingPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                    query.addJoin(new Join("DataTrackingPolicy", "DataTrackingSSID", new String[] { "SSID_TRACKING_ID" }, new String[] { "SSID_TRACKING_ID" }, 2));
                    continue;
                }
                case 902: {
                    query.addJoin(new Join("ConfigDataItem", "DataUsageActions", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                    query.addJoin(new Join("DataUsageActions", "DataTrackingSSID", new String[] { "SSID_TRACKING_ID" }, new String[] { "SSID_TRACKING_ID" }, 2));
                    continue;
                }
                case 903: {
                    query.addJoin(new Join("ConfigDataItem", "DataUsageLevels", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                    query.addJoin(new Join("DataUsageLevels", "DataTrackingSSID", new String[] { "SSID_TRACKING_ID" }, new String[] { "SSID_TRACKING_ID" }, 2));
                    continue;
                }
                case 761: {
                    query.addJoin(new Join("ConfigDataItem", "MacLoginWindow", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacLoginWindow", "MacLoginWindowSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("ConfigDataItem", "MacScreenSaverSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 762: {
                    query.addJoin(new Join("ConfigDataItem", "MacLoginWindowItemSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("MacLoginWindowItemSettings", "MacLoginWindowItems", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 526:
                case 763: {
                    query.addJoin(new Join("ConfigDataItem", "CfgDataItemToFontRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    query.addJoin(new Join("CfgDataItemToFontRel", "FontDetails", new String[] { "FONT_ID" }, new String[] { "FONT_ID" }, 1));
                    continue;
                }
                case 764: {
                    query.addJoin(new Join("ConfigDataItem", "MacGatekeeperPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 527: {
                    query.addJoin(new Join("ConfigDataItem", "SharedDeviceConfiguration", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
                case 529: {
                    query.addJoin(new Join("ConfigDataItem", "IOSAccessibilitySettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
                    continue;
                }
            }
        }
        MDMConfigQueryUtil.logger.log(Level.FINEST, "Config Select Query: {0} for fetching configuration", query);
        query.setCriteria(configQueryObject.getConfigCriteria());
        if (configQueryObject.getConfigJoins() != null && !configQueryObject.getConfigJoins().isEmpty()) {
            for (final Join configJoin : configQueryObject.getConfigJoins()) {
                query.addJoin(configJoin);
            }
        }
        if (configQueryObject.getConfigColumns() != null) {
            for (final Column configColumn : configQueryObject.getConfigColumns()) {
                query.addSelectColumn(configColumn);
            }
        }
        else {
            query.addSelectColumn(new Column((String)null, "*"));
        }
        if (configQueryObject.getConfigSortColumn() != null) {
            query.addSortColumn(configQueryObject.getConfigSortColumn());
        }
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
        if (!dataObject.isEmpty()) {
            for (final Integer configId2 : configQueryObject.getConfigIds()) {
                switch (configId2) {
                    case 557: {
                        final Row configDataItemRow = dataObject.getRow("ConfigDataItem");
                        final Long configDataItemId = (Long)configDataItemRow.get("CONFIG_DATA_ITEM_ID");
                        final SelectQuery webClipQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipToConfigRel"));
                        webClipQuery.addJoin(new Join("WebClipToConfigRel", "WebClipPolicies", new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 2));
                        final Criteria webClipConfigCriteria = new Criteria(new Column("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        webClipQuery.setCriteria(webClipConfigCriteria);
                        webClipQuery.addSelectColumn(new Column((String)null, "*"));
                        final DataObject webClipDO = MDMUtil.getPersistenceLite().get(webClipQuery);
                        final SelectQuery backGroundAppsQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidKioskPolicyBackgroundApps"));
                        backGroundAppsQuery.addJoin(new Join("AndroidKioskPolicyBackgroundApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "AndroidKioskPolicyBackgroundApps", "MDBACKGROUNDAPPGROUPDETAILS", 1));
                        final Criteria backGroupAppsCriteria = new Criteria(new Column("AndroidKioskPolicyBackgroundApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        backGroundAppsQuery.setCriteria(backGroupAppsCriteria);
                        backGroundAppsQuery.addSelectColumn(new Column((String)null, "*"));
                        final DataObject backGroundAppsDO = MDMUtil.getPersistenceLite().get(backGroundAppsQuery);
                        final SelectQuery batteryOptimizedAppsQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidKioskBatteryOptimizedApps"));
                        batteryOptimizedAppsQuery.addJoin(new Join("AndroidKioskBatteryOptimizedApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "AndroidKioskBatteryOptimizedApps", "MDBATTERYOPTIMIZEDAPPS", 1));
                        final Criteria batteryOptimizedAppsCriteria = new Criteria(new Column("AndroidKioskBatteryOptimizedApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        batteryOptimizedAppsQuery.setCriteria(batteryOptimizedAppsCriteria);
                        batteryOptimizedAppsQuery.addSelectColumn(new Column((String)null, "*"));
                        final DataObject batteryOptimizedDO = MDMUtil.getPersistenceLite().get(batteryOptimizedAppsQuery);
                        final SelectQuery screenLayoutQuery = (SelectQuery)new SelectQueryImpl(new Table("ScreenLayoutSettings"));
                        final Table folderPage = new Table("ScreenLayoutPageDetails", "FolderPage");
                        final Table folderPageToPageLayout = new Table("ScreenPageToPageLayout", "FolderPageToPageRel");
                        final Table folderPageLayout = new Table("ScreenPageLayout", "FolderScreenPageLayout");
                        final Table folderPageAppLayout = new Table("ScreenPageLayoutToAppRel", "FolderPageLayoutApp");
                        final Table folderPageLayoutWebClip = new Table("ScreenPageLayoutToWebClipRel", "FolderPageLayoutWebclip");
                        final Table folderWebclipPolicies = new Table("WebClipPolicies", "FolderWebclipPolicies");
                        final Table folderAppGroupDetail = new Table("MdAppGroupDetails", "FolderAppGroupDetails");
                        final Table folderAppGroupPackage = new Table("MdPackageToAppData", "FolderAppPackageToAppData");
                        final Table appGroupDetails = new Table("MdAppGroupDetails", "screenLayoutAppGroup");
                        final Table screenLayoutPackageAppData = new Table("MdPackageToAppData", "ScreenLayoutAppPackageToAppData");
                        final Table screenLayoutWebClip = new Table("WebClipPolicies", "screenLayoutWebClipPolicies");
                        screenLayoutQuery.addJoin(new Join("ScreenLayoutSettings", "ScreenLayout", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 2));
                        screenLayoutQuery.addJoin(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 2));
                        screenLayoutQuery.addJoin(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 2));
                        screenLayoutQuery.addJoin(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 2));
                        screenLayoutQuery.addJoin(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 2));
                        screenLayoutQuery.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToAppRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(new Table("ScreenPageLayoutToAppRel"), appGroupDetails, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToWebClipRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(new Table("ScreenPageLayoutToWebClipRel"), screenLayoutWebClip, new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToFolderRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join("ScreenPageLayoutToFolderRel", "ScreenPageLayoutFolderToPageRel", new String[] { "FOLDER_ID" }, new String[] { "FOLDER_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(new Table("ScreenPageLayoutFolderToPageRel"), folderPage, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPage, folderPageToPageLayout, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPageToPageLayout, folderPageLayout, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPageLayout, folderPageAppLayout, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPageAppLayout, folderAppGroupDetail, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPageLayout, folderPageLayoutWebClip, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderPageLayoutWebClip, folderWebclipPolicies, new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(appGroupDetails, screenLayoutPackageAppData, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery.addJoin(new Join(folderAppGroupDetail, folderAppGroupPackage, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        final Criteria screenLayoutCriteria = new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        screenLayoutQuery.setCriteria(screenLayoutCriteria);
                        screenLayoutQuery.addSelectColumn(new Column((String)null, "*"));
                        final DataObject screenLayoutDO = MDMUtil.getPersistenceLite().get(screenLayoutQuery);
                        if (!webClipDO.isEmpty()) {
                            dataObject.merge(webClipDO);
                        }
                        if (!screenLayoutDO.isEmpty()) {
                            dataObject.merge(screenLayoutDO);
                        }
                        if (!backGroundAppsDO.isEmpty()) {
                            dataObject.merge(backGroundAppsDO);
                        }
                        if (!batteryOptimizedDO.isEmpty()) {
                            dataObject.merge(batteryOptimizedDO);
                            continue;
                        }
                        continue;
                    }
                    case 183: {
                        final Row configDataItemRow = dataObject.getRow("ConfigDataItem");
                        final Long configDataItemId = (Long)configDataItemRow.get("CONFIG_DATA_ITEM_ID");
                        final SelectQuery webClipQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipToConfigRel"));
                        webClipQuery.addJoin(new Join("WebClipToConfigRel", "WebClipPolicies", new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 2));
                        final Criteria webClipConfigCriteria = new Criteria(new Column("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        webClipQuery.setCriteria(webClipConfigCriteria);
                        webClipQuery.addSelectColumn(new Column((String)null, "*"));
                        final DataObject webClipDO = MDMUtil.getPersistenceLite().get(webClipQuery);
                        final SelectQuery screenLayoutQuery2 = (SelectQuery)new SelectQueryImpl(new Table("ScreenLayoutSettings"));
                        final Table folderPage2 = new Table("ScreenLayoutPageDetails", "FolderPage");
                        final Table folderPageToPageLayout2 = new Table("ScreenPageToPageLayout", "FolderPageToPageRel");
                        final Table folderPageLayout2 = new Table("ScreenPageLayout", "FolderScreenPageLayout");
                        final Table folderPageAppLayout2 = new Table("ScreenPageLayoutToAppRel", "FolderPageLayoutApp");
                        final Table folderPageLayoutWebClip2 = new Table("ScreenPageLayoutToWebClipRel", "FolderPageLayoutWebclip");
                        final Table folderWebclipPolicies2 = new Table("WebClipPolicies", "FolderWebclipPolicies");
                        final Table folderAppGroupDetail2 = new Table("MdAppGroupDetails", "FolderAppGroupDetails");
                        final Table folderAppGroupPackage2 = new Table("MdPackageToAppData", "FolderAppPackageToAppData");
                        final Table appGroupDetails2 = new Table("MdAppGroupDetails", "screenLayoutAppGroup");
                        final Table screenLayoutPackageAppData2 = new Table("MdPackageToAppData", "ScreenLayoutAppPackageToAppData");
                        final Table screenLayoutWebClip2 = new Table("WebClipPolicies", "screenLayoutWebClipPolicies");
                        screenLayoutQuery2.addJoin(new Join("ScreenLayoutSettings", "ScreenLayout", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 2));
                        screenLayoutQuery2.addJoin(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 2));
                        screenLayoutQuery2.addJoin(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 2));
                        screenLayoutQuery2.addJoin(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 2));
                        screenLayoutQuery2.addJoin(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 2));
                        screenLayoutQuery2.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToAppRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(new Table("ScreenPageLayoutToAppRel"), appGroupDetails2, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToWebClipRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(new Table("ScreenPageLayoutToWebClipRel"), screenLayoutWebClip2, new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToFolderRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join("ScreenPageLayoutToFolderRel", "ScreenPageLayoutFolderToPageRel", new String[] { "FOLDER_ID" }, new String[] { "FOLDER_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(new Table("ScreenPageLayoutFolderToPageRel"), folderPage2, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPage2, folderPageToPageLayout2, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPageToPageLayout2, folderPageLayout2, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPageLayout2, folderPageAppLayout2, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPageAppLayout2, folderAppGroupDetail2, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPageLayout2, folderPageLayoutWebClip2, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderPageLayoutWebClip2, folderWebclipPolicies2, new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(appGroupDetails2, screenLayoutPackageAppData2, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        screenLayoutQuery2.addJoin(new Join(folderAppGroupDetail2, folderAppGroupPackage2, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
                        final Criteria screenLayoutCriteria2 = new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                        screenLayoutQuery2.setCriteria(screenLayoutCriteria2);
                        screenLayoutQuery2.addSelectColumn(new Column((String)null, "*"));
                        final DataObject screenLayoutDO2 = MDMUtil.getPersistenceLite().get(screenLayoutQuery2);
                        if (!webClipDO.isEmpty()) {
                            dataObject.merge(webClipDO);
                        }
                        if (!screenLayoutDO2.isEmpty()) {
                            dataObject.merge(screenLayoutDO2);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        return dataObject;
    }
    
    static {
        MDMConfigQueryUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
