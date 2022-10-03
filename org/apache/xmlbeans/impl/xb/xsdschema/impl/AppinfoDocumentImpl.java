package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AppinfoDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AppinfoDocumentImpl extends XmlComplexContentImpl implements AppinfoDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName APPINFO$0;
    
    public AppinfoDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Appinfo getAppinfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Appinfo target = null;
            target = (Appinfo)this.get_store().find_element_user(AppinfoDocumentImpl.APPINFO$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAppinfo(final Appinfo appinfo) {
        this.generatedSetterHelperImpl(appinfo, AppinfoDocumentImpl.APPINFO$0, 0, (short)1);
    }
    
    @Override
    public Appinfo addNewAppinfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Appinfo target = null;
            target = (Appinfo)this.get_store().add_element_user(AppinfoDocumentImpl.APPINFO$0);
            return target;
        }
    }
    
    static {
        APPINFO$0 = new QName("http://www.w3.org/2001/XMLSchema", "appinfo");
    }
    
    public static class AppinfoImpl extends XmlComplexContentImpl implements Appinfo
    {
        private static final long serialVersionUID = 1L;
        private static final QName SOURCE$0;
        
        public AppinfoImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AppinfoImpl.SOURCE$0);
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
                target = (XmlAnyURI)this.get_store().find_attribute_user(AppinfoImpl.SOURCE$0);
                return target;
            }
        }
        
        @Override
        public boolean isSetSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AppinfoImpl.SOURCE$0) != null;
            }
        }
        
        @Override
        public void setSource(final String source) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(AppinfoImpl.SOURCE$0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(AppinfoImpl.SOURCE$0);
                }
                target.setStringValue(source);
            }
        }
        
        @Override
        public void xsetSource(final XmlAnyURI source) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(AppinfoImpl.SOURCE$0);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(AppinfoImpl.SOURCE$0);
                }
                target.set(source);
            }
        }
        
        @Override
        public void unsetSource() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AppinfoImpl.SOURCE$0);
            }
        }
        
        static {
            SOURCE$0 = new QName("", "source");
        }
    }
}
