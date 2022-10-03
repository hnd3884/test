package com.azul.crs.com.fasterxml.jackson.databind;

import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.azul.crs.com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.azul.crs.com.fasterxml.jackson.databind.util.Named;
import java.lang.reflect.Type;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import java.util.concurrent.atomic.AtomicReference;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.azul.crs.com.fasterxml.jackson.core.Base64Variant;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.TimeZone;
import java.util.Locale;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import com.azul.crs.com.fasterxml.jackson.databind.util.LinkedNode;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.ContextAttributes;
import java.text.DateFormat;
import com.azul.crs.com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.azul.crs.com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.core.StreamReadCapability;
import com.azul.crs.com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.azul.crs.com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.azul.crs.com.fasterxml.jackson.databind.deser.DeserializerCache;
import java.io.Serializable;

public abstract class DeserializationContext extends DatabindContext implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final DeserializerCache _cache;
    protected final DeserializerFactory _factory;
    protected final DeserializationConfig _config;
    protected final int _featureFlags;
    protected final JacksonFeatureSet<StreamReadCapability> _readCapabilities;
    protected final Class<?> _view;
    protected transient JsonParser _parser;
    protected final InjectableValues _injectableValues;
    protected transient ArrayBuilders _arrayBuilders;
    protected transient ObjectBuffer _objectBuffer;
    protected transient DateFormat _dateFormat;
    protected transient ContextAttributes _attributes;
    protected LinkedNode<JavaType> _currentType;
    
    protected DeserializationContext(final DeserializerFactory df) {
        this(df, null);
    }
    
    protected DeserializationContext(final DeserializerFactory df, DeserializerCache cache) {
        if (df == null) {
            throw new NullPointerException("Cannot pass null DeserializerFactory");
        }
        this._factory = df;
        if (cache == null) {
            cache = new DeserializerCache();
        }
        this._cache = cache;
        this._featureFlags = 0;
        this._readCapabilities = null;
        this._config = null;
        this._injectableValues = null;
        this._view = null;
        this._attributes = null;
    }
    
    protected DeserializationContext(final DeserializationContext src, final DeserializerFactory factory) {
        this._cache = src._cache;
        this._factory = factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._readCapabilities = src._readCapabilities;
        this._view = src._view;
        this._parser = src._parser;
        this._injectableValues = src._injectableValues;
        this._attributes = src._attributes;
    }
    
    protected DeserializationContext(final DeserializationContext src, final DeserializationConfig config, final JsonParser p, final InjectableValues injectableValues) {
        this._cache = src._cache;
        this._factory = src._factory;
        this._readCapabilities = ((p == null) ? null : p.getReadCapabilities());
        this._config = config;
        this._featureFlags = config.getDeserializationFeatures();
        this._view = config.getActiveView();
        this._parser = p;
        this._injectableValues = injectableValues;
        this._attributes = config.getAttributes();
    }
    
    protected DeserializationContext(final DeserializationContext src, final DeserializationConfig config) {
        this._cache = src._cache;
        this._factory = src._factory;
        this._readCapabilities = null;
        this._config = config;
        this._featureFlags = config.getDeserializationFeatures();
        this._view = null;
        this._parser = null;
        this._injectableValues = null;
        this._attributes = null;
    }
    
    protected DeserializationContext(final DeserializationContext src) {
        this._cache = new DeserializerCache();
        this._factory = src._factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._readCapabilities = src._readCapabilities;
        this._view = src._view;
        this._injectableValues = null;
    }
    
    @Override
    public DeserializationConfig getConfig() {
        return this._config;
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._view;
    }
    
    @Override
    public final boolean canOverrideAccessModifiers() {
        return this._config.canOverrideAccessModifiers();
    }
    
    @Override
    public final boolean isEnabled(final MapperFeature feature) {
        return this._config.isEnabled(feature);
    }
    
    @Override
    public final JsonFormat.Value getDefaultPropertyFormat(final Class<?> baseType) {
        return this._config.getDefaultPropertyFormat(baseType);
    }
    
    @Override
    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }
    
    @Override
    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    @Override
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) throws IllegalArgumentException {
        if (baseType.hasRawClass(subclass)) {
            return baseType;
        }
        return this.getConfig().getTypeFactory().constructSpecializedType(baseType, subclass, false);
    }
    
    @Override
    public Locale getLocale() {
        return this._config.getLocale();
    }
    
    @Override
    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }
    
    @Override
    public Object getAttribute(final Object key) {
        return this._attributes.getAttribute(key);
    }
    
    @Override
    public DeserializationContext setAttribute(final Object key, final Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }
    
    public JavaType getContextualType() {
        return (this._currentType == null) ? null : this._currentType.value();
    }
    
    public DeserializerFactory getFactory() {
        return this._factory;
    }
    
    public final boolean isEnabled(final DeserializationFeature feat) {
        return (this._featureFlags & feat.getMask()) != 0x0;
    }
    
    public final boolean isEnabled(final StreamReadCapability cap) {
        return this._readCapabilities.isEnabled(cap);
    }
    
    public final int getDeserializationFeatures() {
        return this._featureFlags;
    }
    
    public final boolean hasDeserializationFeatures(final int featureMask) {
        return (this._featureFlags & featureMask) == featureMask;
    }
    
    public final boolean hasSomeOfFeatures(final int featureMask) {
        return (this._featureFlags & featureMask) != 0x0;
    }
    
    public final JsonParser getParser() {
        return this._parser;
    }
    
    public final Object findInjectableValue(final Object valueId, final BeanProperty forProperty, final Object beanInstance) throws JsonMappingException {
        if (this._injectableValues == null) {
            this.reportBadDefinition(ClassUtil.classOf(valueId), String.format("No 'injectableValues' configured, cannot inject value with id [%s]", valueId));
        }
        return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
    }
    
    public final Base64Variant getBase64Variant() {
        return this._config.getBase64Variant();
    }
    
    public final JsonNodeFactory getNodeFactory() {
        return this._config.getNodeFactory();
    }
    
    public CoercionAction findCoercionAction(final LogicalType targetType, final Class<?> targetClass, final CoercionInputShape inputShape) {
        return this._config.findCoercionAction(targetType, targetClass, inputShape);
    }
    
    public CoercionAction findCoercionFromBlankString(final LogicalType targetType, final Class<?> targetClass, final CoercionAction actionIfBlankNotAllowed) {
        return this._config.findCoercionFromBlankString(targetType, targetClass, actionIfBlankNotAllowed);
    }
    
    public boolean hasValueDeserializerFor(final JavaType type, final AtomicReference<Throwable> cause) {
        try {
            return this._cache.hasValueDeserializerFor(this, this._factory, type);
        }
        catch (final JsonMappingException e) {
            if (cause != null) {
                cause.set(e);
            }
        }
        catch (final RuntimeException e2) {
            if (cause == null) {
                throw e2;
            }
            cause.set(e2);
        }
        return false;
    }
    
    public final JsonDeserializer<Object> findContextualValueDeserializer(final JavaType type, final BeanProperty prop) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser != null) {
            deser = (JsonDeserializer<Object>)this.handleSecondaryContextualization(deser, prop, type);
        }
        return deser;
    }
    
    public final JsonDeserializer<Object> findNonContextualValueDeserializer(final JavaType type) throws JsonMappingException {
        return this._cache.findValueDeserializer(this, this._factory, type);
    }
    
    public final JsonDeserializer<Object> findRootValueDeserializer(final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser == null) {
            return null;
        }
        deser = (JsonDeserializer<Object>)this.handleSecondaryContextualization(deser, null, type);
        TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(null);
            return new TypeWrappedDeserializer(typeDeser, deser);
        }
        return deser;
    }
    
    public final KeyDeserializer findKeyDeserializer(final JavaType keyType, final BeanProperty prop) throws JsonMappingException {
        KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
        if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer)kd).createContextual(this, prop);
        }
        return kd;
    }
    
    public abstract ReadableObjectId findObjectId(final Object p0, final ObjectIdGenerator<?> p1, final ObjectIdResolver p2);
    
    public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;
    
    public final JavaType constructType(final Class<?> cls) {
        return (cls == null) ? null : this._config.constructType(cls);
    }
    
    public Class<?> findClass(final String className) throws ClassNotFoundException {
        return this.getTypeFactory().findClass(className);
    }
    
    public final ObjectBuffer leaseObjectBuffer() {
        ObjectBuffer buf = this._objectBuffer;
        if (buf == null) {
            buf = new ObjectBuffer();
        }
        else {
            this._objectBuffer = null;
        }
        return buf;
    }
    
    public final void returnObjectBuffer(final ObjectBuffer buf) {
        if (this._objectBuffer == null || buf.initialCapacity() >= this._objectBuffer.initialCapacity()) {
            this._objectBuffer = buf;
        }
    }
    
    public final ArrayBuilders getArrayBuilders() {
        if (this._arrayBuilders == null) {
            this._arrayBuilders = new ArrayBuilders();
        }
        return this._arrayBuilders;
    }
    
    public abstract JsonDeserializer<Object> deserializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    public abstract KeyDeserializer keyDeserializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, final BeanProperty prop, final JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode<JavaType>(type, this._currentType);
            try {
                deser = ((ContextualDeserializer)deser).createContextual(this, prop);
            }
            finally {
                this._currentType = this._currentType.next();
            }
        }
        return deser;
    }
    
    public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, final BeanProperty prop, final JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode<JavaType>(type, this._currentType);
            try {
                deser = ((ContextualDeserializer)deser).createContextual(this, prop);
            }
            finally {
                this._currentType = this._currentType.next();
            }
        }
        return deser;
    }
    
    public Date parseDate(final String dateStr) throws IllegalArgumentException {
        try {
            final DateFormat df = this._getDateFormat();
            return df.parse(dateStr);
        }
        catch (final ParseException e) {
            throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", dateStr, ClassUtil.exceptionMessage(e)));
        }
    }
    
    public Calendar constructCalendar(final Date d) {
        final Calendar c = Calendar.getInstance(this.getTimeZone());
        c.setTime(d);
        return c;
    }
    
    public String extractScalarFromObject(final JsonParser p, final JsonDeserializer<?> deser, final Class<?> scalarType) throws IOException {
        return this.reportInputMismatch(scalarType, String.format("Cannot deserialize value of type %s from %s (token `JsonToken.START_OBJECT`)", ClassUtil.getClassDescription(scalarType), this._shapeForToken(JsonToken.START_OBJECT)), new Object[0]);
    }
    
    public <T> T readValue(final JsonParser p, final Class<T> type) throws IOException {
        return this.readValue(p, this.getTypeFactory().constructType(type));
    }
    
    public <T> T readValue(final JsonParser p, final JavaType type) throws IOException {
        final JsonDeserializer<Object> deser = this.findRootValueDeserializer(type);
        if (deser == null) {
            this.reportBadDefinition(type, "Could not find JsonDeserializer for type " + ClassUtil.getTypeDescription(type));
        }
        return (T)deser.deserialize(p, this);
    }
    
    public <T> T readPropertyValue(final JsonParser p, final BeanProperty prop, final Class<T> type) throws IOException {
        return this.readPropertyValue(p, prop, this.getTypeFactory().constructType(type));
    }
    
    public <T> T readPropertyValue(final JsonParser p, final BeanProperty prop, final JavaType type) throws IOException {
        final JsonDeserializer<Object> deser = this.findContextualValueDeserializer(type, prop);
        if (deser == null) {
            return this.reportBadDefinition(type, String.format("Could not find JsonDeserializer for type %s (via property %s)", ClassUtil.getTypeDescription(type), ClassUtil.nameOf(prop)));
        }
        return (T)deser.deserialize(p, this);
    }
    
    public JsonNode readTree(final JsonParser p) throws IOException {
        JsonToken t = p.currentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return this.getNodeFactory().missingNode();
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNodeFactory().nullNode();
        }
        return this.findRootValueDeserializer(this._config.constructType(JsonNode.class)).deserialize(p, this);
    }
    
    public boolean handleUnknownProperty(final JsonParser p, final JsonDeserializer<?> deser, final Object instanceOrClass, final String propName) throws IOException {
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            if (h.value().handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
                return true;
            }
        }
        if (!this.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            p.skipChildren();
            return true;
        }
        final Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
        throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, propIds);
    }
    
    public Object handleWeirdKey(final Class<?> keyClass, final String keyValue, String msg, final Object... msgArgs) throws IOException {
        msg = this._format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            final Object key = h.value().handleWeirdKey(this, keyClass, keyValue, msg);
            if (key != DeserializationProblemHandler.NOT_HANDLED) {
                if (key == null || keyClass.isInstance(key)) {
                    return key;
                }
                throw this.weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", ClassUtil.getClassDescription(keyClass), ClassUtil.getClassDescription(key)));
            }
            else {
                h = h.next();
            }
        }
        throw this.weirdKeyException(keyClass, keyValue, msg);
    }
    
    public Object handleWeirdStringValue(final Class<?> targetClass, final String value, String msg, final Object... msgArgs) throws IOException {
        msg = this._format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            final Object instance = h.value().handleWeirdStringValue(this, targetClass, value, msg);
            if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                if (this._isCompatible(targetClass, instance)) {
                    return instance;
                }
                throw this.weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", ClassUtil.getClassDescription(targetClass), ClassUtil.getClassDescription(instance)));
            }
            else {
                h = h.next();
            }
        }
        throw this.weirdStringException(value, targetClass, msg);
    }
    
    public Object handleWeirdNumberValue(final Class<?> targetClass, final Number value, String msg, final Object... msgArgs) throws IOException {
        msg = this._format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            final Object key = h.value().handleWeirdNumberValue(this, targetClass, value, msg);
            if (key != DeserializationProblemHandler.NOT_HANDLED) {
                if (this._isCompatible(targetClass, key)) {
                    return key;
                }
                throw this.weirdNumberException(value, targetClass, this._format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", ClassUtil.getClassDescription(targetClass), ClassUtil.getClassDescription(key)));
            }
            else {
                h = h.next();
            }
        }
        throw this.weirdNumberException(value, targetClass, msg);
    }
    
    public Object handleWeirdNativeValue(final JavaType targetType, final Object badValue, final JsonParser p) throws IOException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        final Class<?> raw = targetType.getRawClass();
        while (h != null) {
            final Object goodValue = h.value().handleWeirdNativeValue(this, targetType, badValue, p);
            if (goodValue != DeserializationProblemHandler.NOT_HANDLED) {
                if (goodValue == null || raw.isInstance(goodValue)) {
                    return goodValue;
                }
                throw JsonMappingException.from(p, this._format("DeserializationProblemHandler.handleWeirdNativeValue() for type %s returned value of type %s", ClassUtil.getClassDescription(targetType), ClassUtil.getClassDescription(goodValue)));
            }
            else {
                h = h.next();
            }
        }
        throw this.weirdNativeValueException(badValue, raw);
    }
    
    public Object handleMissingInstantiator(final Class<?> instClass, final ValueInstantiator valueInst, JsonParser p, String msg, final Object... msgArgs) throws IOException {
        if (p == null) {
            p = this.getParser();
        }
        msg = this._format(msg, msgArgs);
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            final Object instance = h.value().handleMissingInstantiator(this, instClass, valueInst, p, msg);
            if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                if (this._isCompatible(instClass, instance)) {
                    return instance;
                }
                this.reportBadDefinition(this.constructType(instClass), String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", ClassUtil.getClassDescription(instClass), ClassUtil.getClassDescription(instance)));
            }
        }
        if (valueInst == null) {
            msg = String.format("Cannot construct instance of %s: %s", ClassUtil.nameOf(instClass), msg);
            return this.reportBadDefinition(instClass, msg);
        }
        if (!valueInst.canInstantiate()) {
            msg = String.format("Cannot construct instance of %s (no Creators, like default constructor, exist): %s", ClassUtil.nameOf(instClass), msg);
            return this.reportBadDefinition(instClass, msg);
        }
        msg = String.format("Cannot construct instance of %s (although at least one Creator exists): %s", ClassUtil.nameOf(instClass), msg);
        return this.reportInputMismatch(instClass, msg, new Object[0]);
    }
    
    public Object handleInstantiationProblem(final Class<?> instClass, final Object argument, final Throwable t) throws IOException {
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            final Object instance = h.value().handleInstantiationProblem(this, instClass, argument, t);
            if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                if (this._isCompatible(instClass, instance)) {
                    return instance;
                }
                this.reportBadDefinition(this.constructType(instClass), String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", ClassUtil.getClassDescription(instClass), ClassUtil.classNameOf(instance)));
            }
        }
        ClassUtil.throwIfIOE(t);
        if (!this.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
            ClassUtil.throwIfRTE(t);
        }
        throw this.instantiationException(instClass, t);
    }
    
    public Object handleUnexpectedToken(final Class<?> instClass, final JsonParser p) throws IOException {
        return this.handleUnexpectedToken(this.constructType(instClass), p.currentToken(), p, null, new Object[0]);
    }
    
    public Object handleUnexpectedToken(final Class<?> instClass, final JsonToken t, final JsonParser p, final String msg, final Object... msgArgs) throws IOException {
        return this.handleUnexpectedToken(this.constructType(instClass), t, p, msg, msgArgs);
    }
    
    public Object handleUnexpectedToken(final JavaType targetType, final JsonParser p) throws IOException {
        return this.handleUnexpectedToken(targetType, p.currentToken(), p, null, new Object[0]);
    }
    
    public Object handleUnexpectedToken(final JavaType targetType, final JsonToken t, final JsonParser p, String msg, final Object... msgArgs) throws IOException {
        msg = this._format(msg, msgArgs);
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            final Object instance = h.value().handleUnexpectedToken(this, targetType, t, p, msg);
            if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                if (this._isCompatible(targetType.getRawClass(), instance)) {
                    return instance;
                }
                this.reportBadDefinition(targetType, String.format("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", ClassUtil.getTypeDescription(targetType), ClassUtil.classNameOf(instance)));
            }
        }
        if (msg == null) {
            final String targetDesc = ClassUtil.getTypeDescription(targetType);
            if (t == null) {
                msg = String.format("Unexpected end-of-input when trying read value of type %s", targetDesc);
            }
            else {
                msg = String.format("Cannot deserialize value of type %s from %s (token `JsonToken.%s`)", targetDesc, this._shapeForToken(t), t);
            }
        }
        if (t != null && t.isScalarValue()) {
            p.getText();
        }
        this.reportInputMismatch(targetType, msg, new Object[0]);
        return null;
    }
    
    public JavaType handleUnknownTypeId(final JavaType baseType, final String id, final TypeIdResolver idResolver, final String extraDesc) throws IOException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            final JavaType type = h.value().handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
            if (type != null) {
                if (type.hasRawClass(Void.class)) {
                    return null;
                }
                if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
                    return type;
                }
                throw this.invalidTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + ClassUtil.getTypeDescription(type));
            }
            else {
                h = h.next();
            }
        }
        if (!this.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
            return null;
        }
        throw this.invalidTypeIdException(baseType, id, extraDesc);
    }
    
    public JavaType handleMissingTypeId(final JavaType baseType, final TypeIdResolver idResolver, final String extraDesc) throws IOException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            final JavaType type = h.value().handleMissingTypeId(this, baseType, idResolver, extraDesc);
            if (type != null) {
                if (type.hasRawClass(Void.class)) {
                    return null;
                }
                if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
                    return type;
                }
                throw this.invalidTypeIdException(baseType, null, "problem handler tried to resolve into non-subtype: " + ClassUtil.getTypeDescription(type));
            }
            else {
                h = h.next();
            }
        }
        throw this.missingTypeIdException(baseType, extraDesc);
    }
    
    public void handleBadMerge(final JsonDeserializer<?> deser) throws JsonMappingException {
        if (!this.isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
            final JavaType type = this.constructType(deser.handledType());
            final String msg = String.format("Invalid configuration: values of type %s cannot be merged", ClassUtil.getTypeDescription(type));
            throw InvalidDefinitionException.from(this.getParser(), msg, type);
        }
    }
    
    protected boolean _isCompatible(final Class<?> target, final Object value) {
        return value == null || target.isInstance(value) || (target.isPrimitive() && ClassUtil.wrapperType(target).isInstance(value));
    }
    
    public void reportWrongTokenException(final JsonDeserializer<?> deser, final JsonToken expToken, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw this.wrongTokenException(this.getParser(), deser.handledType(), expToken, msg);
    }
    
    public void reportWrongTokenException(final JavaType targetType, final JsonToken expToken, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw this.wrongTokenException(this.getParser(), targetType, expToken, msg);
    }
    
    public void reportWrongTokenException(final Class<?> targetType, final JsonToken expToken, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw this.wrongTokenException(this.getParser(), targetType, expToken, msg);
    }
    
    public <T> T reportUnresolvedObjectId(final ObjectIdReader oidReader, final Object bean) throws JsonMappingException {
        final String msg = String.format("No Object Id found for an instance of %s, to assign to property '%s'", ClassUtil.classNameOf(bean), oidReader.propertyName);
        return this.reportInputMismatch(oidReader.idProperty, msg, new Object[0]);
    }
    
    public <T> T reportInputMismatch(final JsonDeserializer<?> src, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw MismatchedInputException.from(this.getParser(), src.handledType(), msg);
    }
    
    public <T> T reportInputMismatch(final Class<?> targetType, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw MismatchedInputException.from(this.getParser(), targetType, msg);
    }
    
    public <T> T reportInputMismatch(final JavaType targetType, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw MismatchedInputException.from(this.getParser(), targetType, msg);
    }
    
    public <T> T reportInputMismatch(final BeanProperty prop, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        final JavaType type = (prop == null) ? null : prop.getType();
        final MismatchedInputException e = MismatchedInputException.from(this.getParser(), type, msg);
        if (prop != null) {
            final AnnotatedMember member = prop.getMember();
            if (member != null) {
                e.prependPath(member.getDeclaringClass(), prop.getName());
            }
        }
        throw e;
    }
    
    public <T> T reportPropertyInputMismatch(final Class<?> targetType, final String propertyName, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        final MismatchedInputException e = MismatchedInputException.from(this.getParser(), targetType, msg);
        if (propertyName != null) {
            e.prependPath(targetType, propertyName);
        }
        throw e;
    }
    
    public <T> T reportPropertyInputMismatch(final JavaType targetType, final String propertyName, final String msg, final Object... msgArgs) throws JsonMappingException {
        return this.reportPropertyInputMismatch(targetType.getRawClass(), propertyName, msg, msgArgs);
    }
    
    public <T> T reportBadCoercion(final JsonDeserializer<?> src, final Class<?> targetType, final Object inputValue, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        final InvalidFormatException e = InvalidFormatException.from(this.getParser(), msg, inputValue, targetType);
        throw e;
    }
    
    public <T> T reportTrailingTokens(final Class<?> targetType, final JsonParser p, final JsonToken trailingToken) throws JsonMappingException {
        throw MismatchedInputException.from(p, targetType, String.format("Trailing token (of type %s) found after value (bound as %s): not allowed as per `DeserializationFeature.FAIL_ON_TRAILING_TOKENS`", trailingToken, ClassUtil.nameOf(targetType)));
    }
    
    @Deprecated
    public void reportWrongTokenException(final JsonParser p, final JsonToken expToken, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        throw this.wrongTokenException(p, expToken, msg);
    }
    
    @Deprecated
    public void reportUnknownProperty(final Object instanceOrClass, final String fieldName, final JsonDeserializer<?> deser) throws JsonMappingException {
        if (this.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            final Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
            throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
        }
    }
    
    @Deprecated
    public void reportMissingContent(final String msg, final Object... msgArgs) throws JsonMappingException {
        throw MismatchedInputException.from(this.getParser(), (JavaType)null, "No content to map due to end-of-input");
    }
    
    public <T> T reportBadTypeDefinition(final BeanDescription bean, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        final String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        msg = String.format("Invalid type definition for type %s: %s", beanDesc, msg);
        throw InvalidDefinitionException.from(this._parser, msg, bean, null);
    }
    
    public <T> T reportBadPropertyDefinition(final BeanDescription bean, final BeanPropertyDefinition prop, String msg, final Object... msgArgs) throws JsonMappingException {
        msg = this._format(msg, msgArgs);
        final String propName = ClassUtil.nameOf(prop);
        final String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        msg = String.format("Invalid definition for property %s (of type %s): %s", propName, beanDesc, msg);
        throw InvalidDefinitionException.from(this._parser, msg, bean, prop);
    }
    
    @Override
    public <T> T reportBadDefinition(final JavaType type, final String msg) throws JsonMappingException {
        throw InvalidDefinitionException.from(this._parser, msg, type);
    }
    
    @Deprecated
    public <T> T reportBadMerge(final JsonDeserializer<?> deser) throws JsonMappingException {
        this.handleBadMerge(deser);
        return null;
    }
    
    public JsonMappingException wrongTokenException(final JsonParser p, final JavaType targetType, final JsonToken expToken, final String extra) {
        String msg = String.format("Unexpected token (%s), expected %s", p.currentToken(), expToken);
        msg = this._colonConcat(msg, extra);
        return MismatchedInputException.from(p, targetType, msg);
    }
    
    public JsonMappingException wrongTokenException(final JsonParser p, final Class<?> targetType, final JsonToken expToken, final String extra) {
        String msg = String.format("Unexpected token (%s), expected %s", p.currentToken(), expToken);
        msg = this._colonConcat(msg, extra);
        return MismatchedInputException.from(p, targetType, msg);
    }
    
    @Deprecated
    public JsonMappingException wrongTokenException(final JsonParser p, final JsonToken expToken, final String msg) {
        return this.wrongTokenException(p, (JavaType)null, expToken, msg);
    }
    
    public JsonMappingException weirdKeyException(final Class<?> keyClass, final String keyValue, final String msg) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize Map key of type %s from String %s: %s", ClassUtil.nameOf(keyClass), this._quotedString(keyValue), msg), keyValue, keyClass);
    }
    
    public JsonMappingException weirdStringException(final String value, final Class<?> instClass, final String msgBase) {
        final String msg = String.format("Cannot deserialize value of type %s from String %s: %s", ClassUtil.nameOf(instClass), this._quotedString(value), msgBase);
        return InvalidFormatException.from(this._parser, msg, value, instClass);
    }
    
    public JsonMappingException weirdNumberException(final Number value, final Class<?> instClass, final String msg) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from number %s: %s", ClassUtil.nameOf(instClass), String.valueOf(value), msg), value, instClass);
    }
    
    public JsonMappingException weirdNativeValueException(final Object value, final Class<?> instClass) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from native value (`JsonToken.VALUE_EMBEDDED_OBJECT`) of type %s: incompatible types", ClassUtil.nameOf(instClass), ClassUtil.classNameOf(value)), value, instClass);
    }
    
    public JsonMappingException instantiationException(final Class<?> instClass, final Throwable cause) {
        String excMsg;
        if (cause == null) {
            excMsg = "N/A";
        }
        else if ((excMsg = ClassUtil.exceptionMessage(cause)) == null) {
            excMsg = ClassUtil.nameOf(cause.getClass());
        }
        final String msg = String.format("Cannot construct instance of %s, problem: %s", ClassUtil.nameOf(instClass), excMsg);
        return ValueInstantiationException.from(this._parser, msg, this.constructType(instClass), cause);
    }
    
    public JsonMappingException instantiationException(final Class<?> instClass, final String msg0) {
        return ValueInstantiationException.from(this._parser, String.format("Cannot construct instance of %s: %s", ClassUtil.nameOf(instClass), msg0), this.constructType(instClass));
    }
    
    public JsonMappingException invalidTypeIdException(final JavaType baseType, final String typeId, final String extraDesc) {
        final String msg = String.format("Could not resolve type id '%s' as a subtype of %s", typeId, ClassUtil.getTypeDescription(baseType));
        return InvalidTypeIdException.from(this._parser, this._colonConcat(msg, extraDesc), baseType, typeId);
    }
    
    public JsonMappingException missingTypeIdException(final JavaType baseType, final String extraDesc) {
        final String msg = String.format("Missing type id when trying to resolve subtype of %s", baseType);
        return InvalidTypeIdException.from(this._parser, this._colonConcat(msg, extraDesc), baseType, null);
    }
    
    @Deprecated
    public JsonMappingException unknownTypeException(final JavaType type, final String id, final String extraDesc) {
        String msg = String.format("Could not resolve type id '%s' into a subtype of %s", id, ClassUtil.getTypeDescription(type));
        msg = this._colonConcat(msg, extraDesc);
        return MismatchedInputException.from(this._parser, type, msg);
    }
    
    @Deprecated
    public JsonMappingException endOfInputException(final Class<?> instClass) {
        return MismatchedInputException.from(this._parser, instClass, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
    }
    
    @Deprecated
    public void reportMappingException(final String msg, final Object... msgArgs) throws JsonMappingException {
        throw JsonMappingException.from(this.getParser(), this._format(msg, msgArgs));
    }
    
    @Deprecated
    public JsonMappingException mappingException(final String message) {
        return JsonMappingException.from(this.getParser(), message);
    }
    
    @Deprecated
    public JsonMappingException mappingException(final String msg, final Object... msgArgs) {
        return JsonMappingException.from(this.getParser(), this._format(msg, msgArgs));
    }
    
    @Deprecated
    public JsonMappingException mappingException(final Class<?> targetClass) {
        return this.mappingException(targetClass, this._parser.currentToken());
    }
    
    @Deprecated
    public JsonMappingException mappingException(final Class<?> targetClass, final JsonToken token) {
        return JsonMappingException.from(this._parser, String.format("Cannot deserialize instance of %s out of %s token", ClassUtil.nameOf(targetClass), token));
    }
    
    @Deprecated
    protected DateFormat getDateFormat() {
        return this._getDateFormat();
    }
    
    protected DateFormat _getDateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = this._config.getDateFormat();
        df = (this._dateFormat = (DateFormat)df.clone());
        return df;
    }
    
    protected String _shapeForToken(final JsonToken t) {
        if (t == null) {
            return "<end of input>";
        }
        switch (t) {
            case START_OBJECT:
            case END_OBJECT:
            case FIELD_NAME: {
                return "Object value";
            }
            case START_ARRAY:
            case END_ARRAY: {
                return "Array value";
            }
            case VALUE_FALSE:
            case VALUE_TRUE: {
                return "Boolean value";
            }
            case VALUE_EMBEDDED_OBJECT: {
                return "Embedded Object";
            }
            case VALUE_NUMBER_FLOAT: {
                return "Floating-point value";
            }
            case VALUE_NUMBER_INT: {
                return "Integer value";
            }
            case VALUE_STRING: {
                return "String value";
            }
            case VALUE_NULL: {
                return "Null value";
            }
            default: {
                return "[Unavailable value]";
            }
        }
    }
}