package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDocPart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSdtDocPartImpl extends XmlComplexContentImpl implements CTSdtDocPart
{
    private static final long serialVersionUID = 1L;
    private static final QName DOCPARTGALLERY$0;
    private static final QName DOCPARTCATEGORY$2;
    private static final QName DOCPARTUNIQUE$4;
    
    public CTSdtDocPartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTString getDocPartGallery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSdtDocPartImpl.DOCPARTGALLERY$0, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetDocPartGallery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtDocPartImpl.DOCPARTGALLERY$0) != 0;
        }
    }
    
    public void setDocPartGallery(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSdtDocPartImpl.DOCPARTGALLERY$0, 0, (short)1);
    }
    
    public CTString addNewDocPartGallery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSdtDocPartImpl.DOCPARTGALLERY$0);
        }
    }
    
    public void unsetDocPartGallery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtDocPartImpl.DOCPARTGALLERY$0, 0);
        }
    }
    
    public CTString getDocPartCategory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSdtDocPartImpl.DOCPARTCATEGORY$2, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetDocPartCategory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtDocPartImpl.DOCPARTCATEGORY$2) != 0;
        }
    }
    
    public void setDocPartCategory(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSdtDocPartImpl.DOCPARTCATEGORY$2, 0, (short)1);
    }
    
    public CTString addNewDocPartCategory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSdtDocPartImpl.DOCPARTCATEGORY$2);
        }
    }
    
    public void unsetDocPartCategory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtDocPartImpl.DOCPARTCATEGORY$2, 0);
        }
    }
    
    public CTOnOff getDocPartUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSdtDocPartImpl.DOCPARTUNIQUE$4, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDocPartUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtDocPartImpl.DOCPARTUNIQUE$4) != 0;
        }
    }
    
    public void setDocPartUnique(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSdtDocPartImpl.DOCPARTUNIQUE$4, 0, (short)1);
    }
    
    public CTOnOff addNewDocPartUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSdtDocPartImpl.DOCPARTUNIQUE$4);
        }
    }
    
    public void unsetDocPartUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtDocPartImpl.DOCPARTUNIQUE$4, 0);
        }
    }
    
    static {
        DOCPARTGALLERY$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docPartGallery");
        DOCPARTCATEGORY$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docPartCategory");
        DOCPARTUNIQUE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docPartUnique");
    }
}
