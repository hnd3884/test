package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetProtectionImpl extends XmlComplexContentImpl implements CTSheetProtection
{
    private static final long serialVersionUID = 1L;
    private static final QName PASSWORD$0;
    private static final QName SHEET$2;
    private static final QName OBJECTS$4;
    private static final QName SCENARIOS$6;
    private static final QName FORMATCELLS$8;
    private static final QName FORMATCOLUMNS$10;
    private static final QName FORMATROWS$12;
    private static final QName INSERTCOLUMNS$14;
    private static final QName INSERTROWS$16;
    private static final QName INSERTHYPERLINKS$18;
    private static final QName DELETECOLUMNS$20;
    private static final QName DELETEROWS$22;
    private static final QName SELECTLOCKEDCELLS$24;
    private static final QName SORT$26;
    private static final QName AUTOFILTER$28;
    private static final QName PIVOTTABLES$30;
    private static final QName SELECTUNLOCKEDCELLS$32;
    
    public CTSheetProtectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUnsignedShortHex xgetPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnsignedShortHex)this.get_store().find_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
        }
    }
    
    public boolean isSetPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.PASSWORD$0) != null;
        }
    }
    
    public void setPassword(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetPassword(final STUnsignedShortHex stUnsignedShortHex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnsignedShortHex stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().find_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
            if (stUnsignedShortHex2 == null) {
                stUnsignedShortHex2 = (STUnsignedShortHex)this.get_store().add_attribute_user(CTSheetProtectionImpl.PASSWORD$0);
            }
            stUnsignedShortHex2.set((XmlObject)stUnsignedShortHex);
        }
    }
    
    public void unsetPassword() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.PASSWORD$0);
        }
    }
    
    public boolean getSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SHEET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.SHEET$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SHEET$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.SHEET$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.SHEET$2) != null;
        }
    }
    
    public void setSheet(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SHEET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.SHEET$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSheet(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SHEET$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.SHEET$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.SHEET$2);
        }
    }
    
    public boolean getObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.OBJECTS$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.OBJECTS$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.OBJECTS$4) != null;
        }
    }
    
    public void setObjects(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetObjects(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.OBJECTS$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.OBJECTS$4);
        }
    }
    
    public boolean getScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.SCENARIOS$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.SCENARIOS$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.SCENARIOS$6) != null;
        }
    }
    
    public void setScenarios(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetScenarios(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.SCENARIOS$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.SCENARIOS$6);
        }
    }
    
    public boolean getFormatCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATCELLS$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFormatCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATCELLS$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFormatCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8) != null;
        }
    }
    
    public void setFormatCells(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFormatCells(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATCELLS$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFormatCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.FORMATCELLS$8);
        }
    }
    
    public boolean getFormatColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFormatColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFormatColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10) != null;
        }
    }
    
    public void setFormatColumns(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFormatColumns(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATCOLUMNS$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFormatColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.FORMATCOLUMNS$10);
        }
    }
    
    public boolean getFormatRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATROWS$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFormatRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.FORMATROWS$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFormatRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATROWS$12) != null;
        }
    }
    
    public void setFormatRows(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFormatRows(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.FORMATROWS$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFormatRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.FORMATROWS$12);
        }
    }
    
    public boolean getInsertColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14) != null;
        }
    }
    
    public void setInsertColumns(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertColumns(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTCOLUMNS$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.INSERTCOLUMNS$14);
        }
    }
    
    public boolean getInsertRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTROWS$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTROWS$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTROWS$16) != null;
        }
    }
    
    public void setInsertRows(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertRows(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTROWS$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.INSERTROWS$16);
        }
    }
    
    public boolean getInsertHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18) != null;
        }
    }
    
    public void setInsertHyperlinks(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertHyperlinks(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.INSERTHYPERLINKS$18);
        }
    }
    
    public boolean getDeleteColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.DELETECOLUMNS$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDeleteColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.DELETECOLUMNS$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDeleteColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20) != null;
        }
    }
    
    public void setDeleteColumns(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDeleteColumns(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.DELETECOLUMNS$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDeleteColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.DELETECOLUMNS$20);
        }
    }
    
    public boolean getDeleteRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.DELETEROWS$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDeleteRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.DELETEROWS$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDeleteRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETEROWS$22) != null;
        }
    }
    
    public void setDeleteRows(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDeleteRows(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.DELETEROWS$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDeleteRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.DELETEROWS$22);
        }
    }
    
    public boolean getSelectLockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSelectLockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSelectLockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24) != null;
        }
    }
    
    public void setSelectLockedCells(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSelectLockedCells(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSelectLockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.SELECTLOCKEDCELLS$24);
        }
    }
    
    public boolean getSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SORT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.SORT$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SORT$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.SORT$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.SORT$26) != null;
        }
    }
    
    public void setSort(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SORT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.SORT$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSort(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SORT$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.SORT$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.SORT$26);
        }
    }
    
    public boolean getAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.AUTOFILTER$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.AUTOFILTER$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28) != null;
        }
    }
    
    public void setAutoFilter(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoFilter(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.AUTOFILTER$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.AUTOFILTER$28);
        }
    }
    
    public boolean getPivotTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.PIVOTTABLES$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPivotTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.PIVOTTABLES$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPivotTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30) != null;
        }
    }
    
    public void setPivotTables(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPivotTables(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.PIVOTTABLES$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPivotTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.PIVOTTABLES$30);
        }
    }
    
    public boolean getSelectUnlockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSelectUnlockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSelectUnlockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32) != null;
        }
    }
    
    public void setSelectUnlockedCells(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSelectUnlockedCells(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSelectUnlockedCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetProtectionImpl.SELECTUNLOCKEDCELLS$32);
        }
    }
    
    static {
        PASSWORD$0 = new QName("", "password");
        SHEET$2 = new QName("", "sheet");
        OBJECTS$4 = new QName("", "objects");
        SCENARIOS$6 = new QName("", "scenarios");
        FORMATCELLS$8 = new QName("", "formatCells");
        FORMATCOLUMNS$10 = new QName("", "formatColumns");
        FORMATROWS$12 = new QName("", "formatRows");
        INSERTCOLUMNS$14 = new QName("", "insertColumns");
        INSERTROWS$16 = new QName("", "insertRows");
        INSERTHYPERLINKS$18 = new QName("", "insertHyperlinks");
        DELETECOLUMNS$20 = new QName("", "deleteColumns");
        DELETEROWS$22 = new QName("", "deleteRows");
        SELECTLOCKEDCELLS$24 = new QName("", "selectLockedCells");
        SORT$26 = new QName("", "sort");
        AUTOFILTER$28 = new QName("", "autoFilter");
        PIVOTTABLES$30 = new QName("", "pivotTables");
        SELECTUNLOCKEDCELLS$32 = new QName("", "selectUnlockedCells");
    }
}
