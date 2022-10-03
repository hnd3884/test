package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignaturePolicyIdentifierTypeImpl extends XmlComplexContentImpl implements SignaturePolicyIdentifierType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNATUREPOLICYID$0;
    private static final QName SIGNATUREPOLICYIMPLIED$2;
    
    public SignaturePolicyIdentifierTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public SignaturePolicyIdType getSignaturePolicyId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignaturePolicyIdType signaturePolicyIdType = (SignaturePolicyIdType)this.get_store().find_element_user(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYID$0, 0);
            if (signaturePolicyIdType == null) {
                return null;
            }
            return signaturePolicyIdType;
        }
    }
    
    public boolean isSetSignaturePolicyId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYID$0) != 0;
        }
    }
    
    public void setSignaturePolicyId(final SignaturePolicyIdType signaturePolicyIdType) {
        this.generatedSetterHelperImpl((XmlObject)signaturePolicyIdType, SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYID$0, 0, (short)1);
    }
    
    public SignaturePolicyIdType addNewSignaturePolicyId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignaturePolicyIdType)this.get_store().add_element_user(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYID$0);
        }
    }
    
    public void unsetSignaturePolicyId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYID$0, 0);
        }
    }
    
    public XmlObject getSignaturePolicyImplied() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlObject xmlObject = (XmlObject)this.get_store().find_element_user(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYIMPLIED$2, 0);
            if (xmlObject == null) {
                return null;
            }
            return xmlObject;
        }
    }
    
    public boolean isSetSignaturePolicyImplied() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYIMPLIED$2) != 0;
        }
    }
    
    public void setSignaturePolicyImplied(final XmlObject xmlObject) {
        this.generatedSetterHelperImpl(xmlObject, SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYIMPLIED$2, 0, (short)1);
    }
    
    public XmlObject addNewSignaturePolicyImplied() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlObject)this.get_store().add_element_user(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYIMPLIED$2);
        }
    }
    
    public void unsetSignaturePolicyImplied() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignaturePolicyIdentifierTypeImpl.SIGNATUREPOLICYIMPLIED$2, 0);
        }
    }
    
    static {
        SIGNATUREPOLICYID$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignaturePolicyId");
        SIGNATUREPOLICYIMPLIED$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignaturePolicyImplied");
    }
}
