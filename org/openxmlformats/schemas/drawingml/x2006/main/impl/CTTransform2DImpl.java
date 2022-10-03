package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTransform2DImpl extends XmlComplexContentImpl implements CTTransform2D
{
    private static final long serialVersionUID = 1L;
    private static final QName OFF$0;
    private static final QName EXT$2;
    private static final QName ROT$4;
    private static final QName FLIPH$6;
    private static final QName FLIPV$8;
    
    public CTTransform2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPoint2D getOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPoint2D ctPoint2D = (CTPoint2D)this.get_store().find_element_user(CTTransform2DImpl.OFF$0, 0);
            if (ctPoint2D == null) {
                return null;
            }
            return ctPoint2D;
        }
    }
    
    public boolean isSetOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTransform2DImpl.OFF$0) != 0;
        }
    }
    
    public void setOff(final CTPoint2D ctPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPoint2D, CTTransform2DImpl.OFF$0, 0, (short)1);
    }
    
    public CTPoint2D addNewOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPoint2D)this.get_store().add_element_user(CTTransform2DImpl.OFF$0);
        }
    }
    
    public void unsetOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTransform2DImpl.OFF$0, 0);
        }
    }
    
    public CTPositiveSize2D getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTTransform2DImpl.EXT$2, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public boolean isSetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTransform2DImpl.EXT$2) != 0;
        }
    }
    
    public void setExt(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTTransform2DImpl.EXT$2, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTTransform2DImpl.EXT$2);
        }
    }
    
    public void unsetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTransform2DImpl.EXT$2, 0);
        }
    }
    
    public int getRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.ROT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTransform2DImpl.ROT$4);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STAngle xgetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAngle stAngle = (STAngle)this.get_store().find_attribute_user(CTTransform2DImpl.ROT$4);
            if (stAngle == null) {
                stAngle = (STAngle)this.get_default_attribute_value(CTTransform2DImpl.ROT$4);
            }
            return stAngle;
        }
    }
    
    public boolean isSetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTransform2DImpl.ROT$4) != null;
        }
    }
    
    public void setRot(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.ROT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTransform2DImpl.ROT$4);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRot(final STAngle stAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAngle stAngle2 = (STAngle)this.get_store().find_attribute_user(CTTransform2DImpl.ROT$4);
            if (stAngle2 == null) {
                stAngle2 = (STAngle)this.get_store().add_attribute_user(CTTransform2DImpl.ROT$4);
            }
            stAngle2.set((XmlObject)stAngle);
        }
    }
    
    public void unsetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTransform2DImpl.ROT$4);
        }
    }
    
    public boolean getFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTransform2DImpl.FLIPH$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPH$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTransform2DImpl.FLIPH$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTransform2DImpl.FLIPH$6) != null;
        }
    }
    
    public void setFlipH(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTransform2DImpl.FLIPH$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFlipH(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPH$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTransform2DImpl.FLIPH$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTransform2DImpl.FLIPH$6);
        }
    }
    
    public boolean getFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPV$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTransform2DImpl.FLIPV$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPV$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTransform2DImpl.FLIPV$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTransform2DImpl.FLIPV$8) != null;
        }
    }
    
    public void setFlipV(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPV$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTransform2DImpl.FLIPV$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFlipV(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTransform2DImpl.FLIPV$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTransform2DImpl.FLIPV$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTransform2DImpl.FLIPV$8);
        }
    }
    
    static {
        OFF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "off");
        EXT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ext");
        ROT$4 = new QName("", "rot");
        FLIPH$6 = new QName("", "flipH");
        FLIPV$8 = new QName("", "flipV");
    }
}
