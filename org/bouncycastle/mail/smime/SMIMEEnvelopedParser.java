package org.bouncycastle.mail.smime;

import javax.mail.internet.MimeMessage;
import org.bouncycastle.cms.CMSException;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.mail.Part;
import javax.mail.internet.MimePart;
import org.bouncycastle.cms.CMSEnvelopedDataParser;

public class SMIMEEnvelopedParser extends CMSEnvelopedDataParser
{
    private final MimePart message;
    
    private static InputStream getInputStream(final Part part, final int n) throws MessagingException {
        try {
            final InputStream inputStream = part.getInputStream();
            if (n == 0) {
                return new BufferedInputStream(inputStream);
            }
            return new BufferedInputStream(inputStream, n);
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    public SMIMEEnvelopedParser(final MimeBodyPart mimeBodyPart) throws IOException, MessagingException, CMSException {
        this(mimeBodyPart, 0);
    }
    
    public SMIMEEnvelopedParser(final MimeMessage mimeMessage) throws IOException, MessagingException, CMSException {
        this(mimeMessage, 0);
    }
    
    public SMIMEEnvelopedParser(final MimeBodyPart message, final int n) throws IOException, MessagingException, CMSException {
        super(getInputStream((Part)message, n));
        this.message = (MimePart)message;
    }
    
    public SMIMEEnvelopedParser(final MimeMessage message, final int n) throws IOException, MessagingException, CMSException {
        super(getInputStream((Part)message, n));
        this.message = (MimePart)message;
    }
    
    public MimePart getEncryptedContent() {
        return this.message;
    }
}
