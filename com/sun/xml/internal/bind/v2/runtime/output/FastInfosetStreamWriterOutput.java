package com.sun.xml.internal.bind.v2.runtime.output;

import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import java.util.Map;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;

public final class FastInfosetStreamWriterOutput extends XMLStreamWriterOutput
{
    private final StAXDocumentSerializer fiout;
    private final Encoded[] localNames;
    private final TablesPerJAXBContext tables;
    
    public FastInfosetStreamWriterOutput(final StAXDocumentSerializer out, final JAXBContextImpl context) {
        super(out, NoEscapeHandler.theInstance);
        this.fiout = out;
        this.localNames = context.getUTF8NameTable();
        final VocabularyApplicationData vocabAppData = this.fiout.getVocabularyApplicationData();
        AppData appData = null;
        if (vocabAppData == null || !(vocabAppData instanceof AppData)) {
            appData = new AppData();
            this.fiout.setVocabularyApplicationData(appData);
        }
        else {
            appData = (AppData)vocabAppData;
        }
        final TablesPerJAXBContext tablesPerContext = appData.contexts.get(context);
        if (tablesPerContext != null) {
            (this.tables = tablesPerContext).clearOrResetTables(out.getLocalNameIndex());
        }
        else {
            this.tables = new TablesPerJAXBContext(context, out.getLocalNameIndex());
            appData.contexts.put(context, this.tables);
        }
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        if (fragment) {
            this.fiout.initiateLowLevelWriting();
        }
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        super.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final Name name) throws IOException {
        this.fiout.writeLowLevelTerminationAndMark();
        if (this.nsContext.getCurrent().count() == 0) {
            final int qNameIndex = this.tables.elementIndexes[name.qNameIndex] - this.tables.indexOffset;
            final int prefixIndex = this.nsUriIndex2prefixIndex[name.nsUriIndex];
            if (qNameIndex >= 0 && this.tables.elementIndexPrefixes[name.qNameIndex] == prefixIndex) {
                this.fiout.writeLowLevelStartElementIndexed(0, qNameIndex);
            }
            else {
                this.tables.elementIndexes[name.qNameIndex] = this.fiout.getNextElementIndex() + this.tables.indexOffset;
                this.tables.elementIndexPrefixes[name.qNameIndex] = prefixIndex;
                this.writeLiteral(60, name, this.nsContext.getPrefix(prefixIndex), this.nsContext.getNamespaceURI(prefixIndex));
            }
        }
        else {
            this.beginStartTagWithNamespaces(name);
        }
    }
    
    public void beginStartTagWithNamespaces(final Name name) throws IOException {
        final NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
        this.fiout.writeLowLevelStartNamespaces();
        for (int i = nse.count() - 1; i >= 0; --i) {
            final String uri = nse.getNsUri(i);
            if (uri.length() != 0 || nse.getBase() != 1) {
                this.fiout.writeLowLevelNamespace(nse.getPrefix(i), uri);
            }
        }
        this.fiout.writeLowLevelEndNamespaces();
        final int qNameIndex = this.tables.elementIndexes[name.qNameIndex] - this.tables.indexOffset;
        final int prefixIndex = this.nsUriIndex2prefixIndex[name.nsUriIndex];
        if (qNameIndex >= 0 && this.tables.elementIndexPrefixes[name.qNameIndex] == prefixIndex) {
            this.fiout.writeLowLevelStartElementIndexed(0, qNameIndex);
        }
        else {
            this.tables.elementIndexes[name.qNameIndex] = this.fiout.getNextElementIndex() + this.tables.indexOffset;
            this.tables.elementIndexPrefixes[name.qNameIndex] = prefixIndex;
            this.writeLiteral(60, name, this.nsContext.getPrefix(prefixIndex), this.nsContext.getNamespaceURI(prefixIndex));
        }
    }
    
