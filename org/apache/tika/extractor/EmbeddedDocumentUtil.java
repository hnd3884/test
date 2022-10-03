package org.apache.tika.extractor;

import java.util.Iterator;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParserDecorator;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.StatefulParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.parser.ParseContext;
import java.io.Serializable;

public class EmbeddedDocumentUtil implements Serializable
{
    private final ParseContext context;
    private final EmbeddedDocumentExtractor embeddedDocumentExtractor;
    private TikaConfig tikaConfig;
    private MimeTypes mimeTypes;
    private Detector detector;
    
    public EmbeddedDocumentUtil(final ParseContext context) {
        this.context = context;
        this.embeddedDocumentExtractor = getEmbeddedDocumentExtractor(context);
    }
    
    public static EmbeddedDocumentExtractor getEmbeddedDocumentExtractor(final ParseContext context) {
        EmbeddedDocumentExtractor extractor = context.get(EmbeddedDocumentExtractor.class);
        if (extractor == null) {
            final Parser embeddedParser = context.get(Parser.class);
            if (embeddedParser == null) {
                final TikaConfig tikaConfig = context.get(TikaConfig.class);
                if (tikaConfig == null) {
                    context.set((Class<AutoDetectParser>)Parser.class, new AutoDetectParser());
                }
                else {
                    context.set((Class<AutoDetectParser>)Parser.class, new AutoDetectParser(tikaConfig));
                }
            }
            extractor = new ParsingEmbeddedDocumentExtractor(context);
        }
        return extractor;
    }
    
    public static Parser getStatelessParser(final ParseContext context) {
        final Parser p = context.get(Parser.class);
        if (p == null) {
            return null;
        }
        if (p instanceof StatefulParser) {
            return ((StatefulParser)p).getWrappedParser();
        }
        return p;
    }
    
    public PasswordProvider getPasswordProvider() {
        return this.context.get(PasswordProvider.class);
    }
    
    public Detector getDetector() {
        final Detector localDetector = this.context.get(Detector.class);
        if (localDetector != null) {
            return localDetector;
        }
        if (this.detector != null) {
            return this.detector;
        }
        return this.detector = this.getTikaConfig().getDetector();
    }
    
    public MimeTypes getMimeTypes() {
        final MimeTypes localMimeTypes = this.context.get(MimeTypes.class);
        if (localMimeTypes != null) {
            return localMimeTypes;
        }
        if (this.mimeTypes != null) {
            return this.mimeTypes;
        }
        return this.mimeTypes = this.getTikaConfig().getMimeRepository();
    }
    
    public TikaConfig getTikaConfig() {
        if (this.tikaConfig == null) {
            this.tikaConfig = this.context.get(TikaConfig.class);
            if (this.tikaConfig == null) {
                this.tikaConfig = TikaConfig.getDefaultConfig();
            }
        }
        return this.tikaConfig;
    }
    
    public String getExtension(final TikaInputStream is, final Metadata metadata) {
        final String mimeString = metadata.get("Content-Type");
        final MimeTypes localMimeTypes = this.getMimeTypes();
        MimeType mimeType = null;
        boolean detected = false;
        if (mimeString != null) {
            try {
                mimeType = localMimeTypes.forName(mimeString);
            }
            catch (final MimeTypeException ex) {}
        }
        if (mimeType == null) {
            try {
                final MediaType mediaType = this.getDetector().detect((InputStream)is, metadata);
                mimeType = localMimeTypes.forName(mediaType.toString());
                detected = true;
                is.reset();
            }
            catch (final IOException | MimeTypeException ex2) {}
        }
        if (mimeType != null) {
            if (detected) {
                metadata.set("Content-Type", mimeType.toString());
            }
            return mimeType.getExtension();
        }
        return ".bin";
    }
    
    @Deprecated
    public TikaConfig getConfig() {
        TikaConfig config = this.context.get(TikaConfig.class);
        if (config == null) {
            config = TikaConfig.getDefaultConfig();
        }
        return config;
    }
    
    public static void recordException(final Throwable t, final Metadata m) {
        final String ex = ExceptionUtils.getFilteredStackTrace(t);
        m.add(TikaCoreProperties.TIKA_META_EXCEPTION_WARNING, ex);
    }
    
    public static void recordEmbeddedStreamException(final Throwable t, final Metadata m) {
        final String ex = ExceptionUtils.getFilteredStackTrace(t);
        m.add(TikaCoreProperties.TIKA_META_EXCEPTION_EMBEDDED_STREAM, ex);
    }
    
    public boolean shouldParseEmbedded(final Metadata m) {
        return this.getEmbeddedDocumentExtractor().shouldParseEmbedded(m);
    }
    
    private EmbeddedDocumentExtractor getEmbeddedDocumentExtractor() {
        return this.embeddedDocumentExtractor;
    }
    
    public void parseEmbedded(final InputStream inputStream, final ContentHandler handler, final Metadata metadata, final boolean outputHtml) throws IOException, SAXException {
        this.embeddedDocumentExtractor.parseEmbedded(inputStream, handler, metadata, outputHtml);
    }
    
    public static Parser tryToFindExistingLeafParser(final Class clazz, final ParseContext context) {
        Parser p = context.get(Parser.class);
        if (equals(p, clazz)) {
            return p;
        }
        Parser returnParser = null;
        if (p != null) {
            if (p instanceof ParserDecorator) {
                p = findInDecorated((ParserDecorator)p, clazz);
            }
            if (equals(p, clazz)) {
                return p;
            }
            if (p instanceof CompositeParser) {
                returnParser = findInComposite((CompositeParser)p, clazz, context);
            }
        }
        if (returnParser != null && equals(returnParser, clazz)) {
            return returnParser;
        }
        return null;
    }
    
    private static Parser findInDecorated(final ParserDecorator p, final Class clazz) {
        Parser candidate = p.getWrappedParser();
        if (equals(candidate, clazz)) {
            return candidate;
        }
        if (candidate instanceof ParserDecorator) {
            candidate = findInDecorated((ParserDecorator)candidate, clazz);
        }
        return candidate;
    }
    
    private static Parser findInComposite(final CompositeParser p, final Class clazz, final ParseContext context) {
        for (Parser candidate : p.getAllComponentParsers()) {
            if (equals(candidate, clazz)) {
                return candidate;
            }
            if (candidate instanceof ParserDecorator) {
                candidate = findInDecorated((ParserDecorator)candidate, clazz);
            }
            if (equals(candidate, clazz)) {
                return candidate;
            }
            if (candidate instanceof CompositeParser) {
                candidate = findInComposite((CompositeParser)candidate, clazz, context);
            }
            if (equals(candidate, clazz)) {
                return candidate;
            }
        }
        return null;
    }
    
    private static boolean equals(final Parser parser, final Class clazz) {
        return parser != null && parser.getClass().equals(clazz);
    }
}
