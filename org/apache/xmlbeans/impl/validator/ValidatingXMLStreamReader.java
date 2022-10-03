package org.apache.xmlbeans.impl.validator;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import javax.xml.stream.Location;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.SchemaField;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import javax.xml.stream.XMLStreamException;
import java.util.List;
import java.util.Collection;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

public class ValidatingXMLStreamReader extends StreamReaderDelegate implements XMLStreamReader
{
    public static final String OPTION_ATTTRIBUTE_VALIDATION_COMPAT_MODE = "OPTION_ATTTRIBUTE_VALIDATION_COMPAT_MODE";
    private static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final QName XSI_TYPE;
    private static final QName XSI_NIL;
    private static final QName XSI_SL;
    private static final QName XSI_NSL;
    private SchemaType _contentType;
    private SchemaTypeLoader _stl;
    private XmlOptions _options;
    private Collection _errorListener;
    protected Validator _validator;
    private final ElementEventImpl _elemEvent;
    private final AttributeEventImpl _attEvent;
    private final SimpleEventImpl _simpleEvent;
    private PackTextXmlStreamReader _packTextXmlStreamReader;
    private int _state;
    private final int STATE_FIRSTEVENT = 0;
    private final int STATE_VALIDATING = 1;
    private final int STATE_ATTBUFFERING = 2;
    private final int STATE_ERROR = 3;
    private List _attNamesList;
    private List _attValuesList;
    private SchemaType _xsiType;
    private int _depth;
    
    public ValidatingXMLStreamReader() {
        this._elemEvent = new ElementEventImpl();
        this._attEvent = new AttributeEventImpl();
        this._simpleEvent = new SimpleEventImpl();
        this._packTextXmlStreamReader = new PackTextXmlStreamReader();
    }
    
    public void init(final XMLStreamReader xsr, final boolean startWithCurrentEvent, final SchemaType contentType, final SchemaTypeLoader stl, final XmlOptions options, final Collection errorListener) {
        this._packTextXmlStreamReader.init(xsr);
        this.setParent(this._packTextXmlStreamReader);
        this._contentType = contentType;
        this._stl = stl;
        this._options = options;
        this._errorListener = errorListener;
        this._elemEvent.setXMLStreamReader(this._packTextXmlStreamReader);
        this._attEvent.setXMLStreamReader(this._packTextXmlStreamReader);
        this._simpleEvent.setXMLStreamReader(this._packTextXmlStreamReader);
        this._validator = null;
        this._state = 0;
        if (this._attNamesList != null) {
            this._attNamesList.clear();
            this._attValuesList.clear();
        }
        this._xsiType = null;
        this._depth = 0;
        if (startWithCurrentEvent) {
            final int evType = this.getEventType();
            this.validate_event(evType);
        }
    }
    
    @Override
    public Object getProperty(final String s) throws IllegalArgumentException {
        return super.getProperty(s);
    }
    
    @Override
    public int next() throws XMLStreamException {
        final int evType = super.next();
        this.validate_event(evType);
        return evType;
    }
    
