package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.ResponderIDType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ResponderIDTypeImpl extends XmlComplexContentImpl implements ResponderIDType
{
    private static final long serialVersionUID = 1L;
    private static final QName BYNAME$0;
    private static final QName BYKEY$2;
    
    public ResponderIDTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ResponderIDTypeImpl.BYNAME$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(ResponderIDTypeImpl.BYNAME$0, 0);
        }
    }
    
    public boolean isSetByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ResponderIDTypeImpl.BYNAME$0) != 0;
        }
    }
    
    public void setByName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ResponderIDTypeImpl.BYNAME$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(ResponderIDTypeImpl.BYNAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetByName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(ResponderIDTypeImpl.BYNAME$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(ResponderIDTypeImpl.BYNAME$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ResponderIDTypeImpl.BYNAME$0, 0);
        }
    }
    
    public byte[] getByKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ResponderIDTypeImpl.BYKEY$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetByKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(ResponderIDTypeImpl.BYKEY$2, 0);
        }
    }
    
    public boolean isSetByKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ResponderIDTypeImpl.BYKEY$2) != 0;
        }
    }
    
    public void setByKey(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ResponderIDTypeImpl.BYKEY$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(ResponderIDTypeImpl.BYKEY$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetByKey(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(ResponderIDTypeImpl.BYKEY$2, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(ResponderIDTypeImpl.BYKEY$2);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetByKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ResponderIDTypeImpl.BYKEY$2, 0);
        }
    }
    
    static {
        BYNAME$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ByName");
        BYKEY$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ByKey");
    }
}
