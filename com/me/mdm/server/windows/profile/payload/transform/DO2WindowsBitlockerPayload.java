package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.mdm.server.windows.profile.payload.WindowsBitlockerPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsBitlockerPayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsBitlockerPayload createPayload = new WindowsBitlockerPayload();
        try {
            final Row bitlockerPolicyRow = dataObject.getRow("BitlockerPolicy");
            if (bitlockerPolicyRow != null) {
                final Object admxGroupID = bitlockerPolicyRow.get("ADMX_BACKED_POLICY_GROUP_ID");
                if (admxGroupID instanceof Long) {
                    createPayload.addADMXPolicies(dataObject);
                }
                final Object requireStorageCardEncryption = bitlockerPolicyRow.get("REQUIRE_STORAGE_CARD_ENCRYPTION");
                if (requireStorageCardEncryption instanceof Boolean) {
                    createPayload.addStorageCardEncryption((boolean)requireStorageCardEncryption);
                }
                final Object requireDeviceEcryption = bitlockerPolicyRow.get("REQUIRE_DEVICE_ENCRYPTION");
                if (requireDeviceEcryption instanceof Boolean) {
                    createPayload.addRequireDeviceEncryption((boolean)requireDeviceEcryption);
                }
                final Object allowWarningForOtherDiskEncryption = bitlockerPolicyRow.get("ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION");
                if (allowWarningForOtherDiskEncryption instanceof Boolean) {
                    createPayload.addWarningForOtherDiskEncryption(!(boolean)allowWarningForOtherDiskEncryption);
                }
                final Object allowStandardUserEncryption = bitlockerPolicyRow.get("ALLOW_STANDARD_USER_ENCRYPTION");
                if (allowStandardUserEncryption instanceof Boolean) {
                    createPayload.addAllowStandardUserEncryption((boolean)allowStandardUserEncryption);
                }
                final Object recoveryPassRotationConfig = bitlockerPolicyRow.get("CONFIGURE_RECOVERY_PASSWORD_ROTATION");
                if (recoveryPassRotationConfig instanceof Integer) {
                    createPayload.addConfigureRecoveryPassRotation((int)recoveryPassRotationConfig);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Bitlocker payload ", ex);
        }
        return createPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsBitlockerPayload removePayload = new WindowsBitlockerPayload();
        try {
            final Row bitlockerPolicyRow = dataObject.getRow("BitlockerPolicy");
            if (bitlockerPolicyRow != null) {
                final Object admxGroupID = bitlockerPolicyRow.get("ADMX_BACKED_POLICY_GROUP_ID");
                if (admxGroupID instanceof Long) {
                    removePayload.removeADMXPolicies(dataObject);
                }
                final Object requireStorageCardEncryption = bitlockerPolicyRow.get("REQUIRE_STORAGE_CARD_ENCRYPTION");
                if (requireStorageCardEncryption instanceof Boolean) {
                    removePayload.removeStorageCardEncryption();
                }
                final Object requireDeviceEcryption = bitlockerPolicyRow.get("REQUIRE_DEVICE_ENCRYPTION");
                if (requireDeviceEcryption instanceof Boolean) {
                    removePayload.removeRequireDeviceEncryption();
                }
                final Object allowWarningForOtherDiskEncryption = bitlockerPolicyRow.get("ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION");
                if (allowWarningForOtherDiskEncryption instanceof Boolean) {
                    removePayload.removeWarningForOtherDiskEncryption();
                }
                final Object allowStandardUserEncryption = bitlockerPolicyRow.get("ALLOW_STANDARD_USER_ENCRYPTION");
                if (allowStandardUserEncryption instanceof Boolean) {
                    removePayload.removeAllowStandardUserEncryption();
                }
                final Object recoveryPassRotationConfig = bitlockerPolicyRow.get("CONFIGURE_RECOVERY_PASSWORD_ROTATION");
                if (recoveryPassRotationConfig instanceof Integer) {
                    removePayload.removeConfigureRecoveryPassRotation();
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Bitlocker payload ", ex);
        }
        return removePayload;
    }
}
