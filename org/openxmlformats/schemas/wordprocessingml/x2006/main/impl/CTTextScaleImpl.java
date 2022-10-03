package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScale;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextScaleImpl extends XmlComplexContentImpl implements CTTextScale
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextScaleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextScaleImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextScale xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextScale)this.get_store().find_attribute_user(CTTextScaleImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextScaleImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextScaleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextScaleImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STTextScale stTextScale) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextScale stTextScale2 = (STTextScale)this.get_store().find_attribute_user(CTTextScaleImpl.VAL$0);
            if (stTextScale2 == null) {
                stTextScale2 = (STTextScale)this.get_store().add_attribute_user(CTTextScaleImpl.VAL$0);
            }
            stTextScale2.set((XmlObject)stTextScale);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextScaleImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
