package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import java.util.Iterator;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import javax.xml.stream.Location;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import java.util.NoSuchElementException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.InputStream;
import com.sun.xml.internal.fastinfoset.sax.AttributesHolder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import java.util.logging.Logger;
import com.sun.xml.internal.fastinfoset.OctetBufferListener;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.fastinfoset.Decoder;

public class StAXDocumentParser extends Decoder implements XMLStreamReader, FastInfosetStreamReader, OctetBufferListener
{
    private static final Logger logger;
    protected static final int INTERNAL_STATE_START_DOCUMENT = 0;
    protected static final int INTERNAL_STATE_START_ELEMENT_TERMINATE = 1;
    protected static final int INTERNAL_STATE_SINGLE_TERMINATE_ELEMENT_WITH_NAMESPACES = 2;
    protected static final int INTERNAL_STATE_DOUBLE_TERMINATE_ELEMENT = 3;
    protected static final int INTERNAL_STATE_END_DOCUMENT = 4;
    protected static final int INTERNAL_STATE_VOID = -1;
    protected int _internalState;
    protected int _eventType;
    protected QualifiedName[] _qNameStack;
    protected int[] _namespaceAIIsStartStack;
    protected int[] _namespaceAIIsEndStack;
    protected int _stackCount;
    protected String[] _namespaceAIIsPrefix;
    protected String[] _namespaceAIIsNamespaceName;
    protected int[] _namespaceAIIsPrefixIndex;
    protected int _namespaceAIIsIndex;
    protected int _currentNamespaceAIIsStart;
    protected int _currentNamespaceAIIsEnd;
    protected QualifiedName _qualifiedName;
    protected AttributesHolder _attributes;
    protected boolean _clearAttributes;
    protected char[] _characters;
    protected int _charactersOffset;
    protected String _algorithmURI;
    protected int _algorithmId;
    protected boolean _isAlgorithmDataCloned;
    protected byte[] _algorithmData;
    protected int _algorithmDataOffset;
    protected int _algorithmDataLength;
    protected String _piTarget;
    protected String _piData;
    protected NamespaceContextImpl _nsContext;
    protected String _characterEncodingScheme;
    protected StAXManager _manager;
    private byte[] base64TaleBytes;
    private int base64TaleLength;
    
    public StAXDocumentParser() {
        this._qNameStack = new QualifiedName[32];
        this._namespaceAIIsStartStack = new int[32];
        this._namespaceAIIsEndStack = new int[32];
        this._stackCount = -1;
        this._namespaceAIIsPrefix = new String[32];
        this._namespaceAIIsNamespaceName = new String[32];
        this._namespaceAIIsPrefixIndex = new int[32];
        this._attributes = new AttributesHolder();
        this._clearAttributes = false;
        this._nsContext = new NamespaceContextImpl();
        this.base64TaleBytes = new byte[3];
        this.reset();
        this._manager = new StAXManager(1);
    }
    
    public StAXDocumentParser(final InputStream s) {
        this();
        this.setInputStream(s);
        this._manager = new StAXManager(1);
    }
    
    public StAXDocumentParser(final InputStream s, final StAXManager manager) {
        this(s);
        this._manager = manager;
    }
    
    @Override
    public void setInputStream(final InputStream s) {
        super.setInputStream(s);
        this.reset();
    }
    
    @Override
    public void reset() {
        super.reset();
        if (this._internalState != 0 && this._internalState != 4) {
            for (int i = this._namespaceAIIsIndex - 1; i >= 0; --i) {
                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
            }
            this._stackCount = -1;
            this._namespaceAIIsIndex = 0;
            this._characters = null;
            this._algorithmData = null;
        }
        this._characterEncodingScheme = "UTF-8";
        this._eventType = 7;
        this._internalState = 0;
    }
    
