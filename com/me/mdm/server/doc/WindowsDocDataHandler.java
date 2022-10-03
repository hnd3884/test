package com.me.mdm.server.doc;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;

public class WindowsDocDataHandler extends DocMgmtDataHandler
{
    @Override
    public JSONObject getDiffAndUpdateStatus(final Long deviceID, final Long agentLastSyncAt, final boolean ackSupported) throws JSONException {
        final JSONObject superResponse = super.getDiffAndUpdateStatus(deviceID, agentLastSyncAt, ackSupported);
        final JSONObject response = new JSONObject();
        final JSONObject msgResponse = new JSONObject();
        final Iterator iterator = superResponse.keys();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            if (key.equalsIgnoreCase("Status") || key.equalsIgnoreCase("MsgResponseType")) {
                response.put(key, superResponse.get(key));
            }
            else {
                msgResponse.put(key, superResponse.get(key));
            }
        }
        response.put("MsgResponse", (Object)msgResponse);
        return response;
    }
}
