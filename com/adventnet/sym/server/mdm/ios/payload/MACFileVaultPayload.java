package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;

public class MACFileVaultPayload extends IOSPayload
{
    NSDictionary fileVaultDict;
    
    public MACFileVaultPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.MCX.FileVault2", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.fileVaultDict = new NSDictionary();
        this.fileVaultDict = this.getPayloadDict();
    }
    
    public void setEnable(final boolean on) {
        if (on) {
            this.fileVaultDict.put("Enable", (Object)"On");
        }
        else {
            this.fileVaultDict.put("Enable", (Object)"Off");
        }
    }
    
    public void setDiffer(final boolean differ) {
        this.fileVaultDict.put("Defer", (Object)differ);
    }
    
    public void setUserEntersMissingInfo(final boolean userEntersMissingInfo) {
        this.fileVaultDict.put("UserEntersMissingInfo", (Object)userEntersMissingInfo);
    }
    
    public void setUseRecoveryKey(final boolean useRecoveryKey) {
        this.fileVaultDict.put("UseRecoveryKey", (Object)useRecoveryKey);
    }
    
    public void setShowRecoveryKey(final boolean showRecoveryKey) {
        this.fileVaultDict.put("ShowRecoveryKey", (Object)showRecoveryKey);
    }
    
    public void setOutputPath(final String outputPath) {
        this.fileVaultDict.put("OutputPath", (Object)outputPath);
    }
    
    public void setPayloadCertificateUUID(final String certificateUUID) {
        this.fileVaultDict.put("PayloadCertificateUUID", (Object)certificateUUID);
    }
    
    public void setUserName(final String certificateUUID) {
        this.fileVaultDict.put("Username", (Object)certificateUUID);
    }
    
    public void setPassword(final String certificateUUID) {
        this.fileVaultDict.put("Password", (Object)certificateUUID);
    }
    
    public void setUseKeychain(final boolean useKeyChain) {
        this.fileVaultDict.put("UseKeychain", (Object)useKeyChain);
    }
    
    public void setDeferForceAtUserLoginMaxBypassAttempts(final int deferForceAtUserLoginMaxBypassAttempts) {
        this.fileVaultDict.put("DeferForceAtUserLoginMaxBypassAttempts", (Object)deferForceAtUserLoginMaxBypassAttempts);
    }
    
    public void setDeferDontAskAtUserLogout(final boolean deferDontAskAtUserLogout) {
        this.fileVaultDict.put("DeferDontAskAtUserLogout", (Object)deferDontAskAtUserLogout);
    }
}
