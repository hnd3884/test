package com.fasterxml.jackson.dataformat.xml.util;

import java.util.Arrays;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamWriter2;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.Serializable;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.dataformat.xml.XmlPrettyPrinter;

public class DefaultXmlPrettyPrinter implements XmlPrettyPrinter, Instantiatable<DefaultXmlPrettyPrinter>, Serializable
{
    private static final long serialVersionUID = 1L;
    protected Indenter _arrayIndenter;
    protected Indenter _objectIndenter;
    protected boolean _spacesInObjectEntries;
    protected transient int _nesting;
    protected transient boolean _justHadStartElement;
    
    public DefaultXmlPrettyPrinter() {
        this._arrayIndenter = new FixedSpaceIndenter();
        this._objectIndenter = new Lf2SpacesIndenter();
        this._spacesInObjectEntries = true;
        this._nesting = 0;
    }
    
    protected DefaultXmlPrettyPrinter(final DefaultXmlPrettyPrinter base) {
        this._arrayIndenter = new FixedSpaceIndenter();
        this._objectIndenter = new Lf2SpacesIndenter();
        this._spacesInObjectEntries = true;
        this._nesting = 0;
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
    }
    
    public void indentArraysWith(final Indenter i) {
        this._arrayIndenter = ((i == null) ? new NopIndenter() : i);
    }
    
    public void indentObjectsWith(final Indenter i) {
        this._objectIndenter = ((i == null) ? new NopIndenter() : i);
    }
    
    public void spacesInObjectEntries(final boolean b) {
        this._spacesInObjectEntries = b;
    }
    
    public DefaultXmlPrettyPrinter createInstance() {
        return new DefaultXmlPrettyPrinter(this);
    }
    
    public void writeRootValueSeparator(final JsonGenerator gen) throws IOException {
        gen.writeRaw('\n');
    }
    
    public void beforeArrayValues(final JsonGenerator gen) throws IOException {
    }
    
    public void writeStartArray(final JsonGenerator gen) throws IOException {
    }
    
    public void writeArrayValueSeparator(final JsonGenerator gen) throws IOException {
    }
    
    public void writeEndArray(final JsonGenerator gen, final int nrOfValues) throws IOException {
    }
    
    public void beforeObjectEntries(final JsonGenerator gen) throws IOException, JsonGenerationException {
    }
    
    public void writeStartObject(final JsonGenerator gen) throws IOException {
        if (!this._objectIndenter.isInline()) {
            if (this._nesting > 0) {
                this._objectIndenter.writeIndentation(gen, this._nesting);
            }
            ++this._nesting;
        }
        this._justHadStartElement = true;
        ((ToXmlGenerator)gen)._handleStartObject();
    }
    
    public void writeObjectEntrySeparator(final JsonGenerator gen) throws IOException {
    }
    
    public void writeObjectFieldValueSeparator(final JsonGenerator gen) throws IOException {
    }
    
    public void writeEndObject(final JsonGenerator gen, final int nrOfEntries) throws IOException {
        if (!this._objectIndenter.isInline()) {
            --this._nesting;
        }
        if (this._justHadStartElement) {
            this._justHadStartElement = false;
        }
        else {
            this._objectIndenter.writeIndentation(gen, this._nesting);
        }
        ((ToXmlGenerator)gen)._handleEndObject();
    }
    
