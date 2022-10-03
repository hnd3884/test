package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHMergeImpl extends XmlComplexContentImpl implements CTHMerge
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTHMergeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STMerge.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHMergeImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STMerge.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STMerge xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STMerge)this.get_store().find_attribute_user(CTHMergeImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHMergeImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STMerge.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHMergeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHMergeImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STMerge stMerge) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STMerge stMerge2 = (STMerge)this.get_store().find_attribute_user(CTHMergeImpl.VAL$0);
            if (stMerge2 == null) {
                stMerge2 = (STMerge)this.get_store().add_attribute_user(CTHMergeImpl.VAL$0);
            }
            stMerge2.set((XmlObject)stMerge);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHMergeImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
