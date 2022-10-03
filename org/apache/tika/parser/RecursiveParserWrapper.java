package org.apache.tika.parser;

import org.xml.sax.Attributes;
import org.apache.tika.sax.SecureContentHandler;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.exception.CorruptedFileException;
import org.apache.tika.utils.ParserUtils;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.Set;

public class RecursiveParserWrapper extends ParserDecorator
{
    private static final long serialVersionUID = 9086536568120690938L;
    private final boolean catchEmbeddedExceptions;
    
    public RecursiveParserWrapper(final Parser wrappedParser) {
        this(wrappedParser, true);
    }
    
    public RecursiveParserWrapper(final Parser wrappedParser, final boolean catchEmbeddedExceptions) {
        super(wrappedParser);
        this.catchEmbeddedExceptions = catchEmbeddedExceptions;
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.getWrappedParser().getSupportedTypes(context);
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler recursiveParserWrapperHandler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        if (recursiveParserWrapperHandler instanceof AbstractRecursiveParserWrapperHandler) {
            final ParserState parserState = new ParserState((AbstractRecursiveParserWrapperHandler)recursiveParserWrapperHandler);
            final EmbeddedParserDecorator decorator = new EmbeddedParserDecorator(this.getWrappedParser(), "/", parserState);
            context.set(Parser.class, decorator);
            final ContentHandler localHandler = parserState.recursiveParserWrapperHandler.getNewContentHandler();
            final long started = System.currentTimeMillis();
            parserState.recursiveParserWrapperHandler.startDocument();
            final TemporaryResources tmp = new TemporaryResources();
            int writeLimit = -1;
            if (recursiveParserWrapperHandler instanceof BasicContentHandlerFactory) {
                writeLimit = ((BasicContentHandlerFactory)recursiveParserWrapperHandler).getWriteLimit();
            }
            try {
                final TikaInputStream tis = TikaInputStream.get(stream, tmp);
                final RecursivelySecureContentHandler secureContentHandler = new RecursivelySecureContentHandler(localHandler, tis, writeLimit);
                context.set(RecursivelySecureContentHandler.class, secureContentHandler);
                this.getWrappedParser().parse((InputStream)tis, secureContentHandler, metadata, context);
            }
            catch (final Throwable e) {
                if (!WriteLimitReachedException.isWriteLimitReached(e)) {
                    final String stackTrace = ExceptionUtils.getFilteredStackTrace(e);
                    metadata.add(TikaCoreProperties.CONTAINER_EXCEPTION, stackTrace);
                    throw e;
                }
                metadata.set(TikaCoreProperties.WRITE_LIMIT_REACHED, "true");
            }
            finally {
                tmp.dispose();
                final long elapsedMillis = System.currentTimeMillis() - started;
                metadata.set(TikaCoreProperties.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                parserState.recursiveParserWrapperHandler.endDocument(localHandler, metadata);
                parserState.recursiveParserWrapperHandler.endDocument();
            }
            return;
        }
        throw new IllegalStateException("ContentHandler must implement RecursiveParserWrapperHandler");
    }
    
    private String getResourceName(final Metadata metadata, final ParserState state) {
        String objectName = "";
        if (metadata.get("resourceName") != null) {
            objectName = metadata.get("resourceName");
        }
        else if (metadata.get("embeddedRelationshipId") != null) {
            objectName = metadata.get("embeddedRelationshipId");
        }
        else {
            objectName = "embedded-" + ++state.unknownCount;
        }
        objectName = FilenameUtils.getName(objectName);
        return objectName;
    }
    
    private class EmbeddedParserDecorator extends StatefulParser
    {
        private static final long serialVersionUID = 207648200464263337L;
        private final ParserState parserState;
        private String location;
        
        private EmbeddedParserDecorator(final Parser parser, final String location, final ParserState parseState) {
            super(parser);
            this.location = null;
            this.location = location;
            if (!this.location.endsWith("/")) {
                this.location += "/";
            }
            this.parserState = parseState;
        }
        
        @Override
        public void parse(final InputStream stream, final ContentHandler ignore, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
            if (this.parserState.recursiveParserWrapperHandler.hasHitMaximumEmbeddedResources()) {
                return;
            }
            final String objectName = RecursiveParserWrapper.this.getResourceName(metadata, this.parserState);
            final String objectLocation = this.location + objectName;
            metadata.add(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, objectLocation);
            final ContentHandler localHandler = this.parserState.recursiveParserWrapperHandler.getNewContentHandler();
            this.parserState.recursiveParserWrapperHandler.startEmbeddedDocument(localHandler, metadata);
            final Parser preContextParser = context.get(Parser.class);
            context.set((Class<EmbeddedParserDecorator>)Parser.class, new EmbeddedParserDecorator(this.getWrappedParser(), objectLocation, this.parserState));
            final long started = System.currentTimeMillis();
            final RecursivelySecureContentHandler secureContentHandler = context.get(RecursivelySecureContentHandler.class);
            final ContentHandler preContextHandler = secureContentHandler.handler;
            secureContentHandler.updateContentHandler(localHandler);
            try {
                super.parse(stream, secureContentHandler, metadata, context);
            }
            catch (final SAXException e) {
                if (WriteLimitReachedException.isWriteLimitReached(e)) {
                    metadata.add(TikaCoreProperties.WRITE_LIMIT_REACHED, "true");
                    throw e;
                }
                if (RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                    ParserUtils.recordParserFailure(this, e, metadata);
                    return;
                }
                throw e;
            }
            catch (final CorruptedFileException e2) {
                throw e2;
            }
            catch (final TikaException e3) {
                if (context.get(ZeroByteFileException.IgnoreZeroByteFileException.class) == null || !(e3 instanceof ZeroByteFileException)) {
                    if (!RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                        throw e3;
                    }
                    ParserUtils.recordParserFailure(this, e3, metadata);
                }
            }
            finally {
                context.set(Parser.class, preContextParser);
                secureContentHandler.updateContentHandler(preContextHandler);
                final long elapsedMillis = System.currentTimeMillis() - started;
                metadata.set(TikaCoreProperties.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                this.parserState.recursiveParserWrapperHandler.endEmbeddedDocument(localHandler, metadata);
            }
        }
    }
    
    private static class ParserState
    {
        private final AbstractRecursiveParserWrapperHandler recursiveParserWrapperHandler;
        private int unknownCount;
        
        private ParserState(final AbstractRecursiveParserWrapperHandler handler) {
            this.unknownCount = 0;
            this.recursiveParserWrapperHandler = handler;
        }
    }
    
    private static class RecursivelySecureContentHandler extends SecureContentHandler
    {
        private ContentHandler handler;
        private final int totalWriteLimit;
        private int totalChars;
        
        public RecursivelySecureContentHandler(final ContentHandler handler, final TikaInputStream stream, final int totalWriteLimit) {
            super(handler, stream);
            this.totalChars = 0;
            this.handler = handler;
            this.totalWriteLimit = totalWriteLimit;
        }
        
        public void updateContentHandler(final ContentHandler handler) {
            this.setContentHandler(handler);
            this.handler = handler;
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String name, final Attributes atts) throws SAXException {
            this.handler.startElement(uri, localName, name, atts);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String name) throws SAXException {
            this.handler.endElement(uri, localName, name);
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            if (this.totalWriteLimit < 0) {
                super.characters(ch, start, length);
                return;
            }
            final int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.characters(ch, start, availableLength);
            if (availableLength < length) {
                throw new WriteLimitReachedException(this.totalWriteLimit);
            }
        }
        
        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
            if (this.totalWriteLimit < 0) {
                super.ignorableWhitespace(ch, start, length);
                return;
            }
            final int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.ignorableWhitespace(ch, start, availableLength);
            if (availableLength < length) {
                throw new WriteLimitReachedException(this.totalWriteLimit);
            }
        }
    }
}
