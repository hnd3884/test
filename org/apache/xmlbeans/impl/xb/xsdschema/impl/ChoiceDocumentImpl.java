package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ChoiceDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ChoiceDocumentImpl extends XmlComplexContentImpl implements ChoiceDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CHOICE$0;
    
    public ChoiceDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public ExplicitGroup getChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(ChoiceDocumentImpl.CHOICE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setChoice(final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, ChoiceDocumentImpl.CHOICE$0, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(ChoiceDocumentImpl.CHOICE$0);
            return target;
        }
    }
    
    static {
        CHOICE$0 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
    }
}
