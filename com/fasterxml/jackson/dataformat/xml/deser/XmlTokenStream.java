package com.fasterxml.jackson.dataformat.xml.deser;

import org.codehaus.stax2.XMLStreamLocation2;
import com.fasterxml.jackson.core.JsonLocation;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.XMLStreamReader2;

public class XmlTokenStream
{
    public static final int XML_START_ELEMENT = 1;
    public static final int XML_END_ELEMENT = 2;
    public static final int XML_ATTRIBUTE_NAME = 3;
    public static final int XML_ATTRIBUTE_VALUE = 4;
    public static final int XML_TEXT = 5;
    public static final int XML_END = 6;
    private static final int REPLAY_START_DUP = 1;
    private static final int REPLAY_END = 2;
    private static final int REPLAY_START_DELAYED = 3;
    private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    protected final XMLStreamReader2 _xmlReader;
    protected final Object _sourceReference;
    protected int _formatFeatures;
    protected int _currentState;
    protected int _attributeCount;
    protected boolean _xsiNilFound;
    protected boolean _mixedText;
    protected int _nextAttributeIndex;
    protected String _localName;
    protected String _namespaceURI;
    protected String _textValue;
    protected int _repeatElement;
    protected ElementWrapper _currentWrapper;
    protected String _nextLocalName;
    protected String _nextNamespaceURI;
    
    public XmlTokenStream(final XMLStreamReader xmlReader, final Object sourceRef, final int formatFeatures) {
        this._sourceReference = sourceRef;
        if (xmlReader.getEventType() != 1) {
            throw new IllegalArgumentException("Invalid XMLStreamReader passed: should be pointing to START_ELEMENT (1), instead got " + xmlReader.getEventType());
        }
        this._xmlReader = Stax2ReaderAdapter.wrapIfNecessary(xmlReader);
        this._localName = this._xmlReader.getLocalName();
        this._namespaceURI = this._xmlReader.getNamespaceURI();
        this._formatFeatures = formatFeatures;
        this._checkXsiAttributes();
        this._currentState = 1;
    }
    
    public XMLStreamReader2 getXmlReader() {
        return this._xmlReader;
    }
    
    protected void setFormatFeatures(final int f) {
        this._formatFeatures = f;
    }
    
    public int next() throws XMLStreamException {
        if (this._repeatElement != 0) {
            return this._currentState = this._handleRepeatElement();
        }
        return this._next();
    }
    
    public void skipEndElement() throws IOException, XMLStreamException {
        final int type = this.next();
        if (type != 2) {
            throw new IOException("Expected END_ELEMENT, got event of type " + type);
        }
    }
    
    public int getCurrentToken() {
        return this._currentState;
    }
    
    public String getText() {
        return this._textValue;
    }
    
    public String getLocalName() {
        return this._localName;
    }
    
    public String getNamespaceURI() {
        return this._namespaceURI;
    }
    
    public boolean hasXsiNil() {
        return this._xsiNilFound;
    }
    
    public void closeCompletely() throws XMLStreamException {
        this._xmlReader.closeCompletely();
    }
    
    public void close() throws XMLStreamException {
        this._xmlReader.close();
    }
    
    public JsonLocation getCurrentLocation() {
        return this._extractLocation(this._xmlReader.getLocationInfo().getCurrentLocation());
    }
    
    public JsonLocation getTokenLocation() {
        return this._extractLocation(this._xmlReader.getLocationInfo().getStartLocation());
    }
    
    protected void repeatStartElement() {
        if (this._currentState == 1) {
            if (this._currentWrapper == null) {
                this._currentWrapper = ElementWrapper.matchingWrapper(null, this._localName, this._namespaceURI);
            }
            else {
                this._currentWrapper = ElementWrapper.matchingWrapper(this._currentWrapper.getParent(), this._localName, this._namespaceURI);
            }
            this._repeatElement = 1;
            return;
        }
        if (this._currentState == 2) {
            return;
        }
        throw new IllegalStateException("Current state not XML_START_ELEMENT (1) but " + this._currentState);
    }
    
