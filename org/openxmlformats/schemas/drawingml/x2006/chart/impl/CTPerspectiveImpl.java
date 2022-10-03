package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STPerspective;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPerspective;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPerspectiveImpl extends XmlComplexContentImpl implements CTPerspective
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTPerspectiveImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public short getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPerspectiveImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPerspectiveImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public STPerspective xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPerspective stPerspective = (STPerspective)this.get_store().find_attribute_user(CTPerspectiveImpl.VAL$0);
            if (stPerspective == null) {
                stPerspective = (STPerspective)this.get_default_attribute_value(CTPerspectiveImpl.VAL$0);
            }
            return stPerspective;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPerspectiveImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPerspectiveImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPerspectiveImpl.VAL$0);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetVal(final STPerspective stPerspective) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPerspective stPerspective2 = (STPerspective)this.get_store().find_attribute_user(CTPerspectiveImpl.VAL$0);
            if (stPerspective2 == null) {
                stPerspective2 = (STPerspective)this.get_store().add_attribute_user(CTPerspectiveImpl.VAL$0);
            }
            stPerspective2.set((XmlObject)stPerspective);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPerspectiveImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
