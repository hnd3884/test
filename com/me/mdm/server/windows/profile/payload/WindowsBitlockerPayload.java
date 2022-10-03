package com.me.mdm.server.windows.profile.payload;

import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.windows.profile.admx.ADMXBackedPolicy;
import java.util.List;
import com.me.mdm.server.windows.profile.admx.ADMXBackedPoliciesHandler;
import com.adventnet.persistence.DataObject;
import com.me.mdm.framework.syncml.core.data.Item;

public class WindowsBitlockerPayload extends WindowsADMXBackedPolicyPayload
{
    private static final String BITLOCKER_ROOT = "./Device/Vendor/MSFT/BitLocker";
    
    public void addStorageCardEncryption(final boolean isRequired) {
        final String path = "./Device/Vendor/MSFT/BitLocker/RequireStorageCardEncryption";
        final Item storageCardEncryptionItem = this.createCommandItemTagElement(path, isRequired ? "1" : "0", "int");
        this.getReplacePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void addRequireDeviceEncryption(final boolean isRequired) {
        final String path = "./Device/Vendor/MSFT/BitLocker/RequireDeviceEncryption";
        final Item storageCardEncryptionItem = this.createCommandItemTagElement(path, isRequired ? "1" : "0", "int");
        this.getReplacePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void addWarningForOtherDiskEncryption(final boolean isAllowed) {
        final String path = "./Device/Vendor/MSFT/BitLocker/AllowWarningForOtherDiskEncryption";
        final Item storageCardEncryptionItem = this.createCommandItemTagElement(path, isAllowed ? "1" : "0", "int");
        this.getReplacePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void addAllowStandardUserEncryption(final boolean isAllowed) {
        final String path = "./Device/Vendor/MSFT/BitLocker/AllowStandardUserEncryption";
        final Item storageCardEncryptionItem = this.createCommandItemTagElement(path, isAllowed ? "1" : "0", "int");
        this.getReplacePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void addConfigureRecoveryPassRotation(final int config) {
        final String path = "./Device/Vendor/MSFT/BitLocker/ConfigureRecoveryPasswordRotation";
        final Item storageCardEncryptionItem = this.createCommandItemTagElement(path, String.valueOf(config), "int");
        this.getReplacePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void addADMXPolicies(final DataObject dataObject) throws DataAccessException {
        final List<ADMXBackedPolicy> policies = ADMXBackedPoliciesHandler.getInstance().getADMXData(dataObject);
        this.addADMXBackedPolicy(policies);
    }
    
    public void removeADMXPolicies(final DataObject dataObject) throws DataAccessException {
        final List<ADMXBackedPolicy> policies = ADMXBackedPoliciesHandler.getInstance().getADMXData(dataObject);
        this.removeADMXBackedPolicy(policies);
    }
    
    public void removeStorageCardEncryption() {
        final String path = "./Device/Vendor/MSFT/BitLocker/RequireStorageCardEncryption";
        final Item storageCardEncryptionItem = this.createTargetItemTagElement(path);
        this.getDeletePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void removeRequireDeviceEncryption() {
        final String path = "./Device/Vendor/MSFT/BitLocker/RequireDeviceEncryption";
        final Item storageCardEncryptionItem = this.createTargetItemTagElement(path);
        this.getDeletePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void removeWarningForOtherDiskEncryption() {
        final String path = "./Device/Vendor/MSFT/BitLocker/AllowWarningForOtherDiskEncryption";
        final Item storageCardEncryptionItem = this.createTargetItemTagElement(path);
        this.getDeletePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void removeAllowStandardUserEncryption() {
        final String path = "./Device/Vendor/MSFT/BitLocker/AllowStandardUserEncryption";
        final Item storageCardEncryptionItem = this.createTargetItemTagElement(path);
        this.getDeletePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
    
    public void removeConfigureRecoveryPassRotation() {
        final String path = "./Device/Vendor/MSFT/BitLocker/ConfigureRecoveryPasswordRotation";
        final Item storageCardEncryptionItem = this.createTargetItemTagElement(path);
        this.getDeletePayloadCommand().addRequestItem(storageCardEncryptionItem);
    }
}
