package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;

public class PageSheetTypeImpl extends SheetTypeImpl implements PageSheetType
{
    private static final long serialVersionUID = 1L;
    private static final QName UNIQUEID$0;
    
    public PageSheetTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public String getUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
        }
    }
    
    @Override
    public boolean isSetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageSheetTypeImpl.UNIQUEID$0) != null;
        }
    }
    
    @Override
    public void setUniqueID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetUniqueID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(PageSheetTypeImpl.UNIQUEID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageSheetTypeImpl.UNIQUEID$0);
        }
    }
    
    static {
        UNIQUEID$0 = new QName("", "UniqueID");
    }
}
