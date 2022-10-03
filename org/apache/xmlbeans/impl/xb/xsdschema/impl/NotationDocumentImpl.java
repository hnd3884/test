package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.NotationDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class NotationDocumentImpl extends XmlComplexContentImpl implements NotationDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName NOTATION$0;
    
    public NotationDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Notation getNotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Notation target = null;
            target = (Notation)this.get_store().find_element_user(NotationDocumentImpl.NOTATION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setNotation(final Notation notation) {
        this.generatedSetterHelperImpl(notation, NotationDocumentImpl.NOTATION$0, 0, (short)1);
    }
    
    @Override
    public Notation addNewNotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Notation target = null;
            target = (Notation)this.get_store().add_element_user(NotationDocumentImpl.NOTATION$0);
            return target;
        }
    }
    
    static {
        NOTATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "notation");
    }
    
    public static class NotationImpl extends AnnotatedImpl implements Notation
    {
        private static final long serialVersionUID = 1L;
        private static final QName NAME$0;
        private static final QName PUBLIC$2;
        private static final QName SYSTEM$4;
        
        public NotationImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.NAME$0);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlNCName xgetName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlNCName target = null;
                target = (XmlNCName)this.get_store().find_attribute_user(NotationImpl.NAME$0);
                return target;
            }
        }
        
        @Override
        public void setName(final String name) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.NAME$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(NotationImpl.NAME$0);
                }
                target.setStringValue(name);
            }
        }
        
        @Override
        public void xsetName(final XmlNCName name) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlNCName target = null;
                target = (XmlNCName)this.get_store().find_attribute_user(NotationImpl.NAME$0);
                if (target == null) {
                    target = (XmlNCName)this.get_store().add_attribute_user(NotationImpl.NAME$0);
                }
                target.set(name);
            }
        }
        
        @Override
        public String getPublic() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.PUBLIC$2);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public Public xgetPublic() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Public target = null;
                target = (Public)this.get_store().find_attribute_user(NotationImpl.PUBLIC$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetPublic() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(NotationImpl.PUBLIC$2) != null;
            }
        }
        
        @Override
        public void setPublic(final String xpublic) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.PUBLIC$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(NotationImpl.PUBLIC$2);
                }
                target.setStringValue(xpublic);
            }
        }
        
        @Override
        public void xsetPublic(final Public xpublic) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Public target = null;
                target = (Public)this.get_store().find_attribute_user(NotationImpl.PUBLIC$2);
                if (target == null) {
                    target = (Public)this.get_store().add_attribute_user(NotationImpl.PUBLIC$2);
                }
                target.set(xpublic);
            }
        }
        
        @Override
        public void unsetPublic() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(NotationImpl.PUBLIC$2);
            }
        }
        
        @Override
        public String getSystem() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.SYSTEM$4);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetSystem() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(NotationImpl.SYSTEM$4);
                return target;
            }
        }
        
        @Override
        public boolean isSetSystem() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(NotationImpl.SYSTEM$4) != null;
            }
        }
        
        @Override
        public void setSystem(final String system) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(NotationImpl.SYSTEM$4);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(NotationImpl.SYSTEM$4);
                }
                target.setStringValue(system);
            }
        }
        
        @Override
        public void xsetSystem(final XmlAnyURI system) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(NotationImpl.SYSTEM$4);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(NotationImpl.SYSTEM$4);
                }
                target.set(system);
            }
        }
        
        @Override
        public void unsetSystem() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(NotationImpl.SYSTEM$4);
            }
        }
        
        static {
            NAME$0 = new QName("", "name");
            PUBLIC$2 = new QName("", "public");
            SYSTEM$4 = new QName("", "system");
        }
    }
}
