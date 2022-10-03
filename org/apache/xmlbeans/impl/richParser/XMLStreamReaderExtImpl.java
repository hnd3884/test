package org.apache.xmlbeans.impl.richParser;

import org.apache.xmlbeans.impl.common.XMLChar;
import javax.xml.stream.Location;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDate;
import java.util.Date;
import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.impl.util.Base64;
import java.io.ByteArrayInputStream;
import org.apache.xmlbeans.impl.util.HexBin;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.xmlbeans.impl.common.InvalidLexicalValueException;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderExtImpl implements XMLStreamReaderExt
{
    private final XMLStreamReader _xmlStream;
    private final CharSeqTrimWS _charSeq;
    private String _defaultValue;
    
    public XMLStreamReaderExtImpl(final XMLStreamReader xmlStream) {
        if (xmlStream == null) {
            throw new IllegalArgumentException();
        }
        this._xmlStream = xmlStream;
        this._charSeq = new CharSeqTrimWS(this);
    }
    
    public XMLStreamReader getUnderlyingXmlStream() {
        return this._xmlStream;
    }
    
    @Override
    public String getStringValue() throws XMLStreamException {
        this._charSeq.reload(1);
        return this._charSeq.toString();
    }
    
    @Override
    public String getStringValue(final int wsStyle) throws XMLStreamException {
        this._charSeq.reload(1);
        return XmlWhitespace.collapse(this._charSeq.toString(), wsStyle);
    }
    
    @Override
    public boolean getBooleanValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexBoolean(this._charSeq);
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public byte getByteValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexByte(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public short getShortValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexShort(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public int getIntValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexInt(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public long getLongValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexLong(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigInteger getBigIntegerValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexInteger(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigDecimal getBigDecimalValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexDecimal(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public float getFloatValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexFloat(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public double getDoubleValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexDouble(this._charSeq);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public InputStream getHexBinaryValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        final String text = this._charSeq.toString();
        final byte[] buf = HexBin.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid hexBinary value", this._charSeq.getLocation());
    }
    
    @Override
    public InputStream getBase64Value() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        final String text = this._charSeq.toString();
        final byte[] buf = Base64.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid base64Binary value", this._charSeq.getLocation());
    }
    
    @Override
    public XmlCalendar getCalendarValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return new GDateBuilder(this._charSeq).getCalendar();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public Date getDateValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return new GDateBuilder(this._charSeq).getDate();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDate getGDateValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexGDate(this._charSeq);
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDuration getGDurationValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return new GDuration(this._charSeq);
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public QName getQNameValue() throws XMLStreamException, InvalidLexicalValueException {
        this._charSeq.reload(2);
        try {
            return XsTypeConverter.lexQName(this._charSeq, this._xmlStream.getNamespaceContext());
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e.getMessage(), this._charSeq.getLocation());
        }
    }
    
    @Override
    public String getAttributeStringValue(final int index) throws XMLStreamException {
        return this._xmlStream.getAttributeValue(index);
    }
    
    @Override
    public String getAttributeStringValue(final int index, final int wsStyle) throws XMLStreamException {
        return XmlWhitespace.collapse(this._xmlStream.getAttributeValue(index), wsStyle);
    }
    
    @Override
    public boolean getAttributeBooleanValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexBoolean(this._charSeq.reloadAtt(index, 2));
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public byte getAttributeByteValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexByte(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public short getAttributeShortValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexShort(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public int getAttributeIntValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexInt(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public long getAttributeLongValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexLong(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigInteger getAttributeBigIntegerValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexInteger(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigDecimal getAttributeBigDecimalValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexDecimal(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public float getAttributeFloatValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexFloat(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public double getAttributeDoubleValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexDouble(this._charSeq.reloadAtt(index, 2));
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public InputStream getAttributeHexBinaryValue(final int index) throws XMLStreamException {
        final String text = this._charSeq.reloadAtt(index, 2).toString();
        final byte[] buf = HexBin.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid hexBinary value", this._charSeq.getLocation());
    }
    
    @Override
    public InputStream getAttributeBase64Value(final int index) throws XMLStreamException {
        final String text = this._charSeq.reloadAtt(index, 2).toString();
        final byte[] buf = Base64.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid base64Binary value", this._charSeq.getLocation());
    }
    
    @Override
    public XmlCalendar getAttributeCalendarValue(final int index) throws XMLStreamException {
        try {
            return new GDateBuilder(this._charSeq.reloadAtt(index, 2)).getCalendar();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public Date getAttributeDateValue(final int index) throws XMLStreamException {
        try {
            return new GDateBuilder(this._charSeq.reloadAtt(index, 2)).getDate();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDate getAttributeGDateValue(final int index) throws XMLStreamException {
        try {
            return new GDate(this._charSeq.reloadAtt(index, 2));
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDuration getAttributeGDurationValue(final int index) throws XMLStreamException {
        try {
            return new GDuration(this._charSeq.reloadAtt(index, 2));
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public QName getAttributeQNameValue(final int index) throws XMLStreamException {
        try {
            return XsTypeConverter.lexQName(this._charSeq.reloadAtt(index, 2), this._xmlStream.getNamespaceContext());
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e.getMessage(), this._charSeq.getLocation());
        }
    }
    
    @Override
    public String getAttributeStringValue(final String uri, final String local) throws XMLStreamException {
        return this._charSeq.reloadAtt(uri, local, 1).toString();
    }
    
    @Override
    public String getAttributeStringValue(final String uri, final String local, final int wsStyle) throws XMLStreamException {
        return XmlWhitespace.collapse(this._xmlStream.getAttributeValue(uri, local), wsStyle);
    }
    
    @Override
    public boolean getAttributeBooleanValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexBoolean(cs);
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public byte getAttributeByteValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexByte(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public short getAttributeShortValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexShort(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public int getAttributeIntValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexInt(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public long getAttributeLongValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexLong(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigInteger getAttributeBigIntegerValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexInteger(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public BigDecimal getAttributeBigDecimalValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexDecimal(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public float getAttributeFloatValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexFloat(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public double getAttributeDoubleValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexDouble(cs);
        }
        catch (final NumberFormatException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public InputStream getAttributeHexBinaryValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        final String text = cs.toString();
        final byte[] buf = HexBin.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid hexBinary value", this._charSeq.getLocation());
    }
    
    @Override
    public InputStream getAttributeBase64Value(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        final String text = cs.toString();
        final byte[] buf = Base64.decode(text.getBytes());
        if (buf != null) {
            return new ByteArrayInputStream(buf);
        }
        throw new InvalidLexicalValueException("invalid base64Binary value", this._charSeq.getLocation());
    }
    
    @Override
    public XmlCalendar getAttributeCalendarValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return new GDateBuilder(cs).getCalendar();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public Date getAttributeDateValue(final String uri, final String local) throws XMLStreamException {
        try {
            final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
            return new GDateBuilder(cs).getDate();
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDate getAttributeGDateValue(final String uri, final String local) throws XMLStreamException {
        try {
            final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
            return new GDate(cs);
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public GDuration getAttributeGDurationValue(final String uri, final String local) throws XMLStreamException {
        try {
            return new GDuration(this._charSeq.reloadAtt(uri, local, 2));
        }
        catch (final IllegalArgumentException e) {
            throw new InvalidLexicalValueException(e, this._charSeq.getLocation());
        }
    }
    
    @Override
    public QName getAttributeQNameValue(final String uri, final String local) throws XMLStreamException {
        final CharSequence cs = this._charSeq.reloadAtt(uri, local, 2);
        try {
            return XsTypeConverter.lexQName(cs, this._xmlStream.getNamespaceContext());
        }
        catch (final InvalidLexicalValueException e) {
            throw new InvalidLexicalValueException(e.getMessage(), this._charSeq.getLocation());
        }
    }
    
    @Override
    public void setDefaultValue(final String defaultValue) throws XMLStreamException {
        this._defaultValue = defaultValue;
    }
    
    @Override
    public Object getProperty(final String s) throws IllegalArgumentException {
        return this._xmlStream.getProperty(s);
    }
    
    @Override
    public int next() throws XMLStreamException {
        return this._xmlStream.next();
    }
    
    @Override
    public void require(final int i, final String s, final String s1) throws XMLStreamException {
        this._xmlStream.require(i, s, s1);
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        return this._xmlStream.getElementText();
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        return this._xmlStream.nextTag();
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this._xmlStream.hasNext();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this._xmlStream.close();
    }
    
    @Override
    public String getNamespaceURI(final String s) {
        return this._xmlStream.getNamespaceURI(s);
    }
    
    @Override
    public boolean isStartElement() {
        return this._xmlStream.isStartElement();
    }
    
    @Override
    public boolean isEndElement() {
        return this._xmlStream.isEndElement();
    }
    
    @Override
    public boolean isCharacters() {
        return this._xmlStream.isCharacters();
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this._xmlStream.isWhiteSpace();
    }
    
    @Override
    public String getAttributeValue(final String s, final String s1) {
        return this._xmlStream.getAttributeValue(s, s1);
    }
    
    @Override
    public int getAttributeCount() {
        return this._xmlStream.getAttributeCount();
    }
    
    @Override
    public QName getAttributeName(final int i) {
        return this._xmlStream.getAttributeName(i);
    }
    
    @Override
    public String getAttributeNamespace(final int i) {
        return this._xmlStream.getAttributeNamespace(i);
    }
    
    @Override
    public String getAttributeLocalName(final int i) {
        return this._xmlStream.getAttributeLocalName(i);
    }
    
    @Override
    public String getAttributePrefix(final int i) {
        return this._xmlStream.getAttributePrefix(i);
    }
    
    @Override
    public String getAttributeType(final int i) {
        return this._xmlStream.getAttributeType(i);
    }
    
    @Override
    public String getAttributeValue(final int i) {
        return this._xmlStream.getAttributeValue(i);
    }
    
    @Override
    public boolean isAttributeSpecified(final int i) {
        return this._xmlStream.isAttributeSpecified(i);
    }
    
    @Override
    public int getNamespaceCount() {
        return this._xmlStream.getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int i) {
        return this._xmlStream.getNamespacePrefix(i);
    }
    
    @Override
    public String getNamespaceURI(final int i) {
        return this._xmlStream.getNamespaceURI(i);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this._xmlStream.getNamespaceContext();
    }
    
    @Override
    public int getEventType() {
        return this._xmlStream.getEventType();
    }
    
    @Override
    public String getText() {
        return this._xmlStream.getText();
    }
    
    @Override
    public char[] getTextCharacters() {
        return this._xmlStream.getTextCharacters();
    }
    
    @Override
    public int getTextCharacters(final int i, final char[] chars, final int i1, final int i2) throws XMLStreamException {
        return this._xmlStream.getTextCharacters(i, chars, i1, i2);
    }
    
    @Override
    public int getTextStart() {
        return this._xmlStream.getTextStart();
    }
    
    @Override
    public int getTextLength() {
        return this._xmlStream.getTextLength();
    }
    
    @Override
    public String getEncoding() {
        return this._xmlStream.getEncoding();
    }
    
    @Override
    public boolean hasText() {
        return this._xmlStream.hasText();
    }
    
    @Override
    public Location getLocation() {
        return this._xmlStream.getLocation();
    }
    
    @Override
    public QName getName() {
        return this._xmlStream.getName();
    }
    
    @Override
    public String getLocalName() {
        return this._xmlStream.getLocalName();
    }
    
    @Override
    public boolean hasName() {
        return this._xmlStream.hasName();
    }
    
    @Override
    public String getNamespaceURI() {
        return this._xmlStream.getNamespaceURI();
    }
    
    @Override
    public String getPrefix() {
        return this._xmlStream.getPrefix();
    }
    
    @Override
    public String getVersion() {
        return this._xmlStream.getVersion();
    }
    
    @Override
    public boolean isStandalone() {
        return this._xmlStream.isStandalone();
    }
    
    @Override
    public boolean standaloneSet() {
        return this._xmlStream.standaloneSet();
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this._xmlStream.getCharacterEncodingScheme();
    }
    
    @Override
    public String getPITarget() {
        return this._xmlStream.getPITarget();
    }
    
    @Override
    public String getPIData() {
        return this._xmlStream.getPIData();
    }
    
    private static class CharSeqTrimWS implements CharSequence
    {
        static final int XMLWHITESPACE_PRESERVE = 1;
        static final int XMLWHITESPACE_TRIM = 2;
        private static int INITIAL_SIZE;
        private char[] _buf;
        private int _start;
        private int _length;
        private int _nonWSStart;
        private int _nonWSEnd;
        private String _toStringValue;
        private XMLStreamReaderExtImpl _xmlSteam;
        private final ExtLocation _location;
        private boolean _hasText;
        
        CharSeqTrimWS(final XMLStreamReaderExtImpl xmlSteam) {
            this._buf = new char[CharSeqTrimWS.INITIAL_SIZE];
            this._length = 0;
            this._nonWSStart = 0;
            this._nonWSEnd = 0;
            this._xmlSteam = xmlSteam;
            this._location = new ExtLocation();
        }
        
        void reload(final int style) throws XMLStreamException {
            this._toStringValue = null;
            this._location.reset();
            this._hasText = false;
            this.fillBuffer();
            if (style == 1) {
                this._nonWSStart = 0;
                this._nonWSEnd = this._length;
                if (!this._hasText && this._xmlSteam._defaultValue != null) {
                    this._length = 0;
                    this.fillBufferFromString(this._xmlSteam._defaultValue);
                }
            }
            else if (style == 2) {
                this._nonWSStart = 0;
                while (this._nonWSStart < this._length && XMLChar.isSpace(this._buf[this._nonWSStart])) {
                    ++this._nonWSStart;
                }
                this._nonWSEnd = this._length;
                while (this._nonWSEnd > this._nonWSStart && XMLChar.isSpace(this._buf[this._nonWSEnd - 1])) {
                    --this._nonWSEnd;
                }
                if (this.length() == 0 && this._xmlSteam._defaultValue != null) {
                    this._length = 0;
                    this.fillBufferFromString(this._xmlSteam._defaultValue);
                    this._nonWSStart = 0;
                    while (this._nonWSStart < this._length && XMLChar.isSpace(this._buf[this._nonWSStart])) {
                        ++this._nonWSStart;
                    }
                    this._nonWSEnd = this._length;
                    while (this._nonWSEnd > this._nonWSStart) {
                        if (!XMLChar.isSpace(this._buf[this._nonWSEnd - 1])) {
                            break;
                        }
                        --this._nonWSEnd;
                    }
                }
            }
            this._xmlSteam._defaultValue = null;
        }
        
        private void fillBuffer() throws XMLStreamException {
            this._length = 0;
            if (this._xmlSteam.getEventType() == 7) {
                this._xmlSteam.next();
            }
            if (this._xmlSteam.isStartElement()) {
                this._xmlSteam.next();
            }
            int depth = 0;
            String error = null;
            int eventType = this._xmlSteam.getEventType();
        Label_0281:
            while (true) {
                switch (eventType) {
                    case 4:
                    case 6:
                    case 12: {
                        this._location.set(this._xmlSteam.getLocation());
                        if (depth == 0) {
                            this.addTextToBuffer();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this._location.set(this._xmlSteam.getLocation());
                        break Label_0281;
                    }
                    case 2: {
                        this._location.set(this._xmlSteam.getLocation());
                        if (--depth < 0) {
                            break Label_0281;
                        }
                        break;
                    }
                    case 9: {
                        this._location.set(this._xmlSteam.getLocation());
                        this.addEntityToBuffer();
                        break;
                    }
                    case 1: {
                        ++depth;
                        error = "Unexpected element '" + this._xmlSteam.getName() + "' in text content.";
                        this._location.set(this._xmlSteam.getLocation());
                        break;
                    }
                }
                eventType = this._xmlSteam.next();
            }
            if (error != null) {
                throw new XMLStreamException(error);
            }
        }
        
        private void ensureBufferLength(final int lengthToAdd) {
            if (this._length + lengthToAdd > this._buf.length) {
                final char[] newBuf = new char[this._length + lengthToAdd];
                if (this._length > 0) {
                    System.arraycopy(this._buf, 0, newBuf, 0, this._length);
                }
                this._buf = newBuf;
            }
        }
        
        private void fillBufferFromString(final CharSequence value) {
            final int textLength = value.length();
            this.ensureBufferLength(textLength);
            for (int i = 0; i < textLength; ++i) {
                this._buf[i] = value.charAt(i);
            }
            this._length = textLength;
        }
        
        private void addTextToBuffer() {
            this._hasText = true;
            final int textLength = this._xmlSteam.getTextLength();
            this.ensureBufferLength(textLength);
            System.arraycopy(this._xmlSteam.getTextCharacters(), this._xmlSteam.getTextStart(), this._buf, this._length, textLength);
            this._length += textLength;
        }
        
        private void addEntityToBuffer() {
            final String text = this._xmlSteam.getText();
            final int textLength = text.length();
            this.ensureBufferLength(textLength);
            text.getChars(0, text.length(), this._buf, this._length);
            this._length += text.length();
        }
        
        CharSequence reloadAtt(final int index, final int style) throws XMLStreamException {
            this._location.reset();
            this._location.set(this._xmlSteam.getLocation());
            String value = this._xmlSteam.getAttributeValue(index);
            if (value == null && this._xmlSteam._defaultValue != null) {
                value = this._xmlSteam._defaultValue;
            }
            this._xmlSteam._defaultValue = null;
            final int length = value.length();
            if (style == 1) {
                return value;
            }
            if (style != 2) {
                throw new IllegalStateException("unknown style");
            }
            int nonWSStart;
            for (nonWSStart = 0; nonWSStart < length && XMLChar.isSpace(value.charAt(nonWSStart)); ++nonWSStart) {}
            int nonWSEnd;
            for (nonWSEnd = length; nonWSEnd > nonWSStart && XMLChar.isSpace(value.charAt(nonWSEnd - 1)); --nonWSEnd) {}
            if (nonWSStart == 0 && nonWSEnd == length) {
                return value;
            }
            return value.subSequence(nonWSStart, nonWSEnd);
        }
        
        CharSequence reloadAtt(final String uri, final String local, final int style) throws XMLStreamException {
            this._location.reset();
            this._location.set(this._xmlSteam.getLocation());
            String value = this._xmlSteam.getAttributeValue(uri, local);
            if (value == null && this._xmlSteam._defaultValue != null) {
                value = this._xmlSteam._defaultValue;
            }
            this._xmlSteam._defaultValue = null;
            final int length = value.length();
            if (style == 1) {
                return value;
            }
            if (style != 2) {
                throw new IllegalStateException("unknown style");
            }
            this._nonWSStart = 0;
            while (this._nonWSStart < length && XMLChar.isSpace(value.charAt(this._nonWSStart))) {
                ++this._nonWSStart;
            }
            this._nonWSEnd = length;
            while (this._nonWSEnd > this._nonWSStart && XMLChar.isSpace(value.charAt(this._nonWSEnd - 1))) {
                --this._nonWSEnd;
            }
            if (this._nonWSStart == 0 && this._nonWSEnd == length) {
                return value;
            }
            return value.subSequence(this._nonWSStart, this._nonWSEnd);
        }
        
        Location getLocation() {
            final ExtLocation loc = new ExtLocation();
            loc.set(this._location);
            return loc;
        }
        
        @Override
        public int length() {
            return this._nonWSEnd - this._nonWSStart;
        }
        
        @Override
        public char charAt(final int index) {
            assert index < this._nonWSEnd - this._nonWSStart && -1 < index : "Index " + index + " must be >-1 and <" + (this._nonWSEnd - this._nonWSStart);
            return this._buf[this._nonWSStart + index];
        }
        
        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new String(this._buf, this._nonWSStart + start, end - start);
        }
        
        @Override
        public String toString() {
            if (this._toStringValue != null) {
                return this._toStringValue;
            }
            return this._toStringValue = new String(this._buf, this._nonWSStart, this._nonWSEnd - this._nonWSStart);
        }
        
        static {
            CharSeqTrimWS.INITIAL_SIZE = 100;
        }
        
        private static class ExtLocation implements Location
        {
            private int _line;
            private int _col;
            private int _off;
            private String _pid;
            private String _sid;
            private boolean _isSet;
            
            ExtLocation() {
                this._isSet = false;
            }
            
            @Override
            public int getLineNumber() {
                if (this._isSet) {
                    return this._line;
                }
                throw new IllegalStateException();
            }
            
            @Override
            public int getColumnNumber() {
                if (this._isSet) {
                    return this._col;
                }
                throw new IllegalStateException();
            }
            
            @Override
            public int getCharacterOffset() {
                if (this._isSet) {
                    return this._off;
                }
                throw new IllegalStateException();
            }
            
            @Override
            public String getPublicId() {
                if (this._isSet) {
                    return this._pid;
                }
                throw new IllegalStateException();
            }
            
            @Override
            public String getSystemId() {
                if (this._isSet) {
                    return this._sid;
                }
                throw new IllegalStateException();
            }
            
            void set(final Location loc) {
                if (this._isSet) {
                    return;
                }
                this._isSet = true;
                this._line = loc.getLineNumber();
                this._col = loc.getColumnNumber();
                this._off = loc.getCharacterOffset();
                this._pid = loc.getPublicId();
                this._sid = loc.getSystemId();
            }
            
            void reset() {
                this._isSet = false;
            }
        }
    }
}
