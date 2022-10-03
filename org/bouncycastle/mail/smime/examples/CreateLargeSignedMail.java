package org.bouncycastle.mail.smime.examples;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import javax.mail.internet.MimeMultipart;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.util.Store;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Collection;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import java.util.ArrayList;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import java.security.cert.X509Certificate;
import java.security.KeyPair;

public class CreateLargeSignedMail
{
    static int serialNo;
    
    static X509Certificate makeCertificate(final KeyPair keyPair, final String s, final KeyPair keyPair2, final String s2) throws GeneralSecurityException, IOException, OperatorCreationException {
        final PublicKey public1 = keyPair.getPublic();
        final PrivateKey private1 = keyPair2.getPrivate();
        final PublicKey public2 = keyPair2.getPublic();
        final JcaX509ExtensionUtils jcaX509ExtensionUtils = new JcaX509ExtensionUtils();
        final JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(s2), BigInteger.valueOf(CreateLargeSignedMail.serialNo++), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 8640000000L), new X500Name(s), public1);
        ((X509v3CertificateBuilder)jcaX509v3CertificateBuilder).addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)jcaX509ExtensionUtils.createSubjectKeyIdentifier(public1));
        ((X509v3CertificateBuilder)jcaX509v3CertificateBuilder).addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)jcaX509ExtensionUtils.createAuthorityKeyIdentifier(public2));
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(((X509v3CertificateBuilder)jcaX509v3CertificateBuilder).build(new JcaContentSignerBuilder("MD5withRSA").setProvider("BC").build(private1)));
    }
    
    public static void main(final String[] array) throws Exception {
        final KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA", "BC");
        instance.initialize(1024, new SecureRandom());
        final String s = "O=Bouncy Castle, C=AU";
        final KeyPair generateKeyPair = instance.generateKeyPair();
        final X509Certificate certificate = makeCertificate(generateKeyPair, s, generateKeyPair, s);
        final String s2 = "CN=Eric H. Echidna, E=eric@bouncycastle.org, O=Bouncy Castle, C=AU";
        final KeyPair generateKeyPair2 = instance.generateKeyPair();
        final X509Certificate certificate2 = makeCertificate(generateKeyPair2, s2, generateKeyPair, s);
        final ArrayList list = new ArrayList();
        list.add(certificate2);
        list.add(certificate);
        final JcaCertStore jcaCertStore = new JcaCertStore((Collection)list);
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final SMIMECapabilityVector smimeCapabilityVector = new SMIMECapabilityVector();
        smimeCapabilityVector.addCapability(SMIMECapability.dES_EDE3_CBC);
        smimeCapabilityVector.addCapability(SMIMECapability.rC2_CBC, 128);
        smimeCapabilityVector.addCapability(SMIMECapability.dES_CBC);
        asn1EncodableVector.add((ASN1Encodable)new SMIMECapabilitiesAttribute(smimeCapabilityVector));
        asn1EncodableVector.add((ASN1Encodable)new SMIMEEncryptionKeyPreferenceAttribute(new IssuerAndSerialNumber(new X500Name(s), certificate2.getSerialNumber())));
        final SMIMESignedGenerator smimeSignedGenerator = new SMIMESignedGenerator();
        smimeSignedGenerator.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").setSignedAttributeGenerator(new AttributeTable(asn1EncodableVector)).build("SHA1withRSA", generateKeyPair2.getPrivate(), certificate2));
        smimeSignedGenerator.addCertificates((Store)jcaCertStore);
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(new File(array[0]))));
        mimeBodyPart.setHeader("Content-Type", "application/octet-stream");
        mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");
        final MimeMultipart generate = smimeSignedGenerator.generate(mimeBodyPart);
        final Session defaultInstance = Session.getDefaultInstance(System.getProperties(), (Authenticator)null);
        final InternetAddress from = new InternetAddress("\"Eric H. Echidna\"<eric@bouncycastle.org>");
        final InternetAddress internetAddress = new InternetAddress("example@bouncycastle.org");
        final MimeMessage mimeMessage = new MimeMessage(defaultInstance);
        mimeMessage.setFrom((Address)from);
        mimeMessage.setRecipient(Message.RecipientType.TO, (Address)internetAddress);
        mimeMessage.setSubject("example signed message");
        mimeMessage.setContent((Object)generate, generate.getContentType());
        mimeMessage.saveChanges();
        mimeMessage.writeTo((OutputStream)new FileOutputStream("signed.message"));
    }
    
    static {
        CreateLargeSignedMail.serialNo = 1;
    }
}
