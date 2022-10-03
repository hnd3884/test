package org.bouncycastle.mail.smime;

import java.io.IOException;
import javax.mail.MessagingException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import javax.mail.internet.MimeBodyPart;
import java.io.OutputStream;
import javax.mail.BodyPart;
import org.bouncycastle.cms.CMSProcessable;

public class CMSProcessableBodyPartOutbound implements CMSProcessable
{
    private BodyPart bodyPart;
    private String defaultContentTransferEncoding;
    
    public CMSProcessableBodyPartOutbound(final BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }
    
    public CMSProcessableBodyPartOutbound(final BodyPart bodyPart, final String defaultContentTransferEncoding) {
        this.bodyPart = bodyPart;
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
    }
    
    public void write(OutputStream outputStream) throws IOException, CMSException {
        try {
            if (SMIMEUtil.isCanonicalisationRequired((MimeBodyPart)this.bodyPart, this.defaultContentTransferEncoding)) {
                outputStream = new CRLFOutputStream(outputStream);
            }
            this.bodyPart.writeTo(outputStream);
        }
        catch (final MessagingException ex) {
            throw new CMSException("can't write BodyPart to stream.", (Exception)ex);
        }
    }
    
    public Object getContent() {
        return this.bodyPart;
    }
}
