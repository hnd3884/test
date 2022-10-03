package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AttributeDocumentImpl extends XmlComplexContentImpl implements AttributeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ATTRIBUTE$0;
    
    public AttributeDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public TopLevelAttribute getAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelAttribute target = null;
            target = (TopLevelAttribute)this.get_store().find_element_user(AttributeDocumentImpl.ATTRIBUTE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAttribute(final TopLevelAttribute attribute) {
        this.generatedSetterHelperImpl(attribute, AttributeDocumentImpl.ATTRIBUTE$0, 0, (short)1);
    }
    
    @Override
    public TopLevelAttribute addNewAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelAttribute target = null;
            target = (TopLevelAttribute)this.get_store().add_element_user(AttributeDocumentImpl.ATTRIBUTE$0);
            return target;
        }
    }
    
    static {
        ATTRIBUTE$0 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
    }
}
