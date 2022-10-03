package com.azul.crs.com.fasterxml.jackson.databind.ser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import java.math.BigInteger;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class NumberSerializer extends StdScalarSerializer<Number> implements ContextualSerializer
{
    public static final NumberSerializer instance;
    protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
    protected final boolean _isInt;
    
    public NumberSerializer(final Class<? extends Number> rawType) {
        super(rawType, false);
        this._isInt = (rawType == BigInteger.class);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = this.findFormatOverrides(prov, property, this.handledType());
        if (format != null) {
            switch (format.getShape()) {
                case STRING: {
                    if (this.handledType() == BigDecimal.class) {
                        return bigDecimalAsStringSerializer();
                    }
                    return ToStringSerializer.instance;
                }
            }
        }
        return this;
    }
    
    @Override
    public void serialize(final Number value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (value instanceof BigDecimal) {
            g.writeNumber((BigDecimal)value);
        }
        else if (value instanceof BigInteger) {
            g.writeNumber((BigInteger)value);
        }
        else if (value instanceof Long) {
            g.writeNumber(value.longValue());
        }
        else if (value instanceof Double) {
            g.writeNumber(value.doubleValue());
        }
        else if (value instanceof Float) {
            g.writeNumber(value.floatValue());
        }
        else if (value instanceof Integer || value instanceof Byte || value instanceof Short) {
            g.writeNumber(value.intValue());
        }
        else {
            g.writeNumber(value.toString());
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode(this._isInt ? "integer" : "number", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (this._isInt) {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.BIG_INTEGER);
        }
        else if (this.handledType() == BigDecimal.class) {
            this.visitFloatFormat(visitor, typeHint, JsonParser.NumberType.BIG_DECIMAL);
        }
        else {
            visitor.expectNumberFormat(typeHint);
        }
    }
    
    public static JsonSerializer<?> bigDecimalAsStringSerializer() {
        return BigDecimalAsStringSerializer.BD_INSTANCE;
    }
    
    static {
        instance = new NumberSerializer(Number.class);
    }
    
    static final class BigDecimalAsStringSerializer extends ToStringSerializerBase
    {
        static final BigDecimalAsStringSerializer BD_INSTANCE;
        
        public BigDecimalAsStringSerializer() {
            super(BigDecimal.class);
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final Object value) {
            return false;
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            String text;
            if (gen.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
                final BigDecimal bd = (BigDecimal)value;
                if (!this._verifyBigDecimalRange(gen, bd)) {
                    final String errorMsg = String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", bd.scale(), 9999, 9999);
                    provider.reportMappingProblem(errorMsg, new Object[0]);
                }
                text = bd.toPlainString();
            }
            else {
                text = value.toString();
            }
            gen.writeString(text);
        }
        
        @Override
        public String valueToString(final Object value) {
            throw new IllegalStateException();
        }
        
        protected boolean _verifyBigDecimalRange(final JsonGenerator gen, final BigDecimal value) throws IOException {
            final int scale = value.scale();
            return scale >= -9999 && scale <= 9999;
        }
        
        static {
            BD_INSTANCE = new BigDecimalAsStringSerializer();
        }
    }
}
