package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPoint2DImpl extends XmlComplexContentImpl implements CTPoint2D
{
    private static final long serialVersionUID = 1L;
    private static final QName X$0;
    private static final QName Y$2;
    
    public CTPoint2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPoint2DImpl.X$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTPoint2DImpl.X$0);
        }
    }
    
    public void setX(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPoint2DImpl.X$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPoint2DImpl.X$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetX(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTPoint2DImpl.X$0);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTPoint2DImpl.X$0);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    public long getY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPoint2DImpl.Y$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTPoint2DImpl.Y$2);
        }
    }
    
    public void setY(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPoint2DImpl.Y$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPoint2DImpl.Y$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetY(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTPoint2DImpl.Y$2);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTPoint2DImpl.Y$2);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    static {
        X$0 = new QName("", "x");
        Y$2 = new QName("", "y");
    }
}
