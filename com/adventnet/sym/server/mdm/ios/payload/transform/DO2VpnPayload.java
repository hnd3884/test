package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.dd.plist.NSDictionary;
import java.util.List;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.ios.payload.VPNPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.PerAppVPNPolicyPayload;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.ios.payload.VPNPayLoadType;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import java.util.logging.Logger;

public class DO2VpnPayload implements DO2Payload
{
    public Logger logger;
    private static final int PASSWORD = 0;
    private static final int CERTIFICATE = 1;
    private static final int PASSWORDCERTIFICATE = 2;
    protected IOSPayload[] payloadArray;
    
    public DO2VpnPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.payloadArray = null;
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        VPNPayLoadType vpnPayload = null;
        this.payloadArray = new IOSPayload[1];
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("VpnPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final Long configDataItemID = (Long)payloadRow.get("CONFIG_DATA_ITEM_ID");
                    final String connectionName = (String)payloadRow.get("CONNECTION_NAME");
                    Integer connectionType = (Integer)payloadRow.get("CONNECTION_TYPE");
                    final Boolean isOnDemandEnabled = (Boolean)payloadRow.get("ENABLE_VPN_ON_DEMAND");
                    final Integer vpnType = (Integer)payloadRow.get("VPN_TYPE");
                    if (vpnType != null && vpnType == 2) {
                        vpnPayload = this.getPerAppVPNPayload();
                    }
                    else {
                        vpnPayload = this.getVPNPayload();
                    }
                    if (!MDMStringUtils.isEmpty(connectionName)) {
                        vpnPayload.setUserDefinedName(connectionName);
                    }
                    ++connectionType;
                    vpnPayload.initializeDicts(connectionType);
                    vpnPayload = this.createParticularVPNPayload(dataObject, vpnPayload, configDataItemID, connectionType);
                    vpnPayload = this.createProxyPayload(vpnPayload, payloadRow);
                    if (isOnDemandEnabled) {
                        vpnPayload = this.createOnDemandVpnPayload(vpnPayload, configDataItemID, dataObject);
                    }
                    if (vpnType != null && vpnType == 2) {
                        this.createPerAppVPN(configDataItemID, dataObject, vpnPayload);
                    }
                    vpnPayload = this.addVendorConfig(dataObject, vpnPayload);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while creating vpn policy");
        }
        this.payloadArray[0] = vpnPayload;
        return this.payloadArray;
    }
    
    protected VPNPayLoadType getPerAppVPNPayload() {
        return new PerAppVPNPolicyPayload(1, "MDM", "com.mdm.mobiledevice.vpn.applayer", "Per-App VPN Profile Configuration");
    }
    
    protected VPNPayLoadType getVPNPayload() {
        return new VPNPolicyPayload(1, "MDM", "com.mdm.mobiledevice.vpn", "VPN Profile Configuration");
    }
    
    private VPNPayLoadType createParticularVPNPayload(final DataObject dataObject, VPNPayLoadType vpnPayload, final Long configDataItemID, final int connectionType) {
        switch (connectionType) {
            case 1: {
                vpnPayload = this.createL2TPPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 2: {
                vpnPayload = this.createPPTPPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 3: {
                vpnPayload = this.createIPSecPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 4:
            case 10:
            case 11: {
                vpnPayload = this.createCiscoAnyConnectPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 5:
            case 8: {
                vpnPayload = this.createJuniperPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 6:
            case 12:
            case 13: {
                vpnPayload = this.createF5SSLPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 7: {
                vpnPayload = this.createCustomSSLPayload(vpnPayload, configDataItemID, dataObject);
                break;
            }
            case 9: {
                vpnPayload = this.createIKEv2Payload(vpnPayload, configDataItemID, dataObject);
                vpnPayload.setVPNType("IKEv2");
                break;
            }
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createProxyPayload(final VPNPayLoadType vpnPayload, final Row payloadRow) {
        try {
            final Integer proxyType = (Integer)payloadRow.get("PROXY_TYPE");
            if (proxyType != null && proxyType == 1) {
                final String proxyServer = (String)payloadRow.get("PROXY_SERVER");
                final Integer proxyServerPort = (Integer)payloadRow.get("PROXY_SERVER_PORT");
                final String userName = (String)payloadRow.get("PROXY_USER_NAME");
                String password = "";
                if (payloadRow.get("PROXY_PASSWORD_ID") != null) {
                    final Long proxyPasswordID = (Long)payloadRow.get("PROXY_PASSWORD_ID");
                    password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(proxyPasswordID.toString());
                }
                vpnPayload.setHTTPEnable(1);
                vpnPayload.setHTTPProxy(proxyServer);
                vpnPayload.setHTTPPort(proxyServerPort);
                vpnPayload.setHTTPProxyUsername(userName);
                vpnPayload.setHTTPProxyPassword(password);
            }
            else if (proxyType != null && proxyType == 2) {
                vpnPayload.setProxyAutoConfigEnable(1);
                final String pacURL = (String)payloadRow.get("PROXY_PAC_URL");
                vpnPayload.setProxyAutoConfigURLString(pacURL);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createOnDemandVpnPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) throws DataAccessException {
        try {
            vpnPayload.setOnDemandEnabled(1);
            this.createOnDemandDictRules(configDataItemID, dataObject, vpnPayload);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createL2TPPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnL2TP", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnL2TP", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final Integer userAuthenticationType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    String sharedSecret = "";
                    final Boolean sendTraffic = (Boolean)payloadRow.get("SEND_ALL_NW_TRAFFIC");
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    if (payloadRow.get("SHARED_SECRET_ID") != null) {
                        final Long sharedSecretID = (Long)payloadRow.get("SHARED_SECRET_ID");
                        sharedSecret = PayloadSecretFieldsHandler.getInstance().constructEncodedPayloadSecretField(sharedSecretID.toString());
                    }
                    vpnPayload.setVPNType("L2TP");
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setAuthName(account);
                    }
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setCommRemoteAddress(serverName.trim());
                    }
                    if (sendTraffic) {
                        vpnPayload.setOverridePrimary(1);
                    }
                    else {
                        vpnPayload.setOverridePrimary(0);
                    }
                    if (userAuthenticationType == 1) {
                        vpnPayload.setRSASecurID();
                        vpnPayload.setTokenCard(true);
                    }
                    else if (userAuthenticationType == 0 && !MDMStringUtils.isEmpty(password)) {
                        vpnPayload.setAuthPassword(password);
                        vpnPayload.setTokenCard(false);
                    }
                    if (!MDMStringUtils.isEmpty(sharedSecret)) {
                        vpnPayload.setAuthenticationMethod("SharedSecret");
                        vpnPayload.setSharedSecret(sharedSecret);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createPPTPPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnPPTP", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnPPTP", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final Integer encryptionLevel = (Integer)payloadRow.get("ENCRYPTION_LEVEL");
                    final Boolean sendTraffic = (Boolean)payloadRow.get("SEND_ALL_NW_TRAFFIC");
                    final Integer userAuthenticationType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    String password = (String)payloadRow.get("PASSWORD");
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    vpnPayload.setVPNType("PPTP");
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setCommRemoteAddress(serverName.trim());
                    }
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setAuthName(account);
                    }
                    if (sendTraffic) {
                        vpnPayload.setOverridePrimary(1);
                    }
                    else {
                        vpnPayload.setOverridePrimary(0);
                    }
                    if (userAuthenticationType == 1) {
                        vpnPayload.setRSASecurID();
                        vpnPayload.setTokenCard(true);
                    }
                    else if (userAuthenticationType == 0 && !MDMStringUtils.isEmpty(password)) {
                        vpnPayload.setAuthPassword(password);
                        vpnPayload.setTokenCard(false);
                    }
                    if (encryptionLevel == 0) {
                        vpnPayload.setCCPEnabled(false);
                        vpnPayload.setCCPMPPE40Enabled(false);
                        vpnPayload.setCCPMPPE128Enabled(false);
                    }
                    else if (encryptionLevel == 1) {
                        vpnPayload.setCCPEnabled(true);
                        vpnPayload.setCCPMPPE40Enabled(true);
                        vpnPayload.setCCPMPPE128Enabled(false);
                    }
                    else {
                        vpnPayload.setCCPEnabled(true);
                        vpnPayload.setCCPMPPE40Enabled(false);
                        vpnPayload.setCCPMPPE128Enabled(true);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createIPSecPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnIPSec", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnIPSec", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    String groupName = (String)payloadRow.get("GROUP_NAME");
                    final Integer authType = (Integer)payloadRow.get("MACHINE_AUTHENTICATION");
                    final Boolean includeUserPIN = (Boolean)payloadRow.get("INCLUDE_USER_PIN");
                    final Boolean promptForPassword = (Boolean)payloadRow.get("PROMPT_FOR_PASSWORD");
                    String sharedSecret = "";
                    final Boolean useHybridAuth = (Boolean)payloadRow.get("USE_HYBRID_AUTH");
                    final String certificateID = String.valueOf(payloadRow.get("CERTIFICATE_ID"));
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    if (payloadRow.get("SHARED_SECRET_ID") != null) {
                        final Long sharedSecretID = (Long)payloadRow.get("SHARED_SECRET_ID");
                        sharedSecret = PayloadSecretFieldsHandler.getInstance().constructEncodedPayloadSecretField(sharedSecretID.toString());
                    }
                    vpnPayload.setVPNType("IPSec");
                    vpnPayload.setOverridePrimary(1);
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setRemoteAddress(serverName.trim());
                    }
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setXAuthName(account);
                        vpnPayload.setXAuthEnabled(1);
                    }
                    if (authType == 0) {
                        vpnPayload.setAuthenticationMethod("SharedSecret");
                        if (!MDMStringUtils.isEmpty(sharedSecret)) {
                            vpnPayload.setSharedSecret(sharedSecret);
                        }
                        if (!MDMStringUtils.isEmpty(groupName)) {
                            if (useHybridAuth) {
                                groupName += "[hybrid]";
                            }
                            vpnPayload.setLocalIdentifier(groupName);
                            vpnPayload.setLocalIdentifierType("KeyID");
                        }
                        if (promptForPassword) {
                            vpnPayload.setXAuthPasswordEncryption(promptForPassword);
                        }
                    }
                    else {
                        vpnPayload.setAuthenticationMethod("Certificate");
                        vpnPayload.setPromptForVPNPIN(includeUserPIN);
                        vpnPayload.setOnDemandEnable(0);
                    }
                    if (!MDMStringUtils.isEmpty(password)) {
                        vpnPayload.setXAuthPassword(password);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createJuniperPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnJuniperSSL", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnJuniperSSL", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String authName = (String)payloadRow.get("ACCOUNT");
                    String authPassword = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long password_Id = (Long)payloadRow.get("PASSWORD_ID");
                        authPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(password_Id.toString());
                    }
                    final String remoteAddress = (String)payloadRow.get("SERVER_NAME");
                    final Integer authType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    final String realm = (String)payloadRow.get("REALM");
                    final String certificateID = String.valueOf(payloadRow.get("CERTIFICATE_ID"));
                    final String role = (String)payloadRow.get("ROLE");
                    vpnPayload.setVPNType("VPN");
                    vpnPayload.setOverridePrimary(1);
                    if (!MDMStringUtils.isEmpty(remoteAddress)) {
                        vpnPayload.setRemoteAddress(remoteAddress);
                    }
                    if (!MDMStringUtils.isEmpty(authName)) {
                        vpnPayload.setAuthName(authName);
                    }
                    if (!MDMStringUtils.isEmpty(realm)) {
                        vpnPayload.setRealm(realm);
                    }
                    if (role != null) {
                        vpnPayload.setRole(role);
                    }
                    if (authType == 0) {
                        vpnPayload.setAuthenticationMethod("Password");
                        if (MDMStringUtils.isEmpty(authPassword)) {
                            continue;
                        }
                        vpnPayload.setAuthPassword(authPassword);
                    }
                    else {
                        vpnPayload.setAuthenticationMethod("Certificate");
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createCiscoAnyConnectPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnCisco", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnCisco", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    final String groupName = (String)payloadRow.get("GROUP_NAME");
                    final Integer authType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    String password = (String)payloadRow.get("PASSWORD");
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    final String certificateID = String.valueOf(payloadRow.get("CERTIFICATE_ID"));
                    vpnPayload.setVPNType("VPN");
                    vpnPayload.setOverridePrimary(1);
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setRemoteAddress(serverName.trim());
                    }
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setAuthName(account);
                    }
                    if (!MDMStringUtils.isEmpty(groupName)) {
                        vpnPayload.setGroupForConnection(groupName);
                    }
                    if (authType == 0) {
                        vpnPayload.setAuthenticationMethod("Password");
                        if (MDMStringUtils.isEmpty(password)) {
                            continue;
                        }
                        vpnPayload.setAuthPassword(password);
                    }
                    else {
                        vpnPayload.setAuthenticationMethod("Certificate");
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createF5SSLPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnF5SSL", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnF5SSL", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    final Integer authType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long incomingServerPasswordId = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(incomingServerPasswordId.toString());
                    }
                    final String certificateID = String.valueOf(payloadRow.get("CERTIFICATE_ID"));
                    vpnPayload.setVPNType("VPN");
                    vpnPayload.setOverridePrimary(1);
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setRemoteAddress(serverName.trim());
                    }
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setAuthName(account);
                    }
                    if (authType == 0) {
                        vpnPayload.setAuthenticationMethod("Password");
                        if (MDMStringUtils.isEmpty(password)) {
                            continue;
                        }
                        vpnPayload.setAuthPassword(password);
                    }
                    else {
                        vpnPayload.setAuthenticationMethod("Certificate");
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception in createF5SSLPayload", exp);
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createCustomSSLPayload(final VPNPayLoadType vpnPayload, final Long configDataItemID, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnCustomSSL", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("VpnCustomSSL", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String account = (String)payloadRow.get("ACCOUNT");
                    final String serverName = (String)payloadRow.get("SERVER_NAME");
                    final Integer authType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                    String password = (String)payloadRow.get("PASSWORD");
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long password_id = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(password_id.toString());
                    }
                    final String identifier = (String)payloadRow.get("IDENTIFIER");
                    final String providerIdentifier = (String)payloadRow.get("PROVIDER_IDENTIFIER");
                    vpnPayload.setVPNType("VPN");
                    vpnPayload.setOverridePrimary(1);
                    if (!MDMStringUtils.isEmpty(identifier)) {
                        vpnPayload.setCustomSSLIdentifier(identifier);
                    }
                    if (!MDMStringUtils.isEmpty(serverName)) {
                        vpnPayload.setRemoteAddress(serverName.trim());
                    }
                    if (!MDMStringUtils.isEmpty(account)) {
                        vpnPayload.setAuthName(account);
                    }
                    if (!MDMStringUtils.isEmpty(providerIdentifier)) {
                        vpnPayload.setProviderBundleIdentifier(providerIdentifier);
                    }
                    switch (authType) {
                        case 0: {
                            vpnPayload.setAuthenticationMethod("Password");
                            if (!MDMStringUtils.isEmpty(password)) {
                                vpnPayload.setAuthPassword(password);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            vpnPayload.setAuthenticationMethod("Certificate");
                            continue;
                        }
                        case 2: {
                            vpnPayload.setAuthenticationMethod("Password+Certificate");
                            if (!MDMStringUtils.isEmpty(password)) {
                                vpnPayload.setAuthPassword(password);
                                continue;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception in customsslpayload", exp);
        }
        return vpnPayload;
    }
    
    private VPNPayLoadType createIKEv2Payload(final VPNPayLoadType vpnPayload, final Long configDataItemId, final DataObject dataObject) {
        try {
            if (dataObject != null) {
                final Criteria vpnToPlicyCriteria = new Criteria(new Column("VpnToPolicyRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                final Iterator vpnToPolicyIterator = dataObject.getRows("VpnToPolicyRel", vpnToPlicyCriteria);
                while (vpnToPolicyIterator.hasNext()) {
                    final Row vpnToPolicyRow = vpnToPolicyIterator.next();
                    final Long policyId = (Long)vpnToPolicyRow.get("VPN_POLICY_ID");
                    final Criteria vpnConfigCriteria = new Criteria(new Column("VpnIKEv2", "VPN_POLICY_ID"), (Object)policyId, 0);
                    final Iterator iKEv2Iterator = dataObject.getRows("VpnIKEv2", vpnConfigCriteria);
                    while (iKEv2Iterator.hasNext()) {
                        final Row payloadRow = iKEv2Iterator.next();
                        final String serverName = (String)payloadRow.get("SERVER_NAME");
                        final String remoteId = (String)payloadRow.get("REMOTE_ID");
                        final String localId = (String)payloadRow.get("LOCAL_ID");
                        final Integer authenticationMethod = (Integer)payloadRow.get("AUTHENTICATION_METHOD");
                        final Integer eAPEnable = (Integer)payloadRow.get("EAP_ENABLING");
                        final Integer dpdRate = (Integer)payloadRow.get("DEAD_PER_DETECTION");
                        final Integer pfsEnable = (Integer)payloadRow.get("PFS");
                        final Integer internalIPSubnet = (Integer)payloadRow.get("INTERNAL_IP_SUBNET");
                        final Integer certificateRevocationCheck = (Integer)payloadRow.get("CERTIFICATE_REVOCATION_CHECK");
                        final Integer disableMOBIKE = (Integer)payloadRow.get("MOBIKE");
                        final Integer disableRedirect = (Integer)payloadRow.get("REDIRECT");
                        if (!MDMStringUtils.isEmpty(serverName)) {
                            vpnPayload.setRemoteAddress(serverName.trim());
                        }
                        if (!MDMStringUtils.isEmpty(remoteId)) {
                            vpnPayload.setRemoteIdentifier(remoteId);
                        }
                        if (!MDMStringUtils.isEmpty(localId)) {
                            vpnPayload.setLocalIdentifier(localId);
                        }
                        if (authenticationMethod == 2) {
                            vpnPayload.setAuthenticationMethod("SharedSecret");
                            String sharedSecret = "";
                            if (payloadRow.get("SHARED_SECRET_ID") != null) {
                                final Long sharedSecretID = (Long)payloadRow.get("SHARED_SECRET_ID");
                                sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID.toString());
                            }
                            vpnPayload.setSharedSecret(sharedSecret);
                        }
                        else if (eAPEnable != 0) {
                            vpnPayload.setEAPEnabling();
                            if (eAPEnable == 2) {
                                final String userName = (String)payloadRow.get("EAP_USERNAME");
                                String password = "";
                                if (payloadRow.get("EAP_PASSWORD_ID") != null) {
                                    final Long eapPasswordID = (Long)payloadRow.get("EAP_PASSWORD_ID");
                                    password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(eapPasswordID.toString());
                                }
                                if (!MDMStringUtils.isEmpty(userName)) {
                                    vpnPayload.setAuthName(userName);
                                }
                                if (!MDMStringUtils.isEmpty(password)) {
                                    vpnPayload.setAuthPassword(password);
                                }
                                vpnPayload.setAuthenticationMethod("None");
                            }
                        }
                        vpnPayload.setDeadPeerDetectionRate(this.setDPDRate(dpdRate));
                        vpnPayload.setPerfectForwardSecrecy(pfsEnable);
                        vpnPayload.setInternalIPSubnet(internalIPSubnet);
                        vpnPayload.setCertificateRevocationCheck(certificateRevocationCheck);
                        vpnPayload.setDisableMOBIKE(disableMOBIKE);
                        vpnPayload.setDisableRedirect(disableRedirect);
                        final Iterator iKESAIterator = dataObject.getRows("IKE_SA_ID");
                        while (iKESAIterator.hasNext()) {
                            final Row iKESARow = iKESAIterator.next();
                            final Integer encryptionAlgorithm = (Integer)iKESARow.get("ENCRYPTION_ALGORITHM");
                            final Integer integrityAlgorithm = (Integer)iKESARow.get("INTEGRITY_ALGORITHM");
                            final Integer diffieHellmanGroup = (Integer)iKESARow.get("DIFFIE_HELLMAN_GROUP");
                            final Integer lifeInMinutes = (Integer)iKESARow.get("LIFE_TIME_IN_MINUTES");
                            vpnPayload.setEncryptionAlgorithm(this.setSAEncryptionAlgorithm(encryptionAlgorithm));
                            vpnPayload.setIntegrityAlgorithm(this.setSAIntegrityAlgorithm(integrityAlgorithm));
                            vpnPayload.setDiffieHellmanGroup(diffieHellmanGroup);
                            vpnPayload.setTimeLifeInMinutes(lifeInMinutes);
                        }
                        final Iterator childSAIterator = dataObject.getRows("CHILD_SA_ID");
                        while (childSAIterator.hasNext()) {
                            final Row childSARow = childSAIterator.next();
                            final Integer encryptionAlgorithm = (Integer)childSARow.get("ENCRYPTION_ALGORITHM");
                            final Integer integrityAlgorithm = (Integer)childSARow.get("INTEGRITY_ALGORITHM");
                            final Integer diffieHellmanGroup = (Integer)childSARow.get("DIFFIE_HELLMAN_GROUP");
                            final Integer lifeInMinutes = (Integer)childSARow.get("LIFE_TIME_IN_MINUTES");
                            vpnPayload.securityAssociationType = 1;
                            vpnPayload.setEncryptionAlgorithm(this.setSAEncryptionAlgorithm(encryptionAlgorithm));
                            vpnPayload.setIntegrityAlgorithm(this.setSAIntegrityAlgorithm(integrityAlgorithm));
                            vpnPayload.setDiffieHellmanGroup(diffieHellmanGroup);
                            vpnPayload.setTimeLifeInMinutes(lifeInMinutes);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, null, ex);
        }
        return vpnPayload;
    }
    
    private String setDPDRate(final int dpdRate) {
        String dPDRateType = null;
        switch (dpdRate) {
            case 0: {
                dPDRateType = "None";
                break;
            }
            case 1: {
                dPDRateType = "Low";
                break;
            }
            case 2: {
                dPDRateType = "Medium";
                break;
            }
            case 3: {
                dPDRateType = "High";
                break;
            }
        }
        return dPDRateType;
    }
    
    public String setSAEncryptionAlgorithm(final int encryptionKey) {
        String encryptionAlgorithmType = null;
        switch (encryptionKey) {
            case 0: {
                encryptionAlgorithmType = "DES";
                break;
            }
            case 1: {
                encryptionAlgorithmType = "3DES";
                break;
            }
            case 2: {
                encryptionAlgorithmType = "AES-128";
                break;
            }
            case 3: {
                encryptionAlgorithmType = "AES-256";
                break;
            }
            case 4: {
                encryptionAlgorithmType = "AES-128-GCM";
                break;
            }
            case 5: {
                encryptionAlgorithmType = "AES-256-GCM";
                break;
            }
            case 6: {
                encryptionAlgorithmType = "ChaCha20Poly1305";
                break;
            }
        }
        return encryptionAlgorithmType;
    }
    
    public String setSAIntegrityAlgorithm(final int integrityKey) {
        String integrityAlgorithmType = null;
        switch (integrityKey) {
            case 0: {
                integrityAlgorithmType = "SHA1-96";
                break;
            }
            case 1: {
                integrityAlgorithmType = "SHA1-160";
                break;
            }
            case 2: {
                integrityAlgorithmType = "SHA2-256";
                break;
            }
            case 3: {
                integrityAlgorithmType = "SHA2-384";
                break;
            }
            case 4: {
                integrityAlgorithmType = "SHA2-512";
                break;
            }
        }
        return integrityAlgorithmType;
    }
    
    public void createOnDemandDictRules(final Long configDataItemID, final DataObject dataObject, final VPNPayLoadType vpnPayload) throws DataAccessException {
        vpnPayload.createOnDemandSet();
        try {
            vpnPayload.createEvalActionDict();
            final Iterator it = dataObject.getRows("VPNOnDemandPolicy");
            while (it.hasNext()) {
                final Row vpnOnDemandRow = it.next();
                final String action = vpnOnDemandRow.get("ONDEMAND_ACTION").toString();
                if (action.equalsIgnoreCase("ConnectIfNeeded") || action.equalsIgnoreCase("NeverConnect")) {
                    final Criteria connAttemptcriteria = new Criteria(new Column("VpnConEvalRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemandRow.get("VPN_OD_POLICY_ID"), 0);
                    final Join connAttemptjoin = new Join("VpnConEvalRuleToPolicyRel", "VpnODRulesForConEval", new String[] { "CONN_RULE_ID" }, new String[] { "CONN_RULE_ID" }, 2);
                    final Iterator connAttemptTblIterator = dataObject.getRows("VpnODRulesForConEval", connAttemptcriteria, connAttemptjoin);
                    while (connAttemptTblIterator.hasNext()) {
                        final Row ruleTableRow = connAttemptTblIterator.next();
                        vpnPayload.createOnDemandDict();
                        vpnPayload.setDomainAction(action);
                        this.setConnAttemptRule(ruleTableRow, vpnPayload);
                        vpnPayload.setEvalActionDictsInNSSet();
                    }
                }
                else {
                    vpnPayload.createOnDemandDict();
                    vpnPayload.setVpnODAction(action);
                    final Criteria nwRulecriteria = new Criteria(new Column("VpnNWRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemandRow.get("VPN_OD_POLICY_ID"), 0);
                    final Join nwRulejoin = new Join("VpnNWRuleToPolicyRel", "VpnODRulesForNWChange", new String[] { "NW_CHANGE_RULE_ID" }, new String[] { "NW_CHANGE_RULE_ID" }, 2);
                    final Iterator nwRuleTableIterator = dataObject.getRows("VpnODRulesForNWChange", nwRulecriteria, nwRulejoin);
                    while (nwRuleTableIterator.hasNext()) {
                        final Row ruleTableRow = nwRuleTableIterator.next();
                        this.setNetworkRules(ruleTableRow, vpnPayload);
                    }
                    vpnPayload.setOdDictsInNSSet();
                }
            }
            if (vpnPayload.getEvalActionDict().count() > 0) {
                vpnPayload.createOnDemandDict();
                vpnPayload.setVpnODAction("EvaluateConnection");
                vpnPayload.setActionParameters();
                vpnPayload.setOdDictsInNSSet();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        vpnPayload.setOnDemandRules();
    }
    
    private void setConnAttemptRule(final Row ruleTableRow, final VPNPayLoadType dict) {
        final List columns = ruleTableRow.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            final String columnName = columns.get(i);
            if (columnName.equalsIgnoreCase("DOMAIN_NAME")) {
                final String value = ruleTableRow.get(columnName).toString();
                if (!value.equals("")) {
                    dict.setDomains(value);
                }
            }
            else if (columnName.equalsIgnoreCase("DNS_SERVER_ADDRESS")) {
                final String value = ruleTableRow.get(columnName).toString();
                if (!value.equals("")) {
                    dict.setRequiredDNSServers(value);
                }
            }
            else if (columnName.equalsIgnoreCase("URL_PROBE")) {
                final String value = ruleTableRow.get(columnName).toString();
                if (!value.equals("")) {
                    dict.setRequiredURLStringProbe(value);
                }
            }
        }
    }
    
    private void setNetworkRules(final Row ruleTableRow, final VPNPayLoadType dict) {
        final String ruleName = ruleTableRow.get("NETWORKS_RULE_TYPE").toString();
        final String value = ruleTableRow.get("NETWORKS_VALUE").toString();
        if (ruleName.equals("DNSDomainMatch")) {
            dict.setVpnDNSDomainMatch(value);
        }
        else if (ruleName.equals("DNSServerAddress")) {
            dict.setDNSServerAddressMatch(value);
        }
        else if (ruleName.equals("InterfaceMatch")) {
            dict.setInterfaceTypeMatch(value);
        }
        else if (ruleName.equals("SSIDMatch")) {
            dict.setSSIDMatch(value);
        }
        else if (ruleName.equals("URLProbe")) {
            dict.setURLStringProbe(value);
        }
    }
    
    public void createPerAppVPN(final Long configDataItemID, final DataObject dataObject, final VPNPayLoadType vpnPayload) throws DataAccessException {
        final Iterator it = dataObject.getRows("VpnPolicy");
        while (it.hasNext()) {
            final Row vpnPolicyRow = it.next();
            vpnPayload.setPerAppVPNUUID((String)vpnPolicyRow.get("VPNUUID"));
            final Integer vpnProviderType = (Integer)vpnPolicyRow.get("PROVIDER_TYPE");
            if (vpnProviderType != null && vpnProviderType == 2) {
                vpnPayload.setPerAppVPNProviderType("packet-tunnel");
            }
            else {
                vpnPayload.setPerAppVPNProviderType("app-proxy");
            }
            final Boolean onDemandMatchAppEnabled = (Boolean)vpnPolicyRow.get("ONDEMAND_MATCH_APP_ENABLED");
            vpnPayload.setPerAppVPNOnDemandMatchAppEnabled(onDemandMatchAppEnabled);
        }
    }
    
    private VPNPayLoadType addVendorConfig(final DataObject dataObject, final VPNPayLoadType vpnPayload) throws DataAccessException {
        if (dataObject.containsTable("VpnCustomData")) {
            final NSDictionary vendorDict = (NSDictionary)vpnPayload.getPayloadDict().get((Object)"VendorConfig");
            final Iterator customDataIterator = dataObject.getRows("VpnCustomData");
            while (customDataIterator.hasNext()) {
                final Row customDataRow = customDataIterator.next();
                final String customDataKey = (String)customDataRow.get("KEY");
                final String customDataValue = (String)customDataRow.get("VALUE");
                vendorDict.put(customDataKey, (Object)customDataValue);
            }
        }
        return vpnPayload;
    }
}
