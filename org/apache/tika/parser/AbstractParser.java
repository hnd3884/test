package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;

public abstract class AbstractParser implements Parser
{
    private static final long serialVersionUID = 7186985395903074255L;
    
    @Deprecated
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata) throws IOException, SAXException, TikaException {
        this.parse(stream, handler, metadata, new ParseContext());
    }
}
