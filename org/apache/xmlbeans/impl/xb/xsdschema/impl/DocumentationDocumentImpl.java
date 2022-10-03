package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DocumentationDocumentImpl extends XmlComplexContentImpl implements DocumentationDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName DOCUMENTATION$0;
    
    public DocumentationDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Documentation getDocumentation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Documentation target = null;
            target = (Documentation)this.get_store().find_element_user(DocumentationDocumentImpl.DOCUMENTATION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setDocumentation(final Documentation documentation) {
        this.generatedSetterHelperImpl(documentation, DocumentationDocumentImpl.DOCUMENTATION$0, 0, (short)1);
    }
    
    @Override
    public Documentation addNewDocumentation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Documentation target = null;
            target = (Documentation)this.get_store().add_element_user(DocumentationDocumentImpl.DOCUMENTATION$0);
            return target;
        }
    }
    
    static {
        DOCUMENTATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "documentation");
    }
    
    public static class DocumentationImpl extends XmlComplexContentImpl implements Documentation
    {
        private static final long serialVersionUID = 1L;
        private static final QName SOURCE$0;
        private static final QName LANG$2;
        
        public DocumentationImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DocumentationImpl.SOURCE$0);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(DocumentationImpl.SOURCE$0);
                return target;
            }
        }
        
        @Override
        public boolean isSetSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(DocumentationImpl.SOURCE$0) != null;
            }
        }
        
        @Override
        public void setSource(final String source) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DocumentationImpl.SOURCE$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(DocumentationImpl.SOURCE$0);
                }
                target.setStringValue(source);
            }
        }
        
        @Override
        public void xsetSource(final XmlAnyURI source) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(DocumentationImpl.SOURCE$0);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(DocumentationImpl.SOURCE$0);
                }
                target.set(source);
            }
        }
        
        @Override
        public void unsetSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(DocumentationImpl.SOURCE$0);
            }
        }
        
        @Override
        public String getLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DocumentationImpl.LANG$2);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlLanguage xgetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlLanguage target = null;
                target = (XmlLanguage)this.get_store().find_attribute_user(DocumentationImpl.LANG$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(DocumentationImpl.LANG$2) != null;
            }
        }
        
        @Override
        public void setLang(final String lang) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DocumentationImpl.LANG$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(DocumentationImpl.LANG$2);
                }
                target.setStringValue(lang);
            }
        }
        
        @Override
        public void xsetLang(final XmlLanguage lang) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlLanguage target = null;
                target = (XmlLanguage)this.get_store().find_attribute_user(DocumentationImpl.LANG$2);
                if (target == null) {
                    target = (XmlLanguage)this.get_store().add_attribute_user(DocumentationImpl.LANG$2);
                }
                target.set(lang);
            }
        }
        
        @Override
        public void unsetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(DocumentationImpl.LANG$2);
            }
        }
        
        static {
            SOURCE$0 = new QName("", "source");
            LANG$2 = new QName("http://www.w3.org/XML/1998/namespace", "lang");
        }
    }
}
