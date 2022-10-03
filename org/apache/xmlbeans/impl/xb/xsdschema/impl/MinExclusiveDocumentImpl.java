package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MinExclusiveDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MinExclusiveDocumentImpl extends XmlComplexContentImpl implements MinExclusiveDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MINEXCLUSIVE$0;
    
    public MinExclusiveDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Facet getMinExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().find_element_user(MinExclusiveDocumentImpl.MINEXCLUSIVE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMinExclusive(final Facet minExclusive) {
        this.generatedSetterHelperImpl(minExclusive, MinExclusiveDocumentImpl.MINEXCLUSIVE$0, 0, (short)1);
    }
    
    @Override
    public Facet addNewMinExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(MinExclusiveDocumentImpl.MINEXCLUSIVE$0);
            return target;
        }
    }
    
    static {
        MINEXCLUSIVE$0 = new QName("http://www.w3.org/2001/XMLSchema", "minExclusive");
    }
}
