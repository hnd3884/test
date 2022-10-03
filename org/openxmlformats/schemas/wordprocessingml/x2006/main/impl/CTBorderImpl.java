package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPointMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEighthPointMeasure;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBorderImpl extends XmlComplexContentImpl implements CTBorder
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName COLOR$2;
    private static final QName THEMECOLOR$4;
    private static final QName THEMETINT$6;
    private static final QName THEMESHADE$8;
    private static final QName SZ$10;
    private static final QName SPACE$12;
    private static final QName SHADOW$14;
    private static final QName FRAME$16;
    
    public CTBorderImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STBorder.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STBorder.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBorder xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBorder)this.get_store().find_attribute_user(CTBorderImpl.VAL$0);
        }
    }
    
    public void setVal(final STBorder.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STBorder stBorder) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBorder stBorder2 = (STBorder)this.get_store().find_attribute_user(CTBorderImpl.VAL$0);
            if (stBorder2 == null) {
                stBorder2 = (STBorder)this.get_store().add_attribute_user(CTBorderImpl.VAL$0);
            }
            stBorder2.set((XmlObject)stBorder);
        }
    }
    
    public Object getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.COLOR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STHexColor xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTBorderImpl.COLOR$2);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.COLOR$2) != null;
        }
    }
    
    public void setColor(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.COLOR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.COLOR$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetColor(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTBorderImpl.COLOR$2);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTBorderImpl.COLOR$2);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.COLOR$2);
        }
    }
    
    public STThemeColor.Enum getThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STThemeColor xgetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTBorderImpl.THEMECOLOR$4);
        }
    }
    
    public boolean isSetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.THEMECOLOR$4) != null;
        }
    }
    
    public void setThemeColor(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.THEMECOLOR$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetThemeColor(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTBorderImpl.THEMECOLOR$4);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTBorderImpl.THEMECOLOR$4);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    public void unsetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.THEMECOLOR$4);
        }
    }
    
    public byte[] getThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMETINT$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTBorderImpl.THEMETINT$6);
        }
    }
    
    public boolean isSetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.THEMETINT$6) != null;
        }
    }
    
    public void setThemeTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMETINT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.THEMETINT$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTBorderImpl.THEMETINT$6);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTBorderImpl.THEMETINT$6);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.THEMETINT$6);
        }
    }
    
    public byte[] getThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMESHADE$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTBorderImpl.THEMESHADE$8);
        }
    }
    
    public boolean isSetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.THEMESHADE$8) != null;
        }
    }
    
    public void setThemeShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.THEMESHADE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.THEMESHADE$8);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTBorderImpl.THEMESHADE$8);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTBorderImpl.THEMESHADE$8);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.THEMESHADE$8);
        }
    }
    
    public BigInteger getSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SZ$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STEighthPointMeasure xgetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STEighthPointMeasure)this.get_store().find_attribute_user(CTBorderImpl.SZ$10);
        }
    }
    
    public boolean isSetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.SZ$10) != null;
        }
    }
    
    public void setSz(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SZ$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.SZ$10);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetSz(final STEighthPointMeasure stEighthPointMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEighthPointMeasure stEighthPointMeasure2 = (STEighthPointMeasure)this.get_store().find_attribute_user(CTBorderImpl.SZ$10);
            if (stEighthPointMeasure2 == null) {
                stEighthPointMeasure2 = (STEighthPointMeasure)this.get_store().add_attribute_user(CTBorderImpl.SZ$10);
            }
            stEighthPointMeasure2.set((XmlObject)stEighthPointMeasure);
        }
    }
    
    public void unsetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.SZ$10);
        }
    }
    
    public BigInteger getSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SPACE$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STPointMeasure xgetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPointMeasure)this.get_store().find_attribute_user(CTBorderImpl.SPACE$12);
        }
    }
    
    public boolean isSetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.SPACE$12) != null;
        }
    }
    
    public void setSpace(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SPACE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.SPACE$12);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetSpace(final STPointMeasure stPointMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPointMeasure stPointMeasure2 = (STPointMeasure)this.get_store().find_attribute_user(CTBorderImpl.SPACE$12);
            if (stPointMeasure2 == null) {
                stPointMeasure2 = (STPointMeasure)this.get_store().add_attribute_user(CTBorderImpl.SPACE$12);
            }
            stPointMeasure2.set((XmlObject)stPointMeasure);
        }
    }
    
    public void unsetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.SPACE$12);
        }
    }
    
    public STOnOff.Enum getShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SHADOW$14);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTBorderImpl.SHADOW$14);
        }
    }
    
    public boolean isSetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.SHADOW$14) != null;
        }
    }
    
    public void setShadow(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.SHADOW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.SHADOW$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetShadow(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTBorderImpl.SHADOW$14);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTBorderImpl.SHADOW$14);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.SHADOW$14);
        }
    }
    
    public STOnOff.Enum getFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.FRAME$16);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTBorderImpl.FRAME$16);
        }
    }
    
    public boolean isSetFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.FRAME$16) != null;
        }
    }
    
    public void setFrame(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.FRAME$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.FRAME$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFrame(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTBorderImpl.FRAME$16);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTBorderImpl.FRAME$16);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.FRAME$16);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        COLOR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        THEMECOLOR$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeColor");
        THEMETINT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeTint");
        THEMESHADE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeShade");
        SZ$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sz");
        SPACE$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "space");
        SHADOW$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shadow");
        FRAME$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "frame");
    }
}
