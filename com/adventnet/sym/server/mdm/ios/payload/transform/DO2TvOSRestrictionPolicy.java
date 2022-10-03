package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2TvOSRestrictionPolicy implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        final DO2RestrictionsPolicyPayload restrictions = new DO2RestrictionsPolicyPayload();
        final IOSPayload[] restrictionPyalod = restrictions.createPayload(dataObject);
        settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, restrictionPyalod);
        return settingsPayload;
    }
}
