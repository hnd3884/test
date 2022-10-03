package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class IncludeDocumentImpl extends XmlComplexContentImpl implements IncludeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName INCLUDE$0;
    
    public IncludeDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Include getInclude() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Include target = null;
            target = (Include)this.get_store().find_element_user(IncludeDocumentImpl.INCLUDE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setInclude(final Include include) {
        this.generatedSetterHelperImpl(include, IncludeDocumentImpl.INCLUDE$0, 0, (short)1);
    }
    
    @Override
    public Include addNewInclude() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Include target = null;
            target = (Include)this.get_store().add_element_user(IncludeDocumentImpl.INCLUDE$0);
            return target;
        }
    }
    
    static {
        INCLUDE$0 = new QName("http://www.w3.org/2001/XMLSchema", "include");
    }
    
    public static class IncludeImpl extends AnnotatedImpl implements Include
    {
        private static final long serialVersionUID = 1L;
        private static final QName SCHEMALOCATION$0;
        
        public IncludeImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(IncludeImpl.SCHEMALOCATION$0);
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
                target = (XmlAnyURI)this.get_store().find_attribute_user(IncludeImpl.SCHEMALOCATION$0);
                return target;
            }
        }
        
        @Override
        public void setSchemaLocation(final String schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(IncludeImpl.SCHEMALOCATION$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(IncludeImpl.SCHEMALOCATION$0);
                }
                target.setStringValue(schemaLocation);
            }
        }
        
        @Override
        public void xsetSchemaLocation(final XmlAnyURI schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(IncludeImpl.SCHEMALOCATION$0);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(IncludeImpl.SCHEMALOCATION$0);
                }
                target.set(schemaLocation);
            }
        }
        
        static {
            SCHEMALOCATION$0 = new QName("", "schemaLocation");
        }
    }
}
