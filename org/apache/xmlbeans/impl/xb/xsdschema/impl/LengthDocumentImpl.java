package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.LengthDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class LengthDocumentImpl extends XmlComplexContentImpl implements LengthDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName LENGTH$0;
    
    public LengthDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NumFacet getLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().find_element_user(LengthDocumentImpl.LENGTH$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setLength(final NumFacet length) {
        this.generatedSetterHelperImpl(length, LengthDocumentImpl.LENGTH$0, 0, (short)1);
    }
    
    @Override
    public NumFacet addNewLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(LengthDocumentImpl.LENGTH$0);
            return target;
        }
    }
    
    static {
        LENGTH$0 = new QName("http://www.w3.org/2001/XMLSchema", "length");
    }
}
