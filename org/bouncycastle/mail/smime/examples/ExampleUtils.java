package org.bouncycastle.mail.smime.examples;

import java.util.Enumeration;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import java.io.FileOutputStream;
import javax.mail.internet.MimeBodyPart;

public class ExampleUtils
{
    public static void dumpContent(final MimeBodyPart mimeBodyPart, final String s) throws MessagingException, IOException {
        System.out.println("content type: " + mimeBodyPart.getContentType());
        final FileOutputStream fileOutputStream = new FileOutputStream(s);
        final InputStream inputStream = mimeBodyPart.getInputStream();
        final byte[] array = new byte[10000];
        int read;
        while ((read = inputStream.read(array, 0, array.length)) > 0) {
            fileOutputStream.write(array, 0, read);
        }
        fileOutputStream.close();
    }
    
    public static String findKeyAlias(final KeyStore keyStore, final String s, final char[] array) throws Exception {
        keyStore.load(new FileInputStream(s), array);
        final Enumeration<String> aliases = keyStore.aliases();
        String s2 = null;
        while (aliases.hasMoreElements()) {
            final String s3 = aliases.nextElement();
            if (keyStore.isKeyEntry(s3)) {
                s2 = s3;
            }
        }
        if (s2 == null) {
            throw new IllegalArgumentException("can't find a private key in keyStore: " + s);
        }
        return s2;
    }
}