    private void validate_event(final int evType) {
        if (this._state == 3) {
            return;
        }
        if (this._depth < 0) {
            throw new IllegalArgumentException("ValidatingXMLStreamReader cannot go further than the subtree is was initialized on.");
        }
        switch (evType) {
            case 1: {
                ++this._depth;
                if (this._state == 2) {
                    this.pushBufferedAttributes();
                }
                if (this._validator == null) {
                    final QName qname = new QName(this.getNamespaceURI(), this.getLocalName());
                    if (this._contentType == null) {
                        this._contentType = this.typeForGlobalElement(qname);
                    }
                    if (this._state == 3) {
                        break;
                    }
                    this.initValidator(this._contentType);
                    this._validator.nextEvent(1, this._elemEvent);
                }
                this._validator.nextEvent(1, this._elemEvent);
                final int attCount = this.getAttributeCount();
                this.validate_attributes(attCount);
                break;
            }
            case 10: {
                if (this.getAttributeCount() == 0) {
                    break;
                }
                if (this._state == 0 || this._state == 2) {
                    for (int i = 0; i < this.getAttributeCount(); ++i) {
                        final QName qname2 = new QName(this.getAttributeNamespace(i), this.getAttributeLocalName(i));
                        if (qname2.equals(ValidatingXMLStreamReader.XSI_TYPE)) {
                            final String xsiTypeValue = this.getAttributeValue(i);
                            final String uri = super.getNamespaceURI(QNameHelper.getPrefixPart(xsiTypeValue));
                            final QName xsiTypeQname = new QName(uri, QNameHelper.getLocalPart(xsiTypeValue));
                            this._xsiType = this._stl.findType(xsiTypeQname);
                        }
                        if (this._attNamesList == null) {
                            this._attNamesList = new ArrayList();
                            this._attValuesList = new ArrayList();
                        }
                        if (!this.isSpecialAttribute(qname2)) {
                            this._attNamesList.add(qname2);
                            this._attValuesList.add(this.getAttributeValue(i));
                        }
                    }
                    this._state = 2;
                    break;
                }
                throw new IllegalStateException("ATT event must be only at the beggining of the stream.");
            }
            case 2:
            case 8: {
                --this._depth;
                if (this._state == 2) {
                    this.pushBufferedAttributes();
                }
                this._validator.nextEvent(2, this._elemEvent);
                break;
            }
            case 4:
            case 12: {
                if (this._state == 2) {
                    this.pushBufferedAttributes();
                }
                if (this._validator == null) {
                    if (this._contentType == null) {
                        if (this.isWhiteSpace()) {
                            break;
                        }
                        this.addError("No content type provided for validation of a content model.");
                        this._state = 3;
                        break;
                    }
                    else {
                        this.initValidator(this._contentType);
                        this._validator.nextEvent(1, this._simpleEvent);
                    }
                }
                this._validator.nextEvent(3, this._elemEvent);
                break;
            }
            case 7: {
                ++this._depth;
                break;
            }
            case 3:
            case 5:
            case 6:
            case 9:
            case 11:
            case 13:
            case 14:
            case 15: {
                break;
            }
            default: {
                throw new IllegalStateException("Unknown event type.");
            }
        }
    }
    
    private void pushBufferedAttributes() {
        SchemaType validationType = null;
        if (this._xsiType != null) {
            if (this._contentType == null) {
                validationType = this._xsiType;
            }
            else {
                if (!this._contentType.isAssignableFrom(this._xsiType)) {
                    this.addError("Specified type '" + this._contentType + "' not compatible with found xsi:type '" + this._xsiType + "'.");
                    this._state = 3;
                    return;
                }
                validationType = this._xsiType;
            }
        }
        else if (this._contentType != null) {
            validationType = this._contentType;
        }
        else {
            if (this._attNamesList == null) {
                this.addError("No content type provided for validation of a content model.");
                this._state = 3;
                return;
            }
            validationType = this._stl.findAttributeType(this._attNamesList.get(0));
            if (validationType == null) {
                this.addError("A schema global attribute with name '" + this._attNamesList.get(0) + "' could not be found in the current schema type loader.");
                this._state = 3;
                return;
            }
        }
        this.initValidator(validationType);
        this._validator.nextEvent(1, this._simpleEvent);
        this.validate_attributes(this._attNamesList.size());
        this._attNamesList = null;
        this._attValuesList = null;
        this._state = 1;
    }
    
    private boolean isSpecialAttribute(final QName qn) {
        return qn.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema-instance") && (qn.getLocalPart().equals(ValidatingXMLStreamReader.XSI_TYPE.getLocalPart()) || qn.getLocalPart().equals(ValidatingXMLStreamReader.XSI_NIL.getLocalPart()) || qn.getLocalPart().equals(ValidatingXMLStreamReader.XSI_SL.getLocalPart()) || qn.getLocalPart().equals(ValidatingXMLStreamReader.XSI_NSL.getLocalPart()));
    }
    
    private void initValidator(final SchemaType schemaType) {
        assert schemaType != null;
        this._validator = new Validator(schemaType, null, this._stl, this._options, this._errorListener);
    }
    
    private SchemaType typeForGlobalElement(final QName qname) {
        assert qname != null;
        final SchemaType docType = this._stl.findDocumentType(qname);
        if (docType == null) {
            this.addError("Schema document type not found for element '" + qname + "'.");
            this._state = 3;
        }
        return docType;
    }
    
    private void addError(final String msg) {
        String source = null;
        final Location location = this.getLocation();
        if (location != null) {
            source = location.getPublicId();
            if (source == null) {
                source = location.getSystemId();
            }
            this._errorListener.add(XmlError.forLocation(msg, source, location));
        }
        else {
            this._errorListener.add(XmlError.forMessage(msg));
        }
    }
    
