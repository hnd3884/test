package com.me.mdm.server.seqcommands.ios.policy;

import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSSharedRestrictionSeqCmd implements PolicySpecificSeqCommand
{
    private static final Logger LOGGER;
    private static final String SHARED_DEVICE_CONFIGURATION_FILE = "shared_device_config.xml";
    private static final String POLICY_HANDLER = "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler";
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        int installCommandOrder = 0;
        final JSONObject seqCmdObject = new JSONObject();
        try {
            final JSONObject collectionObject = new JSONObject();
            final JSONArray collectionArray = new JSONArray();
            final JSONArray metaDataList = new JSONArray();
            final JSONObject policySeqParams = new JSONObject();
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            installCommandOrder = policyParams.getInt("order");
            final Long customerID = policyParams.getLong("CUSTOMER_ID");
            final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionId);
            final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionId);
            final String sharedConfigurationPath = mdmProfileDir + File.separator + "shared_device_config.xml";
            final String sharedConfigurationRelativePath = mdmProfileRelativeDirPath + File.separator + "shared_device_config.xml";
            final String sharedConfigurationCommandUUID = new PayloadHandler().generateSharedDeviceRestrictions(collectionId, configDoList, sharedConfigurationPath);
            if (!MDMStringUtils.isEmpty(sharedConfigurationCommandUUID)) {
                final Properties metaDataProperties = new Properties();
                metaDataProperties.setProperty("commandUUID", sharedConfigurationCommandUUID);
                metaDataProperties.setProperty("commandType", "SharedDeviceRestrictions");
                metaDataProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                metaDataProperties.setProperty("commandFilePath", sharedConfigurationRelativePath);
                metaDataList.put((Object)metaDataProperties);
                final JSONObject sharedArrayObject = new JSONObject();
                sharedArrayObject.put("COMMAND_UUID", (Object)sharedConfigurationCommandUUID);
                sharedArrayObject.put("order", installCommandOrder++);
                sharedArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
                collectionArray.put((Object)sharedArrayObject);
                collectionObject.put("metaDataList", (Object)metaDataList);
                collectionObject.put("collectionArray", (Object)collectionArray);
                policySeqParams.put("SharedDeviceRestrictions", true);
                collectionObject.put("params", (Object)policySeqParams);
                seqCmdObject.put("collection", (Object)collectionObject);
            }
        }
        catch (final Exception e) {
            IOSSharedRestrictionSeqCmd.LOGGER.log(Level.SEVERE, "Exception in shared device seqcmd", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
