package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonParseException;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.Base64Variant;
import java.io.Writer;
import com.fasterxml.jackson.core.JsonLocation;
import javax.xml.stream.XMLStreamException;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import java.util.Set;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.PackageVersion;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import javax.xml.stream.XMLStreamReader;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

public class FromXmlParser extends ParserMinimalBase
{
    public static final String DEFAULT_UNNAMED_TEXT_PROPERTY = "";
    protected String _cfgNameForTextElement;
    protected int _formatFeatures;
    protected ObjectCodec _objectCodec;
    protected boolean _closed;
    protected final IOContext _ioContext;
    protected XmlReadContext _parsingContext;
    protected final XmlTokenStream _xmlTokens;
    protected boolean _mayBeLeaf;
    protected JsonToken _nextToken;
    protected String _currText;
    protected ByteArrayBuilder _byteArrayBuilder;
    protected byte[] _binaryValue;
    
    public FromXmlParser(final IOContext ctxt, final int genericParserFeatures, final int xmlFeatures, final ObjectCodec codec, final XMLStreamReader xmlReader) throws IOException {
        super(genericParserFeatures);
        this._cfgNameForTextElement = "";
        this._byteArrayBuilder = null;
        this._formatFeatures = xmlFeatures;
        this._ioContext = ctxt;
        this._objectCodec = codec;
        this._parsingContext = XmlReadContext.createRootContext(-1, -1);
        this._xmlTokens = new XmlTokenStream(xmlReader, ctxt.getSourceReference(), this._formatFeatures);
        if (this._xmlTokens.hasXsiNil()) {
            this._nextToken = JsonToken.VALUE_NULL;
        }
        else if (this._xmlTokens.getCurrentToken() == 1) {
            this._nextToken = JsonToken.START_OBJECT;
        }
        else {
            this._reportError("Internal problem: invalid starting state (%d)", (Object)this._xmlTokens.getCurrentToken());
        }
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }
    
    public void setCodec(final ObjectCodec c) {
        this._objectCodec = c;
    }
    
    public void setXMLTextElementName(final String name) {
        this._cfgNameForTextElement = name;
    }
    
    public boolean requiresCustomCodec() {
        return true;
    }
    
    public FromXmlParser enable(final Feature f) {
        this._formatFeatures |= f.getMask();
        this._xmlTokens.setFormatFeatures(this._formatFeatures);
        return this;
    }
    
    public FromXmlParser disable(final Feature f) {
        this._formatFeatures &= ~f.getMask();
        this._xmlTokens.setFormatFeatures(this._formatFeatures);
        return this;
    }
    
    public final boolean isEnabled(final Feature f) {
        return (this._formatFeatures & f.getMask()) != 0x0;
    }
    
    public FromXmlParser configure(final Feature f, final boolean state) {
        if (state) {
            this.enable(f);
        }
        else {
            this.disable(f);
        }
        return this;
    }
    
    public int getFormatFeatures() {
        return this._formatFeatures;
    }
    
    public JsonParser overrideFormatFeatures(final int values, final int mask) {
        this._formatFeatures = ((this._formatFeatures & ~mask) | (values & mask));
        return (JsonParser)this;
    }
    
    public XMLStreamReader getStaxReader() {
        return (XMLStreamReader)this._xmlTokens.getXmlReader();
    }
    
    public void addVirtualWrapping(final Set<String> namesToWrap) {
        if (!this._parsingContext.inRoot() && !this._parsingContext.getParent().inRoot()) {
            final String name = this._xmlTokens.getLocalName();
            if (name != null && namesToWrap.contains(name)) {
                this._xmlTokens.repeatStartElement();
            }
        }
        this._parsingContext.setNamesToWrap(namesToWrap);
    }
    
