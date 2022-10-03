package com.sun.org.apache.xerces.internal.impl;

import java.util.Iterator;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.events.NotationDeclarationImpl;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import java.util.Enumeration;
import com.sun.xml.internal.stream.events.EntityDeclarationImpl;
import java.util.ArrayList;
import java.util.List;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.Entity;
import javax.xml.stream.Location;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import javax.xml.namespace.QName;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.NoSuchElementException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.BufferedInputStream;
import javax.xml.stream.XMLStreamException;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.StaxErrorReporter;
import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderImpl implements XMLStreamReader
{
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String READER_IN_DEFINED_STATE = "http://java.sun.com/xml/stream/properties/reader-in-defined-state";
    private SymbolTable fSymbolTable;
    protected XMLDocumentScannerImpl fScanner;
    protected NamespaceContextWrapper fNamespaceContextWrapper;
    protected XMLEntityManager fEntityManager;
    protected StaxErrorReporter fErrorReporter;
    protected XMLEntityScanner fEntityScanner;
    protected XMLInputSource fInputSource;
    protected PropertyManager fPropertyManager;
    private int fEventType;
    static final boolean DEBUG = false;
    private boolean fReuse;
    private boolean fReaderInDefinedState;
    private boolean fBindNamespaces;
    private String fDTDDecl;
    private String versionStr;
    
    public XMLStreamReaderImpl(final InputStream inputStream, final PropertyManager props) throws XMLStreamException {
        this.fSymbolTable = new SymbolTable();
        this.fScanner = new XMLNSDocumentScannerImpl();
        this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
        this.fEntityManager = new XMLEntityManager();
        this.fErrorReporter = new StaxErrorReporter();
        this.fEntityScanner = null;
        this.fInputSource = null;
        this.fPropertyManager = null;
        this.fReuse = true;
        this.fReaderInDefinedState = true;
        this.fBindNamespaces = true;
        this.fDTDDecl = null;
        this.versionStr = null;
        this.init(props);
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, inputStream, null);
        this.setInputSource(inputSource);
    }
    
    public XMLDocumentScannerImpl getScanner() {
        System.out.println("returning scanner");
        return this.fScanner;
    }
    
    public XMLStreamReaderImpl(final String systemid, final PropertyManager props) throws XMLStreamException {
        this.fSymbolTable = new SymbolTable();
        this.fScanner = new XMLNSDocumentScannerImpl();
        this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
        this.fEntityManager = new XMLEntityManager();
        this.fErrorReporter = new StaxErrorReporter();
        this.fEntityScanner = null;
        this.fInputSource = null;
        this.fPropertyManager = null;
        this.fReuse = true;
        this.fReaderInDefinedState = true;
        this.fBindNamespaces = true;
        this.fDTDDecl = null;
        this.versionStr = null;
        this.init(props);
        final XMLInputSource inputSource = new XMLInputSource(null, systemid, null);
        this.setInputSource(inputSource);
    }
    
    public XMLStreamReaderImpl(final InputStream inputStream, final String encoding, final PropertyManager props) throws XMLStreamException {
        this.fSymbolTable = new SymbolTable();
        this.fScanner = new XMLNSDocumentScannerImpl();
        this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
        this.fEntityManager = new XMLEntityManager();
        this.fErrorReporter = new StaxErrorReporter();
        this.fEntityScanner = null;
        this.fInputSource = null;
        this.fPropertyManager = null;
        this.fReuse = true;
        this.fReaderInDefinedState = true;
        this.fBindNamespaces = true;
        this.fDTDDecl = null;
        this.versionStr = null;
        this.init(props);
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, new BufferedInputStream(inputStream), encoding);
        this.setInputSource(inputSource);
    }
    
    public XMLStreamReaderImpl(final Reader reader, final PropertyManager props) throws XMLStreamException {
        this.fSymbolTable = new SymbolTable();
        this.fScanner = new XMLNSDocumentScannerImpl();
        this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
        this.fEntityManager = new XMLEntityManager();
        this.fErrorReporter = new StaxErrorReporter();
        this.fEntityScanner = null;
        this.fInputSource = null;
        this.fPropertyManager = null;
        this.fReuse = true;
        this.fReaderInDefinedState = true;
        this.fBindNamespaces = true;
        this.fDTDDecl = null;
        this.versionStr = null;
        this.init(props);
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, new BufferedReader(reader), null);
        this.setInputSource(inputSource);
    }
    
    public XMLStreamReaderImpl(final XMLInputSource inputSource, final PropertyManager props) throws XMLStreamException {
        this.fSymbolTable = new SymbolTable();
        this.fScanner = new XMLNSDocumentScannerImpl();
        this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
        this.fEntityManager = new XMLEntityManager();
        this.fErrorReporter = new StaxErrorReporter();
        this.fEntityScanner = null;
        this.fInputSource = null;
        this.fPropertyManager = null;
        this.fReuse = true;
        this.fReaderInDefinedState = true;
        this.fBindNamespaces = true;
        this.fDTDDecl = null;
        this.versionStr = null;
        this.init(props);
        this.setInputSource(inputSource);
    }
    
    public void setInputSource(final XMLInputSource inputSource) throws XMLStreamException {
        this.fReuse = false;
        try {
            this.fScanner.setInputSource(inputSource);
            if (this.fReaderInDefinedState) {
                this.fEventType = this.fScanner.next();
                if (this.versionStr == null) {
                    this.versionStr = this.getVersion();
                }
                if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1")) {
                    this.switchToXML11Scanner();
                }
            }
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
        catch (final XNIException ex2) {
            throw new XMLStreamException(ex2.getMessage(), this.getLocation(), ex2.getException());
        }
    }
    
    void init(final PropertyManager propertyManager) throws XMLStreamException {
        (this.fPropertyManager = propertyManager).setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
        propertyManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        propertyManager.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        this.reset();
    }
    
    public boolean canReuse() {
        return this.fReuse;
    }
    
    public void reset() {
        this.fReuse = true;
        this.fEventType = 0;
        this.fEntityManager.reset(this.fPropertyManager);
        this.fScanner.reset(this.fPropertyManager);
        this.fDTDDecl = null;
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fReaderInDefinedState = (boolean)this.fPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/reader-in-defined-state");
        this.fBindNamespaces = (boolean)this.fPropertyManager.getProperty("javax.xml.stream.isNamespaceAware");
        this.versionStr = null;
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.fReuse = true;
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this.fScanner.getCharacterEncodingScheme();
    }
    
    public int getColumnNumber() {
        return this.fEntityScanner.getColumnNumber();
    }
    
    @Override
    public String getEncoding() {
        return this.fEntityScanner.getEncoding();
    }
    
    @Override
    public int getEventType() {
        return this.fEventType;
    }
    
    public int getLineNumber() {
        return this.fEntityScanner.getLineNumber();
    }
    
    @Override
    public String getLocalName() {
        if (this.fEventType == 1 || this.fEventType == 2) {
            return this.fScanner.getElementQName().localpart;
        }
        if (this.fEventType == 9) {
            return this.fScanner.getEntityName();
        }
        throw new IllegalStateException("Method getLocalName() cannot be called for " + getEventTypeString(this.fEventType) + " event.");
    }
    
    @Override
    public String getNamespaceURI() {
        if (this.fEventType == 1 || this.fEventType == 2) {
            return this.fScanner.getElementQName().uri;
        }
        return null;
    }
    
    @Override
    public String getPIData() {
        if (this.fEventType == 3) {
            return this.fScanner.getPIData().toString();
        }
        throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + 3);
    }
    
    @Override
    public String getPITarget() {
        if (this.fEventType == 3) {
            return this.fScanner.getPITarget();
        }
        throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + 3);
    }
    
    @Override
    public String getPrefix() {
        if (this.fEventType == 1 || this.fEventType == 2) {
            final String prefix = this.fScanner.getElementQName().prefix;
            return (prefix == null) ? "" : prefix;
        }
        return null;
    }
    
    @Override
    public char[] getTextCharacters() {
        if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6) {
            return this.fScanner.getCharacterData().ch;
        }
        throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextCharacters() ");
    }
    
    @Override
    public int getTextLength() {
        if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6) {
            return this.fScanner.getCharacterData().length;
        }
        throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextLength() ");
    }
    
    @Override
    public int getTextStart() {
        if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6) {
            return this.fScanner.getCharacterData().offset;
        }
        throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextStart() ");
    }
    
    public String getValue() {
        if (this.fEventType == 3) {
            return this.fScanner.getPIData().toString();
        }
        if (this.fEventType == 5) {
            return this.fScanner.getComment();
        }
        if (this.fEventType == 1 || this.fEventType == 2) {
            return this.fScanner.getElementQName().localpart;
        }
        if (this.fEventType == 4) {
            return this.fScanner.getCharacterData().toString();
        }
        return null;
    }
    
    @Override
    public String getVersion() {
        final String version = this.fEntityScanner.getXMLVersion();
        return ("1.0".equals(version) && !this.fEntityScanner.xmlVersionSetExplicitly) ? null : version;
    }
    
    public boolean hasAttributes() {
        return this.fScanner.getAttributeIterator().getLength() > 0;
    }
    
    @Override
    public boolean hasName() {
        return this.fEventType == 1 || this.fEventType == 2;
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this.fEventType != -1 && this.fEventType != 8;
    }
    
    public boolean hasValue() {
        return this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 4;
    }
    
    @Override
    public boolean isEndElement() {
        return this.fEventType == 2;
    }
    
    @Override
    public boolean isStandalone() {
        return this.fScanner.isStandAlone();
    }
    
    @Override
    public boolean isStartElement() {
        return this.fEventType == 1;
    }
    
    @Override
    public boolean isWhiteSpace() {
        if (this.isCharacters() || this.fEventType == 12) {
            final char[] ch = this.getTextCharacters();
            final int start = this.getTextStart();
            for (int end = start + this.getTextLength(), i = start; i < end; ++i) {
                if (!XMLChar.isSpace(ch[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (!this.hasNext()) {
            if (this.fEventType != -1) {
                throw new NoSuchElementException("END_DOCUMENT reached: no more elements on the stream.");
            }
            throw new XMLStreamException("Error processing input source. The input stream is not complete.");
        }
        else {
            try {
                this.fEventType = this.fScanner.next();
                if (this.versionStr == null) {
                    this.versionStr = this.getVersion();
                }
                if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1")) {
                    this.switchToXML11Scanner();
                }
                if (this.fEventType == 4 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 12) {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                }
                return this.fEventType;
            }
            catch (final IOException ex) {
                final int fScannerState = this.fScanner.fScannerState;
                final XMLDocumentScannerImpl fScanner = this.fScanner;
                if (fScannerState == 46) {
                    final Boolean isValidating = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isValidating");
                    if (isValidating != null && !isValidating) {
                        this.fEventType = 11;
                        final XMLDocumentScannerImpl fScanner2 = this.fScanner;
                        final XMLDocumentScannerImpl fScanner3 = this.fScanner;
                        fScanner2.setScannerState(43);
                        this.fScanner.setDriver(this.fScanner.fPrologDriver);
                        if (this.fDTDDecl == null || this.fDTDDecl.length() == 0) {
                            this.fDTDDecl = "<!-- Exception scanning External DTD Subset.  True contents of DTD cannot be determined.  Processing will continue as XMLInputFactory.IS_VALIDATING == false. -->";
                        }
                        return 11;
                    }
                }
                throw new XMLStreamException(ex.getMessage(), this.getLocation(), ex);
            }
            catch (final XNIException ex2) {
                throw new XMLStreamException(ex2.getMessage(), this.getLocation(), ex2.getException());
            }
        }
    }
    
    private void switchToXML11Scanner() throws IOException {
        final int oldEntityDepth = this.fScanner.fEntityDepth;
        final NamespaceContext oldNamespaceContext = this.fScanner.fNamespaceContext;
        (this.fScanner = new XML11NSDocumentScannerImpl()).reset(this.fPropertyManager);
        this.fScanner.setPropertyManager(this.fPropertyManager);
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fEntityManager.fCurrentEntity.mayReadChunks = true;
        this.fScanner.setScannerState(7);
        this.fScanner.fEntityDepth = oldEntityDepth;
        this.fScanner.fNamespaceContext = oldNamespaceContext;
        this.fEventType = this.fScanner.next();
    }
    
    static final String getEventTypeString(final int eventType) {
        switch (eventType) {
            case 1: {
                return "START_ELEMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 5: {
                return "COMMENT";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            case 10: {
                return "ATTRIBUTE";
            }
            case 11: {
                return "DTD";
            }
            case 12: {
                return "CDATA";
            }
            case 6: {
                return "SPACE";
            }
            default: {
                return "UNKNOWN_EVENT_TYPE, " + String.valueOf(eventType);
            }
        }
    }
    
    @Override
    public int getAttributeCount() {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getLength();
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeCount()");
    }
    
    @Override
    public QName getAttributeName(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.convertXNIQNametoJavaxQName(this.fScanner.getAttributeIterator().getQualifiedName(index));
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeName()");
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getLocalName(index);
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getURI(index);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeNamespace()");
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getPrefix(index);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributePrefix()");
    }
    
    public QName getAttributeQName(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            final String localName = this.fScanner.getAttributeIterator().getLocalName(index);
            final String uri = this.fScanner.getAttributeIterator().getURI(index);
            return new QName(uri, localName);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeQName()");
    }
    
    @Override
    public String getAttributeType(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getType(index);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeType()");
    }
    
    @Override
    public String getAttributeValue(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().getValue(index);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        if (this.fEventType != 1 && this.fEventType != 10) {
            throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
        }
        final XMLAttributesImpl attributes = this.fScanner.getAttributeIterator();
        if (namespaceURI == null) {
            return attributes.getValue(attributes.getIndexByLocalName(localName));
        }
        return this.fScanner.getAttributeIterator().getValue((namespaceURI.length() == 0) ? null : namespaceURI, localName);
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.getLocation());
        }
        int eventType = this.next();
        final StringBuffer content = new StringBuffer();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            }
            else if (eventType != 3) {
                if (eventType != 5) {
                    if (eventType == 8) {
                        throw new XMLStreamException("unexpected end of document when reading element text content");
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", this.getLocation());
                    }
                    throw new XMLStreamException("Unexpected event type " + eventType, this.getLocation());
                }
            }
            eventType = this.next();
        }
        return content.toString();
    }
    
    @Override
    public Location getLocation() {
        return new Location() {
            String _systemId = XMLStreamReaderImpl.this.fEntityScanner.getExpandedSystemId();
            String _publicId = XMLStreamReaderImpl.this.fEntityScanner.getPublicId();
            int _offset = XMLStreamReaderImpl.this.fEntityScanner.getCharacterOffset();
            int _columnNumber = XMLStreamReaderImpl.this.fEntityScanner.getColumnNumber();
            int _lineNumber = XMLStreamReaderImpl.this.fEntityScanner.getLineNumber();
            
            public String getLocationURI() {
                return this._systemId;
            }
            
            @Override
            public int getCharacterOffset() {
                return this._offset;
            }
            
            @Override
            public int getColumnNumber() {
                return this._columnNumber;
            }
            
            @Override
            public int getLineNumber() {
                return this._lineNumber;
            }
            
            @Override
            public String getPublicId() {
                return this._publicId;
            }
            
            @Override
            public String getSystemId() {
                return this._systemId;
            }
            
            @Override
            public String toString() {
                final StringBuffer sbuffer = new StringBuffer();
                sbuffer.append("Line number = " + this.getLineNumber());
                sbuffer.append("\n");
                sbuffer.append("Column number = " + this.getColumnNumber());
                sbuffer.append("\n");
                sbuffer.append("System Id = " + this.getSystemId());
                sbuffer.append("\n");
                sbuffer.append("Public Id = " + this.getPublicId());
                sbuffer.append("\n");
                sbuffer.append("Location Uri= " + this.getLocationURI());
                sbuffer.append("\n");
                sbuffer.append("CharacterOffset = " + this.getCharacterOffset());
                sbuffer.append("\n");
                return sbuffer.toString();
            }
        };
    }
    
    @Override
    public QName getName() {
        if (this.fEventType == 1 || this.fEventType == 2) {
            return this.convertXNIQNametoJavaxQName(this.fScanner.getElementQName());
        }
        throw new IllegalStateException("Illegal to call getName() when event type is " + getEventTypeString(this.fEventType) + ". Valid states are " + getEventTypeString(1) + ", " + getEventTypeString(2));
    }
    
    @Override
    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return this.fNamespaceContextWrapper;
    }
    
    @Override
    public int getNamespaceCount() {
        if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13) {
            return this.fScanner.getNamespaceContext().getDeclaredPrefixCount();
        }
        throw new IllegalStateException("Current event state is " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceCount().");
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13) {
            final String prefix = this.fScanner.getNamespaceContext().getDeclaredPrefixAt(index);
            return prefix.equals("") ? null : prefix;
        }
        throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespacePrefix().");
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13) {
            return this.fScanner.getNamespaceContext().getURI(this.fScanner.getNamespaceContext().getDeclaredPrefixAt(index));
        }
        throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceURI().");
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (this.fPropertyManager == null) {
            return null;
        }
        final PropertyManager fPropertyManager = this.fPropertyManager;
        if (name.equals("javax.xml.stream.notations")) {
            return this.getNotationDecls();
        }
        final PropertyManager fPropertyManager2 = this.fPropertyManager;
        if (name.equals("javax.xml.stream.entities")) {
            return this.getEntityDecls();
        }
        return this.fPropertyManager.getProperty(name);
    }
    
    @Override
    public String getText() {
        if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6) {
            return this.fScanner.getCharacterData().toString();
        }
        if (this.fEventType == 9) {
            final String name = this.fScanner.getEntityName();
            if (name == null) {
                return null;
            }
            if (this.fScanner.foundBuiltInRefs) {
                return this.fScanner.getCharacterData().toString();
            }
            final XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
            final Entity en = entityStore.getEntity(name);
            if (en == null) {
                return null;
            }
            if (en.isExternal()) {
                return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId();
            }
            return ((Entity.InternalEntity)en).text;
        }
        else {
            if (this.fEventType != 11) {
                throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states" + getEventTypeString(4) + ", " + getEventTypeString(5) + ", " + getEventTypeString(12) + ", " + getEventTypeString(6) + ", " + getEventTypeString(9) + ", " + getEventTypeString(11) + " valid for getText() ");
            }
            if (this.fDTDDecl != null) {
                return this.fDTDDecl;
            }
            final XMLStringBuffer tmpBuffer = this.fScanner.getDTDDecl();
            return this.fDTDDecl = tmpBuffer.toString();
        }
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (type != this.fEventType) {
            throw new XMLStreamException("Event type " + getEventTypeString(type) + " specified did not match with current parser event " + getEventTypeString(this.fEventType));
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException("Namespace URI " + namespaceURI + " specified did not match with current namespace URI");
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException("LocalName " + localName + " specified did not match with current local name");
        }
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (target == null) {
            throw new NullPointerException("target char array can't be null");
        }
        if (targetStart < 0 || length < 0 || sourceStart < 0 || targetStart >= target.length || targetStart + length > target.length) {
            throw new IndexOutOfBoundsException();
        }
        int copiedLength = 0;
        final int available = this.getTextLength() - sourceStart;
        if (available < 0) {
            throw new IndexOutOfBoundsException("sourceStart is greater thannumber of characters associated with this event");
        }
        if (available < length) {
            copiedLength = available;
        }
        else {
            copiedLength = length;
        }
        System.arraycopy(this.getTextCharacters(), this.getTextStart() + sourceStart, target, targetStart, copiedLength);
        return copiedLength;
    }
    
    @Override
    public boolean hasText() {
        if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12) {
            return this.fScanner.getCharacterData().length > 0;
        }
        if (this.fEventType != 9) {
            return this.fEventType == 11 && this.fScanner.fSeenDoctypeDecl;
        }
        final String name = this.fScanner.getEntityName();
        if (name == null) {
            return false;
        }
        if (this.fScanner.foundBuiltInRefs) {
            return true;
        }
        final XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
        final Entity en = entityStore.getEntity(name);
        if (en == null) {
            return false;
        }
        if (en.isExternal()) {
            return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId() != null;
        }
        return ((Entity.InternalEntity)en).text != null;
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        if (this.fEventType == 1 || this.fEventType == 10) {
            return this.fScanner.getAttributeIterator().isSpecified(index);
        }
        throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for isAttributeSpecified()");
    }
    
    @Override
    public boolean isCharacters() {
        return this.fEventType == 4;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int eventType;
        for (eventType = this.next(); (eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {}
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("found: " + getEventTypeString(eventType) + ", expected " + getEventTypeString(1) + " or " + getEventTypeString(2), this.getLocation());
        }
        return eventType;
    }
    
    @Override
    public boolean standaloneSet() {
        return this.fScanner.standaloneSet();
    }
    
    public QName convertXNIQNametoJavaxQName(final com.sun.org.apache.xerces.internal.xni.QName qname) {
        if (qname == null) {
            return null;
        }
        if (qname.prefix == null) {
            return new QName(qname.uri, qname.localpart);
        }
        return new QName(qname.uri, qname.localpart, qname.prefix);
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null.");
        }
        return this.fScanner.getNamespaceContext().getURI(this.fSymbolTable.addSymbol(prefix));
    }
    
    protected void setPropertyManager(final PropertyManager propertyManager) {
        this.fPropertyManager = propertyManager;
        this.fScanner.setProperty("stax-properties", propertyManager);
        this.fScanner.setPropertyManager(propertyManager);
    }
    
    protected PropertyManager getPropertyManager() {
        return this.fPropertyManager;
    }
    
    static void pr(final String str) {
        System.out.println(str);
    }
    
    protected List getEntityDecls() {
        if (this.fEventType == 11) {
            final XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
            ArrayList list = null;
            if (entityStore.hasEntities()) {
                EntityDeclarationImpl decl = null;
                list = new ArrayList(entityStore.getEntitySize());
                final Enumeration enu = entityStore.getEntityKeys();
                while (enu.hasMoreElements()) {
                    final String key = enu.nextElement();
                    final Entity en = entityStore.getEntity(key);
                    decl = new EntityDeclarationImpl();
                    decl.setEntityName(key);
                    if (en.isExternal()) {
                        decl.setXMLResourceIdentifier(((Entity.ExternalEntity)en).entityLocation);
                        decl.setNotationName(((Entity.ExternalEntity)en).notation);
                    }
                    else {
                        decl.setEntityReplacementText(((Entity.InternalEntity)en).text);
                    }
                    list.add(decl);
                }
            }
            return list;
        }
        return null;
    }
    
    protected List getNotationDecls() {
        if (this.fEventType != 11) {
            return null;
        }
        if (this.fScanner.fDTDScanner == null) {
            return null;
        }
        final DTDGrammar grammar = ((XMLDTDScannerImpl)this.fScanner.fDTDScanner).getGrammar();
        if (grammar == null) {
            return null;
        }
        final List notations = grammar.getNotationDecls();
        final Iterator it = notations.iterator();
        final ArrayList list = new ArrayList();
        while (it.hasNext()) {
            final XMLNotationDecl ni = it.next();
            if (ni != null) {
                list.add(new NotationDeclarationImpl(ni));
            }
        }
        return list;
    }
}
