package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRPrDefaultImpl extends XmlComplexContentImpl implements CTRPrDefault
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    
    public CTRPrDefaultImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRPr getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTRPrDefaultImpl.RPR$0, 0);
            if (ctrPr == null) {
                return null;
            }
            return ctrPr;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrDefaultImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTRPrDefaultImpl.RPR$0, 0, (short)1);
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTRPrDefaultImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrDefaultImpl.RPR$0, 0);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
    }
}
