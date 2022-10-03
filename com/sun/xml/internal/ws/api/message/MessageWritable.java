package com.sun.xml.internal.ws.api.message;

import javax.xml.ws.soap.MTOMFeature;
import java.io.IOException;
import java.io.OutputStream;
import com.oracle.webservices.internal.api.message.ContentType;

public interface MessageWritable
{
    ContentType getContentType();
    
    ContentType writeTo(final OutputStream p0) throws IOException;
    
    void setMTOMConfiguration(final MTOMFeature p0);
}
