package org.bouncycastle.mail.smime.examples;

import java.security.cert.Certificate;
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
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import java.security.cert.X509Certificate;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import java.security.KeyStore;

public class CreateLargeEncryptedMail
{
    public static void main(final String[] array) throws Exception {
        if (array.length != 3) {
            System.err.println("usage: CreateLargeEncryptedMail pkcs12Keystore password inputFile");
            System.exit(0);
        }
        final KeyStore instance = KeyStore.getInstance("PKCS12", "BC");
        final Certificate[] certificateChain = instance.getCertificateChain(ExampleUtils.findKeyAlias(instance, array[0], array[1].toCharArray()));
        final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
        smimeEnvelopedGenerator.addRecipientInfoGenerator((RecipientInfoGenerator)new JceKeyTransRecipientInfoGenerator((X509Certificate)certificateChain[0]).setProvider("BC"));
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(new File(array[2]))));
        mimeBodyPart.setHeader("Content-Type", "application/octet-stream");
        mimeBodyPart.setHeader("Content-Transfer-Encoding", "binary");
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
