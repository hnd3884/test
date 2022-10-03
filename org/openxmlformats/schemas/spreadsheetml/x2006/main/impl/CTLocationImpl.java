package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLocation;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLocationImpl extends XmlComplexContentImpl implements CTLocation
{
    private static final long serialVersionUID = 1L;
    private static final QName REF$0;
    private static final QName FIRSTHEADERROW$2;
    private static final QName FIRSTDATAROW$4;
    private static final QName FIRSTDATACOL$6;
    private static final QName ROWPAGECOUNT$8;
    private static final QName COLPAGECOUNT$10;
    
    public CTLocationImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.REF$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTLocationImpl.REF$0);
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.REF$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.REF$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTLocationImpl.REF$0);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTLocationImpl.REF$0);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public long getFirstHeaderRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFirstHeaderRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
        }
    }
    
    public void setFirstHeaderRow(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstHeaderRow(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTLocationImpl.FIRSTHEADERROW$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public long getFirstDataRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFirstDataRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
        }
    }
    
    public void setFirstDataRow(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstDataRow(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTLocationImpl.FIRSTDATAROW$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public long getFirstDataCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFirstDataCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
        }
    }
    
    public void setFirstDataCol(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstDataCol(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTLocationImpl.FIRSTDATACOL$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public long getRowPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTLocationImpl.ROWPAGECOUNT$8);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetRowPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTLocationImpl.ROWPAGECOUNT$8);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetRowPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLocationImpl.ROWPAGECOUNT$8) != null;
        }
    }
    
    public void setRowPageCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRowPageCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTLocationImpl.ROWPAGECOUNT$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetRowPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLocationImpl.ROWPAGECOUNT$8);
        }
    }
    
    public long getColPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTLocationImpl.COLPAGECOUNT$10);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetColPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTLocationImpl.COLPAGECOUNT$10);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetColPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLocationImpl.COLPAGECOUNT$10) != null;
        }
    }
    
    public void setColPageCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetColPageCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTLocationImpl.COLPAGECOUNT$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetColPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLocationImpl.COLPAGECOUNT$10);
        }
    }
    
    static {
        REF$0 = new QName("", "ref");
        FIRSTHEADERROW$2 = new QName("", "firstHeaderRow");
        FIRSTDATAROW$4 = new QName("", "firstDataRow");
        FIRSTDATACOL$6 = new QName("", "firstDataCol");
        ROWPAGECOUNT$8 = new QName("", "rowPageCount");
        COLPAGECOUNT$10 = new QName("", "colPageCount");
    }
}
