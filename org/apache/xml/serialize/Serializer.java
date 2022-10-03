package org.apache.xml.serialize;

import org.xml.sax.ContentHandler;
import java.io.IOException;
import org.xml.sax.DocumentHandler;
import java.io.Writer;
import java.io.OutputStream;

public interface Serializer
{
    void setOutputByteStream(final OutputStream p0);
    
    void setOutputCharStream(final Writer p0);
    
    void setOutputFormat(final OutputFormat p0);
    
    DocumentHandler asDocumentHandler() throws IOException;
    
    ContentHandler asContentHandler() throws IOException;
    
    DOMSerializer asDOMSerializer() throws IOException;
}
