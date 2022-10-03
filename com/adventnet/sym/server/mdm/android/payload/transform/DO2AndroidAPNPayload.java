package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidAPNPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidAPNPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidAPNPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidAPNPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("ApnPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new AndroidAPNPayload("1.0", "com.mdm.mobiledevice.APN", "APN");
                payload.setAPN((String)row.get("ACCESS_POINT_NAME"));
                String password = "";
                if (row.get("ACCESS_POINT_PASSOWRD_ID") != null) {
                    final Long passwordID = (Long)row.get("ACCESS_POINT_PASSOWRD_ID");
                    password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                }
                payload.setUserPassword(password);
                payload.setUserName((String)row.get("ACCESS_POINT_USER_NAME"));
                payload.setProxyServer((String)row.get("PROXY_SERVER"));
                payload.setPort(((Integer)row.get("PROXY_SERVER_PORT")).toString());
            }
            this.updateAndroidAPNExtnConfig(payload, dataObject);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "DO2AndroidAPNPayload:Exception while creating APN payload ", ex);
        }
        return payload;
    }
    
    private AndroidAPNPayload updateAndroidAPNExtnConfig(final AndroidAPNPayload payload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("AndroidApnPolicyExtn");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            payload.setAuthType((Integer)row.get("AUTH_TYPE"));
            payload.setIsPreferred((Boolean)row.get("IS_PREFERED_APN"));
            payload.setMCC((String)row.get("MCC"));
            payload.setMNC((String)row.get("MNC"));
            payload.setMMSC((String)row.get("MMSC"));
            payload.setMmsPort((String)row.get("MMS_PORT"));
            payload.setMmsProxy((String)row.get("MMS_PROXY"));
            payload.setAPNName((String)row.get("NAME"));
            payload.setProtocol((Integer)row.get("PROTOCOL"));
            payload.setRoamingProtocol((Integer)row.get("ROAMING_PROTOCOL"));
            payload.setServer((String)row.get("SERVER"));
            payload.setType((String)row.get("TYPE"));
            payload.setMVNO((int)row.get("MVNO"));
        }
        return payload;
    }
}
