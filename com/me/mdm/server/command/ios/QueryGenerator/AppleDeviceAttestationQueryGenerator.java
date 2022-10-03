package com.me.mdm.server.command.ios.QueryGenerator;

import com.me.mdm.server.inv.ios.DeviceAttestation.DeviceAttestationModel;
import java.util.logging.Level;
import com.dd.plist.NSData;
import org.apache.commons.codec.binary.Base64;
import com.me.mdm.server.inv.ios.DeviceAttestation.DeviceAttestationHandler;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.NSSet;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class AppleDeviceAttestationQueryGenerator implements CommandQueryCreator
{
    private final Logger logger;
    
    public AppleDeviceAttestationQueryGenerator() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        String strQuery = null;
        try {
            final IOSCommandPayload createDeviceAttestationCommand = new IOSCommandPayload();
            createDeviceAttestationCommand.setRequestType("DeviceInformation");
            final NSSet queriesData = new NSSet();
            queriesData.addObject((NSObject)new NSString("DevicePropertiesAttestation"));
            createDeviceAttestationCommand.getCommandDict().put("Queries", (NSObject)queriesData);
            final DeviceAttestationModel deviceAttestationProperties = new DeviceAttestationHandler().getDeviceAttestationPropertiesFromDB(resourceID);
            final String nonce = deviceAttestationProperties.getNonce();
            final NSData nonceData = new NSData(Base64.encodeBase64String(nonce.getBytes()));
            createDeviceAttestationCommand.getCommandDict().put("DeviceAttestationNonce", (NSObject)nonceData);
            createDeviceAttestationCommand.setCommandUUID(deviceCommand.commandUUID, false);
            strQuery = createDeviceAttestationCommand.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Apple Device Attestation query generation.. ", ex);
        }
        return strQuery;
    }
}
