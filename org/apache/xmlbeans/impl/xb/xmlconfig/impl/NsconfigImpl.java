package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.impl.xb.xmlconfig.NamespacePrefixList;
import java.util.List;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class NsconfigImpl extends XmlComplexContentImpl implements Nsconfig
{
    private static final long serialVersionUID = 1L;
    private static final QName PACKAGE$0;
    private static final QName PREFIX$2;
    private static final QName SUFFIX$4;
    private static final QName URI$6;
    private static final QName URIPREFIX$8;
    
    public NsconfigImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getPackage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.PACKAGE$0, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetPackage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.PACKAGE$0, 0);
            return target;
        }
    }
    
    @Override
    public boolean isSetPackage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(NsconfigImpl.PACKAGE$0) != 0;
        }
    }
    
    @Override
    public void setPackage(final String xpackage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.PACKAGE$0, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(NsconfigImpl.PACKAGE$0);
            }
            target.setStringValue(xpackage);
        }
    }
    
    @Override
    public void xsetPackage(final XmlString xpackage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.PACKAGE$0, 0);
            if (target == null) {
                target = (XmlString)this.get_store().add_element_user(NsconfigImpl.PACKAGE$0);
            }
            target.set(xpackage);
        }
    }
    
    @Override
    public void unsetPackage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(NsconfigImpl.PACKAGE$0, 0);
        }
    }
    
    @Override
    public String getPrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.PREFIX$2, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetPrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.PREFIX$2, 0);
            return target;
        }
    }
    
    @Override
    public boolean isSetPrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(NsconfigImpl.PREFIX$2) != 0;
        }
    }
    
    @Override
    public void setPrefix(final String prefix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.PREFIX$2, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(NsconfigImpl.PREFIX$2);
            }
            target.setStringValue(prefix);
        }
    }
    
    @Override
    public void xsetPrefix(final XmlString prefix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.PREFIX$2, 0);
            if (target == null) {
                target = (XmlString)this.get_store().add_element_user(NsconfigImpl.PREFIX$2);
            }
            target.set(prefix);
        }
    }
    
    @Override
    public void unsetPrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(NsconfigImpl.PREFIX$2, 0);
        }
    }
    
    @Override
    public String getSuffix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.SUFFIX$4, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetSuffix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.SUFFIX$4, 0);
            return target;
        }
    }
    
    @Override
    public boolean isSetSuffix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(NsconfigImpl.SUFFIX$4) != 0;
        }
    }
    
    @Override
    public void setSuffix(final String suffix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(NsconfigImpl.SUFFIX$4, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(NsconfigImpl.SUFFIX$4);
            }
            target.setStringValue(suffix);
        }
    }
    
    @Override
    public void xsetSuffix(final XmlString suffix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(NsconfigImpl.SUFFIX$4, 0);
            if (target == null) {
                target = (XmlString)this.get_store().add_element_user(NsconfigImpl.SUFFIX$4);
            }
            target.set(suffix);
        }
    }
    
    @Override
    public void unsetSuffix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(NsconfigImpl.SUFFIX$4, 0);
        }
    }
    
    @Override
    public Object getUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(NsconfigImpl.URI$6);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public NamespaceList xgetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespaceList target = null;
            target = (NamespaceList)this.get_store().find_attribute_user(NsconfigImpl.URI$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(NsconfigImpl.URI$6) != null;
        }
    }
    
    @Override
    public void setUri(final Object uri) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(NsconfigImpl.URI$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(NsconfigImpl.URI$6);
            }
            target.setObjectValue(uri);
        }
    }
    
    @Override
    public void xsetUri(final NamespaceList uri) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespaceList target = null;
            target = (NamespaceList)this.get_store().find_attribute_user(NsconfigImpl.URI$6);
            if (target == null) {
                target = (NamespaceList)this.get_store().add_attribute_user(NsconfigImpl.URI$6);
            }
            target.set(uri);
        }
    }
    
    @Override
    public void unsetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(NsconfigImpl.URI$6);
        }
    }
    
    @Override
    public List getUriprefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(NsconfigImpl.URIPREFIX$8);
            if (target == null) {
                return null;
            }
            return target.getListValue();
        }
    }
    
    @Override
    public NamespacePrefixList xgetUriprefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespacePrefixList target = null;
            target = (NamespacePrefixList)this.get_store().find_attribute_user(NsconfigImpl.URIPREFIX$8);
            return target;
        }
    }
    
    @Override
    public boolean isSetUriprefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(NsconfigImpl.URIPREFIX$8) != null;
        }
    }
    
    @Override
    public void setUriprefix(final List uriprefix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(NsconfigImpl.URIPREFIX$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(NsconfigImpl.URIPREFIX$8);
            }
            target.setListValue(uriprefix);
        }
    }
    
    @Override
    public void xsetUriprefix(final NamespacePrefixList uriprefix) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespacePrefixList target = null;
            target = (NamespacePrefixList)this.get_store().find_attribute_user(NsconfigImpl.URIPREFIX$8);
            if (target == null) {
                target = (NamespacePrefixList)this.get_store().add_attribute_user(NsconfigImpl.URIPREFIX$8);
            }
            target.set(uriprefix);
        }
    }
    
    @Override
    public void unsetUriprefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(NsconfigImpl.URIPREFIX$8);
        }
    }
    
    static {
        PACKAGE$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "package");
        PREFIX$2 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "prefix");
        SUFFIX$4 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "suffix");
        URI$6 = new QName("", "uri");
        URIPREFIX$8 = new QName("", "uriprefix");
    }
}
