package com.me.mdm.onpremise.server.android.agent;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.util.CloudAPIDataPost;
import com.me.mdm.onpremise.util.MDMUtilImpl;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.util.MDMCheckSumProvider;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class AndroidAgentSecretsHandler
{
    private static AndroidAgentSecretsHandler handler;
    private static Logger logger;
    
    public static AndroidAgentSecretsHandler getInstance() {
        return AndroidAgentSecretsHandler.handler;
    }
    
    public static String getGCMProjectId() throws DataAccessException {
        final DataObject dO = getExistingDO();
        if (dO.isEmpty()) {
            throw new RuntimeException("GCM Project Id not found");
        }
        final Row row = dO.getFirstRow("AndroidAgentSecrets");
        return (String)row.get("GCM_PROJECT_ID");
    }
    
    public static String getGCMAPIKey() throws DataAccessException {
        final DataObject dO = getExistingDO();
        if (dO.isEmpty()) {
            throw new RuntimeException("GCM API Key not found");
        }
        final Row row = dO.getFirstRow("AndroidAgentSecrets");
        return (String)row.get("GCM_API_KEY");
    }
    
    public static String getELMKey() throws DataAccessException {
        final DataObject dO = getExistingDO();
        if (dO.isEmpty()) {
            throw new RuntimeException("ELM Key not found");
        }
        final Row row = dO.getFirstRow("AndroidAgentSecrets");
        return (String)row.get("ELM_KEY");
    }
    
    public static String getAttestationKey() throws DataAccessException {
        final DataObject dO = getExistingDO();
        if (dO.isEmpty()) {
            throw new RuntimeException("Attestation Key not found");
        }
        final Row row = dO.getFirstRow("AndroidAgentSecrets");
        return (String)row.get("ATTESTATION_KEY");
    }
    
    public static String getBackwardCompatibilityElmKey() throws DataAccessException {
        final DataObject dataObject = getExistingDO();
        if (dataObject.isEmpty()) {
            throw new RuntimeException("BackwardCompatibility key not found");
        }
        final Row row = dataObject.getFirstRow("AndroidAgentSecrets");
        return (String)row.get("BACKWARD_COMPATIBILITY_KEY");
    }
    
    private static DataObject getExistingDO() throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAgentSecrets"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        return MDMUtil.getPersistence().get(sQuery);
    }
    
    public boolean setElmKeys(final String elmKey, final String backwardCompatibilityElmKey) throws DataAccessException {
        final DataObject dataObject = getExistingDO();
        if (dataObject.isEmpty()) {
            AndroidAgentSecretsHandler.logger.log(Level.SEVERE, "No data in ANDROIDAGENTSECRETS table, problem in XML default population");
        }
        else {
            final Row row = dataObject.getFirstRow("AndroidAgentSecrets");
            row.set("ELM_KEY", (Object)elmKey);
            row.set("BACKWARD_COMPATIBILITY_KEY", (Object)backwardCompatibilityElmKey);
            dataObject.updateRow(row);
        }
        MDMUtil.getPersistence().update(dataObject);
        return true;
    }
    
    public JSONObject getElmKeysFromDB() throws DataAccessException {
        final DataObject dataObject = getExistingDO();
        final JSONObject elmKeysFromDB = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("AndroidAgentSecrets");
            elmKeysFromDB.put("ELMKey", (Object)row.get("ELM_KEY"));
            elmKeysFromDB.put("BackwardCompatibilityElmKey", (Object)row.get("BACKWARD_COMPATIBILITY_KEY"));
        }
        return elmKeysFromDB;
    }
    
    public void checkForELMKeyUpdateInCreator(final String elmKeyHashFromAPI) {
        try {
            final JSONObject elmKeysFromDB = this.getElmKeysFromDB();
            final String elmKeyInDB = elmKeysFromDB.optString("ELMKey");
            final String backwardCompatibilityKeyInDB = elmKeysFromDB.optString("BackwardCompatibilityElmKey");
            final String elmKeyHashFromDB = (elmKeyInDB != null) ? MDMCheckSumProvider.getInstance().getSHA256HashFromString(elmKeyInDB) : null;
            AndroidAgentSecretsHandler.logger.log(Level.INFO, "Onpremise Distributed task for ELM keys update");
            final Boolean hashNotEqaulsCheck = elmKeyHashFromAPI != null && elmKeyHashFromDB != null && !elmKeyHashFromAPI.equals(elmKeyHashFromDB);
            if (elmKeyHashFromAPI == null || elmKeyHashFromDB == null || hashNotEqaulsCheck) {
                AndroidAgentSecretsHandler.logger.log(Level.INFO, "Getting ELM keys from secure cloud API");
                final JSONObject elmKeysFromCreator = this.getElmKeysFromCreator();
                final String elmKeyFromCreator = String.valueOf(elmKeysFromCreator.optString("ELMKey"));
                final String backwardCompatibilityElmKeyFromCreator = String.valueOf(elmKeysFromCreator.optString("BackwardCompatibilityElmKey"));
                if (!elmKeyFromCreator.isEmpty() && !backwardCompatibilityElmKeyFromCreator.isEmpty() && (backwardCompatibilityKeyInDB == null || !elmKeyFromCreator.equals(elmKeyInDB) || !backwardCompatibilityElmKeyFromCreator.equals(backwardCompatibilityKeyInDB))) {
                    final boolean isElmKeysUpdated = this.setElmKeys(elmKeyFromCreator, backwardCompatibilityElmKeyFromCreator);
                    if (isElmKeysUpdated) {
                        MessageProvider.getInstance().hideMessage("ELM_NOT_CONFIGURED");
                        AndroidAgentSecretsHandler.logger.log(Level.INFO, "ELM keys updated successfully");
                    }
                }
            }
        }
        catch (final Exception e) {
            AndroidAgentSecretsHandler.logger.log(Level.SEVERE, "Exception in checking creator ELM key isUpdate ", e);
        }
    }
    
    public JSONObject updateElmKeysInDB() {
        final Logger logger = Logger.getLogger(MDMUtilImpl.class.getName());
        final JSONObject response = new JSONObject();
        try {
            final JSONObject elmKeysFromCreator = this.getElmKeysFromCreator();
            final String elmKeyFromCreator = String.valueOf(elmKeysFromCreator.optString("ELMKey"));
            final String backwardCompatibilityElmKeyFromCreator = String.valueOf(elmKeysFromCreator.optString("BackwardCompatibilityElmKey"));
            if (!elmKeyFromCreator.isEmpty() && !backwardCompatibilityElmKeyFromCreator.isEmpty()) {
                final AndroidAgentSecretsHandler androidAgentSecretsHandler = new AndroidAgentSecretsHandler();
                final boolean isElmKeysUpdated = androidAgentSecretsHandler.setElmKeys(elmKeyFromCreator, backwardCompatibilityElmKeyFromCreator);
                if (isElmKeysUpdated) {
                    MessageProvider.getInstance().hideMessage("ELM_NOT_CONFIGURED");
                    logger.log(Level.INFO, "ELM keys updated successfully");
                    response.put("RESPONSE", (Object)"ELM keys updated successfully");
                }
            }
            else {
                logger.log(Level.SEVERE, "Error in getting ELM keys from creator {0}", new Object[] { elmKeysFromCreator.optString("error") });
                response.put("RESPONSE", (Object)"ELM keys not updated");
                response.put("error", (Object)elmKeysFromCreator.optString("error"));
                response.put("error_code", (Object)elmKeysFromCreator.optString("error_code"));
            }
        }
        catch (final Exception e) {
            logger.log(Level.INFO, "Exception occurred : ", e);
        }
        return response;
    }
    
    public JSONObject getElmKeysFromCreator() {
        final JSONObject elmKeys = new JSONObject();
        final Logger logger = Logger.getLogger(MDMUtilImpl.class.getName());
        try {
            final String postUrl = "https://mdm.manageengine.com/api/v1/mdm/secretkeys";
            final CloudAPIDataPost postData = new CloudAPIDataPost();
            final JSONObject submitJSONObject = new JSONObject();
            submitJSONObject.put("keys", (Object)"ELM_KEYS");
            postData.encryptAndPostDataToCloud(postUrl, submitJSONObject, "keys");
            if (postData.status.toString().startsWith("20")) {
                final String responseContent = postData.response;
                logger.log(Level.INFO, "Data for ELM Key obtained from creator successfully.");
                if (SyMUtil.isValidJSON(responseContent)) {
                    final JSONObject responseJSONObject = new JSONObject(responseContent);
                    elmKeys.put("ELMKey", (Object)responseJSONObject.optString("Knox_Standard_Onpremise_Key"));
                    elmKeys.put("BackwardCompatibilityElmKey", (Object)responseJSONObject.optString("Backward_Compatibility_Key"));
                }
                else {
                    logger.log(Level.INFO, "Failed due to Some Error in Creator Response JSON is Not valid ");
                    elmKeys.put("error", (Object)"JSON from creator is not valid");
                    elmKeys.put("error_code", (Object)"COM0015");
                }
            }
            else {
                logger.log(Level.INFO, "Failed due to Some Error in Creator unable to download the creator data");
                elmKeys.put("error", (Object)"Unable to download JSON from creator");
                elmKeys.put("error_code", (Object)"COM0015");
            }
        }
        catch (final Exception e) {
            logger.log(Level.INFO, "Exception occurred in getting ELM keys from creator: ", e);
        }
        return elmKeys;
    }
    
    static {
        AndroidAgentSecretsHandler.handler = new AndroidAgentSecretsHandler();
        AndroidAgentSecretsHandler.logger = Logger.getLogger(AndroidAgentSecretsHandler.class.getName());
    }
}
