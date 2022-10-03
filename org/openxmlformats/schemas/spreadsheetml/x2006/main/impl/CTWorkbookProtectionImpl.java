package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTWorkbookProtectionImpl extends XmlComplexContentImpl implements CTWorkbookProtection
{
    private static final long serialVersionUID = 1L;
    private static final QName WORKBOOKPASSWORD$0;
    private static final QName REVISIONSPASSWORD$2;
    private static final QName LOCKSTRUCTURE$4;
    private static final QName LOCKWINDOWS$6;
    private static final QName LOCKREVISION$8;
    
    public CTWorkbookProtectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getWorkbookPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUnsignedShortHex xgetWorkbookPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnsignedShortHex)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
        }
    }
    
    public boolean isSetWorkbookPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0) != null;
        }
    }
    
    public void setWorkbookPassword(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetWorkbookPassword(final STUnsignedShortHex stUnsignedShortHex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnsignedShortHex stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
            if (stUnsignedShortHex2 == null) {
                stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
            }
            stUnsignedShortHex2.set((XmlObject)stUnsignedShortHex);
        }
    }
    
    public void unsetWorkbookPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookProtectionImpl.WORKBOOKPASSWORD$0);
        }
    }
    
    public byte[] getRevisionsPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUnsignedShortHex xgetRevisionsPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnsignedShortHex)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
        }
    }
    
    public boolean isSetRevisionsPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2) != null;
        }
    }
    
    public void setRevisionsPassword(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRevisionsPassword(final STUnsignedShortHex stUnsignedShortHex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnsignedShortHex stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
            if (stUnsignedShortHex2 == null) {
                stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
            }
            stUnsignedShortHex2.set((XmlObject)stUnsignedShortHex);
        }
    }
    
    public void unsetRevisionsPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookProtectionImpl.REVISIONSPASSWORD$2);
        }
    }
    
    public boolean getLockStructure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLockStructure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLockStructure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4) != null;
        }
    }
    
    public void setLockStructure(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLockStructure(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLockStructure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookProtectionImpl.LOCKSTRUCTURE$4);
        }
    }
    
    public boolean getLockWindows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLockWindows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLockWindows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6) != null;
        }
    }
    
    public void setLockWindows(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLockWindows(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLockWindows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookProtectionImpl.LOCKWINDOWS$6);
        }
    }
    
    public boolean getLockRevision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKREVISION$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLockRevision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookProtectionImpl.LOCKREVISION$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLockRevision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8) != null;
        }
    }
    
    public void setLockRevision(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLockRevision(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookProtectionImpl.LOCKREVISION$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLockRevision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookProtectionImpl.LOCKREVISION$8);
        }
    }
    
    static {
        WORKBOOKPASSWORD$0 = new QName("", "workbookPassword");
        REVISIONSPASSWORD$2 = new QName("", "revisionsPassword");
        LOCKSTRUCTURE$4 = new QName("", "lockStructure");
        LOCKWINDOWS$6 = new QName("", "lockWindows");
        LOCKREVISION$8 = new QName("", "lockRevision");
    }
}
