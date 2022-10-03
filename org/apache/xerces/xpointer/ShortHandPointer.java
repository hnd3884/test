package org.apache.xerces.xpointer;

import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.SymbolTable;

final class ShortHandPointer implements XPointerPart
{
    private String fShortHandPointer;
    private boolean fIsFragmentResolved;
    private SymbolTable fSymbolTable;
    int fMatchingChildCount;
    
    public ShortHandPointer() {
        this.fIsFragmentResolved = false;
        this.fMatchingChildCount = 0;
    }
    
    public ShortHandPointer(final SymbolTable fSymbolTable) {
        this.fIsFragmentResolved = false;
        this.fMatchingChildCount = 0;
        this.fSymbolTable = fSymbolTable;
    }
    
    public void parseXPointer(final String fShortHandPointer) throws XNIException {
        this.fShortHandPointer = fShortHandPointer;
        this.fIsFragmentResolved = false;
    }
    
    public boolean resolveXPointer(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations, final int n) throws XNIException {
        if (this.fMatchingChildCount == 0) {
            this.fIsFragmentResolved = false;
        }
        if (n == 0) {
            if (this.fMatchingChildCount == 0) {
                this.fIsFragmentResolved = this.hasMatchingIdentifier(qName, xmlAttributes, augmentations, n);
            }
            if (this.fIsFragmentResolved) {
                ++this.fMatchingChildCount;
            }
        }
        else if (n == 2) {
            if (this.fMatchingChildCount == 0) {
                this.fIsFragmentResolved = this.hasMatchingIdentifier(qName, xmlAttributes, augmentations, n);
            }
        }
        else if (this.fIsFragmentResolved) {
            --this.fMatchingChildCount;
        }
        return this.fIsFragmentResolved;
    }
    
    private boolean hasMatchingIdentifier(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations, final int n) throws XNIException {
        String s = null;
        if (xmlAttributes != null) {
            for (int i = 0; i < xmlAttributes.getLength(); ++i) {
                s = this.getSchemaDeterminedID(xmlAttributes, i);
                if (s != null) {
                    break;
                }
                s = this.getChildrenSchemaDeterminedID(xmlAttributes, i);
                if (s != null) {
                    break;
                }
                s = this.getDTDDeterminedID(xmlAttributes, i);
                if (s != null) {
                    break;
                }
            }
        }
        return s != null && s.equals(this.fShortHandPointer);
    }
    
    public String getDTDDeterminedID(final XMLAttributes xmlAttributes, final int n) throws XNIException {
        if (xmlAttributes.getType(n).equals("ID")) {
            return xmlAttributes.getValue(n);
        }
        return null;
    }
    
    public String getSchemaDeterminedID(final XMLAttributes xmlAttributes, final int n) throws XNIException {
        final AttributePSVI attributePSVI = (AttributePSVI)xmlAttributes.getAugmentations(n).getItem("ATTRIBUTE_PSVI");
        if (attributePSVI != null) {
            XSTypeDefinition xsTypeDefinition = attributePSVI.getMemberTypeDefinition();
            if (xsTypeDefinition != null) {
                xsTypeDefinition = attributePSVI.getTypeDefinition();
            }
            if (xsTypeDefinition != null && ((XSSimpleType)xsTypeDefinition).isIDType()) {
                return attributePSVI.getSchemaNormalizedValue();
            }
        }
        return null;
    }
    
    public String getChildrenSchemaDeterminedID(final XMLAttributes xmlAttributes, final int n) throws XNIException {
        return null;
    }
    
    public boolean isFragmentResolved() {
        return this.fIsFragmentResolved;
    }
    
    public boolean isChildFragmentResolved() {
        return this.fIsFragmentResolved && this.fMatchingChildCount > 0;
    }
    
    public String getSchemeName() {
        return this.fShortHandPointer;
    }
    
    public String getSchemeData() {
        return null;
    }
    
    public void setSchemeName(final String fShortHandPointer) {
        this.fShortHandPointer = fShortHandPointer;
    }
    
    public void setSchemeData(final String s) {
    }
}
