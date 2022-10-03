package org.apache.xerces.jaxp.validation;

import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.w3c.dom.Element;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.w3c.dom.Attr;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.NamedNodeMap;
import org.apache.xerces.dom.NotationImpl;
import org.w3c.dom.Notation;
import org.apache.xerces.dom.EntityImpl;
import org.w3c.dom.Entity;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.w3c.dom.DocumentType;
import org.apache.xerces.dom.PSVIDocumentImpl;
import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.xni.QName;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.apache.xerces.dom.CoreDocumentImpl;
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
    
    public void setDOMResult(final DOMResult domResult) {
        this.fCurrentNode = null;
        this.fFragmentRoot = null;
        this.fIgnoreChars = false;
        this.fTargetChildren.clear();
        if (domResult != null) {
            this.fTarget = domResult.getNode();
            this.fNextSibling = domResult.getNextSibling();
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
    
    public void doctypeDecl(final DocumentType documentType) throws XNIException {
        if (this.fDocumentImpl != null) {
            final DocumentType documentType2 = this.fDocumentImpl.createDocumentType(documentType.getName(), documentType.getPublicId(), documentType.getSystemId());
            final String internalSubset = documentType.getInternalSubset();
            if (internalSubset != null) {
                ((DocumentTypeImpl)documentType2).setInternalSubset(internalSubset);
            }
            final NamedNodeMap entities = documentType.getEntities();
            final NamedNodeMap entities2 = documentType2.getEntities();
            for (int length = entities.getLength(), i = 0; i < length; ++i) {
                final Entity entity = (Entity)entities.item(i);
                final EntityImpl namedItem = (EntityImpl)this.fDocumentImpl.createEntity(entity.getNodeName());
                namedItem.setPublicId(entity.getPublicId());
                namedItem.setSystemId(entity.getSystemId());
                namedItem.setNotationName(entity.getNotationName());
                entities2.setNamedItem(namedItem);
            }
            final NamedNodeMap notations = documentType.getNotations();
            final NamedNodeMap notations2 = documentType2.getNotations();
            for (int length2 = notations.getLength(), j = 0; j < length2; ++j) {
                final Notation notation = (Notation)notations.item(j);
                final NotationImpl namedItem2 = (NotationImpl)this.fDocumentImpl.createNotation(notation.getNodeName());
                namedItem2.setPublicId(notation.getPublicId());
                namedItem2.setSystemId(notation.getSystemId());
                notations2.setNamedItem(namedItem2);
            }
            this.append(documentType2);
        }
    }
    
    public void characters(final Text text) throws XNIException {
        this.append(this.fDocument.createTextNode(text.getNodeValue()));
    }
    
    public void cdata(final CDATASection cdataSection) throws XNIException {
        this.append(this.fDocument.createCDATASection(cdataSection.getNodeValue()));
    }
    
    public void comment(final Comment comment) throws XNIException {
        this.append(this.fDocument.createComment(comment.getNodeValue()));
    }
    
    public void processingInstruction(final ProcessingInstruction processingInstruction) throws XNIException {
        this.append(this.fDocument.createProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData()));
    }
    
    public void setIgnoringCharacters(final boolean fIgnoreChars) {
        this.fIgnoreChars = fIgnoreChars;
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        final int length = xmlAttributes.getLength();
        Element element;
        if (this.fDocumentImpl == null) {
            element = this.fDocument.createElementNS(qName.uri, qName.rawname);
            for (int i = 0; i < length; ++i) {
                xmlAttributes.getName(i, this.fAttributeQName);
                element.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, xmlAttributes.getValue(i));
            }
        }
        else {
            element = this.fDocumentImpl.createElementNS(qName.uri, qName.rawname, qName.localpart);
            for (int j = 0; j < length; ++j) {
                xmlAttributes.getName(j, this.fAttributeQName);
                final AttrImpl attributeNodeNS = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                attributeNodeNS.setValue(xmlAttributes.getValue(j));
                element.setAttributeNodeNS(attributeNodeNS);
                final AttributePSVI psvi = (AttributePSVI)xmlAttributes.getAugmentations(j).getItem("ATTRIBUTE_PSVI");
                if (psvi != null) {
                    if (this.fStorePSVI) {
                        ((PSVIAttrNSImpl)attributeNodeNS).setPSVI(psvi);
                    }
                    final XSSimpleTypeDefinition memberTypeDefinition = psvi.getMemberTypeDefinition();
                    if (memberTypeDefinition == null) {
                        final XSTypeDefinition typeDefinition = psvi.getTypeDefinition();
                        if (typeDefinition != null) {
                            attributeNodeNS.setType(typeDefinition);
                            if (((XSSimpleType)typeDefinition).isIDType()) {
                                ((ElementImpl)element).setIdAttributeNode(attributeNodeNS, true);
                            }
                        }
                    }
                    else {
                        attributeNodeNS.setType(memberTypeDefinition);
                        if (((XSSimpleType)memberTypeDefinition).isIDType()) {
                            ((ElementImpl)element).setIdAttributeNode(attributeNodeNS, true);
                        }
                    }
                }
                attributeNodeNS.setSpecified(xmlAttributes.isSpecified(j));
            }
        }
        this.append(element);
        this.fCurrentNode = element;
        if (this.fFragmentRoot == null) {
            this.fFragmentRoot = element;
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.fIgnoreChars) {
            this.append(this.fDocument.createTextNode(xmlString.toString()));
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.characters(xmlString, augmentations);
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (augmentations != null && this.fDocumentImpl != null) {
            final ElementPSVI psvi = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
            if (psvi != null) {
                if (this.fStorePSVI) {
                    ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(psvi);
                }
                XSTypeDefinition type = psvi.getMemberTypeDefinition();
                if (type == null) {
                    type = psvi.getTypeDefinition();
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
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        final int size = this.fTargetChildren.size();
        if (this.fNextSibling == null) {
            for (int i = 0; i < size; ++i) {
                this.fTarget.appendChild((Node)this.fTargetChildren.get(i));
            }
        }
        else {
            for (int j = 0; j < size; ++j) {
                this.fTarget.insertBefore((Node)this.fTargetChildren.get(j), this.fNextSibling);
            }
        }
    }
    
    public void setDocumentSource(final XMLDocumentSource xmlDocumentSource) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    private void append(final Node node) throws XNIException {
        if (this.fCurrentNode != null) {
            this.fCurrentNode.appendChild(node);
        }
        else {
            if ((DOMResultBuilder.kidOK[this.fTarget.getNodeType()] & 1 << node.getNodeType()) == 0x0) {
                throw new XNIException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
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
