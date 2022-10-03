package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.azul.crs.com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIncludeProperties;
import java.util.Iterator;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Collection;
import java.util.HashSet;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Map;

@JacksonStdImpl
public class MapDeserializer extends ContainerDeserializerBase<Map<Object, Object>> implements ContextualDeserializer, ResolvableDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final KeyDeserializer _keyDeserializer;
    protected boolean _standardStringKey;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected final boolean _hasDefaultCreator;
    protected Set<String> _ignorableProperties;
    protected Set<String> _includableProperties;
    protected IgnorePropertiesUtil.Checker _inclusionChecker;
    
    public MapDeserializer(final JavaType mapType, final ValueInstantiator valueInstantiator, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser) {
        super(mapType, null, null);
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
        this._delegateDeserializer = null;
        this._propertyBasedCreator = null;
        this._standardStringKey = this._isStdKeyDeser(mapType, keyDeser);
        this._inclusionChecker = null;
    }
    
    protected MapDeserializer(final MapDeserializer src) {
        super(src);
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = src._ignorableProperties;
        this._includableProperties = src._includableProperties;
        this._inclusionChecker = src._inclusionChecker;
        this._standardStringKey = src._standardStringKey;
    }
    
    protected MapDeserializer(final MapDeserializer src, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final NullValueProvider nuller, final Set<String> ignorable) {
        this(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, null);
    }
    
    protected MapDeserializer(final MapDeserializer src, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final NullValueProvider nuller, final Set<String> ignorable, final Set<String> includable) {
        super(src, nuller, src._unwrapSingle);
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = ignorable;
        this._includableProperties = includable;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(ignorable, includable);
        this._standardStringKey = this._isStdKeyDeser(this._containerType, keyDeser);
    }
    
    protected MapDeserializer withResolved(final KeyDeserializer keyDeser, final TypeDeserializer valueTypeDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Set<String> ignorable) {
        return this.withResolved(keyDeser, valueTypeDeser, valueDeser, nuller, ignorable, this._includableProperties);
    }
    
    protected MapDeserializer withResolved(final KeyDeserializer keyDeser, final TypeDeserializer valueTypeDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Set<String> ignorable, final Set<String> includable) {
        if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser && this._nullProvider == nuller && this._ignorableProperties == ignorable && this._includableProperties == includable) {
            return this;
        }
        return new MapDeserializer(this, keyDeser, (JsonDeserializer<Object>)valueDeser, valueTypeDeser, nuller, ignorable, includable);
    }
    
    protected final boolean _isStdKeyDeser(final JavaType mapType, final KeyDeserializer keyDeser) {
        if (keyDeser == null) {
            return true;
        }
        final JavaType keyType = mapType.getKeyType();
        if (keyType == null) {
            return true;
        }
        final Class<?> rawKeyType = keyType.getRawClass();
        return (rawKeyType == String.class || rawKeyType == Object.class) && this.isDefaultKeyDeserializer(keyDeser);
    }
    
    @Deprecated
    public void setIgnorableProperties(final String[] ignorable) {
        this._ignorableProperties = ((ignorable == null || ignorable.length == 0) ? null : ArrayBuilders.arrayToSet(ignorable));
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }
    
    public void setIgnorableProperties(final Set<String> ignorable) {
        this._ignorableProperties = ((ignorable == null || ignorable.size() == 0) ? null : ignorable);
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }
    
    public void setIncludableProperties(final Set<String> includable) {
        this._includableProperties = includable;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }
            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
        }
        else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }
            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
        }
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            final SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        }
        this._standardStringKey = this._isStdKeyDeser(this._containerType, this._keyDeserializer);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        KeyDeserializer keyDeser = this._keyDeserializer;
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
        }
        else if (keyDeser instanceof ContextualKeyDeserializer) {
            keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, property);
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        if (property != null) {
            valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
        }
        final JavaType vt = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        Set<String> ignored = this._ignorableProperties;
        Set<String> included = this._includableProperties;
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (StdDeserializer._neitherNull(intr, property)) {
            final AnnotatedMember member = property.getMember();
            if (member != null) {
                final DeserializationConfig config = ctxt.getConfig();
                final JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(config, member);
                if (ignorals != null) {
                    final Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
                    if (!ignoresToAdd.isEmpty()) {
                        ignored = ((ignored == null) ? new HashSet<String>() : new HashSet<String>(ignored));
                        for (final String str : ignoresToAdd) {
                            ignored.add(str);
                        }
                    }
                }
                final JsonIncludeProperties.Value inclusions = intr.findPropertyInclusionByName(config, member);
                if (inclusions != null) {
                    final Set<String> includedToAdd = inclusions.getIncluded();
                    if (includedToAdd != null) {
                        Set<String> newIncluded = new HashSet<String>();
                        if (included == null) {
                            newIncluded = new HashSet<String>(includedToAdd);
                        }
                        else {
                            for (final String str2 : includedToAdd) {
                                if (included.contains(str2)) {
                                    newIncluded.add(str2);
                                }
                            }
                        }
                        included = newIncluded;
                    }
                }
            }
        }
        return this.withResolved(keyDeser, vtd, valueDeser, this.findContentNullProvider(ctxt, property, valueDeser), ignored, included);
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public boolean isCachable() {
        return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null && this._ignorableProperties == null && this._includableProperties == null;
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Map;
    }
    
    @Override
    public Map<Object, Object> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingCreator(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (Map)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (!this._hasDefaultCreator) {
            return (Map)ctxt.handleMissingInstantiator(this.getMapClass(), this.getValueInstantiator(), p, "no default constructor found", new Object[0]);
        }
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5: {
                final Map<Object, Object> result = (Map<Object, Object>)this._valueInstantiator.createUsingDefault(ctxt);
                if (this._standardStringKey) {
                    this._readAndBindStringKeyMap(p, ctxt, result);
                    return result;
                }
                this._readAndBind(p, ctxt, result);
                return result;
            }
            case 6: {
                return this._deserializeFromString(p, ctxt);
            }
            case 3: {
                return this._deserializeFromArray(p, ctxt);
            }
            default: {
                return (Map)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            }
        }
    }
    
    @Override
    public Map<Object, Object> deserialize(final JsonParser p, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException {
        p.setCurrentValue(result);
        final JsonToken t = p.currentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            return (Map)ctxt.handleUnexpectedToken(this.getMapClass(), p);
        }
        if (this._standardStringKey) {
            this._readAndUpdateStringKeyMap(p, ctxt, result);
            return result;
        }
        this._readAndUpdate(p, ctxt, result);
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
    
    public final Class<?> getMapClass() {
        return this._containerType.getRawClass();
    }
    
    @Override
    public JavaType getValueType() {
        return this._containerType;
    }
    
    protected final void _readAndBind(final JsonParser p, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException {
        final KeyDeserializer keyDes = this._keyDeserializer;
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        final boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        }
        String keyStr;
        if (p.isExpectedStartObjectToken()) {
            keyStr = p.nextFieldName();
        }
        else {
            final JsonToken t = p.currentToken();
            if (t != JsonToken.FIELD_NAME) {
                if (t == JsonToken.END_OBJECT) {
                    return;
                }
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
            }
            keyStr = p.currentName();
        }
        while (keyStr != null) {
            final Object key = keyDes.deserializeKey(keyStr, ctxt);
            final JsonToken t2 = p.nextToken();
            Label_0287: {
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
                    p.skipChildren();
                }
                else {
                    try {
                        Object value;
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                break Label_0287;
                            }
                            value = this._nullProvider.getNullValue(ctxt);
                        }
                        else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        }
                        else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (useObjectId) {
                            referringAccumulator.put(key, value);
                        }
                        else {
                            result.put(key, value);
                        }
                    }
                    catch (final UnresolvedForwardReference reference) {
                        this.handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
                    }
                    catch (final Exception e) {
                        ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e, result, keyStr);
                    }
                }
            }
            keyStr = p.nextFieldName();
        }
    }
    
    protected final void _readAndBindStringKeyMap(final JsonParser p, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException {
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        final boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        }
        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        }
        else {
            final JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
            }
            key = p.currentName();
        }
        while (key != null) {
            final JsonToken t = p.nextToken();
            Label_0271: {
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                    p.skipChildren();
                }
                else {
                    try {
                        Object value;
                        if (t == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                break Label_0271;
                            }
                            value = this._nullProvider.getNullValue(ctxt);
                        }
                        else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        }
                        else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (useObjectId) {
                            referringAccumulator.put(key, value);
                        }
                        else {
                            result.put(key, value);
                        }
                    }
                    catch (final UnresolvedForwardReference reference) {
                        this.handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
                    }
                    catch (final Exception e) {
                        ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e, result, key);
                    }
                }
            }
            key = p.nextFieldName();
        }
    }
    
    public Map<Object, Object> _deserializeUsingCreator(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        }
        else if (p.hasToken(JsonToken.FIELD_NAME)) {
            key = p.currentName();
        }
        else {
            key = null;
        }
        while (key != null) {
            final JsonToken t = p.nextToken();
            Label_0289: {
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                    p.skipChildren();
                }
                else {
                    final SettableBeanProperty prop = creator.findCreatorProperty(key);
                    if (prop != null) {
                        if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                            p.nextToken();
                            Map<Object, Object> result;
                            try {
                                result = (Map)creator.build(ctxt, buffer);
                            }
                            catch (final Exception e) {
                                return this.wrapAndThrow(e, this._containerType.getRawClass(), key);
                            }
                            this._readAndBind(p, ctxt, result);
                            return result;
                        }
                    }
                    else {
                        final Object actualKey = this._keyDeserializer.deserializeKey(key, ctxt);
                        Object value;
                        try {
                            if (t == JsonToken.VALUE_NULL) {
                                if (this._skipNullValues) {
                                    break Label_0289;
                                }
                                value = this._nullProvider.getNullValue(ctxt);
                            }
                            else if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt);
                            }
                            else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                            }
                        }
                        catch (final Exception e2) {
                            ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e2, this._containerType.getRawClass(), key);
                            return null;
                        }
                        buffer.bufferMapProperty(actualKey, value);
                    }
                }
            }
            key = p.nextFieldName();
        }
        try {
            return (Map)creator.build(ctxt, buffer);
        }
        catch (final Exception e3) {
            ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e3, this._containerType.getRawClass(), key);
            return null;
        }
    }
    
    protected final void _readAndUpdate(final JsonParser p, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException {
        final KeyDeserializer keyDes = this._keyDeserializer;
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        String keyStr;
        if (p.isExpectedStartObjectToken()) {
            keyStr = p.nextFieldName();
        }
        else {
            final JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
            }
            keyStr = p.currentName();
        }
        while (keyStr != null) {
            final Object key = keyDes.deserializeKey(keyStr, ctxt);
            final JsonToken t2 = p.nextToken();
            if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
                p.skipChildren();
            }
            else {
                try {
                    if (t2 == JsonToken.VALUE_NULL) {
                        if (!this._skipNullValues) {
                            result.put(key, this._nullProvider.getNullValue(ctxt));
                        }
                    }
                    else {
                        final Object old = result.get(key);
                        Object value;
                        if (old != null) {
                            if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt, old);
                            }
                            else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                            }
                        }
                        else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        }
                        else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (value != old) {
                            result.put(key, value);
                        }
                    }
                }
                catch (final Exception e) {
                    ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e, result, keyStr);
                }
            }
            keyStr = p.nextFieldName();
        }
    }
    
    protected final void _readAndUpdateStringKeyMap(final JsonParser p, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException {
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        }
        else {
            final JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
            }
            key = p.currentName();
        }
        while (key != null) {
            final JsonToken t = p.nextToken();
            if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                p.skipChildren();
            }
            else {
                try {
                    if (t == JsonToken.VALUE_NULL) {
                        if (!this._skipNullValues) {
                            result.put(key, this._nullProvider.getNullValue(ctxt));
                        }
                    }
                    else {
                        final Object old = result.get(key);
                        Object value;
                        if (old != null) {
                            if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt, old);
                            }
                            else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                            }
                        }
                        else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        }
                        else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (value != old) {
                            result.put(key, value);
                        }
                    }
                }
                catch (final Exception e) {
                    ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e, result, key);
                }
            }
            key = p.nextFieldName();
        }
    }
    
    private void handleUnresolvedReference(final DeserializationContext ctxt, final MapReferringAccumulator accumulator, final Object key, final UnresolvedForwardReference reference) throws JsonMappingException {
        if (accumulator == null) {
            ctxt.reportInputMismatch(this, "Unresolved forward reference but no identity info: " + reference, new Object[0]);
        }
        final ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
        reference.getRoid().appendReferring(referring);
    }
    
    private static final class MapReferringAccumulator
    {
        private final Class<?> _valueType;
        private Map<Object, Object> _result;
        private List<MapReferring> _accumulator;
        
        public MapReferringAccumulator(final Class<?> valueType, final Map<Object, Object> result) {
            this._accumulator = new ArrayList<MapReferring>();
            this._valueType = valueType;
            this._result = result;
        }
        
        public void put(final Object key, final Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.put(key, value);
            }
            else {
                final MapReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.put(key, value);
            }
        }
        
        public ReadableObjectId.Referring handleUnresolvedReference(final UnresolvedForwardReference reference, final Object key) {
            final MapReferring id = new MapReferring(this, reference, this._valueType, key);
            this._accumulator.add(id);
            return id;
        }
        
        public void resolveForwardReference(final Object id, final Object value) throws IOException {
            final Iterator<MapReferring> iterator = this._accumulator.iterator();
            Map<Object, Object> previous = this._result;
            while (iterator.hasNext()) {
                final MapReferring ref = iterator.next();
                if (ref.hasId(id)) {
                    iterator.remove();
                    previous.put(ref.key, value);
                    previous.putAll(ref.next);
                    return;
                }
                previous = ref.next;
            }
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
        }
    }
    
    static class MapReferring extends ReadableObjectId.Referring
    {
        private final MapReferringAccumulator _parent;
        public final Map<Object, Object> next;
        public final Object key;
        
        MapReferring(final MapReferringAccumulator parent, final UnresolvedForwardReference ref, final Class<?> valueType, final Object key) {
            super(ref, valueType);
            this.next = new LinkedHashMap<Object, Object>();
            this._parent = parent;
            this.key = key;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
