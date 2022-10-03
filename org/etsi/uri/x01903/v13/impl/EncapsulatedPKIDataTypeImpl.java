package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.apache.xmlbeans.impl.values.JavaBase64HolderEx;

public class EncapsulatedPKIDataTypeImpl extends JavaBase64HolderEx implements EncapsulatedPKIDataType
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName ENCODING$2;
    
    public EncapsulatedPKIDataTypeImpl(final SchemaType schemaType) {
        super(schemaType, true);
    }
    
    protected EncapsulatedPKIDataTypeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(EncapsulatedPKIDataTypeImpl.ID$0);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(EncapsulatedPKIDataTypeImpl.ID$0);
        }
    }
    
    public String getEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
        }
    }
    
    public boolean isSetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2) != null;
        }
    }
    
    public void setEncoding(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEncoding(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(EncapsulatedPKIDataTypeImpl.ENCODING$2);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(EncapsulatedPKIDataTypeImpl.ENCODING$2);
        }
    }
    
    static {
        ID$0 = new QName("", "Id");
        ENCODING$2 = new QName("", "Encoding");
    }
}
