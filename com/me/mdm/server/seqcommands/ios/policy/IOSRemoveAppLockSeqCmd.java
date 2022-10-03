package com.me.mdm.server.seqcommands.ios.policy;

import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemoveAppLockSeqCmd implements PolicySpecificSeqCommand
{
    private static final String REMOVE_KIOSK_RESTRICTION_PROFILE = "Remove_kiosk_restriction_profile.xml";
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray metaDataList = new JSONArray();
        final JSONArray collectionArray = new JSONArray();
        try {
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            for (int i = 0; i < configDoList.size(); ++i) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(183)) {
                    final Row appLockRow = dataObject.getFirstRow("AppLockPolicy");
                    final int kioskType = (int)appLockRow.get("KIOSK_MODE");
                    if (kioskType != 2) {
                        final int installCommandOrder = this.addRemoveCustomKioskProfile(policyParams, metaDataList, collectionArray);
                        policyParams.put("order", installCommandOrder);
                    }
                    if (kioskType == 3) {
                        int installCommandOrder = this.addRemoveSingleWebAppKioskCommand(policyParams, metaDataList, collectionArray);
                        final String singleWebAppFeedbackCommandUUID = "RemoveSingleWebAppKioskFeedback;Collection=" + collectionId.toString();
                        final Properties feedbackProperties = new Properties();
                        feedbackProperties.setProperty("commandUUID", singleWebAppFeedbackCommandUUID);
                        feedbackProperties.setProperty("commandType", "RemoveSingleWebAppKioskFeedback");
                        feedbackProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                        metaDataList.put((Object)feedbackProperties);
                        final JSONObject singleWebAppFeedbackArrayObject = new JSONObject();
                        singleWebAppFeedbackArrayObject.put("COMMAND_UUID", (Object)singleWebAppFeedbackCommandUUID);
                        singleWebAppFeedbackArrayObject.put("order", installCommandOrder++);
                        singleWebAppFeedbackArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler");
                        collectionArray.put((Object)singleWebAppFeedbackArrayObject);
                    }
                }
            }
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in remove app lock profile seq cmd", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    public int addRemoveSingleWebAppKioskCommand(final JSONObject policyParams, final JSONArray metaDataList, final JSONArray collectionArray) {
        int installCommandOrder = policyParams.getInt("order");
        final Long collectionId = policyParams.getLong("COLLECTION_ID");
        final String singleWebAppCommandUUID = "RemoveSingleWebAppKioskAppConfiguration;Collection=" + collectionId.toString();
        final Properties metaDataProperties = new Properties();
        metaDataProperties.setProperty("commandUUID", singleWebAppCommandUUID);
        metaDataProperties.setProperty("commandType", "RemoveSingleWebAppKioskAppConfiguration");
        metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
        metaDataList.put((Object)metaDataProperties);
        final JSONObject singleWebAppArrayObject = new JSONObject();
        singleWebAppArrayObject.put("COMMAND_UUID", (Object)singleWebAppCommandUUID);
        singleWebAppArrayObject.put("order", installCommandOrder++);
        singleWebAppArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler");
        collectionArray.put((Object)singleWebAppArrayObject);
        return installCommandOrder;
    }
    
    public int addRemoveCustomKioskProfile(final JSONObject policyParams, final JSONArray metaDataList, final JSONArray collectionArray) {
        int installCommandOrder = policyParams.getInt("order");
        final Long collectionId = policyParams.getLong("COLLECTION_ID");
        final Long customerID = policyParams.getLong("CUSTOMER_ID");
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionId);
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionId);
        final String kioskRemoveProfile = mdmProfileDir + File.separator + "Remove_kiosk_restriction_profile.xml";
        final String kioskRemoveProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "Remove_kiosk_restriction_profile.xml";
        final String removeCommandUUID = PayloadHandler.getInstance().generateCustomKioskRemoveProfile(collectionId, kioskRemoveProfile);
        final Properties removeProfile = new Properties();
        removeProfile.setProperty("commandUUID", removeCommandUUID);
        removeProfile.setProperty("commandType", "RemoveKioskDefaultRestriction");
        removeProfile.setProperty("commandFilePath", kioskRemoveProfileRelativePath);
        removeProfile.setProperty("dynamicVariable", "false");
        metaDataList.put((Object)removeProfile);
        final JSONObject kioskArrayObject = new JSONObject();
        kioskArrayObject.put("COMMAND_UUID", (Object)removeCommandUUID);
        kioskArrayObject.put("order", installCommandOrder++);
        kioskArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
        collectionArray.put((Object)kioskArrayObject);
        return installCommandOrder;
    }
}
