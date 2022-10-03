package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import java.io.FileInputStream;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.core.filter.FilteringParserDelegate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.core.TreeNode;
import java.util.Iterator;
import com.fasterxml.jackson.core.type.ResolvedType;
import java.io.DataInput;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.File;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Map;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.util.TimeZone;
import java.util.Locale;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.StreamReadFeature;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import java.io.Serializable;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.ObjectCodec;

public class ObjectReader extends ObjectCodec implements Versioned, Serializable
{
    private static final long serialVersionUID = 2L;
    protected final DeserializationConfig _config;
    protected final DefaultDeserializationContext _context;
    protected final JsonFactory _parserFactory;
    protected final boolean _unwrapRoot;
    private final TokenFilter _filter;
    protected final JavaType _valueType;
    protected final JsonDeserializer<Object> _rootDeserializer;
    protected final Object _valueToUpdate;
    protected final FormatSchema _schema;
    protected final InjectableValues _injectableValues;
    protected final DataFormatReaders _dataFormatReaders;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
    protected transient JavaType _jsonNodeType;
    
    protected ObjectReader(final ObjectMapper mapper, final DeserializationConfig config) {
        this(mapper, config, null, null, null, null);
    }
    
    protected ObjectReader(final ObjectMapper mapper, final DeserializationConfig config, final JavaType valueType, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues) {
        this._config = config;
        this._context = mapper._deserializationContext;
        this._rootDeserializers = mapper._rootDeserializers;
        this._parserFactory = mapper._jsonFactory;
        this._valueType = valueType;
        this._valueToUpdate = valueToUpdate;
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._rootDeserializer = this._prefetchRootDeserializer(valueType);
        this._dataFormatReaders = null;
        this._filter = null;
    }
    
