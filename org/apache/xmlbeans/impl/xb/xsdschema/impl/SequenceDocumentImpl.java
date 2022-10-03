package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.SequenceDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SequenceDocumentImpl extends XmlComplexContentImpl implements SequenceDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SEQUENCE$0;
    
    public SequenceDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public ExplicitGroup getSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(SequenceDocumentImpl.SEQUENCE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setSequence(final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, SequenceDocumentImpl.SEQUENCE$0, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(SequenceDocumentImpl.SEQUENCE$0);
            return target;
        }
    }
    
    static {
        SEQUENCE$0 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
    }
}
