package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.TransformType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.TransformDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TransformDocumentImpl extends XmlComplexContentImpl implements TransformDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName TRANSFORM$0;
    
    public TransformDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public TransformType getTransform() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TransformType transformType = (TransformType)this.get_store().find_element_user(TransformDocumentImpl.TRANSFORM$0, 0);
            if (transformType == null) {
                return null;
            }
            return transformType;
        }
    }
    
    public void setTransform(final TransformType transformType) {
        this.generatedSetterHelperImpl((XmlObject)transformType, TransformDocumentImpl.TRANSFORM$0, 0, (short)1);
    }
    
    public TransformType addNewTransform() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TransformType)this.get_store().add_element_user(TransformDocumentImpl.TRANSFORM$0);
        }
    }
    
    static {
        TRANSFORM$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "Transform");
    }
}
