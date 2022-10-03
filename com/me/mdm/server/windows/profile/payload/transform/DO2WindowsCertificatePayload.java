package com.me.mdm.server.windows.profile.payload.transform;

import java.io.InputStream;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.bouncycastle.util.encoders.Base64;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.windows.profile.payload.WindowsClientCertificatePayload;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WinMobileCertificatePayload;
import com.me.mdm.server.windows.profile.payload.WindowsCertificatePayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsCertificatePayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        WindowsCertificatePayload payload = null;
        WindowsCertificatePayload winPhoneCertificatePayload = null;
        WinMobileCertificatePayload winMobileCertificatePayload = null;
        WindowsClientCertificatePayload clientCertificatePayload = null;
        try {
            final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
            payload = new WindowsCertificatePayload();
            payload.getAddPayloadCommand().addRequestItem(payload.createTargetItemTagElement("%certificate_add_payload_xml%"));
            payload.getNonAtomicDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%certificate_payload_xml_nonAtomicDelete%"));
            winPhoneCertificatePayload = new WindowsCertificatePayload();
            winMobileCertificatePayload = new WinMobileCertificatePayload();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long certificateId = (Long)row.get("CERTIFICATE_ID");
                final String certificateFile = (String)row.get("CERTIFICATE_FILE_NAME");
                final String thumbprint = (String)row.get("CERTIFICATE_THUMBPRINT");
                final Long customerID = (Long)row.get("CUSTOMER_ID");
                final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
                final String certPath = cerFolder + File.separator + certificateFile;
                final Map<String, String> certificateDetails = this.getCertificateContentsAndType(certPath);
                final String contents = certificateDetails.get("Content");
                final String certificateContent = PayloadSecretFieldsHandler.getInstance().constructSSLCertificate(certificateId.toString());
                if (!certificateDetails.get("Type").equals("Client")) {
                    if (certificateDetails.get("Type").equals("Root")) {
                        winPhoneCertificatePayload.setDeleteForRootCertificate(thumbprint);
                        winMobileCertificatePayload.setDeleteForRootCertificate(thumbprint);
                        winPhoneCertificatePayload.setEncodedRootCertificateContent(certificateContent, thumbprint);
                        winMobileCertificatePayload.setEncodedRootCertificateContent(certificateContent, thumbprint);
                    }
                    else {
                        winPhoneCertificatePayload.setDeleteForCACertificate(thumbprint);
                        winMobileCertificatePayload.setDeleteForCACertificate(thumbprint);
                        winPhoneCertificatePayload.setEncodedCACertificateContent(certificateContent, thumbprint);
                        winMobileCertificatePayload.setEncodedCACertificateContent(certificateContent, thumbprint);
                    }
                    this.packOsSpecificPayloadToXML(dataObject, winMobileCertificatePayload, "install", "WindowsPhone81Certificate");
                    this.packOsSpecificPayloadToXML(dataObject, winPhoneCertificatePayload, "install", "Windows10MobileCertificate");
                }
                else {
                    if (!certificateFile.contains("pfx")) {
                        continue;
                    }
                    clientCertificatePayload = new WindowsClientCertificatePayload();
                    final String password = (String)row.get("CERTIFICATE_PASSWORD");
                    String certPasswordPlaceholder = "";
                    if (!password.equals("")) {
                        certPasswordPlaceholder = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString());
                    }
                    clientCertificatePayload.setClientCertificateNonAtomicDelete(thumbprint);
                    clientCertificatePayload.setKeyLocationPayload(thumbprint);
                    clientCertificatePayload.setPFXCertPasswordPayload(thumbprint, certPasswordPlaceholder);
                    clientCertificatePayload.setPFXKeyExportablePayload(thumbprint);
                    clientCertificatePayload.setPFXCertBlobPayload(thumbprint, certificateContent);
                    this.packOsSpecificPayloadToXML(dataObject, clientCertificatePayload, "install", "Windows10MobileCertificate");
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Certificate payload ", exp);
        }
        return payload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        WindowsCertificatePayload payload = null;
        WindowsCertificatePayload winPhoneCertificatePayload = null;
        WinMobileCertificatePayload winMobileCertificatePayload = null;
        WindowsClientCertificatePayload clientCertificatePayload = null;
        try {
            final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long customerID = (Long)row.get("CUSTOMER_ID");
                final String certificateFile = (String)row.get("CERTIFICATE_FILE_NAME");
                final String thumbprint = (String)row.get("CERTIFICATE_THUMBPRINT");
                final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
                final String certPath = cerFolder + File.separator + certificateFile;
                final String type = this.getCertificateContentsAndType(certPath).get("Type");
                payload = new WindowsCertificatePayload();
                payload.getDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%certificate_payload_xml%"));
                winPhoneCertificatePayload = new WindowsCertificatePayload();
                winMobileCertificatePayload = new WinMobileCertificatePayload();
                if (!type.equals("Client")) {
                    if (type.equals("Root")) {
                        winPhoneCertificatePayload.setRemoveProfileRootPayload(thumbprint);
                        winMobileCertificatePayload.setRemoveProfileRootPayload(thumbprint);
                    }
                    else {
                        winPhoneCertificatePayload.setRemoveProfileCAPayload(thumbprint);
                        winMobileCertificatePayload.setRemoveProfileCAPayload(thumbprint);
                    }
                    this.packOsSpecificPayloadToXML(dataObject, winMobileCertificatePayload, "remove", "WindowsPhone81Certificate");
                    this.packOsSpecificPayloadToXML(dataObject, winPhoneCertificatePayload, "remove", "Windows10MobileCertificate");
                }
                else {
                    if (!certificateFile.contains("pfx")) {
                        continue;
                    }
                    clientCertificatePayload = new WindowsClientCertificatePayload();
                    clientCertificatePayload.setClientCertificateDelete(thumbprint);
                    this.packOsSpecificPayloadToXML(dataObject, clientCertificatePayload, "remove", "Windows10MobileCertificate");
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Certificate remove payload ", exp);
        }
        return payload;
    }
    
    protected Map<String, String> getCertificateContentsAndType(final String certificatePath) {
        final Map<String, String> certificateMap = new HashMap<String, String>();
        try {
            final byte[] certificateContentBytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certificatePath);
            if (certificatePath.contains(".pfx")) {
                certificateMap.put("Type", "Client");
                certificateMap.put("Content", new String(Base64.encode(certificateContentBytes)).replaceAll("\\s+", ""));
            }
            else {
                final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                final InputStream in = new ByteArrayInputStream(certificateContentBytes);
                final X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
                if (cert.getKeyUsage() != null && cert.getKeyUsage()[5]) {
                    if (CertificateUtils.isCertificateSelfSigned(cert)) {
                        certificateMap.put("Type", "Root");
                    }
                    else {
                        certificateMap.put("Type", "CA");
                    }
                }
                else {
                    certificateMap.put("Type", "CA");
                }
                certificateMap.put("Content", new String(Base64.encode(cert.getEncoded())).replaceAll("\\s+", ""));
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Certificate Content and Type : ", exp);
        }
        return certificateMap;
    }
}
