package org.bouncycastle.mail.smime;

import java.io.IOException;
import javax.mail.MessagingException;
import org.bouncycastle.cms.CMSException;
import java.io.OutputStream;
import javax.mail.BodyPart;
import org.bouncycastle.cms.CMSProcessable;

public class CMSProcessableBodyPart implements CMSProcessable
{
    private BodyPart bodyPart;
    
    public CMSProcessableBodyPart(final BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        try {
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
