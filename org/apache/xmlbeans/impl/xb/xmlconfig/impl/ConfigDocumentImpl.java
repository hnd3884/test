package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ConfigDocumentImpl extends XmlComplexContentImpl implements ConfigDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CONFIG$0;
    
    public ConfigDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Config getConfig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Config target = null;
            target = (Config)this.get_store().find_element_user(ConfigDocumentImpl.CONFIG$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setConfig(final Config config) {
        this.generatedSetterHelperImpl(config, ConfigDocumentImpl.CONFIG$0, 0, (short)1);
    }
    
    @Override
    public Config addNewConfig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Config target = null;
            target = (Config)this.get_store().add_element_user(ConfigDocumentImpl.CONFIG$0);
            return target;
        }
    }
    
    static {
        CONFIG$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "config");
    }
    
    public static class ConfigImpl extends XmlComplexContentImpl implements Config
    {
        private static final long serialVersionUID = 1L;
        private static final QName NAMESPACE$0;
        private static final QName QNAME$2;
        private static final QName EXTENSION$4;
        private static final QName USERTYPE$6;
        
        public ConfigImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public Nsconfig[] getNamespaceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(ConfigImpl.NAMESPACE$0, targetList);
                final Nsconfig[] result = new Nsconfig[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Nsconfig getNamespaceArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)this.get_store().find_element_user(ConfigImpl.NAMESPACE$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfNamespaceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(ConfigImpl.NAMESPACE$0);
            }
        }
        
        @Override
        public void setNamespaceArray(final Nsconfig[] namespaceArray) {
            this.check_orphaned();
            this.arraySetterHelper(namespaceArray, ConfigImpl.NAMESPACE$0);
        }
        
        @Override
        public void setNamespaceArray(final int i, final Nsconfig namespace) {
            this.generatedSetterHelperImpl(namespace, ConfigImpl.NAMESPACE$0, i, (short)2);
        }
        
        @Override
        public Nsconfig insertNewNamespace(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)this.get_store().insert_element_user(ConfigImpl.NAMESPACE$0, i);
                return target;
            }
        }
        
        @Override
        public Nsconfig addNewNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)this.get_store().add_element_user(ConfigImpl.NAMESPACE$0);
                return target;
            }
        }
        
        @Override
        public void removeNamespace(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ConfigImpl.NAMESPACE$0, i);
            }
        }
        
        @Override
        public Qnameconfig[] getQnameArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(ConfigImpl.QNAME$2, targetList);
                final Qnameconfig[] result = new Qnameconfig[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Qnameconfig getQnameArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)this.get_store().find_element_user(ConfigImpl.QNAME$2, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfQnameArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(ConfigImpl.QNAME$2);
            }
        }
        
        @Override
        public void setQnameArray(final Qnameconfig[] qnameArray) {
            this.check_orphaned();
            this.arraySetterHelper(qnameArray, ConfigImpl.QNAME$2);
        }
        
        @Override
        public void setQnameArray(final int i, final Qnameconfig qname) {
            this.generatedSetterHelperImpl(qname, ConfigImpl.QNAME$2, i, (short)2);
        }
        
        @Override
        public Qnameconfig insertNewQname(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)this.get_store().insert_element_user(ConfigImpl.QNAME$2, i);
                return target;
            }
        }
        
        @Override
        public Qnameconfig addNewQname() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)this.get_store().add_element_user(ConfigImpl.QNAME$2);
                return target;
            }
        }
        
        @Override
        public void removeQname(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ConfigImpl.QNAME$2, i);
            }
        }
        
        @Override
        public Extensionconfig[] getExtensionArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(ConfigImpl.EXTENSION$4, targetList);
                final Extensionconfig[] result = new Extensionconfig[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Extensionconfig getExtensionArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)this.get_store().find_element_user(ConfigImpl.EXTENSION$4, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfExtensionArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(ConfigImpl.EXTENSION$4);
            }
        }
        
        @Override
        public void setExtensionArray(final Extensionconfig[] extensionArray) {
            this.check_orphaned();
            this.arraySetterHelper(extensionArray, ConfigImpl.EXTENSION$4);
        }
        
        @Override
        public void setExtensionArray(final int i, final Extensionconfig extension) {
            this.generatedSetterHelperImpl(extension, ConfigImpl.EXTENSION$4, i, (short)2);
        }
        
        @Override
        public Extensionconfig insertNewExtension(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)this.get_store().insert_element_user(ConfigImpl.EXTENSION$4, i);
                return target;
            }
        }
        
        @Override
        public Extensionconfig addNewExtension() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)this.get_store().add_element_user(ConfigImpl.EXTENSION$4);
                return target;
            }
        }
        
        @Override
        public void removeExtension(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ConfigImpl.EXTENSION$4, i);
            }
        }
        
        @Override
        public Usertypeconfig[] getUsertypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(ConfigImpl.USERTYPE$6, targetList);
                final Usertypeconfig[] result = new Usertypeconfig[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Usertypeconfig getUsertypeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)this.get_store().find_element_user(ConfigImpl.USERTYPE$6, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfUsertypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(ConfigImpl.USERTYPE$6);
            }
        }
        
        @Override
        public void setUsertypeArray(final Usertypeconfig[] usertypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(usertypeArray, ConfigImpl.USERTYPE$6);
        }
        
        @Override
        public void setUsertypeArray(final int i, final Usertypeconfig usertype) {
            this.generatedSetterHelperImpl(usertype, ConfigImpl.USERTYPE$6, i, (short)2);
        }
        
        @Override
        public Usertypeconfig insertNewUsertype(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)this.get_store().insert_element_user(ConfigImpl.USERTYPE$6, i);
                return target;
            }
        }
        
        @Override
        public Usertypeconfig addNewUsertype() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)this.get_store().add_element_user(ConfigImpl.USERTYPE$6);
                return target;
            }
        }
        
        @Override
        public void removeUsertype(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(ConfigImpl.USERTYPE$6, i);
            }
        }
        
        static {
            NAMESPACE$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "namespace");
            QNAME$2 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "qname");
            EXTENSION$4 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "extension");
            USERTYPE$6 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "usertype");
        }
    }
}
