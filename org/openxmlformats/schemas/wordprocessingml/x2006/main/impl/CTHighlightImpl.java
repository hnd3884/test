package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHighlightImpl extends XmlComplexContentImpl implements CTHighlight
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTHighlightImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STHighlightColor.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHighlightImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STHighlightColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHighlightColor xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHighlightColor)this.get_store().find_attribute_user(CTHighlightImpl.VAL$0);
        }
    }
    
    public void setVal(final STHighlightColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHighlightImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHighlightImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STHighlightColor stHighlightColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHighlightColor stHighlightColor2 = (STHighlightColor)this.get_store().find_attribute_user(CTHighlightImpl.VAL$0);
            if (stHighlightColor2 == null) {
                stHighlightColor2 = (STHighlightColor)this.get_store().add_attribute_user(CTHighlightImpl.VAL$0);
            }
            stHighlightColor2.set((XmlObject)stHighlightColor);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
