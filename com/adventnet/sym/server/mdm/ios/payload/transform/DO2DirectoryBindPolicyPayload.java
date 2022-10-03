package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.HashSet;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.dd.plist.NSArray;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.DirectoryBindPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2DirectoryBindPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2DirectoryBindPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final DirectoryBindPolicyPayload payload = new DirectoryBindPolicyPayload(1, "MDM", "com.apple.DirectoryService.managed", "Directory bind Policy");
        final DirectoryBindPolicyPayload[] payloadArray = { null };
        try {
            Iterator iterator = dataObject.getRows("DMManagedDomain");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String domain = (String)row.get("AD_DOMAIN_NAME");
                if (!MDMStringUtils.isEmpty(domain)) {
                    payload.setHostName(domain);
                }
            }
            iterator = dataObject.getRows("DirectoryBindConfig");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String objectName = (String)row.get("OBJECT_NAME");
                if (!MDMStringUtils.isEmpty(objectName)) {
                    payload.setClientID(objectName);
                }
            }
            iterator = dataObject.getRows("DirectoryBindPolicyTemplate");
            Integer type = 1;
            while (iterator.hasNext()) {
                final Row row2 = iterator.next();
                type = (Integer)row2.get("TYPE");
            }
            type.intValue();
            iterator = dataObject.getRows("ADBindPolicyTemplate");
            while (iterator.hasNext()) {
                final Row adRow = iterator.next();
                payload.setADCreateMobileAccountAtLogin((boolean)adRow.get("CREATE_MA_AT_LOGIN"));
                payload.setADWarnUserBeforeCreatingMA((boolean)adRow.get("WARN_BEFORE_MA"));
                payload.setADForceHomeLocal((boolean)adRow.get("FORCE_HOME_LOCAL"));
                payload.setADUseWindowsUNCPath((boolean)adRow.get("WINDOWS_UNC_PATH"));
                payload.setADAllowMultiDomainAuth((boolean)adRow.get("ALLOW_MULTI_DOMAIN_AUTH"));
                final String defaultShell = (String)adRow.get("DEFAULT_SHELL");
                if (!MDMStringUtils.isEmpty(defaultShell)) {
                    payload.setADDefaultUserShell(defaultShell);
                }
                this.setNamespace((int)adRow.get("NAMESPACE"), payload);
                this.setMountStyle((int)adRow.get("MOUNT_STYLE"), payload);
                this.setPacketEncryption((int)adRow.get("PACKET_ENCRYPTION"), payload);
                this.setPacketSign((int)adRow.get("PACKET_SIGN"), payload);
                payload.setADTrustChangePassIntervalDays((int)adRow.get("TRUST_PASS_INTERVAL"));
                final Boolean dcFlag = (Boolean)adRow.get("PREFERRED_DC_FLAG");
                if (dcFlag) {
                    final String dc = (String)adRow.get("PREFERRED_DC");
                    payload.setADPreferredDCServer(dc);
                    payload.setADPreferredDCServerFlag(dcFlag);
                }
            }
            this.setOU(dataObject, payload);
            this.setCredential(dataObject, payload);
            this.setDDNSRestriction(dataObject, payload);
            this.setAdminGroups(dataObject, payload);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:createPayload] ~~~~~~~~~~~~~~~~~~~", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private NSArray getNSArray(final Set objects) {
        final NSArray array = new NSArray(objects.size());
        final Iterator<String> iterator = objects.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            array.setValue(i++, (Object)iterator.next());
        }
        return array;
    }
    
    private void setOU(final DataObject dataObject, final DirectoryBindPolicyPayload payload) {
        try {
            final Iterator iterator = dataObject.getRows("ADBindOU");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String ou = (String)row.get("OU");
                if (!MDMStringUtils.isEmpty(ou)) {
                    payload.setADOrganizationalUnit(ou);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~Error at setOU function[DO2DirectoryBindPolicyPayload.java] ~~~~~~~~~~~~~~~~~~~~~~~~~~~", e);
        }
    }
    
    private void setCredential(final DataObject dataObject, final DirectoryBindPolicyPayload payload) {
        try {
            final Iterator iterator = dataObject.getRows("Credential");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String username = (String)row.get("CRD_USERNAME");
                String password = "";
                if (row.get("CREDENTIAL_ID") != null) {
                    final Long passwordID = (Long)row.get("CREDENTIAL_ID");
                    password = PayloadSecretFieldsHandler.getInstance().constructCredentialPayloadSecretField(passwordID.toString());
                }
                if (!MDMStringUtils.isEmpty(username) && !MDMStringUtils.isEmpty(password)) {
                    payload.setUserName(username);
                    payload.setPassword(password);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~Error at setCredential function[DO2DirectoryBindPolicyPayload.java] ~~~~~~~~~~~~~~~~~~~~~~~~~~~", e);
        }
    }
    
    private void setDDNSRestriction(final DataObject dataObject, final DirectoryBindPolicyPayload payload) {
        try {
            final Iterator iterator = dataObject.getRows("ADBindRestrictedDDNS");
            final Set<String> ddnsSet = new HashSet<String>();
            while (iterator.hasNext()) {
                final Row ddnsRow = iterator.next();
                final String ddns = (String)ddnsRow.get("RESTRICTED_INTERFACE");
                if (ddns != null) {
                    ddnsSet.add(ddns);
                }
            }
            final NSArray restrictedDDNS = this.getNSArray(ddnsSet);
            if (ddnsSet.size() > 0) {
                payload.setADRestrictDDNSFlag(true);
                payload.setADRestrictDDNS(restrictedDDNS);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~Error at setDDNSRestriction function[DO2DirectoryBindPolicyPayload.java] ~~~~~~~~~~~~~~~~~~~~~~~~~~~", e);
        }
    }
    
    private void setAdminGroups(final DataObject dataObject, final DirectoryBindPolicyPayload payload) {
        try {
            final Iterator iterator = dataObject.getRows("ADBindPrivilegeGroup");
            final Set<String> adminSet = new HashSet<String>();
            while (iterator.hasNext()) {
                final Row adminRow = iterator.next();
                final String admin = (String)adminRow.get("GROUP_NAME");
                if (!MDMStringUtils.isEmpty(admin)) {
                    adminSet.add(admin);
                }
            }
            final NSArray adminArray = this.getNSArray(adminSet);
            if (adminSet.size() > 0) {
                payload.setADDomainAdminGroupListFlag(true);
                payload.setADDomainAdminGroupList(adminArray);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~Error at setAdminGroups function[DO2DirectoryBindPolicyPayload.java] ~~~~~~~~~~~~~~~~~~~~~~~~~~~", e);
        }
    }
    
    private void setNamespace(final int namespace, final DirectoryBindPolicyPayload payload) {
        switch (namespace) {
            case 2: {
                payload.setADNamespace("forrest");
                break;
            }
            default: {
                payload.setADNamespace("domain");
                break;
            }
        }
    }
    
    private void setPacketSign(final int sign, final DirectoryBindPolicyPayload payload) {
        switch (sign) {
            case 2: {
                payload.setADPacketSign("disable");
                break;
            }
            case 3: {
                payload.setADPacketSign("require");
                break;
            }
            default: {
                payload.setADPacketSign("allow");
                break;
            }
        }
    }
    
    private void setPacketEncryption(final int encryption, final DirectoryBindPolicyPayload payload) {
        switch (encryption) {
            case 2: {
                payload.setADPacketEncrypt("disable");
                break;
            }
            case 3: {
                payload.setADPacketEncrypt("require");
                break;
            }
            case 4: {
                payload.setADPacketEncrypt("ssl");
                break;
            }
            default: {
                payload.setADPacketEncrypt("allow");
                break;
            }
        }
    }
    
    private void setMountStyle(final int mount, final DirectoryBindPolicyPayload payload) {
        switch (mount) {
            case 2: {
                payload.setADMountStyle("afp");
                break;
            }
            default: {
                payload.setADMountStyle("smb");
                break;
            }
        }
    }
}
