package com.me.mdm.server.adep;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import com.me.mdm.server.certificate.MDMCertificateGenerator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;

public class AppleDEPCertificateHandler
{
    private static AppleDEPCertificateHandler certificateHandler;
    public static Logger logger;
    public static final String DEP_SERVER_PUBLIC_KEY_FINAL_NAME = "depserver.pem";
    public static final String DEP_SERVER_PRIVATE_KEY_FINAL_NAME = "depserver.key";
    public static final String DEP_SERVER_TOKEN_FINAL_NAME = "deptoken.p7m";
    
    public static AppleDEPCertificateHandler getInstance() {
        if (AppleDEPCertificateHandler.certificateHandler == null) {
            AppleDEPCertificateHandler.certificateHandler = new AppleDEPCertificateHandler();
        }
        return AppleDEPCertificateHandler.certificateHandler;
    }
    
    public String getDEPCertificateFolder(final Long customerId) {
        final String serverCertificateFilePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("DEP") + File.separator + customerId;
        return serverCertificateFilePath;
    }
    
    @Deprecated
    public String getDEPServerPublicKeyPath(final Long customerId) {
        return this.getDEPCertificateFolder(customerId) + File.separator + "depserver.pem";
    }
    
    @Deprecated
    public String getDEPServerPrivateKeyPath(final Long customerId) {
        return this.getDEPCertificateFolder(customerId) + File.separator + "depserver.key";
    }
    
    public String getDEPCertificateFolder(final Long customerId, final Long depTokenId) {
        final String certificatePath = this.getDEPCertificateFolder(customerId) + File.separator + depTokenId;
        return certificatePath;
    }
    
    public String getDEPServerPublicKeyPath(final Long customerId, final Long depTokenID) {
        return this.getDEPCertificateFolder(customerId, depTokenID) + File.separator + "depserver.pem";
    }
    
    public String getDEPServerTokenPath(final Long customerId, final Long depTokenID) {
        return this.getDEPCertificateFolder(customerId, depTokenID) + File.separator + "deptoken.p7m";
    }
    
    public String getDEPServerPrivateKeyPath(final Long customerId, final Long depTokenID) {
        return this.getDEPCertificateFolder(customerId, depTokenID) + File.separator + "depserver.key";
    }
    
    @Deprecated
    public void generateDEPCertificate(final Long customerId) throws Exception {
        final String serverCrt = this.getDEPServerPublicKeyPath(customerId);
        final String serverKey = this.getDEPServerPrivateKeyPath(customerId);
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(this.getDEPCertificateFolder(customerId))) {
            ApiFactoryProvider.getFileAccessAPI().createDirectory(this.getDEPCertificateFolder(customerId));
        }
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(serverCrt)) {
            ApiFactoryProvider.getFileAccessAPI().createNewFile(serverCrt);
        }
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(serverKey)) {
            ApiFactoryProvider.getFileAccessAPI().createNewFile(serverKey);
        }
        MDMCertificateGenerator.getInstance().generateAppleDEPCertificate(serverCrt, serverKey);
    }
    
    private void generateDEPCertificate(final Long customerId, final Long depTokenId) throws Exception {
        final String serverCrt = this.getDEPServerPublicKeyPath(customerId, depTokenId);
        final String serverKey = this.getDEPServerPrivateKeyPath(customerId, depTokenId);
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(this.getDEPCertificateFolder(customerId))) {
            ApiFactoryProvider.getFileAccessAPI().createDirectory(this.getDEPCertificateFolder(customerId));
        }
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(this.getDEPCertificateFolder(customerId, depTokenId))) {
            ApiFactoryProvider.getFileAccessAPI().createDirectory(this.getDEPCertificateFolder(customerId, depTokenId));
        }
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(serverCrt)) {
            ApiFactoryProvider.getFileAccessAPI().createNewFile(serverCrt);
        }
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(serverKey)) {
            ApiFactoryProvider.getFileAccessAPI().createNewFile(serverKey);
        }
        MDMCertificateGenerator.getInstance().generateAppleDEPCertificate(serverCrt, serverKey);
    }
    
    private void regenerateDEPCertificate(final Long customerId, final Long depTokenId) throws Exception {
        ApiFactoryProvider.getFileAccessAPI().deleteFile(this.getDEPServerPublicKeyPath(customerId, depTokenId));
        this.generateDEPCertificate(customerId, depTokenId);
    }
    
    public boolean isKeyPairExist(final Long customerID, final Long depTokenId) {
        final String pubKey = this.getDEPServerPublicKeyPath(customerID, depTokenId);
        final String priKey = this.getDEPServerPrivateKeyPath(customerID, depTokenId);
        return ApiFactoryProvider.getFileAccessAPI().isFileExists(pubKey) && ApiFactoryProvider.getFileAccessAPI().isFileExists(priKey) && ApiFactoryProvider.getFileAccessAPI().getFileSize(pubKey) > 0L && ApiFactoryProvider.getFileAccessAPI().getFileSize(priKey) > 0L;
    }
    
    public String getDEPTokenPublicKeyPath(final Long customerID, final Long tokenId) throws Exception {
        if (!this.isKeyPairExist(customerID, tokenId)) {
            AppleDEPCertificateHandler.logger.log(Level.WARNING, "Generating new DEP Certificate as it is empty..");
            getInstance().generateDEPCertificate(customerID, tokenId);
            if (DEPEnrollmentUtil.getDEPEnrollmentStatus(customerID) == 0) {
                DEPEnrollmentUtil.setDEPEnrollmentStatus(1, customerID);
            }
        }
        else {
            final PemReader pemreader = new PemReader((Reader)new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(this.getDEPServerPublicKeyPath(customerID, tokenId))));
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
            final X509Certificate signedCertificate = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(pemreader.readPemObject().getContent()));
            if (signedCertificate.getNotAfter().before(new Date())) {
                AppleDEPCertificateHandler.logger.log(Level.WARNING, "Generating new DEP Certificate as it is expired..");
                this.regenerateDEPCertificate(customerID, tokenId);
            }
        }
        return this.getDEPServerPublicKeyPath(customerID, tokenId);
    }
    
    static {
        AppleDEPCertificateHandler.certificateHandler = null;
        AppleDEPCertificateHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
