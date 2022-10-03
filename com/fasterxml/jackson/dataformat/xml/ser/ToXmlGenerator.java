package com.fasterxml.jackson.dataformat.xml.ser;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonStreamContext;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.InputStream;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.fasterxml.jackson.core.PrettyPrinter;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import org.codehaus.stax2.ri.Stax2WriterAdapter;
import com.fasterxml.jackson.core.ObjectCodec;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.dataformat.xml.XmlPrettyPrinter;
import com.fasterxml.jackson.core.io.IOContext;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamWriter2;
import com.fasterxml.jackson.core.base.GeneratorBase;

public final class ToXmlGenerator extends GeneratorBase
{
    protected static final String DEFAULT_UNKNOWN_ELEMENT = "unknown";
    protected final XMLStreamWriter2 _xmlWriter;
    protected final XMLStreamWriter _originalXmlWriter;
    protected final boolean _stax2Emulation;
    protected final IOContext _ioContext;
    protected int _formatFeatures;
    protected XmlPrettyPrinter _xmlPrettyPrinter;
    protected boolean _initialized;
    protected QName _nextName;
    protected boolean _nextIsAttribute;
    protected boolean _nextIsUnwrapped;
    protected boolean _nextIsCData;
    protected LinkedList<QName> _elementNameStack;
    
    public ToXmlGenerator(final IOContext ctxt, final int stdFeatures, final int xmlFeatures, final ObjectCodec codec, final XMLStreamWriter sw) {
        super(stdFeatures, codec);
        this._nextName = null;
        this._nextIsAttribute = false;
        this._nextIsUnwrapped = false;
        this._nextIsCData = false;
        this._elementNameStack = new LinkedList<QName>();
        this._formatFeatures = xmlFeatures;
        this._ioContext = ctxt;
        this._originalXmlWriter = sw;
        this._xmlWriter = Stax2WriterAdapter.wrapIfNecessary(sw);
        this._stax2Emulation = (this._xmlWriter != sw);
        this._xmlPrettyPrinter = ((this._cfgPrettyPrinter instanceof XmlPrettyPrinter) ? this._cfgPrettyPrinter : null);
    }
    
