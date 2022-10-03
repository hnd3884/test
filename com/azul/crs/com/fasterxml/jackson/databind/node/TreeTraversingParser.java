package com.azul.crs.com.fasterxml.jackson.databind.node;

import java.io.OutputStream;
import com.azul.crs.com.fasterxml.jackson.core.Base64Variant;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.azul.crs.com.fasterxml.jackson.core.JsonLocation;
import com.azul.crs.com.fasterxml.jackson.core.JsonStreamContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.core.JsonParseException;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.StreamReadCapability;
import com.azul.crs.com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.azul.crs.com.fasterxml.jackson.core.Version;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.core.ObjectCodec;
import com.azul.crs.com.fasterxml.jackson.core.base.ParserMinimalBase;

public class TreeTraversingParser extends ParserMinimalBase
{
    protected ObjectCodec _objectCodec;
    protected NodeCursor _nodeCursor;
    protected boolean _closed;
    
    public TreeTraversingParser(final JsonNode n) {
        this(n, null);
    }
    
    public TreeTraversingParser(final JsonNode n, final ObjectCodec codec) {
        super(0);
        this._objectCodec = codec;
        this._nodeCursor = new NodeCursor.RootCursor(n, null);
    }
    
    @Override
    public void setCodec(final ObjectCodec c) {
        this._objectCodec = c;
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    @Override
    public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
        return TreeTraversingParser.DEFAULT_READ_CAPABILITIES;
    }
    
    @Override
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            this._nodeCursor = null;
            this._currToken = null;
        }
    }
    
    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        this._currToken = this._nodeCursor.nextToken();
        if (this._currToken == null) {
            this._closed = true;
            return null;
        }
        switch (this._currToken) {
            case START_OBJECT: {
                this._nodeCursor = this._nodeCursor.startObject();
                break;
            }
            case START_ARRAY: {
                this._nodeCursor = this._nodeCursor.startArray();
                break;
            }
            case END_OBJECT:
            case END_ARRAY: {
                this._nodeCursor = this._nodeCursor.getParent();
                break;
            }
        }
        return this._currToken;
    }
    
    @Override
    public JsonParser skipChildren() throws IOException {
        if (this._currToken == JsonToken.START_OBJECT) {
            this._nodeCursor = this._nodeCursor.getParent();
            this._currToken = JsonToken.END_OBJECT;
        }
        else if (this._currToken == JsonToken.START_ARRAY) {
            this._nodeCursor = this._nodeCursor.getParent();
            this._currToken = JsonToken.END_ARRAY;
        }
        return this;
    }
    
    @Override
    public boolean isClosed() {
        return this._closed;
    }
    
    @Override
    public String getCurrentName() {
        NodeCursor crsr = this._nodeCursor;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            crsr = crsr.getParent();
        }
        return (crsr == null) ? null : crsr.getCurrentName();
    }
    
    @Override
    public void overrideCurrentName(final String name) {
        NodeCursor crsr = this._nodeCursor;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            crsr = crsr.getParent();
        }
        if (crsr != null) {
            crsr.overrideCurrentName(name);
        }
    }
    
    @Override
    public JsonStreamContext getParsingContext() {
        return this._nodeCursor;
    }
    
    @Override
    public JsonLocation getTokenLocation() {
        return JsonLocation.NA;
    }
    
    @Override
    public JsonLocation getCurrentLocation() {
        return JsonLocation.NA;
    }
    
    @Override
    public String getText() {
        if (this._closed) {
            return null;
        }
        switch (this._currToken) {
            case FIELD_NAME: {
                return this._nodeCursor.getCurrentName();
            }
            case VALUE_STRING: {
                return this.currentNode().textValue();
            }
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT: {
                return String.valueOf(this.currentNode().numberValue());
            }
            case VALUE_EMBEDDED_OBJECT: {
                final JsonNode n = this.currentNode();
                if (n != null && n.isBinary()) {
                    return n.asText();
                }
                break;
            }
        }
        return (this._currToken == null) ? null : this._currToken.asString();
    }
    
    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.getText().toCharArray();
    }
    
    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return this.getText().length();
    }
    
    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return 0;
    }
    
    @Override
    public boolean hasTextCharacters() {
        return false;
    }
    
    @Override
    public NumberType getNumberType() throws IOException {
        final JsonNode n = this.currentNumericNode();
        return (n == null) ? null : n.numberType();
    }
    
    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return this.currentNumericNode().bigIntegerValue();
    }
    
    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return this.currentNumericNode().decimalValue();
    }
    
    @Override
    public double getDoubleValue() throws IOException {
        return this.currentNumericNode().doubleValue();
    }
    
    @Override
    public float getFloatValue() throws IOException {
        return (float)this.currentNumericNode().doubleValue();
    }
    
    @Override
    public int getIntValue() throws IOException {
        final NumericNode node = (NumericNode)this.currentNumericNode();
        if (!node.canConvertToInt()) {
            this.reportOverflowInt();
        }
        return node.intValue();
    }
    
    @Override
    public long getLongValue() throws IOException {
        final NumericNode node = (NumericNode)this.currentNumericNode();
        if (!node.canConvertToLong()) {
            this.reportOverflowLong();
        }
        return node.longValue();
    }
    
    @Override
    public Number getNumberValue() throws IOException {
        return this.currentNumericNode().numberValue();
    }
    
    @Override
    public Object getEmbeddedObject() {
        if (!this._closed) {
            final JsonNode n = this.currentNode();
            if (n != null) {
                if (n.isPojo()) {
                    return ((POJONode)n).getPojo();
                }
                if (n.isBinary()) {
                    return ((BinaryNode)n).binaryValue();
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isNaN() {
        if (!this._closed) {
            final JsonNode n = this.currentNode();
            if (n instanceof NumericNode) {
                return ((NumericNode)n).isNaN();
            }
        }
        return false;
    }
    
    @Override
    public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
        final JsonNode n = this.currentNode();
        if (n == null) {
            return null;
        }
        if (n instanceof TextNode) {
            return ((TextNode)n).getBinaryValue(b64variant);
        }
        return n.binaryValue();
    }
    
    @Override
    public int readBinaryValue(final Base64Variant b64variant, final OutputStream out) throws IOException, JsonParseException {
        final byte[] data = this.getBinaryValue(b64variant);
        if (data != null) {
            out.write(data, 0, data.length);
            return data.length;
        }
        return 0;
    }
    
    protected JsonNode currentNode() {
        if (this._closed || this._nodeCursor == null) {
            return null;
        }
        return this._nodeCursor.currentNode();
    }
    
    protected JsonNode currentNumericNode() throws JsonParseException {
        final JsonNode n = this.currentNode();
        if (n == null || !n.isNumber()) {
            final JsonToken t = (n == null) ? null : n.asToken();
            throw this._constructError("Current token (" + t + ") not numeric, cannot use numeric value accessors");
        }
        return n;
    }
    
    @Override
    protected void _handleEOF() throws JsonParseException {
        this._throwInternal();
    }
}
