package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBreakImpl extends XmlComplexContentImpl implements CTBreak
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName MIN$2;
    private static final QName MAX$4;
    private static final QName MAN$6;
    private static final QName PT$8;
    
    public CTBreakImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBreakImpl.ID$0);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.ID$0);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBreakImpl.ID$0);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBreakImpl.ID$0) != null;
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBreakImpl.ID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.ID$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBreakImpl.ID$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBreakImpl.ID$0);
        }
    }
    
    public long getMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MIN$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBreakImpl.MIN$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.MIN$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBreakImpl.MIN$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBreakImpl.MIN$2) != null;
        }
    }
    
    public void setMin(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MIN$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBreakImpl.MIN$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMin(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.MIN$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBreakImpl.MIN$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBreakImpl.MIN$2);
        }
    }
    
    public long getMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MAX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBreakImpl.MAX$4);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.MAX$4);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBreakImpl.MAX$4);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBreakImpl.MAX$4) != null;
        }
    }
    
    public void setMax(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MAX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBreakImpl.MAX$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMax(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBreakImpl.MAX$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBreakImpl.MAX$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBreakImpl.MAX$4);
        }
    }
    
    public boolean getMan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MAN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBreakImpl.MAN$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBreakImpl.MAN$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBreakImpl.MAN$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBreakImpl.MAN$6) != null;
        }
    }
    
    public void setMan(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.MAN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBreakImpl.MAN$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMan(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBreakImpl.MAN$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBreakImpl.MAN$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBreakImpl.MAN$6);
        }
    }
    
    public boolean getPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.PT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBreakImpl.PT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBreakImpl.PT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBreakImpl.PT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBreakImpl.PT$8) != null;
        }
    }
    
    public void setPt(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBreakImpl.PT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBreakImpl.PT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPt(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBreakImpl.PT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBreakImpl.PT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBreakImpl.PT$8);
        }
    }
    
    static {
        ID$0 = new QName("", "id");
        MIN$2 = new QName("", "min");
        MAX$4 = new QName("", "max");
        MAN$6 = new QName("", "man");
        PT$8 = new QName("", "pt");
    }
}
