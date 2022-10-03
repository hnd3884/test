package org.bouncycastle.mail.smime.examples;

import javax.mail.internet.MimeBodyPart;
import java.util.Enumeration;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import java.security.PrivateKey;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import java.security.cert.X509Certificate;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;

public class ReadEncryptedMail
{
    public static void main(final String[] array) throws Exception {
        if (array.length != 2) {
            System.err.println("usage: ReadEncryptedMail pkcs12Keystore password");
            System.exit(0);
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
        final MimeBodyPart mimeBodyPart = SMIMEUtil.toMimeBodyPart(new SMIMEEnveloped(new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new FileInputStream("encrypted.message"))).getRecipientInfos().get((RecipientId)new JceKeyTransRecipientId((X509Certificate)instance.getCertificate(s))).getContent((Recipient)new JceKeyTransEnvelopedRecipient((PrivateKey)instance.getKey(s, null)).setProvider("BC")));
        System.out.println("Message Contents");
        System.out.println("----------------");
        System.out.println(mimeBodyPart.getContent());
    }
}