    protected void skipAttributes() {
        if (this._currentState == 3) {
            this._attributeCount = 0;
            this._currentState = 1;
        }
        else if (this._currentState != 1) {
            if (this._currentState != 5) {
                throw new IllegalStateException("Current state not XML_START_ELEMENT or XML_ATTRIBUTE_NAME (1) but " + this._currentState);
            }
        }
    }
    
    protected String convertToString() throws XMLStreamException {
        if (this._currentState != 3 || this._nextAttributeIndex != 0) {
            return null;
        }
        String text = this._collectUntilTag();
        if (this._xmlReader.getEventType() == 2) {
            if (text == null) {
                text = "";
            }
            if (this._currentWrapper != null) {
                this._currentWrapper = this._currentWrapper.getParent();
            }
            this._localName = this._xmlReader.getLocalName();
            this._namespaceURI = this._xmlReader.getNamespaceURI();
            this._attributeCount = 0;
            this._currentState = 5;
            return this._textValue = text;
        }
        return null;
    }
    
    private final int _next() throws XMLStreamException {
        switch (this._currentState) {
            case 4: {
                ++this._nextAttributeIndex;
            }
            case 1: {
                if (this._xsiNilFound) {
                    this._xsiNilFound = false;
                    switch (this._skipUntilTag()) {
                        case 2: {
                            return this._handleEndElement();
                        }
                        case 8: {
                            throw new IllegalStateException("Unexpected end-of-input after null token");
                        }
                        default: {
                            throw new IllegalStateException("Unexpected START_ELEMENT after null token");
                        }
                    }
                }
                else {
                    if (this._nextAttributeIndex < this._attributeCount) {
                        this._localName = this._xmlReader.getAttributeLocalName(this._nextAttributeIndex);
                        this._namespaceURI = this._xmlReader.getAttributeNamespace(this._nextAttributeIndex);
                        this._textValue = this._xmlReader.getAttributeValue(this._nextAttributeIndex);
                        return this._currentState = 3;
                    }
                    final String text = this._collectUntilTag();
                    final boolean startElementNext = this._xmlReader.getEventType() == 1;
                    if (startElementNext) {
                        if (text == null || this._allWs(text)) {
                            this._mixedText = false;
                            return this._initStartElement();
                        }
                        this._mixedText = true;
                        this._textValue = text;
                        return this._currentState = 5;
                    }
                    else {
                        if (text != null) {
                            this._mixedText = false;
                            this._textValue = text;
                            return this._currentState = 5;
                        }
                        this._mixedText = false;
                        return this._handleEndElement();
                    }
                }
                break;
            }
            case 3: {
                return this._currentState = 4;
            }
            case 5: {
                if (this._mixedText) {
                    this._mixedText = false;
                    return this._initStartElement();
                }
                return this._handleEndElement();
            }
            case 6: {
                return 6;
            }
            default: {
                switch (this._skipUntilTag()) {
                    case 8: {
                        return this._currentState = 6;
                    }
                    case 2: {
                        return this._handleEndElement();
                    }
                    default: {
                        return this._initStartElement();
                    }
                }
                break;
            }
        }
    }
    
