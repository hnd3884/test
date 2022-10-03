package com.sun.xml.internal.stream.buffer.sax;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xml.sax.Attributes;
import java.util.Collections;
import java.util.HashSet;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;

public class SAXBufferProcessor extends AbstractProcessor implements XMLReader
{
    protected EntityResolver _entityResolver;
    protected DTDHandler _dtdHandler;
    protected ContentHandler _contentHandler;
    protected ErrorHandler _errorHandler;
    protected LexicalHandler _lexicalHandler;
    protected boolean _namespacePrefixesFeature;
    protected AttributesHolder _attributes;
    protected String[] _namespacePrefixes;
    protected int _namespacePrefixesIndex;
    protected int[] _namespaceAttributesStartingStack;
    protected int[] _namespaceAttributesStack;
    protected int _namespaceAttributesStackIndex;
    private static final DefaultWithLexicalHandler DEFAULT_LEXICAL_HANDLER;
    
    public SAXBufferProcessor() {
        this._entityResolver = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._dtdHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._contentHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._errorHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._lexicalHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._namespacePrefixesFeature = false;
        this._attributes = new AttributesHolder();
        this._namespacePrefixes = new String[16];
        this._namespaceAttributesStartingStack = new int[16];
        this._namespaceAttributesStack = new int[16];
    }
    
    @Deprecated
    public SAXBufferProcessor(final XMLStreamBuffer buffer) {
        this._entityResolver = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._dtdHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._contentHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._errorHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._lexicalHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._namespacePrefixesFeature = false;
        this._attributes = new AttributesHolder();
        this._namespacePrefixes = new String[16];
        this._namespaceAttributesStartingStack = new int[16];
        this._namespaceAttributesStack = new int[16];
        this.setXMLStreamBuffer(buffer);
    }
    
