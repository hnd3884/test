package com.sun.xml.internal.stream.buffer.stax;

import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import javax.xml.stream.Location;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;

public class StreamReaderBufferProcessor extends AbstractProcessor implements XMLStreamReaderEx
{
    private static final int CACHE_SIZE = 16;
    protected ElementStackEntry[] _stack;
    protected ElementStackEntry _stackTop;
    protected int _depth;
    protected String[] _namespaceAIIsPrefix;
    protected String[] _namespaceAIIsNamespaceName;
    protected int _namespaceAIIsEnd;
    protected InternalNamespaceContext _nsCtx;
    protected int _eventType;
    protected AttributesHolder _attributeCache;
    protected CharSequence _charSequence;
    protected char[] _characters;
    protected int _textOffset;
    protected int _textLen;
    protected String _piTarget;
    protected String _piData;
    private static final int PARSING = 1;
    private static final int PENDING_END_DOCUMENT = 2;
    private static final int COMPLETED = 3;
    private int _completionState;
    
    public StreamReaderBufferProcessor() {
        this._stack = new ElementStackEntry[16];
        this._namespaceAIIsPrefix = new String[16];
        this._namespaceAIIsNamespaceName = new String[16];
        this._nsCtx = new InternalNamespaceContext();
        for (int i = 0; i < this._stack.length; ++i) {
            this._stack[i] = new ElementStackEntry();
        }
        this._attributeCache = new AttributesHolder();
    }
    
