package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.MaxExclusiveDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MaxExclusiveDocumentImpl extends XmlComplexContentImpl implements MaxExclusiveDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MAXEXCLUSIVE$0;
    
    public MaxExclusiveDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Facet getMaxExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().find_element_user(MaxExclusiveDocumentImpl.MAXEXCLUSIVE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setMaxExclusive(final Facet maxExclusive) {
        this.generatedSetterHelperImpl(maxExclusive, MaxExclusiveDocumentImpl.MAXEXCLUSIVE$0, 0, (short)1);
    }
    
    @Override
    public Facet addNewMaxExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(MaxExclusiveDocumentImpl.MAXEXCLUSIVE$0);
            return target;
        }
    }
    
    static {
        MAXEXCLUSIVE$0 = new QName("http://www.w3.org/2001/XMLSchema", "maxExclusive");
    }
}
