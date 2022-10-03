package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupTransform2DImpl extends XmlComplexContentImpl implements CTGroupTransform2D
{
    private static final long serialVersionUID = 1L;
    private static final QName OFF$0;
    private static final QName EXT$2;
    private static final QName CHOFF$4;
    private static final QName CHEXT$6;
    private static final QName ROT$8;
    private static final QName FLIPH$10;
    private static final QName FLIPV$12;
    
    public CTGroupTransform2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPoint2D getOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPoint2D ctPoint2D = (CTPoint2D)this.get_store().find_element_user(CTGroupTransform2DImpl.OFF$0, 0);
            if (ctPoint2D == null) {
                return null;
            }
            return ctPoint2D;
        }
    }
    
    public boolean isSetOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupTransform2DImpl.OFF$0) != 0;
        }
    }
    
    public void setOff(final CTPoint2D ctPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPoint2D, CTGroupTransform2DImpl.OFF$0, 0, (short)1);
    }
    
    public CTPoint2D addNewOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPoint2D)this.get_store().add_element_user(CTGroupTransform2DImpl.OFF$0);
        }
    }
    
    public void unsetOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupTransform2DImpl.OFF$0, 0);
        }
    }
    
    public CTPositiveSize2D getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTGroupTransform2DImpl.EXT$2, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public boolean isSetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupTransform2DImpl.EXT$2) != 0;
        }
    }
    
    public void setExt(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTGroupTransform2DImpl.EXT$2, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTGroupTransform2DImpl.EXT$2);
        }
    }
    
    public void unsetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupTransform2DImpl.EXT$2, 0);
        }
    }
    
    public CTPoint2D getChOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPoint2D ctPoint2D = (CTPoint2D)this.get_store().find_element_user(CTGroupTransform2DImpl.CHOFF$4, 0);
            if (ctPoint2D == null) {
                return null;
            }
            return ctPoint2D;
        }
    }
    
    public boolean isSetChOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupTransform2DImpl.CHOFF$4) != 0;
        }
    }
    
    public void setChOff(final CTPoint2D ctPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPoint2D, CTGroupTransform2DImpl.CHOFF$4, 0, (short)1);
    }
    
    public CTPoint2D addNewChOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPoint2D)this.get_store().add_element_user(CTGroupTransform2DImpl.CHOFF$4);
        }
    }
    
    public void unsetChOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupTransform2DImpl.CHOFF$4, 0);
        }
    }
    
    public CTPositiveSize2D getChExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTGroupTransform2DImpl.CHEXT$6, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public boolean isSetChExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupTransform2DImpl.CHEXT$6) != 0;
        }
    }
    
    public void setChExt(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTGroupTransform2DImpl.CHEXT$6, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewChExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTGroupTransform2DImpl.CHEXT$6);
        }
    }
    
    public void unsetChExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupTransform2DImpl.CHEXT$6, 0);
        }
    }
    
    public int getRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.ROT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupTransform2DImpl.ROT$8);
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
            STAngle stAngle = (STAngle)this.get_store().find_attribute_user(CTGroupTransform2DImpl.ROT$8);
            if (stAngle == null) {
                stAngle = (STAngle)this.get_default_attribute_value(CTGroupTransform2DImpl.ROT$8);
            }
            return stAngle;
        }
    }
    
    public boolean isSetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupTransform2DImpl.ROT$8) != null;
        }
    }
    
    public void setRot(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.ROT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupTransform2DImpl.ROT$8);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRot(final STAngle stAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAngle stAngle2 = (STAngle)this.get_store().find_attribute_user(CTGroupTransform2DImpl.ROT$8);
            if (stAngle2 == null) {
                stAngle2 = (STAngle)this.get_store().add_attribute_user(CTGroupTransform2DImpl.ROT$8);
            }
            stAngle2.set((XmlObject)stAngle);
        }
    }
    
    public void unsetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupTransform2DImpl.ROT$8);
        }
    }
    
    public boolean getFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupTransform2DImpl.FLIPH$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGroupTransform2DImpl.FLIPH$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPH$10) != null;
        }
    }
    
    public void setFlipH(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFlipH(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGroupTransform2DImpl.FLIPH$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFlipH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupTransform2DImpl.FLIPH$10);
        }
    }
    
    public boolean getFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupTransform2DImpl.FLIPV$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGroupTransform2DImpl.FLIPV$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPV$12) != null;
        }
    }
    
    public void setFlipV(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFlipV(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGroupTransform2DImpl.FLIPV$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFlipV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupTransform2DImpl.FLIPV$12);
        }
    }
    
    static {
        OFF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "off");
        EXT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ext");
        CHOFF$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "chOff");
        CHEXT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "chExt");
        ROT$8 = new QName("", "rot");
        FLIPH$10 = new QName("", "flipH");
        FLIPV$12 = new QName("", "flipV");
    }
}
