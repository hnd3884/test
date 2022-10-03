package com.adventnet.sym.server.mdm.ios.payload.transform.remove;

import com.adventnet.sym.server.mdm.ios.payload.MACFileVaultPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2Payload;

public class DO2RemoveMacFileVaultPolicy implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final MACFileVaultPayload[] payloadArray = { null };
        final MACFileVaultPayload payload = new MACFileVaultPayload(1, "MDM", "com.mdm.mac.filevault", "Remove FileVault Configuration");
        payload.setEnable(false);
        payloadArray[0] = payload;
        return payloadArray;
    }
}
