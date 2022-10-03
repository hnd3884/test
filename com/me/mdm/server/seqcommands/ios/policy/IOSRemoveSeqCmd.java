package com.me.mdm.server.seqcommands.ios.policy;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemoveSeqCmd implements PolicySpecificSeqCommand
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        try {
            final int installCommandOrder = policyParams.getInt("order");
            final Long collectionId = policyParams.getLong("COLLECTION_ID");
            final String commandUUID = "RemoveProfile;Collection=" + collectionId.toString();
            final JSONObject collectionArrayObject = new JSONObject();
            collectionArrayObject.put("COMMAND_UUID", (Object)commandUUID);
            collectionArrayObject.put("order", installCommandOrder);
            collectionArrayObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            collectionArray.put((Object)collectionArrayObject);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in remove profile seq cmd", e);
            throw e;
        }
        return seqCmdObject;
    }
}
