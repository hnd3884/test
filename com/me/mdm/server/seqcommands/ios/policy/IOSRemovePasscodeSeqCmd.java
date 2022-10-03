package com.me.mdm.server.seqcommands.ios.policy;

import org.json.JSONException;
import java.util.Properties;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemovePasscodeSeqCmd implements PolicySpecificSeqCommand
{
    private static final String REMOVE_PASSCODE_DISABLE_PROFILE = "remove_passcode_disable_profile.xml";
    private static final String PASSCODE_POLICY_HANDLER = "com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler";
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        try {
            int installCommandOrder = policyParams.getInt("order");
            final JSONObject params = policyParams.getJSONObject("params");
            final List keyList = (List)policyParams.get("keylist");
            final boolean removeDisablePasscode = keyList.contains("DisablePasscode");
            final boolean removeRestrictPasscode = keyList.contains("RestrictPasscode");
            int i = 0;
            while (i < configDoList.size()) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(172)) {
                    final Row passcodeRow = dataObject.getFirstRow("PasscodePolicy");
                    if (passcodeRow != null) {
                        final boolean restrictPasscode = (boolean)passcodeRow.get("RESTRICT_PASSCODE");
                        final boolean forcePasscode = (boolean)passcodeRow.get("FORCE_PASSCODE");
                        if (!forcePasscode && restrictPasscode && !removeDisablePasscode) {
                            installCommandOrder = this.addRemovePasscodeDisableCommand(policyParams, metaDataList, collectionArray);
                            policyParams.put("order", installCommandOrder);
                        }
                        else if (forcePasscode && restrictPasscode && !removeRestrictPasscode) {
                            installCommandOrder = this.addRemoveRestrictPasscodeCommand(policyParams, metaDataList, collectionArray);
                            policyParams.put("order", installCommandOrder);
                        }
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in remove passcode profile seq cmd", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    public int addRemovePasscodeDisableCommand(final JSONObject policyParams, final JSONArray metaDataList, final JSONArray collectionArray) {
        int installCommandOrder = 1;
        try {
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            installCommandOrder = policyParams.getInt("order");
            final Long customerID = policyParams.getLong("CUSTOMER_ID");
            final JSONObject params = policyParams.getJSONObject("params");
            String profileIdentifier = String.valueOf(params.get("PROFILE_PAYLOAD_IDENTIFIER"));
            profileIdentifier += ".disablePasscode";
            final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionId);
            final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionId);
            final String passcodePath = mdmProfileDir + File.separator + "remove_passcode_disable_profile.xml";
            final String passcodeRelativePath = mdmProfileRelativeDirPath + File.separator + "remove_passcode_disable_profile.xml";
            final String passcodeDisableCommandUUID = new PayloadHandler().generateRemovePasscodeDisableRestriction(collectionId, passcodePath, profileIdentifier);
            final Properties metaDataProperties = new Properties();
            metaDataProperties.setProperty("commandUUID", passcodeDisableCommandUUID);
            metaDataProperties.setProperty("commandType", "RemoveDisablePasscode");
            metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataProperties.setProperty("commandFilePath", passcodeRelativePath);
            metaDataList.put((Object)metaDataProperties);
            final JSONObject PasscodeDisableArrayObject = new JSONObject();
            PasscodeDisableArrayObject.put("COMMAND_UUID", (Object)passcodeDisableCommandUUID);
            PasscodeDisableArrayObject.put("order", installCommandOrder++);
            PasscodeDisableArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler");
            collectionArray.put((Object)PasscodeDisableArrayObject);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "JSONException in remove passcode profile seq cmd", (Throwable)e);
        }
        return installCommandOrder;
    }
    
    public int addRemoveRestrictPasscodeCommand(final JSONObject policyParams, final JSONArray metaDataList, final JSONArray collectionArray) {
        int installCommandOrder = 0;
        try {
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            installCommandOrder = policyParams.getInt("order");
            final String passcodeRestrictCommandUUID = "RemoveRestrictedPasscode;Collection=" + collectionId.toString();
            final Properties metaDataProperties = new Properties();
            metaDataProperties.setProperty("commandUUID", passcodeRestrictCommandUUID);
            metaDataProperties.setProperty("commandType", "RemoveRestrictedPasscode");
            metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.put((Object)metaDataProperties);
            final JSONObject removePasswordArrayObject = new JSONObject();
            removePasswordArrayObject.put("COMMAND_UUID", (Object)passcodeRestrictCommandUUID);
            removePasswordArrayObject.put("order", installCommandOrder++);
            removePasswordArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler");
            collectionArray.put((Object)removePasswordArrayObject);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in remove disable passcode profile seq cmd", (Throwable)e);
        }
        return installCommandOrder;
    }
}
