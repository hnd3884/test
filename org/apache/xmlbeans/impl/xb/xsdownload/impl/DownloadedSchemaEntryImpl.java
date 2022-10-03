package org.apache.xmlbeans.impl.xb.xsdownload.impl;

import org.apache.xmlbeans.XmlAnyURI;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DownloadedSchemaEntryImpl extends XmlComplexContentImpl implements DownloadedSchemaEntry
{
    private static final long serialVersionUID = 1L;
    private static final QName FILENAME$0;
    private static final QName SHA1$2;
    private static final QName SCHEMALOCATION$4;
    private static final QName NAMESPACE$6;
    
    public DownloadedSchemaEntryImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getFilename() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.FILENAME$0, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetFilename() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_element_user(DownloadedSchemaEntryImpl.FILENAME$0, 0);
            return target;
        }
    }
    
    @Override
    public void setFilename(final String filename) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.FILENAME$0, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(DownloadedSchemaEntryImpl.FILENAME$0);
            }
            target.setStringValue(filename);
        }
    }
    
    @Override
    public void xsetFilename(final XmlToken filename) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_element_user(DownloadedSchemaEntryImpl.FILENAME$0, 0);
            if (target == null) {
                target = (XmlToken)this.get_store().add_element_user(DownloadedSchemaEntryImpl.FILENAME$0);
            }
            target.set(filename);
        }
    }
    
    @Override
    public String getSha1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SHA1$2, 0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetSha1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SHA1$2, 0);
            return target;
        }
    }
    
    @Override
    public void setSha1(final String sha1) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SHA1$2, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(DownloadedSchemaEntryImpl.SHA1$2);
            }
            target.setStringValue(sha1);
        }
    }
    
    @Override
    public void xsetSha1(final XmlToken sha1) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SHA1$2, 0);
            if (target == null) {
                target = (XmlToken)this.get_store().add_element_user(DownloadedSchemaEntryImpl.SHA1$2);
            }
            target.set(sha1);
        }
    }
    
    @Override
    public String[] getSchemaLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, targetList);
            final String[] result = new String[targetList.size()];
            for (int i = 0, len = targetList.size(); i < len; ++i) {
                result[i] = targetList.get(i).getStringValue();
            }
            return result;
        }
    }
    
    @Override
    public String getSchemaLocationArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlAnyURI[] xgetSchemaLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, targetList);
            final XmlAnyURI[] result = new XmlAnyURI[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public XmlAnyURI xgetSchemaLocationArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfSchemaLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DownloadedSchemaEntryImpl.SCHEMALOCATION$4);
        }
    }
    
    @Override
    public void setSchemaLocationArray(final String[] schemaLocationArray) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(schemaLocationArray, DownloadedSchemaEntryImpl.SCHEMALOCATION$4);
        }
    }
    
    @Override
    public void setSchemaLocationArray(final int i, final String schemaLocation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(schemaLocation);
        }
    }
    
    @Override
    public void xsetSchemaLocationArray(final XmlAnyURI[] schemaLocationArray) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(schemaLocationArray, DownloadedSchemaEntryImpl.SCHEMALOCATION$4);
        }
    }
    
    @Override
    public void xsetSchemaLocationArray(final int i, final XmlAnyURI schemaLocation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(schemaLocation);
        }
    }
    
    @Override
    public void insertSchemaLocation(final int i, final String schemaLocation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue target = (SimpleValue)this.get_store().insert_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            target.setStringValue(schemaLocation);
        }
    }
    
    @Override
    public void addSchemaLocation(final String schemaLocation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().add_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4);
            target.setStringValue(schemaLocation);
        }
    }
    
    @Override
    public XmlAnyURI insertNewSchemaLocation(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().insert_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
            return target;
        }
    }
    
    @Override
    public XmlAnyURI addNewSchemaLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().add_element_user(DownloadedSchemaEntryImpl.SCHEMALOCATION$4);
            return target;
        }
    }
    
    @Override
    public void removeSchemaLocation(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DownloadedSchemaEntryImpl.SCHEMALOCATION$4, i);
        }
    }
    
    @Override
    public String getNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6, 0);
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
            target = (XmlAnyURI)this.get_store().find_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6, 0);
            return target;
        }
    }
    
    @Override
    public boolean isSetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DownloadedSchemaEntryImpl.NAMESPACE$6) != 0;
        }
    }
    
    @Override
    public void setNamespace(final String namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6, 0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6);
            }
            target.setStringValue(namespace);
        }
    }
    
    @Override
    public void xsetNamespace(final XmlAnyURI namespace) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6, 0);
            if (target == null) {
                target = (XmlAnyURI)this.get_store().add_element_user(DownloadedSchemaEntryImpl.NAMESPACE$6);
            }
            target.set(namespace);
        }
    }
    
    @Override
    public void unsetNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DownloadedSchemaEntryImpl.NAMESPACE$6, 0);
        }
    }
    
    static {
        FILENAME$0 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "filename");
        SHA1$2 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "sha1");
        SCHEMALOCATION$4 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "schemaLocation");
        NAMESPACE$6 = new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "namespace");
    }
}
