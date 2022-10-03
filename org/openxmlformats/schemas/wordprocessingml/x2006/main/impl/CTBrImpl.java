package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrClear;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBrImpl extends XmlComplexContentImpl implements CTBr
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    private static final QName CLEAR$2;
    
    public CTBrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STBrType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBrImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STBrType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBrType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBrType)this.get_store().find_attribute_user(CTBrImpl.TYPE$0);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBrImpl.TYPE$0) != null;
        }
    }
    
    public void setType(final STBrType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBrImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBrImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STBrType stBrType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBrType stBrType2 = (STBrType)this.get_store().find_attribute_user(CTBrImpl.TYPE$0);
            if (stBrType2 == null) {
                stBrType2 = (STBrType)this.get_store().add_attribute_user(CTBrImpl.TYPE$0);
            }
            stBrType2.set((XmlObject)stBrType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBrImpl.TYPE$0);
        }
    }
    
    public STBrClear.Enum getClear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBrImpl.CLEAR$2);
            if (simpleValue == null) {
                return null;
            }
            return (STBrClear.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBrClear xgetClear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBrClear)this.get_store().find_attribute_user(CTBrImpl.CLEAR$2);
        }
    }
    
    public boolean isSetClear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBrImpl.CLEAR$2) != null;
        }
    }
    
    public void setClear(final STBrClear.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBrImpl.CLEAR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBrImpl.CLEAR$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetClear(final STBrClear stBrClear) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBrClear stBrClear2 = (STBrClear)this.get_store().find_attribute_user(CTBrImpl.CLEAR$2);
            if (stBrClear2 == null) {
                stBrClear2 = (STBrClear)this.get_store().add_attribute_user(CTBrImpl.CLEAR$2);
            }
            stBrClear2.set((XmlObject)stBrClear);
        }
    }
    
    public void unsetClear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBrImpl.CLEAR$2);
        }
    }
    
    static {
        TYPE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
        CLEAR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "clear");
    }
}
