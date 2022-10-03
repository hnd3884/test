package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.azul.crs.com.fasterxml.jackson.core.io.NumberInput;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.util.AccessPattern;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashSet;

public class NumberDeserializers
{
    private static final HashSet<String> _classNames;
    
    public static JsonDeserializer<?> find(final Class<?> rawType, final String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
            if (rawType == Void.TYPE) {
                return NullifyingDeserializer.instance;
            }
        }
        else {
            if (!NumberDeserializers._classNames.contains(clsName)) {
                return null;
            }
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }
    
    static {
        _classNames = new HashSet<String>();
        final Class[] array;
        final Class<?>[] numberTypes = array = new Class[] { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class };
        for (final Class<?> cls : array) {
            NumberDeserializers._classNames.add(cls.getName());
        }
    }
    
    protected abstract static class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T>
    {
        private static final long serialVersionUID = 1L;
        protected final LogicalType _logicalType;
        protected final T _nullValue;
        protected final T _emptyValue;
        protected final boolean _primitive;
        
        protected PrimitiveOrWrapperDeserializer(final Class<T> vc, final LogicalType logicalType, final T nvl, final T empty) {
            super(vc);
            this._logicalType = logicalType;
            this._nullValue = nvl;
            this._emptyValue = empty;
            this._primitive = vc.isPrimitive();
        }
        
        @Deprecated
        protected PrimitiveOrWrapperDeserializer(final Class<T> vc, final T nvl, final T empty) {
            this((Class<Object>)vc, LogicalType.OtherScalar, nvl, empty);
        }
        
        @Override
        public AccessPattern getNullAccessPattern() {
            if (this._primitive) {
                return AccessPattern.DYNAMIC;
            }
            if (this._nullValue == null) {
                return AccessPattern.ALWAYS_NULL;
            }
            return AccessPattern.CONSTANT;
        }
        
