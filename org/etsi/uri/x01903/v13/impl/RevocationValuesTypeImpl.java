package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.OtherCertStatusValuesType;
import org.etsi.uri.x01903.v13.OCSPValuesType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.RevocationValuesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class RevocationValuesTypeImpl extends XmlComplexContentImpl implements RevocationValuesType
{
    private static final long serialVersionUID = 1L;
    private static final QName CRLVALUES$0;
    private static final QName OCSPVALUES$2;
    private static final QName OTHERVALUES$4;
    private static final QName ID$6;
    
    public RevocationValuesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CRLValuesType getCRLValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CRLValuesType crlValuesType = (CRLValuesType)this.get_store().find_element_user(RevocationValuesTypeImpl.CRLVALUES$0, 0);
            if (crlValuesType == null) {
                return null;
            }
            return crlValuesType;
        }
    }
    
    public boolean isSetCRLValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RevocationValuesTypeImpl.CRLVALUES$0) != 0;
        }
    }
    
    public void setCRLValues(final CRLValuesType crlValuesType) {
        this.generatedSetterHelperImpl((XmlObject)crlValuesType, RevocationValuesTypeImpl.CRLVALUES$0, 0, (short)1);
    }
    
    public CRLValuesType addNewCRLValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CRLValuesType)this.get_store().add_element_user(RevocationValuesTypeImpl.CRLVALUES$0);
        }
    }
    
    public void unsetCRLValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RevocationValuesTypeImpl.CRLVALUES$0, 0);
        }
    }
    
    public OCSPValuesType getOCSPValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OCSPValuesType ocspValuesType = (OCSPValuesType)this.get_store().find_element_user(RevocationValuesTypeImpl.OCSPVALUES$2, 0);
            if (ocspValuesType == null) {
                return null;
            }
            return ocspValuesType;
        }
    }
    
    public boolean isSetOCSPValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RevocationValuesTypeImpl.OCSPVALUES$2) != 0;
        }
    }
    
    public void setOCSPValues(final OCSPValuesType ocspValuesType) {
        this.generatedSetterHelperImpl((XmlObject)ocspValuesType, RevocationValuesTypeImpl.OCSPVALUES$2, 0, (short)1);
    }
    
    public OCSPValuesType addNewOCSPValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OCSPValuesType)this.get_store().add_element_user(RevocationValuesTypeImpl.OCSPVALUES$2);
        }
    }
    
    public void unsetOCSPValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RevocationValuesTypeImpl.OCSPVALUES$2, 0);
        }
    }
    
    public OtherCertStatusValuesType getOtherValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OtherCertStatusValuesType otherCertStatusValuesType = (OtherCertStatusValuesType)this.get_store().find_element_user(RevocationValuesTypeImpl.OTHERVALUES$4, 0);
            if (otherCertStatusValuesType == null) {
                return null;
            }
            return otherCertStatusValuesType;
        }
    }
    
    public boolean isSetOtherValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RevocationValuesTypeImpl.OTHERVALUES$4) != 0;
        }
    }
    
    public void setOtherValues(final OtherCertStatusValuesType otherCertStatusValuesType) {
        this.generatedSetterHelperImpl((XmlObject)otherCertStatusValuesType, RevocationValuesTypeImpl.OTHERVALUES$4, 0, (short)1);
    }
    
    public OtherCertStatusValuesType addNewOtherValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OtherCertStatusValuesType)this.get_store().add_element_user(RevocationValuesTypeImpl.OTHERVALUES$4);
        }
    }
    
    public void unsetOtherValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RevocationValuesTypeImpl.OTHERVALUES$4, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RevocationValuesTypeImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(RevocationValuesTypeImpl.ID$6);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RevocationValuesTypeImpl.ID$6) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RevocationValuesTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RevocationValuesTypeImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(RevocationValuesTypeImpl.ID$6);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(RevocationValuesTypeImpl.ID$6);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RevocationValuesTypeImpl.ID$6);
        }
    }
    
    static {
        CRLVALUES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLValues");
        OCSPVALUES$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OCSPValues");
        OTHERVALUES$4 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OtherValues");
        ID$6 = new QName("", "Id");
    }
}
