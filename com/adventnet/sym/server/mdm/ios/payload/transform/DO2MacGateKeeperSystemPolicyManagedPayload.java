package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.MacGateKeeperSystemPolicyManagedPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacGateKeeperSystemPolicyManagedPayload implements DO2Payload
{
    String tableName;
    
    public DO2MacGateKeeperSystemPolicyManagedPayload() {
        this.tableName = "RestrictionsPolicy";
    }
    
    public DO2MacGateKeeperSystemPolicyManagedPayload(final String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final MacGateKeeperSystemPolicyManagedPayload[] payloadArray = { null };
            final Row gateKeeperRow = dataObject.getRow(this.tableName);
            final boolean overrideSettings = (boolean)gateKeeperRow.get("MAC_ALLOW_USER_OVERIDE_GATEKEEPER");
            final int gateKeeperSettings = (int)gateKeeperRow.get("MAC_GATEKEEPER_SETTINGS");
            if (overrideSettings && (gateKeeperSettings == 1 || gateKeeperSettings == 2)) {
                final MacGateKeeperSystemPolicyManagedPayload payload = new MacGateKeeperSystemPolicyManagedPayload(1, "MDM", "com.mdm.mac.gatekeeper.systempolicycontrol", "GateKeeper Management Payload");
                payload.setDisabledOveride(true);
                payloadArray[0] = payload;
                return payloadArray;
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger("MDMConfig").log(Level.SEVERE, "Error in DO2MacGateKeeperSystemPolicyManagedPayload", (Throwable)ex);
        }
        return null;
    }
}
