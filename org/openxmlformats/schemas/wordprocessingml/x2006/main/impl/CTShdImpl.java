package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShdImpl extends XmlComplexContentImpl implements CTShd
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName COLOR$2;
    private static final QName THEMECOLOR$4;
    private static final QName THEMETINT$6;
    private static final QName THEMESHADE$8;
    private static final QName FILL$10;
    private static final QName THEMEFILL$12;
    private static final QName THEMEFILLTINT$14;
    private static final QName THEMEFILLSHADE$16;
    
    public CTShdImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STShd.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STShd.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STShd xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STShd)this.get_store().find_attribute_user(CTShdImpl.VAL$0);
        }
    }
    
    public void setVal(final STShd.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STShd stShd) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShd stShd2 = (STShd)this.get_store().find_attribute_user(CTShdImpl.VAL$0);
            if (stShd2 == null) {
                stShd2 = (STShd)this.get_store().add_attribute_user(CTShdImpl.VAL$0);
            }
            stShd2.set((XmlObject)stShd);
        }
    }
    
    public Object getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.COLOR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STHexColor xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTShdImpl.COLOR$2);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.COLOR$2) != null;
        }
    }
    
    public void setColor(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.COLOR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.COLOR$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetColor(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTShdImpl.COLOR$2);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTShdImpl.COLOR$2);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.COLOR$2);
        }
    }
    
    public STThemeColor.Enum getThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STThemeColor xgetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTShdImpl.THEMECOLOR$4);
        }
    }
    
    public boolean isSetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMECOLOR$4) != null;
        }
    }
    
    public void setThemeColor(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMECOLOR$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetThemeColor(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTShdImpl.THEMECOLOR$4);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTShdImpl.THEMECOLOR$4);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    public void unsetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMECOLOR$4);
        }
    }
    
    public byte[] getThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMETINT$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMETINT$6);
        }
    }
    
    public boolean isSetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMETINT$6) != null;
        }
    }
    
    public void setThemeTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMETINT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMETINT$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMETINT$6);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTShdImpl.THEMETINT$6);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMETINT$6);
        }
    }
    
    public byte[] getThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMESHADE$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMESHADE$8);
        }
    }
    
    public boolean isSetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMESHADE$8) != null;
        }
    }
    
    public void setThemeShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMESHADE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMESHADE$8);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMESHADE$8);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTShdImpl.THEMESHADE$8);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMESHADE$8);
        }
    }
    
    public Object getFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.FILL$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STHexColor xgetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTShdImpl.FILL$10);
        }
    }
    
    public boolean isSetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.FILL$10) != null;
        }
    }
    
    public void setFill(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.FILL$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.FILL$10);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetFill(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTShdImpl.FILL$10);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTShdImpl.FILL$10);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    public void unsetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.FILL$10);
        }
    }
    
    public STThemeColor.Enum getThemeFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILL$12);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STThemeColor xgetThemeFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTShdImpl.THEMEFILL$12);
        }
    }
    
    public boolean isSetThemeFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMEFILL$12) != null;
        }
    }
    
    public void setThemeFill(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILL$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMEFILL$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetThemeFill(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTShdImpl.THEMEFILL$12);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTShdImpl.THEMEFILL$12);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    public void unsetThemeFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMEFILL$12);
        }
    }
    
    public byte[] getThemeFillTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLTINT$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeFillTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLTINT$14);
        }
    }
    
    public boolean isSetThemeFillTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMEFILLTINT$14) != null;
        }
    }
    
    public void setThemeFillTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLTINT$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMEFILLTINT$14);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeFillTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLTINT$14);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTShdImpl.THEMEFILLTINT$14);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeFillTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMEFILLTINT$14);
        }
    }
    
    public byte[] getThemeFillShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeFillShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
        }
    }
    
    public boolean isSetThemeFillShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShdImpl.THEMEFILLSHADE$16) != null;
        }
    }
    
    public void setThemeFillShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeFillShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTShdImpl.THEMEFILLSHADE$16);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeFillShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShdImpl.THEMEFILLSHADE$16);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        COLOR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        THEMECOLOR$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeColor");
        THEMETINT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeTint");
        THEMESHADE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeShade");
        FILL$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fill");
        THEMEFILL$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeFill");
        THEMEFILLTINT$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeFillTint");
        THEMEFILLSHADE$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeFillShade");
    }
}
