package org.apache.tika.parser;

import org.xml.sax.SAXException;
import org.apache.tika.exception.WriteLimitReachedException;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.ParserUtils;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.Metadata;
import java.util.Collections;
import java.util.HashMap;
import org.apache.tika.mime.MediaType;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.tika.mime.MediaTypeRegistry;

public class CompositeParser extends AbstractParser
{
    private static final long serialVersionUID = 2192845797749627824L;
    private MediaTypeRegistry registry;
    private List<Parser> parsers;
    private Parser fallback;
    
    public CompositeParser(final MediaTypeRegistry registry, final List<Parser> parsers, final Collection<Class<? extends Parser>> excludeParsers) {
        this.fallback = new EmptyParser();
        if (excludeParsers == null || excludeParsers.isEmpty()) {
            this.parsers = parsers;
        }
        else {
            this.parsers = new ArrayList<Parser>();
            for (final Parser p : parsers) {
                if (!this.isExcluded(excludeParsers, p.getClass())) {
                    this.parsers.add(p);
                }
            }
        }
        this.registry = registry;
    }
    
    public CompositeParser(final MediaTypeRegistry registry, final List<Parser> parsers) {
        this(registry, parsers, null);
    }
    
    public CompositeParser(final MediaTypeRegistry registry, final Parser... parsers) {
        this(registry, Arrays.asList(parsers));
    }
    
    public CompositeParser() {
        this(new MediaTypeRegistry(), new Parser[0]);
    }
    
    public Map<MediaType, Parser> getParsers(final ParseContext context) {
        final Map<MediaType, Parser> map = new HashMap<MediaType, Parser>();
        for (final Parser parser : this.parsers) {
            for (final MediaType type : parser.getSupportedTypes(context)) {
                map.put(this.registry.normalize(type), parser);
            }
        }
        return map;
    }
    
    private boolean isExcluded(final Collection<Class<? extends Parser>> excludeParsers, final Class<? extends Parser> p) {
        return excludeParsers.contains(p) || this.assignableFrom(excludeParsers, p);
    }
    
    private boolean assignableFrom(final Collection<Class<? extends Parser>> excludeParsers, final Class<? extends Parser> p) {
        for (final Class<? extends Parser> e : excludeParsers) {
            if (e.isAssignableFrom(p)) {
                return true;
            }
        }
        return false;
    }
    
    public Map<MediaType, List<Parser>> findDuplicateParsers(final ParseContext context) {
        final Map<MediaType, Parser> types = new HashMap<MediaType, Parser>();
        final Map<MediaType, List<Parser>> duplicates = new HashMap<MediaType, List<Parser>>();
        for (final Parser parser : this.parsers) {
            for (final MediaType type : parser.getSupportedTypes(context)) {
                final MediaType canonicalType = this.registry.normalize(type);
                if (types.containsKey(canonicalType)) {
                    List<Parser> list = duplicates.get(canonicalType);
                    if (list == null) {
                        list = new ArrayList<Parser>();
                        list.add(types.get(canonicalType));
                        duplicates.put(canonicalType, list);
                    }
                    list.add(parser);
                }
                else {
                    types.put(canonicalType, parser);
                }
            }
        }
        return duplicates;
    }
    
    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }
    
    public void setMediaTypeRegistry(final MediaTypeRegistry registry) {
        this.registry = registry;
    }
    
    public List<Parser> getAllComponentParsers() {
        return Collections.unmodifiableList((List<? extends Parser>)this.parsers);
    }
    
    public Map<MediaType, Parser> getParsers() {
        return this.getParsers(new ParseContext());
    }
    
    public void setParsers(final Map<MediaType, Parser> parsers) {
        this.parsers = new ArrayList<Parser>(parsers.size());
        for (final Map.Entry<MediaType, Parser> entry : parsers.entrySet()) {
            this.parsers.add(ParserDecorator.withTypes(entry.getValue(), Collections.singleton(entry.getKey())));
        }
    }
    
    public Parser getFallback() {
        return this.fallback;
    }
    
    public void setFallback(final Parser fallback) {
        this.fallback = fallback;
    }
    
    protected Parser getParser(final Metadata metadata) {
        return this.getParser(metadata, new ParseContext());
    }
    
    protected Parser getParser(final Metadata metadata, final ParseContext context) {
        final Map<MediaType, Parser> map = this.getParsers(context);
        String contentTypeString = metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE);
        if (contentTypeString == null) {
            contentTypeString = metadata.get("Content-Type");
        }
        MediaType type = MediaType.parse(contentTypeString);
        if (type != null) {
            type = this.registry.normalize(type);
        }
        while (type != null) {
            final Parser parser = map.get(type);
            if (parser != null) {
                return parser;
            }
            type = this.registry.getSupertype(type);
        }
        return this.fallback;
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.getParsers(context).keySet();
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final Parser parser = this.getParser(metadata, context);
        final TemporaryResources tmp = new TemporaryResources();
        try {
            final TikaInputStream taggedStream = TikaInputStream.get(stream, tmp);
            final TaggedContentHandler taggedHandler = (handler != null) ? new TaggedContentHandler(handler) : null;
            ParserUtils.recordParserDetails(parser, metadata);
            try {
                parser.parse((InputStream)taggedStream, taggedHandler, metadata, context);
            }
            catch (final SecurityException e) {
                throw e;
            }
            catch (final IOException e2) {
                taggedStream.throwIfCauseOf((Throwable)e2);
                throw new TikaException("TIKA-198: Illegal IOException from " + parser, e2);
            }
            catch (final SAXException e3) {
                WriteLimitReachedException.throwIfWriteLimitReached(e3);
                if (taggedHandler != null) {
                    taggedHandler.throwIfCauseOf(e3);
                }
                throw new TikaException("TIKA-237: Illegal SAXException from " + parser, e3);
            }
            catch (final RuntimeException e4) {
                throw new TikaException("Unexpected RuntimeException from " + parser, e4);
            }
        }
        finally {
            tmp.dispose();
        }
    }
}
