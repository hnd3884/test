package org.etsi.uri.x01903.v13.impl;

import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CRLRefType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CRLRefTypeImpl extends XmlComplexContentImpl implements CRLRefType
{
    private static final long serialVersionUID = 1L;
    private static final QName DIGESTALGANDVALUE$0;
    private static final QName CRLIDENTIFIER$2;
    
    public CRLRefTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public DigestAlgAndValueType getDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DigestAlgAndValueType digestAlgAndValueType = (DigestAlgAndValueType)this.get_store().find_element_user(CRLRefTypeImpl.DIGESTALGANDVALUE$0, 0);
            if (digestAlgAndValueType == null) {
                return null;
            }
            return digestAlgAndValueType;
        }
    }
    
    public void setDigestAlgAndValue(final DigestAlgAndValueType digestAlgAndValueType) {
        this.generatedSetterHelperImpl((XmlObject)digestAlgAndValueType, CRLRefTypeImpl.DIGESTALGANDVALUE$0, 0, (short)1);
    }
    
    public DigestAlgAndValueType addNewDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestAlgAndValueType)this.get_store().add_element_user(CRLRefTypeImpl.DIGESTALGANDVALUE$0);
        }
    }
    
    public CRLIdentifierType getCRLIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CRLIdentifierType crlIdentifierType = (CRLIdentifierType)this.get_store().find_element_user(CRLRefTypeImpl.CRLIDENTIFIER$2, 0);
            if (crlIdentifierType == null) {
                return null;
            }
            return crlIdentifierType;
        }
    }
    
    public boolean isSetCRLIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CRLRefTypeImpl.CRLIDENTIFIER$2) != 0;
        }
    }
    
    public void setCRLIdentifier(final CRLIdentifierType crlIdentifierType) {
        this.generatedSetterHelperImpl((XmlObject)crlIdentifierType, CRLRefTypeImpl.CRLIDENTIFIER$2, 0, (short)1);
    }
    
    public CRLIdentifierType addNewCRLIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CRLIdentifierType)this.get_store().add_element_user(CRLRefTypeImpl.CRLIDENTIFIER$2);
        }
    }
    
    public void unsetCRLIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CRLRefTypeImpl.CRLIDENTIFIER$2, 0);
        }
    }
    
    static {
        DIGESTALGANDVALUE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "DigestAlgAndValue");
        CRLIDENTIFIER$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLIdentifier");
    }
}
