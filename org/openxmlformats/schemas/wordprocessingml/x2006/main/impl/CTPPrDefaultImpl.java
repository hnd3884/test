package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrDefault;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPPrDefaultImpl extends XmlComplexContentImpl implements CTPPrDefault
{
    private static final long serialVersionUID = 1L;
    private static final QName PPR$0;
    
    public CTPPrDefaultImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPPr getPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPPr ctpPr = (CTPPr)this.get_store().find_element_user(CTPPrDefaultImpl.PPR$0, 0);
            if (ctpPr == null) {
                return null;
            }
            return ctpPr;
        }
    }
    
    public boolean isSetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrDefaultImpl.PPR$0) != 0;
        }
    }
    
    public void setPPr(final CTPPr ctpPr) {
        this.generatedSetterHelperImpl((XmlObject)ctpPr, CTPPrDefaultImpl.PPR$0, 0, (short)1);
    }
    
    public CTPPr addNewPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPPr)this.get_store().add_element_user(CTPPrDefaultImpl.PPR$0);
        }
    }
    
    public void unsetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrDefaultImpl.PPR$0, 0);
        }
    }
    
    static {
        PPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pPr");
    }
}
