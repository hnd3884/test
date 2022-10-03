package com.me.mdm.server.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.encryption.windows.bitlocker.BitlockerHandler;
import org.json.JSONObject;

public class WindowsBitlockerConfigHandler extends DefaultConfigHandler
{
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            final Long bitlockerId = serverJSON.getLong("BITLOCKER_POLICY_ID");
            final boolean isValidBitlockerId = new BitlockerHandler().isValidBitlockerID(bitlockerId);
            if (!isValidBitlockerId) {
                throw new APIHTTPException("COM0024", new Object[] { "bitlocker_policy_id" });
            }
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while validating bitlocker server json ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
