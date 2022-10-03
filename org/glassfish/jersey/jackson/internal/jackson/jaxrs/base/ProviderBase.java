package org.glassfish.jersey.jackson.internal.jackson.jaxrs.base;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.Writer;
import java.io.Reader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.ws.rs.core.NoContentException;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectReaderModifier;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectReaderInjector;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.InputStream;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterModifier;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.core.JsonEncoding;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.JaxRSFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.AnnotationBundleKey;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.util.LRUMap;
import java.util.HashMap;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.util.ClassKey;
import java.util.HashSet;
import com.fasterxml.jackson.core.Versioned;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.MapperConfiguratorBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ProviderBase<THIS extends ProviderBase<THIS, MAPPER, EP_CONFIG, MAPPER_CONFIG>, MAPPER extends ObjectMapper, EP_CONFIG extends EndpointConfigBase<EP_CONFIG>, MAPPER_CONFIG extends MapperConfiguratorBase<MAPPER_CONFIG, MAPPER>> implements MessageBodyReader<Object>, MessageBodyWriter<Object>, Versioned
{
    public static final String HEADER_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    protected static final HashSet<ClassKey> DEFAULT_UNTOUCHABLES;
    public static final Class<?>[] DEFAULT_UNREADABLES;
    public static final Class<?>[] DEFAULT_UNWRITABLES;
    protected static final int JAXRS_FEATURE_DEFAULTS;
    protected final MAPPER_CONFIG _mapperConfig;
    protected HashMap<ClassKey, Boolean> _cfgCustomUntouchables;
    protected boolean _cfgCheckCanSerialize;
    protected boolean _cfgCheckCanDeserialize;
    protected int _jaxRSFeatures;
    protected Class<?> _defaultReadView;
    protected Class<?> _defaultWriteView;
    public static final HashSet<ClassKey> _untouchables;
    public static final Class<?>[] _unreadableClasses;
    public static final Class<?>[] _unwritableClasses;
    protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _readers;
    protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _writers;
    protected final AtomicReference<IOException> _noContentExceptionRef;
    
    protected ProviderBase(final MAPPER_CONFIG mconfig) {
        this._cfgCheckCanSerialize = false;
        this._cfgCheckCanDeserialize = false;
        this._readers = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);
        this._writers = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);
        this._noContentExceptionRef = new AtomicReference<IOException>();
        this._mapperConfig = mconfig;
        this._jaxRSFeatures = ProviderBase.JAXRS_FEATURE_DEFAULTS;
    }
    
    @Deprecated
    protected ProviderBase() {
        this._cfgCheckCanSerialize = false;
        this._cfgCheckCanDeserialize = false;
        this._readers = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);
        this._writers = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);
        this._noContentExceptionRef = new AtomicReference<IOException>();
        this._mapperConfig = null;
        this._jaxRSFeatures = ProviderBase.JAXRS_FEATURE_DEFAULTS;
    }
    
    public void checkCanDeserialize(final boolean state) {
        this._cfgCheckCanDeserialize = state;
    }
    
    public void checkCanSerialize(final boolean state) {
        this._cfgCheckCanSerialize = state;
    }
    
    public void addUntouchable(final Class<?> type) {
        if (this._cfgCustomUntouchables == null) {
            this._cfgCustomUntouchables = new HashMap<ClassKey, Boolean>();
        }
        this._cfgCustomUntouchables.put(new ClassKey(type), Boolean.TRUE);
    }
    
    public void removeUntouchable(final Class<?> type) {
        if (this._cfgCustomUntouchables == null) {
            this._cfgCustomUntouchables = new HashMap<ClassKey, Boolean>();
        }
        this._cfgCustomUntouchables.put(new ClassKey(type), Boolean.FALSE);
    }
    
    public void setAnnotationsToUse(final Annotations[] annotationsToUse) {
        this._mapperConfig.setAnnotationsToUse(annotationsToUse);
    }
    
    public void setMapper(final MAPPER m) {
        ((MapperConfiguratorBase<IMPL, MAPPER>)this._mapperConfig).setMapper(m);
    }
    
    public THIS setDefaultReadView(final Class<?> view) {
        this._defaultReadView = view;
        return this._this();
    }
    
    public THIS setDefaultWriteView(final Class<?> view) {
        this._defaultWriteView = view;
        return this._this();
    }
    
    public THIS setDefaultView(final Class<?> view) {
        this._defaultWriteView = view;
        this._defaultReadView = view;
        return this._this();
    }
    
    public THIS configure(final JaxRSFeature feature, final boolean state) {
        return state ? this.enable(feature) : this.disable(feature);
    }
    
    public THIS enable(final JaxRSFeature feature) {
        this._jaxRSFeatures |= feature.getMask();
        return this._this();
    }
    
    public THIS enable(final JaxRSFeature first, final JaxRSFeature... f2) {
        this._jaxRSFeatures |= first.getMask();
        for (final JaxRSFeature f3 : f2) {
            this._jaxRSFeatures |= f3.getMask();
        }
        return this._this();
    }
    
    public THIS disable(final JaxRSFeature feature) {
        this._jaxRSFeatures &= ~feature.getMask();
        return this._this();
    }
    
    public THIS disable(final JaxRSFeature first, final JaxRSFeature... f2) {
        this._jaxRSFeatures &= ~first.getMask();
        for (final JaxRSFeature f3 : f2) {
            this._jaxRSFeatures &= ~f3.getMask();
        }
        return this._this();
    }
    
    public boolean isEnabled(final JaxRSFeature f) {
        return (this._jaxRSFeatures & f.getMask()) != 0x0;
    }
    
    public THIS configure(final DeserializationFeature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this._this();
    }
    
    public THIS enable(final DeserializationFeature f) {
        this._mapperConfig.configure(f, true);
        return this._this();
    }
    
    public THIS disable(final DeserializationFeature f) {
        this._mapperConfig.configure(f, false);
        return this._this();
    }
    
    public THIS configure(final SerializationFeature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this._this();
    }
    
    public THIS enable(final SerializationFeature f) {
        this._mapperConfig.configure(f, true);
        return this._this();
    }
    
    public THIS disable(final SerializationFeature f) {
        this._mapperConfig.configure(f, false);
        return this._this();
    }
    
    public THIS enable(final JsonParser.Feature f) {
        this._mapperConfig.configure(f, true);
        return this._this();
    }
    
    public THIS enable(final JsonGenerator.Feature f) {
        this._mapperConfig.configure(f, true);
        return this._this();
    }
    
    public THIS disable(final JsonParser.Feature f) {
        this._mapperConfig.configure(f, false);
        return this._this();
    }
    
    public THIS disable(final JsonGenerator.Feature f) {
        this._mapperConfig.configure(f, false);
        return this._this();
    }
    
    public THIS configure(final JsonParser.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this._this();
    }
    
    public THIS configure(final JsonGenerator.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this._this();
    }
    
    protected boolean hasMatchingMediaTypeForReading(final MediaType mediaType) {
        return this.hasMatchingMediaType(mediaType);
    }
    
    protected boolean hasMatchingMediaTypeForWriting(final MediaType mediaType) {
        return this.hasMatchingMediaType(mediaType);
    }
    
    protected abstract boolean hasMatchingMediaType(final MediaType p0);
    
    protected abstract MAPPER _locateMapperViaProvider(final Class<?> p0, final MediaType p1);
    
    protected EP_CONFIG _configForReading(final MAPPER mapper, final Annotation[] annotations, final Class<?> defaultView) {
        ObjectReader r;
        if (defaultView != null) {
            r = mapper.readerWithView((Class)defaultView);
        }
        else {
            r = mapper.reader();
        }
        return this._configForReading(r, annotations);
    }
    
    protected EP_CONFIG _configForWriting(final MAPPER mapper, final Annotation[] annotations, final Class<?> defaultView) {
        ObjectWriter w;
        if (defaultView != null) {
            w = mapper.writerWithView((Class)defaultView);
        }
        else {
            w = mapper.writer();
        }
        return this._configForWriting(w, annotations);
    }
    
    protected abstract EP_CONFIG _configForReading(final ObjectReader p0, final Annotation[] p1);
    
    protected abstract EP_CONFIG _configForWriting(final ObjectWriter p0, final Annotation[] p1);
    
    public long getSize(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (!this.hasMatchingMediaType(mediaType)) {
            return false;
        }
        final Boolean customUntouchable = this._findCustomUntouchable(type);
        if (customUntouchable != null) {
            return !customUntouchable;
        }
        if (this._isIgnorableForWriting(new ClassKey(type))) {
            return false;
        }
        for (final Class<?> cls : ProviderBase._unwritableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        return !this._cfgCheckCanSerialize || this.locateMapper(type, mediaType).canSerialize((Class)type);
    }
    
    public void writeTo(Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final EP_CONFIG endpoint = this._endpointForWriting(value, type, genericType, annotations, mediaType, httpHeaders);
        this._modifyHeaders(value, type, genericType, annotations, httpHeaders, endpoint);
        ObjectWriter writer = endpoint.getWriter();
        final JsonEncoding enc = this.findEncoding(mediaType, httpHeaders);
        final JsonGenerator g = this._createGenerator(writer, entityStream, enc);
        boolean ok = false;
        try {
            if (writer.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                g.useDefaultPrettyPrinter();
            }
            JavaType rootType = null;
            if (genericType != null && value != null && !(genericType instanceof Class)) {
                final TypeFactory typeFactory = writer.getTypeFactory();
                final JavaType baseType = typeFactory.constructType(genericType);
                rootType = typeFactory.constructSpecializedType(baseType, (Class)type);
                if (rootType.getRawClass() == Object.class) {
                    rootType = null;
                }
            }
            if (rootType != null) {
                writer = writer.forType(rootType);
            }
            value = endpoint.modifyBeforeWrite(value);
            final ObjectWriterModifier mod = ObjectWriterInjector.getAndClear();
            if (mod != null) {
                writer = mod.modify(endpoint, httpHeaders, value, writer, g);
            }
            writer.writeValue(g, value);
            ok = true;
        }
        finally {
            if (ok) {
                g.close();
            }
            else {
                try {
                    g.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    protected JsonEncoding findEncoding(final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders) {
        return JsonEncoding.UTF8;
    }
    
    protected void _modifyHeaders(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MultivaluedMap<String, Object> httpHeaders, final EP_CONFIG endpoint) throws IOException {
        if (this.isEnabled(JaxRSFeature.ADD_NO_SNIFF_HEADER)) {
            httpHeaders.add((Object)"X-Content-Type-Options", (Object)"nosniff");
        }
    }
    
    protected JsonGenerator _createGenerator(final ObjectWriter writer, final OutputStream rawStream, final JsonEncoding enc) throws IOException {
        final JsonGenerator g = writer.getFactory().createGenerator(rawStream, enc);
        g.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        return g;
    }
    
    protected EP_CONFIG _endpointForWriting(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders) {
        if (!this.isEnabled(JaxRSFeature.CACHE_ENDPOINT_WRITERS)) {
            return this._configForWriting(this.locateMapper(type, mediaType), annotations, this._defaultWriteView);
        }
        final AnnotationBundleKey key = new AnnotationBundleKey(annotations, type);
        EP_CONFIG endpoint;
        synchronized (this._writers) {
            endpoint = this._writers.get(key);
        }
        if (endpoint == null) {
            final MAPPER mapper = this.locateMapper(type, mediaType);
            endpoint = this._configForWriting(mapper, annotations, this._defaultWriteView);
            synchronized (this._writers) {
                this._writers.put(key.immutableKey(), endpoint);
            }
        }
        return endpoint;
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (!this.hasMatchingMediaType(mediaType)) {
            return false;
        }
        final Boolean customUntouchable = this._findCustomUntouchable(type);
        if (customUntouchable != null) {
            return !customUntouchable;
        }
        if (this._isIgnorableForReading(new ClassKey(type))) {
            return false;
        }
        for (final Class<?> cls : ProviderBase._unreadableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        if (this._cfgCheckCanDeserialize) {
            if (this._isSpecialReadable(type)) {
                return true;
            }
            final ObjectMapper mapper = this.locateMapper(type, mediaType);
            if (!mapper.canDeserialize(mapper.constructType((Type)type))) {
                return false;
            }
        }
        return true;
    }
    
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final EP_CONFIG endpoint = this._endpointForReading(type, genericType, annotations, mediaType, httpHeaders);
        ObjectReader reader = endpoint.getReader();
        final JsonParser p = this._createParser(reader, entityStream);
        if (p == null || p.nextToken() == null) {
            if (JaxRSFeature.ALLOW_EMPTY_INPUT.enabledIn(this._jaxRSFeatures)) {
                return null;
            }
            IOException fail = this._noContentExceptionRef.get();
            if (fail == null) {
                fail = this._createNoContentException();
            }
            throw fail;
        }
        else {
            final Class<?> rawType = type;
            if (rawType == JsonParser.class) {
                return p;
            }
            final TypeFactory tf = reader.getTypeFactory();
            final JavaType resolvedType = tf.constructType(genericType);
            final boolean multiValued = rawType == MappingIterator.class;
            if (multiValued) {
                final JavaType[] contents = tf.findTypeParameters(resolvedType, (Class)MappingIterator.class);
                final JavaType valueType = (contents == null || contents.length == 0) ? tf.constructType((Type)Object.class) : contents[0];
                reader = reader.forType(valueType);
            }
            else {
                reader = reader.forType(resolvedType);
            }
            final ObjectReaderModifier mod = ObjectReaderInjector.getAndClear();
            if (mod != null) {
                reader = mod.modify(endpoint, httpHeaders, resolvedType, reader, p);
            }
            if (multiValued) {
                return reader.readValues(p);
            }
            return reader.readValue(p);
        }
    }
    
    protected JsonParser _createParser(final ObjectReader reader, final InputStream rawStream) throws IOException {
        final JsonParser p = reader.getFactory().createParser(rawStream);
        p.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return p;
    }
    
    protected EP_CONFIG _endpointForReading(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders) {
        if (!this.isEnabled(JaxRSFeature.CACHE_ENDPOINT_READERS)) {
            return this._configForReading(this.locateMapper(type, mediaType), annotations, this._defaultReadView);
        }
        final AnnotationBundleKey key = new AnnotationBundleKey(annotations, type);
        EP_CONFIG endpoint;
        synchronized (this._readers) {
            endpoint = this._readers.get(key);
        }
        if (endpoint == null) {
            final MAPPER mapper = this.locateMapper(type, mediaType);
            endpoint = this._configForReading(mapper, annotations, this._defaultReadView);
            synchronized (this._readers) {
                this._readers.put(key.immutableKey(), endpoint);
            }
        }
        return endpoint;
    }
    
    public MAPPER locateMapper(final Class<?> type, final MediaType mediaType) {
        if (this.isEnabled(JaxRSFeature.DYNAMIC_OBJECT_MAPPER_LOOKUP)) {
            MAPPER m = this._locateMapperViaProvider(type, mediaType);
            if (m == null) {
                m = ((MapperConfiguratorBase<IMPL, MAPPER>)this._mapperConfig).getConfiguredMapper();
                if (m == null) {
                    m = ((MapperConfiguratorBase<IMPL, MAPPER>)this._mapperConfig).getDefaultMapper();
                }
            }
            return m;
        }
        MAPPER m = ((MapperConfiguratorBase<IMPL, MAPPER>)this._mapperConfig).getConfiguredMapper();
        if (m == null) {
            m = this._locateMapperViaProvider(type, mediaType);
            if (m == null) {
                m = ((MapperConfiguratorBase<IMPL, MAPPER>)this._mapperConfig).getDefaultMapper();
            }
        }
        return m;
    }
    
    protected boolean _isSpecialReadable(final Class<?> type) {
        return JsonParser.class == type;
    }
    
    protected boolean _isIgnorableForReading(final ClassKey typeKey) {
        return ProviderBase._untouchables.contains(typeKey);
    }
    
    protected boolean _isIgnorableForWriting(final ClassKey typeKey) {
        return ProviderBase._untouchables.contains(typeKey);
    }
    
    protected IOException _createNoContentException() {
        return (IOException)new NoContentException("No content (empty input stream)");
    }
    
    protected static boolean _containedIn(final Class<?> mainType, final HashSet<ClassKey> set) {
        if (set != null) {
            final ClassKey key = new ClassKey(mainType);
            if (set.contains(key)) {
                return true;
            }
            for (final Class<?> cls : findSuperTypes(mainType, null)) {
                key.reset(cls);
                if (set.contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Boolean _findCustomUntouchable(final Class<?> mainType) {
        if (this._cfgCustomUntouchables != null) {
            final ClassKey key = new ClassKey(mainType);
            Boolean b = this._cfgCustomUntouchables.get(key);
            if (b != null) {
                return b;
            }
            for (final Class<?> cls : findSuperTypes(mainType, null)) {
                key.reset(cls);
                b = this._cfgCustomUntouchables.get(key);
                if (b != null) {
                    return b;
                }
            }
        }
        return null;
    }
    
    protected static List<Class<?>> findSuperTypes(final Class<?> cls, final Class<?> endBefore) {
        return findSuperTypes(cls, endBefore, new ArrayList<Class<?>>(8));
    }
    
    protected static List<Class<?>> findSuperTypes(final Class<?> cls, final Class<?> endBefore, final List<Class<?>> result) {
        _addSuperTypes(cls, endBefore, result, false);
        return result;
    }
    
    protected static void _addSuperTypes(final Class<?> cls, final Class<?> endBefore, final Collection<Class<?>> result, final boolean addClassItself) {
        if (cls == endBefore || cls == null || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(cls)) {
                return;
            }
            result.add(cls);
        }
        for (final Class<?> intCls : cls.getInterfaces()) {
            _addSuperTypes(intCls, endBefore, result, true);
        }
        _addSuperTypes(cls.getSuperclass(), endBefore, result, true);
    }
    
    private final THIS _this() {
        return (THIS)this;
    }
    
    static {
        (DEFAULT_UNTOUCHABLES = new HashSet<ClassKey>()).add(new ClassKey(InputStream.class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(Reader.class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(OutputStream.class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(Writer.class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(char[].class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(String.class));
        ProviderBase.DEFAULT_UNTOUCHABLES.add(new ClassKey(byte[].class));
        DEFAULT_UNREADABLES = new Class[] { InputStream.class, Reader.class };
        DEFAULT_UNWRITABLES = new Class[] { InputStream.class, OutputStream.class, Writer.class, StreamingOutput.class, Response.class };
        JAXRS_FEATURE_DEFAULTS = JaxRSFeature.collectDefaults();
        _untouchables = ProviderBase.DEFAULT_UNTOUCHABLES;
        _unreadableClasses = ProviderBase.DEFAULT_UNREADABLES;
        _unwritableClasses = ProviderBase.DEFAULT_UNWRITABLES;
    }
}
