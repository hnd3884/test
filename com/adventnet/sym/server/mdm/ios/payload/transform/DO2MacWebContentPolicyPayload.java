package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.WebConentFilterPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2MacWebContentPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2MacWebContentPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        try {
            final Do2WebContentPolicyPayload webContentPolicyPayload = new Do2WebContentPolicyPayload();
            final IOSPayload[] wcfPaylod = webContentPolicyPayload.createPayload(dataObject);
            final WebConentFilterPayload payload = (WebConentFilterPayload)wcfPaylod[0];
            settingsPayload = DO2PayloadHandler.mergePayload(settingsPayload, wcfPaylod);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in macOS webcontent filter payload creation", e);
        }
        return settingsPayload;
    }
}
