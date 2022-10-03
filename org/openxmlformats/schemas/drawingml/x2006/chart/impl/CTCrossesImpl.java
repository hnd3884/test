package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCrossesImpl extends XmlComplexContentImpl implements CTCrosses
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTCrossesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STCrosses.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCrossesImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STCrosses.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCrosses xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCrosses)this.get_store().find_attribute_user(CTCrossesImpl.VAL$0);
        }
    }
    
    public void setVal(final STCrosses.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCrossesImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCrossesImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STCrosses stCrosses) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCrosses stCrosses2 = (STCrosses)this.get_store().find_attribute_user(CTCrossesImpl.VAL$0);
            if (stCrosses2 == null) {
                stCrosses2 = (STCrosses)this.get_store().add_attribute_user(CTCrossesImpl.VAL$0);
            }
            stCrosses2.set((XmlObject)stCrosses);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
