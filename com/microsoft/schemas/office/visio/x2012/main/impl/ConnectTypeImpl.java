package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.ConnectType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ConnectTypeImpl extends XmlComplexContentImpl implements ConnectType
{
    private static final long serialVersionUID = 1L;
    private static final QName FROMSHEET$0;
    private static final QName FROMCELL$2;
    private static final QName FROMPART$4;
    private static final QName TOSHEET$6;
    private static final QName TOCELL$8;
    private static final QName TOPART$10;
    
    public ConnectTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getFromSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMSHEET$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFromSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ConnectTypeImpl.FROMSHEET$0);
        }
    }
    
    public void setFromSheet(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMSHEET$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.FROMSHEET$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFromSheet(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ConnectTypeImpl.FROMSHEET$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ConnectTypeImpl.FROMSHEET$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getFromCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMCELL$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetFromCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ConnectTypeImpl.FROMCELL$2);
        }
    }
    
    public boolean isSetFromCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ConnectTypeImpl.FROMCELL$2) != null;
        }
    }
    
    public void setFromCell(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMCELL$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.FROMCELL$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFromCell(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ConnectTypeImpl.FROMCELL$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ConnectTypeImpl.FROMCELL$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetFromCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ConnectTypeImpl.FROMCELL$2);
        }
    }
    
    public int getFromPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMPART$4);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetFromPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(ConnectTypeImpl.FROMPART$4);
        }
    }
    
    public boolean isSetFromPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ConnectTypeImpl.FROMPART$4) != null;
        }
    }
    
    public void setFromPart(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.FROMPART$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.FROMPART$4);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetFromPart(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(ConnectTypeImpl.FROMPART$4);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(ConnectTypeImpl.FROMPART$4);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetFromPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ConnectTypeImpl.FROMPART$4);
        }
    }
    
    public long getToSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOSHEET$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetToSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ConnectTypeImpl.TOSHEET$6);
        }
    }
    
    public void setToSheet(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOSHEET$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.TOSHEET$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetToSheet(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ConnectTypeImpl.TOSHEET$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ConnectTypeImpl.TOSHEET$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getToCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOCELL$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetToCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ConnectTypeImpl.TOCELL$8);
        }
    }
    
    public boolean isSetToCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ConnectTypeImpl.TOCELL$8) != null;
        }
    }
    
    public void setToCell(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOCELL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.TOCELL$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetToCell(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ConnectTypeImpl.TOCELL$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ConnectTypeImpl.TOCELL$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetToCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ConnectTypeImpl.TOCELL$8);
        }
    }
    
    public int getToPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOPART$10);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetToPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(ConnectTypeImpl.TOPART$10);
        }
    }
    
    public boolean isSetToPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ConnectTypeImpl.TOPART$10) != null;
        }
    }
    
    public void setToPart(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ConnectTypeImpl.TOPART$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ConnectTypeImpl.TOPART$10);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetToPart(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(ConnectTypeImpl.TOPART$10);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(ConnectTypeImpl.TOPART$10);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetToPart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ConnectTypeImpl.TOPART$10);
        }
    }
    
    static {
        FROMSHEET$0 = new QName("", "FromSheet");
        FROMCELL$2 = new QName("", "FromCell");
        FROMPART$4 = new QName("", "FromPart");
        TOSHEET$6 = new QName("", "ToSheet");
        TOCELL$8 = new QName("", "ToCell");
        TOPART$10 = new QName("", "ToPart");
    }
}
