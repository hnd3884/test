package com.me.mdm.server.seqcommands.ios.policy;

import org.json.JSONException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemoveSingletonRestrictionSeqCmd implements PolicySpecificSeqCommand
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        try {
            this.addRemoveSingletonRestrictionCommand(policyParams, metaDataList, collectionArray);
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in creating remove singleton restriction", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    public int addRemoveSingletonRestrictionCommand(final JSONObject policyParams, final JSONArray metaDataList, final JSONArray collectionArray) {
        int installCommandOrder = 0;
        try {
            installCommandOrder = policyParams.getInt("order");
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            final String commandUUID = "RemoveSingletonRestriction;Collection=" + collectionId.toString();
            final Properties installProperties = new Properties();
            installProperties.setProperty("commandUUID", commandUUID);
            installProperties.setProperty("commandType", "RemoveSingletonRestriction");
            installProperties.setProperty("dynamicVariable", "false");
            metaDataList.put((Object)installProperties);
            final JSONObject collectionArrayObject = new JSONObject();
            collectionArrayObject.put("COMMAND_UUID", (Object)commandUUID);
            collectionArrayObject.put("order", installCommandOrder);
            collectionArrayObject.put("handler", (Object)"com.me.mdm.server.profiles.ios.IOSSingletonRemRestrictSeqCmdResHandler");
            collectionArray.put((Object)collectionArrayObject);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "JSONException in creating remove singleton restriction", (Throwable)e);
        }
        return installCommandOrder;
    }
}
