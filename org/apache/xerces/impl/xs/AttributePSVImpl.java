package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.AttributePSVI;

public class AttributePSVImpl implements AttributePSVI
{
    protected XSAttributeDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fSpecified;
    protected ValidatedInfo fValue;
    protected short fValidationAttempted;
    protected short fValidity;
    protected String[] fErrors;
    protected String fValidationContext;
    protected boolean fIsConstant;
    
    public AttributePSVImpl() {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fValue = new ValidatedInfo();
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
    }
    
    public AttributePSVImpl(final boolean fIsConstant, final AttributePSVI attributePSVI) {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fValue = new ValidatedInfo();
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
        this.fDeclaration = attributePSVI.getAttributeDeclaration();
        this.fTypeDecl = attributePSVI.getTypeDefinition();
        this.fSpecified = attributePSVI.getIsSchemaSpecified();
        this.fValue.copyFrom(attributePSVI.getSchemaValue());
        this.fValidationAttempted = attributePSVI.getValidationAttempted();
        this.fValidity = attributePSVI.getValidity();
        if (attributePSVI instanceof AttributePSVImpl) {
            final AttributePSVImpl attributePSVImpl = (AttributePSVImpl)attributePSVI;
            this.fErrors = (String[])((attributePSVImpl.fErrors != null) ? ((String[])attributePSVImpl.fErrors.clone()) : null);
        }
        else {
            final StringList errorCodes = attributePSVI.getErrorCodes();
            final int length = errorCodes.getLength();
            if (length > 0) {
                final StringList errorMessages = attributePSVI.getErrorMessages();
                final String[] fErrors = new String[length << 1];
                int i = 0;
                int n = 0;
                while (i < length) {
                    fErrors[n++] = errorCodes.item(i);
                    fErrors[n++] = errorMessages.item(i);
                    ++i;
                }
                this.fErrors = fErrors;
            }
        }
        this.fValidationContext = attributePSVI.getValidationContext();
        this.fIsConstant = fIsConstant;
    }
    
    public ItemPSVI constant() {
        if (this.isConstant()) {
            return this;
        }
        return new AttributePSVImpl(true, this);
    }
    
    public boolean isConstant() {
        return this.fIsConstant;
    }
    
    public String getSchemaDefault() {
        return (this.fDeclaration == null) ? null : this.fDeclaration.getConstraintValue();
    }
    
    public String getSchemaNormalizedValue() {
        return this.fValue.getNormalizedValue();
    }
    
    public boolean getIsSchemaSpecified() {
        return this.fSpecified;
    }
    
    public short getValidationAttempted() {
        return this.fValidationAttempted;
    }
    
    public short getValidity() {
        return this.fValidity;
    }
    
    public StringList getErrorCodes() {
        if (this.fErrors == null || this.fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(this.fErrors, true);
    }
    
    public StringList getErrorMessages() {
        if (this.fErrors == null || this.fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(this.fErrors, false);
    }
    
    public String getValidationContext() {
        return this.fValidationContext;
    }
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDecl;
    }
    
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.fValue.getMemberTypeDefinition();
    }
    
    public XSAttributeDeclaration getAttributeDeclaration() {
        return this.fDeclaration;
    }
    
    public Object getActualNormalizedValue() {
        return this.fValue.getActualValue();
    }
    
    public short getActualNormalizedValueType() {
        return this.fValue.getActualValueType();
    }
    
    public ShortList getItemValueTypes() {
        return this.fValue.getListValueTypes();
    }
    
    public XSValue getSchemaValue() {
        return this.fValue;
    }
    
    public void reset() {
        this.fValue.reset();
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
    }
}
