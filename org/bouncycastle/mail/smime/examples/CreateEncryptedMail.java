package org.bouncycastle.mail.smime.examples;

import java.security.cert.Certificate;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Authenticator;
import javax.mail.Session;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.CMSAlgorithm;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import java.security.cert.X509Certificate;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class CreateEncryptedMail
{
    public static void main(final String[] array) throws Exception {
        if (array.length != 2) {
            System.err.println("usage: CreateEncryptedMail pkcs12Keystore password");
            System.exit(0);
        }
        if (Security.getProvider("BC") == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
        }
        final KeyStore instance = KeyStore.getInstance("PKCS12", "BC");
        instance.load(new FileInputStream(array[0]), array[1].toCharArray());
        final Enumeration<String> aliases = instance.aliases();
        String s = null;
        while (aliases.hasMoreElements()) {
            final String s2 = aliases.nextElement();
            if (instance.isKeyEntry(s2)) {
                s = s2;
            }
        }
        if (s == null) {
            System.err.println("can't find a private key!");
            System.exit(0);
        }
        final Certificate[] certificateChain = instance.getCertificateChain(s);
        final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
        smimeEnvelopedGenerator.addRecipientInfoGenerator((RecipientInfoGenerator)new JceKeyTransRecipientInfoGenerator((X509Certificate)certificateChain[0]).setProvider("BC"));
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setText("Hello world!");
        final MimeBodyPart generate = smimeEnvelopedGenerator.generate(mimeBodyPart, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC).setProvider("BC").build());
        final Session defaultInstance = Session.getDefaultInstance(System.getProperties(), (Authenticator)null);
        final InternetAddress from = new InternetAddress("\"Eric H. Echidna\"<eric@bouncycastle.org>");
        final InternetAddress internetAddress = new InternetAddress("example@bouncycastle.org");
        final MimeMessage mimeMessage = new MimeMessage(defaultInstance);
        mimeMessage.setFrom((Address)from);
        mimeMessage.setRecipient(Message.RecipientType.TO, (Address)internetAddress);
        mimeMessage.setSubject("example encrypted message");
        mimeMessage.setContent(generate.getContent(), generate.getContentType());
        mimeMessage.saveChanges();
        mimeMessage.writeTo((OutputStream)new FileOutputStream("encrypted.message"));
    }
}
