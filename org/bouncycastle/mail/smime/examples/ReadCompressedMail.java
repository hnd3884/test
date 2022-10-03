package org.bouncycastle.mail.smime.examples;

import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.mail.smime.SMIMECompressed;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;

public class ReadCompressedMail
{
    public static void main(final String[] array) throws Exception {
        final MimeBodyPart mimeBodyPart = SMIMEUtil.toMimeBodyPart(new SMIMECompressed(new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new FileInputStream("compressed.message"))).getContent((InputExpanderProvider)new ZlibExpanderProvider()));
        System.out.println("Message Contents");
        System.out.println("----------------");
        System.out.println(mimeBodyPart.getContent());
    }
}
