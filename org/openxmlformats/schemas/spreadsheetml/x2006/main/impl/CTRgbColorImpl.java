package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedIntHex;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRgbColorImpl extends XmlComplexContentImpl implements CTRgbColor
{
    private static final long serialVersionUID = 1L;
    private static final QName RGB$0;
    
    public CTRgbColorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRgbColorImpl.RGB$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUnsignedIntHex xgetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnsignedIntHex)this.get_store().find_attribute_user(CTRgbColorImpl.RGB$0);
        }
    }
    
    public boolean isSetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRgbColorImpl.RGB$0) != null;
        }
    }
    
    public void setRgb(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRgbColorImpl.RGB$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRgbColorImpl.RGB$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRgb(final STUnsignedIntHex stUnsignedIntHex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnsignedIntHex stUnsignedIntHex2 = (STUnsignedIntHex)this.get_store().find_attribute_user(CTRgbColorImpl.RGB$0);
            if (stUnsignedIntHex2 == null) {
                stUnsignedIntHex2 = (STUnsignedIntHex)this.get_store().add_attribute_user(CTRgbColorImpl.RGB$0);
            }
            stUnsignedIntHex2.set((XmlObject)stUnsignedIntHex);
        }
    }
    
    public void unsetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRgbColorImpl.RGB$0);
        }
    }
    
    static {
        RGB$0 = new QName("", "rgb");
    }
}
