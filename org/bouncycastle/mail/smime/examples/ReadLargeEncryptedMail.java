package org.bouncycastle.mail.smime.examples;

import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import java.security.PrivateKey;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import java.security.cert.X509Certificate;
import org.bouncycastle.mail.smime.SMIMEEnvelopedParser;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import org.bouncycastle.mail.smime.util.SharedFileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.security.KeyStore;

public class ReadLargeEncryptedMail
{
    public static void main(final String[] array) throws Exception {
        if (array.length != 3) {
            System.err.println("usage: ReadLargeEncryptedMail pkcs12Keystore password outputFile");
            System.exit(0);
        }
        final KeyStore instance = KeyStore.getInstance("PKCS12", "BC");
        final String keyAlias = ExampleUtils.findKeyAlias(instance, array[0], array[1].toCharArray());
        ExampleUtils.dumpContent(SMIMEUtil.toMimeBodyPart(new SMIMEEnvelopedParser(new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new SharedFileInputStream("encrypted.message"))).getRecipientInfos().get((RecipientId)new JceKeyTransRecipientId((X509Certificate)instance.getCertificate(keyAlias))).getContentStream((Recipient)new JceKeyTransEnvelopedRecipient((PrivateKey)instance.getKey(keyAlias, null)).setProvider("BC"))), array[2]);
    }
}
