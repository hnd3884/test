package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AppsPayload implements DO2Payload
{
    public Logger logger;
    
    public DO2AppsPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        DO2Payload payloadHandler = null;
        try {
            final Row mdPackageToAppRow = dataObject.getFirstRow("MdPackageToAppData");
            final Row mdPackageToGroup = dataObject.getFirstRow("MdPackageToAppGroup");
            final Integer supportedDevice = (Integer)mdPackageToAppRow.get("SUPPORTED_DEVICES");
            final Integer packageType = (Integer)mdPackageToGroup.get("PACKAGE_TYPE");
            if (supportedDevice == 16 && packageType == 2) {
                payloadHandler = new DO2MacOSAppPayload();
            }
            else {
                payloadHandler = new DO2IOSAppPayload();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in creating payload for App Installation", ex);
        }
        return payloadHandler.createPayload(dataObject);
    }
}
