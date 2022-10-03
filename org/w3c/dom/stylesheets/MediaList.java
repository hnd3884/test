package org.w3c.dom.stylesheets;

import org.w3c.dom.DOMException;

public interface MediaList
{
    String getMediaText();
    
    void setMediaText(final String p0) throws DOMException;
    
    int getLength();
    
    String item(final int p0);
    
    void deleteMedium(final String p0) throws DOMException;
    
    void appendMedium(final String p0) throws DOMException;
}
