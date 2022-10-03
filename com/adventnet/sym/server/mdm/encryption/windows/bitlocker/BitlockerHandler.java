package com.adventnet.sym.server.mdm.encryption.windows.bitlocker;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.me.mdm.server.windows.profile.admx.ADMXBackedPoliciesHandler;
import com.me.mdm.server.windows.profile.admx.ADMXBackedPolicy;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;

public class BitlockerHandler
{
    public Long addBitlockerPolicy(final JSONObject bitlockerAPIJSON) throws DataAccessException, JSONException {
        final WritableDataObject dataObject = new WritableDataObject();
        final Iterator<String> keys = bitlockerAPIJSON.keys();
        final Row bitlockerRow = new Row("BitlockerPolicy");
        final ArrayList<ADMXBackedPolicy> admxDataList = new ArrayList<ADMXBackedPolicy>();
        while (keys.hasNext()) {
            final String s;
            final String key = s = keys.next();
            switch (s) {
                case "require_storage_card_encryption": {
                    bitlockerRow.set("REQUIRE_STORAGE_CARD_ENCRYPTION", (Object)bitlockerAPIJSON.getBoolean(key));
                    continue;
                }
                case "enforce_bitlocker": {
                    bitlockerRow.set("REQUIRE_DEVICE_ENCRYPTION", (Object)bitlockerAPIJSON.getBoolean(key));
                    continue;
                }
                case "encryption_method": {
                    admxDataList.add(this.getEncryptionMethodADMX(bitlockerAPIJSON.getJSONObject(key)));
                    continue;
                }
                case "additional_startup_authentication": {
                    admxDataList.add(this.getStartUpAuthADMX(bitlockerAPIJSON.getJSONObject(key)));
                    continue;
                }
                case "min_pin_length": {
                    admxDataList.add(this.getMinLenghtADMX(bitlockerAPIJSON.get(key).toString()));
                    continue;
                }
                case "recovery_message": {
                    admxDataList.add(this.getRecoveryMessageADMX(bitlockerAPIJSON.getJSONObject(key)));
                    continue;
                }
                case "os_drive_recovery_options": {
                    admxDataList.add(this.getOsDriveRecoveryADMX(bitlockerAPIJSON.getJSONObject(key)));
                    continue;
                }
                case "fixed_drive_recovery_options": {
                    admxDataList.add(this.getFixedDriveRecoveryADMX(bitlockerAPIJSON.getJSONObject(key)));
                    continue;
                }
                case "fixed_drive_read_only": {
                    if (bitlockerAPIJSON.getBoolean(key)) {
                        final ADMXBackedPolicy fixedDriveReadOnlyADMXData = new ADMXBackedPolicy("FDVDenyWriteAccess_Name", true);
                        admxDataList.add(fixedDriveReadOnlyADMXData);
                        continue;
                    }
                    final ADMXBackedPolicy fixedDriveReadOnlyADMXData = new ADMXBackedPolicy("FDVDenyWriteAccess_Name", false);
                    admxDataList.add(fixedDriveReadOnlyADMXData);
                    continue;
                }
                case "removable_drive_read_only": {
                    if (bitlockerAPIJSON.getBoolean(key)) {
                        final ADMXBackedPolicy removableDriveReadOnlyADMXData = new ADMXBackedPolicy("RDVDenyWriteAccess_Name", true);
                        removableDriveReadOnlyADMXData.addData("RDVCrossOrg", String.valueOf(bitlockerAPIJSON.has("removable_drive_cross_origin") && bitlockerAPIJSON.getBoolean("removable_drive_cross_origin")).toLowerCase());
                        admxDataList.add(removableDriveReadOnlyADMXData);
                        continue;
                    }
                    final ADMXBackedPolicy removableDriveReadOnlyADMXData = new ADMXBackedPolicy("RDVDenyWriteAccess_Name", false);
                    admxDataList.add(removableDriveReadOnlyADMXData);
                    continue;
                }
                case "silent_encrypt_azure_ad_devices": {
                    bitlockerRow.set("ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION", (Object)!bitlockerAPIJSON.getBoolean(key));
                    continue;
                }
                case "allow_standard_user_encryption": {
                    bitlockerRow.set("ALLOW_STANDARD_USER_ENCRYPTION", (Object)bitlockerAPIJSON.getBoolean(key));
                    continue;
                }
                case "recovery_pass_rotation": {
                    bitlockerRow.set("CONFIGURE_RECOVERY_PASSWORD_ROTATION", (Object)bitlockerAPIJSON.getInt(key));
                    continue;
                }
            }
        }
        if (!admxDataList.isEmpty()) {
            final Object admxGroupID = ADMXBackedPoliciesHandler.getInstance().addAdmxDataGroup(admxDataList, dataObject, "BITLOCKER_ADMX_POLICIES");
            bitlockerRow.set("ADMX_BACKED_POLICY_GROUP_ID", admxGroupID);
        }
        dataObject.addRow(bitlockerRow);
        MDMUtil.getPersistenceLite().update((DataObject)dataObject);
        return (Long)bitlockerRow.get("BITLOCKER_POLICY_ID");
    }
    
