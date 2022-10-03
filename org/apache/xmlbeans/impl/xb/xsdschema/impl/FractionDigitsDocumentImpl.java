package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.FractionDigitsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class FractionDigitsDocumentImpl extends XmlComplexContentImpl implements FractionDigitsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName FRACTIONDIGITS$0;
    
    public FractionDigitsDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NumFacet getFractionDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().find_element_user(FractionDigitsDocumentImpl.FRACTIONDIGITS$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setFractionDigits(final NumFacet fractionDigits) {
        this.generatedSetterHelperImpl(fractionDigits, FractionDigitsDocumentImpl.FRACTIONDIGITS$0, 0, (short)1);
    }
    
    @Override
    public NumFacet addNewFractionDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(FractionDigitsDocumentImpl.FRACTIONDIGITS$0);
            return target;
        }
    }
    
    static {
        FRACTIONDIGITS$0 = new QName("http://www.w3.org/2001/XMLSchema", "fractionDigits");
    }
}
