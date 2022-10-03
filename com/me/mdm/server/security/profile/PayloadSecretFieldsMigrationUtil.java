package com.me.mdm.server.security.profile;

import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import java.util.HashSet;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.DataAccessException;
import java.util.Map;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PayloadSecretFieldsMigrationUtil
{
    private static PayloadSecretFieldsMigrationUtil payloadSecretFieldsMigrationUtil;
    public static Logger logger;
    public List<PayloadSecretFieldsTableMapping> secretFieldsMappedTableList;
    
    public static PayloadSecretFieldsMigrationUtil getInstance() {
        if (PayloadSecretFieldsMigrationUtil.payloadSecretFieldsMigrationUtil == null) {
            PayloadSecretFieldsMigrationUtil.payloadSecretFieldsMigrationUtil = new PayloadSecretFieldsMigrationUtil();
        }
        return PayloadSecretFieldsMigrationUtil.payloadSecretFieldsMigrationUtil;
    }
    
    public List<PayloadSecretFieldsTableMapping> getSecretFieldsMappedTableList() {
        return this.secretFieldsMappedTableList;
    }
    
    public PayloadSecretFieldsMigrationUtil() {
        this.secretFieldsMappedTableList = new ArrayList<PayloadSecretFieldsTableMapping>();
        HashMap<String, String> secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("NEW_DEFAULT_PASSCODE", "NEW_DEFAULT_PASSCODE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("AndroidPasscodePolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("INCOMING_SERVER_PASSWORD", "INCOMING_SERVER_PASSWORD_ID");
        secretFieldsColumnsMap.put("OUTGOING_SERVER_PASSWORD", "OUTGOING_SERVER_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("AndroidEMailPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PROXY_PASSWORD", "PROXY_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("WifiPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("WifiNonEnterprise", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        List<Join> joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "WifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        HashMap<String, List<String>> selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("WifiEnterprise", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID", "IDENTITY_CERTIFICATE_ID"));
        List<String> certificateColumns = Arrays.asList("CERTIFICATE_ID", "IDENTITY_CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("WifiEnterprise", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "AndroidActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("AndroidActiveSyncPolicy", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "IDENTITY_CERT_ID"));
        certificateColumns = Arrays.asList("IDENTITY_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("AndroidActiveSyncPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("EXIT_KIOSK_PASSWORD", "EXIT_KIOSK_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("AndroidKioskPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("SMTP_ALT_PASSWORD", "SMTP_ALT_PASSWORD_ID");
        secretFieldsColumnsMap.put("AUTH_SECRET", "AUTH_SECRET_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("WpEmailPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("WpExchangeActiveSyncPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "EMailPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("INCOMING_PASSWORD", "INCOMING_PASSWORD_ID");
        secretFieldsColumnsMap.put("OUTGOING_PASSWORD", "OUTGOING_PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("EMailPolicy", Arrays.asList("CONFIG_DATA_ITEM_ID", "INCOMING_PASSWORD", "INCOMING_PASSWORD_ID", "OUTGOING_PASSWORD", "OUTGOING_PASSWORD_ID", "SIGNING_CERT_ID", "ENCRYPTION_CERT_ID"));
        certificateColumns = Arrays.asList("SIGNING_CERT_ID", "ENCRYPTION_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("EMailPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "ExchangeActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("ExchangeActiveSyncPolicy", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "SIGNING_CERT_ID", "ENCRYPTION_CERT_ID", "IDENTITY_CERT_ID"));
        certificateColumns = Arrays.asList("SIGNING_CERT_ID", "ENCRYPTION_CERT_ID", "IDENTITY_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("ExchangeActiveSyncPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PROXY_PASSWORD", "PROXY_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("GlobalHttpProxyPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("ACCOUNT_PASSWORD", "ACCOUNT_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("LdapPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("ACCOUNT_PASSWORD", "ACCOUNT_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("CardDAVPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("ACCOUNT_PASSWORD", "ACCOUNT_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("CalDAVPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("ACCOUNT_PASSWORD", "ACCOUNT_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("SubscibedCalendarPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "PayloadWifiEnterprise", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PROXY_PASSWORD", "PROXY_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("PayloadProxyConfig", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("PayloadWifiEnterprise", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID", "IDENTITY_CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID", "IDENTITY_CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("PayloadWifiEnterprise", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("ACCESS_POINT_PASSOWRD", "ACCESS_POINT_PASSOWRD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("ApnPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnL2TP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        secretFieldsColumnsMap.put("SHARED_SECRET", "SHARED_SECRET_ID");
        secretFieldsColumnsMap.put("L2TP_SECRET", "L2TP_SECRET_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnL2TP", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "SHARED_SECRET", "SHARED_SECRET_ID", "L2TP_SECRET", "L2TP_SECRET_ID", "USER_CERTIFICATE_ID", "CA_CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("USER_CERTIFICATE_ID", "CA_CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnL2TP", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnCustomSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnCustomSSL", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnCustomSSL", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PROXY_PASSWORD", "PROXY_PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnCisco", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnCisco", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnCisco", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnJuniperSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnJuniperSSL", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnJuniperSSL", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnF5SSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnF5SSL", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnF5SSL", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnPPTP", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnPPTP", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CA_CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CA_CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnPPTP", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnIPSec", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        secretFieldsColumnsMap.put("SHARED_SECRET", "SHARED_SECRET_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnIPSec", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "SHARED_SECRET", "SHARED_SECRET_ID", "CERTIFICATE_ID", "CA_CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID", "CA_CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnIPSec", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnPaloAlto", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnPaloAlto", Arrays.asList("CONFIG_DATA_ITEM_ID", "PASSWORD", "PASSWORD_ID", "CERTIFICATE_ID"));
        certificateColumns = Arrays.asList("CERTIFICATE_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnPaloAlto", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        secretFieldsColumnsMap = new HashMap<String, String>();
        secretFieldsColumnsMap.put("PASSWORD", "PASSWORD_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("OpenVPNPolicy", (HashMap<String, String>)secretFieldsColumnsMap.clone()));
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            secretFieldsColumnsMap = new HashMap<String, String>();
            joinList = new ArrayList<Join>();
            joinList.add(new Join("ConfigDataItem", "VpnToPolicyRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            joinList.add(new Join("VpnToPolicyRel", "VpnIKEv2", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
            secretFieldsColumnsMap.put("EAP_PASSWORD", "EAP_PASSWORD_ID");
            secretFieldsColumnsMap.put("SHARED_SECRET", "SHARED_SECRET_ID");
            selectColumnsMap = new HashMap<String, List<String>>();
            selectColumnsMap.put("VpnToPolicyRel", Arrays.asList("CONFIG_DATA_ITEM_ID", "VPN_POLICY_ID"));
            selectColumnsMap.put("VpnIKEv2", Arrays.asList("VPN_POLICY_ID", "EAP_PASSWORD", "SHARED_SECRET", "SHARED_SECRET_ID", "EAP_PASSWORD_ID", "CA_CERTIFICATE_ID"));
            certificateColumns = Arrays.asList("CA_CERTIFICATE_ID");
            this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnToPolicyRel", (HashMap<String, String>)secretFieldsColumnsMap.clone(), joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        }
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "SSOToCertificateRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("SSOToCertificateRel", Arrays.asList("CONFIG_DATA_ITEM_ID", "CLIENT_CERT_ID"));
        certificateColumns = Arrays.asList("CLIENT_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("SSOToCertificateRel", null, joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "VpnToPolicyRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        joinList.add(new Join("VpnToPolicyRel", "VpnPolicyToCertificate", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1));
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("VpnToPolicyRel", Arrays.asList("CONFIG_DATA_ITEM_ID", "VPN_POLICY_ID"));
        selectColumnsMap.put("VpnPolicyToCertificate", Arrays.asList("VPN_POLICY_ID", "CLIENT_CERT_ID"));
        certificateColumns = Arrays.asList("CLIENT_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("VpnToPolicyRel", null, joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "MacFileVault2Policy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        joinList.add(new Join("MacFileVault2Policy", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("MacFileVault2Policy", Arrays.asList("CONFIG_DATA_ITEM_ID", "ENCRYPTION_SETTINGS_ID"));
        selectColumnsMap.put("MDMFileVaultPersonalKeyConfiguration", Arrays.asList("ENCRYPTION_SETTINGS_ID", "RECOVERY_ENCRYPT_CERT_ID"));
        certificateColumns = Arrays.asList("RECOVERY_ENCRYPT_CERT_ID");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("MacFileVault2Policy", null, joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
        joinList = new ArrayList<Join>();
        joinList.add(new Join("ConfigDataItem", "MacFileVault2Policy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        joinList.add(new Join("MacFileVault2Policy", "MDMFileVaultInstitutionConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        selectColumnsMap = new HashMap<String, List<String>>();
        selectColumnsMap.put("MacFileVault2Policy", Arrays.asList("CONFIG_DATA_ITEM_ID", "ENCRYPTION_SETTINGS_ID"));
        selectColumnsMap.put("MDMFileVaultInstitutionConfiguration", Arrays.asList("ENCRYPTION_SETTINGS_ID", "INSTITUTION_ENCRYPTION_CERT"));
        certificateColumns = Arrays.asList("INSTITUTION_ENCRYPTION_CERT");
        this.secretFieldsMappedTableList.add(new PayloadSecretFieldsTableMapping("MacFileVault2Policy", null, joinList, (HashMap<String, List<String>>)selectColumnsMap.clone(), certificateColumns));
    }
    
    public static List<Integer> getSecretFieldConfigIds() {
        final List<Integer> configIds = new ArrayList<Integer>();
        configIds.add(185);
        configIds.add(553);
        configIds.add(556);
        configIds.add(554);
        configIds.add(557);
        configIds.add(559);
        configIds.add(562);
        configIds.add(564);
        configIds.add(555);
        configIds.add(566);
        configIds.add(605);
        configIds.add(609);
        configIds.add(602);
        configIds.add(603);
        configIds.add(607);
        configIds.add(606);
        configIds.add(774);
        configIds.add(177);
        configIds.add(176);
        configIds.add(766);
        configIds.add(521);
        configIds.add(756);
        configIds.add(174);
        configIds.add(175);
        configIds.add(184);
        configIds.add(768);
        configIds.add(178);
        configIds.add(179);
        configIds.add(181);
        configIds.add(180);
        configIds.add(187);
        configIds.add(771);
        configIds.add(515);
        configIds.add(772);
        configIds.add(516);
        configIds.add(773);
        configIds.add(770);
        configIds.add(520);
        configIds.add(702);
        configIds.add(701);
        configIds.add(704);
        configIds.add(703);
        return configIds;
    }
    
    public static SelectQuery getCommonProfileConfigurationSelectQuery(final Long customerId) {
        final SelectQuery selectQuery = ProfileUtil.getProfileToConfigIdQuery();
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
        final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(customerCriteria);
        return selectQuery;
    }
    
    private static Boolean migrateSecretFieldColumn(final Row row, final String columnNameToBeMigrated, final String columnNameToWhichMigrate, final DataObject secretFieldsDO, final Map managedPasswordsForCustomer, final Long customerId, final Long userId) throws DataAccessException {
        Boolean passwordIdColumnToUpdate = Boolean.FALSE;
        if (row.get(columnNameToBeMigrated) == null || ((String)row.get(columnNameToBeMigrated)).isEmpty()) {
            row.set(columnNameToBeMigrated, (Object)"");
            row.set(columnNameToWhichMigrate, (Object)null);
        }
        else {
            final String columnValueToBeMigrated = (String)row.get(columnNameToBeMigrated);
            Object passwordId;
            if (managedPasswordsForCustomer.containsKey(columnValueToBeMigrated)) {
                passwordId = managedPasswordsForCustomer.get(columnValueToBeMigrated);
            }
            else {
                final Row managedPasswordRow = new Row("MDMManagedPassword");
                managedPasswordRow.set("PASSWORD", (Object)columnValueToBeMigrated);
                managedPasswordRow.set("CUSTOMER_ID", (Object)customerId);
                managedPasswordRow.set("ADDED_BY", (Object)userId);
                managedPasswordRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                secretFieldsDO.addRow(managedPasswordRow);
                passwordId = managedPasswordRow.get("MANAGED_PASSWORD_ID");
                managedPasswordsForCustomer.put(columnValueToBeMigrated, passwordId);
            }
            row.set(columnNameToBeMigrated, (Object)columnValueToBeMigrated);
            row.set(columnNameToWhichMigrate, passwordId);
            passwordIdColumnToUpdate = Boolean.TRUE;
        }
        return passwordIdColumnToUpdate;
    }
    
    public static void migrateSecretFieldColumns(final Row row, final List<String> secretFields, final HashMap<String, String> secretColumnsMap, final DataObject secretFieldsDO, final Map managedPasswordsForCustomer, final Long customerId, final Long userId, final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping, final Set<Long> collectionList, final String tableName) throws DataAccessException {
        Boolean rowUpdatedWithPasswordId = Boolean.FALSE;
        for (final String columnNameToBeMigrated : secretFields) {
            final String columnNameToWhichMigrate = secretColumnsMap.get(columnNameToBeMigrated);
            final Boolean columnUpdatedWithPasswordId = migrateSecretFieldColumn(row, columnNameToBeMigrated, columnNameToWhichMigrate, secretFieldsDO, managedPasswordsForCustomer, customerId, userId);
            rowUpdatedWithPasswordId = (columnUpdatedWithPasswordId || rowUpdatedWithPasswordId);
        }
        secretFieldsDO.updateRow(row);
        if (rowUpdatedWithPasswordId || (payloadSecretFieldsTableMapping.checkIfTableHasCertificateColumn() && checkIfCertificateFieldPresent(row, payloadSecretFieldsTableMapping.getCertificateListFromMap(tableName), payloadSecretFieldsTableMapping))) {
            setCollectionIdToMigrate(secretFieldsDO, row, collectionList, payloadSecretFieldsTableMapping);
        }
    }
    
    private static void setCollectionIdToMigrate(final DataObject secretFieldsDO, final Row criteriaRow, final Set<Long> collectionList, final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping) throws DataAccessException {
        final ArrayList<String> tableNames = new ArrayList<String>();
        tableNames.add("CfgDataToCollection");
        tableNames.add("ConfigDataItem");
        tableNames.addAll(payloadSecretFieldsTableMapping.getTables());
        final DataObject collectionDO = secretFieldsDO.getDataObject((List)tableNames, criteriaRow);
        if (collectionDO != null && !collectionDO.isEmpty() && collectionDO.containsTable("CfgDataToCollection")) {
            final Iterator collectionRowsIterator = collectionDO.getRows("CfgDataToCollection");
            while (collectionRowsIterator.hasNext()) {
                final Row row = collectionRowsIterator.next();
                final Long collectionId = (Long)row.get("COLLECTION_ID");
                collectionList.add(collectionId);
            }
        }
    }
    
    public static void resetSecretFieldColumn(final Row row, final List<String> secretFields, final DataObject secretFieldsDO) throws DataAccessException {
        for (final String columnNameToReset : secretFields) {
            if (row.get(columnNameToReset) != null && !((String)row.get(columnNameToReset)).isEmpty()) {
                row.set(columnNameToReset, (Object)"");
            }
        }
        secretFieldsDO.updateRow(row);
    }
    
    public static void updateDynamicVariableForCommand(final Set<Long> collectionIds) throws DataAccessException {
        if (!collectionIds.isEmpty()) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdCommands");
            updateQuery.addJoin(new Join("MdCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria collectionIdsCriteria = new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
            final Criteria dynamicVariableCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_DYNAMIC_VARIABLE"), (Object)Boolean.FALSE, 0);
            updateQuery.setCriteria(collectionIdsCriteria.and(dynamicVariableCriteria));
            updateQuery.setUpdateColumn("COMMAND_DYNAMIC_VARIABLE", (Object)Boolean.TRUE);
            DataAccess.update(updateQuery);
        }
    }
    
    public static Map<String, Map<String, String>> getCredentialIDsForPasswords(final List<Long> passwordIDs, final Long customerID) {
        final Map<String, Map<String, String>> managedPasswords = new HashMap<String, Map<String, String>>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Credential"));
            final Criteria customerCriteria = new Criteria(new Column("Credential", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria credentialIDCriteria = new Criteria(new Column("Credential", "CREDENTIAL_ID"), (Object)passwordIDs.toArray(), 8);
            selectQuery.setCriteria(customerCriteria.and(credentialIDCriteria));
            selectQuery.addSelectColumn(new Column("Credential", "CRD_PASSWORD"));
            selectQuery.addSelectColumn(new Column("Credential", "CREDENTIAL_ID"));
            selectQuery.addSelectColumn(new Column("Credential", "CRD_ENC_TYPE"));
            final DataObject managedPasswordDO = MDMUtil.getPersistence().get(selectQuery);
            if (managedPasswordDO != null && !managedPasswordDO.isEmpty()) {
                for (final Long passwordId : passwordIDs) {
                    final Criteria passwordIDCriteria = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)passwordId, 0);
                    final Row matchingRow = managedPasswordDO.getRow("Credential", passwordIDCriteria);
                    if (matchingRow != null) {
                        final String passwordStr = (String)matchingRow.get("CRD_PASSWORD");
                        final Integer encryption = (Integer)matchingRow.get("CRD_ENC_TYPE");
                        final Map<String, String> innerMap = new HashMap<String, String>();
                        innerMap.put("CRD_ENC_TYPE", encryption.toString());
                        innerMap.put("CRD_PASSWORD", passwordStr);
                        managedPasswords.put(passwordId.toString(), innerMap);
                    }
                }
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsMigrationUtil.logger.log(Level.SEVERE, "Exception in getCredentailIDsForPasswords() ", ex);
        }
        return managedPasswords;
    }
    
    public static Set<Long> getCollIDsForOtherSecretFieldPolicies() throws Exception {
        final Set<Long> collectionIDList = new HashSet<Long>();
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(new Table("CertificatePolicy"));
        selectQuery1.addSelectColumn(new Column("CertificatePolicy", "CONFIG_DATA_ITEM_ID"));
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("SCEPPolicy"));
        selectQuery2.addSelectColumn(new Column("SCEPPolicy", "CONFIG_DATA_ITEM_ID"));
        final SelectQuery selectQuery3 = (SelectQuery)new SelectQueryImpl(new Table("DirectoryBindConfig"));
        selectQuery3.addSelectColumn(new Column("DirectoryBindConfig", "CONFIG_DATA_ITEM_ID"));
        final UnionQuery query1 = (UnionQuery)new UnionQueryImpl((Query)selectQuery1, (Query)selectQuery2, false);
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)query1, (Query)selectQuery3, false);
        final DerivedTable derivedTable = new DerivedTable("SUB_TABLE", (Query)unionQuery);
        final SelectQuery selectQuery4 = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        selectQuery4.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery4.addJoin(new Join("CfgDataToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery4.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        selectQuery4.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
        selectQuery4.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        selectQuery4.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
        selectQuery4.addSelectColumn(new Column("CollectionStatus", "COLLECTION_ID"));
        selectQuery4.addSelectColumn(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
        final Table configDataItemTable = Table.getTable("ConfigDataItem");
        selectQuery4.addJoin(new Join(configDataItemTable, (Table)derivedTable, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria publishedCollection = new Criteria(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0);
        selectQuery4.setCriteria(publishedCollection);
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery4);
        if (dmDataSetWrapper != null) {
            while (dmDataSetWrapper.next()) {
                final Long collectionId = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
                collectionIDList.add(collectionId);
            }
        }
        return collectionIDList;
    }
    
    public static Iterator<Row> getCertificateCredentialsInfo(final List<Long> certificateIds, final Long customerId) {
        Iterator<Row> certificatesRows = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
            final Criteria customerCriteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria certificateIDCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certificateIds.toArray(), 8);
            selectQuery.setCriteria(customerCriteria.and(certificateIDCriteria));
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"));
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"));
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "CERTIFICATE_PASSWORD"));
            final DataObject certificatesDO = MDMUtil.getPersistence().get(selectQuery);
            if (certificatesDO != null && !certificatesDO.isEmpty()) {
                certificatesRows = certificatesDO.getRows("CredentialCertificateInfo");
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsMigrationUtil.logger.log(Level.SEVERE, "Exception in getCertificateCredentialsInfo() ", ex);
        }
        return certificatesRows;
    }
    
    public static Map<String, String> getSCEPChallenge(final List<Long> scepConfigIds) {
        final Map<String, String> scepChallengeMap = new HashMap<String, String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPConfigurations"));
            final Criteria scepIDCriteria = new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepConfigIds.toArray(), 8);
            selectQuery.setCriteria(scepIDCriteria);
            selectQuery.addSelectColumn(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("SCEPConfigurations", "CHALLENGE_ENCRYPTED"));
            final DataObject scepDO = MDMUtil.getPersistence().get(selectQuery);
            if (scepDO != null && !scepDO.isEmpty()) {
                for (final Long scepConfigId : scepConfigIds) {
                    final Criteria scepConfigIDCriteria = new Criteria(Column.getColumn("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepConfigId, 0);
                    final Row matchingRow = scepDO.getRow("SCEPConfigurations", scepConfigIDCriteria);
                    if (matchingRow != null) {
                        final String scepChallenge = (String)matchingRow.get("CHALLENGE_ENCRYPTED");
                        scepChallengeMap.put(scepConfigId.toString(), scepChallenge);
                    }
                }
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsMigrationUtil.logger.log(Level.SEVERE, "Exception in getSCEPChallenge() ", ex);
        }
        return scepChallengeMap;
    }
    
    public static Boolean checkIfCertificateFieldPresent(final Row row, final List<String> certificateColumns, final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping) {
        if (payloadSecretFieldsTableMapping.checkIfTableHasCertificateColumn() && certificateColumns != null) {
            for (final String certificateColumn : certificateColumns) {
                if (row.get(certificateColumn) != null && (long)row.get(certificateColumn) != -1L) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    public static void getCollnIdForCertificate(final Row row, final List<String> certificateColumns, final PayloadSecretFieldsTableMapping payloadSecretFieldsTableMapping, final Set<Long> collectionList, final DataObject secretFieldsDO) throws DataAccessException {
        if (checkIfCertificateFieldPresent(row, certificateColumns, payloadSecretFieldsTableMapping)) {
            setCollectionIdToMigrate(secretFieldsDO, row, collectionList, payloadSecretFieldsTableMapping);
        }
    }
    
    static {
        PayloadSecretFieldsMigrationUtil.payloadSecretFieldsMigrationUtil = null;
        PayloadSecretFieldsMigrationUtil.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
