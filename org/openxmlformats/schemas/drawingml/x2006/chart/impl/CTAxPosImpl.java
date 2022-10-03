package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAxPosImpl extends XmlComplexContentImpl implements CTAxPos
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTAxPosImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STAxPos.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAxPosImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STAxPos.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STAxPos xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAxPos)this.get_store().find_attribute_user(CTAxPosImpl.VAL$0);
        }
    }
    
    public void setVal(final STAxPos.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAxPosImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAxPosImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STAxPos stAxPos) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAxPos stAxPos2 = (STAxPos)this.get_store().find_attribute_user(CTAxPosImpl.VAL$0);
            if (stAxPos2 == null) {
                stAxPos2 = (STAxPos)this.get_store().add_attribute_user(CTAxPosImpl.VAL$0);
            }
            stAxPos2.set((XmlObject)stAxPos);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
