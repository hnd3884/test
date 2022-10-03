package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.dom.AttrImpl;
import org.w3c.dom.Element;
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
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.apache.xerces.dom.PSVIDocumentImpl;
import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.xni.QName;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.Document;

final class DOMResultAugmentor implements DOMDocumentHandler
{
    private final DOMValidatorHelper fDOMValidatorHelper;
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private boolean fIgnoreChars;
    private final QName fAttributeQName;
    
    public DOMResultAugmentor(final DOMValidatorHelper fdomValidatorHelper) {
        this.fAttributeQName = new QName();
        this.fDOMValidatorHelper = fdomValidatorHelper;
    }
    
    public void setDOMResult(final DOMResult domResult) {
        this.fIgnoreChars = false;
        if (domResult != null) {
            final Node node = domResult.getNode();
            this.fDocument = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
            this.fDocumentImpl = ((this.fDocument instanceof CoreDocumentImpl) ? ((CoreDocumentImpl)this.fDocument) : null);
            this.fStorePSVI = (this.fDocument instanceof PSVIDocumentImpl);
            return;
        }
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
    }
    
    public void doctypeDecl(final DocumentType documentType) throws XNIException {
    }
    
    public void characters(final Text text) throws XNIException {
    }
    
    public void cdata(final CDATASection cdataSection) throws XNIException {
    }
    
    public void comment(final Comment comment) throws XNIException {
    }
    
    public void processingInstruction(final ProcessingInstruction processingInstruction) throws XNIException {
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
        final Element element = (Element)this.fDOMValidatorHelper.getCurrentElement();
        final NamedNodeMap attributes = element.getAttributes();
        final int length = attributes.getLength();
        if (this.fDocumentImpl != null) {
            for (int i = 0; i < length; ++i) {
                final AttrImpl attrImpl = (AttrImpl)attributes.item(i);
                final AttributePSVI attributePSVI = (AttributePSVI)xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (attributePSVI != null && this.processAttributePSVI(attrImpl, attributePSVI)) {
                    ((ElementImpl)element).setIdAttributeNode(attrImpl, true);
                }
            }
        }
        final int length2 = xmlAttributes.getLength();
        if (length2 > length) {
            if (this.fDocumentImpl == null) {
                for (int j = length; j < length2; ++j) {
                    xmlAttributes.getName(j, this.fAttributeQName);
                    element.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, xmlAttributes.getValue(j));
                }
            }
            else {
                for (int k = length; k < length2; ++k) {
                    xmlAttributes.getName(k, this.fAttributeQName);
                    final AttrImpl attributeNodeNS = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                    attributeNodeNS.setValue(xmlAttributes.getValue(k));
                    element.setAttributeNodeNS(attributeNodeNS);
                    final AttributePSVI attributePSVI2 = (AttributePSVI)xmlAttributes.getAugmentations(k).getItem("ATTRIBUTE_PSVI");
                    if (attributePSVI2 != null && this.processAttributePSVI(attributeNodeNS, attributePSVI2)) {
                        ((ElementImpl)element).setIdAttributeNode(attributeNodeNS, true);
                    }
                    attributeNodeNS.setSpecified(false);
                }
            }
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
            this.fDOMValidatorHelper.getCurrentElement().appendChild(this.fDocument.createTextNode(xmlString.toString()));
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.characters(xmlString, augmentations);
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        final Node currentElement = this.fDOMValidatorHelper.getCurrentElement();
        if (augmentations != null && this.fDocumentImpl != null) {
            final ElementPSVI psvi = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
            if (psvi != null) {
                if (this.fStorePSVI) {
                    ((PSVIElementNSImpl)currentElement).setPSVI(psvi);
                }
                XSTypeDefinition type = psvi.getMemberTypeDefinition();
                if (type == null) {
                    type = psvi.getTypeDefinition();
                }
                ((ElementNSImpl)currentElement).setType(type);
            }
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
    }
    
    public void setDocumentSource(final XMLDocumentSource xmlDocumentSource) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    private boolean processAttributePSVI(final AttrImpl attrImpl, final AttributePSVI psvi) {
        if (this.fStorePSVI) {
            ((PSVIAttrNSImpl)attrImpl).setPSVI(psvi);
        }
        final XSSimpleTypeDefinition memberTypeDefinition = psvi.getMemberTypeDefinition();
        if (memberTypeDefinition != null) {
            attrImpl.setType(memberTypeDefinition);
            return ((XSSimpleType)memberTypeDefinition).isIDType();
        }
        final XSTypeDefinition typeDefinition = psvi.getTypeDefinition();
        if (typeDefinition != null) {
            attrImpl.setType(typeDefinition);
            return ((XSSimpleType)typeDefinition).isIDType();
        }
        return false;
    }
}
