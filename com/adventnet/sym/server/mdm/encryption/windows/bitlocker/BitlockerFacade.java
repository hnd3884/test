package com.adventnet.sym.server.mdm.encryption.windows.bitlocker;

import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class BitlockerFacade
{
    public JSONObject addBitlockerPolicy(final JSONObject message) throws Exception {
        final JSONObject requestJSON = this.validatePostBodyJSON(message);
        final Long bitlocerID = new BitlockerHandler().addBitlockerPolicy(requestJSON);
        return new BitlockerHandler().getBitlockerPolicy(bitlocerID);
    }
    
    public JSONObject getBitlockerPolicy(final JSONObject message) throws DataAccessException, JSONException {
        final Long bitlockerID = APIUtil.getResourceID(message, "policie_id");
        final JSONObject bitlockerJSON = new BitlockerHandler().getBitlockerPolicy(bitlockerID);
        if (bitlockerJSON.length() == 0) {
            throw new APIHTTPException("COM0024", new Object[0]);
        }
        return bitlockerJSON;
    }
    
    private JSONObject validatePostBodyJSON(final JSONObject message) throws JSONException, APIHTTPException {
        if (!message.has("msg_body")) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        final JSONObject requestJSON = message.getJSONObject("msg_body");
        if (requestJSON.length() == 0) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        final Iterator<String> keys = requestJSON.keys();
        while (keys.hasNext()) {
            final String s;
            final String key = s = keys.next();
            switch (s) {
                case "recovery_message": {
                    this.validateRecoveryMessage(requestJSON.getJSONObject(key));
                    continue;
                }
                case "encryption_method": {
                    this.validateEncryptionMethod(requestJSON.getJSONObject(key));
                    continue;
                }
                case "additional_startup_authentication": {
                    this.validateAdditionalAuthentication(requestJSON.getJSONObject(key));
                    continue;
                }
                case "os_drive_recovery_options":
                case "fixed_drive_recovery_options": {
                    this.validateRecoveryDriveOption(requestJSON.getJSONObject(key), key);
                    continue;
                }
                case "removable_drive_cross_origin": {
                    if (!requestJSON.has("removable_drive_read_only") || !requestJSON.getBoolean("removable_drive_read_only")) {
                        throw new APIHTTPException("COM0005", new Object[] { "removable_drive_cross_origin" });
                    }
                    continue;
                }
            }
        }
        return requestJSON;
    }
    
    private void validateRecoveryMessage(final JSONObject jsonObject) throws JSONException, APIHTTPException {
        if (!jsonObject.has("type")) {
            throw new APIHTTPException("COM0005", new Object[] { "recovery_message" });
        }
        switch (jsonObject.getInt("type")) {
            case 0:
            case 1: {
                return;
            }
            case 2: {
                if (!jsonObject.has("recovery_message")) {
                    throw new APIHTTPException("COM0005", new Object[] { "recovery_message" });
                }
                break;
            }
            case 3: {
                if (!jsonObject.has("recovery_url")) {
                    throw new APIHTTPException("COM0005", new Object[] { "recovery_message" });
                }
                break;
            }
            default: {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
        }
    }
    
    private void validateEncryptionMethod(final JSONObject jsonObject) throws APIHTTPException {
        if (!jsonObject.has("fixed_drive") || !jsonObject.has("removable_drive") || !jsonObject.has("os_drive")) {
            throw new APIHTTPException("COM0005", new Object[] { "encryption_method" });
        }
    }
    
    private void validateRecoveryDriveOption(final JSONObject jsonObject, final String key) throws JSONException, APIHTTPException {
        if (!jsonObject.has("store_key_packages_in_ad_ds") || !jsonObject.has("store_recovery_info_in_ad_ds") || !jsonObject.has("wait_for_recovery_info_backup_in_ad_ds") || !jsonObject.has("recovery_key") || !jsonObject.has("recovery_password") || !jsonObject.has("allow_dra") || !jsonObject.has("hide_recovery_options")) {
            throw new APIHTTPException("COM0005", new Object[] { key });
        }
        if (jsonObject.getInt("recovery_key") == 0 && jsonObject.getInt("recovery_password") == 0) {
            throw new APIHTTPException("COM0005", new Object[] { key });
        }
        if ((jsonObject.getInt("recovery_key") == 1 || jsonObject.getInt("recovery_password") == 1) && jsonObject.getBoolean("hide_recovery_options")) {
            throw new APIHTTPException("COM0005", new Object[] { key });
        }
        if (!jsonObject.getBoolean("store_recovery_info_in_ad_ds") && (jsonObject.getBoolean("store_key_packages_in_ad_ds") || jsonObject.getBoolean("wait_for_recovery_info_backup_in_ad_ds"))) {
            throw new APIHTTPException("COM0005", new Object[] { key });
        }
        if (jsonObject.getBoolean("hide_recovery_options") && !jsonObject.getBoolean("store_recovery_info_in_ad_ds")) {
            throw new APIHTTPException("COM0005", new Object[] { key });
        }
    }
    
    private void validateAdditionalAuthentication(final JSONObject jsonObject) throws JSONException, APIHTTPException {
        if (!jsonObject.has("allow_non_tpm_devices") || !jsonObject.has("tpm") || !jsonObject.has("tpm_key") || !jsonObject.has("tpm_pin") || !jsonObject.has("tpm_key_pin")) {
            throw new APIHTTPException("COM0005", new Object[] { "additional_startup_authentication" });
        }
        if (jsonObject.getInt("tpm") == 1 && (jsonObject.getInt("tpm_key_pin") != 0 || jsonObject.getInt("tpm_key") != 0 || jsonObject.getInt("tpm_pin") != 0)) {
            throw new APIHTTPException("COM0005", new Object[] { "additional_startup_authentication" });
        }
        if (jsonObject.getInt("tpm_key_pin") == 1 && (jsonObject.getInt("tpm") != 0 || jsonObject.getInt("tpm_key") != 0 || jsonObject.getInt("tpm_pin") != 0)) {
            throw new APIHTTPException("COM0005", new Object[] { "additional_startup_authentication" });
        }
        if (jsonObject.getInt("tpm_key") == 1 && (jsonObject.getInt("tpm_key_pin") != 0 || jsonObject.getInt("tpm") != 0 || jsonObject.getInt("tpm_pin") != 0)) {
            throw new APIHTTPException("COM0005", new Object[] { "additional_startup_authentication" });
        }
        if (jsonObject.getInt("tpm_pin") == 1 && (jsonObject.getInt("tpm_key_pin") != 0 || jsonObject.getInt("tpm_key") != 0 || jsonObject.getInt("tpm") != 0)) {
            throw new APIHTTPException("COM0005", new Object[] { "additional_startup_authentication" });
        }
    }
}
