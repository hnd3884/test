package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotTableStyleImpl extends XmlComplexContentImpl implements CTPivotTableStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName SHOWROWHEADERS$2;
    private static final QName SHOWCOLHEADERS$4;
    private static final QName SHOWROWSTRIPES$6;
    private static final QName SHOWCOLSTRIPES$8;
    private static final QName SHOWLASTCOLUMN$10;
    
    public CTPivotTableStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPivotTableStyleImpl.NAME$0);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.NAME$0) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPivotTableStyleImpl.NAME$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPivotTableStyleImpl.NAME$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.NAME$0);
        }
    }
    
    public boolean getShowRowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowRowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
        }
    }
    
    public boolean isSetShowRowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2) != null;
        }
    }
    
    public void setShowRowHeaders(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowRowHeaders(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowRowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.SHOWROWHEADERS$2);
        }
    }
    
    public boolean getShowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
        }
    }
    
    public boolean isSetShowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4) != null;
        }
    }
    
    public void setShowColHeaders(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowColHeaders(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.SHOWCOLHEADERS$4);
        }
    }
    
    public boolean getShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
        }
    }
    
    public boolean isSetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6) != null;
        }
    }
    
    public void setShowRowStripes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowRowStripes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowRowStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.SHOWROWSTRIPES$6);
        }
    }
    
    public boolean getShowColStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowColStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
        }
    }
    
    public boolean isSetShowColStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8) != null;
        }
    }
    
    public void setShowColStripes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowColStripes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowColStripes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.SHOWCOLSTRIPES$8);
        }
    }
    
    public boolean getShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
        }
    }
    
    public boolean isSetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10) != null;
        }
    }
    
    public void setShowLastColumn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowLastColumn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowLastColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableStyleImpl.SHOWLASTCOLUMN$10);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        SHOWROWHEADERS$2 = new QName("", "showRowHeaders");
        SHOWCOLHEADERS$4 = new QName("", "showColHeaders");
        SHOWROWSTRIPES$6 = new QName("", "showRowStripes");
        SHOWCOLSTRIPES$8 = new QName("", "showColStripes");
        SHOWLASTCOLUMN$10 = new QName("", "showLastColumn");
    }
}
