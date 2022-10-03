package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CalcChainDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CalcChainDocumentImpl extends XmlComplexContentImpl implements CalcChainDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CALCCHAIN$0;
    
    public CalcChainDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCalcChain getCalcChain() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCalcChain ctCalcChain = (CTCalcChain)this.get_store().find_element_user(CalcChainDocumentImpl.CALCCHAIN$0, 0);
            if (ctCalcChain == null) {
                return null;
            }
            return ctCalcChain;
        }
    }
    
    public void setCalcChain(final CTCalcChain ctCalcChain) {
        this.generatedSetterHelperImpl((XmlObject)ctCalcChain, CalcChainDocumentImpl.CALCCHAIN$0, 0, (short)1);
    }
    
    public CTCalcChain addNewCalcChain() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalcChain)this.get_store().add_element_user(CalcChainDocumentImpl.CALCCHAIN$0);
        }
    }
    
    static {
        CALCCHAIN$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calcChain");
    }
}
