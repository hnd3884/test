package com.adventnet.sym.server.mdm.android.payload.transform;

import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.adventnet.sym.server.mdm.inv.CertificateConstants;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.DynamicScepServer;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidSCEPPayload implements DO2AndroidPayload
{
    private static final Logger LOGGER;
    String url;
    String caName;
    String subject;
    String challenge;
    Integer retryCount;
    Integer retryDelay;
    Integer keyLength;
    String subjectAlternativeName;
    Integer keyUsage;
    String caCertificatePayload;
    
    public DO2AndroidSCEPPayload() {
        this.url = null;
        this.caName = null;
        this.subject = "";
        this.challenge = "";
        this.retryCount = null;
        this.retryDelay = null;
        this.keyLength = 1024;
        this.subjectAlternativeName = "";
        this.keyUsage = 0;
        this.caCertificatePayload = "";
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidSCEPPayload androidSCEPPayload = null;
        Iterator iterator = null;
        try {
            iterator = dataObject.getRows("SCEPConfigurations");
            while (iterator.hasNext()) {
                final Row payloadRow = iterator.next();
                final long scepConfigId = (long)payloadRow.get("SCEP_CONFIG_ID");
                final ScepServer scepServer = DynamicScepServer.getScepServerForScepId(scepConfigId);
                androidSCEPPayload = new AndroidSCEPPayload("1", "com.mdm.mobiledevice.Scep", "Scep");
                this.url = (String)payloadRow.get("URL");
                DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: SCEP configuration URL: {0}", new Object[] { this.url });
                this.caName = (String)payloadRow.get("NAME");
                if (((String)payloadRow.get("NAME")).isEmpty()) {
                    this.caName = "MDM-CA";
                }
                if (payloadRow.get("SUBJECT") != null && !((String)payloadRow.get("SUBJECT")).isEmpty()) {
                    this.subject = (String)payloadRow.get("SUBJECT");
                }
                final String identifierAttribute = this.getSubjectIdentifierAttribute(scepServer);
                if (!identifierAttribute.isEmpty()) {
                    this.subject = (this.subject.isEmpty() ? "" : (this.subject + ",")) + identifierAttribute;
                }
                final int challengeType = (int)payloadRow.get("CHALLENGE_TYPE");
                DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: SCEP configuration challenge type: {0}", new Object[] { challengeType });
                if (challengeType == 1) {
                    this.challenge = PayloadSecretFieldsHandler.getInstance().constructPayloadSCEPChallenge(Long.toString(scepConfigId));
                }
                else if (challengeType == 2) {
                    this.challenge = "%challenge_password%" + scepConfigId;
                }
                if (payloadRow.get("RETRIES") != null) {
                    this.retryCount = ((Long)payloadRow.get("RETRIES")).intValue();
                }
                if (payloadRow.get("RETRY_DELAY") != null) {
                    this.retryDelay = ((Long)payloadRow.get("RETRY_DELAY")).intValue();
                }
                if ((int)payloadRow.get("KEY_SIZE") == 1) {
                    this.keyLength = 2048;
                }
                DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: SCEP configuration Key size: {0}", new Object[] { this.keyLength });
                final int subjectAltNameType = (int)payloadRow.get("SUBJECT_ALTNAME_TYPE");
                DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: SCEP configuration subject altname type: {0}", new Object[] { subjectAltNameType });
                if (subjectAltNameType != 0) {
                    final String subjectAltNameValue = (String)payloadRow.get("SUBJECT_ALTNAME_VALUE");
                    if (subjectAltNameType == 1) {
                        this.subjectAlternativeName = subjectAltNameValue;
                    }
                    else if (subjectAltNameType == 2) {
                        this.subjectAlternativeName = subjectAltNameValue;
                    }
                    else if (subjectAltNameType == 3) {
                        this.subjectAlternativeName = subjectAltNameValue;
                    }
                    else if (subjectAltNameType == 4) {
                        this.subjectAlternativeName = subjectAltNameValue;
                    }
                }
                this.keyUsage = (Integer)payloadRow.get("KEY_USAGE");
                this.caCertificatePayload = this.checkAndGetCAcertificateChainForScepId(scepServer);
                androidSCEPPayload.setURL(this.url);
                androidSCEPPayload.setCAName(this.caName);
                androidSCEPPayload.setSubject(this.subject);
                androidSCEPPayload.setSANType(subjectAltNameType);
                androidSCEPPayload.setSubjecAltName(this.subjectAlternativeName);
                if (this.retryCount != null) {
                    androidSCEPPayload.setRetries(this.retryCount);
                }
                if (this.retryDelay != null) {
                    androidSCEPPayload.setRetryDelay(this.retryDelay);
                }
                androidSCEPPayload.setChallengePassword(this.challenge);
                androidSCEPPayload.setPrivateKeySize(this.keyLength);
                androidSCEPPayload.setKeyUsage(this.keyUsage);
                DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: Getting ca certificate for scep config id: {0}", new Object[] { scepConfigId });
                if (!MDMStringUtils.isEmpty(this.caCertificatePayload)) {
                    androidSCEPPayload.setCACertificatePayload(this.caCertificatePayload);
                    DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "DO2AndroidSCEPPayload: CA certificate added successfully: {0}", new Object[] { subjectAltNameType });
                }
            }
        }
        catch (final Exception ex) {
            DO2AndroidSCEPPayload.LOGGER.log(Level.SEVERE, "DO2AndroidSCEPPayload: Exception while creating Android SCEP payload :: ", ex);
        }
        return androidSCEPPayload;
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
    
    private String checkAndGetCAcertificateChainForScepId(final ScepServer scepServer) {
        if (scepServer == null) {
            return "";
        }
        try {
            final CredentialCertificate certificate = scepServer.getCertificate();
            if (certificate != null) {
                final Long certificateId = certificate.getCertificateId();
                final String certificatePlaceholder = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
                return certificatePlaceholder;
            }
        }
        catch (final Exception e) {
            DO2AndroidSCEPPayload.LOGGER.log(Level.SEVERE, e, () -> "Exception while getting ca certificate for scep id " + scepServer2.getScepServerId());
        }
        DO2AndroidSCEPPayload.LOGGER.log(Level.INFO, "CA certificate is not present, so returning empty string for scep id: {0}", new Object[] { scepServer.getScepServerId() });
        return "";
    }
    
    static {
        LOGGER = Logger.getLogger(DO2AndroidSCEPPayload.class.getName());
    }
}
