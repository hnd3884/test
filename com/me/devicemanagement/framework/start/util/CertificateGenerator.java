package com.me.devicemanagement.framework.start.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Writer;
import java.io.OutputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import java.util.Date;
import org.bouncycastle.openssl.PEMWriter;
import java.io.OutputStreamWriter;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
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
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CertificateGenerator
{
    private static CertificateGenerator generator;
    
    public static CertificateGenerator getInstance() {
        if (CertificateGenerator.generator == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
            CertificateGenerator.generator = new CertificateGenerator();
        }
        return CertificateGenerator.generator;
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
        final OutputStream certificateOutputStream = ApiFactoryProvider.getFileAccessAPI().writeFile(caRootCertificateFileName);
        final Writer certWriter = new OutputStreamWriter(certificateOutputStream);
        final PEMWriter certPemWriter = new PEMWriter(certWriter);
        certPemWriter.writeObject((Object)rootCA);
        certPemWriter.flush();
        certPemWriter.close();
        final OutputStream keyOutputStream = ApiFactoryProvider.getFileAccessAPI().writeFile(caRootKeyFileName);
        final Writer keyWriter = new OutputStreamWriter(keyOutputStream);
        final PEMWriter keyPemWriter = new PEMWriter(keyWriter);
        keyPemWriter.writeObject((Object)keyPair.getPrivate());
        keyPemWriter.flush();
        keyPemWriter.close();
    }
    
    protected int getGeneralNameType(final String subjectAlternativeName) {
        final String ipAddressRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final Pattern ipAddressPattern = Pattern.compile(ipAddressRegex);
        final Matcher ipAddressMatcher = ipAddressPattern.matcher(subjectAlternativeName);
        if (ipAddressMatcher.matches()) {
            return 7;
        }
        return 2;
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        CertificateGenerator.generator = null;
    }
}
