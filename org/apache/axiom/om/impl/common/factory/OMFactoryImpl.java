package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.impl.common.AxiomSourcedElementSupport;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.xml.NSUtils;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.common.AxiomNamedInformationItemSupport;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.impl.intf.AxiomElement;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMText;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomText;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.builder.OMFactoryEx;

public class OMFactoryImpl implements OMFactoryEx
{
    private final OMMetaFactory metaFactory;
    private final NodeFactory nodeFactory;
    
    public OMFactoryImpl(final OMMetaFactory metaFactory, final NodeFactory nodeFactory) {
        this.metaFactory = metaFactory;
        this.nodeFactory = nodeFactory;
    }
    
    protected final <T extends CoreNode> T createNode(final Class<T> type) {
        final T node = this.nodeFactory.createNode(type);
        this.initNode(node);
        return node;
    }
    
    protected void initNode(final CoreNode node) {
    }
    
    public final OMMetaFactory getMetaFactory() {
        return this.metaFactory;
    }
    
    public final OMNamespace createOMNamespace(final String uri, final String prefix) {
        return (OMNamespace)new OMNamespaceImpl(uri, prefix);
    }
    
    public final OMDocument createOMDocument() {
        return this.createNode((Class<OMDocument>)AxiomDocument.class);
    }
    
    public final OMDocument createOMDocument(final OMXMLParserWrapper builder) {
        final AxiomDocument document = this.createNode(AxiomDocument.class);
        document.coreSetBuilder(builder);
        return (OMDocument)document;
    }
    
    public final OMDocType createOMDocType(final OMContainer parent, final String rootName, final String publicId, final String systemId, final String internalSubset) {
        return this.createOMDocType(parent, rootName, publicId, systemId, internalSubset, false);
    }
    
    public final OMDocType createOMDocType(final OMContainer parent, final String rootName, final String publicId, final String systemId, final String internalSubset, final boolean fromBuilder) {
        final AxiomDocType node = this.createNode(AxiomDocType.class);
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        if (parent != null) {
            ((OMContainerEx)parent).addChild((OMNode)node, fromBuilder);
        }
        return (OMDocType)node;
    }
    
    private AxiomText createAxiomText(final OMContainer parent, final Object content, final int type, final boolean fromBuilder) {
        AxiomText node = null;
        switch (type) {
            case 4: {
                node = this.createNode(AxiomCharacterDataNode.class);
                break;
            }
            case 6: {
                final AxiomCharacterDataNode cdata = this.createNode(AxiomCharacterDataNode.class);
                cdata.coreSetIgnorable(true);
                node = cdata;
                break;
            }
            case 12: {
                node = this.createNode(AxiomCDATASection.class);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid node type");
            }
        }
        if (parent != null) {
            ((OMContainerEx)parent).addChild((OMNode)node, fromBuilder);
        }
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        return node;
    }
    
    public final OMText createOMText(final OMContainer parent, final String text, final int type, final boolean fromBuilder) {
        return (OMText)this.createAxiomText(parent, text, type, fromBuilder);
    }
    
    public final OMText createOMText(final String s, final int type) {
        return (OMText)this.createAxiomText(null, s, type, false);
    }
    
