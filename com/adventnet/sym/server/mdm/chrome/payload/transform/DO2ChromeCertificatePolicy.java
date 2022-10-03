package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONException;
import com.dd.plist.Base64;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeCertificatePayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeCertificatePolicy implements DO2ChromePayload
{
    @Override
    public ChromeCertificatePayload createPayload(final DataObject dataObject) {
        ChromeCertificatePayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeCertificatePayload("1.0", "Certificate", "Certificate");
                final String certGUID = String.valueOf(payload.getPayloadJSON().get("PayloadUUID"));
                payload.setGUID(certGUID);
                final String type = this.getCertType(row);
                payload.setType(type);
                final Long certificateId = (Long)row.get("CERTIFICATE_ID");
                final String certificate = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
                payload.setX509(certificate);
            }
        }
        catch (final Exception ex) {
            DO2ChromeCertificatePolicy.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
    
    private String getCertificateContent(final String fileName) throws JSONException {
        try {
            final byte[] b = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(fileName);
            return Base64.encodeBytes(b);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            return "";
        }
    }
    
    private String getCertType(final Row row) {
        String issuer = (String)row.get("CERTIFICATE_ISSUER_DN");
        String subject = (String)row.get("CERTIFICATE_SUBJECT_DN");
        issuer = issuer.replaceAll("\\s", "");
        subject = subject.replaceAll("\\s", "");
        if (issuer.equals(subject)) {
            return "Server";
        }
        return "Authority";
    }
}
