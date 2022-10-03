package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGeomRectImpl extends XmlComplexContentImpl implements CTGeomRect
{
    private static final long serialVersionUID = 1L;
    private static final QName L$0;
    private static final QName T$2;
    private static final QName R$4;
    private static final QName B$6;
    
    public CTGeomRectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Object getL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.L$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.L$0);
        }
    }
    
    public void setL(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.L$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomRectImpl.L$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetL(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.L$0);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTGeomRectImpl.L$0);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public Object getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.T$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.T$2);
        }
    }
    
    public void setT(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.T$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomRectImpl.T$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetT(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.T$2);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTGeomRectImpl.T$2);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public Object getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.R$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.R$4);
        }
    }
    
    public void setR(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.R$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomRectImpl.R$4);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetR(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.R$4);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTGeomRectImpl.R$4);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public Object getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.B$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.B$6);
        }
    }
    
    public void setB(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomRectImpl.B$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomRectImpl.B$6);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetB(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTGeomRectImpl.B$6);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTGeomRectImpl.B$6);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    static {
        L$0 = new QName("", "l");
        T$2 = new QName("", "t");
        R$4 = new QName("", "r");
        B$6 = new QName("", "b");
    }
}
