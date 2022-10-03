package com.me.mdm.server.inv.ios.DeviceAttestation;

import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AppleDeviceAttestationResponseHandler implements CommandResponseProcessor.QueuedResponseProcessor
{
    Logger logger;
    
    public AppleDeviceAttestationResponseHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) throws Exception {
        final Long startTime = System.currentTimeMillis();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("QueryResponses", params.optString("strData"));
            if (!nsDict.containsKey("DevicePropertiesAttestation")) {
                this.logger.log(Level.SEVERE, "DevicePropertiesAttestation Property is not found in Device_Information command for Device Attestation for resourceID - {0}", resourceID);
                return null;
            }
            new DeviceAttestationHandler().deviceAttestationResponseHandler(nsDict, resourceID);
            this.logger.log(Level.INFO, "Device_Information command processed successfully for Device Attestation for resourceID - {0}", resourceID);
            this.logger.log(Level.INFO, "Time taken for processing Command {0} is {2}", new Object[] { commandUUID, System.currentTimeMillis() - startTime });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while processing Device_Information command for Device Attestation for resourceID - {0} - {1}", new Object[] { resourceID, ex });
        }
        return null;
    }
}