    private final String _collectUntilTag() throws XMLStreamException {
        if (this._xmlReader.isEmptyElement()) {
            this._xmlReader.next();
            if (FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL.enabledIn(this._formatFeatures)) {
                return null;
            }
            return "";
        }
        else {
            CharSequence chars = null;
            while (true) {
                switch (this._xmlReader.next()) {
                    case 1: {
                        return (chars == null) ? "" : chars.toString();
                    }
                    case 2:
                    case 8: {
                        return (chars == null) ? "" : chars.toString();
                    }
                    case 4:
                    case 12: {
                        final String str = this._getText(this._xmlReader);
                        if (chars == null) {
                            chars = str;
                        }
                        else {
                            if (chars instanceof String) {
                                chars = new StringBuilder(chars);
                            }
                            ((StringBuilder)chars).append(str);
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
    
    private final int _skipUntilTag() throws XMLStreamException {
        while (this._xmlReader.hasNext()) {
            final int type;
            switch (type = this._xmlReader.next()) {
                case 1:
                case 2:
                case 8: {
                    return type;
                }
                default: {
                    continue;
                }
            }
        }
        throw new IllegalStateException("Expected to find a tag, instead reached end of input");
    }
    
    private final String _getText(final XMLStreamReader2 r) throws XMLStreamException {
        try {
            return r.getText();
        }
        catch (final RuntimeException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof XMLStreamException) {
                throw (XMLStreamException)cause;
            }
            throw e;
        }
    }
    
    private final int _initStartElement() throws XMLStreamException {
        final String ns = this._xmlReader.getNamespaceURI();
        final String localName = this._xmlReader.getLocalName();
        this._checkXsiAttributes();
        if (this._currentWrapper != null) {
            if (!this._currentWrapper.matchesWrapper(localName, ns)) {
                this._localName = this._currentWrapper.getWrapperLocalName();
                this._namespaceURI = this._currentWrapper.getWrapperNamespace();
                this._currentWrapper = this._currentWrapper.getParent();
                this._nextLocalName = localName;
                this._nextNamespaceURI = ns;
                this._repeatElement = 3;
                return this._currentState = 2;
            }
            this._currentWrapper = this._currentWrapper.intermediateWrapper();
        }
        this._localName = localName;
        this._namespaceURI = ns;
        return this._currentState = 1;
    }
    
    private final void _checkXsiAttributes() {
        final int count = this._xmlReader.getAttributeCount();
        this._attributeCount = count;
        if (count >= 1 && "nil".equals(this._xmlReader.getAttributeLocalName(0)) && "http://www.w3.org/2001/XMLSchema-instance".equals(this._xmlReader.getAttributeNamespace(0))) {
            this._nextAttributeIndex = 1;
            this._xsiNilFound = "true".equals(this._xmlReader.getAttributeValue(0));
            return;
        }
        this._nextAttributeIndex = 0;
        this._xsiNilFound = false;
    }
    
    protected int _handleRepeatElement() throws XMLStreamException {
        final int type = this._repeatElement;
        this._repeatElement = 0;
        if (type == 1) {
            this._currentWrapper = this._currentWrapper.intermediateWrapper();
            return 1;
        }
        if (type == 2) {
            this._localName = this._xmlReader.getLocalName();
            this._namespaceURI = this._xmlReader.getNamespaceURI();
            if (this._currentWrapper != null) {
                this._currentWrapper = this._currentWrapper.getParent();
            }
            return 2;
        }
        if (type == 3) {
            if (this._currentWrapper != null) {
                this._currentWrapper = this._currentWrapper.intermediateWrapper();
            }
            this._localName = this._nextLocalName;
            this._namespaceURI = this._nextNamespaceURI;
            this._nextLocalName = null;
            this._nextNamespaceURI = null;
            return 1;
        }
        throw new IllegalStateException("Unrecognized type to repeat: " + type);
    }
    
    private final int _handleEndElement() {
        if (this._currentWrapper != null) {
            final ElementWrapper w = this._currentWrapper;
            if (w.isMatching()) {
                this._repeatElement = 2;
                this._localName = w.getWrapperLocalName();
                this._namespaceURI = w.getWrapperNamespace();
                this._currentWrapper = this._currentWrapper.getParent();
            }
            else {
                this._currentWrapper = this._currentWrapper.getParent();
            }
        }
        return this._currentState = 2;
    }
    
    private JsonLocation _extractLocation(final XMLStreamLocation2 location) {
        if (location == null) {
            return new JsonLocation(this._sourceReference, -1L, -1, -1);
        }
        return new JsonLocation(this._sourceReference, (long)location.getCharacterOffset(), location.getLineNumber(), location.getColumnNumber());
    }
    
    protected boolean _allWs(final String str) {
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
    
    @Override
    public String toString() {
        return String.format("(Token stream: state=%s attr=%s nextAttr=%s name=%s text=%s repeat?=%s wrapper=[%s] repeatElement=%s nextName=%s)", this._currentState, this._attributeCount, this._nextAttributeIndex, this._localName, this._textValue, this._repeatElement, this._currentWrapper, this._repeatElement, this._nextLocalName);
    }
}
