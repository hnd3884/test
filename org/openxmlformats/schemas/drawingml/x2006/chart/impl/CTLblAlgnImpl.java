package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLblAlgn;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLblAlgn;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLblAlgnImpl extends XmlComplexContentImpl implements CTLblAlgn
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLblAlgnImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STLblAlgn.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLblAlgnImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STLblAlgn.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLblAlgn xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLblAlgn)this.get_store().find_attribute_user(CTLblAlgnImpl.VAL$0);
        }
    }
    
    public void setVal(final STLblAlgn.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLblAlgnImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLblAlgnImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STLblAlgn stLblAlgn) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLblAlgn stLblAlgn2 = (STLblAlgn)this.get_store().find_attribute_user(CTLblAlgnImpl.VAL$0);
            if (stLblAlgn2 == null) {
                stLblAlgn2 = (STLblAlgn)this.get_store().add_attribute_user(CTLblAlgnImpl.VAL$0);
            }
            stLblAlgn2.set((XmlObject)stLblAlgn);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
