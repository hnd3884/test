package com.me.mdm.server.agent.mac;

import java.util.Map;
import com.me.mdm.server.enrollment.ios.MacBootstrapTokenHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class SetBootstrapTokenReqProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        Logger.getLogger("MDMLogger").log(Level.INFO, "inside SetBootstrapTokenReqProcessor.processQueuedCommand()");
        try {
            final JSONObject hashPlist = params.getJSONObject("hashPlist");
            final Long resourceId = (Long)params.opt("RESOURCE_ID");
            final Long customerId = params.getLong("CUSTOMER_ID");
            final String udid = params.getString("UDID");
            final String bootstrapToken = hashPlist.optString("BootstrapToken", (String)null);
            final Long reqTime = (Long)params.get("reqTime");
            Logger.getLogger("MDMLogger").log(Level.INFO, "SetBootstrapTokenReqProcessor.processQueuedCommand():- resourceId={0}, customerID={1}, udid={2}, reqTime={3}, token is null={4}, token is empty={5}", new Object[] { resourceId, customerId, udid, reqTime, bootstrapToken == null, bootstrapToken != null && bootstrapToken.isEmpty() });
            final Long erIdForResId = ManagedDeviceHandler.getInstance().getEnrollmentReqIdForResourceId(resourceId);
            final Long reqErId = Long.valueOf((String)hashPlist.get("ENROLLMENT_REQUEST_ID"));
            if (erIdForResId != null && erIdForResId.equals(reqErId)) {
                final Map bootstrapData = new HashMap();
                bootstrapData.put("TOKEN", (bootstrapToken == null || bootstrapToken.isEmpty()) ? null : bootstrapToken);
                bootstrapData.put("TOKEN_MODIFIED_AT", reqTime);
                MacBootstrapTokenHandler.getInstance().addOrUpdateMacBootstrapToken(resourceId, customerId, udid, bootstrapData);
            }
            else {
                MacBootstrapTokenHandler.getInstance().addOrUpdateMacBootstrapTokenTemp(udid, bootstrapToken, customerId, reqTime);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception while setBootstrapToken request queue", e);
        }
        return null;
    }
}
