package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultConfigurationHander;
import com.adventnet.sym.server.mdm.ios.payload.MACFileVaultPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import org.json.JSONObject;

public class DO2FileVaultPolicy implements DO2Payload
{
    public boolean isInstitutionalRecoveryKey;
    public boolean isPersonalRecoveryKey;
    public boolean canCreateEscrowPayload;
    public JSONObject personalConfigurationJSON;
    public JSONObject institutionalConfigurationJSON;
    public JSONObject generalConfigurationJSON;
    
    public DO2FileVaultPolicy() {
        this.isInstitutionalRecoveryKey = false;
        this.isPersonalRecoveryKey = false;
        this.canCreateEscrowPayload = false;
        this.personalConfigurationJSON = null;
        this.institutionalConfigurationJSON = null;
        this.generalConfigurationJSON = null;
    }
    
    public IOSPayload createFileVaultPayload(final Long fileFaultSettingsID) {
        final IOSPayload payload = this.getFileVaultPayload(fileFaultSettingsID);
        return payload;
    }
    
    private MACFileVaultPayload getFileVaultPayload(final Long settingsID) {
        MACFileVaultPayload payload = null;
        try {
            final JSONObject encryptionSettingsJSON = (JSONObject)MDMFileVaultConfigurationHander.getFileVaultDetails(settingsID, null).getJSONArray("ResponseData").get(0);
            payload = new MACFileVaultPayload(1, "MDM", "com.mdm.mac.filevault", "FileVault Configuration");
            if (encryptionSettingsJSON != null) {
                final JSONObject generalSettingsJSON = encryptionSettingsJSON.getJSONObject("MDMFileVaultSettings");
                payload.setDiffer(true);
                payload.setEnable(true);
                payload.setDeferDontAskAtUserLogout(false);
                payload.setDeferForceAtUserLoginMaxBypassAttempts(0);
                this.generalConfigurationJSON = generalSettingsJSON;
                final int recoveryType = generalSettingsJSON.getInt("RECOVERY_KEY_TYPE");
                if (recoveryType == 1 || recoveryType == 3) {
                    final JSONObject personalRKJSON = encryptionSettingsJSON.getJSONObject("MDMFileVaultPersonalKeyConfiguration");
                    this.personalConfigurationJSON = personalRKJSON;
                    payload.setUseRecoveryKey(true);
                    payload.setShowRecoveryKey(personalRKJSON.getBoolean("SHOW_RECOVERY_KEY"));
                    this.canCreateEscrowPayload = this.generalConfigurationJSON.getBoolean("COPY_RECOVERY_KEY_TO_MDM");
                }
                if (recoveryType == 2) {
                    payload.setUseRecoveryKey(false);
                }
                if (recoveryType == 2 || recoveryType == 3) {
                    this.isInstitutionalRecoveryKey = true;
                    final JSONObject institutionalJSON = encryptionSettingsJSON.getJSONObject("MDMFileVaultInstitutionConfiguration");
                    this.institutionalConfigurationJSON = institutionalJSON;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2FileVaultPolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return payload;
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final Row fileVaultRow = dataObject.getRow("MacFileVault2Policy");
            final Long settingsID = (Long)fileVaultRow.get("ENCRYPTION_SETTINGS_ID");
            final IOSPayload[] payloadArray = { this.createFileVaultPayload(settingsID) };
            return payloadArray;
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(DO2FileVaultPolicy.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
}
