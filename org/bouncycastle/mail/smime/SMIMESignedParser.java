package org.bouncycastle.mail.smime;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.MailcapCommandMap;
import javax.activation.CommandMap;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import org.bouncycastle.cms.CMSException;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.bouncycastle.cms.CMSTypedStream;
import javax.mail.BodyPart;
import java.io.File;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.CMSSignedDataParser;

public class SMIMESignedParser extends CMSSignedDataParser
{
    Object message;
    MimeBodyPart content;
    
    private static InputStream getInputStream(final Part part) throws MessagingException {
        try {
            if (part.isMimeType("multipart/signed")) {
                throw new MessagingException("attempt to create signed data object from multipart content - use MimeMultipart constructor.");
            }
            return part.getInputStream();
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    private static File getTmpFile() throws MessagingException {
        try {
            return File.createTempFile("bcMail", ".mime");
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    private static CMSTypedStream getSignedInputStream(final BodyPart bodyPart, final String s, final File file) throws MessagingException {
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            SMIMEUtil.outputBodyPart(bufferedOutputStream, true, bodyPart, s);
            bufferedOutputStream.close();
            return new CMSTypedStream((InputStream)new TemporaryFileInputStream(file));
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final MimeMultipart mimeMultipart) throws MessagingException, CMSException {
        this(digestCalculatorProvider, mimeMultipart, getTmpFile());
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final MimeMultipart mimeMultipart, final File file) throws MessagingException, CMSException {
        this(digestCalculatorProvider, mimeMultipart, "7bit", file);
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final MimeMultipart mimeMultipart, final String s) throws MessagingException, CMSException {
        this(digestCalculatorProvider, mimeMultipart, s, getTmpFile());
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final MimeMultipart message, final String s, final File file) throws MessagingException, CMSException {
        super(digestCalculatorProvider, getSignedInputStream(message.getBodyPart(0), s, file), getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
        this.drainContent();
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final Part message) throws MessagingException, CMSException, SMIMEException {
        super(digestCalculatorProvider, getInputStream(message));
        this.message = message;
        final CMSTypedStream signedContent = this.getSignedContent();
        if (signedContent != null) {
            this.content = SMIMEUtil.toWriteOnceBodyPart(signedContent);
        }
    }
    
    public SMIMESignedParser(final DigestCalculatorProvider digestCalculatorProvider, final Part message, final File file) throws MessagingException, CMSException, SMIMEException {
        super(digestCalculatorProvider, getInputStream(message));
        this.message = message;
        final CMSTypedStream signedContent = this.getSignedContent();
        if (signedContent != null) {
            this.content = SMIMEUtil.toMimeBodyPart(signedContent, file);
        }
    }
    
    public MimeBodyPart getContent() {
        return this.content;
    }
    
    public MimeMessage getContentAsMimeMessage(final Session session) throws MessagingException, IOException {
        if (this.message instanceof MimeMultipart) {
            return new MimeMessage(session, ((MimeMultipart)this.message).getBodyPart(0).getInputStream());
        }
        return new MimeMessage(session, this.getSignedContent().getContentStream());
    }
    
    public Object getContentWithSignature() {
        return this.message;
    }
    
    private void drainContent() throws CMSException {
        try {
            this.getSignedContent().drain();
        }
        catch (final IOException ex) {
            throw new CMSException("unable to read content for verification: " + ex, (Exception)ex);
        }
    }
    
    static {
        final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
        if (defaultCommandMap instanceof MailcapCommandMap) {
            final MailcapCommandMap mailcapCommandMap = (MailcapCommandMap)defaultCommandMap;
            mailcapCommandMap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
            mailcapCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
            mailcapCommandMap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
            mailcapCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
            mailcapCommandMap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    CommandMap.setDefaultCommandMap(mailcapCommandMap);
                    return null;
                }
            });
        }
    }
    
    private static class TemporaryFileInputStream extends BufferedInputStream
    {
        private final File _file;
        
        TemporaryFileInputStream(final File file) throws FileNotFoundException {
            super(new FileInputStream(file));
            this._file = file;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            this._file.delete();
        }
    }
}
