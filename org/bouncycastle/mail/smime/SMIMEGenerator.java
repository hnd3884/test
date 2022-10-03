package org.bouncycastle.mail.smime;

import org.bouncycastle.cms.CMSEnvelopedGenerator;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import java.security.Provider;
import java.io.IOException;
import javax.mail.Multipart;
import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.Header;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.util.Strings;
import java.util.Map;

public class SMIMEGenerator
{
    private static Map BASE_CIPHER_NAMES;
    protected boolean useBase64;
    protected String encoding;
    
    protected SMIMEGenerator() {
        this.useBase64 = true;
        this.encoding = "base64";
    }
    
    public void setContentTransferEncoding(final String encoding) {
        this.encoding = encoding;
        this.useBase64 = Strings.toLowerCase(encoding).equals("base64");
    }
    
    protected MimeBodyPart makeContentBodyPart(final MimeBodyPart mimeBodyPart) throws SMIMEException {
        try {
            final MimeMessage mimeMessage = new MimeMessage((Session)null);
            final Enumeration allHeaders = mimeBodyPart.getAllHeaders();
            mimeMessage.setDataHandler(mimeBodyPart.getDataHandler());
            while (allHeaders.hasMoreElements()) {
                final Header header = allHeaders.nextElement();
                mimeMessage.setHeader(header.getName(), header.getValue());
            }
            mimeMessage.saveChanges();
            final Enumeration allHeaders2 = mimeMessage.getAllHeaders();
            while (allHeaders2.hasMoreElements()) {
                final Header header2 = allHeaders2.nextElement();
                if (Strings.toLowerCase(header2.getName()).startsWith("content-")) {
                    mimeBodyPart.setHeader(header2.getName(), header2.getValue());
                }
            }
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception saving message state.", (Exception)ex);
        }
        return mimeBodyPart;
    }
    
    protected MimeBodyPart makeContentBodyPart(final MimeMessage mimeMessage) throws SMIMEException {
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeMessage.removeHeader("Message-Id");
            mimeMessage.removeHeader("Mime-Version");
            try {
                if (mimeMessage.getContent() instanceof Multipart) {
                    mimeBodyPart.setContent((Object)mimeMessage.getRawInputStream(), mimeMessage.getContentType());
                    this.extractHeaders(mimeBodyPart, mimeMessage);
                    return mimeBodyPart;
                }
            }
            catch (final MessagingException ex) {}
            mimeBodyPart.setContent(mimeMessage.getContent(), mimeMessage.getContentType());
            mimeBodyPart.setDataHandler(mimeMessage.getDataHandler());
            this.extractHeaders(mimeBodyPart, mimeMessage);
        }
        catch (final MessagingException ex2) {
            throw new SMIMEException("exception saving message state.", (Exception)ex2);
        }
        catch (final IOException ex3) {
            throw new SMIMEException("exception getting message content.", ex3);
        }
        return mimeBodyPart;
    }
    
    private void extractHeaders(final MimeBodyPart mimeBodyPart, final MimeMessage mimeMessage) throws MessagingException {
        final Enumeration allHeaders = mimeMessage.getAllHeaders();
        while (allHeaders.hasMoreElements()) {
            final Header header = allHeaders.nextElement();
            mimeBodyPart.addHeader(header.getName(), header.getValue());
        }
    }
    
    protected KeyGenerator createSymmetricKeyGenerator(final String s, final Provider provider) throws NoSuchAlgorithmException {
        try {
            return this.createKeyGenerator(s, provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            try {
                final String s2 = SMIMEGenerator.BASE_CIPHER_NAMES.get(s);
                if (s2 != null) {
                    return this.createKeyGenerator(s2, provider);
                }
            }
            catch (final NoSuchAlgorithmException ex2) {}
            if (provider != null) {
                return this.createSymmetricKeyGenerator(s, null);
            }
            throw ex;
        }
    }
    
    private KeyGenerator createKeyGenerator(final String s, final Provider provider) throws NoSuchAlgorithmException {
        if (provider != null) {
            return KeyGenerator.getInstance(s, provider);
        }
        return KeyGenerator.getInstance(s);
    }
    
    static {
        (SMIMEGenerator.BASE_CIPHER_NAMES = new HashMap()).put(CMSEnvelopedGenerator.DES_EDE3_CBC, "DESEDE");
        SMIMEGenerator.BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES128_CBC, "AES");
        SMIMEGenerator.BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES192_CBC, "AES");
        SMIMEGenerator.BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES256_CBC, "AES");
    }
}