    public StreamReaderBufferProcessor(final XMLStreamBuffer buffer) throws XMLStreamException {
        this();
        this.setXMLStreamBuffer(buffer);
    }
    
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer) throws XMLStreamException {
        this.setBuffer(buffer, buffer.isFragment());
        this._completionState = 1;
        this._namespaceAIIsEnd = 0;
        this._characters = null;
        this._charSequence = null;
        this._eventType = 7;
    }
    
    public XMLStreamBuffer nextTagAndMark() throws XMLStreamException {
        while (true) {
            final int s = this.peekStructure();
            if ((s & 0xF0) == 0x20) {
                final Map<String, String> inscope = new HashMap<String, String>(this._namespaceAIIsEnd);
                for (int i = 0; i < this._namespaceAIIsEnd; ++i) {
                    inscope.put(this._namespaceAIIsPrefix[i], this._namespaceAIIsNamespaceName[i]);
                }
                final XMLStreamBufferMark mark = new XMLStreamBufferMark(inscope, this);
                this.next();
                return mark;
            }
            if ((s & 0xF0) == 0x10) {
                this.readStructure();
                final XMLStreamBufferMark mark2 = new XMLStreamBufferMark(new HashMap<String, String>(this._namespaceAIIsEnd), this);
                this.next();
                return mark2;
            }
            if (this.next() == 2) {
                return null;
            }
        }
    }
    
    @Override
    public Object getProperty(final String name) {
        return null;
    }
    
    @Override
    public int next() throws XMLStreamException {
        switch (this._completionState) {
            case 3: {
                throw new XMLStreamException("Invalid State");
            }
            case 2: {
                this._namespaceAIIsEnd = 0;
                this._completionState = 3;
                return this._eventType = 8;
            }
            default: {
                switch (this._eventType) {
                    case 2: {
                        if (this._depth > 1) {
                            this.popElementStack(--this._depth);
                            break;
                        }
                        if (this._depth == 1) {
                            --this._depth;
                            break;
                        }
                        break;
                    }
                }
                this._characters = null;
                this._charSequence = null;
                while (true) {
                    final int eiiState = this.readEiiState();
                    switch (eiiState) {
                        case 1: {
                            continue;
                        }
                        case 3: {
                            final String uri = this.readStructureString();
                            final String localName = this.readStructureString();
                            final String prefix = this.getPrefixFromQName(this.readStructureString());
                            this.processElement(prefix, uri, localName, this.isInscope(this._depth));
                            return this._eventType = 1;
                        }
                        case 4: {
                            this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                            return this._eventType = 1;
                        }
                        case 5: {
                            this.processElement(null, this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                            return this._eventType = 1;
                        }
                        case 6: {
                            this.processElement(null, null, this.readStructureString(), this.isInscope(this._depth));
                            return this._eventType = 1;
                        }
                        case 7: {
                            this._textLen = this.readStructure();
                            this._textOffset = this.readContentCharactersBuffer(this._textLen);
                            this._characters = this._contentCharactersBuffer;
                            return this._eventType = 4;
                        }
                        case 8: {
                            this._textLen = this.readStructure16();
                            this._textOffset = this.readContentCharactersBuffer(this._textLen);
                            this._characters = this._contentCharactersBuffer;
                            return this._eventType = 4;
                        }
                        case 9: {
                            this._characters = this.readContentCharactersCopy();
                            this._textLen = this._characters.length;
                            this._textOffset = 0;
                            return this._eventType = 4;
                        }
                        case 10: {
                            this._eventType = 4;
                            this._charSequence = this.readContentString();
                            return this._eventType = 4;
                        }
                        case 11: {
                            this._eventType = 4;
                            this._charSequence = (CharSequence)this.readContentObject();
                            return this._eventType = 4;
                        }
                        case 12: {
                            this._textLen = this.readStructure();
                            this._textOffset = this.readContentCharactersBuffer(this._textLen);
                            this._characters = this._contentCharactersBuffer;
                            return this._eventType = 5;
                        }
                        case 13: {
                            this._textLen = this.readStructure16();
                            this._textOffset = this.readContentCharactersBuffer(this._textLen);
                            this._characters = this._contentCharactersBuffer;
                            return this._eventType = 5;
                        }
                        case 14: {
                            this._characters = this.readContentCharactersCopy();
                            this._textLen = this._characters.length;
                            this._textOffset = 0;
                            return this._eventType = 5;
                        }
                        case 15: {
                            this._charSequence = this.readContentString();
                            return this._eventType = 5;
                        }
                        case 16: {
                            this._piTarget = this.readStructureString();
                            this._piData = this.readStructureString();
                            return this._eventType = 3;
                        }
                        case 17: {
                            if (this._depth > 1) {
                                return this._eventType = 2;
                            }
                            if (this._depth == 1) {
                                if (this._fragmentMode && --this._treeCount == 0) {
                                    this._completionState = 2;
                                }
                                return this._eventType = 2;
                            }
                            this._namespaceAIIsEnd = 0;
                            this._completionState = 3;
                            return this._eventType = 8;
                        }
                        default: {
                            throw new XMLStreamException("Internal XSB error: Invalid State=" + eiiState);
                        }
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public final void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (type != this._eventType) {
            throw new XMLStreamException("");
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException("");
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException("");
        }
    }
    
    @Override
    public final String getElementTextTrim() throws XMLStreamException {
        return this.getElementText().trim();
    }
    
    @Override
    public final String getElementText() throws XMLStreamException {
        if (this._eventType != 1) {
            throw new XMLStreamException("");
        }
        this.next();
        return this.getElementText(true);
    }
    
    public final String getElementText(final boolean startElementRead) throws XMLStreamException {
        if (!startElementRead) {
            throw new XMLStreamException("");
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
                        throw new XMLStreamException("");
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException("");
                    }
                    throw new XMLStreamException("");
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
            throw new XMLStreamException("");
        }
        return eventType;
    }
    
    @Override
    public final boolean hasNext() {
        return this._eventType != 8;
    }
    
    @Override
    public void close() throws XMLStreamException {
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
            final int start = this.getTextStart();
            for (int length = this.getTextLength(), i = start; i < length; ++i) {
                final char c = ch[i];
                if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public final String getAttributeValue(String namespaceURI, final String localName) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        return this._attributeCache.getValue(namespaceURI, localName);
    }
    
    @Override
    public final int getAttributeCount() {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getLength();
    }
    
    @Override
    public final QName getAttributeName(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        final String prefix = this._attributeCache.getPrefix(index);
        final String localName = this._attributeCache.getLocalName(index);
        final String uri = this._attributeCache.getURI(index);
        return new QName(uri, localName, prefix);
    }
    
    @Override
    public final String getAttributeNamespace(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return fixEmptyString(this._attributeCache.getURI(index));
    }
    
    @Override
    public final String getAttributeLocalName(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getLocalName(index);
    }
    
    @Override
    public final String getAttributePrefix(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return fixEmptyString(this._attributeCache.getPrefix(index));
    }
    
    @Override
    public final String getAttributeType(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getType(index);
    }
    
    @Override
    public final String getAttributeValue(final int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getValue(index);
    }
    
    @Override
    public final boolean isAttributeSpecified(final int index) {
        return false;
    }
    
    @Override
    public final int getNamespaceCount() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._stackTop.namespaceAIIsEnd - this._stackTop.namespaceAIIsStart;
        }
        throw new IllegalStateException("");
    }
    
    @Override
    public final String getNamespacePrefix(final int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsPrefix[this._stackTop.namespaceAIIsStart + index];
        }
        throw new IllegalStateException("");
    }
    
    @Override
    public final String getNamespaceURI(final int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsNamespaceName[this._stackTop.namespaceAIIsStart + index];
        }
        throw new IllegalStateException("");
    }
    
    @Override
    public final String getNamespaceURI(final String prefix) {
        return this._nsCtx.getNamespaceURI(prefix);
    }
    
    @Override
    public final NamespaceContextEx getNamespaceContext() {
        return this._nsCtx;
    }
    
    @Override
    public final int getEventType() {
        return this._eventType;
    }
    
    @Override
    public final String getText() {
        if (this._characters != null) {
            final String s = new String(this._characters, this._textOffset, this._textLen);
            return (String)(this._charSequence = s);
        }
        if (this._charSequence != null) {
            return this._charSequence.toString();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public final char[] getTextCharacters() {
        if (this._characters != null) {
            return this._characters;
        }
        if (this._charSequence != null) {
            this._characters = this._charSequence.toString().toCharArray();
            this._textLen = this._characters.length;
            this._textOffset = 0;
            return this._characters;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public final int getTextStart() {
        if (this._characters != null) {
            return this._textOffset;
        }
        if (this._charSequence != null) {
            return 0;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public final int getTextLength() {
        if (this._characters != null) {
            return this._textLen;
        }
        if (this._charSequence != null) {
            return this._charSequence.length();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public final int getTextCharacters(int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (this._characters == null) {
            if (this._charSequence == null) {
                throw new IllegalStateException("");
            }
            this._characters = this._charSequence.toString().toCharArray();
            this._textLen = this._characters.length;
            this._textOffset = 0;
        }
        try {
            final int remaining = this._textLen - sourceStart;
            final int len = (remaining > length) ? length : remaining;
            sourceStart += this._textOffset;
            System.arraycopy(this._characters, sourceStart, target, targetStart, len);
            return len;
        }
        catch (final IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public final CharSequence getPCDATA() {
        if (this._characters != null) {
            return new CharSequenceImpl(this._textOffset, this._textLen);
        }
        if (this._charSequence != null) {
            return this._charSequence;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public final String getEncoding() {
        return "UTF-8";
    }
    
    @Override
    public final boolean hasText() {
        return this._characters != null || this._charSequence != null;
    }
    
    @Override
    public final Location getLocation() {
        return new DummyLocation();
    }
    
    @Override
    public final boolean hasName() {
        return this._eventType == 1 || this._eventType == 2;
    }
    
    @Override
    public final QName getName() {
        return this._stackTop.getQName();
    }
    
    @Override
    public final String getLocalName() {
        return this._stackTop.localName;
    }
    
    @Override
    public final String getNamespaceURI() {
        return this._stackTop.uri;
    }
    
    @Override
    public final String getPrefix() {
        return this._stackTop.prefix;
    }
    
    @Override
    public final String getVersion() {
        return "1.0";
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
        return "UTF-8";
    }
    
    @Override
    public final String getPITarget() {
        if (this._eventType == 3) {
            return this._piTarget;
        }
        throw new IllegalStateException("");
    }
    
    @Override
    public final String getPIData() {
        if (this._eventType == 3) {
            return this._piData;
        }
        throw new IllegalStateException("");
    }
    
    protected void processElement(final String prefix, final String uri, final String localName, final boolean inscope) {
        this.pushElementStack();
        this._stackTop.set(prefix, uri, localName);
        this._attributeCache.clear();
        int item = this.peekStructure();
        if ((item & 0xF0) == 0x40 || inscope) {
            item = this.processNamespaceAttributes(item, inscope);
        }
        if ((item & 0xF0) == 0x30) {
            this.processAttributes(item);
        }
    }
    
    private boolean isInscope(final int depth) {
        return this._buffer.getInscopeNamespaces().size() > 0 && depth == 0;
    }
    
    private void resizeNamespaceAttributes() {
        final String[] namespaceAIIsPrefix = new String[this._namespaceAIIsEnd * 2];
        System.arraycopy(this._namespaceAIIsPrefix, 0, namespaceAIIsPrefix, 0, this._namespaceAIIsEnd);
        this._namespaceAIIsPrefix = namespaceAIIsPrefix;
        final String[] namespaceAIIsNamespaceName = new String[this._namespaceAIIsEnd * 2];
        System.arraycopy(this._namespaceAIIsNamespaceName, 0, namespaceAIIsNamespaceName, 0, this._namespaceAIIsEnd);
        this._namespaceAIIsNamespaceName = namespaceAIIsNamespaceName;
    }
    
    private int processNamespaceAttributes(int item, final boolean inscope) {
        this._stackTop.namespaceAIIsStart = this._namespaceAIIsEnd;
        final Set<String> prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        while ((item & 0xF0) == 0x40) {
            if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
                this.resizeNamespaceAttributes();
            }
            switch (AbstractProcessor.getNIIState(item)) {
                case 1: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = (this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "");
                    if (inscope) {
                        prefixSet.add("");
                        break;
                    }
                    break;
                }
                case 2: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
                    if (inscope) {
                        prefixSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
                    break;
                }
                case 3: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
                    if (inscope) {
                        prefixSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
                    break;
                }
                case 4: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
                    if (inscope) {
                        prefixSet.add("");
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
                    break;
                }
            }
            this.readStructure();
            item = this.peekStructure();
        }
        if (inscope) {
            for (final Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
                final String key = fixNull(e.getKey());
                if (!prefixSet.contains(key)) {
                    if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
                        this.resizeNamespaceAttributes();
                    }
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = key;
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = e.getValue();
                }
            }
        }
        this._stackTop.namespaceAIIsEnd = this._namespaceAIIsEnd;
        return item;
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private void processAttributes(int item) {
        do {
            switch (AbstractProcessor.getAIIState(item)) {
                case 1: {
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    final String prefix = this.getPrefixFromQName(this.readStructureString());
                    this._attributeCache.addAttributeWithPrefix(prefix, uri, localName, this.readStructureString(), this.readContentString());
                    break;
                }
                case 2: {
                    this._attributeCache.addAttributeWithPrefix(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    this._attributeCache.addAttributeWithPrefix("", this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    this._attributeCache.addAttributeWithPrefix("", "", this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                default: {
                    assert false : "Internal XSB Error: wrong attribute state, Item=" + item;
                    break;
                }
            }
            this.readStructure();
            item = this.peekStructure();
        } while ((item & 0xF0) == 0x30);
    }
    
    private void pushElementStack() {
        if (this._depth == this._stack.length) {
            final ElementStackEntry[] tmp = this._stack;
            System.arraycopy(tmp, 0, this._stack = new ElementStackEntry[this._stack.length * 3 / 2 + 1], 0, tmp.length);
            for (int i = tmp.length; i < this._stack.length; ++i) {
                this._stack[i] = new ElementStackEntry();
            }
        }
        this._stackTop = this._stack[this._depth++];
    }
    
    private void popElementStack(final int depth) {
        this._stackTop = this._stack[depth - 1];
        this._namespaceAIIsEnd = this._stack[depth].namespaceAIIsStart;
    }
    
    private static String fixEmptyString(final String s) {
        if (s.length() == 0) {
            return null;
        }
        return s;
    }
    
    private class CharSequenceImpl implements CharSequence
    {
        private final int _offset;
        private final int _length;
        
        CharSequenceImpl(final int offset, final int length) {
            this._offset = offset;
            this._length = length;
        }
        
        @Override
        public int length() {
            return this._length;
        }
        
        @Override
        public char charAt(final int index) {
            if (index >= 0 && index < StreamReaderBufferProcessor.this._textLen) {
                return StreamReaderBufferProcessor.this._characters[StreamReaderBufferProcessor.this._textOffset + index];
            }
            throw new IndexOutOfBoundsException();
        }
        
        @Override
        public CharSequence subSequence(final int start, final int end) {
            final int length = end - start;
            if (end < 0 || start < 0 || end > length || start > end) {
                throw new IndexOutOfBoundsException();
            }
            return new CharSequenceImpl(this._offset + start, length);
        }
        
        @Override
        public String toString() {
            return new String(StreamReaderBufferProcessor.this._characters, this._offset, this._length);
        }
    }
    
    private final class ElementStackEntry
    {
        String prefix;
        String uri;
        String localName;
        QName qname;
        int namespaceAIIsStart;
        int namespaceAIIsEnd;
        
        public void set(final String prefix, final String uri, final String localName) {
            this.prefix = prefix;
            this.uri = uri;
            this.localName = localName;
            this.qname = null;
            final int namespaceAIIsEnd = StreamReaderBufferProcessor.this._namespaceAIIsEnd;
            this.namespaceAIIsEnd = namespaceAIIsEnd;
            this.namespaceAIIsStart = namespaceAIIsEnd;
        }
        
        public QName getQName() {
            if (this.qname == null) {
                this.qname = new QName(this.fixNull(this.uri), this.localName, this.fixNull(this.prefix));
            }
            return this.qname;
        }
        
        private String fixNull(final String s) {
            return (s == null) ? "" : s;
        }
    }
    
    private final class InternalNamespaceContext implements NamespaceContextEx
    {
        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix cannot be null");
            }
            if (StreamReaderBufferProcessor.this._stringInterningFeature) {
                prefix = prefix.intern();
                for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                    if (prefix == StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]) {
                        return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                    }
                }
            }
            else {
                for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                    if (prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i])) {
                        return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                    }
                }
            }
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
            return null;
        }
        
        @Override
        public String getPrefix(final String namespaceURI) {
            final Iterator i = this.getPrefixes(namespaceURI);
            if (i.hasNext()) {
                return i.next();
            }
            return null;
        }
        
        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException("NamespaceURI cannot be null");
            }
            if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                return Collections.singletonList("xml").iterator();
            }
            if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                return Collections.singletonList("xmlns").iterator();
            }
            return new Iterator() {
                private int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
                private boolean requireFindNext = true;
                private String p;
                
                private String findNext() {
                    while (this.i >= 0) {
                        if (namespaceURI.equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i]) && InternalNamespaceContext.this.getNamespaceURI(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i]).equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i])) {
                            return this.p = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i];
                        }
                        --this.i;
                    }
                    return this.p = null;
                }
                
                @Override
                public boolean hasNext() {
                    if (this.requireFindNext) {
                        this.findNext();
                        this.requireFindNext = false;
                    }
                    return this.p != null;
                }
                
                @Override
                public Object next() {
                    if (this.requireFindNext) {
                        this.findNext();
                    }
                    this.requireFindNext = true;
                    if (this.p == null) {
                        throw new NoSuchElementException();
                    }
                    return this.p;
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public Iterator<Binding> iterator() {
            return new Iterator<Binding>() {
                private final int end = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
                private int current = this.end;
                private boolean requireFindNext = true;
                private Binding namespace;
                
                private Binding findNext() {
                    while (this.current >= 0) {
                        String prefix;
                        int i;
                        for (prefix = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.current], i = this.end; i > this.current && !prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]); --i) {}
                        if (i == this.current--) {
                            return this.namespace = new BindingImpl(prefix, StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.current]);
                        }
                    }
                    return this.namespace = null;
                }
                
                @Override
                public boolean hasNext() {
                    if (this.requireFindNext) {
                        this.findNext();
                        this.requireFindNext = false;
                    }
                    return this.namespace != null;
                }
                
                @Override
                public Binding next() {
                    if (this.requireFindNext) {
                        this.findNext();
                    }
                    this.requireFindNext = true;
                    if (this.namespace == null) {
                        throw new NoSuchElementException();
                    }
                    return this.namespace;
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        private class BindingImpl implements Binding
        {
            final String _prefix;
            final String _namespaceURI;
            
            BindingImpl(final String prefix, final String namespaceURI) {
                this._prefix = prefix;
                this._namespaceURI = namespaceURI;
            }
            
            @Override
            public String getPrefix() {
                return this._prefix;
            }
            
            @Override
            public String getNamespaceURI() {
                return this._namespaceURI;
            }
        }
    }
    
    private class DummyLocation implements Location
    {
        @Override
        public int getLineNumber() {
            return -1;
        }
        
        @Override
        public int getColumnNumber() {
            return -1;
        }
        
        @Override
        public int getCharacterOffset() {
            return -1;
        }
        
        @Override
        public String getPublicId() {
            return null;
        }
        
        @Override
        public String getSystemId() {
            return StreamReaderBufferProcessor.this._buffer.getSystemId();
        }
    }
}
