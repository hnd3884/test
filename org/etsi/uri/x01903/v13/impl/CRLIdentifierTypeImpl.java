package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CRLIdentifierTypeImpl extends XmlComplexContentImpl implements CRLIdentifierType
{
    private static final long serialVersionUID = 1L;
    private static final QName ISSUER$0;
    private static final QName ISSUETIME$2;
    private static final QName NUMBER$4;
    private static final QName URI$6;
    
    public CRLIdentifierTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getIssuer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUER$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetIssuer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUER$0, 0);
        }
    }
    
    public void setIssuer(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUER$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CRLIdentifierTypeImpl.ISSUER$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetIssuer(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUER$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CRLIdentifierTypeImpl.ISSUER$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public Calendar getIssueTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUETIME$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetIssueTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUETIME$2, 0);
        }
    }
    
    public void setIssueTime(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUETIME$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CRLIdentifierTypeImpl.ISSUETIME$2);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetIssueTime(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(CRLIdentifierTypeImpl.ISSUETIME$2, 0);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_element_user(CRLIdentifierTypeImpl.ISSUETIME$2);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public BigInteger getNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.NUMBER$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_element_user(CRLIdentifierTypeImpl.NUMBER$4, 0);
        }
    }
    
    public boolean isSetNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CRLIdentifierTypeImpl.NUMBER$4) != 0;
        }
    }
    
    public void setNumber(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CRLIdentifierTypeImpl.NUMBER$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CRLIdentifierTypeImpl.NUMBER$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetNumber(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CRLIdentifierTypeImpl.NUMBER$4, 0);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_element_user(CRLIdentifierTypeImpl.NUMBER$4);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CRLIdentifierTypeImpl.NUMBER$4, 0);
        }
    }
    
    public String getURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CRLIdentifierTypeImpl.URI$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(CRLIdentifierTypeImpl.URI$6);
        }
    }
    
    public boolean isSetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CRLIdentifierTypeImpl.URI$6) != null;
        }
    }
    
    public void setURI(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CRLIdentifierTypeImpl.URI$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CRLIdentifierTypeImpl.URI$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetURI(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(CRLIdentifierTypeImpl.URI$6);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(CRLIdentifierTypeImpl.URI$6);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CRLIdentifierTypeImpl.URI$6);
        }
    }
    
    static {
        ISSUER$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "Issuer");
        ISSUETIME$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "IssueTime");
        NUMBER$4 = new QName("http://uri.etsi.org/01903/v1.3.2#", "Number");
        URI$6 = new QName("", "URI");
    }
}
