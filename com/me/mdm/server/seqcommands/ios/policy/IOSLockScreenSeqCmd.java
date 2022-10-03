package com.me.mdm.server.seqcommands.ios.policy;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.Properties;
import com.me.mdm.server.profiles.LockScreenDataHandler;
import com.me.mdm.server.profiles.ios.IOSLockScreenHandler;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSLockScreenSeqCmd implements PolicySpecificSeqCommand
{
    private static Logger logger;
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject lockScreenCommandObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        try {
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            final int installCommandOrder = policyParams.getInt("order");
            for (int i = 0; i < configDoList.size(); ++i) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(522)) {
                    final Row lockScreenRow = dataObject.getFirstRow("LockScreenConfiguration");
                    final Integer orientation = (Integer)lockScreenRow.get("ORIENTATION");
                    final IOSLockScreenHandler iosLockScreenHandler = new IOSLockScreenHandler();
                    final List resolutionList = iosLockScreenHandler.getiOSUniqueDeviceResolution(orientation);
                    final LockScreenDataHandler handler = new LockScreenDataHandler();
                    handler.generateImageForPayload(dataObject, resolutionList);
                    final Properties lockScreenProperties = new Properties();
                    final String commandUUID = "LockScreenMessages;Collection=" + collectionId.toString();
                    lockScreenProperties.setProperty("commandUUID", commandUUID);
                    lockScreenProperties.setProperty("commandType", "LockScreenMessages");
                    lockScreenProperties.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
                    metaDataList.put((Object)lockScreenProperties);
                    final JSONObject collectionArrayObject = new JSONObject();
                    collectionArrayObject.put("COMMAND_UUID", (Object)commandUUID);
                    collectionArrayObject.put("order", installCommandOrder);
                    collectionArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
                    collectionArray.put((Object)collectionArrayObject);
                    IOSLockScreenSeqCmd.logger.log(Level.INFO, "Sequential command generated for Lockscreen payload of collection {0} and metaData{1}", new Object[] { collectionId, metaDataList.toString() });
                    break;
                }
            }
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            lockScreenCommandObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            IOSLockScreenSeqCmd.logger.log(Level.SEVERE, "Exception while creating seq cmd for lock screen", e);
            throw new Exception("mdm.profile.lockscreen.publish.failed");
        }
        return lockScreenCommandObject;
    }
    
    static {
        IOSLockScreenSeqCmd.logger = Logger.getLogger("MDMConfigLogger");
    }
}
