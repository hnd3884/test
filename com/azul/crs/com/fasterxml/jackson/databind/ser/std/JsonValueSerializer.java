package com.azul.crs.com.fasterxml.jackson.databind.ser.std;

import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.azul.crs.com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.azul.crs.com.fasterxml.jackson.core.type.WritableTypeId;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.azul.crs.com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable, SchemaAware
{
    protected final AnnotatedMember _accessor;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final BeanProperty _property;
    protected final JavaType _valueType;
    protected final boolean _forceTypeInformation;
    protected transient PropertySerializerMap _dynamicSerializers;
    
    public JsonValueSerializer(final AnnotatedMember accessor, final TypeSerializer vts, final JsonSerializer<?> ser) {
        super(accessor.getType());
        this._accessor = accessor;
        this._valueType = accessor.getType();
        this._valueTypeSerializer = vts;
        this._valueSerializer = (JsonSerializer<Object>)ser;
        this._property = null;
        this._forceTypeInformation = true;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }
    
    @Deprecated
    public JsonValueSerializer(final AnnotatedMember accessor, final JsonSerializer<?> ser) {
        this(accessor, null, ser);
    }
    
    public JsonValueSerializer(final JsonValueSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        super(_notNullClass(src.handledType()));
        this._accessor = src._accessor;
        this._valueType = src._valueType;
        this._valueTypeSerializer = vts;
        this._valueSerializer = (JsonSerializer<Object>)ser;
        this._property = property;
        this._forceTypeInformation = forceTypeInfo;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }
    
    private static final Class<Object> _notNullClass(final Class<?> cls) {
        return (Class<Object>)((cls == null) ? Object.class : cls);
    }
    
    protected JsonValueSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        if (this._property == property && this._valueTypeSerializer == vts && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation) {
            return this;
        }
        return new JsonValueSerializer(this, property, vts, ser, forceTypeInfo);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider ctxt, final Object bean) {
        final Object referenced = this._accessor.getValue(bean);
        if (referenced == null) {
            return true;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            try {
                ser = this._findDynamicSerializer(ctxt, referenced.getClass());
            }
            catch (final JsonMappingException e) {
                throw new RuntimeJsonMappingException(e);
            }
        }
        return ser.isEmpty(ctxt, referenced);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider ctxt, final BeanProperty property) throws JsonMappingException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser != null) {
            ser = ctxt.handlePrimaryContextualization(ser, property);
            return this.withResolved(property, typeSer, ser, this._forceTypeInformation);
        }
        if (ctxt.isEnabled(MapperFeature.USE_STATIC_TYPING) || this._valueType.isFinal()) {
            ser = ctxt.findPrimaryPropertySerializer(this._valueType, property);
            final boolean forceTypeInformation = this.isNaturalTypeWithStdHandling(this._valueType.getRawClass(), ser);
            return this.withResolved(property, typeSer, ser, forceTypeInformation);
        }
        if (property != this._property) {
            return this.withResolved(property, typeSer, ser, this._forceTypeInformation);
        }
        return this;
    }
    
    @Override
    public void serialize(final Object bean, final JsonGenerator gen, final SerializerProvider ctxt) throws IOException {
        Object value;
        try {
            value = this._accessor.getValue(bean);
        }
        catch (final Exception e) {
            value = null;
            this.wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
        }
        if (value == null) {
            ctxt.defaultSerializeNull(gen);
        }
        else {
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = this._findDynamicSerializer(ctxt, value.getClass());
            }
            if (this._valueTypeSerializer != null) {
                ser.serializeWithType(value, gen, ctxt, this._valueTypeSerializer);
            }
            else {
                ser.serialize(value, gen, ctxt);
            }
        }
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider ctxt, final TypeSerializer typeSer0) throws IOException {
        Object value;
        try {
            value = this._accessor.getValue(bean);
        }
        catch (final Exception e) {
            value = null;
            this.wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
        }
        if (value == null) {
            ctxt.defaultSerializeNull(gen);
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = this._findDynamicSerializer(ctxt, value.getClass());
        }
        else if (this._forceTypeInformation) {
            final WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0.typeId(bean, JsonToken.VALUE_STRING));
            ser.serialize(value, gen, ctxt);
            typeSer0.writeTypeSuffix(gen, typeIdDef);
            return;
        }
        final TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
        ser.serializeWithType(value, gen, ctxt, rr);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider ctxt, final Type typeHint) throws JsonMappingException {
        if (this._valueSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._valueSerializer).getSchema(ctxt, null);
        }
        return JsonSchema.getDefaultSchemaNode();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final Class<?> declaring = this._accessor.getDeclaringClass();
        if (declaring != null && ClassUtil.isEnumType(declaring) && this._acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = visitor.getProvider().findTypedValueSerializer(this._valueType, false, this._property);
            if (ser == null) {
                visitor.expectAnyFormat(typeHint);
                return;
            }
        }
        ser.acceptJsonFormatVisitor(visitor, this._valueType);
    }
    
    protected boolean _acceptJsonFormatVisitorForEnum(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final Class<?> enumType) throws JsonMappingException {
        final JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
        if (stringVisitor != null) {
            final Set<String> enums = new LinkedHashSet<String>();
            for (final Object en : enumType.getEnumConstants()) {
                try {
                    enums.add(String.valueOf(this._accessor.getValue(en)));
                }
                catch (final Exception e) {
                    Throwable t;
                    for (t = e; t instanceof InvocationTargetException && t.getCause() != null; t = t.getCause()) {}
                    ClassUtil.throwIfError(t);
                    throw JsonMappingException.wrapWithPath(t, en, this._accessor.getName() + "()");
                }
            }
            stringVisitor.enumTypes(enums);
        }
        return true;
    }
    
    protected boolean isNaturalTypeWithStdHandling(final Class<?> rawType, final JsonSerializer<?> ser) {
        if (rawType.isPrimitive()) {
            if (rawType != Integer.TYPE && rawType != Boolean.TYPE && rawType != Double.TYPE) {
                return false;
            }
        }
        else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
            return false;
        }
        return this.isDefaultSerializer(ser);
    }
    
    protected JsonSerializer<Object> _findDynamicSerializer(final SerializerProvider ctxt, final Class<?> valueClass) throws JsonMappingException {
        JsonSerializer<Object> serializer = this._dynamicSerializers.serializerFor(valueClass);
        if (serializer == null) {
            if (this._valueType.hasGenericTypes()) {
                final JavaType fullType = ctxt.constructSpecializedType(this._valueType, valueClass);
                serializer = ctxt.findPrimaryPropertySerializer(fullType, this._property);
                final PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(fullType, serializer);
                this._dynamicSerializers = result.map;
            }
            else {
                serializer = ctxt.findPrimaryPropertySerializer(valueClass, this._property);
                final PropertySerializerMap.SerializerAndMapResult result2 = this._dynamicSerializers.addSerializer(valueClass, serializer);
                this._dynamicSerializers = result2.map;
            }
        }
        return serializer;
    }
    
    @Override
    public String toString() {
        return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
    }
    
    static class TypeSerializerRerouter extends TypeSerializer
    {
        protected final TypeSerializer _typeSerializer;
        protected final Object _forObject;
        
        public TypeSerializerRerouter(final TypeSerializer ts, final Object ob) {
            this._typeSerializer = ts;
            this._forObject = ob;
        }
        
        @Override
        public TypeSerializer forProperty(final BeanProperty prop) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public JsonTypeInfo.As getTypeInclusion() {
            return this._typeSerializer.getTypeInclusion();
        }
        
        @Override
        public String getPropertyName() {
            return this._typeSerializer.getPropertyName();
        }
        
        @Override
        public TypeIdResolver getTypeIdResolver() {
            return this._typeSerializer.getTypeIdResolver();
        }
        
        @Override
        public WritableTypeId writeTypePrefix(final JsonGenerator g, final WritableTypeId typeId) throws IOException {
            typeId.forValue = this._forObject;
            return this._typeSerializer.writeTypePrefix(g, typeId);
        }
        
        @Override
        public WritableTypeId writeTypeSuffix(final JsonGenerator g, final WritableTypeId typeId) throws IOException {
            return this._typeSerializer.writeTypeSuffix(g, typeId);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForScalar(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForObject(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForArray(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForScalar(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForObject(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForArray(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForScalar(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForObject(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForArray(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForScalar(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForArray(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForScalar(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForArray(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
        }
    }
}
