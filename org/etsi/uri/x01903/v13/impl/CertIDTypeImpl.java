package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CertIDType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CertIDTypeImpl extends XmlComplexContentImpl implements CertIDType
{
    private static final long serialVersionUID = 1L;
    private static final QName CERTDIGEST$0;
    private static final QName ISSUERSERIAL$2;
    private static final QName URI$4;
    
    public CertIDTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public DigestAlgAndValueType getCertDigest() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DigestAlgAndValueType digestAlgAndValueType = (DigestAlgAndValueType)this.get_store().find_element_user(CertIDTypeImpl.CERTDIGEST$0, 0);
            if (digestAlgAndValueType == null) {
                return null;
            }
            return digestAlgAndValueType;
        }
    }
    
    public void setCertDigest(final DigestAlgAndValueType digestAlgAndValueType) {
        this.generatedSetterHelperImpl((XmlObject)digestAlgAndValueType, CertIDTypeImpl.CERTDIGEST$0, 0, (short)1);
    }
    
    public DigestAlgAndValueType addNewCertDigest() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestAlgAndValueType)this.get_store().add_element_user(CertIDTypeImpl.CERTDIGEST$0);
        }
    }
    
    public X509IssuerSerialType getIssuerSerial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final X509IssuerSerialType x509IssuerSerialType = (X509IssuerSerialType)this.get_store().find_element_user(CertIDTypeImpl.ISSUERSERIAL$2, 0);
            if (x509IssuerSerialType == null) {
                return null;
            }
            return x509IssuerSerialType;
        }
    }
    
    public void setIssuerSerial(final X509IssuerSerialType x509IssuerSerialType) {
        this.generatedSetterHelperImpl((XmlObject)x509IssuerSerialType, CertIDTypeImpl.ISSUERSERIAL$2, 0, (short)1);
    }
    
    public X509IssuerSerialType addNewIssuerSerial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (X509IssuerSerialType)this.get_store().add_element_user(CertIDTypeImpl.ISSUERSERIAL$2);
        }
    }
    
    public String getURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CertIDTypeImpl.URI$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(CertIDTypeImpl.URI$4);
        }
    }
    
    public boolean isSetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CertIDTypeImpl.URI$4) != null;
        }
    }
    
    public void setURI(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CertIDTypeImpl.URI$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CertIDTypeImpl.URI$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetURI(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(CertIDTypeImpl.URI$4);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(CertIDTypeImpl.URI$4);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CertIDTypeImpl.URI$4);
        }
    }
    
    static {
        CERTDIGEST$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CertDigest");
        ISSUERSERIAL$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "IssuerSerial");
        URI$4 = new QName("", "URI");
    }
}
