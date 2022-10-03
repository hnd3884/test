package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacRestrictionPolicy implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        final DO2RestrictionsPolicyPayload restrictions = new DO2RestrictionsPolicyPayload();
        final IOSPayload[] restrictionPyalod = restrictions.createPayload(dataObject);
        settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, restrictionPyalod);
        final DO2MacGateKeeperSystemPolicyControlPayload gateKeeperSPControlPayload = new DO2MacGateKeeperSystemPolicyControlPayload();
        settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, gateKeeperSPControlPayload.createPayload(dataObject));
        final DO2MacGateKeeperSystemPolicyManagedPayload gateKeeperSPManagedPayload = new DO2MacGateKeeperSystemPolicyManagedPayload();
        settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, gateKeeperSPManagedPayload.createPayload(dataObject));
        return settingsPayload;
    }
}
