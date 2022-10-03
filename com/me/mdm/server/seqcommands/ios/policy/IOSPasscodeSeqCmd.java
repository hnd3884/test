package com.me.mdm.server.seqcommands.ios.policy;

import com.adventnet.persistence.Row;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSPasscodeSeqCmd implements PolicySpecificSeqCommand
{
    private static Logger logger;
    private static final String PASSCODE_POLICY_HANDLER = "com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler";
    private static final String PASSCODE_DISABLE_PROFILE = "passcode_disable_profile.xml";
    private static final String PASSCODE_RESTRICT_PROFILE = "passcode_restrict_profile.xml";
    public static final String PROFILE_RESTRICT_IDENTIFIER = "com.mdm.passcode_restriction_install_profile";
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        final JSONObject policySeqParams = new JSONObject();
        try {
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            int installCommandOrder = policyParams.getInt("order");
            final Long customerID = policyParams.getLong("CUSTOMER_ID");
            final JSONObject params = policyParams.getJSONObject("params");
            String profileIdentifier = String.valueOf(params.get("PROFILE_PAYLOAD_IDENTIFIER"));
            int i = 0;
            while (i < configDoList.size()) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(172)) {
                    final Row passcodeRow = dataObject.getFirstRow("PasscodePolicy");
                    if (passcodeRow != null) {
                        final boolean restrictPasscode = (boolean)passcodeRow.get("RESTRICT_PASSCODE");
                        final boolean forcePasscode = (boolean)passcodeRow.get("FORCE_PASSCODE");
                        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionId);
                        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionId);
                        if (!forcePasscode && restrictPasscode) {
                            profileIdentifier += ".disablePasscode";
                            final String passcodePath = mdmProfileDir + File.separator + "passcode_disable_profile.xml";
                            final String passcodeRelativePath = mdmProfileRelativeDirPath + File.separator + "passcode_disable_profile.xml";
                            final String passcodeDisableCommandUUID = new PayloadHandler().generatePasscodeDisableRestriction(collectionId, passcodePath, profileIdentifier);
                            final Properties metaDataProperties = new Properties();
                            metaDataProperties.setProperty("commandUUID", passcodeDisableCommandUUID);
                            metaDataProperties.setProperty("commandType", "DisablePasscode");
                            metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                            metaDataProperties.setProperty("commandFilePath", passcodeRelativePath);
                            metaDataList.put((Object)metaDataProperties);
                            final JSONObject PasscodeDisableArrayObject = new JSONObject();
                            PasscodeDisableArrayObject.put("COMMAND_UUID", (Object)passcodeDisableCommandUUID);
                            PasscodeDisableArrayObject.put("order", installCommandOrder++);
                            PasscodeDisableArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler");
                            collectionArray.put((Object)PasscodeDisableArrayObject);
                            final String clearPasscodeUUID = "ClearPasscodeForPasscodeRestriction;Collection=" + collectionId.toString();
                            final Properties clearPasscodeProperties = new Properties();
                            clearPasscodeProperties.setProperty("commandUUID", clearPasscodeUUID);
                            clearPasscodeProperties.setProperty("commandType", "ClearPasscodeForPasscodeRestriction");
                            clearPasscodeProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                            metaDataList.put((Object)clearPasscodeProperties);
                            final JSONObject passcodeClearArrayObject = new JSONObject();
                            passcodeClearArrayObject.put("COMMAND_UUID", (Object)clearPasscodeUUID);
                            passcodeClearArrayObject.put("order", installCommandOrder++);
                            passcodeClearArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSPasscodeSeqCmdResponseHandler");
                            collectionArray.put((Object)passcodeClearArrayObject);
                            policySeqParams.put("isClearPasscodeCommand", true);
                            IOSPasscodeSeqCmd.logger.log(Level.INFO, "Added clear passcode for install profile collectionID:{0}", new Object[] { collectionId });
                        }
                        else if (forcePasscode && restrictPasscode) {
                            final String passcodePath = mdmProfileDir + File.separator + "passcode_restrict_profile.xml";
                            final String passcodeRelativePath = mdmProfileRelativeDirPath + File.separator + "passcode_restrict_profile.xml";
                            final String passcodeRestrictCommandUUID = new PayloadHandler().generateRestrictPasscode(collectionId, passcodePath);
                            final Properties metaDataProperties = new Properties();
                            metaDataProperties.setProperty("commandUUID", passcodeRestrictCommandUUID);
                            metaDataProperties.setProperty("commandType", "RestrictPasscode");
                            metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                            metaDataProperties.setProperty("commandFilePath", passcodeRelativePath);
                            metaDataList.put((Object)metaDataProperties);
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
            collectionObject.put("params", (Object)policySeqParams);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw e2;
        }
        return seqCmdObject;
    }
    
    static {
        IOSPasscodeSeqCmd.logger = Logger.getLogger("MDMConfigLogger");
    }
}
