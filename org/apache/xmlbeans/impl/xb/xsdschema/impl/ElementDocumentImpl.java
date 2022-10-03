package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ElementDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ElementDocumentImpl extends XmlComplexContentImpl implements ElementDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ELEMENT$0;
    
    public ElementDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public TopLevelElement getElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelElement target = null;
            target = (TopLevelElement)this.get_store().find_element_user(ElementDocumentImpl.ELEMENT$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setElement(final TopLevelElement element) {
        this.generatedSetterHelperImpl(element, ElementDocumentImpl.ELEMENT$0, 0, (short)1);
    }
    
    @Override
    public TopLevelElement addNewElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelElement target = null;
            target = (TopLevelElement)this.get_store().add_element_user(ElementDocumentImpl.ELEMENT$0);
            return target;
        }
    }
    
    static {
        ELEMENT$0 = new QName("http://www.w3.org/2001/XMLSchema", "element");
    }
}
