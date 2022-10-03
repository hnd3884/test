package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSdtRunImpl extends XmlComplexContentImpl implements CTSdtRun
{
    private static final long serialVersionUID = 1L;
    private static final QName SDTPR$0;
    private static final QName SDTENDPR$2;
    private static final QName SDTCONTENT$4;
    
    public CTSdtRunImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSdtPr getSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtPr ctSdtPr = (CTSdtPr)this.get_store().find_element_user(CTSdtRunImpl.SDTPR$0, 0);
            if (ctSdtPr == null) {
                return null;
            }
            return ctSdtPr;
        }
    }
    
    public boolean isSetSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtRunImpl.SDTPR$0) != 0;
        }
    }
    
    public void setSdtPr(final CTSdtPr ctSdtPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtPr, CTSdtRunImpl.SDTPR$0, 0, (short)1);
    }
    
    public CTSdtPr addNewSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtPr)this.get_store().add_element_user(CTSdtRunImpl.SDTPR$0);
        }
    }
    
    public void unsetSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtRunImpl.SDTPR$0, 0);
        }
    }
    
    public CTSdtEndPr getSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtEndPr ctSdtEndPr = (CTSdtEndPr)this.get_store().find_element_user(CTSdtRunImpl.SDTENDPR$2, 0);
            if (ctSdtEndPr == null) {
                return null;
            }
            return ctSdtEndPr;
        }
    }
    
    public boolean isSetSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtRunImpl.SDTENDPR$2) != 0;
        }
    }
    
    public void setSdtEndPr(final CTSdtEndPr ctSdtEndPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtEndPr, CTSdtRunImpl.SDTENDPR$2, 0, (short)1);
    }
    
    public CTSdtEndPr addNewSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtEndPr)this.get_store().add_element_user(CTSdtRunImpl.SDTENDPR$2);
        }
    }
    
    public void unsetSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtRunImpl.SDTENDPR$2, 0);
        }
    }
    
    public CTSdtContentRun getSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtContentRun ctSdtContentRun = (CTSdtContentRun)this.get_store().find_element_user(CTSdtRunImpl.SDTCONTENT$4, 0);
            if (ctSdtContentRun == null) {
                return null;
            }
            return ctSdtContentRun;
        }
    }
    
    public boolean isSetSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtRunImpl.SDTCONTENT$4) != 0;
        }
    }
    
    public void setSdtContent(final CTSdtContentRun ctSdtContentRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtContentRun, CTSdtRunImpl.SDTCONTENT$4, 0, (short)1);
    }
    
    public CTSdtContentRun addNewSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtContentRun)this.get_store().add_element_user(CTSdtRunImpl.SDTCONTENT$4);
        }
    }
    
    public void unsetSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtRunImpl.SDTCONTENT$4, 0);
        }
    }
    
    static {
        SDTPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtPr");
        SDTENDPR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtEndPr");
        SDTCONTENT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtContent");
    }
}