        @Override
        public final T getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", this.handledType().toString());
            }
            return this._nullValue;
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
            return this._emptyValue;
        }
        
        @Override
        public final LogicalType logicalType() {
            return this._logicalType;
        }
    }
    
    @JacksonStdImpl
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean>
    {
        private static final long serialVersionUID = 1L;
        static final BooleanDeserializer primitiveInstance;
        static final BooleanDeserializer wrapperInstance;
        
        public BooleanDeserializer(final Class<Boolean> cls, final Boolean nvl) {
            super(cls, LogicalType.Boolean, nvl, Boolean.FALSE);
        }
        
        @Override
        public Boolean deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (this._primitive) {
                return this._parseBooleanPrimitive(p, ctxt);
            }
            return this._parseBoolean(p, ctxt, this._valueClass);
        }
        
        @Override
        public Boolean deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            final JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (this._primitive) {
                return this._parseBooleanPrimitive(p, ctxt);
            }
            return this._parseBoolean(p, ctxt, this._valueClass);
        }
        
        static {
            primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
            wrapperInstance = new BooleanDeserializer(Boolean.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte>
    {
        private static final long serialVersionUID = 1L;
        static final ByteDeserializer primitiveInstance;
        static final ByteDeserializer wrapperInstance;
        
        public ByteDeserializer(final Class<Byte> cls, final Byte nvl) {
            super(cls, LogicalType.Integer, nvl, (Byte)0);
        }
        
        @Override
        public Byte deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getByteValue();
            }
            if (this._primitive) {
                return this._parseBytePrimitive(p, ctxt);
            }
            return this._parseByte(p, ctxt);
        }
        
        protected Byte _parseByte(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return this.getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (Byte)this.getEmptyValue(ctxt);
                    }
                    return p.getByteValue();
                }
                case 11: {
                    return this.getNullValue(ctxt);
                }
                case 7: {
                    return p.getByteValue();
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                default: {
                    return (Byte)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (Byte)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
                return this.getNullValue(ctxt);
            }
            int value;
            try {
                value = NumberInput.parseInt(text);
            }
            catch (final IllegalArgumentException iae) {
                return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value", new Object[0]);
            }
            if (this._byteOverflow(value)) {
                return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
            }
            return (byte)value;
        }
        
        static {
            primitiveInstance = new ByteDeserializer(Byte.TYPE, (Byte)0);
            wrapperInstance = new ByteDeserializer(Byte.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short>
    {
        private static final long serialVersionUID = 1L;
        static final ShortDeserializer primitiveInstance;
        static final ShortDeserializer wrapperInstance;
        
        public ShortDeserializer(final Class<Short> cls, final Short nvl) {
            super(cls, LogicalType.Integer, nvl, (Short)0);
        }
        
        @Override
        public Short deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getShortValue();
            }
            if (this._primitive) {
                return this._parseShortPrimitive(p, ctxt);
            }
            return this._parseShort(p, ctxt);
        }
        
        protected Short _parseShort(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return this.getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (Short)this.getEmptyValue(ctxt);
                    }
                    return p.getShortValue();
                }
                case 11: {
                    return this.getNullValue(ctxt);
                }
                case 7: {
                    return p.getShortValue();
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Short)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (Short)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
                return this.getNullValue(ctxt);
            }
            int value;
            try {
                value = NumberInput.parseInt(text);
            }
            catch (final IllegalArgumentException iae) {
                return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value", new Object[0]);
            }
            if (this._shortOverflow(value)) {
                return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
            }
            return (short)value;
        }
        
        static {
            primitiveInstance = new ShortDeserializer(Short.TYPE, (Short)0);
            wrapperInstance = new ShortDeserializer(Short.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character>
    {
        private static final long serialVersionUID = 1L;
        static final CharacterDeserializer primitiveInstance;
        static final CharacterDeserializer wrapperInstance;
        
        public CharacterDeserializer(final Class<Character> cls, final Character nvl) {
            super(cls, LogicalType.Integer, nvl, '\0');
        }
        
        @Override
        public Character deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 7: {
                    final CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this._valueClass, CoercionInputShape.Integer);
                    switch (act) {
                        case Fail: {
                            this._checkCoercionFail(ctxt, act, this._valueClass, p.getNumberValue(), "Integer value (" + p.getText() + ")");
                        }
                        case AsNull: {
                            return this.getNullValue(ctxt);
                        }
                        case AsEmpty: {
                            return (Character)this.getEmptyValue(ctxt);
                        }
                        default: {
                            final int value = p.getIntValue();
                            if (value >= 0 && value <= 65535) {
                                return (char)value;
                            }
                            return (Character)ctxt.handleWeirdNumberValue(this.handledType(), value, "value outside valid Character range (0x0000 - 0xFFFF)", new Object[0]);
                        }
                    }
                    break;
                }
                case 11: {
                    if (this._primitive) {
                        this._verifyNullForPrimitive(ctxt);
                    }
                    return this.getNullValue(ctxt);
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Character)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            if (text.length() == 1) {
                return text.charAt(0);
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (Character)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
                return this.getNullValue(ctxt);
            }
            return (Character)ctxt.handleWeirdStringValue(this.handledType(), text, "Expected either Integer value code or 1-character String", new Object[0]);
        }
        
        static {
            primitiveInstance = new CharacterDeserializer(Character.TYPE, '\0');
            wrapperInstance = new CharacterDeserializer(Character.class, null);
        }
    }
    
    @JacksonStdImpl
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer>
    {
        private static final long serialVersionUID = 1L;
        static final IntegerDeserializer primitiveInstance;
        static final IntegerDeserializer wrapperInstance;
        
        public IntegerDeserializer(final Class<Integer> cls, final Integer nvl) {
            super(cls, LogicalType.Integer, nvl, 0);
        }
        
        @Override
        public boolean isCachable() {
            return true;
        }
        
        @Override
        public Integer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getIntValue();
            }
            if (this._primitive) {
                return this._parseIntPrimitive(p, ctxt);
            }
            return this._parseInteger(p, ctxt, Integer.class);
        }
        
        @Override
        public Integer deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getIntValue();
            }
            if (this._primitive) {
                return this._parseIntPrimitive(p, ctxt);
            }
            return this._parseInteger(p, ctxt, Integer.class);
        }
        
        static {
            primitiveInstance = new IntegerDeserializer(Integer.TYPE, 0);
            wrapperInstance = new IntegerDeserializer(Integer.class, null);
        }
    }
    
    @JacksonStdImpl
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long>
    {
        private static final long serialVersionUID = 1L;
        static final LongDeserializer primitiveInstance;
        static final LongDeserializer wrapperInstance;
        
        public LongDeserializer(final Class<Long> cls, final Long nvl) {
            super(cls, LogicalType.Integer, nvl, 0L);
        }
        
        @Override
        public boolean isCachable() {
            return true;
        }
        
        @Override
        public Long deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getLongValue();
            }
            if (this._primitive) {
                return this._parseLongPrimitive(p, ctxt);
            }
            return this._parseLong(p, ctxt, Long.class);
        }
        
        static {
            primitiveInstance = new LongDeserializer(Long.TYPE, 0L);
            wrapperInstance = new LongDeserializer(Long.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float>
    {
        private static final long serialVersionUID = 1L;
        static final FloatDeserializer primitiveInstance;
        static final FloatDeserializer wrapperInstance;
        
        public FloatDeserializer(final Class<Float> cls, final Float nvl) {
            super(cls, LogicalType.Float, nvl, 0.0f);
        }
        
        @Override
        public Float deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return p.getFloatValue();
            }
            if (this._primitive) {
                return this._parseFloatPrimitive(p, ctxt);
            }
            return this._parseFloat(p, ctxt);
        }
        
        protected final Float _parseFloat(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 11: {
                    return this.getNullValue(ctxt);
                }
                case 7:
                case 8: {
                    return p.getFloatValue();
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Float)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final Float nan = this._checkFloatSpecialValue(text);
            if (nan != null) {
                return nan;
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (Float)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
                return this.getNullValue(ctxt);
            }
            try {
                return Float.parseFloat(text);
            }
            catch (final IllegalArgumentException ex) {
                return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Float` value", new Object[0]);
            }
        }
        
        static {
            primitiveInstance = new FloatDeserializer(Float.TYPE, 0.0f);
            wrapperInstance = new FloatDeserializer(Float.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double>
    {
        private static final long serialVersionUID = 1L;
        static final DoubleDeserializer primitiveInstance;
        static final DoubleDeserializer wrapperInstance;
        
        public DoubleDeserializer(final Class<Double> cls, final Double nvl) {
            super(cls, LogicalType.Float, nvl, 0.0);
        }
        
        @Override
        public Double deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return p.getDoubleValue();
            }
            if (this._primitive) {
                return this._parseDoublePrimitive(p, ctxt);
            }
            return this._parseDouble(p, ctxt);
        }
        
        @Override
        public Double deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return p.getDoubleValue();
            }
            if (this._primitive) {
                return this._parseDoublePrimitive(p, ctxt);
            }
            return this._parseDouble(p, ctxt);
        }
        
        protected final Double _parseDouble(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 11: {
                    return this.getNullValue(ctxt);
                }
                case 7:
                case 8: {
                    return p.getDoubleValue();
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Double)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final Double nan = this._checkDoubleSpecialValue(text);
            if (nan != null) {
                return nan;
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (Double)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
                return this.getNullValue(ctxt);
            }
            try {
                return StdDeserializer._parseDouble(text);
            }
            catch (final IllegalArgumentException ex) {
                return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Double` value", new Object[0]);
            }
        }
        
        static {
            primitiveInstance = new DoubleDeserializer(Double.TYPE, 0.0);
            wrapperInstance = new DoubleDeserializer(Double.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class NumberDeserializer extends StdScalarDeserializer<Object>
    {
        public static final NumberDeserializer instance;
        
        public NumberDeserializer() {
            super(Number.class);
        }
        
        @Override
        public final LogicalType logicalType() {
            return LogicalType.Integer;
        }
        
        @Override
        public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 7: {
                    if (ctxt.hasSomeOfFeatures(NumberDeserializer.F_MASK_INT_COERCIONS)) {
                        return this._coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                }
                case 8: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN()) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._hasTextualNull(text)) {
                return this.getNullValue(ctxt);
            }
            if (this._isPosInf(text)) {
                return Double.POSITIVE_INFINITY;
            }
            if (this._isNegInf(text)) {
                return Double.NEGATIVE_INFINITY;
            }
            if (this._isNaN(text)) {
                return Double.NaN;
            }
            try {
                if (!this._isIntNumber(text)) {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return new BigDecimal(text);
                    }
                    return Double.valueOf(text);
                }
                else {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                        return new BigInteger(text);
                    }
                    final long value = Long.parseLong(text);
                    if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L) {
                        return (int)value;
                    }
                    return value;
                }
            }
            catch (final IllegalArgumentException iae) {
                return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
            }
        }
        
        @Override
        public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            switch (p.currentTokenId()) {
                case 6:
                case 7:
                case 8: {
                    return this.deserialize(p, ctxt);
                }
                default: {
                    return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
                }
            }
        }
        
        static {
            instance = new NumberDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger>
    {
        public static final BigIntegerDeserializer instance;
        
        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) {
            return BigInteger.ZERO;
        }
        
        @Override
        public final LogicalType logicalType() {
            return LogicalType.Integer;
        }
        
        @Override
        public BigInteger deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return p.getBigIntegerValue();
            }
            String text = null;
            switch (p.currentTokenId()) {
                case 6: {
                    text = p.getText();
                    break;
                }
                case 8: {
                    final CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return this.getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (BigInteger)this.getEmptyValue(ctxt);
                    }
                    return p.getDecimalValue().toBigInteger();
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (BigInteger)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (BigInteger)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._hasTextualNull(text)) {
                return this.getNullValue(ctxt);
            }
            try {
                return new BigInteger(text);
            }
            catch (final IllegalArgumentException ex) {
                return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
            }
        }
        
        static {
            instance = new BigIntegerDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal>
    {
        public static final BigDecimalDeserializer instance;
        
        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) {
            return BigDecimal.ZERO;
        }
        
        @Override
        public final LogicalType logicalType() {
            return LogicalType.Float;
        }
        
        @Override
        public BigDecimal deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            String text = null;
            switch (p.currentTokenId()) {
                case 7:
                case 8: {
                    return p.getDecimalValue();
                }
                case 6: {
                    text = p.getText();
                    break;
                }
                case 1: {
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (BigDecimal)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                }
            }
            final CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return this.getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return (BigDecimal)this.getEmptyValue(ctxt);
            }
            text = text.trim();
            if (this._hasTextualNull(text)) {
                return this.getNullValue(ctxt);
            }
            try {
                return new BigDecimal(text);
            }
            catch (final IllegalArgumentException ex) {
                return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
            }
        }
        
        static {
            instance = new BigDecimalDeserializer();
        }
    }
}
