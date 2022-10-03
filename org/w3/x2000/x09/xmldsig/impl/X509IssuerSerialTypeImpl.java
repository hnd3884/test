package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class X509IssuerSerialTypeImpl extends XmlComplexContentImpl implements X509IssuerSerialType
{
    private static final long serialVersionUID = 1L;
    private static final QName X509ISSUERNAME$0;
    private static final QName X509SERIALNUMBER$2;
    
    public X509IssuerSerialTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getX509IssuerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetX509IssuerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0, 0);
        }
    }
    
    public void setX509IssuerName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetX509IssuerName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(X509IssuerSerialTypeImpl.X509ISSUERNAME$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public BigInteger getX509SerialNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetX509SerialNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2, 0);
        }
    }
    
    public void setX509SerialNumber(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetX509SerialNumber(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2, 0);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_element_user(X509IssuerSerialTypeImpl.X509SERIALNUMBER$2);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    static {
        X509ISSUERNAME$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "X509IssuerName");
        X509SERIALNUMBER$2 = new QName("http://www.w3.org/2000/09/xmldsig#", "X509SerialNumber");
    }
}
