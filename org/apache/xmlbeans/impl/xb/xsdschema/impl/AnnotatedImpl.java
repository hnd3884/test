package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;

public class AnnotatedImpl extends OpenAttrsImpl implements Annotated
{
    private static final long serialVersionUID = 1L;
    private static final QName ANNOTATION$0;
    private static final QName ID$2;
    
    public AnnotatedImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public AnnotationDocument.Annotation getAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AnnotationDocument.Annotation target = null;
            target = (AnnotationDocument.Annotation)this.get_store().find_element_user(AnnotatedImpl.ANNOTATION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(AnnotatedImpl.ANNOTATION$0) != 0;
        }
    }
    
    @Override
    public void setAnnotation(final AnnotationDocument.Annotation annotation) {
        this.generatedSetterHelperImpl(annotation, AnnotatedImpl.ANNOTATION$0, 0, (short)1);
    }
    
    @Override
    public AnnotationDocument.Annotation addNewAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AnnotationDocument.Annotation target = null;
            target = (AnnotationDocument.Annotation)this.get_store().add_element_user(AnnotatedImpl.ANNOTATION$0);
            return target;
        }
    }
    
    @Override
    public void unsetAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(AnnotatedImpl.ANNOTATION$0, 0);
        }
    }
    
    @Override
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AnnotatedImpl.ID$2);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)this.get_store().find_attribute_user(AnnotatedImpl.ID$2);
            return target;
        }
    }
    
    @Override
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AnnotatedImpl.ID$2) != null;
        }
    }
    
    @Override
    public void setId(final String id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AnnotatedImpl.ID$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AnnotatedImpl.ID$2);
            }
            target.setStringValue(id);
        }
    }
    
    @Override
    public void xsetId(final XmlID id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)this.get_store().find_attribute_user(AnnotatedImpl.ID$2);
            if (target == null) {
                target = (XmlID)this.get_store().add_attribute_user(AnnotatedImpl.ID$2);
            }
            target.set(id);
        }
    }
    
    @Override
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AnnotatedImpl.ID$2);
        }
    }
    
    static {
        ANNOTATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
        ID$2 = new QName("", "id");
    }
}
