package org.apache.axiom.attachments;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.axiom.om.OMException;
import java.io.InputStream;
import javax.activation.DataHandler;
import org.apache.axiom.mime.ContentType;

abstract class AttachmentsDelegate
{
    abstract ContentType getContentType();
    
    abstract DataHandler getDataHandler(final String p0);
    
    abstract void addDataHandler(final String p0, final DataHandler p1);
    
    abstract void removeDataHandler(final String p0);
    
    abstract InputStream getRootPartInputStream(final boolean p0) throws OMException;
    
    abstract String getRootPartContentID();
    
    abstract String getRootPartContentType();
    
    abstract IncomingAttachmentStreams getIncomingAttachmentStreams();
    
    abstract Set getContentIDs(final boolean p0);
    
    abstract Map getMap();
    
    abstract long getContentLength() throws IOException;
}
