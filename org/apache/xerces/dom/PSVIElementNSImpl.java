package org.apache.xerces.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.ElementPSVImpl;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.XSTypeAlternative;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.ElementPSVI;

public class PSVIElementNSImpl extends ElementNSImpl implements ElementPSVI
{
    static final long serialVersionUID = 6815489624636016068L;
    protected XSElementDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fNil;
    protected boolean fSpecified;
    protected ValidatedInfo fValue;
    protected XSNotationDeclaration fNotation;
    protected short fValidationAttempted;
    protected short fValidity;
    protected StringList fErrorCodes;
    protected StringList fErrorMessages;
    protected String fValidationContext;
    protected XSModel fSchemaInformation;
    protected ObjectList fInheritedAttributes;
    protected ObjectList fFailedAssertions;
    protected XSTypeAlternative fTypeAlternative;
    
    public PSVIElementNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String s2, final String s3) {
        super(coreDocumentImpl, s, s2, s3);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = true;
        this.fValue = new ValidatedInfo();
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fErrorMessages = null;
        this.fValidationContext = null;
        this.fSchemaInformation = null;
        this.fInheritedAttributes = null;
        this.fFailedAssertions = null;
        this.fTypeAlternative = null;
    }
    
    public PSVIElementNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String s2) {
        super(coreDocumentImpl, s, s2);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = true;
        this.fValue = new ValidatedInfo();
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fErrorMessages = null;
        this.fValidationContext = null;
        this.fSchemaInformation = null;
        this.fInheritedAttributes = null;
        this.fFailedAssertions = null;
        this.fTypeAlternative = null;
    }
    
    public ItemPSVI constant() {
        return new ElementPSVImpl(true, this);
    }
    
    public boolean isConstant() {
        return false;
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
        if (this.fErrorCodes != null) {
            return this.fErrorCodes;
        }
        return StringListImpl.EMPTY_LIST;
    }
    
    public StringList getErrorMessages() {
        if (this.fErrorMessages != null) {
            return this.fErrorMessages;
        }
        return StringListImpl.EMPTY_LIST;
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
    
    public XSModel getSchemaInformation() {
        return this.fSchemaInformation;
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
    
    public void setPSVI(final ElementPSVI elementPSVI) {
        this.fDeclaration = elementPSVI.getElementDeclaration();
        this.fNotation = elementPSVI.getNotation();
        this.fValidationContext = elementPSVI.getValidationContext();
        this.fTypeDecl = elementPSVI.getTypeDefinition();
        this.fSchemaInformation = elementPSVI.getSchemaInformation();
        this.fValidity = elementPSVI.getValidity();
        this.fValidationAttempted = elementPSVI.getValidationAttempted();
        this.fErrorCodes = elementPSVI.getErrorCodes();
        this.fErrorMessages = elementPSVI.getErrorMessages();
        if (this.fTypeDecl instanceof XSSimpleTypeDefinition || (this.fTypeDecl instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)this.fTypeDecl).getContentType() == 1)) {
            this.fValue.copyFrom(elementPSVI.getSchemaValue());
        }
        else {
            this.fValue.reset();
        }
        this.fSpecified = elementPSVI.getIsSchemaSpecified();
        this.fNil = elementPSVI.getNil();
        this.fTypeAlternative = elementPSVI.getTypeAlternative();
        this.fInheritedAttributes = elementPSVI.getInheritedAttributes();
        this.fFailedAssertions = elementPSVI.getFailedAssertions();
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
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
