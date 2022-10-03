package com.me.mdm.server.seqcommands.ios.policy;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemoveRestrictionSeqCmd implements PolicySpecificSeqCommand
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        try {
            final List keyList = (List)policyParams.get("keylist");
            final boolean removeSingletonConfigured = keyList.contains("SingletonRestriction");
            if (!removeSingletonConfigured) {
                new IOSRemoveSingletonRestrictionSeqCmd().addRemoveSingletonRestrictionCommand(policyParams, metaDataList, collectionArray);
            }
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in remove restrict passcode profile seq cmd", e);
            throw e;
        }
        return seqCmdObject;
    }
}
