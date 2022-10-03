package org.bouncycastle.mail.smime.examples;

import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.mail.smime.SMIMECompressedParser;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import org.bouncycastle.mail.smime.util.SharedFileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;

public class ReadLargeCompressedMail
{
    public static void main(final String[] array) throws Exception {
        ExampleUtils.dumpContent(SMIMEUtil.toMimeBodyPart(new SMIMECompressedParser(new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new SharedFileInputStream("compressed.message"))).getContent((InputExpanderProvider)new ZlibExpanderProvider())), array[0]);
    }
}
