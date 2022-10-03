package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MinLengthDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MinLengthDocumentImpl extends XmlComplexContentImpl implements MinLengthDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MINLENGTH$0;
    
    public MinLengthDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NumFacet getMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().find_element_user(MinLengthDocumentImpl.MINLENGTH$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMinLength(final NumFacet minLength) {
        this.generatedSetterHelperImpl(minLength, MinLengthDocumentImpl.MINLENGTH$0, 0, (short)1);
    }
    
    @Override
    public NumFacet addNewMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(MinLengthDocumentImpl.MINLENGTH$0);
            return target;
        }
    }
    
    static {
        MINLENGTH$0 = new QName("http://www.w3.org/2001/XMLSchema", "minLength");
    }
}
