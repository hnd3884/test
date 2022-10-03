package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MaxLengthDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MaxLengthDocumentImpl extends XmlComplexContentImpl implements MaxLengthDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MAXLENGTH$0;
    
    public MaxLengthDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NumFacet getMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().find_element_user(MaxLengthDocumentImpl.MAXLENGTH$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMaxLength(final NumFacet maxLength) {
        this.generatedSetterHelperImpl(maxLength, MaxLengthDocumentImpl.MAXLENGTH$0, 0, (short)1);
    }
    
    @Override
    public NumFacet addNewMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(MaxLengthDocumentImpl.MAXLENGTH$0);
            return target;
        }
    }
    
    static {
        MAXLENGTH$0 = new QName("http://www.w3.org/2001/XMLSchema", "maxLength");
    }
}
