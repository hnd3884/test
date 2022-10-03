package com.me.mdm.server.windows.profile.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.windows.profile.payload.content.security.AppLock;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import org.json.JSONObject;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsNetworkIsolationPayload;
import com.me.mdm.server.windows.profile.payload.WinEDPPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsEDPPolicy extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WinEDPPayload winEDPPayload = new WinEDPPayload();
        final WindowsNetworkIsolationPayload windowsNetworkIsolationPayload = new WindowsNetworkIsolationPayload();
        try {
            final Iterator iterator = dataObject.getRows("DataProtectionPolicy");
            while (iterator.hasNext()) {
                final Row policyRow = iterator.next();
                final Long policyID = (Long)policyRow.get("POLICY_ID");
                final Row corporatePolicyRow = dataObject.getRow("CorporatePolicy", new Criteria(Column.getColumn("CorporatePolicy", "POLICY_ID"), (Object)policyID, 0));
                final List<Long> ruleIDList = new ArrayList<Long>();
                final Iterator ruleIterator = dataObject.getRows("RuleToPolicy", new Criteria(Column.getColumn("RuleToPolicy", "POLICY_ID"), corporatePolicyRow.get("POLICY_ID"), 0));
                while (ruleIterator.hasNext()) {
                    final Row row = ruleIterator.next();
                    final Long ruleID = (Long)row.get("RULE_ID");
                    ruleIDList.add(ruleID);
                }
                final List<String> enterpriseDomainName = new ArrayList<String>();
                final List<String> enterpriseProtectedDomainName = new ArrayList<String>();
                final List<String> enterpriseIPRange = new ArrayList<String>();
                final List<String> enterpriseCloudResources = new ArrayList<String>();
                final List<String> internalProxyServers = new ArrayList<String>();
                final List<String> proxyServers = new ArrayList<String>();
                final List<String> neutralResources = new ArrayList<String>();
                final Iterator networkRules = dataObject.getRows("EnterpriseNetworkLimit", new Criteria(Column.getColumn("EnterpriseNetworkLimit", "RULE_ID"), (Object)ruleIDList.toArray(), 8));
                String primaryDomain = "";
                while (networkRules.hasNext()) {
                    final Row networkRow = networkRules.next();
                    final Integer type = (Integer)networkRow.get("RULE_TYPE");
                    final String value = (String)networkRow.get("VALUE");
                    switch (type) {
                        case 4: {
                            enterpriseCloudResources.add(value);
                            continue;
                        }
                        case 5: {
                            enterpriseIPRange.add(value);
                            continue;
                        }
                        case 3: {
                            enterpriseProtectedDomainName.add(value);
                            continue;
                        }
                        case 1: {
                            primaryDomain = value;
                            continue;
                        }
                        case 2: {
                            enterpriseDomainName.add(value);
                            continue;
                        }
                        case 6: {
                            internalProxyServers.add(value);
                            continue;
                        }
                        case 7: {
                            proxyServers.add(value);
                            continue;
                        }
                        case 8: {
                            neutralResources.add(value);
                            continue;
                        }
                    }
                }
                enterpriseDomainName.add(0, primaryDomain);
                final Iterator appRules = dataObject.getRows("EnterpriseApplications", new Criteria(Column.getColumn("EnterpriseApplications", "RULE_ID"), (Object)ruleIDList.toArray(), 8));
                final List<JSONObject> modernApps = new ArrayList<JSONObject>();
                final List<String> legacyApps = new ArrayList<String>();
                while (appRules.hasNext()) {
                    final Row row2 = appRules.next();
                    final Integer type2 = (Integer)row2.get("APP_TYPE");
                    final String appIdentifier = (String)row2.get("APP_IDENTIFIER");
                    switch (type2) {
                        case 1: {
                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("productName", (Object)appIdentifier);
                            modernApps.add(jsonObject);
                            continue;
                        }
                        case 2: {
                            legacyApps.add(appIdentifier);
                            continue;
                        }
                    }
                }
                final Row windowsConfigRow = dataObject.getRow("WindowsEnterpriseConfig", new Criteria(Column.getColumn("WindowsEnterpriseConfig", "RULE_ID"), (Object)ruleIDList.toArray(), 8));
                final Boolean allowUserDecrypt = (Boolean)windowsConfigRow.get("ALLOW_USER_DECRYPTION");
                final Boolean revokeOnUnenroll = (Boolean)windowsConfigRow.get("REVOKE_ON_UNENROLL");
                final Long dataRecoveryCert = (Long)windowsConfigRow.get("DATA_RECOVERY_CERT_ID");
                final Integer enforcementLevel = (Integer)windowsConfigRow.get("ENFORCEMENT_LEVEL");
                final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                final String certBlob = PayloadSecretFieldsHandler.getInstance().constructRecoverCertificate(dataRecoveryCert.toString());
                windowsNetworkIsolationPayload.setEnterpriseCloudResources(enterpriseCloudResources);
                windowsNetworkIsolationPayload.setEnterpriseIPRange(enterpriseIPRange);
                windowsNetworkIsolationPayload.setEnterpriseNetworkDomainNames(enterpriseDomainName);
                windowsNetworkIsolationPayload.setNeutralResources(neutralResources);
                windowsNetworkIsolationPayload.setInternalProxyResources(internalProxyServers);
                windowsNetworkIsolationPayload.setProxyServer(proxyServers);
                winEDPPayload.getReplacePayloadCommand().setRequestItems(windowsNetworkIsolationPayload.getReplacePayloadCommand().getRequestItems());
                winEDPPayload.setEDPEnforcementLevel(enforcementLevel);
                winEDPPayload.setDataRecoveryBlob(certBlob);
                winEDPPayload.setshowEDPIcon(Boolean.TRUE);
                winEDPPayload.setEnterpriseProtectedDomains(enterpriseProtectedDomainName);
                if (modernApps.size() != 0) {
                    final String appData = new AppLock().createAppLockXML(modernApps, 1, Boolean.TRUE);
                    winEDPPayload.setAppLockPolicyBlob(appData, "StoreApps");
                }
                if (legacyApps.size() != 0) {
                    final String legacyAppsData = new AppLock().createAppLockXML(modernApps, 2, Boolean.TRUE);
                    winEDPPayload.setAppLockPolicyBlob(legacyAppsData, "EXE");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "cannot configure windows EDP payload : ", e);
        }
        return winEDPPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WinEDPPayload winEDPPayload = new WinEDPPayload();
        winEDPPayload.setEDPEnforcementLevel(0);
        return winEDPPayload;
    }
}
