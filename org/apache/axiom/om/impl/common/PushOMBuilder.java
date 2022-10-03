package org.apache.axiom.om.impl.common;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMNode;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.OMXMLParserWrapper;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

public class PushOMBuilder extends AbstractXMLStreamWriter implements DataHandlerWriter
{
    private final AxiomSourcedElement root;
    private final OMFactoryEx factory;
    private OMElement parent;
    
    public PushOMBuilder(final AxiomSourcedElement root) throws XMLStreamException {
        this.root = root;
        this.factory = (OMFactoryEx)AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(root);
        final OMContainer parent = AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getParent(root);
        if (parent instanceof OMElement) {
            final Iterator it = ((OMElement)parent).getNamespacesInScope();
            while (it.hasNext()) {
                final OMNamespace ns = it.next();
                this.setPrefix(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        }
        throw new IllegalArgumentException("Unsupported property " + name);
    }
    
    protected void doWriteStartDocument() {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument()");
    }
    
    protected void doWriteStartDocument(final String encoding, final String version) {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String, String)");
    }
    
    protected void doWriteStartDocument(final String version) {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String)");
    }
    
    protected void doWriteEndDocument() {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEndDocument()");
    }
    
    protected void doWriteDTD(final String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }
    
    private OMNamespace getOMNamespace(String prefix, String namespaceURI, final boolean isDecl) {
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (!isDecl && namespaceURI.length() == 0) {
            return null;
        }
        if (this.parent != null) {
            final OMNamespace ns = this.parent.findNamespaceURI(prefix);
            if (ns != null && ns.getNamespaceURI().equals(namespaceURI)) {
                return ns;
            }
        }
        return this.factory.createOMNamespace(namespaceURI, prefix);
    }
    
    protected void doWriteStartElement(final String prefix, final String localName, final String namespaceURI) {
        final OMNamespace ns = this.getOMNamespace(prefix, namespaceURI, false);
        if (this.parent == null) {
            AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$validateName(this.root, prefix, localName, namespaceURI);
            this.parent = (OMElement)this.root;
        }
        else {
            this.parent = this.factory.createOMElement(localName, (OMContainer)this.parent, (OMXMLParserWrapper)null);
        }
        if (ns != null) {
            this.parent.setNamespace(ns, false);
        }
    }
    
    protected void doWriteStartElement(final String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }
    
    protected void doWriteEndElement() {
        if (this.parent == this.root) {
            this.parent = null;
        }
        else {
            ((OMContainerEx)this.parent).setComplete(true);
            this.parent = (OMElement)this.parent.getParent();
        }
    }
    
    protected void doWriteEmptyElement(final String prefix, final String localName, final String namespaceURI) {
        this.doWriteStartElement(prefix, localName, namespaceURI);
        this.doWriteEndElement();
    }
    
    protected void doWriteEmptyElement(final String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }
    
    protected void doWriteAttribute(final String prefix, final String namespaceURI, final String localName, final String value) {
        final OMAttribute attr = this.factory.createOMAttribute(localName, this.getOMNamespace(prefix, namespaceURI, false), value);
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalAppendAttribute((AxiomElement)this.parent, attr);
    }
    
    protected void doWriteAttribute(final String localName, final String value) throws XMLStreamException {
        this.doWriteAttribute(null, null, localName, value);
    }
    
    protected void doWriteNamespace(final String prefix, final String namespaceURI) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration((AxiomElement)this.parent, this.getOMNamespace(prefix, namespaceURI, true));
    }
    
    protected void doWriteDefaultNamespace(final String namespaceURI) {
        this.doWriteNamespace(null, namespaceURI);
    }
    
    protected void doWriteCharacters(final char[] text, final int start, final int len) {
        this.doWriteCharacters(new String(text, start, len));
    }
    
    protected void doWriteCharacters(final String text) {
        this.factory.createOMText((OMContainer)this.parent, text, 4, true);
    }
    
    protected void doWriteCData(final String data) {
        this.factory.createOMText((OMContainer)this.parent, data, 12, true);
    }
    
    protected void doWriteComment(final String data) {
        this.factory.createOMComment((OMContainer)this.parent, data, true);
    }
    
    protected void doWriteEntityRef(final String name) throws XMLStreamException {
        this.factory.createOMEntityReference((OMContainer)this.parent, name, (String)null, true);
    }
    
    protected void doWriteProcessingInstruction(final String target, final String data) {
        this.factory.createOMProcessingInstruction((OMContainer)this.parent, target, data, true);
    }
    
    protected void doWriteProcessingInstruction(final String target) {
        this.doWriteProcessingInstruction(target, "");
    }
    
    public void flush() throws XMLStreamException {
    }
    
    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT call XMLStreamWriter#close()");
    }
    
    public void writeDataHandler(final DataHandler dataHandler, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        final OMText child = this.factory.createOMText((Object)dataHandler, optimize);
        if (contentID != null) {
            child.setContentID(contentID);
        }
        this.parent.addChild((OMNode)child);
    }
    
    public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        this.parent.addChild((OMNode)this.factory.createOMText(contentID, dataHandlerProvider, optimize));
    }
}