    public void initGenerator() throws IOException {
        if (this._initialized) {
            return;
        }
        this._initialized = true;
        try {
            if (Feature.WRITE_XML_1_1.enabledIn(this._formatFeatures)) {
                this._xmlWriter.writeStartDocument("UTF-8", "1.1");
            }
            else {
                if (!Feature.WRITE_XML_DECLARATION.enabledIn(this._formatFeatures)) {
                    return;
                }
                this._xmlWriter.writeStartDocument("UTF-8", "1.0");
            }
            if (this._xmlPrettyPrinter != null && !this._stax2Emulation) {
                this._xmlPrettyPrinter.writePrologLinefeed(this._xmlWriter);
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    protected PrettyPrinter _constructDefaultPrettyPrinter() {
        return (PrettyPrinter)new DefaultXmlPrettyPrinter();
    }
    
    public JsonGenerator setPrettyPrinter(final PrettyPrinter pp) {
        this._cfgPrettyPrinter = pp;
        this._xmlPrettyPrinter = ((pp instanceof XmlPrettyPrinter) ? pp : null);
        return (JsonGenerator)this;
    }
    
    public Object getOutputTarget() {
        return this._originalXmlWriter;
    }
    
    public int getOutputBuffered() {
        return -1;
    }
    
    public int getFormatFeatures() {
        return this._formatFeatures;
    }
    
    public JsonGenerator overrideFormatFeatures(final int values, final int mask) {
        final int oldF = this._formatFeatures;
        final int newF = (this._formatFeatures & ~mask) | (values & mask);
        if (oldF != newF) {
            this._formatFeatures = newF;
        }
        return (JsonGenerator)this;
    }
    
    public ToXmlGenerator enable(final Feature f) {
        this._formatFeatures |= f.getMask();
        return this;
    }
    
    public ToXmlGenerator disable(final Feature f) {
        this._formatFeatures &= ~f.getMask();
        return this;
    }
    
    public final boolean isEnabled(final Feature f) {
        return (this._formatFeatures & f.getMask()) != 0x0;
    }
    
    public ToXmlGenerator configure(final Feature f, final boolean state) {
        if (state) {
            this.enable(f);
        }
        else {
            this.disable(f);
        }
        return this;
    }
    
    public boolean canWriteFormattedNumbers() {
        return true;
    }
    
    public boolean inRoot() {
        return this._writeContext.inRoot();
    }
    
    public XMLStreamWriter getStaxWriter() {
        return (XMLStreamWriter)this._xmlWriter;
    }
    
    public void setNextIsAttribute(final boolean isAttribute) {
        this._nextIsAttribute = isAttribute;
    }
    
    public void setNextIsUnwrapped(final boolean isUnwrapped) {
        this._nextIsUnwrapped = isUnwrapped;
    }
    
    public void setNextIsCData(final boolean isCData) {
        this._nextIsCData = isCData;
    }
    
    public final void setNextName(final QName name) {
        this._nextName = name;
    }
    
    public final boolean setNextNameIfMissing(final QName name) {
        if (this._nextName == null) {
            this._nextName = name;
            return true;
        }
        return false;
    }
    
    public void startWrappedValue(final QName wrapperName, final QName wrappedName) throws IOException {
        if (wrapperName != null) {
            try {
                if (this._xmlPrettyPrinter != null) {
                    this._xmlPrettyPrinter.writeStartElement(this._xmlWriter, wrapperName.getNamespaceURI(), wrapperName.getLocalPart());
                }
                else {
                    this._xmlWriter.writeStartElement(wrapperName.getNamespaceURI(), wrapperName.getLocalPart());
                }
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
            }
        }
        this.setNextName(wrappedName);
    }
    
    public void finishWrappedValue(final QName wrapperName, final QName wrappedName) throws IOException {
        if (wrapperName != null) {
            try {
                if (this._xmlPrettyPrinter != null) {
                    this._xmlPrettyPrinter.writeEndElement(this._xmlWriter, this._writeContext.getEntryCount());
                }
                else {
                    this._xmlWriter.writeEndElement();
                }
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
            }
        }
    }
    
    public void writeRepeatedFieldName() throws IOException {
        if (this._writeContext.writeFieldName(this._nextName.getLocalPart()) == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
    }
    
    public final void writeFieldName(final String name) throws IOException {
        if (this._writeContext.writeFieldName(name) == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        final String ns = (this._nextName == null) ? "" : this._nextName.getNamespaceURI();
        this.setNextName(new QName(ns, name));
    }
    
    public final void writeStringField(final String fieldName, final String value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeString(value);
    }
    
    public final void writeStartArray() throws IOException {
        this._verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
        }
    }
    
    public final void writeEndArray() throws IOException {
        if (!this._writeContext.inArray()) {
            this._reportError("Current context not Array but " + this._writeContext.typeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray((JsonGenerator)this, this._writeContext.getEntryCount());
        }
        this._writeContext = this._writeContext.getParent();
    }
    
    public final void writeStartObject() throws IOException {
        this._verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
        }
        else {
            this._handleStartObject();
        }
    }
    
    public final void writeEndObject() throws IOException {
        if (!this._writeContext.inObject()) {
            this._reportError("Current context not Object but " + this._writeContext.typeDesc());
        }
        this._writeContext = this._writeContext.getParent();
        if (this._cfgPrettyPrinter != null) {
            final int count = this._nextIsAttribute ? 0 : this._writeContext.getEntryCount();
            this._cfgPrettyPrinter.writeEndObject((JsonGenerator)this, count);
        }
        else {
            this._handleEndObject();
        }
    }
    
    public final void _handleStartObject() throws IOException {
        if (this._nextName == null) {
            this.handleMissingName();
        }
        this._elementNameStack.addLast(this._nextName);
        try {
            this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public final void _handleEndObject() throws IOException {
        if (this._elementNameStack.isEmpty()) {
            throw new JsonGenerationException("Can not write END_ELEMENT without open START_ELEMENT", (JsonGenerator)this);
        }
        this._nextName = this._elementNameStack.removeLast();
        try {
            this._nextIsAttribute = false;
            this._xmlWriter.writeEndElement();
            if (this._elementNameStack.isEmpty() && this._xmlPrettyPrinter != null && !this._stax2Emulation) {
                this._xmlPrettyPrinter.writePrologLinefeed(this._xmlWriter);
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeFieldName(final SerializableString name) throws IOException {
        this.writeFieldName(name.getValue());
    }
    
    public void writeString(final String text) throws IOException {
        if (text == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write String value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeAttribute(this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), text);
            }
            else if (this.checkNextIsUnwrapped()) {
                if (this._nextIsCData) {
                    this._xmlWriter.writeCData(text);
                }
                else {
                    this._xmlWriter.writeCharacters(text);
                }
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), text, this._nextIsCData);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                if (this._nextIsCData) {
                    this._xmlWriter.writeCData(text);
                }
                else {
                    this._xmlWriter.writeCharacters(text);
                }
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeString(final char[] text, final int offset, final int len) throws IOException {
        this._verifyValueWrite("write String value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeAttribute(this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), new String(text, offset, len));
            }
            else if (this.checkNextIsUnwrapped()) {
                if (this._nextIsCData) {
                    this._xmlWriter.writeCData(text, offset, len);
                }
                else {
                    this._xmlWriter.writeCharacters(text, offset, len);
                }
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), text, offset, len, this._nextIsCData);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                if (this._nextIsCData) {
                    this._xmlWriter.writeCData(text, offset, len);
                }
                else {
                    this._xmlWriter.writeCharacters(text, offset, len);
                }
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeString(final SerializableString text) throws IOException {
        this.writeString(text.getValue());
    }
    
    public void writeRawUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    public void writeUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    public void writeRawValue(final String text) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRawValue");
        }
        try {
            this._verifyValueWrite("write raw value");
            if (this._nextName == null) {
                this.handleMissingName();
            }
            if (this._nextIsAttribute) {
                this._xmlWriter.writeAttribute(this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), text);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeRaw(text);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRawValue(final String text, final int offset, final int len) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRawValue");
        }
        try {
            this._verifyValueWrite("write raw value");
            if (this._nextName == null) {
                this.handleMissingName();
            }
            if (this._nextIsAttribute) {
                this._xmlWriter.writeAttribute(this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), text.substring(offset, offset + len));
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeRaw(text, offset, len);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRawValue(final char[] text, final int offset, final int len) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRawValue");
        }
        this._verifyValueWrite("write raw value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeAttribute(this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), new String(text, offset, len));
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeRaw(text, offset, len);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRawValue(final SerializableString text) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    public void writeRaw(final String text) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRaw");
        }
        try {
            this._xmlWriter.writeRaw(text);
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRaw(final String text, final int offset, final int len) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRaw");
        }
        try {
            this._xmlWriter.writeRaw(text, offset, len);
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRaw(final char[] text, final int offset, final int len) throws IOException {
        if (this._stax2Emulation) {
            this._reportUnimplementedStax2("writeRaw");
        }
        try {
            this._xmlWriter.writeRaw(text, offset, len);
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeRaw(final char c) throws IOException {
        this.writeRaw(String.valueOf(c));
    }
    
    public void writeBinary(final Base64Variant b64variant, final byte[] data, final int offset, final int len) throws IOException {
        if (data == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write Binary value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                final byte[] fullBuffer = this.toFullBuffer(data, offset, len);
                this._xmlWriter.writeBinaryAttribute("", this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), fullBuffer);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeBinary(data, offset, len);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), data, offset, len);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeBinary(data, offset, len);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public int writeBinary(final Base64Variant b64variant, final InputStream data, final int dataLength) throws IOException {
        if (data == null) {
            this.writeNull();
            return 0;
        }
        this._verifyValueWrite("write Binary value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                final byte[] fullBuffer = this.toFullBuffer(data, dataLength);
                this._xmlWriter.writeBinaryAttribute("", this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), fullBuffer);
            }
            else if (this.checkNextIsUnwrapped()) {
                this.writeStreamAsBinary(data, dataLength);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), this.toFullBuffer(data, dataLength), 0, dataLength);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this.writeStreamAsBinary(data, dataLength);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
        return dataLength;
    }
    
    private void writeStreamAsBinary(final InputStream data, int len) throws IOException, XMLStreamException {
        final byte[] tmp = new byte[3];
        int offset = 0;
        int read;
        while ((read = data.read(tmp, offset, Math.min(3 - offset, len))) != -1) {
            offset += read;
            len -= read;
            if (offset == 3) {
                offset = 0;
                this._xmlWriter.writeBinary(tmp, 0, 3);
            }
            if (len == 0) {
                break;
            }
        }
        if (offset > 0) {
            this._xmlWriter.writeBinary(tmp, 0, offset);
        }
    }
    
    private byte[] toFullBuffer(final byte[] data, final int offset, final int len) {
        if (offset == 0 && len == data.length) {
            return data;
        }
        final byte[] result = new byte[len];
        if (len > 0) {
            System.arraycopy(data, offset, result, 0, len);
        }
        return result;
    }
    
    private byte[] toFullBuffer(final InputStream data, final int len) throws IOException {
        final byte[] result = new byte[len];
        int count;
        for (int offset = 0; offset < len; offset += count) {
            count = data.read(result, offset, len - offset);
            if (count < 0) {
                this._reportError("Too few bytes available: missing " + (len - offset) + " bytes (out of " + len + ")");
            }
        }
        return result;
    }
    
    public void writeBoolean(final boolean value) throws IOException {
        this._verifyValueWrite("write boolean value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeBooleanAttribute((String)null, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), value);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeBoolean(value);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), value);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeBoolean(value);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNull() throws IOException {
        this._verifyValueWrite("write null value");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (!this._nextIsAttribute) {
                if (!this.checkNextIsUnwrapped()) {
                    if (this._xmlPrettyPrinter != null) {
                        this._xmlPrettyPrinter.writeLeafNullElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                    }
                    else {
                        this._xmlWriter.writeEmptyElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                    }
                }
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final int i) throws IOException {
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeIntAttribute((String)null, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), i);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeInt(i);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), i);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeInt(i);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final long l) throws IOException {
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeLongAttribute((String)null, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), l);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeLong(l);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), l);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeLong(l);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final double d) throws IOException {
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeDoubleAttribute((String)null, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), d);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeDouble(d);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), d);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeDouble(d);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final float f) throws IOException {
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeFloatAttribute((String)null, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), f);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeFloat(f);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), f);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeFloat(f);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final BigDecimal dec) throws IOException {
        if (dec == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        final boolean usePlain = this.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        try {
            if (this._nextIsAttribute) {
                if (usePlain) {
                    this._xmlWriter.writeAttribute("", this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), dec.toPlainString());
                }
                else {
                    this._xmlWriter.writeDecimalAttribute("", this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), dec);
                }
            }
            else if (this.checkNextIsUnwrapped()) {
                if (usePlain) {
                    this._xmlWriter.writeCharacters(dec.toPlainString());
                }
                else {
                    this._xmlWriter.writeDecimal(dec);
                }
            }
            else if (this._xmlPrettyPrinter != null) {
                if (usePlain) {
                    this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), dec.toPlainString(), false);
                }
                else {
                    this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), dec);
                }
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                if (usePlain) {
                    this._xmlWriter.writeCharacters(dec.toPlainString());
                }
                else {
                    this._xmlWriter.writeDecimal(dec);
                }
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final BigInteger value) throws IOException {
        if (value == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        if (this._nextName == null) {
            this.handleMissingName();
        }
        try {
            if (this._nextIsAttribute) {
                this._xmlWriter.writeIntegerAttribute("", this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), value);
            }
            else if (this.checkNextIsUnwrapped()) {
                this._xmlWriter.writeInteger(value);
            }
            else if (this._xmlPrettyPrinter != null) {
                this._xmlPrettyPrinter.writeLeafElement(this._xmlWriter, this._nextName.getNamespaceURI(), this._nextName.getLocalPart(), value);
            }
            else {
                this._xmlWriter.writeStartElement(this._nextName.getNamespaceURI(), this._nextName.getLocalPart());
                this._xmlWriter.writeInteger(value);
                this._xmlWriter.writeEndElement();
            }
        }
        catch (final XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
        }
    }
    
    public void writeNumber(final String encodedValue) throws IOException, UnsupportedOperationException {
        this.writeString(encodedValue);
    }
    
    protected final void _verifyValueWrite(final String typeMsg) throws IOException {
        final int status = this._writeContext.writeValue();
        if (status == 5) {
            this._reportError("Can not " + typeMsg + ", expecting field name");
        }
    }
    
    public void flush() throws IOException {
        if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            try {
                this._xmlWriter.flush();
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, (JsonGenerator)this);
            }
        }
    }
    
    public void close() throws IOException {
        super.close();
        if (this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            try {
                while (true) {
                    final JsonStreamContext ctxt = (JsonStreamContext)this._writeContext;
                    if (ctxt.inArray()) {
                        this.writeEndArray();
                    }
                    else {
                        if (!ctxt.inObject()) {
                            break;
                        }
                        this.writeEndObject();
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                throw new JsonGenerationException((Throwable)e, (JsonGenerator)this);
            }
        }
        try {
            if (this._ioContext.isResourceManaged() || this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                this._xmlWriter.closeCompletely();
            }
            else {
                this._xmlWriter.close();
            }
        }
        catch (final XMLStreamException e2) {
            StaxUtil.throwAsGenerationException(e2, (JsonGenerator)this);
        }
    }
    
    protected void _releaseBuffers() {
    }
    
    protected boolean checkNextIsUnwrapped() {
        if (this._nextIsUnwrapped) {
            this._nextIsUnwrapped = false;
            return true;
        }
        return false;
    }
    
    protected void handleMissingName() {
        throw new IllegalStateException("No element/attribute name specified when trying to output element");
    }
    
    protected void _reportUnimplementedStax2(final String missingMethod) throws IOException {
        throw new JsonGenerationException("Underlying Stax XMLStreamWriter (of type " + this._originalXmlWriter.getClass().getName() + ") does not implement Stax2 API natively and is missing method '" + missingMethod + "': this breaks functionality such as indentation that relies on it. You need to upgrade to using compliant Stax implementation like Woodstox or Aalto", (JsonGenerator)this);
    }
    
    public enum Feature implements FormatFeature
    {
        WRITE_XML_DECLARATION(false), 
        WRITE_XML_1_1(false), 
        WRITE_NULLS_AS_XSI_NIL(false);
        
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
