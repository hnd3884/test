package com.me.mdm.server.seqcommands.ios.policy;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRestrictPasscodeSeqCmd implements PolicySpecificSeqCommand
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray collectionArray = new JSONArray();
        final JSONArray metaDataList = new JSONArray();
        new IOSRemovePasscodeSeqCmd().addRemoveRestrictPasscodeCommand(policyParams, metaDataList, collectionArray);
        collectionObject.put("metaDataList", (Object)metaDataList);
        collectionObject.put("collectionArray", (Object)collectionArray);
        seqCmdObject.put("collection", (Object)collectionObject);
        return seqCmdObject;
    }
}
