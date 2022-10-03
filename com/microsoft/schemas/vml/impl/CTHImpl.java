package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.vml.STTrueFalseBlank;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTH;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHImpl extends XmlComplexContentImpl implements CTH
{
    private static final long serialVersionUID = 1L;
    private static final QName POSITION$0;
    private static final QName POLAR$2;
    private static final QName MAP$4;
    private static final QName INVX$6;
    private static final QName INVY$8;
    private static final QName SWITCH$10;
    private static final QName XRANGE$12;
    private static final QName YRANGE$14;
    private static final QName RADIUSRANGE$16;
    
    public CTHImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.POSITION$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.POSITION$0);
        }
    }
    
    public boolean isSetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.POSITION$0) != null;
        }
    }
    
    public void setPosition(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.POSITION$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.POSITION$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPosition(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.POSITION$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.POSITION$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.POSITION$0);
        }
    }
    
    public String getPolar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.POLAR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPolar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.POLAR$2);
        }
    }
    
    public boolean isSetPolar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.POLAR$2) != null;
        }
    }
    
    public void setPolar(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.POLAR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.POLAR$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPolar(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.POLAR$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.POLAR$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPolar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.POLAR$2);
        }
    }
    
    public String getMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.MAP$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.MAP$4);
        }
    }
    
    public boolean isSetMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.MAP$4) != null;
        }
    }
    
    public void setMap(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.MAP$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.MAP$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMap(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.MAP$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.MAP$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.MAP$4);
        }
    }
    
    public STTrueFalse.Enum getInvx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.INVX$6);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInvx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTHImpl.INVX$6);
        }
    }
    
    public boolean isSetInvx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.INVX$6) != null;
        }
    }
    
    public void setInvx(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.INVX$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.INVX$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInvx(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTHImpl.INVX$6);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTHImpl.INVX$6);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInvx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.INVX$6);
        }
    }
    
    public STTrueFalse.Enum getInvy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.INVY$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInvy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTHImpl.INVY$8);
        }
    }
    
    public boolean isSetInvy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.INVY$8) != null;
        }
    }
    
    public void setInvy(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.INVY$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.INVY$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInvy(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTHImpl.INVY$8);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTHImpl.INVY$8);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInvy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.INVY$8);
        }
    }
    
    public STTrueFalseBlank.Enum getSwitch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.SWITCH$10);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalseBlank xgetSwitch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().find_attribute_user(CTHImpl.SWITCH$10);
        }
    }
    
    public boolean isSetSwitch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.SWITCH$10) != null;
        }
    }
    
    public void setSwitch(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.SWITCH$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.SWITCH$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSwitch(final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_attribute_user(CTHImpl.SWITCH$10);
            if (stTrueFalseBlank2 == null) {
                stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().add_attribute_user(CTHImpl.SWITCH$10);
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void unsetSwitch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.SWITCH$10);
        }
    }
    
    public String getXrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.XRANGE$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetXrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.XRANGE$12);
        }
    }
    
    public boolean isSetXrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.XRANGE$12) != null;
        }
    }
    
    public void setXrange(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.XRANGE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.XRANGE$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetXrange(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.XRANGE$12);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.XRANGE$12);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetXrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.XRANGE$12);
        }
    }
    
    public String getYrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.YRANGE$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetYrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.YRANGE$14);
        }
    }
    
    public boolean isSetYrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.YRANGE$14) != null;
        }
    }
    
    public void setYrange(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.YRANGE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.YRANGE$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetYrange(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.YRANGE$14);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.YRANGE$14);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetYrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.YRANGE$14);
        }
    }
    
    public String getRadiusrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.RADIUSRANGE$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetRadiusrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTHImpl.RADIUSRANGE$16);
        }
    }
    
    public boolean isSetRadiusrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHImpl.RADIUSRANGE$16) != null;
        }
    }
    
    public void setRadiusrange(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHImpl.RADIUSRANGE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHImpl.RADIUSRANGE$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRadiusrange(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHImpl.RADIUSRANGE$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHImpl.RADIUSRANGE$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetRadiusrange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHImpl.RADIUSRANGE$16);
        }
    }
    
    static {
        POSITION$0 = new QName("", "position");
        POLAR$2 = new QName("", "polar");
        MAP$4 = new QName("", "map");
        INVX$6 = new QName("", "invx");
        INVY$8 = new QName("", "invy");
        SWITCH$10 = new QName("", "switch");
        XRANGE$12 = new QName("", "xrange");
        YRANGE$14 = new QName("", "yrange");
        RADIUSRANGE$16 = new QName("", "radiusrange");
    }
}
