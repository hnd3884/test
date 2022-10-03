package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STZoom;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTZoom;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTZoomImpl extends XmlComplexContentImpl implements CTZoom
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName PERCENT$2;
    
    public CTZoomImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STZoom.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTZoomImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STZoom.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STZoom xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STZoom)this.get_store().find_attribute_user(CTZoomImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTZoomImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STZoom.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTZoomImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTZoomImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STZoom stZoom) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STZoom stZoom2 = (STZoom)this.get_store().find_attribute_user(CTZoomImpl.VAL$0);
            if (stZoom2 == null) {
                stZoom2 = (STZoom)this.get_store().add_attribute_user(CTZoomImpl.VAL$0);
            }
            stZoom2.set((XmlObject)stZoom);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTZoomImpl.VAL$0);
        }
    }
    
    public BigInteger getPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTZoomImpl.PERCENT$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTZoomImpl.PERCENT$2);
        }
    }
    
    public void setPercent(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTZoomImpl.PERCENT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTZoomImpl.PERCENT$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetPercent(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTZoomImpl.PERCENT$2);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTZoomImpl.PERCENT$2);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        PERCENT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "percent");
    }
}
