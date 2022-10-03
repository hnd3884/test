package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MinInclusiveDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MinInclusiveDocumentImpl extends XmlComplexContentImpl implements MinInclusiveDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MININCLUSIVE$0;
    
    public MinInclusiveDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Facet getMinInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().find_element_user(MinInclusiveDocumentImpl.MININCLUSIVE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMinInclusive(final Facet minInclusive) {
        this.generatedSetterHelperImpl(minInclusive, MinInclusiveDocumentImpl.MININCLUSIVE$0, 0, (short)1);
    }
    
    @Override
    public Facet addNewMinInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(MinInclusiveDocumentImpl.MININCLUSIVE$0);
            return target;
        }
    }
    
    static {
        MININCLUSIVE$0 = new QName("http://www.w3.org/2001/XMLSchema", "minInclusive");
    }
}
