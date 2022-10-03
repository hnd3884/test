package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideSizeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideSizeCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideSizeImpl extends XmlComplexContentImpl implements CTSlideSize
{
    private static final long serialVersionUID = 1L;
    private static final QName CX$0;
    private static final QName CY$2;
    private static final QName TYPE$4;
    
    public CTSlideSizeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getCx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.CX$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STSlideSizeCoordinate xgetCx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSlideSizeCoordinate)this.get_store().find_attribute_user(CTSlideSizeImpl.CX$0);
        }
    }
    
    public void setCx(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.CX$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideSizeImpl.CX$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetCx(final STSlideSizeCoordinate stSlideSizeCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideSizeCoordinate stSlideSizeCoordinate2 = (STSlideSizeCoordinate)this.get_store().find_attribute_user(CTSlideSizeImpl.CX$0);
            if (stSlideSizeCoordinate2 == null) {
                stSlideSizeCoordinate2 = (STSlideSizeCoordinate)this.get_store().add_attribute_user(CTSlideSizeImpl.CX$0);
            }
            stSlideSizeCoordinate2.set((XmlObject)stSlideSizeCoordinate);
        }
    }
    
    public int getCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.CY$2);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STSlideSizeCoordinate xgetCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSlideSizeCoordinate)this.get_store().find_attribute_user(CTSlideSizeImpl.CY$2);
        }
    }
    
    public void setCy(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.CY$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideSizeImpl.CY$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetCy(final STSlideSizeCoordinate stSlideSizeCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideSizeCoordinate stSlideSizeCoordinate2 = (STSlideSizeCoordinate)this.get_store().find_attribute_user(CTSlideSizeImpl.CY$2);
            if (stSlideSizeCoordinate2 == null) {
                stSlideSizeCoordinate2 = (STSlideSizeCoordinate)this.get_store().add_attribute_user(CTSlideSizeImpl.CY$2);
            }
            stSlideSizeCoordinate2.set((XmlObject)stSlideSizeCoordinate);
        }
    }
    
    public STSlideSizeType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideSizeImpl.TYPE$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STSlideSizeType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSlideSizeType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideSizeType stSlideSizeType = (STSlideSizeType)this.get_store().find_attribute_user(CTSlideSizeImpl.TYPE$4);
            if (stSlideSizeType == null) {
                stSlideSizeType = (STSlideSizeType)this.get_default_attribute_value(CTSlideSizeImpl.TYPE$4);
            }
            return stSlideSizeType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideSizeImpl.TYPE$4) != null;
        }
    }
    
    public void setType(final STSlideSizeType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideSizeImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideSizeImpl.TYPE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STSlideSizeType stSlideSizeType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideSizeType stSlideSizeType2 = (STSlideSizeType)this.get_store().find_attribute_user(CTSlideSizeImpl.TYPE$4);
            if (stSlideSizeType2 == null) {
                stSlideSizeType2 = (STSlideSizeType)this.get_store().add_attribute_user(CTSlideSizeImpl.TYPE$4);
            }
            stSlideSizeType2.set((XmlObject)stSlideSizeType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideSizeImpl.TYPE$4);
        }
    }
    
    static {
        CX$0 = new QName("", "cx");
        CY$2 = new QName("", "cy");
        TYPE$4 = new QName("", "type");
    }
}
