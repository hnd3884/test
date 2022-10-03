package org.apache.axiom.om.impl.builder;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.util.stax.xop.MimePartProvider;

public class AttachmentsMimePartProvider implements MimePartProvider, Detachable
{
    private final Attachments attachments;
    
    public AttachmentsMimePartProvider(final Attachments attachments) {
        this.attachments = attachments;
    }
    
    public boolean isLoaded(final String contentID) {
        return false;
    }
    
    public DataHandler getDataHandler(final String contentID) throws IOException {
        final DataHandler dh = this.attachments.getDataHandler(contentID);
        if (dh == null) {
            throw new IllegalArgumentException("No attachment found for content ID '" + contentID + "'");
        }
        return dh;
    }
    
    public void detach() {
        this.attachments.getAllContentIDs();
    }
}
