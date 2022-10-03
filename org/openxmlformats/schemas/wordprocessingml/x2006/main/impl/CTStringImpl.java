package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStringImpl extends XmlComplexContentImpl implements CTString
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTStringImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStringImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTStringImpl.VAL$0);
        }
    }
    
    public void setVal(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStringImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStringImpl.VAL$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetVal(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTStringImpl.VAL$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTStringImpl.VAL$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
