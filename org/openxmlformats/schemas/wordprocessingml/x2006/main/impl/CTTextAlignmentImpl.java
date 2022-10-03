package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextAlignment;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextAlignmentImpl extends XmlComplexContentImpl implements CTTextAlignment
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextAlignmentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTextAlignment.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAlignmentImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STTextAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextAlignment xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextAlignment)this.get_store().find_attribute_user(CTTextAlignmentImpl.VAL$0);
        }
    }
    
    public void setVal(final STTextAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAlignmentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextAlignmentImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STTextAlignment stTextAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAlignment stTextAlignment2 = (STTextAlignment)this.get_store().find_attribute_user(CTTextAlignmentImpl.VAL$0);
            if (stTextAlignment2 == null) {
                stTextAlignment2 = (STTextAlignment)this.get_store().add_attribute_user(CTTextAlignmentImpl.VAL$0);
            }
            stTextAlignment2.set((XmlObject)stTextAlignment);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
