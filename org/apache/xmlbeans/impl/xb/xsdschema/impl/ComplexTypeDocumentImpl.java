package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexTypeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ComplexTypeDocumentImpl extends XmlComplexContentImpl implements ComplexTypeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName COMPLEXTYPE$0;
    
    public ComplexTypeDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public TopLevelComplexType getComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelComplexType target = null;
            target = (TopLevelComplexType)this.get_store().find_element_user(ComplexTypeDocumentImpl.COMPLEXTYPE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setComplexType(final TopLevelComplexType complexType) {
        this.generatedSetterHelperImpl(complexType, ComplexTypeDocumentImpl.COMPLEXTYPE$0, 0, (short)1);
    }
    
    @Override
    public TopLevelComplexType addNewComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelComplexType target = null;
            target = (TopLevelComplexType)this.get_store().add_element_user(ComplexTypeDocumentImpl.COMPLEXTYPE$0);
            return target;
        }
    }
    
    static {
        COMPLEXTYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
    }
}
