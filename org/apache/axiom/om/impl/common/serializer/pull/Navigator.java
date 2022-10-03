package org.apache.axiom.om.impl.common.serializer.pull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collections;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMProcessingInstruction;
import java.util.Map;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.om.OMSerializable;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.common.util.OMDataSourceUtil;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreElement;
import java.io.IOException;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMText;
import java.io.Writer;
import org.apache.axiom.core.CoreCharacterDataContainer;
import org.apache.axiom.om.OMDocType;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMSourcedElement;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.core.CoreDocument;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreNode;
import org.apache.commons.logging.Log;
import javax.xml.stream.XMLStreamConstants;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;

final class Navigator extends PullSerializerState implements DataHandlerReader, CharacterDataReader, DTDReader, XMLStreamConstants
{
    private static final Log log;
    private final PullSerializer serializer;
    private CoreNode node;
    private boolean visited;
    private final CoreParentNode rootNode;
    private int currentEvent;
    private final boolean cache;
    private final boolean preserveNamespaceContext;
    private int attributeCount;
    private CoreNSAwareAttribute[] attributes;
    private int namespaceCount;
    private CoreNamespaceDeclaration[] namespaces;
    private OMDataSource ds;
    
    static {
        log = LogFactory.getLog((Class)Navigator.class);
    }
    
    Navigator(final PullSerializer serializer, final CoreParentNode startNode, final boolean cache, final boolean preserveNamespaceContext) {
        this.attributeCount = -1;
        this.attributes = new CoreNSAwareAttribute[16];
        this.namespaceCount = -1;
        this.namespaces = new CoreNamespaceDeclaration[16];
        this.serializer = serializer;
        this.rootNode = startNode;
        this.cache = cache;
        this.preserveNamespaceContext = preserveNamespaceContext;
        if (startNode instanceof CoreDocument) {
            this.node = startNode;
        }
        this.currentEvent = 7;
    }
    
    @Override
    DTDReader getDTDReader() {
        return (DTDReader)this;
    }
    
    @Override
    DataHandlerReader getDataHandlerReader() {
        return (DataHandlerReader)this;
    }
    
    @Override
    CharacterDataReader getCharacterDataReader() {
        return (CharacterDataReader)this;
    }
    
