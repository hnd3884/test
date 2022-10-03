package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.server.util.MDMFeatureParamsHandler;

public class WinMobileSCEPPayload extends WindowsSCEPPayload
{
    public WinMobileSCEPPayload(final String scepConfigName) {
        this.baseLocURI = "./Device/Vendor/MSFT/ClientCertificateInstall/SCEP/" + scepConfigName;
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("doNotPushAsUser")) {
            this.baseLocURI = "./User/Vendor/MSFT/ClientCertificateInstall/SCEP/" + scepConfigName;
        }
        this.baseInstallLocURI = this.baseLocURI + "/Install/";
    }
    
    @Override
    public void setSCEPServerURL(final String serverURL) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "ServerURL", serverURL, "chr");
    }
    
    @Override
    public void setSubjectName(final String subjectName) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "SubjectName", subjectName, "chr");
    }
    
    @Override
    public void setCAThumbprint(final String caThumbprint) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "CAThumbprint", caThumbprint, "chr");
    }
    
    @Override
    public void setSubjecAltName(final String subjectAltNameString) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "SubjectAlternativeNames", subjectAltNameString, "chr");
    }
    
    @Override
    public void setRetryCount(final Integer retryCount) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "RetryCount", retryCount.toString(), "int");
    }
    
    @Override
    public void setRetryDelay(final Integer retryDelay) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "RetryDelay", retryDelay.toString(), "int");
    }
    
    @Override
    public void setChallengePassword(final String challengePassword) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "Challenge", challengePassword, "chr");
    }
    
    @Override
    public void setPrivateKeySize(final Integer keySize) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "KeyLength", keySize.toString(), "int");
    }
    
    @Override
    public void setKeyUsage(final Integer keyUsageInDecimal) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "KeyUsage", keyUsageInDecimal.toString(), "int");
    }
    
    @Override
    public void setCSRHashAlgorithm(final String csrHashAlgorithm) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "HashAlgorithm", csrHashAlgorithm, "chr");
    }
    
    @Override
    public void setEKUMapping(final String ekuMappingOID) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "EKUMapping", ekuMappingOID, "chr");
    }
    
    @Override
    public void setKeyProtection(final Integer keyProtection) {
        this.addReplacePayloadCommand(this.baseInstallLocURI + "KeyProtection", keyProtection.toString(), "int");
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        winConfigPayload.setPayloadContent(this.getAddPayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
