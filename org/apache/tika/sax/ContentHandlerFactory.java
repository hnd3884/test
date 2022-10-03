package org.apache.tika.sax;

import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import org.xml.sax.ContentHandler;
import java.io.Serializable;

public interface ContentHandlerFactory extends Serializable
{
    ContentHandler getNewContentHandler();
    
    @Deprecated
    ContentHandler getNewContentHandler(final OutputStream p0, final String p1) throws UnsupportedEncodingException;
    
    ContentHandler getNewContentHandler(final OutputStream p0, final Charset p1);
}
