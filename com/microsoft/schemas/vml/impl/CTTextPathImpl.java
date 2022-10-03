package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTTextPath;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextPathImpl extends XmlComplexContentImpl implements CTTextPath
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName STYLE$2;
    private static final QName ON$4;
    private static final QName FITSHAPE$6;
    private static final QName FITPATH$8;
    private static final QName TRIM$10;
    private static final QName XSCALE$12;
    private static final QName STRING$14;
    
    public CTTextPathImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.ID$0);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.ID$0) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.ID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextPathImpl.ID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.ID$0);
        }
    }
    
    public String getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.STYLE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.STYLE$2);
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.STYLE$2) != null;
        }
    }
    
    public void setStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.STYLE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.STYLE$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.STYLE$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextPathImpl.STYLE$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.STYLE$2);
        }
    }
    
    public STTrueFalse.Enum getOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.ON$4);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.ON$4);
        }
    }
    
    public boolean isSetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.ON$4) != null;
        }
    }
    
    public void setOn(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.ON$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.ON$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOn(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.ON$4);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextPathImpl.ON$4);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.ON$4);
        }
    }
    
    public STTrueFalse.Enum getFitshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.FITSHAPE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFitshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.FITSHAPE$6);
        }
    }
    
    public boolean isSetFitshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.FITSHAPE$6) != null;
        }
    }
    
    public void setFitshape(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.FITSHAPE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.FITSHAPE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFitshape(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.FITSHAPE$6);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextPathImpl.FITSHAPE$6);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFitshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.FITSHAPE$6);
        }
    }
    
    public STTrueFalse.Enum getFitpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.FITPATH$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFitpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.FITPATH$8);
        }
    }
    
    public boolean isSetFitpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.FITPATH$8) != null;
        }
    }
    
    public void setFitpath(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.FITPATH$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.FITPATH$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFitpath(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.FITPATH$8);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextPathImpl.FITPATH$8);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFitpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.FITPATH$8);
        }
    }
    
    public STTrueFalse.Enum getTrim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.TRIM$10);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetTrim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.TRIM$10);
        }
    }
    
    public boolean isSetTrim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.TRIM$10) != null;
        }
    }
    
    public void setTrim(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.TRIM$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.TRIM$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTrim(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.TRIM$10);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextPathImpl.TRIM$10);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetTrim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.TRIM$10);
        }
    }
    
    public STTrueFalse.Enum getXscale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.XSCALE$12);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetXscale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.XSCALE$12);
        }
    }
    
    public boolean isSetXscale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.XSCALE$12) != null;
        }
    }
    
    public void setXscale(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.XSCALE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.XSCALE$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetXscale(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextPathImpl.XSCALE$12);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextPathImpl.XSCALE$12);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetXscale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.XSCALE$12);
        }
    }
    
    public String getString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.STRING$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.STRING$14);
        }
    }
    
    public boolean isSetString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextPathImpl.STRING$14) != null;
        }
    }
    
    public void setString(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextPathImpl.STRING$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextPathImpl.STRING$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetString(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextPathImpl.STRING$14);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextPathImpl.STRING$14);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextPathImpl.STRING$14);
        }
    }
    
    static {
        ID$0 = new QName("", "id");
        STYLE$2 = new QName("", "style");
        ON$4 = new QName("", "on");
        FITSHAPE$6 = new QName("", "fitshape");
        FITPATH$8 = new QName("", "fitpath");
        TRIM$10 = new QName("", "trim");
        XSCALE$12 = new QName("", "xscale");
        STRING$14 = new QName("", "string");
    }
}
