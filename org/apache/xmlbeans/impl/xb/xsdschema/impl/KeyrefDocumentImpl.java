package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class KeyrefDocumentImpl extends XmlComplexContentImpl implements KeyrefDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName KEYREF$0;
    
    public KeyrefDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Keyref getKeyref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keyref target = null;
            target = (Keyref)this.get_store().find_element_user(KeyrefDocumentImpl.KEYREF$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setKeyref(final Keyref keyref) {
        this.generatedSetterHelperImpl(keyref, KeyrefDocumentImpl.KEYREF$0, 0, (short)1);
    }
    
    @Override
    public Keyref addNewKeyref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keyref target = null;
            target = (Keyref)this.get_store().add_element_user(KeyrefDocumentImpl.KEYREF$0);
            return target;
        }
    }
    
    static {
        KEYREF$0 = new QName("http://www.w3.org/2001/XMLSchema", "keyref");
    }
    
    public static class KeyrefImpl extends KeybaseImpl implements Keyref
    {
        private static final long serialVersionUID = 1L;
        private static final QName REFER$0;
        
        public KeyrefImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public QName getRefer() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(KeyrefImpl.REFER$0);
                if (target == null) {
                    return null;
                }
                return target.getQNameValue();
            }
        }
        
        @Override
        public XmlQName xgetRefer() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(KeyrefImpl.REFER$0);
                return target;
            }
        }
        
        @Override
        public void setRefer(final QName refer) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(KeyrefImpl.REFER$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(KeyrefImpl.REFER$0);
                }
                target.setQNameValue(refer);
            }
        }
        
        @Override
        public void xsetRefer(final XmlQName refer) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(KeyrefImpl.REFER$0);
                if (target == null) {
                    target = (XmlQName)this.get_store().add_attribute_user(KeyrefImpl.REFER$0);
                }
                target.set(refer);
            }
        }
        
        static {
            REFER$0 = new QName("", "refer");
        }
    }
}
