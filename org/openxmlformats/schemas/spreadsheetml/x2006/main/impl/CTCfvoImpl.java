package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCfvoImpl extends XmlComplexContentImpl implements CTCfvo
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName TYPE$2;
    private static final QName VAL$4;
    private static final QName GTE$6;
    
    public CTCfvoImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCfvoImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfvoImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCfvoImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCfvoImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfvoImpl.EXTLST$0, 0);
        }
    }
    
    public STCfvoType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.TYPE$2);
            if (simpleValue == null) {
                return null;
            }
            return (STCfvoType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCfvoType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCfvoType)this.get_store().find_attribute_user(CTCfvoImpl.TYPE$2);
        }
    }
    
    public void setType(final STCfvoType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfvoImpl.TYPE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STCfvoType stCfvoType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCfvoType stCfvoType2 = (STCfvoType)this.get_store().find_attribute_user(CTCfvoImpl.TYPE$2);
            if (stCfvoType2 == null) {
                stCfvoType2 = (STCfvoType)this.get_store().add_attribute_user(CTCfvoImpl.TYPE$2);
            }
            stCfvoType2.set((XmlObject)stCfvoType);
        }
    }
    
    public String getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.VAL$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTCfvoImpl.VAL$4);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfvoImpl.VAL$4) != null;
        }
    }
    
    public void setVal(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.VAL$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfvoImpl.VAL$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetVal(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTCfvoImpl.VAL$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTCfvoImpl.VAL$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfvoImpl.VAL$4);
        }
    }
    
    public boolean getGte() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.GTE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfvoImpl.GTE$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetGte() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfvoImpl.GTE$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfvoImpl.GTE$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetGte() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfvoImpl.GTE$6) != null;
        }
    }
    
    public void setGte(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfvoImpl.GTE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfvoImpl.GTE$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetGte(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfvoImpl.GTE$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfvoImpl.GTE$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetGte() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfvoImpl.GTE$6);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        TYPE$2 = new QName("", "type");
        VAL$4 = new QName("", "val");
        GTE$6 = new QName("", "gte");
    }
}
