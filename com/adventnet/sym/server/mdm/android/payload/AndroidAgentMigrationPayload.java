package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class AndroidAgentMigrationPayload extends AndroidPayload
{
    public AndroidAgentMigrationPayload() {
    }
    
    public AndroidAgentMigrationPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "AgentMigration", payloadIdentifier, payloadDisplayName);
    }
    
    public void setMigrationData(final JSONObject value) throws JSONException {
        this.getPayloadJSON().put("MigrationData", (Object)value);
    }
}
