package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.STIndex;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentImpl extends XmlComplexContentImpl implements CTComment
{
    private static final long serialVersionUID = 1L;
    private static final QName POS$0;
    private static final QName TEXT$2;
    private static final QName EXTLST$4;
    private static final QName AUTHORID$6;
    private static final QName DT$8;
    private static final QName IDX$10;
    
    public CTCommentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPoint2D getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPoint2D ctPoint2D = (CTPoint2D)this.get_store().find_element_user(CTCommentImpl.POS$0, 0);
            if (ctPoint2D == null) {
                return null;
            }
            return ctPoint2D;
        }
    }
    
    public void setPos(final CTPoint2D ctPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPoint2D, CTCommentImpl.POS$0, 0, (short)1);
    }
    
    public CTPoint2D addNewPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPoint2D)this.get_store().add_element_user(CTCommentImpl.POS$0);
        }
    }
    
    public String getText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCommentImpl.TEXT$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTCommentImpl.TEXT$2, 0);
        }
    }
    
    public void setText(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCommentImpl.TEXT$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTCommentImpl.TEXT$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetText(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTCommentImpl.TEXT$2, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTCommentImpl.TEXT$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTCommentImpl.EXTLST$4, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTCommentImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTCommentImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentImpl.EXTLST$4, 0);
        }
    }
    
    public long getAuthorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetAuthorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$6);
        }
    }
    
    public void setAuthorId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.AUTHORID$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetAuthorId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCommentImpl.AUTHORID$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCommentImpl.AUTHORID$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public Calendar getDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.DT$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_attribute_user(CTCommentImpl.DT$8);
        }
    }
    
    public boolean isSetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCommentImpl.DT$8) != null;
        }
    }
    
    public void setDt(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.DT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.DT$8);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetDt(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_attribute_user(CTCommentImpl.DT$8);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_attribute_user(CTCommentImpl.DT$8);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCommentImpl.DT$8);
        }
    }
    
    public long getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.IDX$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STIndex xgetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STIndex)this.get_store().find_attribute_user(CTCommentImpl.IDX$10);
        }
    }
    
    public void setIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommentImpl.IDX$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommentImpl.IDX$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIdx(final STIndex stIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STIndex stIndex2 = (STIndex)this.get_store().find_attribute_user(CTCommentImpl.IDX$10);
            if (stIndex2 == null) {
                stIndex2 = (STIndex)this.get_store().add_attribute_user(CTCommentImpl.IDX$10);
            }
            stIndex2.set((XmlObject)stIndex);
        }
    }
    
    static {
        POS$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "pos");
        TEXT$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "text");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        AUTHORID$6 = new QName("", "authorId");
        DT$8 = new QName("", "dt");
        IDX$10 = new QName("", "idx");
    }
}