    public final OMText createOMText(final String s) {
        return (OMText)this.createAxiomText(null, s, 4, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final String text, final int type) {
        return (OMText)this.createAxiomText(parent, text, type, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final String text) {
        return (OMText)this.createAxiomText(parent, text, 4, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final char[] charArray, final int type) {
        return (OMText)this.createAxiomText(parent, new String(charArray), type, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final QName text, final int type) {
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        final OMNamespace ns = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$handleNamespace((AxiomElement)parent, text.getNamespaceURI(), text.getPrefix());
        return (OMText)this.createAxiomText(parent, (ns == null) ? text.getLocalPart() : (String.valueOf(ns.getPrefix()) + ":" + text.getLocalPart()), type, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final QName text) {
        return (OMText)this.createAxiomText(parent, text, 4, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final String s, final String mimeType, final boolean optimize) {
        return (OMText)this.createAxiomText(parent, new TextContent(s, mimeType, optimize), 4, false);
    }
    
    public final OMText createOMText(final String s, final String mimeType, final boolean optimize) {
        return this.createOMText(null, s, mimeType, optimize);
    }
    
    public final OMText createOMText(final OMContainer parent, final OMText source) {
        return (OMText)((AxiomText)source).coreClone(AxiomSemantics.CLONE_POLICY, null, (CoreParentNode)parent);
    }
    
    public final OMText createOMText(final Object dataHandler, final boolean optimize) {
        return this.createOMText(null, dataHandler, optimize, false);
    }
    
    public final OMText createOMText(final OMContainer parent, final Object dataHandler, final boolean optimize, final boolean fromBuilder) {
        return (OMText)this.createAxiomText(parent, new TextContent(dataHandler, optimize), 4, fromBuilder);
    }
    
    public final OMText createOMText(final String contentID, final DataHandlerProvider dataHandlerProvider, final boolean optimize) {
        return (OMText)this.createAxiomText(null, new TextContent(contentID, dataHandlerProvider, optimize), 4, false);
    }
    
    public final OMProcessingInstruction createOMProcessingInstruction(final OMContainer parent, final String piTarget, final String piData) {
        return this.createOMProcessingInstruction(parent, piTarget, piData, false);
    }
    
    public final OMProcessingInstruction createOMProcessingInstruction(final OMContainer parent, final String piTarget, final String piData, final boolean fromBuilder) {
        final AxiomProcessingInstruction node = this.createNode(AxiomProcessingInstruction.class);
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        if (parent != null) {
            ((OMContainerEx)parent).addChild((OMNode)node, fromBuilder);
        }
        return (OMProcessingInstruction)node;
    }
    
    public final OMEntityReference createOMEntityReference(final OMContainer parent, final String name) {
        return this.createOMEntityReference(parent, name, null, false);
    }
    
    public final OMEntityReference createOMEntityReference(final OMContainer parent, final String name, final String replacementText, final boolean fromBuilder) {
        final AxiomEntityReference node = this.createNode(AxiomEntityReference.class);
        node.coreSetName(name);
        node.coreSetReplacementText(replacementText);
        if (parent != null) {
            ((OMContainerEx)parent).addChild((OMNode)node, fromBuilder);
        }
        return (OMEntityReference)node;
    }
    
    public final OMComment createOMComment(final OMContainer parent, final String content) {
        return this.createOMComment(parent, content, false);
    }
    
    public final OMComment createOMComment(final OMContainer parent, final String content, final boolean fromBuilder) {
        final AxiomComment node = this.createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        if (parent != null) {
            ((OMContainerEx)parent).addChild((OMNode)node, fromBuilder);
        }
        return (OMComment)node;
    }
    
    public final OMElement createOMElement(final String localName, final OMNamespace ns) {
        return this.createOMElement(localName, ns, null);
    }
    
    public final <T extends AxiomElement> T createAxiomElement(final Class<T> type, final OMContainer parent, final String localName, final OMNamespace ns, final OMXMLParserWrapper builder, final boolean generateNSDecl) {
        final T element = this.createNode(type);
        element.coreSetBuilder(builder);
        if (parent != null) {
            AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild((AxiomContainer)parent, (OMNode)element, builder != null);
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(element, localName, ns, generateNSDecl);
        return element;
    }
    
    public final OMElement createOMElement(final String localName, final OMNamespace ns, final OMContainer parent) {
        return this.createAxiomElement((Class<OMElement>)AxiomElement.class, parent, localName, ns, null, true);
    }
    
    public final OMElement createOMElement(final String localName, final OMContainer parent, final OMXMLParserWrapper builder) {
        return this.createAxiomElement((Class<OMElement>)AxiomElement.class, parent, localName, null, builder, false);
    }
    
    public final OMElement createOMElement(final QName qname, final OMContainer parent) {
        final AxiomElement element = this.createNode(AxiomElement.class);
        if (parent != null) {
            parent.addChild((OMNode)element);
        }
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(element, qname.getLocalPart());
        String prefix = qname.getPrefix();
        final String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            OMNamespace ns = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespace(element, namespaceURI, (prefix.length() == 0) ? null : prefix);
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = NSUtils.generatePrefix(namespaceURI);
                }
                ns = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareNamespace(element, namespaceURI, prefix);
            }
            AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(element, ns);
        }
        else {
            if (prefix.length() > 0) {
                throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
            }
            if (AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getDefaultNamespace(element) != null) {
                AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareDefaultNamespace(element, "");
            }
            AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(element, null);
        }
        return (OMElement)element;
    }
    
    public final OMElement createOMElement(final QName qname) {
        return this.createOMElement(qname, null);
    }
    
    public final OMElement createOMElement(final String localName, final String namespaceURI, final String prefix) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        if (namespaceURI.length() != 0) {
            return this.createOMElement(localName, this.createOMNamespace(namespaceURI, prefix));
        }
        if (prefix != null && prefix.length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        return this.createOMElement(localName, null);
    }
    
    public final OMSourcedElement createOMElement(final OMDataSource source) {
        final AxiomSourcedElement element = this.createNode(AxiomSourcedElement.class);
        AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(element, source);
        return (OMSourcedElement)element;
    }
    
    public final OMSourcedElement createOMElement(final OMDataSource source, final String localName, final OMNamespace ns) {
        final AxiomSourcedElement element = this.createNode(AxiomSourcedElement.class);
        AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(element, localName, ns, source);
        return (OMSourcedElement)element;
    }
    
    public final OMSourcedElement createOMElement(final OMDataSource source, final QName qname) {
        final AxiomSourcedElement element = this.createNode(AxiomSourcedElement.class);
        AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(element, qname, source);
        return (OMSourcedElement)element;
    }
    
    public final OMAttribute createOMAttribute(final String localName, OMNamespace ns, final String value) {
        if (ns != null && ns.getPrefix() == null) {
            final String namespaceURI = ns.getNamespaceURI();
            if (namespaceURI.length() == 0) {
                ns = null;
            }
            else {
                ns = (OMNamespace)new OMNamespaceImpl(namespaceURI, NSUtils.generatePrefix(namespaceURI));
            }
        }
        if (ns != null) {
            if (ns.getNamespaceURI().length() == 0) {
                if (ns.getPrefix().length() > 0) {
                    throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
                }
                ns = null;
            }
            else if (ns.getPrefix().length() == 0) {
                throw new IllegalArgumentException("Cannot create an unprefixed attribute with a namespace");
            }
        }
        final AxiomAttribute attr = this.createNode(AxiomAttribute.class);
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(attr, localName);
        attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(attr, ns);
        attr.coreSetType("CDATA");
        return (OMAttribute)attr;
    }
    
    public final OMNode importNode(final OMNode child) {
        final int type = child.getType();
        switch (type) {
            case 1: {
                final OMElement childElement = (OMElement)child;
                final OMElement newElement = new StAXOMBuilder((OMFactory)this, childElement.getXMLStreamReader()).getDocumentElement();
                newElement.buildWithAttachments();
                return (OMNode)newElement;
            }
            case 4: {
                final OMText importedText = (OMText)child;
                OMText newText;
                if (importedText.isBinary()) {
                    final boolean isOptimize = importedText.isOptimized();
                    newText = this.createOMText(importedText.getDataHandler(), isOptimize);
                }
                else if (importedText.isCharacters()) {
                    newText = this.createOMText(null, importedText.getTextCharacters(), importedText.getType());
                }
                else {
                    newText = this.createOMText(null, importedText.getText());
                }
                return (OMNode)newText;
            }
            case 3: {
                final OMProcessingInstruction importedPI = (OMProcessingInstruction)child;
                return (OMNode)this.createOMProcessingInstruction(null, importedPI.getTarget(), importedPI.getValue());
            }
            case 5: {
                final OMComment importedComment = (OMComment)child;
                return (OMNode)this.createOMComment(null, importedComment.getValue());
            }
            case 11: {
                final OMDocType importedDocType = (OMDocType)child;
                return (OMNode)this.createOMDocType(null, importedDocType.getRootName(), importedDocType.getPublicId(), importedDocType.getSystemId(), importedDocType.getInternalSubset());
            }
            default: {
                throw new UnsupportedOperationException("Not Implemented Yet for the given node type");
            }
        }
    }
}
