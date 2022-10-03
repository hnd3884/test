package org.bouncycastle.mail.smime.examples;

import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Authenticator;
import javax.mail.Session;
import org.bouncycastle.operator.OutputCompressor;
import org.bouncycastle.cms.jcajce.ZlibCompressor;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.SMIMECompressedGenerator;

public class CreateCompressedMail
{
    public static void main(final String[] array) throws Exception {
        final SMIMECompressedGenerator smimeCompressedGenerator = new SMIMECompressedGenerator();
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setText("Hello world!");
        final MimeBodyPart generate = smimeCompressedGenerator.generate(mimeBodyPart, (OutputCompressor)new ZlibCompressor());
        final Session defaultInstance = Session.getDefaultInstance(System.getProperties(), (Authenticator)null);
        final InternetAddress from = new InternetAddress("\"Eric H. Echidna\"<eric@bouncycastle.org>");
        final InternetAddress internetAddress = new InternetAddress("example@bouncycastle.org");
        final MimeMessage mimeMessage = new MimeMessage(defaultInstance);
        mimeMessage.setFrom((Address)from);
        mimeMessage.setRecipient(Message.RecipientType.TO, (Address)internetAddress);
        mimeMessage.setSubject("example compressed message");
        mimeMessage.setContent(generate.getContent(), generate.getContentType());
        mimeMessage.saveChanges();
        mimeMessage.writeTo((OutputStream)new FileOutputStream("compressed.message"));
    }
}
