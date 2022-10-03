package org.apache.tika.parser.external;

import org.apache.tika.parser.Parser;
import java.util.List;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;

public class CompositeExternalParser extends CompositeParser
{
    private static final long serialVersionUID = 6962436916649024024L;
    
    public CompositeExternalParser() throws IOException, TikaException {
        this(new MediaTypeRegistry());
    }
    
    public CompositeExternalParser(final MediaTypeRegistry registry) throws IOException, TikaException {
        super(registry, (List<Parser>)ExternalParsersFactory.create());
    }
}
