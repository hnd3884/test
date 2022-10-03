package org.apache.tika.parser.multiple;

import org.apache.tika.io.TemporaryResources;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.sax.ContentHandlerFactory;
import java.io.InputStream;
import org.xml.sax.ContentHandler;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.apache.tika.utils.ParserUtils;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.Metadata;
import java.util.Iterator;
import org.apache.tika.parser.ParseContext;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.tika.config.Param;
import java.util.Map;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.parser.Parser;
import java.util.Collection;
import org.apache.tika.parser.AbstractParser;

public abstract class AbstractMultipleParser extends AbstractParser
{
    protected static final String METADATA_POLICY_CONFIG_KEY = "metadataPolicy";
    private static final long serialVersionUID = 5383668090329836559L;
    private final MetadataPolicy policy;
    private final Collection<? extends Parser> parsers;
    private final Set<MediaType> offeredTypes;
    private MediaTypeRegistry registry;
    
    public AbstractMultipleParser(final MediaTypeRegistry registry, final Collection<? extends Parser> parsers, final Map<String, Param> params) {
        this(registry, getMetadataPolicy(params), parsers);
    }
    
    public AbstractMultipleParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Parser... parsers) {
        this(registry, policy, Arrays.asList(parsers));
    }
    
    public AbstractMultipleParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Collection<? extends Parser> parsers) {
        this.policy = policy;
        this.parsers = parsers;
        this.registry = registry;
        this.offeredTypes = new HashSet<MediaType>();
        for (final Parser parser : parsers) {
            this.offeredTypes.addAll(parser.getSupportedTypes(new ParseContext()));
        }
    }
    
    protected static MetadataPolicy getMetadataPolicy(final Map<String, Param> params) {
        if (params.containsKey("metadataPolicy")) {
            return params.get("metadataPolicy").getValue();
        }
        throw new IllegalArgumentException("Required parameter 'metadataPolicy' not supplied");
    }
    
    protected static Metadata mergeMetadata(final Metadata newMetadata, final Metadata lastMetadata, final MetadataPolicy policy) {
        if (policy == MetadataPolicy.DISCARD_ALL) {
            return newMetadata;
        }
        for (final String n : lastMetadata.names()) {
            if (!n.equals(TikaCoreProperties.TIKA_PARSED_BY.getName())) {
                if (!n.equals(ParserUtils.EMBEDDED_PARSER.getName())) {
                    if (!n.equals(TikaCoreProperties.EMBEDDED_EXCEPTION.getName())) {
                        final String[] newVals = newMetadata.getValues(n);
                        final String[] oldVals = lastMetadata.getValues(n);
                        if (newVals == null || newVals.length == 0) {
                            for (final String val : oldVals) {
                                newMetadata.add(n, val);
                            }
                        }
                        else if (!Arrays.deepEquals(oldVals, newVals)) {
                            switch (policy) {
                                case FIRST_WINS: {
                                    newMetadata.remove(n);
                                    for (final String val : oldVals) {
                                        newMetadata.add(n, val);
                                    }
                                }
                                case KEEP_ALL: {
                                    final List<String> vals = new ArrayList<String>(Arrays.asList(oldVals));
                                    newMetadata.remove(n);
                                    for (final String oldVal : oldVals) {
                                        newMetadata.add(n, oldVal);
                                    }
                                    for (final String newVal : newVals) {
                                        if (!vals.contains(newVal)) {
                                            newMetadata.add(n, newVal);
                                            vals.add(newVal);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return newMetadata;
    }
    
    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }
    
    public void setMediaTypeRegistry(final MediaTypeRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.offeredTypes;
    }
    
    public MetadataPolicy getMetadataPolicy() {
        return this.policy;
    }
    
    public List<Parser> getAllParsers() {
        return Collections.unmodifiableList((List<? extends Parser>)new ArrayList<Parser>(this.parsers));
    }
    
    protected void parserPrepare(final Parser parser, final Metadata metadata, final ParseContext context) {
    }
    
    protected abstract boolean parserCompleted(final Parser p0, final Metadata p1, final ContentHandler p2, final ParseContext p3, final Exception p4);
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        this.parse(stream, handler, null, metadata, context);
    }
    
    @Deprecated
    public void parse(final InputStream stream, final ContentHandlerFactory handlers, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        this.parse(stream, null, handlers, metadata, context);
    }
    
    private void parse(final InputStream stream, ContentHandler handler, final ContentHandlerFactory handlerFactory, final Metadata originalMetadata, final ParseContext context) throws IOException, SAXException, TikaException {
        Metadata metadata;
        Metadata lastMetadata = metadata = ParserUtils.cloneMetadata(originalMetadata);
        final TemporaryResources tmp = new TemporaryResources();
        try {
            InputStream taggedStream = ParserUtils.ensureStreamReReadable(stream, tmp);
            for (final Parser p : this.parsers) {
                if (handlerFactory != null) {
                    handler = handlerFactory.getNewContentHandler();
                }
                ParserUtils.recordParserDetails(p, originalMetadata);
                metadata = ParserUtils.cloneMetadata(originalMetadata);
                this.parserPrepare(p, metadata, context);
                Exception failure = null;
                try {
                    p.parse(taggedStream, handler, metadata, context);
                }
                catch (final Exception e) {
                    ParserUtils.recordParserFailure(p, e, originalMetadata);
                    ParserUtils.recordParserFailure(p, e, metadata);
                    failure = e;
                }
                final boolean tryNext = this.parserCompleted(p, metadata, handler, context, failure);
                metadata = mergeMetadata(metadata, lastMetadata, this.policy);
                if (!tryNext) {
                    if (failure == null) {
                        break;
                    }
                    if (failure instanceof IOException) {
                        throw (IOException)failure;
                    }
                    if (failure instanceof SAXException) {
                        throw (SAXException)failure;
                    }
                    if (failure instanceof TikaException) {
                        throw (TikaException)failure;
                    }
                    throw new TikaException("Unexpected RuntimeException from " + p, failure);
                }
                else {
                    lastMetadata = ParserUtils.cloneMetadata(metadata);
                    taggedStream = ParserUtils.streamResetForReRead(taggedStream, tmp);
                }
            }
        }
        finally {
            tmp.dispose();
        }
        for (final String n : metadata.names()) {
            originalMetadata.remove(n);
            for (final String val : metadata.getValues(n)) {
                originalMetadata.add(n, val);
            }
        }
    }
    
    public enum MetadataPolicy
    {
        DISCARD_ALL, 
        FIRST_WINS, 
        LAST_WINS, 
        KEEP_ALL;
    }
}
