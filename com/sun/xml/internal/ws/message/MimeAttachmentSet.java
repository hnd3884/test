package com.sun.xml.internal.ws.message;

import java.util.Iterator;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.Map;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.api.message.AttachmentSet;

public final class MimeAttachmentSet implements AttachmentSet
{
    private final MimeMultipartParser mpp;
    private Map<String, Attachment> atts;
    
    public MimeAttachmentSet(final MimeMultipartParser mpp) {
        this.atts = new HashMap<String, Attachment>();
        this.mpp = mpp;
    }
    
    @Nullable
    @Override
    public Attachment get(final String contentId) {
        Attachment att = this.atts.get(contentId);
        if (att != null) {
            return att;
        }
        try {
            att = this.mpp.getAttachmentPart(contentId);
            if (att != null) {
                this.atts.put(contentId, att);
            }
        }
        catch (final IOException e) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(contentId), e);
        }
        return att;
    }
    
    @Override
    public boolean isEmpty() {
        return this.atts.size() <= 0 && this.mpp.getAttachmentParts().isEmpty();
    }
    
    @Override
    public void add(final Attachment att) {
        this.atts.put(att.getContentId(), att);
    }
    
    @Override
    public Iterator<Attachment> iterator() {
        final Map<String, Attachment> attachments = this.mpp.getAttachmentParts();
        for (final Map.Entry<String, Attachment> att : attachments.entrySet()) {
            if (this.atts.get(att.getKey()) == null) {
                this.atts.put(att.getKey(), att.getValue());
            }
        }
        return this.atts.values().iterator();
    }
}