    @Override
    String getPrefix() {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            return ((OMElement)this.node).getPrefix();
        }
        throw new IllegalStateException();
    }
    
    @Override
    String getNamespaceURI() {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            return ((OMElement)this.node).getNamespaceURI();
        }
        throw new IllegalStateException();
    }
    
    @Override
    String getLocalName() {
        switch (this.currentEvent) {
            case 1:
            case 2: {
                return ((OMElement)this.node).getLocalName();
            }
            case 9: {
                return ((OMEntityReference)this.node).getName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    QName getName() {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            if (this.node instanceof OMSourcedElement) {
                ((OMElement)this.node).getFirstOMChild();
            }
            return ((OMElement)this.node).getQName();
        }
        throw new IllegalStateException();
    }
    
    @Override
    int getTextLength() {
        return this.getTextFromNode().length();
    }
    
    @Override
    int getTextStart() {
        switch (this.currentEvent) {
            case 4:
            case 5:
            case 6:
            case 12: {
                return 0;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        final String text = this.getTextFromNode();
        final int copied = Math.min(length, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }
    
    @Override
    char[] getTextCharacters() {
        return this.getTextFromNode().toCharArray();
    }
    
    @Override
    String getText() {
        switch (this.currentEvent) {
            case 11: {
                final String internalSubset = ((OMDocType)this.node).getInternalSubset();
                return (internalSubset != null) ? internalSubset : "";
            }
            case 9: {
                return ((OMEntityReference)this.node).getReplacementText();
            }
            default: {
                return this.getTextFromNode();
            }
        }
    }
    
    private String getTextFromNode() {
        switch (this.currentEvent) {
            case 4:
            case 5:
            case 6:
            case 12: {
                return ((CoreCharacterDataContainer)this.node).coreGetCharacterData().toString();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        switch (this.currentEvent) {
            case 4:
            case 6:
            case 12: {
                final OMText text = (OMText)this.node;
                if (text.isCharacters()) {
                    writer.write(text.getTextCharacters());
                    break;
                }
                writer.write(text.getText());
                break;
            }
            case 5: {
                writer.write(((OMComment)this.node).getValue());
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    int getEventType() {
        return this.currentEvent;
    }
    
    private void loadAttributes() {
        if (this.attributeCount == -1) {
            this.attributeCount = 0;
            for (CoreAttribute attr = ((CoreElement)this.node).coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
                if (attr instanceof CoreNSAwareAttribute) {
                    if (this.attributeCount == this.attributes.length) {
                        final CoreNSAwareAttribute[] newAttributes = new CoreNSAwareAttribute[this.attributes.length * 2];
                        System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
                        this.attributes = newAttributes;
                    }
                    this.attributes[this.attributeCount] = (CoreNSAwareAttribute)attr;
                    ++this.attributeCount;
                }
            }
        }
    }
    
    private CoreNSAwareAttribute getAttribute(final int index) {
        this.loadAttributes();
        return this.attributes[index];
    }
    
    private void loadNamespaces() {
        if (this.namespaceCount == -1) {
            this.namespaceCount = 0;
            for (CoreAttribute attr = ((CoreElement)this.node).coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
                if (attr instanceof CoreNamespaceDeclaration) {
                    this.addNamespace((CoreNamespaceDeclaration)attr);
                }
            }
            if (this.preserveNamespaceContext && this.node == this.rootNode) {
                CoreElement element = (CoreElement)this.node;
                while (true) {
                    final CoreParentNode parent = element.coreGetParent();
                    if (!(parent instanceof CoreElement)) {
                        break;
                    }
                    element = (CoreElement)parent;
                    for (CoreAttribute attr = element.coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
                        if (attr instanceof CoreNamespaceDeclaration) {
                            final CoreNamespaceDeclaration ns = (CoreNamespaceDeclaration)attr;
                            final String prefix = ns.coreGetDeclaredPrefix();
                            boolean masked = false;
                            for (int i = 0; i < this.namespaceCount; ++i) {
                                if (this.namespaces[i].coreGetDeclaredPrefix().equals(prefix)) {
                                    masked = true;
                                    break;
                                }
                            }
                            if (!masked) {
                                this.addNamespace(ns);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private CoreNamespaceDeclaration getNamespace(final int index) {
        this.loadNamespaces();
        return this.namespaces[index];
    }
    
    private void addNamespace(final CoreNamespaceDeclaration ns) {
        if (!"xml".equals(ns.coreGetDeclaredPrefix())) {
            if (this.namespaceCount == this.namespaces.length) {
                final CoreNamespaceDeclaration[] newNamespaces = new CoreNamespaceDeclaration[this.namespaces.length * 2];
                System.arraycopy(this.namespaces, 0, newNamespaces, 0, this.namespaces.length);
                this.namespaces = newNamespaces;
            }
            this.namespaces[this.namespaceCount] = ns;
            ++this.namespaceCount;
        }
    }
    
    @Override
    String getNamespaceURI(final int i) {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            return this.getNamespace(i).coreGetCharacterData().toString();
        }
        throw new IllegalStateException();
    }
    
    @Override
    String getNamespacePrefix(final int i) {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            final String prefix = this.getNamespace(i).coreGetDeclaredPrefix();
            return (prefix.length() == 0) ? null : prefix;
        }
        throw new IllegalStateException();
    }
    
    @Override
    int getNamespaceCount() {
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            this.loadNamespaces();
            return this.namespaceCount;
        }
        throw new IllegalStateException();
    }
    
    @Override
    boolean isAttributeSpecified(final int i) {
        if (this.currentEvent == 1) {
            return true;
        }
        throw new IllegalStateException("attribute type accessed in illegal event!");
    }
    
    @Override
    String getAttributeValue(final int i) {
        if (this.currentEvent == 1) {
            return this.getAttribute(i).coreGetCharacterData().toString();
        }
        throw new IllegalStateException("attribute type accessed in illegal event!");
    }
    
    @Override
    String getAttributeType(final int i) {
        if (this.currentEvent == 1) {
            return this.getAttribute(i).coreGetType();
        }
        throw new IllegalStateException("attribute type accessed in illegal event!");
    }
    
    @Override
    String getAttributePrefix(final int i) {
        if (this.currentEvent == 1) {
            final String prefix = this.getAttribute(i).coreGetPrefix();
            return (prefix.length() == 0) ? null : prefix;
        }
        throw new IllegalStateException("attribute prefix accessed in illegal event!");
    }
    
    @Override
    String getAttributeLocalName(final int i) {
        if (this.currentEvent == 1) {
            return this.getAttribute(i).coreGetLocalName();
        }
        throw new IllegalStateException("attribute localName accessed in illegal event!");
    }
    
    @Override
    String getAttributeNamespace(final int i) {
        if (this.currentEvent == 1) {
            final String namespaceURI = this.getAttribute(i).coreGetNamespaceURI();
            return (namespaceURI.length() == 0) ? null : namespaceURI;
        }
        throw new IllegalStateException("attribute nameSpace accessed in illegal event!");
    }
    
    @Override
    QName getAttributeName(final int i) {
        if (this.currentEvent == 1) {
            return ((OMAttribute)this.getAttribute(i)).getQName();
        }
        throw new IllegalStateException("attribute count accessed in illegal event!");
    }
    
    @Override
    int getAttributeCount() {
        if (this.currentEvent == 1) {
            this.loadAttributes();
            return this.attributeCount;
        }
        throw new IllegalStateException("attribute count accessed in illegal event (" + this.currentEvent + ")!");
    }
    
    @Override
    String getAttributeValue(final String s, final String s1) {
        if (this.currentEvent == 1) {
            final QName qname = new QName(s, s1);
            final OMAttribute attr = ((OMElement)this.node).getAttribute(qname);
            return (attr == null) ? null : attr.getAttributeValue();
        }
        throw new IllegalStateException("attribute type accessed in illegal event!");
    }
    
    @Override
    Boolean isWhiteSpace() {
        return null;
    }
    
    @Override
    String getNamespaceURI(final String prefix) {
        if ((this.currentEvent == 1 || this.currentEvent == 2) && this.node instanceof OMElement) {
            final OMNamespace namespaceURI = ((OMElement)this.node).findNamespaceURI(prefix);
            return (namespaceURI != null) ? namespaceURI.getNamespaceURI() : null;
        }
        return null;
    }
    
    @Override
    boolean hasNext() throws XMLStreamException {
        return true;
    }
    
    @Override
    String getElementText() throws XMLStreamException {
        return null;
    }
    
    private boolean nextNode() {
        if (this.node == null) {
            assert !this.visited;
            this.node = this.rootNode;
            return true;
        }
        else if (this.node instanceof OMContainer && !this.visited) {
            final CoreParentNode current = (CoreParentNode)this.node;
            final CoreChildNode firstChild = this.cache ? current.coreGetFirstChild() : current.coreGetFirstChildIfAvailable();
            if (firstChild != null) {
                this.node = firstChild;
                this.visited = false;
                return true;
            }
            return (current.getState() == 0 || current.getState() == 3) && (this.visited = true);
        }
        else {
            if (this.node == this.rootNode) {
                this.node = null;
                return this.visited = true;
            }
            final CoreChildNode current2 = (CoreChildNode)this.node;
            final CoreChildNode nextSibling = this.cache ? current2.coreGetNextSibling() : current2.coreGetNextSiblingIfAvailable();
            if (nextSibling != null) {
                this.node = nextSibling;
                this.visited = false;
                return true;
            }
            final CoreParentNode parent = current2.coreGetParent();
            this.node = parent;
            return (parent.getState() == 0 || parent.getState() == 3 || parent.getBuilder() == null) && (this.visited = true);
        }
    }
    
    @Override
    void next() throws XMLStreamException {
        if (this.nextNode()) {
            if (this.node instanceof OMSourcedElement) {
                final OMSourcedElement element = (OMSourcedElement)this.node;
                if (!element.isExpanded()) {
                    final OMDataSource ds = element.getDataSource();
                    if (ds != null) {
                        if (this.serializer.isDataSourceALeaf()) {
                            this.ds = ds;
                            this.currentEvent = -1;
                            this.visited = true;
                            return;
                        }
                        if (!OMDataSourceUtil.isPushDataSource(ds) && (!this.cache || !OMDataSourceUtil.isDestructiveRead(ds))) {
                            final XMLStreamReader reader = ds.getReader();
                            while (reader.next() != 1) {}
                            this.serializer.pushState(new IncludeWrapper(this.serializer, reader));
                            this.visited = true;
                            return;
                        }
                    }
                }
            }
            if (this.node == null || this.node instanceof OMDocument) {
                assert this.visited;
                this.serializer.switchState(EndDocumentState.INSTANCE);
            }
            else {
                if (this.node instanceof OMElement) {
                    this.currentEvent = (this.visited ? 2 : 1);
                }
                else {
                    this.currentEvent = ((OMNode)this.node).getType();
                }
                this.ds = null;
                this.attributeCount = -1;
                this.namespaceCount = -1;
            }
        }
        else {
            CoreParentNode container = (CoreParentNode)this.node;
            final StAXOMBuilder builder = (StAXOMBuilder)container.getBuilder();
            int depth;
            CoreParentNode parent;
            for (depth = 1; container != this.rootNode && container instanceof CoreElement; container = parent, ++depth) {
                parent = ((CoreElement)container).coreGetParent();
                if (parent.getBuilder() != builder) {
                    break;
                }
            }
            final XMLStreamReader reader2 = builder.disableCaching();
            if (Navigator.log.isDebugEnabled()) {
                Navigator.log.debug((Object)("Switching to pull-through mode; first event is " + XMLEventUtils.getEventTypeString(reader2.getEventType()) + "; depth is " + depth));
            }
            final PullThroughWrapper wrapper = new PullThroughWrapper(this.serializer, builder, (OMContainer)container, reader2, depth);
            this.serializer.pushState(wrapper);
            this.node = container;
            this.visited = true;
        }
    }
    
    @Override
    int nextTag() throws XMLStreamException {
        return -1;
    }
    
    @Override
    Object getProperty(final String s) throws IllegalArgumentException {
        CoreParentNode container;
        if (this.node == null) {
            container = this.rootNode;
        }
        else if (this.node instanceof OMContainer) {
            container = (CoreParentNode)this.node;
        }
        else {
            container = ((CoreChildNode)this.node).coreGetParent();
        }
        final OMXMLParserWrapper builder = container.getBuilder();
        if (builder != null && builder instanceof StAXBuilder) {
            final StAXBuilder staxBuilder = (StAXBuilder)builder;
            if (!staxBuilder.isClosed()) {
                try {
                    return ((StAXBuilder)builder).getReaderProperty(s);
                }
                catch (final IllegalStateException ex) {
                    return null;
                }
            }
        }
        return null;
    }
    
    @Override
    NamespaceContext getNamespaceContext() {
        return (NamespaceContext)new MapBasedNamespaceContext((Map)this.getAllNamespaces((OMSerializable)this.node));
    }
    
    @Override
    String getEncoding() {
        if (this.currentEvent != 7) {
            throw new IllegalStateException();
        }
        if (this.node instanceof OMDocument) {
            return ((OMDocument)this.node).getCharsetEncoding();
        }
        return null;
    }
    
    @Override
    String getVersion() {
        return "1.0";
    }
    
    @Override
    boolean isStandalone() {
        return true;
    }
    
    @Override
    boolean standaloneSet() {
        return false;
    }
    
    @Override
    String getCharacterEncodingScheme() {
        if (this.currentEvent != 7) {
            throw new IllegalStateException();
        }
        if (this.node instanceof OMDocument) {
            return ((OMDocument)this.node).getXMLEncoding();
        }
        return null;
    }
    
    @Override
    String getPITarget() {
        if (this.currentEvent == 3) {
            return ((OMProcessingInstruction)this.node).getTarget();
        }
        throw new IllegalStateException();
    }
    
    @Override
    String getPIData() {
        if (this.currentEvent == 3) {
            return ((OMProcessingInstruction)this.node).getValue();
        }
        throw new IllegalStateException();
    }
    
    public boolean isBinary() {
        return this.node instanceof OMText && ((OMText)this.node).isBinary();
    }
    
    public boolean isOptimized() {
        if (this.node instanceof OMText) {
            return ((OMText)this.node).isOptimized();
        }
        throw new IllegalStateException();
    }
    
    public boolean isDeferred() {
        if (this.node instanceof OMText) {
            return false;
        }
        throw new IllegalStateException();
    }
    
    public String getContentID() {
        if (this.node instanceof OMText) {
            return ((OMText)this.node).getContentID();
        }
        throw new IllegalStateException();
    }
    
    public DataHandler getDataHandler() throws XMLStreamException {
        if (this.node instanceof OMText) {
            return (DataHandler)((OMText)this.node).getDataHandler();
        }
        throw new IllegalStateException();
    }
    
    public DataHandlerProvider getDataHandlerProvider() {
        throw new IllegalStateException();
    }
    
    public String getRootName() {
        if (this.currentEvent == 11) {
            return ((OMDocType)this.node).getRootName();
        }
        throw new IllegalStateException();
    }
    
    public String getPublicId() {
        if (this.currentEvent == 11) {
            return ((OMDocType)this.node).getPublicId();
        }
        throw new IllegalStateException();
    }
    
    public String getSystemId() {
        if (this.currentEvent == 11) {
            return ((OMDocType)this.node).getSystemId();
        }
        throw new IllegalStateException();
    }
    
    private Map<String, String> getAllNamespaces(final OMSerializable contextNode) {
        if (contextNode == null) {
            return Collections.emptyMap();
        }
        OMContainer context;
        if (contextNode instanceof OMContainer) {
            context = (OMContainer)contextNode;
        }
        else {
            context = ((OMNode)contextNode).getParent();
        }
        final Map<String, String> nsMap = new LinkedHashMap<String, String>();
        while (context != null && !(context instanceof OMDocument)) {
            final OMElement element = (OMElement)context;
            Iterator it = element.getAllDeclaredNamespaces();
            while (it.hasNext()) {
                this.addNamespaceToMap(it.next(), nsMap);
            }
            if (element.getNamespace() != null) {
                this.addNamespaceToMap(element.getNamespace(), nsMap);
            }
            it = element.getAllAttributes();
            while (it.hasNext()) {
                final OMAttribute attr = it.next();
                if (attr.getNamespace() != null) {
                    this.addNamespaceToMap(attr.getNamespace(), nsMap);
                }
            }
            context = element.getParent();
        }
        return nsMap;
    }
    
    private void addNamespaceToMap(final OMNamespace ns, final Map<String, String> map) {
        if (map.get(ns.getPrefix()) == null) {
            map.put(ns.getPrefix(), ns.getNamespaceURI());
        }
    }
    
    @Override
    OMDataSource getDataSource() {
        if (Navigator.log.isDebugEnabled() && this.ds != null) {
            Navigator.log.debug((Object)("Exposed OMDataSource: " + this.ds));
        }
        return this.ds;
    }
    
    @Override
    void released() throws XMLStreamException {
    }
    
    @Override
    void restored() throws XMLStreamException {
        this.next();
    }
    
    public String toString() {
        return String.valueOf(super.toString()) + "[cache=" + this.cache + ",document=" + (this.rootNode instanceof OMDocument) + "]";
    }
}
