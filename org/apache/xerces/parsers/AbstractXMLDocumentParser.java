package org.apache.xerces.parsers;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;

public abstract class AbstractXMLDocumentParser extends XMLParser implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler
{
    protected boolean fInDTD;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDTDSource fDTDSource;
    protected XMLDTDContentModelSource fDTDContentModelSource;
    
    protected AbstractXMLDocumentParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
        xmlParserConfiguration.setDocumentHandler(this);
        xmlParserConfiguration.setDTDHandler(this);
        xmlParserConfiguration.setDTDContentModelHandler(this);
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void setDocumentSource(final XMLDocumentSource fDocumentSource) {
        this.fDocumentSource = fDocumentSource;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
    }
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        this.fInDTD = false;
    }
    
    public void setDTDSource(final XMLDTDSource fdtdSource) {
        this.fDTDSource = fdtdSource;
    }
    
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    public void startContentModel(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void any(final Augmentations augmentations) throws XNIException {
    }
    
    public void empty(final Augmentations augmentations) throws XNIException {
    }
    
    public void startGroup(final Augmentations augmentations) throws XNIException {
    }
    
    public void pcdata(final Augmentations augmentations) throws XNIException {
    }
    
    public void element(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void separator(final short n, final Augmentations augmentations) throws XNIException {
    }
    
    public void occurrence(final short n, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGroup(final Augmentations augmentations) throws XNIException {
    }
    
    public void endContentModel(final Augmentations augmentations) throws XNIException {
    }
    
    public void setDTDContentModelSource(final XMLDTDContentModelSource fdtdContentModelSource) {
        this.fDTDContentModelSource = fdtdContentModelSource;
    }
    
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }
    
    protected void reset() throws XNIException {
        super.reset();
        this.fInDTD = false;
    }
}
