package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.GlobalHttpProxyPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2GlobalHttpProxyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2GlobalHttpProxyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        GlobalHttpProxyPayload globalProxyPayload = null;
        final GlobalHttpProxyPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("GlobalHttpProxyPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                globalProxyPayload = new GlobalHttpProxyPayload(1, "MDM", "com.mdm.mobiledevice.globalhttpproxy", "GlobalHttpProxy Policy");
                final Integer proxyType = (Integer)row.get("PROXY_TYPE");
                final String proxyServerName = (String)row.get("PROXY_SERVER");
                final Integer proxyServerPort = (Integer)row.get("PROXY_SERVER_PORT");
                final String proxyUserName = (String)row.get("PROXY_USER_NAME");
                String proxyPassword = "";
                if (row.get("PROXY_PASSWORD_ID") != null) {
                    final Long proxyPasswordID = (Long)row.get("PROXY_PASSWORD_ID");
                    proxyPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(proxyPasswordID.toString());
                }
                final String proxyPacUrl = (String)row.get("PROXY_PAC_URL");
                globalProxyPayload = new GlobalHttpProxyPayload(1, "MDM", "com.mdm.mobiledevice.globalhttpproxy", "GlobalHttpProxy Policy");
                if (proxyType == 1) {
                    globalProxyPayload.setManualProxy(proxyServerName, proxyServerPort, proxyUserName, proxyPassword);
                }
                else {
                    if (proxyType != 2) {
                        continue;
                    }
                    globalProxyPayload.setAutomaticProxy(proxyPacUrl);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in publishing the globalhttp payload", ex);
        }
        payloadArray[0] = globalProxyPayload;
        return payloadArray;
    }
}
