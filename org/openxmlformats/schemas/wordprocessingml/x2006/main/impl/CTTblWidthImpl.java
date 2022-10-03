package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblWidthImpl extends XmlComplexContentImpl implements CTTblWidth
{
    private static final long serialVersionUID = 1L;
    private static final QName W$0;
    private static final QName TYPE$2;
    
    public CTTblWidthImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblWidthImpl.W$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTTblWidthImpl.W$0);
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTblWidthImpl.W$0) != null;
        }
    }
    
    public void setW(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblWidthImpl.W$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTblWidthImpl.W$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetW(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTTblWidthImpl.W$0);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTTblWidthImpl.W$0);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTblWidthImpl.W$0);
        }
    }
    
    public STTblWidth.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblWidthImpl.TYPE$2);
            if (simpleValue == null) {
                return null;
            }
            return (STTblWidth.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTblWidth xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTblWidth)this.get_store().find_attribute_user(CTTblWidthImpl.TYPE$2);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTblWidthImpl.TYPE$2) != null;
        }
    }
    
    public void setType(final STTblWidth.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblWidthImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTblWidthImpl.TYPE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STTblWidth stTblWidth) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTblWidth stTblWidth2 = (STTblWidth)this.get_store().find_attribute_user(CTTblWidthImpl.TYPE$2);
            if (stTblWidth2 == null) {
                stTblWidth2 = (STTblWidth)this.get_store().add_attribute_user(CTTblWidthImpl.TYPE$2);
            }
            stTblWidth2.set((XmlObject)stTblWidth);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTblWidthImpl.TYPE$2);
        }
    }
    
    static {
        W$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
        TYPE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
    }
}
