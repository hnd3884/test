package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.MacGateKeeperSystemPolicyControlPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacGateKeeperSystemPolicyControlPayload implements DO2Payload
{
    String tableName;
    
    public DO2MacGateKeeperSystemPolicyControlPayload() {
        this.tableName = "RestrictionsPolicy";
    }
    
    public DO2MacGateKeeperSystemPolicyControlPayload(final String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final MacGateKeeperSystemPolicyControlPayload payload = new MacGateKeeperSystemPolicyControlPayload(1, "MDM", "com.mdm.mac.gatekeeper.systempolicycontrol", "GateKeeper Control Payload");
            final MacGateKeeperSystemPolicyControlPayload[] payloadArray = { null };
            final Row gateKeeperRow = dataObject.getRow(this.tableName);
            final int gateKeeperSettings = (int)gateKeeperRow.get("MAC_GATEKEEPER_SETTINGS");
            if (gateKeeperSettings == 1) {
                payload.setEnabledAssesment(true);
                payload.setAllowIdentifiedDevelopers(false);
            }
            else if (gateKeeperSettings == 2) {
                payload.setEnabledAssesment(true);
                payload.setAllowIdentifiedDevelopers(true);
            }
            else if (gateKeeperSettings == 3) {
                payload.setEnabledAssesment(false);
            }
            else if (gateKeeperSettings == 4) {
                return null;
            }
            payloadArray[0] = payload;
            return payloadArray;
        }
        catch (final DataAccessException ex) {
            Logger.getLogger("MDMConfig").log(Level.SEVERE, "Error in DO2MacGateKeeperSystemPolicyControlPayload", (Throwable)ex);
            return null;
        }
    }
}
