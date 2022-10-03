package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.STName;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentAuthorImpl extends XmlComplexContentImpl implements CTCommentAuthor
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName ID$2;
    private static final QName NAME$4;
    private static final QName INITIALS$6;
    private static final QName LASTIDX$8;
    private static final QName CLRIDX$10;
    
    public CTCommentAuthorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCommentAuthorImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentAuthorImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommentAuthorImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCommentAuthorImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentAuthorImpl.EXTLST$0, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.ID$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.ID$2);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentAuthorImpl.ID$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.ID$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCommentAuthorImpl.ID$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.NAME$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STName xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STName)this.get_store().find_attribute_user(CTCommentAuthorImpl.NAME$4);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.NAME$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentAuthorImpl.NAME$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STName stName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STName stName2 = (STName)this.get_store().find_attribute_user(CTCommentAuthorImpl.NAME$4);
            if (stName2 == null) {
                stName2 = (STName)this.get_store().add_attribute_user(CTCommentAuthorImpl.NAME$4);
            }
            stName2.set((XmlObject)stName);
        }
    }
    
    public String getInitials() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.INITIALS$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STName xgetInitials() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STName)this.get_store().find_attribute_user(CTCommentAuthorImpl.INITIALS$6);
        }
    }
    
    public void setInitials(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.INITIALS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentAuthorImpl.INITIALS$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetInitials(final STName stName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STName stName2 = (STName)this.get_store().find_attribute_user(CTCommentAuthorImpl.INITIALS$6);
            if (stName2 == null) {
                stName2 = (STName)this.get_store().add_attribute_user(CTCommentAuthorImpl.INITIALS$6);
            }
            stName2.set((XmlObject)stName);
        }
    }
    
    public long getLastIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetLastIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
        }
    }
    
    public void setLastIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetLastIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCommentAuthorImpl.LASTIDX$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public long getClrIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetClrIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
        }
    }
    
    public void setClrIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetClrIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCommentAuthorImpl.CLRIDX$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        ID$2 = new QName("", "id");
        NAME$4 = new QName("", "name");
        INITIALS$6 = new QName("", "initials");
        LASTIDX$8 = new QName("", "lastIdx");
        CLRIDX$10 = new QName("", "clrIdx");
    }
}
