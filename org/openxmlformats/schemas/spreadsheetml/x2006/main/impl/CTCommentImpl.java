package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STGuid;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentImpl extends XmlComplexContentImpl implements CTComment
{
    private static final long serialVersionUID = 1L;
    private static final QName TEXT$0;
    private static final QName REF$2;
    private static final QName AUTHORID$4;
    private static final QName GUID$6;
    
    public CTCommentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRst getText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRst ctRst = (CTRst)this.get_store().find_element_user(CTCommentImpl.TEXT$0, 0);
            if (ctRst == null) {
                return null;
            }
            return ctRst;
        }
    }
    
    public void setText(final CTRst ctRst) {
        this.generatedSetterHelperImpl((XmlObject)ctRst, CTCommentImpl.TEXT$0, 0, (short)1);
    }
    
    public CTRst addNewText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRst)this.get_store().add_element_user(CTCommentImpl.TEXT$0);
        }
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.REF$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTCommentImpl.REF$2);
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.REF$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.REF$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTCommentImpl.REF$2);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTCommentImpl.REF$2);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public long getAuthorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetAuthorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$4);
        }
    }
    
    public void setAuthorId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.AUTHORID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetAuthorId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCommentImpl.AUTHORID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getGuid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.GUID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGuid xgetGuid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGuid)this.get_store().find_attribute_user(CTCommentImpl.GUID$6);
        }
    }
    
    public boolean isSetGuid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCommentImpl.GUID$6) != null;
        }
    }
    
    public void setGuid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.GUID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.GUID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGuid(final STGuid stGuid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGuid stGuid2 = (STGuid)this.get_store().find_attribute_user(CTCommentImpl.GUID$6);
            if (stGuid2 == null) {
                stGuid2 = (STGuid)this.get_store().add_attribute_user(CTCommentImpl.GUID$6);
            }
            stGuid2.set((XmlObject)stGuid);
        }
    }
    
    public void unsetGuid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCommentImpl.GUID$6);
        }
    }
    
    static {
        TEXT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "text");
        REF$2 = new QName("", "ref");
        AUTHORID$4 = new QName("", "authorId");
        GUID$6 = new QName("", "guid");
    }
}
