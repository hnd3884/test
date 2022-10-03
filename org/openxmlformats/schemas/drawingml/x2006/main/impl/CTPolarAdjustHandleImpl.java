package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPolarAdjustHandleImpl extends XmlComplexContentImpl implements CTPolarAdjustHandle
{
    private static final long serialVersionUID = 1L;
    private static final QName POS$0;
    private static final QName GDREFR$2;
    private static final QName MINR$4;
    private static final QName MAXR$6;
    private static final QName GDREFANG$8;
    private static final QName MINANG$10;
    private static final QName MAXANG$12;
    
    public CTPolarAdjustHandleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAdjPoint2D getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjPoint2D ctAdjPoint2D = (CTAdjPoint2D)this.get_store().find_element_user(CTPolarAdjustHandleImpl.POS$0, 0);
            if (ctAdjPoint2D == null) {
                return null;
            }
            return ctAdjPoint2D;
        }
    }
    
    public void setPos(final CTAdjPoint2D ctAdjPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctAdjPoint2D, CTPolarAdjustHandleImpl.POS$0, 0, (short)1);
    }
    
    public CTAdjPoint2D addNewPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().add_element_user(CTPolarAdjustHandleImpl.POS$0);
        }
    }
    
    public String getGdRefR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideName xgetGdRefR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideName)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
        }
    }
    
    public boolean isSetGdRefR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2) != null;
        }
    }
    
    public void setGdRefR(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGdRefR(final STGeomGuideName stGeomGuideName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideName stGeomGuideName2 = (STGeomGuideName)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
            if (stGeomGuideName2 == null) {
                stGeomGuideName2 = (STGeomGuideName)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.GDREFR$2);
            }
            stGeomGuideName2.set((XmlObject)stGeomGuideName);
        }
    }
    
    public void unsetGdRefR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.GDREFR$2);
        }
    }
    
    public Object getMinR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMinR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
        }
    }
    
    public boolean isSetMinR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINR$4) != null;
        }
    }
    
    public void setMinR(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMinR(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MINR$4);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMinR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.MINR$4);
        }
    }
    
    public Object getMaxR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjCoordinate xgetMaxR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjCoordinate)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
        }
    }
    
    public boolean isSetMaxR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXR$6) != null;
        }
    }
    
    public void setMaxR(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMaxR(final STAdjCoordinate stAdjCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjCoordinate stAdjCoordinate2 = (STAdjCoordinate)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
            if (stAdjCoordinate2 == null) {
                stAdjCoordinate2 = (STAdjCoordinate)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MAXR$6);
            }
            stAdjCoordinate2.set((XmlObject)stAdjCoordinate);
        }
    }
    
    public void unsetMaxR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.MAXR$6);
        }
    }
    
    public String getGdRefAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideName xgetGdRefAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideName)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
        }
    }
    
    public boolean isSetGdRefAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8) != null;
        }
    }
    
    public void setGdRefAng(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGdRefAng(final STGeomGuideName stGeomGuideName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideName stGeomGuideName2 = (STGeomGuideName)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
            if (stGeomGuideName2 == null) {
                stGeomGuideName2 = (STGeomGuideName)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.GDREFANG$8);
            }
            stGeomGuideName2.set((XmlObject)stGeomGuideName);
        }
    }
    
    public void unsetGdRefAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.GDREFANG$8);
        }
    }
    
    public Object getMinAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjAngle xgetMinAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjAngle)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
        }
    }
    
    public boolean isSetMinAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINANG$10) != null;
        }
    }
    
    public void setMinAng(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMinAng(final STAdjAngle stAdjAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjAngle stAdjAngle2 = (STAdjAngle)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
            if (stAdjAngle2 == null) {
                stAdjAngle2 = (STAdjAngle)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MINANG$10);
            }
            stAdjAngle2.set((XmlObject)stAdjAngle);
        }
    }
    
    public void unsetMinAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.MINANG$10);
        }
    }
    
    public Object getMaxAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjAngle xgetMaxAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjAngle)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
        }
    }
    
    public boolean isSetMaxAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12) != null;
        }
    }
    
    public void setMaxAng(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetMaxAng(final STAdjAngle stAdjAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjAngle stAdjAngle2 = (STAdjAngle)this.get_store().find_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
            if (stAdjAngle2 == null) {
                stAdjAngle2 = (STAdjAngle)this.get_store().add_attribute_user(CTPolarAdjustHandleImpl.MAXANG$12);
            }
            stAdjAngle2.set((XmlObject)stAdjAngle);
        }
    }
    
    public void unsetMaxAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPolarAdjustHandleImpl.MAXANG$12);
        }
    }
    
    static {
        POS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pos");
        GDREFR$2 = new QName("", "gdRefR");
        MINR$4 = new QName("", "minR");
        MAXR$6 = new QName("", "maxR");
        GDREFANG$8 = new QName("", "gdRefAng");
        MINANG$10 = new QName("", "minAng");
        MAXANG$12 = new QName("", "maxAng");
    }
}
