package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleTypeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SimpleTypeDocumentImpl extends XmlComplexContentImpl implements SimpleTypeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLETYPE$0;
    
    public SimpleTypeDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public TopLevelSimpleType getSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelSimpleType target = null;
            target = (TopLevelSimpleType)this.get_store().find_element_user(SimpleTypeDocumentImpl.SIMPLETYPE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setSimpleType(final TopLevelSimpleType simpleType) {
        this.generatedSetterHelperImpl(simpleType, SimpleTypeDocumentImpl.SIMPLETYPE$0, 0, (short)1);
    }
    
    @Override
    public TopLevelSimpleType addNewSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TopLevelSimpleType target = null;
            target = (TopLevelSimpleType)this.get_store().add_element_user(SimpleTypeDocumentImpl.SIMPLETYPE$0);
            return target;
        }
    }
    
    static {
        SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
    }
}
