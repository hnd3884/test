package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;

public abstract class AttachmentPart
{
    public abstract int getSize() throws SOAPException;
    
    public abstract void clearContent();
    
    public abstract Object getContent() throws SOAPException;
    
    public abstract void setContent(final Object p0, final String p1);
    
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
    
    public String getContentType() {
        final String[] as = this.getMimeHeader("Content-Type");
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
    
    public void setContentType(final String contentType) {
        this.setMimeHeader("Content-Type", contentType);
    }
    
    public abstract void removeMimeHeader(final String p0);
    
    public abstract void removeAllMimeHeaders();
    
    public abstract String[] getMimeHeader(final String p0);
    
    public abstract void setMimeHeader(final String p0, final String p1);
    
    public abstract void addMimeHeader(final String p0, final String p1);
    
    public abstract Iterator getAllMimeHeaders();
    
    public abstract Iterator getMatchingMimeHeaders(final String[] p0);
    
    public abstract Iterator getNonMatchingMimeHeaders(final String[] p0);
}
