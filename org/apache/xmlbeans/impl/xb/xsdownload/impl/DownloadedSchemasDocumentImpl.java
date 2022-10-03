package org.apache.xmlbeans.impl.xb.xsdownload.impl;

import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SimpleValue;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemasDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DownloadedSchemasDocumentImpl extends XmlComplexContentImpl implements DownloadedSchemasDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName DOWNLOADEDSCHEMAS$0;
    
    public DownloadedSchemasDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public DownloadedSchemas getDownloadedSchemas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DownloadedSchemas target = null;
            target = (DownloadedSchemas)this.get_store().find_element_user(DownloadedSchemasDocumentImpl.DOWNLOADEDSCHEMAS$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setDownloadedSchemas(final DownloadedSchemas downloadedSchemas) {
        this.generatedSetterHelperImpl(downloadedSchemas, DownloadedSchemasDocumentImpl.DOWNLOADEDSCHEMAS$0, 0, (short)1);
    }
    
    @Override
    public DownloadedSchemas addNewDownloadedSchemas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DownloadedSchemas target = null;
            target = (DownloadedSchemas)this.get_store().add_element_user(DownloadedSchemasDocumentImpl.DOWNLOADEDSCHEMAS$0);
            return target;
        }
    }
    
    static {
        DOWNLOADEDSCHEMAS$0 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "downloaded-schemas");
    }
    
    public static class DownloadedSchemasImpl extends XmlComplexContentImpl implements DownloadedSchemas
    {
        private static final long serialVersionUID = 1L;
        private static final QName ENTRY$0;
        private static final QName DEFAULTDIRECTORY$2;
        
        public DownloadedSchemasImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public DownloadedSchemaEntry[] getEntryArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(DownloadedSchemasImpl.ENTRY$0, targetList);
                final DownloadedSchemaEntry[] result = new DownloadedSchemaEntry[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public DownloadedSchemaEntry getEntryArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)this.get_store().find_element_user(DownloadedSchemasImpl.ENTRY$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfEntryArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(DownloadedSchemasImpl.ENTRY$0);
            }
        }
        
        @Override
        public void setEntryArray(final DownloadedSchemaEntry[] entryArray) {
            this.check_orphaned();
            this.arraySetterHelper(entryArray, DownloadedSchemasImpl.ENTRY$0);
        }
        
        @Override
        public void setEntryArray(final int i, final DownloadedSchemaEntry entry) {
            this.generatedSetterHelperImpl(entry, DownloadedSchemasImpl.ENTRY$0, i, (short)2);
        }
        
        @Override
        public DownloadedSchemaEntry insertNewEntry(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)this.get_store().insert_element_user(DownloadedSchemasImpl.ENTRY$0, i);
                return target;
            }
        }
        
        @Override
        public DownloadedSchemaEntry addNewEntry() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)this.get_store().add_element_user(DownloadedSchemasImpl.ENTRY$0);
                return target;
            }
        }
        
        @Override
        public void removeEntry(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(DownloadedSchemasImpl.ENTRY$0, i);
            }
        }
        
        @Override
        public String getDefaultDirectory() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlToken xgetDefaultDirectory() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)this.get_store().find_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetDefaultDirectory() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2) != null;
            }
        }
        
        @Override
        public void setDefaultDirectory(final String defaultDirectory) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                }
                target.setStringValue(defaultDirectory);
            }
        }
        
        @Override
        public void xsetDefaultDirectory(final XmlToken defaultDirectory) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)this.get_store().find_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                if (target == null) {
                    target = (XmlToken)this.get_store().add_attribute_user(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
                }
                target.set(defaultDirectory);
            }
        }
        
        @Override
        public void unsetDefaultDirectory() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(DownloadedSchemasImpl.DEFAULTDIRECTORY$2);
            }
        }
        
        static {
            ENTRY$0 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "entry");
            DEFAULTDIRECTORY$2 = new QName("", "defaultDirectory");
        }
    }
}
