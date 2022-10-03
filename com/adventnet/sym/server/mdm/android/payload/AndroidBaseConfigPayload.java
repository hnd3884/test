package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

public class AndroidBaseConfigPayload extends AndroidPayload
{
    AndroidBaseConfigPayload() {
    }
    
    AndroidBaseConfigPayload(final int commandVersion, final String commandType, final String commandIdentifier, final String commandDisplayName) throws JSONException {
        final String commandUUID = UUID.randomUUID().toString();
        final JSONObject payload = this.getPayloadJSON();
        payload.put("PayloadVersion", commandVersion);
        payload.put("PayloadUUID", (Object)commandUUID);
        payload.put("PayloadType", (Object)commandType);
        payload.put("PayloadIdentifier", (Object)commandIdentifier);
        payload.put("PayloadDisplayName", (Object)commandDisplayName);
    }
}
