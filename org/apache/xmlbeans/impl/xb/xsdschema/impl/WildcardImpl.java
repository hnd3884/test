package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NamespaceList;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;

public class WildcardImpl extends AnnotatedImpl implements Wildcard
{
    private static final long serialVersionUID = 1L;
    private static final QName NAMESPACE$0;
    private static final QName PROCESSCONTENTS$2;
    
    public WildcardImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Object getNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(WildcardImpl.NAMESPACE$0);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(WildcardImpl.NAMESPACE$0);
            }
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public NamespaceList xgetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespaceList target = null;
            target = (NamespaceList)this.get_store().find_attribute_user(WildcardImpl.NAMESPACE$0);
            if (target == null) {
                target = (NamespaceList)this.get_default_attribute_value(WildcardImpl.NAMESPACE$0);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(WildcardImpl.NAMESPACE$0) != null;
        }
    }
    
    @Override
    public void setNamespace(final Object namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(WildcardImpl.NAMESPACE$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(WildcardImpl.NAMESPACE$0);
            }
            target.setObjectValue(namespace);
        }
    }
    
    @Override
    public void xsetNamespace(final NamespaceList namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamespaceList target = null;
            target = (NamespaceList)this.get_store().find_attribute_user(WildcardImpl.NAMESPACE$0);
            if (target == null) {
                target = (NamespaceList)this.get_store().add_attribute_user(WildcardImpl.NAMESPACE$0);
            }
            target.set(namespace);
        }
    }
    
    @Override
    public void unsetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(WildcardImpl.NAMESPACE$0);
        }
    }
    
    @Override
    public ProcessContents.Enum getProcessContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(WildcardImpl.PROCESSCONTENTS$2);
            }
            if (target == null) {
                return null;
            }
            return (ProcessContents.Enum)target.getEnumValue();
        }
    }
    
    @Override
    public ProcessContents xgetProcessContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ProcessContents target = null;
            target = (ProcessContents)this.get_store().find_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            if (target == null) {
                target = (ProcessContents)this.get_default_attribute_value(WildcardImpl.PROCESSCONTENTS$2);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetProcessContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(WildcardImpl.PROCESSCONTENTS$2) != null;
        }
    }
    
    @Override
    public void setProcessContents(final ProcessContents.Enum processContents) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            }
            target.setEnumValue(processContents);
        }
    }
    
    @Override
    public void xsetProcessContents(final ProcessContents processContents) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ProcessContents target = null;
            target = (ProcessContents)this.get_store().find_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            if (target == null) {
                target = (ProcessContents)this.get_store().add_attribute_user(WildcardImpl.PROCESSCONTENTS$2);
            }
            target.set(processContents);
        }
    }
    
    @Override
    public void unsetProcessContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(WildcardImpl.PROCESSCONTENTS$2);
        }
    }
    
    static {
        NAMESPACE$0 = new QName("", "namespace");
        PROCESSCONTENTS$2 = new QName("", "processContents");
    }
    
    public static class ProcessContentsImpl extends JavaStringEnumerationHolderEx implements ProcessContents
    {
        private static final long serialVersionUID = 1L;
        
        public ProcessContentsImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected ProcessContentsImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
