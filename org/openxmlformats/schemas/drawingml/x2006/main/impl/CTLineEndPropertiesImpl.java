package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLineEndPropertiesImpl extends XmlComplexContentImpl implements CTLineEndProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    private static final QName W$2;
    private static final QName LEN$4;
    
    public CTLineEndPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STLineEndType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STLineEndType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLineEndType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineEndType)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLineEndPropertiesImpl.TYPE$0) != null;
        }
    }
    
    public void setType(final STLineEndType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STLineEndType stLineEndType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineEndType stLineEndType2 = (STLineEndType)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
            if (stLineEndType2 == null) {
                stLineEndType2 = (STLineEndType)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.TYPE$0);
            }
            stLineEndType2.set((XmlObject)stLineEndType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLineEndPropertiesImpl.TYPE$0);
        }
    }
    
    public STLineEndWidth.Enum getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.W$2);
            if (simpleValue == null) {
                return null;
            }
            return (STLineEndWidth.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLineEndWidth xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineEndWidth)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.W$2);
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLineEndPropertiesImpl.W$2) != null;
        }
    }
    
    public void setW(final STLineEndWidth.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.W$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.W$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetW(final STLineEndWidth stLineEndWidth) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineEndWidth stLineEndWidth2 = (STLineEndWidth)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.W$2);
            if (stLineEndWidth2 == null) {
                stLineEndWidth2 = (STLineEndWidth)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.W$2);
            }
            stLineEndWidth2.set((XmlObject)stLineEndWidth);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLineEndPropertiesImpl.W$2);
        }
    }
    
    public STLineEndLength.Enum getLen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.LEN$4);
            if (simpleValue == null) {
                return null;
            }
            return (STLineEndLength.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLineEndLength xgetLen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineEndLength)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.LEN$4);
        }
    }
    
    public boolean isSetLen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLineEndPropertiesImpl.LEN$4) != null;
        }
    }
    
    public void setLen(final STLineEndLength.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.LEN$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.LEN$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLen(final STLineEndLength stLineEndLength) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineEndLength stLineEndLength2 = (STLineEndLength)this.get_store().find_attribute_user(CTLineEndPropertiesImpl.LEN$4);
            if (stLineEndLength2 == null) {
                stLineEndLength2 = (STLineEndLength)this.get_store().add_attribute_user(CTLineEndPropertiesImpl.LEN$4);
            }
            stLineEndLength2.set((XmlObject)stLineEndLength);
        }
    }
    
    public void unsetLen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLineEndPropertiesImpl.LEN$4);
        }
    }
    
    static {
        TYPE$0 = new QName("", "type");
        W$2 = new QName("", "w");
        LEN$4 = new QName("", "len");
    }
}
