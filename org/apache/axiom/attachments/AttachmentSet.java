package org.apache.axiom.attachments;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.axiom.om.OMException;
import java.io.InputStream;
import javax.activation.DataHandler;
import org.apache.axiom.mime.ContentType;
import java.util.LinkedHashMap;
import java.util.Map;

class AttachmentSet extends AttachmentsDelegate
{
    private final Map attachmentsMap;
    
    AttachmentSet() {
        this.attachmentsMap = new LinkedHashMap();
    }
    
    @Override
    ContentType getContentType() {
        return null;
    }
    
    @Override
    DataHandler getDataHandler(final String contentID) {
        return this.attachmentsMap.get(contentID);
    }
    
    @Override
    void addDataHandler(final String contentID, final DataHandler dataHandler) {
        this.attachmentsMap.put(contentID, dataHandler);
    }
    
    @Override
    void removeDataHandler(final String blobContentID) {
        this.attachmentsMap.remove(blobContentID);
    }
    
    @Override
    InputStream getRootPartInputStream(final boolean preserve) throws OMException {
        throw new OMException("Invalid operation. Attachments are created programatically.");
    }
    
    @Override
    String getRootPartContentID() {
        return null;
    }
    
    @Override
    String getRootPartContentType() {
        throw new OMException("The attachments map was created programatically. Unsupported operation.");
    }
    
    @Override
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        throw new IllegalStateException("The attachments map was created programatically. No streams are available.");
    }
    
    @Override
    Set getContentIDs(final boolean fetchAll) {
        return this.attachmentsMap.keySet();
    }
    
    @Override
    Map getMap() {
        return Collections.unmodifiableMap((Map<?, ?>)this.attachmentsMap);
    }
    
    @Override
    long getContentLength() throws IOException {
        return -1L;
    }
}
