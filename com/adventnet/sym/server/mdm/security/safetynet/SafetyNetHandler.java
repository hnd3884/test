package com.adventnet.sym.server.mdm.security.safetynet;

import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Base64;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.security.AgentSecretHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SafetyNetHandler
{
    Logger mdmLogger;
    
    public SafetyNetHandler() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject getSafetyNetCredentials(final String udid) throws Exception {
        this.mdmLogger.log(Level.INFO, "Fetching the safety net credentials for {0} at {1}", new Object[] { udid, System.currentTimeMillis() });
        final AgentSecretHandler secretHandler = new AgentSecretHandler();
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        final String attestationApiKey = this.getAttestationApiKey();
        final String nonce = secretHandler.generateNonce(udid);
        if (attestationApiKey.isEmpty()) {
            throw new Exception("Could not fetch the API key");
        }
        final Long uniqueId = secretHandler.insertSecretsRowInDb(resourceID, nonce);
        final JSONObject responseJson = new JSONObject();
        responseJson.put("ApiKey", (Object)attestationApiKey);
        responseJson.put("Nonce", (Object)nonce);
        responseJson.put("SafetyNetId", (Object)uniqueId);
        return responseJson;
    }
    
    public void storeSafetyNetResponse(final JSONObject response) throws Exception {
        final String responseObject = String.valueOf(((JSONObject)response.get("Message")).get("AttestationResponse"));
        final String safetyNetId = String.valueOf(((JSONObject)response.get("Message")).get("SafetyNetId"));
        final String udid = String.valueOf(response.get("UDID"));
        final AttestationResponse attestationResponse = new AttestationVerifier().parseAndVerify(responseObject);
        if (this.isValidResponse(attestationResponse, safetyNetId)) {
            this.mdmLogger.log(Level.INFO, "The response from agent is valid so store it in DB");
            this.writeResponseToDB(safetyNetId, attestationResponse, this.getAdviceFromResponse(response));
            this.handlePostResponseFunctions(attestationResponse, udid);
        }
        else {
            this.mdmLogger.log(Level.INFO, " The response from agent is valid, delete secret details and save the failed details");
            if (this.isAlreadyAttestedDevice(udid)) {
                this.mdmLogger.log(Level.INFO, " Already attested device so no need to store error code data");
            }
            else {
                this.mdmLogger.log(Level.INFO, "Storing the error code as failed attestation verifying");
                this.storeErrorStates(13000, "Cannot decode the response", udid);
            }
        }
        new AgentSecretHandler().deleteSecretDetails(safetyNetId);
    }
    
    private String getAttestationApiKey() {
        try {
            return MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("AttestationApiKey");
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, "Cannot get the attestation API key ", exp);
            return "";
        }
    }
    
    public JSONObject decodeAttestationResponse(final String res) throws Exception {
        final String[] jwtParts = res.split("\\.");
        JSONObject decodedPayload;
        if (jwtParts.length == 3) {
            decodedPayload = new JSONObject(new String(Base64.getDecoder().decode(jwtParts[1])));
            decodedPayload.put("AttestationDecodeStatus", 1);
        }
        else {
            decodedPayload = new JSONObject();
            decodedPayload.put("AttestationDecodeStatus", 0);
        }
        return decodedPayload;
    }
    
    public boolean isValidResponse(final AttestationResponse response, final String safetyNetId) throws Exception {
        if (!response.getApkPackageName().equalsIgnoreCase("com.manageengine.mdm.android") && !response.getApkPackageName().equalsIgnoreCase("com.manageengine.mdm.samsung.knox")) {
            this.mdmLogger.log(Level.INFO, "SafetNetHandler : Invalid package name in response");
            return false;
        }
        if (!this.isValidNonce(response.getNonce(), safetyNetId)) {
            this.mdmLogger.log(Level.INFO, "SafetNetHandler : Invalid nonce in response");
            return false;
        }
        return true;
    }
    
    private boolean isValidNonce(final byte[] nonceInResponse, final String safetynetId) throws Exception {
        final String responseNonce = new String(nonceInResponse);
        final String storedNonce = new AgentSecretHandler().getNonceForId(safetynetId);
        if (responseNonce.equalsIgnoreCase(storedNonce)) {
            return true;
        }
        this.mdmLogger.log(Level.INFO, "SafetNetHandler : Nonce do not match so discard request");
        return false;
    }
    
    private String getAdviceFromResponse(final JSONObject response) {
        try {
            final JSONObject decodedResponse = this.decodeAttestationResponse(response.toString());
            if (decodedResponse.has("advice")) {
                return String.valueOf(decodedResponse.get("advice"));
            }
            return "";
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, " The advice field is not found", exp);
            return "";
        }
    }
    
    private void writeResponseToDB(final String safetyNetId, final AttestationResponse attResponse, final String advice) {
        try {
            final Long resourceId = new AgentSecretHandler().getResourceIdFromSecretId(safetyNetId);
            final SelectQuery securityDetailsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
            securityDetailsQuery.addSelectColumn(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"));
            securityDetailsQuery.setCriteria(new Criteria(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"), (Object)resourceId, 0));
            final DataObject dObj = DataAccess.get(securityDetailsQuery);
            Row securityRow;
            if (!dObj.isEmpty()) {
                securityRow = dObj.getFirstRow("SafetyNetStatus");
            }
            else {
                securityRow = new Row("SafetyNetStatus");
                securityRow.set("RESOURCE_ID", (Object)resourceId);
            }
            securityRow.set("SAFETYNET_BASIC_INTEGRITY", (Object)attResponse.hasBasicIntegrity());
            securityRow.set("SAFETYNET_CTS", (Object)attResponse.isCtsProfileMatch());
            securityRow.set("SAFETYNET_TIMESTAMP", (Object)attResponse.getTimestampMs());
            securityRow.set("SAFETYNET_ADVICE", (Object)advice);
            securityRow.set("SAFETYNET_AVAILABIITY", (Object)true);
            if (dObj.isEmpty()) {
                dObj.addRow(securityRow);
            }
            else {
                dObj.updateRow(securityRow);
            }
            DataAccess.update(dObj);
        }
        catch (final DataAccessException exp) {
            this.mdmLogger.log(Level.SEVERE, "SafetyNetHandler : writeResponseToDB : exception while writing the final attestation response details in DB ", (Throwable)exp);
        }
    }
    
    public void handlePostResponseFunctions(final AttestationResponse attestationResponse, final String udid) {
        final Long deviceResourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceResourceId);
        final JSONObject complianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerId);
        if (complianceRules.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES", false)) {
            if (!attestationResponse.hasBasicIntegrity()) {
                this.mdmLogger.log(Level.INFO, " Wiping device : {0} because it failed the integrity check", udid);
                this.addWipeCommandToQueue(deviceResourceId, 1, "mdm.agent.wipe.integrity_failed", "The device failed the basic integrity check and doesn't comply with the organization's requirements for management.");
            }
            else if (complianceRules.optBoolean("WIPE_CTS_FAILED_DEVICES", false) && !attestationResponse.isCtsProfileMatch()) {
                this.mdmLogger.log(Level.INFO, " Wiping device : {0} because it failed the CTS check", udid);
                this.addWipeCommandToQueue(deviceResourceId, 2, "mdm.agent.wipe.cts_failed", "The device failed the CTS(compatibility check) check and doesn't comply with the organization's requirements for management.");
            }
        }
    }
    
    public void handlePostResponseWipe(final Long deviceResourceId, final int reasonForWipe, final String reasonForWipI18Key, final String reasonForWipeText) {
        try {
            this.mdmLogger.log(Level.INFO, " The user has configured to wipe integrity failed devices, so wiping device with udid : {0}", ManagedDeviceHandler.getInstance().getUDIDFromResourceID(deviceResourceId));
            final Long configuredCustomerId = (Long)MdComplianceRulesHandler.getInstance().getComplianceRuleConfigUserId(CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceResourceId));
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, deviceResourceId, DMUserHandler.getUserName(configuredCustomerId), "mdm.agent.compliance.action.corporatewipe_security_failed", ManagedDeviceHandler.getInstance().getDeviceName(deviceResourceId), CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceResourceId));
            this.changeManagedDeviceStatus(deviceResourceId);
            ManagedDeviceHandler.getInstance().addReasonForCommand(deviceResourceId, reasonForWipe, reasonForWipI18Key, reasonForWipeText, DeviceCommandRepository.getInstance().getCommandID("CorporateWipe"));
            DeviceInvCommandHandler.getInstance().sendCommandToDevice(new DeviceDetails(deviceResourceId), "CorporateWipe", configuredCustomerId);
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, " Exception while wiping a device ", exp);
        }
    }
    
    private void addWipeCommandToQueue(final Long resId, final int wipeReason, final String reasonForWipeI18Key, final String reasonForWipeText) {
        final CommonQueueData safetyNetData = new CommonQueueData();
        safetyNetData.setCustomerId(CustomerInfoUtil.getInstance().getCustomerIDForResID(resId));
        safetyNetData.setClassName("com.adventnet.sym.server.mdm.security.safetynet.SafetyNetQueueActions");
        try {
            final JSONObject details = new JSONObject();
            details.put("DeviceWipeResId", (Object)resId);
            details.put("WipeReason", wipeReason);
            details.put("WipeReasonI18", (Object)reasonForWipeI18Key);
            details.put("WipeReasonFeedBack", (Object)reasonForWipeText);
            safetyNetData.setTaskName("WipeDevice");
            safetyNetData.setJsonQueueData(details);
            CommonQueueUtil.getInstance().addToQueue(safetyNetData, CommonQueues.MDM_ENROLLMENT);
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.INFO, " SafetyNetHandler : Exception while adding the wipe command to queue ", exp);
        }
    }
    
    private void changeManagedDeviceStatus(final Long resId) throws Exception {
        final JSONObject deviceDetails = new JSONObject();
        deviceDetails.put("UDID", (Object)ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resId));
        deviceDetails.put("RESOURCE_ID", (Object)resId);
        deviceDetails.put("MANAGED_STATUS", 4);
        deviceDetails.put("UNREGISTERED_TIME", System.currentTimeMillis());
        deviceDetails.put("REMARKS", (Object)"mdm.enroll.safety_net_failed");
        deviceDetails.put("PLATFORM_TYPE", 2);
        ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(deviceDetails);
    }
    
    public boolean isError(final JSONObject obj) {
        return obj.has("ErrorCode");
    }
    
    public boolean isAlreadyAttestedDevice(final Long resId) {
        final SelectQuery attestationQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
        attestationQuery.addSelectColumn(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"));
        attestationQuery.addSelectColumn(Column.getColumn("SafetyNetStatus", "SAFETYNET_BASIC_INTEGRITY"));
        attestationQuery.setCriteria(new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_BASIC_INTEGRITY"), (Object)null, 2).and(new Criteria(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"), (Object)resId, 0)));
        try {
            final DataObject result = DataAccess.get(attestationQuery);
            return result.isEmpty();
        }
        catch (final DataAccessException exp) {
            this.mdmLogger.log(Level.SEVERE, "Exception while getting the previous ", (Throwable)exp);
            return true;
        }
    }
    
    public boolean isAlreadyAttestedDevice(final String udid) {
        return this.isAlreadyAttestedDevice(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid));
    }
    
    public void storeErrorStates(final int errorCode, final String errorMsg, final String udid) {
        try {
            this.mdmLogger.log(Level.INFO, " There was an error while getting the safety net attestation for device {0} reason : {1}", new Object[] { udid, errorMsg });
            final SelectQuery errorStateWritingQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
            errorStateWritingQuery.addSelectColumn(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"));
            errorStateWritingQuery.setCriteria(new Criteria(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"), (Object)ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid), 0));
            final DataObject dObj = DataAccess.get(errorStateWritingQuery);
            Row errorRow;
            if (dObj.isEmpty()) {
                errorRow = new Row("SafetyNetStatus");
            }
            else {
                errorRow = dObj.getFirstRow("SafetyNetStatus");
            }
            errorRow.set("SAFETYNET_ERROR_CODE", (Object)errorCode);
            errorRow.set("SAFETYNET_ERROR_REASON", (Object)errorMsg);
            errorRow.set("SAFETYNET_AVAILABIITY", (Object)false);
            if (dObj.isEmpty()) {
                dObj.addRow(errorRow);
            }
            else {
                dObj.updateRow(errorRow);
            }
            DataAccess.update(dObj);
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, " Error while writing the error state to DB ", exp);
        }
    }
}
