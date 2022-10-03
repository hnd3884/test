package com.sun.org.apache.xerces.internal.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;

public class PSVIElementNSImpl extends ElementNSImpl implements ElementPSVI
{
    static final long serialVersionUID = 6815489624636016068L;
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
    protected StringList fErrorCodes;
    protected String fValidationContext;
    protected XSModel fSchemaInformation;
    
    public PSVIElementNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) {
        super(ownerDocument, namespaceURI, qualifiedName, localName);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = true;
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
        this.fSchemaInformation = null;
    }
    
    public PSVIElementNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName) {
        super(ownerDocument, namespaceURI, qualifiedName);
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = true;
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
        return this.fErrorCodes;
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
    public XSModel getSchemaInformation() {
        return this.fSchemaInformation;
    }
    
    public void setPSVI(final ElementPSVI elem) {
        this.fDeclaration = elem.getElementDeclaration();
        this.fNotation = elem.getNotation();
        this.fValidationContext = elem.getValidationContext();
        this.fTypeDecl = elem.getTypeDefinition();
        this.fSchemaInformation = elem.getSchemaInformation();
        this.fValidity = elem.getValidity();
        this.fValidationAttempted = elem.getValidationAttempted();
        this.fErrorCodes = elem.getErrorCodes();
        this.fNormalizedValue = elem.getSchemaNormalizedValue();
        this.fActualValue = elem.getActualNormalizedValue();
        this.fActualValueType = elem.getActualNormalizedValueType();
        this.fItemValueTypes = elem.getItemValueTypes();
        this.fMemberType = elem.getMemberTypeDefinition();
        this.fSpecified = elem.getIsSchemaSpecified();
        this.fNil = elem.getNil();
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
