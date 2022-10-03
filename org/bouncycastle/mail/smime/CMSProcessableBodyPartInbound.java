package org.bouncycastle.mail.smime;

import java.io.IOException;
import javax.mail.MessagingException;
import org.bouncycastle.cms.CMSException;
import java.io.OutputStream;
import javax.mail.BodyPart;
import org.bouncycastle.cms.CMSProcessable;

public class CMSProcessableBodyPartInbound implements CMSProcessable
{
    private final BodyPart bodyPart;
    private final String defaultContentTransferEncoding;
    
    public CMSProcessableBodyPartInbound(final BodyPart bodyPart) {
        this(bodyPart, "7bit");
    }
    
    public CMSProcessableBodyPartInbound(final BodyPart bodyPart, final String defaultContentTransferEncoding) {
        this.bodyPart = bodyPart;
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        try {
            SMIMEUtil.outputBodyPart(outputStream, true, this.bodyPart, this.defaultContentTransferEncoding);
        }
        catch (final MessagingException ex) {
            throw new CMSException("can't write BodyPart to stream: " + ex, (Exception)ex);
        }
    }
    
    public Object getContent() {
        return this.bodyPart;
    }
}
