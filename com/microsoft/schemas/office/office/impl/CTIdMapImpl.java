package com.microsoft.schemas.office.office.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.office.CTIdMap;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIdMapImpl extends XmlComplexContentImpl implements CTIdMap
{
    private static final long serialVersionUID = 1L;
    private static final QName EXT$0;
    private static final QName DATA$2;
    
    public CTIdMapImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STExt.Enum getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIdMapImpl.EXT$0);
            if (simpleValue == null) {
                return null;
            }
            return (STExt.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STExt xgetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STExt)this.get_store().find_attribute_user(CTIdMapImpl.EXT$0);
        }
    }
    
    public boolean isSetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIdMapImpl.EXT$0) != null;
        }
    }
    
    public void setExt(final STExt.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIdMapImpl.EXT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIdMapImpl.EXT$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetExt(final STExt stExt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STExt stExt2 = (STExt)this.get_store().find_attribute_user(CTIdMapImpl.EXT$0);
            if (stExt2 == null) {
                stExt2 = (STExt)this.get_store().add_attribute_user(CTIdMapImpl.EXT$0);
            }
            stExt2.set((XmlObject)stExt);
        }
    }
    
    public void unsetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIdMapImpl.EXT$0);
        }
    }
    
    public String getData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIdMapImpl.DATA$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTIdMapImpl.DATA$2);
        }
    }
    
    public boolean isSetData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIdMapImpl.DATA$2) != null;
        }
    }
    
    public void setData(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIdMapImpl.DATA$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIdMapImpl.DATA$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetData(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTIdMapImpl.DATA$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTIdMapImpl.DATA$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIdMapImpl.DATA$2);
        }
    }
    
    static {
        EXT$0 = new QName("urn:schemas-microsoft-com:vml", "ext");
        DATA$2 = new QName("", "data");
    }
}
