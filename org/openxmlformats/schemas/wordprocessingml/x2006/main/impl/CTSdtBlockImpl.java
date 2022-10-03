package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSdtBlockImpl extends XmlComplexContentImpl implements CTSdtBlock
{
    private static final long serialVersionUID = 1L;
    private static final QName SDTPR$0;
    private static final QName SDTENDPR$2;
    private static final QName SDTCONTENT$4;
    
    public CTSdtBlockImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSdtPr getSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtPr ctSdtPr = (CTSdtPr)this.get_store().find_element_user(CTSdtBlockImpl.SDTPR$0, 0);
            if (ctSdtPr == null) {
                return null;
            }
            return ctSdtPr;
        }
    }
    
    public boolean isSetSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtBlockImpl.SDTPR$0) != 0;
        }
    }
    
    public void setSdtPr(final CTSdtPr ctSdtPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtPr, CTSdtBlockImpl.SDTPR$0, 0, (short)1);
    }
    
    public CTSdtPr addNewSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtPr)this.get_store().add_element_user(CTSdtBlockImpl.SDTPR$0);
        }
    }
    
    public void unsetSdtPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtBlockImpl.SDTPR$0, 0);
        }
    }
    
    public CTSdtEndPr getSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtEndPr ctSdtEndPr = (CTSdtEndPr)this.get_store().find_element_user(CTSdtBlockImpl.SDTENDPR$2, 0);
            if (ctSdtEndPr == null) {
                return null;
            }
            return ctSdtEndPr;
        }
    }
    
    public boolean isSetSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtBlockImpl.SDTENDPR$2) != 0;
        }
    }
    
    public void setSdtEndPr(final CTSdtEndPr ctSdtEndPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtEndPr, CTSdtBlockImpl.SDTENDPR$2, 0, (short)1);
    }
    
    public CTSdtEndPr addNewSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtEndPr)this.get_store().add_element_user(CTSdtBlockImpl.SDTENDPR$2);
        }
    }
    
    public void unsetSdtEndPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtBlockImpl.SDTENDPR$2, 0);
        }
    }
    
    public CTSdtContentBlock getSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtContentBlock ctSdtContentBlock = (CTSdtContentBlock)this.get_store().find_element_user(CTSdtBlockImpl.SDTCONTENT$4, 0);
            if (ctSdtContentBlock == null) {
                return null;
            }
            return ctSdtContentBlock;
        }
    }
    
    public boolean isSetSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtBlockImpl.SDTCONTENT$4) != 0;
        }
    }
    
    public void setSdtContent(final CTSdtContentBlock ctSdtContentBlock) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtContentBlock, CTSdtBlockImpl.SDTCONTENT$4, 0, (short)1);
    }
    
    public CTSdtContentBlock addNewSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtContentBlock)this.get_store().add_element_user(CTSdtBlockImpl.SDTCONTENT$4);
        }
    }
    
    public void unsetSdtContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtBlockImpl.SDTCONTENT$4, 0);
        }
    }
    
    static {
        SDTPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtPr");
        SDTENDPR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtEndPr");
        SDTCONTENT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdtContent");
    }
}
