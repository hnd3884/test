package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.TotalDigitsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TotalDigitsDocumentImpl extends XmlComplexContentImpl implements TotalDigitsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName TOTALDIGITS$0;
    
    public TotalDigitsDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public TotalDigits getTotalDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TotalDigits target = null;
            target = (TotalDigits)this.get_store().find_element_user(TotalDigitsDocumentImpl.TOTALDIGITS$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setTotalDigits(final TotalDigits totalDigits) {
        this.generatedSetterHelperImpl(totalDigits, TotalDigitsDocumentImpl.TOTALDIGITS$0, 0, (short)1);
    }
    
    @Override
    public TotalDigits addNewTotalDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TotalDigits target = null;
            target = (TotalDigits)this.get_store().add_element_user(TotalDigitsDocumentImpl.TOTALDIGITS$0);
            return target;
        }
    }
    
    static {
        TOTALDIGITS$0 = new QName("http://www.w3.org/2001/XMLSchema", "totalDigits");
    }
    
    public static class TotalDigitsImpl extends NumFacetImpl implements TotalDigits
    {
        private static final long serialVersionUID = 1L;
        
        public TotalDigitsImpl(final SchemaType sType) {
            super(sType);
        }
    }
}
