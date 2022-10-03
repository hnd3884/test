package org.apache.axiom.attachments;

import javax.activation.DataHandler;

public interface Part
{
    DataHandler getDataHandler();
    
    long getSize();
    
    String getContentType();
    
    String getContentID();
    
    String getHeader(final String p0);
}
