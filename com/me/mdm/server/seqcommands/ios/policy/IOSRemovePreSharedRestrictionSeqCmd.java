package com.me.mdm.server.seqcommands.ios.policy;

import org.json.JSONObject;
import java.util.List;

public class IOSRemovePreSharedRestrictionSeqCmd extends IOSRemoveSharedRestrictionSeqCmd
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final List keyList = (List)policyParams.get("keylist");
        if (keyList.contains(String.valueOf(527))) {
            return new JSONObject();
        }
        return super.getSequentialCommandForPolicy(configDoList, policyParams);
    }
}
