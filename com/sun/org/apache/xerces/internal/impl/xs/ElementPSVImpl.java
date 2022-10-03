package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;

public class ElementPSVImpl implements ElementPSVI
{
    protected XSElementDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fNil;
    protected boolean fSpecified;
    protected String fNormalizedValue;
    protected Object fActualValue;
    protected short fActualValueType;
    protected ShortList fItemValueTypes;
    protected XSNotationDeclaration fNotation;
    protected XSSimpleTypeDefinition fMemberType;
    protected short fValidationAttempted;
    protected short fValidity;
    protected String[] fErrorCodes;
    protected String fValidationContext;
    protected SchemaGrammar[] fGrammars;
    protected XSModel fSchemaInformation;
    
    public ElementPSVImpl() {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fNormalizedValue = null;
        this.fActualValue = null;
        this.fActualValueType = 45;
        this.fItemValueTypes = null;
        this.fNotation = null;
        this.fMemberType = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fValidationContext = null;
        this.fGrammars = null;
        this.fSchemaInformation = null;
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
    public boolean getNil() {
        return this.fNil;
    }
    
    @Override
    public XSNotationDeclaration getNotation() {
        return this.fNotation;
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
    public XSElementDeclaration getElementDeclaration() {
        return this.fDeclaration;
    }
    
    @Override
    public synchronized XSModel getSchemaInformation() {
        if (this.fSchemaInformation == null && this.fGrammars != null) {
            this.fSchemaInformation = new XSModelImpl(this.fGrammars);
        }
        return this.fSchemaInformation;
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
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fNotation = null;
        this.fMemberType = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fValidationContext = null;
        this.fNormalizedValue = null;
        this.fActualValue = null;
        this.fActualValueType = 45;
        this.fItemValueTypes = null;
    }
}
