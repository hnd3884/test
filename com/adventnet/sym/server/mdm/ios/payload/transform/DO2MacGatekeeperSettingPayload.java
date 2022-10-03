package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacGatekeeperSettingPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] iosPayloads = null;
        final DO2MacGateKeeperSystemPolicyControlPayload gateKeeperSPControlPayload = new DO2MacGateKeeperSystemPolicyControlPayload("MacGatekeeperPolicy");
        iosPayloads = DO2PayloadHandler.mergePayload(iosPayloads, gateKeeperSPControlPayload.createPayload(dataObject));
        final DO2MacGateKeeperSystemPolicyManagedPayload gateKeeperSPManagedPayload = new DO2MacGateKeeperSystemPolicyManagedPayload("MacGatekeeperPolicy");
        iosPayloads = DO2PayloadHandler.mergePayload(iosPayloads, gateKeeperSPManagedPayload.createPayload(dataObject));
        return iosPayloads;
    }
}