    @Override
    public void attribute(final Name name, final String value) throws IOException {
        this.fiout.writeLowLevelStartAttributes();
        final int qNameIndex = this.tables.attributeIndexes[name.qNameIndex] - this.tables.indexOffset;
        if (qNameIndex >= 0) {
            this.fiout.writeLowLevelAttributeIndexed(qNameIndex);
        }
        else {
            this.tables.attributeIndexes[name.qNameIndex] = this.fiout.getNextAttributeIndex() + this.tables.indexOffset;
            final int namespaceURIId = name.nsUriIndex;
            if (namespaceURIId == -1) {
                this.writeLiteral(120, name, "", "");
            }
            else {
                final int prefix = this.nsUriIndex2prefixIndex[namespaceURIId];
                this.writeLiteral(120, name, this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix));
            }
        }
        this.fiout.writeLowLevelAttributeValue(value);
    }
    
    private void writeLiteral(final int type, final Name name, final String prefix, final String namespaceURI) throws IOException {
        final int localNameIndex = this.tables.localNameIndexes[name.localNameIndex] - this.tables.indexOffset;
        if (localNameIndex < 0) {
            this.tables.localNameIndexes[name.localNameIndex] = this.fiout.getNextLocalNameIndex() + this.tables.indexOffset;
            this.fiout.writeLowLevelStartNameLiteral(type, prefix, this.localNames[name.localNameIndex].buf, namespaceURI);
        }
        else {
            this.fiout.writeLowLevelStartNameLiteral(type, prefix, localNameIndex, namespaceURI);
        }
    }
    
    @Override
    public void endStartTag() throws IOException {
        this.fiout.writeLowLevelEndStartElement();
    }
    
    @Override
    public void endTag(final Name name) throws IOException {
        this.fiout.writeLowLevelEndElement();
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException {
        this.fiout.writeLowLevelEndElement();
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException {
        if (needsSeparatingWhitespace) {
            this.fiout.writeLowLevelText(" ");
        }
        if (!(value instanceof Base64Data)) {
            final int len = value.length();
            if (len < this.buf.length) {
                value.writeTo(this.buf, 0);
                this.fiout.writeLowLevelText(this.buf, len);
            }
            else {
                this.fiout.writeLowLevelText(value.toString());
            }
        }
        else {
            final Base64Data dataValue = (Base64Data)value;
            this.fiout.writeLowLevelOctets(dataValue.get(), dataValue.getDataLen());
        }
    }
    
    @Override
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException {
        if (needsSeparatingWhitespace) {
            this.fiout.writeLowLevelText(" ");
        }
        this.fiout.writeLowLevelText(value);
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException {
        this.fiout.writeLowLevelTerminationAndMark();
        int type = 0;
        if (this.nsContext.getCurrent().count() > 0) {
            final NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
            this.fiout.writeLowLevelStartNamespaces();
            for (int i = nse.count() - 1; i >= 0; --i) {
                final String uri = nse.getNsUri(i);
                if (uri.length() != 0 || nse.getBase() != 1) {
                    this.fiout.writeLowLevelNamespace(nse.getPrefix(i), uri);
                }
            }
            this.fiout.writeLowLevelEndNamespaces();
            type = 0;
        }
        final boolean isIndexed = this.fiout.writeLowLevelStartElement(type, this.nsContext.getPrefix(prefix), localName, this.nsContext.getNamespaceURI(prefix));
        if (!isIndexed) {
            this.tables.incrementMaxIndexValue();
        }
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException {
        this.fiout.writeLowLevelStartAttributes();
        boolean isIndexed;
        if (prefix == -1) {
            isIndexed = this.fiout.writeLowLevelAttribute("", "", localName);
        }
        else {
            isIndexed = this.fiout.writeLowLevelAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName);
        }
        if (!isIndexed) {
            this.tables.incrementMaxIndexValue();
        }
        this.fiout.writeLowLevelAttributeValue(value);
    }
    
    static final class TablesPerJAXBContext
    {
        final int[] elementIndexes;
        final int[] elementIndexPrefixes;
        final int[] attributeIndexes;
        final int[] localNameIndexes;
        int indexOffset;
        int maxIndex;
        boolean requiresClear;
        
        TablesPerJAXBContext(final JAXBContextImpl context, final int initialIndexOffset) {
            this.elementIndexes = new int[context.getNumberOfElementNames()];
            this.elementIndexPrefixes = new int[context.getNumberOfElementNames()];
            this.attributeIndexes = new int[context.getNumberOfAttributeNames()];
            this.localNameIndexes = new int[context.getNumberOfLocalNames()];
            this.indexOffset = 1;
            this.maxIndex = initialIndexOffset + this.elementIndexes.length + this.attributeIndexes.length;
        }
        
        public void requireClearTables() {
            this.requiresClear = true;
        }
        
        public void clearOrResetTables(final int intialIndexOffset) {
            if (this.requiresClear) {
                this.requiresClear = false;
                this.indexOffset += this.maxIndex;
                this.maxIndex = intialIndexOffset + this.elementIndexes.length + this.attributeIndexes.length;
                if (this.indexOffset + this.maxIndex < 0) {
                    this.clearAll();
                }
            }
            else {
                this.maxIndex = intialIndexOffset + this.elementIndexes.length + this.attributeIndexes.length;
                if (this.indexOffset + this.maxIndex < 0) {
                    this.resetAll();
                }
            }
        }
        
        private void clearAll() {
            this.clear(this.elementIndexes);
            this.clear(this.attributeIndexes);
            this.clear(this.localNameIndexes);
            this.indexOffset = 1;
        }
        
        private void clear(final int[] array) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = 0;
            }
        }
        
        public void incrementMaxIndexValue() {
            ++this.maxIndex;
            if (this.indexOffset + this.maxIndex < 0) {
                this.resetAll();
            }
        }
        
        private void resetAll() {
            this.clear(this.elementIndexes);
            this.clear(this.attributeIndexes);
            this.clear(this.localNameIndexes);
            this.indexOffset = 1;
        }
        
        private void reset(final int[] array) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] > this.indexOffset) {
                    array[i] = array[i] - this.indexOffset + 1;
                }
                else {
                    array[i] = 0;
                }
            }
        }
    }
    
    static final class AppData implements VocabularyApplicationData
    {
        final Map<JAXBContext, TablesPerJAXBContext> contexts;
        final Collection<TablesPerJAXBContext> collectionOfContexts;
        
        AppData() {
            this.contexts = new WeakHashMap<JAXBContext, TablesPerJAXBContext>();
            this.collectionOfContexts = this.contexts.values();
        }
        
        @Override
        public void clear() {
            for (final TablesPerJAXBContext c : this.collectionOfContexts) {
                c.requireClearTables();
            }
        }
    }
}
