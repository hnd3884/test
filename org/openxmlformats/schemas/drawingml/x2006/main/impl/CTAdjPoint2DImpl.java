package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAdjPoint2DImpl extends XmlComplexContentImpl implements CTAdjPoint2D
{
    private static final long serialVersionUID = 1L;
    private static final QName X$0;
    private static final QName Y$2;
    
    public CTAdjPoint2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Object getX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAdjPoint2DImpl.X$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTAdjPoint2DImpl.X$0);
        }
    }
    
    public void setX(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAdjPoint2DImpl.X$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAdjPoint2DImpl.X$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetX(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTAdjPoint2DImpl.X$0);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTAdjPoint2DImpl.X$0);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public Object getY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAdjPoint2DImpl.Y$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTAdjPoint2DImpl.Y$2);
        }
    }
    
    public void setY(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAdjPoint2DImpl.Y$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAdjPoint2DImpl.Y$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetY(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTAdjPoint2DImpl.Y$2);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTAdjPoint2DImpl.Y$2);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    static {
        X$0 = new QName("", "x");
        Y$2 = new QName("", "y");
    }
}
