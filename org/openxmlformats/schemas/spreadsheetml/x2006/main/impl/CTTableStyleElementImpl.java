package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableStyleType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleElementImpl extends XmlComplexContentImpl implements CTTableStyleElement
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    private static final QName SIZE$2;
    private static final QName DXFID$4;
    
    public CTTableStyleElementImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTableStyleType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STTableStyleType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTableStyleType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTableStyleType)this.get_store().find_attribute_user(CTTableStyleElementImpl.TYPE$0);
        }
    }
    
    public void setType(final STTableStyleType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleElementImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STTableStyleType stTableStyleType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTableStyleType stTableStyleType2 = (STTableStyleType)this.get_store().find_attribute_user(CTTableStyleElementImpl.TYPE$0);
            if (stTableStyleType2 == null) {
                stTableStyleType2 = (STTableStyleType)this.get_store().add_attribute_user(CTTableStyleElementImpl.TYPE$0);
            }
            stTableStyleType2.set((XmlObject)stTableStyleType);
        }
    }
    
    public long getSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.SIZE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableStyleElementImpl.SIZE$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStyleElementImpl.SIZE$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTTableStyleElementImpl.SIZE$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleElementImpl.SIZE$2) != null;
        }
    }
    
    public void setSize(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.SIZE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleElementImpl.SIZE$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSize(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStyleElementImpl.SIZE$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableStyleElementImpl.SIZE$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleElementImpl.SIZE$2);
        }
    }
    
    public long getDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.DXFID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableStyleElementImpl.DXFID$4);
        }
    }
    
    public boolean isSetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleElementImpl.DXFID$4) != null;
        }
    }
    
    public void setDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleElementImpl.DXFID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleElementImpl.DXFID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableStyleElementImpl.DXFID$4);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableStyleElementImpl.DXFID$4);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleElementImpl.DXFID$4);
        }
    }
    
    static {
        TYPE$0 = new QName("", "type");
        SIZE$2 = new QName("", "size");
        DXFID$4 = new QName("", "dxfId");
    }
}
