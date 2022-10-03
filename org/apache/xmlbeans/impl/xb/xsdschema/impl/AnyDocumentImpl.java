package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AnyDocumentImpl extends XmlComplexContentImpl implements AnyDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ANY$0;
    
    public AnyDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Any getAny() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Any target = null;
            target = (Any)this.get_store().find_element_user(AnyDocumentImpl.ANY$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAny(final Any any) {
        this.generatedSetterHelperImpl(any, AnyDocumentImpl.ANY$0, 0, (short)1);
    }
    
    @Override
    public Any addNewAny() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Any target = null;
            target = (Any)this.get_store().add_element_user(AnyDocumentImpl.ANY$0);
            return target;
        }
    }
    
    static {
        ANY$0 = new QName("http://www.w3.org/2001/XMLSchema", "any");
    }
    
    public static class AnyImpl extends WildcardImpl implements Any
    {
        private static final long serialVersionUID = 1L;
        private static final QName MINOCCURS$0;
        private static final QName MAXOCCURS$2;
        
        public AnyImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public BigInteger getMinOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnyImpl.MINOCCURS$0);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(AnyImpl.MINOCCURS$0);
                }
                if (target == null) {
                    return null;
                }
                return target.getBigIntegerValue();
            }
        }
        
        @Override
        public XmlNonNegativeInteger xgetMinOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlNonNegativeInteger target = null;
                target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(AnyImpl.MINOCCURS$0);
                if (target == null) {
                    target = (XmlNonNegativeInteger)this.get_default_attribute_value(AnyImpl.MINOCCURS$0);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetMinOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AnyImpl.MINOCCURS$0) != null;
            }
        }
        
        @Override
        public void setMinOccurs(final BigInteger minOccurs) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnyImpl.MINOCCURS$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(AnyImpl.MINOCCURS$0);
                }
                target.setBigIntegerValue(minOccurs);
            }
        }
        
        @Override
        public void xsetMinOccurs(final XmlNonNegativeInteger minOccurs) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlNonNegativeInteger target = null;
                target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(AnyImpl.MINOCCURS$0);
                if (target == null) {
                    target = (XmlNonNegativeInteger)this.get_store().add_attribute_user(AnyImpl.MINOCCURS$0);
                }
                target.set(minOccurs);
            }
        }
        
        @Override
        public void unsetMinOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AnyImpl.MINOCCURS$0);
            }
        }
        
        @Override
        public Object getMaxOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnyImpl.MAXOCCURS$2);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(AnyImpl.MAXOCCURS$2);
                }
                if (target == null) {
                    return null;
                }
                return target.getObjectValue();
            }
        }
        
        @Override
        public AllNNI xgetMaxOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AllNNI target = null;
                target = (AllNNI)this.get_store().find_attribute_user(AnyImpl.MAXOCCURS$2);
                if (target == null) {
                    target = (AllNNI)this.get_default_attribute_value(AnyImpl.MAXOCCURS$2);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetMaxOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AnyImpl.MAXOCCURS$2) != null;
            }
        }
        
        @Override
        public void setMaxOccurs(final Object maxOccurs) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AnyImpl.MAXOCCURS$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(AnyImpl.MAXOCCURS$2);
                }
                target.setObjectValue(maxOccurs);
            }
        }
        
        @Override
        public void xsetMaxOccurs(final AllNNI maxOccurs) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AllNNI target = null;
                target = (AllNNI)this.get_store().find_attribute_user(AnyImpl.MAXOCCURS$2);
                if (target == null) {
                    target = (AllNNI)this.get_store().add_attribute_user(AnyImpl.MAXOCCURS$2);
                }
                target.set(maxOccurs);
            }
        }
        
        @Override
        public void unsetMaxOccurs() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AnyImpl.MAXOCCURS$2);
            }
        }
        
        static {
            MINOCCURS$0 = new QName("", "minOccurs");
            MAXOCCURS$2 = new QName("", "maxOccurs");
        }
    }
}
