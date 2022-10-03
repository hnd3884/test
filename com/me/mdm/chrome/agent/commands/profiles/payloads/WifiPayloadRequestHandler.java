package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONException;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.commands.profiles.ONCPayload;

public class WifiPayloadRequestHandler extends ONCPayloadRequestHandler
{
    @Override
    protected void addConfigToONCProfile(final ONCPayload existingPayload, final JSONObject payloadData) throws JSONException {
        existingPayload.addNetworkConfig(payloadData);
    }
    
    @Override
    protected boolean removeConfigInONC(final ONCPayload existingPayload, final String guid) throws JSONException {
        return existingPayload.removeNetworkConfigIfExist(guid);
    }
}
