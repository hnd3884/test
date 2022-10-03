package com.adventnet.sym.server.mdm.ios.payload.transform;

import org.json.JSONException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultConfigurationHander;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.MACFileVaultRecoveryKeyEscrowPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class Do2MacFileVaultEscrowPolicy implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final IOSPayload[] payloadArray = { null };
            final Row fileVaultRow = dataObject.getRow("MacFileVault2Policy");
            final Long settingsID = (Long)fileVaultRow.get("ENCRYPTION_SETTINGS_ID");
            final IOSPayload iosPayload = this.createFileVaultEscrowPayLoad(settingsID);
            payloadArray[0] = iosPayload;
            return payloadArray;
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(DO2FileVaultPolicy.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    private IOSPayload createFileVaultEscrowPayLoad(final Long settingsID) {
        final IOSPayload payload = this.getFileVaultPayload(settingsID);
        return payload;
    }
    
    private MACFileVaultRecoveryKeyEscrowPayload getFileVaultPayload(final Long settingsID) {
        MACFileVaultRecoveryKeyEscrowPayload payload = null;
        try {
            final JSONObject encryptionSettingsJSON = (JSONObject)MDMFileVaultConfigurationHander.getFileVaultDetails(settingsID, null).getJSONArray("ResponseData").get(0);
            payload = new MACFileVaultRecoveryKeyEscrowPayload(1, "MDM", "com.mdm.mac.filevault_Escrow", "FileVault Recovery Key Escrow Configuration");
            if (encryptionSettingsJSON != null) {
                final JSONObject generalSettingsJSON = encryptionSettingsJSON.getJSONObject("MDMFileVaultSettings");
                payload.setLocation((String)generalSettingsJSON.get("MESSAGE_TO_USER"));
                final JSONObject personalKeyJSON = encryptionSettingsJSON.getJSONObject("MDMFileVaultPersonalKeyConfiguration");
                payload.setDeviceKey((String)personalKeyJSON.get("RECOVERY_KEY_IDENTIFIER"));
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(Do2MacFileVaultEscrowPolicy.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final JSONException ex2) {
            Logger.getLogger(Do2MacFileVaultEscrowPolicy.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
        }
        return payload;
    }
}
