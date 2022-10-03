package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.SimpleExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleRestrictionType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SimpleContentDocumentImpl extends XmlComplexContentImpl implements SimpleContentDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLECONTENT$0;
    
    public SimpleContentDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public SimpleContent getSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleContent target = null;
            target = (SimpleContent)this.get_store().find_element_user(SimpleContentDocumentImpl.SIMPLECONTENT$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setSimpleContent(final SimpleContent simpleContent) {
        this.generatedSetterHelperImpl(simpleContent, SimpleContentDocumentImpl.SIMPLECONTENT$0, 0, (short)1);
    }
    
    @Override
    public SimpleContent addNewSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleContent target = null;
            target = (SimpleContent)this.get_store().add_element_user(SimpleContentDocumentImpl.SIMPLECONTENT$0);
            return target;
        }
    }
    
    static {
        SIMPLECONTENT$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleContent");
    }
    
    public static class SimpleContentImpl extends AnnotatedImpl implements SimpleContent
    {
        private static final long serialVersionUID = 1L;
        private static final QName RESTRICTION$0;
        private static final QName EXTENSION$2;
        
        public SimpleContentImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public SimpleRestrictionType getRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleRestrictionType target = null;
                target = (SimpleRestrictionType)this.get_store().find_element_user(SimpleContentImpl.RESTRICTION$0, 0);
                if (target == null) {
                    return null;
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SimpleContentImpl.RESTRICTION$0) != 0;
            }
        }
        
        @Override
        public void setRestriction(final SimpleRestrictionType restriction) {
            this.generatedSetterHelperImpl(restriction, SimpleContentImpl.RESTRICTION$0, 0, (short)1);
        }
        
        @Override
        public SimpleRestrictionType addNewRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleRestrictionType target = null;
                target = (SimpleRestrictionType)this.get_store().add_element_user(SimpleContentImpl.RESTRICTION$0);
                return target;
            }
        }
        
        @Override
        public void unsetRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SimpleContentImpl.RESTRICTION$0, 0);
            }
        }
        
        @Override
        public SimpleExtensionType getExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleExtensionType target = null;
                target = (SimpleExtensionType)this.get_store().find_element_user(SimpleContentImpl.EXTENSION$2, 0);
                if (target == null) {
                    return null;
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SimpleContentImpl.EXTENSION$2) != 0;
            }
        }
        
        @Override
        public void setExtension(final SimpleExtensionType extension) {
            this.generatedSetterHelperImpl(extension, SimpleContentImpl.EXTENSION$2, 0, (short)1);
        }
        
        @Override
        public SimpleExtensionType addNewExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleExtensionType target = null;
                target = (SimpleExtensionType)this.get_store().add_element_user(SimpleContentImpl.EXTENSION$2);
                return target;
            }
        }
        
        @Override
        public void unsetExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SimpleContentImpl.EXTENSION$2, 0);
            }
        }
        
        static {
            RESTRICTION$0 = new QName("http://www.w3.org/2001/XMLSchema", "restriction");
            EXTENSION$2 = new QName("http://www.w3.org/2001/XMLSchema", "extension");
        }
    }
}
