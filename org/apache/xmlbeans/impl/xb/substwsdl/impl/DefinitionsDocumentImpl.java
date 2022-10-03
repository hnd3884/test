package org.apache.xmlbeans.impl.xb.substwsdl.impl;

import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.substwsdl.TImport;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DefinitionsDocumentImpl extends XmlComplexContentImpl implements DefinitionsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName DEFINITIONS$0;
    
    public DefinitionsDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Definitions getDefinitions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Definitions target = null;
            target = (Definitions)this.get_store().find_element_user(DefinitionsDocumentImpl.DEFINITIONS$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setDefinitions(final Definitions definitions) {
        this.generatedSetterHelperImpl(definitions, DefinitionsDocumentImpl.DEFINITIONS$0, 0, (short)1);
    }
    
    @Override
    public Definitions addNewDefinitions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Definitions target = null;
            target = (Definitions)this.get_store().add_element_user(DefinitionsDocumentImpl.DEFINITIONS$0);
            return target;
        }
    }
    
    static {
        DEFINITIONS$0 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "definitions");
    }
    
    public static class DefinitionsImpl extends XmlComplexContentImpl implements Definitions
    {
        private static final long serialVersionUID = 1L;
        private static final QName IMPORT$0;
        private static final QName TYPES$2;
        private static final QName MESSAGE$4;
        private static final QName BINDING$6;
        private static final QName PORTTYPE$8;
        private static final QName SERVICE$10;
        
        public DefinitionsImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public TImport[] getImportArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.IMPORT$0, targetList);
                final TImport[] result = new TImport[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TImport getImportArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)this.get_store().find_element_user(DefinitionsImpl.IMPORT$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfImportArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.IMPORT$0);
            }
        }
        
        @Override
        public void setImportArray(final TImport[] ximportArray) {
            this.check_orphaned();
            this.arraySetterHelper(ximportArray, DefinitionsImpl.IMPORT$0);
        }
        
        @Override
        public void setImportArray(final int i, final TImport ximport) {
            this.generatedSetterHelperImpl(ximport, DefinitionsImpl.IMPORT$0, i, (short)2);
        }
        
        @Override
        public TImport insertNewImport(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)this.get_store().insert_element_user(DefinitionsImpl.IMPORT$0, i);
                return target;
            }
        }
        
        @Override
        public TImport addNewImport() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)this.get_store().add_element_user(DefinitionsImpl.IMPORT$0);
                return target;
            }
        }
        
        @Override
        public void removeImport(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.IMPORT$0, i);
            }
        }
        
        @Override
        public XmlObject[] getTypesArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.TYPES$2, targetList);
                final XmlObject[] result = new XmlObject[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public XmlObject getTypesArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().find_element_user(DefinitionsImpl.TYPES$2, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfTypesArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.TYPES$2);
            }
        }
        
        @Override
        public void setTypesArray(final XmlObject[] typesArray) {
            this.check_orphaned();
            this.arraySetterHelper(typesArray, DefinitionsImpl.TYPES$2);
        }
        
        @Override
        public void setTypesArray(final int i, final XmlObject types) {
            this.generatedSetterHelperImpl(types, DefinitionsImpl.TYPES$2, i, (short)2);
        }
        
        @Override
        public XmlObject insertNewTypes(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().insert_element_user(DefinitionsImpl.TYPES$2, i);
                return target;
            }
        }
        
        @Override
        public XmlObject addNewTypes() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().add_element_user(DefinitionsImpl.TYPES$2);
                return target;
            }
        }
        
        @Override
        public void removeTypes(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.TYPES$2, i);
            }
        }
        
        @Override
        public XmlObject[] getMessageArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.MESSAGE$4, targetList);
                final XmlObject[] result = new XmlObject[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public XmlObject getMessageArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().find_element_user(DefinitionsImpl.MESSAGE$4, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMessageArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.MESSAGE$4);
            }
        }
        
        @Override
        public void setMessageArray(final XmlObject[] messageArray) {
            this.check_orphaned();
            this.arraySetterHelper(messageArray, DefinitionsImpl.MESSAGE$4);
        }
        
        @Override
        public void setMessageArray(final int i, final XmlObject message) {
            this.generatedSetterHelperImpl(message, DefinitionsImpl.MESSAGE$4, i, (short)2);
        }
        
        @Override
        public XmlObject insertNewMessage(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().insert_element_user(DefinitionsImpl.MESSAGE$4, i);
                return target;
            }
        }
        
        @Override
        public XmlObject addNewMessage() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().add_element_user(DefinitionsImpl.MESSAGE$4);
                return target;
            }
        }
        
        @Override
        public void removeMessage(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.MESSAGE$4, i);
            }
        }
        
        @Override
        public XmlObject[] getBindingArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.BINDING$6, targetList);
                final XmlObject[] result = new XmlObject[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public XmlObject getBindingArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().find_element_user(DefinitionsImpl.BINDING$6, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfBindingArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.BINDING$6);
            }
        }
        
        @Override
        public void setBindingArray(final XmlObject[] bindingArray) {
            this.check_orphaned();
            this.arraySetterHelper(bindingArray, DefinitionsImpl.BINDING$6);
        }
        
        @Override
        public void setBindingArray(final int i, final XmlObject binding) {
            this.generatedSetterHelperImpl(binding, DefinitionsImpl.BINDING$6, i, (short)2);
        }
        
        @Override
        public XmlObject insertNewBinding(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().insert_element_user(DefinitionsImpl.BINDING$6, i);
                return target;
            }
        }
        
        @Override
        public XmlObject addNewBinding() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().add_element_user(DefinitionsImpl.BINDING$6);
                return target;
            }
        }
        
        @Override
        public void removeBinding(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.BINDING$6, i);
            }
        }
        
        @Override
        public XmlObject[] getPortTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.PORTTYPE$8, targetList);
                final XmlObject[] result = new XmlObject[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public XmlObject getPortTypeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().find_element_user(DefinitionsImpl.PORTTYPE$8, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfPortTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.PORTTYPE$8);
            }
        }
        
        @Override
        public void setPortTypeArray(final XmlObject[] portTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(portTypeArray, DefinitionsImpl.PORTTYPE$8);
        }
        
        @Override
        public void setPortTypeArray(final int i, final XmlObject portType) {
            this.generatedSetterHelperImpl(portType, DefinitionsImpl.PORTTYPE$8, i, (short)2);
        }
        
        @Override
        public XmlObject insertNewPortType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().insert_element_user(DefinitionsImpl.PORTTYPE$8, i);
                return target;
            }
        }
        
        @Override
        public XmlObject addNewPortType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().add_element_user(DefinitionsImpl.PORTTYPE$8);
                return target;
            }
        }
        
        @Override
        public void removePortType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.PORTTYPE$8, i);
            }
        }
        
        @Override
        public XmlObject[] getServiceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DefinitionsImpl.SERVICE$10, targetList);
                final XmlObject[] result = new XmlObject[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public XmlObject getServiceArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().find_element_user(DefinitionsImpl.SERVICE$10, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfServiceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DefinitionsImpl.SERVICE$10);
            }
        }
        
        @Override
        public void setServiceArray(final XmlObject[] serviceArray) {
            this.check_orphaned();
            this.arraySetterHelper(serviceArray, DefinitionsImpl.SERVICE$10);
        }
        
        @Override
        public void setServiceArray(final int i, final XmlObject service) {
            this.generatedSetterHelperImpl(service, DefinitionsImpl.SERVICE$10, i, (short)2);
        }
        
        @Override
        public XmlObject insertNewService(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().insert_element_user(DefinitionsImpl.SERVICE$10, i);
                return target;
            }
        }
        
        @Override
        public XmlObject addNewService() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)this.get_store().add_element_user(DefinitionsImpl.SERVICE$10);
                return target;
            }
        }
        
        @Override
        public void removeService(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DefinitionsImpl.SERVICE$10, i);
            }
        }
        
        static {
            IMPORT$0 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "import");
            TYPES$2 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "types");
            MESSAGE$4 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "message");
            BINDING$6 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "binding");
            PORTTYPE$8 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "portType");
            SERVICE$10 = new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "service");
        }
    }
}
