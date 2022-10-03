package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public interface DO2ChromePayload
{
    public static final Logger LOGGER = Logger.getLogger("MDMProfileConfigLogger");
    
    ChromePayload createPayload(final DataObject p0);
}
