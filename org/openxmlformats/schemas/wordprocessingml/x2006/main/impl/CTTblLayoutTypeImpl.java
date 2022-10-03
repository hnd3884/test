package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblLayoutTypeImpl extends XmlComplexContentImpl implements CTTblLayoutType
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    
    public CTTblLayoutTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTblLayoutType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STTblLayoutType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTblLayoutType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTblLayoutType)this.get_store().find_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTblLayoutTypeImpl.TYPE$0) != null;
        }
    }
    
    public void setType(final STTblLayoutType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STTblLayoutType stTblLayoutType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTblLayoutType stTblLayoutType2 = (STTblLayoutType)this.get_store().find_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
            if (stTblLayoutType2 == null) {
                stTblLayoutType2 = (STTblLayoutType)this.get_store().add_attribute_user(CTTblLayoutTypeImpl.TYPE$0);
            }
            stTblLayoutType2.set((XmlObject)stTblLayoutType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTblLayoutTypeImpl.TYPE$0);
        }
    }
    
    static {
        TYPE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
    }
}
