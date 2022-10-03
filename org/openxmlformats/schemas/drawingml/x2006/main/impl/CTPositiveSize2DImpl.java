package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPositiveSize2DImpl extends XmlComplexContentImpl implements CTPositiveSize2D
{
    private static final long serialVersionUID = 1L;
    private static final QName CX$0;
    private static final QName CY$2;
    
    public CTPositiveSize2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getCx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CX$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetCx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveCoordinate)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CX$0);
        }
    }
    
    public void setCx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CX$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPositiveSize2DImpl.CX$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCx(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CX$0);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTPositiveSize2DImpl.CX$0);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public long getCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CY$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveCoordinate)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CY$2);
        }
    }
    
    public void setCy(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CY$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPositiveSize2DImpl.CY$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCy(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPositiveSize2DImpl.CY$2);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTPositiveSize2DImpl.CY$2);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    static {
        CX$0 = new QName("", "cx");
        CY$2 = new QName("", "cy");
    }
}
