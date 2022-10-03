package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MaxInclusiveDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MaxInclusiveDocumentImpl extends XmlComplexContentImpl implements MaxInclusiveDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MAXINCLUSIVE$0;
    
    public MaxInclusiveDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Facet getMaxInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().find_element_user(MaxInclusiveDocumentImpl.MAXINCLUSIVE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMaxInclusive(final Facet maxInclusive) {
        this.generatedSetterHelperImpl(maxInclusive, MaxInclusiveDocumentImpl.MAXINCLUSIVE$0, 0, (short)1);
    }
    
    @Override
    public Facet addNewMaxInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(MaxInclusiveDocumentImpl.MAXINCLUSIVE$0);
            return target;
        }
    }
    
    static {
        MAXINCLUSIVE$0 = new QName("http://www.w3.org/2001/XMLSchema", "maxInclusive");
    }
}
