package com.me.devicemanagement.onpremise.start.util;

import java.security.PrivateKey;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import java.util.Date;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CertificateGenerator extends com.me.devicemanagement.framework.start.util.CertificateGenerator
{
    private static CertificateGenerator generator;
    
    public static CertificateGenerator getInstance() {
        if (CertificateGenerator.generator == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
            CertificateGenerator.generator = new CertificateGenerator();
        }
        return CertificateGenerator.generator;
    }
    
    public void generateSelfSignedServerCACertificate(final String serverCertificateFile, final String serverKeyFile, final String commonName, final String... algorithm) throws Exception {
        X509Certificate serverCACertificate = null;
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 20);
        final Date notAfter = cal.getTime();
        String encryptionAlgorithm;
        if (algorithm == null || algorithm.length == 0) {
            encryptionAlgorithm = "SHA1withRSAEncryption";
        }
        else {
            encryptionAlgorithm = algorithm[0];
        }
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.C, "US");
        builderex.addRDN(BCStyle.ST, "CA");
        builderex.addRDN(BCStyle.OU, "ManageEngine");
        builderex.addRDN(BCStyle.O, "Zoho Corporation");
        builderex.addRDN(BCStyle.CN, commonName);
        final X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.C, "US");
        issuerBuilder.addRDN(BCStyle.ST, "CA");
        issuerBuilder.addRDN(BCStyle.OU, "ManageEngine");
        issuerBuilder.addRDN(BCStyle.O, "Zoho Corporation");
        issuerBuilder.addRDN(BCStyle.CN, commonName);
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(issuerBuilder.build(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder(encryptionAlgorithm).setProvider("BC").build(keyPair.getPrivate());
        builder.addExtension(Extension.basicConstraints, true, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(164);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_serverAuth };
        builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
        builder.addExtension(MiscObjectIdentifiers.netscapeCertType, false, (ASN1Encodable)new NetscapeCertType(64));
        final GeneralName[] names = { null };
        final int generalNameType = this.getGeneralNameType(commonName);
        final GeneralName g1 = new GeneralName(generalNameType, commonName);
        names[0] = g1;
        final GeneralNames subjectAltName = new GeneralNames(names);
        final X509CertificateHolder holder = builder.build(contentSigner);
        serverCACertificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(serverCertificateFile));
        certwriter.writeObject((Object)serverCACertificate);
        certwriter.flush();
        certwriter.close();
        final PEMWriter writer = new PEMWriter((Writer)new FileWriter(serverKeyFile));
        writer.writeObject((Object)keyPair.getPrivate());
        writer.flush();
        writer.close();
    }
    
    public void generateSelfSignedServerCertificate(final String caRootCertificateFileName, final String caRootKeyFileName, final String serverCertificateFileName, final String serverKeyFileName, final String commonName) throws Exception {
        final File rootCACertFile = new File(caRootCertificateFileName);
        final File rootCAKeyFile = new File(caRootKeyFileName);
        final X509Certificate rootCA = CertificateUtils.loadX509CertificateFromFile(rootCACertFile);
        final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromFile(rootCAKeyFile);
        this.generateSelfSignedServerCertificate(rootCA, privateKey, serverCertificateFileName, serverKeyFileName, commonName);
    }
    
    public void generateSelfSignedServerCertificate(final X509Certificate rootCA, final PrivateKey rootCAKey, final String serverCertificateFileName, final String serverKeyFileName, final String commonName) throws Exception {
        X509Certificate serverCertificate = null;
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final BigInteger serialNumber = BigInteger.ONE;
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 20);
        final Date notAfter = cal.getTime();
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.C, "US");
        builderex.addRDN(BCStyle.ST, "CA");
        builderex.addRDN(BCStyle.OU, "ManageEngine");
        builderex.addRDN(BCStyle.O, "Zoho Corporation");
        builderex.addRDN(BCStyle.CN, commonName);
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(rootCA, serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(rootCAKey);
        builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(false));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(160);
        builder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
        final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth };
        builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
        final X509CertificateHolder holder = builder.build(contentSigner);
        serverCertificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(serverCertificateFileName));
        certwriter.writeObject((Object)serverCertificate);
        certwriter.flush();
        certwriter.close();
        final PEMWriter writer = new PEMWriter((Writer)new FileWriter(serverKeyFileName));
        writer.writeObject((Object)keyPair.getPrivate());
        writer.flush();
        writer.close();
    }
    
    public void cloneSelfSignedServerCACertificate(final String serverCertificateFile, final String serverKeyFile) throws Exception {
        X509Certificate serverCA = null;
        final X509Certificate oldCertificate = CertificateUtils.loadX509CertificateFromFile(new File(serverCertificateFile));
        final PrivateKey oldKey = CertificateUtils.loadPrivateKeyFromFile(new File(serverKeyFile));
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Date notBefore = oldCertificate.getNotBefore();
        final Date notAfter = oldCertificate.getNotAfter();
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(oldCertificate.getIssuerX500Principal(), serialNumber, notBefore, notAfter, oldCertificate.getSubjectX500Principal(), oldCertificate.getPublicKey());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(oldKey);
        builder.addExtension(Extension.basicConstraints, true, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(oldCertificate.getPublicKey());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(oldCertificate.getPublicKey());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(164);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_serverAuth };
        builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
        builder.addExtension(MiscObjectIdentifiers.netscapeCertType, false, (ASN1Encodable)new NetscapeCertType(64));
        final X509CertificateHolder holder = builder.build(contentSigner);
        serverCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(serverCertificateFile));
        certwriter.writeObject((Object)serverCA);
        certwriter.flush();
        certwriter.close();
    }
    
    public void cloneSelfSignedServerCACertificateWithoutValidation(final String serverCertificateFile, final String serverKeyFile) throws Exception {
        X509Certificate serverCA = null;
        final X509Certificate oldCertificate = CertificateUtils.loadX509CertificateFromFileWithoutValidation(new File(serverCertificateFile));
        final PrivateKey oldKey = CertificateUtils.loadPrivateKeyFromFile(new File(serverKeyFile));
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Date notBefore = oldCertificate.getNotBefore();
        final Date notAfter = oldCertificate.getNotAfter();
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(oldCertificate.getIssuerX500Principal(), serialNumber, notBefore, notAfter, oldCertificate.getSubjectX500Principal(), oldCertificate.getPublicKey());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(oldKey);
        builder.addExtension(Extension.basicConstraints, true, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(oldCertificate.getPublicKey());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(oldCertificate.getPublicKey());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(164);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_serverAuth };
        builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
        builder.addExtension(MiscObjectIdentifiers.netscapeCertType, false, (ASN1Encodable)new NetscapeCertType(64));
        final X509CertificateHolder holder = builder.build(contentSigner);
        serverCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(serverCertificateFile));
        certwriter.writeObject((Object)serverCA);
        certwriter.flush();
        certwriter.close();
    }
    
    public void generateCARootCertificate(final String caRootCertificateFileName, final String caRootKeyFileName, final String... authorityNames) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        X509Certificate rootCA = null;
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 100);
        final Date notAfter = cal.getTime();
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.C, "US");
        builderex.addRDN(BCStyle.ST, "CA");
        builderex.addRDN(BCStyle.OU, "ManageEngine");
        builderex.addRDN(BCStyle.O, "Zoho Corporation");
        builderex.addRDN(BCStyle.CN, "ManageEngineCA");
        final X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.C, "US");
        issuerBuilder.addRDN(BCStyle.ST, "CA");
        issuerBuilder.addRDN(BCStyle.OU, "ManageEngine");
        issuerBuilder.addRDN(BCStyle.O, "Zoho Corporation");
        issuerBuilder.addRDN(BCStyle.CN, "ManageEngineCA");
        if (authorityNames.length > 0) {
            issuerBuilder.addRDN(BCStyle.CN, authorityNames[0]);
            issuerBuilder.addRDN(BCStyle.OU, authorityNames[1]);
            builderex.addRDN(BCStyle.CN, authorityNames[0]);
            builderex.addRDN(BCStyle.OU, authorityNames[1]);
        }
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(issuerBuilder.build(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(keyPair.getPrivate());
        builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(166);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final X509CertificateHolder holder = builder.build(contentSigner);
        rootCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certWriter = new PEMWriter((Writer)new FileWriter(caRootCertificateFileName));
        certWriter.writeObject((Object)rootCA);
        certWriter.flush();
        certWriter.close();
        final PEMWriter keyWriter = new PEMWriter((Writer)new FileWriter(caRootKeyFileName));
        keyWriter.writeObject((Object)keyPair.getPrivate());
        keyWriter.flush();
        keyWriter.close();
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        CertificateGenerator.generator = null;
    }
}
