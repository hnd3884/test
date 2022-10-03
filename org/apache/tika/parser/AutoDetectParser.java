package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.xml.sax.SAXException;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.sax.SecureContentHandler;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;

public class AutoDetectParser extends CompositeParser
{
    private static final long serialVersionUID = 6110455808615143122L;
    private Detector detector;
    
    public AutoDetectParser() {
        this(TikaConfig.getDefaultConfig());
    }
    
    public AutoDetectParser(final Detector detector) {
        this(TikaConfig.getDefaultConfig());
        this.setDetector(detector);
    }
    
    public AutoDetectParser(final Parser... parsers) {
        this(new DefaultDetector(), parsers);
    }
    
    public AutoDetectParser(final Detector detector, final Parser... parsers) {
        super(MediaTypeRegistry.getDefaultRegistry(), parsers);
        this.setDetector(detector);
    }
    
    public AutoDetectParser(final TikaConfig config) {
        super(config.getMediaTypeRegistry(), new Parser[] { config.getParser() });
        this.setDetector(config.getDetector());
    }
    
    public Detector getDetector() {
        return this.detector;
    }
    
    public void setDetector(final Detector detector) {
        this.detector = detector;
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final TemporaryResources tmp = new TemporaryResources();
        try {
            final TikaInputStream tis = TikaInputStream.get(stream, tmp);
            final MediaType type = this.detector.detect((InputStream)tis, metadata);
            if (metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE) == null || !metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE).equals(type.toString())) {
                metadata.set("Content-Type", type.toString());
            }
            if (tis.getOpenContainer() == null) {
                tis.mark(1);
                if (tis.read() == -1) {
                    throw new ZeroByteFileException("InputStream must have > 0 bytes");
                }
                tis.reset();
            }
            final SecureContentHandler sch = (handler != null) ? new SecureContentHandler(handler, tis) : null;
            if (context.get(EmbeddedDocumentExtractor.class) == null) {
                final Parser p = context.get(Parser.class);
                if (p == null) {
                    context.set(Parser.class, this);
                }
                context.set((Class<ParsingEmbeddedDocumentExtractor>)EmbeddedDocumentExtractor.class, new ParsingEmbeddedDocumentExtractor(context));
            }
            try {
                super.parse((InputStream)tis, sch, metadata, context);
            }
            catch (final SAXException e) {
                sch.throwIfCauseOf(e);
                throw e;
            }
        }
        finally {
            tmp.dispose();
        }
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata) throws IOException, SAXException, TikaException {
        final ParseContext context = new ParseContext();
        context.set(Parser.class, this);
        this.parse(stream, handler, metadata, context);
    }
}
