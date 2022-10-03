package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.ADCertPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2ADCertPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2ADCertPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        ADCertPayload payload = null;
        final IOSPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("ADCertConfiguration");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String certServerAddress = (String)row.get("CERT_SERVER_ADDRESS");
                final String description = (String)row.get("DESCRIPTION");
                final String certAuthority = (String)row.get("CERT_AUTHORITY");
                final String certTemplate = (String)row.get("CERT_TEMPLATE_NAME");
                final Integer certExpNotifyThread = (Integer)row.get("CERT_EXP_NOTIFY_THREAD");
                Integer rsaKeySize = (Integer)row.get("RSA_KEY_SIZE");
                if (rsaKeySize == 0) {
                    rsaKeySize = 1024;
                }
                else if (rsaKeySize == 1) {
                    rsaKeySize = 2048;
                }
                else {
                    rsaKeySize = 4096;
                }
                final boolean autoRenewEnabled = (boolean)row.get("AUTO_RENEW_ENABLED");
                final boolean allowAllApps = (boolean)row.get("ALLOW_ALL_APPS_TO_ACCESS");
                final boolean isKeyExtractable = (boolean)row.get("IS_KEY_EXTRACTABLE");
                payload = new ADCertPayload(1, "MDM", "com.mdm.mobiledevice.certificate", "AD Certificate Payload");
                payload.setCertServer(certServerAddress);
                payload.setCertificateAuthority(certAuthority);
                payload.setCertTemplate(certTemplate);
                payload.setCertificateRenewalTimeInterval(certExpNotifyThread);
                payload.setKeysize(rsaKeySize);
                payload.setEnableAutoRenewal(autoRenewEnabled);
                payload.setAllowAllAppsAccess(allowAllApps);
                payload.setDescription(description);
                payload.setKeyIsExtractable(isKeyExtractable);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while ADCS createPayload", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
