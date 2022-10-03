package org.apache.xmlbeans.impl.xb.ltgfmt.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.ltgfmt.FileDesc;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class FileDescImpl extends XmlComplexContentImpl implements FileDesc
{
    private static final long serialVersionUID = 1L;
    private static final QName CODE$0;
    private static final QName TSDIR$2;
    private static final QName FOLDER$4;
    private static final QName FILENAME$6;
    private static final QName ROLE$8;
    private static final QName VALIDITY$10;
    
    public FileDescImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Code getCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Code target = null;
            target = (Code)this.get_store().find_element_user(FileDescImpl.CODE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(FileDescImpl.CODE$0) != 0;
        }
    }
    
    @Override
    public void setCode(final Code code) {
        this.generatedSetterHelperImpl(code, FileDescImpl.CODE$0, 0, (short)1);
    }
    
    @Override
    public Code addNewCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Code target = null;
            target = (Code)this.get_store().add_element_user(FileDescImpl.CODE$0);
            return target;
        }
    }
    
    @Override
    public void unsetCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(FileDescImpl.CODE$0, 0);
        }
    }
    
    @Override
    public String getTsDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.TSDIR$2);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetTsDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.TSDIR$2);
            return target;
        }
    }
    
    @Override
    public boolean isSetTsDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FileDescImpl.TSDIR$2) != null;
        }
    }
    
    @Override
    public void setTsDir(final String tsDir) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.TSDIR$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FileDescImpl.TSDIR$2);
            }
            target.setStringValue(tsDir);
        }
    }
    
    @Override
    public void xsetTsDir(final XmlToken tsDir) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.TSDIR$2);
            if (target == null) {
                target = (XmlToken)this.get_store().add_attribute_user(FileDescImpl.TSDIR$2);
            }
            target.set(tsDir);
        }
    }
    
    @Override
    public void unsetTsDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FileDescImpl.TSDIR$2);
        }
    }
    
    @Override
    public String getFolder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.FOLDER$4);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetFolder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.FOLDER$4);
            return target;
        }
    }
    
    @Override
    public boolean isSetFolder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FileDescImpl.FOLDER$4) != null;
        }
    }
    
    @Override
    public void setFolder(final String folder) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.FOLDER$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FileDescImpl.FOLDER$4);
            }
            target.setStringValue(folder);
        }
    }
    
    @Override
    public void xsetFolder(final XmlToken folder) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.FOLDER$4);
            if (target == null) {
                target = (XmlToken)this.get_store().add_attribute_user(FileDescImpl.FOLDER$4);
            }
            target.set(folder);
        }
    }
    
    @Override
    public void unsetFolder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FileDescImpl.FOLDER$4);
        }
    }
    
    @Override
    public String getFileName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.FILENAME$6);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetFileName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.FILENAME$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetFileName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FileDescImpl.FILENAME$6) != null;
        }
    }
    
    @Override
    public void setFileName(final String fileName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.FILENAME$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FileDescImpl.FILENAME$6);
            }
            target.setStringValue(fileName);
        }
    }
    
    @Override
    public void xsetFileName(final XmlToken fileName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(FileDescImpl.FILENAME$6);
            if (target == null) {
                target = (XmlToken)this.get_store().add_attribute_user(FileDescImpl.FILENAME$6);
            }
            target.set(fileName);
        }
    }
    
    @Override
    public void unsetFileName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FileDescImpl.FILENAME$6);
        }
    }
    
    @Override
    public Role.Enum getRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.ROLE$8);
            if (target == null) {
                return null;
            }
            return (Role.Enum)target.getEnumValue();
        }
    }
    
    @Override
    public Role xgetRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Role target = null;
            target = (Role)this.get_store().find_attribute_user(FileDescImpl.ROLE$8);
            return target;
        }
    }
    
    @Override
    public boolean isSetRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FileDescImpl.ROLE$8) != null;
        }
    }
    
    @Override
    public void setRole(final Role.Enum role) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.ROLE$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FileDescImpl.ROLE$8);
            }
            target.setEnumValue(role);
        }
    }
    
    @Override
    public void xsetRole(final Role role) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Role target = null;
            target = (Role)this.get_store().find_attribute_user(FileDescImpl.ROLE$8);
            if (target == null) {
                target = (Role)this.get_store().add_attribute_user(FileDescImpl.ROLE$8);
            }
            target.set(role);
        }
    }
    
    @Override
    public void unsetRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FileDescImpl.ROLE$8);
        }
    }
    
    @Override
    public boolean getValidity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.VALIDITY$10);
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetValidity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(FileDescImpl.VALIDITY$10);
            return target;
        }
    }
    
    @Override
    public boolean isSetValidity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FileDescImpl.VALIDITY$10) != null;
        }
    }
    
    @Override
    public void setValidity(final boolean validity) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FileDescImpl.VALIDITY$10);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FileDescImpl.VALIDITY$10);
            }
            target.setBooleanValue(validity);
        }
    }
    
    @Override
    public void xsetValidity(final XmlBoolean validity) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(FileDescImpl.VALIDITY$10);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(FileDescImpl.VALIDITY$10);
            }
            target.set(validity);
        }
    }
    
    @Override
    public void unsetValidity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FileDescImpl.VALIDITY$10);
        }
    }
    
    static {
        CODE$0 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "code");
        TSDIR$2 = new QName("", "tsDir");
        FOLDER$4 = new QName("", "folder");
        FILENAME$6 = new QName("", "fileName");
        ROLE$8 = new QName("", "role");
        VALIDITY$10 = new QName("", "validity");
    }
    
    public static class RoleImpl extends JavaStringEnumerationHolderEx implements Role
    {
        private static final long serialVersionUID = 1L;
        
        public RoleImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected RoleImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
