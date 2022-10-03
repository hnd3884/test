package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorImpl extends XmlComplexContentImpl implements CTColor
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName THEMECOLOR$2;
    private static final QName THEMETINT$4;
    private static final QName THEMESHADE$6;
    
    public CTColorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Object getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STHexColor xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTColorImpl.VAL$0);
        }
    }
    
    public void setVal(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.VAL$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetVal(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTColorImpl.VAL$0);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTColorImpl.VAL$0);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    public STThemeColor.Enum getThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMECOLOR$2);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STThemeColor xgetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTColorImpl.THEMECOLOR$2);
        }
    }
    
    public boolean isSetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.THEMECOLOR$2) != null;
        }
    }
    
    public void setThemeColor(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMECOLOR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.THEMECOLOR$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetThemeColor(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTColorImpl.THEMECOLOR$2);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTColorImpl.THEMECOLOR$2);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    public void unsetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.THEMECOLOR$2);
        }
    }
    
    public byte[] getThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMETINT$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTColorImpl.THEMETINT$4);
        }
    }
    
    public boolean isSetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.THEMETINT$4) != null;
        }
    }
    
    public void setThemeTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMETINT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.THEMETINT$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTColorImpl.THEMETINT$4);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTColorImpl.THEMETINT$4);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.THEMETINT$4);
        }
    }
    
    public byte[] getThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMESHADE$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTColorImpl.THEMESHADE$6);
        }
    }
    
    public boolean isSetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.THEMESHADE$6) != null;
        }
    }
    
    public void setThemeShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEMESHADE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.THEMESHADE$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTColorImpl.THEMESHADE$6);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTColorImpl.THEMESHADE$6);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.THEMESHADE$6);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        THEMECOLOR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeColor");
        THEMETINT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeTint");
        THEMESHADE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeShade");
    }
}
