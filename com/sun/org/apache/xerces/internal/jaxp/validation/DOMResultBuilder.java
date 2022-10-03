package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.w3c.dom.NamedNodeMap;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
import org.w3c.dom.Notation;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import org.w3c.dom.Entity;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import org.w3c.dom.DocumentType;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import javax.xml.transform.dom.DOMResult;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import org.w3c.dom.Document;

final class DOMResultBuilder implements DOMDocumentHandler
{
    private static final int[] kidOK;
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private Node fTarget;
    private Node fNextSibling;
    private Node fCurrentNode;
    private Node fFragmentRoot;
    private final ArrayList fTargetChildren;
    private boolean fIgnoreChars;
    private final QName fAttributeQName;
    
    public DOMResultBuilder() {
        this.fTargetChildren = new ArrayList();
        this.fAttributeQName = new QName();
    }
    
    @Override
    public void setDOMResult(final DOMResult result) {
        this.fCurrentNode = null;
        this.fFragmentRoot = null;
        this.fIgnoreChars = false;
        this.fTargetChildren.clear();
        if (result != null) {
            this.fTarget = result.getNode();
            this.fNextSibling = result.getNextSibling();
            this.fDocument = (Document)((this.fTarget.getNodeType() == 9) ? this.fTarget : this.fTarget.getOwnerDocument());
            this.fDocumentImpl = ((this.fDocument instanceof CoreDocumentImpl) ? ((CoreDocumentImpl)this.fDocument) : null);
            this.fStorePSVI = (this.fDocument instanceof PSVIDocumentImpl);
            return;
        }
        this.fTarget = null;
        this.fNextSibling = null;
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
    }
    
    @Override
    public void doctypeDecl(final DocumentType node) throws XNIException {
        if (this.fDocumentImpl != null) {
            final DocumentType docType = this.fDocumentImpl.createDocumentType(node.getName(), node.getPublicId(), node.getSystemId());
            final String internalSubset = node.getInternalSubset();
            if (internalSubset != null) {
                ((DocumentTypeImpl)docType).setInternalSubset(internalSubset);
            }
            NamedNodeMap oldMap = node.getEntities();
            NamedNodeMap newMap = docType.getEntities();
            for (int length = oldMap.getLength(), i = 0; i < length; ++i) {
                final Entity oldEntity = (Entity)oldMap.item(i);
                final EntityImpl newEntity = (EntityImpl)this.fDocumentImpl.createEntity(oldEntity.getNodeName());
                newEntity.setPublicId(oldEntity.getPublicId());
                newEntity.setSystemId(oldEntity.getSystemId());
                newEntity.setNotationName(oldEntity.getNotationName());
                newMap.setNamedItem(newEntity);
            }
            oldMap = node.getNotations();
            newMap = docType.getNotations();
            for (int length = oldMap.getLength(), i = 0; i < length; ++i) {
                final Notation oldNotation = (Notation)oldMap.item(i);
                final NotationImpl newNotation = (NotationImpl)this.fDocumentImpl.createNotation(oldNotation.getNodeName());
                newNotation.setPublicId(oldNotation.getPublicId());
                newNotation.setSystemId(oldNotation.getSystemId());
                newMap.setNamedItem(newNotation);
            }
            this.append(docType);
        }
    }
    
    @Override
    public void characters(final Text node) throws XNIException {
        this.append(this.fDocument.createTextNode(node.getNodeValue()));
    }
    
    @Override
    public void cdata(final CDATASection node) throws XNIException {
        this.append(this.fDocument.createCDATASection(node.getNodeValue()));
    }
    
    @Override
    public void comment(final Comment node) throws XNIException {
        this.append(this.fDocument.createComment(node.getNodeValue()));
    }
    
    @Override
    public void processingInstruction(final ProcessingInstruction node) throws XNIException {
        this.append(this.fDocument.createProcessingInstruction(node.getTarget(), node.getData()));
    }
    
