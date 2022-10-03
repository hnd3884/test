package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;

public class CTTblGridImpl extends CTTblGridBaseImpl implements CTTblGrid
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLGRIDCHANGE$0;
    
    public CTTblGridImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTblGridChange getTblGridChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblGridChange ctTblGridChange = (CTTblGridChange)this.get_store().find_element_user(CTTblGridImpl.TBLGRIDCHANGE$0, 0);
            if (ctTblGridChange == null) {
                return null;
            }
            return ctTblGridChange;
        }
    }
    
    @Override
    public boolean isSetTblGridChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblGridImpl.TBLGRIDCHANGE$0) != 0;
        }
    }
    
    @Override
    public void setTblGridChange(final CTTblGridChange ctTblGridChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTblGridChange, CTTblGridImpl.TBLGRIDCHANGE$0, 0, (short)1);
    }
    
    @Override
    public CTTblGridChange addNewTblGridChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblGridChange)this.get_store().add_element_user(CTTblGridImpl.TBLGRIDCHANGE$0);
        }
    }
    
    @Override
    public void unsetTblGridChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblGridImpl.TBLGRIDCHANGE$0, 0);
        }
    }
    
    static {
        TBLGRIDCHANGE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblGridChange");
    }
}
