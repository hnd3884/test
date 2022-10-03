package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.parser.multiple.FallbackParser;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.apache.tika.mime.MediaTypeRegistry;
import java.util.Collection;
import java.util.HashSet;
import org.apache.tika.mime.MediaType;
import java.util.Set;

public class ParserDecorator extends AbstractParser
{
    private static final long serialVersionUID = -3861669115439125268L;
    private final Parser parser;
    
    public ParserDecorator(final Parser parser) {
        this.parser = parser;
    }
    
    public static final Parser withTypes(final Parser parser, final Set<MediaType> types) {
        return new ParserDecorator(parser) {
            private static final long serialVersionUID = -7345051519565330731L;
            
            @Override
            public Set<MediaType> getSupportedTypes(final ParseContext context) {
                return types;
            }
            
            @Override
            public String getDecorationName() {
                return "With Types";
            }
        };
    }
    
    public static final Parser withoutTypes(final Parser parser, final Set<MediaType> excludeTypes) {
        return new ParserDecorator(parser) {
            private static final long serialVersionUID = 7979614774021768609L;
            
            @Override
            public Set<MediaType> getSupportedTypes(final ParseContext context) {
                final Set<MediaType> parserTypes = new HashSet<MediaType>(super.getSupportedTypes(context));
                parserTypes.removeAll(excludeTypes);
                return parserTypes;
            }
            
            @Override
            public String getDecorationName() {
                return "Without Types";
            }
        };
    }
    
    @Deprecated
    public static final Parser withFallbacks(final Collection<? extends Parser> parsers, final Set<MediaType> types) {
        final MediaTypeRegistry registry = MediaTypeRegistry.getDefaultRegistry();
        final Parser p = new FallbackParser(registry, AbstractMultipleParser.MetadataPolicy.KEEP_ALL, parsers);
        if (types == null || types.isEmpty()) {
            return p;
        }
        return withTypes(p, types);
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.parser.getSupportedTypes(context);
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        this.parser.parse(stream, handler, metadata, context);
    }
    
    public String getDecorationName() {
        return null;
    }
    
    public Parser getWrappedParser() {
        return this.parser;
    }
}
