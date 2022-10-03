package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;

public class CTMoveBookmarkImpl extends CTBookmarkImpl implements CTMoveBookmark
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTHOR$0;
    private static final QName DATE$2;
    
    public CTMoveBookmarkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public String getAuthor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public STString xgetAuthor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
        }
    }
    
    @Override
    public void setAuthor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetAuthor(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTMoveBookmarkImpl.AUTHOR$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    @Override
    public Calendar getDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMoveBookmarkImpl.DATE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    @Override
    public STDateTime xgetDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDateTime)this.get_store().find_attribute_user(CTMoveBookmarkImpl.DATE$2);
        }
    }
    
    @Override
    public void setDate(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMoveBookmarkImpl.DATE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMoveBookmarkImpl.DATE$2);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    @Override
    public void xsetDate(final STDateTime stDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDateTime stDateTime2 = (STDateTime)this.get_store().find_attribute_user(CTMoveBookmarkImpl.DATE$2);
            if (stDateTime2 == null) {
                stDateTime2 = (STDateTime)this.get_store().add_attribute_user(CTMoveBookmarkImpl.DATE$2);
            }
            stDateTime2.set((XmlObject)stDateTime);
        }
    }
    
    static {
        AUTHOR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "author");
        DATE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "date");
    }
}
