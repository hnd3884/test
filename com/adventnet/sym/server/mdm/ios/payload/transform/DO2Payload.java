package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public interface DO2Payload
{
    IOSPayload[] createPayload(final DataObject p0);
}
