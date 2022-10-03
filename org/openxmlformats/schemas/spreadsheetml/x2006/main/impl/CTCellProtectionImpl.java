package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellProtection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCellProtectionImpl extends XmlComplexContentImpl implements CTCellProtection
{
    private static final long serialVersionUID = 1L;
    private static final QName LOCKED$0;
    private static final QName HIDDEN$2;
    
    public CTCellProtectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellProtectionImpl.LOCKED$0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCellProtectionImpl.LOCKED$0);
        }
    }
    
    public boolean isSetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellProtectionImpl.LOCKED$0) != null;
        }
    }
    
    public void setLocked(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellProtectionImpl.LOCKED$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellProtectionImpl.LOCKED$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLocked(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellProtectionImpl.LOCKED$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellProtectionImpl.LOCKED$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellProtectionImpl.LOCKED$0);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellProtectionImpl.HIDDEN$2);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCellProtectionImpl.HIDDEN$2);
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellProtectionImpl.HIDDEN$2) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellProtectionImpl.HIDDEN$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellProtectionImpl.HIDDEN$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellProtectionImpl.HIDDEN$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellProtectionImpl.HIDDEN$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellProtectionImpl.HIDDEN$2);
        }
    }
    
    static {
        LOCKED$0 = new QName("", "locked");
        HIDDEN$2 = new QName("", "hidden");
    }
}
