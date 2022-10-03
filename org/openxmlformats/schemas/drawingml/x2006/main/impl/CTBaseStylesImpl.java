package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBaseStylesImpl extends XmlComplexContentImpl implements CTBaseStyles
{
    private static final long serialVersionUID = 1L;
    private static final QName CLRSCHEME$0;
    private static final QName FONTSCHEME$2;
    private static final QName FMTSCHEME$4;
    private static final QName EXTLST$6;
    
    public CTBaseStylesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTColorScheme getClrScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorScheme ctColorScheme = (CTColorScheme)this.get_store().find_element_user(CTBaseStylesImpl.CLRSCHEME$0, 0);
            if (ctColorScheme == null) {
                return null;
            }
            return ctColorScheme;
        }
    }
    
    public void setClrScheme(final CTColorScheme ctColorScheme) {
        this.generatedSetterHelperImpl((XmlObject)ctColorScheme, CTBaseStylesImpl.CLRSCHEME$0, 0, (short)1);
    }
    
    public CTColorScheme addNewClrScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorScheme)this.get_store().add_element_user(CTBaseStylesImpl.CLRSCHEME$0);
        }
    }
    
    public CTFontScheme getFontScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontScheme ctFontScheme = (CTFontScheme)this.get_store().find_element_user(CTBaseStylesImpl.FONTSCHEME$2, 0);
            if (ctFontScheme == null) {
                return null;
            }
            return ctFontScheme;
        }
    }
    
    public void setFontScheme(final CTFontScheme ctFontScheme) {
        this.generatedSetterHelperImpl((XmlObject)ctFontScheme, CTBaseStylesImpl.FONTSCHEME$2, 0, (short)1);
    }
    
    public CTFontScheme addNewFontScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontScheme)this.get_store().add_element_user(CTBaseStylesImpl.FONTSCHEME$2);
        }
    }
    
    public CTStyleMatrix getFmtScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrix ctStyleMatrix = (CTStyleMatrix)this.get_store().find_element_user(CTBaseStylesImpl.FMTSCHEME$4, 0);
            if (ctStyleMatrix == null) {
                return null;
            }
            return ctStyleMatrix;
        }
    }
    
    public void setFmtScheme(final CTStyleMatrix ctStyleMatrix) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrix, CTBaseStylesImpl.FMTSCHEME$4, 0, (short)1);
    }
    
    public CTStyleMatrix addNewFmtScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrix)this.get_store().add_element_user(CTBaseStylesImpl.FMTSCHEME$4);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTBaseStylesImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBaseStylesImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBaseStylesImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTBaseStylesImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBaseStylesImpl.EXTLST$6, 0);
        }
    }
    
    static {
        CLRSCHEME$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrScheme");
        FONTSCHEME$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fontScheme");
        FMTSCHEME$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fmtScheme");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
