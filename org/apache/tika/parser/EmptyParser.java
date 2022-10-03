package org.apache.tika.parser;

import org.xml.sax.SAXException;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import java.util.Collections;
import org.apache.tika.mime.MediaType;
import java.util.Set;

public class EmptyParser extends AbstractParser
{
    public static final EmptyParser INSTANCE;
    private static final long serialVersionUID = -4218649699095732123L;
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return Collections.emptySet();
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws SAXException {
        final XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
    }
    
    static {
        INSTANCE = new EmptyParser();
    }
}
