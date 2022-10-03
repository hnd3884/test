package org.apache.tika.parser;

import java.util.ArrayList;
import org.apache.tika.mime.MediaType;
import java.util.Map;
import java.util.Iterator;
import org.apache.tika.utils.ServiceLoaderUtils;
import java.util.List;
import java.util.Collections;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;
import java.util.Collection;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.config.ServiceLoader;

public class DefaultParser extends CompositeParser
{
    private static final long serialVersionUID = 3612324825403757520L;
    private final transient ServiceLoader loader;
    
    public DefaultParser(final MediaTypeRegistry registry, final ServiceLoader loader, final Collection<Class<? extends Parser>> excludeParsers, final EncodingDetector encodingDetector) {
        super(registry, getDefaultParsers(loader, encodingDetector, excludeParsers));
        this.loader = loader;
    }
    
    public DefaultParser(final MediaTypeRegistry registry, final ServiceLoader loader, final Collection<Class<? extends Parser>> excludeParsers) {
        super(registry, getDefaultParsers(loader, new DefaultEncodingDetector(loader), excludeParsers));
        this.loader = loader;
    }
    
    public DefaultParser(final MediaTypeRegistry registry, final ServiceLoader loader, final EncodingDetector encodingDetector) {
        this(registry, loader, Collections.EMPTY_SET, encodingDetector);
    }
    
    public DefaultParser(final MediaTypeRegistry registry, final ServiceLoader loader) {
        this(registry, loader, Collections.EMPTY_SET, new DefaultEncodingDetector(loader));
    }
    
    public DefaultParser(final MediaTypeRegistry registry, final ClassLoader loader) {
        this(registry, new ServiceLoader(loader));
    }
    
    public DefaultParser(final ClassLoader loader) {
        this(MediaTypeRegistry.getDefaultRegistry(), new ServiceLoader(loader));
    }
    
    public DefaultParser(final MediaTypeRegistry registry) {
        this(registry, new ServiceLoader());
    }
    
    public DefaultParser() {
        this(MediaTypeRegistry.getDefaultRegistry());
    }
    
    private static List<Parser> getDefaultParsers(final ServiceLoader loader, final EncodingDetector encodingDetector, final Collection<Class<? extends Parser>> excludeParsers) {
        final List<Parser> parsers = loader.loadStaticServiceProviders(Parser.class, excludeParsers);
        if (encodingDetector != null) {
            for (final Parser p : parsers) {
                setEncodingDetector(p, encodingDetector);
            }
        }
        ServiceLoaderUtils.sortLoadedClasses(parsers);
        return parsers;
    }
    
    private static void setEncodingDetector(final Parser p, final EncodingDetector encodingDetector) {
        if (p instanceof AbstractEncodingDetectorParser) {
            ((AbstractEncodingDetectorParser)p).setEncodingDetector(encodingDetector);
        }
        else if (p instanceof CompositeParser) {
            for (final Parser child : ((CompositeParser)p).getAllComponentParsers()) {
                setEncodingDetector(child, encodingDetector);
            }
        }
        else if (p instanceof ParserDecorator) {
            setEncodingDetector(((ParserDecorator)p).getWrappedParser(), encodingDetector);
        }
    }
    
    @Override
    public Map<MediaType, Parser> getParsers(final ParseContext context) {
        final Map<MediaType, Parser> map = super.getParsers(context);
        if (this.loader != null) {
            final MediaTypeRegistry registry = this.getMediaTypeRegistry();
            final List<Parser> parsers = this.loader.loadDynamicServiceProviders(Parser.class);
            Collections.reverse(parsers);
            for (final Parser parser : parsers) {
                for (final MediaType type : parser.getSupportedTypes(context)) {
                    map.put(registry.normalize(type), parser);
                }
            }
        }
        return map;
    }
    
    @Override
    public List<Parser> getAllComponentParsers() {
        List<Parser> parsers = super.getAllComponentParsers();
        if (this.loader != null) {
            parsers = new ArrayList<Parser>(parsers);
            parsers.addAll(this.loader.loadDynamicServiceProviders(Parser.class));
        }
        return parsers;
    }
}
