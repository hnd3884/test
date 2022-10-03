package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.OtherCertStatusRefsType;
import org.etsi.uri.x01903.v13.OCSPRefsType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.CRLRefsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CompleteRevocationRefsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CompleteRevocationRefsTypeImpl extends XmlComplexContentImpl implements CompleteRevocationRefsType
{
    private static final long serialVersionUID = 1L;
    private static final QName CRLREFS$0;
    private static final QName OCSPREFS$2;
    private static final QName OTHERREFS$4;
    private static final QName ID$6;
    
    public CompleteRevocationRefsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CRLRefsType getCRLRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CRLRefsType crlRefsType = (CRLRefsType)this.get_store().find_element_user(CompleteRevocationRefsTypeImpl.CRLREFS$0, 0);
            if (crlRefsType == null) {
                return null;
            }
            return crlRefsType;
        }
    }
    
    public boolean isSetCRLRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CompleteRevocationRefsTypeImpl.CRLREFS$0) != 0;
        }
    }
    
    public void setCRLRefs(final CRLRefsType crlRefsType) {
        this.generatedSetterHelperImpl((XmlObject)crlRefsType, CompleteRevocationRefsTypeImpl.CRLREFS$0, 0, (short)1);
    }
    
    public CRLRefsType addNewCRLRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CRLRefsType)this.get_store().add_element_user(CompleteRevocationRefsTypeImpl.CRLREFS$0);
        }
    }
    
    public void unsetCRLRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CompleteRevocationRefsTypeImpl.CRLREFS$0, 0);
        }
    }
    
    public OCSPRefsType getOCSPRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OCSPRefsType ocspRefsType = (OCSPRefsType)this.get_store().find_element_user(CompleteRevocationRefsTypeImpl.OCSPREFS$2, 0);
            if (ocspRefsType == null) {
                return null;
            }
            return ocspRefsType;
        }
    }
    
    public boolean isSetOCSPRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CompleteRevocationRefsTypeImpl.OCSPREFS$2) != 0;
        }
    }
    
    public void setOCSPRefs(final OCSPRefsType ocspRefsType) {
        this.generatedSetterHelperImpl((XmlObject)ocspRefsType, CompleteRevocationRefsTypeImpl.OCSPREFS$2, 0, (short)1);
    }
    
    public OCSPRefsType addNewOCSPRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OCSPRefsType)this.get_store().add_element_user(CompleteRevocationRefsTypeImpl.OCSPREFS$2);
        }
    }
    
    public void unsetOCSPRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CompleteRevocationRefsTypeImpl.OCSPREFS$2, 0);
        }
    }
    
    public OtherCertStatusRefsType getOtherRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OtherCertStatusRefsType otherCertStatusRefsType = (OtherCertStatusRefsType)this.get_store().find_element_user(CompleteRevocationRefsTypeImpl.OTHERREFS$4, 0);
            if (otherCertStatusRefsType == null) {
                return null;
            }
            return otherCertStatusRefsType;
        }
    }
    
    public boolean isSetOtherRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CompleteRevocationRefsTypeImpl.OTHERREFS$4) != 0;
        }
    }
    
    public void setOtherRefs(final OtherCertStatusRefsType otherCertStatusRefsType) {
        this.generatedSetterHelperImpl((XmlObject)otherCertStatusRefsType, CompleteRevocationRefsTypeImpl.OTHERREFS$4, 0, (short)1);
    }
    
    public OtherCertStatusRefsType addNewOtherRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OtherCertStatusRefsType)this.get_store().add_element_user(CompleteRevocationRefsTypeImpl.OTHERREFS$4);
        }
    }
    
    public void unsetOtherRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CompleteRevocationRefsTypeImpl.OTHERREFS$4, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CompleteRevocationRefsTypeImpl.ID$6) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(CompleteRevocationRefsTypeImpl.ID$6);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CompleteRevocationRefsTypeImpl.ID$6);
        }
    }
    
    static {
        CRLREFS$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLRefs");
        OCSPREFS$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OCSPRefs");
        OTHERREFS$4 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OtherRefs");
        ID$6 = new QName("", "Id");
    }
}
