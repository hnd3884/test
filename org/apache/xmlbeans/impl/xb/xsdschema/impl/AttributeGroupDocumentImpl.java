package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AttributeGroupDocumentImpl extends XmlComplexContentImpl implements AttributeGroupDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ATTRIBUTEGROUP$0;
    
    public AttributeGroupDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NamedAttributeGroup getAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamedAttributeGroup target = null;
            target = (NamedAttributeGroup)this.get_store().find_element_user(AttributeGroupDocumentImpl.ATTRIBUTEGROUP$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAttributeGroup(final NamedAttributeGroup attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, AttributeGroupDocumentImpl.ATTRIBUTEGROUP$0, 0, (short)1);
    }
    
    @Override
    public NamedAttributeGroup addNewAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamedAttributeGroup target = null;
            target = (NamedAttributeGroup)this.get_store().add_element_user(AttributeGroupDocumentImpl.ATTRIBUTEGROUP$0);
            return target;
        }
    }
    
    static {
        ATTRIBUTEGROUP$0 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
    }
}
