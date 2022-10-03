package org.etsi.uri.x01903.v13.impl;

import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.OCSPIdentifierType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.OCSPRefType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class OCSPRefTypeImpl extends XmlComplexContentImpl implements OCSPRefType
{
    private static final long serialVersionUID = 1L;
    private static final QName OCSPIDENTIFIER$0;
    private static final QName DIGESTALGANDVALUE$2;
    
    public OCSPRefTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public OCSPIdentifierType getOCSPIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OCSPIdentifierType ocspIdentifierType = (OCSPIdentifierType)this.get_store().find_element_user(OCSPRefTypeImpl.OCSPIDENTIFIER$0, 0);
            if (ocspIdentifierType == null) {
                return null;
            }
            return ocspIdentifierType;
        }
    }
    
    public void setOCSPIdentifier(final OCSPIdentifierType ocspIdentifierType) {
        this.generatedSetterHelperImpl((XmlObject)ocspIdentifierType, OCSPRefTypeImpl.OCSPIDENTIFIER$0, 0, (short)1);
    }
    
    public OCSPIdentifierType addNewOCSPIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OCSPIdentifierType)this.get_store().add_element_user(OCSPRefTypeImpl.OCSPIDENTIFIER$0);
        }
    }
    
    public DigestAlgAndValueType getDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DigestAlgAndValueType digestAlgAndValueType = (DigestAlgAndValueType)this.get_store().find_element_user(OCSPRefTypeImpl.DIGESTALGANDVALUE$2, 0);
            if (digestAlgAndValueType == null) {
                return null;
            }
            return digestAlgAndValueType;
        }
    }
    
    public boolean isSetDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(OCSPRefTypeImpl.DIGESTALGANDVALUE$2) != 0;
        }
    }
    
    public void setDigestAlgAndValue(final DigestAlgAndValueType digestAlgAndValueType) {
        this.generatedSetterHelperImpl((XmlObject)digestAlgAndValueType, OCSPRefTypeImpl.DIGESTALGANDVALUE$2, 0, (short)1);
    }
    
    public DigestAlgAndValueType addNewDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestAlgAndValueType)this.get_store().add_element_user(OCSPRefTypeImpl.DIGESTALGANDVALUE$2);
        }
    }
    
    public void unsetDigestAlgAndValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(OCSPRefTypeImpl.DIGESTALGANDVALUE$2, 0);
        }
    }
    
    static {
        OCSPIDENTIFIER$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OCSPIdentifier");
        DIGESTALGANDVALUE$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "DigestAlgAndValue");
    }
}
