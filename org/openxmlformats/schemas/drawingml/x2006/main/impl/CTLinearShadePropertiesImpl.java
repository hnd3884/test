package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedAngle;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLinearShadeProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLinearShadePropertiesImpl extends XmlComplexContentImpl implements CTLinearShadeProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName ANG$0;
    private static final QName SCALED$2;
    
    public CTLinearShadePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveFixedAngle xgetAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveFixedAngle)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
        }
    }
    
    public boolean isSetAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.ANG$0) != null;
        }
    }
    
    public void setAng(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetAng(final STPositiveFixedAngle stPositiveFixedAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveFixedAngle stPositiveFixedAngle2 = (STPositiveFixedAngle)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
            if (stPositiveFixedAngle2 == null) {
                stPositiveFixedAngle2 = (STPositiveFixedAngle)this.get_store().add_attribute_user(CTLinearShadePropertiesImpl.ANG$0);
            }
            stPositiveFixedAngle2.set((XmlObject)stPositiveFixedAngle);
        }
    }
    
    public void unsetAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinearShadePropertiesImpl.ANG$0);
        }
    }
    
    public boolean getScaled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetScaled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
        }
    }
    
    public boolean isSetScaled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.SCALED$2) != null;
        }
    }
    
    public void setScaled(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetScaled(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTLinearShadePropertiesImpl.SCALED$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetScaled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinearShadePropertiesImpl.SCALED$2);
        }
    }
    
    static {
        ANG$0 = new QName("", "ang");
        SCALED$2 = new QName("", "scaled");
    }
}
