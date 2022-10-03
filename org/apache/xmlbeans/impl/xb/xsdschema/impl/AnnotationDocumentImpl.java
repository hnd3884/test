package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.AppinfoDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AnnotationDocumentImpl extends XmlComplexContentImpl implements AnnotationDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ANNOTATION$0;
    
    public AnnotationDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Annotation getAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Annotation target = null;
            target = (Annotation)this.get_store().find_element_user(AnnotationDocumentImpl.ANNOTATION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAnnotation(final Annotation annotation) {
        this.generatedSetterHelperImpl(annotation, AnnotationDocumentImpl.ANNOTATION$0, 0, (short)1);
    }
    
    @Override
    public Annotation addNewAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Annotation target = null;
            target = (Annotation)this.get_store().add_element_user(AnnotationDocumentImpl.ANNOTATION$0);
            return target;
        }
    }
    
    static {
        ANNOTATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
    }
    
    public static class AnnotationImpl extends OpenAttrsImpl implements Annotation
    {
        private static final long serialVersionUID = 1L;
        private static final QName APPINFO$0;
        private static final QName DOCUMENTATION$2;
        private static final QName ID$4;
        
        public AnnotationImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public AppinfoDocument.Appinfo[] getAppinfoArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(AnnotationImpl.APPINFO$0, targetList);
                final AppinfoDocument.Appinfo[] result = new AppinfoDocument.Appinfo[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public AppinfoDocument.Appinfo getAppinfoArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)this.get_store().find_element_user(AnnotationImpl.APPINFO$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfAppinfoArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(AnnotationImpl.APPINFO$0);
            }
        }
        
        @Override
        public void setAppinfoArray(final AppinfoDocument.Appinfo[] appinfoArray) {
            this.check_orphaned();
            this.arraySetterHelper(appinfoArray, AnnotationImpl.APPINFO$0);
        }
        
        @Override
        public void setAppinfoArray(final int i, final AppinfoDocument.Appinfo appinfo) {
            this.generatedSetterHelperImpl(appinfo, AnnotationImpl.APPINFO$0, i, (short)2);
        }
        
        @Override
        public AppinfoDocument.Appinfo insertNewAppinfo(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)this.get_store().insert_element_user(AnnotationImpl.APPINFO$0, i);
                return target;
            }
        }
        
        @Override
        public AppinfoDocument.Appinfo addNewAppinfo() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)this.get_store().add_element_user(AnnotationImpl.APPINFO$0);
                return target;
            }
        }
        
        @Override
        public void removeAppinfo(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(AnnotationImpl.APPINFO$0, i);
            }
        }
        
        @Override
        public DocumentationDocument.Documentation[] getDocumentationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(AnnotationImpl.DOCUMENTATION$2, targetList);
                final DocumentationDocument.Documentation[] result = new DocumentationDocument.Documentation[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public DocumentationDocument.Documentation getDocumentationArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)this.get_store().find_element_user(AnnotationImpl.DOCUMENTATION$2, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfDocumentationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(AnnotationImpl.DOCUMENTATION$2);
            }
        }
        
        @Override
        public void setDocumentationArray(final DocumentationDocument.Documentation[] documentationArray) {
            this.check_orphaned();
            this.arraySetterHelper(documentationArray, AnnotationImpl.DOCUMENTATION$2);
        }
        
        @Override
        public void setDocumentationArray(final int i, final DocumentationDocument.Documentation documentation) {
            this.generatedSetterHelperImpl(documentation, AnnotationImpl.DOCUMENTATION$2, i, (short)2);
        }
        
        @Override
        public DocumentationDocument.Documentation insertNewDocumentation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)this.get_store().insert_element_user(AnnotationImpl.DOCUMENTATION$2, i);
                return target;
            }
        }
        
        @Override
        public DocumentationDocument.Documentation addNewDocumentation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)this.get_store().add_element_user(AnnotationImpl.DOCUMENTATION$2);
                return target;
            }
        }
        
        @Override
        public void removeDocumentation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(AnnotationImpl.DOCUMENTATION$2, i);
            }
        }
        
        @Override
        public String getId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnnotationImpl.ID$4);
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
                target = (XmlID)this.get_store().find_attribute_user(AnnotationImpl.ID$4);
                return target;
            }
        }
        
        @Override
        public boolean isSetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AnnotationImpl.ID$4) != null;
            }
        }
        
        @Override
        public void setId(final String id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnnotationImpl.ID$4);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(AnnotationImpl.ID$4);
                }
                target.setStringValue(id);
            }
        }
        
        @Override
        public void xsetId(final XmlID id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)this.get_store().find_attribute_user(AnnotationImpl.ID$4);
                if (target == null) {
                    target = (XmlID)this.get_store().add_attribute_user(AnnotationImpl.ID$4);
                }
                target.set(id);
            }
        }
        
        @Override
        public void unsetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AnnotationImpl.ID$4);
            }
        }
        
        static {
            APPINFO$0 = new QName("http://www.w3.org/2001/XMLSchema", "appinfo");
            DOCUMENTATION$2 = new QName("http://www.w3.org/2001/XMLSchema", "documentation");
            ID$4 = new QName("", "id");
        }
    }
}
