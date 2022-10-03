package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;

public class CTTblPrImpl extends CTTblPrBaseImpl implements CTTblPr
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLPRCHANGE$0;
    
    public CTTblPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTblPrChange getTblPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPrChange ctTblPrChange = (CTTblPrChange)this.get_store().find_element_user(CTTblPrImpl.TBLPRCHANGE$0, 0);
            if (ctTblPrChange == null) {
                return null;
            }
            return ctTblPrChange;
        }
    }
    
    @Override
    public boolean isSetTblPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrImpl.TBLPRCHANGE$0) != 0;
        }
    }
    
    @Override
    public void setTblPrChange(final CTTblPrChange ctTblPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPrChange, CTTblPrImpl.TBLPRCHANGE$0, 0, (short)1);
    }
    
    @Override
    public CTTblPrChange addNewTblPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPrChange)this.get_store().add_element_user(CTTblPrImpl.TBLPRCHANGE$0);
        }
    }
    
    @Override
    public void unsetTblPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrImpl.TBLPRCHANGE$0, 0);
        }
    }
    
    static {
        TBLPRCHANGE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblPrChange");
    }
}
