package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CompleteCertificateRefsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CompleteCertificateRefsTypeImpl extends XmlComplexContentImpl implements CompleteCertificateRefsType
{
    private static final long serialVersionUID = 1L;
    private static final QName CERTREFS$0;
    private static final QName ID$2;
    
    public CompleteCertificateRefsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CertIDListType getCertRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertIDListType certIDListType = (CertIDListType)this.get_store().find_element_user(CompleteCertificateRefsTypeImpl.CERTREFS$0, 0);
            if (certIDListType == null) {
                return null;
            }
            return certIDListType;
        }
    }
    
    public void setCertRefs(final CertIDListType certIDListType) {
        this.generatedSetterHelperImpl((XmlObject)certIDListType, CompleteCertificateRefsTypeImpl.CERTREFS$0, 0, (short)1);
    }
    
    public CertIDListType addNewCertRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertIDListType)this.get_store().add_element_user(CompleteCertificateRefsTypeImpl.CERTREFS$0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CompleteCertificateRefsTypeImpl.ID$2) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(CompleteCertificateRefsTypeImpl.ID$2);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CompleteCertificateRefsTypeImpl.ID$2);
        }
    }
    
    static {
        CERTREFS$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CertRefs");
        ID$2 = new QName("", "Id");
    }
}
