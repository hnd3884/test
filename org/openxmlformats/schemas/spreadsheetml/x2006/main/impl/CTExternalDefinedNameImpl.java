package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalDefinedNameImpl extends XmlComplexContentImpl implements CTExternalDefinedName
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName REFERSTO$2;
    private static final QName SHEETID$4;
    
    public CTExternalDefinedNameImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.NAME$0);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.NAME$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.NAME$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public String getRefersTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetRefersTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
        }
    }
    
    public boolean isSetRefersTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2) != null;
        }
    }
    
    public void setRefersTo(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRefersTo(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.REFERSTO$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetRefersTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTExternalDefinedNameImpl.REFERSTO$2);
        }
    }
    
    public long getSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
        }
    }
    
    public boolean isSetSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTExternalDefinedNameImpl.SHEETID$4) != null;
        }
    }
    
    public void setSheetId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSheetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTExternalDefinedNameImpl.SHEETID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTExternalDefinedNameImpl.SHEETID$4);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        REFERSTO$2 = new QName("", "refersTo");
        SHEETID$4 = new QName("", "sheetId");
    }
}
