package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public class DefaultXMLDocumentHandler implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler
{
    private XMLDocumentSource fDocumentSource;
    private XMLDTDSource fDTDSource;
    private XMLDTDContentModelSource fCMSource;
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext context, final Augmentations augs) throws XNIException {
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
    
    public void startPrefixMapping(final String prefix, final String uri, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
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
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
    }
    
    public void endPrefixMapping(final String prefix, final Augmentations augs) throws XNIException {
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
    public void startDTD(final XMLLocator locator, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endAttlist(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endConditional(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endDTD(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startContentModel(final String elementName, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void any(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void empty(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void startGroup(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void pcdata(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void element(final String elementName, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void separator(final short separator, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void occurrence(final short occurrence, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endGroup(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endContentModel(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    @Override
    public void setDTDSource(final XMLDTDSource source) {
        this.fDTDSource = source;
    }
    
    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    @Override
    public void setDTDContentModelSource(final XMLDTDContentModelSource source) {
        this.fCMSource = source;
    }
    
    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fCMSource;
    }
}
