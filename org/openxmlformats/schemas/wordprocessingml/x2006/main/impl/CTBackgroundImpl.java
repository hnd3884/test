package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBackground;

public class CTBackgroundImpl extends CTPictureBaseImpl implements CTBackground
{
    private static final long serialVersionUID = 1L;
    private static final QName COLOR$0;
    private static final QName THEMECOLOR$2;
    private static final QName THEMETINT$4;
    private static final QName THEMESHADE$6;
    
    public CTBackgroundImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public Object getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.COLOR$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    @Override
    public STHexColor xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTBackgroundImpl.COLOR$0);
        }
    }
    
    @Override
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundImpl.COLOR$0) != null;
        }
    }
    
    @Override
    public void setColor(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.COLOR$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundImpl.COLOR$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    @Override
    public void xsetColor(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTBackgroundImpl.COLOR$0);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTBackgroundImpl.COLOR$0);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    @Override
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundImpl.COLOR$0);
        }
    }
    
    @Override
    public STThemeColor.Enum getThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    @Override
    public STThemeColor xgetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
        }
    }
    
    @Override
    public boolean isSetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundImpl.THEMECOLOR$2) != null;
        }
    }
    
    @Override
    public void setThemeColor(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    @Override
    public void xsetThemeColor(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTBackgroundImpl.THEMECOLOR$2);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    @Override
    public void unsetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundImpl.THEMECOLOR$2);
        }
    }
    
    @Override
    public byte[] getThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMETINT$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    @Override
    public STUcharHexNumber xgetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTBackgroundImpl.THEMETINT$4);
        }
    }
    
    @Override
    public boolean isSetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundImpl.THEMETINT$4) != null;
        }
    }
    
    @Override
    public void setThemeTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMETINT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundImpl.THEMETINT$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    @Override
    public void xsetThemeTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTBackgroundImpl.THEMETINT$4);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTBackgroundImpl.THEMETINT$4);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    @Override
    public void unsetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundImpl.THEMETINT$4);
        }
    }
    
    @Override
    public byte[] getThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMESHADE$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    @Override
    public STUcharHexNumber xgetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTBackgroundImpl.THEMESHADE$6);
        }
    }
    
    @Override
    public boolean isSetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundImpl.THEMESHADE$6) != null;
        }
    }
    
    @Override
    public void setThemeShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.THEMESHADE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundImpl.THEMESHADE$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    @Override
    public void xsetThemeShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTBackgroundImpl.THEMESHADE$6);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTBackgroundImpl.THEMESHADE$6);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    @Override
    public void unsetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundImpl.THEMESHADE$6);
        }
    }
    
    static {
        COLOR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        THEMECOLOR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeColor");
        THEMETINT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeTint");
        THEMESHADE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeShade");
    }
}
