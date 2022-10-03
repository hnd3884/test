package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;

public abstract class DTDParser extends XMLGrammarParser implements XMLDTDHandler, XMLDTDContentModelHandler
{
    protected XMLDTDScanner fDTDScanner;
    
    public DTDParser(final SymbolTable symbolTable) {
        super(symbolTable);
    }
    
    public DTDGrammar getDTDGrammar() {
        return null;
    }
    
    public void startEntity(final String name, final String publicId, final String systemId, final String encoding) throws XNIException {
    }
    
    public void textDecl(final String version, final String encoding) throws XNIException {
    }
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augmentations) throws XNIException {
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
    public void endConditional(final Augmentations augmentations) throws XNIException {
    }
    
    @Override
    public void endDTD(final Augmentations augmentations) throws XNIException {
    }
    
    public void endEntity(final String name, final Augmentations augmentations) throws XNIException {
    }
    
    public void startContentModel(final String elementName, final short type) throws XNIException {
    }
    
    public void mixedElement(final String elementName) throws XNIException {
    }
    
    public void childrenStartGroup() throws XNIException {
    }
    
    public void childrenElement(final String elementName) throws XNIException {
    }
    
    public void childrenSeparator(final short separator) throws XNIException {
    }
    
    public void childrenOccurrence(final short occurrence) throws XNIException {
    }
    
    public void childrenEndGroup() throws XNIException {
    }
    
    public void endContentModel() throws XNIException {
    }
}
