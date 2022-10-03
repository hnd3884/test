package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ImportDocumentImpl extends XmlComplexContentImpl implements ImportDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName IMPORT$0;
    
    public ImportDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Import getImport() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Import target = null;
            target = (Import)this.get_store().find_element_user(ImportDocumentImpl.IMPORT$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setImport(final Import ximport) {
        this.generatedSetterHelperImpl(ximport, ImportDocumentImpl.IMPORT$0, 0, (short)1);
    }
    
    @Override
    public Import addNewImport() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Import target = null;
            target = (Import)this.get_store().add_element_user(ImportDocumentImpl.IMPORT$0);
            return target;
        }
    }
    
    static {
        IMPORT$0 = new QName("http://www.w3.org/2001/XMLSchema", "import");
    }
    
    public static class ImportImpl extends AnnotatedImpl implements Import
    {
        private static final long serialVersionUID = 1L;
        private static final QName NAMESPACE$0;
        private static final QName SCHEMALOCATION$2;
        
        public ImportImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ImportImpl.NAMESPACE$0);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(ImportImpl.NAMESPACE$0);
                return target;
            }
        }
        
        @Override
        public boolean isSetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(ImportImpl.NAMESPACE$0) != null;
            }
        }
        
        @Override
        public void setNamespace(final String namespace) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ImportImpl.NAMESPACE$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(ImportImpl.NAMESPACE$0);
                }
                target.setStringValue(namespace);
            }
        }
        
        @Override
        public void xsetNamespace(final XmlAnyURI namespace) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(ImportImpl.NAMESPACE$0);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(ImportImpl.NAMESPACE$0);
                }
                target.set(namespace);
            }
        }
        
        @Override
        public void unsetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(ImportImpl.NAMESPACE$0);
            }
        }
        
        @Override
        public String getSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ImportImpl.SCHEMALOCATION$2);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(ImportImpl.SCHEMALOCATION$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(ImportImpl.SCHEMALOCATION$2) != null;
            }
        }
        
        @Override
        public void setSchemaLocation(final String schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ImportImpl.SCHEMALOCATION$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(ImportImpl.SCHEMALOCATION$2);
                }
                target.setStringValue(schemaLocation);
            }
        }
        
        @Override
        public void xsetSchemaLocation(final XmlAnyURI schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(ImportImpl.SCHEMALOCATION$2);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(ImportImpl.SCHEMALOCATION$2);
                }
                target.set(schemaLocation);
            }
        }
        
        @Override
        public void unsetSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(ImportImpl.SCHEMALOCATION$2);
            }
        }
        
        static {
            NAMESPACE$0 = new QName("", "namespace");
            SCHEMALOCATION$2 = new QName("", "schemaLocation");
        }
    }
}
