package org.bouncycastle.mail.smime.examples;

import java.util.Hashtable;
import javax.mail.internet.MimeBodyPart;
import java.util.Enumeration;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.security.cert.Certificate;
import org.bouncycastle.mail.smime.SMIMEException;
import javax.mail.Transport;
import org.bouncycastle.util.Strings;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import javax.mail.Multipart;
import org.bouncycastle.util.Store;
import java.util.Collection;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import java.util.ArrayList;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.security.PrivateKey;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

public class SendSignedAndEncryptedMail
{
    public static void main(final String[] array) {
        if (array.length != 5) {
            System.err.println("usage: SendSignedAndEncryptedMail <pkcs12Keystore> <password> <keyalias> <smtp server> <email address>");
            System.exit(0);
        }
        try {
            final MailcapCommandMap defaultCommandMap = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
            defaultCommandMap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
            defaultCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
            defaultCommandMap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
            defaultCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
            defaultCommandMap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
            CommandMap.setDefaultCommandMap(defaultCommandMap);
            Security.addProvider((Provider)new BouncyCastleProvider());
            final KeyStore instance = KeyStore.getInstance("PKCS12", "BC");
            instance.load(new FileInputStream(array[0]), array[1].toCharArray());
            final Certificate[] certificateChain = instance.getCertificateChain(array[2]);
            final PrivateKey privateKey = (PrivateKey)instance.getKey(array[2], array[1].toCharArray());
            if (privateKey == null) {
                throw new Exception("cannot find private key for alias: " + array[2]);
            }
            final Properties properties = System.getProperties();
            ((Hashtable<String, String>)properties).put("mail.smtp.host", array[3]);
            final Session defaultInstance = Session.getDefaultInstance(properties, (Authenticator)null);
            final MimeMessage mimeMessage = new MimeMessage(defaultInstance);
            mimeMessage.setFrom((Address)new InternetAddress(array[4]));
            mimeMessage.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(array[4]));
            mimeMessage.setSubject("example encrypted message");
            mimeMessage.setContent((Object)"example encrypted message", "text/plain");
            mimeMessage.saveChanges();
            final SMIMECapabilityVector smimeCapabilityVector = new SMIMECapabilityVector();
            smimeCapabilityVector.addCapability(SMIMECapability.dES_EDE3_CBC);
            smimeCapabilityVector.addCapability(SMIMECapability.rC2_CBC, 128);
            smimeCapabilityVector.addCapability(SMIMECapability.dES_CBC);
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add((ASN1Encodable)new SMIMEEncryptionKeyPreferenceAttribute(new IssuerAndSerialNumber(new X500Name(((X509Certificate)certificateChain[0]).getIssuerDN().getName()), ((X509Certificate)certificateChain[0]).getSerialNumber())));
            asn1EncodableVector.add((ASN1Encodable)new SMIMECapabilitiesAttribute(smimeCapabilityVector));
            final SMIMESignedGenerator smimeSignedGenerator = new SMIMESignedGenerator();
            smimeSignedGenerator.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").setSignedAttributeGenerator(new AttributeTable(asn1EncodableVector)).build("DSA".equals(privateKey.getAlgorithm()) ? "SHA1withDSA" : "MD5withRSA", privateKey, (X509Certificate)certificateChain[0]));
            final ArrayList list = new ArrayList();
            list.add(certificateChain[0]);
            smimeSignedGenerator.addCertificates((Store)new JcaCertStore((Collection)list));
            final MimeMultipart generate = smimeSignedGenerator.generate(mimeMessage);
            final MimeMessage mimeMessage2 = new MimeMessage(defaultInstance);
            final Enumeration allHeaderLines = mimeMessage.getAllHeaderLines();
            while (allHeaderLines.hasMoreElements()) {
                mimeMessage2.addHeaderLine((String)allHeaderLines.nextElement());
            }
            mimeMessage2.setContent((Multipart)generate);
            mimeMessage2.saveChanges();
            final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
            smimeEnvelopedGenerator.addRecipientInfoGenerator((RecipientInfoGenerator)new JceKeyTransRecipientInfoGenerator((X509Certificate)certificateChain[0]).setProvider("BC"));
            final MimeBodyPart generate2 = smimeEnvelopedGenerator.generate(mimeMessage2, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC).setProvider("BC").build());
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            generate2.writeTo((OutputStream)byteArrayOutputStream);
            final MimeMessage mimeMessage3 = new MimeMessage(defaultInstance, (InputStream)new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            final Enumeration allHeaderLines2 = mimeMessage.getAllHeaderLines();
            while (allHeaderLines2.hasMoreElements()) {
                final String s = allHeaderLines2.nextElement();
                if (!Strings.toLowerCase(s).startsWith("content-")) {
                    mimeMessage3.addHeaderLine(s);
                }
            }
            Transport.send((Message)mimeMessage3);
        }
        catch (final SMIMEException ex) {
            ex.getUnderlyingException().printStackTrace(System.err);
            ex.printStackTrace(System.err);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace(System.err);
        }
    }
}
