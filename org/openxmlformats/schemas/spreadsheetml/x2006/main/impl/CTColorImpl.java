package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlDouble;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedIntHex;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorImpl extends XmlComplexContentImpl implements CTColor
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTO$0;
    private static final QName INDEXED$2;
    private static final QName RGB$4;
    private static final QName THEME$6;
    private static final QName TINT$8;
    
    public CTColorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.AUTO$0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTColorImpl.AUTO$0);
        }
    }
    
    public boolean isSetAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.AUTO$0) != null;
        }
    }
    
    public void setAuto(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.AUTO$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.AUTO$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAuto(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTColorImpl.AUTO$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTColorImpl.AUTO$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.AUTO$0);
        }
    }
    
    public long getIndexed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.INDEXED$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIndexed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTColorImpl.INDEXED$2);
        }
    }
    
    public boolean isSetIndexed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.INDEXED$2) != null;
        }
    }
    
    public void setIndexed(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.INDEXED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.INDEXED$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIndexed(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTColorImpl.INDEXED$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTColorImpl.INDEXED$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIndexed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.INDEXED$2);
        }
    }
    
    public byte[] getRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.RGB$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STUnsignedIntHex xgetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUnsignedIntHex)this.get_store().find_attribute_user(CTColorImpl.RGB$4);
        }
    }
    
    public boolean isSetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.RGB$4) != null;
        }
    }
    
    public void setRgb(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.RGB$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.RGB$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRgb(final STUnsignedIntHex stUnsignedIntHex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnsignedIntHex stUnsignedIntHex2 = (STUnsignedIntHex)this.get_store().find_attribute_user(CTColorImpl.RGB$4);
            if (stUnsignedIntHex2 == null) {
                stUnsignedIntHex2 = (STUnsignedIntHex)this.get_store().add_attribute_user(CTColorImpl.RGB$4);
            }
            stUnsignedIntHex2.set((XmlObject)stUnsignedIntHex);
        }
    }
    
    public void unsetRgb() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.RGB$4);
        }
    }
    
    public long getTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEME$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTColorImpl.THEME$6);
        }
    }
    
    public boolean isSetTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.THEME$6) != null;
        }
    }
    
    public void setTheme(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.THEME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.THEME$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTheme(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTColorImpl.THEME$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTColorImpl.THEME$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.THEME$6);
        }
    }
    
    public double getTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.TINT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTColorImpl.TINT$8);
            }
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble = (XmlDouble)this.get_store().find_attribute_user(CTColorImpl.TINT$8);
            if (xmlDouble == null) {
                xmlDouble = (XmlDouble)this.get_default_attribute_value(CTColorImpl.TINT$8);
            }
            return xmlDouble;
        }
    }
    
    public boolean isSetTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTColorImpl.TINT$8) != null;
        }
    }
    
    public void setTint(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorImpl.TINT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorImpl.TINT$8);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetTint(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTColorImpl.TINT$8);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTColorImpl.TINT$8);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTColorImpl.TINT$8);
        }
    }
    
    static {
        AUTO$0 = new QName("", "auto");
        INDEXED$2 = new QName("", "indexed");
        RGB$4 = new QName("", "rgb");
        THEME$6 = new QName("", "theme");
        TINT$8 = new QName("", "tint");
    }
}