    public SAXBufferProcessor(final XMLStreamBuffer buffer, final boolean produceFragmentEvent) {
        this._entityResolver = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._dtdHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._contentHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._errorHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._lexicalHandler = SAXBufferProcessor.DEFAULT_LEXICAL_HANDLER;
        this._namespacePrefixesFeature = false;
        this._attributes = new AttributesHolder();
        this._namespacePrefixes = new String[16];
        this._namespaceAttributesStartingStack = new int[16];
        this._namespaceAttributesStack = new int[16];
        this.setXMLStreamBuffer(buffer, produceFragmentEvent);
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return this._namespacePrefixesFeature;
        }
        if (name.equals("http://xml.org/sax/features/external-general-entities")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/external-parameter-entities")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/string-interning")) {
            return this._stringInterningFeature;
        }
        throw new SAXNotRecognizedException("Feature not supported: " + name);
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            if (!value) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        }
        else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            this._namespacePrefixesFeature = value;
        }
        else if (!name.equals("http://xml.org/sax/features/external-general-entities")) {
            if (!name.equals("http://xml.org/sax/features/external-parameter-entities")) {
                if (!name.equals("http://xml.org/sax/features/string-interning")) {
                    throw new SAXNotRecognizedException("Feature not supported: " + name);
                }
                if (value != this._stringInterningFeature) {
                    throw new SAXNotSupportedException(name + ":" + value);
                }
            }
        }
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            return this.getLexicalHandler();
        }
        throw new SAXNotRecognizedException("Property not recognized: " + name);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (!name.equals("http://xml.org/sax/properties/lexical-handler")) {
            throw new SAXNotRecognizedException("Property not recognized: " + name);
        }
        if (value instanceof LexicalHandler) {
            this.setLexicalHandler((LexicalHandler)value);
            return;
        }
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
        this._entityResolver = resolver;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) {
        this._dtdHandler = handler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this._dtdHandler;
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this._contentHandler = handler;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this._contentHandler;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this._errorHandler = handler;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }
    
    public void setLexicalHandler(final LexicalHandler handler) {
        this._lexicalHandler = handler;
    }
    
    public LexicalHandler getLexicalHandler() {
        return this._lexicalHandler;
    }
    
    @Override
    public void parse(final InputSource input) throws IOException, SAXException {
        this.process();
    }
    
    @Override
    public void parse(final String systemId) throws IOException, SAXException {
        this.process();
    }
    
    @Deprecated
    public final void process(final XMLStreamBuffer buffer) throws SAXException {
        this.setXMLStreamBuffer(buffer);
        this.process();
    }
    
    public final void process(final XMLStreamBuffer buffer, final boolean produceFragmentEvent) throws SAXException {
        this.setXMLStreamBuffer(buffer);
        this.process();
    }
    
    @Deprecated
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }
    
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer, final boolean produceFragmentEvent) {
        if (!produceFragmentEvent && this._treeCount > 1) {
            throw new IllegalStateException("Can't write a forest to a full XML infoset");
        }
        this.setBuffer(buffer, produceFragmentEvent);
    }
    
    public final void process() throws SAXException {
        if (!this._fragmentMode) {
            final LocatorImpl nullLocator = new LocatorImpl();
            nullLocator.setSystemId(this._buffer.getSystemId());
            nullLocator.setLineNumber(-1);
            nullLocator.setColumnNumber(-1);
            this._contentHandler.setDocumentLocator(nullLocator);
            this._contentHandler.startDocument();
        }
        while (this._treeCount > 0) {
            final int item = this.readEiiState();
            switch (item) {
                case 1: {
                    this.processDocument();
                    --this._treeCount;
                    continue;
                }
                case 17: {
                    return;
                }
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
                    --this._treeCount;
                    continue;
                }
                case 4: {
                    final String prefix = this.readStructureString();
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    this.processElement(uri, localName, this.getQName(prefix, localName), this.isInscope());
                    --this._treeCount;
                    continue;
                }
                case 5: {
                    final String uri2 = this.readStructureString();
                    final String localName2 = this.readStructureString();
                    this.processElement(uri2, localName2, localName2, this.isInscope());
                    --this._treeCount;
                    continue;
                }
                case 6: {
                    final String localName3 = this.readStructureString();
                    this.processElement("", localName3, localName3, this.isInscope());
                    --this._treeCount;
                    continue;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    continue;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    continue;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    continue;
                }
                case 15: {
                    this.processComment(this.readContentString());
                    continue;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                default: {
                    throw this.reportFatalError("Illegal state for DIIs: " + item);
                }
            }
        }
        if (!this._fragmentMode) {
            this._contentHandler.endDocument();
        }
    }
    
    private void processCommentAsCharArraySmall() throws SAXException {
        final int length = this.readStructure();
        final int start = this.readContentCharactersBuffer(length);
        this.processComment(this._contentCharactersBuffer, start, length);
    }
    
    private SAXParseException reportFatalError(final String msg) throws SAXException {
        final SAXParseException spe = new SAXParseException(msg, (Locator)null);
        if (this._errorHandler != null) {
            this._errorHandler.fatalError(spe);
        }
        return spe;
    }
    
    private boolean isInscope() {
        return this._buffer.getInscopeNamespaces().size() > 0;
    }
    
    private void processDocument() throws SAXException {
        while (true) {
            final int item = this.readEiiState();
            switch (item) {
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
                    continue;
                }
                case 4: {
                    final String prefix = this.readStructureString();
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    this.processElement(uri, localName, this.getQName(prefix, localName), this.isInscope());
                    continue;
                }
                case 5: {
                    final String uri2 = this.readStructureString();
                    final String localName2 = this.readStructureString();
                    this.processElement(uri2, localName2, localName2, this.isInscope());
                    continue;
                }
                case 6: {
                    final String localName3 = this.readStructureString();
                    this.processElement("", localName3, localName3, this.isInscope());
                    continue;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    continue;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    continue;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    continue;
                }
                case 15: {
                    this.processComment(this.readContentString());
                    continue;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                case 17: {
                    return;
                }
                default: {
                    throw this.reportFatalError("Illegal state for child of DII: " + item);
                }
            }
        }
    }
    
    protected void processElement(final String uri, final String localName, final String qName, final boolean inscope) throws SAXException {
        boolean hasAttributes = false;
        boolean hasNamespaceAttributes = false;
        int item = this.peekStructure();
        final Set<String> prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        if ((item & 0xF0) == 0x40) {
            this.cacheNamespacePrefixStartingIndex();
            hasNamespaceAttributes = true;
            item = this.processNamespaceAttributes(item, inscope, prefixSet);
        }
        if (inscope) {
            this.readInscopeNamespaces(prefixSet);
        }
        if ((item & 0xF0) == 0x30) {
            hasAttributes = true;
            this.processAttributes(item);
        }
        this._contentHandler.startElement(uri, localName, qName, this._attributes);
        if (hasAttributes) {
            this._attributes.clear();
        }
        do {
            item = this.readEiiState();
            switch (item) {
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), false);
                    continue;
                }
                case 4: {
                    final String p = this.readStructureString();
                    final String u = this.readStructureString();
                    final String ln = this.readStructureString();
                    this.processElement(u, ln, this.getQName(p, ln), false);
                    continue;
                }
                case 5: {
                    final String u2 = this.readStructureString();
                    final String ln2 = this.readStructureString();
                    this.processElement(u2, ln2, ln2, false);
                    continue;
                }
                case 6: {
                    final String ln3 = this.readStructureString();
                    this.processElement("", ln3, ln3, false);
                    continue;
                }
                case 7: {
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    this._contentHandler.characters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 8: {
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    this._contentHandler.characters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 9: {
                    final char[] ch = this.readContentCharactersCopy();
                    this._contentHandler.characters(ch, 0, ch.length);
                    continue;
                }
                case 10: {
                    final String s = this.readContentString();
                    this._contentHandler.characters(s.toCharArray(), 0, s.length());
                    continue;
                }
                case 11: {
                    final CharSequence c = (CharSequence)this.readContentObject();
                    final String s2 = c.toString();
                    this._contentHandler.characters(s2.toCharArray(), 0, s2.length());
                    continue;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    continue;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    continue;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    continue;
                }
                case 104: {
                    this.processComment(this.readContentString());
                    continue;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                case 17: {
                    continue;
                }
                default: {
                    throw this.reportFatalError("Illegal state for child of EII: " + item);
                }
            }
        } while (item != 17);
        this._contentHandler.endElement(uri, localName, qName);
        if (hasNamespaceAttributes) {
            this.processEndPrefixMapping();
        }
    }
    
    private void readInscopeNamespaces(final Set<String> prefixSet) throws SAXException {
        for (final Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
            final String key = fixNull(e.getKey());
            if (!prefixSet.contains(key)) {
                this.processNamespaceAttribute(key, e.getValue());
            }
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private void processCommentAsCharArrayCopy() throws SAXException {
        final char[] ch = this.readContentCharactersCopy();
        this.processComment(ch, 0, ch.length);
    }
    
    private void processCommentAsCharArrayMedium() throws SAXException {
        final int length = this.readStructure16();
        final int start = this.readContentCharactersBuffer(length);
        this.processComment(this._contentCharactersBuffer, start, length);
    }
    
    private void processEndPrefixMapping() throws SAXException {
        final int[] namespaceAttributesStack = this._namespaceAttributesStack;
        final int namespaceAttributesStackIndex = this._namespaceAttributesStackIndex - 1;
        this._namespaceAttributesStackIndex = namespaceAttributesStackIndex;
        final int end = namespaceAttributesStack[namespaceAttributesStackIndex];
        final int start = (this._namespaceAttributesStackIndex >= 0) ? this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] : 0;
        for (int i = end - 1; i >= start; --i) {
            this._contentHandler.endPrefixMapping(this._namespacePrefixes[i]);
        }
        this._namespacePrefixesIndex = start;
    }
    
    private int processNamespaceAttributes(int item, final boolean collectPrefixes, final Set<String> prefixSet) throws SAXException {
        do {
            switch (AbstractProcessor.getNIIState(item)) {
                case 1: {
                    this.processNamespaceAttribute("", "");
                    if (collectPrefixes) {
                        prefixSet.add("");
                        break;
                    }
                    break;
                }
                case 2: {
                    final String prefix = this.readStructureString();
                    this.processNamespaceAttribute(prefix, "");
                    if (collectPrefixes) {
                        prefixSet.add(prefix);
                        break;
                    }
                    break;
                }
                case 3: {
                    final String prefix = this.readStructureString();
                    this.processNamespaceAttribute(prefix, this.readStructureString());
                    if (collectPrefixes) {
                        prefixSet.add(prefix);
                        break;
                    }
                    break;
                }
                case 4: {
                    this.processNamespaceAttribute("", this.readStructureString());
                    if (collectPrefixes) {
                        prefixSet.add("");
                        break;
                    }
                    break;
                }
                default: {
                    throw this.reportFatalError("Illegal state: " + item);
                }
            }
            this.readStructure();
            item = this.peekStructure();
        } while ((item & 0xF0) == 0x40);
        this.cacheNamespacePrefixIndex();
        return item;
    }
    
    private void processAttributes(int item) throws SAXException {
        do {
            switch (AbstractProcessor.getAIIState(item)) {
                case 1: {
                    this._attributes.addAttributeWithQName(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 2: {
                    final String p = this.readStructureString();
                    final String u = this.readStructureString();
                    final String ln = this.readStructureString();
                    this._attributes.addAttributeWithQName(u, ln, this.getQName(p, ln), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    final String u2 = this.readStructureString();
                    final String ln2 = this.readStructureString();
                    this._attributes.addAttributeWithQName(u2, ln2, ln2, this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    final String ln3 = this.readStructureString();
                    this._attributes.addAttributeWithQName("", ln3, ln3, this.readStructureString(), this.readContentString());
                    break;
                }
                default: {
                    throw this.reportFatalError("Illegal state: " + item);
                }
            }
            this.readStructure();
            item = this.peekStructure();
        } while ((item & 0xF0) == 0x30);
    }
    
    private void processNamespaceAttribute(final String prefix, final String uri) throws SAXException {
        this._contentHandler.startPrefixMapping(prefix, uri);
        if (this._namespacePrefixesFeature) {
            if (prefix != "") {
                this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", prefix, this.getQName("xmlns", prefix), "CDATA", uri);
            }
            else {
                this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", uri);
            }
        }
        this.cacheNamespacePrefix(prefix);
    }
    
    private void cacheNamespacePrefix(final String prefix) {
        if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
            final String[] namespaceAttributes = new String[this._namespacePrefixesIndex * 3 / 2 + 1];
            System.arraycopy(this._namespacePrefixes, 0, namespaceAttributes, 0, this._namespacePrefixesIndex);
            this._namespacePrefixes = namespaceAttributes;
        }
        this._namespacePrefixes[this._namespacePrefixesIndex++] = prefix;
    }
    
    private void cacheNamespacePrefixIndex() {
        if (this._namespaceAttributesStackIndex == this._namespaceAttributesStack.length) {
            final int[] namespaceAttributesStack = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
            System.arraycopy(this._namespaceAttributesStack, 0, namespaceAttributesStack, 0, this._namespaceAttributesStackIndex);
            this._namespaceAttributesStack = namespaceAttributesStack;
        }
        this._namespaceAttributesStack[this._namespaceAttributesStackIndex++] = this._namespacePrefixesIndex;
    }
    
    private void cacheNamespacePrefixStartingIndex() {
        if (this._namespaceAttributesStackIndex == this._namespaceAttributesStartingStack.length) {
            final int[] namespaceAttributesStart = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
            System.arraycopy(this._namespaceAttributesStartingStack, 0, namespaceAttributesStart, 0, this._namespaceAttributesStackIndex);
            this._namespaceAttributesStartingStack = namespaceAttributesStart;
        }
        this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] = this._namespacePrefixesIndex;
    }
    
    private void processComment(final String s) throws SAXException {
        this.processComment(s.toCharArray(), 0, s.length());
    }
    
    private void processComment(final char[] ch, final int start, final int length) throws SAXException {
        this._lexicalHandler.comment(ch, start, length);
    }
    
    private void processProcessingInstruction(final String target, final String data) throws SAXException {
        this._contentHandler.processingInstruction(target, data);
    }
    
    static {
        DEFAULT_LEXICAL_HANDLER = new DefaultWithLexicalHandler();
    }
}
