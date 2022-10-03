package org.apache.xerces.jaxp;

import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import java.util.HashMap;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.xni.parser.XMLDTDFilter;

final class UnparsedEntityHandler implements XMLDTDFilter, EntityState
{
    private XMLDTDSource fDTDSource;
    private XMLDTDHandler fDTDHandler;
    private final ValidationManager fValidationManager;
    private HashMap fUnparsedEntities;
    
    UnparsedEntityHandler(final ValidationManager fValidationManager) {
        this.fUnparsedEntities = null;
        this.fValidationManager = fValidationManager;
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fValidationManager.setEntityState(this);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xmlLocator, augmentations);
        }
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(s, augmentations);
        }
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xmlResourceIdentifier, augmentations);
        }
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xmlString, augmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(s, xmlString, augmentations);
        }
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(s, s2, augmentations);
        }
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(s, augmentations);
        }
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(s, s2, s3, array, s4, xmlString, xmlString2, augmentations);
        }
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(s, xmlString, xmlString2, augmentations);
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(s, xmlResourceIdentifier, augmentations);
        }
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(s, s);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(s, xmlResourceIdentifier, augmentations);
        }
    }
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(n, augmentations);
        }
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xmlString, augmentations);
        }
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
    }
    
    public void setDTDSource(final XMLDTDSource fdtdSource) {
        this.fDTDSource = fdtdSource;
    }
    
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public boolean isEntityDeclared(final String s) {
        return false;
    }
    
    public boolean isEntityUnparsed(final String s) {
        return this.fUnparsedEntities != null && this.fUnparsedEntities.containsKey(s);
    }
    
    public void reset() {
        if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
            this.fUnparsedEntities.clear();
        }
    }
}
