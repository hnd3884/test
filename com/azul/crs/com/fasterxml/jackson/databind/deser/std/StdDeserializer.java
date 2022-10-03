package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.exc.StreamReadException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import com.azul.crs.com.fasterxml.jackson.databind.util.AccessPattern;
import com.azul.crs.com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.azul.crs.com.fasterxml.jackson.annotation.Nulls;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyMetadata;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.util.Converter;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import java.util.Map;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.core.exc.InputCoercionException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParseException;
import java.util.Date;
import com.azul.crs.com.fasterxml.jackson.core.io.NumberInput;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;

public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable, ValueInstantiator.Gettable
{
    private static final long serialVersionUID = 1L;
    protected static final int F_MASK_INT_COERCIONS;
    @Deprecated
    protected static final int F_MASK_ACCEPT_ARRAYS;
    protected final Class<?> _valueClass;
    protected final JavaType _valueType;
    
    protected StdDeserializer(final Class<?> vc) {
        this._valueClass = vc;
        this._valueType = null;
    }
    
    protected StdDeserializer(final JavaType valueType) {
        this._valueClass = ((valueType == null) ? Object.class : valueType.getRawClass());
        this._valueType = valueType;
    }
    
    protected StdDeserializer(final StdDeserializer<?> src) {
        this._valueClass = src._valueClass;
        this._valueType = src._valueType;
    }
    
    @Override
    public Class<?> handledType() {
        return this._valueClass;
    }
    
    @Deprecated
    public final Class<?> getValueClass() {
        return this._valueClass;
    }
    
    public JavaType getValueType() {
        return this._valueType;
    }
    
    public JavaType getValueType(final DeserializationContext ctxt) {
        if (this._valueType != null) {
            return this._valueType;
        }
        return ctxt.constructType(this._valueClass);
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return null;
    }
    
    protected boolean isDefaultDeserializer(final JsonDeserializer<?> deserializer) {
        return ClassUtil.isJacksonStdImpl(deserializer);
    }
    
    protected boolean isDefaultKeyDeserializer(final KeyDeserializer keyDeser) {
        return ClassUtil.isJacksonStdImpl(keyDeser);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
    
    protected T _deserializeFromArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final CoercionAction act = this._findCoercionFromEmptyArray(ctxt);
        final boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        if (unwrap || act != CoercionAction.Fail) {
            final JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                switch (act) {
                    case AsEmpty: {
                        return (T)this.getEmptyValue(ctxt);
                    }
                    case AsNull:
                    case TryConvert: {
                        return this.getNullValue(ctxt);
                    }
                }
            }
            else if (unwrap) {
                final T parsed = this._deserializeWrappedValue(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    this.handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;
            }
        }
        return (T)ctxt.handleUnexpectedToken(this.getValueType(ctxt), JsonToken.START_ARRAY, p, null, new Object[0]);
    }
    
    @Deprecated
    protected T _deserializeFromEmpty(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.START_ARRAY) || !ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            return (T)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
        }
        final JsonToken t = p.nextToken();
        if (t == JsonToken.END_ARRAY) {
            return null;
        }
        return (T)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
    }
    
    protected T _deserializeFromString(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ValueInstantiator inst = this.getValueInstantiator();
        final Class<?> rawTargetType = this.handledType();
        String value = p.getValueAsString();
        if (inst != null && inst.canCreateFromString()) {
            return (T)inst.createFromString(ctxt, value);
        }
        if (value.isEmpty()) {
            final CoercionAction act = ctxt.findCoercionAction(this.logicalType(), rawTargetType, CoercionInputShape.EmptyString);
            return (T)this._deserializeFromEmptyString(p, ctxt, act, rawTargetType, "empty String (\"\")");
        }
        if (_isBlank(value)) {
            final CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), rawTargetType, CoercionAction.Fail);
            return (T)this._deserializeFromEmptyString(p, ctxt, act, rawTargetType, "blank String (all whitespace)");
        }
        if (inst != null) {
            value = value.trim();
            if (inst.canCreateFromInt() && ctxt.findCoercionAction(LogicalType.Integer, Integer.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                return (T)inst.createFromInt(ctxt, this._parseIntPrimitive(ctxt, value));
            }
            if (inst.canCreateFromLong() && ctxt.findCoercionAction(LogicalType.Integer, Long.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                return (T)inst.createFromLong(ctxt, this._parseLongPrimitive(ctxt, value));
            }
            if (inst.canCreateFromBoolean() && ctxt.findCoercionAction(LogicalType.Boolean, Boolean.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                final String str = value.trim();
                if ("true".equals(str)) {
                    return (T)inst.createFromBoolean(ctxt, true);
                }
                if ("false".equals(str)) {
                    return (T)inst.createFromBoolean(ctxt, false);
                }
            }
        }
        return (T)ctxt.handleMissingInstantiator(rawTargetType, inst, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
    }
    
    protected Object _deserializeFromEmptyString(final JsonParser p, final DeserializationContext ctxt, final CoercionAction act, final Class<?> rawTargetType, final String desc) throws IOException {
        switch (act) {
            case AsEmpty: {
                return this.getEmptyValue(ctxt);
            }
            case Fail: {
                this._checkCoercionFail(ctxt, act, rawTargetType, "", "empty String (\"\")");
                break;
            }
        }
        return null;
    }
    
    protected T _deserializeWrappedValue(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            final String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
            final T result = (T)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p.currentToken(), p, msg, new Object[0]);
            return result;
        }
        return this.deserialize(p, ctxt);
    }
    
    @Deprecated
    protected final boolean _parseBooleanPrimitive(final DeserializationContext ctxt, final JsonParser p, final Class<?> targetType) throws IOException {
        return this._parseBooleanPrimitive(p, ctxt);
    }
    
    protected final boolean _parseBooleanPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0159: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0159;
                }
                case 7: {
                    return Boolean.TRUE.equals(this._coerceBooleanFromInt(p, ctxt, Boolean.TYPE));
                }
                case 9: {
                    return true;
                }
                case 10: {
                    return false;
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return false;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Boolean.TYPE);
                    break Label_0159;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final boolean parsed = this._parseBooleanPrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return (boolean)ctxt.handleUnexpectedToken(Boolean.TYPE, p);
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Boolean, Boolean.TYPE);
        if (act == CoercionAction.AsNull) {
            this._verifyNullForPrimitive(ctxt);
            return false;
        }
        if (act == CoercionAction.AsEmpty) {
            return false;
        }
        text = text.trim();
        final int len = text.length();
        if (len == 4) {
            if (this._isTrue(text)) {
                return true;
            }
        }
        else if (len == 5 && this._isFalse(text)) {
            return false;
        }
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return false;
        }
        final Boolean b = (Boolean)ctxt.handleWeirdStringValue(Boolean.TYPE, text, "only \"true\"/\"True\"/\"TRUE\" or \"false\"/\"False\"/\"FALSE\" recognized", new Object[0]);
        return Boolean.TRUE.equals(b);
    }
    
    protected boolean _isTrue(final String text) {
        final char c = text.charAt(0);
        if (c == 't') {
            return "true".equals(text);
        }
        return c == 'T' && ("TRUE".equals(text) || "True".equals(text));
    }
    
    protected boolean _isFalse(final String text) {
        final char c = text.charAt(0);
        if (c == 'f') {
            return "false".equals(text);
        }
        return c == 'F' && ("FALSE".equals(text) || "False".equals(text));
    }
    
    protected final Boolean _parseBoolean(final JsonParser p, final DeserializationContext ctxt, final Class<?> targetType) throws IOException {
        String text = null;
        switch (p.currentTokenId()) {
            case 6: {
                text = p.getText();
                break;
            }
            case 7: {
                return this._coerceBooleanFromInt(p, ctxt, targetType);
            }
            case 9: {
                return true;
            }
            case 10: {
                return false;
            }
            case 11: {
                return null;
            }
            case 1: {
                text = ctxt.extractScalarFromObject(p, this, targetType);
                break;
            }
            case 3: {
                return this._deserializeFromArray(p, ctxt);
            }
            default: {
                return (Boolean)ctxt.handleUnexpectedToken(targetType, p);
            }
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Boolean, targetType);
        if (act == CoercionAction.AsNull) {
            return null;
        }
        if (act == CoercionAction.AsEmpty) {
            return false;
        }
        text = text.trim();
        final int len = text.length();
        if (len == 4) {
            if (this._isTrue(text)) {
                return true;
            }
        }
        else if (len == 5 && this._isFalse(text)) {
            return false;
        }
        if (this._checkTextualNull(ctxt, text)) {
            return null;
        }
        return (Boolean)ctxt.handleWeirdStringValue(targetType, text, "only \"true\" or \"false\" recognized", new Object[0]);
    }
    
    protected final byte _parseBytePrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0184: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0184;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, Byte.TYPE);
                    if (act == CoercionAction.AsNull) {
                        return 0;
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return 0;
                    }
                    return p.getByteValue();
                }
                case 7: {
                    return p.getByteValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Byte.TYPE);
                    break Label_0184;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final byte parsed = this._parseBytePrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return (byte)ctxt.handleUnexpectedToken(ctxt.constructType(Byte.TYPE), p);
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Byte.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0;
        }
        int value;
        try {
            value = NumberInput.parseInt(text);
        }
        catch (final IllegalArgumentException iae) {
            return (byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `byte` value", new Object[0]);
        }
        if (this._byteOverflow(value)) {
            return (byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
        }
        return (byte)value;
    }
    
    protected final short _parseShortPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0184: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0184;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, Short.TYPE);
                    if (act == CoercionAction.AsNull) {
                        return 0;
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return 0;
                    }
                    return p.getShortValue();
                }
                case 7: {
                    return p.getShortValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Short.TYPE);
                    break Label_0184;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final short parsed = this._parseShortPrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return (short)ctxt.handleUnexpectedToken(ctxt.constructType(Short.TYPE), p);
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Short.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0;
        }
        int value;
        try {
            value = NumberInput.parseInt(text);
        }
        catch (final IllegalArgumentException iae) {
            return (short)ctxt.handleWeirdStringValue(Short.TYPE, text, "not a valid `short` value", new Object[0]);
        }
        if (this._shortOverflow(value)) {
            return (short)ctxt.handleWeirdStringValue(Short.TYPE, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
        }
        return (short)value;
    }
    
    protected final int _parseIntPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0180: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0180;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, Integer.TYPE);
                    if (act == CoercionAction.AsNull) {
                        return 0;
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return 0;
                    }
                    return p.getValueAsInt();
                }
                case 7: {
                    return p.getIntValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Integer.TYPE);
                    break Label_0180;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final int parsed = this._parseIntPrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return ((Number)ctxt.handleUnexpectedToken(Integer.TYPE, p)).intValue();
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Integer.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0;
        }
        return this._parseIntPrimitive(ctxt, text);
    }
    
    protected final int _parseIntPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            if (text.length() <= 9) {
                return NumberInput.parseInt(text);
            }
            final long l = Long.parseLong(text);
            if (this._intOverflow(l)) {
                final Number v = (Number)ctxt.handleWeirdStringValue(Integer.TYPE, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
                return this._nonNullNumber(v).intValue();
            }
            return (int)l;
        }
        catch (final IllegalArgumentException iae) {
            final Number v2 = (Number)ctxt.handleWeirdStringValue(Integer.TYPE, text, "not a valid `int` value", new Object[0]);
            return this._nonNullNumber(v2).intValue();
        }
    }
    
    protected final Integer _parseInteger(final JsonParser p, final DeserializationContext ctxt, final Class<?> targetType) throws IOException {
        String text = null;
        switch (p.currentTokenId()) {
            case 6: {
                text = p.getText();
                break;
            }
            case 8: {
                final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, targetType);
                if (act == CoercionAction.AsNull) {
                    return this.getNullValue(ctxt);
                }
                if (act == CoercionAction.AsEmpty) {
                    return (Integer)this.getEmptyValue(ctxt);
                }
                return p.getValueAsInt();
            }
            case 7: {
                return p.getIntValue();
            }
            case 11: {
                return this.getNullValue(ctxt);
            }
            case 1: {
                text = ctxt.extractScalarFromObject(p, this, targetType);
                break;
            }
            case 3: {
                return this._deserializeFromArray(p, ctxt);
            }
            default: {
                return (Integer)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            }
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
        if (act == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
        }
        if (act == CoercionAction.AsEmpty) {
            return (Integer)this.getEmptyValue(ctxt);
        }
        text = text.trim();
        if (this._checkTextualNull(ctxt, text)) {
            return this.getNullValue(ctxt);
        }
        return this._parseIntPrimitive(ctxt, text);
    }
    
    protected final long _parseLongPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0180: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0180;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, Long.TYPE);
                    if (act == CoercionAction.AsNull) {
                        return 0L;
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return 0L;
                    }
                    return p.getValueAsLong();
                }
                case 7: {
                    return p.getLongValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0L;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Long.TYPE);
                    break Label_0180;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final long parsed = this._parseLongPrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return ((Number)ctxt.handleUnexpectedToken(Long.TYPE, p)).longValue();
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Long.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0L;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0L;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0L;
        }
        return this._parseLongPrimitive(ctxt, text);
    }
    
    protected final long _parseLongPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            return NumberInput.parseLong(text);
        }
        catch (final IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(Long.TYPE, text, "not a valid `long` value", new Object[0]);
            return this._nonNullNumber(v).longValue();
        }
    }
    
    protected final Long _parseLong(final JsonParser p, final DeserializationContext ctxt, final Class<?> targetType) throws IOException {
        String text = null;
        switch (p.currentTokenId()) {
            case 6: {
                text = p.getText();
                break;
            }
            case 8: {
                final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, targetType);
                if (act == CoercionAction.AsNull) {
                    return this.getNullValue(ctxt);
                }
                if (act == CoercionAction.AsEmpty) {
                    return (Long)this.getEmptyValue(ctxt);
                }
                return p.getValueAsLong();
            }
            case 11: {
                return this.getNullValue(ctxt);
            }
            case 7: {
                return p.getLongValue();
            }
            case 1: {
                text = ctxt.extractScalarFromObject(p, this, targetType);
                break;
            }
            case 3: {
                return this._deserializeFromArray(p, ctxt);
            }
            default: {
                return (Long)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            }
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
        if (act == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
        }
        if (act == CoercionAction.AsEmpty) {
            return (Long)this.getEmptyValue(ctxt);
        }
        text = text.trim();
        if (this._checkTextualNull(ctxt, text)) {
            return this.getNullValue(ctxt);
        }
        return this._parseLongPrimitive(ctxt, text);
    }
    
    protected final float _parseFloatPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0144: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0144;
                }
                case 7:
                case 8: {
                    return p.getFloatValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0.0f;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Float.TYPE);
                    break Label_0144;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final float parsed = this._parseFloatPrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return ((Number)ctxt.handleUnexpectedToken(Float.TYPE, p)).floatValue();
        }
        final Float nan = this._checkFloatSpecialValue(text);
        if (nan != null) {
            return nan;
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Float.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0.0f;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0.0f;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0.0f;
        }
        return this._parseFloatPrimitive(ctxt, text);
    }
    
    protected final float _parseFloatPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            return Float.parseFloat(text);
        }
        catch (final IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(Float.TYPE, text, "not a valid `float` value", new Object[0]);
            return this._nonNullNumber(v).floatValue();
        }
    }
    
    protected Float _checkFloatSpecialValue(final String text) {
        if (!text.isEmpty()) {
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Float.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Float.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Float.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    protected final double _parseDoublePrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        Label_0144: {
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break Label_0144;
                }
                case 7:
                case 8: {
                    return p.getDoubleValue();
                }
                case 11: {
                    this._verifyNullForPrimitive(ctxt);
                    return 0.0;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, Double.TYPE);
                    break Label_0144;
                }
                case 3: {
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        final double parsed = this._parseDoublePrimitive(p, ctxt);
                        this._verifyEndArrayForSingle(p, ctxt);
                        return parsed;
                    }
                    break;
                }
            }
            return ((Number)ctxt.handleUnexpectedToken(Double.TYPE, p)).doubleValue();
        }
        final Double nan = this._checkDoubleSpecialValue(text);
        if (nan != null) {
            return nan;
        }
        final CoercionAction act = this._checkFromStringCoercion(ctxt, text, LogicalType.Integer, Double.TYPE);
        if (act == CoercionAction.AsNull) {
            return 0.0;
        }
        if (act == CoercionAction.AsEmpty) {
            return 0.0;
        }
        text = text.trim();
        if (this._hasTextualNull(text)) {
            this._verifyNullForPrimitiveCoercion(ctxt, text);
            return 0.0;
        }
        return this._parseDoublePrimitive(ctxt, text);
    }
    
    protected final double _parseDoublePrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            return _parseDouble(text);
        }
        catch (final IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(Double.TYPE, text, "not a valid `double` value (as String to convert)", new Object[0]);
            return this._nonNullNumber(v).doubleValue();
        }
    }
    
    protected static final double _parseDouble(final String numStr) throws NumberFormatException {
        if ("2.2250738585072012e-308".equals(numStr)) {
            return Double.MIN_NORMAL;
        }
        return Double.parseDouble(numStr);
    }
    
    protected Double _checkDoubleSpecialValue(final String text) {
        if (!text.isEmpty()) {
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Double.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    protected Date _parseDate(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = null;
        switch (p.currentTokenId()) {
            case 6: {
                text = p.getText();
                break;
            }
            case 7: {
                long ts;
                try {
                    ts = p.getLongValue();
                }
                catch (final JsonParseException | InputCoercionException e) {
                    final Number v = (Number)ctxt.handleWeirdNumberValue(this._valueClass, p.getNumberValue(), "not a valid 64-bit `long` for creating `java.util.Date`", new Object[0]);
                    ts = v.longValue();
                }
                return new Date(ts);
            }
            case 11: {
                return this.getNullValue(ctxt);
            }
            case 1: {
                text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                break;
            }
            case 3: {
                return this._parseDateFromArray(p, ctxt);
            }
            default: {
                return (Date)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
        return this._parseDate(text.trim(), ctxt);
    }
    
    protected Date _parseDateFromArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final CoercionAction act = this._findCoercionFromEmptyArray(ctxt);
        final boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        if (unwrap || act != CoercionAction.Fail) {
            final JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                switch (act) {
                    case AsEmpty: {
                        return (Date)this.getEmptyValue(ctxt);
                    }
                    case AsNull:
                    case TryConvert: {
                        return this.getNullValue(ctxt);
                    }
                }
            }
            else if (unwrap) {
                final Date parsed = this._parseDate(p, ctxt);
                this._verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
        }
        return (Date)ctxt.handleUnexpectedToken(this._valueClass, JsonToken.START_ARRAY, p, null, new Object[0]);
    }
    
    protected Date _parseDate(final String value, final DeserializationContext ctxt) throws IOException {
        try {
            if (value.isEmpty()) {
                final CoercionAction act = this._checkFromStringCoercion(ctxt, value);
                switch (act) {
                    case AsEmpty: {
                        return new Date(0L);
                    }
                    default: {
                        return null;
                    }
                }
            }
            else {
                if (this._hasTextualNull(value)) {
                    return null;
                }
                return ctxt.parseDate(value);
            }
        }
        catch (final IllegalArgumentException iae) {
            return (Date)ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", ClassUtil.exceptionMessage(iae));
        }
    }
    
    protected final String _parseString(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        }
        if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            final Object ob = p.getEmbeddedObject();
            if (ob instanceof byte[]) {
                return ctxt.getBase64Variant().encode((byte[])ob, false);
            }
            if (ob == null) {
                return null;
            }
            return ob.toString();
        }
        else {
            if (p.hasToken(JsonToken.START_OBJECT)) {
                return ctxt.extractScalarFromObject(p, this, this._valueClass);
            }
            final String value = p.getValueAsString();
            if (value != null) {
                return value;
            }
            return (String)ctxt.handleUnexpectedToken(String.class, p);
        }
    }
    
    protected boolean _hasTextualNull(final String value) {
        return "null".equals(value);
    }
    
    protected final boolean _isNegInf(final String text) {
        return "-Infinity".equals(text) || "-INF".equals(text);
    }
    
    protected final boolean _isPosInf(final String text) {
        return "Infinity".equals(text) || "INF".equals(text);
    }
    
    protected final boolean _isNaN(final String text) {
        return "NaN".equals(text);
    }
    
    protected static final boolean _isBlank(final String text) {
        for (int len = text.length(), i = 0; i < len; ++i) {
            if (text.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }
    
    protected CoercionAction _checkFromStringCoercion(final DeserializationContext ctxt, final String value) throws IOException {
        return this._checkFromStringCoercion(ctxt, value, this.logicalType(), this.handledType());
    }
    
    protected CoercionAction _checkFromStringCoercion(final DeserializationContext ctxt, final String value, final LogicalType logicalType, final Class<?> rawTargetType) throws IOException {
        if (value.isEmpty()) {
            final CoercionAction act = ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.EmptyString);
            return this._checkCoercionFail(ctxt, act, rawTargetType, value, "empty String (\"\")");
        }
        if (_isBlank(value)) {
            final CoercionAction act = ctxt.findCoercionFromBlankString(logicalType, rawTargetType, CoercionAction.Fail);
            return this._checkCoercionFail(ctxt, act, rawTargetType, value, "blank String (all whitespace)");
        }
        final CoercionAction act = ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.String);
        if (act == CoercionAction.Fail) {
            ctxt.reportInputMismatch(this, "Cannot coerce String value (\"%s\") to %s (but might if coercion using `CoercionConfig` was enabled)", value, this._coercedTypeDesc());
        }
        return act;
    }
    
    protected CoercionAction _checkFloatToIntCoercion(final JsonParser p, final DeserializationContext ctxt, final Class<?> rawTargetType) throws IOException {
        final CoercionAction act = ctxt.findCoercionAction(LogicalType.Integer, rawTargetType, CoercionInputShape.Float);
        if (act == CoercionAction.Fail) {
            return this._checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Floating-point value (" + p.getText() + ")");
        }
        return act;
    }
    
    protected Boolean _coerceBooleanFromInt(final JsonParser p, final DeserializationContext ctxt, final Class<?> rawTargetType) throws IOException {
        final CoercionAction act = ctxt.findCoercionAction(LogicalType.Boolean, rawTargetType, CoercionInputShape.Integer);
        switch (act) {
            case Fail: {
                this._checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Integer value (" + p.getText() + ")");
                return Boolean.FALSE;
            }
            case AsNull: {
                return null;
            }
            case AsEmpty: {
                return Boolean.FALSE;
            }
            default: {
                if (p.getNumberType() == JsonParser.NumberType.INT) {
                    return p.getIntValue() != 0;
                }
                return !"0".equals(p.getText());
            }
        }
    }
    
    protected CoercionAction _checkCoercionFail(final DeserializationContext ctxt, final CoercionAction act, final Class<?> targetType, final Object inputValue, final String inputDesc) throws IOException {
        if (act == CoercionAction.Fail) {
            ctxt.reportBadCoercion(this, targetType, inputValue, "Cannot coerce %s to %s (but could if coercion was enabled using `CoercionConfig`)", inputDesc, this._coercedTypeDesc());
        }
        return act;
    }
    
    protected boolean _checkTextualNull(final DeserializationContext ctxt, final String text) throws JsonMappingException {
        if (this._hasTextualNull(text)) {
            if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
                this._reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
            }
            return true;
        }
        return false;
    }
    
    protected Object _coerceIntegral(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final int feats = ctxt.getDeserializationFeatures();
        if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            return p.getBigIntegerValue();
        }
        if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            return p.getLongValue();
        }
        return p.getNumberValue();
    }
    
    protected final void _verifyNullForPrimitive(final DeserializationContext ctxt) throws JsonMappingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch(this, "Cannot coerce `null` to %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", this._coercedTypeDesc());
        }
    }
    
    protected final void _verifyNullForPrimitiveCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        }
        else {
            if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                return;
            }
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        }
        final String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
        this._reportFailedNullCoerce(ctxt, enable, feat, strDesc);
    }
    
    protected void _reportFailedNullCoerce(final DeserializationContext ctxt, final boolean state, final Enum<?> feature, final String inputDesc) throws JsonMappingException {
        final String enableDesc = state ? "enable" : "disable";
        ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value as %s (%s `%s.%s` to allow)", inputDesc, this._coercedTypeDesc(), enableDesc, feature.getDeclaringClass().getSimpleName(), feature.name());
    }
    
    protected String _coercedTypeDesc() {
        final JavaType t = this.getValueType();
        boolean structured;
        String typeDesc;
        if (t != null && !t.isPrimitive()) {
            structured = (t.isContainerType() || t.isReferenceType());
            typeDesc = ClassUtil.getTypeDescription(t);
        }
        else {
            final Class<?> cls = this.handledType();
            structured = (cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls));
            typeDesc = ClassUtil.getClassDescription(cls);
        }
        if (structured) {
            return "element of " + typeDesc;
        }
        return typeDesc + " value";
    }
    
    @Deprecated
    protected boolean _parseBooleanFromInt(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        this._verifyNumberForScalarCoercion(ctxt, p);
        return !"0".equals(p.getText());
    }
    
    @Deprecated
    protected void _verifyStringForScalarCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        final MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" to %s (enable `%s.%s` to allow)", str, this._coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name());
        }
    }
    
    @Deprecated
    protected Object _coerceEmptyString(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        }
        else {
            if (!isPrimitive || !ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                return this.getNullValue(ctxt);
            }
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        }
        this._reportFailedNullCoerce(ctxt, enable, feat, "empty String (\"\")");
        return null;
    }
    
    @Deprecated
    protected void _failDoubleToIntCoercion(final JsonParser p, final DeserializationContext ctxt, final String type) throws IOException {
        ctxt.reportInputMismatch(this.handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", p.getValueAsString(), type);
    }
    
    @Deprecated
    protected final void _verifyNullForScalarCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            final String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
            this._reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
        }
    }
    
    @Deprecated
    protected void _verifyNumberForScalarCoercion(final DeserializationContext ctxt, final JsonParser p) throws IOException {
        final MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            final String valueDesc = p.getText();
            ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) to %s (enable `%s.%s` to allow)", valueDesc, this._coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name());
        }
    }
    
    @Deprecated
    protected Object _coerceNullToken(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        if (isPrimitive) {
            this._verifyNullForPrimitive(ctxt);
        }
        return this.getNullValue(ctxt);
    }
    
    @Deprecated
    protected Object _coerceTextualNull(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            this._reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
        }
        return this.getNullValue(ctxt);
    }
    
    @Deprecated
    protected boolean _isEmptyOrTextualNull(final String value) {
        return value.isEmpty() || "null".equals(value);
    }
    
    protected JsonDeserializer<Object> findDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanProperty property) throws JsonMappingException {
        return ctxt.findContextualValueDeserializer(type, property);
    }
    
    protected final boolean _isIntNumber(final String text) {
        final int len = text.length();
        if (len > 0) {
            final char c = text.charAt(0);
            int i;
            if (c == '-' || c == '+') {
                if (len == 1) {
                    return false;
                }
                i = 1;
            }
            else {
                i = 0;
            }
            while (i < len) {
                final int ch = text.charAt(i);
                if (ch > 57 || ch < 48) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }
    
    protected JsonDeserializer<?> findConvertingContentDeserializer(final DeserializationContext ctxt, final BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (_neitherNull(intr, prop)) {
            final AnnotatedMember member = prop.getMember();
            if (member != null) {
                final Object convDef = intr.findDeserializationContentConverter(member);
                if (convDef != null) {
                    final Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
                    final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
                    if (existingDeserializer == null) {
                        existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
                    }
                    return new StdDelegatingDeserializer<Object>(conv, delegateType, existingDeserializer);
                }
            }
        }
        return existingDeserializer;
    }
    
    protected JsonFormat.Value findFormatOverrides(final DeserializationContext ctxt, final BeanProperty prop, final Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
        }
        return ctxt.getDefaultPropertyFormat(typeForDefaults);
    }
    
    protected Boolean findFormatFeature(final DeserializationContext ctxt, final BeanProperty prop, final Class<?> typeForDefaults, final JsonFormat.Feature feat) {
        final JsonFormat.Value format = this.findFormatOverrides(ctxt, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }
    
    protected final NullValueProvider findValueNullProvider(final DeserializationContext ctxt, final SettableBeanProperty prop, final PropertyMetadata propMetadata) throws JsonMappingException {
        if (prop != null) {
            return this._findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop.getValueDeserializer());
        }
        return null;
    }
    
    protected NullValueProvider findContentNullProvider(final DeserializationContext ctxt, final BeanProperty prop, final JsonDeserializer<?> valueDeser) throws JsonMappingException {
        final Nulls nulls = this.findContentNullStyle(ctxt, prop);
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                JavaType type = ctxt.constructType(valueDeser.handledType());
                if (type.isContainerType()) {
                    type = type.getContentType();
                }
                return NullsFailProvider.constructForRootValue(type);
            }
            return NullsFailProvider.constructForProperty(prop, prop.getType().getContentType());
        }
        else {
            final NullValueProvider prov = this._findNullProvider(ctxt, prop, nulls, valueDeser);
            if (prov != null) {
                return prov;
            }
            return valueDeser;
        }
    }
    
    protected Nulls findContentNullStyle(final DeserializationContext ctxt, final BeanProperty prop) throws JsonMappingException {
        if (prop != null) {
            return prop.getMetadata().getContentNulls();
        }
        return null;
    }
    
    protected final NullValueProvider _findNullProvider(final DeserializationContext ctxt, final BeanProperty prop, final Nulls nulls, final JsonDeserializer<?> valueDeser) throws JsonMappingException {
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                return NullsFailProvider.constructForRootValue(ctxt.constructType(valueDeser.handledType()));
            }
            return NullsFailProvider.constructForProperty(prop);
        }
        else if (nulls == Nulls.AS_EMPTY) {
            if (valueDeser == null) {
                return null;
            }
            if (valueDeser instanceof BeanDeserializerBase) {
                final ValueInstantiator vi = ((BeanDeserializerBase)valueDeser).getValueInstantiator();
                if (!vi.canCreateUsingDefault()) {
                    final JavaType type = prop.getType();
                    ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
                }
            }
            final AccessPattern access = valueDeser.getEmptyAccessPattern();
            if (access == AccessPattern.ALWAYS_NULL) {
                return NullsConstantProvider.nuller();
            }
            if (access == AccessPattern.CONSTANT) {
                return NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
            }
            return new NullsAsEmptyProvider(valueDeser);
        }
        else {
            if (nulls == Nulls.SKIP) {
                return NullsConstantProvider.skipper();
            }
            return null;
        }
    }
    
    protected CoercionAction _findCoercionFromEmptyString(final DeserializationContext ctxt) {
        return ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyString);
    }
    
    protected CoercionAction _findCoercionFromEmptyArray(final DeserializationContext ctxt) {
        return ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyArray);
    }
    
    protected CoercionAction _findCoercionFromBlankString(final DeserializationContext ctxt) {
        return ctxt.findCoercionFromBlankString(this.logicalType(), this.handledType(), CoercionAction.Fail);
    }
    
    protected void handleUnknownProperty(final JsonParser p, final DeserializationContext ctxt, Object instanceOrClass, final String propName) throws IOException {
        if (instanceOrClass == null) {
            instanceOrClass = this.handledType();
        }
        if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
            return;
        }
        p.skipChildren();
    }
    
    protected void handleMissingEndArrayForSingle(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", this.handledType().getName());
    }
    
    protected void _verifyEndArrayForSingle(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
            this.handleMissingEndArrayForSingle(p, ctxt);
        }
    }
    
    protected static final boolean _neitherNull(final Object a, final Object b) {
        return a != null && b != null;
    }
    
    protected final boolean _byteOverflow(final int value) {
        return value < -128 || value > 255;
    }
    
    protected final boolean _shortOverflow(final int value) {
        return value < -32768 || value > 32767;
    }
    
    protected final boolean _intOverflow(final long value) {
        return value < -2147483648L || value > 2147483647L;
    }
    
    protected Number _nonNullNumber(Number n) {
        if (n == null) {
            n = 0;
        }
        return n;
    }
    
    static {
        F_MASK_INT_COERCIONS = (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask());
        F_MASK_ACCEPT_ARRAYS = (DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask());
    }
}
