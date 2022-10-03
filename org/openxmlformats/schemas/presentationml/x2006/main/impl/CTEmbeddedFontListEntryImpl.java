package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontDataId;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontListEntry;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEmbeddedFontListEntryImpl extends XmlComplexContentImpl implements CTEmbeddedFontListEntry
{
    private static final long serialVersionUID = 1L;
    private static final QName FONT$0;
    private static final QName REGULAR$2;
    private static final QName BOLD$4;
    private static final QName ITALIC$6;
    private static final QName BOLDITALIC$8;
    
    public CTEmbeddedFontListEntryImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextFont getFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTEmbeddedFontListEntryImpl.FONT$0, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public void setFont(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTEmbeddedFontListEntryImpl.FONT$0, 0, (short)1);
    }
    
    public CTTextFont addNewFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTEmbeddedFontListEntryImpl.FONT$0);
        }
    }
    
    public CTEmbeddedFontDataId getRegular() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontDataId ctEmbeddedFontDataId = (CTEmbeddedFontDataId)this.get_store().find_element_user(CTEmbeddedFontListEntryImpl.REGULAR$2, 0);
            if (ctEmbeddedFontDataId == null) {
                return null;
            }
            return ctEmbeddedFontDataId;
        }
    }
    
    public boolean isSetRegular() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEmbeddedFontListEntryImpl.REGULAR$2) != 0;
        }
    }
    
    public void setRegular(final CTEmbeddedFontDataId ctEmbeddedFontDataId) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedFontDataId, CTEmbeddedFontListEntryImpl.REGULAR$2, 0, (short)1);
    }
    
    public CTEmbeddedFontDataId addNewRegular() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontDataId)this.get_store().add_element_user(CTEmbeddedFontListEntryImpl.REGULAR$2);
        }
    }
    
    public void unsetRegular() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEmbeddedFontListEntryImpl.REGULAR$2, 0);
        }
    }
    
    public CTEmbeddedFontDataId getBold() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontDataId ctEmbeddedFontDataId = (CTEmbeddedFontDataId)this.get_store().find_element_user(CTEmbeddedFontListEntryImpl.BOLD$4, 0);
            if (ctEmbeddedFontDataId == null) {
                return null;
            }
            return ctEmbeddedFontDataId;
        }
    }
    
    public boolean isSetBold() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEmbeddedFontListEntryImpl.BOLD$4) != 0;
        }
    }
    
    public void setBold(final CTEmbeddedFontDataId ctEmbeddedFontDataId) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedFontDataId, CTEmbeddedFontListEntryImpl.BOLD$4, 0, (short)1);
    }
    
    public CTEmbeddedFontDataId addNewBold() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontDataId)this.get_store().add_element_user(CTEmbeddedFontListEntryImpl.BOLD$4);
        }
    }
    
    public void unsetBold() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEmbeddedFontListEntryImpl.BOLD$4, 0);
        }
    }
    
    public CTEmbeddedFontDataId getItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontDataId ctEmbeddedFontDataId = (CTEmbeddedFontDataId)this.get_store().find_element_user(CTEmbeddedFontListEntryImpl.ITALIC$6, 0);
            if (ctEmbeddedFontDataId == null) {
                return null;
            }
            return ctEmbeddedFontDataId;
        }
    }
    
    public boolean isSetItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEmbeddedFontListEntryImpl.ITALIC$6) != 0;
        }
    }
    
    public void setItalic(final CTEmbeddedFontDataId ctEmbeddedFontDataId) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedFontDataId, CTEmbeddedFontListEntryImpl.ITALIC$6, 0, (short)1);
    }
    
    public CTEmbeddedFontDataId addNewItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontDataId)this.get_store().add_element_user(CTEmbeddedFontListEntryImpl.ITALIC$6);
        }
    }
    
    public void unsetItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEmbeddedFontListEntryImpl.ITALIC$6, 0);
        }
    }
    
    public CTEmbeddedFontDataId getBoldItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontDataId ctEmbeddedFontDataId = (CTEmbeddedFontDataId)this.get_store().find_element_user(CTEmbeddedFontListEntryImpl.BOLDITALIC$8, 0);
            if (ctEmbeddedFontDataId == null) {
                return null;
            }
            return ctEmbeddedFontDataId;
        }
    }
    
    public boolean isSetBoldItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEmbeddedFontListEntryImpl.BOLDITALIC$8) != 0;
        }
    }
    
    public void setBoldItalic(final CTEmbeddedFontDataId ctEmbeddedFontDataId) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedFontDataId, CTEmbeddedFontListEntryImpl.BOLDITALIC$8, 0, (short)1);
    }
    
    public CTEmbeddedFontDataId addNewBoldItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontDataId)this.get_store().add_element_user(CTEmbeddedFontListEntryImpl.BOLDITALIC$8);
        }
    }
    
    public void unsetBoldItalic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEmbeddedFontListEntryImpl.BOLDITALIC$8, 0);
        }
    }
    
    static {
        FONT$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "font");
        REGULAR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "regular");
        BOLD$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bold");
        ITALIC$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "italic");
        BOLDITALIC$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "boldItalic");
    }
}