    @Override
    public void setIgnoringCharacters(final boolean ignore) {
        this.fIgnoreChars = ignore;
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        final int attrCount = attributes.getLength();
        Element elem;
        if (this.fDocumentImpl == null) {
            elem = this.fDocument.createElementNS(element.uri, element.rawname);
            for (int i = 0; i < attrCount; ++i) {
                attributes.getName(i, this.fAttributeQName);
                elem.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, attributes.getValue(i));
            }
        }
        else {
            elem = this.fDocumentImpl.createElementNS(element.uri, element.rawname, element.localpart);
            for (int i = 0; i < attrCount; ++i) {
                attributes.getName(i, this.fAttributeQName);
                final AttrImpl attr = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                attr.setValue(attributes.getValue(i));
                final AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (attrPSVI != null) {
                    if (this.fStorePSVI) {
                        ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
                    }
                    Object type = attrPSVI.getMemberTypeDefinition();
                    if (type == null) {
                        type = attrPSVI.getTypeDefinition();
                        if (type != null) {
                            attr.setType(type);
                            if (((XSSimpleType)type).isIDType()) {
                                ((ElementImpl)elem).setIdAttributeNode(attr, true);
                            }
                        }
                    }
                    else {
                        attr.setType(type);
                        if (((XSSimpleType)type).isIDType()) {
                            ((ElementImpl)elem).setIdAttributeNode(attr, true);
                        }
                    }
                }
                attr.setSpecified(attributes.isSpecified(i));
                elem.setAttributeNode(attr);
            }
        }
        this.append(elem);
        this.fCurrentNode = elem;
        if (this.fFragmentRoot == null) {
            this.fFragmentRoot = elem;
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.fIgnoreChars) {
            this.append(this.fDocument.createTextNode(text.toString()));
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (augs != null && this.fDocumentImpl != null) {
            final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
            if (elementPSVI != null) {
                if (this.fStorePSVI) {
                    ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
                }
                XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                if (type == null) {
                    type = elementPSVI.getTypeDefinition();
                }
                ((ElementNSImpl)this.fCurrentNode).setType(type);
            }
        }
        if (this.fCurrentNode == this.fFragmentRoot) {
            this.fCurrentNode = null;
            this.fFragmentRoot = null;
            return;
        }
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        final int length = this.fTargetChildren.size();
        if (this.fNextSibling == null) {
            for (int i = 0; i < length; ++i) {
                this.fTarget.appendChild(this.fTargetChildren.get(i));
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                this.fTarget.insertBefore(this.fTargetChildren.get(i), this.fNextSibling);
            }
        }
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    private void append(final Node node) throws XNIException {
        if (this.fCurrentNode != null) {
            this.fCurrentNode.appendChild(node);
        }
        else {
            if ((DOMResultBuilder.kidOK[this.fTarget.getNodeType()] & 1 << node.getNodeType()) == 0x0) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new XNIException(msg);
            }
            this.fTargetChildren.add(node);
        }
    }
    
    static {
        (kidOK = new int[13])[9] = 1410;
        final int[] kidOK2 = DOMResultBuilder.kidOK;
        final int n = 11;
        final int[] kidOK3 = DOMResultBuilder.kidOK;
        final int n2 = 6;
        final int[] kidOK4 = DOMResultBuilder.kidOK;
        final int n3 = 5;
        final int[] kidOK5 = DOMResultBuilder.kidOK;
        final int n4 = 1;
        final int n5 = 442;
        kidOK4[n3] = (kidOK5[n4] = n5);
        kidOK2[n] = (kidOK3[n2] = n5);
        DOMResultBuilder.kidOK[2] = 40;
        DOMResultBuilder.kidOK[10] = 0;
        DOMResultBuilder.kidOK[7] = 0;
        DOMResultBuilder.kidOK[8] = 0;
        DOMResultBuilder.kidOK[3] = 0;
        DOMResultBuilder.kidOK[4] = 0;
        DOMResultBuilder.kidOK[12] = 0;
    }
}
