package com.me.mdm.server.certificate;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import java.util.Date;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;

public class MDMCertificateGenerator
{
    private static MDMCertificateGenerator mdmCertificateGenerator;
    
    public static MDMCertificateGenerator getInstance() {
        if (MDMCertificateGenerator.mdmCertificateGenerator == null) {
            MDMCertificateGenerator.mdmCertificateGenerator = new MDMCertificateGenerator();
        }
        return MDMCertificateGenerator.mdmCertificateGenerator;
    }
    
    public void generateAppleDEPCertificate(final String depCertificateFileName, final String depKeyFileName) throws Exception {
        X509Certificate rootCA = null;
        final BigInteger serialNumber = BigInteger.ONE;
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 25);
        final Date notAfter = cal.getTime();
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.C, "US");
        builderex.addRDN(BCStyle.ST, "CA");
        builderex.addRDN(BCStyle.OU, "ManageEngine");
        builderex.addRDN(BCStyle.O, "Zoho Corporation");
        builderex.addRDN(BCStyle.CN, "Self Signed DEP");
        final X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.C, "US");
        issuerBuilder.addRDN(BCStyle.ST, "CA");
        issuerBuilder.addRDN(BCStyle.OU, "ManageEngine");
        issuerBuilder.addRDN(BCStyle.O, "Zoho Corporation");
        issuerBuilder.addRDN(BCStyle.CN, "Self Signed DEP");
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(issuerBuilder.build(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSAEncryption").setProvider("BC").build(keyPair.getPrivate());
        builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(166);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final X509CertificateHolder holder = builder.build(contentSigner);
        rootCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        final File tmpDepCertificateFileName = File.createTempFile("tmp_depserver_" + timeStamp, ".pem");
        final File tmpDepKeyFileName = File.createTempFile("tmp_depserver_" + timeStamp, ".key");
        final PEMWriter certWriter = new PEMWriter((Writer)new FileWriter(tmpDepCertificateFileName));
        certWriter.writeObject((Object)rootCA);
        certWriter.flush();
        certWriter.close();
        final PEMWriter keyWriter = new PEMWriter((Writer)new FileWriter(tmpDepKeyFileName));
        keyWriter.writeObject((Object)keyPair.getPrivate());
        keyWriter.flush();
        keyWriter.close();
        ApiFactoryProvider.getFileAccessAPI().writeFile(depCertificateFileName, (InputStream)new FileInputStream(tmpDepCertificateFileName));
        ApiFactoryProvider.getFileAccessAPI().writeFile(depKeyFileName, (InputStream)new FileInputStream(tmpDepKeyFileName));
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        MDMCertificateGenerator.mdmCertificateGenerator = null;
    }
}
