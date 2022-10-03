package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidCertificatePayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidCertificatePayload implements DO2AndroidPayload
{
    public Logger logger;
    public static final String CERTIFICATE_TYPE_PKCS12 = "PKCS12";
    public static final String CERTIFICATE_TYPE_PKCS1 = "PKCS1";
    
    public DO2AndroidCertificatePayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidCertificatePayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new AndroidCertificatePayload("1.0", "com.mdm.mobiledevice.certificate", "Certificate Policy");
                final Long certificateId = (Long)row.get("CERTIFICATE_ID");
                final String cerPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString());
                final String certificate = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
                final String cerFilename = (String)row.get("CERTIFICATE_FILE_NAME");
                payload.setCertificateContent(certificate);
                if (cerFilename != null) {
                    payload.setCertificateFileName(cerFilename);
                    payload.setCertificateType(this.getCertificateType(cerFilename));
                }
                if (cerPassword != null) {
                    payload.setPassword(cerPassword);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "DO2AndroidCertificatePolicyPayload:Exception while creating certificate payload ", ex);
        }
        return payload;
    }
    
    private String getCertificateType(final String cerFilename) {
        if (cerFilename.toLowerCase().endsWith(".p12") || cerFilename.toLowerCase().endsWith(".pfx")) {
            return "PKCS12";
        }
        return "PKCS1";
    }
}
