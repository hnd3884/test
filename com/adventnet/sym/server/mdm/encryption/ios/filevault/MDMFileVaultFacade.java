package com.adventnet.sym.server.mdm.encryption.ios.filevault;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.encryption.MDMEncryptionSettingsHandler;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class MDMFileVaultFacade
{
    public JSONObject getFileVaultDetail(final JSONObject request) throws JSONException, DataAccessException {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long enryptionSettingId = APIUtil.getResourceID(request, "filevault_id");
        final JSONObject fileVaultJSON = MDMFileVaultConfigurationHander.getFileVaultDetails(enryptionSettingId, new Criteria(new Column("MDMEncryptionSettings", "CUSTOMER_ID"), (Object)customerId, 0));
        final JSONArray fileVaultArray = fileVaultJSON.getJSONArray("ResponseData");
        if (fileVaultArray == null || fileVaultArray.length() == 0) {
            throw new APIHTTPException("COM0008", new Object[] { enryptionSettingId });
        }
        final JSONObject filevaultResponseJSON = fileVaultArray.getJSONObject(0);
        this.removeUnwantedKeyResponse(filevaultResponseJSON);
        return JSONUtil.getInstance().changeJSONKeyCase(filevaultResponseJSON, 2);
    }
    
    public JSONObject addFileVault(final JSONObject request) throws Exception {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long userId = APIUtil.getUserID(request);
        final JSONObject requestJSON = request.getJSONObject("msg_body");
        final JSONObject filevaultJSON = this.convertFileVaultJSONToServerJSON(requestJSON);
        this.validateFilevaultJSON(filevaultJSON);
        filevaultJSON.put("CUSTOMER_ID", (Object)customerId);
        filevaultJSON.put("ADDED_USER", (Object)userId);
        filevaultJSON.put("MODIFIED_USER", (Object)userId);
        final JSONObject filevaultConfigJSON = MDMFileVaultConfigurationHander.addOrUpdateMDMFileVaultSettings(filevaultJSON);
        if (filevaultConfigJSON.has("Error")) {
            throw new APIHTTPException("ENC0001", new Object[0]);
        }
        final JSONObject finalJSON = filevaultConfigJSON.getJSONObject("ResponseData");
        final JSONObject resourceJSON = new JSONObject();
        resourceJSON.put("filevault_id", finalJSON.get("ENCRYPTION_SETTINGS_ID"));
        final JSONObject messageHeaderJSON = request.getJSONObject("msg_header");
        messageHeaderJSON.put("resource_identifier", (Object)resourceJSON);
        return this.getFileVaultDetail(request);
    }
    
    public JSONObject modifyFileVault(final JSONObject request) throws Exception {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long userId = APIUtil.getUserID(request);
        final Long enryptionSettingId = APIUtil.getResourceID(request, "filevault_id");
        if (MDMDeviceEncryptionSettingsHandler.isResourceAssociatedForEncryptionId(enryptionSettingId)) {
            throw new APIHTTPException("ENC0002", new Object[0]);
        }
        final JSONObject requestJSON = request.getJSONObject("msg_body");
        final JSONObject filevaultJSON = this.convertFileVaultJSONToServerJSON(requestJSON);
        this.validateFilevaultJSON(filevaultJSON);
        filevaultJSON.put("CUSTOMER_ID", (Object)customerId);
        filevaultJSON.put("ADDED_USER", (Object)userId);
        filevaultJSON.put("MODIFIED_USER", (Object)userId);
        filevaultJSON.put("ENCRYPTION_SETTINGS_ID", (Object)enryptionSettingId);
        final JSONObject filevaultConfigJSON = MDMFileVaultConfigurationHander.addOrUpdateMDMFileVaultSettings(filevaultJSON);
        if (filevaultConfigJSON.has("Error")) {
            throw new APIHTTPException("ENC0001", new Object[0]);
        }
        final JSONObject finalJSON = filevaultConfigJSON.getJSONObject("ResponseData");
        final JSONObject resourceJSON = new JSONObject();
        resourceJSON.put("filevault_id", finalJSON.get("ENCRYPTION_SETTINGS_ID"));
        final JSONObject messageHeaderJSON = request.getJSONObject("msg_header");
        messageHeaderJSON.put("resource_identifier", (Object)resourceJSON);
        return this.getFileVaultDetail(request);
    }
    
    public void deleteFilevault(final JSONObject request) throws JSONException {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long enryptionSettingId = APIUtil.getResourceID(request, "filevault_id");
        if (MDMDeviceEncryptionSettingsHandler.isResourceAssociatedForEncryptionId(enryptionSettingId)) {
            throw new APIHTTPException("ENC0003", new Object[0]);
        }
        if (!MDMEncryptionSettingsHandler.checkCustomerForEncryptionSetting(enryptionSettingId, customerId)) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        if (!MDMEncryptionSettingsHandler.deleteEncryptionSettings(enryptionSettingId, customerId)) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject convertFileVaultJSONToServerJSON(final JSONObject filevaultJSON) throws APIHTTPException {
        final JSONObject serverJSON = new JSONObject();
        try {
            final Iterator iterator = filevaultJSON.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                if (key.equalsIgnoreCase("MDMEncryptionSettings")) {
                    final JSONObject encryptionJSON = filevaultJSON.getJSONObject(key);
                    final Iterator encryptionIterator = encryptionJSON.keys();
                    while (encryptionIterator.hasNext()) {
                        final String encryptionKey = encryptionIterator.next();
                        serverJSON.put(encryptionKey.toUpperCase(), encryptionJSON.get(encryptionKey));
                    }
                }
                else if (key.equalsIgnoreCase("MDMFileVaultSettings")) {
                    final JSONObject filevaultSettingJSON = filevaultJSON.getJSONObject(key);
                    final Iterator filevaultIterator = filevaultSettingJSON.keys();
                    while (filevaultIterator.hasNext()) {
                        final String filevaultSettingKey = filevaultIterator.next();
                        serverJSON.put(filevaultSettingKey.toUpperCase(), filevaultSettingJSON.get(filevaultSettingKey));
                    }
                }
                else if (key.equalsIgnoreCase("MDMFileVaultPersonalKeyConfiguration")) {
                    final JSONObject personalRecoveryJSON = filevaultJSON.getJSONObject(key);
                    final Iterator personalRecoveryIterator = personalRecoveryJSON.keys();
                    while (personalRecoveryIterator.hasNext()) {
                        final String personalRecoveryKey = personalRecoveryIterator.next();
                        serverJSON.put(personalRecoveryKey.toUpperCase(), personalRecoveryJSON.get(personalRecoveryKey));
                    }
                }
                else {
                    if (!key.equalsIgnoreCase("MDMFileVaultInstitutionConfiguration")) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    final JSONObject institutionalRecoveryKeyJSON = filevaultJSON.getJSONObject(key);
                    final Iterator institutionalIterator = institutionalRecoveryKeyJSON.keys();
                    while (institutionalIterator.hasNext()) {
                        final String institutionalKey = institutionalIterator.next();
                        serverJSON.put(institutionalKey.toUpperCase(), institutionalRecoveryKeyJSON.get(institutionalKey));
                    }
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        return serverJSON;
    }
    
    private void removeUnwantedKeyResponse(final JSONObject filevaultJSON) throws APIHTTPException {
        try {
            filevaultJSON.getJSONObject("MDMFileVaultSettings").remove("ENCRYPTION_SETTINGS_ID");
            if (filevaultJSON.has("MDMFileVaultInstitutionConfiguration")) {
                filevaultJSON.getJSONObject("MDMFileVaultInstitutionConfiguration").remove("ENCRYPTION_SETTINGS_ID");
            }
            if (filevaultJSON.has("MDMFileVaultPersonalKeyConfiguration")) {
                filevaultJSON.getJSONObject("MDMFileVaultPersonalKeyConfiguration").remove("ENCRYPTION_SETTINGS_ID");
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void validateFilevaultJSON(final JSONObject filevaultJSON) throws APIHTTPException {
        try {
            filevaultJSON.get("SETTINGS_NAME");
            final Integer type = filevaultJSON.getInt("RECOVERY_KEY_TYPE");
            if (type == 2 && type == 3) {
                filevaultJSON.getLong("INSTITUTION_ENCRYPTION_CERT");
            }
        }
        catch (final JSONException e) {
            final String message = e.getMessage();
            final String[] keyarray = message.split("\"");
            throw new APIHTTPException("COM0005", new Object[] { keyarray[1] });
        }
    }
}
