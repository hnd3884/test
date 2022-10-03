package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction.ChromeBrowserRestrictionManager;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class ChromeBrowserRestrictionsPayloadRequestHandler extends PayloadRequestHandler
{
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            new ChromeBrowserRestrictionManager().setChromeBrowserRestrictions(context, payloadData, payloadResp);
        }
        catch (final Exception e) {
            this.logger.info("Exception while Parsing the CHromeBrowserPayload");
        }
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, modifyPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            this.logger.info("Going to remove applied Restrictions");
            final Context context = request.getContainer().getContext();
            new ChromeBrowserRestrictionManager().revertBrowserRestrictions(context, payloadResp);
        }
        catch (final Exception e) {
            this.logger.info("Exception while applying the Chrome Payload Restriction");
        }
    }
}
