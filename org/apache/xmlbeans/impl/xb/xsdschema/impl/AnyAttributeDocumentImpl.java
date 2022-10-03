package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AnyAttributeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AnyAttributeDocumentImpl extends XmlComplexContentImpl implements AnyAttributeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ANYATTRIBUTE$0;
    
    public AnyAttributeDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Wildcard getAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().find_element_user(AnyAttributeDocumentImpl.ANYATTRIBUTE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAnyAttribute(final Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, AnyAttributeDocumentImpl.ANYATTRIBUTE$0, 0, (short)1);
    }
    
    @Override
    public Wildcard addNewAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().add_element_user(AnyAttributeDocumentImpl.ANYATTRIBUTE$0);
            return target;
        }
    }
    
    static {
        ANYATTRIBUTE$0 = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
    }
}
