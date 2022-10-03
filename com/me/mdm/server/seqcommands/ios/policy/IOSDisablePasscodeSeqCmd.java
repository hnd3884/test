package com.me.mdm.server.seqcommands.ios.policy;

import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSDisablePasscodeSeqCmd implements PolicySpecificSeqCommand
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        try {
            new IOSRemovePasscodeSeqCmd().addRemovePasscodeDisableCommand(policyParams, metaDataList, collectionArray);
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            IOSDisablePasscodeSeqCmd.LOGGER.log(Level.SEVERE, "Exception in disable passcode seq cmd", e);
            throw e;
        }
        return seqCmdObject;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
