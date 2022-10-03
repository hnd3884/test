package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import org.xml.sax.ContentHandler;
import java.util.Properties;
import java.io.Writer;
import java.io.OutputStream;

public interface Serializer
{
    void setOutputStream(final OutputStream p0);
    
    OutputStream getOutputStream();
    
    void setWriter(final Writer p0);
    
    Writer getWriter();
    
    void setOutputFormat(final Properties p0);
    
    Properties getOutputFormat();
    
    ContentHandler asContentHandler() throws IOException;
    
    DOMSerializer asDOMSerializer() throws IOException;
    
    boolean reset();
}
