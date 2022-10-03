package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleInfoImpl extends XmlComplexContentImpl implements CTTableStyleInfo
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName SHOWFIRSTCOLUMN$2;
    private static final QName SHOWLASTCOLUMN$4;
    private static final QName SHOWROWSTRIPES$6;
    private static final QName SHOWCOLUMNSTRIPES$8;
    
    public CTTableStyleInfoImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableStyleInfoImpl.NAME$0);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleInfoImpl.NAME$0) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleInfoImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableStyleInfoImpl.NAME$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableStyleInfoImpl.NAME$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleInfoImpl.NAME$0);
        }
    }
    
    public boolean getShowFirstColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowFirstColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
        }
    }
    
    public boolean isSetShowFirstColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2) != null;
        }
    }
    
    public void setShowFirstColumn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowFirstColumn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowFirstColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleInfoImpl.SHOWFIRSTCOLUMN$2);
        }
    }
    
    public boolean getShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
        }
    }
    
    public boolean isSetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4) != null;
        }
    }
    
    public void setShowLastColumn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowLastColumn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleInfoImpl.SHOWLASTCOLUMN$4);
        }
    }
    
    public boolean getShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
        }
    }
    
    public boolean isSetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6) != null;
        }
    }
    
    public void setShowRowStripes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowRowStripes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleInfoImpl.SHOWROWSTRIPES$6);
        }
    }
    
    public boolean getShowColumnStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowColumnStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
        }
    }
    
    public boolean isSetShowColumnStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8) != null;
        }
    }
    
    public void setShowColumnStripes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowColumnStripes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowColumnStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleInfoImpl.SHOWCOLUMNSTRIPES$8);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        SHOWFIRSTCOLUMN$2 = new QName("", "showFirstColumn");
        SHOWLASTCOLUMN$4 = new QName("", "showLastColumn");
        SHOWROWSTRIPES$6 = new QName("", "showRowStripes");
        SHOWCOLUMNSTRIPES$8 = new QName("", "showColumnStripes");
    }
}
