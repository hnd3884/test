package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHeightImpl extends XmlComplexContentImpl implements CTHeight
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName HRULE$2;
    
    public CTHeightImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeightImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTHeightImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeightImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeightImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeightImpl.VAL$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetVal(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTHeightImpl.VAL$0);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTHeightImpl.VAL$0);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeightImpl.VAL$0);
        }
    }
    
    public STHeightRule.Enum getHRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeightImpl.HRULE$2);
            if (simpleValue == null) {
                return null;
            }
            return (STHeightRule.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHeightRule xgetHRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHeightRule)this.get_store().find_attribute_user(CTHeightImpl.HRULE$2);
        }
    }
    
    public boolean isSetHRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeightImpl.HRULE$2) != null;
        }
    }
    
    public void setHRule(final STHeightRule.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeightImpl.HRULE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeightImpl.HRULE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHRule(final STHeightRule stHeightRule) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHeightRule stHeightRule2 = (STHeightRule)this.get_store().find_attribute_user(CTHeightImpl.HRULE$2);
            if (stHeightRule2 == null) {
                stHeightRule2 = (STHeightRule)this.get_store().add_attribute_user(CTHeightImpl.HRULE$2);
            }
            stHeightRule2.set((XmlObject)stHeightRule);
        }
    }
    
    public void unsetHRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeightImpl.HRULE$2);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        HRULE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hRule");
    }
}