    public String getCurrentName() throws IOException {
        String name;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            final XmlReadContext parent = this._parsingContext.getParent();
            name = parent.getCurrentName();
        }
        else {
            name = this._parsingContext.getCurrentName();
        }
        if (name == null) {
            throw new IllegalStateException("Missing name, in state: " + this._currToken);
        }
        return name;
    }
    
    public void overrideCurrentName(final String name) {
        XmlReadContext ctxt = this._parsingContext;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            ctxt = ctxt.getParent();
        }
        ctxt.setCurrentName(name);
    }
    
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            try {
                if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                    this._xmlTokens.closeCompletely();
                }
                else {
                    this._xmlTokens.close();
                }
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsParseException(e, (JsonParser)this);
            }
            finally {
                this._releaseBuffers();
            }
        }
    }
    
    public boolean isClosed() {
        return this._closed;
    }
    
    public XmlReadContext getParsingContext() {
        return this._parsingContext;
    }
    
    public JsonLocation getTokenLocation() {
        return this._xmlTokens.getTokenLocation();
    }
    
    public JsonLocation getCurrentLocation() {
        return this._xmlTokens.getCurrentLocation();
    }
    
    public boolean isExpectedStartArrayToken() {
        final JsonToken t = this._currToken;
        if (t == JsonToken.START_OBJECT) {
            this._currToken = JsonToken.START_ARRAY;
            this._parsingContext.convertToArray();
            if (this._nextToken == JsonToken.END_OBJECT) {
                this._nextToken = JsonToken.END_ARRAY;
            }
            else {
                this._nextToken = null;
            }
            this._xmlTokens.skipAttributes();
            return true;
        }
        return t == JsonToken.START_ARRAY;
    }
    
    public JsonToken nextToken() throws IOException {
        this._binaryValue = null;
        if (this._nextToken != null) {
            final JsonToken t = this._nextToken;
            this._currToken = t;
            this._nextToken = null;
            switch (t) {
                case START_OBJECT: {
                    this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                    break;
                }
                case START_ARRAY: {
                    this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
                    break;
                }
                case END_OBJECT:
                case END_ARRAY: {
                    this._parsingContext = this._parsingContext.getParent();
                    break;
                }
                case FIELD_NAME: {
                    this._parsingContext.setCurrentName(this._xmlTokens.getLocalName());
                    break;
                }
            }
            return t;
        }
        int token;
        try {
            token = this._xmlTokens.next();
        }
        catch (final XMLStreamException e) {
            token = StaxUtil.throwAsParseException(e, (JsonParser)this);
        }
        while (token == 1) {
            if (this._mayBeLeaf) {
                this._nextToken = JsonToken.FIELD_NAME;
                this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                return this._currToken = JsonToken.START_OBJECT;
            }
            if (!this._parsingContext.inArray()) {
                final String name = this._xmlTokens.getLocalName();
                this._parsingContext.setCurrentName(name);
                if (this._parsingContext.shouldWrap(name)) {
                    this._xmlTokens.repeatStartElement();
                }
                this._mayBeLeaf = true;
                return this._currToken = JsonToken.FIELD_NAME;
            }
            try {
                token = this._xmlTokens.next();
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsParseException(e, (JsonParser)this);
            }
            this._mayBeLeaf = true;
        }
        while (true) {
            switch (token) {
                case 2: {
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        if (this._parsingContext.inArray()) {
                            this._nextToken = JsonToken.END_OBJECT;
                            this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                            return this._currToken = JsonToken.START_OBJECT;
                        }
                        if (this._currToken != JsonToken.VALUE_NULL) {
                            return this._currToken = JsonToken.VALUE_NULL;
                        }
                    }
                    this._currToken = (this._parsingContext.inArray() ? JsonToken.END_ARRAY : JsonToken.END_OBJECT);
                    this._parsingContext = this._parsingContext.getParent();
                    return this._currToken;
                }
                case 3: {
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        this._nextToken = JsonToken.FIELD_NAME;
                        this._currText = this._xmlTokens.getText();
                        this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                        return this._currToken = JsonToken.START_OBJECT;
                    }
                    this._parsingContext.setCurrentName(this._xmlTokens.getLocalName());
                    return this._currToken = JsonToken.FIELD_NAME;
                }
                case 4: {
                    this._currText = this._xmlTokens.getText();
                    return this._currToken = JsonToken.VALUE_STRING;
                }
                case 5: {
                    this._currText = this._xmlTokens.getText();
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        try {
                            this._xmlTokens.skipEndElement();
                        }
                        catch (final XMLStreamException e) {
                            StaxUtil.throwAsParseException(e, (JsonParser)this);
                        }
                        if (this._parsingContext.inArray() && this._isEmpty(this._currText)) {
                            this._nextToken = JsonToken.END_OBJECT;
                            this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                            return this._currToken = JsonToken.START_OBJECT;
                        }
                        return this._currToken = JsonToken.VALUE_STRING;
                    }
                    else {
                        if (this._parsingContext.inObject() && this._currToken != JsonToken.FIELD_NAME && this._isEmpty(this._currText)) {
                            try {
                                token = this._xmlTokens.next();
                            }
                            catch (final XMLStreamException e) {
                                StaxUtil.throwAsParseException(e, (JsonParser)this);
                            }
                            continue;
                        }
                        this._parsingContext.setCurrentName(this._cfgNameForTextElement);
                        this._nextToken = JsonToken.VALUE_STRING;
                        return this._currToken = JsonToken.FIELD_NAME;
                    }
                    break;
                }
                case 6: {
                    return this._currToken = null;
                }
                default: {
                    return this._internalErrorUnknownToken(token);
                }
            }
        }
    }
    
    public String nextTextValue() throws IOException {
        this._binaryValue = null;
        if (this._nextToken == null) {
            int token;
            try {
                token = this._xmlTokens.next();
            }
            catch (final XMLStreamException e) {
                token = StaxUtil.throwAsParseException(e, (JsonParser)this);
            }
            while (token == 1) {
                if (this._mayBeLeaf) {
                    this._nextToken = JsonToken.FIELD_NAME;
                    this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                    this._currToken = JsonToken.START_OBJECT;
                    return null;
                }
                if (!this._parsingContext.inArray()) {
                    final String name = this._xmlTokens.getLocalName();
                    this._parsingContext.setCurrentName(name);
                    if (this._parsingContext.shouldWrap(name)) {
                        this._xmlTokens.repeatStartElement();
                    }
                    this._mayBeLeaf = true;
                    this._currToken = JsonToken.FIELD_NAME;
                    return null;
                }
                try {
                    token = this._xmlTokens.next();
                }
                catch (final XMLStreamException e) {
                    StaxUtil.throwAsParseException(e, (JsonParser)this);
                }
                this._mayBeLeaf = true;
            }
            switch (token) {
                case 2: {
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        this._currToken = JsonToken.VALUE_STRING;
                        return this._currText = "";
                    }
                    this._currToken = (this._parsingContext.inArray() ? JsonToken.END_ARRAY : JsonToken.END_OBJECT);
                    this._parsingContext = this._parsingContext.getParent();
                    return null;
                }
                case 3: {
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        this._nextToken = JsonToken.FIELD_NAME;
                        this._currText = this._xmlTokens.getText();
                        this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                        this._currToken = JsonToken.START_OBJECT;
                        return null;
                    }
                    this._parsingContext.setCurrentName(this._xmlTokens.getLocalName());
                    this._currToken = JsonToken.FIELD_NAME;
                    return null;
                }
                case 4: {
                    this._currToken = JsonToken.VALUE_STRING;
                    return this._currText = this._xmlTokens.getText();
                }
                case 5: {
                    this._currText = this._xmlTokens.getText();
                    if (this._mayBeLeaf) {
                        this._mayBeLeaf = false;
                        try {
                            this._xmlTokens.skipEndElement();
                        }
                        catch (final XMLStreamException e) {
                            StaxUtil.throwAsParseException(e, (JsonParser)this);
                        }
                        this._currToken = JsonToken.VALUE_STRING;
                        return this._currText;
                    }
                    this._parsingContext.setCurrentName(this._cfgNameForTextElement);
                    this._nextToken = JsonToken.VALUE_STRING;
                    this._currToken = JsonToken.FIELD_NAME;
                    return null;
                }
                case 6: {
                    this._currToken = null;
                    break;
                }
            }
            return this._internalErrorUnknownToken(token);
        }
        final JsonToken t = this._nextToken;
        this._currToken = t;
        this._nextToken = null;
        if (t == JsonToken.VALUE_STRING) {
            return this._currText;
        }
        this._updateState(t);
        return null;
    }
    
    private void _updateState(final JsonToken t) {
        switch (t) {
            case START_OBJECT: {
                this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
                break;
            }
            case START_ARRAY: {
                this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
                break;
            }
            case END_OBJECT:
            case END_ARRAY: {
                this._parsingContext = this._parsingContext.getParent();
                break;
            }
            case FIELD_NAME: {
                this._parsingContext.setCurrentName(this._xmlTokens.getLocalName());
                break;
            }
            default: {
                this._internalErrorUnknownToken(t);
                break;
            }
        }
    }
    
    public String getText() throws IOException {
        if (this._currToken == null) {
            return null;
        }
        switch (this._currToken) {
            case FIELD_NAME: {
                return this.getCurrentName();
            }
            case VALUE_STRING: {
                return this._currText;
            }
            default: {
                return this._currToken.asString();
            }
        }
    }
    
    public final String getValueAsString() throws IOException {
        return this.getValueAsString(null);
    }
    
    public String getValueAsString(final String defValue) throws IOException {
        final JsonToken t = this._currToken;
        if (t == null) {
            return null;
        }
        switch (t) {
            case FIELD_NAME: {
                return this.getCurrentName();
            }
            case VALUE_STRING: {
                return this._currText;
            }
            case START_OBJECT: {
                try {
                    final String str = this._xmlTokens.convertToString();
                    if (str != null) {
                        this._parsingContext = this._parsingContext.getParent();
                        this._currToken = JsonToken.VALUE_STRING;
                        this._nextToken = null;
                        try {
                            this._xmlTokens.skipEndElement();
                        }
                        catch (final XMLStreamException e) {
                            StaxUtil.throwAsParseException(e, (JsonParser)this);
                        }
                        return this._currText = str;
                    }
                }
                catch (final XMLStreamException e2) {
                    StaxUtil.throwAsParseException(e2, (JsonParser)this);
                }
                return null;
            }
            default: {
                if (this._currToken.isScalarValue()) {
                    return this._currToken.asString();
                }
                return defValue;
            }
        }
    }
    
    public char[] getTextCharacters() throws IOException {
        final String text = this.getText();
        return (char[])((text == null) ? null : text.toCharArray());
    }
    
    public int getTextLength() throws IOException {
        final String text = this.getText();
        return (text == null) ? 0 : text.length();
    }
    
    public int getTextOffset() throws IOException {
        return 0;
    }
    
    public boolean hasTextCharacters() {
        return false;
    }
    
    public int getText(final Writer writer) throws IOException {
        final String str = this.getText();
        if (str == null) {
            return 0;
        }
        writer.write(str);
        return str.length();
    }
    
    public Object getEmbeddedObject() throws IOException {
        return null;
    }
    
    public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
            this._reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
        }
        if (this._binaryValue == null) {
            try {
                this._binaryValue = this._decodeBase64(b64variant);
            }
            catch (final IllegalArgumentException iae) {
                throw this._constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
            }
        }
        return this._binaryValue;
    }
    
    protected byte[] _decodeBase64(final Base64Variant b64variant) throws IOException {
        final ByteArrayBuilder builder = this._getByteArrayBuilder();
        final String str = this.getText();
        this._decodeBase64(str, builder, b64variant);
        return builder.toByteArray();
    }
    
    public BigInteger getBigIntegerValue() throws IOException {
        return null;
    }
    
    public BigDecimal getDecimalValue() throws IOException {
        return null;
    }
    
    public double getDoubleValue() throws IOException {
        return 0.0;
    }
    
    public float getFloatValue() throws IOException {
        return 0.0f;
    }
    
    public int getIntValue() throws IOException {
        return 0;
    }
    
    public long getLongValue() throws IOException {
        return 0L;
    }
    
    public JsonParser.NumberType getNumberType() throws IOException {
        return null;
    }
    
    public Number getNumberValue() throws IOException {
        return null;
    }
    
    protected void _handleEOF() throws JsonParseException {
        if (!this._parsingContext.inRoot()) {
            final String marker = this._parsingContext.inArray() ? "Array" : "Object";
            this._reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, this._parsingContext.getStartLocation(this._ioContext.getSourceReference())), (JsonToken)null);
        }
    }
    
    protected void _releaseBuffers() throws IOException {
    }
    
    protected ByteArrayBuilder _getByteArrayBuilder() {
        if (this._byteArrayBuilder == null) {
            this._byteArrayBuilder = new ByteArrayBuilder();
        }
        else {
            this._byteArrayBuilder.reset();
        }
        return this._byteArrayBuilder;
    }
    
    protected boolean _isEmpty(final String str) {
        final int len = (str == null) ? 0 : str.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                if (str.charAt(i) > ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private <T> T _internalErrorUnknownToken(final Object token) {
        throw new IllegalStateException("Internal error: unrecognized XmlTokenStream token: " + token);
    }
    
    public enum Feature implements FormatFeature
    {
        EMPTY_ELEMENT_AS_NULL(true);
        
        final boolean _defaultState;
        final int _mask;
        
        public static int collectDefaults() {
            int flags = 0;
            for (final Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }
        
        private Feature(final boolean defaultState) {
            this._defaultState = defaultState;
            this._mask = 1 << this.ordinal();
        }
        
        public boolean enabledByDefault() {
            return this._defaultState;
        }
        
        public int getMask() {
            return this._mask;
        }
        
        public boolean enabledIn(final int flags) {
            return (flags & this.getMask()) != 0x0;
        }
    }
}
