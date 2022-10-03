package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

class ShortHandPointer implements XPointerPart
{
    private String fShortHandPointer;
    private boolean fIsFragmentResolved;
    private SymbolTable fSymbolTable;
    int fMatchingChildCount;
    
    public ShortHandPointer() {
        this.fIsFragmentResolved = false;
        this.fMatchingChildCount = 0;
    }
    
    public ShortHandPointer(final SymbolTable symbolTable) {
        this.fIsFragmentResolved = false;
        this.fMatchingChildCount = 0;
        this.fSymbolTable = symbolTable;
    }
    
    @Override
    public void parseXPointer(final String part) throws XNIException {
        this.fShortHandPointer = part;
        this.fIsFragmentResolved = false;
    }
    
    @Override
    public boolean resolveXPointer(final QName element, final XMLAttributes attributes, final Augmentations augs, final int event) throws XNIException {
        if (this.fMatchingChildCount == 0) {
            this.fIsFragmentResolved = false;
        }
        if (event == 0) {
            if (this.fMatchingChildCount == 0) {
                this.fIsFragmentResolved = this.hasMatchingIdentifier(element, attributes, augs, event);
            }
            if (this.fIsFragmentResolved) {
                ++this.fMatchingChildCount;
            }
        }
        else if (event == 2) {
            if (this.fMatchingChildCount == 0) {
                this.fIsFragmentResolved = this.hasMatchingIdentifier(element, attributes, augs, event);
            }
        }
        else if (this.fIsFragmentResolved) {
            --this.fMatchingChildCount;
        }
        return this.fIsFragmentResolved;
    }
    
    private boolean hasMatchingIdentifier(final QName element, final XMLAttributes attributes, final Augmentations augs, final int event) throws XNIException {
        String normalizedValue = null;
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                normalizedValue = this.getSchemaDeterminedID(attributes, i);
                if (normalizedValue != null) {
                    break;
                }
                normalizedValue = this.getChildrenSchemaDeterminedID(attributes, i);
                if (normalizedValue != null) {
                    break;
                }
                normalizedValue = this.getDTDDeterminedID(attributes, i);
                if (normalizedValue != null) {
                    break;
                }
            }
        }
        return normalizedValue != null && normalizedValue.equals(this.fShortHandPointer);
    }
    
    public String getDTDDeterminedID(final XMLAttributes attributes, final int index) throws XNIException {
        if (attributes.getType(index).equals("ID")) {
            return attributes.getValue(index);
        }
        return null;
    }
    
    public String getSchemaDeterminedID(final XMLAttributes attributes, final int index) throws XNIException {
        final Augmentations augs = attributes.getAugmentations(index);
        final AttributePSVI attrPSVI = (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
        if (attrPSVI != null) {
            XSTypeDefinition typeDef = attrPSVI.getMemberTypeDefinition();
            if (typeDef != null) {
                typeDef = attrPSVI.getTypeDefinition();
            }
            if (typeDef != null && ((XSSimpleType)typeDef).isIDType()) {
                return attrPSVI.getSchemaNormalizedValue();
            }
        }
        return null;
    }
    
    public String getChildrenSchemaDeterminedID(final XMLAttributes attributes, final int index) throws XNIException {
        return null;
    }
    
    @Override
    public boolean isFragmentResolved() {
        return this.fIsFragmentResolved;
    }
    
    @Override
    public boolean isChildFragmentResolved() {
        return this.fIsFragmentResolved & this.fMatchingChildCount > 0;
    }
    
    @Override
    public String getSchemeName() {
        return this.fShortHandPointer;
    }
    
    @Override
    public String getSchemeData() {
        return null;
    }
    
    @Override
    public void setSchemeName(final String schemeName) {
        this.fShortHandPointer = schemeName;
    }
    
    @Override
    public void setSchemeData(final String schemeData) {
    }
}
