package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.om.OMDataSource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.ext.stax.DTDReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;

abstract class AbstractWrapper extends PullSerializerState implements CharacterDataReader
{
    protected final XMLStreamReader reader;
    private final PullSerializer serializer;
    private int depth;
    private DTDReader dtdReader;
    private DataHandlerReader dataHandlerReader;
    private CharacterDataReader characterDataReader;
    
    AbstractWrapper(final PullSerializer serializer, final XMLStreamReader reader, final int startDepth) {
        this.reader = reader;
        this.serializer = serializer;
        this.depth = startDepth;
        if (reader.getEventType() == 2) {
            --this.depth;
        }
    }
    
    @Override
    final DTDReader getDTDReader() {
        if (this.dtdReader == null) {
            try {
                this.dtdReader = (DTDReader)this.reader.getProperty(DTDReader.PROPERTY);
            }
            catch (final IllegalArgumentException ex) {}
            if (this.dtdReader == null) {
                this.dtdReader = (DTDReader)NullDTDReader.INSTANCE;
            }
        }
        return this.dtdReader;
    }
    
    @Override
    final DataHandlerReader getDataHandlerReader() {
        if (this.dataHandlerReader == null) {
            this.dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(this.reader);
            if (this.dataHandlerReader == null) {
                this.dataHandlerReader = (DataHandlerReader)NullDataHandlerReader.INSTANCE;
            }
        }
        return this.dataHandlerReader;
    }
    
    @Override
    final CharacterDataReader getCharacterDataReader() {
        if (this.characterDataReader == null) {
            try {
                this.characterDataReader = (CharacterDataReader)this.reader.getProperty(CharacterDataReader.PROPERTY);
            }
            catch (final IllegalArgumentException ex) {}
            if (this.characterDataReader == null) {
                this.characterDataReader = (CharacterDataReader)this;
            }
        }
        return this.characterDataReader;
    }
    
    public final void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        writer.write(this.reader.getText());
    }
    
    @Override
    final int getEventType() {
        return this.reader.getEventType();
    }
    
    @Override
    final boolean hasNext() throws XMLStreamException {
        return this.reader.hasNext();
    }
    
    @Override
    final void next() throws XMLStreamException {
        if (!this.doNext()) {
            this.serializer.popState();
        }
    }
    
    final boolean doNext() throws XMLStreamException {
        if (this.reader.getEventType() == 1) {
            ++this.depth;
        }
        if (this.depth == 0) {
            return false;
        }
        if (this.reader.next() == 2) {
            --this.depth;
        }
        return true;
    }
    
    @Override
    final int nextTag() throws XMLStreamException {
        final int result = this.reader.nextTag();
        switch (result) {
            case 1: {
                ++this.depth;
                break;
            }
            case 2: {
                --this.depth;
                break;
            }
        }
        return result;
    }
    
    @Override
    final Object getProperty(final String name) throws IllegalArgumentException {
        return this.reader.getProperty(name);
    }
    
    @Override
    final String getVersion() {
        return this.reader.getVersion();
    }
    
    @Override
    final String getCharacterEncodingScheme() {
        return this.reader.getCharacterEncodingScheme();
    }
    
    @Override
    final String getEncoding() {
        return this.reader.getEncoding();
    }
    
    @Override
    final boolean isStandalone() {
        return this.reader.isStandalone();
    }
    
    @Override
    final boolean standaloneSet() {
        return this.reader.standaloneSet();
    }
    
    @Override
    final String getPrefix() {
        return this.reader.getPrefix();
    }
    
    @Override
    final String getNamespaceURI() {
        return this.reader.getNamespaceURI();
    }
    
    @Override
    final String getLocalName() {
        return this.reader.getLocalName();
    }
    
    @Override
    final QName getName() {
        return this.reader.getName();
    }
    
    @Override
    final int getNamespaceCount() {
        return this.reader.getNamespaceCount();
    }
    
    @Override
    final String getNamespacePrefix(final int index) {
        return this.reader.getNamespacePrefix(index);
    }
    
    @Override
    final String getNamespaceURI(final int index) {
        return this.reader.getNamespaceURI(index);
    }
    
    @Override
    final int getAttributeCount() {
        return this.reader.getAttributeCount();
    }
    
    @Override
    final String getAttributePrefix(final int index) {
        return this.reader.getAttributePrefix(index);
    }
    
    @Override
    final String getAttributeNamespace(final int index) {
        return this.reader.getAttributeNamespace(index);
    }
    
    @Override
    final String getAttributeLocalName(final int index) {
        return this.reader.getAttributeLocalName(index);
    }
    
    @Override
    final QName getAttributeName(final int index) {
        return this.reader.getAttributeName(index);
    }
    
    @Override
    final boolean isAttributeSpecified(final int index) {
        return this.reader.isAttributeSpecified(index);
    }
    
    @Override
    final String getAttributeType(final int index) {
        return this.reader.getAttributeType(index);
    }
    
    @Override
    final String getAttributeValue(final int index) {
        return this.reader.getAttributeValue(index);
    }
    
    @Override
    final String getAttributeValue(final String namespaceURI, final String localName) {
        return this.reader.getAttributeValue(namespaceURI, localName);
    }
    
    @Override
    final NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
    
    @Override
    final String getNamespaceURI(final String prefix) {
        return this.reader.getNamespaceURI(prefix);
    }
    
    @Override
    final String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }
    
    @Override
    final String getText() {
        return this.reader.getText();
    }
    
    @Override
    final char[] getTextCharacters() {
        return this.reader.getTextCharacters();
    }
    
    @Override
    final int getTextStart() {
        return this.reader.getTextStart();
    }
    
    @Override
    final int getTextLength() {
        return this.reader.getTextLength();
    }
    
    @Override
    final int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    @Override
    final Boolean isWhiteSpace() {
        return this.reader.isWhiteSpace();
    }
    
    @Override
    final String getPIData() {
        return this.reader.getPIData();
    }
    
    @Override
    final String getPITarget() {
        return this.reader.getPITarget();
    }
    
    @Override
    final OMDataSource getDataSource() {
        return null;
    }
    
    @Override
    final void restored() {
    }
    
    public String toString() {
        return String.valueOf(super.toString()) + "[reader=" + this.reader + "]";
    }
}