    @Override
    public void writeStartElement(final XMLStreamWriter2 sw, final String nsURI, final String localName) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            if (this._justHadStartElement) {
                this._justHadStartElement = false;
            }
            this._objectIndenter.writeIndentation(sw, this._nesting);
            ++this._nesting;
        }
        sw.writeStartElement(nsURI, localName);
        this._justHadStartElement = true;
    }
    
    @Override
    public void writeEndElement(final XMLStreamWriter2 sw, final int nrOfEntries) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            --this._nesting;
        }
        if (this._justHadStartElement) {
            this._justHadStartElement = false;
        }
        else {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeEndElement();
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final String text, final boolean isCData) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        if (isCData) {
            sw.writeCData(text);
        }
        else {
            sw.writeCharacters(text);
        }
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final char[] buffer, final int offset, final int len, final boolean isCData) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        if (isCData) {
            sw.writeCData(buffer, offset, len);
        }
        else {
            sw.writeCharacters(buffer, offset, len);
        }
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final boolean value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeBoolean(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final int value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeInt(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final long value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeLong(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final double value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeDouble(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final float value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeFloat(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final BigInteger value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeInteger(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final BigDecimal value) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeDecimal(value);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafElement(final XMLStreamWriter2 sw, final String nsURI, final String localName, final byte[] data, final int offset, final int len) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeStartElement(nsURI, localName);
        sw.writeBinary(data, offset, len);
        sw.writeEndElement();
        this._justHadStartElement = false;
    }
    
    @Override
    public void writeLeafNullElement(final XMLStreamWriter2 sw, final String nsURI, final String localName) throws XMLStreamException {
        if (!this._objectIndenter.isInline()) {
            this._objectIndenter.writeIndentation(sw, this._nesting);
        }
        sw.writeEmptyElement(nsURI, localName);
        this._justHadStartElement = false;
    }
    
    @Override
    public void writePrologLinefeed(final XMLStreamWriter2 sw) throws XMLStreamException {
        sw.writeRaw(Lf2SpacesIndenter.SYSTEM_LINE_SEPARATOR);
    }
    
    protected static class NopIndenter implements Indenter, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        public NopIndenter() {
        }
        
        @Override
        public void writeIndentation(final JsonGenerator jg, final int level) {
        }
        
        @Override
        public boolean isInline() {
            return true;
        }
        
        @Override
        public void writeIndentation(final XMLStreamWriter2 sw, final int level) {
        }
    }
    
    protected static class FixedSpaceIndenter implements Indenter, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        public FixedSpaceIndenter() {
        }
        
        @Override
        public void writeIndentation(final XMLStreamWriter2 sw, final int level) throws XMLStreamException {
            sw.writeRaw(" ");
        }
        
        @Override
        public void writeIndentation(final JsonGenerator g, final int level) throws IOException {
            g.writeRaw(' ');
        }
        
        @Override
        public boolean isInline() {
            return true;
        }
    }
    
    protected static class Lf2SpacesIndenter implements Indenter, Serializable
    {
        private static final long serialVersionUID = 1L;
        static final String SYSTEM_LINE_SEPARATOR;
        static final int SPACE_COUNT = 64;
        static final char[] SPACES;
        
        public Lf2SpacesIndenter() {
        }
        
        @Override
        public boolean isInline() {
            return false;
        }
        
        @Override
        public void writeIndentation(final XMLStreamWriter2 sw, int level) throws XMLStreamException {
            sw.writeRaw(Lf2SpacesIndenter.SYSTEM_LINE_SEPARATOR);
            for (level += level; level > 64; level -= Lf2SpacesIndenter.SPACES.length) {
                sw.writeRaw(Lf2SpacesIndenter.SPACES, 0, 64);
            }
            sw.writeRaw(Lf2SpacesIndenter.SPACES, 0, level);
        }
        
        @Override
        public void writeIndentation(final JsonGenerator jg, int level) throws IOException {
            jg.writeRaw(Lf2SpacesIndenter.SYSTEM_LINE_SEPARATOR);
            for (level += level; level > 64; level -= Lf2SpacesIndenter.SPACES.length) {
                jg.writeRaw(Lf2SpacesIndenter.SPACES, 0, 64);
            }
            jg.writeRaw(Lf2SpacesIndenter.SPACES, 0, level);
        }
        
        static {
            String lf = null;
            try {
                lf = System.getProperty("line.separator");
            }
            catch (final Throwable t) {}
            SYSTEM_LINE_SEPARATOR = ((lf == null) ? "\n" : lf);
            Arrays.fill(SPACES = new char[64], ' ');
        }
    }
    
    public interface Indenter
    {
        void writeIndentation(final JsonGenerator p0, final int p1) throws IOException;
        
        void writeIndentation(final XMLStreamWriter2 p0, final int p1) throws XMLStreamException;
        
        boolean isInline();
    }
}
