package org.apache.xerces.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.AttributePSVImpl;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.AttributePSVI;

public class PSVIAttrNSImpl extends AttrNSImpl implements AttributePSVI
{
    static final long serialVersionUID = -3241738699421018889L;
    protected XSAttributeDeclaration fDeclaration;
    protected XSTypeDefinition fTypeDecl;
    protected boolean fSpecified;
    protected ValidatedInfo fValue;
    protected short fValidationAttempted;
    protected short fValidity;
    protected StringList fErrorCodes;
    protected StringList fErrorMessages;
    protected String fValidationContext;
    
    public PSVIAttrNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String s2, final String s3) {
        super(coreDocumentImpl, s, s2, s3);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = true;
        this.fValue = new ValidatedInfo();
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fErrorMessages = null;
        this.fValidationContext = null;
    }
    
    public PSVIAttrNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String s2) {
        super(coreDocumentImpl, s, s2);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = true;
        this.fValue = new ValidatedInfo();
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrorCodes = null;
        this.fErrorMessages = null;
        this.fValidationContext = null;
    }
    
    public ItemPSVI constant() {
        return new AttributePSVImpl(true, this);
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
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDecl;
    }
    
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.fValue.getMemberTypeDefinition();
    }
    
    public XSAttributeDeclaration getAttributeDeclaration() {
        return this.fDeclaration;
    }
    
    public void setPSVI(final AttributePSVI attributePSVI) {
        this.fDeclaration = attributePSVI.getAttributeDeclaration();
        this.fValidationContext = attributePSVI.getValidationContext();
        this.fValidity = attributePSVI.getValidity();
        this.fValidationAttempted = attributePSVI.getValidationAttempted();
        this.fErrorCodes = attributePSVI.getErrorCodes();
        this.fErrorMessages = attributePSVI.getErrorMessages();
        this.fValue.copyFrom(attributePSVI.getSchemaValue());
        this.fTypeDecl = attributePSVI.getTypeDefinition();
        this.fSpecified = attributePSVI.getIsSchemaSpecified();
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
