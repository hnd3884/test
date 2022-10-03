package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaNameList;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ExtensionconfigImpl extends XmlComplexContentImpl implements Extensionconfig
{
    private static final long serialVersionUID = 1L;
    private static final QName INTERFACE$0;
    private static final QName PREPOSTSET$2;
    private static final QName FOR$4;
    
    public ExtensionconfigImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Interface[] getInterfaceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ExtensionconfigImpl.INTERFACE$0, targetList);
            final Interface[] result = new Interface[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public Interface getInterfaceArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Interface target = null;
            target = (Interface)this.get_store().find_element_user(ExtensionconfigImpl.INTERFACE$0, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfInterfaceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionconfigImpl.INTERFACE$0);
        }
    }
    
    @Override
    public void setInterfaceArray(final Interface[] xinterfaceArray) {
        this.check_orphaned();
        this.arraySetterHelper(xinterfaceArray, ExtensionconfigImpl.INTERFACE$0);
    }
    
    @Override
    public void setInterfaceArray(final int i, final Interface xinterface) {
        this.generatedSetterHelperImpl(xinterface, ExtensionconfigImpl.INTERFACE$0, i, (short)2);
    }
    
    @Override
    public Interface insertNewInterface(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Interface target = null;
            target = (Interface)this.get_store().insert_element_user(ExtensionconfigImpl.INTERFACE$0, i);
            return target;
        }
    }
    
    @Override
    public Interface addNewInterface() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Interface target = null;
            target = (Interface)this.get_store().add_element_user(ExtensionconfigImpl.INTERFACE$0);
            return target;
        }
    }
    
    @Override
    public void removeInterface(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionconfigImpl.INTERFACE$0, i);
        }
    }
    
    @Override
    public PrePostSet getPrePostSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            PrePostSet target = null;
            target = (PrePostSet)this.get_store().find_element_user(ExtensionconfigImpl.PREPOSTSET$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetPrePostSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionconfigImpl.PREPOSTSET$2) != 0;
        }
    }
    
    @Override
    public void setPrePostSet(final PrePostSet prePostSet) {
        this.generatedSetterHelperImpl(prePostSet, ExtensionconfigImpl.PREPOSTSET$2, 0, (short)1);
    }
    
    @Override
    public PrePostSet addNewPrePostSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            PrePostSet target = null;
            target = (PrePostSet)this.get_store().add_element_user(ExtensionconfigImpl.PREPOSTSET$2);
            return target;
        }
    }
    
    @Override
    public void unsetPrePostSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionconfigImpl.PREPOSTSET$2, 0);
        }
    }
    
    @Override
    public Object getFor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ExtensionconfigImpl.FOR$4);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public JavaNameList xgetFor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            JavaNameList target = null;
            target = (JavaNameList)this.get_store().find_attribute_user(ExtensionconfigImpl.FOR$4);
            return target;
        }
    }
    
    @Override
    public boolean isSetFor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ExtensionconfigImpl.FOR$4) != null;
        }
    }
    
    @Override
    public void setFor(final Object xfor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ExtensionconfigImpl.FOR$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ExtensionconfigImpl.FOR$4);
            }
            target.setObjectValue(xfor);
        }
    }
    
    @Override
    public void xsetFor(final JavaNameList xfor) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            JavaNameList target = null;
            target = (JavaNameList)this.get_store().find_attribute_user(ExtensionconfigImpl.FOR$4);
            if (target == null) {
                target = (JavaNameList)this.get_store().add_attribute_user(ExtensionconfigImpl.FOR$4);
            }
            target.set(xfor);
        }
    }
    
    @Override
    public void unsetFor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ExtensionconfigImpl.FOR$4);
        }
    }
    
    static {
        INTERFACE$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "interface");
        PREPOSTSET$2 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "prePostSet");
        FOR$4 = new QName("", "for");
    }
    
    public static class InterfaceImpl extends XmlComplexContentImpl implements Interface
    {
        private static final long serialVersionUID = 1L;
        private static final QName STATICHANDLER$0;
        private static final QName NAME$2;
        
        public InterfaceImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getStaticHandler() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_element_user(InterfaceImpl.STATICHANDLER$0, 0);
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
                target = (XmlString)this.get_store().find_element_user(InterfaceImpl.STATICHANDLER$0, 0);
                return target;
            }
        }
        
        @Override
        public void setStaticHandler(final String staticHandler) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_element_user(InterfaceImpl.STATICHANDLER$0, 0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_element_user(InterfaceImpl.STATICHANDLER$0);
                }
                target.setStringValue(staticHandler);
            }
        }
        
        @Override
        public void xsetStaticHandler(final XmlString staticHandler) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)this.get_store().find_element_user(InterfaceImpl.STATICHANDLER$0, 0);
                if (target == null) {
                    target = (XmlString)this.get_store().add_element_user(InterfaceImpl.STATICHANDLER$0);
                }
                target.set(staticHandler);
            }
        }
        
        @Override
        public String getName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(InterfaceImpl.NAME$2);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlString xgetName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)this.get_store().find_attribute_user(InterfaceImpl.NAME$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(InterfaceImpl.NAME$2) != null;
            }
        }
        
        @Override
        public void setName(final String name) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(InterfaceImpl.NAME$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(InterfaceImpl.NAME$2);
                }
                target.setStringValue(name);
            }
        }
        
        @Override
        public void xsetName(final XmlString name) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)this.get_store().find_attribute_user(InterfaceImpl.NAME$2);
                if (target == null) {
                    target = (XmlString)this.get_store().add_attribute_user(InterfaceImpl.NAME$2);
                }
                target.set(name);
            }
        }
        
        @Override
        public void unsetName() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(InterfaceImpl.NAME$2);
            }
        }
        
        static {
            STATICHANDLER$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "staticHandler");
            NAME$2 = new QName("", "name");
        }
    }
    
    public static class PrePostSetImpl extends XmlComplexContentImpl implements PrePostSet
    {
        private static final long serialVersionUID = 1L;
        private static final QName STATICHANDLER$0;
        
        public PrePostSetImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public String getStaticHandler() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_element_user(PrePostSetImpl.STATICHANDLER$0, 0);
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
                target = (XmlString)this.get_store().find_element_user(PrePostSetImpl.STATICHANDLER$0, 0);
                return target;
            }
        }
        
        @Override
        public void setStaticHandler(final String staticHandler) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_element_user(PrePostSetImpl.STATICHANDLER$0, 0);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_element_user(PrePostSetImpl.STATICHANDLER$0);
                }
                target.setStringValue(staticHandler);
            }
        }
        
        @Override
        public void xsetStaticHandler(final XmlString staticHandler) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)this.get_store().find_element_user(PrePostSetImpl.STATICHANDLER$0, 0);
                if (target == null) {
                    target = (XmlString)this.get_store().add_element_user(PrePostSetImpl.STATICHANDLER$0);
                }
                target.set(staticHandler);
            }
        }
        
        static {
            STATICHANDLER$0 = new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "staticHandler");
        }
    }
}
