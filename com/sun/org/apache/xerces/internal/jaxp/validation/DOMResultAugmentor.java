package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import org.w3c.dom.Element;
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
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import javax.xml.transform.dom.DOMResult;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import org.w3c.dom.Document;

final class DOMResultAugmentor implements DOMDocumentHandler
{
    private DOMValidatorHelper fDOMValidatorHelper;
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private boolean fIgnoreChars;
    private final QName fAttributeQName;
    
    public DOMResultAugmentor(final DOMValidatorHelper helper) {
        this.fAttributeQName = new QName();
        this.fDOMValidatorHelper = helper;
    }
    
    @Override
    public void setDOMResult(final DOMResult result) {
        this.fIgnoreChars = false;
        if (result != null) {
            final Node target = result.getNode();
            this.fDocument = (Document)((target.getNodeType() == 9) ? target : target.getOwnerDocument());
            this.fDocumentImpl = ((this.fDocument instanceof CoreDocumentImpl) ? ((CoreDocumentImpl)this.fDocument) : null);
            this.fStorePSVI = (this.fDocument instanceof PSVIDocumentImpl);
            return;
        }
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
    }
    
    @Override
    public void doctypeDecl(final DocumentType node) throws XNIException {
    }
    
    @Override
    public void characters(final Text node) throws XNIException {
    }
    
    @Override
    public void cdata(final CDATASection node) throws XNIException {
    }
    
    @Override
    public void comment(final Comment node) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final ProcessingInstruction node) throws XNIException {
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
        final Element currentElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
        final NamedNodeMap attrMap = currentElement.getAttributes();
        final int oldLength = attrMap.getLength();
        if (this.fDocumentImpl != null) {
            for (int i = 0; i < oldLength; ++i) {
                final AttrImpl attr = (AttrImpl)attrMap.item(i);
                final AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (attrPSVI != null && this.processAttributePSVI(attr, attrPSVI)) {
                    ((ElementImpl)currentElement).setIdAttributeNode(attr, true);
                }
            }
        }
        final int newLength = attributes.getLength();
        if (newLength > oldLength) {
            if (this.fDocumentImpl == null) {
                for (int i = oldLength; i < newLength; ++i) {
                    attributes.getName(i, this.fAttributeQName);
                    currentElement.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, attributes.getValue(i));
                }
            }
            else {
                for (int i = oldLength; i < newLength; ++i) {
                    attributes.getName(i, this.fAttributeQName);
                    final AttrImpl attr2 = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                    attr2.setValue(attributes.getValue(i));
                    final AttributePSVI attrPSVI2 = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                    if (attrPSVI2 != null && this.processAttributePSVI(attr2, attrPSVI2)) {
                        ((ElementImpl)currentElement).setIdAttributeNode(attr2, true);
                    }
                    attr2.setSpecified(false);
                    currentElement.setAttributeNode(attr2);
                }
            }
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
            final Element currentElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
            currentElement.appendChild(this.fDocument.createTextNode(text.toString()));
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        final Node currentElement = this.fDOMValidatorHelper.getCurrentElement();
        if (augs != null && this.fDocumentImpl != null) {
            final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
            if (elementPSVI != null) {
                if (this.fStorePSVI) {
                    ((PSVIElementNSImpl)currentElement).setPSVI(elementPSVI);
                }
                XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                if (type == null) {
                    type = elementPSVI.getTypeDefinition();
                }
                ((ElementNSImpl)currentElement).setType(type);
            }
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    private boolean processAttributePSVI(final AttrImpl attr, final AttributePSVI attrPSVI) {
        if (this.fStorePSVI) {
            ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
        }
        Object type = attrPSVI.getMemberTypeDefinition();
        if (type != null) {
            attr.setType(type);
            return ((XSSimpleType)type).isIDType();
        }
        type = attrPSVI.getTypeDefinition();
        if (type != null) {
            attr.setType(type);
            return ((XSSimpleType)type).isIDType();
        }
        return false;
    }
}
