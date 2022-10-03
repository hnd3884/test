package org.apache.xmlbeans.impl.xb.ltgfmt.impl;

import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.ltgfmt.FileDesc;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TestCaseImpl extends XmlComplexContentImpl implements TestCase
{
    private static final long serialVersionUID = 1L;
    private static final QName DESCRIPTION$0;
    private static final QName FILES$2;
    private static final QName ID$4;
    private static final QName ORIGIN$6;
    private static final QName MODIFIED$8;
    
    public TestCaseImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(TestCaseImpl.DESCRIPTION$0, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(TestCaseImpl.DESCRIPTION$0, 0);
            return target;
        }
    }
    
    @Override
    public boolean isSetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TestCaseImpl.DESCRIPTION$0) != 0;
        }
    }
    
    @Override
    public void setDescription(final String description) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(TestCaseImpl.DESCRIPTION$0, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(TestCaseImpl.DESCRIPTION$0);
            }
            target.setStringValue(description);
        }
    }
    
    @Override
    public void xsetDescription(final XmlString description) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_element_user(TestCaseImpl.DESCRIPTION$0, 0);
            if (target == null) {
                target = (XmlString)this.get_store().add_element_user(TestCaseImpl.DESCRIPTION$0);
            }
            target.set(description);
        }
    }
    
    @Override
    public void unsetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TestCaseImpl.DESCRIPTION$0, 0);
        }
    }
    
    @Override
    public Files getFiles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Files target = null;
            target = (Files)this.get_store().find_element_user(TestCaseImpl.FILES$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setFiles(final Files files) {
        this.generatedSetterHelperImpl(files, TestCaseImpl.FILES$2, 0, (short)1);
    }
    
    @Override
    public Files addNewFiles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Files target = null;
            target = (Files)this.get_store().add_element_user(TestCaseImpl.FILES$2);
            return target;
        }
    }
    
    @Override
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.ID$4);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)this.get_store().find_attribute_user(TestCaseImpl.ID$4);
            return target;
        }
    }
    
    @Override
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(TestCaseImpl.ID$4) != null;
        }
    }
    
    @Override
    public void setId(final String id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.ID$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TestCaseImpl.ID$4);
            }
            target.setStringValue(id);
        }
    }
    
    @Override
    public void xsetId(final XmlID id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)this.get_store().find_attribute_user(TestCaseImpl.ID$4);
            if (target == null) {
                target = (XmlID)this.get_store().add_attribute_user(TestCaseImpl.ID$4);
            }
            target.set(id);
        }
    }
    
    @Override
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(TestCaseImpl.ID$4);
        }
    }
    
    @Override
    public String getOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.ORIGIN$6);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(TestCaseImpl.ORIGIN$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(TestCaseImpl.ORIGIN$6) != null;
        }
    }
    
    @Override
    public void setOrigin(final String origin) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.ORIGIN$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TestCaseImpl.ORIGIN$6);
            }
            target.setStringValue(origin);
        }
    }
    
    @Override
    public void xsetOrigin(final XmlToken origin) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(TestCaseImpl.ORIGIN$6);
            if (target == null) {
                target = (XmlToken)this.get_store().add_attribute_user(TestCaseImpl.ORIGIN$6);
            }
            target.set(origin);
        }
    }
    
    @Override
    public void unsetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(TestCaseImpl.ORIGIN$6);
        }
    }
    
    @Override
    public boolean getModified() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.MODIFIED$8);
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetModified() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(TestCaseImpl.MODIFIED$8);
            return target;
        }
    }
    
    @Override
    public boolean isSetModified() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(TestCaseImpl.MODIFIED$8) != null;
        }
    }
    
    @Override
    public void setModified(final boolean modified) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TestCaseImpl.MODIFIED$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TestCaseImpl.MODIFIED$8);
            }
            target.setBooleanValue(modified);
        }
    }
    
    @Override
    public void xsetModified(final XmlBoolean modified) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(TestCaseImpl.MODIFIED$8);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(TestCaseImpl.MODIFIED$8);
            }
            target.set(modified);
        }
    }
    
    @Override
    public void unsetModified() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(TestCaseImpl.MODIFIED$8);
        }
    }
    
    static {
        DESCRIPTION$0 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "description");
        FILES$2 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "files");
        ID$4 = new QName("", "id");
        ORIGIN$6 = new QName("", "origin");
        MODIFIED$8 = new QName("", "modified");
    }
    
    public static class FilesImpl extends XmlComplexContentImpl implements Files
    {
        private static final long serialVersionUID = 1L;
        private static final QName FILE$0;
        
        public FilesImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public FileDesc[] getFileArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(FilesImpl.FILE$0, targetList);
                final FileDesc[] result = new FileDesc[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public FileDesc getFileArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FileDesc target = null;
                target = (FileDesc)this.get_store().find_element_user(FilesImpl.FILE$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfFileArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(FilesImpl.FILE$0);
            }
        }
        
        @Override
        public void setFileArray(final FileDesc[] fileArray) {
            this.check_orphaned();
            this.arraySetterHelper(fileArray, FilesImpl.FILE$0);
        }
        
        @Override
        public void setFileArray(final int i, final FileDesc file) {
            this.generatedSetterHelperImpl(file, FilesImpl.FILE$0, i, (short)2);
        }
        
        @Override
        public FileDesc insertNewFile(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FileDesc target = null;
                target = (FileDesc)this.get_store().insert_element_user(FilesImpl.FILE$0, i);
                return target;
            }
        }
        
        @Override
        public FileDesc addNewFile() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FileDesc target = null;
                target = (FileDesc)this.get_store().add_element_user(FilesImpl.FILE$0);
                return target;
            }
        }
        
        @Override
        public void removeFile(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(FilesImpl.FILE$0, i);
            }
        }
        
        static {
            FILE$0 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "file");
        }
    }
}
