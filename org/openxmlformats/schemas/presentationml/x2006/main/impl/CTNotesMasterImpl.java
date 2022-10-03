package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNotesMasterImpl extends XmlComplexContentImpl implements CTNotesMaster
{
    private static final long serialVersionUID = 1L;
    private static final QName CSLD$0;
    private static final QName CLRMAP$2;
    private static final QName HF$4;
    private static final QName NOTESSTYLE$6;
    private static final QName EXTLST$8;
    
    public CTNotesMasterImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommonSlideData getCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommonSlideData ctCommonSlideData = (CTCommonSlideData)this.get_store().find_element_user(CTNotesMasterImpl.CSLD$0, 0);
            if (ctCommonSlideData == null) {
                return null;
            }
            return ctCommonSlideData;
        }
    }
    
    public void setCSld(final CTCommonSlideData ctCommonSlideData) {
        this.generatedSetterHelperImpl((XmlObject)ctCommonSlideData, CTNotesMasterImpl.CSLD$0, 0, (short)1);
    }
    
    public CTCommonSlideData addNewCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommonSlideData)this.get_store().add_element_user(CTNotesMasterImpl.CSLD$0);
        }
    }
    
    public CTColorMapping getClrMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMapping ctColorMapping = (CTColorMapping)this.get_store().find_element_user(CTNotesMasterImpl.CLRMAP$2, 0);
            if (ctColorMapping == null) {
                return null;
            }
            return ctColorMapping;
        }
    }
    
    public void setClrMap(final CTColorMapping ctColorMapping) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMapping, CTNotesMasterImpl.CLRMAP$2, 0, (short)1);
    }
    
    public CTColorMapping addNewClrMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMapping)this.get_store().add_element_user(CTNotesMasterImpl.CLRMAP$2);
        }
    }
    
    public CTHeaderFooter getHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTNotesMasterImpl.HF$4, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesMasterImpl.HF$4) != 0;
        }
    }
    
    public void setHf(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTNotesMasterImpl.HF$4, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTNotesMasterImpl.HF$4);
        }
    }
    
    public void unsetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesMasterImpl.HF$4, 0);
        }
    }
    
    public CTTextListStyle getNotesStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTNotesMasterImpl.NOTESSTYLE$6, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetNotesStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesMasterImpl.NOTESSTYLE$6) != 0;
        }
    }
    
    public void setNotesStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTNotesMasterImpl.NOTESSTYLE$6, 0, (short)1);
    }
    
    public CTTextListStyle addNewNotesStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTNotesMasterImpl.NOTESSTYLE$6);
        }
    }
    
    public void unsetNotesStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesMasterImpl.NOTESSTYLE$6, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTNotesMasterImpl.EXTLST$8, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesMasterImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTNotesMasterImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTNotesMasterImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesMasterImpl.EXTLST$8, 0);
        }
    }
    
    static {
        CSLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cSld");
        CLRMAP$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "clrMap");
        HF$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "hf");
        NOTESSTYLE$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notesStyle");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
