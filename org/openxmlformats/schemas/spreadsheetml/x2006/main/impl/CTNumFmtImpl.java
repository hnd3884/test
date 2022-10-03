package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumFmtImpl extends XmlComplexContentImpl implements CTNumFmt
{
    private static final long serialVersionUID = 1L;
    private static final QName NUMFMTID$0;
    private static final QName FORMATCODE$2;
    
    public CTNumFmtImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.NUMFMTID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STNumFmtId xgetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumFmtId)this.get_store().find_attribute_user(CTNumFmtImpl.NUMFMTID$0);
        }
    }
    
    public void setNumFmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.NUMFMTID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumFmtImpl.NUMFMTID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetNumFmtId(final STNumFmtId stNumFmtId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumFmtId stNumFmtId2 = (STNumFmtId)this.get_store().find_attribute_user(CTNumFmtImpl.NUMFMTID$0);
            if (stNumFmtId2 == null) {
                stNumFmtId2 = (STNumFmtId)this.get_store().add_attribute_user(CTNumFmtImpl.NUMFMTID$0);
            }
            stNumFmtId2.set((XmlObject)stNumFmtId);
        }
    }
    
    public String getFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$2);
        }
    }
    
    public void setFormatCode(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumFmtImpl.FORMATCODE$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormatCode(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTNumFmtImpl.FORMATCODE$2);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTNumFmtImpl.FORMATCODE$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    static {
        NUMFMTID$0 = new QName("", "numFmtId");
        FORMATCODE$2 = new QName("", "formatCode");
    }
}
