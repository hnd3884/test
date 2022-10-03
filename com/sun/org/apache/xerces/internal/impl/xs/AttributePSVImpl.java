package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;

public class AttributePSVImpl implements AttributePSVI
{
    protected XSAttributeDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fSpecified;
    protected String fNormalizedValue;
    protected Object fActualValue;
    protected short fActualValueType;
    protected ShortList fItemValueTypes;
    protected XSSimpleTypeDefinition fMemberType;
    protected short fValidationAttempted;
    protected short fValidity;
    protected String[] fErrorCodes;
    protected String fValidationContext;
    
    public AttributePSVImpl() {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fNormalizedValue = null;
        this.fActualValue = null;
        this.fActualValueType = 45;
        this.fItemValueTypes = null;
        this.fMemberType = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fValidationContext = null;
    }
    
    @Override
    public String getSchemaDefault() {
        return (this.fDeclaration == null) ? null : this.fDeclaration.getConstraintValue();
    }
    
    @Override
    public String getSchemaNormalizedValue() {
        return this.fNormalizedValue;
    }
    
    @Override
    public boolean getIsSchemaSpecified() {
        return this.fSpecified;
    }
    
    @Override
    public short getValidationAttempted() {
        return this.fValidationAttempted;
    }
    
    @Override
    public short getValidity() {
        return this.fValidity;
    }
    
    @Override
    public StringList getErrorCodes() {
        if (this.fErrorCodes == null) {
            return null;
        }
        return new StringListImpl(this.fErrorCodes, this.fErrorCodes.length);
    }
    
    @Override
    public String getValidationContext() {
        return this.fValidationContext;
    }
    
    @Override
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDecl;
    }
    
    @Override
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.fMemberType;
    }
    
    @Override
    public XSAttributeDeclaration getAttributeDeclaration() {
        return this.fDeclaration;
    }
    
    @Override
    public Object getActualNormalizedValue() {
        return this.fActualValue;
    }
    
    @Override
    public short getActualNormalizedValueType() {
        return this.fActualValueType;
    }
    
    @Override
    public ShortList getItemValueTypes() {
        return this.fItemValueTypes;
    }
    
    public void reset() {
        this.fNormalizedValue = null;
        this.fActualValue = null;
        this.fActualValueType = 45;
        this.fItemValueTypes = null;
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fMemberType = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fValidationContext = null;
    }
}
