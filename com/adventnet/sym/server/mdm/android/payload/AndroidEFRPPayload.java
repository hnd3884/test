package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONArray;
import org.json.JSONException;

public class AndroidEFRPPayload extends AndroidPayload
{
    public AndroidEFRPPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "EnterpriseFactoryResetSettings", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAuthorizedMailIds(final JSONArray mailUserIds) throws JSONException {
        this.getPayloadJSON().put("AuthorizedAccountIds", (Object)mailUserIds);
    }
    
    public void setFRPSwitch(final Boolean status) throws JSONException {
        this.getPayloadJSON().put("IsEFRPOn", (Object)status);
    }
}
