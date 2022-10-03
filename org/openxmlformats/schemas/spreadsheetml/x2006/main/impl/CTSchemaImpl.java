package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSchemaImpl extends XmlComplexContentImpl implements CTSchema
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName SCHEMAREF$2;
    private static final QName NAMESPACE$4;
    
    public CTSchemaImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.ID$0);
        }
    }
    
    public void setID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSchemaImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.ID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTSchemaImpl.ID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public String getSchemaRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.SCHEMAREF$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSchemaRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.SCHEMAREF$2);
        }
    }
    
    public boolean isSetSchemaRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSchemaImpl.SCHEMAREF$2) != null;
        }
    }
    
    public void setSchemaRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.SCHEMAREF$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSchemaImpl.SCHEMAREF$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSchemaRef(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.SCHEMAREF$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTSchemaImpl.SCHEMAREF$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSchemaRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSchemaImpl.SCHEMAREF$2);
        }
    }
    
    public String getNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.NAMESPACE$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.NAMESPACE$4);
        }
    }
    
    public boolean isSetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSchemaImpl.NAMESPACE$4) != null;
        }
    }
    
    public void setNamespace(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemaImpl.NAMESPACE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSchemaImpl.NAMESPACE$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetNamespace(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTSchemaImpl.NAMESPACE$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTSchemaImpl.NAMESPACE$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSchemaImpl.NAMESPACE$4);
        }
    }
    
    static {
        ID$0 = new QName("", "ID");
        SCHEMAREF$2 = new QName("", "SchemaRef");
        NAMESPACE$4 = new QName("", "Namespace");
    }
}
