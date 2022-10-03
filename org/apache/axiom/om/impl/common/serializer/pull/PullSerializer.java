package org.apache.axiom.om.impl.common.serializer.pull;

import java.io.IOException;
import java.io.Writer;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.om.OMDataSource;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.core.CoreParentNode;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;

public final class PullSerializer extends AbstractXMLStreamReader implements DataHandlerReader, DTDReader, CharacterDataReader
{
    private static final Log log;
    private PullSerializerState state;
    private PullSerializerState savedState;
    private boolean isDataSourceALeaf;
    
    static {
        log = LogFactory.getLog((Class)PullSerializer.class);
    }
    
    public PullSerializer(final CoreParentNode startNode, final boolean cache, final boolean preserveNamespaceContext) {
        this.state = new Navigator(this, startNode, cache, preserveNamespaceContext);
        if (PullSerializer.log.isDebugEnabled()) {
            PullSerializer.log.debug((Object)("Pull serializer created; initial state is " + this.state));
        }
    }
    
    void switchState(final PullSerializerState newState) throws XMLStreamException {
        if (PullSerializer.log.isDebugEnabled()) {
            PullSerializer.log.debug((Object)("Switching to state " + newState));
        }
        this.internalSwitchState(newState);
    }
    
    private void internalSwitchState(final PullSerializerState newState) throws XMLStreamException {
        final PullSerializerState oldState = this.state;
        final PullSerializerState savedState = this.savedState;
        this.state = newState;
        this.savedState = null;
        if (savedState != null) {
            savedState.released();
        }
        oldState.released();
    }
    
    void pushState(final PullSerializerState newState) {
        if (this.savedState != null) {
            throw new IllegalStateException();
        }
        if (PullSerializer.log.isDebugEnabled()) {
            PullSerializer.log.debug((Object)("Switching to state " + newState));
        }
        this.savedState = this.state;
        this.state = newState;
    }
    
    void popState() throws XMLStreamException {
        final PullSerializerState savedState = this.savedState;
        if (savedState == null) {
            throw new IllegalStateException();
        }
        if (PullSerializer.log.isDebugEnabled()) {
            PullSerializer.log.debug((Object)("Restoring state " + savedState));
        }
        this.savedState = null;
        this.internalSwitchState(savedState);
        savedState.restored();
    }
    
    OMDataSource getDataSource() {
        return this.state.getDataSource();
    }
    
    void enableDataSourceEvents(final boolean value) {
        this.isDataSourceALeaf = true;
    }
    
    boolean isDataSourceALeaf() {
        return this.isDataSourceALeaf;
    }
    
    public int getEventType() {
        return this.state.getEventType();
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.state.hasNext();
    }
    
    public int next() throws XMLStreamException {
        this.state.next();
        return this.state.getEventType();
    }
    
    public int nextTag() throws XMLStreamException {
        final int eventType = this.state.nextTag();
        return (eventType == -1) ? super.nextTag() : eventType;
    }
    
    public void close() throws XMLStreamException {
        this.switchState(ClosedState.INSTANCE);
    }
    
    public Object getProperty(final String name) {
        final Object value = XMLStreamReaderUtils.processGetProperty((DataHandlerReader)this, name);
        if (value != null) {
            return value;
        }
        if (DTDReader.PROPERTY.equals(name) || CharacterDataReader.PROPERTY.equals(name)) {
            return this;
        }
        return this.state.getProperty(name);
    }
    
    public String getVersion() {
        return this.state.getVersion();
    }
    
    public String getCharacterEncodingScheme() {
        return this.state.getCharacterEncodingScheme();
    }
    
    public String getEncoding() {
        return this.state.getEncoding();
    }
    
    public boolean isStandalone() {
        return this.state.isStandalone();
    }
    
    public boolean standaloneSet() {
        return this.state.standaloneSet();
    }
    
    public String getPrefix() {
        return this.state.getPrefix();
    }
    
    public String getNamespaceURI() {
        return this.state.getNamespaceURI();
    }
    
    public String getLocalName() {
        return this.state.getLocalName();
    }
    
    public QName getName() {
        return this.state.getName();
    }
    
    public int getNamespaceCount() {
        return this.state.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int index) {
        return this.state.getNamespacePrefix(index);
    }
    
    public String getNamespaceURI(final int index) {
        return this.state.getNamespaceURI(index);
    }
    
    public int getAttributeCount() {
        return this.state.getAttributeCount();
    }
    
    public String getAttributePrefix(final int index) {
        return this.state.getAttributePrefix(index);
    }
    
    public String getAttributeNamespace(final int index) {
        return this.state.getAttributeNamespace(index);
    }
    
    public String getAttributeLocalName(final int index) {
        return this.state.getAttributeLocalName(index);
    }
    
    public QName getAttributeName(final int index) {
        return this.state.getAttributeName(index);
    }
    
    public boolean isAttributeSpecified(final int index) {
        return this.state.isAttributeSpecified(index);
    }
    
    public String getAttributeType(final int index) {
        return this.state.getAttributeType(index);
    }
    
    public String getAttributeValue(final int index) {
        return this.state.getAttributeValue(index);
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        return this.state.getAttributeValue(namespaceURI, localName);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.state.getNamespaceContext();
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.state.getNamespaceURI(prefix);
    }
    
    public String getElementText() throws XMLStreamException {
        final String text = this.state.getElementText();
        return (text == null) ? super.getElementText() : text;
    }
    
    public String getText() {
        return this.state.getText();
    }
    
    public char[] getTextCharacters() {
        return this.state.getTextCharacters();
    }
    
    public int getTextStart() {
        return this.state.getTextStart();
    }
    
    public int getTextLength() {
        return this.state.getTextLength();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.state.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    public boolean isWhiteSpace() {
        final Boolean isWhiteSpace = this.state.isWhiteSpace();
        return (isWhiteSpace == null) ? super.isWhiteSpace() : isWhiteSpace;
    }
    
    public String getPIData() {
        return this.state.getPIData();
    }
    
    public String getPITarget() {
        return this.state.getPITarget();
    }
    
    public boolean isBinary() {
        return this.state.getDataHandlerReader().isBinary();
    }
    
    public boolean isOptimized() {
        return this.state.getDataHandlerReader().isOptimized();
    }
    
    public boolean isDeferred() {
        return this.state.getDataHandlerReader().isDeferred();
    }
    
    public String getContentID() {
        return this.state.getDataHandlerReader().getContentID();
    }
    
    public DataHandler getDataHandler() throws XMLStreamException {
        return this.state.getDataHandlerReader().getDataHandler();
    }
    
    public DataHandlerProvider getDataHandlerProvider() {
        return this.state.getDataHandlerReader().getDataHandlerProvider();
    }
    
    public String getRootName() {
        return this.state.getDTDReader().getRootName();
    }
    
    public String getPublicId() {
        return this.state.getDTDReader().getPublicId();
    }
    
    public String getSystemId() {
        return this.state.getDTDReader().getSystemId();
    }
    
    public void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        this.state.getCharacterDataReader().writeTextTo(writer);
    }
}
