package com.adventnet.sym.server.mdm.android.payload.transform;

import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public interface DO2AndroidPayload
{
    AndroidPayload createPayload(final DataObject p0);
}
