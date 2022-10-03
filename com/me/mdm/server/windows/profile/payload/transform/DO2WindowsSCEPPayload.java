package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.sym.server.mdm.inv.CertificateConstants;
import java.io.UnsupportedEncodingException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.Map;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.me.mdm.server.windows.profile.payload.WinMobileSCEPUserPayload;
import com.me.mdm.server.windows.profile.payload.WinMobileSCEPPayload;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.net.URLEncoder;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.certificates.scep.DynamicScepServer;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsSCEPPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;
import org.bouncycastle.asn1.x509.KeyPurposeId;

public class DO2WindowsSCEPPayload extends DO2WindowsPayload
{
    String serverURL;
    String challenge;
    Integer retryCount;
    Integer retryDelay;
    Integer keyUsage;
    Integer keyLength;
    String hashAlgorithm;
    String caThumbprint;
    String subjectName;
    String subjectAlternativeNames;
    String ekuMapping;
    Integer keyProtection;
    String caCertContents;
    
    public DO2WindowsSCEPPayload() {
        this.serverURL = null;
        this.challenge = "";
        this.retryCount = null;
        this.retryDelay = null;
        this.keyUsage = 128;
        this.keyLength = 1024;
        this.hashAlgorithm = "SHA-256";
        this.caThumbprint = null;
        this.subjectName = null;
        this.subjectAlternativeNames = null;
        this.ekuMapping = KeyPurposeId.id_kp_clientAuth.getId();
        this.keyProtection = 2;
        this.caCertContents = null;
    }
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsSCEPPayload payload = new WindowsSCEPPayload();
        payload.addReplacePayloadCommand("%scep_payload_xml%");
        payload.addExecPayloadCommand("%scep_exec_payload_xml%");
        payload.addAddPayloadCommand("%scep_add_payload_xml%");
        payload.getNonAtomicDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%scep_payload_xml_nonAtomicDelete%"));
        WindowsSCEPPayload win81ScepPayload = null;
        WinMobileSCEPPayload win10ScepPayload = null;
        try {
            final Iterator scepConfigIter = dataObject.getRows("SCEPConfigurations");
            while (scepConfigIter.hasNext()) {
                final Row scepConfigRow = scepConfigIter.next();
                final long scepConfigId = (long)scepConfigRow.get("SCEP_CONFIG_ID");
                final ScepServer scepServer = DynamicScepServer.getScepServerForScepId(scepConfigId);
                final Row templateRow = dataObject.getRow("SCEPServerToTemplate", new Criteria(Column.getColumn("SCEPServerToTemplate", "SCEP_CONFIG_ID"), scepConfigRow.get("SCEP_CONFIG_ID"), 0));
                final Row serverRow = dataObject.getRow("SCEPServers", new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), templateRow.get("SCEP_SERVER_ID"), 0));
                final String scepConfigName = URLEncoder.encode(String.valueOf(scepConfigRow.get("SCEP_CONFIGURATION_NAME")), "UTF-8");
                win81ScepPayload = new WindowsSCEPPayload(scepConfigName);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("WinSCEPAsUser")) {
                    win10ScepPayload = new WinMobileSCEPPayload(scepConfigName);
                }
                else {
                    win10ScepPayload = new WinMobileSCEPUserPayload(scepConfigName);
                }
                this.serverURL = (String)scepConfigRow.get("URL");
                if (this.serverURL.endsWith("/pkiclient.exe")) {
                    this.serverURL = this.serverURL.substring(0, this.serverURL.lastIndexOf(47));
                }
                final int challengeType = (int)scepConfigRow.get("CHALLENGE_TYPE");
                if (challengeType == 1) {
                    this.challenge = PayloadSecretFieldsHandler.getInstance().constructPayloadSCEPChallenge(Long.toString(scepConfigId));
                }
                else if (challengeType == 2) {
                    this.challenge = "%challenge_password%" + scepConfigId;
                }
                if (scepConfigRow.get("RETRIES") != null) {
                    this.retryCount = ((Long)scepConfigRow.get("RETRIES")).intValue();
                }
                if (scepConfigRow.get("RETRY_DELAY") != null) {
                    this.retryDelay = ((Long)scepConfigRow.get("RETRY_DELAY")).intValue();
                }
                final int keyUsageValFromDB = (int)scepConfigRow.get("KEY_USAGE");
                if (keyUsageValFromDB == 1) {
                    this.keyUsage = 128;
                }
                else if (keyUsageValFromDB == 4) {
                    this.keyUsage = 32;
                }
                else if (keyUsageValFromDB == 5) {
                    this.keyUsage = 160;
                }
                if ((int)scepConfigRow.get("KEY_SIZE") == 1) {
                    this.keyLength = 2048;
                }
                this.caThumbprint = (String)scepConfigRow.get("CA_FINGER_PRINT");
                this.subjectName = (String)scepConfigRow.get("SUBJECT");
                final String identifierAttribute = this.getSubjectIdentifierAttribute(scepServer);
                if (!identifierAttribute.isEmpty()) {
                    this.subjectName = (this.subjectName.isEmpty() ? "" : (this.subjectName + ",")) + identifierAttribute;
                }
                if (serverRow != null) {
                    final Long caCertID = (Long)serverRow.get("CA_CERTIFICATE_ID");
                    if (caCertID != null && caCertID > 0L) {
                        final Row certPolicyRow = dataObject.getRow("CertificatePolicy", new Criteria(Column.getColumn("CertificatePolicy", "CERTIFICATE_ID"), (Object)caCertID, 0));
                        if (certPolicyRow == null) {
                            final Row caCertrow = dataObject.getRow("CASERVERCERTIFICATES", new Criteria(Column.getColumn("CASERVERCERTIFICATES", "CERTIFICATE_ID"), (Object)caCertID, 0));
                            final String certificateFile = (String)caCertrow.get("CERTIFICATE_FILE_NAME");
                            final Long customerID = (Long)caCertrow.get("CUSTOMER_ID");
                            final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
                            final String certPath = cerFolder + File.separator + certificateFile;
                            final Map<String, String> certificateDetails = new DO2WindowsCertificatePayload().getCertificateContentsAndType(certPath);
                            this.caCertContents = certificateDetails.get("Content");
                        }
                    }
                }
                final int subjectAltNameType = (int)scepConfigRow.get("SUBJECT_ALTNAME_TYPE");
                if (subjectAltNameType != 0) {
                    final String subjectAltNameValue = (String)scepConfigRow.get("SUBJECT_ALTNAME_VALUE");
                    if (subjectAltNameType == 1) {
                        if (scepServer != null && scepServer.getServerType() == ScepServerType.DIGICERT) {
                            this.subjectAlternativeNames = "11+" + subjectAltNameValue;
                        }
                        else {
                            this.subjectAlternativeNames = "2+" + subjectAltNameValue;
                        }
                    }
                    else if (subjectAltNameType == 2) {
                        this.subjectAlternativeNames = "3+" + subjectAltNameValue;
                    }
                    else if (subjectAltNameType == 3) {
                        this.subjectAlternativeNames = "7+" + subjectAltNameValue;
                    }
                    else if (subjectAltNameType == 4) {
                        this.subjectAlternativeNames = "11+" + subjectAltNameValue;
                    }
                }
                win81ScepPayload.setSCEPServerURL(this.serverURL);
                win10ScepPayload.setSCEPServerURL(this.serverURL);
                win81ScepPayload.setChallengePassword(this.challenge);
                win10ScepPayload.setChallengePassword(this.challenge);
                if (this.retryCount != null) {
                    win81ScepPayload.setRetryCount(this.retryCount);
                    win10ScepPayload.setRetryCount(this.retryCount);
                }
                if (this.retryDelay != null) {
                    win81ScepPayload.setRetryDelay(this.retryDelay);
                    win10ScepPayload.setRetryDelay(this.retryDelay);
                }
                win81ScepPayload.setKeyUsage(this.keyUsage);
                win10ScepPayload.setKeyUsage(this.keyUsage);
                win81ScepPayload.setPrivateKeySize(this.keyLength);
                win10ScepPayload.setPrivateKeySize(this.keyLength);
                win81ScepPayload.setCAThumbprint(this.caThumbprint);
                win10ScepPayload.setCAThumbprint(this.caThumbprint);
                win81ScepPayload.setSubjectName(this.subjectName);
                win10ScepPayload.setSubjectName(this.subjectName);
                if (this.subjectAlternativeNames != null && !this.subjectAlternativeNames.trim().isEmpty()) {
                    win81ScepPayload.setSubjecAltName(this.subjectAlternativeNames);
                    win10ScepPayload.setSubjecAltName(this.subjectAlternativeNames);
                }
                if (this.caCertContents != null && serverRow != null && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("pushCertInSCEPPayload")) {
                    final Long caCertID2 = (Long)serverRow.get("CA_CERTIFICATE_ID");
                    final String certificate = PayloadSecretFieldsHandler.getInstance().constructSSLCertificate(caCertID2.toString());
                    win10ScepPayload.setEncodedRootCertificateContent(certificate, this.caThumbprint);
                }
                win81ScepPayload.setKeyProtection(this.keyProtection);
                win10ScepPayload.setKeyProtection(this.keyProtection);
                win81ScepPayload.setCSRHashAlgorithm(this.hashAlgorithm);
                win10ScepPayload.setCSRHashAlgorithm(this.hashAlgorithm);
                win81ScepPayload.setEKUMapping(this.ekuMapping);
                win10ScepPayload.setEKUMapping(this.ekuMapping);
                win81ScepPayload.setEnrollExec();
                win10ScepPayload.setEnrollExec();
                win81ScepPayload.setSCEPNonAtomicDeleteCommand();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating SCEP payload ", ex);
        }
        this.packOsSpecificPayloadToXML(dataObject, win81ScepPayload, "install", "WindowsPhone81Scep");
        this.packOsSpecificPayloadToXML(dataObject, win10ScepPayload, "install", "Windows10MobileScep");
        return payload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsSCEPPayload payload = new WindowsSCEPPayload();
        final Item deleteItem = new Item();
        deleteItem.setTarget(new Location("%scep_payload_xml%"));
        payload.getDeletePayloadCommand().addRequestItem(deleteItem);
        WindowsSCEPPayload win81ScepPayload = null;
        WinMobileSCEPPayload win10ScepPayload = null;
        try {
            final Iterator scepConfigIter = dataObject.getRows("SCEPConfigurations");
            while (scepConfigIter.hasNext()) {
                final Row scepConfigRow = scepConfigIter.next();
                final String scepConfigName = URLEncoder.encode(String.valueOf(scepConfigRow.get("SCEP_CONFIGURATION_NAME")), "UTF-8");
                win81ScepPayload = new WindowsSCEPPayload(scepConfigName);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("WinSCEPAsUser")) {
                    win10ScepPayload = new WinMobileSCEPPayload(scepConfigName);
                }
                else {
                    win10ScepPayload = new WinMobileSCEPUserPayload(scepConfigName);
                }
                win81ScepPayload.setSCEPProfileDeleteCommand();
                win10ScepPayload.setSCEPProfileDeleteCommand();
            }
        }
        catch (final DataAccessException | UnsupportedEncodingException ex) {
            this.logger.log(Level.SEVERE, "Error while creating SCEP remove payload ", ex);
        }
        this.packOsSpecificPayloadToXML(dataObject, win81ScepPayload, "remove", "WindowsPhone81Scep");
        this.packOsSpecificPayloadToXML(dataObject, win10ScepPayload, "remove", "Windows10MobileScep");
        return payload;
    }
    
    private String getSubjectIdentifierAttribute(final ScepServer scepServer) {
        if (scepServer == null) {
            return "";
        }
        final ScepServerType scepServerType = scepServer.getServerType();
        if (scepServerType == ScepServerType.ADCS) {
            return CertificateConstants.SUBJECT_SERIAL_NUMBER + "=" + "%profileId%" + "." + "%resourceid%";
        }
        if (scepServerType == ScepServerType.DIGICERT) {
            return CertificateConstants.SUBJECT_UNIQUE_IDENTIFIER + "=" + "%profileId%" + "." + "%resourceid%";
        }
        return "";
    }
}
