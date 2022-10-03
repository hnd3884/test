package org.bouncycastle.mail.smime;

import javax.mail.internet.MimeMessage;
import org.bouncycastle.cms.CMSException;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import javax.mail.Part;
import javax.mail.internet.MimePart;
import org.bouncycastle.cms.CMSEnvelopedData;

public class SMIMEEnveloped extends CMSEnvelopedData
{
    MimePart message;
    
    private static InputStream getInputStream(final Part part) throws MessagingException {
        try {
            return part.getInputStream();
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    public SMIMEEnveloped(final MimeBodyPart message) throws MessagingException, CMSException {
        super(getInputStream((Part)message));
        this.message = (MimePart)message;
    }
    
    public SMIMEEnveloped(final MimeMessage message) throws MessagingException, CMSException {
        super(getInputStream((Part)message));
        this.message = (MimePart)message;
    }
    
    public MimePart getEncryptedContent() {
        return this.message;
    }
}
