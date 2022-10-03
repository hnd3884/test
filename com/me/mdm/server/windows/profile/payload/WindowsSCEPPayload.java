package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsSCEPPayload extends WindowsPayload
{
    protected String baseLocURI;
    protected String baseInstallLocURI;
    
    public WindowsSCEPPayload() {
        this.baseLocURI = null;
        this.baseInstallLocURI = null;
    }
    
    public WindowsSCEPPayload(final String scepConfigName) {
        this.baseLocURI = null;
        this.baseInstallLocURI = null;
        this.baseLocURI = "./Vendor/MSFT/CertificateStore/My/SCEP/" + scepConfigName;
        this.baseInstallLocURI = this.baseLocURI + "/Install/";
    }
    
    public void setSCEPServerURL(final String serverURL) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "ServerURL", serverURL, "chr");
    }
    
    public void setSubjectName(final String subjectName) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "SubjectName", subjectName, "chr");
    }
    
    public void setCAThumbprint(final String caThumbprint) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "CAThumbprint", caThumbprint, "chr");
    }
    
    public void setSubjecAltName(final String subjectAltNameString) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "SubjectAlternativeNames", subjectAltNameString, "chr");
    }
    
    public void setRetryCount(final Integer retryCount) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "RetryCount", retryCount.toString(), "int");
    }
    
    public void setRetryDelay(final Integer retryDelay) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "RetryDelay", retryDelay.toString(), "int");
    }
    
    public void setChallengePassword(final String challengePassword) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "Challenge", challengePassword, "chr");
    }
    
    public void setPrivateKeySize(final Integer keySize) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "KeyLength", keySize.toString(), "int");
    }
    
    public void setKeyUsage(final Integer keyUsageInDecimal) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "KeyUsage", keyUsageInDecimal.toString(), "int");
    }
    
    public void setCSRHashAlgorithm(final String csrHashAlgorithm) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "HashAlgorithm", csrHashAlgorithm, "chr");
    }
    
    public void setEKUMapping(final String ekuMappingOID) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "EKUMapping", ekuMappingOID, "chr");
    }
    
    public void setKeyProtection(final Integer keyProtection) {
        this.addAddPayloadCommand(this.baseInstallLocURI + "KeyProtection", keyProtection.toString(), "int");
    }
    
    public void setEnrollExec() {
        this.addExecPayloadCommand(this.baseInstallLocURI + "Enroll");
    }
    
    public void setSCEPProfileDeleteCommand() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.baseLocURI));
    }
    
    public void setSCEPNonAtomicDeleteCommand() {
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.baseLocURI));
    }
    
    public void setEncodedRootCertificateContent(final String contents, final String thumbprint) {
        final String keyName = "./Vendor/MSFT/RootCATrustedCertificates/Root/" + thumbprint + "/EncodedCertificate";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, contents, "b64"));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getAddPayloadCommand());
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
