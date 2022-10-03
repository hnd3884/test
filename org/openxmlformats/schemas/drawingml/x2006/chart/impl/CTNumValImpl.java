package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumValImpl extends XmlComplexContentImpl implements CTNumVal
{
    private static final long serialVersionUID = 1L;
    private static final QName V$0;
    private static final QName IDX$2;
    private static final QName FORMATCODE$4;
    
    public CTNumValImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumValImpl.V$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTNumValImpl.V$0, 0);
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumValImpl.V$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTNumValImpl.V$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTNumValImpl.V$0, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTNumValImpl.V$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public long getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumValImpl.IDX$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTNumValImpl.IDX$2);
        }
    }
    
    public void setIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumValImpl.IDX$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumValImpl.IDX$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTNumValImpl.IDX$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTNumValImpl.IDX$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumValImpl.FORMATCODE$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTNumValImpl.FORMATCODE$4);
        }
    }
    
    public boolean isSetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNumValImpl.FORMATCODE$4) != null;
        }
    }
    
    public void setFormatCode(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumValImpl.FORMATCODE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumValImpl.FORMATCODE$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormatCode(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTNumValImpl.FORMATCODE$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTNumValImpl.FORMATCODE$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNumValImpl.FORMATCODE$4);
        }
    }
    
    static {
        V$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "v");
        IDX$2 = new QName("", "idx");
        FORMATCODE$4 = new QName("", "formatCode");
    }
}