    protected ObjectReader(final ObjectReader base, final DeserializationConfig config, final JavaType valueType, final JsonDeserializer<Object> rootDeser, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues, final DataFormatReaders dataFormatReaders) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = valueType;
        this._rootDeserializer = rootDeser;
        this._valueToUpdate = valueToUpdate;
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = dataFormatReaders;
        this._filter = base._filter;
    }
    
    protected ObjectReader(final ObjectReader base, final DeserializationConfig config) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = base._filter;
    }
    
    protected ObjectReader(final ObjectReader base, final JsonFactory f) {
        this._config = ((MapperConfigBase<CFG, DeserializationConfig>)base._config).with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = f;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = base._unwrapRoot;
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = base._filter;
    }
    
    protected ObjectReader(final ObjectReader base, final TokenFilter filter) {
        this._config = base._config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = base._unwrapRoot;
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = filter;
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected ObjectReader _new(final ObjectReader base, final JsonFactory f) {
        return new ObjectReader(base, f);
    }
    
    protected ObjectReader _new(final ObjectReader base, final DeserializationConfig config) {
        return new ObjectReader(base, config);
    }
    
    protected ObjectReader _new(final ObjectReader base, final DeserializationConfig config, final JavaType valueType, final JsonDeserializer<Object> rootDeser, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues, final DataFormatReaders dataFormatReaders) {
        return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
    }
    
    protected <T> MappingIterator<T> _newIterator(final JsonParser p, final DeserializationContext ctxt, final JsonDeserializer<?> deser, final boolean parserManaged) {
        return new MappingIterator<T>(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
    }
    
    protected JsonToken _initForReading(final DeserializationContext ctxt, final JsonParser p) throws IOException {
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        this._config.initialize(p);
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                ctxt.reportInputMismatch(this._valueType, "No content to map due to end-of-input", new Object[0]);
            }
        }
        return t;
    }
    
    protected void _initForMultiRead(final DeserializationContext ctxt, final JsonParser p) throws IOException {
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        this._config.initialize(p);
    }
    
    public ObjectReader with(final DeserializationFeature feature) {
        return this._with(this._config.with(feature));
    }
    
    public ObjectReader with(final DeserializationFeature first, final DeserializationFeature... other) {
        return this._with(this._config.with(first, other));
    }
    
    public ObjectReader withFeatures(final DeserializationFeature... features) {
        return this._with(this._config.withFeatures(features));
    }
    
    public ObjectReader without(final DeserializationFeature feature) {
        return this._with(this._config.without(feature));
    }
    
    public ObjectReader without(final DeserializationFeature first, final DeserializationFeature... other) {
        return this._with(this._config.without(first, other));
    }
    
    public ObjectReader withoutFeatures(final DeserializationFeature... features) {
        return this._with(this._config.withoutFeatures(features));
    }
    
    public ObjectReader with(final JsonParser.Feature feature) {
        return this._with(this._config.with(feature));
    }
    
    public ObjectReader withFeatures(final JsonParser.Feature... features) {
        return this._with(this._config.withFeatures(features));
    }
    
    public ObjectReader without(final JsonParser.Feature feature) {
        return this._with(this._config.without(feature));
    }
    
    public ObjectReader withoutFeatures(final JsonParser.Feature... features) {
        return this._with(this._config.withoutFeatures(features));
    }
    
    public ObjectReader with(final StreamReadFeature feature) {
        return this._with(this._config.with(feature.mappedFeature()));
    }
    
    public ObjectReader without(final StreamReadFeature feature) {
        return this._with(this._config.without(feature.mappedFeature()));
    }
    
    public ObjectReader with(final FormatFeature feature) {
        return this._with(this._config.with(feature));
    }
    
    public ObjectReader withFeatures(final FormatFeature... features) {
        return this._with(this._config.withFeatures(features));
    }
    
    public ObjectReader without(final FormatFeature feature) {
        return this._with(this._config.without(feature));
    }
    
    public ObjectReader withoutFeatures(final FormatFeature... features) {
        return this._with(this._config.withoutFeatures(features));
    }
    
    public ObjectReader at(final String pointerExpr) {
        this._assertNotNull("pointerExpr", pointerExpr);
        return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(pointerExpr));
    }
    
    public ObjectReader at(final JsonPointer pointer) {
        this._assertNotNull("pointer", pointer);
        return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(pointer));
    }
    
    public ObjectReader with(final DeserializationConfig config) {
        return this._with(config);
    }
    
    public ObjectReader with(final InjectableValues injectableValues) {
        if (this._injectableValues == injectableValues) {
            return this;
        }
        return this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader with(final JsonNodeFactory f) {
        return this._with(this._config.with(f));
    }
    
    public ObjectReader with(final JsonFactory f) {
        if (f == this._parserFactory) {
            return this;
        }
        final ObjectReader r = this._new(this, f);
        if (f.getCodec() == null) {
            f.setCodec((ObjectCodec)r);
        }
        return r;
    }
    
    public ObjectReader withRootName(final String rootName) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).withRootName(rootName));
    }
    
    public ObjectReader withRootName(final PropertyName rootName) {
        return this._with(this._config.withRootName(rootName));
    }
    
    public ObjectReader withoutRootName() {
        return this._with(this._config.withRootName(PropertyName.NO_NAME));
    }
    
    public ObjectReader with(final FormatSchema schema) {
        if (this._schema == schema) {
            return this;
        }
        this._verifySchemaType(schema);
        return this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader forType(final JavaType valueType) {
        if (valueType != null && valueType.equals(this._valueType)) {
            return this;
        }
        final JsonDeserializer<Object> rootDeser = this._prefetchRootDeserializer(valueType);
        DataFormatReaders det = this._dataFormatReaders;
        if (det != null) {
            det = det.withType(valueType);
        }
        return this._new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
    }
    
    public ObjectReader forType(final Class<?> valueType) {
        return this.forType(this._config.constructType(valueType));
    }
    
    public ObjectReader forType(final TypeReference<?> valueTypeRef) {
        return this.forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
    }
    
    @Deprecated
    public ObjectReader withType(final JavaType valueType) {
        return this.forType(valueType);
    }
    
    @Deprecated
    public ObjectReader withType(final Class<?> valueType) {
        return this.forType(this._config.constructType(valueType));
    }
    
    @Deprecated
    public ObjectReader withType(final Type valueType) {
        return this.forType(this._config.getTypeFactory().constructType(valueType));
    }
    
    @Deprecated
    public ObjectReader withType(final TypeReference<?> valueTypeRef) {
        return this.forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
    }
    
    public ObjectReader withValueToUpdate(final Object value) {
        if (value == this._valueToUpdate) {
            return this;
        }
        if (value == null) {
            return this._new(this, this._config, this._valueType, this._rootDeserializer, null, this._schema, this._injectableValues, this._dataFormatReaders);
        }
        JavaType t;
        if (this._valueType == null) {
            t = this._config.constructType(value.getClass());
        }
        else {
            t = this._valueType;
        }
        return this._new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader withView(final Class<?> activeView) {
        return this._with(this._config.withView(activeView));
    }
    
    public ObjectReader with(final Locale l) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).with(l));
    }
    
    public ObjectReader with(final TimeZone tz) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).with(tz));
    }
    
    public ObjectReader withHandler(final DeserializationProblemHandler h) {
        return this._with(this._config.withHandler(h));
    }
    
    public ObjectReader with(final Base64Variant defaultBase64) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).with(defaultBase64));
    }
    
    public ObjectReader withFormatDetection(final ObjectReader... readers) {
        return this.withFormatDetection(new DataFormatReaders(readers));
    }
    
    public ObjectReader withFormatDetection(final DataFormatReaders readers) {
        return this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
    }
    
    public ObjectReader with(final ContextAttributes attrs) {
        return this._with(this._config.with(attrs));
    }
    
    public ObjectReader withAttributes(final Map<?, ?> attrs) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).withAttributes(attrs));
    }
    
    public ObjectReader withAttribute(final Object key, final Object value) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).withAttribute(key, value));
    }
    
    public ObjectReader withoutAttribute(final Object key) {
        return this._with(((MapperConfigBase<CFG, DeserializationConfig>)this._config).withoutAttribute(key));
    }
    
    protected ObjectReader _with(final DeserializationConfig newConfig) {
        if (newConfig == this._config) {
            return this;
        }
        ObjectReader r = this._new(this, newConfig);
        if (this._dataFormatReaders != null) {
            r = r.withFormatDetection(this._dataFormatReaders.with(newConfig));
        }
        return r;
    }
    
    public boolean isEnabled(final DeserializationFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._config.isEnabled(f, this._parserFactory);
    }
    
    public boolean isEnabled(final StreamReadFeature f) {
        return this._config.isEnabled(f.mappedFeature(), this._parserFactory);
    }
    
    public DeserializationConfig getConfig() {
        return this._config;
    }
    
    public JsonFactory getFactory() {
        return this._parserFactory;
    }
    
    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }
    
    public InjectableValues getInjectableValues() {
        return this._injectableValues;
    }
    
    public JavaType getValueType() {
        return this._valueType;
    }
    
    public JsonParser createParser(final File src) throws IOException {
        this._assertNotNull("src", src);
        return this._parserFactory.createParser(src);
    }
    
    public JsonParser createParser(final URL src) throws IOException {
        this._assertNotNull("src", src);
        return this._parserFactory.createParser(src);
    }
    
    public JsonParser createParser(final InputStream in) throws IOException {
        this._assertNotNull("in", in);
        return this._parserFactory.createParser(in);
    }
    
    public JsonParser createParser(final Reader r) throws IOException {
        this._assertNotNull("r", r);
        return this._parserFactory.createParser(r);
    }
    
    public JsonParser createParser(final byte[] content) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content);
    }
    
    public JsonParser createParser(final byte[] content, final int offset, final int len) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content, offset, len);
    }
    
    public JsonParser createParser(final String content) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content);
    }
    
    public JsonParser createParser(final char[] content) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content);
    }
    
    public JsonParser createParser(final char[] content, final int offset, final int len) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content, offset, len);
    }
    
    public JsonParser createParser(final DataInput content) throws IOException {
        this._assertNotNull("content", content);
        return this._parserFactory.createParser(content);
    }
    
    public JsonParser createNonBlockingByteArrayParser() throws IOException {
        return this._parserFactory.createNonBlockingByteArrayParser();
    }
    
    public <T> T readValue(final JsonParser p) throws IOException {
        this._assertNotNull("p", p);
        return (T)this._bind(p, this._valueToUpdate);
    }
    
    public <T> T readValue(final JsonParser p, final Class<T> valueType) throws IOException {
        this._assertNotNull("p", p);
        return this.forType(valueType).readValue(p);
    }
    
    public <T> T readValue(final JsonParser p, final TypeReference<T> valueTypeRef) throws IOException {
        this._assertNotNull("p", p);
        return this.forType(valueTypeRef).readValue(p);
    }
    
    public <T> T readValue(final JsonParser p, final ResolvedType valueType) throws IOException {
        this._assertNotNull("p", p);
        return this.forType((JavaType)valueType).readValue(p);
    }
    
    public <T> T readValue(final JsonParser p, final JavaType valueType) throws IOException {
        this._assertNotNull("p", p);
        return this.forType(valueType).readValue(p);
    }
    
    public <T> Iterator<T> readValues(final JsonParser p, final Class<T> valueType) throws IOException {
        this._assertNotNull("p", p);
        return (Iterator<T>)this.forType(valueType).readValues(p);
    }
    
    public <T> Iterator<T> readValues(final JsonParser p, final TypeReference<T> valueTypeRef) throws IOException {
        this._assertNotNull("p", p);
        return (Iterator<T>)this.forType(valueTypeRef).readValues(p);
    }
    
    public <T> Iterator<T> readValues(final JsonParser p, final ResolvedType valueType) throws IOException {
        this._assertNotNull("p", p);
        return this.readValues(p, (JavaType)valueType);
    }
    
    public <T> Iterator<T> readValues(final JsonParser p, final JavaType valueType) throws IOException {
        this._assertNotNull("p", p);
        return (Iterator<T>)this.forType(valueType).readValues(p);
    }
    
    public JsonNode createArrayNode() {
        return this._config.getNodeFactory().arrayNode();
    }
    
    public JsonNode createObjectNode() {
        return this._config.getNodeFactory().objectNode();
    }
    
    public JsonNode missingNode() {
        return this._config.getNodeFactory().missingNode();
    }
    
    public JsonNode nullNode() {
        return this._config.getNodeFactory().nullNode();
    }
    
    public JsonParser treeAsTokens(final TreeNode n) {
        this._assertNotNull("n", n);
        final ObjectReader codec = this.withValueToUpdate(null);
        return (JsonParser)new TreeTraversingParser((JsonNode)n, codec);
    }
    
    public <T extends TreeNode> T readTree(final JsonParser p) throws IOException {
        this._assertNotNull("p", p);
        return (T)this._bindAsTreeOrNull(p);
    }
    
    public void writeTree(final JsonGenerator g, final TreeNode rootNode) {
        throw new UnsupportedOperationException();
    }
    
    public <T> T readValue(final InputStream src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(src), false);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> T readValue(final InputStream src, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(src);
    }
    
    public <T> T readValue(final Reader src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> T readValue(final Reader src, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(src);
    }
    
    public <T> T readValue(final String src) throws JsonProcessingException, JsonMappingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        try {
            return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
        }
        catch (final JsonProcessingException e) {
            throw e;
        }
        catch (final IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }
    
    public <T> T readValue(final String src, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(src);
    }
    
    public <T> T readValue(final byte[] content) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(content, 0, content.length);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(content), false));
    }
    
    public <T> T readValue(final byte[] content, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(content);
    }
    
    public <T> T readValue(final byte[] buffer, final int offset, final int length) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(buffer, offset, length);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(buffer, offset, length), false));
    }
    
    public <T> T readValue(final byte[] buffer, final int offset, final int length, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(buffer, offset, length);
    }
    
    public <T> T readValue(final File src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> T readValue(final File src, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(src);
    }
    
    public <T> T readValue(final URL src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> T readValue(final URL src, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(src);
    }
    
    public <T> T readValue(final JsonNode content) throws IOException {
        this._assertNotNull("content", content);
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(content);
        }
        return (T)this._bindAndClose(this._considerFilter(this.treeAsTokens((TreeNode)content), false));
    }
    
    public <T> T readValue(final JsonNode content, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(content);
    }
    
    public <T> T readValue(final DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return (T)this._bindAndClose(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> T readValue(final DataInput content, final Class<T> valueType) throws IOException {
        return this.forType(valueType).readValue(content);
    }
    
    public JsonNode readTree(final InputStream src) throws IOException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndCloseAsTree(src);
        }
        return this._bindAndCloseAsTree(this._considerFilter(this.createParser(src), false));
    }
    
    public JsonNode readTree(final Reader src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return this._bindAndCloseAsTree(this._considerFilter(this.createParser(src), false));
    }
    
    public JsonNode readTree(final String json) throws JsonProcessingException, JsonMappingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        try {
            return this._bindAndCloseAsTree(this._considerFilter(this.createParser(json), false));
        }
        catch (final JsonProcessingException e) {
            throw e;
        }
        catch (final IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }
    
    public JsonNode readTree(final byte[] json) throws IOException {
        this._assertNotNull("json", json);
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        return this._bindAndCloseAsTree(this._considerFilter(this.createParser(json), false));
    }
    
    public JsonNode readTree(final byte[] json, final int offset, final int len) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        return this._bindAndCloseAsTree(this._considerFilter(this.createParser(json, offset, len), false));
    }
    
    public JsonNode readTree(final DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return this._bindAndCloseAsTree(this._considerFilter(this.createParser(src), false));
    }
    
    public <T> MappingIterator<T> readValues(final JsonParser p) throws IOException {
        this._assertNotNull("p", p);
        final DeserializationContext ctxt = this.createDeserializationContext(p);
        return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), false);
    }
    
    public <T> MappingIterator<T> readValues(final InputStream src) throws IOException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false);
        }
        return this._bindAndReadValues(this._considerFilter(this.createParser(src), true));
    }
    
    public <T> MappingIterator<T> readValues(final Reader src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        final JsonParser p = this._considerFilter(this.createParser(src), true);
        final DeserializationContext ctxt = this.createDeserializationContext(p);
        this._initForMultiRead(ctxt, p);
        p.nextToken();
        return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
    }
    
    public <T> MappingIterator<T> readValues(final String json) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        final JsonParser p = this._considerFilter(this.createParser(json), true);
        final DeserializationContext ctxt = this.createDeserializationContext(p);
        this._initForMultiRead(ctxt, p);
        p.nextToken();
        return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
    }
    
    public <T> MappingIterator<T> readValues(final byte[] src, final int offset, final int length) throws IOException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false);
        }
        return this._bindAndReadValues(this._considerFilter(this.createParser(src, offset, length), true));
    }
    
    public final <T> MappingIterator<T> readValues(final byte[] src) throws IOException {
        this._assertNotNull("src", src);
        return this.readValues(src, 0, src.length);
    }
    
    public <T> MappingIterator<T> readValues(final File src) throws IOException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), false);
        }
        return this._bindAndReadValues(this._considerFilter(this.createParser(src), true));
    }
    
    public <T> MappingIterator<T> readValues(final URL src) throws IOException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return this._bindAndReadValues(this._considerFilter(this.createParser(src), true));
    }
    
    public <T> MappingIterator<T> readValues(final DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return this._bindAndReadValues(this._considerFilter(this.createParser(src), true));
    }
    
    public <T> T treeToValue(final TreeNode n, final Class<T> valueType) throws JsonProcessingException {
        this._assertNotNull("n", n);
        try {
            return this.readValue(this.treeAsTokens(n), valueType);
        }
        catch (final JsonProcessingException e) {
            throw e;
        }
        catch (final IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }
    
    public void writeValue(final JsonGenerator gen, final Object value) throws IOException {
        throw new UnsupportedOperationException("Not implemented for ObjectReader");
    }
    
    protected Object _bind(final JsonParser p, final Object valueToUpdate) throws IOException {
        final DeserializationContext ctxt = this.createDeserializationContext(p);
        final JsonToken t = this._initForReading(ctxt, p);
        Object result;
        if (t == JsonToken.VALUE_NULL) {
            if (valueToUpdate == null) {
                result = this._findRootDeserializer(ctxt).getNullValue(ctxt);
            }
            else {
                result = valueToUpdate;
            }
        }
        else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = valueToUpdate;
        }
        else {
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt);
            if (this._unwrapRoot) {
                result = this._unwrapAndDeserialize(p, ctxt, this._valueType, deser);
            }
            else if (valueToUpdate == null) {
                result = deser.deserialize(p, ctxt);
            }
            else {
                result = deser.deserialize(p, ctxt, valueToUpdate);
            }
        }
        p.clearCurrentToken();
        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, this._valueType);
        }
        return result;
    }
    
    protected Object _bindAndClose(final JsonParser p0) throws IOException {
        try (final JsonParser p = p0) {
            final DeserializationContext ctxt = this.createDeserializationContext(p);
            final JsonToken t = this._initForReading(ctxt, p);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                if (this._valueToUpdate == null) {
                    result = this._findRootDeserializer(ctxt).getNullValue(ctxt);
                }
                else {
                    result = this._valueToUpdate;
                }
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = this._valueToUpdate;
            }
            else {
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt);
                if (this._unwrapRoot) {
                    result = this._unwrapAndDeserialize(p, ctxt, this._valueType, deser);
                }
                else if (this._valueToUpdate == null) {
                    result = deser.deserialize(p, ctxt);
                }
                else {
                    deser.deserialize(p, ctxt, this._valueToUpdate);
                    result = this._valueToUpdate;
                }
            }
            if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                this._verifyNoTrailingTokens(p, ctxt, this._valueType);
            }
            return result;
        }
    }
    
    protected final JsonNode _bindAndCloseAsTree(final JsonParser p0) throws IOException {
        try (final JsonParser p = p0) {
            return this._bindAsTree(p);
        }
    }
    
    protected final JsonNode _bindAsTree(final JsonParser p) throws IOException {
        this._config.initialize(p);
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return this._config.getNodeFactory().missingNode();
            }
        }
        final boolean checkTrailing = this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        JsonNode resultNode;
        DeserializationContext ctxt;
        if (t == JsonToken.VALUE_NULL) {
            resultNode = this._config.getNodeFactory().nullNode();
            if (!checkTrailing) {
                return resultNode;
            }
            ctxt = this.createDeserializationContext(p);
        }
        else {
            ctxt = this.createDeserializationContext(p);
            final JsonDeserializer<Object> deser = this._findTreeDeserializer(ctxt);
            if (this._unwrapRoot) {
                resultNode = (JsonNode)this._unwrapAndDeserialize(p, ctxt, this._jsonNodeType(), deser);
            }
            else {
                resultNode = deser.deserialize(p, ctxt);
            }
        }
        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, this._jsonNodeType());
        }
        return resultNode;
    }
    
    protected final JsonNode _bindAsTreeOrNull(final JsonParser p) throws IOException {
        this._config.initialize(p);
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return null;
            }
        }
        final boolean checkTrailing = this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        JsonNode resultNode;
        DeserializationContext ctxt;
        if (t == JsonToken.VALUE_NULL) {
            resultNode = this._config.getNodeFactory().nullNode();
            if (!checkTrailing) {
                return resultNode;
            }
            ctxt = this.createDeserializationContext(p);
        }
        else {
            ctxt = this.createDeserializationContext(p);
            final JsonDeserializer<Object> deser = this._findTreeDeserializer(ctxt);
            if (this._unwrapRoot) {
                resultNode = (JsonNode)this._unwrapAndDeserialize(p, ctxt, this._jsonNodeType(), deser);
            }
            else {
                resultNode = deser.deserialize(p, ctxt);
            }
        }
        if (checkTrailing) {
            this._verifyNoTrailingTokens(p, ctxt, this._jsonNodeType());
        }
        return resultNode;
    }
    
    protected <T> MappingIterator<T> _bindAndReadValues(final JsonParser p) throws IOException {
        final DeserializationContext ctxt = this.createDeserializationContext(p);
        this._initForMultiRead(ctxt, p);
        p.nextToken();
        return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
    }
    
    protected Object _unwrapAndDeserialize(final JsonParser p, final DeserializationContext ctxt, final JavaType rootType, final JsonDeserializer<Object> deser) throws IOException {
        final PropertyName expRootName = this._config.findRootName(rootType);
        final String expSimpleName = expRootName.getSimpleName();
        if (p.getCurrentToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (p.nextToken() != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        final String actualName = p.getCurrentName();
        if (!expSimpleName.equals(actualName)) {
            ctxt.reportPropertyInputMismatch(rootType, actualName, "Root name '%s' does not match expected ('%s') for type %s", actualName, expSimpleName, rootType);
        }
        p.nextToken();
        Object result;
        if (this._valueToUpdate == null) {
            result = deser.deserialize(p, ctxt);
        }
        else {
            deser.deserialize(p, ctxt, this._valueToUpdate);
            result = this._valueToUpdate;
        }
        if (p.nextToken() != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, this._valueType);
        }
        return result;
    }
    
    protected JsonParser _considerFilter(final JsonParser p, final boolean multiValue) {
        return (JsonParser)((this._filter == null || FilteringParserDelegate.class.isInstance(p)) ? p : new FilteringParserDelegate(p, this._filter, false, multiValue));
    }
    
    protected final void _verifyNoTrailingTokens(final JsonParser p, final DeserializationContext ctxt, final JavaType bindType) throws IOException {
        final JsonToken t = p.nextToken();
        if (t != null) {
            Class<?> bt = ClassUtil.rawClass(bindType);
            if (bt == null && this._valueToUpdate != null) {
                bt = this._valueToUpdate.getClass();
            }
            ctxt.reportTrailingTokens(bt, p, t);
        }
    }
    
    protected Object _detectBindAndClose(final byte[] src, final int offset, final int length) throws IOException {
        final DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        return match.getReader()._bindAndClose(p);
    }
    
    protected Object _detectBindAndClose(final DataFormatReaders.Match match, final boolean forceClosing) throws IOException {
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndClose(p);
    }
    
    protected <T> MappingIterator<T> _detectBindAndReadValues(final DataFormatReaders.Match match, final boolean forceClosing) throws IOException {
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndReadValues(p);
    }
    
    protected JsonNode _detectBindAndCloseAsTree(final InputStream in) throws IOException {
        final DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return match.getReader()._bindAndCloseAsTree(p);
    }
    
    protected void _reportUnkownFormat(final DataFormatReaders detector, final DataFormatReaders.Match match) throws JsonProcessingException {
        throw new JsonParseException((JsonParser)null, "Cannot detect format from input, does not look like any of detectable formats " + detector.toString());
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._parserFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName());
        }
    }
    
    protected DefaultDeserializationContext createDeserializationContext(final JsonParser p) {
        return this._context.createInstance(this._config, p, this._injectableValues);
    }
    
    protected InputStream _inputStream(final URL src) throws IOException {
        return src.openStream();
    }
    
    protected InputStream _inputStream(final File f) throws IOException {
        return new FileInputStream(f);
    }
    
    protected void _reportUndetectableSource(final Object src) throws JsonParseException {
        throw new JsonParseException((JsonParser)null, "Cannot use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
    }
    
    protected JsonDeserializer<Object> _findRootDeserializer(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._rootDeserializer != null) {
            return this._rootDeserializer;
        }
        final JavaType t = this._valueType;
        if (t == null) {
            ctxt.reportBadDefinition((JavaType)null, "No value type configured for ObjectReader");
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(t);
        if (deser != null) {
            return deser;
        }
        deser = ctxt.findRootValueDeserializer(t);
        if (deser == null) {
            ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t);
        }
        this._rootDeserializers.put(t, deser);
        return deser;
    }
    
    protected JsonDeserializer<Object> _findTreeDeserializer(final DeserializationContext ctxt) throws JsonMappingException {
        final JavaType nodeType = this._jsonNodeType();
        JsonDeserializer<Object> deser = this._rootDeserializers.get(nodeType);
        if (deser == null) {
            deser = ctxt.findRootValueDeserializer(nodeType);
            if (deser == null) {
                ctxt.reportBadDefinition(nodeType, "Cannot find a deserializer for type " + nodeType);
            }
            this._rootDeserializers.put(nodeType, deser);
        }
        return deser;
    }
    
    protected JsonDeserializer<Object> _prefetchRootDeserializer(final JavaType valueType) {
        if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
            return null;
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser == null) {
            try {
                final DeserializationContext ctxt = this.createDeserializationContext(null);
                deser = ctxt.findRootValueDeserializer(valueType);
                if (deser != null) {
                    this._rootDeserializers.put(valueType, deser);
                }
                return deser;
            }
            catch (final JsonProcessingException ex) {}
        }
        return deser;
    }
    
    protected final JavaType _jsonNodeType() {
        JavaType t = this._jsonNodeType;
        if (t == null) {
            t = this.getTypeFactory().constructType(JsonNode.class);
            this._jsonNodeType = t;
        }
        return t;
    }
    
    protected final void _assertNotNull(final String paramName, final Object src) {
        if (src == null) {
            throw new IllegalArgumentException(String.format("argument \"%s\" is null", paramName));
        }
    }
}
