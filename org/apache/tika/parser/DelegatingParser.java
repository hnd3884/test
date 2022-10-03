package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.Set;

public class DelegatingParser extends AbstractParser
{
    protected Parser getDelegateParser(final ParseContext context) {
        return context.get(Parser.class, EmptyParser.INSTANCE);
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.getDelegateParser(context).getSupportedTypes(context);
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws SAXException, IOException, TikaException {
        this.getDelegateParser(context).parse(stream, handler, metadata, context);
    }
}
