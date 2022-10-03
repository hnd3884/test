package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.values.JavaStringHolderEx;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument.Field;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class FieldDocumentImpl extends XmlComplexContentImpl implements FieldDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName FIELD$0;
    
    public FieldDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Field getField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Field target = null;
            target = (Field)this.get_store().find_element_user(FieldDocumentImpl.FIELD$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setField(final Field field) {
        this.generatedSetterHelperImpl(field, FieldDocumentImpl.FIELD$0, 0, (short)1);
    }
    
    @Override
    public Field addNewField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Field target = null;
            target = (Field)this.get_store().add_element_user(FieldDocumentImpl.FIELD$0);
            return target;
        }
    }
    
    static {
        FIELD$0 = new QName("http://www.w3.org/2001/XMLSchema", "field");
    }
    
    public static class FieldImpl extends AnnotatedImpl implements Field
    {
        private static final long serialVersionUID = 1L;
        private static final QName XPATH$0;
        
        public FieldImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getXpath() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(FieldImpl.XPATH$0);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public Xpath xgetXpath() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Xpath target = null;
                target = (Xpath)this.get_store().find_attribute_user(FieldImpl.XPATH$0);
                return target;
            }
        }
        
        @Override
        public void setXpath(final String xpath) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(FieldImpl.XPATH$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(FieldImpl.XPATH$0);
                }
                target.setStringValue(xpath);
            }
        }
        
        @Override
        public void xsetXpath(final Xpath xpath) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Xpath target = null;
                target = (Xpath)this.get_store().find_attribute_user(FieldImpl.XPATH$0);
                if (target == null) {
                    target = (Xpath)this.get_store().add_attribute_user(FieldImpl.XPATH$0);
                }
                target.set(xpath);
            }
        }
        
        static {
            XPATH$0 = new QName("", "xpath");
        }
        
        public static class XpathImpl extends JavaStringHolderEx implements Xpath
        {
            private static final long serialVersionUID = 1L;
            
            public XpathImpl(final SchemaType sType) {
                super(sType, false);
            }
            
            protected XpathImpl(final SchemaType sType, final boolean b) {
                super(sType, b);
            }
        }
    }
}
