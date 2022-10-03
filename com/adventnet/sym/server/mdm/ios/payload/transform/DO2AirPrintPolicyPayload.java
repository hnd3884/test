package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.AirPrintPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AirPrintPolicyPayload implements DO2Payload
{
    public static Logger logger;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        Iterator iterator = null;
        final AirPrintPayload[] airPrintPayload = { null };
        try {
            if (!dataObject.isEmpty()) {
                iterator = dataObject.getRows("AirPrintPolicy");
                (airPrintPayload[0] = new AirPrintPayload(1, "MDM", "com.mdm.mobiledevice.airprint", "AirPrint Profile Configuration")).initializeDict();
                while (iterator.hasNext()) {
                    final Row airPrintRow = iterator.next();
                    final String hostAddress = (String)airPrintRow.get("HOST_ADDRESS");
                    final String resourcePath = (String)airPrintRow.get("RESOURCE_PATH");
                    final Integer port = (Integer)airPrintRow.get("PORT");
                    final boolean forceTLS = (boolean)airPrintRow.get("FORCETLS");
                    if (!MDMStringUtils.isEmpty(hostAddress)) {
                        airPrintPayload[0].setHostAddress(hostAddress);
                    }
                    if (!MDMStringUtils.isEmpty(resourcePath)) {
                        airPrintPayload[0].setResourcePath(resourcePath);
                    }
                    if (port != null) {
                        airPrintPayload[0].setPort(port);
                    }
                    airPrintPayload[0].setForceTLS(forceTLS);
                }
                airPrintPayload[0].setAirPrintPayload();
            }
        }
        catch (final Exception e) {
            DO2AirPrintPolicyPayload.logger.log(Level.SEVERE, "Error occurred while creating airprint", e);
        }
        return airPrintPayload;
    }
    
    static {
        DO2AirPrintPolicyPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
