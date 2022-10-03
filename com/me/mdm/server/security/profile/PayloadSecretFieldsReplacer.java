package com.me.mdm.server.security.profile;

import com.me.mdm.server.windows.profile.payload.content.security.RecoverCertificate;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.security.cert.X509Certificate;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.regex.Pattern;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.profiles.config.DefaultConfigHandler;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class PayloadSecretFieldsReplacer
{
    public static Logger logger;
    
    public JSONObject handlePayloadSecretFields(final JSONObject property, final JSONObject apiJSON, JSONObject resultJSON, final Object secretField) {
        try {
            final String secretFieldStr = secretField.toString();
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Setting empty value for the field:{0}", new Object[] { property.get("secret_field") });
            resultJSON.put(String.valueOf(property.get("name")), (Object)"");
            if (!secretFieldStr.isEmpty()) {
                final Long customerId = (apiJSON.optLong("CUSTOMER_ID") != 0L) ? apiJSON.getLong("CUSTOMER_ID") : CustomerInfoUtil.getInstance().getCustomerId();
                final Long userId = (apiJSON.optLong("LAST_MODIFIED_BY") != 0L) ? apiJSON.getLong("LAST_MODIFIED_BY") : ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final Long passwordID = MDMManagedPasswordHandler.getMDMManagedPasswordID(secretFieldStr, customerId, userId);
                resultJSON.put(String.valueOf(property.get("secret_field")), (Object)passwordID);
            }
            else {
                PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Password field is empty inserting null value...");
                resultJSON = new DefaultConfigHandler().setPayloadNullableColumn(resultJSON, String.valueOf(property.get("secret_field")));
            }
            apiJSON.remove(String.valueOf(property.get("alias")));
            if (apiJSON.has(String.valueOf(property.get("secret_field")).toLowerCase())) {
                apiJSON.remove(String.valueOf(property.get("secret_field")).toLowerCase());
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Exception in handlePayloadSecretFields()", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return resultJSON;
    }
    
    public JSONObject replaceSecretFieldIdInDoToApi(final JSONObject property, final Object secretFieldId, final JSONObject config) {
        if (secretFieldId != null) {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final String password = MDMManagedPasswordHandler.getMDMManagedPassword((Long)secretFieldId, customerId);
            config.put(String.valueOf(property.get("return_secret_field_value")), (Object)password);
        }
        return config;
    }
    
    public String constructPayloadSecretField(final String managedPasswordId) {
        return "%MDMPassword_(\\d+)%".replace("(\\d+)", managedPasswordId);
    }
    
    public String constructCredentialPayloadSecretField(final String credentialPasswordId) {
        return "%Credential_(\\d+)%".replace("(\\d+)", credentialPasswordId);
    }
    
    public String constructEncodedPayloadSecretField(final String credentialPasswordId) {
        return "%MDMPasswordEC_(\\d+)%".replace("(\\d+)", credentialPasswordId);
    }
    
    public String constructReplaceEncodedPayloadSecretField(final String credentialPasswordId) {
        return "<string>%MDMPasswordEC_(\\d+)%</string>".replace("(\\d+)", credentialPasswordId);
    }
    
    public String constructPayloadCertificate(final String certificateId) {
        return "%MDMCertificate_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructIOSPayloadCertificate(final String certificateId) {
        return "%IOSCertificate_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructFilevaultPayloadCertificate(final String certificateId) {
        return "%FileVaultCert_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructSSLCertificate(final String certificateId) {
        return "%MDMSSLCertificate_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructRecoverCertificate(final String certificateId) {
        return "%RecoverCertificate_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructPayloadCertificatePassword(final String certificateId) {
        return "%MDMCertPassword_(\\d+)%".replace("(\\d+)", certificateId);
    }
    
    public String constructPayloadSCEPChallenge(final String scepConfigId) {
        return "%MDMSCEPChallenge_(\\d+)%".replace("(\\d+)", scepConfigId);
    }
    
    public Boolean checkIfPayloadSecretFieldPresent(final String payloadContent) {
        return Pattern.compile("%MDMPassword_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfPayloadCredentialSecretFieldIsPresent(final String payloadContent) {
        return Pattern.compile("%Credential_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfEncodedPayloadSecretFieldIsPresent(final String payloadContent) {
        return Pattern.compile("%MDMPasswordEC_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfPayloadCertificatePresent(final String payloadContent) {
        return Pattern.compile("%MDMCertificate_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfIOSPayloadCertificatePresent(final String payloadContent) {
        return Pattern.compile("%IOSCertificate_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfFilevaultPayloadCertificatePresent(final String payloadContent) {
        return Pattern.compile("%FileVaultCert_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfSSLCertificatePresent(final String payloadContent) {
        return Pattern.compile("%MDMSSLCertificate_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfRecoverCertificatePresent(final String payloadContent) {
        return Pattern.compile("%RecoverCertificate_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfPayloadCertificatePasswordPresent(final String payloadContent) {
        return Pattern.compile("%MDMCertPassword_(\\d+)%").matcher(payloadContent).find();
    }
    
    public Boolean checkIfPayloadSCEPChallengePresent(final String payloadContent) {
        return Pattern.compile("%MDMSCEPChallenge_(\\d+)%").matcher(payloadContent).find();
    }
    
    public String replacePayloadSecretFieldIds(String payloadContent, final Long customerId) {
        final Pattern pattern = Pattern.compile("%MDMPassword_(\\d+)%");
        final Matcher matcher = pattern.matcher(payloadContent);
        final List<Long> managedPasswordIds = new ArrayList<Long>();
        while (matcher.find()) {
            managedPasswordIds.add(Long.parseLong(matcher.group(1)));
        }
        if (!managedPasswordIds.isEmpty()) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Replacing payload secret field ids : {0}", managedPasswordIds);
            final Map<String, String> managedPasswords = MDMManagedPasswordHandler.getMDMManagedPasswords(managedPasswordIds, customerId);
            for (final String passwordId : managedPasswords.keySet()) {
                String passwordToBeReplaced = managedPasswords.get(passwordId);
                passwordToBeReplaced = this.escapeSpecialCharactersInPassword(passwordToBeReplaced);
                final String tempRegex = this.constructPayloadSecretField(passwordId);
                payloadContent = payloadContent.replaceAll(tempRegex, passwordToBeReplaced);
            }
        }
        return payloadContent;
    }
    
    public String replaceEncodedPayloadSecretFieldIds(String payloadContent, final Long customerId) {
        final Pattern pattern = Pattern.compile("<string>%MDMPasswordEC_(\\d+)%</string>");
        final Matcher matcher = pattern.matcher(payloadContent);
        final List<Long> managedPasswordIds = new ArrayList<Long>();
        while (matcher.find()) {
            managedPasswordIds.add(Long.parseLong(matcher.group(1)));
        }
        if (!managedPasswordIds.isEmpty()) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Replacing encoded payload secret field ids : {0}", managedPasswordIds);
            final Map<String, String> managedPasswords = MDMManagedPasswordHandler.getMDMManagedPasswords(managedPasswordIds, customerId);
            for (final String passwordId : managedPasswords.keySet()) {
                final String passwordToBeReplaced = managedPasswords.get(passwordId);
                final String tempRegex = this.constructReplaceEncodedPayloadSecretField(passwordId);
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, "<data>\n" + Base64.getEncoder().encodeToString(passwordToBeReplaced.getBytes()) + "\n" + "</data>");
            }
        }
        return payloadContent;
    }
    
    public String replacePayloadSecretFieldForCredentialIds(String payloadContent, final Long customerId) {
        final Pattern pattern = Pattern.compile("%Credential_(\\d+)%");
        final Matcher matcher = pattern.matcher(payloadContent);
        final List<Long> credentialPasswordIds = new ArrayList<Long>();
        while (matcher.find()) {
            credentialPasswordIds.add(Long.parseLong(matcher.group(1)));
        }
        if (!credentialPasswordIds.isEmpty()) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Replacing payload secret field ids for Credentials table: {0}", credentialPasswordIds);
            final Map<String, Map<String, String>> credentialPasswords = PayloadSecretFieldsMigrationUtil.getCredentialIDsForPasswords(credentialPasswordIds, customerId);
            for (final String passwordId : credentialPasswords.keySet()) {
                final Map<String, String> innerMap = credentialPasswords.get(passwordId);
                final String password = innerMap.get("CRD_PASSWORD");
                final Integer encryption = Integer.parseInt(innerMap.get("CRD_ENC_TYPE"));
                final String tempRegex = this.constructCredentialPayloadSecretField(passwordId);
                final String passwordToBeReplaced = ApiFactoryProvider.getCryptoAPI().decrypt(password, encryption);
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, passwordToBeReplaced);
            }
        }
        return payloadContent;
    }
    
    public String replacePayloadCertificate(String payloadContent, final String certFileName, final Long certificateId, final Long customerID) {
        try {
            if (FileUploadUtil.hasVulnerabilityInFileName(certFileName)) {
                PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Base64 encoded certificate has vulnerability in file name : {0}", certFileName);
                return payloadContent;
            }
            final String tempRegex = this.constructPayloadCertificate(certificateId.toString());
            final String filePath = MDMUtil.getCredentialCertificateFolder(customerID);
            final String completeFilePath = filePath + File.separator + certFileName;
            final String certContentBase64Encoded = ProfileCertificateUtil.getInstance().getCertFileContents(completeFilePath);
            if (certContentBase64Encoded != null) {
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, certContentBase64Encoded);
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Exception occured while replacing certificate : {0}", ex);
        }
        return payloadContent;
    }
    
    public String replaceIOSCertificate(String payloadContent, final String certFileName, final Long certificateId, final Long customerID) {
        try {
            if (FileUploadUtil.hasVulnerabilityInFileName(certFileName)) {
                PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "IOS certificate has vulnerability in file name : {0}", certFileName);
                return payloadContent;
            }
            final String tempRegex = "<string>" + this.constructIOSPayloadCertificate(certificateId.toString()) + "</string>";
            final String filePath = MDMUtil.getCredentialCertificateFolder(customerID);
            final String completeFilePath = filePath + File.separator + certFileName;
            final String certContentBase64Encoded = ProfileCertificateUtil.getInstance().getCertFileContents(completeFilePath);
            if (certContentBase64Encoded != null) {
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, "<data>\n" + certContentBase64Encoded + "\n" + "</data>");
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Exception occured while replacing ios certificate : {0}", ex);
        }
        return payloadContent;
    }
    
    public String replaceFilevaultPayloadCertificate(String payloadContent, final String certFileName, final Long certificateId, final Long customerID, final String certificatePassword) {
        try {
            if (FileUploadUtil.hasVulnerabilityInFileName(certFileName)) {
                PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Filevault certificate has vulnerability in file name : {0}", certFileName);
                return payloadContent;
            }
            final String tempRegex = "<string>" + this.constructFilevaultPayloadCertificate(certificateId.toString()) + "</string>";
            final String filePath = MDMUtil.getCredentialCertificateFolder(customerID);
            final String completeFilePath = filePath + File.separator + certFileName;
            final X509Certificate certificate = CredentialsMgmtAction.readCertificateFromPKCS12(completeFilePath, certificatePassword);
            final String base64Str = Base64.getEncoder().encodeToString(certificate.getEncoded());
            if (base64Str != null) {
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, "<data>\n" + base64Str + "\n" + "</data>");
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Exception occured while replacing Filevault certificate : {0}", ex);
        }
        return payloadContent;
    }
    
    public String replaceSSLCertificate(String payloadContent, final String certFileName, final Long certificateId, final Long customerID) {
        try {
            if (FileUploadUtil.hasVulnerabilityInFileName(certFileName)) {
                PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "SSL certificate has vulnerability in file name : {0}", certFileName);
                return payloadContent;
            }
            final String tempRegex = this.constructSSLCertificate(certificateId.toString());
            final String filePath = MDMUtil.getCredentialCertificateFolder(customerID);
            final String completeFilePath = filePath + File.separator + certFileName;
            final byte[] certificateContent = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(completeFilePath);
            final String base64Str = Base64.getEncoder().encodeToString(certificateContent);
            if (base64Str != null) {
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, base64Str);
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Exception occured while replacing ssl certificate : {0}", ex);
        }
        return payloadContent;
    }
    
    public String replacePayloadCertificatePassword(String payloadContent, String certPassword, final Long certificateId) {
        if (this.checkIfPayloadCertificatePasswordPresent(payloadContent) && certPassword != null && !MDMStringUtils.isEmpty(certPassword)) {
            final String tempRegex = this.constructPayloadCertificatePassword(certificateId.toString());
            certPassword = this.escapeSpecialCharactersInPassword(certPassword);
            payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, certPassword);
        }
        return payloadContent;
    }
    
    public String checkAndReplacePayloadCertificateIds(String payloadContent, final Long customerId) {
        final List<Long> certificateIds = new ArrayList<Long>();
        if (this.checkIfPayloadCertificatePresent(payloadContent)) {
            final Pattern pattern = Pattern.compile("%MDMCertificate_(\\d+)%");
            final Matcher matcher = pattern.matcher(payloadContent);
            while (matcher.find()) {
                certificateIds.add(Long.parseLong(matcher.group(1)));
            }
        }
        if (this.checkIfFilevaultPayloadCertificatePresent(payloadContent)) {
            final Pattern pattern = Pattern.compile("%FileVaultCert_(\\d+)%");
            final Matcher matcher = pattern.matcher(payloadContent);
            while (matcher.find()) {
                certificateIds.add(Long.parseLong(matcher.group(1)));
            }
        }
        if (this.checkIfIOSPayloadCertificatePresent(payloadContent)) {
            final Pattern pattern = Pattern.compile("%IOSCertificate_(\\d+)%");
            final Matcher matcher = pattern.matcher(payloadContent);
            while (matcher.find()) {
                certificateIds.add(Long.parseLong(matcher.group(1)));
            }
        }
        if (this.checkIfSSLCertificatePresent(payloadContent)) {
            final Pattern pattern = Pattern.compile("%MDMSSLCertificate_(\\d+)%");
            final Matcher matcher = pattern.matcher(payloadContent);
            while (matcher.find()) {
                certificateIds.add(Long.parseLong(matcher.group(1)));
            }
        }
        if (!certificateIds.isEmpty()) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Handling certificateIds : {0}", certificateIds);
            final Iterator<Row> certificatesRowsItr = PayloadSecretFieldsMigrationUtil.getCertificateCredentialsInfo(certificateIds, customerId);
            if (certificatesRowsItr != null) {
                while (certificatesRowsItr.hasNext()) {
                    final Row certificateRow = certificatesRowsItr.next();
                    final Long certificateId = (Long)certificateRow.get("CERTIFICATE_ID");
                    final String certFileName = (String)certificateRow.get("CERTIFICATE_FILE_NAME");
                    final String certificatePassword = (String)certificateRow.get("CERTIFICATE_PASSWORD");
                    if (this.checkIfPayloadCertificatePresent(payloadContent)) {
                        payloadContent = this.replacePayloadCertificate(payloadContent, certFileName, certificateId, customerId);
                    }
                    if (this.checkIfFilevaultPayloadCertificatePresent(payloadContent)) {
                        payloadContent = this.replaceFilevaultPayloadCertificate(payloadContent, certFileName, certificateId, customerId, certificatePassword);
                    }
                    if (this.checkIfIOSPayloadCertificatePresent(payloadContent)) {
                        payloadContent = this.replaceIOSCertificate(payloadContent, certFileName, certificateId, customerId);
                    }
                    if (this.checkIfSSLCertificatePresent(payloadContent)) {
                        payloadContent = this.replaceSSLCertificate(payloadContent, certFileName, certificateId, customerId);
                    }
                    payloadContent = this.replacePayloadCertificatePassword(payloadContent, certificatePassword, certificateId);
                }
            }
        }
        return payloadContent;
    }
    
    public String replaceRecoverCertificate(String payloadContent, final Long customerId) {
        try {
            final Pattern pattern = Pattern.compile("%RecoverCertificate_(\\d+)%");
            final Matcher matcher = pattern.matcher(payloadContent);
            final List<Long> certificateIds = new ArrayList<Long>();
            while (matcher.find()) {
                certificateIds.add(Long.parseLong(matcher.group(1)));
            }
            if (!certificateIds.isEmpty()) {
                PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Replacing recover certificates : {0}", certificateIds);
                for (final Long certificateId : certificateIds) {
                    final String certBlob = new RecoverCertificate(certificateId, customerId).getRecoveryCertificateBlob();
                    final String tempRegex = this.constructRecoverCertificate(certificateId.toString());
                    payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, certBlob);
                }
            }
        }
        catch (final Exception ex) {
            PayloadSecretFieldsReplacer.logger.log(Level.SEVERE, "Exception occured while replacing Recover certificate : {0}", ex);
        }
        return payloadContent;
    }
    
    public String replacePayloadSCEPChallenge(String payloadContent) {
        final Pattern pattern = Pattern.compile("%MDMSCEPChallenge_(\\d+)%");
        final Matcher matcher = pattern.matcher(payloadContent);
        final List<Long> scepConfigIds = new ArrayList<Long>();
        while (matcher.find()) {
            scepConfigIds.add(Long.parseLong(matcher.group(1)));
        }
        if (!scepConfigIds.isEmpty()) {
            PayloadSecretFieldsReplacer.logger.log(Level.INFO, "Replacing payload SCEP Config ids : {0}", scepConfigIds);
            final Map<String, String> scepChallengeMap = PayloadSecretFieldsMigrationUtil.getSCEPChallenge(scepConfigIds);
            for (final String scepConfigId : scepChallengeMap.keySet()) {
                String challengePasswordToBeReplaced = scepChallengeMap.get(scepConfigId);
                challengePasswordToBeReplaced = this.escapeSpecialCharactersInPassword(challengePasswordToBeReplaced);
                final String tempRegex = this.constructPayloadSCEPChallenge(scepConfigId);
                payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, tempRegex, challengePasswordToBeReplaced);
            }
        }
        return payloadContent;
    }
    
    public String replaceAllPayloadSecrets(String payloadContent, final Long customerId) {
        if (this.checkIfPayloadSecretFieldPresent(payloadContent)) {
            payloadContent = this.replacePayloadSecretFieldIds(payloadContent, customerId);
        }
        if (this.checkIfPayloadCredentialSecretFieldIsPresent(payloadContent)) {
            payloadContent = this.replacePayloadSecretFieldForCredentialIds(payloadContent, customerId);
        }
        if (this.checkIfEncodedPayloadSecretFieldIsPresent(payloadContent)) {
            payloadContent = this.replaceEncodedPayloadSecretFieldIds(payloadContent, customerId);
        }
        if (this.checkIfPayloadCertificatePresent(payloadContent) || this.checkIfFilevaultPayloadCertificatePresent(payloadContent) || this.checkIfIOSPayloadCertificatePresent(payloadContent) || this.checkIfSSLCertificatePresent(payloadContent)) {
            payloadContent = this.checkAndReplacePayloadCertificateIds(payloadContent, customerId);
        }
        if (this.checkIfRecoverCertificatePresent(payloadContent)) {
            payloadContent = this.replaceRecoverCertificate(payloadContent, customerId);
        }
        if (this.checkIfPayloadSCEPChallengePresent(payloadContent)) {
            payloadContent = this.replacePayloadSCEPChallenge(payloadContent);
        }
        return payloadContent;
    }
    
    public abstract String escapeSpecialCharactersInPassword(final String p0);
    
    static {
        PayloadSecretFieldsReplacer.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
