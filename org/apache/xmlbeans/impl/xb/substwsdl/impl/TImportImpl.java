package org.apache.xmlbeans.impl.xb.substwsdl.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.substwsdl.TImport;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TImportImpl extends XmlComplexContentImpl implements TImport
{
    private static final long serialVersionUID = 1L;
    private static final QName NAMESPACE$0;
    private static final QName LOCATION$2;
    
    public TImportImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TImportImpl.NAMESPACE$0);
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
            target = (XmlAnyURI)this.get_store().find_attribute_user(TImportImpl.NAMESPACE$0);
            return target;
        }
    }
    
    @Override
    public void setNamespace(final String namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TImportImpl.NAMESPACE$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TImportImpl.NAMESPACE$0);
            }
            target.setStringValue(namespace);
        }
    }
    
    @Override
    public void xsetNamespace(final XmlAnyURI namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_attribute_user(TImportImpl.NAMESPACE$0);
            if (target == null) {
                target = (XmlAnyURI)this.get_store().add_attribute_user(TImportImpl.NAMESPACE$0);
            }
            target.set(namespace);
        }
    }
    
    @Override
    public String getLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TImportImpl.LOCATION$2);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlAnyURI xgetLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_attribute_user(TImportImpl.LOCATION$2);
            return target;
        }
    }
    
    @Override
    public void setLocation(final String location) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TImportImpl.LOCATION$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TImportImpl.LOCATION$2);
            }
            target.setStringValue(location);
        }
    }
    
    @Override
    public void xsetLocation(final XmlAnyURI location) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_attribute_user(TImportImpl.LOCATION$2);
            if (target == null) {
                target = (XmlAnyURI)this.get_store().add_attribute_user(TImportImpl.LOCATION$2);
            }
            target.set(location);
        }
    }
    
    static {
        NAMESPACE$0 = new QName("", "namespace");
        LOCATION$2 = new QName("", "location");
    }
}
