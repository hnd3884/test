package com.me.mdm.server.windows.profile.payload.content.security;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import org.bouncycastle.util.encoders.Base64;
import org.apache.commons.lang3.ArrayUtils;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RecoverCertificate
{
    public Logger logger;
    Long certificateID;
    Long customerID;
    byte[] recoveryVersionHeader;
    
    public RecoverCertificate(final Long certID, final Long customerID) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.recoveryVersionHeader = new byte[] { 1, 0, 1, 0 };
        this.certificateID = certID;
        this.customerID = customerID;
    }
    
    public String getRecoveryCertificateBlob() throws Exception {
        final String path = this.getCACertFilePath(this.certificateID);
        final byte[] certDetails = this.setCertificateDetails(path);
        final List filePathList = new ArrayList();
        filePathList.add(path);
        int numCerts = 1;
        try {
            final List list = CertificateUtils.splitMultipleCertificatesInEachFileToCertificateList(filePathList);
            numCerts = list.size();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "unable to get num certs defaulting to 1 cert", e);
        }
        final int certLen = certDetails.length;
        final int zero = 0;
        final int offsetFromPublicKey = 28;
        final int sourceKeyTag = 2;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(40);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(this.recoveryVersionHeader);
        byteBuffer.putInt(numCerts);
        byteBuffer.putInt(certLen + 32);
        byteBuffer.putInt(certLen + 28);
        byteBuffer.putInt(zero);
        byteBuffer.putInt(sourceKeyTag);
        byteBuffer.putInt(certLen);
        byteBuffer.putInt(offsetFromPublicKey);
        byteBuffer.putInt(zero);
        byteBuffer.putInt(zero);
        final byte[] header = byteBuffer.array();
        final byte[] result = ArrayUtils.addAll(header, certDetails);
        return new String(Base64.encode(result));
    }
    
    private byte[] setCertificateDetails(final String certFilePath) throws Exception {
        if (certFilePath != null && ApiFactoryProvider.getFileAccessAPI().isFileExists(certFilePath)) {
            return ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certFilePath);
        }
        return null;
    }
    
    private String getCACertFilePath(final Long caCertID) throws DataAccessException, Exception {
        String filePath = null;
        final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(this.customerID, caCertID);
        if (certDO != null) {
            final String destFolder = MDMUtil.getCredentialCertificateFolder(this.customerID);
            final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
            final String certFileName = (String)certRow.get("CERTIFICATE_FILE_NAME");
            filePath = destFolder + File.separator + certFileName;
        }
        return filePath;
    }
}
