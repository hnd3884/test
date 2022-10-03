package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STOnOffStyleType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleTextStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleTextStyleImpl extends XmlComplexContentImpl implements CTTableStyleTextStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName FONT$0;
    private static final QName FONTREF$2;
    private static final QName SCRGBCLR$4;
    private static final QName SRGBCLR$6;
    private static final QName HSLCLR$8;
    private static final QName SYSCLR$10;
    private static final QName SCHEMECLR$12;
    private static final QName PRSTCLR$14;
    private static final QName EXTLST$16;
    private static final QName B$18;
    private static final QName I$20;
    
    public CTTableStyleTextStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFontCollection getFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontCollection collection = (CTFontCollection)this.get_store().find_element_user(CTTableStyleTextStyleImpl.FONT$0, 0);
            if (collection == null) {
                return null;
            }
            return collection;
        }
    }
    
    public boolean isSetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.FONT$0) != 0;
        }
    }
    
    public void setFont(final CTFontCollection collection) {
        this.generatedSetterHelperImpl((XmlObject)collection, CTTableStyleTextStyleImpl.FONT$0, 0, (short)1);
    }
    
    public CTFontCollection addNewFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontCollection)this.get_store().add_element_user(CTTableStyleTextStyleImpl.FONT$0);
        }
    }
    
    public void unsetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.FONT$0, 0);
        }
    }
    
    public CTFontReference getFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontReference ctFontReference = (CTFontReference)this.get_store().find_element_user(CTTableStyleTextStyleImpl.FONTREF$2, 0);
            if (ctFontReference == null) {
                return null;
            }
            return ctFontReference;
        }
    }
    
    public boolean isSetFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.FONTREF$2) != 0;
        }
    }
    
    public void setFontRef(final CTFontReference ctFontReference) {
        this.generatedSetterHelperImpl((XmlObject)ctFontReference, CTTableStyleTextStyleImpl.FONTREF$2, 0, (short)1);
    }
    
    public CTFontReference addNewFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontReference)this.get_store().add_element_user(CTTableStyleTextStyleImpl.FONTREF$2);
        }
    }
    
    public void unsetFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.FONTREF$2, 0);
        }
    }
    
    public CTScRgbColor getScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScRgbColor ctScRgbColor = (CTScRgbColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.SCRGBCLR$4, 0);
            if (ctScRgbColor == null) {
                return null;
            }
            return ctScRgbColor;
        }
    }
    
    public boolean isSetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.SCRGBCLR$4) != 0;
        }
    }
    
    public void setScrgbClr(final CTScRgbColor ctScRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctScRgbColor, CTTableStyleTextStyleImpl.SCRGBCLR$4, 0, (short)1);
    }
    
    public CTScRgbColor addNewScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScRgbColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.SCRGBCLR$4);
        }
    }
    
    public void unsetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.SCRGBCLR$4, 0);
        }
    }
    
    public CTSRgbColor getSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSRgbColor ctsRgbColor = (CTSRgbColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.SRGBCLR$6, 0);
            if (ctsRgbColor == null) {
                return null;
            }
            return ctsRgbColor;
        }
    }
    
    public boolean isSetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.SRGBCLR$6) != 0;
        }
    }
    
    public void setSrgbClr(final CTSRgbColor ctsRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctsRgbColor, CTTableStyleTextStyleImpl.SRGBCLR$6, 0, (short)1);
    }
    
    public CTSRgbColor addNewSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSRgbColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.SRGBCLR$6);
        }
    }
    
    public void unsetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.SRGBCLR$6, 0);
        }
    }
    
    public CTHslColor getHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHslColor ctHslColor = (CTHslColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.HSLCLR$8, 0);
            if (ctHslColor == null) {
                return null;
            }
            return ctHslColor;
        }
    }
    
    public boolean isSetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.HSLCLR$8) != 0;
        }
    }
    
    public void setHslClr(final CTHslColor ctHslColor) {
        this.generatedSetterHelperImpl((XmlObject)ctHslColor, CTTableStyleTextStyleImpl.HSLCLR$8, 0, (short)1);
    }
    
    public CTHslColor addNewHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHslColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.HSLCLR$8);
        }
    }
    
    public void unsetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.HSLCLR$8, 0);
        }
    }
    
    public CTSystemColor getSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSystemColor ctSystemColor = (CTSystemColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.SYSCLR$10, 0);
            if (ctSystemColor == null) {
                return null;
            }
            return ctSystemColor;
        }
    }
    
    public boolean isSetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.SYSCLR$10) != 0;
        }
    }
    
    public void setSysClr(final CTSystemColor ctSystemColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSystemColor, CTTableStyleTextStyleImpl.SYSCLR$10, 0, (short)1);
    }
    
    public CTSystemColor addNewSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSystemColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.SYSCLR$10);
        }
    }
    
    public void unsetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.SYSCLR$10, 0);
        }
    }
    
    public CTSchemeColor getSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchemeColor ctSchemeColor = (CTSchemeColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.SCHEMECLR$12, 0);
            if (ctSchemeColor == null) {
                return null;
            }
            return ctSchemeColor;
        }
    }
    
    public boolean isSetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.SCHEMECLR$12) != 0;
        }
    }
    
    public void setSchemeClr(final CTSchemeColor ctSchemeColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSchemeColor, CTTableStyleTextStyleImpl.SCHEMECLR$12, 0, (short)1);
    }
    
    public CTSchemeColor addNewSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemeColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.SCHEMECLR$12);
        }
    }
    
    public void unsetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.SCHEMECLR$12, 0);
        }
    }
    
    public CTPresetColor getPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetColor ctPresetColor = (CTPresetColor)this.get_store().find_element_user(CTTableStyleTextStyleImpl.PRSTCLR$14, 0);
            if (ctPresetColor == null) {
                return null;
            }
            return ctPresetColor;
        }
    }
    
    public boolean isSetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.PRSTCLR$14) != 0;
        }
    }
    
    public void setPrstClr(final CTPresetColor ctPresetColor) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetColor, CTTableStyleTextStyleImpl.PRSTCLR$14, 0, (short)1);
    }
    
    public CTPresetColor addNewPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetColor)this.get_store().add_element_user(CTTableStyleTextStyleImpl.PRSTCLR$14);
        }
    }
    
    public void unsetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.PRSTCLR$14, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableStyleTextStyleImpl.EXTLST$16, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleTextStyleImpl.EXTLST$16) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableStyleTextStyleImpl.EXTLST$16, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableStyleTextStyleImpl.EXTLST$16);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleTextStyleImpl.EXTLST$16, 0);
        }
    }
    
    public STOnOffStyleType.Enum getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.B$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableStyleTextStyleImpl.B$18);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STOnOffStyleType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOffStyleType xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOffStyleType stOnOffStyleType = (STOnOffStyleType)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.B$18);
            if (stOnOffStyleType == null) {
                stOnOffStyleType = (STOnOffStyleType)this.get_default_attribute_value(CTTableStyleTextStyleImpl.B$18);
            }
            return stOnOffStyleType;
        }
    }
    
    public boolean isSetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.B$18) != null;
        }
    }
    
    public void setB(final STOnOffStyleType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.B$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleTextStyleImpl.B$18);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetB(final STOnOffStyleType stOnOffStyleType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOffStyleType stOnOffStyleType2 = (STOnOffStyleType)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.B$18);
            if (stOnOffStyleType2 == null) {
                stOnOffStyleType2 = (STOnOffStyleType)this.get_store().add_attribute_user(CTTableStyleTextStyleImpl.B$18);
            }
            stOnOffStyleType2.set((XmlObject)stOnOffStyleType);
        }
    }
    
    public void unsetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleTextStyleImpl.B$18);
        }
    }
    
    public STOnOffStyleType.Enum getI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.I$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableStyleTextStyleImpl.I$20);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STOnOffStyleType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOffStyleType xgetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOffStyleType stOnOffStyleType = (STOnOffStyleType)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.I$20);
            if (stOnOffStyleType == null) {
                stOnOffStyleType = (STOnOffStyleType)this.get_default_attribute_value(CTTableStyleTextStyleImpl.I$20);
            }
            return stOnOffStyleType;
        }
    }
    
    public boolean isSetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.I$20) != null;
        }
    }
    
    public void setI(final STOnOffStyleType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.I$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleTextStyleImpl.I$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetI(final STOnOffStyleType stOnOffStyleType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOffStyleType stOnOffStyleType2 = (STOnOffStyleType)this.get_store().find_attribute_user(CTTableStyleTextStyleImpl.I$20);
            if (stOnOffStyleType2 == null) {
                stOnOffStyleType2 = (STOnOffStyleType)this.get_store().add_attribute_user(CTTableStyleTextStyleImpl.I$20);
            }
            stOnOffStyleType2.set((XmlObject)stOnOffStyleType);
        }
    }
    
    public void unsetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleTextStyleImpl.I$20);
        }
    }
    
    static {
        FONT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "font");
        FONTREF$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fontRef");
        SCRGBCLR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scrgbClr");
        SRGBCLR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srgbClr");
        HSLCLR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hslClr");
        SYSCLR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sysClr");
        SCHEMECLR$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "schemeClr");
        PRSTCLR$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstClr");
        EXTLST$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        B$18 = new QName("", "b");
        I$20 = new QName("", "i");
    }
}
