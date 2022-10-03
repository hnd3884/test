package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ListDocumentImpl extends XmlComplexContentImpl implements ListDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName LIST$0;
    
    public ListDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public List getList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            List target = null;
            target = (List)this.get_store().find_element_user(ListDocumentImpl.LIST$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setList(final List list) {
        this.generatedSetterHelperImpl(list, ListDocumentImpl.LIST$0, 0, (short)1);
    }
    
    @Override
    public List addNewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            List target = null;
            target = (List)this.get_store().add_element_user(ListDocumentImpl.LIST$0);
            return target;
        }
    }
    
    static {
        LIST$0 = new QName("http://www.w3.org/2001/XMLSchema", "list");
    }
    
    public static class ListImpl extends AnnotatedImpl implements List
    {
        private static final long serialVersionUID = 1L;
        private static final QName SIMPLETYPE$0;
        private static final QName ITEMTYPE$2;
        
        public ListImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public LocalSimpleType getSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().find_element_user(ListImpl.SIMPLETYPE$0, 0);
                if (target == null) {
                    return null;
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(ListImpl.SIMPLETYPE$0) != 0;
            }
        }
        
        @Override
        public void setSimpleType(final LocalSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, ListImpl.SIMPLETYPE$0, 0, (short)1);
        }
        
        @Override
        public LocalSimpleType addNewSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().add_element_user(ListImpl.SIMPLETYPE$0);
                return target;
            }
        }
        
        @Override
        public void unsetSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ListImpl.SIMPLETYPE$0, 0);
            }
        }
        
        @Override
        public QName getItemType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ListImpl.ITEMTYPE$2);
                if (target == null) {
                    return null;
                }
                return target.getQNameValue();
            }
        }
        
        @Override
        public XmlQName xgetItemType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(ListImpl.ITEMTYPE$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetItemType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(ListImpl.ITEMTYPE$2) != null;
            }
        }
        
        @Override
        public void setItemType(final QName itemType) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(ListImpl.ITEMTYPE$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(ListImpl.ITEMTYPE$2);
                }
                target.setQNameValue(itemType);
            }
        }
        
        @Override
        public void xsetItemType(final XmlQName itemType) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(ListImpl.ITEMTYPE$2);
                if (target == null) {
                    target = (XmlQName)this.get_store().add_attribute_user(ListImpl.ITEMTYPE$2);
                }
                target.set(itemType);
            }
        }
        
        @Override
        public void unsetItemType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(ListImpl.ITEMTYPE$2);
            }
        }
        
        static {
            SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
            ITEMTYPE$2 = new QName("", "itemType");
        }
    }
}
