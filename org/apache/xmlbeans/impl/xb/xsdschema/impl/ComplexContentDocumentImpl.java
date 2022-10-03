package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ComplexContentDocumentImpl extends XmlComplexContentImpl implements ComplexContentDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName COMPLEXCONTENT$0;
    
    public ComplexContentDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public ComplexContent getComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ComplexContent target = null;
            target = (ComplexContent)this.get_store().find_element_user(ComplexContentDocumentImpl.COMPLEXCONTENT$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setComplexContent(final ComplexContent complexContent) {
        this.generatedSetterHelperImpl(complexContent, ComplexContentDocumentImpl.COMPLEXCONTENT$0, 0, (short)1);
    }
    
    @Override
    public ComplexContent addNewComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ComplexContent target = null;
            target = (ComplexContent)this.get_store().add_element_user(ComplexContentDocumentImpl.COMPLEXCONTENT$0);
            return target;
        }
    }
    
    static {
        COMPLEXCONTENT$0 = new QName("http://www.w3.org/2001/XMLSchema", "complexContent");
    }
    
    public static class ComplexContentImpl extends AnnotatedImpl implements ComplexContent
    {
        private static final long serialVersionUID = 1L;
        private static final QName RESTRICTION$0;
        private static final QName EXTENSION$2;
        private static final QName MIXED$4;
        
        public ComplexContentImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public ComplexRestrictionType getRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ComplexRestrictionType target = null;
                target = (ComplexRestrictionType)this.get_store().find_element_user(ComplexContentImpl.RESTRICTION$0, 0);
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
                return this.get_store().count_elements(ComplexContentImpl.RESTRICTION$0) != 0;
            }
        }
        
        @Override
        public void setRestriction(final ComplexRestrictionType restriction) {
            this.generatedSetterHelperImpl(restriction, ComplexContentImpl.RESTRICTION$0, 0, (short)1);
        }
        
        @Override
        public ComplexRestrictionType addNewRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ComplexRestrictionType target = null;
                target = (ComplexRestrictionType)this.get_store().add_element_user(ComplexContentImpl.RESTRICTION$0);
                return target;
            }
        }
        
        @Override
        public void unsetRestriction() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ComplexContentImpl.RESTRICTION$0, 0);
            }
        }
        
        @Override
        public ExtensionType getExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ExtensionType target = null;
                target = (ExtensionType)this.get_store().find_element_user(ComplexContentImpl.EXTENSION$2, 0);
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
                return this.get_store().count_elements(ComplexContentImpl.EXTENSION$2) != 0;
            }
        }
        
        @Override
        public void setExtension(final ExtensionType extension) {
            this.generatedSetterHelperImpl(extension, ComplexContentImpl.EXTENSION$2, 0, (short)1);
        }
        
        @Override
        public ExtensionType addNewExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ExtensionType target = null;
                target = (ExtensionType)this.get_store().add_element_user(ComplexContentImpl.EXTENSION$2);
                return target;
            }
        }
        
        @Override
        public void unsetExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ComplexContentImpl.EXTENSION$2, 0);
            }
        }
        
        @Override
        public boolean getMixed() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ComplexContentImpl.MIXED$4);
                return target != null && target.getBooleanValue();
            }
        }
        
        @Override
        public XmlBoolean xgetMixed() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlBoolean target = null;
                target = (XmlBoolean)this.get_store().find_attribute_user(ComplexContentImpl.MIXED$4);
                return target;
            }
        }
        
        @Override
        public boolean isSetMixed() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(ComplexContentImpl.MIXED$4) != null;
            }
        }
        
        @Override
        public void setMixed(final boolean mixed) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ComplexContentImpl.MIXED$4);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(ComplexContentImpl.MIXED$4);
                }
                target.setBooleanValue(mixed);
            }
        }
        
        @Override
        public void xsetMixed(final XmlBoolean mixed) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlBoolean target = null;
                target = (XmlBoolean)this.get_store().find_attribute_user(ComplexContentImpl.MIXED$4);
                if (target == null) {
                    target = (XmlBoolean)this.get_store().add_attribute_user(ComplexContentImpl.MIXED$4);
                }
                target.set(mixed);
            }
        }
        
        @Override
        public void unsetMixed() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(ComplexContentImpl.MIXED$4);
            }
        }
        
        static {
            RESTRICTION$0 = new QName("http://www.w3.org/2001/XMLSchema", "restriction");
            EXTENSION$2 = new QName("http://www.w3.org/2001/XMLSchema", "extension");
            MIXED$4 = new QName("", "mixed");
        }
    }
}
