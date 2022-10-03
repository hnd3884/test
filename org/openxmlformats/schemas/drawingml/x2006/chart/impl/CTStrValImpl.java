package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStrValImpl extends XmlComplexContentImpl implements CTStrVal
{
    private static final long serialVersionUID = 1L;
    private static final QName V$0;
    private static final QName IDX$2;
    
    public CTStrValImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTStrValImpl.V$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTStrValImpl.V$0, 0);
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTStrValImpl.V$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTStrValImpl.V$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTStrValImpl.V$0, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTStrValImpl.V$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public long getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrValImpl.IDX$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTStrValImpl.IDX$2);
        }
    }
    
    public void setIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrValImpl.IDX$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrValImpl.IDX$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTStrValImpl.IDX$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTStrValImpl.IDX$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        V$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "v");
        IDX$2 = new QName("", "idx");
    }
}
