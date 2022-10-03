package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTheme;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHint;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontsImpl extends XmlComplexContentImpl implements CTFonts
{
    private static final long serialVersionUID = 1L;
    private static final QName HINT$0;
    private static final QName ASCII$2;
    private static final QName HANSI$4;
    private static final QName EASTASIA$6;
    private static final QName CS$8;
    private static final QName ASCIITHEME$10;
    private static final QName HANSITHEME$12;
    private static final QName EASTASIATHEME$14;
    private static final QName CSTHEME$16;
    
    public CTFontsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STHint.Enum getHint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HINT$0);
            if (simpleValue == null) {
                return null;
            }
            return (STHint.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHint xgetHint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHint)this.get_store().find_attribute_user(CTFontsImpl.HINT$0);
        }
    }
    
    public boolean isSetHint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.HINT$0) != null;
        }
    }
    
    public void setHint(final STHint.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HINT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.HINT$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHint(final STHint stHint) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHint stHint2 = (STHint)this.get_store().find_attribute_user(CTFontsImpl.HINT$0);
            if (stHint2 == null) {
                stHint2 = (STHint)this.get_store().add_attribute_user(CTFontsImpl.HINT$0);
            }
            stHint2.set((XmlObject)stHint);
        }
    }
    
    public void unsetHint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.HINT$0);
        }
    }
    
    public String getAscii() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.ASCII$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetAscii() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTFontsImpl.ASCII$2);
        }
    }
    
    public boolean isSetAscii() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.ASCII$2) != null;
        }
    }
    
    public void setAscii(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.ASCII$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.ASCII$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAscii(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTFontsImpl.ASCII$2);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTFontsImpl.ASCII$2);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetAscii() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.ASCII$2);
        }
    }
    
    public String getHAnsi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HANSI$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetHAnsi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTFontsImpl.HANSI$4);
        }
    }
    
    public boolean isSetHAnsi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.HANSI$4) != null;
        }
    }
    
    public void setHAnsi(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HANSI$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.HANSI$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHAnsi(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTFontsImpl.HANSI$4);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTFontsImpl.HANSI$4);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetHAnsi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.HANSI$4);
        }
    }
    
    public String getEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.EASTASIA$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTFontsImpl.EASTASIA$6);
        }
    }
    
    public boolean isSetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.EASTASIA$6) != null;
        }
    }
    
    public void setEastAsia(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.EASTASIA$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.EASTASIA$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEastAsia(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTFontsImpl.EASTASIA$6);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTFontsImpl.EASTASIA$6);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.EASTASIA$6);
        }
    }
    
    public String getCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.CS$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTFontsImpl.CS$8);
        }
    }
    
    public boolean isSetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.CS$8) != null;
        }
    }
    
    public void setCs(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.CS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.CS$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCs(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTFontsImpl.CS$8);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTFontsImpl.CS$8);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.CS$8);
        }
    }
    
    public STTheme.Enum getAsciiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.ASCIITHEME$10);
            if (simpleValue == null) {
                return null;
            }
            return (STTheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTheme xgetAsciiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTheme)this.get_store().find_attribute_user(CTFontsImpl.ASCIITHEME$10);
        }
    }
    
    public boolean isSetAsciiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.ASCIITHEME$10) != null;
        }
    }
    
    public void setAsciiTheme(final STTheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.ASCIITHEME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.ASCIITHEME$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAsciiTheme(final STTheme stTheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTheme stTheme2 = (STTheme)this.get_store().find_attribute_user(CTFontsImpl.ASCIITHEME$10);
            if (stTheme2 == null) {
                stTheme2 = (STTheme)this.get_store().add_attribute_user(CTFontsImpl.ASCIITHEME$10);
            }
            stTheme2.set((XmlObject)stTheme);
        }
    }
    
    public void unsetAsciiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.ASCIITHEME$10);
        }
    }
    
    public STTheme.Enum getHAnsiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HANSITHEME$12);
            if (simpleValue == null) {
                return null;
            }
            return (STTheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTheme xgetHAnsiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTheme)this.get_store().find_attribute_user(CTFontsImpl.HANSITHEME$12);
        }
    }
    
    public boolean isSetHAnsiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.HANSITHEME$12) != null;
        }
    }
    
    public void setHAnsiTheme(final STTheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.HANSITHEME$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.HANSITHEME$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHAnsiTheme(final STTheme stTheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTheme stTheme2 = (STTheme)this.get_store().find_attribute_user(CTFontsImpl.HANSITHEME$12);
            if (stTheme2 == null) {
                stTheme2 = (STTheme)this.get_store().add_attribute_user(CTFontsImpl.HANSITHEME$12);
            }
            stTheme2.set((XmlObject)stTheme);
        }
    }
    
    public void unsetHAnsiTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.HANSITHEME$12);
        }
    }
    
    public STTheme.Enum getEastAsiaTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.EASTASIATHEME$14);
            if (simpleValue == null) {
                return null;
            }
            return (STTheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTheme xgetEastAsiaTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTheme)this.get_store().find_attribute_user(CTFontsImpl.EASTASIATHEME$14);
        }
    }
    
    public boolean isSetEastAsiaTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.EASTASIATHEME$14) != null;
        }
    }
    
    public void setEastAsiaTheme(final STTheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.EASTASIATHEME$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.EASTASIATHEME$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEastAsiaTheme(final STTheme stTheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTheme stTheme2 = (STTheme)this.get_store().find_attribute_user(CTFontsImpl.EASTASIATHEME$14);
            if (stTheme2 == null) {
                stTheme2 = (STTheme)this.get_store().add_attribute_user(CTFontsImpl.EASTASIATHEME$14);
            }
            stTheme2.set((XmlObject)stTheme);
        }
    }
    
    public void unsetEastAsiaTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.EASTASIATHEME$14);
        }
    }
    
    public STTheme.Enum getCstheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.CSTHEME$16);
            if (simpleValue == null) {
                return null;
            }
            return (STTheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTheme xgetCstheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTheme)this.get_store().find_attribute_user(CTFontsImpl.CSTHEME$16);
        }
    }
    
    public boolean isSetCstheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.CSTHEME$16) != null;
        }
    }
    
    public void setCstheme(final STTheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.CSTHEME$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.CSTHEME$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCstheme(final STTheme stTheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTheme stTheme2 = (STTheme)this.get_store().find_attribute_user(CTFontsImpl.CSTHEME$16);
            if (stTheme2 == null) {
                stTheme2 = (STTheme)this.get_store().add_attribute_user(CTFontsImpl.CSTHEME$16);
            }
            stTheme2.set((XmlObject)stTheme);
        }
    }
    
    public void unsetCstheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.CSTHEME$16);
        }
    }
    
    static {
        HINT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hint");
        ASCII$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ascii");
        HANSI$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hAnsi");
        EASTASIA$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsia");
        CS$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cs");
        ASCIITHEME$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "asciiTheme");
        HANSITHEME$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hAnsiTheme");
        EASTASIATHEME$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsiaTheme");
        CSTHEME$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cstheme");
    }
}
