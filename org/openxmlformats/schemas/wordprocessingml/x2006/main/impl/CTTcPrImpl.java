package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;

public class CTTcPrImpl extends CTTcPrInnerImpl implements CTTcPr
{
    private static final long serialVersionUID = 1L;
    private static final QName TCPRCHANGE$0;
    
    public CTTcPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTcPrChange getTcPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTcPrChange ctTcPrChange = (CTTcPrChange)this.get_store().find_element_user(CTTcPrImpl.TCPRCHANGE$0, 0);
            if (ctTcPrChange == null) {
                return null;
            }
            return ctTcPrChange;
        }
    }
    
    @Override
    public boolean isSetTcPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrImpl.TCPRCHANGE$0) != 0;
        }
    }
    
    @Override
    public void setTcPrChange(final CTTcPrChange ctTcPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTcPrChange, CTTcPrImpl.TCPRCHANGE$0, 0, (short)1);
    }
    
    @Override
    public CTTcPrChange addNewTcPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTcPrChange)this.get_store().add_element_user(CTTcPrImpl.TCPRCHANGE$0);
        }
    }
    
    @Override
    public void unsetTcPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrImpl.TCPRCHANGE$0, 0);
        }
    }
    
    static {
        TCPRCHANGE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcPrChange");
    }
}
