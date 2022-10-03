package org.apache.xmlbeans.impl.soap;

import javax.xml.transform.Source;
import java.util.Iterator;
import org.w3c.dom.Document;

public abstract class SOAPPart implements Document
{
    public abstract SOAPEnvelope getEnvelope() throws SOAPException;
    
    public String getContentId() {
        final String[] as = this.getMimeHeader("Content-Id");
        if (as != null && as.length > 0) {
            return as[0];
        }
        return null;
    }
    
    public String getContentLocation() {
        final String[] as = this.getMimeHeader("Content-Location");
        if (as != null && as.length > 0) {
            return as[0];
        }
        return null;
    }
    
    public void setContentId(final String contentId) {
        this.setMimeHeader("Content-Id", contentId);
    }
    
    public void setContentLocation(final String contentLocation) {
        this.setMimeHeader("Content-Location", contentLocation);
    }
    
    public abstract void removeMimeHeader(final String p0);
    
    public abstract void removeAllMimeHeaders();
    
    public abstract String[] getMimeHeader(final String p0);
    
    public abstract void setMimeHeader(final String p0, final String p1);
    
    public abstract void addMimeHeader(final String p0, final String p1);
    
    public abstract Iterator getAllMimeHeaders();
    
    public abstract Iterator getMatchingMimeHeaders(final String[] p0);
    
    public abstract Iterator getNonMatchingMimeHeaders(final String[] p0);
    
    public abstract void setContent(final Source p0) throws SOAPException;
    
    public abstract Source getContent() throws SOAPException;
}
