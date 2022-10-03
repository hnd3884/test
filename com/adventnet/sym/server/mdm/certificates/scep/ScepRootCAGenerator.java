package com.adventnet.sym.server.mdm.certificates.scep;

import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.certificates.MdmCertAuthUtil;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.cert.CertIOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Calendar;
import java.security.PublicKey;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.KeyPair;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScepRootCAGenerator
{
    Logger logger;
    private static ScepRootCAGenerator rootCAGenerator;
    
    private ScepRootCAGenerator() {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
    }
    
    public static ScepRootCAGenerator getInstance() {
        if (ScepRootCAGenerator.rootCAGenerator == null) {
            ScepRootCAGenerator.rootCAGenerator = new ScepRootCAGenerator();
        }
        return ScepRootCAGenerator.rootCAGenerator;
    }
    
    public void generateRootCACertificateForCustomer(final Long customerID) {
        try {
            this.logger.log(Level.INFO, "======================= ROOT CA CREATION BEGINS for CUSTOMER {0} =====================", new Object[] { customerID });
            final KeyPair rootCAKeyPair = CertificateUtil.createRsaKeyPair(2048);
            final X500Name certificateIssuer = this.getCertificateIssuer();
            final int validityPeriod_years = 50;
            final JcaX509v3CertificateBuilder certificateBuilder = this.buildCertificate(certificateIssuer, rootCAKeyPair.getPublic(), validityPeriod_years);
            final ContentSigner certificateSigner = new JcaContentSignerBuilder("SHA512withRSA").build(rootCAKeyPair.getPrivate());
            final X509CertificateHolder certificateHolder = certificateBuilder.build(certificateSigner);
            final X509Certificate rootCACertificate = (X509Certificate)CertificateUtil.convertInputStreamToX509CertificateChain(new ByteArrayInputStream(certificateHolder.getEncoded()))[0];
            this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, Root CA Certificate and Private Key created for customer successfully", new Object[] { customerID });
            this.storeCACredentialsToFile(customerID, rootCACertificate, rootCAKeyPair.getPrivate());
            CustomerParamsHandler.getInstance().addOrUpdateParameter("CA_CERT_CREATED", "true", (long)customerID);
            this.logger.log(Level.INFO, "======================= ROOT CA CREATION SUCCESSFUL for CUSTOMER {0} =====================", new Object[] { customerID });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "ScepRootCAGenerator: Exception while creating Root CA certificate and key for customer : " + n);
        }
    }
    
    private X500Name getCertificateIssuer() {
        final X500NameBuilder certIssuerNameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        certIssuerNameBuilder.addRDN(BCStyle.CN, "MDM");
        final X500Name certIssuerName = certIssuerNameBuilder.build();
        return certIssuerName;
    }
    
    private JcaX509v3CertificateBuilder buildCertificate(final X500Name issuer, final PublicKey publicKey, final int validityYears) throws CertIOException, NoSuchAlgorithmException {
        final Date certificateValidNotBefore = Calendar.getInstance().getTime();
        final Date certificateValidNotAfter = CertificateUtil.getCertificateValidityEndDate(validityYears);
        final BigInteger serialNumber = CertificateUtil.getRandomSerialNumber();
        this.logger.log(Level.INFO, "ScepRootCAGenerator: TBSCertificate details: Issuer: {0} \t Valididy: {1} \t Not Before: {2} \t Not After: {3} \t Serial Number: {4}", new Object[] { issuer, validityYears, certificateValidNotBefore, certificateValidNotAfter, serialNumber });
        final X500Name subject = issuer;
        final JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuer, serialNumber, certificateValidNotBefore, certificateValidNotAfter, subject, publicKey);
        CertificateUtil.addSubjectKeyIdentifier(certificateBuilder, publicKey);
        CertificateUtil.addTypicalCAKeyUsages(certificateBuilder);
        return certificateBuilder;
    }
    
    private void storeCACredentialsToFile(final Long customerID, final X509Certificate rootCACertificate, final PrivateKey privateKey) throws Exception {
        final String scepRootCACertificatePath = MdmCertAuthUtil.Scep.getScepRootCACertificatePath(customerID);
        final String scepRootCAPrivateKeyPath = MdmCertAuthUtil.Scep.getScepRootCAPrivateKeyPath(customerID);
        this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, CA file path: {1}\tPrivate Key file path: {2}", new Object[] { customerID, scepRootCACertificatePath, scepRootCAPrivateKeyPath });
        ApiFactoryProvider.getFileAccessAPI().createDirectory(new File(scepRootCACertificatePath).getParent());
        if (ApiFactoryProvider.getFileAccessAPI().readFile(scepRootCACertificatePath) == null || ApiFactoryProvider.getFileAccessAPI().readFile(scepRootCAPrivateKeyPath) == null) {
            this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, CA cert and Private key file isn't present already", new Object[] { customerID });
            ApiFactoryProvider.getFileAccessAPI().deleteFile(scepRootCACertificatePath);
            ApiFactoryProvider.getFileAccessAPI().deleteFile(scepRootCAPrivateKeyPath);
            this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, Storing files", new Object[] { customerID });
            ApiFactoryProvider.getFileAccessAPI().writeFile(scepRootCACertificatePath, rootCACertificate.getEncoded());
            ApiFactoryProvider.getFileAccessAPI().writeFile(scepRootCAPrivateKeyPath, privateKey.getEncoded());
            this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, Files stored", customerID);
        }
        this.logger.log(Level.INFO, "ScepRootCAGenerator: CustomerId = {0}, Verifying whether files are available...", customerID);
        this.verifyIfFileAvailable(scepRootCACertificatePath);
        this.verifyIfFileAvailable(scepRootCAPrivateKeyPath);
    }
    
    private void verifyIfFileAvailable(final String filePath) {
        this.logger.log(Level.INFO, "ScepRootCAGenerator: File path = {0} Verifying if file is present", new Object[] { filePath });
        final boolean isFileExists = ApiFactoryProvider.getFileAccessAPI().isFileExists(filePath);
        final long fileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(filePath);
        this.logger.log(Level.INFO, "ScepRootCAGenerator: Is file present: {0}. File size: {1}", new Object[] { isFileExists, fileSize });
    }
}
