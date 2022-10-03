package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class AttachmentUnmarshallerImpl extends AttachmentUnmarshaller
{
    private final AttachmentSet attachments;
    
    public AttachmentUnmarshallerImpl(final AttachmentSet attachments) {
        this.attachments = attachments;
    }
    
    @Override
    public DataHandler getAttachmentAsDataHandler(final String cid) {
        final Attachment a = this.attachments.get(this.stripScheme(cid));
        if (a == null) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(cid));
        }
        return a.asDataHandler();
    }
    
    @Override
    public byte[] getAttachmentAsByteArray(final String cid) {
        final Attachment a = this.attachments.get(this.stripScheme(cid));
        if (a == null) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(cid));
        }
        return a.asByteArray();
    }
    
    private String stripScheme(String cid) {
        if (cid.startsWith("cid:")) {
            cid = cid.substring(4);
        }
        return cid;
    }
}
