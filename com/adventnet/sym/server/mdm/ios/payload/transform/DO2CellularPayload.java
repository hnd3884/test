package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.CellularPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2CellularPayload implements DO2Payload
{
    private static final Logger LOGGER;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final CellularPayload[] cellularPayloadArray = { null };
        CellularPayload cellularPayload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("ApnPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String accessPointName = (String)payloadRow.get("ACCESS_POINT_NAME");
                    final String userName = (String)payloadRow.get("ACCESS_POINT_USER_NAME");
                    String password = "";
                    if (payloadRow.get("ACCESS_POINT_PASSOWRD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("ACCESS_POINT_PASSOWRD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    final String proxyHostName = (String)payloadRow.get("PROXY_SERVER");
                    final Integer proxyPort = (Integer)payloadRow.get("PROXY_SERVER_PORT");
                    cellularPayload = new CellularPayload(1, "MDM", "com.mdm.mobiledevice.cellular", "Cellular Profile Configuration");
                    cellularPayload.initializeAPNsDict();
                    if (accessPointName != null && !accessPointName.equalsIgnoreCase("--")) {
                        cellularPayload.setAccessPointName(accessPointName);
                    }
                    if (userName != null && !userName.equalsIgnoreCase("--")) {
                        cellularPayload.setAccessPointUserName(userName);
                    }
                    if (password != null && !password.equalsIgnoreCase("--")) {
                        cellularPayload.setAccessPointPassword(password);
                    }
                    if (proxyHostName != null && !proxyHostName.equalsIgnoreCase("--")) {
                        cellularPayload.setAccessPointProxyHostName(proxyHostName);
                    }
                    if (proxyPort != null && proxyPort != 0) {
                        cellularPayload.setAccessPointProxyPort(proxyPort);
                    }
                    cellularPayload.setCellularDict();
                }
            }
        }
        catch (final Exception e) {
            DO2CellularPayload.LOGGER.log(Level.SEVERE, "Exception in Cellular Payload : ", e);
        }
        cellularPayloadArray[0] = cellularPayload;
        return cellularPayloadArray;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
