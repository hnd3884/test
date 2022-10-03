package com.me.mdm.server.seqcommands.ios.policy;

import java.util.logging.Level;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRestrictionProfileStatusSeqCmd implements PolicySpecificSeqCommand
{
    private static final Logger LOGGER;
    
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
            final String commandUUID = "RestrictionProfileStatus;Collection=" + collectionId.toString();
            final Properties metaDataProperties = new Properties();
            metaDataProperties.setProperty("commandUUID", commandUUID);
            metaDataProperties.setProperty("commandType", "RestrictionProfileStatus");
            metaDataProperties.setProperty("dynamicVariable", "false");
            metaDataList.put((Object)metaDataProperties);
            final JSONObject collectionArrayObject = new JSONObject();
            collectionArrayObject.put("COMMAND_UUID", (Object)commandUUID);
            collectionArrayObject.put("order", installCommandOrder);
            collectionArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            collectionArray.put((Object)collectionArrayObject);
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            policySeqParams.put("RestrictionProfileStatus", true);
            collectionObject.put("params", (Object)policySeqParams);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in IOSRestrictionProfileStatusSeqCmd", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