    public JSONObject getBitlockerPolicy(final Long bitlockerID) throws DataAccessException, JSONException {
        final Criteria bitlockerCriteria = new Criteria(new Column("BitlockerPolicy", "BITLOCKER_POLICY_ID"), (Object)bitlockerID, 0);
        final JSONObject bitlockerJSON = new JSONObject();
        final DataObject bitlockerDO = this.getBitlockerDO(bitlockerCriteria);
        final Row bitlockerRow = bitlockerDO.getRow("BitlockerPolicy");
        if (bitlockerRow != null) {
            final Object requireStorageCardEncryption = bitlockerRow.get("REQUIRE_STORAGE_CARD_ENCRYPTION");
            if (requireStorageCardEncryption != null) {
                bitlockerJSON.put("require_storage_card_encryption", requireStorageCardEncryption);
            }
            final Object enforceBitlocker = bitlockerRow.get("REQUIRE_DEVICE_ENCRYPTION");
            if (enforceBitlocker != null) {
                bitlockerJSON.put("enforce_bitlocker", enforceBitlocker);
            }
            final Object otherDriveEncryption = bitlockerRow.get("ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION");
            if (otherDriveEncryption != null) {
                bitlockerJSON.put("silent_encrypt_azure_ad_devices", !(boolean)otherDriveEncryption);
            }
            final Object standardUserEncryption = bitlockerRow.get("ALLOW_STANDARD_USER_ENCRYPTION");
            if (standardUserEncryption != null) {
                bitlockerJSON.put("allow_standard_user_encryption", standardUserEncryption);
            }
            final Object configureRecoveryPassRotation = bitlockerRow.get("CONFIGURE_RECOVERY_PASSWORD_ROTATION");
            if (configureRecoveryPassRotation != null) {
                bitlockerJSON.put("recovery_pass_rotation", configureRecoveryPassRotation);
            }
            final List<ADMXBackedPolicy> admxDataList = ADMXBackedPoliciesHandler.getInstance().getADMXData(bitlockerDO);
            this.populateADMXPolicies(admxDataList, bitlockerJSON);
            bitlockerJSON.put("BITLOCKER_POLICY_ID", (Object)bitlockerID);
            return bitlockerJSON;
        }
        return new JSONObject();
    }
    
    public Long getAdmxGroupId(final Long bitlockerID) throws Exception {
        final Object bitlockerDBValue = MDMDBUtil.getValueFromDB("BitlockerPolicy", "ADMX_BACKED_POLICY_GROUP_ID", (Object)bitlockerID, "BITLOCKER_POLICY_ID");
        return (bitlockerDBValue instanceof Long) ? ((Long)bitlockerDBValue) : null;
    }
    
    public boolean isValidBitlockerID(final Long bitlockerID) throws Exception {
        final Object bitlockerDBValue = MDMDBUtil.getValueFromDB("BitlockerPolicy", "BITLOCKER_POLICY_ID", (Object)bitlockerID, "BITLOCKER_POLICY_ID");
        return bitlockerDBValue != null;
    }
    
