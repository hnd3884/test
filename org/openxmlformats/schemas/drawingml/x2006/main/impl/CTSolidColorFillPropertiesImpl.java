package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSolidColorFillPropertiesImpl extends XmlComplexContentImpl implements CTSolidColorFillProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName SCRGBCLR$0;
    private static final QName SRGBCLR$2;
    private static final QName HSLCLR$4;
    private static final QName SYSCLR$6;
    private static final QName SCHEMECLR$8;
    private static final QName PRSTCLR$10;
    
    public CTSolidColorFillPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTScRgbColor getScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScRgbColor ctScRgbColor = (CTScRgbColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.SCRGBCLR$0, 0);
            if (ctScRgbColor == null) {
                return null;
            }
            return ctScRgbColor;
        }
    }
    
    public boolean isSetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.SCRGBCLR$0) != 0;
        }
    }
    
    public void setScrgbClr(final CTScRgbColor ctScRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctScRgbColor, CTSolidColorFillPropertiesImpl.SCRGBCLR$0, 0, (short)1);
    }
    
    public CTScRgbColor addNewScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScRgbColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.SCRGBCLR$0);
        }
    }
    
    public void unsetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.SCRGBCLR$0, 0);
        }
    }
    
    public CTSRgbColor getSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSRgbColor ctsRgbColor = (CTSRgbColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.SRGBCLR$2, 0);
            if (ctsRgbColor == null) {
                return null;
            }
            return ctsRgbColor;
        }
    }
    
    public boolean isSetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.SRGBCLR$2) != 0;
        }
    }
    
    public void setSrgbClr(final CTSRgbColor ctsRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctsRgbColor, CTSolidColorFillPropertiesImpl.SRGBCLR$2, 0, (short)1);
    }
    
    public CTSRgbColor addNewSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSRgbColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.SRGBCLR$2);
        }
    }
    
    public void unsetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.SRGBCLR$2, 0);
        }
    }
    
    public CTHslColor getHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHslColor ctHslColor = (CTHslColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.HSLCLR$4, 0);
            if (ctHslColor == null) {
                return null;
            }
            return ctHslColor;
        }
    }
    
    public boolean isSetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.HSLCLR$4) != 0;
        }
    }
    
    public void setHslClr(final CTHslColor ctHslColor) {
        this.generatedSetterHelperImpl((XmlObject)ctHslColor, CTSolidColorFillPropertiesImpl.HSLCLR$4, 0, (short)1);
    }
    
    public CTHslColor addNewHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHslColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.HSLCLR$4);
        }
    }
    
    public void unsetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.HSLCLR$4, 0);
        }
    }
    
    public CTSystemColor getSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSystemColor ctSystemColor = (CTSystemColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.SYSCLR$6, 0);
            if (ctSystemColor == null) {
                return null;
            }
            return ctSystemColor;
        }
    }
    
    public boolean isSetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.SYSCLR$6) != 0;
        }
    }
    
    public void setSysClr(final CTSystemColor ctSystemColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSystemColor, CTSolidColorFillPropertiesImpl.SYSCLR$6, 0, (short)1);
    }
    
    public CTSystemColor addNewSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSystemColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.SYSCLR$6);
        }
    }
    
    public void unsetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.SYSCLR$6, 0);
        }
    }
    
    public CTSchemeColor getSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchemeColor ctSchemeColor = (CTSchemeColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.SCHEMECLR$8, 0);
            if (ctSchemeColor == null) {
                return null;
            }
            return ctSchemeColor;
        }
    }
    
    public boolean isSetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.SCHEMECLR$8) != 0;
        }
    }
    
    public void setSchemeClr(final CTSchemeColor ctSchemeColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSchemeColor, CTSolidColorFillPropertiesImpl.SCHEMECLR$8, 0, (short)1);
    }
    
    public CTSchemeColor addNewSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemeColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.SCHEMECLR$8);
        }
    }
    
    public void unsetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.SCHEMECLR$8, 0);
        }
    }
    
    public CTPresetColor getPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetColor ctPresetColor = (CTPresetColor)this.get_store().find_element_user(CTSolidColorFillPropertiesImpl.PRSTCLR$10, 0);
            if (ctPresetColor == null) {
                return null;
            }
            return ctPresetColor;
        }
    }
    
    public boolean isSetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSolidColorFillPropertiesImpl.PRSTCLR$10) != 0;
        }
    }
    
    public void setPrstClr(final CTPresetColor ctPresetColor) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetColor, CTSolidColorFillPropertiesImpl.PRSTCLR$10, 0, (short)1);
    }
    
    public CTPresetColor addNewPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetColor)this.get_store().add_element_user(CTSolidColorFillPropertiesImpl.PRSTCLR$10);
        }
    }
    
    public void unsetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSolidColorFillPropertiesImpl.PRSTCLR$10, 0);
        }
    }
    
    static {
        SCRGBCLR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scrgbClr");
        SRGBCLR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srgbClr");
        HSLCLR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hslClr");
        SYSCLR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sysClr");
        SCHEMECLR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "schemeClr");
        PRSTCLR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstClr");
    }
}
