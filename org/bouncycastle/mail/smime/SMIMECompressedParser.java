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
import org.bouncycastle.cms.CMSCompressedDataParser;

public class SMIMECompressedParser extends CMSCompressedDataParser
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
    
    public SMIMECompressedParser(final MimeBodyPart mimeBodyPart) throws MessagingException, CMSException {
        this(mimeBodyPart, 0);
    }
    
    public SMIMECompressedParser(final MimeMessage mimeMessage) throws MessagingException, CMSException {
        this(mimeMessage, 0);
    }
    
    public SMIMECompressedParser(final MimeBodyPart message, final int n) throws MessagingException, CMSException {
        super(getInputStream((Part)message, n));
        this.message = (MimePart)message;
    }
    
    public SMIMECompressedParser(final MimeMessage message, final int n) throws MessagingException, CMSException {
        super(getInputStream((Part)message, n));
        this.message = (MimePart)message;
    }
    
    public MimePart getCompressedContent() {
        return this.message;
    }
}