    public DataObject getBitlockerDO(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BitlockerPolicy"));
        ADMXBackedPoliciesHandler.getInstance().addADMXColumns(selectQuery, new Column("BitlockerPolicy", "ADMX_BACKED_POLICY_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "BITLOCKER_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "ADMX_BACKED_POLICY_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "CONFIGURE_RECOVERY_PASSWORD_ROTATION"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "ALLOW_STANDARD_USER_ENCRYPTION"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "REQUIRE_DEVICE_ENCRYPTION"));
        selectQuery.addSelectColumn(new Column("BitlockerPolicy", "REQUIRE_STORAGE_CARD_ENCRYPTION"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    private void populateADMXPolicies(final List<ADMXBackedPolicy> admxDataList, final JSONObject apiJSON) throws JSONException {
        for (final ADMXBackedPolicy admxData : admxDataList) {
            final JSONObject admxAPIJson = new JSONObject();
            final String gpName = admxData.getGpName();
            switch (gpName) {
                case "FDVRecoveryUsage_Name": {
                    admxAPIJson.put("allow_dra", Boolean.parseBoolean(admxData.getData().get("FDVAllowDRA_Name")));
                    admxAPIJson.put("recovery_password", Integer.parseInt(admxData.getData().get("FDVRecoveryPasswordUsageDropDown_Name")));
                    admxAPIJson.put("recovery_key", Integer.parseInt(admxData.getData().get("FDVRecoveryKeyUsageDropDown_Name")));
                    admxAPIJson.put("hide_recovery_options", Boolean.parseBoolean(admxData.getData().get("FDVHideRecoveryPage_Name")));
                    admxAPIJson.put("store_recovery_info_in_ad_ds", Boolean.parseBoolean(admxData.getData().get("FDVActiveDirectoryBackup_Name")));
                    admxAPIJson.put("store_key_packages_in_ad_ds", admxData.getData().get("FDVActiveDirectoryBackupDropDown_Name").equals("1"));
                    admxAPIJson.put("wait_for_recovery_info_backup_in_ad_ds", Boolean.parseBoolean(admxData.getData().get("FDVRequireActiveDirectoryBackup_Name")));
                    apiJSON.put("fixed_drive_recovery_options", (Object)admxAPIJson);
                    continue;
                }
                case "OSRecoveryUsage_Name": {
                    admxAPIJson.put("allow_dra", Boolean.parseBoolean(admxData.getData().get("OSAllowDRA_Name")));
                    admxAPIJson.put("recovery_password", Integer.parseInt(admxData.getData().get("OSRecoveryPasswordUsageDropDown_Name")));
                    admxAPIJson.put("recovery_key", Integer.parseInt(admxData.getData().get("OSRecoveryKeyUsageDropDown_Name")));
                    admxAPIJson.put("hide_recovery_options", Boolean.parseBoolean(admxData.getData().get("OSHideRecoveryPage_Name")));
                    admxAPIJson.put("store_recovery_info_in_ad_ds", Boolean.parseBoolean(admxData.getData().get("OSActiveDirectoryBackup_Name")));
                    admxAPIJson.put("store_key_packages_in_ad_ds", admxData.getData().get("OSActiveDirectoryBackupDropDown_Name").equals("1"));
                    admxAPIJson.put("wait_for_recovery_info_backup_in_ad_ds", Boolean.parseBoolean(admxData.getData().get("OSRequireActiveDirectoryBackup_Name")));
                    apiJSON.put("os_drive_recovery_options", (Object)admxAPIJson);
                    continue;
                }
                case "PrebootRecoveryInfo_Name": {
                    admxAPIJson.put("type", Integer.parseInt(admxData.getData().get("PrebootRecoveryInfoDropDown_Name")));
                    admxAPIJson.put("recovery_message", (Object)admxData.getData().get("RecoveryMessage_Input"));
                    admxAPIJson.put("recovery_url", (Object)admxData.getData().get("RecoveryUrl_Input"));
                    apiJSON.put("recovery_message", (Object)admxAPIJson);
                    continue;
                }
                case "MinimumPINLength_Name": {
                    apiJSON.put("min_pin_length", Integer.parseInt(admxData.getData().get("MinPINLength")));
                    continue;
                }
                case "ConfigureAdvancedStartup_Name": {
                    admxAPIJson.put("allow_non_tpm_devices", Boolean.parseBoolean(admxData.getData().get("ConfigureNonTPMStartupKeyUsage_Name")));
                    admxAPIJson.put("tpm", Integer.parseInt(admxData.getData().get("ConfigureTPMUsageDropDown_Name")));
                    admxAPIJson.put("tpm_pin", Integer.parseInt(admxData.getData().get("ConfigurePINUsageDropDown_Name")));
                    admxAPIJson.put("tpm_key", Integer.parseInt(admxData.getData().get("ConfigureTPMStartupKeyUsageDropDown_Name")));
                    admxAPIJson.put("tpm_key_pin", Integer.parseInt(admxData.getData().get("ConfigureTPMPINKeyUsageDropDown_Name")));
                    apiJSON.put("additional_startup_authentication", (Object)admxAPIJson);
                    continue;
                }
                case "EncryptionMethodWithXts_Name": {
                    admxAPIJson.put("os_drive", Integer.parseInt(admxData.getData().get("EncryptionMethodWithXtsOsDropDown_Name")));
                    admxAPIJson.put("removable_drive", Integer.parseInt(admxData.getData().get("EncryptionMethodWithXtsRdvDropDown_Name")));
                    admxAPIJson.put("fixed_drive", Integer.parseInt(admxData.getData().get("EncryptionMethodWithXtsFdvDropDown_Name")));
                    apiJSON.put("encryption_method", (Object)admxAPIJson);
                    continue;
                }
                case "FDVDenyWriteAccess_Name": {
                    apiJSON.put("fixed_drive_read_only", admxData.isEnabled());
                    continue;
                }
                case "RDVDenyWriteAccess_Name": {
                    apiJSON.put("removable_drive_read_only", admxData.isEnabled());
                    apiJSON.put("removable_drive_cross_origin", Boolean.parseBoolean(admxData.getData().get("RDVCrossOrg")));
                    continue;
                }
            }
        }
    }
    
    private ADMXBackedPolicy getFixedDriveRecoveryADMX(final JSONObject fixedDriveRecoveryApiJSON) throws JSONException {
        final ADMXBackedPolicy fixedDriveRecoveryADMXData = new ADMXBackedPolicy("FDVRecoveryUsage_Name", true);
        fixedDriveRecoveryADMXData.addData("FDVAllowDRA_Name", fixedDriveRecoveryApiJSON.get("allow_dra").toString().toLowerCase());
        fixedDriveRecoveryADMXData.addData("FDVRecoveryPasswordUsageDropDown_Name", fixedDriveRecoveryApiJSON.get("recovery_password").toString().toLowerCase());
        fixedDriveRecoveryADMXData.addData("FDVRecoveryKeyUsageDropDown_Name", fixedDriveRecoveryApiJSON.get("recovery_key").toString().toLowerCase());
        fixedDriveRecoveryADMXData.addData("FDVHideRecoveryPage_Name", fixedDriveRecoveryApiJSON.get("hide_recovery_options").toString().toLowerCase());
        fixedDriveRecoveryADMXData.addData("FDVActiveDirectoryBackup_Name", fixedDriveRecoveryApiJSON.get("store_recovery_info_in_ad_ds").toString().toLowerCase());
        if (fixedDriveRecoveryApiJSON.getBoolean("store_key_packages_in_ad_ds")) {
            fixedDriveRecoveryADMXData.addData("FDVActiveDirectoryBackupDropDown_Name", "1");
        }
        else {
            fixedDriveRecoveryADMXData.addData("FDVActiveDirectoryBackupDropDown_Name", "2");
        }
        fixedDriveRecoveryADMXData.addData("FDVRequireActiveDirectoryBackup_Name", fixedDriveRecoveryApiJSON.get("wait_for_recovery_info_backup_in_ad_ds").toString().toLowerCase());
        return fixedDriveRecoveryADMXData;
    }
    
    private ADMXBackedPolicy getOsDriveRecoveryADMX(final JSONObject osDriveRecoveryApiJSON) throws JSONException {
        final ADMXBackedPolicy osDriveRecoveryADMXData = new ADMXBackedPolicy("OSRecoveryUsage_Name", true);
        osDriveRecoveryADMXData.addData("OSAllowDRA_Name", osDriveRecoveryApiJSON.get("allow_dra").toString().toLowerCase());
        osDriveRecoveryADMXData.addData("OSRecoveryPasswordUsageDropDown_Name", osDriveRecoveryApiJSON.get("recovery_password").toString().toLowerCase());
        osDriveRecoveryADMXData.addData("OSRecoveryKeyUsageDropDown_Name", osDriveRecoveryApiJSON.get("recovery_key").toString().toLowerCase());
        osDriveRecoveryADMXData.addData("OSHideRecoveryPage_Name", osDriveRecoveryApiJSON.get("hide_recovery_options").toString().toLowerCase());
        osDriveRecoveryADMXData.addData("OSActiveDirectoryBackup_Name", osDriveRecoveryApiJSON.get("store_recovery_info_in_ad_ds").toString().toLowerCase());
        if (osDriveRecoveryApiJSON.getBoolean("store_key_packages_in_ad_ds")) {
            osDriveRecoveryADMXData.addData("OSActiveDirectoryBackupDropDown_Name", "1");
        }
        else {
            osDriveRecoveryADMXData.addData("OSActiveDirectoryBackupDropDown_Name", "2");
        }
        osDriveRecoveryADMXData.addData("OSRequireActiveDirectoryBackup_Name", osDriveRecoveryApiJSON.get("wait_for_recovery_info_backup_in_ad_ds").toString().toLowerCase());
        return osDriveRecoveryADMXData;
    }
    
    private ADMXBackedPolicy getRecoveryMessageADMX(final JSONObject recoveryMessageApiJSON) throws JSONException {
        final ADMXBackedPolicy recoveryMessageADMXData = new ADMXBackedPolicy("PrebootRecoveryInfo_Name", true);
        recoveryMessageADMXData.addData("PrebootRecoveryInfoDropDown_Name", recoveryMessageApiJSON.get("type").toString());
        recoveryMessageADMXData.addData("RecoveryMessage_Input", recoveryMessageApiJSON.optString("recovery_message", ""));
        recoveryMessageADMXData.addData("RecoveryUrl_Input", recoveryMessageApiJSON.optString("recovery_url", ""));
        return recoveryMessageADMXData;
    }
    
    private ADMXBackedPolicy getMinLenghtADMX(final String minPinLenght) {
        final ADMXBackedPolicy minPINLenADMXData = new ADMXBackedPolicy("MinimumPINLength_Name", true);
        minPINLenADMXData.addData("MinPINLength", minPinLenght);
        return minPINLenADMXData;
    }
    
    private ADMXBackedPolicy getStartUpAuthADMX(final JSONObject startUpAuthApiJSON) throws JSONException {
        final ADMXBackedPolicy additionalAuthADMXData = new ADMXBackedPolicy("ConfigureAdvancedStartup_Name", true);
        additionalAuthADMXData.addData("ConfigureNonTPMStartupKeyUsage_Name", startUpAuthApiJSON.get("allow_non_tpm_devices").toString().toLowerCase());
        additionalAuthADMXData.addData("ConfigureTPMUsageDropDown_Name", String.valueOf(startUpAuthApiJSON.getInt("tpm")));
        additionalAuthADMXData.addData("ConfigurePINUsageDropDown_Name", String.valueOf(startUpAuthApiJSON.getInt("tpm_pin")));
        additionalAuthADMXData.addData("ConfigureTPMStartupKeyUsageDropDown_Name", String.valueOf(startUpAuthApiJSON.getInt("tpm_key")));
        additionalAuthADMXData.addData("ConfigureTPMPINKeyUsageDropDown_Name", String.valueOf(startUpAuthApiJSON.getInt("tpm_key_pin")));
        return additionalAuthADMXData;
    }
    
    private ADMXBackedPolicy getEncryptionMethodADMX(final JSONObject encryptionMethodApiJSON) throws JSONException {
        final ADMXBackedPolicy encryptionMethodADMX = new ADMXBackedPolicy("EncryptionMethodWithXts_Name", true);
        encryptionMethodADMX.addData("EncryptionMethodWithXtsOsDropDown_Name", encryptionMethodApiJSON.get("os_drive").toString());
        encryptionMethodADMX.addData("EncryptionMethodWithXtsFdvDropDown_Name", encryptionMethodApiJSON.optString("fixed_drive", ""));
        encryptionMethodADMX.addData("EncryptionMethodWithXtsRdvDropDown_Name", encryptionMethodApiJSON.optString("removable_drive", ""));
        return encryptionMethodADMX;
    }
}