    protected void resetOnError() {
        super.reset();
        if (this._v != null) {
            this._prefixTable.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
        this._stackCount = -1;
        this._namespaceAIIsIndex = 0;
        this._characters = null;
        this._algorithmData = null;
        this._eventType = 7;
        this._internalState = 0;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (this._manager != null) {
            return this._manager.getProperty(name);
        }
        return null;
    }
    
    @Override
    public int next() throws XMLStreamException {
        try {
            if (this._internalState != -1) {
                switch (this._internalState) {
                    case 0: {
                        this.decodeHeader();
                        this.processDII();
                        this._internalState = -1;
                        break;
                    }
                    case 1: {
                        if (this._currentNamespaceAIIsEnd > 0) {
                            for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                            }
                            this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        }
                        this.popStack();
                        this._internalState = -1;
                        return this._eventType = 2;
                    }
                    case 2: {
                        for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                            this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                        }
                        this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        this._internalState = -1;
                        break;
                    }
                    case 3: {
                        if (this._currentNamespaceAIIsEnd > 0) {
                            for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                            }
                            this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        }
                        if (this._stackCount == -1) {
                            this._internalState = 4;
                            return this._eventType = 8;
                        }
                        this.popStack();
                        this._internalState = ((this._currentNamespaceAIIsEnd > 0) ? 2 : -1);
                        return this._eventType = 2;
                    }
                    case 4: {
                        throw new NoSuchElementException(CommonResourceBundle.getInstance().getString("message.noMoreEvents"));
                    }
                }
            }
            this._characters = null;
            this._algorithmData = null;
            this._currentNamespaceAIIsEnd = 0;
            final int b = this.read();
            switch (DecoderStateTables.EII(b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[b], false);
                    return this._eventType;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[b & 0x1F], true);
                    return this._eventType;
                }
                case 2: {
                    this.processEII(this.processEIIIndexMedium(b), (b & 0x40) > 0);
                    return this._eventType;
                }
                case 3: {
                    this.processEII(this.processEIIIndexLarge(b), (b & 0x40) > 0);
                    return this._eventType;
                }
                case 5: {
                    final QualifiedName qn = this.processLiteralQualifiedName(b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (b & 0x40) > 0);
                    return this._eventType;
                }
                case 4: {
                    this.processEIIWithNamespaces((b & 0x40) > 0);
                    return this._eventType;
                }
                case 6: {
                    this._octetBufferLength = (b & 0x1) + 1;
                    this.processUtf8CharacterString(b);
                    return this._eventType = 4;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString(b);
                    return this._eventType = 4;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString(b);
                    return this._eventType = 4;
                }
                case 9: {
                    this._octetBufferLength = (b & 0x1) + 1;
                    this.processUtf16CharacterString(b);
                    return this._eventType = 4;
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf16CharacterString(b);
                    return this._eventType = 4;
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf16CharacterString(b);
                    return this._eventType = 4;
                }
                case 12: {
                    final boolean addToTable = (b & 0x10) > 0;
                    this._identifier = (b & 0x2) << 6;
                    final int b2 = this.read();
                    this._identifier |= (b2 & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(b2);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                        this._characters = this._characterContentChunkTable._array;
                    }
                    else {
                        this._characters = this._charBuffer;
                        this._charactersOffset = 0;
                    }
                    return this._eventType = 4;
                }
                case 13: {
                    final boolean addToTable = (b & 0x10) > 0;
                    this._algorithmId = (b & 0x2) << 6;
                    final int b2 = this.read();
                    this._algorithmId |= (b2 & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(b2);
                    this.processCIIEncodingAlgorithm(addToTable);
                    if (this._algorithmId == 9) {
                        return this._eventType = 12;
                    }
                    return this._eventType = 4;
                }
                case 14: {
                    final int index = b & 0xF;
                    this._characterContentChunkTable._cachedIndex = index;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    return this._eventType = 4;
                }
                case 15: {
                    final int index = ((b & 0x3) << 8 | this.read()) + 16;
                    this._characterContentChunkTable._cachedIndex = index;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    return this._eventType = 4;
                }
                case 16: {
                    final int index = ((b & 0x3) << 16 | this.read() << 8 | this.read()) + 1040;
                    this._characterContentChunkTable._cachedIndex = index;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    return this._eventType = 4;
                }
                case 17: {
                    final int index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    this._characterContentChunkTable._cachedIndex = index;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    return this._eventType = 4;
                }
                case 18: {
                    this.processCommentII();
                    return this._eventType;
                }
                case 19: {
                    this.processProcessingII();
                    return this._eventType;
                }
                case 21: {
                    this.processUnexpandedEntityReference(b);
                    return this.next();
                }
                case 23: {
                    if (this._stackCount != -1) {
                        this.popStack();
                        this._internalState = 3;
                        return this._eventType = 2;
                    }
                    this._internalState = 4;
                    return this._eventType = 8;
                }
                case 22: {
                    if (this._stackCount != -1) {
                        this.popStack();
                        if (this._currentNamespaceAIIsEnd > 0) {
                            this._internalState = 2;
                        }
                        return this._eventType = 2;
                    }
                    this._internalState = 4;
                    return this._eventType = 8;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
                }
            }
        }
        catch (final IOException e) {
            this.resetOnError();
            StAXDocumentParser.logger.log(Level.FINE, "next() exception", e);
            throw new XMLStreamException(e);
        }
        catch (final FastInfosetException e2) {
            this.resetOnError();
            StAXDocumentParser.logger.log(Level.FINE, "next() exception", e2);
            throw new XMLStreamException(e2);
        }
        catch (final RuntimeException e3) {
            this.resetOnError();
            StAXDocumentParser.logger.log(Level.FINE, "next() exception", e3);
            throw e3;
        }
    }
    
    private final void processUtf8CharacterString(final int b) throws IOException {
        if ((b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            this._characters = this._characterContentChunkTable._array;
            this._charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, this._charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
        }
        else {
            this.decodeUtf8StringAsCharBuffer();
            this._characters = this._charBuffer;
            this._charactersOffset = 0;
        }
    }
    
    private final void processUtf16CharacterString(final int b) throws IOException {
        this.decodeUtf16StringAsCharBuffer();
        if ((b & 0x10) > 0) {
            this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            this._characters = this._characterContentChunkTable._array;
        }
        else {
            this._characters = this._charBuffer;
            this._charactersOffset = 0;
        }
    }
    
    private void popStack() {
        this._qualifiedName = this._qNameStack[this._stackCount];
        this._currentNamespaceAIIsStart = this._namespaceAIIsStartStack[this._stackCount];
        this._currentNamespaceAIIsEnd = this._namespaceAIIsEndStack[this._stackCount];
        this._qNameStack[this._stackCount--] = null;
    }
    
    @Override
    public final void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (type != this._eventType) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotMatch", new Object[] { getEventTypeString(type) }));
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.namespaceURINotMatch", new Object[] { namespaceURI }));
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.localNameNotMatch", new Object[] { localName }));
        }
    }
    
    @Override
    public final String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), this.getLocation());
        }
        this.next();
        return this.getElementText(true);
    }
    
    public final String getElementText(final boolean startElementRead) throws XMLStreamException {
        if (!startElementRead) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), this.getLocation());
        }
        int eventType = this.getEventType();
        final StringBuilder content = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            }
            else if (eventType != 3) {
                if (eventType != 5) {
                    if (eventType == 8) {
                        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEOF"));
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"), this.getLocation());
                    }
                    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEventType") + getEventTypeString(eventType), this.getLocation());
                }
            }
            eventType = this.next();
        }
        return content.toString();
    }
    
    @Override
    public final int nextTag() throws XMLStreamException {
        this.next();
        return this.nextTag(true);
    }
    
    public final int nextTag(final boolean currentTagRead) throws XMLStreamException {
        int eventType = this.getEventType();
        if (!currentTagRead) {
            eventType = this.next();
        }
        while ((eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5) {
            eventType = this.next();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.expectedStartOrEnd"), this.getLocation());
        }
        return eventType;
    }
    
    @Override
    public final boolean hasNext() throws XMLStreamException {
        return this._eventType != 8;
    }
    
    @Override
    public void close() throws XMLStreamException {
        try {
            super.closeIfRequired();
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public final String getNamespaceURI(final String prefix) {
        final String namespace = this.getNamespaceDecl(prefix);
        if (namespace != null) {
            return namespace;
        }
        if (prefix == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPrefix"));
        }
        return null;
    }
    
    @Override
    public final boolean isStartElement() {
        return this._eventType == 1;
    }
    
    @Override
    public final boolean isEndElement() {
        return this._eventType == 2;
    }
    
    @Override
    public final boolean isCharacters() {
        return this._eventType == 4;
    }
    
    @Override
    public final boolean isWhiteSpace() {
        if (this.isCharacters() || this._eventType == 12) {
            final char[] ch = this.getTextCharacters();
            for (int start = this.getTextStart(), length = this.getTextLength(), i = start; i < start + length; ++i) {
                if (!XMLChar.isSpace(ch[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public final String getAttributeValue(final String namespaceURI, final String localName) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        if (localName == null) {
            throw new IllegalArgumentException();
        }
        if (namespaceURI != null) {
            for (int i = 0; i < this._attributes.getLength(); ++i) {
                if (this._attributes.getLocalName(i).equals(localName) && this._attributes.getURI(i).equals(namespaceURI)) {
                    return this._attributes.getValue(i);
                }
            }
        }
        else {
            for (int i = 0; i < this._attributes.getLength(); ++i) {
                if (this._attributes.getLocalName(i).equals(localName)) {
                    return this._attributes.getValue(i);
                }
            }
        }
        return null;
    }
    
    @Override
    public final int getAttributeCount() {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getLength();
    }
    
    @Override
    public final QName getAttributeName(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getQualifiedName(index).getQName();
    }
    
    @Override
    public final String getAttributeNamespace(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getURI(index);
    }
    
    @Override
    public final String getAttributeLocalName(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getLocalName(index);
    }
    
    @Override
    public final String getAttributePrefix(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getPrefix(index);
    }
    
    @Override
    public final String getAttributeType(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getType(index);
    }
    
    @Override
    public final String getAttributeValue(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getValue(index);
    }
    
    @Override
    public final boolean isAttributeSpecified(final int index) {
        return false;
    }
    
    @Override
    public final int getNamespaceCount() {
        if (this._eventType == 1 || this._eventType == 2) {
            return (this._currentNamespaceAIIsEnd > 0) ? (this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart) : 0;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceCount"));
    }
    
    @Override
    public final String getNamespacePrefix(final int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsPrefix[this._currentNamespaceAIIsStart + index];
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
    }
    
    @Override
    public final String getNamespaceURI(final int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsNamespaceName[this._currentNamespaceAIIsStart + index];
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
    }
    
    @Override
    public final NamespaceContext getNamespaceContext() {
        return this._nsContext;
    }
    
    @Override
    public final int getEventType() {
        return this._eventType;
    }
    
    @Override
    public final String getText() {
        if (this._characters == null) {
            this.checkTextState();
        }
        if (this._characters == this._characterContentChunkTable._array) {
            return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
        }
        return new String(this._characters, this._charactersOffset, this._charBufferLength);
    }
    
    @Override
    public final char[] getTextCharacters() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._characters;
    }
    
    @Override
    public final int getTextStart() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._charactersOffset;
    }
    
    @Override
    public final int getTextLength() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._charBufferLength;
    }
    
    @Override
    public final int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (this._characters == null) {
            this.checkTextState();
        }
        try {
            final int bytesToCopy = Math.min(this._charBufferLength, length);
            System.arraycopy(this._characters, this._charactersOffset + sourceStart, target, targetStart, bytesToCopy);
            return bytesToCopy;
        }
        catch (final IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected final void checkTextState() {
        if (this._algorithmData == null) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
        }
        try {
            this.convertEncodingAlgorithmDataToCharacters();
        }
        catch (final Exception e) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
        }
    }
    
    @Override
    public final String getEncoding() {
        return this._characterEncodingScheme;
    }
    
    @Override
    public final boolean hasText() {
        return this._characters != null;
    }
    
    @Override
    public final Location getLocation() {
        return EventLocation.getNilLocation();
    }
    
    @Override
    public final QName getName() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.getQName();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
    }
    
    @Override
    public final String getLocalName() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.localName;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetLocalName"));
    }
    
    @Override
    public final boolean hasName() {
        return this._eventType == 1 || this._eventType == 2;
    }
    
    @Override
    public final String getNamespaceURI() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.namespaceName;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceURI"));
    }
    
    @Override
    public final String getPrefix() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.prefix;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPrefix"));
    }
    
    @Override
    public final String getVersion() {
        return null;
    }
    
    @Override
    public final boolean isStandalone() {
        return false;
    }
    
    @Override
    public final boolean standaloneSet() {
        return false;
    }
    
    @Override
    public final String getCharacterEncodingScheme() {
        return null;
    }
    
    @Override
    public final String getPITarget() {
        if (this._eventType != 3) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPITarget"));
        }
        return this._piTarget;
    }
    
    @Override
    public final String getPIData() {
        if (this._eventType != 3) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPIData"));
        }
        return this._piData;
    }
    
    public final String getNameString() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.getQNameString();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
    }
    
    public final String getAttributeNameString(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getQualifiedName(index).getQNameString();
    }
    
    public final String getTextAlgorithmURI() {
        return this._algorithmURI;
    }
    
    public final int getTextAlgorithmIndex() {
        return this._algorithmId;
    }
    
    public final boolean hasTextAlgorithmBytes() {
        return this._algorithmData != null;
    }
    
    @Deprecated
    public final byte[] getTextAlgorithmBytes() {
        if (this._algorithmData == null) {
            return null;
        }
        final byte[] algorithmData = new byte[this._algorithmData.length];
        System.arraycopy(this._algorithmData, 0, algorithmData, 0, this._algorithmData.length);
        return algorithmData;
    }
    
    public final byte[] getTextAlgorithmBytesClone() {
        if (this._algorithmData == null) {
            return null;
        }
        final byte[] algorithmData = new byte[this._algorithmDataLength];
        System.arraycopy(this._algorithmData, this._algorithmDataOffset, algorithmData, 0, this._algorithmDataLength);
        return algorithmData;
    }
    
    public final int getTextAlgorithmStart() {
        return this._algorithmDataOffset;
    }
    
    public final int getTextAlgorithmLength() {
        return this._algorithmDataLength;
    }
    
    public final int getTextAlgorithmBytes(final int sourceStart, final byte[] target, final int targetStart, final int length) throws XMLStreamException {
        try {
            System.arraycopy(this._algorithmData, sourceStart, target, targetStart, length);
            return length;
        }
        catch (final IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public final int peekNext() throws XMLStreamException {
        try {
            switch (DecoderStateTables.EII(this.peek(this))) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    return 1;
                }
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17: {
                    return 4;
                }
                case 18: {
                    return 5;
                }
                case 19: {
                    return 3;
                }
                case 21: {
                    return 9;
                }
                case 22:
                case 23: {
                    return (this._stackCount != -1) ? 2 : 8;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
                }
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
        catch (final FastInfosetException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    @Override
    public void onBeforeOctetBufferOverwrite() {
        if (this._algorithmData != null) {
            this._algorithmData = this.getTextAlgorithmBytesClone();
            this._algorithmDataOffset = 0;
            this._isAlgorithmDataCloned = true;
        }
    }
    
    @Override
    public final int accessNamespaceCount() {
        return (this._currentNamespaceAIIsEnd > 0) ? (this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart) : 0;
    }
    
    @Override
    public final String accessLocalName() {
        return this._qualifiedName.localName;
    }
    
    @Override
    public final String accessNamespaceURI() {
        return this._qualifiedName.namespaceName;
    }
    
    @Override
    public final String accessPrefix() {
        return this._qualifiedName.prefix;
    }
    
    @Override
    public final char[] accessTextCharacters() {
        if (this._characters == null) {
            return null;
        }
        final char[] clonedCharacters = new char[this._characters.length];
        System.arraycopy(this._characters, 0, clonedCharacters, 0, this._characters.length);
        return clonedCharacters;
    }
    
    @Override
    public final int accessTextStart() {
        return this._charactersOffset;
    }
    
    @Override
    public final int accessTextLength() {
        return this._charBufferLength;
    }
    
    protected final void processDII() throws FastInfosetException, IOException {
        final int b = this.read();
        if (b > 0) {
            this.processDIIOptionalProperties(b);
        }
    }
    
    protected final void processDIIOptionalProperties(final int b) throws FastInfosetException, IOException {
        if (b == 32) {
            this.decodeInitialVocabulary();
            return;
        }
        if ((b & 0x40) > 0) {
            this.decodeAdditionalData();
        }
        if ((b & 0x20) > 0) {
            this.decodeInitialVocabulary();
        }
        if ((b & 0x10) > 0) {
            this.decodeNotations();
        }
        if ((b & 0x8) > 0) {
            this.decodeUnparsedEntities();
        }
        if ((b & 0x4) > 0) {
            this._characterEncodingScheme = this.decodeCharacterEncodingScheme();
        }
        if ((b & 0x2) > 0) {
            final boolean b2 = this.read() > 0;
        }
        if ((b & 0x1) > 0) {
            this.decodeVersion();
        }
    }
    
    protected final void resizeNamespaceAIIs() {
        final String[] namespaceAIIsPrefix = new String[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsPrefix, 0, namespaceAIIsPrefix, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsPrefix = namespaceAIIsPrefix;
        final String[] namespaceAIIsNamespaceName = new String[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsNamespaceName, 0, namespaceAIIsNamespaceName, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsNamespaceName = namespaceAIIsNamespaceName;
        final int[] namespaceAIIsPrefixIndex = new int[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsPrefixIndex, 0, namespaceAIIsPrefixIndex, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsPrefixIndex = namespaceAIIsPrefixIndex;
    }
    
    protected final void processEIIWithNamespaces(final boolean hasAttributes) throws FastInfosetException, IOException {
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        this._currentNamespaceAIIsStart = this._namespaceAIIsIndex;
        String prefix = "";
        String namespaceName = "";
        int b;
        for (b = this.read(); (b & 0xFC) == 0xCC; b = this.read()) {
            if (this._namespaceAIIsIndex == this._namespaceAIIsPrefix.length) {
                this.resizeNamespaceAIIs();
            }
            switch (b & 0x3) {
                case 0: {
                    final String[] namespaceAIIsPrefix = this._namespaceAIIsPrefix;
                    final int namespaceAIIsIndex = this._namespaceAIIsIndex;
                    final String[] namespaceAIIsNamespaceName = this._namespaceAIIsNamespaceName;
                    final int namespaceAIIsIndex2 = this._namespaceAIIsIndex;
                    final String s = "";
                    namespaceAIIsPrefix[namespaceAIIsIndex] = (namespaceAIIsNamespaceName[namespaceAIIsIndex2] = s);
                    namespaceName = s;
                    prefix = s;
                    final int[] namespaceAIIsPrefixIndex = this._namespaceAIIsPrefixIndex;
                    final int n = this._namespaceAIIsIndex++;
                    final int n2 = -1;
                    namespaceAIIsPrefixIndex[n] = n2;
                    this._prefixIndex = n2;
                    this._namespaceNameIndex = n2;
                    break;
                }
                case 1: {
                    final String[] namespaceAIIsPrefix2 = this._namespaceAIIsPrefix;
                    final int namespaceAIIsIndex3 = this._namespaceAIIsIndex;
                    final String s2 = "";
                    namespaceAIIsPrefix2[namespaceAIIsIndex3] = s2;
                    prefix = s2;
                    final String[] namespaceAIIsNamespaceName2 = this._namespaceAIIsNamespaceName;
                    final int namespaceAIIsIndex4 = this._namespaceAIIsIndex;
                    final String decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
                    namespaceAIIsNamespaceName2[namespaceAIIsIndex4] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName;
                    namespaceName = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName;
                    final int[] namespaceAIIsPrefixIndex2 = this._namespaceAIIsPrefixIndex;
                    final int n3 = this._namespaceAIIsIndex++;
                    final int prefixIndex = -1;
                    namespaceAIIsPrefixIndex2[n3] = prefixIndex;
                    this._prefixIndex = prefixIndex;
                    break;
                }
                case 2: {
                    final String[] namespaceAIIsPrefix3 = this._namespaceAIIsPrefix;
                    final int namespaceAIIsIndex5 = this._namespaceAIIsIndex;
                    final String decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    namespaceAIIsPrefix3[namespaceAIIsIndex5] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix;
                    prefix = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix;
                    final String[] namespaceAIIsNamespaceName3 = this._namespaceAIIsNamespaceName;
                    final int namespaceAIIsIndex6 = this._namespaceAIIsIndex;
                    final String s3 = "";
                    namespaceAIIsNamespaceName3[namespaceAIIsIndex6] = s3;
                    namespaceName = s3;
                    this._namespaceNameIndex = -1;
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    final String[] namespaceAIIsPrefix4 = this._namespaceAIIsPrefix;
                    final int namespaceAIIsIndex7 = this._namespaceAIIsIndex;
                    final String decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix2 = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    namespaceAIIsPrefix4[namespaceAIIsIndex7] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix2;
                    prefix = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix2;
                    final String[] namespaceAIIsNamespaceName4 = this._namespaceAIIsNamespaceName;
                    final int namespaceAIIsIndex8 = this._namespaceAIIsIndex;
                    final String decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName2 = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
                    namespaceAIIsNamespaceName4[namespaceAIIsIndex8] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName2;
                    namespaceName = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName2;
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
                    break;
                }
            }
            this._prefixTable.pushScopeWithPrefixEntry(prefix, namespaceName, this._prefixIndex, this._namespaceNameIndex);
        }
        if (b != 240) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
        }
        this._currentNamespaceAIIsEnd = this._namespaceAIIsIndex;
        b = this.read();
        switch (DecoderStateTables.EII(b)) {
            case 0: {
                this.processEII(this._elementNameTable._array[b], hasAttributes);
                break;
            }
            case 2: {
                this.processEII(this.processEIIIndexMedium(b), hasAttributes);
                break;
            }
            case 3: {
                this.processEII(this.processEIIIndexLarge(b), hasAttributes);
                break;
            }
            case 5: {
                final QualifiedName qn = this.processLiteralQualifiedName(b & 0x3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
    }
    
    protected final void processEII(final QualifiedName name, final boolean hasAttributes) throws FastInfosetException, IOException {
        if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
        }
        this._eventType = 1;
        this._qualifiedName = name;
        if (this._clearAttributes) {
            this._attributes.clear();
            this._clearAttributes = false;
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        ++this._stackCount;
        if (this._stackCount == this._qNameStack.length) {
            final QualifiedName[] qNameStack = new QualifiedName[this._qNameStack.length * 2];
            System.arraycopy(this._qNameStack, 0, qNameStack, 0, this._qNameStack.length);
            this._qNameStack = qNameStack;
            final int[] namespaceAIIsStartStack = new int[this._namespaceAIIsStartStack.length * 2];
            System.arraycopy(this._namespaceAIIsStartStack, 0, namespaceAIIsStartStack, 0, this._namespaceAIIsStartStack.length);
            this._namespaceAIIsStartStack = namespaceAIIsStartStack;
            final int[] namespaceAIIsEndStack = new int[this._namespaceAIIsEndStack.length * 2];
            System.arraycopy(this._namespaceAIIsEndStack, 0, namespaceAIIsEndStack, 0, this._namespaceAIIsEndStack.length);
            this._namespaceAIIsEndStack = namespaceAIIsEndStack;
        }
        this._qNameStack[this._stackCount] = this._qualifiedName;
        this._namespaceAIIsStartStack[this._stackCount] = this._currentNamespaceAIIsStart;
        this._namespaceAIIsEndStack[this._stackCount] = this._currentNamespaceAIIsEnd;
    }
    
    protected final void processAIIs() throws FastInfosetException, IOException {
        if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
            this._duplicateAttributeVerifier.clear();
        }
        this._clearAttributes = true;
        boolean terminate = false;
        do {
            int b = this.read();
            QualifiedName name = null;
            switch (DecoderStateTables.AII(b)) {
                case 0: {
                    name = this._attributeNameTable._array[b];
                    break;
                }
                case 1: {
                    final int i = ((b & 0x1F) << 8 | this.read()) + 64;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 2: {
                    final int i = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 3: {
                    name = this.processLiteralQualifiedName(b & 0x3, this._attributeNameTable.getNext());
                    name.createAttributeValues(256);
                    this._attributeNameTable.add(name);
                    break;
                }
                case 5: {
                    this._internalState = 1;
                }
                case 4: {
                    terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
                }
            }
            if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
            }
            this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
            b = this.read();
            switch (DecoderStateTables.NISTRING(b)) {
                case 0: {
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 1: {
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 2: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 3: {
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 4: {
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 5: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 6: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    final String value = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 7: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    this.processAIIEncodingAlgorithm(name, addToTable);
                    continue;
                }
                case 8: {
                    this._attributes.addAttribute(name, this._attributeValueTable._array[b & 0x3F]);
                    continue;
                }
                case 9: {
                    final int index = ((b & 0x1F) << 8 | this.read()) + 64;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    continue;
                }
                case 10: {
                    final int index = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    continue;
                }
                case 11: {
                    this._attributes.addAttribute(name, "");
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
                }
            }
        } while (!terminate);
        this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
    }
    
    protected final QualifiedName processEIIIndexMedium(final int b) throws FastInfosetException, IOException {
        final int i = ((b & 0x7) << 8 | this.read()) + 32;
        return this._elementNameTable._array[i];
    }
    
    protected final QualifiedName processEIIIndexLarge(final int b) throws FastInfosetException, IOException {
        int i;
        if ((b & 0x30) == 0x20) {
            i = ((b & 0x7) << 16 | this.read() << 8 | this.read()) + 2080;
        }
        else {
            i = ((this.read() & 0xF) << 16 | this.read() << 8 | this.read()) + 526368;
        }
        return this._elementNameTable._array[i];
    }
    
    protected final QualifiedName processLiteralQualifiedName(final int state, QualifiedName q) throws FastInfosetException, IOException {
        if (q == null) {
            q = new QualifiedName();
        }
        switch (state) {
            case 0: {
                return q.set("", "", this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, -1, this._identifier);
            }
            case 1: {
                return q.set("", this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, this._namespaceNameIndex, this._identifier);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, this._prefixIndex, this._namespaceNameIndex, this._identifier);
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
            }
        }
    }
    
    protected final void processCommentII() throws FastInfosetException, IOException {
        this._eventType = 5;
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                if (this._addToTable) {
                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                }
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                break;
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                final CharArray ca = this._v.otherString.get(this._integer);
                this._characters = ca.ch;
                this._charactersOffset = ca.start;
                this._charBufferLength = ca.length;
                break;
            }
            case 3: {
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                this._charBufferLength = 0;
                break;
            }
        }
    }
    
    protected final void processProcessingII() throws FastInfosetException, IOException {
        this._eventType = 3;
        this._piTarget = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                this._piData = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(this._piData));
                    break;
                }
                break;
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                this._piData = this._v.otherString.get(this._integer).toString();
                break;
            }
            case 3: {
                this._piData = "";
                break;
            }
        }
    }
    
    protected final void processUnexpandedEntityReference(final int b) throws FastInfosetException, IOException {
        this._eventType = 9;
        final String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        final String system_identifier = ((b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
        final String public_identifier = ((b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
        if (StAXDocumentParser.logger.isLoggable(Level.FINEST)) {
            StAXDocumentParser.logger.log(Level.FINEST, "processUnexpandedEntityReference: entity_reference_name={0} system_identifier={1}public_identifier={2}", new Object[] { entity_reference_name, system_identifier, public_identifier });
        }
    }
    
    protected final void processCIIEncodingAlgorithm(final boolean addToTable) throws FastInfosetException, IOException {
        this._algorithmData = this._octetBuffer;
        this._algorithmDataOffset = this._octetBufferStart;
        this._algorithmDataLength = this._octetBufferLength;
        this._isAlgorithmDataCloned = false;
        if (this._algorithmId >= 32) {
            this._algorithmURI = this._v.encodingAlgorithm.get(this._algorithmId - 32);
            if (this._algorithmURI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { this._identifier }));
            }
        }
        else if (this._algorithmId > 9) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        if (addToTable) {
            this.convertEncodingAlgorithmDataToCharacters();
            this._characterContentChunkTable.add(this._characters, this._characters.length);
        }
    }
    
    protected final void processAIIEncodingAlgorithm(final QualifiedName name, final boolean addToTable) throws FastInfosetException, IOException {
        EncodingAlgorithm ea = null;
        String URI = null;
        if (this._identifier >= 32) {
            URI = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { this._identifier }));
            }
            if (this._registeredEncodingAlgorithms != null) {
                ea = this._registeredEncodingAlgorithms.get(URI);
            }
        }
        else if (this._identifier >= 9) {
            if (this._identifier == 9) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        else {
            ea = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier);
        }
        Object algorithmData;
        if (ea != null) {
            algorithmData = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        }
        else {
            final byte[] data = new byte[this._octetBufferLength];
            System.arraycopy(this._octetBuffer, this._octetBufferStart, data, 0, this._octetBufferLength);
            algorithmData = data;
        }
        this._attributes.addAttributeWithAlgorithmData(name, URI, this._identifier, algorithmData);
        if (addToTable) {
            this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(name.qName)));
        }
    }
    
    protected final void convertEncodingAlgorithmDataToCharacters() throws FastInfosetException, IOException {
        final StringBuffer buffer = new StringBuffer();
        if (this._algorithmId == 1) {
            this.convertBase64AlorithmDataToCharacters(buffer);
        }
        else if (this._algorithmId < 9) {
            final Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).decodeFromBytes(this._algorithmData, this._algorithmDataOffset, this._algorithmDataLength);
            BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).convertToCharacters(array, buffer);
        }
        else {
            if (this._algorithmId == 9) {
                this._octetBufferOffset -= this._octetBufferLength;
                this.decodeUtf8StringIntoCharBuffer();
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                return;
            }
            if (this._algorithmId >= 32) {
                final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(this._algorithmURI);
                if (ea == null) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
                }
                final Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                ea.convertToCharacters(data, buffer);
            }
        }
        this._characters = new char[buffer.length()];
        buffer.getChars(0, buffer.length(), this._characters, 0);
        this._charactersOffset = 0;
        this._charBufferLength = this._characters.length;
    }
    
    protected void convertBase64AlorithmDataToCharacters(final StringBuffer buffer) throws EncodingAlgorithmException, IOException {
        int afterTaleOffset = 0;
        if (this.base64TaleLength > 0) {
            final int bytesToCopy = Math.min(3 - this.base64TaleLength, this._algorithmDataLength);
            System.arraycopy(this._algorithmData, this._algorithmDataOffset, this.base64TaleBytes, this.base64TaleLength, bytesToCopy);
            if (this.base64TaleLength + bytesToCopy == 3) {
                this.base64DecodeWithCloning(buffer, this.base64TaleBytes, 0, 3);
                afterTaleOffset = bytesToCopy;
                this.base64TaleLength = 0;
            }
            else {
                if (!this.isBase64Follows()) {
                    this.base64DecodeWithCloning(buffer, this.base64TaleBytes, 0, this.base64TaleLength + bytesToCopy);
                    return;
                }
                this.base64TaleLength += bytesToCopy;
                return;
            }
        }
        final int taleBytesRemaining = this.isBase64Follows() ? ((this._algorithmDataLength - afterTaleOffset) % 3) : 0;
        if (this._isAlgorithmDataCloned) {
            this.base64DecodeWithoutCloning(buffer, this._algorithmData, this._algorithmDataOffset + afterTaleOffset, this._algorithmDataLength - afterTaleOffset - taleBytesRemaining);
        }
        else {
            this.base64DecodeWithCloning(buffer, this._algorithmData, this._algorithmDataOffset + afterTaleOffset, this._algorithmDataLength - afterTaleOffset - taleBytesRemaining);
        }
        if (taleBytesRemaining > 0) {
            System.arraycopy(this._algorithmData, this._algorithmDataOffset + this._algorithmDataLength - taleBytesRemaining, this.base64TaleBytes, 0, taleBytesRemaining);
            this.base64TaleLength = taleBytesRemaining;
        }
    }
    
    private void base64DecodeWithCloning(final StringBuffer dstBuffer, final byte[] data, final int offset, final int length) throws EncodingAlgorithmException {
        final Object array = BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.decodeFromBytes(data, offset, length);
        BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(array, dstBuffer);
    }
    
    private void base64DecodeWithoutCloning(final StringBuffer dstBuffer, final byte[] data, final int offset, final int length) throws EncodingAlgorithmException {
        BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(data, offset, length, dstBuffer);
    }
    
    public boolean isBase64Follows() throws IOException {
        final int b = this.peek(this);
        switch (DecoderStateTables.EII(b)) {
            case 13: {
                int algorithmId = (b & 0x2) << 6;
                final int b2 = this.peek2(this);
                algorithmId |= (b2 & 0xFC) >> 2;
                return algorithmId == 1;
            }
            default: {
                return false;
            }
        }
    }
    
    public final String getNamespaceDecl(final String prefix) {
        return this._prefixTable.getNamespaceFromPrefix(prefix);
    }
    
    public final String getURI(final String prefix) {
        return this.getNamespaceDecl(prefix);
    }
    
    public final Iterator getPrefixes() {
        return this._prefixTable.getPrefixes();
    }
    
    public final AttributesHolder getAttributesHolder() {
        return this._attributes;
    }
    
    public final void setManager(final StAXManager manager) {
        this._manager = manager;
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
            default: {
                return "UNKNOWN_EVENT_TYPE";
            }
        }
    }
    
    static {
        logger = Logger.getLogger(StAXDocumentParser.class.getName());
    }
    
    protected class NamespaceContextImpl implements NamespaceContext
    {
        @Override
        public final String getNamespaceURI(final String prefix) {
            return StAXDocumentParser.this._prefixTable.getNamespaceFromPrefix(prefix);
        }
        
        @Override
        public final String getPrefix(final String namespaceURI) {
            return StAXDocumentParser.this._prefixTable.getPrefixFromNamespace(namespaceURI);
        }
        
        @Override
        public final Iterator getPrefixes(final String namespaceURI) {
            return StAXDocumentParser.this._prefixTable.getPrefixesFromNamespace(namespaceURI);
        }
    }
}
