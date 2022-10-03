package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.om.OMDataSource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.ext.stax.DTDReader;

final class ClosedState extends PullSerializerState
{
    static final ClosedState INSTANCE;
    
    static {
        INSTANCE = new ClosedState();
    }
    
    private ClosedState() {
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
    Object getProperty(final String name) throws IllegalArgumentException {
        return null;
    }
    
    @Override
    int getAttributeCount() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributeLocalName(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    QName getAttributeName(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributeNamespace(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributePrefix(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributeType(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributeValue(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getAttributeValue(final String namespaceURI, final String localName) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getCharacterEncodingScheme() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getElementText() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getEncoding() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int getEventType() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getLocalName() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    QName getName() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    NamespaceContext getNamespaceContext() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int getNamespaceCount() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getNamespacePrefix(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getNamespaceURI() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getNamespaceURI(final String prefix) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getNamespaceURI(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getPIData() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getPITarget() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getPrefix() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getText() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    char[] getTextCharacters() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int getTextLength() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int getTextStart() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    String getVersion() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    boolean hasNext() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    boolean isAttributeSpecified(final int index) {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    boolean isStandalone() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    Boolean isWhiteSpace() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    void next() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    int nextTag() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }
    
    public boolean standaloneSet() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    OMDataSource getDataSource() {
        throw new IllegalStateException("Reader already closed");
    }
    
    @Override
    void released() throws XMLStreamException {
    }
    
    @Override
    void restored() {
    }
}
