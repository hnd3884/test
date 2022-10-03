package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrExChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrEx;

public class CTTblPrExImpl extends CTTblPrExBaseImpl implements CTTblPrEx
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLPREXCHANGE$0;
    
    public CTTblPrExImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTblPrExChange getTblPrExChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPrExChange ctTblPrExChange = (CTTblPrExChange)this.get_store().find_element_user(CTTblPrExImpl.TBLPREXCHANGE$0, 0);
            if (ctTblPrExChange == null) {
                return null;
            }
            return ctTblPrExChange;
        }
    }
    
    @Override
    public boolean isSetTblPrExChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExImpl.TBLPREXCHANGE$0) != 0;
        }
    }
    
    @Override
    public void setTblPrExChange(final CTTblPrExChange ctTblPrExChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPrExChange, CTTblPrExImpl.TBLPREXCHANGE$0, 0, (short)1);
    }
    
    @Override
    public CTTblPrExChange addNewTblPrExChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPrExChange)this.get_store().add_element_user(CTTblPrExImpl.TBLPREXCHANGE$0);
        }
    }
    
    @Override
    public void unsetTblPrExChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExImpl.TBLPREXCHANGE$0, 0);
        }
    }
    
    static {
        TBLPREXCHANGE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblPrExChange");
    }
}
