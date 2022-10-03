package com.sun.org.apache.xerces.internal.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;

public class PSVIAttrNSImpl extends AttrNSImpl implements AttributePSVI
{
    static final long serialVersionUID = -3241738699421018889L;
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
    protected StringList fErrorCodes;
    protected String fValidationContext;
    
    public PSVIAttrNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) {
        super(ownerDocument, namespaceURI, qualifiedName, localName);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = true;
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
    
    public PSVIAttrNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName) {
        super(ownerDocument, namespaceURI, qualifiedName);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = true;
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
        return this.fErrorCodes;
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
    
    public void setPSVI(final AttributePSVI attr) {
        this.fDeclaration = attr.getAttributeDeclaration();
        this.fValidationContext = attr.getValidationContext();
        this.fValidity = attr.getValidity();
        this.fValidationAttempted = attr.getValidationAttempted();
        this.fErrorCodes = attr.getErrorCodes();
        this.fNormalizedValue = attr.getSchemaNormalizedValue();
        this.fActualValue = attr.getActualNormalizedValue();
        this.fActualValueType = attr.getActualNormalizedValueType();
        this.fItemValueTypes = attr.getItemValueTypes();
        this.fTypeDecl = attr.getTypeDefinition();
        this.fMemberType = attr.getMemberTypeDefinition();
        this.fSpecified = attr.getIsSchemaSpecified();
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
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
