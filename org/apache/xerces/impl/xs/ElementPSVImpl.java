package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSTypeAlternative;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.ElementPSVI;

public class ElementPSVImpl implements ElementPSVI
{
    protected XSElementDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fNil;
    protected boolean fSpecified;
    protected ValidatedInfo fValue;
    protected XSNotationDeclaration fNotation;
    protected short fValidationAttempted;
    protected short fValidity;
    protected String[] fErrors;
    protected String fValidationContext;
    protected SchemaGrammar[] fGrammars;
    protected XSModel fSchemaInformation;
    protected boolean fIsConstant;
    protected ObjectList fInheritedAttributes;
    protected ObjectList fFailedAssertions;
    protected XSTypeAlternative fTypeAlternative;
    
    public ElementPSVImpl() {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fValue = new ValidatedInfo();
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
        this.fGrammars = null;
        this.fSchemaInformation = null;
        this.fInheritedAttributes = null;
        this.fFailedAssertions = null;
        this.fTypeAlternative = null;
    }
    
    public ElementPSVImpl(final boolean fIsConstant, final ElementPSVI elementPSVI) {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fValue = new ValidatedInfo();
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
        this.fGrammars = null;
        this.fSchemaInformation = null;
        this.fInheritedAttributes = null;
        this.fFailedAssertions = null;
        this.fTypeAlternative = null;
        this.fDeclaration = elementPSVI.getElementDeclaration();
        this.fTypeDecl = elementPSVI.getTypeDefinition();
        this.fNil = elementPSVI.getNil();
        this.fSpecified = elementPSVI.getIsSchemaSpecified();
        this.fValue.copyFrom(elementPSVI.getSchemaValue());
        this.fNotation = elementPSVI.getNotation();
        this.fValidationAttempted = elementPSVI.getValidationAttempted();
        this.fValidity = elementPSVI.getValidity();
        this.fValidationContext = elementPSVI.getValidationContext();
        this.fTypeAlternative = elementPSVI.getTypeAlternative();
        this.fInheritedAttributes = elementPSVI.getInheritedAttributes();
        this.fFailedAssertions = elementPSVI.getFailedAssertions();
        if (elementPSVI instanceof ElementPSVImpl) {
            final ElementPSVImpl elementPSVImpl = (ElementPSVImpl)elementPSVI;
            this.fErrors = (String[])((elementPSVImpl.fErrors != null) ? ((String[])elementPSVImpl.fErrors.clone()) : null);
            elementPSVImpl.copySchemaInformationTo(this);
        }
        else {
            final StringList errorCodes = elementPSVI.getErrorCodes();
            final int length = errorCodes.getLength();
            if (length > 0) {
                final StringList errorMessages = elementPSVI.getErrorMessages();
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
            this.fSchemaInformation = elementPSVI.getSchemaInformation();
        }
        this.fIsConstant = fIsConstant;
    }
    
    public ItemPSVI constant() {
        if (this.isConstant()) {
            return this;
        }
        return new ElementPSVImpl(true, this);
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
    
    public boolean getNil() {
        return this.fNil;
    }
    
    public XSNotationDeclaration getNotation() {
        return this.fNotation;
    }
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDecl;
    }
    
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.fValue.getMemberTypeDefinition();
    }
    
    public XSElementDeclaration getElementDeclaration() {
        return this.fDeclaration;
    }
    
    public synchronized XSModel getSchemaInformation() {
        if (this.fSchemaInformation == null && this.fGrammars != null) {
            this.fSchemaInformation = new XSModelImpl(this.fGrammars);
        }
        return this.fSchemaInformation;
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
    
    public ObjectList getInheritedAttributes() {
        if (this.fInheritedAttributes != null) {
            return this.fInheritedAttributes;
        }
        return ObjectListImpl.EMPTY_LIST;
    }
    
    public ObjectList getFailedAssertions() {
        if (this.fFailedAssertions != null) {
            return this.fFailedAssertions;
        }
        return ObjectListImpl.EMPTY_LIST;
    }
    
    public XSTypeAlternative getTypeAlternative() {
        return this.fTypeAlternative;
    }
    
    public void reset() {
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
        this.fValue.reset();
        this.fTypeAlternative = null;
        this.fInheritedAttributes = null;
        this.fFailedAssertions = null;
    }
    
    public void copySchemaInformationTo(final ElementPSVImpl elementPSVImpl) {
        elementPSVImpl.fGrammars = this.fGrammars;
        elementPSVImpl.fSchemaInformation = this.fSchemaInformation;
    }
}
