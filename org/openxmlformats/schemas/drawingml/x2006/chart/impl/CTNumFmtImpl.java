package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumFmtImpl extends XmlComplexContentImpl implements CTNumFmt
{
    private static final long serialVersionUID = 1L;
    private static final QName FORMATCODE$0;
    private static final QName SOURCELINKED$2;
    
    public CTNumFmtImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$0);
        }
    }
    
    public void setFormatCode(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumFmtImpl.FORMATCODE$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormatCode(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTNumFmtImpl.FORMATCODE$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public boolean getSourceLinked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSourceLinked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
        }
    }
    
    public boolean isSetSourceLinked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNumFmtImpl.SOURCELINKED$2) != null;
        }
    }
    
    public void setSourceLinked(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSourceLinked(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNumFmtImpl.SOURCELINKED$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSourceLinked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNumFmtImpl.SOURCELINKED$2);
        }
    }
    
    static {
        FORMATCODE$0 = new QName("", "formatCode");
        SOURCELINKED$2 = new QName("", "sourceLinked");
    }
}
