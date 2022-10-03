package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetlist;
import java.util.List;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class QnameconfigImpl extends XmlComplexContentImpl implements Qnameconfig
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName JAVANAME$2;
    private static final QName TARGET$4;
    
    public QnameconfigImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public QName getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.NAME$0);
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
            target = (XmlQName)this.get_store().find_attribute_user(QnameconfigImpl.NAME$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(QnameconfigImpl.NAME$0) != null;
        }
    }
    
    @Override
    public void setName(final QName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.NAME$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(QnameconfigImpl.NAME$0);
            }
            target.setQNameValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlQName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(QnameconfigImpl.NAME$0);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(QnameconfigImpl.NAME$0);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(QnameconfigImpl.NAME$0);
        }
    }
    
    @Override
    public String getJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.JAVANAME$2);
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
            target = (XmlString)this.get_store().find_attribute_user(QnameconfigImpl.JAVANAME$2);
            return target;
        }
    }
    
    @Override
    public boolean isSetJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(QnameconfigImpl.JAVANAME$2) != null;
        }
    }
    
    @Override
    public void setJavaname(final String javaname) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.JAVANAME$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(QnameconfigImpl.JAVANAME$2);
            }
            target.setStringValue(javaname);
        }
    }
    
    @Override
    public void xsetJavaname(final XmlString javaname) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(QnameconfigImpl.JAVANAME$2);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(QnameconfigImpl.JAVANAME$2);
            }
            target.set(javaname);
        }
    }
    
    @Override
    public void unsetJavaname() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(QnameconfigImpl.JAVANAME$2);
        }
    }
    
    @Override
    public List getTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.TARGET$4);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(QnameconfigImpl.TARGET$4);
            }
            if (target == null) {
                return null;
            }
            return target.getListValue();
        }
    }
    
    @Override
    public Qnametargetlist xgetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Qnametargetlist target = null;
            target = (Qnametargetlist)this.get_store().find_attribute_user(QnameconfigImpl.TARGET$4);
            if (target == null) {
                target = (Qnametargetlist)this.get_default_attribute_value(QnameconfigImpl.TARGET$4);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(QnameconfigImpl.TARGET$4) != null;
        }
    }
    
    @Override
    public void setTarget(final List targetValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(QnameconfigImpl.TARGET$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(QnameconfigImpl.TARGET$4);
            }
            target.setListValue(targetValue);
        }
    }
    
    @Override
    public void xsetTarget(final Qnametargetlist targetValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Qnametargetlist target = null;
            target = (Qnametargetlist)this.get_store().find_attribute_user(QnameconfigImpl.TARGET$4);
            if (target == null) {
                target = (Qnametargetlist)this.get_store().add_attribute_user(QnameconfigImpl.TARGET$4);
            }
            target.set(targetValue);
        }
    }
    
    @Override
    public void unsetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(QnameconfigImpl.TARGET$4);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        JAVANAME$2 = new QName("", "javaname");
        TARGET$4 = new QName("", "target");
    }
}
