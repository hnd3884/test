package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;

final class UnparsedEntityHandler implements XMLDTDFilter, EntityState
{
    private XMLDTDSource fDTDSource;
    private XMLDTDHandler fDTDHandler;
    private final ValidationManager fValidationManager;
    private HashMap fUnparsedEntities;
    
    UnparsedEntityHandler(final ValidationManager manager) {
        this.fUnparsedEntities = null;
        this.fValidationManager = manager;
    }
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augmentations) throws XNIException {
        this.fValidationManager.setEntityState(this);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(locator, augmentations);
        }
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(version, encoding, augmentations);
        }
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(name, augmentations);
        }
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(identifier, augmentations);
        }
    }
    
    @Override
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(text, augmentations);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(target, data, augmentations);
        }
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(name, contentModel, augmentations);
        }
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(elementName, augmentations);
        }
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augmentations);
        }
    }
    
    @Override
    public void endAttlist(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augmentations);
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(name, identifier, augmentations);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augmentations) throws XNIException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(name, name);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(name, identifier, augmentations);
        }
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(type, augmentations);
        }
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(text, augmentations);
        }
    }
    
    @Override
    public void endConditional(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
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
    public void setDTDHandler(final XMLDTDHandler handler) {
        this.fDTDHandler = handler;
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public boolean isEntityDeclared(final String name) {
        return false;
    }
    
    @Override
    public boolean isEntityUnparsed(final String name) {
        return this.fUnparsedEntities != null && this.fUnparsedEntities.containsKey(name);
    }
    
    public void reset() {
        if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
            this.fUnparsedEntities.clear();
        }
    }
}
