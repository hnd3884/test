package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrossBetween;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrossBetween;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCrossBetweenImpl extends XmlComplexContentImpl implements CTCrossBetween
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTCrossBetweenImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STCrossBetween.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCrossBetweenImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STCrossBetween.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCrossBetween xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCrossBetween)this.get_store().find_attribute_user(CTCrossBetweenImpl.VAL$0);
        }
    }
    
    public void setVal(final STCrossBetween.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCrossBetweenImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCrossBetweenImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STCrossBetween stCrossBetween) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCrossBetween stCrossBetween2 = (STCrossBetween)this.get_store().find_attribute_user(CTCrossBetweenImpl.VAL$0);
            if (stCrossBetween2 == null) {
                stCrossBetween2 = (STCrossBetween)this.get_store().add_attribute_user(CTCrossBetweenImpl.VAL$0);
            }
            stCrossBetween2.set((XmlObject)stCrossBetween);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
