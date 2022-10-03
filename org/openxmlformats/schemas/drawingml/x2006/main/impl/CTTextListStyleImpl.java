package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextListStyleImpl extends XmlComplexContentImpl implements CTTextListStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName DEFPPR$0;
    private static final QName LVL1PPR$2;
    private static final QName LVL2PPR$4;
    private static final QName LVL3PPR$6;
    private static final QName LVL4PPR$8;
    private static final QName LVL5PPR$10;
    private static final QName LVL6PPR$12;
    private static final QName LVL7PPR$14;
    private static final QName LVL8PPR$16;
    private static final QName LVL9PPR$18;
    private static final QName EXTLST$20;
    
    public CTTextListStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextParagraphProperties getDefPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.DEFPPR$0, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetDefPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.DEFPPR$0) != 0;
        }
    }
    
    public void setDefPPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.DEFPPR$0, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewDefPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.DEFPPR$0);
        }
    }
    
    public void unsetDefPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.DEFPPR$0, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl1PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL1PPR$2, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl1PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL1PPR$2) != 0;
        }
    }
    
    public void setLvl1PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL1PPR$2, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl1PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL1PPR$2);
        }
    }
    
    public void unsetLvl1PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL1PPR$2, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl2PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL2PPR$4, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl2PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL2PPR$4) != 0;
        }
    }
    
    public void setLvl2PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL2PPR$4, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl2PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL2PPR$4);
        }
    }
    
    public void unsetLvl2PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL2PPR$4, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl3PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL3PPR$6, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl3PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL3PPR$6) != 0;
        }
    }
    
    public void setLvl3PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL3PPR$6, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl3PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL3PPR$6);
        }
    }
    
    public void unsetLvl3PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL3PPR$6, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl4PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL4PPR$8, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl4PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL4PPR$8) != 0;
        }
    }
    
    public void setLvl4PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL4PPR$8, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl4PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL4PPR$8);
        }
    }
    
    public void unsetLvl4PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL4PPR$8, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl5PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL5PPR$10, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl5PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL5PPR$10) != 0;
        }
    }
    
    public void setLvl5PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL5PPR$10, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl5PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL5PPR$10);
        }
    }
    
    public void unsetLvl5PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL5PPR$10, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl6PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL6PPR$12, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl6PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL6PPR$12) != 0;
        }
    }
    
    public void setLvl6PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL6PPR$12, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl6PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL6PPR$12);
        }
    }
    
    public void unsetLvl6PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL6PPR$12, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl7PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL7PPR$14, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl7PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL7PPR$14) != 0;
        }
    }
    
    public void setLvl7PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL7PPR$14, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl7PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL7PPR$14);
        }
    }
    
    public void unsetLvl7PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL7PPR$14, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl8PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL8PPR$16, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl8PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL8PPR$16) != 0;
        }
    }
    
    public void setLvl8PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL8PPR$16, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl8PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL8PPR$16);
        }
    }
    
    public void unsetLvl8PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL8PPR$16, 0);
        }
    }
    
    public CTTextParagraphProperties getLvl9PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextListStyleImpl.LVL9PPR$18, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetLvl9PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.LVL9PPR$18) != 0;
        }
    }
    
    public void setLvl9PPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextListStyleImpl.LVL9PPR$18, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewLvl9PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextListStyleImpl.LVL9PPR$18);
        }
    }
    
    public void unsetLvl9PPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.LVL9PPR$18, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTextListStyleImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextListStyleImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextListStyleImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTextListStyleImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextListStyleImpl.EXTLST$20, 0);
        }
    }
    
    static {
        DEFPPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "defPPr");
        LVL1PPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl1pPr");
        LVL2PPR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl2pPr");
        LVL3PPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl3pPr");
        LVL4PPR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl4pPr");
        LVL5PPR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl5pPr");
        LVL6PPR$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl6pPr");
        LVL7PPR$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl7pPr");
        LVL8PPR$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl8pPr");
        LVL9PPR$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl9pPr");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
