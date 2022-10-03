package com.me.mdm.server.seqcommands.ios.policy;

import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSAppLockSeqCmd implements PolicySpecificSeqCommand
{
    public static final String APP_LOCK_POLICY_HANDLER = "com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler";
    
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
            for (int i = 0; i < configDoList.size(); ++i) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(183)) {
                    final Row appLockRow = dataObject.getFirstRow("AppLockPolicy");
                    final int kioskType = (int)appLockRow.get("KIOSK_MODE");
                    if (kioskType == 3) {
                        final String singleWebAppCommandUUID = "SingleWebAppKioskAppConfiguration;Collection=" + collectionId.toString();
                        final Properties metaDataProperties = new Properties();
                        metaDataProperties.setProperty("commandUUID", singleWebAppCommandUUID);
                        metaDataProperties.setProperty("commandType", "SingleWebAppKioskAppConfiguration");
                        metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                        metaDataList.put((Object)metaDataProperties);
                        final JSONObject singleWebAppArrayObject = new JSONObject();
                        singleWebAppArrayObject.put("COMMAND_UUID", (Object)singleWebAppCommandUUID);
                        singleWebAppArrayObject.put("order", installCommandOrder++);
                        singleWebAppArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler");
                        collectionArray.put((Object)singleWebAppArrayObject);
                        final String singleWebAppFeedbackCommandUUID = "SingleWebAppKioskFeedback;Collection=" + collectionId.toString();
                        final Properties feedbackProperties = new Properties();
                        feedbackProperties.setProperty("commandUUID", singleWebAppFeedbackCommandUUID);
                        feedbackProperties.setProperty("commandType", "SingleWebAppKioskFeedback");
                        feedbackProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                        metaDataList.put((Object)feedbackProperties);
                        final JSONObject singleWebAppFeedbackArrayObject = new JSONObject();
                        singleWebAppFeedbackArrayObject.put("COMMAND_UUID", (Object)singleWebAppFeedbackCommandUUID);
                        singleWebAppFeedbackArrayObject.put("order", installCommandOrder++);
                        singleWebAppFeedbackArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler");
                        collectionArray.put((Object)singleWebAppFeedbackArrayObject);
                    }
                    else if (kioskType == 2) {
                        final boolean showMEMDMApp = (boolean)appLockRow.get("SHOW_ME_MDM_APP");
                        if (!showMEMDMApp) {
                            final JSONObject showMEMDM = new JSONObject();
                            final String removeAppCatalog = "DefaultRemoveAppCatalogWebClips;Collection=" + collectionId.toString();
                            final Properties feedbackProperties2 = new Properties();
                            feedbackProperties2.setProperty("commandUUID", removeAppCatalog);
                            feedbackProperties2.setProperty("commandType", "DefaultRemoveAppCatalogWebClips");
                            feedbackProperties2.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                            metaDataList.put((Object)feedbackProperties2);
                            showMEMDM.put("COMMAND_UUID", (Object)removeAppCatalog);
                            showMEMDM.put("order", installCommandOrder++);
                            showMEMDM.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSAppLockSeqCmdResponseHandler");
                            collectionArray.put((Object)showMEMDM);
                        }
                    }
                    collectionObject.put("metaDataList", (Object)metaDataList);
                    collectionObject.put("collectionArray", (Object)collectionArray);
                    collectionObject.put("params", (Object)policySeqParams);
                    seqCmdObject.put("collection", (Object)collectionObject);
                }
            }
        }
        catch (final Exception e) {
            throw e;
        }
        return seqCmdObject;
    }
}
