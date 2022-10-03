package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.ObjectType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ObjectTypeImpl extends XmlComplexContentImpl implements ObjectType
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName MIMETYPE$2;
    private static final QName ENCODING$4;
    
    public ObjectTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(ObjectTypeImpl.ID$0);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ObjectTypeImpl.ID$0) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ObjectTypeImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(ObjectTypeImpl.ID$0);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(ObjectTypeImpl.ID$0);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ObjectTypeImpl.ID$0);
        }
    }
    
    public String getMimeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.MIMETYPE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMimeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ObjectTypeImpl.MIMETYPE$2);
        }
    }
    
    public boolean isSetMimeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ObjectTypeImpl.MIMETYPE$2) != null;
        }
    }
    
    public void setMimeType(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.MIMETYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ObjectTypeImpl.MIMETYPE$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMimeType(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ObjectTypeImpl.MIMETYPE$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ObjectTypeImpl.MIMETYPE$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMimeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ObjectTypeImpl.MIMETYPE$2);
        }
    }
    
    public String getEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.ENCODING$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(ObjectTypeImpl.ENCODING$4);
        }
    }
    
    public boolean isSetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ObjectTypeImpl.ENCODING$4) != null;
        }
    }
    
    public void setEncoding(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ObjectTypeImpl.ENCODING$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ObjectTypeImpl.ENCODING$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEncoding(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(ObjectTypeImpl.ENCODING$4);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(ObjectTypeImpl.ENCODING$4);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetEncoding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ObjectTypeImpl.ENCODING$4);
        }
    }
    
    static {
        ID$0 = new QName("", "Id");
        MIMETYPE$2 = new QName("", "MimeType");
        ENCODING$4 = new QName("", "Encoding");
    }
}
