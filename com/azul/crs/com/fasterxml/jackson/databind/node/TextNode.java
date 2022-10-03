package com.azul.crs.com.fasterxml.jackson.databind.node;

import com.azul.crs.com.fasterxml.jackson.core.io.CharTypes;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.core.io.NumberInput;
import com.azul.crs.com.fasterxml.jackson.core.Base64Variants;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.azul.crs.com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.azul.crs.com.fasterxml.jackson.core.Base64Variant;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;

public class TextNode extends ValueNode
{
    private static final long serialVersionUID = 2L;
    static final TextNode EMPTY_STRING_NODE;
    protected final String _value;
    
    public TextNode(final String v) {
        this._value = v;
    }
    
    public static TextNode valueOf(final String v) {
        if (v == null) {
            return null;
        }
        if (v.isEmpty()) {
            return TextNode.EMPTY_STRING_NODE;
        }
        return new TextNode(v);
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.STRING;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_STRING;
    }
    
    @Override
    public String textValue() {
        return this._value;
    }
    
    public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException {
        final String str = this._value.trim();
        final int initBlockSize = 4 + (str.length() >> 2) * 3;
        final ByteArrayBuilder builder = new ByteArrayBuilder(Math.max(16, Math.min(65536, initBlockSize)));
        try {
            b64variant.decode(str, builder);
        }
        catch (final IllegalArgumentException e) {
            throw InvalidFormatException.from(null, String.format("Cannot access contents of TextNode as binary due to broken Base64 encoding: %s", e.getMessage()), str, byte[].class);
        }
        return builder.toByteArray();
    }
    
    @Override
    public byte[] binaryValue() throws IOException {
        return this.getBinaryValue(Base64Variants.getDefaultVariant());
    }
    
    @Override
    public String asText() {
        return this._value;
    }
    
    @Override
    public String asText(final String defaultValue) {
        return (this._value == null) ? defaultValue : this._value;
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        if (this._value != null) {
            final String v = this._value.trim();
            if ("true".equals(v)) {
                return true;
            }
            if ("false".equals(v)) {
                return false;
            }
        }
        return defaultValue;
    }
    
    @Override
    public int asInt(final int defaultValue) {
        return NumberInput.parseAsInt(this._value, defaultValue);
    }
    
    @Override
    public long asLong(final long defaultValue) {
        return NumberInput.parseAsLong(this._value, defaultValue);
    }
    
    @Override
    public double asDouble(final double defaultValue) {
        return NumberInput.parseAsDouble(this._value, defaultValue);
    }
    
    @Override
    public final void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._value == null) {
            g.writeNull();
        }
        else {
            g.writeString(this._value);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof TextNode && ((TextNode)o)._value.equals(this._value));
    }
    
    @Override
    public int hashCode() {
        return this._value.hashCode();
    }
    
    @Deprecated
    protected static void appendQuoted(final StringBuilder sb, final String content) {
        sb.append('\"');
        CharTypes.appendQuoted(sb, content);
        sb.append('\"');
    }
    
    static {
        EMPTY_STRING_NODE = new TextNode("");
    }
}
