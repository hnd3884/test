package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class UsertypeconfigImpl extends XmlComplexContentImpl implements Usertypeconfig
{
    private static final long serialVersionUID = 1L;
    private static final QName STATICHANDLER$0;
    private static final QName NAME$2;
    private static final QName JAVANAME$4;
    
    public UsertypeconfigImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getStaticHandler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(UsertypeconfigImpl.STATICHANDLER$0, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetStaticHandler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(UsertypeconfigImpl.STATICHANDLER$0, 0);
            return target;
        }
    }
    
    @Override
    public void setStaticHandler(final String staticHandler) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(UsertypeconfigImpl.STATICHANDLER$0, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(UsertypeconfigImpl.STATICHANDLER$0);
            }
            target.setStringValue(staticHandler);
        }
    }
    
    @Override
    public void xsetStaticHandler(final XmlString staticHandler) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(UsertypeconfigImpl.STATICHANDLER$0, 0);
            if (target == null) {
                target = (XmlString)this.get_store().add_element_user(UsertypeconfigImpl.STATICHANDLER$0);
            }
            target.set(staticHandler);
        }
    }
    
    @Override
    public QName getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(UsertypeconfigImpl.NAME$2);
            if (target == null) {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    @Override
    public XmlQName xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(UsertypeconfigImpl.NAME$2);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(UsertypeconfigImpl.NAME$2) != null;
        }
    }
    
    @Override
    public void setName(final QName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(UsertypeconfigImpl.NAME$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(UsertypeconfigImpl.NAME$2);
            }
            target.setQNameValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlQName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(UsertypeconfigImpl.NAME$2);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(UsertypeconfigImpl.NAME$2);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(UsertypeconfigImpl.NAME$2);
        }
    }
    
    @Override
    public String getJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            return target;
        }
    }
    
    @Override
    public boolean isSetJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(UsertypeconfigImpl.JAVANAME$4) != null;
        }
    }
    
    @Override
    public void setJavaname(final String javaname) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            }
            target.setStringValue(javaname);
        }
    }
    
    @Override
    public void xsetJavaname(final XmlString javaname) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(UsertypeconfigImpl.JAVANAME$4);
            }
            target.set(javaname);
        }
    }
    
    @Override
    public void unsetJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(UsertypeconfigImpl.JAVANAME$4);
        }
    }
    
    static {
        STATICHANDLER$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "staticHandler");
        NAME$2 = new QName("", "name");
        JAVANAME$4 = new QName("", "javaname");
    }
}