    protected void validate_attributes(final int attCount) {
        for (int i = 0; i < attCount; ++i) {
            this.validate_attribute(i);
        }
        if (this._options == null || !this._options.hasOption("OPTION_ATTTRIBUTE_VALIDATION_COMPAT_MODE")) {
            this._validator.nextEvent(5, this._simpleEvent);
        }
    }
    
    protected void validate_attribute(final int attIndex) {
        ValidatorListener.Event event;
        if (this._attNamesList == null) {
            this._attEvent.setAttributeIndex(attIndex);
            final QName qn = this._attEvent.getName();
            if (this.isSpecialAttribute(qn)) {
                return;
            }
            event = this._attEvent;
        }
        else {
            this._simpleEvent._qname = this._attNamesList.get(attIndex);
            this._simpleEvent._text = this._attValuesList.get(attIndex);
            event = this._simpleEvent;
        }
        this._validator.nextEvent(4, event);
    }
    
    public boolean isValid() {
        return this._state != 3 && this._validator != null && this._validator.isValid();
    }
    
    static {
        XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
        XSI_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil");
        XSI_SL = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        XSI_NSL = new QName("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
    }
    
    private static class PackTextXmlStreamReader extends StreamReaderDelegate implements XMLStreamReader
    {
        private boolean _hasBufferedText;
        private StringBuffer _buffer;
        private int _textEventType;
        
        private PackTextXmlStreamReader() {
            this._buffer = new StringBuffer();
        }
        
        void init(final XMLStreamReader xmlstream) {
            this.setParent(xmlstream);
            this._hasBufferedText = false;
            this._buffer.delete(0, this._buffer.length());
        }
        
        @Override
        public int next() throws XMLStreamException {
            if (this._hasBufferedText) {
                this.clearBuffer();
                return super.getEventType();
            }
            final int evType = super.next();
            if (evType == 4 || evType == 12 || evType == 6) {
                this._textEventType = evType;
                this.bufferText();
            }
            return evType;
        }
        
        private void clearBuffer() {
            this._buffer.delete(0, this._buffer.length());
            this._hasBufferedText = false;
        }
        
        private void bufferText() throws XMLStreamException {
            if (super.hasText()) {
                this._buffer.append(super.getText());
            }
            this._hasBufferedText = true;
            while (this.hasNext()) {
                final int evType = super.next();
                switch (evType) {
                    case 4:
                    case 6:
                    case 12: {
                        if (super.hasText()) {
                            this._buffer.append(super.getText());
                            continue;
                        }
                        continue;
                    }
                    case 5: {
                        continue;
                    }
                    default: {}
                }
            }
        }
        
        @Override
        public String getText() {
            assert this._hasBufferedText;
            return this._buffer.toString();
        }
        
        @Override
        public int getTextLength() {
            assert this._hasBufferedText;
            return this._buffer.length();
        }
        
        @Override
        public int getTextStart() {
            assert this._hasBufferedText;
            return 0;
        }
        
        @Override
        public char[] getTextCharacters() {
            assert this._hasBufferedText;
            return this._buffer.toString().toCharArray();
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) {
            assert this._hasBufferedText;
            this._buffer.getChars(sourceStart, sourceStart + length, target, targetStart);
            return length;
        }
        
        @Override
        public boolean isWhiteSpace() {
            assert this._hasBufferedText;
            return XmlWhitespace.isAllSpace(this._buffer);
        }
        
        @Override
        public boolean hasText() {
            return this._hasBufferedText || super.hasText();
        }
        
        @Override
        public int getEventType() {
            if (this._hasBufferedText) {
                return this._textEventType;
            }
            return super.getEventType();
        }
    }
    
    private static class ElementEventImpl implements ValidatorListener.Event
    {
        private static final int BUF_LENGTH = 1024;
        private char[] _buf;
        private int _length;
        private boolean _supportForGetTextCharacters;
        private XMLStreamReader _xmlStream;
        
        private ElementEventImpl() {
            this._buf = new char[1024];
            this._supportForGetTextCharacters = true;
        }
        
        private void setXMLStreamReader(final XMLStreamReader xsr) {
            this._xmlStream = xsr;
        }
        
        @Override
        public XmlCursor getLocationAsCursor() {
            return null;
        }
        
        @Override
        public Location getLocation() {
            return this._xmlStream.getLocation();
        }
        
        @Override
        public String getXsiType() {
            return this._xmlStream.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
        }
        
        @Override
        public String getXsiNil() {
            return this._xmlStream.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
        }
        
        @Override
        public String getXsiLoc() {
            return this._xmlStream.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        }
        
        @Override
        public String getXsiNoLoc() {
            return this._xmlStream.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
        }
        
        @Override
        public QName getName() {
            if (this._xmlStream.hasName()) {
                return new QName(this._xmlStream.getNamespaceURI(), this._xmlStream.getLocalName());
            }
            return null;
        }
        
        @Override
        public String getText() {
            this._length = 0;
            this.addTextToBuffer();
            return new String(this._buf, 0, this._length);
        }
        
        @Override
        public String getText(final int wsr) {
            return XmlWhitespace.collapse(this._xmlStream.getText(), wsr);
        }
        
        @Override
        public boolean textIsWhitespace() {
            return this._xmlStream.isWhiteSpace();
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix) {
            return this._xmlStream.getNamespaceURI(prefix);
        }
        
        private void addTextToBuffer() {
            final int textLength = this._xmlStream.getTextLength();
            this.ensureBufferLength(textLength);
            if (this._supportForGetTextCharacters) {
                try {
                    this._length = this._xmlStream.getTextCharacters(0, this._buf, this._length, textLength);
                }
                catch (final Exception e) {
                    this._supportForGetTextCharacters = false;
                }
            }
            if (!this._supportForGetTextCharacters) {
                System.arraycopy(this._xmlStream.getTextCharacters(), this._xmlStream.getTextStart(), this._buf, this._length, textLength);
                this._length += textLength;
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
    }
    
    private static final class AttributeEventImpl implements ValidatorListener.Event
    {
        private int _attIndex;
        private XMLStreamReader _xmlStream;
        
        private void setXMLStreamReader(final XMLStreamReader xsr) {
            this._xmlStream = xsr;
        }
        
        @Override
        public XmlCursor getLocationAsCursor() {
            return null;
        }
        
        @Override
        public Location getLocation() {
            return this._xmlStream.getLocation();
        }
        
        @Override
        public String getXsiType() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getXsiNil() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getXsiLoc() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getXsiNoLoc() {
            throw new IllegalStateException();
        }
        
        @Override
        public QName getName() {
            assert this._xmlStream.isStartElement() : "Not on Start Element.";
            final String uri = this._xmlStream.getAttributeNamespace(this._attIndex);
            final QName qn = new QName((uri == null) ? "" : uri, this._xmlStream.getAttributeLocalName(this._attIndex));
            return qn;
        }
        
        @Override
        public String getText() {
            assert this._xmlStream.isStartElement() : "Not on Start Element.";
            return this._xmlStream.getAttributeValue(this._attIndex);
        }
        
        @Override
        public String getText(final int wsr) {
            assert this._xmlStream.isStartElement() : "Not on Start Element.";
            return XmlWhitespace.collapse(this._xmlStream.getAttributeValue(this._attIndex), wsr);
        }
        
        @Override
        public boolean textIsWhitespace() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix) {
            assert this._xmlStream.isStartElement() : "Not on Start Element.";
            return this._xmlStream.getNamespaceURI(prefix);
        }
        
        private void setAttributeIndex(final int attIndex) {
            this._attIndex = attIndex;
        }
    }
    
    private static final class SimpleEventImpl implements ValidatorListener.Event
    {
        private String _text;
        private QName _qname;
        private XMLStreamReader _xmlStream;
        
        private void setXMLStreamReader(final XMLStreamReader xsr) {
            this._xmlStream = xsr;
        }
        
        @Override
        public XmlCursor getLocationAsCursor() {
            return null;
        }
        
        @Override
        public Location getLocation() {
            return this._xmlStream.getLocation();
        }
        
        @Override
        public String getXsiType() {
            return null;
        }
        
        @Override
        public String getXsiNil() {
            return null;
        }
        
        @Override
        public String getXsiLoc() {
            return null;
        }
        
        @Override
        public String getXsiNoLoc() {
            return null;
        }
        
        @Override
        public QName getName() {
            return this._qname;
        }
        
        @Override
        public String getText() {
            return this._text;
        }
        
        @Override
        public String getText(final int wsr) {
            return XmlWhitespace.collapse(this._text, wsr);
        }
        
        @Override
        public boolean textIsWhitespace() {
            return false;
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix) {
            return this._xmlStream.getNamespaceURI(prefix);
        }
    }
}
