package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.CertificatePayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2CertificatePolicyPayload implements DO2Payload
{
    public Logger logger;
    
    public DO2CertificatePolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final CertificatePayload[] payloadArray = { null };
        CertificatePayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new CertificatePayload(1, "MDM", "com.mdm.mobiledevice.certificate", "iOS Certificate Policy");
                final String cerPassword = (String)row.get("CERTIFICATE_PASSWORD");
                final String cerFilename = (String)row.get("CERTIFICATE_FILE_NAME");
                final Long certificateId = (Long)row.get("CERTIFICATE_ID");
                final Long customerID = (Long)row.get("CUSTOMER_ID");
                final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
                final String certPath = cerFolder + File.separator + cerFilename;
                if (certPath != null) {
                    final String certificate = PayloadSecretFieldsHandler.getInstance().constructIOSPayloadCertificate(certificateId.toString());
                    payload.setCertificatePayloadContent(certificate);
                }
                if (cerFilename != null) {
                    payload.setPayloadCertificateFileName(cerFilename);
                }
                if (cerFilename != null && (cerFilename.toLowerCase().endsWith(".p12") || cerFilename.toLowerCase().endsWith(".pfx"))) {
                    if (!cerPassword.equals("")) {
                        payload.setPassword(PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString()));
                    }
                    payload.setPayloadType("com.apple.security.pkcs12");
                }
                else if (cerFilename.toLowerCase().endsWith(".pem")) {
                    payload.setPayloadType("com.apple.security.pem");
                }
                else if (cerFilename.toLowerCase().endsWith(".der")) {
                    payload.setPayloadType("com.apple.security.pkcs1");
                }
                else {
                    payload.setPayloadType("com.apple.security.root");
                }
                final Row certificatePayloadRow = dataObject.getRow("CertificatePolicy", row);
                this.addCertificatePolicySetting(certificatePayloadRow, payload);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "DO2CertificatePolicyPayload:Exception while creating certificate payload ", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private void addCertificatePolicySetting(final Row certificatePayloadRow, final CertificatePayload payload) {
        if (certificatePayloadRow != null) {
            final boolean allowAppAccess = (boolean)certificatePayloadRow.get("ALLOW_OTHER_APP_ACCESS");
            final boolean isKeyExtractable = (boolean)certificatePayloadRow.get("KEY_IS_EXTRACTABLE");
            payload.setKeyExtractable(isKeyExtractable);
            payload.setOtherAppAccess(allowAppAccess);
        }
    }
}
