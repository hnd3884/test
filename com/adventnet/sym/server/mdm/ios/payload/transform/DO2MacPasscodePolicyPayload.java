package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacPasscodePolicyPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        final DO2PasscodePolicyPayload policy = new DO2PasscodePolicyPayload();
        final IOSPayload[] passcodePayload = policy.createPayload(dataObject);
        settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, passcodePayload);
        return settingsPayload;
    }
}
