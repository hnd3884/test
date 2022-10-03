package com.me.mdm.server.profiles.mac.recoverylock;

import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import org.json.JSONObject;

public class RecoveryLockPostSecurityInfoResponse extends RecoveryLockResponse
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        params = super.processSeqQueuedCommand(params);
        final Long resourceId = params.getLong("resourceId");
        final String udid = params.getString("strUDID");
        final String status = params.getString("strStatus");
        RecoveryLockPostSecurityInfoResponse.LOGGER.log(Level.INFO, "Post Security info response status: {0}: {1}", new Object[] { udid, status });
        try {
            if (status.equalsIgnoreCase("Acknowledged")) {
                this.updateSecurityInfo(resourceId, params);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                RecoveryLockPostSecurityInfoResponse.LOGGER.log(Level.INFO, "Device has sent Not Now for Post Security Info command. Resource: {0}", new Object[] { params.getLong("resourceId") });
                params.put("action", 5);
            }
            else {
                if (status.equalsIgnoreCase("Error")) {
                    this.handleError(params);
                }
                params.put("action", 2);
            }
        }
        catch (final Exception e) {
            RecoveryLockPostSecurityInfoResponse.LOGGER.log(Level.SEVERE, e, () -> "Exception while updating post recovery lock security info for resource: " + n);
            params.put("action", 2);
        }
        SeqCmdRepository.getInstance().processSeqCommand(params);
        return params;
    }
}
