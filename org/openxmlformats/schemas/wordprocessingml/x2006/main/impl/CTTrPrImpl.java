package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrChange;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;

public class CTTrPrImpl extends CTTrPrBaseImpl implements CTTrPr
{
    private static final long serialVersionUID = 1L;
    private static final QName INS$0;
    private static final QName DEL$2;
    private static final QName TRPRCHANGE$4;
    
    public CTTrPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTrackChange getIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTrPrImpl.INS$0, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public boolean isSetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrImpl.INS$0) != 0;
        }
    }
    
    @Override
    public void setIns(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTrPrImpl.INS$0, 0, (short)1);
    }
    
    @Override
    public CTTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTrPrImpl.INS$0);
        }
    }
    
    @Override
    public void unsetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrImpl.INS$0, 0);
        }
    }
    
    @Override
    public CTTrackChange getDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTrPrImpl.DEL$2, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public boolean isSetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrImpl.DEL$2) != 0;
        }
    }
    
    @Override
    public void setDel(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTrPrImpl.DEL$2, 0, (short)1);
    }
    
    @Override
    public CTTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTrPrImpl.DEL$2);
        }
    }
    
    @Override
    public void unsetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrImpl.DEL$2, 0);
        }
    }
    
    @Override
    public CTTrPrChange getTrPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrPrChange ctTrPrChange = (CTTrPrChange)this.get_store().find_element_user(CTTrPrImpl.TRPRCHANGE$4, 0);
            if (ctTrPrChange == null) {
                return null;
            }
            return ctTrPrChange;
        }
    }
    
    @Override
    public boolean isSetTrPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrImpl.TRPRCHANGE$4) != 0;
        }
    }
    
    @Override
    public void setTrPrChange(final CTTrPrChange ctTrPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrPrChange, CTTrPrImpl.TRPRCHANGE$4, 0, (short)1);
    }
    
    @Override
    public CTTrPrChange addNewTrPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrPrChange)this.get_store().add_element_user(CTTrPrImpl.TRPRCHANGE$4);
        }
    }
    
    @Override
    public void unsetTrPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrImpl.TRPRCHANGE$4, 0);
        }
    }
    
    static {
        INS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        TRPRCHANGE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trPrChange");
    }
}
