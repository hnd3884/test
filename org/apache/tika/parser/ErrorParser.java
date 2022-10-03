package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import java.util.Collections;
import org.apache.tika.mime.MediaType;
import java.util.Set;

public class ErrorParser extends AbstractParser
{
    public static final ErrorParser INSTANCE;
    private static final long serialVersionUID = 7727423956957641824L;
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return Collections.emptySet();
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws TikaException {
        throw new TikaException("Parse error");
    }
    
    static {
        INSTANCE = new ErrorParser();
    }
}
