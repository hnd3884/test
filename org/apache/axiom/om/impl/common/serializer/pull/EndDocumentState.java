package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import java.util.Collections;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.ext.stax.DTDReader;

final class EndDocumentState extends PullSerializerState
{
    static final EndDocumentState INSTANCE;
    
    static {
        INSTANCE = new EndDocumentState();
    }
    
    private EndDocumentState() {
    }
    
    @Override
    DTDReader getDTDReader() {
        return (DTDReader)NullDTDReader.INSTANCE;
    }
    
    @Override
    DataHandlerReader getDataHandlerReader() {
        return (DataHandlerReader)NullDataHandlerReader.INSTANCE;
    }
    
    @Override
    CharacterDataReader getCharacterDataReader() {
        return (CharacterDataReader)NullCharacterDataReader.INSTANCE;
    }
    
    @Override
    int getEventType() {
        return 8;
    }
    
    @Override
    boolean hasNext() throws XMLStreamException {
        return false;
    }
    
    @Override
    void next() throws XMLStreamException {
        throw new NoSuchElementException("End of the document reached");
    }
    
    @Override
    int nextTag() throws XMLStreamException {
        throw new IllegalStateException();
    }
    
    @Override
    Object getProperty(final String name) throws IllegalArgumentException {
        return null;
    }
    
    @Override
    String getVersion() {
        return null;
    }
    
    @Override
    String getCharacterEncodingScheme() {
        throw new IllegalStateException();
    }
    
    @Override
    String getEncoding() {
        throw new IllegalStateException();
    }
    
    @Override
    boolean isStandalone() {
        throw new IllegalStateException();
    }
    
    @Override
    boolean standaloneSet() {
        throw new IllegalStateException();
    }
    
    @Override
    String getPrefix() {
        throw new IllegalStateException();
    }
    
    @Override
    String getNamespaceURI() {
        throw new IllegalStateException();
    }
    
    @Override
    String getLocalName() {
        throw new IllegalStateException();
    }
    
    @Override
    QName getName() {
        throw new IllegalStateException();
    }
    
    @Override
    int getNamespaceCount() {
        throw new IllegalStateException();
    }
    
    @Override
    String getNamespacePrefix(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getNamespaceURI(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    int getAttributeCount() {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributePrefix(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributeNamespace(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributeLocalName(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    QName getAttributeName(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    boolean isAttributeSpecified(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributeType(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributeValue(final int index) {
        throw new IllegalStateException();
    }
    
    @Override
    String getAttributeValue(final String namespaceURI, final String localName) {
        throw new IllegalStateException();
    }
    
    @Override
    NamespaceContext getNamespaceContext() {
        return (NamespaceContext)new MapBasedNamespaceContext(Collections.EMPTY_MAP);
    }
    
    @Override
    String getNamespaceURI(final String prefix) {
        return null;
    }
    
    @Override
    String getElementText() throws XMLStreamException {
        throw new IllegalStateException();
    }
    
    @Override
    String getText() {
        throw new IllegalStateException();
    }
    
    @Override
    char[] getTextCharacters() {
        throw new IllegalStateException();
    }
    
    @Override
    int getTextStart() {
        throw new IllegalStateException();
    }
    
    @Override
    int getTextLength() {
        throw new IllegalStateException();
    }
    
    @Override
    int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        throw new IllegalStateException();
    }
    
    @Override
    Boolean isWhiteSpace() {
        return Boolean.FALSE;
    }
    
    @Override
    String getPIData() {
        throw new IllegalStateException();
    }
    
    @Override
    String getPITarget() {
        throw new IllegalStateException();
    }
    
    @Override
    OMDataSource getDataSource() {
        return null;
    }
    
    @Override
    void released() throws XMLStreamException {
    }
    
    @Override
    void restored() throws XMLStreamException {
    }
}
