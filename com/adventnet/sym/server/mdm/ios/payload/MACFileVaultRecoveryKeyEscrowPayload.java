package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;

public class MACFileVaultRecoveryKeyEscrowPayload extends IOSPayload
{
    NSDictionary fileVaultDict;
    
    public MACFileVaultRecoveryKeyEscrowPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.security.FDERecoveryKeyEscrow", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.fileVaultDict = new NSDictionary();
        this.fileVaultDict = this.getPayloadDict();
    }
    
    public void setLocation(final String userMessage) {
        this.fileVaultDict.put("Location", (Object)userMessage);
    }
    
    public void setEncryptCertPayloadUUID(final String personalRecoveryCertUUID) {
        this.fileVaultDict.put("EncryptCertPayloadUUID", (Object)personalRecoveryCertUUID);
    }
    
    public void setDeviceKey(final String deviceKey) {
        this.fileVaultDict.put("DeviceKey", (Object)deviceKey);
    }
}
