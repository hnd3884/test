package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTXYAdjustHandleImpl extends XmlComplexContentImpl implements CTXYAdjustHandle
{
    private static final long serialVersionUID = 1L;
    private static final QName POS$0;
    private static final QName GDREFX$2;
    private static final QName MINX$4;
    private static final QName MAXX$6;
    private static final QName GDREFY$8;
    private static final QName MINY$10;
    private static final QName MAXY$12;
    
    public CTXYAdjustHandleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAdjPoint2D getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjPoint2D ctAdjPoint2D = (CTAdjPoint2D)this.get_store().find_element_user(CTXYAdjustHandleImpl.POS$0, 0);
            if (ctAdjPoint2D == null) {
                return null;
            }
            return ctAdjPoint2D;
        }
    }
    
    public void setPos(final CTAdjPoint2D ctAdjPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctAdjPoint2D, CTXYAdjustHandleImpl.POS$0, 0, (short)1);
    }
    
    public CTAdjPoint2D addNewPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().add_element_user(CTXYAdjustHandleImpl.POS$0);
        }
    }
    
    public String getGdRefX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideName xgetGdRefX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideName)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
        }
    }
    
    public boolean isSetGdRefX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFX$2) != null;
        }
    }
    
    public void setGdRefX(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGdRefX(final STGeomGuideName stGeomGuideName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideName stGeomGuideName2 = (STGeomGuideName)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
            if (stGeomGuideName2 == null) {
                stGeomGuideName2 = (STGeomGuideName)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.GDREFX$2);
            }
            stGeomGuideName2.set((XmlObject)stGeomGuideName);
        }
    }
    
    public void unsetGdRefX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.GDREFX$2);
        }
    }
    
    public Object getMinX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINX$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMinX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINX$4);
        }
    }
    
    public boolean isSetMinX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINX$4) != null;
        }
    }
    
    public void setMinX(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MINX$4);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMinX(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINX$4);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MINX$4);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMinX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.MINX$4);
        }
    }
    
    public Object getMaxX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMaxX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
        }
    }
    
    public boolean isSetMaxX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXX$6) != null;
        }
    }
    
    public void setMaxX(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMaxX(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MAXX$6);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMaxX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.MAXX$6);
        }
    }
    
    public String getGdRefY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideName xgetGdRefY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideName)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
        }
    }
    
    public boolean isSetGdRefY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFY$8) != null;
        }
    }
    
    public void setGdRefY(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGdRefY(final STGeomGuideName stGeomGuideName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideName stGeomGuideName2 = (STGeomGuideName)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
            if (stGeomGuideName2 == null) {
                stGeomGuideName2 = (STGeomGuideName)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.GDREFY$8);
            }
            stGeomGuideName2.set((XmlObject)stGeomGuideName);
        }
    }
    
    public void unsetGdRefY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.GDREFY$8);
        }
    }
    
    public Object getMinY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINY$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMinY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINY$10);
        }
    }
    
    public boolean isSetMinY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINY$10) != null;
        }
    }
    
    public void setMinY(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINY$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MINY$10);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMinY(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MINY$10);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MINY$10);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMinY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.MINY$10);
        }
    }
    
    public Object getMaxY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMaxY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
        }
    }
    
    public boolean isSetMaxY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXY$12) != null;
        }
    }
    
    public void setMaxY(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMaxY(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTXYAdjustHandleImpl.MAXY$12);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMaxY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXYAdjustHandleImpl.MAXY$12);
        }
    }
    
    static {
        POS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pos");
        GDREFX$2 = new QName("", "gdRefX");
        MINX$4 = new QName("", "minX");
        MAXX$6 = new QName("", "maxX");
        GDREFY$8 = new QName("", "gdRefY");
        MINY$10 = new QName("", "minY");
        MAXY$12 = new QName("", "maxY");
    }
}
