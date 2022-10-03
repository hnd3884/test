package org.apache.tika.extractor;

import java.io.File;
import org.apache.tika.io.TemporaryResources;
import java.util.Set;
import org.apache.tika.parser.StatefulParser;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.parser.Parser;

public class ParserContainerExtractor implements ContainerExtractor
{
    private static final long serialVersionUID = 2261131045580861514L;
    private final Parser parser;
    private final Detector detector;
    
    public ParserContainerExtractor() {
        this(TikaConfig.getDefaultConfig());
    }
    
    public ParserContainerExtractor(final TikaConfig config) {
        this(new AutoDetectParser(config), new DefaultDetector(config.getMimeRepository()));
    }
    
    public ParserContainerExtractor(final Parser parser, final Detector detector) {
        this.parser = parser;
        this.detector = detector;
    }
    
    @Override
    public boolean isSupported(final TikaInputStream input) throws IOException {
        final MediaType type = this.detector.detect((InputStream)input, new Metadata());
        return this.parser.getSupportedTypes(new ParseContext()).contains(type);
    }
    
    @Override
    public void extract(final TikaInputStream stream, final ContainerExtractor recurseExtractor, final EmbeddedResourceHandler handler) throws IOException, TikaException {
        final ParseContext context = new ParseContext();
        context.set((Class<RecursiveParser>)Parser.class, new RecursiveParser(this.parser, recurseExtractor, handler));
        try {
            this.parser.parse((InputStream)stream, new DefaultHandler(), new Metadata(), context);
        }
        catch (final SAXException e) {
            throw new TikaException("Unexpected SAX exception", e);
        }
    }
    
    private class RecursiveParser extends StatefulParser
    {
        private final ContainerExtractor extractor;
        private final EmbeddedResourceHandler handler;
        
        private RecursiveParser(final Parser statelessParser, final ContainerExtractor extractor, final EmbeddedResourceHandler handler) {
            super(statelessParser);
            this.extractor = extractor;
            this.handler = handler;
        }
        
        @Override
        public Set<MediaType> getSupportedTypes(final ParseContext context) {
            return ParserContainerExtractor.this.parser.getSupportedTypes(context);
        }
        
        @Override
        public void parse(final InputStream stream, final ContentHandler ignored, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
            final TemporaryResources tmp = new TemporaryResources();
            try {
                final TikaInputStream tis = TikaInputStream.get(stream, tmp);
                final String filename = metadata.get("resourceName");
                final MediaType type = ParserContainerExtractor.this.detector.detect((InputStream)tis, metadata);
                if (this.extractor == null) {
                    this.handler.handle(filename, type, (InputStream)tis);
                }
                else {
                    final File file = tis.getFile();
                    try (final InputStream input = (InputStream)TikaInputStream.get(file)) {
                        this.handler.handle(filename, type, input);
                    }
                    this.extractor.extract(tis, this.extractor, this.handler);
                }
            }
            finally {
                tmp.dispose();
            }
        }
    }
}
