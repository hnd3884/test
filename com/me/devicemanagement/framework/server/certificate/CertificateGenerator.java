package com.me.devicemanagement.framework.server.certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.DERPrintableString;
import java.util.Calendar;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
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
    
    public X509Certificate generateClientCertificate(final X509Certificate serverCertificate, final PrivateKey serverKey, final PublicKey clientPublicKey, final String subjectName) throws Exception {
        X509Certificate clientCertificate = null;
        final BigInteger serialNumber = BigInteger.ONE;
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 10);
        final Date notAfter = cal.getTime();
        JcaX509v3CertificateBuilder builder = null;
        final DERPrintableString commonName = new DERPrintableString(subjectName);
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.CN, (ASN1Encodable)commonName);
        builder = new JcaX509v3CertificateBuilder(serverCertificate, serialNumber, notBefore, notAfter, builderex.build(), clientPublicKey);
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSAEncryption").setProvider("BC").build(serverKey);
        builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(false));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(clientPublicKey);
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(128);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
        final X509CertificateHolder holder = builder.build(contentSigner);
        clientCertificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        return clientCertificate;
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        CertificateGenerator.generator = null;
    }
}
