package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTUnderlineImpl extends XmlComplexContentImpl implements CTUnderline
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName COLOR$2;
    private static final QName THEMECOLOR$4;
    private static final QName THEMETINT$6;
    private static final QName THEMESHADE$8;
    
    public CTUnderlineImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STUnderline.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STUnderline.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STUnderline xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnderline)this.get_store().find_attribute_user(CTUnderlineImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlineImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STUnderline.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlineImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STUnderline stUnderline) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnderline stUnderline2 = (STUnderline)this.get_store().find_attribute_user(CTUnderlineImpl.VAL$0);
            if (stUnderline2 == null) {
                stUnderline2 = (STUnderline)this.get_store().add_attribute_user(CTUnderlineImpl.VAL$0);
            }
            stUnderline2.set((XmlObject)stUnderline);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlineImpl.VAL$0);
        }
    }
    
    public Object getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.COLOR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STHexColor xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHexColor)this.get_store().find_attribute_user(CTUnderlineImpl.COLOR$2);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlineImpl.COLOR$2) != null;
        }
    }
    
    public void setColor(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.COLOR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlineImpl.COLOR$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetColor(final STHexColor stHexColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHexColor stHexColor2 = (STHexColor)this.get_store().find_attribute_user(CTUnderlineImpl.COLOR$2);
            if (stHexColor2 == null) {
                stHexColor2 = (STHexColor)this.get_store().add_attribute_user(CTUnderlineImpl.COLOR$2);
            }
            stHexColor2.set((XmlObject)stHexColor);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlineImpl.COLOR$2);
        }
    }
    
    public STThemeColor.Enum getThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                return null;
            }
            return (STThemeColor.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STThemeColor xgetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STThemeColor)this.get_store().find_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
        }
    }
    
    public boolean isSetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlineImpl.THEMECOLOR$4) != null;
        }
    }
    
    public void setThemeColor(final STThemeColor.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetThemeColor(final STThemeColor stThemeColor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STThemeColor stThemeColor2 = (STThemeColor)this.get_store().find_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
            if (stThemeColor2 == null) {
                stThemeColor2 = (STThemeColor)this.get_store().add_attribute_user(CTUnderlineImpl.THEMECOLOR$4);
            }
            stThemeColor2.set((XmlObject)stThemeColor);
        }
    }
    
    public void unsetThemeColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlineImpl.THEMECOLOR$4);
        }
    }
    
    public byte[] getThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMETINT$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTUnderlineImpl.THEMETINT$6);
        }
    }
    
    public boolean isSetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlineImpl.THEMETINT$6) != null;
        }
    }
    
    public void setThemeTint(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMETINT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlineImpl.THEMETINT$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeTint(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTUnderlineImpl.THEMETINT$6);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTUnderlineImpl.THEMETINT$6);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlineImpl.THEMETINT$6);
        }
    }
    
    public byte[] getThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMESHADE$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUcharHexNumber xgetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUcharHexNumber)this.get_store().find_attribute_user(CTUnderlineImpl.THEMESHADE$8);
        }
    }
    
    public boolean isSetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlineImpl.THEMESHADE$8) != null;
        }
    }
    
    public void setThemeShade(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlineImpl.THEMESHADE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlineImpl.THEMESHADE$8);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetThemeShade(final STUcharHexNumber stUcharHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUcharHexNumber stUcharHexNumber2 = (STUcharHexNumber)this.get_store().find_attribute_user(CTUnderlineImpl.THEMESHADE$8);
            if (stUcharHexNumber2 == null) {
                stUcharHexNumber2 = (STUcharHexNumber)this.get_store().add_attribute_user(CTUnderlineImpl.THEMESHADE$8);
            }
            stUcharHexNumber2.set((XmlObject)stUcharHexNumber);
        }
    }
    
    public void unsetThemeShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlineImpl.THEMESHADE$8);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        COLOR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        THEMECOLOR$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeColor");
        THEMETINT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeTint");
        THEMESHADE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeShade");
    }
}
